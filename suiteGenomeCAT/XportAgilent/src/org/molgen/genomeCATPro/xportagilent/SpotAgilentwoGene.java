package org.molgen.genomeCATPro.xportagilent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.common.MyMath;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.IOriginalSpot;
import org.molgen.genomeCATPro.data.ISpot;
import org.molgen.genomeCATPro.data.SpotBasic;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.dblib.Database;
/**
 * @name SpotAgilentwoGene
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
public  class SpotAgilentwoGene extends SpotBasic implements IOriginalSpot, RegionArray {
    
    /**
     * the log2 intensity at red/cy5/f635 used for normalization
     */
    public double log2Cy5 = Double.NaN;
    /**
     * the log2 intensity at green/cy3/f532 used for normalization
     */
    public double log2Cy3 = Double.NaN;
    /**
     * additional information
     */
    String Description;
    String SystematicName;
    /**
     * original red/cy5/f635 intensity
     */
    double rSignal;
    /**
     * original green/cy3/f532 Intensity
     */
    double gSignal;
    double rgRatio10;
    double rgRatio10PValue;
    double log2Ratio;
    protected int block = 0;
    protected int row = 0;
    protected int column = 0;
    protected boolean rg = false;

    public SpotAgilentwoGene() {
    }
    public SpotAgilentwoGene(
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
            double ratio) {

        this.setIid(iid);
        this.setId(String.valueOf(probeID));
        this.setName(probeName);
        
        this.setChrom(chrom);
        this.setChromStart(chromStart);
        this.setChromEnd(chromEnd);
        this.setDescription(Description);

        super.setControlSpot(controlType); // 0: none control, 1: control spot

        this.setRSignal(rSignal);
        this.setGSignal(gSignal);
        this.setRgRatio10(rgRatio10);
        this.setRgRatio10PValue(rgRatio10PValue);
        this.setLog2Ratio(ratio);

        if (this.getRgRatio10() == 0 && (this.getRgRatio10PValue() == 1)) {
            super.setIfExcluded(1);
            /**
             * todo take into import if (pValueLogRatio == 1.0 && logRatio ==
             * 0.0) { this.f635Mean = 0; this.b635Mean = 0; this.f532Mean = 0;
             * this.b532Mean = 0; } else { this.f635Mean = f635Mean;
             * this.b635Mean = b635Mean; this.f532Mean = f532Mean; this.b532Mean
             * = b532Mean; }
             *
             */
        }
    }
    
    @Override
    public List<? extends IFeature> loadFromDB(Data d) throws Exception {
         Logger.getLogger(SpotAgilent.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotAgilentwoGene> list = new Vector<SpotAgilentwoGene>();

        SpotAgilentwoGene _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName,"
                    + " chrom, chromStart, chromEnd,"
                    + " controlType,  DESCRIPTION, SystematicName, "
                    + " rSignal, gSignal, rgRatio10, rgRatio10PValue, ratio "
                    + " from " + d.getTableData()
                    + " where chrom != \'\' "
                    + " order by probeID");

            while (rs.next()) {

                _spot = new SpotAgilentwoGene(
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
                        rs.getDouble("ratio"));
                list.add(_spot);
            }
            return list;
            // ((ChipFeature) c).dataFromSpots(_spots);
        } catch (Exception e) {
            Logger.getLogger(SpotAgilent.class.getName()).log(Level.WARNING, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SpotAgilent.class.getName()).log(Level.WARNING, "Error: ", ex);
                }
            }
        }}

    @Override
    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();
        String tablePlatform = ((ExperimentData) d).getPlatformdata().getTableData();
        String sql
                = " CREATE TABLE " + tableData + " ( "
                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "probeID INT UNSIGNED NOT NULL, "
                + "probeName varchar(255) NOT NULL, "
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
         return new String(
                "INSERT INTO " + d.getTableData()
                + "( id, probeID, probeName, chrom, chromStart, "
                + "chromEnd, controlType,  DESCRIPTION, "
                + "SystematicName, rSignal, gSignal, rgRatio10, rgRatio10PValue,"
                + "ratio ) "
                + " VALUES("
                + this.getIid() + "," + "\'" + this.getId() + "\'" + ", "
                + "\'" + this.getName() + "\'" + ","
                + "\'" + this.getChrom() + "\'" + ","
                + this.getChromStart() + "," + this.getChromEnd() + ","
                + this.isControlSpot() + ","
                + "\'" + this.getDescription() + "\'" + ", " + "\'" + this.getSystematicName() + "\'" + ", "
                + this.rSignal + ", " + this.gSignal + ", " + this.rgRatio10 + ","
                + this.rgRatio10PValue + ", "
                + (Double.isNaN(this.getRatio()) ? null : this.getRatio()) + ")");  }

    @Override
    public boolean hasGeneView() {
        return false;
    }

    @Override
    public String getGeneColName() {
        throw new UnsupportedOperationException("Not supported yet."); 
       
    }
    @Override
    public double getRatio() {
        return this.getLog2Ratio();
    }

    @Override
    public void setRatio(double d) {
        this.setLog2Ratio(d);
    }

    public double getLog2Ratio() {
        return log2Ratio;
    }

    @Override
    public void scaleByFactor(double c, boolean rg) {
        // scale channel?
        if (rg) {
            this.setLog2Ratio(getLog2Cy5() - getLog2Cy3() - c);
        } else {
            this.setLog2Ratio(getLog2Cy3() - getLog2Cy5() - c);
        }
        //setLog2Ratio();
    }

    public void setLog2Ratio(boolean rg) {
        if (this.rg) {
            this.log2Ratio = this.log2Cy5 - this.log2Cy3;
        } else {
            this.log2Ratio = this.log2Cy3 - this.log2Cy5;
        }
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getSystematicName() {
        return SystematicName;
    }

    public void setSystematicName(String SystematicName) {
        this.SystematicName = SystematicName;
    }

    public double getGSignal() {
        return gSignal;
    }

    public void setGSignal(double gSignal) {
        this.gSignal = gSignal;
        this.setLog2Cy3(MyMath.log2(this.gSignal));
    }

    public double getRSignal() {
        return rSignal;
    }

    public void setRSignal(double rSignal) {
        this.rSignal = rSignal;
        this.setLog2Cy5(MyMath.log2(this.rSignal));
    }

    public double getRgRatio10() {
        return rgRatio10;
    }

    public void setRgRatio10(double rgRatio10) {
        this.rgRatio10 = rgRatio10;
    }

    public double getRgRatio10PValue() {
        return rgRatio10PValue;
    }

    public void setRgRatio10PValue(double rgRatio10PValue) {
        this.rgRatio10PValue = rgRatio10PValue;
    }

    @Override
    public double getCy3Value() {
        return this.getGSignal();
    }

    @Override
    public double getCy5Value() {
        return this.getRSignal();
    }

    @Override
    public double getLog2Cy3() {
        return this.log2Cy3;
    }

    @Override
    public void setLog2Cy3(double log2Cy3) {
        this.log2Cy3 = log2Cy3;
    }

    @Override
    public double getLog2Cy5() {
        return this.log2Cy5;
    }

    @Override
    public void setLog2Cy5(double log2Cy5) {
        this.log2Cy5 = log2Cy5;
    }

    public boolean isRg() {
        return rg;
    }

    public void setRg(boolean rg) {
        this.rg = rg;
    }

    @Override
    public Vector<? extends ISpot> getVector() {
        return new Vector<SpotAgilent>();
    }

    protected void setLog2Ratio(double ratio) {
        this.log2Ratio = ratio;
    }

    @Override
    public void addTo(List<? extends ISpot> list) {
        try{
        this._addTo((List<? super SpotAgilentwoGene>) list);
        }
        catch(Exception ex){
            Logger.getLogger(SpotAgilent.class.getName()).log(Level.SEVERE, "Error: ", ex);
        }
    }

    void _addTo(List<? super SpotAgilentwoGene> list) {
        list.add(this);
    }

    @Override
    public boolean getRG() {
        return this.rg;
    }

    @Override
    public int getBlock() {
        return this.block;
    }

    @Override
    public int getRow() {
        return this.row;
    }

    @Override
    public int getColumn() {
        return this.column;
    }

    @Override
    public boolean hasRegionView() {
        return false;
    }

    @Override
    public boolean hasProbeView() {
        return true;
    }

    @Override
    public String getSQLtoPlattform(String tablePlatform, String tableData) {
        return tablePlatform + ".probeName = " + tableData + ".probeName ";
        // " AND " + tablePlatform + ".probeID = " + tableData + ".probeID");
    }

    @Override
    public String getRatioColName() {
        return "ratio";
    }

    @Override
    public String getProbeColName() {
        return "probeName";
    }

    @Override
    public boolean equalsByPos(Region r2) {
        return this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd();
    }

    
}
