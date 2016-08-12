package org.molgen.genomeCATPro.common;

/**
 * @name Utils.java
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @author Wei Chen
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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import org.openide.modules.InstalledFileLocator;

/**
 * 090712 kt getRCommand relocate R batch File 310712 kt new getRCMD run batch
 * with no interactions
 *
 */
public class Utils {

    public static String getUniquableName(String oldName) {
        return new String(oldName.substring(0, (oldName.length() > 10 ? 10 : oldName.length()))
                + "_" + "_" + new SimpleDateFormat("yyMMdd_hhmmssSSS").format(new Date()));

    }

    /**
     * convert int from 0 - .. to alpha
     *
     * @param no
     * @return
     */
    public static String intToAlpha(Integer no) {
        if (no > 25) {
            return intToAlpha(no - 26);
            //for(int i=65;i<=91;i++){
        }
        return String.valueOf((char) (65 + no));
    }
    /**
     * compare doubles, considering NaN
     */
    public static final Comparator<Double> DoubleMaxComparator = new Comparator<Double>() {

        public int compare(Double d1, Double d2) {

            if (Double.isNaN((Double) d1)) {
                return -1;
            }
            if (Double.isNaN((Double) d2)) {
                return 1;
            }
            return Double.compare((Double) d1, (Double) d2);
        }
    };
    /**
     * compare doubles, considering NaN
     */
    public static final Comparator<Double> DoubleMinComparator = new Comparator<Double>() {

        public int compare(Double d1, Double d2) {

            if (Double.isNaN((Double) d1)) {
                return 1;
            }
            if (Double.isNaN((Double) d2)) {
                return -1;
            }
            return Double.compare((Double) d1, (Double) d2);
        }
    };

    /**
     * Get the extension of a file.
     *
     * @param f input file
     * @return the extension of the file
     *
     */
    public static String getExtension(
            File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }

    /**
     * Transform a boolean value to integer,true to 1 and false to 0
     *
     * @param x the boolean value
     * @return the integer value
     */
    public static int getIntValue(boolean x) {

        if (x) {
            return 1;
        }

        return 0;
    }

    /**
     * Transform the name of human chromosome to an integer value, x to 23,y to
     * 24 and the inconsistant value to 0.
     *
     * @param chr the name of chromosome
     * @return the integer value
     */
    public static void deleteFile(String filename) {
        try {
            // make shure that the file not existing for all
            String os = System.getProperty("os.name");
            String command;

            if (os == null || os.toLowerCase().startsWith("windows")) {
                command = "del " + filename;
            } else {
                command = "rm -f " + filename;
            }
//Runtime.getRuntime().exec(command);

            Process p = Runtime.getRuntime().exec(command);
            try {
                p.waitFor();
            } catch (java.lang.InterruptedException e) {
                e.printStackTrace();
                throw (new RuntimeException(e));
            }

        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }

    }

    public static final void makeFileAcessible(String tmpFile) {
        try {

            // make shure that the file is readable for all
            String os = System.getProperty("os.name");
            String command;

            if (os == null || os.toLowerCase().startsWith("windows")) {
                //pathOutFile.replace(":", ":\\");
                command = "attrib -r " + tmpFile;
            } else {
                command = "chmod 777 " + tmpFile;
            }

            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static String getRoot(
            Class c) throws IOException, URISyntaxException {
        URL u = c.getProtectionDomain().getCodeSource().getLocation();
        File f = new File(u.toURI());
        return f.getParent();
    }

    public static boolean isWin() {

        String os = System.getProperty("os.name");
        if (os == null || os.toLowerCase().startsWith("windows")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * get R as BATCH (run batch with no interactions for windows)
     */
    public static String[] getRCMD(String datafile) {
        //String[] command;

        String os = System.getProperty("os.name");
        if (os == null || os.toLowerCase().startsWith("windows")) {
            File f = InstalledFileLocator.getDefault().locate(
                    Defaults.batchfileCMD + ".cmd", "org.molgen.genomeCATPro.appconf", false);
            String[] command = {"cmd", "/c", f.getPath(), datafile};
            //String[] command = {"cmd", "/c",
            //		new String(MainFrame.Rbatch+"Rterm"), "--no-restore","--no-save", new String(" < "+filename)};
            return command;
        } else {
            File f = InstalledFileLocator.getDefault().locate(
                    Defaults.batchfile + ".sh", "org.molgen.genomeCATPro.appconf", false);

            String[] command = {"sh", f.getPath(), datafile};
            return command;
        }
//command += new String(MainFrame.Rbatch + " " );
//System.out.println(command);
//return command;

    }

    /**
     * set command line for Runtime.exec() depending on operation system R_BATCH
     * is set in chg.properties and loaded with programm start R_BATCH contains
     * absolute pathname to batch file inside the batchfile R batch is called
     */
    public static String intToRoman(
            int i) {
        if (i > 50) {
            return intToRoman(i - 50);
        }

        String roman = "";

        if (i >= 10) {
            for (int j = (int) Math.ceil(i / 10); j
                    > 0; j--) {
                i -= 10;
                roman
                        += "X";
            }

        }
        if (i == 9) {
            return (roman += "IX");
        }

        if (i >= 5) {
            roman += "V";
            i -= 5;

            for (int d = i; d
                    > 5; d--) {
                i -= 1;
                roman
                        += "I";
            }

        }
        if (i == 4) {
            return (roman += "IV");
        }
        for (int d = i; d
                > 0; d--) {
            roman += "I";
        }

        return roman;
    }

    public static String[] getRCommand(String datafile) {
        //String[] command;

        String os = System.getProperty("os.name");
        if (os == null || os.toLowerCase().startsWith("windows")) {
            File f = InstalledFileLocator.getDefault().locate(
                    Defaults.batchfile + ".cmd", "org.molgen.genomeCATPro.appconf", false);
            String[] command = {"cmd", "/c", f.getPath(), datafile};
            //String[] command = {"cmd", "/c",
            //		new String(MainFrame.Rbatch+"Rterm"), "--no-restore","--no-save", new String(" < "+filename)};
            return command;
        } else {
            File f = InstalledFileLocator.getDefault().locate(
                    Defaults.batchfile + ".sh", "org.molgen.genomeCATPro.appconf", false);
            //String[] command = {new String(MainFrame.Rbatch+"R"), new String("--no-save <"+filename)};
            String[] command = {"sh", f.getPath(), datafile};
            return command;
        }
//command += new String(MainFrame.Rbatch + " " );
//System.out.println(command);
//return command;

    }
    public static final Comparator<String> orderChroms = new Comparator<String>() {

        public int compare(String c1, String c2) {

            if (c1.matches("chr\\d+") && c2.matches("chr\\d+")) {

                Integer i1 = (new Integer(c1.substring(3)));
                Integer i2 = (new Integer(c2.substring(3)));
                return i1.compareTo(i2);
            } else if (c1.matches("chr[xyXY]") && c2.matches("chr[xyXY]")) {
                return c1.compareToIgnoreCase(c2);
            } else if (c1.matches("chr\\d+")
                    && c2.matches("chr[xyXY]")) {
                return -1;
            } else if (c2.matches("chr\\d+") && c1.matches("chr[xyXY]")) {
                return +1;
            }

            return 0;
        }
    };
}
