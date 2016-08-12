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
import org.molgen.genomeCATPro.common.MyMath;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.data.IFeature;

/**
 * @name CBSWorker.java calls R Package DNAcopy
 * http://www.bioconductor.org/packages/2.12/bioc/html/DNAcopy.html
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
 * 300713 kt bug end segment 290512 kt exportBacs format ratio 290512 kt
 * exportBacs name not empty 200712 kt store package infos as comments, try to
 * install package if not there 030812 kt exportBacs set DecimalFormatSymbol
 * explizit 100812 kt no Grouping DecimalFormat
 */
public class CBSWorker extends SwingWorker<ChipFeature, String> {

    ChipFeature oldChip = null;
    public final static String methodName = "CBS";
    //String rDir = new String("tmp");
    String dataFilename = "";
    String rFilename = "";
    String resultFilename = "";
    private final Informable informable;
    String mainDir = "";
    String newName = "";
    String param = "";

    public CBSWorker(ChipFeature c, String name, Informable inf) {
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

    protected ChipFeature doInBackground() throws Exception {
        publish("run CBS in Background...");
        setProgress(0);
        ChipFeature newChip = new ChipFeature(false);

        Logger.getLogger(CBSWorker.class.getName()).log(Level.CONFIG, "Main: "
                + mainDir);
        if (oldChip == null || oldChip.getError()) {
            Logger.getLogger(CBSWorker.class.getName()).log(Level.SEVERE,
                    " chip has error");
            return newChip;
        }
        this.dataFilename = mainDir + File.separator + File.separator
                + oldChip.getDataEntity().getTableData() + "_cbs.dat";
        dataFilename = dataFilename.replace("\\", "\\\\");

        Logger.getLogger(CBSWorker.class.getName()).log(Level.CONFIG,
                "datafile for R: " + dataFilename);

        File dataFile = new File(dataFilename);
        int bacNo = exportBacs(oldChip, dataFile);

        if (bacNo < 0) {
            Logger.getLogger(CBSWorker.class.getName()).log(
                    Level.WARNING,
                    "There is error during exporting data!");
            publish("There is error during exporting data!");
            return newChip;
        }
        setProgress(10);
        Hashtable<String, Vector<? extends IFeature>> breakPoints = runCBS(oldChip, 10);
        setProgress(80);

        try {
            newChip = importBreakPoints(breakPoints, this.newName);
        } catch (Exception ex) {
            publish("There is error creating cbs chip!");
            Logger.getLogger(CBSWorker.class.getName()).log(Level.WARNING,
                    "There is error creating cbs chip!", ex);
            newChip.error = true;
            return newChip;
        }
        setProgress(90);

        publish("Save Chip...");
        boolean success = false;
        try {
            newChip.saveChipToDB();
            success = true;
        } catch (Exception e) {
        }
        setProgress(100);

        Utils.deleteFile(rFilename);
        Utils.deleteFile(resultFilename);
        Utils.deleteFile(dataFilename);

        //chip.setIfCbs(true);
        System.gc();
        if (!success) {
            publish("Error saving Chip see logfile for details");
        } else {
            publish("Congratulations! CBS succesfully finished");
        }
        return newChip;

        // update the progress
    }

    /**
     * export Features to external file to be import for CBS R Packages
     *
     * @param chip
     * @param outFile
     * @return
     */
    int exportBacs(ChipFeature chip, File outFile) {

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
                    if (chrom
                            <= 0) {
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
            Logger.getLogger(CBSWorker.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

            return - 1;
        }

    }

    /**
     * import output from CBS R Package add breakpoints as data to new chip
     * (child of old chip), MAD is calculated as the median of the absolute
     * deviations from the smoothed segment value.
     *
     * @param oldChip
     * @param breakPoints
     * @return new chip with data
     * @throws java.lang.Exception
     */
    ChipFeature importBreakPoints(
            Hashtable<String, Vector<? extends IFeature>> breakPoints, String _name) throws Exception {
        Track d = new Track(
                "CBS_" + _name,
                oldChip.getDataEntity().getGenomeRelease(),
                Defaults.DataType.SEGMENTS.toString());
        //d.copy(oldChip.getDataEntity());
        d.setId(null);
        d.setGenomeRelease(oldChip.getDataEntity().getGenomeRelease());

        d.setDataType(Defaults.DataType.SEGMENTS);

        d.setClazz(FeatureCBS.class.getName());

        d.setParent(oldChip.getDataEntity());
        d.setOwner(ExperimentService.getUser());

        ChipFeature newChip = new ChipFeature(d);

        //newChip.getDataEntity().setOriginalFile(" ");
        //newChip.getDataEntity().setDyeSwap(oldChip.getDataEntity().getDyeSwap());
        newChip.chrFeatures = breakPoints;

        IFeature currentF;
        List<? extends IFeature> listF = null;
        List<? extends IFeature> listBP = null;
        int indChrom = - 1;
        int index = 0;

        // calculate MAD
        double[] absoluteDeviation = new double[oldChip.getFeaturesSize()];
        for (String strChrom : oldChip.chrFeatures.keySet()) {

            indChrom = RegionLib.fromChrToInt(strChrom);
            if (indChrom <= 0) {
                continue;
            }

            listF = oldChip.chrFeatures.get(strChrom);
            listBP = newChip.chrFeatures.get(strChrom);
            if (listF.size() <= 0 || listBP.size() <= 0) {
                continue;
            }
            Collections.sort(listF, IFeature.comChromStart);

            int start = 0;
            //format cbs result:
            //name  chrom  chrstart    chrstop anzahlspots ratio
            //"Sample.1" 1	554268	247190718	17753	0.0179

            //for (int j = 0; j < breakPoints[indChrom - 1].size(); j += 2) {
            for (FeatureCBS bp : (Vector<FeatureCBS>) listBP) {
                //int no = ((Integer) breakPoints[indChrom - 1].get(j)).intValue();
                int no = bp.getCount();
                double[] ratio = new double[no];
                long[] ende = new long[no]; // kt    300713  bug end segment
                // all feature inside one cbs segment
                for (int m = 0; m < no; m++) {
                    currentF = listF.get(start + m);

                    if (m == 0 && currentF.getChromStart() != bp.getChromStart()) {
                        throw new RuntimeException("Unequal start values! original Feature: " + currentF.getChromStart() + " bp: " + bp.getChromStart());
                    }
                    ratio[m] = currentF.getRatio();
                    ende[m] = currentF.getChromEnd();

                }
                double median = MyMath.median(ratio);
                for (int n = 0; n < no; n++) {
                    absoluteDeviation[index++] = Math.abs(ratio[n] - median);

                }
                start += no;
                Arrays.sort(ende);
                bp.setChromEnd(ende[no - 1]);
            }
        }
        // trunk deviation array
        double[] tmp = Arrays.copyOf(absoluteDeviation, index);
        newChip.getDataEntity().setProcProcessing(CBSWorker.methodName);
        double madCBS = MyMath.formatDoubleValue(MyMath.median(tmp), 3);
        param += ("\nMAD:" + madCBS + "\n");
        newChip.getDataEntity().setParamProcessing("\n" + param.trim());

        return newChip;

    }

    /**
     * map original features and bp segments return new chip with cbs values for
     * each feature
     *
     * @param oldChip
     * @param breakPoints
     * @return
     */
    @Deprecated
    static void matchBreakPoints(ChipFeature chipFeatures, Hashtable<String, Vector<? extends IFeature>> breakPoints) throws Exception {
        // new bacs with cbs ratios erstellen

        List<? extends IFeature> listBP = null;
        // todo new Region as cbs values ??

        Hashtable<String, Vector<? extends IFeature>> segmentFeatures;

        for (String strChrom : chipFeatures.chrFeatures.keySet()) {

            if (chipFeatures.getData(strChrom).size() <= 0) {
                continue;
            }
            List<? extends IFeature> listF = (List<? extends IFeature>) chipFeatures.getData(strChrom);
            Collections.sort(listF, IFeature.comChromStart);
            listBP = breakPoints.get(strChrom);
            Collections.sort(listBP, IFeature.comChromStart);

            int start = 0;
            for (FeatureCBS bp : (List<FeatureCBS>) listBP) {
                int no = bp.getCount();
                double[] ratio = new double[no];
                for (int m = 0; m < no; m++) {

                    listF.get(start + m).setRatio(bp.getRatio());
                }

                start += no;
            }
        }

        /**
         * if(frame!= null){ JOptionPane.showMessageDialog(frame, "The MAD value
         * is "+chip.madCbs+"!"); note.append("The MAD value is " +
         * chip.madCbs+"!"); }
        /**
         * if(frame!= null){ JOptionPane.showMessageDialog(frame, "The MAD value
         * is "+chip.madCbs+"!"); note.append("The MAD value is " +
         * chip.madCbs+"!"); }
         */
    }

    /**
     * run CBS segment the clones into sets with the same copy number by R
     * package DNAcopy then calculate the smoothRatioByCbs as the average ratio
     * of the set,
     *
     * @param chip
     * @param iProgress
     * @return list of features for each chromosome
     */
    public Hashtable<String, Vector<? extends IFeature>> runCBS(ChipFeature chip, int iProgress) {
        //File currDir = new File(MainFrame.CghDir);
        //String mainDir = currDir.getAbsolutePath();

        rFilename = mainDir + File.separator + File.separator
                + chip.getDataEntity().getTableData() + "_cbs.R";
        rFilename = rFilename.replace("\\", "\\\\");
        Logger.getLogger(CBSWorker.class.getName()).log(Level.CONFIG, "executable R: " + rFilename);
        File rFile = new File(rFilename);

        resultFilename = mainDir + File.separator + File.separator + chip.getDataEntity().getTableData() + "_cbs.result";
        resultFilename = resultFilename.replace("\\", "\\\\");
        Logger.getLogger(CBSWorker.class.getName()).log(Level.CONFIG, "result R: " + resultFilename);

        File resultFile = new File(resultFilename);
        try {

            FileWriter out = new FileWriter(rFile);
            out.write(
                    "if(!length(grep(\"DNAcopy\", installed.packages()[,1])) > 0){ source(\"http://bioconductor.org/biocLite.R\"); biocLite(\"DNAcopy\",dependencies=TRUE); }\n"
                    + "library(DNAcopy)\n"
                    + "ddata<-read.table(file=\"" + dataFilename.replace(" ", "\\ ") + "\", na.strings=\"\\\\N\", header=TRUE, sep=\"\\t\")\n"
                    + "data <- na.omit(ddata)\n"
                    + "genomdat<-data$log2Ratio\n"
                    + "chrom<-data$Chrom\n"
                    + "maploc<-data$start\n"
                    + "CNA.object<-CNA(genomdat,chrom,maploc,data.type=\"logratio\")\n"
                    + "result<-segment(CNA.object, verbose=3)\n"
                    + "i <- sessionInfo()\n"
                    + "write(paste(\"#Package:\", i$otherPkgs$DNAcopy$Package), file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n"
                    + "write(paste(\"#Version:\", i$otherPkgs$DNAcopy$Version), append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n"
                    + "write(paste(\"#Reference:\", gsub(\"\\n\", \"\", i$otherPkgs$DNAcopy$Reference)), append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\") \n"
                    + "write.table(result$output, append=TRUE, file=\"" + resultFilename.replace(" ", "\\ ") + "\", sep=\"\\t\",row.names=FALSE, col.names=FALSE)");

            out.close();
        } catch (IOException exception) {
            publish(exception.getMessage());
            Logger.getLogger(CBSWorker.class.getName()).log(
                    Level.SEVERE, "There is error during exporting command!");

        }

        try {

            Runtime.getRuntime();

            String[] command = Utils.getRCommand(rFilename.replace(" ", "\\ "));
            //command += r;
            String line;
            Logger.getLogger(CBSWorker.class.getName()).log(Level.INFO, new Vector<String>(Arrays.asList(command)).toString());
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            publish("call R CBS Module...");
            Logger.getLogger(CBSWorker.class.getName()).log(
                    Level.INFO, "call R CBS Module...");
            int chr = 1;
            while ((line = input.readLine()) != null) {

                publish(line);
                if (line.contains("current chromosome")) {
                    this.setProgress((int) iProgress + (70 / 24 * chr++));
                    /*Logger.getLogger(
                CBSWorker.class.getName()).log(
                Level.INFO, line); */
                }
            }
            input.close();
            try {
                p.waitFor();
            } catch (java.lang.InterruptedException e) {
                publish(e.getMessage());
                Logger.getLogger(CBSWorker.class.getName()).log(
                        Level.SEVERE, "Error: ", e);
                throw (new RuntimeException(e));
            }

            if (p.exitValue() != 0) {
                BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = err.readLine()) != null) {
                    publish(line);
                    Logger.getLogger(CBSWorker.class.getName()).log(
                            Level.INFO, line);
                }
                err.close();
                Logger.getLogger(CBSWorker.class.getName()).log(
                        Level.SEVERE, "start CBS failed: exit " + p.exitValue());
                publish("start CBS failed: exit " + p.exitValue());

            }
        } catch (Exception exception) {
            publish("There is error during calculation, check notes! " + exception.getMessage());
            Logger.getLogger(CBSWorker.class.getName()).log(
                    Level.SEVERE, "There is error during calculation, check notes!",
                    exception);

        }

        Hashtable<String, Vector<? extends IFeature>> breakPoints = new Hashtable<String, Vector<? extends IFeature>>();

        try {

            FeatureCBS newF = null;
            BufferedReader input = new BufferedReader(new FileReader(resultFile));
            String line;

            int index = 0;
            String chrom = "";
            boolean header = false;

            while ((line = input.readLine()) != null) {
                if (line.startsWith("#")) {
                    header = true;
                    param += ("\n" + line);
                    continue;

                }
                if (header = true) {
                    break; // header gelesen

                }
            }

            input = new BufferedReader(new FileReader(resultFile));
            while ((line = input.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] par = line.split("\t");

                chrom = RegionLib.fromIntToChr(Integer.parseInt(par[1]));
                newF = new FeatureCBS();
                if (!breakPoints.containsKey(chrom)) {
                    breakPoints.put(chrom, new Vector<FeatureCBS>());
                }
                newF.setName("segment" + (++index));
                newF.setChrom(RegionLib.fromIntToChr(new Integer(par[1]).intValue()));
                newF.setChromStart(new Integer(par[2]));
                newF.setChromEnd(new Integer(par[3]));
                newF.setRatio(new Double(par[5]));
                newF.setCount(new Integer(par[4]));
                ((Vector<FeatureCBS>) breakPoints.get(chrom)).add(newF);
                //breakPoints[(new Integer(par[1])).intValue() - 1].add(new Integer(par[4]));
                // breakPoints[(new Integer(par[1])).intValue() - 1].add(new Double(par[5]));

            }
            input.close();
        } catch (Exception exception) {
            publish("There is error reading results! " + exception.getMessage());
            Logger.getLogger(CBSWorker.class.getName()).log(Level.WARNING,
                    "There is error reading results!", exception);
        }
        return breakPoints;
    }
}
