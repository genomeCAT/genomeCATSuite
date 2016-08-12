package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name SpotBAC
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
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.data.SpotBasic;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.IOriginalSpot;
/**
 * 
 * class containing data for BAC based experiment data 
 * maintains raw and log2 channel intensities, ratio and error values from 
 * original array-chip.
 * 
 */
/**
 *
 * 170712 kt getCy3/Cy5 -> getLog2...
 */
public class SpotBAC extends SpotBasic implements IOriginalSpot, RegionArray {
    //The data from gpr File;

    /**
     * ID of subgrid where the spot belong
     */
    int block;
    /**
     * ID of column where the spot belong
     */
    int column;
    /**
     * ID of row where the spot belong
     */
    int row;
    /**
     * ID of BAC where the spot belong
     */
    String id;
    /**
     * mean of signal intensity at 635
     */
    double f635Mean;
    /**
     * mean of background signal intesity at 635
     */
    double b635Mean;
    /**
     * mean of signal intensity at 532
     */
    double f532Mean;
    /**
     * mean of background signal intesity at 532
     */
    double b532Mean;
    /**
     * sd for background signal intensitiy
     */
    private double b635sd;
    private double b532sd;
    /**
     * the signal to noise ratio at 635, calculated as
     * (f635mean-b635Mean)/b635Std
     */
    double snr635;
    /**
     * the signal to noise ratio at 532, calculated as
     * (f532mean-b532Mean)/b532Std
     */
    double snr532;
    /**
     * the log2 intensity at 635 used for normalization
     */
    public double f635;
    /**
     * the log2 intensity at 532 used for normalization
     */
    public double f532;
    /**
     * GenePix Pro can flags individual spots according to different criterias.
     * The different flag statuses are Good (has value 100 in the GPR
     * structure), Bad (-100), absent (-75), and Not Found (-50). Unflagged
     * spots have value 0. A spot can only have one of these flags set at each
     * time. The Bad and Good flags are set manually by the user.
     */
    int controlFlag;

    public SpotBAC() {
    }

    SpotBAC(
            int id, String probeID, String name,
            int block, int row, int col,
            String chrom, long start, long stop,
            int control, int ifExcluded,
            double f635Mean, double b635Mean, double b635sd,
            double f532Mean, double b532Mean, double b532sd,
            double snr635, double snr532, double f635, double f532, double ratio) {
        this.setIid(id);
        this.setId(probeID);
        this.setName(name);
        this.block = block;
        this.column = col;
        this.row = row;
        this.setChrom(chrom);
        this.setChromStart(start);
        this.setChromEnd(stop);
        this.setControlFlag(control);
        this.setIfExcluded(ifExcluded);

        this.b532Mean = b532Mean;
        this.f532Mean = f532Mean;
        this.f635Mean = f635Mean;
        this.b635Mean = b635Mean;
        this.b635sd = b635sd;
        this.b532sd = b532sd;
        this.snr532 = snr532;
        this.snr635 = snr635;
        this.setRatio(ratio);
        this.setLog2Cy3(f532);
        this.setLog2Cy5(f635);

    }

    @Override
    /**
     * currentSpot.setF635Norm(currentSpot.f635);
     * currentSpot.setF532Norm(currentSpot.f532 - normalValue);
     * currentSpot.setNormalRatio(currentSpot.f532Norm - currentSpot.f635Norm)
     */
    public void scaleByFactor(double c, boolean dyeswap) {

        // scale channel?
        if (dyeswap) {
            this.setRatio(getLog2Cy5() - getLog2Cy3() - c);
        } else {
            this.setRatio(getLog2Cy3() - getLog2Cy5() - c);
        }
        //setLog2Ratio();

    }


    /**
     *
     * @param d
     * @return
     * @throws Exception
     */
    @Override
    public List<? extends IFeature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(SpotBAC.class.getName()).log(Level.INFO, "loadFromDB");
        List<SpotBAC> list = new Vector<SpotBAC>();

