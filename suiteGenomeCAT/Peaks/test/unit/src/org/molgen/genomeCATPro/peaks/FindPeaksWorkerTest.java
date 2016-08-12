/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/**
 *
 * @author tebel
 */
public class FindPeaksWorkerTest {

    public FindPeaksWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");
        Database.setDBParams(Defaults.localDB, "genomeCAT", "localhost", "3306", "user", "user");

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testFindPeaks() throws InterruptedException {
        ChipFeature chip = null;

        ExperimentData d = ExperimentService.getExperimentByDataId((long) 306);
        ExtractPeakDialog dd = new ExtractPeakDialog(null, true, d);
        dd.setVisible(true);
        while (true);

    }
}
