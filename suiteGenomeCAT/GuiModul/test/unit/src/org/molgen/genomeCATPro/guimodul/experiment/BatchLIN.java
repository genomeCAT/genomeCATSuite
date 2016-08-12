package org.molgen.genomeCATPro.guimodul.experiment;

import java.io.File;
import java.io.FilenameFilter;
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
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.guimodul.XPort.ImportExperimentBatch;
import static org.junit.Assert.*;

/**
 * @name BatchLIN
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
public class BatchLIN {

    public BatchLIN() {
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
        //xport = new ImportPlatformGEOBAC();

    }

    @After
    public void tearDown() {
    }

    //@Test
    public void testBACBatchLIN() throws InterruptedException {

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
            String[] files = {
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454617_MC_6671_6672_r_Cy3_2LIN121801_99_Cy5_fpool_S28_190906.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454646_MC_6103_6104_r_Cy3_LIN24519_03_Cy5_291_S74_201106.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454647_MC_6677_6678_r_Cy3_LIN123549_03_Cy5_fpool_S31_190906.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454648_MC_6673_6674_r_Cy3_9LIN1921_01_Cy5_fpool_S29_190906.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454649_MC_6653_6654_r_Cy3_10LIN46998_01_Cy5_fpool_S20_190906.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454650_MC_6625_6626_r_Cy3_7LIN123644_01_Cy5_fpool_S69_120207.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454651_MC_6651_6652_r_Cy3_8LIN55099_01_Cy5_fpool_S19_190906.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454652_MC_6675_6676_r_Cy3_13LIN69092_03_Cy5_fpool_S30_190906.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454653_MC_7117_7118_r_Cy3_15LIN_83119_99_Cy5_fpool_S84_140507.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454654_MC_7077_7078_r_Cy3_17LIN_3976_07_Cy5_fpool_S59_140507.gpr",
                "/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN/GSM454655_MC_7079_7080_r_Cy3_22LIN_9203_06_Cy5_fpool_S61_140507.gpr"
            };
            for (String file : files) {
                //String file = "/project/H1N1/Array-CGH/Results/Neues_Set/new/MC_7195_7196_r_Cy3_JurkatIP_H3K9_Cy5_JurkatWCE_S4_50607.gpr";
                ExperimentData neu = ImportExperimentBatch.importExperiment(
                        ImportBAC.bac,
                        file,
                        null,
                        "gpl5114",
                        Defaults.GenomeRelease.hg17.toString(),
                        null, null,
                        null, null);
                ExperimentDetail dneu = neu.getExperiment();

                assertNotNull("empty Experiment", neu);
                assertEquals("Filename:", file, neu.getOriginalFile());

                assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
                assertTrue("nofSpots " + neu.getNofSpots() + ") == noImportData (" + neu.getNofImportData(),
                        neu.getNofSpots() == neu.getNofImportData());
                DataManager.addExperiment2Project("LIN_KT", dneu);

            }
        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    public void testBACBatchLIN3() throws InterruptedException {

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
            File f = new File("/project/Kopenhagen/Katrin/GenomeCATPro/manual/data/LIN3"); // current directory

            File[] files = f.listFiles(
                    new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".gpr");
                }
            });

            for (File file : files) {
                //String file = "/project/H1N1/Array-CGH/Results/Neues_Set/new/MC_7195_7196_r_Cy3_JurkatIP_H3K9_Cy5_JurkatWCE_S4_50607.gpr";
                ExperimentData neu = ImportExperimentBatch.importExperiment(
                        ImportBAC.bac,
                        file.getPath(),
                        null,
                        "gpl5114",
                        Defaults.GenomeRelease.hg17.toString(),
                        null, null,
                        null, null);
                ExperimentDetail dneu = neu.getExperiment();

                assertNotNull("empty Experiment", neu);
                assertEquals("Filename:", file.getPath(), neu.getOriginalFile());

                assertTrue("nofImportErrors:" + neu.getNofImportErrors(), neu.getNofImportErrors() == 0);
                assertTrue("nofSpots " + neu.getNofSpots() + ") == noImportData (" + neu.getNofImportData(),
                        neu.getNofSpots() == neu.getNofImportData());
                DataManager.addExperiment2Project("LIN_KT", dneu);

            }
        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
}
