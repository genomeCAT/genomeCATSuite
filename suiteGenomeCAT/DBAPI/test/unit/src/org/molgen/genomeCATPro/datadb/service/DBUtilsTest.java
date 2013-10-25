/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.datadb.service;

import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import static org.junit.Assert.*;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;

/**
 *
 * @author tebel
 */
public class DBUtilsTest {

    public DBUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Database.setDBParams(Defaults.localDB, "genomeCAT", "localhost", "3306", "user", "user");

        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMedian method, of class DBUtils.
     */
    //@Test
    public void testGetMedian() throws Exception {
        System.out.println("getMedian");
        String tableName = "AG0572_Cy3__120215_020220526_hg18_Spots";
        String colratio = "ratio";
        double expResult = 0.0585296;
        double result = DBUtils.getMedian(tableName, colratio);
        assertEquals(expResult, result);
    //fail("The test case is a prototype.");
    }

    /**
     * Test of getQuantile method, of class DBUtils.
     */
    //@Test
    public void testGetQuantile() throws Exception {
        System.out.println("getQuantile");
        String tableName = "AG0572_Cy3__120215_020220526_hg18_Spots";
        String colratio = "ratio";

        double result_09 = DBUtils.getQuantile(tableName, colratio, 0.9);
        double result_01 = DBUtils.getQuantile(tableName, colratio, 0.1);
        assertEquals(result_01, result_09);
    // fail("0.1: " + result_01 + " 0.9: " + result_09);
    }
    //

    @Test
    public void testGetCols() throws Exception {

        String tableName = "AG0809_I90__120821_015959571_hg18_Spots";


        Vector<String> cols = DBUtils.getCols(tableName);
        System.out.println(cols);

    // fail("0.1: " + result_01 + " 0.9: " + result_09);
    }
    
    @Test
    public void testGetData() throws Exception {

        String tableName = "AG0809_I90__120821_015959571_hg18_Spots";


        Vector<Vector<String>> data = DBUtils.getData(-1, tableName);
        System.out.println(data);

    // fail("0.1: " + result_01 + " 0.9: " + result_09);
    }
    //@Test

    public void testAddPositionAtTable() throws Exception {

        String tableName = "AG0809_I90__120821_015959571_hg18_Spots";


        DBUtils.addPositionAtTable(tableName);

    // fail("0.1: " + result_01 + " 0.9: " + result_09);
    }

    /**
     * Test of getAllArrayMethods method, of class DBUtils.
     */
    // @Test
    public void testGetAllArrayMethods() {
        System.out.println("getAllArrayMethods");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getAllArrayMethods();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllDataTypes method, of class DBUtils.
     */
    // @Test
    public void testGetAllDataTypes() {
        System.out.println("getAllDataTypes");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getAllDataTypes();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllArrayTypes method, of class DBUtils.
     */
    // @Test
    public void testGetAllArrayTypes() {
        System.out.println("getAllArrayTypes");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getAllArrayTypes();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllArrayNames method, of class DBUtils.
     */
    // @Test
    public void testGetAllArrayNames() {
        System.out.println("getAllArrayNames");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getAllArrayNames();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllReleases method, of class DBUtils.
     */
    //@Test
    public void testGetAllReleases() {
        System.out.println("getAllReleases");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getAllReleases();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAnnoTableForRelease method, of class DBUtils.
     */
    // @Test
    public void testGetAnnoTableForRelease() {
        System.out.println("getAnnoTableForRelease");
        String table = "";
        GenomeRelease release = null;
        String expResult = "";
        String result = DBUtils.getAnnoTableForRelease(table, release);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStudies method, of class DBUtils.
     */
    //@Test
    public void testGetStudies() {
        System.out.println("getStudies");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getStudies();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllUser method, of class DBUtils.
     */
    // @Test
    public void testGetAllUser() {
        System.out.println("getAllUser");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getAllUser();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSamples method, of class DBUtils.
     */
    // @Test
    public void testGetAllSamples() {
        System.out.println("getAllSamples");
        Vector<String> expResult = null;
        Vector<String> result = DBUtils.getAllSamples();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }
}