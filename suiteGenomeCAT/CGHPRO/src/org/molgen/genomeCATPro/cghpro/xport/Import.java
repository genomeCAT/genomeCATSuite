package org.molgen.genomeCATPro.cghpro.xport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import org.molgen.genomeCATPro.common.Defaults;
/**
 * @name Import
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
/**
 *  120313 kt   user setting if has header
 *  200612 kt   doImport  skip empty lines
 *
 */
public abstract class Import implements XPortImport {

    static final String field_not_mapped = "unmapped";
    protected Connection con = null;
    protected EntityManager em = null;
    protected String[] fileColNames = null;
    protected BufferedReader inBuffer = null;
    protected File inFile = null;
    protected List<String[]> map;
     boolean hasHeader = true;
    //otherSymbols.setDecimalSeparator(',');
    //otherSymbols.setGroupingSeparator('.'); 
    //protected DecimalFormat myFormatter = new DecimalFormat("0.#####E0");
    //protected DecimalFormat myFormatterwin = new DecimalFormat("0,#####E0");
    protected Defaults.GenomeRelease release = null;

    public void newImport(String filename) throws Exception {
        this.inFile = null;
        this.release = null;
        this.map = null;
        //this.file_field_position = null;
        this.fileColNames = null;
        try {
            inFile = new File(filename);

        } catch (Exception ex) {
            Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public File getImportFile() {
        return this.inFile;
    }

    @SuppressWarnings("empty-statement")
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();
        for (int i = 0; i < this.getDBColNames().length; i++) {
            for (String m : this.getFileColNames()) {
                if (m.indexOf(this.getDBColNames()[i]) > 0) {
                    String[] entry = new String[]{"", ""};
                    entry[ind_db] = this.getDBColNames()[i];
                    entry[ind_file] = m;
                    _map.add(entry);
                }
            }
        }

        return _map;
    }

    public String[] getFileColNames() {
        if (this.fileColNames == null) {
            this.readFileColNames();
        }

        return this.fileColNames;
    }

    protected Hashtable<String, Integer> getFileIndexMapping() {
        Hashtable<String, Integer> indmap = new Hashtable<String, Integer>();
        List<String> real = Arrays.asList(this.getFileColNames());
        int ind = -1;
        //  boolean splitDone = false;
        for (String[] _map : this.getMappingFile2DBColNames()) {
            // mapping contains split col more than once, take only the first one
            /*if (this.hasSplitField() && _map[ImportPlatform.ind_file].contentEquals(this.getSplitFieldName())) {
            if (!splitDone) {
            splitDone = true;
            } else {
            continue;
            }
            }*/
            if (_map[ImportPlatform.ind_file].contentEquals(SPLITFIELD)) {
                ind = real.indexOf(this.getSplitFieldName());
            } else {
                for (String f : real) {

                    if (_map[ImportPlatform.ind_file].contentEquals(f)) {
                        ind = real.indexOf(f);
                        break;
                    }
                }
            }
            indmap.put(_map[ImportPlatform.ind_db], ind);
            ind = -1;
        }
        Logger.getLogger(Import.class.getName()).log(Level.INFO, indmap.toString());
        return indmap;
    }

    public List<String[]> getMappingFile2DBColNames() {
        if (this.map == null) {
            this.map = this.getDefaultMappingFile2DBColNames();
        }

        return this.map;
    }

    public String getRelease() {
        if (this.release == null) {
            try {
                this.readRelease();
            } catch (UnsupportedOperationException ex) {
                Logger.getLogger(Import.class.getName()).log(Level.INFO, null, ex);

            }
        }

        return (this.release != null ? this.release.toString() : null);
    }

    protected void readFileColNames() {
        String is;
       
        try {
            this.fileColNames = null;
            inBuffer = new BufferedReader(new FileReader(inFile));

            while ((is = inBuffer.readLine()) != null) {
                if (is == null) {
                    continue;
                }

                if (this.isHeaderLine(is)) {
                    this.fileColNames = is.split("\t");
                    break;
                }
            }
            if (this.fileColNames == null) {
                throw new Exception("readFileColNames: " + "no Header found  ");
            }
        } catch (Exception e) {
            Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected List<String[]> extendMapping() {
        return this.getMappingFile2DBColNames();

    }

    protected int importData(String spotTable) throws Exception {
        String is;
        String[] iss = null;

        int error = 0;
        int noimp = 0;
         boolean data = (this.hasHeader ? false : true);
        Hashtable<String, Integer> indexFileDBcolMapping = getFileIndexMapping();
        try {
            Statement s = con.createStatement();
            String sql;

            //int realSize = mapping.size() + 2;
            List<String[]> mapping = this.extendMapping();
            int realSize = mapping.size();
            String[] dbCols = new String[realSize];

            int i = 0;
            for (String[] _map : mapping) {
                dbCols[i++] = _map[ind_db];
            }

            String[] tmp = new String[dbCols.length];

            String[] splitField = this.getSplitFieldArray();
            inBuffer = new BufferedReader(new FileReader(inFile));
            Integer ind = 0;
            boolean splitdone = false;
            while ((is = inBuffer.readLine()) != null) {
                // 200612 kt skip empty lines
                if(is.contentEquals(""))
                    continue;
                //header
                if (is.indexOf(this.getEndMetaDataTag()) == 0) {
                    data = true;
                    continue;
                }
                if (is.indexOf(this.getEndDataTag()) == 0) {
                    data = false;
                    break;
                }
                if (data && !this.isCommentLine(is)) {
                    iss = is.split("\t", this.fileColNames.length);

                    i = 0;
                    Arrays.fill(tmp, "");
                    splitdone = false;
                    // error because splitfield (file) could be mapped regulary to 
                    // db column
                    for (String[] _map : mapping) {
                        if (this.hasSplitField() && _map[ind_file].contentEquals(SPLITFIELD)) {
                            if (!splitdone) {

                                ind = indexFileDBcolMapping.get(_map[ImportPlatform.ind_db]);
                                //splitField = iss[ind].split("[:-]");
                                splitField = iss[ind].split(this.getSplitPattern());
                                if (splitField.length == 1) {
                                    splitField = this.getSplitFieldArray();
                                }
                                tmp[i++] = splitField[0];
                                tmp[i++] = splitField[1];
                                tmp[i++] = splitField[2];
                                splitdone = true;
                            }
                            continue;
                        } else {
                            ind = indexFileDBcolMapping.get(_map[ImportPlatform.ind_db]);
                        }




                        if (ind != null && ind >= 0) {
                            tmp[i++] = iss[ind];
                        } else {
                            tmp[i++] = null;
                        }
                    }
                    // !!Handling dye swap
                    tmp = modify(map, tmp);
                    //Logger.getLogger(Import.class.getName()).log(Level.INFO,
                    //       "importData: tmp: " + Arrays.deepToString(tmp));


                    sql = null;
                    sql = Import.loadDataLine(spotTable, tmp, dbCols);
                    if (sql != null) {
                        //Logger.getLogger(Import.class.getName()).log(Level.FINE, sql);
                        try {
                            s.execute(sql);
                            noimp++;
                        } catch (Exception sQLException) {
                            Logger.getLogger(Import.class.getName()).log(Level.WARNING, "sql error: " + Arrays.deepToString(tmp) + "\n");

                            Logger.getLogger(Import.class.getName()).log(Level.WARNING,  sQLException.getMessage());
                            ++error;
                        }
                    } else {
                        Logger.getLogger(Import.class.getName()).log(Level.WARNING, "error: " + Arrays.deepToString(tmp) + "\n");
                        ++error;
                    }
                }
            }
            Logger.getLogger(Import.class.getName()).log(Level.INFO, " successfully imported into " + spotTable + ": " + noimp);
        } catch (Exception e) {
            Logger.getLogger(Import.class.getName()).log(Level.SEVERE, "import data", e);
            throw e;
        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return error;
    }

    public static String loadDataLine(
            String spotTable,
            String[] iss,
            String[] colnames) {



        String sqlinsert = "",sqlvalues  = "";
        try {

            for (int i = 0; i < iss.length; i++) {



                if (colnames[i] == null) {
                    // skip column, no input fields at file-> db defaults
                    continue;
                }

                /* kt test if passend mit import anno */
                if (iss[i] == null || iss[i].equals("")) {
                    //empty values, skip column -> db defaults
                    continue;
                }


                // mask special characters ' " \ with \
                Matcher matcher = Pattern.compile("([\'\"\\\\])").matcher(iss[i]);
                iss[i] = matcher.replaceAll("\\\\$1");

                if (!sqlinsert.equals("")) {
                    sqlinsert += ",";
                }
                sqlinsert += colnames[i];
                if (!sqlvalues.equals("")) {
                    sqlvalues += ",";
                }
                sqlvalues += "\'" + iss[i] + "\'";
            }
        } catch (Exception e) {
            Logger.getLogger(Import.class.getName()).log(Level.WARNING, "", e);
            //Logger.getLogger(ImportUtil.class.getName()).log(Level.INFO, "", e);
            return null;
        }
        String sql = "INSERT INTO " + spotTable + " (" + sqlinsert + ") " +
                " VALUES ( " + sqlvalues + " )";
        return sql;
    }

    public Vector<Vector<String>> readData(int nofLines) {
        Vector<Vector<String>> dataList = new Vector<Vector<String>>();
        int lines = 0;
        boolean data = (this.hasHeader ? false : true);
        String[] iss = null;
        Vector<String> dataLine;
        String is = null;
        try {
            inBuffer = new BufferedReader(new FileReader(inFile));
            while ((is = inBuffer.readLine()) != null) {


                if (is.indexOf(this.getEndMetaDataTag()) == 0) {
                    data = true;
                    continue;
                }
                if (data && is != null && !(this.hasHeader && this.isHeaderLine(is)) && !this.isCommentLine(is)) {
                    if (++lines > nofLines) {
                        break;
                    }

                    iss = is.split("\t");
                    dataLine = new Vector<String>(Arrays.asList(iss));
                    dataList.add(dataLine);
                }
            }
            if (dataList.size() < 1) {
                Logger.getLogger(Import.class.getName()).log(Level.WARNING, "readData: no data read");
            }
        } catch (Exception e) {
            Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return dataList;
    }

    protected abstract boolean isCommentLine(String is);

    protected abstract boolean isHeaderLine(String is);

    protected abstract String[] modify(List<String[]> map, String[] tmp);

    protected abstract void readRelease();

    protected abstract List<String[]> setSplitFieldCols(List<String[]> map);
    public final static String SPLITFIELD = "SPLITFIELD";

    protected abstract String getEndMetaDataTag();

    protected abstract String getEndDataTag();

    protected abstract String getCreateTableSQL(String tableData);
}
