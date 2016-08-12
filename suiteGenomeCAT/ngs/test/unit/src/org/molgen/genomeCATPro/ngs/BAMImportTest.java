/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.ngs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.cghpro.chip.FeaturePeak;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Informable;
import org.openide.util.Exceptions;
import static org.junit.Assert.*;
import org.molgen.genomeCATPro.data.IFeature;

/**
 *
 * @author tebel
 */
public class BAMImportTest {

    Informable informable = new Informable() {

        @Override
        public void messageChanged(String message) {

            Logger.getLogger(BAMImportTest.class.getName()).log(Level.INFO, message);
        }
    };

    public BAMImportTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Database.setDBParams(Defaults.localDB, "genomeCAT", "senilebettflucht", "3306", "user", "user");

        DBService.setConnection("senilebettflucht", "3306", "genomeCAT", "user", "user");

        System.setProperty("netbeans.dirs", "/scratch/local/katrin/devel/src/suiteGenomeCAT/build/cluster");
    }

    //@Test
    @SuppressWarnings("empty-statement")
    public void testImportFixedWig() throws InterruptedException {
        Hashtable<String, Vector<? extends IFeature>> data = null;
        try {

            data = FeaturePeak.loadFromFixedStepFile("/project/H1N1/Katrin/test_write.txt");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        int j = 0;
        for (String chrom : data.keySet()) {
            double sum = 0;
            j = 0;
            for (IFeature p : data.get(chrom)) {
                if (j++ < 10) {
                    System.out.println(p.toString() + " " + p.getRatio());
                }
                sum += p.getRatio();
            }

            System.out.println(chrom + " : " + sum);
        }
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testBAMImportGUI() throws InterruptedException {
        XPortNGS mod = new BAMImport();
        JDialog d = new ImportNGSDialog(null, mod);
        d.setVisible(true);

        try {

            while (true) {
                ;
            }
        } catch (Exception ex) {
            Logger.getLogger(BAMImportTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }

    }

    @Test
    @Ignore
    @SuppressWarnings("empty-statement")
    public void testBAMImportGUIImport() throws InterruptedException {
        BAMImport mod = new BAMImport();
        mod.setHasPeakVsControlFile(true);
        mod.setHasBinFile(true);
        mod.setHasBinControlFile(true);
        mod.setHasPeakFile(true);
        mod.setHasPeakControlFile(true);

        mod.setResultBinFilename("/scratch/local/katrin/devel/src/suiteGenomeCAT/SRR037628.sorte_bin_NGS_BAM.txt");
        mod.setResultBinControlFilename("/scratch/local/katrin/devel/src/suiteGenomeCAT/SRR037628.sorte_bin_control_NGS_BAM.txt");
        mod.setResultPeakVsControlFilename("/scratch/local/katrin/devel/src/suiteGenomeCAT/SRR037628.sorte_peak_vs_control_NGS_BAM.txt");
        mod.setResultPeakFilename("/scratch/local/katrin/devel/src/suiteGenomeCAT/SRR037628.sorte_peak_NGS_BAM.txt");
        mod.setResultPeakControlFilename("/scratch/local/katrin/devel/src/suiteGenomeCAT/SRR037628.sorte_peak_control_NGS_BAM.txt");

        JDialog d = new ImportNGSDialog(null, mod);
        d.setVisible(true);

        try {

            while (true) {
                ;
            }
        } catch (Exception ex) {
            Logger.getLogger(BAMImportTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
    //@Test

    @SuppressWarnings("empty-statement")
    public void testBAMImport() throws InterruptedException {

        XPortNGS mod = new BAMImport();
        assertFalse(BAMImport.methodName + " not found", (mod == null));

        mod.initImport();
        mod.setHasControl(false);
        mod.setDataPath("/project/H1N1/Data/600DRAAXX/l3/l3_RA_3h_sorted.bam");

        BAMImport modBam = (BAMImport) mod;
        //modBam.setCalcPeaksPoisson(false);
        modBam.setCalcPeaksQuantile(true);
        modBam.setShift(true);

        PropertyChangeListener listener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                informable.messageChanged("caller: " + evt.getNewValue());
                if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    informable.messageChanged("caller: GOT DONE!!");
                    System.exit(1);
                }
            }
        };

        mod.doRunImport(informable, listener);
        // worker.exportBacs(chip, new File("/project/H1N1/Katrin/582.txt"));

        try {

            while (true) {
                ;
            }
        } catch (Exception ex) {
            Logger.getLogger(BAMImportTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }

    }

    //@Test
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
}
