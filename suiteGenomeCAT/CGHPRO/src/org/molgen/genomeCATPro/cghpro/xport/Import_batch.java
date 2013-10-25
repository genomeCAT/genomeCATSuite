package org.molgen.genomeCATPro.cghpro.xport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

/**
 * @name Import_batch
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
 *  140513 kt   readData this.getEndMetaDataTag() or hasHeader
 *  070513 kt  input format  error leads to print out message
 *  030513 kt  allow read data shorter than mapping
 *  170413 kt   importdata: 
 *                  throw exception at error; 
 *                  nosucc, noprocc, error
 *                  finished tag
 *  090413 kt   noHeaderLine (importData)
 *  020413 kt   clear insert count inside thread (importData)
 *  260313 kt   getNoimp
 *  260313 kt   read file col names meta end
 *  130313 kt   user setting if has header
 *  120313 kt   getError
 *  150812 kt   importData in with thread pool (10)
 *              http://thegreyblog.blogspot.de/2011/12/using-threadpoolexecutor-to-parallelize.html#!/2011/12/using-threadpoolexecutor-to-parallelize.html 
 *
 *  
 *  191212  kt   introduce mySep as Separator to be overloaded
 *  191212  kt  importData: emergency exit: if modify returns null ignore line
 */
public abstract class Import_batch implements XPortImport {

    static final int noBatch = 100;
    static final String field_not_mapped = "unmapped";
    protected Connection con = null;
    protected EntityManager em = null;
    protected String[] fileColNames = null;
    protected BufferedReader inBuffer = null;
    protected File inFile = null;
    protected List<String[]> map;
    boolean hasHeader = false;  // kt 120313

    final static String mySep = "\t";
    //otherSymbols.setDecimalSeparator(',');
    //otherSymbols.setGroupingSeparator('.'); 
    //protected DecimalFormat myFormatter = new DecimalFormat("0.#####E0");
    //protected DecimalFormat myFormatterwin = new DecimalFormat("0,#####E0");
    protected Defaults.GenomeRelease release = null;
    Integer error = 0;
    Integer nosucc = 0;
    Integer noprocc = 0;

