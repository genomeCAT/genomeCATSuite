package org.molgen.genomeCATPro.cghpro.chip;
//</editor-fold>

/**
 * @name ChipImpl
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @author Wei Chen
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.data.DataService;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;

import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.ISpot;

/*
 * 300512 saveTrackToDB() addSamples (of ParentTrack) for Track
 * 050612 kt    update notification
 */
public abstract class ChipImpl implements Chip {

    protected Data dataEntity = null;
    /**
     * indicate if there is any error during the chip loading
     */
    boolean error = false;
    private Connection con;

    public ChipImpl() {
    }

    /**
     * Constructor used for error handling
     */
    public ChipImpl(boolean error) {
        this.error = error;
    }

    public ChipImpl(Data d) {
        this.dataEntity = d;
        if (d == null) {
            this.error = true;
        }
    }

    public String getName() {
        return (this.getDataEntity() != null ? this.getDataEntity().getName() : "");
    }

    public abstract void dataFromSpots(List<? extends ISpot> spots);

    public boolean getError() {
        return this.error;
    }

    public Data getDataEntity() {
        return this.dataEntity;
    }

    public void setDataEntity(Data d) {
        this.dataEntity = d;
        if (d == null) {
            this.setError(true);
        }
    }

    public void setError(boolean b) {
        this.error = b;
    }

    public abstract List<? extends Region> getData();

    public abstract double getMedianLog2Ratio(List<? extends Object> data);

