package org.molgen.genomeCATPro.guimodul.experiment;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.cghpro.xport.ImportTrack;
import org.molgen.genomeCATPro.cghpro.xport.ImportTrackAffy;
import org.molgen.genomeCATPro.cghpro.xport.ImportTrackWIG;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.molgen.genomeCATPro.guimodul.track.ImportTrackDialog;
import static org.junit.Assert.*;

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
public class TestTrack {

    public TestTrack() {
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
    //@Ignore
    public void testBEDBatch() throws InterruptedException {

        try {

            /*batch(String filetype, 
             String filename, 
             String release,
             boolean hasHeader,
             SampleDetail s1, SampleDetail s2, String project, String splitPos) {
             */
            String file = "/home/paula/GenomeCATPro/data/test/test_OLUSC_Subt_5_120619_035025529_hg18_Spots.bed";
            Track t = ImportTrackDialog.batch(
                    ImportTrack.track_bedgraph_txt,
                    file,
                    Defaults.GenomeRelease.hg18.toString(),
                    true,
                    null, null, "test", null);

            assertNotNull("empty Track", t);
            assertEquals("Filename:", file, t.getOriginalFile());

            assertTrue("nofImportErrors:" + t.getNofImportErrors(), t.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + t.getNofPeaks(), t.getNofPeaks() == t.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    //@Ignore
    public void testWIGFixedBatch() throws InterruptedException {

        try {

            /*batch(String filetype, 
             String filename, 
             String release,
             boolean hasHeader,
             SampleDetail s1, SampleDetail s2, String project, String splitPos) {
             */
            String file = "/home/paula/GenomeCATPro/data/test/test_wig_bin_1000.txt";
            Track t = ImportTrackDialog.batch(
                    ImportTrackWIG.track_wig_txt,
                    file,
                    Defaults.GenomeRelease.hg18.toString(),
                    false,
                    null, null, "test", null);

            assertNotNull("empty Track", t);
            assertEquals("Filename:", file, t.getOriginalFile());

            assertTrue("nofImportErrors:" + t.getNofImportErrors(), t.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + t.getNofPeaks(), t.getNofPeaks() == t.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    //@Ignore
    public void testWIGVariableBatch() throws InterruptedException {

        try {

            /*batch(String filetype, 
             String filename, 
             String release,
             boolean hasHeader,
             SampleDetail s1, SampleDetail s2, String project, String splitPos) {
             */
            String file = "/home/paula/GenomeCATPro/data/test/ucsc_laminb1_wig.txt";
            Track t = ImportTrackDialog.batch(
                    ImportTrackWIG.track_wig_txt,
                    file,
                    Defaults.GenomeRelease.hg18.toString(),
                    false,
                    null, null, "test", null);

            assertNotNull("empty Track", t);
            assertEquals("Filename:", file, t.getOriginalFile());

            assertTrue("nofImportErrors:" + t.getNofImportErrors(), t.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + t.getNofPeaks(), t.getNofPeaks() == t.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    public void testAffyBatch() throws InterruptedException {

        try {

            /*batch(String filetype, 
             String filename, 
             String release,
             boolean hasHeader,
             SampleDetail s1, SampleDetail s2, String project, String splitPos) {
             */
            String file = "/home/paula/GenomeCATPro/data/test/test_GSM696325_Affy.txt";
            Track t = ImportTrackDialog.batch(
                    ImportTrackAffy.track_affy_cnv_txt,
                    file,
                    Defaults.GenomeRelease.hg18.toString(),
                    true,
                    null, null, "test", null);

            assertNotNull("empty Track", t);
            assertEquals("Filename:", file, t.getOriginalFile());

            assertTrue("nofImportErrors:" + t.getNofImportErrors(), t.getNofImportErrors() == 0);
            assertTrue("nofSpots != noImportData:" + t.getNofPeaks(), t.getNofPeaks() == t.getNofImportData());

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    @Ignore

    @SuppressWarnings("empty-statement")
    public void testCreateTrack() throws InterruptedException {

        try {

            ImportTrackDialog dialog = new ImportTrackDialog(new JFrame());
            dialog.setImportFile("/home/paula/GenomeCATPro/data/test/bed_track.bed",
                    ImportTrack.track_bedgraph_txt);

            dialog.setVisible(true);
            while (true);
        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    //@Test
    public void testWIGPattern() throws InterruptedException {

        try {

            Pattern wigpattern;
            Matcher wigmatcher;
            wigpattern = Pattern.compile(
                    "fixedStep\\s+chrom=(\\w+)(?:\\s*start=)(\\d+)(?:\\s*step=(\\d+))?(?:\\s*span=(\\d+))?",
                    //"fixedStep\\s+chrom=(\\w)\\s+(start=(\\d+))\\s*(step=(\\d+))",
                    //.*(span=(\\d+))?",
                    Pattern.CASE_INSENSITIVE);

            //[]
            //String line = "fixedStep chrom=chr1     start=10  \t  step=100     span=1000";
            String line = "fixedStep chrom=chr1 start=10 step=1000";
            wigmatcher = wigpattern.matcher(line);
            if (wigmatcher.find()) {
                System.out.println(wigmatcher.group(0));

                System.out.println("Group1: " + wigmatcher.group(1));

                System.out.println("Group2: " + wigmatcher.group(2));

                if (wigmatcher.groupCount() >= 3) {
                    System.out.println("Group3: " + wigmatcher.group(3));
                }
                if (wigmatcher.groupCount() >= 4) {
                    System.out.println("Group4: " + wigmatcher.group(4));
                }
            } else {
                org.junit.Assert.fail("no match");
            }

        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @SuppressWarnings("empty-statement")

    public void testCreateWIGTrack() throws InterruptedException {

        try {

            ImportTrackDialog dialog = new ImportTrackDialog(new JFrame());
            dialog.setImportFile("/home/paula/GenomeCATPro/data/test/test_wig_bin_1000.txt",
                    ImportTrackWIG.track_wig_txt);

            dialog.setVisible(true);
            while (true);
        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    //@Test
    @SuppressWarnings("empty-statement")
    public void testDeleteTrack() throws InterruptedException {
        try {
            Track t = TrackService.getTrackById((long) 239);
            TrackService.deleteTrack(t, true, null);
        } catch (Exception ex) {
            Logger.getLogger(TestTrack.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }

    }
    //@Test
}
