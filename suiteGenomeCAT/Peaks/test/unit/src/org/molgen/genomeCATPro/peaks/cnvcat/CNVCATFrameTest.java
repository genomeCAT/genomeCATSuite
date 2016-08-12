/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks.cnvcat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

/**
 *
 * @author tebel
 */
public class CNVCATFrameTest {

    public CNVCATFrameTest() {
    }

    @Before
    public void setUp() {
        Database.setDBParams(Defaults.localDB, "genomeCAT", "localhost", "3306", "user", "user");

        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");
        //xport = new ImportPlatformGEOBAC();

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testRunPeaks() {
        JFrame p = new JFrame("test");

        JPanel ab = new CNVCATFrame(null);
        p.getContentPane().add(ab);
        p.setEnabled(true);
        p.setVisible(true);

        while (true);
    }
}
