package org.molgen.genomeCATPro.datadb.service;

import java.util.Collections;
import java.util.List;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.molgen.dblib.DBService;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentAtStudy;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.Study;

import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.dbentities.TrackAtStudy;

/**
 * @name ProjectService
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
public class ProjectService {

    public static void remove(Study s, EntityManager em) throws Exception {
        //EntityManager em = DBService.getEntityManger();
        try {
            javax.persistence.Query query = em.createQuery(
                    "SELECT es FROM ExperimentAtStudy es " +
                    " WHERE es.studyID = ?1");
            query.setParameter(1, s.getStudyID());
            for (ExperimentAtStudy es : (List<ExperimentAtStudy>) query.getResultList()) {
                em.remove(es);
            }
            em.remove(s);

        } catch (Exception ex) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
                    "remove:", ex);
            throw ex;

        } finally {
            //em.close();
        }
    }

    /**
     * add existing experiment (already in db) to existing study
     * @param s
     * @param e
     */
    public static void addExperiment(Study s, ExperimentDetail e, EntityManager em) {


        ExperimentAtStudy association = new ExperimentAtStudy();

        association.setStudy(s);
        association.setExperiment(e);
        association.setExperimentDetailID(e.getExperimentDetailID());
        association.setStudyID(s.getStudyID());


        if (em == null) {
            em = DBService.getEntityManger();
        }
        em.persist(association);
    // notify listener 

    }

    /**
     * add track experiment (already in db) to existing study
     * @param s
     * @param t
     */
    public static void addTrack(Study s, Track t, EntityManager em) {

        if (!em.contains(s)) {
            s = em.merge(s); // detached -> managed

        }
        TrackAtStudy association = new TrackAtStudy();
        association.setStudy(s);
        association.setTrack(t);
        association.setTrackID(t.getTrackID());
        association.setStudyID(s.getStudyID());


        if (em == null) {
            em = DBService.getEntityManger();
        }
        em.persist(association);
    // notify listener 

    }

    /**
     * get entity by id
     * @param id
     * @return
     */
    public static Study getProjectById(Long id) {
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }
        Study d = em.find(Study.class, id);



        Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                "getProjectById: " + (d != null ? d.toFullString() : " not found " + id));
        return d;

    }

    public static Study getProjectByName(String projectname, EntityManager em) throws Exception {

        try {
            if (em == null) {
                em = DBService.getEntityManger();
            }
            javax.persistence.Query query = em.createQuery(
                    " select s from Study s" +
                    " where s.name =  ?1");
            query.setParameter(1, projectname);
            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            List<Study> list = query.getResultList();
            if (list.size() <= 0) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (Exception e) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
                    "getProjectByName:", e);
            throw e;
        }
    }

    /**
     * list all experiments (detail) for a certain project
     * @param s
     * @return
     */
    public static List<ExperimentDetail> listExperimentsForProject(Study s) {
        if (s.getStudyID() == null) {
            return Collections.EMPTY_LIST;
        }
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            Query query = em.createQuery(
                    "SELECT e FROM ExperimentDetail e, ExperimentAtStudy es " +
                    " where e.experimentDetailID = es.experimentDetailID " +
                    " and es.studyID = ?1 order by e.created");

            query.setParameter(1, s.getStudyID());
            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    "listExperimentsForProject:" +
                    query.getResultList().toString());
            List<ExperimentDetail> list = query.getResultList();

            return list;
        } catch (Exception ex) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
                    "listExperimentsForProject:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    public static Study getProjectForTrack(Track t) {

        EntityManager em = DBService.getEntityManger();

        try {
            Query query = em.createQuery(
                    "SELECT s FROM Study s, TrackAtStudy ts " +
                    " where s.studyID = ts.studyID " +
                    " and ts.trackID = ?1 ");

            query.setParameter(1, t.getTrackID());
            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    "getProjectForTrack " + t.toString() + ":" +
                    query.getResultList().toString());
            List<Study> list = query.getResultList();

            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }

        } finally {
            em.close();
        }
    }

    public static Study getProjectForExperimentDetail(ExperimentDetail e) {

        EntityManager em = DBService.getEntityManger();

        try {
            Query query = em.createQuery(
                    "SELECT s FROM Study s, ExperimentAtStudy es " +
                    " where s.studyID = es.studyID " +
                    " and es.experimentDetailID = ?1 ");

            query.setParameter(1, e.getExperimentDetailID());
            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    "getProjectForExperimentDetail " + e.toString() + ":" +
                    query.getResultList().toString());
            List<Study> list = query.getResultList();

            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }

        } finally {
            em.close();
        }
    }

    public static List<Track> listTracksForProject(Study s) {
        if (s.getStudyID() == null) {
            return Collections.EMPTY_LIST;
        }
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            Query query = em.createQuery(
                    "SELECT t FROM Track t, TrackAtStudy ts " +
                    " where t.trackID = ts.trackID " +
                    " and ts.studyID = ?1 order by t.created");

            query.setParameter(1, s.getStudyID());
            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    "listTracksForProject:" +
                    query.getResultList().toString());
            List<Track> list = query.getResultList();

            return list;
        } catch (Exception ex) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
                    "listTracksForProject:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    public static List<ExperimentDetail> listExperimentsWoProject() {

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            Query query = em.createQuery(
                    "SELECT e FROM ExperimentDetail e " +
                    " WHERE NOT EXISTS (" +
                    "SELECT es.experimentDetailID from ExperimentAtStudy es " +
                    " where es.experimentDetailID = e.experimentDetailID )");
            //SELECT DISTINCT auth FROM Author auth
            //WHERE EXISTS (SELECT spouseAuth FROM Author spouseAuth WHERE spouseAuth = auth.spouse)

            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    "listExperimentsWoProject:" +
                    query.getResultList().toString());
            List<ExperimentDetail> list = query.getResultList();

            return list;
        } catch (Exception ex) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
                    "listExperimentsWoProject:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    public static List<ExperimentDetail> listExperimentsWoProjectWithFilter(
            String release, String user, String sample) {

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return Collections.EMPTY_LIST;
        }
        Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                "listExperimentsWoProjectWithFilter called with  release: " +
                release + " user: " + user + " sample: " + sample);
        try {
            Query query = em.createQuery(
                    " SELECT DISTINCT e FROM ExperimentDetail e, " +
                    " ExperimentData d , " +
                    " SampleInExperiment sie , SampleDetail sd " +
                    " WHERE " +
                    " d.experiment.experimentDetailID = e.experimentDetailID " +
                    " and d.genomeRelease like ?1" +
                    " and d.owner.name like ?3 " +
                    " and e.experimentDetailID = sie.experimentDetailID " +
                    " and sie.sampleDetailID = sd.sampleDetailID " +
                    " and sd.name like ?2 " +
                    " AND NOT EXISTS (" +
                    " SELECT es.experimentDetailID from ExperimentAtStudy es " +
                    " where es.experimentDetailID = e.experimentDetailID )");



            query.setParameter(1, "%" + release + "%");
            query.setParameter(2, "%" + sample + "%");
            query.setParameter(3, "%" + user + "%");
            //SELECT DISTINCT auth FROM Author auth
            //WHERE EXISTS (SELECT spouseAuth FROM Author spouseAuth WHERE spouseAuth = auth.spouse)

            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    "listExperimentsWoProjectWithFilter:" +
                    query.getResultList().toString());
            List<ExperimentDetail> list = query.getResultList();

            return list;
        } catch (Exception ex) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
                    "listExperimentsWoProjectWithFilter:", ex);
            return Collections.EMPTY_LIST;
        } finally {
            em.close();
        }

    }

    /**public static List<ExperimentDetail> listTrackssWoProjectWithFilter(
    String release, String user, String sample) {
    
    EntityManager em = DBService.getEntityManger();
    if (em == null) {
    return Collections.EMPTY_LIST;
    }
    Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
    "called with  release: " +
    release + " user: " + user + " sample: " + sample);
    try {
    Query query = em.createQuery(
    " SELECT DISTINCT e FROM ExperimentDetail e, " +
    " ExperimentData d , " +
    " SampleInExperiment sie , SampleDetail sd " +
    " WHERE " +
    " d.experiment.experimentDetailID = e.experimentDetailID " +
    " and d.genomeRelease like ?1" +
    " and d.owner.name like ?3 " +
    " and e.experimentDetailID = sie.experimentDetailID " +
    " and sie.sampleDetailID = sd.sampleDetailID " +
    " and sd.name like ?2 " +
    " AND NOT EXISTS (" +
    " SELECT es.experimentDetailID from ExperimentAtStudy es " +
    " where es.experimentDetailID = e.experimentDetailID )");
    
    
    
    query.setParameter(1, "%" + release + "%");
    query.setParameter(2, "%" + sample + "%");
    query.setParameter(3, "%" + user + "%");
    //SELECT DISTINCT auth FROM Author auth
    //WHERE EXISTS (SELECT spouseAuth FROM Author spouseAuth WHERE spouseAuth = auth.spouse)
    
    Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
    "listTrackssWoProjectWithFilter:" +
    query.getResultList().toString());
    List<ExperimentDetail> list = query.getResultList();
    
    return list;
    } catch (Exception ex) {
    Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
    "listTrackssWoProjectWithFilter:", ex);
    return Collections.EMPTY_LIST;
    } finally {
    em.close();
    }
    
    }
     **/
    /**
     * list all projects
     * @param em
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Study> listProjects() {
        EntityManager em = DBService.getEntityManger();

        javax.persistence.Query query = em.createQuery(
                " select s from Study s" +
                " order by  s.name ");
        Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                query.getResultList().toString());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public static List<Study> listProjectsWithFilter(
            String studyname,
            String release, String user, String sample) {
        EntityManager em = DBService.getEntityManger();
        Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                "called with project: " + studyname + " release: " +
                release + " user: " + user + " sample: " + sample);

        javax.persistence.Query query = em.createNativeQuery(
                /*  "SELECT DISTINCT s FROM " +
                "Study s, " +
                "ExperimentDetail e, " +
                "ExperimentAtStudy es, " +
                "ExperimentData d , " +
                "SampleInExperiment sie , SampleDetail sd" +
                " where e.experimentDetailID = es.experimentDetailID " +
                " and s.studyID = es.studyID  " +
                " and d.experiment.experimentDetailID = e.experimentDetailID " +
                " and s.name like ?1 " +
                " and d.genomeRelease like ?2" +
                " and e.experimentDetailID = sie.experimentDetailID " +
                " and sie.sampleDetailID = sd.sampleDetailID " +
                " and sd.name like ?3 " +
                " and s.owner.name like ?4 "*/
                "SELECT DISTINCT s.* FROM " +
                " Study as s , User as u " +
                " WHERE  s.idOwner = u.UserID and u.name like ?4 " +
                " AND s.name like ?1 " +
                " AND ( EXISTS (" +
                " select  1 from " +
                " ExperimentDetail as e, ExperimentAtStudy as es, ExperimentList as d , " +
                " SampleInExperiment as sie , SampleDetail as sd" +
                " where e.experimentDetailID = es.experimentDetailID " +
                " and s.StudyID = es.studyID  " +
                " and e.experimentDetailID = sie.experimentDetailID " +
                " and d.experimentDetailID = e.experimentDetailID " +
                " and sie.sampleDetailID = sd.sampleDetailID " +
                " and d.genomeRelease like ?2" +
                " and sd.name like ?3 ) " +
                " OR  EXISTS ( " +
                "  select  1 from " +
                " TrackList as t, TrackAtStudy as ts,  " +
                " SampleInTrack as sit , SampleDetail as sd" +
                " where t.TrackID = ts.TrackID " +
                " and s.StudyID = ts.StudyID  " +
                " and t.TrackID = sit.trackID " +
                " and sit.sampleDetailID = sd.sampleDetailID " +
                " and t.genomeRelease like ?5" +
                " and sd.name like ?6 ) " +
                "  )", Study.class);
        query.setParameter(1, "%" + studyname + "%");
        query.setParameter(2, "%" + release + "%");
        query.setParameter(3, "%" + sample + "%");
        query.setParameter(4, "%" + user + "%");
        query.setParameter(5, "%" + release + "%");
        query.setParameter(6, "%" + sample + "%");

        Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                query.getResultList().toString());
        return query.getResultList();
    }

    /**
     * list all projects for a certain user
     * @param em
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Study> listProjects(String owner, EntityManager em) throws Exception {
        try {
            if (em == null) {
                em = DBService.getEntityManger();
            }
            javax.persistence.Query query = em.createQuery(
                    " select s from Study s" +
                    " where s.owner.name =  ?1" +
                    " order by  s.name ");
            query.setParameter(1, owner);
            Logger.getLogger(ProjectService.class.getName()).log(Level.INFO,
                    query.getResultList().toString());
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE,
                    "listProjects", e);
            throw e;
        }
    }

    /**
     * get project without completetd transaction
     * @param name
     * @param release
     * @param proc
     * @param param
     * @return
     */
    public static Study createProject(String owner, EntityManager em) throws Exception {



        try {
            Study s = new Study();


            s.setOwner(ExperimentService.getUser());



            em.persist(s);
            return s;

        } catch (Exception ex) {
            Logger.getLogger(ProjectService.class.getName()).log(Level.SEVERE, "", ex);
            throw ex;

        } finally {
            // em.close();
        }

    }

    public static void notifyListener() {
        for (ServiceListener s : _listener) {
            s.dbChanged();
        }

    }
    static List<ServiceListener> _listener = new Vector<ServiceListener>();

    public static void addListener(ServiceListener al) {
        _listener.add(al);
    }

    public void removeListener(ServiceListener al) {
        _listener.remove(al);
    }
}