    public String getMySep() {
        return mySep;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public int getError() {
        return error;
    }

    public int getNoimp() {
        return nosucc;
    }

    public void  newImport(String filename) throws Exception {
        this.inFile = null;
        this.release = null;
        this.map = null;
        //this.file_field_position = null;
        this.fileColNames = null;
        try {
            inFile = new File(filename);

        } catch (Exception ex) {
            Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        this.noprocc = 0;
        this.nosucc = 0;
        this.error = 0;
    }

    public File getImportFile() {
        return this.inFile;
    }

    @SuppressWarnings("empty-statement")
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();
        for (int i = 0; i < this.getDBColNames().length; i++) {
            try {
                for (String m : this.getFileColNames()) {
                    if (m.indexOf(this.getDBColNames()[i]) > 0) {
                        String[] entry = new String[]{"", ""};
                        entry[ind_db] = this.getDBColNames()[i];
                        entry[ind_file] = m;
                        _map.add(entry);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return _map;
    }

    public String[] getFileColNames() throws Exception {


        this.readFileColNames();


        return this.fileColNames;
    }

    protected Hashtable<String, Integer> getFileIndexMapping() {
        Hashtable<String, Integer> indmap = new Hashtable<String, Integer>();
        List<String> real = new Vector<String>();
        int ind = -1;
        try {
            real = Arrays.asList(this.getFileColNames());
        } catch (Exception ex) {
            Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        Logger.getLogger(Import_batch.class.getName()).log(Level.INFO, indmap.toString());
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
                Logger.getLogger(Import_batch.class.getName()).log(Level.INFO, null, ex);

            }
        }

        return (this.release != null ? this.release.toString() : null);
    }

    protected void readFileColNames() throws Exception {
        String is;

        try {
            this.fileColNames = null;
            inBuffer = new BufferedReader(new FileReader(inFile));
            boolean meta_end = (this.getEndMetaDataTag() == null);

            while ((is = inBuffer.readLine()) != null) {
                if (is == null) {
                    continue;
                }
                // 260313 kt read file col names meta end

                if (this.getEndMetaDataTag() != null && is.indexOf(this.getEndMetaDataTag()) == 0) {
                    meta_end = true;
                    continue;
                }
                if ((this.isHasHeader() & !this.isCommentLine(is) & meta_end) || this.isHeaderLine(is)) {
                    // kt 120313 hasHeader set or header line format
                    this.fileColNames = is.split(this.getMySep());
                    break;
                }
            }
            if (this.fileColNames == null) {

                //throw new Exception("readFileColNames: " + "no Header found  ");
                //this.fileColNames = is.split(this.getMySep());
            } else {
                Logger.getLogger(Import_batch.class.getName()).log(Level.INFO,
                        Arrays.deepToString(this.fileColNames));
            }
        } catch (Exception e) {
            Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, e);
            throw e;
        } finally {
            try {
                if (inBuffer != null) {
                    inBuffer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected List<String[]> extendMapping() {
        return this.getMappingFile2DBColNames();

    }

    synchronized void addInserts(int count, int succ, int err, int iBatch) {
        Logger.getLogger(Import.class.getName()).log(Level.INFO,
                " add inserts (" + iBatch + ")  count: " + count + " succ: " + succ +
                " err: " + err);
        this.noprocc += count;
        this.nosucc += succ;
        this.error += err;
        Logger.getLogger(Import.class.getName()).log(Level.INFO,
                " add inserts (" + iBatch + ")  noproc: " + this.noprocc +
                " nofsucc: " + this.nosucc +
                " error: " + this.error);

    }

    @SuppressWarnings("empty-statement")
    protected int importData(String spotTable) throws Exception {
        String is;
        String[] iss = null;
        final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");

        this.error = 0;
        this.nosucc = 0;
        boolean data = (this.isHasHeader() ? false : true);
        Hashtable<String, Integer> indexFileDBcolMapping = getFileIndexMapping();
        try {
            int c_batch = 10;
            final Statement s[] = {null, null, null, null, null, null, null, null, null, null};
            final Connection c[] = {null, null, null, null, null, null, null, null, null, null};
            final long tstamp[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            final int insert_count[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            Thread _batch[] = {null, null, null, null, null, null, null, null, null, null};

            String sql;

            int iRead = 0;
            int iBatch = 0;

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



            s[iRead] = con.createStatement();
            tstamp[iRead] = new Date().getTime();
            insert_count[iRead] = 0;
            boolean finished = false;
            while ((is = inBuffer.readLine()) != null || !finished) {
                if (is == null) {
                    finished = true;
                } else {
                    // 200612 kt skip empty lines
                    if (is.contentEquals("")) {
                        continue;
                    //header
                    }
                    // 260313 we have meta data
                    if (this.getEndMetaDataTag() != null && is.indexOf(this.getEndMetaDataTag()) == 0) {
                        data = true;
                        continue;
                    }
                    // 
                    if (this.getEndDataTag() != null && is.indexOf(this.getEndDataTag()) == 0) {
                        data = false;
                        continue;
                    }
                    // kt 130313 track
                    if (this.getEndMetaDataTag() == null && !data && this.isHasHeader() && !this.isCommentLine(is)) {
                        // swap from header to data
                        data = true;
                        continue;

                    }
                    // kt 090413
                    if (data && !this.isCommentLine(is) && !this.isHeaderLine(is)) {
                        try {
                            
                            iss = is.split(this.getMySep(), this.fileColNames.length);

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
                                        if (splitField.length < 3) {
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
                                if (iss.length <= (ind == null ? 0 : ind)) {
                                    //System.out.println("do nothing");
                                    //030513    kt  allow read data shorter than mapping
                                } else {
                                    if (ind != null && ind >= 0) {
                                        tmp[i++] = iss[ind];
                                    } else {
                                        tmp[i++] = null;
                                    }
                                }

                            }
                            // !!Handling dye swap
                            tmp = modify(map, tmp);
                            if (tmp == null) {
                                // emergency  exit -> modify decides to skip this line
                                // reallocate tmp
                                tmp = new String[dbCols.length];
                                // next line
                                continue;
                            }
                        //Logger.getLogger(Import.class.getName()).log(Level.INFO,
                        //       "importData: tmp: " + Arrays.deepToString(tmp));
                        } catch (Exception e) {
                            //070513    kt  input format  error leads to print out message
                            Logger.getLogger(Import_batch.class.getName()).log(
                                    Level.WARNING, "error: " + Arrays.deepToString(iss) + " \n" +
                                    Arrays.deepToString(tmp) + "\n");
                            this.error++;
                            this.noprocc++;
                            continue;
                        }
                        sql = null;

                        sql = Import_batch.loadDataLine(spotTable, tmp, dbCols);
                        if (sql == null) {
                            Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING, "error: " + Arrays.deepToString(tmp) + "\n");
                            this.error++;
                            this.noprocc++;
                            continue;
                        }
                        s[iRead].addBatch(sql);
                        insert_count[iRead] = (insert_count[iRead] + 1);
                    }

                }
                if (insert_count[iRead] >= Import_batch.noBatch || finished) {

                    Logger.getLogger(Import.class.getName()).log(Level.INFO,
                            "importData: tmp (" + iRead + ") : " + Arrays.deepToString(tmp));

                    Logger.getLogger(Import_batch.class.getName()).log(
                            Level.INFO, "Read time (" + iRead + ") : " +
                            df.format(new Date(tstamp[iRead])) + " -  " +
                            df.format(new Date()));

                    iBatch = iRead;

                    iRead = ((iRead + 1) >= c_batch ? 0 : (iRead + 1));
                    if (_batch[iRead] != null) {
                        Logger.getLogger(Import.class.getName()).log(Level.INFO,
                                "wait for (" + iRead + ")  ");

                        _batch[iRead].join(0);
                    }

                    // oldest insert finished -> keep results
                            /*
                    020413 kt
                    if (insert_count[iNextRead] > 0) {
                    noimp = this.addInserts(noimp, insert_count[iNextRead]);
                    
                    } else {
                    error += (insert_count[iNextRead] * -1);
                    }
                     */


                    // start new insert batch 

                    // next batch

                    // starte new read
                    //insert_count[iRead] = 0;
                    tstamp[iRead] = new Date().getTime();
                    if (c[iRead] == null) {
                        c[iRead] = Database.getDBConnection(Defaults.localDB);
                    }
                    //c[iRead].setAutoCommit(false);
                    if (s[iRead] == null) {
                        s[iRead] = c[iRead].createStatement();
                    } else {
                        s[iRead].clearBatch();

                    // new thread insert batch 
                    }
                    _batch[iBatch] = new Thread(new Runnable() {

                        int err = 0;
                        int succ = 0;
                        int _iBatch = 0;

                        Runnable init(int i) {
                            _iBatch = i;
                            return this;
                        }

                        @Override
                        public void run() {

                            try {
                                err = 0;

                                tstamp[_iBatch] = new Date().getTime();
                                Logger.getLogger(Import_batch.class.getName()).log(
                                        Level.INFO, "start Thread insert (" + _iBatch + ") : " +
                                        df.format(new Date(tstamp[_iBatch])));

                                s[_iBatch].executeBatch();



                                //s[_iBatch].close();
                                Logger.getLogger(Import_batch.class.getName()).log(
                                        Level.INFO, "end Thread insert (" + _iBatch + ") : " +
                                        df.format(new Date(tstamp[_iBatch])) + " -  " +
                                        df.format(new Date()));
                                // 020413   kt  clear insert count inside thread

                                Import_batch.this.addInserts(insert_count[_iBatch], insert_count[_iBatch], err, _iBatch);

                                s[_iBatch].clearBatch();
                                insert_count[_iBatch] = 0;
                            //c[_iBatch].commit();
                            } catch (BatchUpdateException sQLException) {
                                //170413    kt  throw exception at error
                                //Logger.getLogger(Import.class.getName()).log(Level.WARNING, "sql error: " + Arrays.deepToString(tmp) + "\n");
                                Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING,
                                        "Batch update count: " + sQLException.getUpdateCounts().length);

                                Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING,
                                        "thread: " + _iBatch + " " + sQLException.getSQLState() + " " + sQLException.getMessage());

                                int[] updateCounts = sQLException.getUpdateCounts();

                                for (int i = 0; i < updateCounts.length; i++) {
                                    System.out.println(updateCounts[i]);
                                    if (updateCounts[i] >= 0) {
                                        // Successfully executed; 
                                        succ += 1;

                                    } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                                        succ += 1;
                                    } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                                        // Failed to execute
                                        err += 1;
                                    }
                                }

                                // 020413   kt  clear insert count inside thread

                                Import_batch.this.addInserts(insert_count[_iBatch], succ, err, _iBatch);
                                try {
                                    //c[_iBatch].rollback();
                                    s[_iBatch].clearBatch();
                                } catch (SQLException ex) {
                                    Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING,
                                            "", ex);
                                }
                                insert_count[_iBatch] = 0;
                                Logger.getLogger(Import_batch.class.getName()).log(
                                        Level.INFO, "end Thread insert (" + _iBatch + ") : " +
                                        df.format(new Date(tstamp[_iBatch])) + " -  " +
                                        df.format(new Date()));
                                throw new RuntimeException("Error");



                            } catch (Exception ex1) {
                                Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING,
                                        "", ex1);
                            }


                        }
                    }.init(iBatch));
                    _batch[iBatch].start();
                    Logger.getLogger(Import.class.getName()).log(Level.INFO,
                            "start Read: (" + iRead + ") " + df.format(new Date(tstamp[iRead])));
                }
            }


            // keep all results
            for (int j = 0; j < c_batch; j++) {
                if (_batch[j] != null) {
                    _batch[j].join(0);

                }
            }
            /*int err = 0;
            try {
            
            if (insert_count[iRead] > 0) {
            Logger.getLogger(Import.class.getName()).log(Level.INFO,
            "Last importData: tmp (" + iRead + ") : " + Arrays.deepToString(tmp));
            
            Logger.getLogger(Import_batch.class.getName()).log(
            Level.INFO, "Last Read time (" + iRead + ") : " +
            df.format(new Date(tstamp[iRead])) + " -  " +
            df.format(new Date()));
            // thread for next insert statements not yet started
            
            int[] updateCounts = s[iRead].executeBatch();
            
            
            
            s[iRead].clearBatch();
            
            // this.addInserts(insert_count[iRead], err, iRead);
            
            insert_count[iRead] = 0;
            }
            
            } catch (BatchUpdateException sQLException) {
            //Logger.getLogger(Import.class.getName()).log(Level.WARNING, "sql error: " + Arrays.deepToString(tmp) + "\n");
            Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING,
            "Batch update count: " + sQLException.getUpdateCounts().length);
            Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING,
            sQLException.getSQLState() + " " + sQLException.getMessage());
            err++;
            
            
            
            }
             */


            Logger.getLogger(Import_batch.class.getName()).log(Level.INFO, " processed " + spotTable + ": " + this.noprocc);
            Logger.getLogger(Import_batch.class.getName()).log(Level.INFO, " successfully processed " + spotTable + ": " + this.nosucc);
            Logger.getLogger(Import_batch.class.getName()).log(Level.INFO, " error " + spotTable + ": " + this.error);
        } catch (Exception e) {
            Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, "import data", e);
            //Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, "last read", iss);
            throw e;
        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, ex);
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

            for (int i = 0; i <
                    iss.length; i++) {



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
            Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING, "", e);
            //Logger.getLogger(ImportUtil.class.getName()).log(Level.INFO, "", e);





            return null;
        }
        String sql = "INSERT INTO " + spotTable + " (" + sqlinsert + ") " +
                " VALUES ( " + sqlvalues + " )";
        return sql;
    }

