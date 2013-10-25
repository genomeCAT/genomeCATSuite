package org.molgen.genomeCATPro.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 * @name FeatureImplAnno
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
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
public class FeatureImplAnno extends FeatureImpl {

    //public final static String annoColName = "anno";
    String annoValue = "";

    public FeatureImplAnno() {
        super();
    }

    public FeatureImplAnno(Long iid, String id,
            String chrom, long chromStart, long chromEnd,
            double ratio, int count,
            int ifAberrant, String anno) {
        super(iid, id, chrom, chromStart, chromEnd, ratio, count, ifAberrant);
        this.setAnnoValue(anno);

    }

    public String getAnnoValue() {
        return annoValue;
    }

    public void setAnnoValue(String annoValue) {
        this.annoValue = annoValue;
    }

    @Override
    public String toHTMLString() {
        return super.toHTMLString() + " " + this.getAnnoValue();
    }

    @Override
    public List<? extends Feature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(FeatureImplAnno.class.getName()).log(Level.INFO, "loadFromDB");
        List<FeatureImpl> list = new Vector<FeatureImpl>();
        FeatureImpl f = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "Select id, name, chrom, chromStart, chromEnd, ratio, count, anno " +
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by chrom, chromStart");

            String _chrom = "";
            while (rs.next()) {
                _chrom = rs.getString("chrom");
                f = new FeatureImplAnno(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getDouble("ratio"),
                        rs.getInt("count"), 0,
                        rs.getString(Defaults.annoColName)
                        );
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

    @Override
    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData() +
                "(name, chrom, chromStart, chromEnd, ratio, count ) " +
                "values( " +
                "\'" + this.getId() + "\',\'" + this.getChrom() + "\'," +
                "\'" + this.getChromStart() + "\',\'" + this.getChromEnd() + "\'," +
                "\'" + this.getRatio() + "\',\'" + this.getCount() + "\' " +
                "\'" + this.getAnnoValue() + "\'" +
                ")");
    }

    @Override
    public String getCreateTableSQL(Data d) {
        String sql =
                " CREATE TABLE " + d.getTableData() + " ( " +
                "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                "name varchar(255) NOT NULL, " +
                "chrom varChar(45) NOT NULL," +
                "chromStart int(10) unsigned NOT NULL," +
                "chromEnd int(10) unsigned NOT NULL," +
                "ratio DOUBLE, " +
                "count int , " +
                Defaults.annoColName + " varchar(255) NOT NULL default '' ," +
                "INDEX (id)," +
                "INDEX (chrom (5)), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd) ) " +
                "TYPE=MyISAM";
        return sql;
    }
}
