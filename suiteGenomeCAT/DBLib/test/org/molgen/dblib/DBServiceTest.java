/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.dblib;

import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tebel
 */
public class DBServiceTest {

    public DBServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
     * Test of setConnection method, of class DBService.
     */
    @Test
    public void testSetConnection() {
        System.out.println("TEST setConnection");
        String host = "localhost";
        String port = "3306";
        String db = "genomeCAT";
        String user = "user";
        String pwd = "user";
        Boolean result = DBService.setConnection(host, port, db, user, pwd);


        assertTrue("connection failed", result);
    }

    /**
     * Test of testConnection method, of class DBService.
     */
    @Test
    public void testTestConnection() {
        System.out.println("TEST testConnection");
        String host = "localhost";
        String port = "3307";
        String db = "genomeCAT";
        String user = "user";
        String pwd = "user";

        boolean result = DBService.testConnection(host, port, db, user, pwd);
        assertTrue("new connection failed", result);
        result = DBService.testConnection("cnvcatPU", host, port, db, user, pwd);
        assertTrue("new connection failed", result);


    }

    @Test
    public void restoreConnection() {
        System.out.println("TEST Restore Connection with " + DBService.properties.values().toString());
        EntityManager em = DBService.getEntityManger();

        assertTrue("restore connection failed", em != null);
    }
}