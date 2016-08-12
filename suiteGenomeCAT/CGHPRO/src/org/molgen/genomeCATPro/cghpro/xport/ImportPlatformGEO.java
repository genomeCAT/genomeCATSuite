package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportPlatformGEO
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;

/**
 * 020813 kt	XPortImport createNewImport(); 090413 kt remove probeName 050413 kt
 * import 1...X,Y or 1...23,24 as chr... 260313 kt getEndMetaDataTag:null 130313
 * kt isHasHeader:true
 */
public class ImportPlatformGEO extends ImportPlatform implements XPortPlatform {

    public final static String platform_geo_txt = "GEO_GPL_TXT";

    public ImportPlatformGEO createNewImport() {
        return new ImportPlatformGEO();
    }

    @Override
    public String getName() {
        return new String("GEO");
    }

    @Override
    public boolean isHasHeader() {
        return true;
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{
            ImportPlatformGEO.platform_geo_txt
        }));
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
    protected String getEndDataTag() {
        return "END";
    }

    @Override
    protected String[] modify(List<String[]> map, String[] tmp) {

        // like 1 to chr1
        // nothing to do for geo
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)[ind_db].contentEquals("controlType")) {
                if (tmp[i].contentEquals("pos")) {
                    tmp[i] = "true";
                } else {
                    tmp[i] = "false";
                }
                break;
            }
        }
        // 050413 kt
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
        return tmp;

    }

    @Override
    public String getCreateTableSQL(String tableData) {
        String sql = " CREATE TABLE " + tableData + " ( "
                + "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + //"probeID varchar(255)  NOT NULL, " +
                "probeName varchar(255) NOT NULL, "
                + "REFSEQ varchar(255) NOT NULL, "
                + "col int(10) UNSIGNED, "
                + "row int(10) UNSIGNED, "
                + "chrom varChar(45) NOT NULL,"
                + "chromStart int(10) unsigned NOT NULL,"
                + "chromEnd int(10) unsigned NOT NULL,"
                + "controlType tinyint(1) unsigned NOT NULL default 0,"
                + "GB_ACC varchar(255), "
                + "GENE varchar(255), "
                + "GENE_SYMBOL varchar(255), "
                + "GENE_NAME varchar(255), "
                + "UNIGENE_ID varchar(255), "
                + "ENSEMBL_ID varchar(255), "
                + "TIGR_ID varchar(255), "
                + "GO_ID varchar(255), "
                + "ACCESSION_STRING varchar(255),"
                + "DESCRIPTION varchar(255), "
                + "PRIMARY KEY (id),"
                + //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "INDEX (REFSEQ),"
                + "INDEX (probeName)  "
                + " ) TYPE=MyISAM";
        return sql;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
            "probeName", "REFSEQ",
            "chrom", "chromStart", "chromEnd",
            "controlType", "GENE", "col", "row",
            "GB_ACC", "GENE_SYMBOL", "GENE_NAME",
            "UNIGENE_ID", "ENSEMBL_ID", "TIGR_ID", "GO_ID",
            "ACCESSION_STRING", "DESCRIPTION"
        };
    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();
        String[] entry = new String[2];

        entry[ind_db] = "probeName";
        entry[ind_file] = "ID";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "REFSEQ";
        entry[ind_file] = "REFSEQ";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GENE";
        entry[ind_file] = "GENE";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = "";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromStart";
        entry[ind_file] = "";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = "";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "controlType";
        entry[ind_file] = "CONTROL_TYPE";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "ACCESSION_STRING";
        entry[ind_file] = "ACCESSION_STRING";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "DESCRIPTION";
        entry[ind_file] = "DESCRIPTION";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GO_ID";
        entry[ind_file] = "GO_ID";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "TIGR_ID";
        entry[ind_file] = "TIGR_ID";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "ENSEMBL_ID";
        entry[ind_file] = "ENSEMBL_ID";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GB_ACC";
        entry[ind_file] = "GB_ACC";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GENE_SYMBOL";
        entry[ind_file] = "GENE_SYMBOL";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GENE_NAME";
        entry[ind_file] = "GENE_NAME";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "UNIGENE_ID";
        entry[ind_file] = "UNIGENE_ID";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "row";
        entry[ind_file] = "ROW";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "col";
        entry[ind_file] = "COL";
        _map.add(entry);
        return _map;
    }

    @Override
    public PlatformDetail getPlatformDetail() {
        PlatformDetail d = new PlatformDetail();
        String name = this.inFile.getName();
        name = name.substring(0, name.indexOf(".txt"));
        d.setName(name);

        //d.setType(Defaults.Type.BAC.toString());
        d.setDescription("imported via " + this.getClass().getName());

        return d;
    }

    protected void readRelease() {
        this.release = null;
    }
}
