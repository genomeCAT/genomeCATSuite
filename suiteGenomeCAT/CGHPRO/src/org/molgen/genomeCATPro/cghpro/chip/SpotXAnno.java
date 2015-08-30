package org.molgen.genomeCATPro.cghpro.chip;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.cghpro.xport.ImportBAC;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;

/**
 * @name SpotBACAnno
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
public class SpotXAnno extends SpotBAC {

    //public final static String annoColName = "anno";
    String annoValue = "";

    @Override
    public String getGeneColName() {
        return null;
    }

    @Override
    public boolean hasGeneView() {
        return false;
    }

    public SpotXAnno() {
        super();
    }

    SpotXAnno(
            int id, String probeId, String name,
            int block, int row, int col,
            String chrom, long start, long stop,
            int control, int ifExcluded,
            double f635Mean, double b635Mean, double b635sd,
            double f532Mean, double b532Mean, double b532sd,
            double snr635, double snr532, double f635, double f532, double ratio, String anno) {
        super(id, probeId, name, block, row, col, chrom, start, stop, control,
                ifExcluded, f635Mean, b635Mean, b635sd, f532Mean, b532Mean, b532sd, snr635, snr532,
                f635, f532, ratio);
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
        return (super.toHTMLString()  + this.getAnnoValue() + " ");
    }
    @Override
    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();
        String tablePlatform = ((ExperimentData) d).getPlatformdata().getTableData();

        return ImportBAC.getCreateTableSQLWithAnno(tableData, tablePlatform,
                Defaults.annoColName);
    }

    @Override
    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData() +
                "(id,block,col, row, probeId, probeName, " +
                "f635Mean, b635Mean,  f532Mean, b532Mean,  " +
                "snr635, snr532, f635, f532, ratio, chrom, chromStart, chromEnd, " +
                "ifExcluded, controlType , " + Defaults.annoColName + " ) " +
                "values( " +
                this.getIid() + "," + this.getBlock() + ", " +
                this.getColumn() + "," + this.getRow() + "," +
                "\'" + this.getId() + "\'" + "," +
                "\'" + this.getName() + "\'" + "," +
                this.f635Mean + ", " + this.b635Mean + ", " +
                this.f532Mean + "," + this.b532Mean + "," +
                this.snr635 + "," + this.snr532 + "," +
                this.f635 + "," + this.f532 + "," +
                this.getRatio() + "," +
                "\'" + this.getChrom() + "\'" + "," +
                this.getChromStart() + "," + this.getChromEnd() + " , " +
                this.isExcluded() + ", " + this.controlFlag + ", " +
                "\'" + this.getAnnoValue() + "\'" + ")");

    }

    @Override
    public List<? extends Feature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotXAnno.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotXAnno> list = new Vector<SpotXAnno>();

        SpotXAnno _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeId, probeName, " +
                    " block, row, col, " +
                    " chrom, chromStart, chromEnd, " +
                    " controlType, ifExcluded,  " +
                    " f635Mean, b635Mean, b635sd, " +
                    " f532Mean,b532Mean,b532sd, " +
                    " snr635, snr532,f635,f532, ratio, "  + Defaults.annoColName + 
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by probeID");


            while (rs.next()) {

                _spot = new SpotXAnno(
                        rs.getInt("id"),
                        rs.getString("probeId"),
                        rs.getString("probeName"),
                        rs.getInt("block"),
                        rs.getInt("row"),
                        rs.getInt("col"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getInt("controlType"),
                        rs.getInt("ifExcluded"),
                        rs.getDouble("f635Mean"),
                        rs.getDouble("b635Mean"),
                        rs.getDouble("b635sd"),
                        rs.getDouble("f532Mean"),
                        rs.getDouble("b532Mean"),
                        rs.getDouble("b532sd"),
                        rs.getDouble("snr635"),
                        rs.getDouble("snr532"),
                        rs.getDouble("f635"),
                        rs.getDouble("f532"),
                        rs.getDouble("ratio"),
                        rs.getString(Defaults.annoColName));
                list.add(_spot);
            }
            return list;
        // ((ChipFeature) c).dataFromSpots(_spots);
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
}
