package org.molgen.genomeCATPro.xportagilent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.molgen.genomeCATPro.dblib.Database;

import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.data.IFeature;

/**
 * @name SpotAgilent
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
/**
 *
 * class containing data for Agilent experiment data maintains raw and log2
 * channel intensities, ratio and error values from original array-chip.
 *
 */
public class SpotAgilent extends SpotAgilentwoGene {

    /**
     * gene annotation information
     */
    String geneName;

    public SpotAgilent() {
    }

    SpotAgilent(
            // from DB
            int iid,
            int probeID,
            String probeName,
            String chrom,
            long chromStart,
            long chromEnd,
            boolean controlType,
            String DESCRIPTION,
            String SystematicName,
            double rSignal,
            double gSignal,
            double rgRatio10,
            double rgRatio10PValue,
            double ratio, String geneName) {
        super(iid, probeID, probeName, chrom, chromStart, chromEnd, controlType, DESCRIPTION,
                SystematicName, rSignal, gSignal, rgRatio10, rgRatio10PValue, ratio);

        this.geneName = geneName;

    }

    @Override
    public List<? extends IFeature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotAgilent.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotAgilent> list = new Vector<SpotAgilent>();

        SpotAgilent _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName,"
                    + " chrom, chromStart, chromEnd, geneName"
                    + " controlType, geneName, DESCRIPTION, SystematicName, "
                    + " rSignal, gSignal, rgRatio10, rgRatio10PValue, ratio "
                    + " from " + d.getTableData()
                    + " where chrom != \'\' "
                    + " order by probeID");

            while (rs.next()) {

                _spot = new SpotAgilent(
                        rs.getInt("id"),
                        rs.getInt("probeID"),
                        rs.getString("probeName"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getBoolean("controlType"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("SystematicName"),
                        rs.getDouble("rSignal"),
                        rs.getDouble("gSignal"),
                        rs.getDouble("rgRatio10"),
                        rs.getDouble("rgRatio10PValue"),
                        rs.getDouble("ratio"),
                        rs.getString("geneName"));
                list.add(_spot);
            }
            return list;
            // ((ChipFeature) c).dataFromSpots(_spots);
        } catch (Exception e) {
            Logger.getLogger(SpotAgilent.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SpotAgilent.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }

    }

    @Override
    public boolean hasGeneView() {
        return true;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    @Override
    public String getGeneColName() {
        return "geneName";
    }

    @Override
    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();
        String tablePlatform = ((ExperimentData) d).getPlatformdata().getTableData();
        String sql
                = " CREATE TABLE " + tableData + " ( "
                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeID INT UNSIGNED NOT NULL, "
                + "probeName varchar(255) NOT NULL, "
                + "geneName varchar(255), "
                + "chrom varChar(45)  DEFAULT \'\',"
                + "chromStart int(10) unsigned  default 0,"
                + "chromEnd int(10) unsigned  default 0,"
                + "controlType tinyint(1) unsigned NOT NULL default 0,"
                + "DESCRIPTION varchar(255), "
                + "SystematicName varchar(255), "
                + "rSignal DOUBLE, gSignal DOUBLE, "
                + "rgRatio10 DOUBLE, rgRatio10PValue DOUBLE, "
                + "ratio DOUBLE, "
                + "PRIMARY KEY (id),"
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "INDEX (geneName (10)) , "
                + "CONSTRAINT `fk_Data_Spots_" + tablePlatform + "` "
                + "FOREIGN KEY (`probeName` ) "
                + " REFERENCES `" + tablePlatform + "` "
                + " (`probeName`) "
                + " ON DELETE NO ACTION "
                + " ON UPDATE NO ACTION) ";
        return sql;

    }

    @Override
    public String getInsertSQL(Data d) {
        return "INSERT INTO " + d.getTableData()
                + "( id, probeID, probeName, chrom, chromStart, "
                + "chromEnd, controlType, geneName, DESCRIPTION, "
                + "SystematicName, rSignal, gSignal, rgRatio10, rgRatio10PValue,"
                + "ratio ) "
                + " VALUES("
                + this.getIid() + "," + "\'" + this.getId() + "\'" + ", "
                + "\'" + this.getName() + "\'" + ","
                + "\'" + this.getChrom() + "\'" + ","
                + this.getChromStart() + "," + this.getChromEnd() + ","
                + this.isControlSpot() + "," + "\'" + this.getGeneName() + "\'" + ","
                + "\'" + this.getDescription() + "\'" + ", " + "\'" + this.getSystematicName() + "\'" + ", "
                + this.rSignal + ", " + this.gSignal + ", " + this.rgRatio10 + ","
                + this.rgRatio10PValue + ", "
                + (Double.isNaN(this.getRatio()) ? null : this.getRatio()) + ")";

    }

}
