/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.xportagilent;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.cghpro.xport.XPortPlatform;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.guimodul.experiment.ConvertExperimentDialog;
import org.molgen.genomeCATPro.guimodul.platform.CreatePlatformDialog;

/**
 *
 * @author tebel
 */
public class TestAgilent {

    XPortPlatform xport;

    public TestAgilent() {
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
        //  xport = new ImportPlatformGEOBAC();
        xport = new ImportPlatformFETXT();
    }

    @After
    public void tearDown() {
    }

    //@Test
    @SuppressWarnings("empty-statement")
    public void testConvert() throws InterruptedException {


        try {
            ExperimentData d = ExperimentService.getExperimentByDataId((long) 57);
            ConvertExperimentDialog.convertExperiment(d);
        } catch (Exception ex) {
            Logger.getLogger(TestAgilent.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testPlatform() throws InterruptedException {

        try {

            CreatePlatformDialog dialog = new CreatePlatformDialog(
                    new javax.swing.JFrame(),
                    this.xport,
                    ImportPlatformFETXT.platform_fe);
            dialog.setVisible(true);
            while (true);
        } catch (Exception ex) {
            Logger.getLogger(TestAgilent.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
}
