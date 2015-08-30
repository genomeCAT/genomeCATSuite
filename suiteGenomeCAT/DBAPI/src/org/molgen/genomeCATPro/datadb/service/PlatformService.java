package org.molgen.genomeCATPro.datadb.service;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import javax.persistence.Query;
import org.molgen.dblib.DBService;
import org.molgen.genomeCATPro.common.Defaults;

import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;

/**
 * @name Platform Service
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package.
 * Copyright Aug 27, 2010 Katrin Tebel <tebel at molgen.mpg.de>.
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
public class PlatformService {

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

    @SuppressWarnings("unchecked")
    public static List<PlatformData> listPlatformData(PlatformDetail e) {
        EntityManager em = DBService.getEntityManger();
        Query query = em.createQuery(
                "SELECT p FROM PlatformData p " +
                " where p.platform.platformID = ?1 ");

        query.setParameter(1, e.getPlatformID());
        Logger.getLogger(PlatformService.class.getName()).log(Level.INFO,
                query.getResultList().toString());
        List<PlatformData> list = (List<PlatformData>) query.getResultList();
        for (PlatformData d : list) {
            d.setPlatform(e);
        }
        return list;
    }

    /**
     * only for validation purpose, no full entities returned
     */
    public static PlatformData getPlatformData(
            String name, String genomeRelease) throws Exception {
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {
            Query query = em.createQuery(
                    "SELECT s FROM PlatformData s " +
                    " where s.name = ?1 " +
                    " and s.genomeRelease = ?2 ");


            query.setParameter(1, name);
            query.setParameter(2, genomeRelease);


            Logger.getLogger(PlatformService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            List<PlatformData> list = query.getResultList();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getPlatformData: ", e);
            throw e;
        } finally {
            em.close();
        }

    }

    public static PlatformData getPlatformForRelease(Long platformid, String release) throws Exception {
        List<PlatformData> list = PlatformService.getPlatformDataByDetailId(platformid);
        //String release = ((ImportFileVisualPanel3) getComponent()).getFieldRelease().getText();
        if (release != null && !release.equals("")) {

            for (PlatformData dd : list) {
                if (dd.getGenomeRelease().equals(release)) {
                    return dd;
                }
            }
        }
        return null;
    }

    /**
     * only to check if platform exists, no full entities returned
     * @param name
     * @return
     * @throws java.lang.Exception
     */
    @SuppressWarnings("unchecked")
    public static PlatformDetail getPlatformDetailByName(String name) throws Exception {



        List<PlatformDetail> list = new Vector<PlatformDetail>();

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        try {
            String sql = "select PlatformDetail.* " +
                    " from PlatformDetail " +
                    " where PlatformDetail.name = \'" + name + "\'";


            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    PlatformDetail.class);

            list = (List<PlatformDetail>) q.getResultList();




        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getPlatformDetailByName", e);
            throw e;
        } finally {
            em.close();
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static PlatformDetail getPlatformDetailById(Long id) throws Exception {



        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        try {
            return em.find(PlatformDetail.class, id);



        } catch (Exception ex) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getPlatformDetailById", ex);
            throw ex;
        } finally {
            em.close();
        }

    }

    /**
     * get parent instance (platformdetail) for platformdata
     * @param id
     * @return
     * @throws java.lang.Exception
     */
    @SuppressWarnings("unchecked")
    public static PlatformDetail getPlatformDetailForData(PlatformData d) throws Exception {

        Logger.getLogger(PlatformService.class.getName()).log(Level.INFO,
                "getPlatformDetailForData: " + d.toFullString());

        List<PlatformDetail> list = new Vector<PlatformDetail>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        try {
            String sql = "select PlatformDetail.* " +
                    " from PlatformList, PlatformDetail " +
                    " where (" +
                    " PlatformList.platformListID = " + d.getPlatformListID() +
                    " and PlatformList.platformDetailID = PlatformDetail.platformID" +
                    " )";

            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    PlatformDetail.class);

            list = (List<PlatformDetail>) q.getResultList();
            if (list.size() <= 0) {
                Logger.getLogger(PlatformService.class.getName()).log(Level.WARNING,
                        "no entry in table platformlist for id " + d.toString() + " found !");
                return null;
            }


            return list.get(0);


        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getPlatformDetailForData", e);
            throw e;
        } finally {
            em.close();
        }

    }

    @SuppressWarnings("unchecked")
    public static List<PlatformData> getPlatformDataByDetailId(Long id) throws Exception {


        List<PlatformData> list = new Vector<PlatformData>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        try {
            String sql = "select PlatformList.* " +
                    " from PlatformList, PlatformDetail " +
                    " where (" +
                    " PlatformDetail.platformID = " + id +
                    " and PlatformList.platformDetailID = PlatformDetail.platformID" +
                    " )";

            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    PlatformData.class);

            list = (List<PlatformData>) q.getResultList();
            if (list.size() <= 0) {
                Logger.getLogger(PlatformService.class.getName()).log(Level.WARNING,
                        "no entry in table platformlist for id " + id + " found !");
                return null;
            }
            PlatformDetail e = PlatformService.getPlatformDetailById(id);
            for (PlatformData d : list) {
                d.setPlatform(e);
            }


            return list;


        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getPlatformDataByDetailId", e);
            throw e;
        } finally {
            em.close();
        }

    }

    @SuppressWarnings("unchecked")
    public static void getPlatformDataByExperimentData(ExperimentData d) throws Exception {


        List<PlatformData> list = new Vector<PlatformData>();
        EntityManager em = DBService.getEntityManger();

        try {
            String sql = "select PlatformData.* " +
                    " from PlatformData, ExperimentData " +
                    " where (" +
                    " ExperimentData.experimentListID= ?" +
                    " and PlatformData.platformListID = ExperimentData.platformdata.platformListID" +
                    " )";

            javax.persistence.Query query = em.createQuery(sql);
            query.setParameter(1, d.getId());

            list = (List<PlatformData>) query.getResultList();

            for (PlatformData data : list) {
                data.getPlattform();
                d.setPlatformdata(data);
            }


        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getPlatformDataByExperimentData", e);
            throw e;
        } finally {
            em.close();
        }

    }

    @SuppressWarnings("unchecked")
    public static PlatformData getPlatformDataById(Long id) throws Exception {


        List<PlatformData> list = new Vector<PlatformData>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        try {
            PlatformData d = em.find(PlatformData.class, id);
            // force  loading beause of lazy loading
            PlatformDetail e = d.getPlattform();
            return d;
        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getPlatformDataById", e);
            throw e;
        } finally {
            em.close();
        }

    }

    /**
     * get all 
     * @param barcode
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<PlatformDetail> getAgilentPlatformByBarcode(String barcode) throws Exception {

        List<PlatformDetail> list = new Vector<PlatformDetail>();


        EntityManager em = DBService.getEntityManger();
        try {
            String sql = "select PlatformDetail.* " +
                    " from PlatformDetail " +
                    " where (" +
                    "PlatformDetail.description like \'%" + barcode.substring(0, 7) + "%\'" +
                    " and PlatformDetail.manufacturer like \'%Agilent%\' " +
                    ")";
            Logger.getLogger(PlatformService.class.getName()).log(Level.INFO, sql);
            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    PlatformDetail.class);


            Logger.getLogger(PlatformService.class.getName()).log(Level.INFO,
                    q.getResultList().toString());


            list = q.getResultList();
        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getAgilentPlatformByBarcode", e);
            throw e;
        } finally {
            em.close();
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<PlatformDetail> getPlatformByTypeAndMethod(
            Defaults.Method method,
            Defaults.Type type) throws Exception {

        List<PlatformDetail> list = new Vector<PlatformDetail>();

        EntityManager em = DBService.getEntityManger();
        try {
            String sql =
                    "select PlatformDetail.* " +
                    " from PlatformDetail " +
                    " where  ( " +
                    " PlatformDetail.method = \'" + method + "\'" +
                    " and PlatformDetail.type = \'" + type + "\'" +
                    " ) ";
            Logger.getLogger(PlatformService.class.getName()).log(Level.INFO, sql);
            javax.persistence.Query q = em.createNativeQuery(sql, PlatformDetail.class);
            if (q.getResultList().size() == 0) {
                sql =
                        "select PlatformDetail.* " +
                        " from PlatformDetail " +
                        " where  ( " +
                        " PlatformDetail.method like \'%" +
                        (method == null ? "" : method) + "%\'" +
                        " and PlatformDetail.type like \'%" +
                        (type == null ? "" : type) + "%\'" +
                        " ) ";
                Logger.getLogger(PlatformService.class.getName()).log(Level.INFO, sql);
                q = em.createNativeQuery(sql, PlatformDetail.class);
            }
            list = q.getResultList();
        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "getAgilentPlatformByProtocoll", e);
            throw e;
        } finally {
            em.close();
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<PlatformDetail> listPlatformDetail() throws Exception {


        List<PlatformDetail> list = new Vector<PlatformDetail>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            String sql = "select PlatformDetail.* " +
                    " from PlatformDetail ";

            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    PlatformDetail.class);


            Logger.getLogger(PlatformService.class.getName()).log(Level.INFO,
                    q.getResultList().toString());


            list = q.getResultList();
        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "listPlatformDetail", e);
            throw e;
        } finally {
            em.close();
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<PlatformData> listPlatformData() throws Exception {


        List<PlatformData> list = new Vector<PlatformData>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            String sql = "select PlatformList.* " +
                    " from PlatformList ";

            javax.persistence.Query q = em.createNativeQuery(
                    sql,
                    PlatformData.class);


            Logger.getLogger(PlatformService.class.getName()).log(Level.INFO,
                    q.getResultList().toString());


            list = q.getResultList();
            // force  loading beause of lazy loading
            for (PlatformData d : list) {
                d.getPlattform();
            }
        } catch (Exception e) {
            Logger.getLogger(PlatformService.class.getName()).log(Level.SEVERE,
                    "listPlatformData", e);
            throw e;
        } finally {
            em.close();
        }
        return list;
    }
}
