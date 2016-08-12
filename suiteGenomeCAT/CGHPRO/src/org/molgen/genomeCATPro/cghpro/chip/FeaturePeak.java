package org.molgen.genomeCATPro.cghpro.chip;

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
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.data.FeatureImpl;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.IFeature;

/**
 * @name FeaturePeak
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
public class FeaturePeak extends FeatureImpl {

    //public final static String ICON_PATH_CBS = "org/molgen/genomeCATPro/cghpro/chip/cbs16.gif";
    double sumRatios = 0;

    public FeaturePeak() {
    }

    public FeaturePeak(Long iid, String name,
            String chrom, long chromStart, long chromEnd,
            double peakRatio, double sumRatio, int count, int ifAberrant) {

        super(chrom, chromStart, chromEnd);
        this.iid = iid;
        this.setId(id);
        this.setRatio(ratio);
        this.setSumRatios(sumRatios);
        this.setCount(1);
        this.ifAberrant = ifAberrant;
    }

    public double getSumRatios() {
        return sumRatios;
    }

    public void setSumRatios(double sumRatios) {
        this.sumRatios = sumRatios;
    }

    @Override
    public String getCreateTableSQL(Data d) {
        String sql
                = " CREATE TABLE " + d.getTableData() + " ( "
                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "name varchar(255) NOT NULL, "
                + "chrom varChar(45) NOT NULL,"
                + "chromStart int(10) unsigned NOT NULL,"
                + "chromEnd int(10) unsigned NOT NULL,"
                + "ratio DOUBLE, "
                + "sumRatio DOUBLE, "
                + "count int , "
                + "PRIMARY KEY (id),"
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd) ) "
                + "TYPE=MyISAM";
        return sql;
    }

    @Override
    public String getInsertSQL(Data d) {
        return new String(
                "INSERT INTO " + d.getTableData()
                + "(name, chrom, chromStart, chromEnd, ratio, sumRatio, count ) "
                + "values( "
                + "\'" + this.getId() + "\',\'" + this.getChrom() + "\',"
                + "\'" + this.getChromStart() + "\',\'" + this.getChromEnd() + "\',"
                + "\'" + this.getRatio() + "\',\'" + this.getSumRatios() + "\',"
                + "\'" + this.getCount() + "\' )");
    }

    @Override
    public String getIconPath() {
        return "org/molgen/genomeCATPro/cghpro/chip/cbs16.gif";
    }

    public static Hashtable<String, Vector<? extends IFeature>> loadFromFixedStepFile(String resultFile) throws Exception {
        Hashtable<String, Vector<? extends IFeature>> data
                = new Hashtable<String, Vector<? extends IFeature>>();

        try {
            /*          
            fixedStep chrom=chr19 start=49307401 step=300 span=200
            1000
            900
            800
            700
             */

            // FeatureHMM newF = null;
            FeaturePeak peak = null;
            BufferedReader input = new BufferedReader(new FileReader(resultFile));
            String line;

            int index = 0;

            String _chrom = "";

            long _start = 0;
            int _step = 0;
            int _span = 0;
            double _v = 0;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.contentEquals("")) {
                    continue;
                }
                if (line.startsWith("fixed")) {
                    int s = line.indexOf("chrom=") + "chrom=".length();
                    int e = line.substring(s).indexOf(" ");
                    _chrom = line.substring(s, s + e);
                    s = line.indexOf("start=") + "start=".length();
                    e = line.substring(s).indexOf(" ");
                    _start = Integer.parseInt(line.substring(s, s + e));
                    s = line.indexOf("step=") + "step=".length();
                    e = line.substring(s).indexOf(" ");
                    _step = Integer.parseInt(line.substring(s, s + e));
                    s = line.indexOf("span=") + "span=".length();
                    e = line.substring(s).indexOf(" ");
                    if (e > 0) {
                        _span = Integer.parseInt(line.substring(s, s + e));
                    } else {
                        _span = Integer.parseInt(line.substring(s));
                    }

                    if (!data.containsKey(_chrom)) {
                        data.put(_chrom, new Vector<FeaturePeak>());
                    }

                    continue;
                }
                // DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
                // otherSymbols.setDecimalSeparator('.');
                _v = new Double(line);
                if (_v == 0) {
                    _start += _step;
                    continue;
                } else {
                    peak = new FeaturePeak();
                    peak.iid = ++index;
                    peak.setId("BIN" + (index));
                    //peak.setName("segment" + (index));
                    peak.setChrom(_chrom);
                    peak.setChromStart(_start);

                    peak.setChromEnd(_start + _span - 1);
                    peak.setRatio(_v);
                    peak.setCount(1);
                    ((Vector<FeaturePeak>) data.get(_chrom)).add(peak);
                    _start += _step;
                }
            }
            input.close();
        } catch (Exception e) {
            // publish("There is error reading results! " + exception.getMessage());
            Logger.getLogger(FeaturePeak.class.getName()).log(Level.SEVERE,
                    "There is error reading results!", e);
            throw e;
        }
        return data;
    }

    /**
     *
     * throws Exception for error on one row
     *
     * @param resultFile
     * @return
     */
    public static Hashtable<String, Vector<? extends IFeature>> loadFromPeakFile(String resultFile) throws Exception {
        Hashtable<String, Vector<? extends IFeature>> data
                = new Hashtable<String, Vector<? extends IFeature>>();

        try {
            FeaturePeak peak = null;
            BufferedReader input = new BufferedReader(new FileReader(resultFile));
            String line;

            int index = 0;

            String _chrom = "";
            double _peakRatio = 0.0;
            double _sumRatio = 0.0;
            long _start = 0;
            long _end = 0;

            while ((line = input.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("track")) {
                    continue;
                }
                String[] par = line.split("\t");
                /* chr1	16389	16571	2054

                 */
                _chrom = par[0];

                if (!data.containsKey(_chrom)) {
                    data.put(_chrom, new Vector<FeaturePeak>());
                }

                //_peakRatio = new Double(par[7]);
                _sumRatio = new Double(par[3]);
                _start = new Integer(par[1]);
                _end = new Integer(par[2]);

                peak = new FeaturePeak();
                peak.iid = ++index;
                peak.setId("PEAK_" + (index));
                //peak.setName("segment" + (index));
                peak.setChrom(_chrom);
                peak.setChromStart(_start);
                peak.setChromEnd(_end);
                peak.setRatio(_sumRatio);
                peak.setCount(1);
                peak.setSumRatios(_sumRatio);

                ((Vector<FeaturePeak>) data.get(_chrom)).add(peak);

            }

            input.close();
        } catch (Exception e) {
            // publish("There is error reading results! " + exception.getMessage());
            Logger.getLogger(FeaturePeak.class.getName()).log(Level.SEVERE,
                    "There is error reading results!", e);
            throw e;
        }
        return data;
    }

    public static Hashtable<String, Vector<? extends IFeature>> loadFromRINGOFile(String resultFile) throws Exception {
        Hashtable<String, Vector<? extends IFeature>> data
                = new Hashtable<String, Vector<? extends IFeature>>();

        try {
            FeaturePeak peak = null;
            BufferedReader input = new BufferedReader(new FileReader(resultFile));
            String line;

            int index = 0;
            int _count = 0;
            String _chrom = "";
            double _peakRatio = 0.0;
            double _sumRatio = 0.0;
            long _start = 0;
            long _end = 0;

            while ((line = input.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] par = line.split("\t");

                _chrom = par[1];

                if (!data.containsKey(_chrom)) {
                    data.put(_chrom, new Vector<FeaturePeak>());
                }
                /*              0   
                1 AG0800.sm.chr1.cher1	
                2   chr1	
                3   144926868	
                4   144935072		
                6   AG0800.sm		
                8   5.2176631436857	
                9   104.853214492599
                 */

                _peakRatio = new Double(par[7]);
                _sumRatio = new Double(par[8]);
                _start = new Integer(par[2]);
                _end = new Integer(par[3]);

                _count = 1;

                peak = new FeaturePeak();
                peak.iid = ++index;
                peak.setId("RINGO_" + (index));
                //peak.setName("segment" + (index));
                peak.setChrom(_chrom);
                peak.setChromStart(_start);
                peak.setChromEnd(_end);
                peak.setRatio(_peakRatio);
                peak.setCount(_count);
                peak.setSumRatios(_sumRatio);

                ((Vector<FeaturePeak>) data.get(_chrom)).add(peak);

            }

            input.close();
        } catch (Exception e) {
            // publish("There is error reading results! " + exception.getMessage());
            Logger.getLogger(FeaturePeak.class.getName()).log(Level.SEVERE,
                    "There is error reading results!", e);
            throw e;
        }
        return data;
    }

    /**
     * load data from db - schema specific sql
     *
     * @param d - Data Entity
     * @return List of Features
     * @throws java.lang.Exception
     */
    @Override
    public List<? extends IFeature> loadFromDB(Data d) throws Exception {
        Logger.getLogger(FeaturePeak.class.getName()).log(Level.INFO, "loadFromDB");
        List<FeatureImpl> list = new Vector<FeatureImpl>();
        FeatureImpl f = null;
        Connection con = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "Select id, name, chrom, chromStart, chromEnd, ratio, sumRatio, count "
                    + " from " + d.getTableData()
                    + " where chrom != \'\' "
                    + " order by chrom, chromStart");

            String _chrom = "";
            while (rs.next()) {
                _chrom = rs.getString("chrom");
                f = new FeaturePeak(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getDouble("ratio"),
                        rs.getDouble("sumRatio"),
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
