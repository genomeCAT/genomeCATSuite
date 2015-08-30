package org.molgen.genomeCATPro.data;

/**
 * @name DataService
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
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.dblib.Database;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 * 
 * 010812    kt  add getValueFromFeature
 */
public class DataService {

    static Lookup.Template<Feature> tmplFeature = new Lookup.Template<Feature>(
            org.molgen.genomeCATPro.data.Feature.class);

    public static Feature getFeatureClazz(String clazz) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(DataService.class.getName()).log(Level.INFO,
                "looking for datatype clazz " + clazz);


        Result<Feature> rslt = Lookup.getDefault().lookup(tmplFeature);
        for (Lookup.Item item : rslt.allItems()) {
            if (item.getType().getName().contentEquals(clazz)) {
                Logger.getLogger(DataService.class.getName()).log(Level.INFO,
                        "return: " + item.getDisplayName());
                return (Feature) item.getInstance();
            }
        }
        Logger.getLogger(DataService.class.getName()).log(Level.INFO,
                "return: " + FeatureImpl.class.getName());
        return new FeatureImpl();
    }
    /**
     * invoke field method on feature
     * @param fclazz
     * @param field
     * @return
     */
    public static boolean hasValue(Object o, String getField) {
        //Object f = DataService.getFeatureClazz(fclazz);
        //Class c = f.getClass();

        try {

            Class c = o.getClass();
            Method m = c.getMethod(getField, (Class[]) null);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        } catch (Exception ex) {
            Logger.getLogger(DataService.class.getName()).log(Level.SEVERE,
                    "", ex);
        }

        /*Method[] methodList = c.getMethods();
        for(int i=0; i < methodList.length; i++){
        if(methodList[i].getName().substring(3).equalsIgnoreCase(field))
        
        }*/
        return false;

    }
    /**
     * invoke field method on feature
     * @param fclazz
     * @param field
     * @return
     */
    public static Object getValue(Object o, String getField) {
        //Object f = DataService.getFeatureClazz(fclazz);
        //Class c = f.getClass();

        try {

            Class c = o.getClass();
            Method m = c.getMethod(getField, (Class[]) null);
            return m.invoke(o, (Object[]) null);
        } catch (Exception ex) {
            Logger.getLogger(DataService.class.getName()).log(Level.SEVERE,
                    "", ex);
        }

        /*Method[] methodList = c.getMethods();
        for(int i=0; i < methodList.length; i++){
        if(methodList[i].getName().substring(3).equalsIgnoreCase(field))
        
        }*/
        return null;

    }

    public static List<String> getFieldList(Object o) {
        //Object f = DataService.getFeatureClazz(fclazz);
        //Class c = f.getClass();
        Vector<String> list = new Vector<String>();
        try {

            Class c = o.getClass();
            Method[] methodList = c.getDeclaredMethods();
            for (int i = 0; i < methodList.length; i++) {
                if (methodList[i].getName().startsWith("get")) {
                    list.add(methodList[i].getName().substring(3));

                }
            }
            return list;
        } catch (Exception ex) {
            Logger.getLogger(DataService.class.getName()).log(Level.SEVERE,
                    "", ex);
        }


        return null;

    }

    public static void saveDataToDB(Data d, List<? extends Feature> data) throws SQLException, Exception {

        Connection con = null;
        Statement s = null;
        try {
            con = Database.getDBConnection(Defaults.localDB);
            s = con.createStatement();
        } catch (Exception ex) {
            Logger.getLogger(DataService.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }




        Logger.getLogger(DataService.class.getName()).log(Level.INFO,
                "New Data - table data: " + d.getTableData());

        // create Spot Tabelle
        Feature f = DataService.getFeatureClazz(d.getClazz());
        s.execute("DROP TABLE if EXISTS " + d.getTableData());
        String sql = f.getCreateTableSQL(d);

        Logger.getLogger(DataService.class.getName()).log(Level.INFO, sql);


        s.execute(sql);


        // read data -> insert into table
        // todo dye swap

        int error = 0;

        for (Feature current : data) {
            sql = current.getInsertSQL(d);

            try {
                s.execute(sql);

            } catch (SQLException sQLException) {
                Logger.getLogger(DataService.class.getName()).log(
                        Level.WARNING,
                        "not saved into " + d.getTableData() + "\n " + sql,
                        sQLException);
                ++error;

            }

        }
    }
}
