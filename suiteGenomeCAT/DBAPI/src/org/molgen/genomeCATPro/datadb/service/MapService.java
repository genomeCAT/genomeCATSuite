package org.molgen.genomeCATPro.datadb.service;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;

/**
 * @name MapService
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
public class MapService {

    static List<ServiceListener> _listener = new Vector<ServiceListener>();

    public static void addListener(ServiceListener al) {
        _listener.add(al);
    }

    public void removeListener(ServiceListener al) {
        _listener.remove(al);
    }

    public static void notifyListener() {
        for (ServiceListener s : _listener) {
            s.dbChanged();
        }
    }

    public static void perstistData(MapData dMap, EntityManager em) throws Exception {
        if (em == null) {
            em = DBService.getEntityManger();
        }
        Logger.getLogger(MapService.class.getName()).log(Level.INFO,
                "persistsData: ", dMap.getName());
        EntityTransaction userTransaction = null;
        try {
            userTransaction = em.getTransaction();
            if (!userTransaction.isActive()) {
                userTransaction.begin();
            }
            if (dMap.getOwner() == null) {

                dMap.setOwner(ExperimentService.getUser());

            }
            //Eintrag Tabelle SampleList und implizit experiment erzeugen

            em.persist(dMap);
            em.flush();
            em.refresh(dMap);

            if (em == null) {
                userTransaction.commit();
            }
            MapService.notifyListener();
        } catch (Exception ex) {
            Logger.getLogger(MapService.class.getName()).log(Level.SEVERE, "", ex);
            throw ex;

        } finally {
            if (em == null && userTransaction != null && userTransaction.isActive()) {
                userTransaction.rollback();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static List<MapData> getMapDataList(MapDetail p) throws Exception {
        List<MapData> list = null;

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        try {
            Query query = em.createQuery(
                    "SELECT m FROM MapData m "
                    + " where m.mapName = ?1 ");

            query.setParameter(1, p.getMapName());
            Logger.getLogger(MapService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            list = query.getResultList();
            return list;
        } catch (Exception e) {
            Logger.getLogger(MapService.class.getName()).log(Level.SEVERE,
                    "getMapDataList: ", e);
            throw e;
        } finally {
            em.close();
        }

    }

    @SuppressWarnings("unchecked")
    public static MapDetail getMapDetailByName(String name) throws Exception {

        List<MapDetail> list = null;

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {
            String sql = "select mapId, mapName, created, modified, mapType, "
                    + " tableData, genomeRelease "
                    + " from MapList "
                    + " where mapName = \'" + name + "\'"
                    + " group by mapName";

            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    MapDetail.class);

            list = (List<MapDetail>) q.getResultList();

            if (list.size() <= 0) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (Exception e) {
            Logger.getLogger(MapService.class.getName()).log(Level.SEVERE,
                    "getMapDetailByName", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<MapDetail> getMapList() throws Exception {

        List<MapDetail> list = null;

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {
            String sql = "select mapId, mapName, created, modified, mapType, "
                    + " tableData, genomeRelease "
                    + " from MapList "
                    + " group by mapName";

            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    MapDetail.class);

            list = (List<MapDetail>) q.getResultList();

            return list;

        } catch (Exception e) {
            Logger.getLogger(MapService.class.getName()).log(Level.SEVERE,
                    "getMapList", e);
            throw e;
        } finally {
            em.close();
        }
    }
}
