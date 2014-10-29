package org.molgen.genomeCATPro.datadb.service;

/**
 * @name TrackService
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
import java.util.Collections;
import java.util.List;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInTrack;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.dbentities.TrackAtStudy;

/**
 * 010612 kt    moveTrack, deleteTrack
 * 050612 kt    deprecate TrackServiceListener
 *              call ExperimentService.Listener
 * 120612 kt    redefine moveTrack
 */
public class TrackService {
    /* 050612 kt
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
     */

    /**
     * 060612 kt
     * 
     * @param t
     * @param parentTrack
     * @param em
     */
    @Deprecated
    public static void detachFromParentExperimentData(Track t, ExperimentData p, EntityManager em) {

        if (!em.contains(t)) {
            t = em.merge(t); // detached -> managed

        }


        t.setParentExperiment(null);
    }

    /**
     * 120612 kt
     * try to move track upwards in hierarchy to top experimentdata or project
     * if  track parent has experimentdata move it to top level parent data
     *     if already at top level -  move to project
     *     if movetostudy required -  move to project
     * 
     * if no parent exp data 
     *    if project in upward hierarchy found 
     *      move track to project
     * release parent track
     * otherwise     return false
     * 
     * @param moveToStudy - true: try not to move to top experimentdata - move to study
     * @return 
     *      true:   succesfully moved 
     *      false:  track is at top level or  no study in hierarchy
     */
    public static boolean moveTrack(Track s, boolean moveToStudy, EntityManager em) {
        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                "moveTrack: " + s.toString() + (moveToStudy ? " to Study " : " to top Exp"));
        boolean commit = false;
        if (em == null) {
            em = DBService.getEntityManger();
            em.getTransaction().begin();
            commit = true;
        }
        if (!em.contains(s)) {
            s = em.merge(s); // detached -> managed

        }
        //Track mt = em.find(Track.class, t.getTrackID());
        // Track mParent = em.find(Track.class,parentTrack.getTrackID());

        Study project = null;

        if (s.getParentExperiment() != null) {
            //  && !moveToStudy) {
            ExperimentData parent = s.getParentExperiment();


            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
            // if (s.getParentExperiment().equals(parent)) {
            if ((s.getParentExperiment().equals(parent) && s.getParentTrack() == null) || moveToStudy) {
                // already at top level -> move to study
                // moveToStudy required -> move to study
                project = ProjectService.getProjectForExperimentDetail(parent.getExperiment());
                s.setParentExperiment(null);
            } else {
                s.setParentExperiment(parent);
            }
        } else {
            Track parent = s;

            while (parent.getParentTrack() != null) {
                parent = parent.getParentTrack();
            }
            if (parent.equals(s)) {
                // already at top level - nothing to move
                Logger.getLogger(TrackService.class.getName()).log(Level.WARNING,
                        "moveTrack: already at very top " + s.toString());
                return false;
            } else {
                project = ProjectService.getProjectForTrack(parent);
                s.setParentExperiment(null);
            }
        }
        if (project != null) {
            ProjectService.addTrack(project, s, em);

        }
        if (project == null && s.getParentExperiment() == null) {
            Logger.getLogger(TrackService.class.getName()).log(Level.WARNING,
                    "moveTrack: not moveable - no top project found ");

            return false;
        }

        s.setParentTrack(null);


