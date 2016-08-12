/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.cghpro;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.service.PlatformService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.guimodul.platform.ConvertPlatformDialog;

/**
 *
 * @author tebel
 */
public class TestConvertPlatform {

    

    public TestConvertPlatform() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        Database.setDBParams(Defaults.localDB, "genomecat", "localhost", "3306", "test", "test");

        DBService.setConnection("localhost", "3306", "genomecat", "test", "test");//xport = new ImportPlatformGEOBAC();
       
        
    }

    @After
    public void tearDown() {
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testConvert() throws InterruptedException {

        try {
            PlatformData s = PlatformService.getPlatformDataById((long) 1);
            ConvertPlatformDialog.convertPlatformData(s);
        } catch (Exception ex) {
            Logger.getLogger(TestConvertPlatform.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

}
