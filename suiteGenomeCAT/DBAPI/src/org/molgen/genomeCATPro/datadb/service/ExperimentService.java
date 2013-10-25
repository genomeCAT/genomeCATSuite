package org.molgen.genomeCATPro.datadb.service;

/**
 * @name ExperimentService
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.molgen.dblib.DBService;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentAtStudy;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInExperiment;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.dbentities.User;

/**
 * 
 * 050612 kt notify catch java.util.ConcurrentModificationException 
 * 120612 kt deleteExperimentData, deleteExperimentDetail   
 * 120612 kt moveExperimentData
 */
public class ExperimentService {

    static List<ServiceListener> _listener = Collections.synchronizedList(new ArrayList<ServiceListener>());

    public static void addListener(ServiceListener al) {
        synchronized (_listener) {
            _listener.add(al);
        }
    }

    public void removeListener(ServiceListener al) {
        synchronized (_listener) {
            _listener.remove(al);
        }
    }

    @SuppressWarnings("empty-statement")
    public static void notifyListener() {
        try {
            synchronized (_listener) {
                for (ServiceListener s : _listener) {
                    s.dbChanged();
                }
            }
        } catch (java.util.ConcurrentModificationException e) { // 050612 kt

        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "notify", e);
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * only for validation purpose, no full entities returned
     */
    public static ExperimentData getExperimentData(
            String name, String genomeRelease) throws Exception {
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {
            Query query = em.createQuery(
                    "SELECT s FROM ExperimentData s " +
                    " where s.name = ?1 " +
                    " and s.genomeRelease = ?2 ");


            query.setParameter(1, name);
            query.setParameter(2, genomeRelease);


            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            List<ExperimentData> list = query.getResultList();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "getExperimentData: ", e);
            throw e;
        } finally {
            em.close();
        }

    }

