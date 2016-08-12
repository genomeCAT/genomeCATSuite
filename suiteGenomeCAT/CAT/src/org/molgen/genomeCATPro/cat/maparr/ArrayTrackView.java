package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name ArrayTrackView
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
import java.awt.Color;
import java.sql.*;
import java.awt.Graphics;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;

/**
 * class to display array data based CBS
 *
 */
public class ArrayTrackView extends ArrayView {

    public ArrayTrackView() {
        super();
    }

    public ArrayTrackView(ArrayData d, ChromTab chromtab) throws Exception {
        super(d, chromtab);
    }

    public ArrayTrackView(ArrayData d,
            Vector<String> names, Vector<Long> start, Vector<Long> stop,
            Vector<Double> data, ChromTab chromtab) {
        super(d, names, start, stop, data, chromtab);
    }

    /**
     * get statistics for array
     *
     * @param arrayId
     * @return
     */
    @Override
    void LoadArrayChrom() throws Exception {
        Logger.getLogger(ArrayTrackView.class.getName()).log(Level.INFO,
                "LoadArrayChrom");
        try {

            Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            PreparedStatement ps = con.prepareStatement(
                    " select chromStart, chromEnd, "
                    + this.spot.getRatioColName() + ", "
                    + this.spot.getProbeColName()
                    + " from " + this.tableName
                    + " where chrom = ? order by chromStart");

            ps.setString(1, chromtab.chrom);
            ResultSet rs = ps.executeQuery();
            int id = 0;
            while (rs.next()) {

                int start = rs.getInt(1);
                int stop = rs.getInt(2);
                double ratio = rs.getDouble(3);

                arrayStart.add(id, new Long(start));
                arrayStop.add(id, new Long(stop));
                arrayRatio.add(id, new Double(ratio));
                arrayName.add(id, rs.getString(4));
                id++;
            }
        }// start, ende (laenge) ratio
        catch (Exception e) {
            Logger.getLogger(ArrayTrackView.class.getName()).log(Level.SEVERE,
                    "LoadArrayChrom", e);

            throw e;
        }
        if (arrayRatio.size() == 0) {
            arrayRatio.add(0.0);
            arrayStart.add(new Long(0));
            arrayStop.add(new Long(0));
            arrayName.add("no data");
            //throw new RuntimeException("no data found for " + arrayId + " chrom: " + chromtab.chrom);

        }
    }

    @Override
    public void paintArrayView(Graphics g) {
        try {

            long start, stop, _start, _width;
            double ratio;
            int iColor;

            for (int i = 0; i < arrayRatio.size(); i++) {
                /*
                x - the x coordinate of the rectangle to be drawn.
                y - the y coordinate of the rectangle to be drawn.
                width - the width of the rectangle to be drawn.
                height - the height of the rectangle to be drawn.
                 */
                start = arrayStart.get(i).longValue();
                stop = arrayStop.get(i).longValue();
                if (stop < chromtab.pos_first_x) {
                    continue;
                }
                if (start > chromtab.pos_max_x) {
                    break;
                }
                ratio = arrayRatio.get(i).doubleValue();
                if (((Double) ratio).isNaN()) {
                    continue;
                }
                iColor = ArrayViewBase.mapColorGradient(ratio, pos_max_y);
                // relate genome selected area (pos_off_x... pos_max_x) to print area 0 ... width)

                _start = (start - chromtab.pos_first_x) > 0 ? start - chromtab.pos_first_x : 1;
                _width = (stop > chromtab.pos_max_x ? (chromtab.pos_max_x - chromtab.pos_first_x) - _start : (stop - chromtab.pos_first_x) - _start);

                g.setColor(ArrayViewBase.getColor(iColor,
                        parent != null ? parent.isColorScaleRedGreen() : true));
                g.drawRect(ChromTab.off_legend
                        + (int) Math.round(_start / chromtab.scale_x),
                        center - (int) Math.round(ratio * scale_y) - 5,
                        (int) Math.round(_width / chromtab.scale_x),
                        10);
                //g.setColor(ArrayViewBase.getColor(iColor));

                g.fillRect(ChromTab.off_legend
                        + (int) Math.round(_start / chromtab.scale_x),
                        center - (int) Math.round(ratio * scale_y) - 5,
                        (int) Math.round(_width / chromtab.scale_x),
                        10);
                g.setColor(Color.black);
                g.drawRect(ChromTab.off_legend + (int) Math.round(_start / chromtab.scale_x),
                        center - (int) Math.round(ratio * scale_y) - 1,
                        (int) Math.round(_width / chromtab.scale_x),
                        2);
                g.fillRect(ChromTab.off_legend + (int) Math.round(_start / chromtab.scale_x),
                        center - (int) Math.round(ratio * scale_y) - 1,
                        (int) Math.round(_width / chromtab.scale_x),
                        2);

            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error visualizing data");
        }

    }
}
