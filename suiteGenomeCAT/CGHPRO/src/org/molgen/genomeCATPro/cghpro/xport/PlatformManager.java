package org.molgen.genomeCATPro.cghpro.xport;
/**
 * @name PlatformManager
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;


public class PlatformManager {

    public static void doDelete(PlatformData s) throws Exception {
        // drop table
        // delete platform
        // check if experiments exists
        // 



        PlatformDetail d = s.getPlattform();
        EntityManager em = DBService.getEntityManger();
        EntityTransaction userTransaction = em.getTransaction();
        userTransaction.begin();

        try {
            Query query;
            query = em.createNativeQuery(
                    "DROP TABLE if EXISTS " + s.getTableData());
            query.executeUpdate();

            query = em.createQuery(
                    "DELETE FROM PlatformData d WHERE " +
                    "d.platformListID = :id");
            query.setParameter("id", s.getPlatformListID());


            int deleted = query.executeUpdate();

            Logger.getLogger(PlatformManager.class.getName()).log(Level.INFO,
                    "PlatformData " +
                    (deleted > 0 ? s.getName() : "none"));
            if (d != null) {
                d.removePlatformData(s);
            }
            return;
        } catch (Exception e) {
            Logger.getLogger(PlatformManager.class.getName()).log(Level.SEVERE, "", e);
            throw e;
        } finally {
            if (userTransaction != null && userTransaction.isActive()) {
                userTransaction.rollback();

            }
            em.close();
        }

    }

    @SuppressWarnings("empty-statement")
    public static void doConvert(PlatformData oldData, PlatformData newData, String filepath) throws Exception {
        // export chrom, start, stop id to external file

        // for each file column get table column
        // if found import, otherwise report error

        // create data
        // copy table
        // update table
        //s.getUpdateCount());

        oldData.getPlattform().addPlatformData(newData);
        newData.setOriginalFile(filepath);
        newData.initTableData();
        String bedTable = newData.getTableData() + "_tmp";
        EntityManager em = DBService.getEntityManger();
        Connection con = null;
        Statement s = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);
            s = con.createStatement();
        } catch (Exception ex) {
            Logger.getLogger(PlatformManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        filepath.replace("\\", "\\\\");
        try {

            // create tmp table with bed data
            s.execute("DROP TABLE if EXISTS " + bedTable);
            s.execute(" CREATE TABLE " + bedTable + " ( " +
                    "chrom varChar(255) NOT NULL," +
                    "chromStart int(10) unsigned NOT NULL," +
                    "chromEnd int(10) unsigned NOT NULL," +
                    "id varchar(255), " +
                    " PRIMARY KEY (id), " +
                    "INDEX (chrom, chromStart, chromEnd) " +
                    " ) TYPE=MyISAM");
            s.execute("LOAD DATA INFILE \'" + filepath + "\' INTO TABLE " + bedTable);

            int nofConvData = s.getUpdateCount();
            Logger.getLogger(PlatformManager.class.getName()).log(Level.INFO,
                    " doConvert: nof converted data rows: " + nofConvData);

            // create new data table only for items in bed table with 
            // content of old data table
            s.execute("DROP TABLE if EXISTS " + newData.getTableData());
            s.execute("CREATE TABLE " + newData.getTableData() + " LIKE " + oldData.getTableData());
            String sql = "INSERT INTO " + newData.getTableData() +
                    "  SELECT o.* FROM " + oldData.getTableData() +
                    " as o , " + bedTable + " as bed " +
                    " where o.id = bed.id";

            Logger.getLogger(PlatformManager.class.getName()).log(Level.INFO, sql);
            s.execute(sql);
            int nofInsData = s.getUpdateCount();

            Logger.getLogger(PlatformManager.class.getName()).log(Level.INFO,
                    "inserted: " + nofInsData);


            // update new data with chrom positions from bed table
            sql = " Update " + newData.getTableData() + " as newd, " +
                    bedTable + " as bed " +
                    " set newd.chrom = bed.chrom , newd.chromStart = bed.chromStart, " +
                    " newd.chromEnd = bed.chromEnd " +
                    " where newd.id = bed.id";
            s.execute(sql);
            int nofUpdData = s.getUpdateCount();
            Logger.getLogger(PlatformManager.class.getName()).log(Level.INFO,
                    "updated: " + nofUpdData);
            sql = "SELECT count(*) from " + newData.getTableData();
            ResultSet rs = s.executeQuery(sql);

            rs.next();

            newData.setNofSpots(rs.getInt(1));




            // make newdata as PlatformData persistent
            em.getTransaction().begin();
            em.persist(newData);
            em.flush();


            em.refresh(newData);
            em.getTransaction().commit();
            // check gegen convData
            oldData.getPlattform().addPlatformData(newData);
        } catch (Exception ex) {
            Logger.getLogger(PlatformManager.class.getName()).log(Level.SEVERE, null, ex);
            if (con != null) {
                try {
                    s = con.createStatement();
                    s.execute("DROP TABLE if EXISTS " + newData.getTableData());
                } catch (Exception ie) {
                    Logger.getLogger(PlatformManager.class.getName()).log(Level.SEVERE, null, ie);
                }
            }
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            } else {
                try {
                    if (newData.getPlatformListID() != null) {
                        Logger.getLogger(PlatformManager.class.getName()).log(Level.INFO,
                                "doConvertPlatform: find Plattform  " + newData.getPlatformListID());
                        em.remove(em.find(PlatformData.class, newData.getPlatformListID()));
                    }
                    Logger.getLogger(PlatformManager.class.getName()).log(Level.SEVERE,
                            "doConvert", ex);
                    throw ex;
                } catch (Exception ie) {
                    Logger.getLogger(PlatformManager.class.getName()).log(Level.SEVERE, null, ie);
                }
            }
            oldData.getPlattform().removePlatformData(newData);
            throw ex;
        } finally {

            if (con != null && bedTable != null) {
                try {
                    s = con.createStatement();
                    s.execute("DROP TABLE if EXISTS " + bedTable);
                } catch (Exception ie) {
                    Logger.getLogger(PlatformManager.class.getName()).log(Level.SEVERE, null, ie);
                }

            }
            if (con != null) {
                con.close();
            }
        }
    }
}
