package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportBAC
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Jan 19, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.molgen.genomeCATPro.cghpro.chip.SpotBAC;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.MyMath;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 * 100716 review
 * 020813 kt	XPortImport createNewImport(); 170413 kt allow empty chrom position
 * in table definition 160413 kt header tags modified 010812 add
 * getCreateTableSQLWithAnno
 */
public class ImportBAC extends ImportExperimentFile implements XPortExperimentFile {

    public final static String bac = "GENEPIX_GPR_TXT";
    String time = "";
    //protected DecimalFormat myFormatter = new DecimalFormat("0.#####E0");

    @Override
    protected String getEndMetaDataTag() {

        return "\"Type=GenePix";

    }

    @Override
    public ImportBAC createNewImport() {
        return new ImportBAC();
    }

    @Override
    protected boolean isCommentLine(String is) {
        if (is.indexOf("\"") == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isHasHeader() {
        return true;
    }

    @Override
    protected boolean isHeaderLine(String is) {
        if (is.contains("\"Block\"")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getDataClazz() {
        return SpotBAC.class.getName();
    }

    @Override
    protected String getExperimentNameFromFile() {
        String name = this.inFile.getName();
        name = name.substring(0, name.indexOf(".gpr"));
        return name;
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

    /**
     *
     * @param tableData
     * @param tablePlatform
     * @return
     
    @Override
    public String getCreateTableSQL(String tableData, String tablePlatform) {
        return (new SpotBAC()).getCreateTableSQL(this.experimentdata);*/
        /*
        String sql = "CREATE TABLE " + tableData + "("
                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "block int(4) NOT NULL default '0',"
                + "col int(4) NOT NULL default '0',"
                + "row int(4) NOT NULL default '0',"
                + "probeID varchar(255) NOT NULL default '',"
                + "probeName varchar(255) ,"
                + "f635Mean float NOT NULL default '0',"
                + "b635Mean float NOT NULL default '0',"
                + "b635sd float NOT NULL default '0',"
                + "f532Mean float NOT NULL default '0',"
                + "b532Mean float NOT NULL default '0',"
                + "b532sd float NOT NULL default '0',"
                + "snr635 double NOT NULL default '0',"
                + "snr532 double NOT NULL default '0',"
                + "f635 double NOT NULL default '0',"
                + "f532 double NOT NULL default '0',"
                + "ratio double NOT NULL default '0',"
                + "chrom varChar(45)  DEFAULT '',"
                + "chromStart int(10) unsigned default 0,"
                + "chromEnd int(10) unsigned  default 0,"
                + " ifExcluded int(1)  NOT NULL default '0',"
                + " controlType int NOT NULL default 0,"
                + "PRIMARY KEY (id),"
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "CONSTRAINT `fk_Data_Spots_" + tablePlatform + "` "
                + "FOREIGN KEY (`probeID` ) "
                + " REFERENCES `" + tablePlatform + "` "
                + " (`probeName`) "
                + " ON DELETE NO ACTION "
                + " ON UPDATE NO ACTION) ";

        return sql;*/
    
    /**
     * create table without primary key, table will be filled by select into,
     * primary key is added after select into
     
    public static String getCreateTableSQLWithAnno(
            String tableData, String tablePlatform, String annoColName) {

        String sql = "CREATE TABLE " + tableData + "("
                + "id BIGINT UNSIGNED ,"
                + "block int(4) NOT NULL default '0',"
                + "col int(4) NOT NULL default '0',"
                + "row int(4) NOT NULL default '0',"
                + "probeID varchar(255) NOT NULL default '',"
                + "probeName varchar(255) ,"
                + "f635Mean float NOT NULL default '0',"
                + "b635Mean float NOT NULL default '0',"
                + "b635sd float NOT NULL default '0',"
                + "f532Mean float NOT NULL default '0',"
                + "b532Mean float NOT NULL default '0',"
                + "b532sd float NOT NULL default '0',"
                + "snr635 double NOT NULL default '0',"
                + "snr532 double NOT NULL default '0',"
                + "f635 double NOT NULL default '0',"
                + "f532 double NOT NULL default '0',"
                + "ratio double NOT NULL default '0',"
                + "chrom varChar(45) unsigned '',"
                + "chromStart int(10) unsigned default 0,"
                + "chromEnd int(10) unsigned  default 0,"
                + " ifExcluded int(1)  NOT NULL default '0',"
                + " controlType int NOT NULL default 0,"
                + "gc_position LINESTRING NOT NULL,"
                + annoColName + " varchar(255) NOT NULL default '' ,"
                + "INDEX (id), "
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "CONSTRAINT `fk_Data_Spots_" + tablePlatform + "` "
                + "FOREIGN KEY (`probeID` ) "
                + " REFERENCES `" + tablePlatform + "` "
                + " (`probeName`) "
                + " ON DELETE NO ACTION "
                + " ON UPDATE NO ACTION) ";

        return sql;
        * **/
    
    /**
     * 
     * @param tabledata
     * @throws Exception 
     */
    @Override
    public void generateTable(String tabledata) throws Exception {
        Statement s;
        try {
             s = con.createStatement();
            s.execute(
                    "DROP TABLE if EXISTS " + tabledata);
            String sql = (new SpotBAC()).getCreateTableSQL(this.experimentdata);
            Logger.getLogger(ImportBAC.class.getName()).log(
                    Level.INFO,
                    sql);
            s.execute(sql);

            // update insert row with data from annotation table 
            String triggername = "tr_position_" + this.experimentdata.getTableData();
            String tableAnno = this.experimentdata.getPlatformdata().getTableData();
            try {
                s.execute(" DROP TRIGGER if EXISTS " + triggername);
            } catch (SQLException sQLException) {
            }
            sql = ""
                    + "CREATE TRIGGER " + triggername
                    + " BEFORE INSERT ON " + this.experimentdata.getTableData()
                    + "  FOR EACH ROW "
                    + "BEGIN "
                    + "DECLARE done INT DEFAULT 0;  "
                    + "DECLARE varAlias varchar(100);  "
                    + "DECLARE varChrom varchar(45);  "
                    + "DECLARE varStart INT; "
                    + "DECLARE varStop INT;   "
                    + "DECLARE cs CURSOR FOR  SELECT chrom ,chromStart, chromEnd, alias FROM "
                    + tableAnno + " WHERE " + tableAnno + ".probeName = new.probeID; "
                    + "DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1; "
                    + "OPEN cs;  "
                    + "FETCH cs INTO varChrom, varStart, varStop, varAlias;  "
                    + "IF NOT done THEN "
                    + //"CLOSE cs; " +
                    " SET done = \'Position not found in annotation table\'; "
                    + "END IF;  "
                    + //" SELECT \'FOUND POSITION \', chrom, start, stop; " +
                    " SET new.chrom = varChrom;  "
                    + " SET new.chromStart = varStart;  "
                    + " SET new.chromEnd = varStop;  "
                    + " SET new.probeName = varAlias;  "
                    + "CLOSE cs;  "
                    + " END ;";

            Logger.getLogger(ImportBAC.class.getName()).log(
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
        List<String[]> _map = new Vector<>();

        String[] entry;

        entry = new String[2];
        entry[ind_db] = "probeID";
        entry[ind_file] = "\"ID\"";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "block";
        entry[ind_file] = "\"Block\"";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "row";
        entry[ind_file] = "\"Row\"";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "col";
        entry[ind_file] = "\"Column\"";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "controlType";
        entry[ind_file] = "\"Flags\"";
        _map.add(entry);

        // foreground Mean
        entry = new String[2];
        entry[ind_db] = "f635Mean";
        entry[ind_file] = "\"F635 Mean\"";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "f532Mean";
        entry[ind_file] = "\"F532 Mean\"";
        _map.add(entry);

        // background Mean
        entry = new String[2];
        entry[ind_db] = "b635Mean";
        entry[ind_file] = "\"B635 Mean\"";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "b532Mean";
        entry[ind_file] = "\"B532 Mean\"";
        _map.add(entry);

        // background SD
        entry = new String[2];
        entry[ind_db] = "b532sd";
        entry[ind_file] = "\"B532 SD\"";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "b635sd";
        entry[ind_file] = "\"B635 SD\"";
        _map.add(entry);

        return _map;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
            "probeID",
            "block", "row", "col",
            "controlType",
            "f635Mean", "b635Mean", "b635sd",
            "f532Mean", "b532Mean", "b532sd"
        };
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<>(
                Arrays.asList(new String[]{
            ImportBAC.bac
        }));
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

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

            Logger.getLogger(ImportBAC.class.getName()).log(
                    Level.INFO,
                    list != null && list.size() > 0 ? list.get(0).getClass().getName() : "not found");
            return list;
        } catch (Exception ex) {

            Logger.getLogger(ImportBAC.class.getName()).log(Level.SEVERE,
                    "Error: ", ex);
            throw ex;
            //return Collections.emptyList();
        }

    }

    @Override
    public Vector<Vector<String>> readData(int i) {
        return this.readHeaderGPR(i);
    }

    Vector<Vector<String>> readHeaderGPR(int nofLines) {
        Vector<Vector<String>> dataList = new Vector<Vector<String>>();
        int lines = 0;

        boolean header = false;

        Vector<String> dataLine;

        String is = null;

        Pattern pattern;
        Matcher matcher;
        try {
            inBuffer = new BufferedReader(new FileReader(inFile));

            while ((is = inBuffer.readLine()) != null) {
                if (is == null) {
                    continue;
                }
                if (this.isHeaderLine(is)) {
                    this.fileColNames = is.split("\t");
                    continue;
                }
                if (is.indexOf("\"") == 0) {
                    header = true;
                    if (is.contains("\"DateTime")) {

                        pattern = Pattern.compile("DateTime=(\\d+)/(\\d+)/(\\d+)\\s(\\d+):(\\d+):(\\d+)");
                        matcher = pattern.matcher(is);
                        if (matcher.find()) {

                            this.time = (matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + " "
                                    + matcher.group(4) + ":" + matcher.group(5) + ":" + matcher.group(6));

                        }

                    }
                    continue;
                }

                if (header) {
                    //had header but no header line now

                    dataLine = new Vector<String>(Arrays.asList(is.split("\t")));
                    dataList.add(dataLine);
                }
                if (lines++ > nofLines) {
                    break;
                }
            }

        } catch (Exception e) {
            Logger.getLogger(ImportBAC.class.getName()).log(Level.SEVERE, null, e);

        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(ImportBAC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dataList;
    }

    @Override
    protected List<String[]> extendMapping() {
        List<String[]> _map = this.getMappingFile2DBColNames();
        String[] e = new String[2];
        e[ind_db] = "snr635";
        e[ind_file] = "";

        _map.add(e);
        e = new String[2];
        e[ind_db] = "snr532";
        e[ind_file] = "";
        _map.add(e);

        e = new String[2];
        e[ind_db] = "f635";
        e[ind_file] = "";
        _map.add(e);

        e = new String[2];
        e[ind_db] = "f532";
        e[ind_file] = "";
        _map.add(e);

        e = new String[2];
        e[ind_db] = "ratio";
        e[ind_file] = "";
        _map.add(e);

        e = new String[2];
        e[ind_db] = "ifExcluded";
        e[ind_file] = "";
        _map.add(e);

        for (int i = 0; i < _map.size(); i++) {
            if (_map.get(i)[ind_db].contentEquals("f635Mean")) {
                if635Mean = i;

            }
            if (_map.get(i)[ind_db].contentEquals("b635Mean")) {
                ib635Mean = i;

            }
            if (_map.get(i)[ind_db].contentEquals("b635sd")) {
                ib635std = i;

            }

            if (_map.get(i)[ind_db].contentEquals("f635")) {
                if635 = i;

            }
            if (_map.get(i)[ind_db].contentEquals("snr635")) {
                if635snr = i;

            }

            if (_map.get(i)[ind_db].contentEquals("f532Mean")) {
                if532Mean = i;

            }
            if (_map.get(i)[ind_db].contentEquals("b532Mean")) {
                ib532Mean = i;

            }
            if (_map.get(i)[ind_db].contentEquals("b532sd")) {
                ib532std = i;

            }

            if (_map.get(i)[ind_db].contentEquals("f532")) {
                if532 = i;

            }
            if (_map.get(i)[ind_db].contentEquals("snr532")) {
                if532snr = i;

            }
            if (_map.get(i)[ind_db].contentEquals("ratio")) {
                iratio = i;

            }
            if (_map.get(i)[ind_db].contentEquals("controlType")) {
                icontrolType = i;

            }
            if (_map.get(i)[ind_db].contentEquals("ifExcluded")) {
                ifexcluded = i;

            }

        }

        return _map;
    }
    int if532Mean = -1;
    int ib532Mean = -1;
    int ib532std = -1;
    int if532snr = -1;
    int if532 = -1;
    int if635Mean = -1;
    int ib635Mean = -1;
    int ib635std = -1;
    int if635snr = -1;
    int if635 = -1;
    int icontrolType = -1;
    int iratio = -1;
    int ifexcluded = -1;

    /**
     * 1) distinguish between one channel/two channel 2) set g/rProcesssedSignal
     * to 0 if pvalueLogRatio == 1 and LogRatio == 0 (bad quality)
     *
     * @param map
     * @param tmp
     * @return
     */
    protected String[] modify(List<String[]> map, String[] tmp) {

        // todo logratio aus r/g (abhängig ob dyeswap 
        int control = Integer.parseInt(tmp[icontrolType]);

        double fMean = Double.parseDouble(tmp[if532Mean]);
        double bMean = Double.parseDouble(tmp[ib532Mean]);
        double bStd = Double.parseDouble(tmp[ib532std]);
        boolean ifExcluded = false;

        if (control < 0) {
            ifExcluded = false;
        }
        double snr = (fMean - bMean) / bStd;
        if (Double.isNaN(snr)) {
            snr = 0;
            ifExcluded = true;
        }
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        DecimalFormat myFormatter = new DecimalFormat("0.#####", otherSymbols);
        tmp[if532snr] = myFormatter.format(snr);
        double ratio532 = MyMath.formatDoubleValue(MyMath.log2(fMean), 3);
        tmp[if532] = myFormatter.format(ratio532);

        fMean = Double.parseDouble(tmp[if635Mean]);
        bMean = Double.parseDouble(tmp[ib635Mean]);
        bStd = Double.parseDouble(tmp[ib635std]);

        snr = (fMean - bMean) / bStd;
        if (Double.isNaN(snr)) {
            snr = 0;
            ifExcluded = true;
        }
        tmp[if635snr] = myFormatter.format(snr);

        double ratio635 = MyMath.formatDoubleValue(MyMath.log2(fMean), 3);

        tmp[if635] = myFormatter.format(ratio635);

        double ratio;
        if (this.isDyeSwap()) {
            ratio = ratio635 - ratio532;
        } else {
            ratio = ratio532 - ratio635;
        }
        tmp[iratio] = myFormatter.format(ratio);
        tmp[ifexcluded] = ifExcluded ? "1" : "0";

        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != null) {
                tmp[i] = Pattern.compile("([\"])").matcher(tmp[i]).replaceAll("");
            }
            //System.out.print(tmp[i]+"\\t");

        }
        //System.out.println("");
        return tmp;

        //calc Spot.java
        // this.snr635 = (f635Mean - b635Mean) / b635Std;
        // this.snr532 = (f532Mean - b532Mean) / b532Std;
        /*if (Double.isNaN(this.snr635)) {
    
    this.snr635 = 0;
    System.out.println(this.id + " snr 635 NAN!!");
    setIfExcluded(-1);
    }
    if (Double.isNaN(this.snr532)) {
    this.snr532 = 0;
    System.out.println(this.id + " snr 532 NAN!!");
    setIfExcluded(-1);
    }*/
        //BatchAnalysis.java

        /* calc f635 and f532   chip.setSpotByMean();
     * includeSpotsByNegSignal();
    Iterator e = allSpots.iterator();
    Spot currentSpot;
    while(e.hasNext()){
    currentSpot = (Spot)e.next();
    currentSpot.setF635(MyMath.formatDoubleValue(MyMath.log2(currentSpot.f635Mean), 3));
    currentSpot.setF635Norm(MyMath.formatDoubleValue(MyMath.log2(currentSpot.f635Mean),3));
    currentSpot.setF532(MyMath.formatDoubleValue(MyMath.log2(currentSpot.f532Mean),3));
    currentSpot.setF532Norm(MyMath.formatDoubleValue(MyMath.log2(currentSpot.f532Mean),3));
    currentSpot.setNormalRatio(currentSpot.f532Norm - currentSpot.f635Norm);
    }
    ifBackgroundSub = false;
    averageRatio();
    setIfNormalize(false);
    setIfMa(false);
    setIfHmm(false);
    setIfCbs(false);
     * calc ratio as  currentSpot.f532-currentSpot.f635;
         */
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
}