    private void saveToDB() throws SQLException, Exception {
        try {
            DataService.saveDataToDB(getDataEntity(), (List<? extends IFeature>) this.getData());
            if (this.error) {
                throw new RuntimeException("Error");
            }
            String sql = "SELECT count(*) from " + getDataEntity().getTableData();

            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sql);

            rs.next();

            getDataEntity().setNof(rs.getInt(1));
            // TODO MEDIAN!
            sql = "SELECT  AVG(ratio), VAR_SAMP(ratio), STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) " + " from " + getDataEntity().getTableData();

            rs = s.executeQuery(sql);

            rs.next();
            //DecimalFormat myFormatter = new DecimalFormat("0.#####E0");
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            //otherSymbols.setGroupingSeparator(',');
            DecimalFormat myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
            getDataEntity().setMean(new Double(myFormatter.format(rs.getDouble(1))));
            getDataEntity().setVariance(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
            getDataEntity().setStddev(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
            getDataEntity().setMinRatio(rs.getDouble(4));
            getDataEntity().setMaxRatio(rs.getDouble(5));
            try {
                double median = DBUtils.getMedian(getDataEntity().getTableData(), "ratio");
                getDataEntity().setMedian(median);
            } catch (Exception e) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE,
                        "get median", e);
                throw e;
            }
        } catch (Exception ex) {
            Logger.getLogger(DataService.class.getName()).log(Level.SEVERE, "saveToDB", ex);
            throw ex;
        } finally {
            con.close();
        }
    }

    public void saveTrackToDB() {
        EntityManager em = DBService.getEntityManger();
        EntityTransaction userTransaction = null;

        //if(experiment != null)
        //    if(experiment.getArray().getGenomeRelease().contains(this.release))
        Statement s = null;

        try {

            Data _data = TrackService.getTrack(
                    this.getDataEntity().getName(),
                    this.getDataEntity().getGenomeRelease());

            if (_data != null) {
                throw new RuntimeException(
                        "Track exists already in database "
                        + this.getDataEntity().getName() + " "
                        + this.getDataEntity().getGenomeRelease() + " "
                        + this.getDataEntity().getDataType() + " -> "
                        + this.getDataEntity().toFullString());
            }

            userTransaction = em.getTransaction();
            userTransaction.begin();

            TrackService.persistsTrack((Track) getDataEntity(), em);

            this.saveToDB();
            //em.flush();

            Data parent = this.dataEntity.getParent();

            //300512
            if (parent != null && parent instanceof Track && ((Track) parent).getSamples().size() > 0) {

                TrackService.forwardSamples(((Track) parent).getSamples(), (Track) this.dataEntity, em);

            }
            userTransaction.commit();

            if (parent != null) {
                parent.addChildData(this.dataEntity);

            }

        } catch (Exception e) {

            Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE, "Exception: ", e);
            //throw new RuntimeException(e);
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            try {
                s = con.createStatement();
                s.execute("DROP TABLE if EXISTS " + this.dataEntity.getTableData());
            } catch (Exception ie) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE, null, ie);
            }

            if (userTransaction != null && userTransaction.isActive()) {
                userTransaction.rollback();
            }
            // transaction no longer active
            // merge would create entity, so use find!!
            em.getTransaction().begin();
            Track _track = null;
            if (getDataEntity() != null && getDataEntity() != null) {
                _track = em.find(Track.class, _track.getTrackID());
                Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO,
                        "Exception: find  Track " + _track.getTrackID());
            }
            if (_track != null) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO,
                        "Exception: delete  Track " + _track.getTrackID());
                try {
                    em.remove(_track);
                } catch (Exception es) {
                    Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE, "delete Track", es);
                }
            }

            em.getTransaction().commit();

        } finally {
            em.close();
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE, "", ex);
            }
        }
    }

    public void saveChipToDB() throws Exception {
        if (this.dataEntity instanceof ExperimentData) {
            this.saveExperimentToDB();
        }
        if (this.dataEntity instanceof Track) {
            this.saveTrackToDB();
        }

        ExperimentService.notifyListener(); // 050612 kt

    }

    public void saveExperimentToDB() throws Exception {
        Statement s;
        EntityManager em = DBService.getEntityManger();
        EntityTransaction userTransaction = null;
        try {

            ExperimentData _data = ExperimentService.getExperimentData(
                    this.getDataEntity().getName(),
                    this.getDataEntity().getGenomeRelease());

            if (_data != null) {
                throw new RuntimeException(
                        "Experiment exists already in database "
                        + this.getDataEntity().getName() + " "
                        + this.getDataEntity().getGenomeRelease() + " "
                        + this.getDataEntity().getDataType() + " -> "
                        + this.getDataEntity().toFullString());
            }

            userTransaction = em.getTransaction();
            userTransaction.begin();
            ExperimentService.persistsExperimentData((ExperimentData) getDataEntity(), em);
            this.saveToDB();
            em.flush();
            userTransaction.commit();

        } catch (Exception e) {
            try {
                //throw new RuntimeException(e);
                con = Database.getDBConnection(CorePropertiesMod.props().getDb());
                try {
                    s = con.createStatement();
                    s.execute("DROP TABLE if EXISTS " + getDataEntity().getTableData());
                } catch (Exception ie) {
                    Logger.getLogger(DataService.class.getName()).log(Level.SEVERE, null, ie);
                }

                if (userTransaction != null && userTransaction.isActive()) {
                    userTransaction.rollback();
                }
// transaction no longer active
// merge would create entity, so use find!!

                ExperimentData _data = null;
                em.getTransaction().begin();
                if (getDataEntity() != null && getDataEntity() != null) {
                    _data = em.find(ExperimentData.class, getDataEntity().getId());
                    Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO,
                            "Exception: find Data " + getDataEntity().getId());
                }

                if (_data != null) {
                    Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO,
                            "Exception: delete  Data " + _data.getId());
                    try {
                        em.remove(_data);
                    } catch (Exception es) {
                        Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE, "delete Data", es);
                    }
                }

                em.getTransaction().commit();
            } catch (Exception exception) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE,
                        "ERROR: ", exception);
            }
        } finally {
            em.close();
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.SEVERE, "", ex);
            }
        }

    }

    public static Chip loadChipAsTrackFromDB(
            Class chipclazz, Data d) {
        /*List<Track> _tracks = null;
        
        Data data = null;
        try {
        if (datatype == null) {
        _tracks = TrackService.getTrack(name, genomeRelease);
        } else {
        _tracks = TrackService.getTrack(name, genomeRelease, datatype);
        // todo more than one?
        }
        } catch (Exception exception) {
        
        Logger.getLogger(ChipImpl.class.getName()).log(
        Level.WARNING, "loadChipAsExperimentFromDB", exception);
        return new ChipFeature(true);
        }
        if (_tracks.size() == 0) {
        
        
        Logger.getLogger(ChipImpl.class.getName()).log(
        Level.WARNING,
        "No Data found for " +
        name + " " + genomeRelease + " " + datatype);
        return new ChipFeature(true);
        }
        data = _tracks.get(0);
         */
        return ChipImpl.loadChipFromDB(chipclazz, d);

    }

    /*public static Chip loadChipAsExperimentFromDB(
    Class chipclazz, String name, String genomeRelease, String datatype) {*/
    public static Chip loadChipAsExperimentFromDB(Class chipclazz, Data d) {
        /*List<ExperimentData> list = null;
        
        Data data = null;
        try {
        if (datatype == null) {
        list = ExperimentService.getExperimentData(name, genomeRelease);
        } else {
        list = ExperimentService.getExperimentData(name, genomeRelease, datatype);
        // todo more than one?
        }
        } catch (Exception exception) {
        
        Logger.getLogger(ChipImpl.class.getName()).log(
        Level.WARNING, "loadChipAsExperimentFromDB", exception);
        return new ChipFeature(true);
        }
        if (list.size() == 0) {
        
        
        Logger.getLogger(ChipImpl.class.getName()).log(
        Level.WARNING,
        "No Data found for " +
        name + " " + genomeRelease + " " + datatype);
        return new ChipFeature(true);
        }
        data = list.get(0);
         */

        return ChipImpl.loadChipFromDB(chipclazz, d);

    }

    //variabel: 
    //      welcher Chip soll geladen werden
    //          Chip muss mit Daten kompatibel sein 
    //              z.b. CBSFeature nicht mit ChipSpot
    //      soll Data aus Track oder Experiment geholt werden          
    static Chip loadChipFromDB(
            Class chipclazz, Data d) {
        Chip c;
        IFeature f = null;

        try {
            // lookup Feature.Clazz
            f = DataService.getFeatureClazz(d.getClazz());
        } catch (ClassCastException e) {
            Logger.getLogger(ChipImpl.class.getName()).log(
                    Level.SEVERE, "loadChipFromDB", e);
            return new ChipFeature(true);
        }
        try {
            c = (Chip) chipclazz.newInstance();
            c.setDataEntity(d);
        } catch (InstantiationException ex) {
            Logger.getLogger(ChipImpl.class.getName()).log(
                    Level.SEVERE, "loadChipFromDB", ex);
            return new ChipFeature(true);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ChipImpl.class.getName()).log(
                    Level.SEVERE, "loadChipFromDB", ex);
            return new ChipFeature(true);
        }
