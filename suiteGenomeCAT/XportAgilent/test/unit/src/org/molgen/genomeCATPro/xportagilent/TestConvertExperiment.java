/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.xportagilent;

import org.molgen.genomeCATPro.cghpro.xport.ImportPlatformBACID;
import org.molgen.genomeCATPro.cghpro.xport.XPortPlatform;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.guimodul.experiment.ConvertExperimentDialog;

/**
 *
 * @author tebel
 */
public class TestConvertExperiment {

    XPortPlatform xport;

    public TestConvertExperiment() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");
        //xport = new ImportPlatformGEOBAC();
        xport = new ImportPlatformBACID();
    }

    @After
    public void tearDown() {
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testConvert() throws InterruptedException {

        try {
            ExperimentData d = ExperimentService.getExperimentByDataId((long) 57);
            ConvertExperimentDialog.convertExperiment(d);
        } catch (Exception ex) {
            Logger.getLogger(TestConvertExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

}
