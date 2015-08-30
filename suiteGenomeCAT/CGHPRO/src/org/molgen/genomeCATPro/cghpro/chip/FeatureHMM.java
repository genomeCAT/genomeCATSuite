package org.molgen.genomeCATPro.cghpro.chip;
/**
 * @name FeatureHMM
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.data.FeatureImpl;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
/**
 * 
 * 
 */
public class FeatureHMM extends FeatureImpl {

    public final static String ICON_PATH_CBS = "org/molgen/genomeCATPro/cghpro/chip/cbs16.gif";
    double predictedValue = 0;

    public FeatureHMM() {
    }

    public FeatureHMM(Long iid, String name,
            String chrom, long chromStart, long chromEnd,
            double ratio, double predictedRatio, int count, int ifAberrant) {

        super(chrom, chromStart, chromEnd);
        this.iid = iid;
        this.setId(id);
        this.setRatio(ratio);
        this.setPredictedValue(predictedRatio);
        this.setCount(count);
        this.ifAberrant = ifAberrant;
    }

    public double getPredictedValue() {
        return predictedValue;
    }

    public void setPredictedValue(double predictedValue) {
        this.predictedValue = predictedValue;
    }

    @Override
    public String getCreateTableSQL(Data d) {
        String sql =
                " CREATE TABLE " + d.getTableData() + " ( " +
                "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
                "name varchar(255) NOT NULL, " +
                "chrom varChar(45) NOT NULL," +
                "chromStart int(10) unsigned NOT NULL," +
                "chromEnd int(10) unsigned NOT NULL," +
                "ratio DOUBLE, " +
                "predictedRatio DOUBLE, " +
                "count int , " +
                "PRIMARY KEY (id)," +
                "INDEX (chrom (5) ), " +
                "INDEX (chromStart ), " +
                "INDEX (chromEnd) ) " +
                "TYPE=MyISAM";
        return sql;
    }

    @Override
    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData() +
                "(name, chrom, chromStart, chromEnd, ratio, predictedRatio, count ) " +
                "values( " +
                "\'" + this.getId() + "\',\'" + this.getChrom() + "\'," +
                "\'" + this.getChromStart() + "\',\'" + this.getChromEnd() + "\'," +
                "\'" + this.getRatio() + "\',\'" + this.getPredictedValue() + "\'," +
                "\'" + this.getCount() + "\' )");
    }

    @Override
    public String getIconPath() {
        return ICON_PATH_CBS;
    }

    /**
     * integrate all features with same value into one segment
     * throws Exception for error on one row
     * @param resultFile
     * @return
     */
    public static Hashtable<String, Vector<? extends Feature>> loadFromHMMFile(String resultFile) throws Exception {
        Hashtable<String, Vector<? extends Feature>> data =
                new Hashtable<String, Vector<? extends Feature>>();
        /*
        "0 Clone"	
        "1 Target"	
        "2 Chrom"	
        "3 start"	
        "4 end"	
        "5 log2Ratio"	
        "6 X1"	chrom
        "7 X2"	kb
        "8 X3"	1 state
        "9 X4"	2 smoothed value for a clone
        "10 X5"	3 probability of being in a state
        "11 X6"	4 predicted value of a state
        "12 X7"	5 dispersion
        "13 X8"	6 observed value
        "14 y.vecMerged"	Vector with merged values.
        
        
        column 4 = predicted value of a state ~ state medians
        column 6 = observed value ~ log2Ratios
        column 2 = smoothed value for a clone ~ state medians weighted by the estimated probability of being in each state
         */
        try {


            // FeatureHMM newF = null;
            FeatureHMM lastF = null;
            BufferedReader input = new BufferedReader(new FileReader(resultFile));
            String line;

            int index = 0;
            int _count = 0;
            String _chrom = "";
            double _mergedValue = 0.0;
            double _segmentValue = 0.0;
            long _start = 0;
            long _end = 0;

            while ((line = input.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] par = line.split("\t");

                _chrom = RegionLib.fromIntToChr(Integer.parseInt(par[2]));

                if (!data.containsKey(_chrom)) {
                    data.put(_chrom, new Vector<FeatureHMM>());
                }
                _mergedValue = new Double(par[14]);
                _segmentValue = new Double(par[11]);
                _start = new Integer(par[3]);
                _end = new Integer(par[4]);


                if (lastF != null && _chrom.contentEquals(lastF.chrom) && _mergedValue == lastF.ratio && _segmentValue == lastF.predictedValue) {
                    if (_end > lastF.chromEnd) {
                        lastF.setChromEnd(_end);
                    }
                    lastF.setCount(++_count);
                    if (!((Vector<FeatureHMM>) data.get(lastF.chrom)).contains(lastF)) {
                        ((Vector<FeatureHMM>) data.get(lastF.chrom)).add(lastF);
                    }
                //((Vector<FeatureHMM>) data.get(_chrom)).add(lastF);
                } else {
                    _count = 1;

                    lastF = new FeatureHMM();
                    lastF.iid = ++index;
                    lastF.setId("HMM" + (index));
                    //lastF.setName("segment" + (index));
                    lastF.setChrom(_chrom);
                    lastF.setChromStart(_start);
                    lastF.setChromEnd(_end);
                    lastF.setRatio(_mergedValue);
                    lastF.setCount(_count);
                    lastF.setPredictedValue(_segmentValue);

                }
            }
            if (lastF != null && !((Vector<FeatureHMM>) data.get(_chrom)).contains(lastF)) {
                ((Vector<FeatureHMM>) data.get(_chrom)).add(lastF);
            }
            input.close();
        } catch (Exception e) {
            // publish("There is error reading results! " + exception.getMessage());
            Logger.getLogger(FeatureHMM.class.getName()).log(Level.SEVERE,
                    "There is error reading results!", e);
            throw e;
        }
        return data;
    }

    /**
     * load data from db - schema specific sql
     * @param d - Data Entity
     * @return List of Features
     * @throws java.lang.Exception
     */
    @Override
    public List<? extends Feature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(FeatureHMM.class.getName()).log(Level.INFO, "loadFromDB");
        List<FeatureImpl> list = new Vector<FeatureImpl>();
        FeatureImpl f = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "Select id, name, chrom, chromStart, chromEnd, ratio, predictedRatio, count " +
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by chrom, chromStart");

            String _chrom = "";
            while (rs.next()) {
                _chrom = rs.getString("chrom");
                f = new FeatureHMM(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getDouble("ratio"),
                        rs.getDouble("predictedRatio"),
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
}
