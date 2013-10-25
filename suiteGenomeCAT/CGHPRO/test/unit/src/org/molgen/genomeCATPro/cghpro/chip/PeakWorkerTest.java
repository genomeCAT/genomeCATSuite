package org.molgen.genomeCATPro.cghpro.chip;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;





import org.molgen.dblib.Database;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Informable;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author tebel
 */
public class PeakWorkerTest {

    Informable informable = new Informable() {

        @Override
        public void messageChanged(String message) {

            Logger.getLogger(PeakWorkerTest.class.getName()).log(Level.INFO, message);
        }
    };

    public PeakWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        Database.setDBParams(Defaults.localDB, "genomeCAT", "localhost", "3306", "user", "user");

        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");


    }

    @After
    public void tearDown() {
    }

    //@Test
    @SuppressWarnings("empty-statement")
    public void testImportFixedWig() throws InterruptedException {
        Hashtable<String, Vector<? extends Feature>> data = null;
        try {



            data = FeaturePeak.loadFromFixedStepFile("/project/H1N1/Katrin/test_write.txt");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        int j = 0;
        for (String chrom : data.keySet()) {
            double sum = 0;
            j = 0;
            for (Feature p : data.get(chrom)) {
                if(j++ < 10) System.out.println(p.toString() + " " + p.getRatio());
                sum += p.getRatio();
            }
            
            System.out.println(chrom + " : " + sum);
            

        }


    }
    @Test

    @SuppressWarnings("empty-statement")
    public void testPeak() throws InterruptedException {
        ChipFeature chip = null;
        try {
            ExperimentData e = ExperimentService.getExperimentByDataId((long) 66);
            chip = (ChipFeature) ChipImpl.loadChipAsExperimentFromDB(ChipFeature.class, e);


        } catch (Exception ex) {
            Logger.getLogger(PeakWorkerTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
        assertFalse("Chip has error", chip.getError());





        PeakWorker worker = new PeakWorker(chip, "testRINGO_1", true, true, 0.5, 100, 100, false, informable);
        // worker.execute();
        worker.exportData(chip, new File("/project/H1N1/Katrin/RINGO_66.txt"));
        ChipFeature newChip = null;

    }
}