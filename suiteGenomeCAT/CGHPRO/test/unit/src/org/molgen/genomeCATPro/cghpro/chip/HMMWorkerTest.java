package org.molgen.genomeCATPro.cghpro.chip;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Informable;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import static org.junit.Assert.*;

/**
 *
 * @author tebel
 */
public class HMMWorkerTest {

    Informable informable = new Informable() {

        @Override
        public void messageChanged(String message) {

            Logger.getLogger(HMMWorkerTest.class.getName()).log(Level.INFO, message);
        }
    };

    public HMMWorkerTest() {
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

    @Test
    @SuppressWarnings("empty-statement")
    public void testHMM() throws InterruptedException {
        ChipFeature chip = null;
        try {
            ExperimentData e = ExperimentService.getExperimentByDataId((long) 66);
            chip = (ChipFeature) ChipImpl.loadChipAsExperimentFromDB(ChipFeature.class, e);

        } catch (Exception ex) {
            Logger.getLogger(HMMWorkerTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
        assertFalse("Chip has error", chip.getError());

        HMMWorker worker = new HMMWorker(
                chip, "testHMM_1",
                informable);
        worker.execute();
        // worker.exportBacs(chip, new File("/project/H1N1/Katrin/582.txt"));
        ChipFeature newChip = null;
        try {

            while ((newChip = worker.get()) == null) {
                ;
            }
        } catch (ExecutionException ex) {
            Logger.getLogger(HMMWorkerTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }

    }
}
