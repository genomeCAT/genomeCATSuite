/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.experiment;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import static org.junit.Assert.*;

/**
 *
 * @author tebel
 */
public class TestSampleDetailView {

    public TestSampleDetailView() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Database.setDBParams(Defaults.localDB, "genomeCAT", "localhost", "3306", "user", "user");

        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");

    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testEditSample() throws InterruptedException {

        try {
            // set logger
            Logger log = Logger.getLogger(SampleDetailView.class.getName()); 
            log.setLevel(Level.FINE);
            Handler[] handlers = Logger.getLogger("").getHandlers();
            boolean foundConsoleHandler = false;
            for (int index = 0; index < handlers.length; index++) {
                // set console handler to SEVERE
                if (handlers[index] instanceof ConsoleHandler) {
                    handlers[index].setLevel(Level.FINE);
                    //handlers[index].setFormatter(new LoggingSimpleFormatter());
                    foundConsoleHandler = true;
                }

            }
            if (!foundConsoleHandler) {
                // no console handler found
                System.err.println("No consoleHandler found, adding one.");
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.FINE);
                //consoleHandler.setFormatter(new LoggingSimpleFormatter());
                Logger.getLogger("").addHandler(consoleHandler);
            }
            //
            
            String name = "Test Sample 1";
            SampleDetail _d = ExperimentService.getSampleDetailByName(name);
            if (_d == null) {
                _d = new SampleDetail();
                _d.setName(name);
            }
            SampleDetail neu = SampleDetailView.SampleDetailViewDialog(_d, true);
            assertEquals("neu gleich alt:" , neu,_d);
            // while (true);


        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
}