package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name ArrayViewBase
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
import java.sql.*;
import java.util.Vector;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;

/**
 * basic class to display array data implemtents some static factorylike methods
 * load data todo: for bac arrays there are more than one row per id
 */
public class ArrayViewBase {

    /**
     * factory to create new ArrayView from db
     */
    static ArrayView getView(ArrayData d,
            Vector<String> name, Vector start, Vector stop, Vector data, ChromTab chromtab) {
        ArrayView a;
        try {
            Class[] cargs = new Class[6];
            Object[] oargs = new Object[6];
            cargs[0] = ArrayData.class;
            oargs[0] = d;
            cargs[1] = Vector.class;
            oargs[1] = name;
            cargs[2] = Vector.class;
            oargs[2] = start;
            cargs[3] = Vector.class;
            oargs[3] = stop;
            cargs[4] = Vector.class;
            oargs[4] = data;
            cargs[5] = ChromTab.class;
            oargs[5] = chromtab;
            java.lang.reflect.Constructor c = d.getArrayClazz().getConstructor(cargs);
            a = (ArrayView) c.newInstance(oargs);
        } catch (Exception e) {
            Logger.getLogger(ArrayViewBase.class.getName()).log(Level.SEVERE,
                    "getView " + d.getArrayClazz(), e);

            throw new RuntimeException(e.getMessage());
        }
        return a;
    }

    /**
     * factory to create new ArrayView with given data
     */
    static ArrayView getView(ArrayData d,
            Vector name, Vector start, Vector stop, Vector data, Vector pValues, ChromTab chromtab) {
        ArrayView a;
        try {
            Class[] cargs = new Class[7];
            Object[] oargs = new Object[7];
            cargs[0] = ArrayData.class;
            oargs[0] = d;
            cargs[1] = Vector.class;
            oargs[1] = name;
            cargs[2] = Vector.class;
            oargs[2] = start;
            cargs[3] = Vector.class;
            oargs[3] = stop;
            cargs[4] = Vector.class;
            oargs[4] = data;
            cargs[5] = Vector.class;
            oargs[5] = pValues;
            cargs[6] = ChromTab.class;
            oargs[6] = chromtab;
            java.lang.reflect.Constructor c = d.getArrayClazz().getConstructor(cargs);
            a = (ArrayView) c.newInstance(oargs);
        } catch (Exception e) {
            Logger.getLogger(ArrayViewBase.class.getName()).log(Level.SEVERE,
                    "getView", e);
            throw new RuntimeException(e.getMessage());
        }
        return a;
    }

    /**
     * factory to create new ArrayView
     */
    static ArrayView getView(ArrayData d, ChromTab chromtab) throws Exception {
        ArrayView a;

        Class[] cargs = new Class[2];
        Object[] oargs = new Object[2];

        cargs[0] = ArrayData.class;
        oargs[0] = d;
        cargs[1] = ChromTab.class;
        oargs[1] = chromtab;
        java.lang.reflect.Constructor c = d.getArrayClazz().getConstructor(cargs);
        a = (ArrayView) c.newInstance(oargs);

        return a;
    }

    /**
     *
     * @param arrayId
     * @param chrom
     * @param start
     * @param stop
     * @param filename
     * @param cols
     */
    public static void exportArray(String arrayId, String chrom, int start, int stop,
            String filename, String[] cols) {

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
            throw new RuntimeException(e);
        }

        try {
            Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            Statement s = con.createStatement();
            String sql = "SELECT ";
            for (int i = 0; i < cols.length; i++) {
                if (i > 0) {
                    sql += " , ";
                }
                sql += cols[i];
            }
            sql += " FROM " + arrayId;
            if (chrom != null && start + stop > 0) {
                sql += " WHERE chrom = \'" + chrom + "\' and";
                sql += " chromStart BETWEEN " + start + " AND " + stop;
            }
            sql += " into outfile \'" + filename + "\' ";
            System.out.println(sql);
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static void initColorScaleRedGreen() {
        if (ArrayFrame.colorScale == null) {

            ArrayFrame.colorScale = new Color[256];
        }
        for (int i = 0; i < 128; i++) {
            //scale red from dark red to gray
            ArrayFrame.colorScale[i] = new Color(255 - i, i, i);
        }
        for (int i = 0; i < 128; i++) {
            //scale green from gray to dark green
            ArrayFrame.colorScale[128 + i] = new Color(128 - i, 128 + i, 128 - i);

        }

    }

    public static void initColorScaleYellowBlue() {
        if (ArrayFrame.colorScale == null) {

            ArrayFrame.colorScale = new Color[256];
        }
        for (int i = 0; i
                < 128; i++) {
            //scale red from blue to gray
            ArrayFrame.colorScale[i] = new Color(255 - i, 255 - i, i);
        }

        for (int i = 0; i
                < 128; i++) {
            //scale yellow to  gray 
            ArrayFrame.colorScale[128 + i] = new Color(128 - i, 128 - i, 128 + i);

        }
    }

    public static Color getColor(
            int i, boolean isRedGreen) {
        if (ArrayFrame.colorScale == null) {
            if (isRedGreen) {
                initColorScaleRedGreen();
            } else {
                initColorScaleYellowBlue();
            }

        }
        return ArrayFrame.colorScale[i];
    }

    public static void resetColorScale() {
        ArrayFrame.colorScale = null;
    }

    /**
     * creating the color scale
     *
     * @param img image to print
     */
    public static void paintColorScale(BufferedImage img, boolean isRedGreen, boolean horizontal) {
        int yy = 0;
        int xx = 0;
        if (horizontal) {
            yy = img.getWidth();
            xx
                    = img.getHeight();
        } else {
            yy = img.getHeight();
            xx
                    = img.getWidth();
        }

        double scale = (new Double(yy / 2) / new Double(255));
        //System.out.println(scale);
        Graphics2D g = img.createGraphics();
        if (isRedGreen) {
            initColorScaleRedGreen();
        } else {
            initColorScaleYellowBlue();
        }

        int y = 0;
        for (int j = 0; j
                < 255; j++) {

            // print farbscale
            g.setColor(ArrayFrame.colorScale[j]);
            y
                    = (int) Math.round((j) * 2 * scale);
            //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)-y));
            // x, y, w, h
            if (!horizontal) {
                g.fillRect(0, yy - y, xx, 1);
            } else {
                g.fillRect(y, 0, 1, img.getHeight());

                //g.fillRect(0, (Defines.ARRAY_HEIGTH / 2) + y, 10, 1);
                //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)+y));
            }

        }
    }

    /**
     * map color at ratio within a gradient
     *
     * @param ratio value for ratio
     * @param pos_max_y maximal value
     * @return adapted color 255-2*255 for positive ratios 0 - 255 for negative
     * ratios
     */
    public static int mapColorGradient(double ratio, double pos_max_y) {
        if (ratio < 0) {
            ratio *= -1;

            if (ratio >= pos_max_y) {
                return 0;
            }

            if (ratio == 0) {
                return 128;
            }

            return (int) (128 - (128 * (ratio) / pos_max_y));
        } else {
            if (ratio >= pos_max_y) {
                return 255;
            }

            if (ratio == 0) {
                return 128;
            }

            return (int) (128 + (128 * (ratio) / pos_max_y));
        }
    }
}
