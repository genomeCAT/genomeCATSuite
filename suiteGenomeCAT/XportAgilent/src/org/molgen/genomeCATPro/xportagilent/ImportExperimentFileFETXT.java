package org.molgen.genomeCATPro.xportagilent;

/**
 * @name ImportExperimentFileFETXT
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.cghpro.xport.ImportExperimentFile;
import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.data.ISpot;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.PlatformService;
import org.molgen.genomeCATPro.xportagilent.FEProtocollList.FEProtocoll;

/**
 * 020813 kt	XPortImport createNewImport(); 170413 kt allow empty chrom position
 * in table definition 100812 add getCreateTableSQLWithAnno 051012
 * getExperimentNameFromFile cut off all endings like bed,txt
 */
public class ImportExperimentFileFETXT extends ImportExperimentFile implements XPortExperimentFile {

    public final static String fe = "Agilent_FE_TXT";

    boolean hasGene = false;
    ISpot _spot;
    private String barcode;
    private String protocoll = null;
    private FEProtocoll p = null;
    String metaHeaderTag = "FEPARAMS";
    String metaStatsTag = "STATS";
    String metaDataTag = "FEATURES";
    String dataTag = "DATA";

    public ImportExperimentFileFETXT() {
        this._spot = new SpotAgilent();
    }

    @Override
    public ImportExperimentFileFETXT createNewImport() {
        return new ImportExperimentFileFETXT();
    }

    @Override
    protected String getEndMetaDataTag() {
        return this.metaDataTag;
    }

