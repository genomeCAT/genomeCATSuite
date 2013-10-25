package org.molgen.genomeCATPro.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 * @name FeatureImpl
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
public class FeatureImpl implements Feature, Cloneable, RegionArray {

    public long iid;
    /** id of the Feature/BAC*/
    public String id;
    /**name of the chromosome*/
    public String chrom;
    /**base pair position of start*/
    public long chromStart = -1;
    /**base pair position of end*/
    public long chromEnd = -1;
    /** average ratio between replicates*/
    public double ratio;
    // dynamic information depending on user parameter
    /**indicator if this Feature/BAC is aberrant, 0:normal; 1:gain, -1:loss*/
    public int ifAberrant;
    private int count = 0;
    final static DecimalFormat myFormatter = new DecimalFormat("0.###");

    public void setName(String name) {
        this.setId(name);
    }

    public String getName() {
        return this.getId();
    }

    public String getChrom() {
        return chrom;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    public long getChromEnd() {
        return chromEnd;
    }

    public void setChromEnd(long chromEnd) {
        this.chromEnd = chromEnd;
    }

    public long getChromStart() {
        return chromStart;
    }

    public void setChromStart(long chromStart) {
        this.chromStart = chromStart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FeatureImpl() {
        this.id = "";
        this.ifAberrant = 0;
    }

    public FeatureImpl(String chrom, long chromStart, long chromEnd) {
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
    }

    public String toFullString() {
        return new String(this.getId() + " " +
                this.chrom + ":" + this.chromStart + "-" + this.chromEnd + "(" + this.getRatio() + ")");
    }

    FeatureImpl(Long iid, String id, String chrom, long chromStart, long chromEnd, double ratio,
            int ifAberrant) {
        this.iid = iid;
        this.id = id;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
        this.ifAberrant = ifAberrant;
        this.ratio = ratio;

    }

    public FeatureImpl(Long iid, String id, String chrom, long chromStart, long chromEnd,
            double ratio, int count,
            int ifAberrant) {
        this(iid, id, chrom, chromStart, chromEnd, ratio, ifAberrant);
        this.count = count;

    }

    /**
     *Set the ratio to the input 'ratio'
     *@param ratio input
     **/
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     *Get the ratio
     *@return the ratio 
     **/
    public double getRatio() {
        return ratio;
    }

    public boolean isAberrant() {
        if (this.ifAberrant == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *Set the ifAberrant to the input
     *@param ifAberrant input
     **/
    public void setIfAberrant(int ifAberrant) {

        this.ifAberrant = ifAberrant;

    }

    public int getIfAberrant() {

        return this.ifAberrant;
    }

    public String getCreateTableSQL(Data d) {
        String sql =
                " CREATE TABLE " + d.getTableData() + " ( " +
                "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
                "name varchar(255) NOT NULL, " +
                "chrom varChar(45) NOT NULL," +
                "chromStart int(10) unsigned NOT NULL," +
                "chromEnd int(10) unsigned NOT NULL," +
                "ratio DOUBLE, " +
                "count int , " +
                "PRIMARY KEY (id)," +
                "INDEX (chrom (5) ), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd) ) " +
                "TYPE=MyISAM";
        return sql;
    }

    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData() +
                "(name, chrom, chromStart, chromEnd, ratio, count ) " +
                "values( " +
                "\'" + this.getId() + "\',\'" + this.getChrom() + "\'," +
                "\'" + this.getChromStart() + "\',\'" + this.getChromEnd() + "\'," +
                "\'" + this.getRatio() + "\',\'" + this.getCount() + "\' )");
    }

    public String toHTMLString() {
        return new String(this.getName() + " " + myFormatter.format(getRatio()) + " ");
    }

    public boolean equalsByPos(Region r2) {
        return (this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd());

    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public final static String ICON_PATH = "org/molgen/genomeCATPro/annotation/page_16.png";

    public String getIconPath() {
        return ICON_PATH;
    }

    public List<? extends Feature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(FeatureImpl.class.getName()).log(Level.INFO, "loadFromDB");
        List<FeatureImpl> list = new Vector<FeatureImpl>();
        FeatureImpl f = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "Select id, name, chrom, chromStart, chromEnd, ratio, count " +
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by chrom, chromStart");

            String _chrom = "";
            while (rs.next()) {
                _chrom = rs.getString("chrom");
                f = new FeatureImpl(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getDouble("ratio"),
                        rs.getInt("count"),
                        0);
                list.add(f);

            }
            return list;
        } catch (Exception e) {
            Logger.getLogger(FeatureImpl.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(FeatureImpl.class.getName()).log(Level.INFO, "Error: ", ex);
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
        return false;
    }

    public String getRatioColName() {
        return "ratio";
    }

    public String getGeneColName() {
        return null;
    }

    public String getProbeColName() {
        return "name";
    }

    @Override
    public String toString() {

        return new String(getChrom() + ":" + getChromStart() + "-" + getChromEnd());

    }
}
