package org.molgen.genomeCATPro.ngs;

/**
 * @name BAMWorkerImpl.java
 * 
 * 
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
import java.beans.PropertyChangeListener;
import org.molgen.genomeCATPro.common.Informable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

import org.molgen.genomeCATPro.common.Utils;

/**
 * 
 * 260713    kt     RPKM peak sum/reads_total_per_million
 */

// gui: set parameter to import modul
// gui: init informable to get published messages
// gui: call import modul "run"
// gui: add  property change listenr to import modul to get progress and end of thread
// 
// import modul: create worker with informable
// import modul: add listener to worker 
// import modul: call worker execute 
// 
// 
// worker.runInBackground  -> create new thread , publish messages & process
// worker.done: process end
// 
// import modul: get success state from worker as return value
//
// gui: get process event SwingWorker.StateValue.DONE to carry on
// gui: evaluate success state from import modul
public class BAMImport implements XPortNGS {

    public final static String methodName = "NGS_BAM";
    String name = "";
    String dataPath = "";
    boolean dataSorted = false;
    String controlPath = "";
    boolean controlSorted = false;
    String rFilename = "";
    String resultPeakFilename = "";
    boolean hasPeakFile = false;
    String resultPeakControlFilename = "";
    boolean hasPeakControlFile = false;
    String resultPeakVsControlFilename = "";
    boolean hasPeakVsControlFile = false;
    String resultBinFilename = "";
    boolean hasBinFile = false;
    String resultBinControlFilename = "";
    boolean hasBinControlFile = false;
    String mainDir = "";
    // use control for peak finding
    boolean hasControl = false;
    // shift reads acc to half of estimated fragment length
    boolean shift = false;
    // resize reads to half of estimated fragment lenght
    boolean resize = false;
    // calculate peaks; otherwise just read in binned read counts
    //normalize with control 
    //ref:  Normalization, bias correction, and peak calling for ChIP-seq
    boolean normalizeWithControl = false;
    // normalize with gc based loess 
    // ref:
    boolean normalizeWithGCLoess = false;
    // calculate peaks with threshold from poisson distribution
    // ref: chipseq.peakCutoff
    boolean calcPeaksPoisson = false;
    // calculate peaks with quantile based threshold
    // ref:
    boolean calcPeaksQuantile = false;
    // calculate peaks which differ from control
    // ref: (chipseq)
    private int binsize = 1000;
    private boolean success = false;

    /**
     * create new bam import module
     */
    public BAMImport() {
        this.mainDir = System.getProperty("user.dir");
    }

    /**
     * initialize bam import module
     */
    public void initImport() {
        this.binsize = 1000;
        this.calcPeaksPoisson = false;
        this.calcPeaksQuantile = false;
        this.normalizeWithControl = false;
        this.normalizeWithGCLoess = false;
        this.success = false;
        this.resize = false;
        this.shift = false;
        this.hasControl = false;
        this.hasBinControlFile = false;
        this.hasBinFile = false;
        this.hasPeakControlFile = false;
        this.hasPeakFile = false;
        this.hasPeakVsControlFile = false;
        this.controlSorted = false;
        this.dataSorted = false;


    }

    /**
     * run import, create new thread via swingworker,
     * bridge status messages from worker to gui
     * @param inf
     */
    public void doRunImport(Informable inf, PropertyChangeListener listener) {


        BAMWorkerImpl w = new BAMWorkerImpl(inf);

        // worker add Listener
        w.addPropertyChangeListener(listener);
        w.execute();
    }

    String getControlAsBin() {
        return this.resultBinControlFilename;
    }

    String getControlAsPeak() {
        return this.resultPeakControlFilename;
    }

    String getDataAsBin() {
        return this.resultBinFilename;
    }

    String getDataAsPeak() {

        return this.resultPeakFilename;
    }

    public void setResultBinControlFilename(String resultBinControlFilename) {
        this.resultBinControlFilename = resultBinControlFilename;
    }

    public void setResultBinFilename(String resultBinFilename) {
        this.resultBinFilename = resultBinFilename;
    }

