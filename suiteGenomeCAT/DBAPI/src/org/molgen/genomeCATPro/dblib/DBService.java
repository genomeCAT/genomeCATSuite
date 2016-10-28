package org.molgen.genomeCATPro.dblib;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static org.molgen.genomeCATPro.dblib.Database.lastinformed;

/**
 * @name DBService Handle DB Connection via one Persistance Unit
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Oct 6, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
public class DBService {

    private static final Vector<Observer> obs = new Vector<>();
    static long lastinformed = 0;
    static String defaultPu = "genomeCATPU";
    static HashMap<String, EntityManagerFactory> factories = null;
    static Map<String, String> properties = new HashMap<String, String>();

    public static boolean setConnection(String host, String port, String db, String user, String pwd) {
        return DBService.setConnection(defaultPu, host, port, db, user, pwd);
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
     * set DB Connection Parameter, try to connect if no connection could be
     * established an log message is created
     *
     */
    public static boolean setConnection(String pu, String host, String port, String db, String user, String pwd) {
        properties.clear();
        DBService.properties.put("javax.persistence.jdbc.user", user);
        DBService.properties.put("javax.persistence.jdbc.password", pwd);
        DBService.properties.put("javax.persistence.jdbc.url", "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true");
        DBService.properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        Logger.getLogger(DBService.class.getName()).log(Level.INFO,
                " set connection: host: " + host + " port: " + port + " db: " + db + " user: " + user);
        setFactory(pu, (EntityManagerFactory) null);
        if (DBService.getEntityManger(pu) == null) {
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, "Connection failed");
            return false;
        }
        return true;
    }

    static EntityManagerFactory getFactory(String pu) {
        if (factories == null) {
            factories = new HashMap<String, EntityManagerFactory>();
        }
        if (!DBService.factories.containsKey(pu)) {
            DBService.factories.put(pu, null);
        }
        return DBService.factories.get(pu);
    }

    static void setFactory(String pu, EntityManagerFactory emf) {
        if (factories == null) {
            factories = new HashMap<String, EntityManagerFactory>();

        }

        DBService.factories.put(pu, emf);

    }

    public static boolean testConnection(String host, String port, String db, String user, String pwd) {
        return DBService.testConnection(defaultPu, host, port, db, user, pwd);
    }

    public static boolean testConnection(String pu, String host, String port, String db, String user, String pwd) {
        DBService.setFactory(pu, (EntityManagerFactory) null);

        Map<String, String> _properties = new HashMap<String, String>();
        Map<String, String> old = properties;

        _properties.put("javax.persistence.jdbc.user", user);
        _properties.put("javax.persistence.jdbc.password", pwd);
        _properties.put("javax.persistence.jdbc.url", "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true");
        _properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");

        properties = _properties;

        Logger.getLogger(DBService.class.getName()).log(Level.INFO,
                " test connection: host: " + host + " port: " + port + " db: " + db + " user: " + user);

        if (DBService.getEntityManger(pu) == null) {
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, "Connection failed");
            DBService.properties = old;
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @return new EntityManager Instance
     */
    public static EntityManager getEntityManger() {
        return DBService.getEntityManger(defaultPu);
    }

    public static EntityManager getEntityManger(String pu) {
        EntityManager e = null;
        try {
            if (DBService.getFactory(pu) == null) {
                DBService.setFactory(pu, Persistence.createEntityManagerFactory(pu, properties));
            }
            e = DBService.getFactory(pu).createEntityManager();
            if (e == null) {
                //kt 06102016 log status of database connection
                Database.inform("connection to database failed!");
                Logger.getLogger(DBService.class.getName()).log(Level.SEVERE,
                        "getEntityManager", "no database connection");
                setFactory(pu, (EntityManagerFactory) null);
            }

        } catch (Exception ex) {
            //kt 06102016 log status of database connection
            Database.inform("connection to database failed!");
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE,
                    "getEntityManager", ex);
            setFactory(pu, (EntityManagerFactory) null);
        }
        return e;
    }
}
