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
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.GeneImpl;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/**
 *
 * @author tebel
 */
public class TestAnnotateData {

    public TestAnnotateData() {
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

    @Test(timeout = 360000000)
    @SuppressWarnings("empty-statement")
    public void testAnnotate() throws InterruptedException {

        try {
            //ExperimentData d = ExperimentService.getExperimentByDataId((long) 359);
            ExperimentData d = ExperimentService.getExperimentByDataId((long) 711);
            //AnnotateDialog.annotateDataExperiment(d);

            /*Data newData = DataManager.annotateData(
            d, GeneImpl.nameId, "getName2",
            DataManager.AnnoQuery.Overlap, null, 0, 0);*/

            /*Data newData = DataManager.annotateData(
            d, GeneImpl.nameId, "getName2",
            DataManager.AnnoQuery.DataContainsAnno, DataManager.AnnoSubject.Middle, 100, 100);
            */
            /*Data newData =  DataManager.annotateData(
            d,  GeneImpl.nameId, "getName2",
            DataManager.AnnoQuery.DataContainsAnno,  DataManager.AnnoSubject.Whole, 0, 0);
            */
            
            /*Data newData =  DataManager.annotateData(
            d,  GeneImpl.nameId, "getName2",
            DataManager.AnnoQuery.DataWithinAnno,  DataManager.AnnoSubject.Whole, 0, 0); */

            Data newData =  DataManager.annotateData(
            d,  GeneImpl.nameId, "getName2",
            DataManager.AnnoQuery.DataWithinAnno,  DataManager.AnnoSubject.Middle, 0, 100); 

            
            
            // BAC    

           /* ExperimentData d = ExperimentService.getExperimentByDataId((long) 331);
            Data newData;

            newData = DataManager.annotateData(
                    d, GeneImpl.nameId, "getName2",
                    DataManager.AnnoQuery.Overlap, null, 0, 0);
        
            newData =  DataManager.annotateData(
            d,  GeneImpl.nameId, "getName2",
            DataManager.AnnoQuery.DataContainsAnno,  DataManager.AnnoSubject.Whole, 0, 0);*/



        } catch (Exception ex) {
            Logger.getLogger(TestAnnotateData.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
}