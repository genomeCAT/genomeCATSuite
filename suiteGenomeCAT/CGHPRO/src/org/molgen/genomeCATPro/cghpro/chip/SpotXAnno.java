package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name SpotXAnno
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.ISpot;

/**
 * 100716 kt redesign spot with anno 050613 kt bug constructor ratio
 */
public class SpotXAnno extends SpotX implements ISpot, RegionArray {

    String annoValue = "";

    public String getAnnoValue() {
        return annoValue;
    }

    public void setAnnoValue(String annoValue) {
        this.annoValue = annoValue;
    }

    SpotXAnno(int iid,
            String probeID, String probeName, String geneName, String chrom, long start, long stop,
            String desc, double ratio, String anno) {
        super(iid, probeID, probeName, geneName, chrom, start, stop,
                desc, ratio);
        this.annoValue = anno;
    }

    @Override
    public List<? extends IFeature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotXAnno.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotXAnno> list = new Vector<>();

        SpotXAnno _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName, geneName, "
                    + " chrom, chromStart, chromEnd, "
                    + " ratio, DESCRIPTION, " + Defaults.annoColName
                    + " from " + d.getTableData()
                    + " where chrom != \'\' "
                    + " order by probeID");

            while (rs.next()) {

                _spot = new SpotXAnno(
                        rs.getInt("id"),
                        rs.getString("probeID"),
                        rs.getString("probeName"),
                        rs.getString("geneName"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getString("DESCRIPTION"),
                        rs.getDouble("ratio"),
                        rs.getString(Defaults.annoColName));
                list.add(_spot);
            }
            return list;

        } catch (Exception e) {
            Logger.getLogger(SpotXAnno.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SpotXAnno.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }

    }

    @Override
    public String getInsertSQL(Data d) {
        return "INSERT INTO " + d.getTableData()
                + "(id, probeID, probeName,  geneName, DESCRIPTION, "
                + "ratio, chrom, chromStart, chromEnd, " 
                + Defaults.annoColName
                + ") "
                + "values( "
                + this.getIid() + ","
                + "\'" + this.getId() + "\'" + ","
                + "\'" + this.getName() + "\'" + ","
                + "\'" + this.getGeneName() + "\'" + ","
                + "\'" + this.getDescription() + "\'" + ","
                + this.getRatio() + ","
                + "\'" + this.getChrom() + "\'" + ","
                + this.getChromStart() + "," + this.getChromEnd() +  ","
                + "\'" + this.getAnnoValue() + "\'" +")";

    }

    @Override
    public String toFullString() {
        return this.getName() + " " + this.getAnnoValue() + " " + this.toString();
    }

}
