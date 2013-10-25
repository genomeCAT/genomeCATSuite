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

import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.data.OriginalSpot;
import org.molgen.genomeCATPro.data.Spot;
import org.molgen.genomeCATPro.data.SpotImpl2;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.MyMath;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;

/** * @(#)Spot.java * * Copyright (c) 2004 by Wei Chen
 * * @author Wei Chen
 * * Email: wei@molgen.mpg.de
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License 
 * * as published by the Free Software Foundation; either version 2 
 * * of the License, or (at your option) any later version, 
 * * provided that any use properly credits the author. 
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * * */
/** 
 *Class Spot is a class designed to store all the data from scanner.
 *There are 2 constructors besides the default constructor, one is used for loading data from file.
 * while the other is for loading data from database.
 *@author Wei
 */
public class SpotAgilent extends SpotImpl2 implements OriginalSpot, RegionArray {

    /**the log2 intensity at red/cy5/f635 used for normalization*/
    public double log2Cy5 = Double.NaN;
    /**the log2 intensity at green/cy3/f532 used for normalization*/
    public double log2Cy3 = Double.NaN;
    String geneName;
    String Description;
    String SystematicName;
    /** original  red/cy5/f635  intensity*/
    double rSignal;
    /** original green/cy3/f532 Intensity*/
    double gSignal;
    double rgRatio10;
    double rgRatio10PValue;
    double log2Ratio;
    private int block = 0;
    private int row = 0;
    private int column = 0;
    private boolean rg = false;

    public SpotAgilent() {
    }
    ;

    SpotAgilent(
            // from DB
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
            double ratio) {


        this.setIid(iid);
        this.setId(String.valueOf(probeID));
        this.setName(probeName);
        this.setGeneName(geneName);
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
        /** todo take into import
        if (pValueLogRatio == 1.0 && logRatio == 0.0) {
        this.f635Mean = 0;
        this.b635Mean = 0;
        this.f532Mean = 0;
        this.b532Mean = 0;
        } else {
        this.f635Mean = f635Mean;
        this.b635Mean = b635Mean;
        this.f532Mean = f532Mean;
        this.b532Mean = b532Mean;
        }
        
         */
        }
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
    /**
     * currentSpot.setF635Norm(currentSpot.f635);
     * currentSpot.setF532Norm(currentSpot.f532 - normalValue);
     * currentSpot.setNormalRatio(currentSpot.f532Norm - currentSpot.f635Norm)
     */
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

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
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

    public double getCy3Value() {
        return this.getGSignal();
    }

    public double getCy5Value() {
        return this.getRSignal();
    }

    public List<? extends Feature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotAgilent.class.getName()).log(Level.INFO, "loadFromDB");
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
                    " rSignal, gSignal, rgRatio10, rgRatio10PValue, ratio " +
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by probeID");


            while (rs.next()) {

                _spot = new SpotAgilent(
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
                        rs.getDouble("ratio"));
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

    public double getLog2Cy3() {
        return this.log2Cy3;
    }

    public void setLog2Cy3(double log2Cy3) {
        this.log2Cy3 = log2Cy3;
    }

    public double getLog2Cy5() {
        return this.log2Cy5;
    }

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
    public Vector<? extends Spot> getVector() {
        return new Vector<SpotAgilent>();
    }

    private void setLog2Ratio(double ratio) {
        this.log2Ratio = ratio;
    }

    @Override
    public void addTo(List<? extends Spot> list) {
        this._addTo((List<Spot>) list);
    }

    void _addTo(List<? super SpotAgilent> list) {
        list.add(this);
    }

    public boolean getRG() {
        return this.rg;
    }

    public int getBlock() {
        return this.block;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    @Override
    public String toHTMLString() {
        return (super.toHTMLString()  + " " + this.getGeneName() );
    }

    public boolean hasGeneView() {
        return true;
    }

    public boolean hasRegionView() {
        return false;
    }

    public boolean hasProbeView() {
        return true;
    }

    public String getSQLtoPlattform(String tablePlatform, String tableData) {
        return new String(tablePlatform + ".probeName = " + tableData + ".probeName " );
               // " AND " + tablePlatform + ".probeID = " + tableData + ".probeID");
    }

    public String getRatioColName() {
        return "ratio";
    }

    public String getGeneColName() {
        return "geneName";
    }
  
    public String getProbeColName() {
        return "probeName";
    }
     public String getAnnoValue() {
        return this.getGeneName();
    }

    
    public String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();
        String annoTable = ((ExperimentData) d).getPlatformdata().getTableData();
        return ImportExperimentFileFETXT.getCreateTableSQL(tableData, annoTable);

    }

    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData() +
                "( id, probeID, probeName, chrom, chromStart, " +
                "chromEnd, controlType, geneName, DESCRIPTION, " +
                "SystematicName, rSignal, gSignal, rgRatio10, rgRatio10PValue," +
                "ratio ) " +
                " VALUES(" +
                this.getIid() + "," + "\'" + this.getId() + "\'" + ", " +
                "\'" + this.getName() + "\'" + "," +
                "\'" + this.getChrom() + "\'" + "," +
                this.getChromStart() + "," + this.getChromEnd() + "," +
                this.isControlSpot() + "," + "\'" + this.getGeneName() + "\'" + ", " +
                "\'" + this.getDescription() + "\'" + ", " + "\'" + this.getSystematicName() + "\'" + ", " +
                this.rSignal + ", " + this.gSignal + ", " + this.rgRatio10 + "," +
                this.rgRatio10PValue + ", " +
                (Double.isNaN(this.getRatio()) ? null : this.getRatio()) + ")");



    }

    public boolean equalsByPos(Region r2) {
        return (this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd());

    }
}
