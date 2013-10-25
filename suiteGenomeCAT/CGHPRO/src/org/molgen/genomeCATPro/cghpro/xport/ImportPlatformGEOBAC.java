/**
 * @name ImportPlatformBAC
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
package org.molgen.genomeCATPro.cghpro.xport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;

/**
 * 020813   kt	XPortImport createNewImport();
 * 
 * 
 * import BAC Platform from GEO txt file
 * 
 */
public class ImportPlatformGEOBAC extends ImportPlatform implements XPortPlatform {

    public final static String platform_geo_txt = "GEO_GPL_BAC_TXT";

    public ImportPlatformGEOBAC createNewImport() {
        return new ImportPlatformGEOBAC();
    }

    @Override
    public String getName() {
        return new String("BAC_GEO");
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{
                    ImportPlatformGEOBAC.platform_geo_txt
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
    protected String getEndDataTag() {
        return "END";
    }

    @Override
    protected boolean isHeaderLine(String is) {
        if (is.indexOf("ID") == 0) {
            return true;
        } else {
            return false;
        }
    }

    protected String getCreateTableSQL(String tableData) {
        String sql = new String("CREATE TABLE " + tableData + " ( " +
                " ID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT , " +
                " probeId varchar(255) NOT NULL default ''," +
                " probeName varchar(255) NOT NULL default ''," +
                " alias varchar(255), " +
                " chrom varChar(255) NOT NULL default '', " +
                " chromStart int(10) unsigned NOT NULL default '0'," +
                " chromEnd int(10) unsigned NOT NULL default '0'," +
                " block int(4) unsigned NOT NULL default '0'," +
                " row int(4) unsigned NOT NULL default '0'," +
                " col int(4) unsigned NOT NULL default '0'," +
                "PRIMARY KEY (ID) ," +
                "UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd), " +
                "INDEX (probeName) " +
                ") ;");
        return sql;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
                    "probeId", "probeName", "alias", "chrom",
                    "chromStart", "chromEnd", "block", "row", "col"
                };
    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();
        String[] entry = new String[2];
        /*  
        #ID = 
        #Internal_Name = clone name for internal use
        #Name = original library name
        #Block = block
        #Row = row
        #Col = column
        #Chromosom = chromosome
        #Chromosom_start = clone startposition (UCSC Maz 2004(Hg17),NCBI Build 35)
        #Chromosom_end = clone end position (UCSC Maz 2004(Hg17),NCBI Build 35)
        #GB_RANGE = NCBI Build 35 chr accession [start..end]
        #CLONE_ID = BAC Clone
        #SPOT_ID = spot identifier
         */
        entry[ind_db] = "probeId";
        entry[ind_file] = "ID";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "probeName";
        entry[ind_file] = "Internal_Name";
        _map.add(entry);
        entry = new String[2];
        entry[ind_db] = "alias";
        entry[ind_file] = "Name";
        _map.add(entry);



        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = "Chromosom";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromStart";
        entry[ind_file] = "Chromosom_start";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = "Chromosom_end";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "block";
        entry[ind_file] = "Block";
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
        PlatformDetail d = new PlatformDetail();
        String name = this.inFile.getName();
        name = name.substring(0, name.indexOf(".txt"));
        d.setName(name);
        d.setType(Defaults.Type.BAC.toString());
        d.setDescription("imported via " + this.getClass().getName());



        return d;
    }

    protected void readRelease() {
        Vector<Vector<String>> dataList = new Vector<Vector<String>>();
        int lines = 0;
        boolean data = false;
        String iss[] = null;
        Vector<String> dataLine;
        String is = null;
        try {
            inBuffer = new BufferedReader(new FileReader(inFile));
            Pattern pattern = Pattern.compile("^.*=.*hg[0-9]{2}", Pattern.CASE_INSENSITIVE);
            Matcher matcher;
            this.release = null;
            while ((is = inBuffer.readLine()) != null) {
                matcher = pattern.matcher(is);

                if (matcher.find()) {
                    String s = is.toLowerCase();
                    int i = s.indexOf("hg1");

                    this.release = GenomeRelease.toRelease(s.substring(i, i + 4));
                    Logger.getLogger(ImportPlatform.class.getName()).log(
                            Level.INFO, "readRelease: " + s.substring(i, i + 4) +
                            " " + this.release != null ? this.release.toString() : "not set");
                }

            }

        } catch (Exception e) {
            Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE, null, e);

        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }
}
