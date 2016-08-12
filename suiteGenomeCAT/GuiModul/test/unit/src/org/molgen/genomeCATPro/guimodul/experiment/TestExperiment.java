package org.molgen.genomeCATPro.guimodul.experiment;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.cghpro.chip.LowessLib;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.guimodul.XPort.ImportFileWizardAction;

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
public class TestExperiment {

    public TestExperiment() {
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

    //@Test
    @SuppressWarnings("empty-statement")
    public void testCreateExperiment() throws InterruptedException {

        try {
            ImportFileWizardAction.doImport();
            while (true);
        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @SuppressWarnings("empty-statement")
    public void testDeleteExperiment() throws InterruptedException {
        try {
            ExperimentData e = ExperimentService.getExperimentByDataId((long) 482);
            ExperimentService.deleteExperimentData(e, null);
        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }

    }
    //@Test

    @SuppressWarnings("empty-statement")
    //@Test
    public void testDeleteExperimentDetail() throws InterruptedException {
        try {
            ExperimentDetail e = ExperimentService.getExperimentByDetailId((long) 317);
            ExperimentService.deleteExperimentDetail(e, null);
        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }

    }
    //@Test

    @SuppressWarnings("empty-statement")
    public void testExportData() throws InterruptedException {

        try {

            Data data = ExperimentService.getExperimentByDataId((long) 350);
            ExportDataDialog.exportData(data);
            while (true);
        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
    //@Test

    public void testConvert() throws InterruptedException {

        try {
            ExperimentData d = ExperimentService.getExperimentByDataId((long) 591);
            ConvertExperimentDialog.convertExperiment(d);
        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }

    @Test
    public void testNormalizeBatch() throws InterruptedException {
        int i = 0;

        try {
            //System.setProperty("user.name", "user");
            EntityManager em = DBService.getEntityManger();


            /*select * from ExperimentDetail as e, ExperimentAtStudy as es, Study as s 
             where s.name = 'tumor_cell_lines' and s.studyID = es.studyID and 
             es.experimentDetailID = e.experimentDetailID  
             and  not exists (
             select d.* from  ExperimentList as d  where e.experimentDetailID = d.experimentDetailID and d.genomeRelease = 'hg18:NCBI36:Mar2006'
             and dataType="unprocessed" );*/
            Query query = em.createQuery(
                    "select e from  ExperimentAtStudy  es, "
                    + " ExperimentDetail e , Study s where s.name = ?1 "
                    + "and s.studyID = es.studyID  "
                    + "and e.experimentDetailID = es.experimentDetailID "
                    + " and not exists ( "
                    + " select d from ExperimentData d where "
                    + " d.experiment.experimentDetailID = e.experimentDetailID and "
                    + " d.genomeRelease = ?2 and "
                    + " d.dataType = ?3) ");

            query.setParameter(1, "tumor_cell_lines");
            query.setParameter(2, "hg18:NCBI36:Mar2006");
            query.setParameter(3, "normalized");

            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "anzahl: {0}", query.getResultList().size());
            List<ExperimentDetail> results;
            results = query.getResultList();
            for (ExperimentDetail ed : results) {
                System.out.println(ed.getId());
                ExperimentData d = ExperimentService.getExperimentData(ed.getName(), Defaults.GenomeRelease.hg18.toString());
                if (d == null) {
                    continue;
                }
                System.out.println(d.toFullString());
                if (NormalizeDialog.batch(d, LowessLib.methodName)) {
                    //ExperimentData dneu = ExperimentService.getExperimentData(d1.getName(), Defaults.GenomeRelease.hg18.toString());
                    //System.out.println(dneu.toFullString());
                    i++;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
        System.out.println("converted: " + i);
    }

    public void testConvertBatch() throws InterruptedException {
        int i = 0;

        try {
            //System.setProperty("user.name", "user");
            EntityManager em = DBService.getEntityManger();


            /*select * from ExperimentDetail as e, ExperimentAtStudy as es, Study as s 
             where s.name = 'tumor_cell_lines' and s.studyID = es.studyID and 
             es.experimentDetailID = e.experimentDetailID  
             and  not exists (
             select d.* from  ExperimentList as d  where e.experimentDetailID = d.experimentDetailID and d.genomeRelease = 'hg18:NCBI36:Mar2006'
             and dataType="unprocessed" );*/
            Query query = em.createQuery(
                    "select e from  ExperimentAtStudy  es, "
                    + " ExperimentDetail e , Study s where s.name = ?1 "
                    + "and s.studyID = es.studyID  "
                    + "and e.experimentDetailID = es.experimentDetailID "
                    + " and not exists ( "
                    + " select d from ExperimentData d where "
                    + " d.experiment.experimentDetailID = e.experimentDetailID and "
                    + " d.genomeRelease = ?2 ) ");

            query.setParameter(1, "tumor_cell_lines");
            query.setParameter(2, "hg18:NCBI36:Mar2006");

            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "anzahl: {0}", query.getResultList().size());
            List<ExperimentDetail> results;
            results = (List<ExperimentDetail>) query.getResultList();
            for (ExperimentDetail d1 : results) {
                System.out.println(d1.getId());
                ExperimentData dalt = ExperimentService.getExperimentData(d1.getName(), Defaults.GenomeRelease.hg17.toString());
                if (dalt == null) {
                    continue;
                }
                System.out.println(dalt.toFullString());
                if (ConvertExperimentDialog.batch(dalt, Defaults.GenomeRelease.hg18)) {
                    ExperimentData dneu = ExperimentService.getExperimentData(d1.getName(), Defaults.GenomeRelease.hg18.toString());
                    System.out.println(dneu.toFullString());
                    i++;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
        System.out.println("converted: " + i);
    }

    //@Test
    public void testDeleteBatch() throws InterruptedException {
        int i = 0;

        try {
            System.setProperty("user.name", "user");
            EntityManager em = DBService.getEntityManger();

            Query query = em.createQuery(
                    "select d from  ExperimentAtStudy  es, "
                    + " ExperimentData d , Study s where s.name = ?1 "
                    + "and s.studyID = es.studyID  "
                    + "and d.experiment.experimentDetailID = es.experimentDetailID "
                    + "and d.genomeRelease = ?2 and "
                    + " d.created  < ?3");
            query.setParameter(1, "tumor_cell_lines");
            query.setParameter(2, "hg18:NCBI36:Mar2006");
            java.sql.Date dddate = java.sql.Date.valueOf("2012-10-17");
            query.setParameter(3, dddate);

            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "anzahl: {0}", query.getResultList().size());
            List<ExperimentData> results;
            results = (List<ExperimentData>) query.getResultList();
            for (ExperimentData d1 : results) {
                System.out.println(d1.getId());
                try {
                    ExperimentService.deleteExperimentData(d1, null);
                    i++;
                } catch (Error exception) {
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
        System.out.println("deleted: " + i);
    }
    //  @Test

    public void testTidyUp() throws InterruptedException {

        try {
            ExperimentData d = ExperimentService.getExperimentByDataId((long) 69);
            //DataManager.tidyUp(2);
            DataManager.tidyUp(d);
        } catch (Exception ex) {
            Logger.getLogger(TestExperiment.class.getName()).log(Level.SEVERE, "ex: ", ex);
        }
    }
}