//load Data from DB
        List<? extends IFeature> list = null;
        try {
            list = f.loadFromDB(c.getDataEntity());
            c.setData(list);
        } catch (Exception exception) {
            Logger.getLogger(ChipImpl.class.getName()).log(
                    Level.SEVERE, "loadChipFromDB", exception);
            c.setError(true);
        }

        /*if (!(c instanceof ChipFeature)) {
        Logger.getLogger(FeatureImpl.class.getName()).log(Level.SEVERE,
        "dbLoadFeatures: Chip class not valid " + c.getClass().getName());
        return;
        }*/
        // ((ChipFeature) c).addFeature(f);
        return c;

    }

// get Data Entity - Track, Experiment
// get FeatureClass
// load from DB
    public ChipImpl(ChipImpl c) {
        this.dataEntity = c.dataEntity;
        if (this.dataEntity == null) {
            this.error = true;
        } else {
            this.error = false;
        }

    }

    // abhängig von der Struktur der Spot-Tabelle laden der Daten
    // dynamisches Einbinden von Modulen?
    // zb. LoadBacChip for GPR
    // BacChip = GPR.loadBacChip
    // BacChip = Agilent.loadBacChip
    public GenomeRelease getRelease() {
        return GenomeRelease.toRelease(dataEntity.getGenomeRelease());
    }
    /**
     * draw histogram for original ratios and normalized ratios in 2 different
     * plots, then combine them into 1 tabbedpane. If the chip remain not
     * normalized, there will be only one plot.
     *
     */
    /**
     * kt public void histogramRatio(){ JFrame f = new JFrame(); Container cp =
     * f.getContentPane(); JTabbedPane tabbedPane= new JTabbedPane();
     * cp.add(tabbedPane); Histogram hRatio = new
     * Histogram(MainFrame.screenSize.width-100,MainFrame.screenSize.height-150,
     * Color.red, false, false);
     *
     * hRatio.addLine(0); hRatio.newData(getRatios(),0.1, true);
     *
     * hRatio.addXLabel("log2(R/G)"); hRatio.addTitle("Histogram log2(R/G)");
     *
     * if (ifNormalize){ Histogram hNormalRatio = new
     * Histogram(MainFrame.screenSize.width-100,MainFrame.screenSize.height-150,
     * Color.red, false, false);
     *
     * hNormalRatio.addLine(0); hNormalRatio.newData(getNormalRatios(),0.1,
     * true);
     *
     * hNormalRatio.addXLabel("log2(R/G)"); hNormalRatio.addTitle("Histogram
     * log2(R/G)"); tabbedPane.addTab("normalized data",hNormalRatio);
     *
     * }
     *
     * tabbedPane.addTab("original data",hRatio); plots.add(f);
     *
     * cgh.graphics.Console.run(f,1);
     *
     *
     *
     *
     * }
     */
    /**
     * draw scatterplot for original signal intensity and normalized signal
     * intensity in 2 different plots, then combine them into 1 tabbedpane. If
     * the chip remain not normalized, there will be only one plot.
     *
     */
    /**
     * kt public void scatterPlot(){ ScatterPlotFrame f = new
     * ScatterPlotFrame(); Container cp = f.getContentPane(); JTabbedPane
     * tabbedPane= new JTabbedPane(); cp.add(tabbedPane); ScatterPlotSignal orig
     * = new ScatterPlotSignal(this, false); orig.plot(); if (ifNormalize){
     * ScatterPlotSignal norm = new ScatterPlotSignal(this, true); norm.plot();
     * tabbedPane.addTab("normalized data",norm); }
     *
     * tabbedPane.addTab("original data",orig); plots.add(f);
     * cgh.graphics.Console.run(f,1);
     *
     *
     *
     *
     *
     * }
     */
    /**
     * draw boxplot for original ratios and normalized ratios in 2 different
     * plots, then combine them into 1 tabbedpane. If the chip remain not
     * normalized, there will be only one plot.
     *
     */
    /**
     * public void boxPlot(){
     *
     * JFrame f = new JFrame(); Container cp = f.getContentPane(); JTabbedPane
     * tabbedPane= new JTabbedPane(); cp.add(tabbedPane);
     *
     * BoxPlotChip bRatio = new
     * BoxPlotChip(MainFrame.screenSize.width-100,MainFrame.screenSize.height-150);
     * Univariate[] ratios = new Univariate[blocks.size()+1]; ratios[0] =
     * getRatios(); for(int i = 1; i<=blocks.size();i++){
     * ratios[i]=((Block)blocks.get(i-1)).getRatios();
     *
     * }
     * bRatio.newData(ratios);
     *
     * if(ifNormalize){ BoxPlotChip bNormalRatio = new
     * BoxPlotChip(MainFrame.screenSize.width-100,MainFrame.screenSize.height-150);
     * Univariate[] normalRatios = new Univariate[blocks.size()+1];
     * normalRatios[0] = getNormalRatios(); for(int i = 1;
     * i<=blocks.size();i++){
     * normalRatios[i]=((Block)blocks.get(i-1)).getNormalRatios();
     *
     * }
     * bNormalRatio.newData(normalRatios);
     *
     * tabbedPane.addTab("normalized data",bNormalRatio); }
     *
     * tabbedPane.addTab("original data",bRatio);
     *
     * plots.add(f); cgh.graphics.Console.run(f,1);
     *
     * }	*
     */
    /**
     * draw QQplot for original ratios and normalized ratios in 2 different
     * plots, then combine them into 1 tabbedpane. If the chip remain not
     * normalized, there will be only one plot.
     *
     */
    /**
     * kt public void normalProbabilityPlot(){
     *
     * JFrame f = new JFrame(); Container cp = f.getContentPane(); JTabbedPane
     * tabbedPane= new JTabbedPane(); cp.add(tabbedPane);
     *
     * NormalPlot nRatio = new
     * NormalPlot(MainFrame.screenSize.width-100,MainFrame.screenSize.height-150);
     * nRatio.newData(getRatios(), true); nRatio.addQQLine();
     * nRatio.addTitle("Normal Probability Plot log2(R/G)");
     *
     * if (ifNormalize){ NormalPlot nNormalRatio = new NormalPlot
     * (MainFrame.screenSize.width-100,MainFrame.screenSize.height-150);
     * nNormalRatio.newData(getNormalRatios(), true); nNormalRatio.addQQLine();
     * nNormalRatio.addTitle("Normal Probability Plot log2(R/G)");
     * tabbedPane.addTab("normalized data",nNormalRatio); }
     *
     * tabbedPane.addTab("original data",nRatio); plots.add(f);
     * cgh.graphics.Console.run(f,1); }
     */
    /**
     * draw MAplot for original data and normalized data in 2 different plots,
     * then combine them into 1 tabbedpane. If the chip remain not normalized,
     * there will be only one plot.
     *
     */
    /**
     * public void maPlot(){
     *
     * JFrame f = new JFrame(); Container cp = f.getContentPane(); JTabbedPane
     * tabbedPane= new JTabbedPane(); cp.add(tabbedPane);
     *
     * ScatterPlot mRatio = new
     * ScatterPlot(MainFrame.screenSize.width-100,MainFrame.screenSize.height-150);
     * mRatio.newData(getF635PlusF532(),getRatios(), true);
     * mRatio.addLine(0,"HORIZONTAL_LINE" ); mRatio.addTitle("MA Plot");
     *
     * if(ifNormalize){ ScatterPlot mNormalRatio = new ScatterPlot
     * (MainFrame.screenSize.width-100,MainFrame.screenSize.height-150);
     * mNormalRatio.newData( getF635NormPlusF532Norm(),getNormalRatios(), true);
     * mNormalRatio.addLine(0,"HORIZONTAL_LINE"); mNormalRatio.addTitle("MA
     * Plot"); tabbedPane.addTab("normalized data",mNormalRatio); }
     *
     * tabbedPane.addTab("original data",mRatio); plots.add(f);
     * cgh.graphics.Console.run(f,1); }
     */
}
