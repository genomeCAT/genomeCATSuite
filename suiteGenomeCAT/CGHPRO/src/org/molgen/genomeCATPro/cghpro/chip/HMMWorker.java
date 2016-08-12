package org.molgen.genomeCATPro.cghpro.chip;

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
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.data.IFeature;

/**
 * @name HMMWorker.java
 *
 * calls R Package aCGH
 * http://www.bioconductor.org/packages/devel/bioc/html/aCGH.html
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
/**
 * 310712 kt run install package & execution in batch mode 030812 kt exportData
 * set DecimalFormatSymbol explizit
 *
 */
public class HMMWorker extends SwingWorker<ChipFeature, String> {

    ChipFeature oldChip = null;
    public final static String methodName = "aCGH HMM";
    //String rDir = new String("tmp");
    String dataFilename = "";
    String rFilename = "";
    String pFilename = "";
    String resultFilename = "";
    private final Informable informable;
    String mainDir = "";
    String newName = "";

    public HMMWorker(ChipFeature c, String name, Informable inf) {
        this.oldChip = c;
        this.informable = inf;
        this.mainDir = System.getProperty("user.dir");
        this.newName = name;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {
            informable.messageChanged(message);
        }
    }

    /**
     * do real processing
     *
     * @return
     * @throws java.lang.Exception
     */
    protected ChipFeature doInBackground() throws Exception {
        publish("run " + HMMWorker.methodName + " in Background...");
        setProgress(0);
        ChipFeature newChip = new ChipFeature(false);

        Logger.getLogger(HMMWorker.class.getName()).log(Level.INFO, "Main: "
                + mainDir);
        if (oldChip == null || oldChip.getError()) {
            Logger.getLogger(HMMWorker.class.getName()).log(Level.SEVERE,
                    " chip has error");
            return newChip;
        }
        this.dataFilename = mainDir + File.separator + File.separator
                + oldChip.getDataEntity().getTableData() + "_hmm.dat";
        dataFilename = dataFilename.replace("\\", "\\\\");

        Logger.getLogger(HMMWorker.class.getName()).log(Level.INFO,
                "datafile for R: " + dataFilename);

        File dataFile = new File(dataFilename);
        int no = exportData(oldChip, dataFile);

        if (no < 0) {
            Logger.getLogger(HMMWorker.class.getName()).log(
                    Level.SEVERE,
                    "There is error during exporting data!");
            publish("There is error during exporting data!");
            return newChip;
        }
        setProgress(10);
        boolean success = false;
        success = this.runHMM(oldChip, 10);
        setProgress(80);
        if (!success) {
            Logger.getLogger(HMMWorker.class.getName()).log(
                    Level.SEVERE,
                    "There is error during processing the data!");
            publish("There is error during processing the data!");
            newChip.error = true;
            return newChip;
        }
        try {
            newChip = this.importHMMStates(newName);
        } catch (Exception ex) {
            publish("There is error creating aCGH HMM chip!");
            Logger.getLogger(HMMWorker.class.getName()).log(Level.SEVERE,
                    "There is error creating aCGH HMM chip!", ex);
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

        Utils.deleteFile(rFilename);
        Utils.deleteFile(resultFilename);
        Utils.deleteFile(dataFilename);

        System.gc();
        if (!success) {
            publish("Error saving data see logfile for details");
        } else {
            publish("Congratulations! " + HMMWorker.methodName + "  succesfully finished");
        }
        return newChip;

    }

    /**
     * export data into file to serve R package aCGH
     *
     * @param chip - chip containing data entity
     * @param outFile - output file
     * @return number of exported data, -1 if exception occurs
     */
    public int exportData(ChipFeature chip, File outFile) {

        try {
            int i = 0;
            int chrom = 0;

            FileWriter out = new FileWriter(outFile);
            out.write("Clone\tTarget\tChrom\tstart\tend\tlog2Ratio\n");
            DecimalFormatSymbols NS = new DecimalFormatSymbols();
            NS.setDecimalSeparator('.');
            DecimalFormat N = new DecimalFormat("0.#####", NS);

            for (Vector<? extends IFeature> bacs : chip.chrFeatures.values()) {
                Collections.sort((Vector<IFeature>) bacs, IFeature.comChromStart);
                for (IFeature currentF : bacs) {

                    if (currentF.getChrom().contains("andom") || currentF.getChrom().contains("chrM")) {
                        continue;
                    }

                    chrom = RegionLib.fromChrToInt(currentF.getChrom());
                    if (chrom <= 0) {
                        continue;
                    }

                    //290512 kt
                    out.write("" + ++i + "\t"
                            + ((currentF.getId() == null || currentF.getId().contentEquals("")) ? i : currentF.getId())
                            + "\t" + i
                            + "\t" + chrom + "\t"
                            + currentF.getChromStart() + "\t"
                            + currentF.getChromEnd() + "\t"
                            + N.format(currentF.getRatio()) + "\n");

                }
            }
            out.close();
            return i;
        } catch (IOException exception) {
            publish(exception.getMessage());
            Logger.getLogger(HMMWorker.class.getName()).log(Level.SEVERE, "ERROR: ", exception);
            return - 1;
        }

    }

    /**
     * import R package aCGH results to a new Data entity als child of original
     * Data create new Chip object
     *
     * @param name - name for new chip
     *
     * @return new Chip object containing the data
     * @throws java.lang.Exception
     */
    ChipFeature importHMMStates(String _name) throws Exception {

        File resultFile = new File(resultFilename);
        BufferedReader input = new BufferedReader(new FileReader(resultFile));
        String line;
        boolean header = false;
        String proc = "";

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
                "HMM_" + _name,
                oldChip.getDataEntity().getGenomeRelease(),
                Defaults.DataType.SEGMENTS.toString());
        //d.copy(oldChip.getDataEntity());
        d.setId(null);
        d.setGenomeRelease(oldChip.getDataEntity().getGenomeRelease());

        d.setClazz(FeatureHMM.class.getName());
        d.setParent(oldChip.getDataEntity());
        d.setOwner(ExperimentService.getUser());
        d.setProcProcessing(HMMWorker.methodName);
        d.setParamProcessing("\n" + param.trim());
        ChipFeature newChip = new ChipFeature(d);

        Hashtable<String, Vector<? extends IFeature>> data = null;
        try {
            data = FeatureHMM.loadFromHMMFile(resultFilename);
        } catch (Exception e) {
            publish("There is error during reimport, checklogfile! " + e.getMessage());
            Logger.getLogger(HMMWorker.class.getName()).log(
                    Level.SEVERE, "There is error during reimport, check logfile!",
                    e);
            throw e;
        }

        newChip.chrFeatures = data;
        return newChip;

    }