    public Vector<Vector<String>> readData(int nofLines) {

        // meta - end meta data
        // hasHeader  - user defined single header line
        // 
        Vector<Vector<String>> dataList = new Vector<Vector<String>>();
        int lines = 0;
        boolean data = (this.isHasHeader() ? false : true);
        String[] iss = null;
        Vector<String> dataLine;
        String is = null;
        try {
            inBuffer = new BufferedReader(new FileReader(inFile));


            while ((is = inBuffer.readLine()) != null) {


                if (this.getEndMetaDataTag() != null && is.indexOf(this.getEndMetaDataTag()) == 0) {
                    data = true;
                    continue;

                }
                // kt 120313
                
                if (!data && this.isHasHeader() && !this.isCommentLine(is) && 
                        // kt 140513
                        this.getEndMetaDataTag() == null) {
                    // swap from header to data
                    data = true;
                    continue;

                }





                if (data && is != null &&  !this.isHeaderLine(is) // kt 120313
                        && !this.isCommentLine(is)) {
                    if (++lines > nofLines) {
                        break;
                    }

                    iss = is.split(getMySep());
                    dataLine =
                            new Vector<String>(Arrays.asList(iss));
                    dataList.add(dataLine);
                }


            }

            if (dataList.size() < 1) {
                Logger.getLogger(Import_batch.class.getName()).log(Level.WARNING, "readData: no data read");
            }
        } catch (Exception e) {
            Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(Import_batch.class.getName()).log(Level.SEVERE, null, ex);
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

    protected abstract String getCreateTableSQL(
            String tableData);
}
