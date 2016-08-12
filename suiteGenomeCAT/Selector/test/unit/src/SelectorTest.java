/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.molgen.genomeCATPro.selector.SelectorDialog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/**
 *
 * @author tebel
 */
public class SelectorTest {

    public SelectorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Database.setDBParams(Defaults.localDB, "genomeCAT", "localhost", "3306", "user", "user");

        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testMain() {
        Data d2 = ExperimentService.getExperimentByDataId((long) 350);
        //Data d2 = TrackService.getTrackById((long) 143);
        SelectorDialog s = new SelectorDialog(new javax.swing.JFrame(), true, d2);
        s.setVisible(true);
        while (true);
    }
}
