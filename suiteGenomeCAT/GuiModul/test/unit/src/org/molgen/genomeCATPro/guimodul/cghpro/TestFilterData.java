/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.cghpro;

import org.molgen.genomeCATPro.cghpro.xport.ImportPlatformBACID;
import org.molgen.genomeCATPro.cghpro.xport.XPortPlatform;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.guimodul.experiment.FilterExperimentDialog;

/**
 *
 * @author tebel
 */
public class TestFilterData {

    XPortPlatform xport;

    public TestFilterData() {
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
    public void testFilter() throws InterruptedException {

        try {
            Data s = ExperimentService.getExperimentByDataId((long) 54);
            FilterExperimentDialog.filterData(s);
        } catch (Exception ex) {
            Logger.getLogger(TestFilterData.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
}