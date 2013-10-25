/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.common;

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
public class UtilsTest {

    public UtilsTest() {
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

    @Test
    public void testIntToAlpha() {
        String result = Utils.intToAlpha(1);
        assertTrue("1 -> " + result + " : ", result.contentEquals("B"));
        result = Utils.intToAlpha(0);
        assertTrue("0 -> " + result + " : ", result.contentEquals("A"));
        result = Utils.intToAlpha(25);
        assertTrue("25 -> " + result + " : ", result.contentEquals("Z"));       
        result = Utils.intToAlpha(26);
        assertTrue("26 -> " + result + " : ", result.contentEquals("A"));
        result = Utils.intToAlpha(27);
        assertTrue("27 -> " + result + " : ", result.contentEquals("B"));
         result = Utils.intToAlpha(77);
        assertTrue("77 -> " + result + " : ", result.contentEquals(
                String.valueOf((char) (65 + 77-26-26))));
    }
    //@Test

    public void testIntToRoman() {


        String result = Utils.intToRoman(1);
        assertTrue("1: ", result.contentEquals("I"));
        result = Utils.intToRoman(2);
        assertTrue("2: ", result.contentEquals("II"));
        result = Utils.intToRoman(3);
        assertTrue("3: ", result.contentEquals("III"));
        result = Utils.intToRoman(4);
        assertTrue("4: " + result, result.contentEquals("IV"));
        result = Utils.intToRoman(5);
        assertTrue("5: ", result.contentEquals("V"));
        result = Utils.intToRoman(6);
        assertTrue("6: ", result.contentEquals("VI"));
        result = Utils.intToRoman(7);
        assertTrue("7: ", result.contentEquals("VII"));
        result = Utils.intToRoman(8);
        assertTrue("8: " + result, result.contentEquals("VIII"));
        result = Utils.intToRoman(9);
        assertTrue("9: ", result.contentEquals("IX"));
        result = Utils.intToRoman(10);
        assertTrue("10: " + result, result.contentEquals("X"));
        result = Utils.intToRoman(11);
        assertTrue("11: ", result.contentEquals("XI"));
        result = Utils.intToRoman(12);
        assertTrue("12: ", result.contentEquals("XII"));
        result = Utils.intToRoman(13);
        assertTrue("13: ", result.contentEquals("XIII"));
        result = Utils.intToRoman(14);
        assertTrue("14: " + result, result.contentEquals("XIV"));
        result = Utils.intToRoman(15);
        assertTrue("15: ", result.contentEquals("XV"));
        result = Utils.intToRoman(16);
        assertTrue("16: ", result.contentEquals("XVI"));
        result = Utils.intToRoman(17);
        assertTrue("17: ", result.contentEquals("XVII"));
        result = Utils.intToRoman(18);
        assertTrue("18: " + result, result.contentEquals("XVIII"));
        result = Utils.intToRoman(19);
        assertTrue("19: ", result.contentEquals("XIX"));
        result = Utils.intToRoman(20);
        assertTrue("20: " + result, result.contentEquals("XX"));
        result = Utils.intToRoman(21);
        assertTrue("21: " + result, result.contentEquals("XXI"));
        result = Utils.intToRoman(25);
        assertTrue("25: " + result, result.contentEquals("XXV"));
        result = Utils.intToRoman(39);
        assertTrue("39: " + result, result.contentEquals("XXXIX"));

        result = Utils.intToRoman(89);
        assertTrue("89: " + result, result.contentEquals("XXXIX"));

    }
}