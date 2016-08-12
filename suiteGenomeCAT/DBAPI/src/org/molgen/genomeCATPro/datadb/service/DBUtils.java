package org.molgen.genomeCATPro.datadb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;

/**
 * @name DBUtils
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
public class DBUtils {

    public static Vector<String> getCols(String tableName) {
        Vector<String> vValues = new Vector<String>();
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        try {
            Statement s = con.createStatement();
            String sqlstmt = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS "
                    + "WHERE TABLE_NAME= \'" + tableName + "\' ";
            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                vValues.add(rs.getString(1));
            }
        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }

    /**
     * return ith rows of table , or all if i < 0
     *
     * @param i
     * @param tableName
     * @return
     */
    public static Vector<Vector<String>> getData(int i, String tableName) {
        Vector<Vector<String>> vValues = new Vector<Vector<String>>();
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        try {
            Statement s = con.createStatement();
            String sqlstmt = "SELECT * FROM " + tableName;
            int ii = 0;
            ResultSet rs = s.executeQuery(sqlstmt);
            while ((i < 0 || ii++ < i) && rs.next()) {
                Vector<String> v = new Vector<String>();
                int j = rs.getMetaData().getColumnCount();
                for (int jj = 1; jj <= j; jj++) {
                    v.add(rs.getString(jj));
                }
                vValues.add(v);
            }
        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }

    /**
     * calculate median
     *
     * @param tableName
     * @param colratio
     * @return
     * @throws java.lang.Exception
     */
    static public double getMedian(String tableName, String colratio) throws Exception {
        return DBUtils.getQuantile(tableName, colratio, 0.5);

    }

    static public void addPositionAtTable(String tableName) throws Exception {

        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        try {
            Statement s = con.createStatement();
            String sqlstmt = "select  if (EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS "
                    + "WHERE TABLE_NAME= \'" + tableName + "\' AND column_name='gc_position'), 1, 0 )";

            ResultSet rs = s.executeQuery(sqlstmt);
            boolean suc;
            rs.next();

            if (rs.getInt(1) == 0) {
                sqlstmt = "ALTER TABLE " + tableName + " ADD gc_position LINESTRING";
                suc = s.execute(sqlstmt);
                Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, sqlstmt + " update: " + suc);

                sqlstmt = "UPDATE " + tableName + " SET gc_position =( CASE chrom WHEN \'chrX\' THEN  LINESTRING(Point(23,chromStart), Point(23, chromEnd)) WHEN  \'chrY\' THEN LINESTRING(Point(24,chromStart), Point(24, chromEnd)) ELSE  LINESTRING(Point(substring(chrom,4),chromStart), Point(substring(chrom,4), chromEnd)) END )";
                suc = s.execute(sqlstmt);
                Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, sqlstmt + " update: " + suc);
                sqlstmt = "ALTER TABLE " + tableName + " change gc_position gc_position LINESTRING NOT NULL";
                suc = s.execute(sqlstmt);
                Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, sqlstmt + " update: " + suc);
                sqlstmt = "ALTER TABLE " + tableName + " ADD SPATIAL INDEX(gc_position)";
                suc = s.execute(sqlstmt);

                Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, sqlstmt + " update: " + suc);
                sqlstmt = "ANALYZE TABLE " + tableName;
                suc = s.execute(sqlstmt);

                Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, sqlstmt + " update: " + suc);

            } else {
                Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, sqlstmt + " addPositionAtTable:  gc_position already added");

            }

        } catch (Exception ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "addPositionAtTable", ex);

            throw ex;
        }
    }

    /**
     * calculate median
     *
     * @param tableName
     * @param colratio
     * @param q quantile from 0-1
     * @return
     * @throws java.lang.Exception
     */
    static public double getQuantile(String tableName, String colratio, double q) throws Exception {
        try {
            Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("select count(*) from " + tableName);

            rs.next();

            int count = rs.getInt(1);
            double median = 0;
            rs.close();
            if (count % (1 / q) == 0) {
                rs = s.executeQuery("select " + colratio + " from " + tableName
                        + " order by " + colratio + " limit " + (int) (count * q) + ",1");
                if (!rs.next()) {
                    return 0;
                }

                median = rs.getDouble(1);
            } else {
                rs = s.executeQuery("select " + colratio + " from " + tableName
                        + " order by " + colratio + " limit " + (int) (count * q) + ",2");
                int i = 0;
                while (rs.next()) {
                    i++;
                    median
                            += rs.getDouble(1);
                }

                median /= i;
            }

            return median;
        } catch (Exception ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "getMedian", ex);

            throw ex;
        }
    }

    static public int getMeanLengthPosition(String tableName) throws Exception {
        try {
            Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT avg(GLength(gc_position)) FROM " + tableName);

            rs.next();

            return rs.getInt(1);

        } catch (Exception ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "getMeanLengthPosition", ex);

            throw ex;
        }
    }

    public static Vector<String> getAllArrayMethods() {

        Vector<String> vValues = new Vector<String>();
        for (Defaults.Method method : Defaults.Method.values()) {
            vValues.add(method.toString());
        }

        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        String sqlstmt = "select distinct method from PlatformDetail";
        String value;

        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                value = rs.getString(1);
                if (!vValues.contains(value)) {
                    vValues.add(value);
                }

            }

        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }

    public static Vector<String> getAllDataTypes() {
        //String[] values = null;
        Vector<String> vValues = new Vector<String>();
        for (Defaults.DataType value : Defaults.DataType.values()) {
            vValues.add(value.toString());
        }

        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        String sqlstmt = "select distinct dataType from ExperimentList";
        String value;

        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                value = rs.getString(1);
                if (!vValues.contains(value)) {
                    vValues.add(value);
                }

            }

        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "getAllDataTypes", e);
        }
        return vValues;
    }

    public static Vector<String> getAllArrayTypes() {
        //String[] values = null;
        Vector<String> vValues = new Vector<String>();
        for (Defaults.Type value : Defaults.Type.values()) {
            vValues.add(value.toString());
        }

        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        String sqlstmt = "select distinct type from PlatformDetail";
        String value;

        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                value = rs.getString(1);
                if (!vValues.contains(value)) {
                    vValues.add(value);
                }

            }

        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }

    public static Vector<String> getAllArrayNames() {
        //String[] values = null;
        Vector<String> vValues = new Vector<String>();

        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        String sqlstmt = "select distinct name from PlatformDetail";
        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                vValues.add(rs.getString(1));
            }

        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }

    public static Vector<String> getAllReleases() {
        Vector<String> vValues = new Vector<String>();
        for (Defaults.GenomeRelease method : Defaults.GenomeRelease.values()) {
            vValues.add(method.toString());
        }

        return vValues;
    }

    public static String getAnnoTableForRelease(
            String table, GenomeRelease release) {
        try {

            Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            String sql = "Select tableData from AnnotationList where name = ? "
                    + " and genomeRelease = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, table);
            ps.setString(2, release.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE,
                    "getAnnoTableForRelease", ex);
        }
        return null;
    }

    public static Vector<String> getStudies() {
        //String[] values = null;
        Vector<String> vValues = new Vector<String>();
        //select distinct Study.name from Study, User where Study.idOwner = User.UserID and User.name = "tebel"
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        String sqlstmt = "select distinct Study.name from Study ";

        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                vValues.add(rs.getString(1));
            }

        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }

    public static Vector<String> getAllUser() {
        //String[] values = null;
        Vector<String> vValues = new Vector<String>();
        //select distinct Study.name from Study, User where Study.idOwner = User.UserID and User.name = "tebel"
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        String sqlstmt = "select distinct User.name from User ";

        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                vValues.add(rs.getString(1));
            }

        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }

    public static Vector<String> getAllSamples() {
        //String[] values = null;
        Vector<String> vValues = new Vector<String>();
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        String sqlstmt = "select distinct SampleDetail.name from SampleDetail ";

        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                vValues.add(rs.getString(1));
            }

        } catch (Exception e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return vValues;
    }
}
