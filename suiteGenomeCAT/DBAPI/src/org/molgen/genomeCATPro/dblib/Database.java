package org.molgen.genomeCATPro.dblib;

/**
 * * @(#)Database.java * * This program is free software; you can redistribute
 * it and/or * modify it under the terms of the GNU General Public License * as
 * published by the Free Software Foundation; either version 2 * of the License,
 * or (at your option) any later version, * provided that any use properly
 * credits the author. * This program is distributed in the hope that it will be
 * useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the * GNU General
 * Public License for more details at http://www.gnu.org * *
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * todo: different db connecting infos include port into url
 */
public class Database {
// holds connection parameter for each database

    private static final Vector<Observer> obs = new Vector<>();
    static long lastinformed = 0;
    static HashMap<String, DBParams> dbmap = new HashMap<String, DBParams>();
    static DBParams params;
    

    public static void setDBParams(String dbalias, String db, String host, String port, String user, String pwd) {
        DBParams p = new DBParams(host, db, port, user, pwd);
        dbmap.put(db, p);
    }

    /**
     *
     * @param db
     * @return
     */
    public static DBParams getDBParams(String db) {
        DBParams p;
        p = dbmap.get(db);
        if (p == null) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "No connection information for {0}", db);
            throw new RuntimeException("No connection information for " + db);
        }
        return p;
    }

    public static void addObserver(java.util.Observer o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    static void inform(String message) {

        if ((java.lang.System.currentTimeMillis() - lastinformed) < 9000) {
            return;
        }
        lastinformed = System.currentTimeMillis();
        Object[] arrLocal;
        synchronized (obs) {

            arrLocal = obs.toArray();

            for (int i = arrLocal.length - 1; i >= 0; i--) {
                ((Observer) arrLocal[i]).update(null, message);
            }

        }

    }

    /**
     * get Connection to database jdbc parameter set:
     * jdbcCompliantTruncation=false
     *
     * @param db
     * @return
     */
    @SuppressWarnings("empty-statement")
    public static Connection getDBConnection(String db) {
        //Database.inform("get Database Connection");
        //System.out.println("get DB Connection for " + db);
        try {
            Connection con = null;
            Logger.getLogger(Database.class
                    .getName()).log(Level.INFO, "GetConnection for {0}", db);

            DBParams p = Database.getDBParams(db);
            // p = dbmap.get(db);

            String dbUrl = "jdbc:mysql://" + p.host + ":" + p.port + "/" + p.database
                    + "?user=" + p.user + "&password=" + p.password + "&autoReconnect=true&jdbcCompliantTruncation=false&rewriteBatchedStatements=true";

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(dbUrl);
            //Database.inform("connected to " + p.database + "@" + p.host);
            //Database.informed = true;
            return con;

        } //kt 06102016 log status of database connection
        catch (SQLException e) {
            Logger.getLogger(Database.class
                    .getName()).log(Level.SEVERE, "getDBConnection", e);
            //Database.informed = false;
            Database.inform("connection to database failed!");
            throw new RuntimeException("no Database connection");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Logger.getLogger(Database.class
                    .getName()).log(Level.SEVERE, "getDBConnection", e);
            //Database.informed = false;
            Database.inform("connection to database failed!");
            throw new RuntimeException("no Database Driver installation");

        }

    }
    /**
     * get Connection to database jdbc parameter set:
     * jdbcCompliantTruncation=false
     *
     * @param host
     * @param database
     * @param user
     * @param password
     * @return
     */

    /*
     public static Connection getDBConnection(String host, String database, String user, String password) {
     Connection con = null;
     try {       
    
     String dbUrl = "jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password=" + password + "&autoReconnect=true" + "&jdbcCompliantTruncation=false";
     System.out.println("getConnection with " + dbUrl);
     Class.forName("com.mysql.jdbc.Driver").newInstance();
     con = DriverManager.getConnection(dbUrl);
     return con;
     } catch (Exception e) {
     e.printStackTrace();
     throw new RuntimeException("no Database connection");
     }
    
     }
     */
}
