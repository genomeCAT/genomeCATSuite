package org.molgen.genomeCATPro.cat.maparr;

/**
 * * @(#)ArrayGeneView.java
 *
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package. Katrin Tebel
 * <tebel at molgen.mpg.de>. The contents of this file are subject to the terms
 * of either the GNU General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

/**
 * 140912 kt loadArrayChrom not Null, boundaries
 */
public class ArrayGeneView extends ArrayView {

    static String geneTable = null;

    public ArrayGeneView(ArrayData d, ChromTab chromtab) throws Exception {
        super(d, chromtab);
    }

    public ArrayGeneView(ArrayData d,
            Vector<String> names, Vector<Long> start, Vector<Long> stop,
            Vector<Double> data, ChromTab chromtab) {
        super(d, names, start, stop, data, chromtab);
    }

    // geneId
    @Override
    void LoadArrayChrom() {

        try {
            Logger.getLogger(ArrayGeneView.class.getName()).log(Level.INFO,
                    "LoadArrayChrom");

            Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            /*PreparedStatement ps = con.prepareStatement(	"select chromStart, chromEnd, " + 
            Defaults.COL_RATIO_LOC + "," + Defaults.COL_PVALUE_LOC +
            " from "+ arrayId +
            " where chrom = ? order by chromStart");
             */
            PreparedStatement ps = con.prepareStatement(
                    "SELECT min(chromStart), max(chromEnd), "
                    + "AVG(" + this.spot.getRatioColName() + "), "
                    + this.spot.getGeneColName()
                    + " FROM " + this.tableName
                    + " WHERE chrom = ? and  " + this.spot.getGeneColName() + " != \'\' "
                    + " AND  " + this.spot.getGeneColName() + " is not null "
                    + " GROUP BY " + this.spot.getGeneColName() + " order by chromStart");

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
            e.printStackTrace();
            //e.printStackTrace();
        }
        if (arrayRatio.size() == 0) {
            if (arrayRatio.size() == 0) {
                arrayRatio.add(0.0);
                arrayStart.add(new Long(0));
                arrayStop.add(new Long(0));
                arrayName.add("no data");
                //throw new RuntimeException("no data found for " + arrayId + " chrom: " + chromtab.chrom);

            }
        }
    }
}
