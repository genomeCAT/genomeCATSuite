package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name SpotXwoGene
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
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.data.SpotBasic;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.ISpot;

/**
 * 050613 kt bug constructor ratio
 */
public class SpotXwoGene extends SpotBasic implements ISpot, RegionArray {

    private String desc;

    public SpotXwoGene() {
    }

    SpotXwoGene(int iid,
            String probeID, String probeName, String chrom, long start, long stop,
            String desc, double ratio) {
        this.setIid(iid);
        this.setId(String.valueOf(probeID));
        this.setName(probeName);

        this.setChrom(chrom);
        this.setChromStart(start);
        this.setChromEnd(stop);
        this.setDescription(desc);
        this.setRatio(ratio);
    }

    @Override
    /**
     * currentSpot.setF635Norm(currentSpot.f635);
     * currentSpot.setF532Norm(currentSpot.f532 - normalValue);
     * currentSpot.setNormalRatio(currentSpot.f532Norm - currentSpot.f635Norm)
     */
    public void scaleByFactor(double c, boolean dyeswap) {

        this.setRatio(this.getRatio() - c);
    }
    //setLog2Ratio();

    @Override
    public String getSQLtoPlattform(String tablePlatform, String tableData) {
        return tablePlatform + ".probeName = " + tableData + ".probeID ";
    }

    @Override
    public List<? extends IFeature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotXwoGene.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotXwoGene> list = new Vector<>();

        SpotXwoGene _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName,  "
                    + " chrom, chromStart, chromEnd, "
                    + " ratio, DESCRIPTION"
                    + " from " + d.getTableData()
                    + " where chrom != \'\' "
                    + " order by probeID");

            while (rs.next()) {

                _spot = new SpotXwoGene(
                        rs.getInt("id"),
                        rs.getString("probeID"),
                        rs.getString("probeName"),
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
            Logger.getLogger(SpotXwoGene.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SpotXwoGene.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }

    }

    public boolean hasGeneView() {
        return false;
    }

    public boolean hasRegionView() {
        return true;
    }

    public boolean hasProbeView() {
        return true;
    }

    public String getRatioColName() {
        return "ratio";
    }

    @Override
    public String getGeneColName() {
        return null;
    }

    @Override
    public String getProbeColName() {
        return "probeID";
    }

    public String getDescription() {
        return this.desc;
    }

    @SuppressWarnings("empty-statement")
    private void setControlFlag(int control) {
        ;

    }

    @Override
    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();

        String sql = " CREATE TABLE " + tableData + " ( "
                + "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeID varchar(255) NOT NULL default '', "
                + "probeName varchar(255) , "
                + "chrom varChar(45) default '',"
                + "chromStart int(10) unsigned default 0,"
                + "chromEnd int(10) unsigned default 0,"
                + "DESCRIPTION varchar(255), "
                + "ratio DOUBLE, "
                + "PRIMARY KEY (id),"
                + //"UNIQUE KEY (probeID)," +
                "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd) "
               
                + " ) TYPE=MyISAM";

        return sql;
    }

    @Override
    public String getInsertSQL(Data d) {
        return "INSERT INTO " + d.getTableData()
                + "(id, probeID, probeName,  DESCRIPTION, "
                + "ratio, chrom, chromStart, chromEnd "
                + ") "
                + "values( "
                + this.getIid() + ","
                + "\'" + this.getId() + "\'" + ","
                + "\'" + this.getName() + "\'" + ","
                + "\'" + this.getDescription() + "\'" + ","
                + this.getRatio() + ","
                + "\'" + this.getChrom() + "\'" + ","
                + this.getChromStart() + "," + this.getChromEnd() + ")";

    }

    @Override
    public String toString() {
        return getChrom() + ":" + getChromStart() + "-" + getChromEnd();
    }

    public String toFullString() {
        return this.getName() + " " + this.toString();
    }

    /**
     *
     * @param r2
     * @return
     */
    @Override
    public boolean equalsByPos(Region r2) {
        return (this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd());

    }

    private void setDescription(String desc) {
        this.desc = desc;
    }

}
