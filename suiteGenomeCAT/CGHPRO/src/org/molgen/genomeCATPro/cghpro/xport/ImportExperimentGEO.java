package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportExperimentGEO
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 *
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.cghpro.chip.SpotX;
import org.molgen.genomeCATPro.cghpro.chip.SpotXwoGene;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.ISpot;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 * 020813 kt	XPortImport createNewImport(); 170413 kt allow empty chrom position
 * in table definition 010812 add getCreateTableSQLWithAnno
 */
public class ImportExperimentGEO extends ImportExperimentFile implements XPortExperimentFile {

    public final static String geo = "GEO_GSM_TXT";
    ISpot _spot = new SpotX();
    boolean hasGene = false;
    String time = "";
    //protected DecimalFormat myFormatter = new DecimalFormat("0.#####E0");

    @Override
    public ImportExperimentGEO createNewImport() {

        return new ImportExperimentGEO();
    }

    @Override
    protected String getEndMetaDataTag() {

        return null;

    }

    @Override
    protected boolean isCommentLine(String is) {
        if (is.indexOf("#") == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isHeaderLine(String is) {
        if (is.indexOf("ID") == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getDataClazz() {
        if (_spot != null) {
            return _spot.getClass().getName();
        } else {
            return "";
        }
    }

    @Override
    protected String getExperimentNameFromFile() {
        String name = this.inFile.getName();
        name = name.substring(0, name.lastIndexOf("."));
        return name;
    }

    /*@Override
    public String getCreateTableSQL(String tableData, String tablePlatform) {
        String sql = new String(" CREATE TABLE " + tableData + " ( "
                + "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeID varchar(255) NOT NULL default '', "
                + "probeName varchar(255) , "
                + "geneName varchar(255), "
                + "chrom varChar(45) default '',"
                + "chromStart int(10) unsigned default 0,"
                + "chromEnd int(10) unsigned default 0,"
                + "DESCRIPTION varchar(255), "
                + "ratio DOUBLE, "
                + "PRIMARY KEY (id),"
                + //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "INDEX (geneName (10))  "
                + " ) TYPE=MyISAM");

        return sql;
    }
     */
    /**
     * create table without primary key, table will be filled by select into,
     * primary key is added after select into
     *
     * @param tableData
     * @param tablePlatform
     * @param annoColName
     * @return
     */
    /*
    public static String getCreateTableSQLWithAnno(
            String tableData, String tablePlatform, String annoColName) {

        String sql = " CREATE TABLE " + tableData + " ( "
                + "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeName varchar(255) NOT NULL default '', "
                + "geneName varchar(255), "
                + "chrom varchar(45) default '',"
                + "chromStart int(10) unsigned default 0,"
                + "chromEnd int(10) unsigned default 0,"
                + "DESCRIPTION varchar(255), "
                + "ratio DOUBLE, "
                + "gc_position LINESTRING NOT NULL,"
                + annoColName + " varchar(255) NOT NULL default '' ,"
                + "PRIMARY KEY (id),"
                + //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "INDEX (geneName (10))  "
                + " ) TYPE=MyISAM";

        return sql;
    }
     */
    
    /**
     * create sql for trigger to transfer relevant, but existing fields from
     * platform table into experiment data table
     *
     * @param colGene
     * @param colRefSeq
     * @param colDesc
     * @param triggername
     * @param tablePlatform
     * @param tableData
     * @return
     */
    String generateTriggerSql(String triggername, String tablePlatform, String tableData) {
        String colGene = null;
        String colRefSeq = null;
        String colDesc = null;
        Vector<String> cols = DBUtils.getCols(tablePlatform);
        this.hasGene = false;
        Logger.getLogger(ImportExperimentGEO.class.getName()).log(Level.INFO, "COLS {0}", cols);
        for (String col : cols) {
            if (col.compareToIgnoreCase("GENE") == 0 ) {
                colGene = col;
                hasGene = true;
            }
            if (col.toUpperCase().contains("REFSEQ")) {
                colRefSeq = col;
            }
            if (col.toUpperCase().contains("DESCRIPTION")) {
                colDesc = col;
            }
        }
        if (colGene == null) {
            for (String col : cols) {
                if (col.toUpperCase().contains("GENE")) {
                    colGene = col;
                    hasGene = true;
                    break;
                }
            }
        }

        String sql
                = "CREATE TRIGGER " + triggername
                + " BEFORE INSERT ON " + tableData
                + "  FOR EACH ROW "
                + "BEGIN "
                + "DECLARE done INT DEFAULT 0;  "
                + (colRefSeq != null ? "DECLARE varRefseq varchar(255);  " : "")
                + (colGene != null ? "DECLARE varGene varchar(255); " : "")
                + (colDesc != null ? "DECLARE varDesc varchar(255); " : "")
                + "DECLARE varChrom varchar(45);  "
                + "DECLARE varStart INT; "
                + "DECLARE varStop INT;   "
                + "DECLARE cs CURSOR FOR  SELECT "
                + (colRefSeq != null ? (colRefSeq + ", ") : "")
                + (colGene != null ? (colGene + ", ") : "")
                + (colDesc != null ? (colDesc + ", ") : "")
                + " chrom ,chromStart, chromEnd FROM "
                + tablePlatform + " WHERE " + tablePlatform + ".probeName = new.probeID; "
                + "DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1; "
                + "OPEN cs;  "
                + "FETCH cs INTO "
                + (colRefSeq != null ? "varRefseq," : "")
                + (colGene != null ? "varGene, " : "")
                + (colDesc != null ? " varDesc," : "")
                + " varChrom, varStart, varStop;  "
                + "IF NOT done THEN "
                //"CLOSE cs; " +
                + " SET done = \'Position not found in annotation table\'; "
                + "END IF;  "
                + " SET new.chrom = varChrom;  "
                + " SET new.chromStart = varStart;  "
                + " SET new.chromEnd = varStop;  "
                + (colRefSeq != null ? " SET new.probeName = varRefseq;  " : "")
                + (colDesc != null ? " SET new.Description = varDesc;  " : "")
                + (colGene != null ? " SET new.geneName = varGene; " : "")
                + " CLOSE cs; "
                + " END ;";
        return sql;
    }

    /**
     * kt review 050716
     *
     *
     * @param tabledata
     * @throws java.lang.Exception
     */
    @Override
    public void generateTable(String tabledata) throws Exception {
        Statement s;
        try {

            String tablePlatform = this.experimentdata.getPlatformdata().getTableData();
            //con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            s = con.createStatement();
            String triggername = "tr_position_" + this.experimentdata.getTableData();

            String sqlTrigger = this.generateTriggerSql(triggername, tablePlatform, tabledata);
          
            s.execute(
                    "DROP TABLE if EXISTS " + tabledata);
            if (!this.hasGene) {
                this._spot = new SpotXwoGene();
            }
            this.experimentdata.setClazz(this.getDataClazz());
           
            String sql = this._spot.getCreateTableSQL(experimentdata);
//this.getCreateTableSQL(tabledata);
            Logger.getLogger(ImportExperimentGEO.class.getName()).log(
                    Level.INFO,
                    sql);
            s.execute(sql);
            
            try {
                s.execute("DROP TRIGGER IF EXISTS " + triggername);
            } catch (SQLException sQLException) {
            }
            // update insert row with data from annotation table 
             Logger.getLogger(ImportExperimentGEO.class.getName()).log(
                    Level.INFO,
                    sqlTrigger);

            s.execute(sqlTrigger);
            
             Logger.getLogger(ImportExperimentGEO.class.getName()).log(
                    Level.INFO, "adapted clazz: " + this.experimentdata.getClazz());
        } catch (Exception ex) {
            Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.SEVERE, "generateTable", ex);
            throw ex;
        }
    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<>();

        String[] entry;

        entry = new String[2];
        entry[ind_db] = "probeID";
        entry[ind_file] = "ID_REF";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "ratio";
        entry[ind_file] = "VALUE";
        _map.add(entry);

        return _map;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
            "probeID", "ratio"
        };
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<>(
                Arrays.asList(new String[]{
            ImportExperimentGEO.geo
        }));
    }

    @Override
    public String getName() {
        return "GEO Sample";
    }

    /**
     *
     * @return
     */
    @Override
    public String getFileInfoAsHTML() {
        String info = "<html>";
        info += ("time: " + this.time + "<br/>");

        info += "</html>";
        return info;
    }

    /**
     * find list of suitable arrays use barcode of FE File to get Protocoll
     *
     * @param type
     * @param method
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public List<PlatformDetail> getPlatformList(String type, String method) throws Exception {
        List<PlatformDetail> list = new Vector<>();
        try {

            if (method != null) {
                this.setMethod(Defaults.Method.toMethod(method));
                // find array, plattform
                //  this.method + type
            }
            list = PlatformService.getPlatformByTypeAndMethod(
                    this.method,
                    this.type);

            Logger
                    .getLogger(ImportExperimentGEO.class
                            .getName()).log(
                            Level.INFO,
                            list != null && list.size() > 0 ? list.get(0).getClass().getName() : "not found");
            return list;

        } catch (Exception ex) {

            Logger.getLogger(ImportExperimentGEO.class
                    .getName()).log(Level.SEVERE,
                            "Error: ", ex);
            throw ex;
            //return Collections.emptyList();
        }

    }

    @Override
    protected List<String[]> extendMapping() {
        List<String[]> _map = this.getMappingFile2DBColNames();

        for (int i = 0; i < _map.size(); i++) {

            if (_map.get(i)[ind_db].contentEquals("ratio")) {
                iratio = i;

            }

        }

        return _map;
    }
    int iratio = -1;

    /**
     *
     *
     * @param map
     * @param tmp
     * @return
     */
    @Override
    protected String[] modify(List<String[]> map, String[] tmp) {

        double ratio = Double.parseDouble(tmp[iratio] == null || tmp[iratio].contentEquals("") ? "0" : tmp[iratio]);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        DecimalFormat myFormatter = new DecimalFormat("0.#####", otherSymbols);
        tmp[iratio] = myFormatter.format(ratio);

        return tmp;

    }

    @Override
    public boolean hasSplitField() {
        return false;
    }

    @Override
    public void setSplitFieldName(String field) {
    }

    @Override
    public String getSplitFieldName() {
        return "";
    }

    /**
     *
     * @return
     */
    @Override
    public String[] getSplitFieldArray() {
        return new String[0];
    }

    /**
     *
     * @return
     */
    @Override
    public String getSplitPattern() {
        return "";
    }

    @Override
    protected String getEndDataTag() {
        return "END";
    }

    @Override
    @SuppressWarnings("empty-statement")
    protected void readRelease() {
        ;
    }

    @Override
    protected List<String[]> setSplitFieldCols(List<String[]> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
