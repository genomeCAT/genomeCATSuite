/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.experiment;

import org.molgen.genomeCATPro.guimodul.project.Project2ExperimentDialog;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.ProjectService;
import org.molgen.genomeCATPro.guimodul.project.ProjectView;

/**
 *
 * @author tebel
 */
public class ProjectViewTest {

    public ProjectViewTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    //@Test
    @SuppressWarnings("empty-statement")
    public void testListExperimentsForStudy() {


        try {
            Study s = ProjectService.getProjectById(new Long(8));
            List<ExperimentDetail> list = ProjectService.listExperimentsForProject(s);
            for (ExperimentDetail d : list) {
                System.out.println(d.toFullString());
            }


        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
    @Test

    @SuppressWarnings("empty-statement")
    public void testProjectView() {


        try {
            ProjectView p = new ProjectView(null,true);

            p.setVisible(true);
            while (true);
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    //@Test
    @SuppressWarnings("empty-statement")
    public void testFullQueryTree() {
        String project = "";
        String release = "hg18";
        String user = "ebert";
        String sample = "";

        try {
            List<Study> list = ProjectService.listProjectsWithFilter(
                    project, release, user, sample);


            for (Study s : list) {
                System.out.println(s.toFullString());
            }
            List<ExperimentDetail> list2 = ProjectService.listExperimentsWoProjectWithFilter(
                    release, user, sample);
            for (ExperimentDetail s : list2) {
                System.out.println(s.toFullString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    //@Test
    @SuppressWarnings("empty-statement")
    public void testAddExperiment2Project() throws InterruptedException {

        try {
            ExperimentDetail d = ExperimentService.getExperimentDetailByName("MC_3105_3106_Cy3_SCLC_Am_Cy5_SCLC_A_S61_51205");
            Project2ExperimentDialog.addExperiment2Project(d);
            while (true);
        } catch (Exception ex) {
            Logger.getLogger(ProjectViewTest.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
}