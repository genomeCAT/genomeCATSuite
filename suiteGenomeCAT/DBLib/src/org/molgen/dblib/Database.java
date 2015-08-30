package org.molgen.dblib;

/** * @(#)Database.java
 * * 
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License
 * * as published by the Free Software Foundation; either version 2
 * * of the License, or (at your option) any later version,
 * * provided that any use properly credits the author.
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * * */
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * todo: different db connecting infos
 * include port into url
 */
public class Database {
// holds connection parameter for each database

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
        DBParams p = (DBParams) dbmap.get(db);
        if (p == null) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE,
                    "No connection information for " + db);
            throw new RuntimeException("No connection information for " + db);
        }
        return p;
    }

    /** get Connection to database  
     *  jdbc parameter set: jdbcCompliantTruncation=false
     * @param db
     * @return
     */
    @SuppressWarnings("empty-statement")
    public static Connection getDBConnection(String db) {


        //System.out.println("get DB Connection for " + db);
        Connection con = null;
        try {

            Logger.getLogger(Database.class.getName()).log(Level.INFO, "GetConnection for " + db);

            DBParams p = (DBParams) dbmap.get(db);

            String dbUrl = "jdbc:mysql://" + p.host + ":" + p.port + "/" + p.database + 
                    "?user="+p.user + "&password="+p.password + "&autoReconnect=true&jdbcCompliantTruncation=false&rewriteBatchedStatements=true";
              
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(dbUrl);
            //con = DriverManager.getConnection(dbUrl, p.user, p.password);
            return con;
        } catch (Exception e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "getDBConnection", e);
            throw new RuntimeException("no Database connection");
        }
    }
    /** get Connection to database
     *  jdbc parameter set: jdbcCompliantTruncation=false
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