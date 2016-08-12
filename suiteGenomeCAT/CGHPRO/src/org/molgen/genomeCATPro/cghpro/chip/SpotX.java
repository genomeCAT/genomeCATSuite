package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name Spot
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
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.ISpot;

/**
 * 100716 kt redesign spot with anno 050613 kt bug constructor ratio
 */
public class SpotX extends SpotXwoGene implements ISpot, RegionArray {

    private String gene;

    @Override
    public boolean hasGeneView() {
        return true;
    }

    @Override
    public String getGeneColName() {
        return "geneName";
    }

    public String getGeneName() {
        return this.gene;
    }

    public void setGeneName(String geneName) {
        this.gene = geneName;
    }

    public SpotX() {

    }

    SpotX(int iid,
            String probeID, String probeName, String geneName, String chrom, long start, long stop,
            String desc, double ratio) {
        super(iid, probeID, probeName, chrom, start, stop,
                desc, ratio);

        this.setGeneName(geneName);

    }

    @Override
    public List<? extends IFeature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotX.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotX> list = new Vector<>();

        SpotX _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName, geneName, "
                    + " chrom, chromStart, chromEnd, "
                    + " ratio, DESCRIPTION"
                    + " from " + d.getTableData()
                    + " where chrom != \'\' "
                    + " order by probeID");

            while (rs.next()) {

                _spot = new SpotX(
                        rs.getInt("id"),
                        rs.getString("probeID"),
                        rs.getString("probeName"),
                        rs.getString("geneName"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getString("DESCRIPTION"),
                        rs.getDouble("ratio"));
                list.add(_spot);
            }
            return list;
            // ((ChipFeature) c).dataFromSpots(_spots);
        } catch (Exception e) {
            Logger.getLogger(SpotX.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SpotX.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }

    }

    @Override
    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();

        String sql = " CREATE TABLE " + tableData + " ( "
                + "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeID varchar(255) NOT NULL default '', "
                + "probeName varchar(255) , "
                + "geneName varchar(255), "
                + "chrom varChar(45) default '',"
                + "chromStart int(10) unsigned default 0,"
                + "chromEnd int(10) unsigned default 0,"
                + "DESCRIPTION varchar(255), "
                + "ratio DOUBLE, "
                + "PRIMARY KEY (id),"
                + //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "INDEX (geneName (10))  "
                + " ) TYPE=MyISAM";

        return sql;
    }

    @Override
    public String getInsertSQL(Data d) {
        return "INSERT INTO " + d.getTableData()
                + "(id, probeID, probeName, geneName, DESCRIPTION, "
                + "ratio, chrom, chromStart, chromEnd "
                + ") "
                + "values( "
                + this.getIid() + ","
                + "\'" + this.getId() + "\'" + ","
                + "\'" + this.getName() + "\'" + ","
                + "\'" + this.getGeneName() + "\'" + ","
                + "\'" + this.getDescription() + "\'" + ","
                + this.getRatio() + ","
                + "\'" + this.getChrom() + "\'" + ","
                + this.getChromStart() + "," + this.getChromEnd() + ")";

    }

    @Override
    public String toFullString() {
        return this.getName() + " " + this.getGeneName() + " " + this.toString();
    }

}
