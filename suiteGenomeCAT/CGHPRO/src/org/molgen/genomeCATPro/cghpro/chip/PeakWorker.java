package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name PeakWorker.java
 * 
 * calls R Package RINGO
 * 
 * http://www.bioconductor.org/packages/release/bioc/html/Ringo.html
 * 
 * 
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @author Wei Chen
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
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.common.Informable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.data.DataService;
import org.molgen.genomeCATPro.data.FeatureWithSpot;
import org.molgen.genomeCATPro.data.OriginalSpot;
import org.molgen.genomeCATPro.data.Spot;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/**
 * 050613   kt      exception spot clazz
 * 310712   kt      run install package & execution in batch mode
 * 030812   kt      exportData set DecimalFormatSymbol explizit
 * 100812   kt      no Grouping DecimalFormat
 */
public class PeakWorker extends SwingWorker<ChipFeature, String> {

    ChipFeature oldChip = null;
    public final static String methodName = "RINGO find cher";
    //String rDir = new String("tmp");
    String dataFilename = "";
    String rFilename = "";
    String resultFilename = "";
    private final Informable informable;
    String mainDir = "";
    String newName = "";
    double threshold = 0.0;
    int probeDist = 0;
    int probeCount = 0;
    boolean deleteFiles = false;
    private String histFilename = "";
    boolean normalize = false;
    boolean smooth = true;