        if (commit) {
            em.flush();
            em.getTransaction().commit();
        }
        return true;
    }

    /**
     * 
     * @param id
     * @return
     */
    public static Track getTrackById(
            Long id) {
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }



        Track d = em.find(Track.class, id);

        //ExperimentDetail dd = d.getExperiment();

        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                "getTrackById: " + (d != null ? d.toFullString() : " not found " + id));


        return d;

    }

    /**
     * list root nodes only (i.e. witouth parent track) 
     * @param e
     * @return
     */
    public static List<Track> listChildrenForExperimentData(ExperimentData e) {
        if (e.getId() == null) {
            return Collections.EMPTY_LIST;
        }
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            Query query = em.createQuery(
                    "SELECT t FROM Track t " +
                    " where t.parentExperiment.experimentListID = ?1" +
                    " and t.parentTrack is null order by t.created");

            query.setParameter(1, e.getId());
            Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                    "listChildrenExperimentData:" +
                    query.getResultList().toString());
            List<Track> list = query.getResultList();
            for (Track d : list) {
                // force loading 
                d.setParentExperiment(e);

            }
            return list;
        } catch (Exception ex) {
            Logger.getLogger(TrackService.class.getName()).log(Level.SEVERE,
                    "listChildrenForExperimentData:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    /**
     * get indirekt samples (attached to parent experiment) for a track
     * @param t Track
     * @return
     */
    public static List<SampleDetail> getIndirektSampleInformationForTrack(Track t) {

        if (t == null || t.getId() == null) {
            return Collections.EMPTY_LIST;
        }
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {

            Query query = em.createQuery(
                    "SELECT sample FROM  " +
                    " SampleInExperiment as sie, ExperimentDetail detail , " +
                    " SampleDetail as sample , Track t " +
                    " where sie.sampleDetailID = sample.sampleDetailID " +
                    " and sie.experimentDetailID = detail.experimentDetailID " +
                    " and t.parentExperiment.experiment = detail.experimentDetailID " +
                    " and t.trackID = ?1");


            query.setParameter(1, t.getId());
            Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                    "getIndirektSampleInformationForTrack:" +
                    query.getResultList().toString());
            List<SampleDetail> list = query.getResultList();

            return list;
        } catch (Exception ex) {
            Logger.getLogger(TrackService.class.getName()).log(Level.SEVERE,
                    "getIndirektSampleInformationForTrack:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }


    }

    /**
     * get all direct chilren  of a track
     * @param e
     * @return
     */
    public static List<Track> listChildrenForTrack(Track e) {
        if (e.getId() == null) {
            return Collections.EMPTY_LIST;
        }
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            Query query = em.createQuery(
                    "SELECT t FROM Track t " +
                    " where t.parentTrack.trackID = ?1 order by t.created ");

            query.setParameter(1, e.getId());
            Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                    "listChildrenForTrack: " +
                    query.getResultList().toString());
            List<Track> list = query.getResultList();
            for (Track d : list) {
                // force loading 
                d.setParentTrack(e);

            }
            return list;
        } catch (Exception ex) {
            Logger.getLogger(TrackService.class.getName()).log(Level.SEVERE,
                    "listChildrenForTrack:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    /**
     * 010612 kt
     * delete track and subtree
     * if track is not deletable (user is not owner) move track upwards (first up Exp, then study)
     * @param track
     * @param moveToStudy if delete is called from deleteExperiment move not owned tracks to top projcect
     * @param em
     * @throws java.lang.Exception, java.lang.Error if track is not deletable
     */
    public static void deleteTrack(Track track, boolean moveToStudy, EntityManager em) throws Exception {

        boolean commit = false;
        if (em == null) {
            em = DBService.getEntityManger();
            em.getTransaction().begin();
            commit = true;
        }
        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                "deleteTrack: " + track.toString());

        try {
            if (!em.contains(track)) {
                track = em.merge(track); // detached -> managed

            }
            if (!track.getOwner().equals(ExperimentService.getUser())) {
                Logger.getLogger(TrackService.class.getName()).log(Level.WARNING,
                        ExperimentService.getUser() + " is not owner for " + track.toFullString());

                String msg = ExperimentService.getUser() + " is not owner for " + track.toString() + " !";
                throw new Error(msg);
            }

            List<Track> listTracks = track.getChildrenList();
            for (Track ct : listTracks) {
                try {
                    TrackService.deleteTrack(ct, moveToStudy, em);
                } catch (Error error) {
                    // try to remove from subtree
                    if (!TrackService.moveTrack(ct, moveToStudy, em)) {
                        throw new Error("track " + ct.toString() + " not moveable");
                    }
                }
            }

            Query query;

            //track.setParentExperiment(null);

            query = em.createNativeQuery("DROP TABLE if EXISTS " + track.getTableData());
            query.executeUpdate();


            List<SampleInTrack> listSamples = track.getSamples();
            for (SampleInTrack sit : listSamples) {
                if (!em.contains(sit)) {
                    sit = em.merge(sit); // detached -> managed

                }
                em.remove(sit);
            }
            Study p = ProjectService.getProjectForTrack(track);
            if (p != null) {
                query = em.createQuery(
                        "SELECT tas FROM TrackAtStudy tas " +
                        " WHERE tas.studyID = ?1 and tas.trackID = ?2");
                query.setParameter(1, p.getStudyID());
                query.setParameter(2, track.getTrackID());

                for (TrackAtStudy tas : (List<TrackAtStudy>) query.getResultList()) {
                    em.remove(tas);
                }
            }
            em.remove(track);
            if (commit) {
                em.getTransaction().commit();
            }

        } catch (Exception e) {

            Logger.getLogger(TrackService.class.getName()).log(Level.SEVERE, track.toString(), e);
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

    /**
     * no full entities returned
     * @param em
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Track> getAllTracks(EntityManager em) {

        javax.persistence.Query query = em.createNativeQuery(
                " select t from Track t" +
                " group by t.genomeRelease, t.name, t.procProcessing, t.paramProcessing ",
                Track.class);
        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                query.getResultList().toString());

        return query.getResultList();
    }

    /**
     * no full entities returned
     * @param name
     * @param release
     * @param proc
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Track> findTrack(String name, String release, String proc, String param) {
        EntityManager em = DBService.getEntityManger();
        Query query = em.createQuery("SELECT t FROM Track t" +
                " where t.name = ?1 " +
                " and t.genomicRelease = ?2 " +
                " and t.procProcessing = ?3" +
                " and t.paramProcessing = ?4");



        query.setParameter(1, name);
        query.setParameter(2, release);
        query.setParameter(3, proc);
        query.setParameter(4, param);
        Logger.getLogger(TrackService.class.getName()).log(Level.INFO,
                query.getResultList().toString());
        return query.getResultList();
    }

    /**
     * attach samples from list to an other track
     * @param samples
     * @param track
     * @param em
     * @throws java.lang.Exception
     */
    public static void forwardSamples(List<SampleInTrack> samples, Track track, EntityManager em) throws Exception {
        if (!em.isOpen()) {
            throw new RuntimeException("importSamples method meant to be inside open em/transaction!!");
        }

        for (SampleInTrack sie : samples) {
            SampleDetail detail = sie.getSample();
            if (detail.getSampleDetailID() == null ||
                    em.find(SampleDetail.class, detail.getSampleDetailID()) == null) {
                //isNewPlatformDetail = true;

                Logger.getLogger(
                        TrackService.class.getName()).log(Level.INFO,
                        "importSamples: create sampledetail " + detail.toFullString());


                // make new sampledetail persistent in db
                em.persist(detail);  //cascade??

            //em.flush();

            } else {
                // update sample from db with current entity
                detail = em.merge(detail);

            }
            em.flush();
            em.refresh(detail);
            SampleInTrack sieNeu = track.addSample(detail, sie.isInverse());
            Logger.getLogger(
                    TrackService.class.getName()).log(Level.INFO,
                    "importSamples: create new SampleInTrack " + sieNeu.toFullString());
            em.persist(sieNeu);

        }

    }

    /**
     * attach samples from list to a track
     * @param samples
     * @param track
     * @param isNewTrack
     * @param em
     * @return
     * @throws java.lang.Exception
     */
    public static List<SampleInTrack> importSamples(
            List<SampleInTrack> samples, Track track, boolean isNewTrack, EntityManager em) throws Exception {
        // sample existiert bereits ?  merge : create
        // sample existiert und bereits fürs Experiment : merge : create
        if (!em.isOpen()) {
            throw new RuntimeException("importSamples method meant to be inside open em/transaction!!");
        }

        List<SampleInTrack> new_samples = new Vector<SampleInTrack>();
        for (SampleInTrack sie : samples) {



            SampleDetail detail = sie.getSample();
            if (detail.getSampleDetailID() == null ||
                    em.find(SampleDetail.class, detail.getSampleDetailID()) == null) {
                //isNewPlatformDetail = true;

                Logger.getLogger(
                        TrackService.class.getName()).log(Level.INFO,
                        "importSamples: create sampledetail " + detail.toFullString());


                // make new sampledetail persistent in db
                em.persist(detail);  //cascade??

            //em.flush();

            } else {
                // update sample from db with current entity
                detail = em.merge(detail);

            }

            em.flush();
            em.refresh(detail);
            sie.setSample(detail);
            sie.setTrack(track);






            if (isNewTrack)// old experiments have samples attached
            {
                Logger.getLogger(
                        TrackService.class.getName()).log(Level.INFO,
                        "importSamples: create new SampleInTrack " + sie.toFullString());
                em.persist(sie);
            //em.flush();
            } else {
                Logger.getLogger(
                        TrackService.class.getName()).log(Level.INFO,
                        "importSamples: merge existing SampleInTrack " + sie.toFullString());

                sie = em.merge(sie);

            }

            new_samples.add(sie);
        //em.flush();
        }

        return new_samples; // return entities from persistent context

    }

    /**
     * make track instance persistent
     * @param t
     * @param em
     * @throws java.lang.Exception
     */
    public static void persistsTrack(Track t, EntityManager em) throws Exception {
        if (em == null) {
            em = DBService.getEntityManger();
        }

        try {

            if (t.getOwner() == null) {

                t.setOwner(ExperimentService.getUser());

            }

            em.persist(t);


        } catch (Exception ex) {
            Logger.getLogger(TrackService.class.getName()).log(Level.SEVERE, null, ex);

            throw ex;

        } finally {
        }
    }

    /**
     * only for validation purpose, no full entities returned
     */
    @SuppressWarnings("unchecked")
    public static Track getTrack(
            String name, String genomeRelease) {
        EntityManager em = DBService.getEntityManger();
        Query query = em.createQuery("SELECT t FROM Track t" +
                " where t.name = ?1 " +
                " and t.genomeRelease = ?2 ");


        query.setParameter(1, name);
        query.setParameter(2, genomeRelease);


        Logger.getLogger(
                TrackService.class.getName()).log(
                Level.INFO,
                query.getResultList().toString());
        List<Track> list = query.getResultList();



        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * get all types of data contained in db
     * @return
     */
    public static Vector<String> getAllDataTypes() {


        Vector<String> vValues = new Vector<String>();
        //select distinct Study.name from Study, User where Study.idOwner = User.UserID and User.name = "tebel"
        Connection con = Database.getDBConnection(Defaults.localDB);
        String sqlstmt = "select distinct dataType from TrackList ";
        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                vValues.add(rs.getString(1));
            }
//values = new String[vValues.size()];
//values = vValues.toArray(values);


        } catch (Exception e) {
            Logger.getLogger(TrackService.class.getName()).log(Level.SEVERE, null, e);

        }

        return vValues;
    }

    /**
     * get all methods of processing contained in db
     */
    public static Vector<String> getAllProcs() {


        Vector<String> vValues = new Vector<String>();
        //select distinct Study.name from Study, User where Study.idOwner = User.UserID and User.name = "tebel"
        Connection con = Database.getDBConnection(Defaults.localDB);
        String sqlstmt = "select distinct procProcessing from TrackList ";
        try {
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(sqlstmt);
            while (rs.next()) {
                vValues.add(rs.getString(1));
            }


        } catch (Exception e) {
            Logger.getLogger(TrackService.class.getName()).log(Level.SEVERE, null, e);

        }
        return vValues;
    }
}