    public void setResultPeakControlFilename(String resultPeakControlFilename) {
        this.resultPeakControlFilename = resultPeakControlFilename;
    }

    public void setResultPeakFilename(String resultPeakFilename) {
        this.resultPeakFilename = resultPeakFilename;
    }

    public void setResultPeakVsControlFilename(String resultPeakVsControlFilename) {
        this.resultPeakVsControlFilename = resultPeakVsControlFilename;
    }

    String getDataAsPeakVsControl() {
        return this.resultPeakVsControlFilename;
    }

    boolean getError() {
        return !this.success;
    }

    /**
     * thread worker class to process bam import
     * 
     * the return type of the doInBackground and get methods are specified as the first 
     * type of the SwingWorker, and the second type is the type used to return for the publish  
     * and process methods
     */
    private class BAMWorkerImpl extends SwingWorker<Boolean, String> {

        private final Informable inf;

        private BAMWorkerImpl(Informable informable) {
            this.inf = informable;
        }

        // This method is invoked when the worker is finished
        // its task
        @Override
        protected void done() {
            try {
                success = get();
            } catch (Exception e) {
                Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.WARNING, "do run NGS Worker ", e);

            }
        }

        @Override
        /**
         * run processing
         */
        protected Boolean doInBackground() throws Exception {

            publish("run " + BAMImport.methodName + " for " + name + " in Background...");
            setProgress(0);



            Logger.getLogger(BAMImport.class.getName()).log(Level.INFO, "Main: " +
                    mainDir);

            setProgress(10);

            boolean _success = this.runBAMProcessing(10);
            /*if (!success) {
            Logger.getLogger(BAMWorkerImpl.class.getName()).log(
            Level.SEVERE,
            BAMImport.methodName + ": there is error during processing the data!");
            publish(BAMImport.methodName + ": there is error during processing the data!");
            newChip.setError(true);
            return newChip;
            }
            try {
            if (BAMImport.this.calcPeaksPoisson || BAMImport.this.calcPeaksQuantile) {
            if (BAMImport.this.hasControl) {
            newChip = this.importData(BAMImport.this.resultPeakVsControlFilename, false);
            } else {
            newChip = this.importData(BAMImport.this.resultPeakFilename, false);
            }
            } else {
            newChip = this.importData(BAMImport.this.resultBinFilename, true);
            }
            setProgress(80);
            
            
            
            
            } catch (Exception ex) {
            publish(BAMImport.methodName + ": there is error importing  R files");
            Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.SEVERE,
            BAMImport.methodName + ": there is error importing R files", ex);
            newChip.setError(true);
            return newChip;
            }
            setProgress(90);
            
            
            
            try {
            newChip.saveChipToDB();
            
            
            success = true;
            } catch (Exception e) {
            }*/
            setProgress(100);



            System.gc();
            if (!_success) {
                publish(BAMImport.methodName + ": error saving data see logfile for details");
            } else {
                publish(BAMImport.methodName + ": Congratulations!   succesfully finished");
            }
            return _success;
        }

