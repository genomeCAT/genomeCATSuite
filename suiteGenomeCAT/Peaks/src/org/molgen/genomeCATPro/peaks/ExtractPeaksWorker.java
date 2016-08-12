package org.molgen.genomeCATPro.peaks;

/**
 * @name ExtractPeaksWorker.java
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
import org.molgen.genomeCATPro.cghpro.chip.*;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Informable;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.peaks.cnvcat.AberrationCNVCAT;
import org.molgen.genomeCATPro.data.IFeature;

/**
 * 200712 getMAD - adapt to new format for param string
 *
 */
public class ExtractPeaksWorker extends SwingWorker<ChipFeature, String> {

    public final static String methodName = "ExtractRegions";
    public static final Integer DEF_WINDOW = 2;
    public static final Double DEF_THRESHOLD = 0.2;
    public static final Double DEF_OUTLIER = 20.0;
    public static final Integer DEF_GAP = 1000;
    ChipFeature parentChip;
    String trackId;
    Double threshold;
    Integer window;
    Double outlier;
    boolean fakePeak;
    Integer maxGap;
    Double mad;
    private final Informable informable;
    int progressState;
    boolean isCBS = false;
    String madSource = "";

    public ExtractPeaksWorker(
            ChipFeature c,
            String trackId,
            Double threshold,
            Integer window,
            Double outlier,
            Integer maxGap,
            boolean fakePeak,
            Informable inf) {
        this.parentChip = c;
        this.informable = inf;
        this.trackId = trackId;
        this.threshold = threshold;
        this.window = window;
        this.outlier = outlier;
        this.maxGap = maxGap * 1000;
        this.fakePeak = fakePeak;

    }

    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {
            informable.messageChanged(message);
        }
    }

    protected ChipFeature doInBackground() throws Exception {
        publish("run ExtractRegions in Background...");
        setProgress(progressState = 0);

        publish("get Quality factor (MAD) ");
        mad = getMAD();

        setProgress(progressState = 10);
        ChipFeature newChip = doExtract(70);
        setProgress(progressState = 80);
        boolean error = newChip.getError();
        if (!error) {
            publish("Save Chip " + newChip.getDataEntity().getName());
            try {
                newChip.saveChipToDB();
            } catch (Exception exception) {
                error = true;
            }
        }
        setProgress(progressState = 100);

        //chip.setIfCbs(true);
        if (error) {
            publish("Error running Extract Regions - see logfile for details");
        } else {
            publish("Congratulations! Extract Regions succesfully finished");
        }
        return newChip;
        // update the progress

    }

    public ChipFeature doExtract(int processrange) throws Exception {

        // 
        //trackId += new String("_" + threshold + "_" + window + "_" + outlier);
        int progressStart = progressState;

        //int maxGap = 0;      // maximal space between 2 spots, if exceeded close current aberration
        int flagWindowDel = 0, flagOutDel = 0;
        int flagWindowDup = 0, flagOutDup = 0;
        AberrationCNVCAT abDeletion = null;
        AberrationCNVCAT abDuplication = null;
        double sumRatioDup = 0, sumRatioDel = 0, maxOutlierDel = 0, maxOutlierDup = 0;
        boolean bDeletion = false;
        boolean bDuplication = false;
        boolean bSkip = false;

        ChipFeature newChip = new ChipFeature(true);

        try {
            // new chip to hold peak data

            newChip = new ChipPeaks(
                    new Track(trackId,
                            this.parentChip.getDataEntity().getGenomeRelease(),
                            Defaults.DataType.PEAK.toString()));
            Track _track = (Track) newChip.getDataEntity();
            _track.setOwner((this.parentChip.getDataEntity().getOwner()));
            _track.setParent(this.parentChip.getDataEntity());

            _track.setClazz(AberrationCNVCAT.class.getName());
            _track.setProcProcessing(ExtractPeaksWorker.methodName);
            _track.addParamProcessing("Treshold:" + threshold);
            _track.addParamProcessing("Window:" + window);
            _track.addParamProcessing("maxGap:" + maxGap);
            _track.addParamProcessing("Outlier:" + outlier);
            _track.addParamProcessing("emptydata:" + fakePeak);
            _track.addParamProcessing(madSource);
            _track.addParamProcessing("MAD:" + mad);

            /**
             * get mad javax.persistence.Query qChip = em.createNamedQuery(
             * "AnalyzedChips.findById"); qChip.setParameter("id", chipId);
             *
             * AnalyzedChips chip = (AnalyzedChips) qChip.getSingleResult(); dq
             * = em.createNativeQuery( "SELECT DISTINCT chrom from " + chipId +
             * " WHERE chrom REGEXP '^chr([0-9]*|X|Y)$' and chrom != 'chr0'
             * order by chrom"); List<Vector> chromList = dq.getResultList();
             * List<Vector> list; double mad = 0.0; if (isCBS) { mad =
             * chip.getMadCbs(); } else { mad = getMADNormalRatio(chipId); }
             * out.write("MAD: " + mad + "\n");
             */
            // get data
            int i = 0;
            int chr = 0;
            for (String chrom : parentChip.chrFeatures.keySet()) {

                this.setProgress(progressState = (progressStart + (70 / 24 * ++chr)));
                flagWindowDel = flagOutDel = 0;
                flagWindowDup = flagOutDup = 0;
                maxOutlierDel = maxOutlierDup = 0;
                abDeletion = null;
                abDuplication = null;
                sumRatioDup = sumRatioDel = 0;
                bDeletion = false;
                bDuplication = false;
                bSkip = false;

                /*
                if (isCBS) {
                
                sql_select = "select id, chrom, chromStart, chromEnd, smoothRatioByCbs from " + chipId +
                " where chrom = \'" + chrom + "\' order by chromStart, chromEnd";
                } else {
                
                sql_select = "select id, chrom, chromStart, chromEnd, ratio from " +
                chipId +
                " where chrom = \'" + chrom + "\' order by chromStart, chromEnd";
                }
                 */
                List<? extends IFeature> list = (List<? extends IFeature>) parentChip.getData(chrom);
                Collections.sort(list, IFeature.comChromStart);

                for (IFeature f : list) {

                    // kt 0150909 check gap
                    if (abDuplication == null || abDeletion != null) {
                        if (abDuplication != null && (abDuplication.getChromEnd() + maxGap) < f.getChromStart()) {
                            bSkip = true;
                        }
                        if (abDeletion != null && (abDeletion.getChromEnd() + maxGap) < f.getChromStart()) {
                            bSkip = true;
                        }
                    }
                    bDuplication = false;
                    bDeletion = false;
                    if (!bSkip) {
                        if (f.getRatio() >= threshold || f.getRatio() <= (threshold * -1)) {
                            // Aberration
                            if (f.getRatio() >= threshold) {
                                // Duplication
                                bDuplication = true;
                                bDeletion = false;
                                flagOutDel++;

                                if (abDuplication == null
                                        || (isCBS && Math.abs(f.getRatio() - abDuplication.getRatio()) > mad)) {
                                    // new Duplication
                                    if (abDuplication != null && isCBS) {
                                        // save old Duplication
                                        ((ChipPeaks) newChip).addPeak(abDuplication, mad);
                                        publish("extract Region: " + abDuplication.toString());
                                    }
                                    abDuplication = new AberrationCNVCAT(
                                            new String("Region" + ++i),
                                            parentChip.getDataEntity().getName(),
                                            Aberration.DUPLICATION,
                                            f.getChrom(),
                                            f.getChromStart(),
                                            f.getChromEnd(),
                                            f.getRatio(), f.getId());
                                    flagWindowDup = 1;
                                    sumRatioDup = f.getRatio();
                                    flagOutDup = 0;
                                } else {
                                    // continue Duplication
                                    flagWindowDup++;
                                    sumRatioDup += f.getRatio();
                                    abDuplication.addFeature(
                                            f.getId(), f.getChromEnd(), f.getRatio());
                                }
                                maxOutlierDup = (int) Math.ceil((abDuplication.getCount() * outlier / 100) + 0.5d);
                            }
                            if (f.getRatio() <= (threshold * -1)) {
                                //Deletion
                                bDuplication = false;
                                bDeletion = true;
                                flagOutDup++;

                                if (abDeletion == null
                                        || (isCBS && Math.abs(f.getRatio() - abDeletion.getRatio()) > mad)) {
                                    // new Deletion 
                                    if (abDeletion != null && isCBS) {
                                        // save old Deletion
                                        ((ChipPeaks) newChip).addPeak(abDeletion, mad);
                                        publish("extract Region: " + abDeletion.toString());
                                    }

                                    abDeletion = new AberrationCNVCAT(
                                            new String("Region" + ++i),
                                            parentChip.getDataEntity().getName(),
                                            Aberration.DELETION,
                                            f.getChrom(),
                                            f.getChromStart(),
                                            f.getChromEnd(),
                                            f.getRatio(), f.getId());
                                    flagWindowDel = 1;
                                    sumRatioDel = f.getRatio();
                                    flagOutDel = 0;
                                } else {
                                    // continue Deletion
                                    flagWindowDel++;
                                    sumRatioDel += f.getRatio();
                                    abDeletion.addFeature(
                                            f.getId(), f.getChromEnd(), f.getRatio());
                                }
                                maxOutlierDel = (int) Math.ceil((abDeletion.getCount() * outlier / 100) + 0.5d);

                            }
                        } else {
                            // no aberration
                            flagOutDel++;
                            flagOutDup++;

                        }
                    }

                    if (!bDuplication && abDuplication != null
                            && (flagOutDup > maxOutlierDup || abDuplication.getChromEnd() + maxGap < f.getChromStart())) {
                        // no Duplication and outlier size extended or max gap extended
                        /*System.out.println("End Aberration because of to much outliers [" +
                        flagOutDup + " > " + maxOutlierDup +
                        ", actCount=" + abDuplication.getCount() +
                        ", procOutlier=" + outlier + " ]");
                         */
                        if (flagWindowDup >= window) {

                            // valid aberration size reached
                            //save Duplication
                            publish("extract Region: " + abDuplication.toString());
                            ((ChipPeaks) newChip).addPeak(abDuplication, mad);

                        }
                        flagWindowDup = 1;
                        flagOutDup = 0;
                        abDuplication = null;
                        sumRatioDup = 0;
                    }
                    if (!bDeletion && abDeletion != null
                            && (flagOutDel > maxOutlierDel || abDeletion.getChromEnd() + maxGap < f.getChromStart())) {
                        // no Deletion and outlier size extended or max gap extended
                        /*System.out.println("End Aberration because of to much outliers [" +
                        flagOutDel + " > " + maxOutlierDel +
                        ", actCount=" + abDeletion.getCount() +
                        ", procOutlier=" + outlier + " ]");
                         */
                        if (flagWindowDel >= window) {
                            //valid aberration size reached
                            //save Deletion
                            ((ChipPeaks) newChip).addPeak(abDeletion, mad);
                            publish("extract Region: " + abDeletion.toString());
                        }
                        flagWindowDel = 1;
                        abDeletion = null;
                        sumRatioDel = 0;
                        flagOutDel = 0;
                    }
                    if (bSkip) {
                        bSkip = false;
                        i--;    // skipped spot must be reviewed again

                    }
                }//end probes for chrom    

                if (abDeletion != null && flagWindowDel >= window) {
                    // save Deletion
                    ((ChipPeaks) newChip).addPeak(abDeletion, mad);
                    publish("extract Region: " + abDeletion.toString());
                }
                if (abDuplication != null && flagWindowDup >= window) {
                    // save Duplication
                    ((ChipPeaks) newChip).addPeak(abDuplication, mad);
                    publish("extract Region: " + abDuplication.toString());
                }
            }

            if (fakePeak) {

                AberrationCNVCAT fake = new AberrationCNVCAT(
                        new String("Region0"),
                        parentChip.getDataEntity().getName(),
                        "",
                        "chr0",
                        new Long(0),
                        new Long(0),
                        0.0, "");
                ((ChipPeaks) newChip).addPeak(fake, 1);
                publish("extract Region: " + fake.toString());
            }
            return newChip;

        } catch (Exception e) {
            publish(e.getMessage());
            Logger.getLogger(ExtractPeaksWorker.class.getName()).log(Level.SEVERE, "Error: ", e);
            return new ChipPeaks(true);
        }

    }

    /**
     * map original features and peaks return new chip with cbs values for each
     * feature
     *
     * @param oldChip
     * @param breakPoints
     * @return
     */
    public static void matchBreakPoints(ChipFeature chipFeatures, ChipPeaks chipPeaks) throws Exception {

        int index = 0;
        int indChrom = 0;
        IFeature current;
        List<? extends IFeature> listF = null;
        List<AberrationCNVCAT> listPeaks = null;
        // todo new Region as cbs values ??

        // double chip
        for (String strChrom : chipFeatures.chrFeatures.keySet()) {

            listF = chipFeatures.chrFeatures.get(strChrom);
            if (listF.size() <= 0) {
                continue;
            }
            Collections.sort(listF, IFeature.comChromStart);
            listPeaks = (List<AberrationCNVCAT>) chipPeaks.getData(strChrom);
            Collections.sort(listPeaks, Aberration.compByStart);

            int start = 0;

            for (AberrationCNVCAT peak : (List<AberrationCNVCAT>) listPeaks) {
                for (IFeature f : listF.subList(start, listF.size())) {
                    if (f.getChromStart() >= peak.getChromStart() && f.getChromEnd() <= peak.getChromEnd()) {
                        start = listF.indexOf(f);
                        f.setIfAberrant(peak.getIfAberrant());
                    } else {
                        f.setIfAberrant(0);
                    }
                }

            }

        }
    }

    public Double getMAD() {

        Data sample = null;
        List<? extends Data> list = new Vector<Data>();
        if (this.parentChip.getDataEntity().getDataType().contentEquals(Defaults.DataType.SEGMENTS.toString())) {
            sample = this.parentChip.getDataEntity();
            this.isCBS = true;
        } else {
            if (this.parentChip.getDataEntity() instanceof ExperimentData) {
                list = ((ExperimentData) this.parentChip.getDataEntity()).getTrackList();
            }
            for (Data s : list) {
                if (s.getDataType().contentEquals(Defaults.DataType.SEGMENTS.toString())
                        && ((Track) s).getProcProcessing() != null
                        && ((Track) s).getProcProcessing().contentEquals(CBSWorker.methodName)) {
                    sample = s;
                    break;
                }
            }
        }
        if (sample != null) {

            String[] paramList = ((Track) sample).getParamProcessing().split("\n");
            for (String p : paramList) {
                String[] param = p.split(":");
                if (param.length > 1 && param[0].indexOf("MAD") >= 0) {
                    publish("get Quality factor as MAD_CBS from " + sample.getName());
                    this.madSource = "get Quality factor as MAD_CBS from " + sample.getName();
                    return Double.parseDouble(param[1]);
                }
            }
        }
        publish("get Quality factor as MAD from " + parentChip.getDataEntity().getName());
        this.madSource = "get Quality factor as MAD from " + parentChip.getDataEntity().getName();
        return parentChip.getMAD();
    }
}