    @Override
    protected boolean isCommentLine(String is) {
        if (!is.contains(dataTag)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isHeaderLine(String is) {

        return false;

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
        if (name.indexOf(".") > 0) {
            name = name.substring(0, name.indexOf("."));    //051012 kt cut off all endings like bed,txt

        }
        return name;
    }

    @Override
    protected void readRelease() {
        this.readHeaderFETXT(1);
    }

    @Override
    protected List<String[]> setSplitFieldCols(List<String[]> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /* 
    public static String getCreateTableSQL(String tableData, String tableAnno) {
        // 170812 kt test import without prim key auto

        String sql
                = " CREATE TABLE " + tableData + " ( "
                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeID INT UNSIGNED NOT NULL, "
                + "probeName varchar(255) NOT NULL, "
                + "chrom varChar(45)  DEFAULT \'\',"
                + "chromStart int(10) unsigned  default 0,"
                + "chromEnd int(10) unsigned  default 0,"
                + "controlType tinyint(1) unsigned NOT NULL default 0,"
                + "geneName varchar(255), "
                + "DESCRIPTION varchar(255), "
                + "SystematicName varchar(255), "
                + "rSignal DOUBLE, gSignal DOUBLE, "
                + "rgRatio10 DOUBLE, rgRatio10PValue DOUBLE, "
                + "ratio DOUBLE, "
                + "PRIMARY KEY (id),"
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "INDEX (geneName (10)) , "
                + "CONSTRAINT `fk_Data_Spots_" + tableAnno + "` "
                + "FOREIGN KEY (`probeName` ) "
                + " REFERENCES `" + tableAnno + "` "
                + " (`probeName`) "
                + " ON DELETE NO ACTION "
                + " ON UPDATE NO ACTION) ";
        return sql;
    }

     */
    /**
     * create table without primary key, table will be filled by select into,
     * primary key is added after select into
     */
    /*
    public static String getCreateTableSQLWithAnno(
            String tableData, String tableAnno, String annoColName) {
        String sql
                = " CREATE TABLE " + tableData + " ( "
                + "id BIGINT UNSIGNED NOT NULL ,"
                + "probeID INT UNSIGNED NOT NULL, "
                + "probeName varchar(255) NOT NULL, "
                + "chrom varChar(45)  DEFAULT \'\',"
                + "chromStart int(10) unsigned  default 0,"
                + "chromEnd int(10) unsigned  default 0,"
                + "controlType tinyint(1) unsigned NOT NULL default 0,"
                + "geneName varchar(255), "
                + "DESCRIPTION varchar(255), "
                + "SystematicName varchar(255), "
                + "rSignal DOUBLE, gSignal DOUBLE, "
                + "rgRatio10 DOUBLE, rgRatio10PValue DOUBLE, "
                + "ratio DOUBLE, "
                + "gc_position LINESTRING NOT NULL,"
                + annoColName + " varchar(255) NOT NULL default '' ,"
                + "INDEX (id),"
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "INDEX (geneName (10)) , "
                + "CONSTRAINT `fk_Data_Spots_" + tableAnno + "` "
                + "FOREIGN KEY (`probeName` ) "
                + " REFERENCES `" + tableAnno + "` "
                + " (`probeName`) "
                + " ON DELETE NO ACTION "
                + " ON UPDATE NO ACTION) ";
        return sql;
    }
     */
    /**
     * kt review 050716
     *
     *
     * @param tabledata
     */
    @Override
    public void generateTable(String tabledata) {
        Statement s;
        try {
            //con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
            s.execute(
                    "DROP TABLE if EXISTS " + tabledata);
            if (!this.hasGene) {
                this._spot = new SpotAgilentwoGene();
            };
            String sql = this._spot.getCreateTableSQL(experimentdata);
            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(
                    Level.INFO, "SQL: " + sql);
            s.execute(sql);
            this.experimentdata.setClazz(this.getDataClazz());
            // update insert row with data from annotation table 
            String triggername = "tr_position_" + this.experimentdata.getTableData();
            String tableAnno = this.experimentdata.getPlatformdata().getTableData();
            s.execute("DROP TRIGGER if EXISTS " + triggername);
            sql = ""
                    + "CREATE TRIGGER " + triggername
                    + " BEFORE INSERT ON " + this.experimentdata.getTableData()
                    + "  FOR EACH ROW "
                    + "BEGIN "
                    + "DECLARE done INT DEFAULT 0;  "
                    + "DECLARE varChrom varchar(45);  "
                    + "DECLARE varStart INT; "
                    + "DECLARE varStop INT;   "
                    + "DECLARE cs CURSOR FOR  SELECT chrom ,chromStart, chromEnd FROM "
                    + tableAnno + " WHERE " + tableAnno + ".probeName = new.probeName; "
                    + //" AND " + tableAnno + ".probeID = new.probeID; " +
                    "DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1; "
                    + "OPEN cs;  "
                    + "FETCH cs INTO varChrom, varStart, varStop;  "
                    + "IF NOT done THEN "
                    + //"CLOSE cs; " +
                    " SET done = \'Position not found in annotation table\'; "
                    + "END IF;  "
                    + //" SELECT \'FOUND POSITION \', chrom, start, stop; " +
                    " SET new.chrom = varChrom;  "
                    + " SET new.chromStart = varStart;  "
                    + " SET new.chromEnd = varStop;  "
                    + "CLOSE cs;  "
                    + " END ;";

            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(
                    Level.INFO,
                    sql);

            s.execute(sql);
        } catch (Exception ex) {
            Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.SEVERE, "generateTable", ex);
        }
    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<>();

        String[] entry;

        entry = new String[2];
        entry[ind_db] = "probeID";
        entry[ind_file] = "FeatureNum";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "probeName";
        entry[ind_file] = "ProbeName";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "geneName";
        entry[ind_file] = "GeneName";
        _map.add(entry);
        if (this.nofChannel == 1) {
            entry = new String[2];
            entry[ind_db] = "rSignal";
            entry[ind_file] = "";
            _map.add(entry);

            entry = new String[2];
            entry[ind_db] = "gSignal";
            entry[ind_file] = "";
            _map.add(entry);
        } else {
            entry = new String[2];
            entry[ind_db] = "rSignal";
            entry[ind_file] = "rProcessedSignal";
            _map.add(entry);

            entry = new String[2];
            entry[ind_db] = "gSignal";
            entry[ind_file] = "gProcessedSignal";
            _map.add(entry);
        }
        entry = new String[2];
        entry[ind_db] = "SystematicName";
        entry[ind_file] = "SystematicName";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "controlType";
        entry[ind_file] = "ControlType";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "rgRatio10";
        entry[ind_file] = "LogRatio";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "rgRatio10PValue";
        entry[ind_file] = "PValueLogRatio";
        _map.add(entry);
        return _map;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
            "probeID", "probeName", "SystematicName",
            "controlType",
            "geneName",
            "rSignal", "gSignal",
            "rgRatio10", "rgRatio10PValue"
        };
    }

    /**
     *
     * @return string import module for ui
     */
    @Override
    public Vector<String> getImportType() {
        return new Vector<>(
                Arrays.asList(new String[]{
            ImportExperimentFileFETXT.fe
        }));
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void readFileColNames() {
        if (this.fileColNames == null) {
            this.readHeaderFETXT(1);
        }
    }

    @Override
    public String getFileInfoAsHTML() {
        String info = "<html>";
        info += ("protocoll: " + this.protocoll + "<br/>");
        info += ("barcode: " + this.barcode + "<br/>");
        info += ("nofChannel: " + this.nofChannel + "<br/>");
        info += ("genome release: " + (this.release != null ? this.release.toString() : "<none>") + "<br/>");
        info += ("Method: " + (p != null ? (this.p.getMethod() != null ? this.p.getMethod() : "<none>") : "<none>") + "<br/>");
        info += ("Type: " + (p != null ? this.p.getType() : "<none>") + "<br/>");
        info += "</html>";
        return info;
    }

    /**
     * find list of suitable arrays use barcode of FE File to get Protocoll
     *
     * @param type
     * @param method
     * @return
     * @throws Exception
     */
    @Override
    public List<PlatformDetail> getPlatformList(String type, String method) throws Exception {
        boolean userdef = (type != null || method != null);

        List<PlatformDetail> list = new Vector<PlatformDetail>();
        try {
            if (!userdef && this.barcode != null) {
                // find array, plattform in db with
                // desc containing Barcode
                list = PlatformService.getAgilentPlatformByBarcode(this.barcode);

                Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.INFO,
                        " found platform {0} with barcode {1}", new Object[]{list.toString(), barcode});
                return list;
            }
            if (!userdef && list.isEmpty()) {
                // get platformlist with header infos
                this.p = FEProtocollList.get(this.protocoll);

            }
            if (userdef || (list.isEmpty() && p == null)) {
                // user select for type, method or nothing else found
                if (method == null && type == null) //throw new RuntimeException("Unknown Protokoll: " + this.protocoll);
                {
                    return Collections.EMPTY_LIST;
                } else {

                    this.p = new FEProtocoll(
                            "",
                            "",
                            Defaults.Type.toType(type),
                            Defaults.Method.toMethod(method), "", "");
                    Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.INFO,
                            " get platform userdef {0} {1}", new Object[]{type, method});

                }

            }
            // find array, plattform
            // passend zu this.
            // this.release, this.protocoll.method + type
            list = PlatformService.getPlatformByTypeAndMethod(
                    p.method,
                    p.type);
            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.INFO,
                    " found platform {0} with protocol {1}",
                    new Object[]{list.toString(), p.toFullString()});

            return list;
        } catch (Exception ex) {

            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.SEVERE,
                    "Error: ", ex);
            throw ex;
            //return Collections.emptyList();
        }

    }

    @Override
    public String getMethod() {
        if (this.p != null) {
            return p.method != null ? p.method.toString() : "";
        } else {
            return super.getMethod();
        }
    }

    @Override
    public String getType() {
        if (this.p != null) {
            return p.type != null ? p.type.toString() : "";
        } else {
            return super.getType();
        }
    }

    @Override
    public ExperimentDetail initExperimentDetail() {
        ExperimentDetail d = super.initExperimentDetail();
        d.setNofChannel(this.nofChannel);
        FEProtocoll _p;
        if (this.p == null) {
            _p = FEProtocollList.get(this.protocoll);
        } else {
            _p = this.p;
        }
        if (_p != null) {
            d.setHybProtocoll(_p.getHybridisation());
            d.setProcessing(d.getProcessing());
        }

        return d;
    }

    @Override
    public Vector<Vector<String>> readData(int i) {
        return this.readHeaderFETXT(i);
    }

    Vector<Vector<String>> readHeaderFETXT(int nofLines) {
        Vector<Vector<String>> dataList = new Vector<Vector<String>>();
        int lines = 0;

        boolean header = false;
        boolean data = false;
        boolean stats = false;
        String iss[] = null;
        Vector<String> dataLine;
        String is = null;
        int indexRelease = -1;
        int indexBarcode = -1;
        int indexProtocoll = -1;
        int indexNofChannel = -1;

        // reset
        this.p = null;
        this.method = null;
        this.type = null;

        try {
            inBuffer = new BufferedReader(new FileReader(inFile));

            while ((is = inBuffer.readLine()) != null) {
                if (is == null) {
                    continue;
                }
                if (is.contains(this.metaHeaderTag)) {
                    header = true;
                    data = false;
                    stats = false;
                    iss = is.split("\t");
                    for (int i = 0; i < iss.length; i++) {
                        if (iss[i].equals("Grid_GenomicBuild")) {
                            indexRelease = i;
                            continue;
                        }
                        if (iss[i].equals("FeatureExtractor_Barcode")) {
                            indexBarcode = i;
                            continue;
                        }
                        if (iss[i].equals("Protocol_Name")) {
                            indexProtocoll = i;
                            continue;
                        }
                        if (iss[i].equals("Scan_NumChannels")) {
                            indexNofChannel = i;

                        }
                    }
                    continue;
                }
                if (is.contains(this.metaStatsTag)) {
                    header = false;
                    data = false;
                    stats = true;
                    continue;
                }
                if (is.contains(this.metaDataTag)) {
                    header = false;
                    data = true;
                    stats = false;
                    iss = is.split("\t");
                    this.fileColNames = iss;
                    continue;
                }
                if (is.contains(this.dataTag)) {
                    iss = is.split("\t");
                    if (data) {
                        if (++lines > nofLines) {
                            break;
                        }
                        dataLine = new Vector<String>(Arrays.asList(iss));
                        dataList.add(dataLine);
                    }
                    if (header) {

                        if (indexProtocoll >= 0) {
                            this.protocoll = iss[indexProtocoll];
                            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(
                                    Level.INFO, "Found in Header: Protocoll " + this.protocoll);

                        }
                        if (indexRelease >= 0) {
                            this.release = GenomeRelease.toRelease(iss[indexRelease]);
                            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.INFO,
                                    "Found in Header: Release " + this.release);

                        }
                        if (indexBarcode >= 0) {
                            this.barcode = iss[indexBarcode];
                            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.INFO,
                                    "Found in Header: Barcode " + this.barcode);

                        }
                        if (indexNofChannel >= 0) {
                            this.nofChannel = Integer.parseInt(iss[indexNofChannel]);
                            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.INFO,
                                    "Found in Header: NofChannel " + this.nofChannel);

                        } else {
                            this.nofChannel = 2;
                        }
                        continue;
                    }
                }
            }
            /*120413 kt test
        if (!data && !header) {
        throw new RuntimeException("Read header - wrong format");
        }*/
        } catch (Exception e) {
            Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.SEVERE, null, e);

        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(ImportExperimentFileFETXT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dataList;
    }
    int ir = -1;
    int ig = -1;
    int iratio = -1;
    int iratio10 = -1;
    int ipratio10 = -1;
    int ichrom = -1;
    int iControl = -1;

    @Override
    protected List<String[]> extendMapping() {
        List<String[]> _map = this.getMappingFile2DBColNames();
        String[] e = new String[2];
        e[ind_db] = "ratio";
        e[ind_file] = "";
        _map.add(e);

        for (int i = 0; i < _map.size(); i++) {
            if (_map.get(i)[ind_db].contentEquals("chrom")) {
                ichrom = i;
                continue;
            }
            if (_map.get(i)[ind_db].contentEquals("controlType")) {
                iControl = i;
                continue;

            }
            if (_map.get(i)[ind_db].contentEquals("rSignal")) {
                ir = i;
                continue;
            }
            if (_map.get(i)[ind_db].contentEquals("gSignal")) {
                ig = i;
                continue;

            }
            if (_map.get(i)[ind_db].contentEquals("ratio")) {
                iratio = i;
                continue;

            }
            if (_map.get(i)[ind_db].contentEquals("rgRatio10")) {
                iratio10 = i;
                continue;

            }
            if (_map.get(i)[ind_db].contentEquals("rgRatio10PValue")) {
                ipratio10 = i;
                continue;

            }
        }

        return _map;
    }

    /**
     * 1) distinguish between one channel/two channel 2) set g/rProcesssedSignal
     * to 0 if pvalueLogRatio == 1 and LogRatio == 0 (bad quality)
     *
     * @param map
     * @param tmp
     * @return
     */
    @Override
    protected String[] modify(List<String[]> map, String[] tmp) {

        if (iControl >= 0) {
            if (tmp[iControl].contentEquals("1") || tmp[iControl].contentEquals("-1")) {
                tmp[iControl] = "true";
            } else {
                tmp[iControl] = "false";
            }

        }

        double ratio = Double.parseDouble(tmp[iratio10] != null ? tmp[iratio10] : "0");

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        DecimalFormat myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
        tmp[iratio10] = myFormatter.format(ratio * -1);

        if (this.nofChannel > 1) {
            double pratio10 = Double.parseDouble(tmp[ipratio10]);
            if (pratio10 == 1 && ratio == 0) {
                ratio = 0;
            } else {
                double rSignal = Double.parseDouble(tmp[ir]);
                double gSignal = Double.parseDouble(tmp[ig]);

                if (this.isDyeSwap()) {
                    ratio = Math.log(rSignal / gSignal) / Math.log(2);
                } else {
                    ratio = Math.log(gSignal / rSignal) / Math.log(2);
                }
            }
        } else {
            if (ir > 0) {
                ratio = Double.parseDouble(tmp[ir]);
            } else if (ig > 0) {
                ratio = Double.parseDouble(tmp[ig]);
            }
            ratio = Math.log(ratio) / Math.log(2);
        }
        tmp[iratio] = myFormatter.format(ratio);
        return tmp;
    }

    @Override
    public String setMappingFile2DBColNames(List<String[]> _map) {
        this.hasGene = false;
        if (this.nofChannel == 1) {
            int rgCount = 0;
            for (int i = 0; i < _map.size(); i++) {

                //
                if (_map.get(i)[ind_db].contentEquals("rSignal")) {
                    if (!_map.get(i)[ind_file].contentEquals("")) {
                        rgCount++;
                    }
                }
                if (_map.get(i)[ind_db].contentEquals("gSignal")) {
                    if (!_map.get(i)[ind_file].contentEquals("")) {
                        rgCount++;
                    }
                }
            }
            if (rgCount == 0 || rgCount == 2) {
                return "either mapping for rSignal OR gSignal must be set!";
            }
        }
//kt rev 300716
        for (int i = 0; i < _map.size(); i++) {
            if (_map.get(i)[ind_file].toUpperCase().contains("GENE")) {
                this.hasGene = true;
            }
        }
        super.setMappingFile2DBColNames(_map);
        return null;
    }

    @Override
    public boolean hasSplitField() {
        return false;
    }

    @Override
    public void setSplitFieldName(String field) {
        ;
    }

    @Override
    public String getSplitFieldName() {
        return "";
    }

    @Override
    public String[] getSplitFieldArray() {
        return new String[0];
    }

    @Override
    public String getSplitPattern() {
        return "";
    }

    @Override
    protected String getEndDataTag() {
        return "END";
    }
}
