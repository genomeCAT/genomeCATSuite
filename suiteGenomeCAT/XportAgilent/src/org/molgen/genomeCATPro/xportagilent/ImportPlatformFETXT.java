/**
 * @name ImportPlatformGEO
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
package org.molgen.genomeCATPro.xportagilent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.cghpro.xport.ImportPlatform;
import org.molgen.genomeCATPro.cghpro.xport.XPortPlatform;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.xportagilent.FEProtocollList.FEProtocoll;

/**
 * 020813 kt	XPortImport createNewImport(); 120413 kt negative controltype
 * 120413 kt import 1...X,Y or 1...23,24 as chr...
 */
public class ImportPlatformFETXT extends ImportPlatform implements XPortPlatform {

    public final static String platform_fe = "Agilent_FE_TXT";
    private String protocoll;
    private String barcode;
    private int nofChannel;
    FEProtocollList.FEProtocoll p;

    public ImportPlatformFETXT createNewImport() {
        return new ImportPlatformFETXT();
    }

    @Override
    public boolean isHasHeader() {
        return true;
    }

    @Override
    public String getName() {
        return platform_fe;
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{
            ImportPlatformFETXT.platform_fe
        }));
    }

    @Override
    public void newImportPlatform(String filename) throws Exception {
        super.newImportPlatform(filename);

        this.release = null;
        this.barcode = null;
        this.p = null;
        this.protocoll = null;

    }
    String metaHeaderTag = "FEPARAMS";
    String metaStatsTag = "STATS";
    String metaDataTag = "FEATURES";
    String dataTag = "DATA";

    @Override
    protected String getEndMetaDataTag() {
        return new String(this.metaDataTag);
    }

    @Override
    protected boolean isCommentLine(String is) {
        if (is.indexOf(dataTag) < 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isHeaderLine(String is) {

        return false;

    }

    /**
     * read header of fe.txt file
     *
     * @param nofLines
     * @return
     */
    public Vector<Vector<String>> readHeaderFETXT(int nofLines) {
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
        try {
            inBuffer = new BufferedReader(new FileReader(inFile));

            while ((is = inBuffer.readLine()) != null) {
                if (is == null) {
                    continue;
                }
                if (is.indexOf(this.metaHeaderTag) >= 0) {
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
                            continue;
                        }
                    }
                    continue;
                }
                if (is.indexOf(this.metaStatsTag) >= 0) {
                    header = false;
                    data = false;
                    stats = true;
                    continue;
                }
                if (is.indexOf(this.metaDataTag) >= 0) {
                    header = false;
                    data = true;
                    stats = false;
                    iss = is.split("\t");
                    this.fileColNames = is.split("\t");
                    Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.INFO,
                            Arrays.deepToString(this.fileColNames));

                    continue;
                }
                if (is.indexOf(this.dataTag) >= 0) {
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
                            Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.INFO, "Found in Header: Protocoll " + this.protocoll);

                        }
                        if (indexRelease >= 0) {
                            this.release = GenomeRelease.toRelease(iss[indexRelease]);
                            Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.INFO, "Found in Header: Release " + this.release);

                        }
                        if (indexBarcode >= 0) {
                            this.barcode = iss[indexBarcode];
                            Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.INFO, "Found in Header: Barcode " + this.barcode);

                        }
                        if (indexNofChannel >= 0) {
                            this.nofChannel = Integer.parseInt(iss[indexNofChannel]);
                            Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.INFO, "Found in Header: NofChannel " + this.nofChannel);

                        } else {
                            this.nofChannel = 2;
                        }
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.SEVERE, null, e);

        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dataList;
    }

    @Override
    protected void readFileColNames() {
        if (this.fileColNames == null) {
            this.readHeaderFETXT(1);
        }

    }

    @Override
    protected String[] modify(List<String[]> map, String[] tmp) {

        // 120413   kt  negative controltype
        // 120413   kt  import 1...X,Y or 1...23,24 as chr...
        if (tmp[ichrom] != null && !tmp[ichrom].contentEquals("") && !tmp[ichrom].contentEquals("--") && !tmp[ichrom].startsWith("chr")) {

            if (tmp[ichrom].charAt(0) == 'X' || tmp[ichrom].charAt(0) == 'Y') {
                tmp[ichrom] = "chr" + tmp[ichrom];
            } else {
                try {
                    tmp[ichrom] = RegionLib.fromIntToChr(Integer.parseInt(tmp[ichrom]));
                } catch (NumberFormatException numberFormatException) {
                    tmp[ichrom] = "chr" + tmp[ichrom];
                }
            }
        }
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)[ind_db].contentEquals("ControlType")) {
                if (tmp[i].contentEquals("1") || tmp[i].contentEquals("-1")) {
                    tmp[i] = "true";
                } else {
                    tmp[i] = "false";
                }
                break;
            }
        }

        return tmp;
    }

    @Override
    public String getCreateTableSQL(String tableData) {
        String sql = " CREATE TABLE " + tableData + " ( "
                + "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeID INT UNSIGNED NOT NULL, "
                + "probeName varchar(255) NOT NULL, "
                + "col int(10) UNSIGNED, "
                + "row int(10) UNSIGNED, "
                + "chrom varChar(45) NOT NULL,"
                + "chromStart int(10) unsigned NOT NULL,"
                + "chromEnd int(10) unsigned NOT NULL,"
                + "controlType tinyint(1) unsigned NOT NULL default 0,"
                + "GB_ACC varchar(255), "
                + "GENE_SYMBOL varchar(255), "
                + "GENE_NAME varchar(255), "
                + "ACCESSION_STRING varchar(255),"
                + "DESCRIPTION varchar(255), "
                + "PRIMARY KEY (id),"
                + //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + " INDEX (GENE_SYMBOL (10)),"
                + " INDEX (probeName)  "
                + " ) TYPE=MyISAM";
        return sql;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
            "probeID", "probeName",
            "col", "row",
            "chrom", "chromStart", "chromEnd",
            "controlType",
            "GB_ACC", "GENE_SYMBOL", "GENE_NAME",
            //"UNIGENE_ID", "ENSEMBL_ID", "TIGR_ID", "GO_ID",
            "ACCESSION_STRING", "DESCRIPTION"
        };
    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<>();

        String[] entry = new String[2];
        /*
        "probeID", "COL", "ROW", "probeName", "controlType",
        "chromPosition",
        "GB_ACC", "GENE_SYMBOL",
        "ACCESSION_STRING", "DESCRIPTION"
        
        "FeatureNum", "Col", "Row", "ProbeName", "ControlType",
        "SystematicName",
        "GeneName", "GeneName",
        "accessions", "Description"
         */
        entry[ind_db] = "probeID";
        entry[ind_file] = "FeatureNum";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "probeName";
        entry[ind_file] = "ProbeName";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GENE_SYMBOL";
        entry[ind_file] = "GeneName";
        _map.add(entry);

        if (!this.hasSplitField()) {
            entry = new String[2];
            entry[ind_db] = "chrom";
            entry[ind_file] = "chr_coord";
            _map.add(entry);

            entry = new String[2];
            entry[ind_db] = "chromStart";
            entry[ind_file] = "chr_coord";
            _map.add(entry);

            entry = new String[2];
            entry[ind_db] = "chromEnd";
            entry[ind_file] = "chr_coord";
            _map.add(entry);
        }

        entry = new String[2];
        entry[ind_db] = "controlType";
        entry[ind_file] = "ControlType";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "ACCESSION_STRING";
        entry[ind_file] = "accessions";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "DESCRIPTION";
        entry[ind_file] = "Description";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GB_ACC";
        entry[ind_file] = "SystematicName";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GENE_NAME";
        entry[ind_file] = "GeneName";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "row";
        entry[ind_file] = "Row";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "col";
        entry[ind_file] = "Col";
        _map.add(entry);
        return _map;
    }

    @Override
    public PlatformDetail getPlatformDetail() {
        List<PlatformDetail> list = null;
        this.readHeaderFETXT(1);

        this.p = FEProtocollList.get(this.protocoll);
        if (p == null) {
            Logger.getLogger(ImportPlatformFETXT.class.getName()).log(Level.INFO,
                    "unknown protocoll" + this.protocoll);
            //throw new RuntimeException("Unknown Protokoll: " + this.protocoll);
            //return Collections.emptyList();
            //throw new RuntimeException();

        }

        PlatformDetail d = new PlatformDetail();
        String name = this.inFile.getName();
        name = name.substring(0, name.indexOf(".txt"));

        d.setName(name);

        //d.setType(Defaults.Type.BAC.toString());
        d.setDescription("imported via " + this.getClass().getName());

        d.setManufacturer("Agilent Technologies");
        d.setDescription("Barcode: " + this.barcode + "\n ");
        d.setMethod(this.p != null ? (this.p.getMethod() != null ? this.p.getMethod().toString() : "") : "");
        d.setType(this.p != null ? (this.p.getType() != null ? this.p.getType().toString() : "") : "");

        return d;
    }

    protected void readRelease() {
        if (this.release == null) {
            this.readHeaderFETXT(1);
        }
    }

    @Override
    public PlatformData doImportPlatform(
            PlatformDetail platformdetail,
            PlatformData platform) {

        PlatformData pd = super.doImportPlatform(platformdetail, platform);
        if (pd == null) {
            return pd;
        }
        if (this.p == null) {
            this.p = FEProtocollList.get(this.protocoll);

        }

        if (this.p == null && this.protocoll != null && !this.protocoll.contentEquals("")) {
            this.p = new FEProtocoll(
                    this.protocoll,
                    pd.getPlattform().getTitel(),
                    Defaults.Type.toType(pd.getPlattform().getType()),
                    Defaults.Method.toMethod(pd.getPlattform().getMethod()), "", "");
            FEProtocollList.add(p);
        }
        return pd;
    }

    @Override
    protected String getEndDataTag() {
        return "END";
    }
}
