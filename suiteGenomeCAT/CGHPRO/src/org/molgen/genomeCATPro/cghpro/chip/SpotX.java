package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name Spot
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @author Wei Chen
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.cghpro.xport.ImportBAC;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.data.Spot;
import org.molgen.genomeCATPro.data.SpotImpl2;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;

/**
 * 050613   kt  bug constructor ratio
 */
/**
 *
 * public void scaleByFactor(double c, boolean dyeswap) {
throw new UnsupportedOperationException("Not supported yet.");
}

public String getSQLtoPlattform(String tablePlatform, String tableData) {
throw new UnsupportedOperationException("Not supported yet.");
}

public List<? extends Feature> loadFromDB(Data d) throws Exception {
throw new UnsupportedOperationException("Not supported yet.");
}

public String getCreateTableSQL(Data d) {
throw new UnsupportedOperationException("Not supported yet.");
}

public String getInsertSQL(Data d) {
throw new UnsupportedOperationException("Not supported yet.");
}

public boolean equalsByPos(Region r2) {
throw new UnsupportedOperationException("Not supported yet.");
}
 */
public class SpotX extends SpotImpl2 implements Spot, RegionArray {

    private String desc;
    private String gene;
    //The data from any file

    public SpotX() {
    }
    /*
     *  rs.getInt("id"),
    rs.getString("probeId"),
    rs.getString("probeName"),
    rs.getString("geneName"),
    rs.getString("chrom"),
    rs.getLong("chromStart"),
    rs.getLong("chromEnd"),
    rs.getString("Description"),
    rs.getDouble("ratio"));
     */

    private SpotX(int iid,
            String probeId, String probeName, String geneName, String chrom, long start, long stop,
            String desc, double ratio) {
        this.setIid(iid);
        this.setId(String.valueOf(probeId));
        this.setName(probeName);
        this.setGeneName(geneName);
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

    public String getSQLtoPlattform(String tablePlatform, String tableData) {
        return new String(tablePlatform + ".probeName = " + tableData + ".probeID ");
    }

    public List<? extends Feature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotX.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotX> list = new Vector<SpotX>();

        SpotX _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName, geneName, " +
                    " chrom, chromStart, chromEnd, " +
                    " ratio, DESCRIPTION" +
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by probeID");


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

    public String getGeneColName() {
        return "geneName";
    }

    public String getProbeColName() {
        return "probeID";
    }

    private String getDescription() {
        return this.desc;
    }

    private String getGeneName() {
        return this.gene;
    }

    @SuppressWarnings("empty-statement")
    private void setControlFlag(int control) {
        ;

    }

    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();
        String tablePlatform = ((ExperimentData) d).getPlatformdata().getTableData();
        return ImportBAC.getCreateTableSQL(tableData, tablePlatform);
    }

    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData() +
                "(id, probeID, probeName, geneName, DESCRIPTION, " +
                "ratio, chrom, chromStart, chromEnd " +
                ") " +
                "values( " +
                this.getIid() + "," +
                "\'" + this.getId() + "\'" + "," +
                "\'" + this.getName() + "\'" + "," +
                "\'" + this.getGeneName() + "\'" + "," +
                "\'" + this.getDescription() + "\'" + "," +
                this.getRatio() + "," +
                "\'" + this.getChrom() + "\'" + "," +
                this.getChromStart() + "," + this.getChromEnd() + ")");

    }

    @Override
    public String toString() {
        return new String(getChrom() + ":" + getChromStart() + "-" + getChromEnd());
    }

    public String toFullString() {
        return new String(this.getName() + " " + this.toString());
    }

    public boolean equalsByPos(Region r2) {
        return (this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd());

    }

    private void setDescription(String desc) {
        this.desc = desc;
    }

    private void setGeneName(String geneName) {
        this.gene = geneName;
    }
}
