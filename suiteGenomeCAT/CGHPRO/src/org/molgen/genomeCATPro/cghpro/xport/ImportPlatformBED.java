/**
 * @name ImportPlatformBACID
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package.
 * Copyright Oct 8, 2010 Katrin Tebel <tebel at molgen.mpg.de>.
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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;

/**
 * 020813   kt	XPortImport createNewImport();
 * 
 * 
 * import BAC platform from cloneid file
 * 
 */
public class ImportPlatformBED extends ImportPlatform implements XPortPlatform {

    public final static String platform_bed = "BED";

    public ImportPlatformBED createNewImport() {
        return new ImportPlatformBED();
    }

    @Override
    public String getName() {
        return new String("BED");
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{ImportPlatformBED.platform_bed}));
    }

    @Override
    protected String getEndMetaDataTag() {
        return new String("#id");
    }

    @Override
    protected boolean isCommentLine(String is) {
        if (is.indexOf("--") == 0) {
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
        if (is.indexOf("#id") == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getCreateTableSQL(String tableData) {
        String sql = new String("CREATE TABLE " + tableData + " ( " +
                " ID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT , " +
                " probeid varchar(255) NOT NULL default ''," +
                " alias varchar(255), " +
                " chrom varChar(255) NOT NULL default '', " +
                " chromStart int(10) unsigned NOT NULL default '0'," +
                " chromEnd int(10) unsigned NOT NULL default '0'," +
                " source varchar(255)," +
                " comment varchar(255), " +
                "PRIMARY KEY (ID) ," +
                //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd) " +
                ") ;");
        return sql;
    }

    @Override
    protected void readRelease() {
        this.release = null;
    }

    protected String[] modify(List<String[]> map, String[] tmp) {


        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)[ind_db].contentEquals("chrom")) {
                Logger.getLogger(ImportPlatform.class.getName()).log(Level.INFO,
                        "modify: " + map.get(i)[ind_db]);
                tmp[i] = "chr" + tmp[i];
                break;
            }
        }
        return tmp;
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

    @Override
    public String[] getDBColNames() {
        return new String[]{
                    "probeid", "alias", "chrom",
                    "chromStart", "chromEnd", "source", "comment"
                };
    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();
        String[] entry = new String[2];

        entry[ind_db] = "probeid";
        entry[ind_file] = "#id";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "alias";
        entry[ind_file] = "alias";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = "chrom";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromStart";
        entry[ind_file] = "start";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = "stop";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "source";
        entry[ind_file] = "source";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "comment";
        entry[ind_file] = "comment";
        _map.add(entry);

        return _map;
    }
}
