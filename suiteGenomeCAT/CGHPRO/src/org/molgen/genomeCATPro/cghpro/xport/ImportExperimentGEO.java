package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportExperimentGEO
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * 
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 * 020813   kt	XPortImport createNewImport();
 * 170413   kt  allow empty chrom position in table definition
 * 010812   add getCreateTableSQLWithAnno
 */
public class ImportExperimentGEO extends ImportExperimentFile implements XPortExperimentFile {

    public final static String geo = "GEO_GSM_TXT";
    String time = "";
    //protected DecimalFormat myFormatter = new DecimalFormat("0.#####E0");

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
        return SpotX.class.getName();
    }

    @Override
    protected String getExperimentNameFromFile() {
        String name = this.inFile.getName();
        name = name.substring(0, name.lastIndexOf("."));
        return name;
    }

    @Override
    protected String getCreateTableSQL(String tableData) {
        String tablePlatform = this.experimentdata.getPlatformdata().getTableData();
        return ImportExperimentGEO.getCreateTableSQL(tableData, tablePlatform);
    }

    public static String getCreateTableSQL(String tableData, String tablePlatform) {
        String sql = new String(" CREATE TABLE " + tableData + " ( " +
                "id INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                "probeID varchar(255) NOT NULL default '', " +
                "probeName varchar(255) , " +
                "geneName varchar(255), " +
                "chrom varChar(45) default ''," +
                "chromStart int(10) unsigned default 0," +
                "chromEnd int(10) unsigned default 0," +
                "DESCRIPTION varchar(255), " +
                "ratio DOUBLE, " +
                "PRIMARY KEY (id)," +
                //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd), " +
                "INDEX (geneName (10))  " +
                " ) TYPE=MyISAM");

        return sql;
    }

    /**
     * create table without primary key, table will be filled by select into,
     * primary key is added after select into
     */
    public static String getCreateTableSQLWithAnno(
            String tableData, String tablePlatform, String annoColName) {



        String sql = new String(" CREATE TABLE " + tableData + " ( " +
                "id INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                "probeID varchar(255) NOT NULL default '', " +
                "probeName varchar(255), " +
                "geneName varchar(255), " +
                "chrom varchar(45) default ''," +
                "chromStart int(10) unsigned default 0," +
                "chromEnd int(10) unsigned default 0," +
                "DESCRIPTION varchar(255), " +
                "ratio DOUBLE, " +
                "gc_position LINESTRING NOT NULL," +
                annoColName + " varchar(255) NOT NULL default '' ," +
                "PRIMARY KEY (id)," +
                //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd), " +
                "INDEX (geneName (10))  " +
                " ) TYPE=MyISAM");

        return sql;
    }

    @Override
    public void generateTable(String tabledata) throws Exception {
        Statement s;
        try {
            //con = Database.getDBConnection(Defaults.localDB);
            s = con.createStatement();
            s.execute(
                    "DROP TABLE if EXISTS " + tabledata);
            String sql = this.getCreateTableSQL(tabledata);
            Logger.getLogger(ImportExperimentGEO.class.getName()).log(
                    Level.INFO,
                    sql);
            s.execute(sql);

            // update insert row with data from annotation table 
            String triggername = "tr_position_" + this.experimentdata.getTableData();
            String tablePlatform = this.experimentdata.getPlatformdata().getTableData();
            try {
                s.execute("DROP TRIGGER " + triggername);
            } catch (SQLException sQLException) {
            }
            sql = "CREATE TRIGGER " + triggername +
                    " BEFORE INSERT ON " + this.experimentdata.getTableData() +
                    "  FOR EACH ROW " +
                    "BEGIN " +
                    "DECLARE done INT DEFAULT 0;  " +
                    "DECLARE varRefseq varchar(255);  " +
                    "DECLARE varGene varchar(255); " +
                    "DECLARE varDesc varchar(255); " +
                    "DECLARE varChrom varchar(45);  " +
                    "DECLARE varStart INT; " +
                    "DECLARE varStop INT;   " +
                    "DECLARE cs CURSOR FOR  SELECT REFSEQ, GENE_SYMBOL, Description, chrom ,chromStart, chromEnd FROM " +
                    tablePlatform + " WHERE " + tablePlatform + ".probeName = new.probeID; " +
                    "DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1; " +
                    "OPEN cs;  " +
                    "FETCH cs INTO varRefseq, varGene, varDesc, varChrom, varStart, varStop;  " +
                    "IF NOT done THEN " +
                    //"CLOSE cs; " +
                    " SET done = \'Position not found in annotation table\'; " +
                    "END IF;  " +
                    " SET new.chrom = varChrom;  " +
                    " SET new.chromStart = varStart;  " +
                    " SET new.chromEnd = varStop;  " +
                    " SET new.probeName = varRefseq;  " +
                    " SET new.Description = varDesc;  " +
                    " SET new.geneName = varGene; " +
                    " CLOSE cs; " +
                    " END ;";


            Logger.getLogger(ImportExperimentGEO.class.getName()).log(
                    Level.INFO,
                    sql);

            s.execute(sql);
        } catch (Exception ex) {
            Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.SEVERE, "generateTable", ex);
            throw ex;
        }
    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();


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

    public String[] getDBColNames() {
        return new String[]{
                    "probeID", "ratio"
                };
    }

    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{
                    ImportExperimentGEO.geo
                }));
    }

    public String getName() {
        return new String("GEO Sample");
    }

    public String getFileInfoAsHTML() {
        String info = "<html>";
        info += ("time: " + this.time + "<br/>");

        info += "</html>";
        return info;
    }

    /**
     * find list of suitable arrays
     * use barcode of FE File to get Protocoll
     * 
     * @return
     */
    public List<PlatformDetail> getPlatformList(String type, String method) throws Exception {
        List<PlatformDetail> list = new Vector<PlatformDetail>();
        try {

            if (method != null) {
                this.setMethod(Defaults.Method.toMethod(method));
            // find array, plattform
            //  this.method + type
            }
            list = PlatformService.getPlatformByTypeAndMethod(
                    this.method,
                    this.type);


            Logger.getLogger(ImportExperimentGEO.class.getName()).log(
                    Level.INFO,
                    list != null && list.size() > 0 ? list.get(0).getClass().getName() : "not found");
            return list;
        } catch (Exception ex) {

            Logger.getLogger(ImportExperimentGEO.class.getName()).log(Level.SEVERE,
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
    protected String[] modify(List<String[]> map, String[] tmp) {

        double ratio = Double.parseDouble(tmp[iratio] == null || tmp[iratio].contentEquals("") ? "0" : tmp[iratio]);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        DecimalFormat myFormatter = new DecimalFormat("0.#####", otherSymbols);
        tmp[iratio] = myFormatter.format(ratio);

        return tmp;


    }

    public boolean hasSplitField() {
        return false;
    }

    public void setSplitFieldName(String field) {
        ;
    }

    public String getSplitFieldName() {
        return "";
    }

    public String[] getSplitFieldArray() {
        return new String[0];
    }

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
