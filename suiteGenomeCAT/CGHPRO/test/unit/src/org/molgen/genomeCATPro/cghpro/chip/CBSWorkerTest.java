package org.molgen.genomeCATPro.cghpro.chip;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;




import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.cghpro.chip.CBSWorker;
import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;

import org.molgen.genomeCATPro.cghpro.chip.ChipImpl;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Informable;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import static org.junit.Assert.*;
/**
 *
 * @author tebel
 */
public class CBSWorkerTest {

    
    Informable informable = new Informable() {

        @Override
        public void messageChanged(String message) {

            Logger.getLogger(CBSWorkerTest.class.getName()).log(Level.INFO, message);
        }
    };

    public CBSWorkerTest() {
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
    public void testCBS() throws InterruptedException {
        ChipFeature chip = null;
        try {
              ExperimentData e = ExperimentService.getExperimentByDataId((long) 1271);
            chip = (ChipFeature) ChipImpl.loadChipAsExperimentFromDB(ChipFeature.class, e);

            /*(ChipFeature) ChipImpl.loadChipAsExperimentFromDB(
                    ChipFeature.class, 
                    "AG0157_Cy3_BM_17652_Cy5_Epi_1037_251469327207_081107_CGH-v4_10_Apr08", 
                    "hg18:NCBI36:Mar2006", null);*/
          
        } catch (Exception ex) {
            Logger.getLogger(CBSWorkerTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
        assertFalse("Chip has error", chip.getError());



        

        CBSWorker worker = new CBSWorker(
                chip, "test",
                informable);

        worker.execute();
        ChipFeature newChip = null;
        try {

            while ((newChip = worker.get()) == null) {
                ;
            }
        } catch (Exception ex) {
            Logger.getLogger(CBSWorkerTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
       
    }
}