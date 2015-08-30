package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportPlatformGEOAffy
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import org.molgen.genomeCATPro.annotation.RegionLib;

/**
 *  020813   kt	XPortImport createNewImport();
 */
public class ImportPlatformGEOAffy extends ImportPlatformGEO implements XPortPlatform {

    public final static String platform_geo_affy_txt = "GEO_GPL_AFFY_TXT";
    private int ichromStart = -1;
    private int ichromEnd = -1;
    private int iPhysPos = -1;

    public ImportPlatformGEOAffy createNewImport() {
        return new ImportPlatformGEOAffy();
    }

    @Override
    public String getName() {
        return new String("GEO GPL AFFY");
    }

    @Override
    public boolean isHasHeader() {
        return true;
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{
                    ImportPlatformGEOAffy.platform_geo_affy_txt
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
    int iChromStart = -1;
    int iChromEnd = -1;

    protected String getCreateTableSQL(String tableData) {
        String sql = new String(" CREATE TABLE " + tableData + " ( " +
                "id INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                "probeName varchar(255) NOT NULL, " +
                "REFSEQ varchar(255) NOT NULL, " +
                "col int(10) UNSIGNED, " +
                "row int(10) UNSIGNED, " +
                "chrom varChar(45) NOT NULL," +
                "chromStart int(10) unsigned NOT NULL," +
                "chromEnd int(10) unsigned NOT NULL," +
                "PhysicalPosition int(10) unsigned NOT NULL," +
                "controlType tinyint(1) unsigned NOT NULL default 0," +
                "GB_ACC varchar(255), " +
                "GENE varchar(255), " +
                "GENE_SYMBOL varchar(255), " +
                "GENE_NAME varchar(255), " +
                "UNIGENE_ID varchar(255), " +
                "ENSEMBL_ID varchar(255), " +
                "TIGR_ID varchar(255), " +
                "GO_ID varchar(255), " +
                "ACCESSION_STRING varchar(255)," +
                "DESCRIPTION varchar(255), " +
                "PRIMARY KEY (id)," +
                //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd), " +
                "INDEX (REFSEQ)," +
                "INDEX (probeName)  " +
                " ) TYPE=MyISAM");
        return sql;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
                    "probeName", "REFSEQ",
                    "chrom", "chromStart", "chromEnd", "PhysicalPosition",
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
        entry[ind_file] = "RANGE_GB";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "GB_ACC";
        entry[ind_file] = "SNP_ID";
        _map.add(entry);


        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = "Chromosome";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromStart";
        entry[ind_file] = "RANGE_START";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = "RANGE_START";

        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "PhysicalPosition";
        entry[ind_file] = "Physical Position";
        _map.add(entry);



        return _map;
    }

    @Override
    protected List<String[]> extendMapping() {

        // 050413 kt

        List<String[]> _map = super.extendMapping();



        for (int i = 0; i < _map.size(); i++) {


            if (_map.get(i)[ind_db].contentEquals("chromStart")) {
                ichromStart = i;
                continue;

            }
            if (_map.get(i)[ind_db].contentEquals("chromEnd")) {
                ichromEnd = i;
                continue;

            }
            if (_map.get(i)[ind_db].contentEquals("PhysicalPosition")) {
                iPhysPos = i;
                continue;

            }

        }

        return _map;
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
        if (tmp[ichromStart] == null || tmp[ichromStart].contentEquals("") ||
                tmp[ichromEnd] == null || tmp[ichromEnd].contentEquals("")) {

            tmp[ichromStart] = tmp[iPhysPos];
            tmp[ichromEnd] = tmp[iPhysPos];

        }
        return tmp;


    }
}
