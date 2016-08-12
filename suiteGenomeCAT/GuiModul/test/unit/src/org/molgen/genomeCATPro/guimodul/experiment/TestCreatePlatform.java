package org.molgen.genomeCATPro.guimodul.experiment;

import java.util.List;
import java.util.Vector;

import org.molgen.genomeCATPro.cghpro.xport.XPortPlatform;
import org.molgen.genomeCATPro.guimodul.platform.CreatePlatformDialog;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.cghpro.xport.ImportPlatformGEO;
import org.molgen.genomeCATPro.cghpro.xport.ImportPlatformGEOAffy;
import org.molgen.genomeCATPro.cghpro.xport.ImportPlatformGEOBAC;
import org.molgen.genomeCATPro.cghpro.xport.ImportPlatformGEOSOFT;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.xportagilent.ImportPlatformFETXT;


/**
 *
 * @author tebel
 */
public class TestCreatePlatform {

    XPortPlatform xport;

    public TestCreatePlatform() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        List<PlatformData> list = new Vector<PlatformData>();

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

        Database.setDBParams(Defaults.localDB, "genomecat", "localhost", "3306", "test", "test");

        DBService.setConnection("localhost", "3306", "genomecat", "test", "test");

    }

    @After
    public void tearDown() {
    }
    // select * from PlatformList  where created like '2013-03-13%';

    @Test
    public void testAgilentImportPlatformGEOBatch() throws InterruptedException {

        try {

            String file = "/home/paula/GenomeCATPro/data/test/test_GPL6480-26599.txt";
            PlatformData d = CreatePlatformDialog.batch(
                    ImportPlatformGEO.platform_geo_txt,
                    file,
                    Defaults.Method.GE.toString(), Defaults.Type.Oligo.toString(),
                    Defaults.GenomeRelease.hg18.toString(),
                    null,
                    "CHROMOSOMAL_LOCATION");

            assertNotNull("empty PlatformData", d);
            assertEquals("Filename:", file, d.getOriginalFile());

            assertEquals("method:", d.getPlattform().getMethod(), Defaults.Method.GE.toString());
            assertTrue("nofImportErrors:" + d.getNofImportErrors(), d.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + d.getNofSpots(), d.getNofSpots() == d.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestCreatePlatform.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    public void testAffyImportPlatformGEOBatch() throws InterruptedException {

        try {

            String file = "/home/paula/GenomeCATPro/data/test/test_GPL6801-4019.txt";

            /*List<String[]> _map = new Vector<String[]>();
             //ID	Chromosome	RANGE_GB	RANGE_START	RANGE_STOP	STRAND	Physical Position	SNP_ID	Allele A	Allele B
            
             _map.add(new String[]{"ID", "probeName"});
             _map.add(new String[]{"Chromosome", "chrom"});
             _map.add(new String[]{"RANGE_GB", "REFSEQ"});
             _map.add(new String[]{"SNP_ID", "Description"});
             _map.add(new String[]{"Physical Position", "chromStart"});
             _map.add(new String[]{"Physical Position", "chromEnd"});
             */
            PlatformData d = CreatePlatformDialog.batch(
                    ImportPlatformGEOAffy.platform_geo_affy_txt,
                    file,
                    Defaults.Method.SNP.toString(), Defaults.Type.Oligo.toString(),
                    Defaults.GenomeRelease.hg19.toString(),
                    null, null);

            assertNotNull("empty PlatformData", d);
            assertEquals("Filename:", file, d.getOriginalFile());

            assertEquals("method:", d.getPlattform().getMethod(), Defaults.Method.SNP.toString());
            assertTrue("nofImportErrors:" + d.getNofImportErrors(), d.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + d.getNofSpots(), d.getNofSpots() == d.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestCreatePlatform.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test

    public void testBACImportPlatformGEOBatch() throws InterruptedException {

        try {
            String file = "/home/paula/GenomeCATPro/data/test/test_gpl5114.txt";

            PlatformData d = CreatePlatformDialog.batch(
                    ImportPlatformGEOBAC.platform_geo_txt,
                    file,
                    Defaults.Method.aCGH.toString(), Defaults.Type.BAC.toString(),
                    Defaults.GenomeRelease.hg18.toString(),
                    null,
                    null);
            assertNotNull("empty PlatformData", d);
            assertEquals("Filename:", file, d.getOriginalFile());

            assertEquals("method:", d.getPlattform().getMethod(), Defaults.Method.aCGH.toString());

            assertTrue("nofImportErrors: " + d.getNofImportErrors(), d.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + d.getNofSpots(), d.getNofSpots() == d.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestCreatePlatform.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    //@Test
    // ok
    /**
     * public void testImportPlatformBACIDBatch() throws InterruptedException {
     *
     * try { String file =
     * "/project/Kopenhagen/Katrin/GenomeCATPro/test/test_bachg18.txt";
     *
     *
     * PlatformData d = CreatePlatformDialog.batch(
     * ImportPlatformBACID.platform_bac, file, Defaults.Method.aCGH.toString(),
     * Defaults.Type.BAC.toString(), Defaults.GenomeRelease.hg18.toString(),
     * null, null); assertNotNull("empty PlatformData", d);
     * assertEquals("Filename:", file, d.getOriginalFile());
     *
     * assertEquals("method:", d.getPlattform().getMethod(),
     * Defaults.Method.aCGH.toString());
     *
     * assertTrue("nofImportErrors: " + d.getNofImportErrors(),
     * d.getNofImportErrors() == 0); assertTrue("nofSpots != noImportData:" +
     * d.getNofSpots(), d.getNofSpots() == d.getNofImportData());
     *
     * } catch (Exception ex) {
     * Logger.getLogger(TestCreatePlatform.class.getName()).log(Level.SEVERE,
     * "ex: ", ex); } }*
     */
    @Test
    public void testAgilentImportPlatformGEOSOFTBatch() throws InterruptedException {

        try {
            String file = "/home/paula/GenomeCATPro/data/test/test_GPL6480_family.soft";

            PlatformData d = CreatePlatformDialog.batch(
                    ImportPlatformGEOSOFT.platform_geo_soft,
                    file,
                    Defaults.Method.GE.toString(), Defaults.Type.Oligo.toString(),
                    Defaults.GenomeRelease.hg18.toString(),
                    null,
                    "CHROMOSOMAL_LOCATION");
            assertNotNull("empty PlatformData", d);
            assertEquals("Filename:", file, d.getOriginalFile());

            assertEquals("method:", d.getPlattform().getMethod(), Defaults.Method.GE.toString());

            assertTrue("nofImportErrors: " + d.getNofImportErrors(), d.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + d.getNofSpots(), d.getNofSpots() == d.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestCreatePlatform.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    @Ignore
    public void testImportPlatformFETXTBatch() throws InterruptedException {

        try {
            String file = "/home/paula/GenomeCATPro/data/test/test_AG0605_MCIp_Cy3_3h_DMSO_Cy5_0h_OB.txt";

            PlatformData d = CreatePlatformDialog.batch(
                    ImportPlatformFETXT.platform_fe,
                    file,
                    Defaults.Method.ChIPChip.toString(), Defaults.Type.Oligo.toString(),
                    Defaults.GenomeRelease.hg18.toString(),
                    null,
                    "SystematicName");
            assertNotNull("empty PlatformData", d);
            assertEquals("Filename:", file, d.getOriginalFile());

            assertEquals("method:", d.getPlattform().getMethod(), Defaults.Method.ChIPChip.toString());
            //System.out.println("error: "+ error);

            assertTrue("nofImportErrors: " + d.getNofImportErrors(), d.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + d.getNofSpots(), d.getNofSpots() == d.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestCreatePlatform.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
}
