package org.molgen.genomeCATPro.xportagilent;

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
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;

/**
 * @name SpotAgilentAnno
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
public class SpotAgilentAnno extends SpotAgilent {

    public final static String annoColName = "anno";
    String annoValue = "";

    public SpotAgilentAnno() {
        super();
    }

    private SpotAgilentAnno(
            int iid,
            int probeID,
            String probeName,
            String chrom,
            long chromStart,
            long chromEnd,
            boolean controlType,
            String geneName,
            String DESCRIPTION,
            String SystematicName,
            double rSignal,
            double gSignal,
            double rgRatio10,
            double rgRatio10PValue,
            double ratio,
            String anno) {
        super(iid, probeID, probeName, chrom, chromStart, chromEnd, controlType, geneName, DESCRIPTION,
                SystematicName, rSignal, gSignal, rgRatio10, rgRatio10PValue, ratio);
        this.setAnnoValue(anno);
    }

    @Override
    public String getAnnoValue() {
        return annoValue;
    }

    public void setAnnoValue(String annoValue) {
        this.annoValue = annoValue;
    }

    @Override
    public String toHTMLString() {
        return (super.toHTMLString() + this.getAnnoValue() + " ");
    }

    @Override
    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();
        String annoTable = ((ExperimentData) d).getPlatformdata().getTableData();
        return ImportExperimentFileFETXT.getCreateTableSQLWithAnno(tableData, annoTable, SpotAgilentAnno.annoColName);

    }

    @Override
    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData() +
                "( id, probeID, probeName, chrom, chromStart, " +
                "chromEnd, controlType, geneName, DESCRIPTION, " +
                "SystematicName, rSignal, gSignal, rgRatio10, rgRatio10PValue," +
                "ratio , " + SpotAgilentAnno.annoColName + " ) " +
                " VALUES(" + this.getIid() + "," + "\'" + this.getId() + "\'" + ", " +
                "\'" + this.getName() + "\'" + "," +
                "\'" + this.getChrom() + "\'" + "," +
                this.getChromStart() + "," + this.getChromEnd() + "," +
                this.isControlSpot() + "," + "\'" + this.getGeneName() + "\'" + ", " +
                "\'" + this.getDescription() + "\'" + ", " + "\'" + this.getSystematicName() + "\'" + ", " +
                this.rSignal + ", " + this.gSignal + ", " + this.rgRatio10 + "," +
                this.rgRatio10PValue + ", " +
                (Double.isNaN(this.getRatio()) ? null : this.getRatio()) + ", " +
                this.getAnnoValue() + ")");
    }

    @Override
    public List<? extends Feature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotAgilentAnno.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotAgilent> list = new Vector<SpotAgilent>();

        SpotAgilent _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName, " +
                    " chrom, chromStart, chromEnd, " +
                    " controlType, geneName, DESCRIPTION, SystematicName, " +
                    " rSignal, gSignal, rgRatio10, rgRatio10PValue, ratio , " + SpotAgilentAnno.annoColName +
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by probeID");


            while (rs.next()) {

                _spot = new SpotAgilentAnno(
                        rs.getInt("id"),
                        rs.getInt("probeID"),
                        rs.getString("probeName"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getBoolean("controlType"),
                        rs.getString("geneName"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("SystematicName"),
                        rs.getDouble("rSignal"),
                        rs.getDouble("gSignal"),
                        rs.getDouble("rgRatio10"),
                        rs.getDouble("rgRatio10PValue"),
                        rs.getDouble("ratio"),
                        rs.getString(SpotAgilentAnno.annoColName));
                list.add(_spot);
            }
            return list;
        // ((ChipFeature) c).dataFromSpots(_spots);
        } catch (Exception e) {
            Logger.getLogger(SpotAgilentAnno.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SpotAgilentAnno.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }

    }
}