    /**
     * merge features with same predicted state value into segments with R
     * package aCGH used as described in "A comparison study: applying
     * segmentation to array CGH data for downstream analysesA comparison study:
     * applying segmentation to array CGH data for downstream analyses"
     *
     *
     * @param chip - chip containing the data entity
     * @param iProgress - counter to visualize progress
     *
     * @return true if succeeded, false if errors occur
     */
    public boolean runHMM(ChipFeature chip, int iProgress) {

        rFilename = mainDir + File.separator + File.separator
                + chip.getDataEntity().getTableData() + "_hmm.R";
        pFilename = mainDir + File.separator + File.separator
                + chip.getDataEntity().getTableData() + "_p_hmm.R";
        rFilename = rFilename.replace("\\", "\\\\");
        pFilename = pFilename.replace("\\", "\\\\");
        Logger.getLogger(HMMWorker.class.getName()).log(Level.INFO, "executable R: " + rFilename);
        File rFile = new File(rFilename);
        //File pFile = new File(pFilename);
        resultFilename = mainDir + File.separator + File.separator + chip.getDataEntity().getTableData() + "_hmm.result";
        resultFilename = resultFilename.replace("\\", "\\\\");
        Logger.getLogger(HMMWorker.class.getName()).log(Level.INFO, "result R: " + resultFilename);

        //File resultFile = new File(resultFilename);
        //File sdFile = new File(sdFilename);
        try {

            //FileWriter outp = new FileWriter(pFile);
            //install required packages from bioconductor 
            /*outp.write(
            "if(!length(grep(\"aCGH\", installed.packages()[,1])) > 0) {source(\"http://bioconductor.org/biocLite.R\");\noptions(device.ask.default = FALSE);\n" +
            " biocLite(\'aCGH\',dependencies=TRUE)\n;};\n");
            outp.close();
             */
            FileWriter out = new FileWriter(rFile);
            out.write(
                   "" // "options(echo=FALSE)\n"
                    + "if(!length(grep(\"aCGH\", installed.packages()[,1])) > 0) {source(\"http://bioconductor.org/biocLite.R\");\noptions(device.ask.default = FALSE);\n"
                    + " biocLite(\'aCGH\',dependencies=TRUE)\n;};\n"
                    + "library(\'aCGH\', logical.return = TRUE)\n"
                    + "ddata<-read.table(file=\"" + dataFilename.replace(" ", "\\ ") + "\", na.strings=\"\\\\N\", header=TRUE, sep=\"\t\")\n"
                    + "data <- na.omit(ddata)\n"
                    + "log2.ratios<-data.frame(test=data$log2Ratio)\n"
                    + "clone.info<-data.frame(Clone=data$Clone, Target=data$Target,Chrom=data$Chrom,kb=round(data$start/1000))\n"
                    + "ex.acgh<-create.aCGH(log2.ratios, clone.info)\n"
                    + "hmm(ex.acgh)<-find.hmm.states(ex.acgh, aic=TRUE, bic=FALSE)\n"
                    + "y<-mergeLevels(ex.acgh$hmm$states.hmm[[1]][,8],ex.acgh$hmm$states.hmm[[1]][,6])\n"
                    + "mad <- median(y$mnNow)\n"
                    + "i <- sessionInfo()\n"
                    + "write(paste(\"#Package:\",i$otherPkgs$aCGH$Package),  file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n"
                    + "write(paste(\"#Version:\", i$otherPkgs$aCGH$Version), append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n"
                    + "write(paste(\"#Reference:\", gsub(\"\\n\", \"\", i$otherPkgs$aCGH$Reference)), append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n"
                    + "write(paste(\"#MAD:\", mad),  append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\")\n"
                    + "out <- data.frame(data, ex.acgh$hmm$states.hmm[[1]])\n"
                    + "out <- data.frame(out, y$vecMerged)\n"
                    + "write.table(out, file=\""
                    + resultFilename.replace(" ", "\\ ") + "\""
                    + ", row.names=FALSE, col.names=FALSE, append=TRUE, sep=\"\t\")\n ");


            /*
             * Each of the sublists contains 2+ 6*n columns where the first two columns contain
            chromosome and kb positions for each clone in the dataset supplied followed
            up by 6 columns for each sample where n = number of samples.
            0   id
            1   chrom
            2   kb
            3   column 1 = state
            4   column 2 = smoothed value for a clone
            5   column 3 = probability of being in a state
            6   column 4 = predicted value of a state
            7   column 5 = dispersion
            8   column 6 = observed value*/
            out.close();
        } catch (IOException exception) {
            publish(exception.getMessage());
            Logger.getLogger(HMMWorker.class.getName()).log(
                    Level.SEVERE, "There is error during exporting command!");
            return false;
        }

        try {
            String[] command;
            String line;
            int extVal = 0;
            BufferedReader input;
            /*publish("install R aCGH HMM Module...");
            Logger.getLogger(HMMWorker.class.getName()).log(
            Level.INFO, "install R aCGH HMM Module...");
            Runtime.getRuntime();
            // first install packages
            command = Utils.getRCMD(pFilename.replace(" ", "\\ "));
            
            Logger.getLogger(HMMWorker.class.getName()).log(Level.INFO, new Vector<String>(Arrays.asList(command)).toString());
            Process pp = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pp.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(pp.getErrorStream()));
            
            
            
            while ((line = input.readLine()) != null) {
            publish(line);
            
            }
            publish("install R aCGH HMM Module end input...");
            input.close();
            while ((line = error.readLine()) != null) {
            publish("ERROR: " + line);
            }
            error.close();
            
            
            try {
            extVal = pp.waitFor();
            } catch (java.lang.InterruptedException e) {
            publish("ERROR: " + e.getMessage());
            Logger.getLogger(HMMWorker.class.getName()).log(
            Level.SEVERE, "Error: ", e);
            //throw (new RuntimeException(e));
            return false;
            }
            pp.destroy();
            if (extVal != 0) {
            
            Logger.getLogger(HMMWorker.class.getName()).log(
            Level.SEVERE, "install R aCGH HMM failed: exit " + pp.exitValue());
            publish("install R aCGH HMM failed: exit " + pp.exitValue());
            return false;
            }
            //???
            
            pp = null;
            input = null;
            error = null;
            System.gc();
            Runtime.getRuntime().gc();
            Runtime.getRuntime().freeMemory();
             */
            publish("call R aCGH HMM Module...");
            Logger.getLogger(HMMWorker.class.getName()).log(
                    Level.INFO, "call R aCGH HMM Module...");
            command = Utils.getRCMD(rFilename.replace(" ", "\\ "));
            //command += r;

            Logger.getLogger(HMMWorker.class.getName()).log(Level.INFO, new Vector<String>(Arrays.asList(command)).toString());
            Process p = Runtime.getRuntime().exec(command);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = input.readLine()) != null) {
                publish(line);

            }
            publish("call R aCGH HMM Module end input...");
            input.close();
            /* while ((line = error.readLine()) != null) {
            publish("R: " + line);
            }
            error.close();
             */

            try {
                extVal = p.waitFor();
            } catch (java.lang.InterruptedException e) {
                publish("ERROR: " + e.getMessage());
                Logger.getLogger(HMMWorker.class.getName()).log(
                        Level.SEVERE, "Error: ", e);
                //throw (new RuntimeException(e));
                return false;
            }

            if (extVal != 0) {
                BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = err.readLine()) != null) {
                    publish(line);
                    Logger.getLogger(HMMWorker.class.getName()).log(
                            Level.INFO, line);
                }
                err.close();
                Logger.getLogger(HMMWorker.class.getName()).log(
                        Level.SEVERE, "start R aCGH HMM failed: exit " + p.exitValue());
                publish("start R aCGH HMM failed: exit " + p.exitValue());
                return false;
            }
        } catch (Exception exception) {
            //publish("There is error during calculation, check logfile! " + exception.getMessage());
            Logger.getLogger(HMMWorker.class.getName()).log(
                    Level.SEVERE, "",
                    exception);
            return false;

        }

        return true;
    }
}
