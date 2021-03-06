package org.molgen.genomeCATPro.guimodul.experiment;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.cghpro.xport.ImportBAC;
import org.molgen.genomeCATPro.cghpro.xport.ImportExperimentGEO;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.guimodul.XPort.ImportExperimentBatch;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.molgen.genomeCATPro.cghpro.xport.ServiceXPort;
import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;
import org.molgen.genomeCATPro.xportagilent.ImportExperimentFileFETXT;

/**
 * @name TestCreateExperiment
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
public class TestExperimentImport {

    public TestExperimentImport() {
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

        DBService.setConnection("localhost", "3306", "genomecat", "test", "test");
        //xport = new ImportPlatformGEOBAC();

    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void testAgilentGSMBatch() throws InterruptedException {

        try {

            /*
             * String filetype,
             String filename,
             List<String[]> map,
             String platformname,
             String release,
             String type, String method,
             String sampleCy3,
             String sampleCy5
             */
            //String file = "/home/paula/GenomeCATProWORK/data/IRB_28_Cy_3_12878_I_F_4x05Frak_meDIP_Cy_5_Inp_2_3.txt";
            String file = "/home/paula/GenomeCATProWORK/data/test/test_GSM465717.txt";
            List<String[]> _map = new Vector<String[]>();
            //ID_REF	VALUE
            _map.add(new String[]{"ID_REF", "probeID"});
            _map.add(new String[]{"VALUE", "ratio"});

            ExperimentData neu = ImportExperimentBatch.importExperiment(
                    ImportExperimentGEO.geo,
                    file,
                    _map,
                    //"IRB_28_Cy_3_12878_I_F_4x05Frak_meDIP_Cy_5_Inp_2_3",
                    "test_GPL64__160316_125318498",
                    Defaults.GenomeRelease.hg18.toString(),
                    null, Defaults.Method.GE.toString(),
                    null, null);

            assertNotNull("empty Experiment", neu);
            assertEquals("Filename:", file, neu.getOriginalFile());

            assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + neu.getNofSpots(), neu.getNofSpots() == neu.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    @Ignore
    public void testAgilentFETXTBatch() throws InterruptedException {

        try {

            /*
             * String filetype,
             String filename,
             List<String[]> map,
             String platformname,
             String release,
             String type, String method,
             String sampleCy3,
             String sampleCy5
             */
            String file = "/home/paula/GenomeCATProWORK/data/test/test_AG0605_MCIp_Cy3_3h_DMSO_Cy5_0h_OB.txt";
            ExperimentData neu = ImportExperimentBatch.importExperiment(
                    ImportExperimentFileFETXT.fe,
                    file,
                    null, "test_AG060__160316_125803768",
                    Defaults.GenomeRelease.hg18.toString(),
                    null, null,
                    "testKTCy3", "testKTCy5");

            assertNotNull("empty Experiment", neu);
            assertEquals("Filename:", file, neu.getOriginalFile());

            assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + neu.getNofSpots(), neu.getNofSpots() == neu.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    @Ignore 
    // funktioniert nicht, das mapping schlägt fehl
    public void testAgilentFETXTBatchWOGENE() throws InterruptedException {

        try {

            String file = "/home/paula/GenomeCATProWORK/data/test/IRB_28_Cy_3_12878_I_F_4x05Frak_meDIP_Cy_5_Inp_2_3.txt";
            XPortExperimentFile importModul = ServiceXPort.getXPortImport(ImportExperimentFileFETXT.fe);
            importModul.newImportFile(file);
            Vector<Vector<String>> datafile = importModul.readData(100);
            List<String[]> mapping = importModul.getMappingFile2DBColNames();

            List<String[]> map = importModul.getMappingFile2DBColNames();

            String msg = importModul.setMappingFile2DBColNames(mapping);
            if (msg != null) {
                Logger.getLogger(ImportExperimentBatch.class.getName()).log(
                        Level.WARNING, msg);
            }

            ExperimentData neu = ImportExperimentBatch.importExperiment(
                    ImportExperimentFileFETXT.fe,
                    file,
                    map, "IRB_28_Cy_3_12878_I_F_4x05Frak_meDIP_Cy_5_Inp_2_3",
                    Defaults.GenomeRelease.hg19.toString(),
                    null, null,
                    "testKTCy3", "testKTCy5");

            assertNotNull("empty Experiment", neu);
            assertEquals("Filename:", file, neu.getOriginalFile());

            assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + neu.getNofSpots(), neu.getNofSpots() == neu.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    public void testAffyGSMBatchCNVWOGene() throws InterruptedException {

        try {

            /*
             * String filetype,
             String filename,
             List<String[]> map,
             String platformname,
             String release,
             String type, String method,
             String sampleCy3,
             String sampleCy5
             */
            String file = "/home/paula/GenomeCATProWORK/data/test/test_GSM696325_Affy.txt";
            List<String[]> _map = new Vector<String[]>();
            //ID_REF	VALUE
            _map.add(new String[]{"ID_REF", "probeID"});
            _map.add(new String[]{"VALUE", "ratio"});

            ExperimentData neu = ImportExperimentBatch.importExperiment(
                    ImportExperimentGEO.geo,
                    file,
                    _map,  "test_GPL6801-wogene",
                    //"test_GPL68__160316_125323358",
                    Defaults.GenomeRelease.hg17.toString(),
                    null, Defaults.Method.SNP.toString(),
                    "testKTCy3", "testKTCy5");

            assertNotNull("empty Experiment", neu);
            assertEquals("Filename:", file, neu.getOriginalFile());

            assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + neu.getNofSpots(), neu.getNofSpots() == neu.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    @Ignore
    public void testAffyGSMBatch() throws InterruptedException {

        try {

            /*
             * String filetype,
             String filename,
             List<String[]> map,
             String platformname,
             String release,
             String type, String method,
             String sampleCy3,
             String sampleCy5
             */
            String file = "/home/paula/GenomeCATProWORK/data/test/test_GSM390975_Affy.txt";
            List<String[]> _map = new Vector<String[]>();
            //ID_REF	VALUE
            _map.add(new String[]{"ID_REF", "probeID"});
            _map.add(new String[]{"VALUE", "ratio"});

            ExperimentData neu = ImportExperimentBatch.importExperiment(
                    ImportExperimentGEO.geo,
                    file,
                    _map,
                    "test_GPL68__160316_125323358",
                    Defaults.GenomeRelease.hg19.toString(),
                    null, Defaults.Method.SNP.toString(),
                    null, null);

            assertNotNull("empty Experiment", neu);
            assertEquals("Filename:", file, neu.getOriginalFile());

            assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + neu.getNofSpots(), neu.getNofSpots() == neu.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    @Ignore
    public void testBACBatch() throws InterruptedException {

        try {

            /*
             * String filetype,
             String filename,
             List<String[]> map,
             String platformname,
             String release,
             String type, String method,
             String sampleCy3,
             String sampleCy5
             */
            String file = "/project/Kopenhagen/Katrin/GenomeCATPro/test/test_MC_8169_8170_r_Cy3_2496_DMSOvital_Cy5_2496_DMSOapopt_S34_261008.gpr";
            //String file = "/project/H1N1/Array-CGH/Results/Neues_Set/new/MC_7195_7196_r_Cy3_JurkatIP_H3K9_Cy5_JurkatWCE_S4_50607.gpr";
            ExperimentData neu = ImportExperimentBatch.importExperiment(
                    ImportBAC.bac,
                    file,
                    null,
                    "test_gpl51__130507_124329476",
                    Defaults.GenomeRelease.hg18.toString(),
                    null, null,
                    "testKTCy3", "testKTCy5");

            assertNotNull("empty Experiment", neu);
            assertEquals("Filename:", file, neu.getOriginalFile());

            assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
            assertTrue("nofSpots " + neu.getNofSpots() + ") == noImportData (" + neu.getNofImportData(),
                    neu.getNofSpots() == neu.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

}