    public static ExperimentData getExperimentByDataId(Long id) {
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        ExperimentData d = em.find(ExperimentData.class, id);
        d.getExperiment();

        Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                "getExperimentByDataId: " + d.toString() + " extended: " + d.toFullString());
        return d;

    }

    public static ExperimentDetail getExperimentByDetailId(Long id) {
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        ExperimentDetail d = em.find(ExperimentDetail.class, id);


        Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                "getExperimentByDetailId: " + d.toString() + " extended: " + d.toFullString());
        return d;

    }

    @SuppressWarnings("unchecked")
    public static ExperimentDetail getExperimentDetailByName(String name) throws Exception {
        List<ExperimentDetail> list = new Vector<ExperimentDetail>();

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {

            Query query = em.createQuery(
                    "SELECT e FROM ExperimentDetail e " +
                    "where e.name = ?1 ");
            query.setParameter(1, name);
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "getExperimentDetailByName: " + query.getResultList().toString());
            list = (List<ExperimentDetail>) query.getResultList();

        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "getExperimentDetailByName: ", e);
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
    public static SampleDetail getSampleDetailByName(String name) throws Exception {
        List<SampleDetail> list = new Vector<SampleDetail>();

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {

            Query query = em.createQuery(
                    "SELECT e FROM SampleDetail e " +
                    "where e.name = ?1 ");
            query.setParameter(1, name);
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "getSampleDetailByName: " + query.getResultList().toString());
            list = (List<SampleDetail>) query.getResultList();

        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "getSampleDetailByName: ", e);
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
    public static List<SampleDetail> listSampleDetails() {

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {

            Query query = em.createQuery(
                    "SELECT s FROM SampleDetail s order by s.name");
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "listSampleDetails: " + query.getResultList().toString());
            return (List<SampleDetail>) query.getResultList();

        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "listSampleDetails", e);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    @SuppressWarnings("unchecked")
    public static List<ExperimentDetail> listExperimentDetails() {

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {

            Query query = em.createQuery(
                    "SELECT e FROM ExperimentDetail e order by e.name");
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            return (List<ExperimentDetail>) query.getResultList();

        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "listExperimentDetails", e);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    @SuppressWarnings("unchecked")
    /**
     * only top level experiment data, without data.parent infos
     */
    public static List<ExperimentData> listTopLevelExperimentData(ExperimentDetail e) {
        if (e.getExperimentDetailID() == null) {
            return Collections.EMPTY_LIST;
        }
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            Query query = em.createQuery(
                    "SELECT el FROM ExperimentData el " +
                    " where el.experiment.experimentDetailID = ?1 " +
                    " and el.parent is null");

            query.setParameter(1, e.getExperimentDetailID());
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "listTopLevelExperimentData:" +
                    query.getResultList().toString());
            List<ExperimentData> list = query.getResultList();
            for (ExperimentData d : list) {
                d.setExperiment(e);
            }
            return list;
        } catch (Exception ex) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "listTopLevelExperimentData:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    /**
     * list experiment data children for experiment data
     */
    public static List<ExperimentData> listChildrenExperimentData(ExperimentData e) {
        if (e.getId() == null) {
            return Collections.EMPTY_LIST;
        }
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            Query query = em.createQuery(
                    "SELECT el FROM ExperimentData el " +
                    " where el.parent.experimentListID = ?1 order by el.created");

            query.setParameter(1, e.getId());
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "listChildrenExperimentData:" +
                    query.getResultList().toString());
            List<ExperimentData> list = query.getResultList();
            for (ExperimentData d : list) {
                // force loading 
                d.getExperiment();
                d.setParent(e);
            }
            return list;
        } catch (Exception ex) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "listChildrenExperimentData:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }
    /***
     * try to delete, if some of the children are not owned by user throw error
     * @param exdata
     * @param em
     * @return
     */
    public static boolean deleteExperimentDetail(ExperimentDetail exdata, EntityManager em) {

        boolean commit = false;
        if (em == null) {
            em = DBService.getEntityManger();
            em.getTransaction().begin();
            commit = true;
        }
        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                "deleteExperimentDetail: " + exdata.toString());
        try {

            List<ExperimentData> listChildren = exdata.getDataList();
            for (ExperimentData ct : listChildren) {
                ExperimentService.deleteExperimentData(ct, em);
            }


            List<SampleInExperiment> listSamples = exdata.getSamples();
            for (SampleInExperiment sie : listSamples) {
                if (!em.contains(sie)) {
                    sie = em.merge(sie); // detached -> managed

                }
                em.remove(sie);
            }
            Study p = ProjectService.getProjectForExperimentDetail(exdata);
            Query query;
            if (p != null) {
                query = em.createQuery(
                        "SELECT eas FROM ExperimentAtStudy eas " +
                        " WHERE eas.studyID = ?1 and eas.experimentDetailID = ?2 ");
                query.setParameter(1, p.getStudyID());
                query.setParameter(2, exdata.getExperimentDetailID());

                for (ExperimentAtStudy eas : (List<ExperimentAtStudy>) query.getResultList()) {
                    em.remove(eas);
                }
            }
            if (!em.contains(exdata)) {
                exdata = em.merge(exdata); // detached -> managed

            }

            em.remove(exdata);
            if (commit) {
                em.getTransaction().commit();
            }
            return true;



        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE, exdata.toString(), e);
        } finally {
            if (commit) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
        return false;
    }

    /**
     * 060612 kt
     * move data upwards in hierarchy to parent experiment detail,  release from parent data
     * @param s
     * @return 
     *      true: successfully moved 
     *      false: already on top at detail
     */
    public static boolean moveExperimentData(ExperimentData d, EntityManager em) {
        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                "moveExperimentData: " + d.toString());

        boolean commit = false;
        if (em == null) {
            em = DBService.getEntityManger();
            em.getTransaction().begin();
            commit = true;
        }
        if (!em.contains(d)) {
            d = em.merge(d); // detached -> managed

        }
        if (d.getParent() == null) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.WARNING,
                    "moveData: already at top " + d.toString());
            return false;
        }

        d.setParent(null);


        if (commit) {
            em.flush();
            em.getTransaction().commit();
        }
        return true;
    }

    /**
     * 120612 kt
     * delete experimentdata and subtree
     *      if data is not deletable (user is not owner) move data upwards to detail
     *      if track is not deleteable () move track upwards to project
     * @param exdata
     * @param em
     * @throws java.lang.Exception, java.lang.Error if data is not deletable
     */
    public static void deleteExperimentData(ExperimentData exdata, EntityManager em) throws Exception {

        boolean commit = false;
        if (em == null) {
            em = DBService.getEntityManger();
            em.getTransaction().begin();
            commit = true;
        }
        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                "deleteExperimentData: " + exdata.toString());
        try {
            if (!em.contains(exdata)) {
                exdata = em.merge(exdata); // detached -> managed

            }
            if (!exdata.getOwner().equals(ExperimentService.getUser())) {
                Logger.getLogger(ExperimentService.class.getName()).log(Level.WARNING,
                        ExperimentService.getUser() + " is not owner for " + exdata.toFullString());

                String msg = ExperimentService.getUser() + " is not owner for " + exdata.toString() + " !";
                throw new Error(msg);
            }
            List<ExperimentData> listChildren = exdata.getChilrenList();
            for (ExperimentData ct : listChildren) {
                try {
                    ExperimentService.deleteExperimentData(ct, em);
                } catch (Error e) {
                    if (!ExperimentService.moveExperimentData(ct, em)) {
                        throw new Error("data " + ct.toString() + " not moveable");
                    }
                }
            }
            List<Track> listTracks = exdata.getTrackList();
            for (Track ct : listTracks) {
                try {
                    TrackService.deleteTrack(ct, true, em);
                } catch (Error error) {
                    // move up to project 
                    if (!TrackService.moveTrack(ct, true, em)) {
                        throw new Error("track " + ct.toString() + " not moveable");
                    }
                }
            }




            // delete basic table

            Query query = em.createNativeQuery("DROP TABLE if EXISTS " + exdata.getTableData());
            query.executeUpdate();

            em.remove(exdata);
            if (commit) {
                em.getTransaction().commit();
            }

        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE, exdata.toString(), e);
            throw new Exception("Error see logfile!");
        } finally {
            if (commit) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public static SampleInExperiment findSampleInExperiment(Long sampleId, Long expId) throws Exception {
        List<SampleInExperiment> list = new Vector<SampleInExperiment>();

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {

            Query query = em.createQuery(
                    "SELECT sie FROM SampleInExperiment sie " +
                    "where sie.experimentDetailID = ?1 " +
                    " and sie.sampleDetailID");

            query.setParameter(1, expId);
            query.setParameter(2, sampleId);
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "findSampleInExperiment", query.getResultList().toString());
            list = (List<SampleInExperiment>) query.getResultList();

        } catch (Exception e) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE,
                    "findSampleInExperiment", e);
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

    /**
     * get user
     *      retrieve user from database wher name equals 1) system.name
     *      or 2) core.props.user
     * @return
     */
    public static User getUser() {

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        Query q = em.createQuery("SELECT u FROM User u WHERE u.name=:name");


        List list = q.setParameter("name", System.getProperty("user.name")).getResultList();

        if (list == null || list.size() == 0) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                    "unknown current User " + System.getProperty("user.name"));

            list = q.setParameter("name", CorePropertiesMod.props().getUser()).getResultList();

            if (list == null || list.size() == 0) {
                Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                        "unknowd User Core " + CorePropertiesMod.props().getUser());
                throw new RuntimeException("could not find user!");
            }
        }
        return (User) list.get(0);

    }

    /**
     * make existing sample instance persistent in database
     * all changes will rollback on error
     * @param s
     * @param em
     * @throws java.lang.Exception
     */
    public static void persistsExperimentData(ExperimentData d, EntityManager _em) throws Exception {
        EntityManager em;
        if (_em == null) {
            em = DBService.getEntityManger();
        } else {
            em = _em;
        }
        Logger.getLogger(ExperimentService.class.getName()).log(Level.INFO,
                "persistsExperimentData",
                d.getName());
        EntityTransaction userTransaction = null;
        try {
            userTransaction = em.getTransaction();
            if (!userTransaction.isActive()) {
                userTransaction.begin();
            }
            if (d.getOwner() == null) {

                d.setOwner(ExperimentService.getUser());
            }

            //Eintrag Tabelle SampleList und implizit experiment erzeugen



            em.persist(d);
            em.flush();
            em.refresh(d);

            if (_em == null) {
                userTransaction.commit();
            }
        } catch (Exception ex) {
            Logger.getLogger(ExperimentService.class.getName()).log(Level.SEVERE, "", ex);
            throw ex;

        } finally {
            if (_em == null && userTransaction != null && userTransaction.isActive()) {
                userTransaction.rollback();
            }
        }
    }
}