        SpotBAC _spot = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT id, probeID, probeName, "
                    + " block, row, col, "
                    + " chrom, chromStart, chromEnd, "
                    + " controlType, ifExcluded,  "
                    + " f635Mean, b635Mean, b635sd, "
                    + " f532Mean,b532Mean,b532sd, "
                    + " snr635, snr532,f635,f532, ratio "
                    + " from " + d.getTableData()
                    + " where chrom != \'\' "
                    + " order by probeID");

            while (rs.next()) {

                _spot = new SpotBAC(
                        rs.getInt("id"),
                        rs.getString("probeID"),
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
                        rs.getDouble("ratio"));
                list.add(_spot);
            }
            return list;
            // ((ChipFeature) c).dataFromSpots(_spots);
        } catch (Exception e) {
            Logger.getLogger(SpotBAC.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SpotBAC.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }

    }

    public double getLog2Cy3() {
        return f532;
    }

    public void setLog2Cy3(double log2Cy3) {
        this.f532 = log2Cy3;
    }

    public double getLog2Cy5() {
        return this.f635;
    }

    public void setLog2Cy5(double log2Cy5) {
        this.f635 = log2Cy5;
    }

    public boolean getRG() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getCy3Value() {
        return this.getLog2Cy3();
    }

    public double getCy5Value() {
        return this.getLog2Cy5();
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

    /**
     * has gene information
     * @return
     */
    @Override
    public boolean hasGeneView() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasRegionView() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasProbeView() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getRatioColName() {
        return "ratio";
    }

    /**
     *
     * @return
     */
    @Override
    public String getGeneColName() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public String getProbeColName() {
        return "probeID";
    }

    private void setControlFlag(int control) {
        this.controlFlag = control;

    }
    
    /**
     *
     * @param tablePlatform
     * @param tableData
     * @return
     */
    @Override
    public String getSQLtoPlattform(String tablePlatform, String tableData) {
        return tablePlatform + ".probeName = " + tableData + ".probeID ";
    }

    /**
     * create table 
     * @param d
     * @return
     * /**
     * create table without primary key, table will be filled by select into,
     * primary key is added after select into
     */
     
    public  String getCreateTableSQL(Data d) {
        String tableData = d.getTableData();
        String tablePlatform = ((ExperimentData) d).getPlatformdata().getTableData();
          String sql = "CREATE TABLE " + tableData + "("
                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "block int(4) NOT NULL default '0',"
                + "col int(4) NOT NULL default '0',"
                + "row int(4) NOT NULL default '0',"
                + "probeID varchar(255) NOT NULL default '',"
                + "probeName varchar(255) ,"
                + "f635Mean float NOT NULL default '0',"
                + "b635Mean float NOT NULL default '0',"
                + "b635sd float NOT NULL default '0',"
                + "f532Mean float NOT NULL default '0',"
                + "b532Mean float NOT NULL default '0',"
                + "b532sd float NOT NULL default '0',"
                + "snr635 double NOT NULL default '0',"
                + "snr532 double NOT NULL default '0',"
                + "f635 double NOT NULL default '0',"
                + "f532 double NOT NULL default '0',"
                + "ratio double NOT NULL default '0',"
                + "chrom varChar(45)  DEFAULT '',"
                + "chromStart int(10) unsigned default 0,"
                + "chromEnd int(10) unsigned  default 0,"
                + " ifExcluded int(1)  NOT NULL default '0',"
                + " controlType int NOT NULL default 0,"
                + "PRIMARY KEY (id),"
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd), "
                + "CONSTRAINT `fk_Data_Spots_" + tablePlatform + "` "
                + "FOREIGN KEY (`probeID` ) "
                + " REFERENCES `" + tablePlatform + "` "
                + " (`probeName`) "
                + " ON DELETE NO ACTION "
                + " ON UPDATE NO ACTION) ";

        return sql;
    }
    /**
     * insert into table
     * @param d
     * @return 
     */
    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData()
                + "(id,block,col, row, probeID, probeName, "
                + "f635Mean, b635Mean,  f532Mean, b532Mean,  "
                + "snr635, snr532, f635, f532, ratio, chrom, chromStart, chromEnd, "
                + "ifExcluded, controlType ) "
                + "values( "
                + this.getIid() + "," + this.getBlock() + ", "
                + this.getColumn() + "," + this.getRow() + ","
                + "\'" + this.getId() + "\'" + ","
                + "\'" + this.getName() + "\'" + ","
                + this.f635Mean + ", " + this.b635Mean + ", "
                + this.f532Mean + "," + this.b532Mean + ","
                + this.snr635 + "," + this.snr532 + ","
                + this.f635 + "," + this.f532 + ","
                + this.getRatio() + ","
                + "\'" + this.getChrom() + "\'" + ","
                + this.getChromStart() + "," + this.getChromEnd() + " , "
                + this.isExcluded() + ", " + this.controlFlag + ")");

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
}