    public PeakWorker(ChipFeature c, String name,
            boolean normalize, boolean smooth,
            double threshold, int probeDist, int probeCount, boolean deleteFiles,
            Informable inf) {
        this.oldChip = c;
        this.informable = inf;
        this.mainDir = System.getProperty("user.dir");
        this.newName = name;
        this.normalize = normalize;
        this.smooth = smooth;
        this.threshold = threshold;
        this.probeCount = probeCount;
        this.deleteFiles = deleteFiles;
        this.probeDist = probeDist;

    }

    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {
            informable.messageChanged(message);
        }
    }

    /**
     * do real processing
     * @return
     * @throws java.lang.Exception
     */
    protected ChipFeature doInBackground() throws Exception {
        publish("run " + PeakWorker.methodName + " in Background...");
        setProgress(0);
        ChipFeature newChip = new ChipFeature(false);


        Logger.getLogger(PeakWorker.class.getName()).log(Level.INFO, "Main: " +
                mainDir);
        if (oldChip == null || oldChip.getError()) {
            Logger.getLogger(PeakWorker.class.getName()).log(Level.SEVERE,
                    " chip has error");
            return newChip;
        }
        this.dataFilename = mainDir + File.separator + File.separator +
                oldChip.getDataEntity().getTableData() + "_RINGO.dat";
        dataFilename = dataFilename.replace("\\", "\\\\");

        Logger.getLogger(PeakWorker.class.getName()).log(Level.INFO,
                "datafile for R: " + dataFilename);


        File dataFile = new File(dataFilename);
        int no = exportData(oldChip, dataFile);


        if (no < 0) {
            Logger.getLogger(PeakWorker.class.getName()).log(
                    Level.SEVERE,
                    "There is error during exporting data!");
            publish("There is error during exporting data!");
            return newChip;
        }
        setProgress(10);
        boolean success = false;
        success = this.runRINGO(oldChip, 10, normalize, smooth, this.threshold, this.probeCount, this.probeDist);
        setProgress(80);
        if (!success) {
            newChip.setError(true);
            Logger.getLogger(PeakWorker.class.getName()).log(
                    Level.SEVERE,
                    "There is error during processing the data!");
            publish("There is error during processing the data!");
            newChip.error = true;
            return newChip;
        }
        try {
            newChip = this.importChers(newName);
        } catch (Exception ex) {
            publish("There is error creating " + PeakWorker.methodName + " chip!");
            Logger.getLogger(PeakWorker.class.getName()).log(Level.SEVERE,
                    "There is error creating " + PeakWorker.methodName + "  chip!", ex);
            newChip.error = true;
            return newChip;
        }
        setProgress(90);

        publish("Save Data...");

        try {
            newChip.saveChipToDB();
            success = true;
        } catch (Exception e) {
        }
        setProgress(100);
        if (deleteFiles) {
            Utils.deleteFile(rFilename);
            Utils.deleteFile(resultFilename);
            Utils.deleteFile(dataFilename);
            Utils.deleteFile(histFilename);
        }

        System.gc();
        if (!success) {
            publish("Error saving data see logfile for details");
        } else {
            publish("Congratulations! " + PeakWorker.methodName + "  succesfully finished");
        }
        return newChip;


    }

    /**
     * export data into file to serve R package aCGH
     * @param chip - chip containing data entity
     * @param outFile - output file 
     * @return number of exported data, -1 if exception occurs
     */
    public int exportData(ChipFeature chip, File outFile) {


        try {
            int i = 0;
            int chrom = 0;
            // supports data entities with red and green channel
            Spot iSpot = null;
            try {
                //050613    kt
                iSpot = DataManager.getSpotClazz(chip.getDataEntity().getClazz());
            } catch (Exception e) {
                Logger.getLogger(DataManager.class.getName()).log(Level.WARNING, null, e);
            }
            if (iSpot == null) {
                publish("exportData: invalid clazz " + chip.getDataEntity().getClazz());
                Logger.getLogger(PeakWorker.class.getName()).log(Level.SEVERE, "ERROR: " + " exportData: invalid clazz " + chip.getDataEntity().getClazz());
                return - 1;

            }
            /*if(! (chip instanceof ChipSpot)){
            chip = (ChipSpot) ChipImpl.loadChipFromDB(ChipSpot.class, chip.getDataEntity());
             */


            FileWriter out = new FileWriter(outFile);
            //rProcessedSignal gProcessedSignal GeneName ProbeName SystematicName
            out.write("RSignal\tGSignal\t\tProbeName\tSystematicName\tGeneName\n");

            DecimalFormatSymbols NS = new DecimalFormatSymbols();
            NS.setDecimalSeparator('.');
            DecimalFormat N = new DecimalFormat("0.#####", NS);

            String gene = "";

            for (Vector<? extends Feature> features : chip.chrFeatures.values()) {

                Collections.sort((Vector<Feature>) features, Feature.comChromStart);
                for (Feature currentF : features) {
                    if (!(currentF instanceof FeatureWithSpot)) {
                        publish("exportData: invalid feature type -without spots");
                        Logger.getLogger(PeakWorker.class.getName()).log(Level.SEVERE, "ERROR: " + " exportData: invalid feature type without spots ");
                        return - 1;
                    }
                    if (currentF.getChrom().contains("andom") || currentF.getChrom().contains("chrM")) {
                        continue;
                    }

                    chrom = RegionLib.fromChrToInt(currentF.getChrom());
                    if (chrom <= 0) {
                        continue;
                    }
                    for (Spot s : ((FeatureWithSpot) currentF).spots) {

                        if (!(s instanceof OriginalSpot)) {
                            publish("exportData: invalid feature type -without spots");
                            Logger.getLogger(PeakWorker.class.getName()).log(Level.SEVERE, "ERROR: " + " exportData: invalid feature type without spots ");
                            return - 1;
                        }
                        if (DataService.hasValue(s, "getAnnoValue")) {
                            gene = DataService.getValue(s, "getAnnoValue").toString();
                        } else {
                            gene = ((OriginalSpot) s).getId();
                        }
                        out.write(
                                ++i + "\t" +
                                N.format(((OriginalSpot) s).getCy5Value()) + "\t" +
                                N.format(((OriginalSpot) s).getCy3Value()) + "\t" + "\t" +
                                ((s.getName() == null || s.getName().contentEquals("")) ? ++i : s.getName()) + "\t" +
                                s.getChrom() + ":" + s.getChromStart() + "-" + s.getChromEnd() + "\t" +
                                gene +
                                "\n");

                    }
                }
            }
            out.close();
            return i;
        } catch (IOException exception) {
            publish(exception.getMessage());
            Logger.getLogger(PeakWorker.class.getName()).log(Level.SEVERE, "ERROR: ", exception);
            return - 1;
        }

    }

    /**
     * import R package RINGO chers to a new Data entity als child of original Data
     * create new Chip object
     * @param name - name for new chip
     * 
     * @return new Chip object containing the data
     * @throws java.lang.Exception
     */
    ChipFeature importChers(String _name) throws Exception {

        File resultFile = new File(resultFilename);
        BufferedReader input = new BufferedReader(new FileReader(resultFile));
        String line;
        boolean header = false;
        // String proc = "";
        String param = "";

        while ((line = input.readLine()) != null) {
            if (line.startsWith("#")) {
                header = true;
                param += "\n" + line;

                continue;

            }
            if (header = true) {
                break; // header gelesen

            }
        }


        Track d = new Track(
                "RINGO_" + _name,
                oldChip.getDataEntity().getGenomeRelease(),
                Defaults.DataType.SEGMENTS.toString());
        //d.copy(oldChip.getDataEntity());
        d.setId(null);
        d.setGenomeRelease(oldChip.getDataEntity().getGenomeRelease());

        d.setClazz(FeaturePeak.class.getName());
        d.setParent(oldChip.getDataEntity());
        d.setOwner(ExperimentService.getUser());
        d.setProcProcessing(PeakWorker.methodName);
        d.setParamProcessing("\n" + param.trim());
        ChipFeature newChip = new ChipFeature(d);

        Hashtable<String, Vector<? extends Feature>> data = null;
        try {
            data = FeaturePeak.loadFromRINGOFile(resultFilename);
        } catch (Exception e) {
            publish("There is error during reimport, checklogfile! " + e.getMessage());
            Logger.getLogger(PeakWorker.class.getName()).log(
                    Level.SEVERE, "There is error during reimport, check logfile!",
                    e);
            throw e;
        }

        newChip.chrFeatures = data;
        return newChip;

    }

    /**
     * merge features with same predicted state value into segments
     * with R package RINGO
     * used as described in 
     * 
     * 
     * /project/Kopenhagen/Katrin/GenomeCATPro/toRead/Peaks/Ringo_test.txt
     * @param chip - chip containing the data entity
     * @param iProgress  - counter to visualize progress
     * 
     *  @return true if succeeded, false if errors occur
     */
    public boolean runRINGO(ChipFeature chip, int iProgress,
            boolean normalize, boolean smooth, double threshold, int nProbes, int probeDist) {

        rFilename = mainDir + File.separator + File.separator +
                chip.getDataEntity().getTableData() + "_RINGO.R";
        rFilename = rFilename.replace("\\", "\\\\");
        Logger.getLogger(PeakWorker.class.getName()).log(Level.INFO, "executable R: " + rFilename);
        File rFile = new File(rFilename);

        resultFilename = mainDir + File.separator + File.separator + chip.getDataEntity().getTableData() + "_RINGO.result";
        resultFilename = resultFilename.replace("\\", "\\\\");
        Logger.getLogger(PeakWorker.class.getName()).log(Level.INFO, "result R: " + resultFilename);

        histFilename = mainDir + File.separator + File.separator + chip.getDataEntity().getTableData() + "_RINGO.png";
        histFilename = histFilename.replace("\\", "\\\\");
        // PARAM: dist Spots, probeAnzahl
        try {

            boolean doubledyeswap = false;
            if (((ExperimentData) chip.getDataEntity()).getParamProcessing() != null) {
                doubledyeswap = ((ExperimentData) chip.getDataEntity()).getParamProcessing().contains(Defaults.DYESWAP);
            }
            FileWriter out = new FileWriter(rFile);
            out.write(
                    "if(!length(grep(\"Ringo\", installed.packages()[,1])) > 0) {source(\"http://bioconductor.org/biocLite.R\")\n biocLite(\"Ringo\",dependencies=TRUE)\n}\n" +
                    "library(\'Ringo\')\n" +
                    "RG3 <-read.maimages(\'" + dataFilename.replace(" ", "\\ ") + "\', " +
                    // genomecat uses normally R/G -> that means dyeswap here - 
                    (doubledyeswap ? " columns=list(G=\"GSignal\",R=\"RSignal\") " : " columns=list(R=\"GSignal\",G=\"RSignal\") ") +
                    " , source=\"generic\", " +
                    "annotation=c(\"ProbeName\",\"SystematicName\", \"GeneName\"))\n" +
                    "targets <- data.frame(FileName=I(c(\"" + chip.getDataEntity().getTableData() + "_RINGO.R" +
                    "\")),Cy3=I(c(\"" + "sample" + "\")),Cy5=I(c(\"" + "control" + "\")))\n" +
                    "row.names(targets) <- c(\"" + chip.getDataEntity().getName() + "\")\n" +
                    "RG3$targets <- targets\n" +
                    "X <- preprocess(RG3, method=" + (normalize ? "\"nimblegen\"" : "\"none\"") + ", idColumn=\"ProbeName\")\n" +
                    "pA <- extractProbeAnno(RG3, \"agilent\", genome=\"human\", microarray=\"Agilent Tiling \")\n" +
                    (probeDist != 0 ? "d <- " + probeDist + "\n" : " d <- signif(median(diff(pA[\"1.start\"])), d=1)\n") +
                    "dist <- d*" + nProbes + "/2; d\n" +
                    "nprobes <- " + nProbes + ";nprobes\n" +
                    (!smooth ? "smoothX <- X\n" : "smoothX <- computeRunningMedians(X, modColumn=\"Cy3\", winHalfSize=d, " +
                    "min.probes=nprobes, probeAnno=pA)\n") +
                    (!Double.isNaN(threshold) ? " y0G <- " + threshold + "\n" : "y0G <- twoGaussiansNull(exprs(smoothX));y0G\n" +
                    "y0 <-  upperBoundNull(exprs(smoothX));y0\n" +
                    "y0G <- min(y0, y0G);y0G\n") +
                    "png(\"" + histFilename.replace(" ", "\\ ") + "\")\n" +
                    "hist(exprs(smoothX), n=100, main=NA, xlab=\"Smoothed expression level [log2]\")\n " +
                    "abline(v=y0G, col=\"blue\")\n" +
                    "legend(x=\"topright\", lwd=1, col=c(\"blue\"),legend=c(\"Threshold\"))\n" +
                    "dev.off()\n" +
                    "chersX <- findChersOnSmoothed(smoothX,  distCutOff=4*dist,minProbesInRow=nprobes, probeAnno=pA, threshold=y0G)\n" +
                    "chersXD <- as.data.frame(chersX)\n" +
                    "chersXD[,2] <- paste(\"chr\",chersXD[,2], sep=\"\")\n" +
                    "i <- sessionInfo()\n" +
                    "write(paste(\"#Package:\", i$otherPkgs$Ringo$Package), file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(paste(\"#Version:\", i$otherPkgs$Ringo$Version), append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(paste(\"#Reference:\", gsub(\"\\n\", \"\", i$otherPkgs$Ringo$Reference)), append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(\"#normalize: " + (normalize ? "nimblegen\"" : "none\"") + ",append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(paste(\"#Distance:\",  d),append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(paste(\"#nofProbes:\",  nprobes),append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(\"#smooth: " + (smooth ? "RunningMedian\"" : "none\"") + ",append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    (smooth ? "write(paste(\"#winhalfsize:\",  dist),append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" : "") +
                    (smooth ? "write(paste(\"#min.probes:\",  nprobes),append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" : "") +
                    "write(paste(\"#threshold:\",  y0G),append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(paste(\"#minProbesInRow:\",  nprobes),append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write(paste(\"#distCutOff:\",  4*dist),append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n" +
                    "write.table(chersXD, file=\"" + resultFilename.replace(" ", "\\ ") + "\", " +
                    " quote = FALSE, append=TRUE, sep=\"\\t\", row.names=FALSE, col.names=FALSE)\n");


            out.close();
        } catch (IOException exception) {
            publish(exception.getMessage());
            Logger.getLogger(PeakWorker.class.getName()).log(
                    Level.SEVERE, "There is error during exporting command!");
            return false;
        }

        try {

            Runtime.getRuntime();



            String[] command = Utils.getRCMD(rFilename.replace(" ", "\\ "));
            //command += r;
            String line;
            Logger.getLogger(PeakWorker.class.getName()).log(Level.INFO, new Vector<String>(Arrays.asList(command)).toString());
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            publish("call R " + PeakWorker.methodName + " Module...");
            Logger.getLogger(PeakWorker.class.getName()).log(
                    Level.INFO, "call R " + PeakWorker.methodName + "  Module...");
            int chr = 1;
            while ((line = input.readLine()) != null) {
                publish(line);
                if (line.contains("current chromosome")) {
                    this.setProgress((int) iProgress + (70 / 24 * chr++));
                }
            }
            input.close();
            try {
                p.waitFor();
            } catch (java.lang.InterruptedException e) {
                publish(e.getMessage());
                Logger.getLogger(PeakWorker.class.getName()).log(
                        Level.SEVERE, "Error: ", e);
                //throw (new RuntimeException(e));
                return false;
            }

            if (p.exitValue() != 0) {
                BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = err.readLine()) != null) {
                    publish(line);
                    Logger.getLogger(PeakWorker.class.getName()).log(
                            Level.INFO, line);
                }
                err.close();
                Logger.getLogger(PeakWorker.class.getName()).log(
                        Level.SEVERE, "start R " + PeakWorker.methodName + " failed: exit " + p.exitValue());
                publish("start R " + PeakWorker.methodName + " failed: exit " + p.exitValue());
                return false;
            }
        } catch (Exception exception) {
            //publish("There is error during calculation, check logfile! " + exception.getMessage());
            Logger.getLogger(PeakWorker.class.getName()).log(
                    Level.SEVERE, "",
                    exception);
            return false;

        }

        return true;
    }
}
