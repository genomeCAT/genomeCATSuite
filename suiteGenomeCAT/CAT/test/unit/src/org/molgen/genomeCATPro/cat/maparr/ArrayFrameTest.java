package org.molgen.genomeCATPro.cat.maparr;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.GeneImpl;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 *
 * @author tebel
 */
public class ArrayFrameTest {

    public ArrayFrameTest() {
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
    //@Test

    @SuppressWarnings("empty-statement")
    public void testExperimentDetailViewWithRelease() {



        Data[] list = FilterExperimentsDialog.getDataList(Defaults.GenomeRelease.hg18);



        while (true);
    }
    //@Test

    @SuppressWarnings("empty-statement")
    public void testExperimentDetailView() {



        Data[] list = FilterExperimentsDialog.getDataList(null);

        ArrayData[] adList = ChooseArrayViewDialog.getArrayDataList(list);

        while (true);
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testArrayFrame() {
        JFrame p = new JFrame("test");


        try {



            ArrayFrame f = new ArrayFrame();
            

            f.addAnno(Defaults.GenomeRelease.hg18.toString(), GeneImpl.nameId);
            p.add(f);
            p.setVisible(true);
            while (true);
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}