        /**
         * execute R Processing
         * @param iProgress
         * @return
         */
        //implizite Parameter
        // shift  -> fragmentlength
        // resize * - fragmentlength
        // hasControl *  -> Name
        // calc Poisson  - calc Quantile -> cutoff
        //
        boolean runBAMProcessing(int iProgress) {

            rFilename = mainDir + File.separator + File.separator +
                    name + "_" + getModulName() + ".R";

            rFilename = rFilename.replace("\\", "\\\\");

            Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.INFO, "executable R: " + rFilename);
            File rFile = new File(rFilename);
            //File pFile = new File(pFilename);
            resultPeakFilename = mainDir + File.separator + File.separator + name + "_peak_" + getModulName() + ".txt";
            resultPeakFilename = resultPeakFilename.replace("\\", "\\\\");
            Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.INFO, "result R: " + resultPeakFilename);
            Utils.deleteFile(resultPeakFilename);

            resultBinFilename = mainDir + File.separator + File.separator + name + "_bin_" + getModulName() + ".txt";
            resultBinFilename = resultBinFilename.replace("\\", "\\\\");
            Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.INFO, "result R: " + resultBinFilename);
            Utils.deleteFile(resultBinFilename);
            if (hasControl) {
                resultBinControlFilename = mainDir + File.separator + File.separator + name + "_bin_control_" + getModulName() + ".txt";
                resultBinControlFilename = resultBinControlFilename.replace("\\", "\\\\");
                Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.INFO, "result R: " + resultBinControlFilename);


                Utils.deleteFile(resultBinControlFilename);


                resultPeakControlFilename = mainDir + File.separator + File.separator + name + "_peak_control_" + getModulName() + ".txt";
                resultPeakControlFilename = resultPeakControlFilename.replace("\\", "\\\\");
                Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.INFO, "result R: " + resultPeakControlFilename);

                Utils.deleteFile(resultPeakControlFilename);

                resultPeakVsControlFilename = mainDir + File.separator + File.separator + name + "_peak_vs_control_" + getModulName() + ".txt";
                resultPeakVsControlFilename = resultPeakVsControlFilename.replace("\\", "\\\\");
                Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.INFO, "result R: " + resultPeakVsControlFilename);

                Utils.deleteFile(resultPeakVsControlFilename);

            } else {
                resultBinControlFilename = null;
                resultPeakControlFilename = null;
                resultPeakVsControlFilename = null;
            }

            //File resultFile = new File(resultFilename);
            //File sdFile = new File(sdFilename);
            try {



                FileWriter out = new FileWriter(rFile);
                // init libraries
                out.write(
                        //"options(echo=FALSE)\n" +
                        "if(!length(grep(\"Rsamtools\", installed.packages()[,1])) > 0) {source(\"http://bioconductor.org/biocLite.R\");\noptions(device.ask.default = FALSE);\n" +
                        " biocLite(\'Rsamtools\',dependencies=TRUE)\n;};\n" +
                        "library(\'Rsamtools\', logical.return = TRUE)\n" +
                        "if(!length(grep(\"chipseq\", installed.packages()[,1])) > 0) {source(\"http://bioconductor.org/biocLite.R\");\noptions(device.ask.default = FALSE);\n" +
                        " biocLite(\'chipseq\',dependencies=TRUE)\n;};\n" +
                        "library(\'chipseq\', logical.return = TRUE)\n" +
                        "if(!length(grep(\"rtracklayer\", installed.packages()[,1])) > 0) {source(\"http://bioconductor.org/biocLite.R\");\noptions(device.ask.default = FALSE);\n" +
                        " biocLite(\'rtracklayer\',dependencies=TRUE)\n;};\n" +
                        "library(\'rtracklayer\', logical.return = TRUE)\n");
                // read files   
                out.write("param <- ScanBamParam(flag=scanBamFlag(isPaired = FALSE,isDuplicate=FALSE),what=c(\'pos\', \'flag\'))\n");

                out.write("d <- readBamGappedAlignments(\'" + dataPath.replace(" ", "\\ ") + "\', index=\'" + dataPath.replace(" ", "\\ ") + "\', param=param); d\n" +
                        " rd <- unlist(grglist(d));\n");
                out.write("length_d <-length(d)/1000000;length_d\n");
                if (hasControl) {
                    out.write("c <- readBamGappedAlignments(\'" + controlPath.replace(" ", "\\ ") + "\', index=\'" + controlPath.replace(" ", "\\ ") + "\', param=param); c\n" +
                            "rc <- unlist(grglist(c));\n");
                    out.write("length_c <-length(c)/1000000;length_c\n");
                }
                // calc fragmentlength
                if (resize) {
                    // assuming, that the real fragment length is rather wide, we take the max of the 
                    // distances based on several calculations
                    out.write(
                            //"lsc <- estimate.mean.fraglen(rd, method=\'correlation\'); lsc\n" +
                            "lss <- estimate.mean.fraglen(rd, method=\'SISSR\'); lss\n" +
                            "lscov <- estimate.mean.fraglen(rd, method=\'coverage\'); lscov\n" +
                            "lmax <- max(c( median(lss), median(lscov))); lmax\n" +
                            "rd <- resize(rd, lmax,fix=\'center\')\n" +
                            "start(rd[start(rd) < 1]) <- 1\n");
                    if (hasControl) {
                        out.write(
                                "lss_control <- estimate.mean.fraglen(rc, method=\'SISSR\'); lss_control\n" +
                                "lscov_control <- estimate.mean.fraglen(rc, method=\'coverage\'); lscov_control\n" +
                                "lmax_control <- max(c( median(lss_control), median(lscov_control))); lmax_control\n" +
                                "rc <- resize(rc, lmax_control,fix=\'center\')\n" +
                                "start(rc[start(rc) < 1]) <- 1\n");
                    }
                }
                if (shift) {
                    // assuming, that the real fragment length is rather wide, we take the max of the 
                    // distances based on several calculations
                    out.write(
                            //"lsc <- estimate.mean.fraglen(rd, method=\'correlation\'); lsc\n" +
                            "lss <- estimate.mean.fraglen(rd, method=\'SISSR\'); lss\n" +
                            "lscov <- estimate.mean.fraglen(rd, method=\'coverage\'); lscov\n" +
                            "lmax <- max(c( median(lss), median(lscov))); lmax\n" +
                            "rd <- c(shift(rd[strand(rd) == \'+\'],lmax/2), shift(rd[strand(rd) == \'-\'],lmax/2));" +
                            "start(rd[start(rd) < 1]) <- 1\n");
                    if (hasControl) {
                        out.write(
                                "lss_control <- estimate.mean.fraglen(rc, method=\'SISSR\'); lss_control\n" +
                                "lscov_control <- estimate.mean.fraglen(rc, method=\'coverage\'); lscov_control\n" +
                                "lmax_control <- max(c( median(lss_control), median(lscov_control))); lmax_control\n" +
                                "rc <- c(shift(rcd[strand(rc) == \'+\'],lmax_control/2), shift(rc[strand(rc) == \'-\'],lmax_control/2));" +
                                "start(rc[start(rc) < 1]) <- 1\n");
                    }
                }
                out.write("rd_cov <- coverage(rd)\n");
                if (hasControl) {
                    out.write("rc_cov <- coverage(rc)\n");
                }
                out.write("options(\'scipen\'=100)\n");
                if (BAMImport.this.calcPeaksPoisson || BAMImport.this.calcPeaksQuantile) {
                    hasPeakFile = true;
                    if (BAMImport.this.calcPeaksPoisson) {

                        out.write("p_value <- 0.001; cut <- peakCutoff(rd_cov, fdr = p_value); cut\n ");
                    }
                    if (BAMImport.this.calcPeaksQuantile) {
                        out.write("p_value <- 0.001; cut <- median(quantile(rd_cov, c(1-p_value))); cut\n");
                    }
                    // kt 260713    peak sum/reads_total_per_million
                    out.write("rd_peaks <- slice(rd_cov, lower = cut)\n" +
                            "rd_peaks_sum <- peakSummary(rd_peaks)\n" +
                            "values(rd_peaks_sum) <- DataFrame(score=((1000*rd_peaks_sum$sum/width(rd_peaks_sum))/length_d))\n" +
                            (BAMImport.this.shift ? "write(paste(\'#shift:\', lmax) , append=TRUE, file=\"" +
                            resultPeakFilename.replace(" ", "\\ ") + "\")\n" : "") +
                            /* (BAMImport.this.resize ? "write(paste(\'#resize center:\', lmax) , append=TRUE, file=\"" + 
                            resultPeakFilename.replace(" ", "\\ ") + "\")\n" +*/
                            (BAMImport.this.calcPeaksPoisson ? "write(\'#peak poisson\', append=TRUE, file=\"" +
                            resultPeakFilename.replace(" ", "\\ ") + "\")\n" : "write(\'#peak quantile\n\',append=TRUE, file=\"" + resultPeakFilename.replace(" ", "\\ ") + "\")\n") +
                            "write(paste(\'#p-value:\',p_value, \'\n#cutoff:\',cut), append=TRUE,  file=\"" + resultPeakFilename.replace(" ", "\\ ") + "\")\n" +
                            "export(rd_peaks_sum, format = \"bedGraph\",append=TRUE, \"" + resultPeakFilename.replace(" ", "\\ ") + "\")\n");


                    if (BAMImport.this.hasControl) {
                        hasPeakControlFile = true;
                        hasPeakVsControlFile = true;

                        if (BAMImport.this.calcPeaksPoisson) {

                            out.write("p_value_control <- 0.001; " +
                                    "cut_control <- peakCutoff(rc_cov, fdr = p_value); cut_control\n ");
                        }
                        if (BAMImport.this.calcPeaksQuantile) {
                            out.write("p_value_control <- 0.001; " +
                                    "cut_control <- median(quantile(rc_cov, c(1-p_value))); cut_control\n");
                        }
                        out.write("rc_peaks <- slice(rc_cov, lower = cut_control)\n" +
                                "rc_peaks_sum <- peakSummary(rc_peaks)\n" +
                                "values(rc_peaks_sum) <- DataFrame(score=((1000*rc_peaks_sum$sum/width(rc_peaks_sum))/length_c))\n" +
                                "write(\'#peakControl\',  file=\"" + resultPeakControlFilename.replace(" ", "\\ ") + "\")\n" +
                                "export(rc_peaks_sum,format = \"bedGraph\",append=TRUE,\"" + resultPeakControlFilename.replace(" ", "\\ ") + "\")\n");
                        // calc differences between data and control
                        out.write("peak_d_c <- diffPeakSummary(rd_peaks, rc_peaks)\n" +
                                "peaks_diff <- within(peak_d_c, {\n" +
                                "diffs <- log2(sums1) - log2(sums2)\n" +
                                "resids <- (diffs - median(diffs)) / mad(diffs)\n" +
                                "#mad schätzer standardabweichung\n" +
                                "#e z-score in a table of the standard: 2 -> 0.9772\n " +
                                "up <- resids > 2 \n" +
                                "down <- resids < -2 \n" +
                                "change <- ifelse(up, \"up\", ifelse(down, \"down\", \"flat\"))\n" +
                                "})\n" +
                                "peaks_diff_exp <- peaks_diff\n" +
                                "values(peaks_diff_exp) <-DataFrame(score = (peaks_diff_exp$resids));\n" +
                                "write(\'#peakVsControl\',  file=\"" + resultPeakVsControlFilename.replace(" ", "\\ ") + "\")\n" +
                                "export(peaks_diff_exp, format = \"bedGraph\", append=TRUE, \"" + resultPeakVsControlFilename.replace(" ", "\\ ") + "\")\n");

                    }

                }
                // output as bin  - in every case

                out.write("rd_bin <- vector(\'list\')\n" +
                        "for(i in c(1:length(rd_cov))){\n " +
                        "v <- as.vector(rd_cov[[i]])\n" +
                        "v <-c(v, rep(0," + BAMImport.this.binsize + "-(length(v)%%" + BAMImport.this.binsize + ")))\n" +
                        "rd_bin[[names(rd_cov[i])]]  <- rowMeans(matrix(v, ncol=" + BAMImport.this.binsize + ", byrow = TRUE))*1000/length_d\n" +
                        "}\n");

                out.write("write(\'track type=wiggle_0 \', file=\'" + resultBinFilename.replace(" ", "\\ ") + "\')\n");
                out.write((BAMImport.this.shift ? "write(paste(\'#shift:\', lmax,\'\n#reads [M]:\',length_d) , file=\"" +
                        resultBinFilename.replace(" ", "\\ ") + "\")\n" : "") +
                        "for(i in c(1:length(rd_bin))){\n" +
                        "write(paste(\'fixedStep chrom=\', names(rd_bin[i]), \' start=1  step=\'," + BAMImport.this.binsize +
                        ",\'    span=\'," + BAMImport.this.binsize + ", sep=\'\'),append=TRUE, " +
                        "file=\"" + resultBinFilename.replace(" ", "\\ ") + "\")\n" +
                        "write(rd_bin[[i]],sep=\'\\n\', append=TRUE, file=\"" + resultBinFilename.replace(" ", "\\ ") + "\")\n" +
                        "}\n");
                hasBinFile = true;
                if (hasControl) {
                    out.write("rc_bin <- vector(\'list\')\n" +
                            "for(i in c(1:length(rc_cov))){\n " +
                            "vc <- as.vector(rc_cov[[i]])\n;" +
                            "v <-c(v, rep(0," + BAMImport.this.binsize + "-(length(v)%%" + BAMImport.this.binsize + ")))\n" +
                            "rc_bin[[names(rc_cov[i])]]  <- rowMeans(matrix(vc, ncol=" + BAMImport.this.binsize + ", byrow = TRUE))*1000/length_c\n" +
                            "}\n");
                    out.write("write(\'track type=wiggle_0 \', file=\'" + resultBinControlFilename.replace(" ", "\\ ") + "\')\n");
                    out.write("write(\'#test\',  file=\"" + resultBinControlFilename.replace(" ", "\\ ") + "\")\n" +
                            "for(i in c(1:length(rc_bin))){\n" +
                            "write(paste(\'fixedStep chrom=\', names(rc_bin[i]), \' start=1 step=\'," + BAMImport.this.binsize +
                            ",\'    span=\'," + BAMImport.this.binsize + ", sep=\'\'),append=TRUE, " +
                            "file=\"" + resultBinControlFilename.replace(" ", "\\ ") + "\")\n" +
                            "write(rc_bin[[i]],sep=\'\\n\', append=TRUE, file=\"" + resultBinControlFilename.replace(" ", "\\ ") + "\")\n" +
                            "}\n");
                    hasBinControlFile = true;
                } else {
                    hasBinControlFile = false;

                //
                }
                out.close();
            } catch (IOException exception) {
                publish(exception.getMessage());
                Logger.getLogger(BAMWorkerImpl.class.getName()).log(
                        Level.SEVERE, "There is error during exporting command!");
                return false;
            }

            try {
                String[] command;
                String line;
                int extVal = 0;
                BufferedReader input;

                publish("call " + getModulName());
                Logger.getLogger(BAMWorkerImpl.class.getName()).log(
                        Level.INFO, "call " + getModulName());
                command = Utils.getRCMD(rFilename.replace(" ", "\\ "));


                Logger.getLogger(BAMWorkerImpl.class.getName()).log(Level.INFO, new Vector<String>(Arrays.asList(command)).toString());
                Process p = Runtime.getRuntime().exec(command);
                input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                //error = new BufferedReader(new InputStreamReader(p.getErrorStream()));



                while ((line = input.readLine()) != null) {
                    publish(line);

                }
                publish("call " + getModulName() + "  end input...");
                input.close();

                try {
                    extVal = p.waitFor();
                } catch (java.lang.InterruptedException e) {
                    publish("ERROR: " + e.getMessage());
                    Logger.getLogger(BAMWorkerImpl.class.getName()).log(
                            Level.SEVERE, "Error: ", e);
                    //throw (new RuntimeException(e));
                    return false;
                }

                if (extVal != 0) {
                    BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    while ((line = err.readLine()) != null) {
                        publish(line);
                        Logger.getLogger(BAMWorkerImpl.class.getName()).log(
                                Level.INFO, line);
                    }
                    err.close();
                    Logger.getLogger(BAMWorkerImpl.class.getName()).log(
                            Level.SEVERE, "call " + getModulName() + " failed: exit " + p.exitValue());
                    publish("call " + getModulName() + " failed: exit " + p.exitValue());
                    return false;
                }
            } catch (Exception exception) {
                //publish("There is error during calculation, check logfile! " + exception.getMessage());
                Logger.getLogger(BAMWorkerImpl.class.getName()).log(
                        Level.SEVERE, "",
                        exception);
                return false;

            }
            return true;

        }

        @Override
        protected void process(List<String> chunks) {
            for (String message : chunks) {
                inf.messageChanged(message);
            }
        }
        /**
         * 
         * @param filename  
         * @param wigfixedstep 
         * @return
         * @throws java.lang.Exception
         */
        /* ChipFeature importData(String filename, boolean wigfixedstep) throws Exception {
        
        File resultFile = new File(filename);
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
        name,
        release,
        Defaults.DataType.PEAK.toString());
        
        d.setId(null);
        
        
        d.setClazz(FeaturePeak.class.getName());
        d.setParent(null);
        d.setOwner(ExperimentService.getUser());
        d.setProcProcessing(getModulName());
        d.setParamProcessing("\n" + param.trim());
        ChipFeature newChip = new ChipFeature(d);
        
        Hashtable<String, Vector<? extends Feature>> data = null;
        try {
        if (wigfixedstep) {
        data = FeaturePeak.loadFromFixedStepFile(filename);
        } else {
        data = FeaturePeak.loadFromPeakFile(filename);
        }
        } catch (Exception e) {
        newChip.setError(true);
        publish("There is error during reimport, checklogfile! " + e.getMessage());
        Logger.getLogger(BAMWorkerImpl.class.getName()).log(
        Level.SEVERE, "There is error during reimport, check logfile!",
        e);
        throw e;
        }
        
        newChip.chrFeatures = data;
        return newChip;
        }
         */
    }//end worker

    public void setDataPath(String d) {
        this.dataPath = d;
        this.name = d.substring(d.lastIndexOf(File.separator) + 1, d.lastIndexOf(".") - 1);
    }

    public void setControlPath(String c) {
        this.controlPath = c;
    }

    public String getModulName() {
        return BAMImport.methodName;
    }

    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{BAMImport.methodName}));
    }

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public boolean isCalcPeaksPoisson() {
        return calcPeaksPoisson;
    }

    public void setCalcPeaksPoisson(boolean calcPeaksPoisson) {
        this.calcPeaksPoisson = calcPeaksPoisson;
    }

    public boolean isCalcPeaksQuantile() {
        return calcPeaksQuantile;
    }

    public void setCalcPeaksQuantile(boolean calcPeaksQuantile) {
        this.calcPeaksQuantile = calcPeaksQuantile;
    }

    public boolean isHasControl() {
        return hasControl;
    }

    public void setHasControl(boolean hasControl) {
        this.hasControl = hasControl;
    }

    public boolean isNormalizeWithControl() {
        return normalizeWithControl;
    }

    public void setNormalizeWithControl(boolean normalizeWithControl) {
        this.normalizeWithControl = normalizeWithControl;
    }

    public boolean isNormalizeWithGCLoess() {
        return normalizeWithGCLoess;
    }

    public void setNormalizeWithGCLoess(boolean normalizeWithGCLoess) {
        this.normalizeWithGCLoess = normalizeWithGCLoess;
    }

    public boolean isControlSorted() {
        return controlSorted;
    }

    public void setControlSorted(boolean controlSorted) {
        this.controlSorted = controlSorted;
    }

    public boolean isDataSorted() {
        return dataSorted;
    }

    public void setDataSorted(boolean dataSorted) {
        this.dataSorted = dataSorted;
    }

    public boolean isHasBinControlFile() {
        return hasBinControlFile;
    }

    /* public void setHasBinControlFile(boolean hasBinControlFile) {
    this.hasBinControlFile = hasBinControlFile;
    }*/
    public boolean isHasBinFile() {
        return hasBinFile;
    }

    public void setHasBinFile(boolean hasBinFile) {
        this.hasBinFile = hasBinFile;
    }

    public void setHasBinControlFile(boolean b) {
        this.hasBinControlFile = b;
    }

    public boolean isHasPeakControlFile() {
        return hasPeakControlFile;
    }

    public void setHasPeakControlFile(boolean hasPeakControlFile) {
        this.hasPeakControlFile = hasPeakControlFile;
    }

    public boolean isHasPeakFile() {
        return hasPeakFile;
    }

    public void setHasPeakFile(boolean hasPeakFile) {
        this.hasPeakFile = hasPeakFile;
    }

    public boolean isHasPeakVsControlFile() {
        return hasPeakVsControlFile;
    }

    public void setHasPeakVsControlFile(boolean hasPeakVsControlFile) {
        this.hasPeakVsControlFile = hasPeakVsControlFile;
    }

    public int getBinsize() {
        return binsize;
    }

    public void setBinsize(int binsize) {
        this.binsize = binsize;
    }
}
