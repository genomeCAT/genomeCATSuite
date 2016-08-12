package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportFE
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Oct 8, 2010
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.logging.SimpleFormatter;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInExperiment;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/**
 * 170413 kt list and delete spots without position information 100413 kt
 * initSample check if sample already exists 150812 kt extends Import_batch
 *
 */
public abstract class ImportExperimentFile extends Import_batch implements XPortExperimentFile {
    //variables to manage connection to db and file

    protected ExperimentDetail experimentdetail = null;
    protected PlatformDetail platformdetail = null;
    protected PlatformData platformdata = null;
    protected Defaults.Method method = null;
    protected Defaults.Type type = null;
    protected Integer nofChannel = 2;
    protected ExperimentData experimentdata;
    boolean centerMean = false;
    boolean centerMedian = false;
    boolean dyeSwap = false;

    

    public boolean isCenterMean() {
        return this.centerMean;
    }

    @Override
    public void setCenterMean(boolean b) {
        this.centerMean = b;
        Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.INFO, "set " + b);

    }
    final static String procCenterMedian = "center by median\n";
    final static String procCenterMean = "center by mean\n";
    final static String procDyeSwap = "dyeswap\n";

    public boolean isCenterMedian() {
        return this.centerMedian;
    }

    public void setCenterMedian(boolean b) {
        this.centerMedian = b;
        Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.INFO, "set " + b);

        /*if (b && this.experimentdata != null &&
    this.experimentdata.getProcProcessing() != null &&
    this.experimentdata.getProcProcessing().indexOf(procCenterMedian) < 0) {
    experimentdata.setProcProcessing(procCenterMedian);
    }
    if (!b && this.experimentdata != null &&
    this.experimentdata.getProcProcessing() != null &&
    this.experimentdata.getProcProcessing().indexOf(procCenterMedian) >= 0) {
    experimentdata.getProcProcessing().replace(procCenterMedian, "");
    }*/
    }

    public boolean isDyeSwap() {
        return this.dyeSwap;
    }

    public void setDyeSwap(boolean b) {
        this.dyeSwap = b;
        Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.INFO, "set " + b);
        /* if (b && this.experimentdata != null &&
    this.experimentdata.getProcProcessing() != null &&
    this.experimentdata.getProcProcessing().indexOf(procDyeSwap) < 0) {
    experimentdata.setProcProcessing(procDyeSwap);
    }
    if (!b && this.experimentdata != null &&
    this.experimentdata.getProcProcessing() != null &&
    this.experimentdata.getProcProcessing().indexOf(procDyeSwap) >= 0) {
    experimentdata.getProcProcessing().replace(procDyeSwap, "");
    }*/
    }

    /**
     * init Instance to import new file
     *
     * @param nameFile
     */
    public void newImportFile(String filename) throws Exception {
        super.newImport(filename);
        this.experimentdetail = null;
        this.experimentdata = null;
        this.platformdata = null;
        this.platformdetail = null;
        this.method = null;
        this.type = null;
        this.con = null;
        this.centerMean = false;
        this.centerMedian = false;
        this.dyeSwap = false;
        this.hasHeader = true; //100413

    }

    public int getNofChannel() {
        return nofChannel;
    }

    public void setNofChannel(int nofChannel) {
        this.nofChannel = nofChannel;
    }

    public void setMethod(Defaults.Method m) {
        this.method = m;
    }

    public void setType(Defaults.Type t) {
        this.type = t;
    }

    public void setRelease(Defaults.GenomeRelease g) {
        this.release = g;
    }

    public boolean validateExperimentDetail(ExperimentDetail detail) {
        //return ((detail.getMethod().equalsIgnoreCase(this.method.toString())) && (detail.getType().equalsIgnoreCase(this.type.toString())));
        return true; // überschreiben erlaubt!!

    }

    public PlatformDetail getPlatformdetail() {
        return platformdetail;
    }

    public void setPlatformdetail(PlatformDetail platformdetail) {
        this.platformdetail = platformdetail;
    }

    public PlatformData getPlatformdata() {
        return platformdata;
    }

    public String getMethod() {
        return this.method != null ? this.method.toString() : "";
    }

    public String getType() {
        return this.type != null ? this.type.toString() : "";
    }

    public void setPlatformdata(PlatformData platformdata) {
        this.platformdata = platformdata;
    }

    public void initSampleList(ExperimentDetail ex) {

        String name = this.inFile.getName();

        int i = name.indexOf("Cy3_");
        int j = name.substring(i + 4).indexOf("_");
        SampleDetail sample1 = new SampleDetail();
        if (i >= 0) {

            if (j > 0) {
                sample1.setName(name.substring(i + 4, i + 4 + j));
            } else {
                sample1.setName(name.substring(i + 4));
            }

        } else {
            sample1.setName("Sample_Cy3");
        }
        //100413    kt  check if sample already exists
        SampleDetail d;
        try {
            d = ExperimentService.getSampleDetailByName(sample1.getName());
            if (d != null) {
                ex.addSample(d, true, false);
            } else {
                ex.addSample(sample1, true, false);
            }
        } catch (Exception ex1) {
            Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.SEVERE, "", ex1);
        }

        Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.INFO, sample1.getName());

        if (this.nofChannel > 1) {
            i = name.indexOf("Cy5_");
            j = name.substring(i + 4).indexOf("_");
            SampleDetail sample2 = new SampleDetail();
            if (i >= 0) {

                if (j > 0) {
                    sample2.setName(name.substring(i + 4, i + 4 + j));
                } else {
                    sample2.setName(name.substring(i + 4));
                }
            } else {
                sample2.setName("Sample_Cy5");
            }
            //100413    kt  check if sample already exists
            try {
                d = ExperimentService.getSampleDetailByName(sample2.getName());
                if (d != null) {
                    ex.addSample(d, false, true);
                } else {
                    ex.addSample(sample2, false, true);
                }
            } catch (Exception ex1) {
                Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.SEVERE, "", ex1);
            }
        }
    }

    public ExperimentDetail getExperimentDetail() {
        return experimentdetail;
    }
    //private String file_field_position = null;

    public void setExperimentDetail(ExperimentDetail _experimentdetail) {
        this.experimentdetail = _experimentdetail;
    }

    public ExperimentDetail initExperimentDetail() {
        ExperimentDetail detail = new ExperimentDetail();
        detail.setName(this.getExperimentNameFromFile());
        detail.setPlatform(this.platformdetail);
        detail.setMethod(this.platformdetail.getMethod()); //voreinstellunge

        detail.setNofChannel(this.nofChannel);
        detail.setType((this.platformdetail.getType()));

        return detail;
    }

    public ExperimentData getExperimentData() {
        this.experimentdata = new ExperimentData();
        if (this.experimentdetail == null) {
            Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.SEVERE,
                    "getExperimentData: invalid experimentdetail or  (null)");
            return null;
        }
        this.experimentdata.setExperiment(experimentdetail);
        this.experimentdata.setPlatformdata(this.platformdata);
        this.experimentdata.setName(this.experimentdetail.getName());
        //this.experimentdata.setExperiment(this.experimentdetail);
        this.experimentdata.setOriginalFile(this.inFile.getAbsolutePath());
        this.experimentdata.setGenomeRelease(this.platformdata.getGenomeRelease());
        this.experimentdata.initTableData();
        this.experimentdata.setClazz(this.getDataClazz());
        this.experimentdata.setDataType(Defaults.DataType.RAW);
        this.experimentdata.setParent(null);
        this.experimentdata.setOwner(ExperimentService.getUser());
        if (this.centerMean) {

            experimentdata.addProcProcessing(procCenterMean);
        }
        if (this.centerMedian) {

            experimentdata.addProcProcessing(procCenterMedian);
        }
        if (this.dyeSwap) {

            experimentdata.addProcProcessing(procDyeSwap);
        }
        return this.experimentdata;
    }

    @Override
    public String setMappingFile2DBColNames(List<String[]> _map) {
        this.map = _map;
        if (this.hasSplitField()) {
            this.map = this.setSplitFieldCols(this.map);
        }
        return null;
    }

   

    public abstract void generateTable(String tableData) throws Exception ;
        /*Statement s;
        try {
            // con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
            s.execute(
                    "DROP TABLE if EXISTS " + tableData);
            String sql = this.getCreateTableSQL(tableData);
            Logger.getLogger(ImportExperimentFile.class.getName()).log(
                    Level.INFO,
                    sql);
            s.execute(sql);
            
            // update insert row with position data from platform table 
            String triggername = "tr_position_" + this.experimentdata.getTableData();
            String tablePlatform = this.experimentdata.getPlatformdata().getTableData();
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
                    + "DECLARE varChrom varchar(45);  "
                    + "DECLARE varStart INT; "
                    + "DECLARE varStop INT;   "
                    + "DECLARE cs CURSOR FOR  SELECT chrom ,chromStart, chromEnd FROM "
                    + tablePlatform + " WHERE " + tablePlatform + ".probeName = new.probeName; "
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

            Logger.getLogger(ImportExperimentFile.class.getName()).log(
                    Level.INFO,
                    sql);

            s.execute(sql);
        } catch (Exception ex) {
            Logger.getLogger(ImportExperimentFile.class.getName()).log(Level.SEVERE, "generateTable", ex);
            throw ex;
        }
    }*/
    Logger logger;

    @Override
    public ExperimentData doImportFile(InformableHandler ifh) {

        logger = Logger.getLogger(ImportExperimentFile.class.getName());
        ifh.setFormatter(new SimpleFormatter());

        logger.addHandler(ifh);
        return this.doImportFile();
    }

    @SuppressWarnings("empty-statement")
    public ExperimentData doImportFile() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        DecimalFormat myFormatter = new DecimalFormat("0.#####", otherSymbols);
        boolean isNewDetail = false;

        if (this.experimentdetail == null || this.experimentdata == null) {
            logger.log(Level.SEVERE,
                    "doImportFile: invalid experimentthis.experimentdetail or experimentdata (null)");
            return null;
        } else {
            logger.log(Level.INFO,
                    "doImportFile: " + this.experimentdetail.toFullString());
        }
        em = DBService.getEntityManger();

        Statement s = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "getDBConnection", ex);
        }

        try {
            List<SampleInExperiment> list = new Vector<SampleInExperiment>(this.experimentdetail.getSamples());
            Collections.copy(list, this.experimentdetail.getSamples());
            this.experimentdetail.setSamples(Collections.EMPTY_LIST);
            // ^ auto persist vermeiden

            em.getTransaction().begin();

            if (this.experimentdetail.getExperimentDetailID() == null
                    || em.find(ExperimentDetail.class, this.experimentdetail.getExperimentDetailID()) == null) {

                isNewDetail = true;

                Logger.getLogger(
                        ImportExperimentFile.class.getName()).log(Level.INFO,
                                "doImportFile: create experiemnt " + this.experimentdetail.toFullString());

                // make experimentthis.experimentdetail persistent in db
                em.persist(this.experimentdetail);
                //em.flush();
            } else {
                // update experiment from db with current entity
                this.experimentdetail = em.merge(this.experimentdetail);
            }
            //this.experimentdetail = em.merge(this.experimentdetail);
            // init experimentdata
            //this.experimentdata.setExperiment(this.experimentdetail);
            this.experimentdetail.addExperimentData(experimentdata);
            this.experimentdata.initTableData();

            // make experimentdata persistent in db
            em.persist(this.experimentdata);
            em.flush(); // get autogenerated keys

            em.refresh(this.experimentdetail);
            em.refresh(this.experimentdata);

            //retrieve samples if ness
            // load sampleinexperiment and samples to persistent context
            this.experimentdetail.setSamples(this.importSamples(list, isNewDetail));

            //synch sample-lists with db
            //em.merge(this.experimentdetail);
            em.flush();
            // kt 220212 test if samples set to exp detail 
            em.refresh(this.experimentdetail);

            // create  db table
            this.generateTable(this.experimentdata.getTableData());
            // read data -> insert into table

            error = this.importData(this.experimentdata.getTableData());

            Logger.getLogger(
                    ImportExperimentFile.class.getName()).log(Level.INFO,
                            "doImportFile: read data, number of errors: " + error);

            //170413    kt  list and delete spots without position information
            String _sql = " SELECT count(*) from " + this.experimentdata.getTableData()
                    + " WHERE chrom is null";
            ResultSet rs = s.executeQuery(_sql);

            if (rs.next()) {
                Integer nofErr = rs.getInt(1);
                if (nofErr > 0) {
                    try {
                        _sql = "SELECT probeID from " + this.experimentdata.getTableData()
                                + " WHERE chrom is null into outfile \'" + this.experimentdata.getOriginalFile() + ".err"
                                + "\'";
                        s.execute(_sql);
                    } catch (SQLException sQLException) {
                        Logger.getLogger(
                                ImportExperimentFile.class.getName()).log(Level.WARNING,
                                        "could not export list of invalid features to file "
                                        + this.experimentdata.getOriginalFile() + ".err");

                    }

                    Logger.getLogger(
                            ImportExperimentFile.class.getName()).log(Level.INFO,
                                    "doImportFile: " + nofErr + "  features could not be imported due missing spot at platform: "
                                    + "see " + this.experimentdata.getOriginalFile() + ".err" + " (lists  this features) ");
                    _sql = " DELETE from " + this.experimentdata.getTableData()
                            + " WHERE chrom is null";
                    s.execute(_sql);
                    int nodel = s.getUpdateCount();
                    System.out.println(nodel);
                    this.nosucc -= nodel;
                }
            }
            // 170812 kt test import without prim key auto
            /* String _sql = "ALTER TABLE " + this.experimentdata.getTableData() + 
            " add column id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT FIRST, add primary key(id)";
            
            boolean update = s.execute(_sql);*/

            //update array
            String sql = "SELECT count(*) from " + this.experimentdata.getTableData();
            rs = s.executeQuery(sql);

            rs.next();
            Integer nofSpots = rs.getInt(1);

            // todo median
            sql = "SELECT  AVG(ratio),  VAR_SAMP(ratio), "
                    + " STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) "
                    + " from " + this.experimentdata.getTableData()
                    + " where (chromStart + chromEnd) != 0";
            rs = s.executeQuery(sql);
            rs.next();
            /* try {
            this.experimentdata = em.merge(this.experimentdata);
            } catch (javax.persistence.PersistenceException e) {
            this.experimentdata = em.find(ExperimentData.class, this.experimentdata.getId());
            }*/

            this.experimentdata.setNof(nofSpots);
            this.experimentdata.setMean(new Double(myFormatter.format(rs.getDouble(1))));
            this.experimentdata.setVariance(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
            this.experimentdata.setStddev(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
            this.experimentdata.setMinRatio(rs.getDouble(4));
            this.experimentdata.setMaxRatio(rs.getDouble(5));

            try {
                double median = DBUtils.getMedian(this.experimentdata.getTableData(), this.getRatioCol());
                this.experimentdata.setMedian(median);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "get median", e);
            }

            // test get modified, id ...???
            //this.experimentdata = em.merge(this.experimentdata);
            if (this.dyeSwap) {
                //experimentdata.setProcProcessing(this.getClass().getName());
                experimentdata.setParamProcessing(Defaults.DYESWAP);

            }
            if (this.centerMean || this.centerMedian) {
                double factor = 0;
                //experimentdata.setProcProcessing(this.getClass().getName());
                if (this.centerMean) {
                    factor = experimentdata.getMean();

                    experimentdata.setParamProcessing(
                            (experimentdata.getParamProcessing() == null ? "" : experimentdata.getParamProcessing()) + "centerMean: " + experimentdata.getMean());

                } else {

                    factor = experimentdata.getMedian();
                    experimentdata.setParamProcessing(
                            experimentdata.getParamProcessing() + "centerMedian: " + experimentdata.getMedian());
                }
                PreparedStatement ps = con.prepareStatement(
                        "update " + experimentdata.getTableData()
                        + " set " + this.getRatioCol() + " =  " + this.getRatioCol() + "-?");

                ps.setDouble(1, factor);
                ps.executeUpdate();

            }
            em.flush();
            em.getTransaction().commit();
            this.experimentdetail.addExperimentData(experimentdata);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "doImportFile: -start rollback", ex);
            if (con != null) {
                try {
                    s = con.createStatement();
                    s.execute("DROP TABLE if EXISTS " + this.experimentdata.getTableData());
                } catch (Exception ie) {
                    logger.log(Level.SEVERE, "drop data table", ie);
                }
            }

            if (em.getTransaction().isActive()) {
                logger.log(Level.INFO,
                        "doImportFile: rollback");
                em.getTransaction().rollback();
            } else {
                try {
                    if (this.experimentdata.getId() != null) {
                        logger.log(Level.INFO,
                                "doImportFile: remove ExperimentData  " + this.experimentdata.getId());
                        em.remove(em.find(PlatformData.class, this.experimentdata.getId()));
                    }
                    if (isNewDetail && this.experimentdetail.getExperimentDetailID() != null) {
                        logger.log(Level.INFO,
                                "doImportFile: remove Experimentthis.experimentdetail  "
                                + this.experimentdetail.getExperimentDetailID());
                        em.remove(em.find(PlatformData.class,
                                this.experimentdetail.getExperimentDetailID()));
                    }
                } catch (Exception ex1) {
                    logger.log(Level.SEVERE, "delete etntities", ex1);
                }

            }
            return null;
        } finally {
            try {
                con.close();
                em.close();
            } catch (Exception e) {
                Logger.getLogger(
                        ImportExperimentFile.class.getName()).log(Level.SEVERE, "close em", e);
            }
        }
        ExperimentService.notifyListener();
        Logger.getLogger(
                ImportExperimentFile.class.getName()).log(Level.INFO, "doImportFile: done");
        return this.experimentdata;
    }

    /**
     * create or update sample and sampleinExperiment
     */
    protected List<SampleInExperiment> importSamples(
            List<SampleInExperiment> samples, boolean isNewExperiment) throws Exception {
        // sample existiert bereits ?  merge : create
        // sample existiert und bereits fürs Experiment : merge : create
        if (!em.isOpen()) {
            throw new RuntimeException("importSamples method meant to be inside open em/transaction!!");
        }
        List<SampleInExperiment> new_samples = new Vector<SampleInExperiment>();
        for (SampleInExperiment sie : samples) {

            SampleDetail detail = sie.getSample();
            if (detail.getSampleDetailID() == null
                    || em.find(SampleDetail.class, detail.getSampleDetailID()) == null) {
                //isNewPlatformDetail = true;

                Logger.getLogger(
                        ImportExperimentFile.class.getName()).log(Level.INFO,
                                "importSamples: create sampledetail " + detail.toFullString());

                // make new sampledetail persistent in db
                em.persist(detail);  //cascade??

                //em.flush();
            } else {
                // update sample from db with current entity
                detail = em.merge(detail);

            }

            em.flush();
            em.refresh(detail);
            sie.setSample(detail);
            sie.setExperiment(this.experimentdetail);

            if (isNewExperiment)// old experiments have samples attached
            {
                Logger.getLogger(
                        ImportExperimentFile.class.getName()).log(Level.INFO,
                                "importSamples: create new SampleInExperiment " + sie.toFullString());
                em.persist(sie);
                //em.flush();
            } else {
                Logger.getLogger(
                        ImportExperimentFile.class.getName()).log(Level.INFO,
                                "importSamples: merge existing SampleInExperiment " + sie.toFullString());

                sie = em.merge(sie);

            }

            new_samples.add(sie);
            //em.flush();

        }
        return new_samples; // return entities from persistent context

    }

    abstract protected String getDataClazz();

    abstract protected String getExperimentNameFromFile();

    private String getRatioCol() {
        return "ratio";
    }
}
