package org.molgen.genomeCATPro.peaks.cnvcat;

/**
 * @name AberrationManagerCNVCAT
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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

import javax.persistence.Query;
import org.molgen.dblib.DBService;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.data.DataService;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.TrackService;


import org.molgen.genomeCATPro.peaks.Aberration;
import org.molgen.genomeCATPro.peaks.AberrationIds;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 * 
 * maintain instances,  filter, group  track data 
 * 
 * 
 * 081013    kt  filterLocation tolearate  exception  table not exists
 * 
 */
public class AberrationManagerCNVCAT extends AberrationManager {

    private boolean groupByCaseID = false;
    private boolean groupByParam = false;
    private boolean groupByNone = false;
    private boolean groupBySample = false;
    private boolean groupByPhenotype = false;
    //EntityManager em = DBService.getEntityManger( );
    static Lookup.Template<Aberration> tmplAberration = new Lookup.Template<Aberration>(
            org.molgen.genomeCATPro.peaks.Aberration.class);

    /**
     * init manager
     */
    public AberrationManagerCNVCAT() {
        super();


        activeCases = org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<AberrantRegions>());
        allAberrationIds = org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<AberrantRegions>());
        dispAberrations = org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<AberrationCNVCAT>());

    }

    /**
     * get for each type  of data object (within module peak) class to handle it
     * @param clazz - type of data object
     * @return  object instance
     */
    public static Feature getAberrationClazz(String clazz) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(DataService.class.getName()).log(Level.INFO,
                "looking for datatype clazz " + clazz);


        Result<Aberration> rslt = Lookup.getDefault().lookup(tmplAberration);
        for (Lookup.Item item : rslt.allItems()) {
            if (item.getType().getName().contentEquals(clazz)) {
                Logger.getLogger(DataService.class.getName()).log(Level.INFO,
                        "return: " + item.getDisplayName());
                return (Feature) item.getInstance();
            }
        }
        Logger.getLogger(DataService.class.getName()).log(Level.INFO,
                "return: " + AberrationCNVCAT.class.getName());
        return null;
    }

    /**
     * query database , apply filter, update local object stores
     * @param filter
     */
    @Override
    public void filterAberrationIds(String[] filter) {
        String _release = filter[0];
        String name = filter[1];
        String proc = filter[2];
        String param = filter[3];


        this.allAberrationIds.clear();
        System.gc();

        List<AberrantRegions> resultlist = new Vector<AberrantRegions>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
                    "No database connection");
            setAllAberrationIds(resultlist);
            return;
        }
        try {
            //javax.persistence.Query q = em.createQuery("SELECT a FROM AberrationIdsMRNET a");
            javax.persistence.Query q = em.createQuery(
                    " select t from Track t" +
                    " where t.name like \'" + name + "\'" +
                    (!proc.contentEquals("%") ? "and t.procProcessing like \'" + proc + "\'" : "") +
                    (!param.contentEquals("%") ? " and t.paramProcessing like \'" + param + "\'" : "") +
                    " and t.genomeRelease like \'" + _release + "\'");
            //q.setHint("toplink.refresh", "true");


            for (Track t : (List<Track>) q.getResultList()) {
                resultlist.add(new AberrantRegions(t));
            }
            setAllAberrationIds(resultlist);
        } finally {
            //em.close();
        }

    }

    /**
     * query database , apply filter, update local object stores
     * including associated samples 
     * @param filter
     */
    public void filterAberrationIdsPlusSample(String[] filter) {
        String _release = filter[0];
        String name = filter[1];
        String proc = filter[2];
        String param = filter[3];
        String sample = filter[4];
        String phenotype = filter[5];
        this.allAberrationIds.clear();
        System.gc();

        List<AberrantRegions> resultlist = new Vector<AberrantRegions>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
                    "No database connection");
            setAllAberrationIds(resultlist);
            return;
        }
        try {

            String sql =
                    " select distinct t.* from TrackList as t  " +
                    //" ExperimentList  as data, ExperimentDetail as detail, " +
                    //" SampleInExperiment as sie, SampleDetail as sample " +
                    " where " +
                    "  t.name like \'" + name + "\'" +
                    (!proc.contentEquals("%") ? "and t.procProcessing like \'" + proc + "\'" : "") +
                    (!param.contentEquals("%") ? " and t.paramProcessing like \'" + param + "\'" : "") +
                    " and t.genomeRelease like \'" + _release + "\'" +
                    " and ( " +
                    "   exists ( select 1 from  " +
                    " ExperimentList  as data, ExperimentDetail as detail, " +
                    " SampleInExperiment as sie, SampleDetail as sample " +
                    " where data.experimentDetailID = detail.experimentDetailID  " +
                    " and sample.name like \'" + sample + "\'" +
                    " and sample.phenotype like \'" + phenotype + "\'" +
                    " and data.experimentListID = t.parentExperimentID" +
                    " and sie.experimentDetailID = detail.experimentDetailID " +
                    " and sie.sampleDetailID = sample.sampleDetailID ) " +
                    " or  exists ( " +
                    " select 1 from SampleInTrack as sit , SampleDetail as sample " +
                    " where sit.trackID = t.trackID " +
                    " and sample.name like \'" + sample + "\'" +
                    " and sample.phenotype like \'" + phenotype + "\'" +
                    " and sit.sampleDetailID = sample.sampleDetailID ) " +
                    ") ";

            Logger.getLogger(AberrationManager.class.getName()).log(Level.INFO,
                    "filter sql: " + sql);
            javax.persistence.Query q = em.createNativeQuery(sql, Track.class);
            //javax.persistence.Query q = em.createQuery("SELECT a FROM AberrationIdsMRNET a");


            //q.setHint("toplink.refresh", "true");


            for (Track t : (List<Track>) q.getResultList()) {
                if(t.getSamples() == null || t.getSamples().size() == 0){
                    for( SampleDetail s : TrackService.getIndirektSampleInformationForTrack(t)){
                        t.addSample(s, false);
                    }
                }
                resultlist.add(new AberrantRegions(t));
            }
            setAllAberrationIds(resultlist);
        } finally {
            //em.close();
        }

    }

    /**
     * query database , apply filter, update local object stores
     * including associated samples 
     * @param chrom
     * @param start
     * @param end
     */
    void filterLocation(String chrom, Long start, Long end) {
        List<AberrantRegions> list = new Vector<AberrantRegions>();
        if (this.getAllAberrationIds().size() <= 0) {
            return;
        }

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
                    "No database connection");
            //setAllAberrationIds(resultlist);
            return;
        }
        try {

            Query q;

            for (AberrantRegions currentAberrationId : (List<AberrantRegions>) this.getAllAberrationIds()) {

                try {
                    q = em.createNativeQuery(" select count(*) " +
                            " from " + currentAberrationId.track.getTableData() +
                            " where chrom = \'" + chrom + "\'" +
                            " and chromStart < " + end +
                            " and chromEnd > " + start);

                    List<Object> r = (List<Object>) q.getSingleResult();

                    if ((Long) r.get(0) > 0) {
                        list.add(currentAberrationId);
                    }
                } catch (Exception e) {
                    //081013    kt  tolearate  exception  table not exists
                    Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
                            "", e);
                }
            }
            setAllAberrationIds(list);
        } catch (Exception e) {

            Logger.getLogger(AberrationManager.class.getName()).log(Level.SEVERE,
                    "", e);
        } finally {
            //em.close();
        }


    }

    /**
     * 
     * @param abc
     * @return
     */
    @Override
    public AberrantRegions getIdForAberration(Aberration abc) {
        AberrationCNVCAT ab = (AberrationCNVCAT) abc;
        for (AberrantRegions a : (List<AberrantRegions>) this.activeCases) {
            if (a.getTrackId().compareToIgnoreCase(ab.getTrackId()) == 0 //&& a.getPhenotype().compareToIgnoreCase(ab.getPhenotype()) == 0
                    ) {
                return a;
            }
        }
        return null;
    }

    @Override
    public int getIndexAberrationId(Aberration ab) {
        for (AberrantRegions a : (List<AberrantRegions>) this.activeCases) {
            if (a.getTrackId().compareToIgnoreCase(ab.getTrackId()) == 0 //&&  a.getPhenotype().compareToIgnoreCase(ab.getPhenotype()) == 0
                    ) {
                return this.activeCases.indexOf(a);
            }
        }
        return -1;
    }

    @Override
    public void deleteAberrations(List<? extends AberrationIds> list) throws Exception {
        /*
        List<AberrantRegions> alist = (List<AberrantRegions>) list;
        EntityManager em = getEntityManager();
        if (em == null) {
        throw new RuntimeException("No Database Connection");
        }
        
        EntityTransaction userTransaction = em.getTransaction();
        
        try {
        userTransaction.begin();
        javax.persistence.Query dq = em.createQuery(
        "DELETE FROM AberrationCGH a WHERE " +
        "a.caseId = :caseId  and a.phenotype = :phenotype"
        );
        for (AberrantRegions a : alist) {
        dq.setParameter("caseId", a.TrackId());
        dq.setParameter("phenotype", a.getPhenotype());
        int deleted = dq.executeUpdate();
        System.out.println("AberrationManager.deleteAberrations: " + deleted +
        " items deleted  where caseId = \'" + a.getCaseId() +
        "\' and phenotype = \'" + a.getPhenotype() + "\'\n");
        
        this.activeCases.remove(a);
        }
        
        userTransaction.commit();
        loadAberrationForActiveCaseIds();
        
        } catch (Exception e) {
        e.printStackTrace();
        throw e;
        } finally {
        if (userTransaction != null && userTransaction.isActive()) {
        userTransaction.rollback();
        
        }
        em.close();
        } */
    }

    /**
     * select distinct values for spezified column (group)
     * @return
     */
    @Override
    public Vector getColorGroupList() {
        Vector groupList = new Vector();
        List<String> list;
        if (this.isGroupByCaseID()) {
            list = getDistinctSelectedCaseID();
        } else {
            if (this.isGroupByParam()) {// group by Primary, Secondary, Tertiary

                list = this.getDistinctSelectedParam();
            } else {
                if (this.isGroupBySample()) {
                    list = this.getDistinctSelectedSampleNames();
                } else {
                    if (this.isGroupByPhenotype()) {
                        list = this.getDistinctSelectedPhenotypes();
                    } else {
                        list = getDistinctAll();
                    }
                }
            }
        }


        for (String name : list) {
            @SuppressWarnings("unchecked")
            Vector<Object> v = new Vector();
            v.add(name);
            groupList.add(v);
        }

        for (int i = 0; i < groupList.size(); i++) {
            ((Vector) groupList.get(i)).add(colors[i % 10]);
             ((Vector) groupList.get(i)).add(new Boolean(false));
        }

        System.out.println("getColorGroupList: " + groupList.toString());

        return groupList;
    }

    public boolean isGroupByCaseID() {
        return this.groupByCaseID;
    }

    public boolean isGroupByParam() {
        return this.groupByParam;
    }

    public void setGroupByCaseID(boolean b) {
        this.groupByCaseID = b;
    }

    public void setGroupByNone(boolean b) {
        this.groupByNone = b;
    }

    public void setGroupByPhenotype(boolean b) {
        this.groupByPhenotype = b;
    }

    public boolean isGroupBySample() {
        return groupBySample;
    }

    public void setGroupByParam(boolean groupByParam) {
        this.groupByParam = groupByParam;
    }

    public boolean isGroupByNone() {
        return groupByNone;
    }

    public void setGroupBySample(boolean groupBySample) {
        this.groupBySample = groupBySample;
    }

    public boolean isGroupByPhenotype() {
        return this.groupByPhenotype;
    }

    private List<String> getDistinctAll() {
        List<String> list = new Vector();
        list.add("all");
        return list;
    }

    private List<String> getDistinctSelectedCaseID() {
        List<String> CaseIDlist = new Vector();
        List<AberrantRegions> list = (List<AberrantRegions>) getSelectedAberrationIds();
        for (AberrantRegions a : list) {
            if (!CaseIDlist.contains(a.getTrackId())) {
                CaseIDlist.add(a.getTrackId());
            }

        }
        System.out.println("getDistinctSelectedCaseId: " + CaseIDlist.toString());

        return CaseIDlist;
    }

    private List<String> getDistinctSelectedParam() {

        List<String> Paramlist = new Vector();
        List<AberrantRegions> list = (List<AberrantRegions>) getSelectedAberrationIds();
        for (AberrantRegions a : list) {
            if (!Paramlist.contains(a.getParamAsString())) {
                Paramlist.add(a.getParamAsString());
            }

        }

        System.out.println("getDistinctSelectedParameter: " + Paramlist.toString());

        return Paramlist;

    }

    private List<String> getDistinctSelectedSampleNames() {

        List<String> sampleList = new Vector();
        List<AberrantRegions> list = (List<AberrantRegions>) getSelectedAberrationIds();
        for (AberrantRegions a : list) {
            for (String s : a.getSampleNames()) {
                if (!sampleList.contains(s)) {
                    sampleList.add(s);
                }

            }
        }
        System.out.println("getDistinctSelectedSampleName: " + sampleList.toString());

        return sampleList;

    }

    private List<String> getDistinctSelectedPhenotypes() {

        List<String> phenotypeList = new Vector();
        List<AberrantRegions> list = (List<AberrantRegions>) getSelectedAberrationIds();
        for (AberrantRegions a : list) {
            for (String s : a.getPhenotypes()) {


                if (!phenotypeList.contains(s)) {
                    phenotypeList.add(s);
                }

            }
        }
        System.out.println("getDistinctSelectedPhenotypes: " + phenotypeList.toString());

        return phenotypeList;

    }

    /**
     * check, if list contains a single caseId more than once
     * @param conflict1
     * 
     * @return vector of aberrations with same caseIds, null if no doubled caseId was found
     */
    @Override
    public List<AberrantRegions> conflictCaseIds(
            List<? extends AberrationIds> list) {
        Hashtable<String, List<AberrantRegions>> listCaseIDs =
                new Hashtable<String, List<AberrantRegions>>();
        List<AberrantRegions> conflict1 = (List<AberrantRegions>) list;
        for (AberrantRegions a : conflict1) {
            if (!listCaseIDs.containsKey(a.getTrackId())) {
                listCaseIDs.put(a.getTrackId(), new Vector());
            }
            listCaseIDs.get(a.getTrackId()).add(a);
        /*System.out.println("create hashtable: for " + a.getPatID() + 
        " size: " +  listPatIDs.get(a.getPatID()).size());
         * */
        }

        if (listCaseIDs.size() < conflict1.size()) {

            List<AberrantRegions> resultList = new Vector();
            String id;
            for (Enumeration<String> e = listCaseIDs.keys(); e.hasMoreElements();) {
                id = e.nextElement();
                //System.out.println(id);
                if (listCaseIDs.get(id).size() > 1) {
                    resultList.addAll(listCaseIDs.get(id));
                }
            }
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    protected void loadAberrationForActiveCaseIds() {


        if (this.activeCases.isEmpty()) {
            return;
        }

        Logger.getLogger(AberrationManager.class.getName()).log(Level.INFO,
                "loadAberrations for ActiveIds:");


        List<List> list;

        int nr = 0;

        try {
            javax.persistence.Query q;

            this.aberrationTable.clear();
            this.chroms.clear();
            EntityManager em = DBService.getEntityManger();
            if (em == null) {
                Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
                        "No database connection");
                return;

            }


            for (AberrantRegions currentAberrationId : (List<AberrantRegions>) this.activeCases) {
                try {
                    currentAberrationId.setXDispColumn(++nr);

                    //currentAberrationId.setCountAberrations(0);


                    Feature f;
                    try {
                        // lookup Feature.Clazz
                        f = AberrationManagerCNVCAT.getAberrationClazz(currentAberrationId.track.getClazz());
                    } catch (ClassCastException e) {
                        Logger.getLogger(AberrationManagerCNVCAT.class.getName()).log(
                                Level.SEVERE, "loadAberrationForActiveCaseIds", e);
                        continue;
                    }
                    List<AberrationCNVCAT> result;

                    if (f != null) {
                        result = (List<AberrationCNVCAT>) f.loadFromDB(currentAberrationId.track);

                    } else {
                        result = AberrationCNVCAT.loadCNVFromDB(currentAberrationId.track);
                    }

                    for (AberrationCNVCAT a : result) {
                        if (!this.aberrationTable.containsKey(a.getChrom())) {
                            this.addChrom(a.getChrom());
                            this.aberrationTable.put(a.getChrom(),
                                    new Vector());
                        }
                        /*
                        if(!this.aberrationTable.get(a.getChrom()).containsKey(currentAberrationId.getTrackId())){
                        this.aberrationTable.get(a.getChrom()).put(currentAberrationId.getTrackId(), 
                        new Vector<AberrationCGH>());
                        }
                        
                        a.setTrackId(currentAberrationId.getTrackId());
                        ((List<AberrationCGH>)this.aberrationTable.get(a.getChrom()).
                        get(currentAberrationId.getTrackId())).add(a);
                         */
                        a.setTrackId(currentAberrationId.getTrackId());
                        ((List<AberrationCNVCAT>) this.aberrationTable.get(a.getChrom())).add(a);
                    //currentAberrationId.setCountAberrations(
                    //       currentAberrationId.getCountAberrations() + 1);
                    }
                    em.clear();

                } catch (Exception e) {
                    Logger.getLogger(AberrationManager.class.getName()).log(Level.SEVERE,
                            "", e);
                }
            }

        } finally {
            // em.close();
            }
    }

    /**
     * get all possible aberrations filtered by distinct caseId and phenotype
     * @return
     */
    @Override
    public List<AberrationIds> findAberrationIds() {

        List<? extends AberrationIds> aberrationIds = new Vector<AberrantRegions>();
        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
                    "No database connection");
            return (List<AberrationIds>) aberrationIds;
        }
        try {
            List<Track> resultList = TrackService.getAllTracks(em);
            for (Track t : resultList) {
                ((List<AberrantRegions>) aberrationIds).add(new AberrantRegions(t));
            }
        } finally {
            //em.close();
            }
        return (List<AberrationIds>) aberrationIds;
    }

    /* public String exportIntoFileMRNET(List<? extends AberrationIds> aList,
    String sOutfile) throws IOException, Exception {
    
    
    if (aList.size() == 0) {
    return null;
    }
    List<AberrantRegions> idList = (List<AberrantRegions>) aList;
    sOutfile = sOutfile + ".csv";
    System.out.println("export path " + sOutfile);
    
    EntityManager em = DBService.getEntityManger();
    if (em == null) {
    
    Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
    "No database connection");
    
    return null;
    }
    
    BufferedWriter out = new BufferedWriter(new FileWriter(sOutfile));
    
    try {
    
    out.write("PatID;Array;StartClone;EndClone;CopyNumber;Ratio;IndConf;IndConfRes;Origin;NrOfTarget;RefDNA;Quality;chrom;chromStart;chromEnd");
    out.newLine();
    //
    //AG0246_s_Cy3_713_Cy5_NA15510_251469344046_090608_CGH_v4_10_Apr08
    //NA15510 -> RefDNA
    //B713 -> PatientId 
    
    DecimalFormat N = (DecimalFormat) DecimalFormat.getInstance();
    DecimalFormatSymbols NS = DecimalFormatSymbols.getInstance();
    NS.setDecimalSeparator(',');
    N.setDecimalFormatSymbols(NS);
    N.setMaximumFractionDigits(5);
    for (AberrantRegions abID : idList) {
    //EntityTransaction userTransaction = em.getTransaction();
    
    //userTransaction.begin();
    int i = abID.getTrackId().toLowerCase().indexOf("cy3_") + 4;
    String patientID = abID.getTrackId().substring(i);
    patientID =
    CNVCATPropertiesMod.props().getMrnetAbr() + patientID.substring(0, patientID.indexOf("_"));
    System.out.println("ExportAberration: PatientID " + patientID);
    i =
    abID.getTrackId().toLowerCase().indexOf("cy5_") + 4;
    String refID = abID.getTrackId().substring(i);
    refID = refID.substring(0, refID.indexOf("_"));
    Query q = em.createNativeQuery(" select peakId, " +
    " chrom, chromStart, chromEnd, ratio, quality" +
    " type, count, firstPeakId, lastPeakId " +
    " from " + abID.track.getTableData() +
    " order by chrom, chromStart ", AberrationCNVCAT.class);
    List<AberrationCNVCAT> list = q.getResultList();
    
    for (AberrationCNVCAT a : list) {
    out.write(
    patientID + ";4;" + a.getFirstPeakId() + ";" + a.getLastPeakId() + ";" +
    //a.getChrom().substring(3) + ";" +
    (a.getType().contentEquals(Aberration.DELETION) ? "1" : "3") + ";" +
    N.format(a.getRatio()) + ";1;none;1;" + a.getCount() + ";" + refID + ";" +
    N.format(a.getQuality()) + ";" + a.getChrom() + ";" +
    a.getChromStart() + ";" + a.getChromEnd());
    out.newLine();
    }
    
    }
    } catch (Exception e) {
    throw e;
    } finally {
    out.close();
    //em.close();
    }
    
    return sOutfile;
    }*/
    @Override
    public String exportIntoFile(List<? extends AberrationIds> aList,
            String sOutfile)
            throws IOException,
            Exception {
        /* if (CNVCATPropertiesMod.props().isExportMRNET()) {
        return this.exportIntoFileMRNET(aList, sOutfile);
        }
         */

        if (aList.size() == 0) {
            return null;
        }
        List<AberrantRegions> idList = (List<AberrantRegions>) aList;
        sOutfile = sOutfile + ".csv";
        System.out.println("export path " + sOutfile);
        EntityManager em = DBService.getEntityManger();

        if (em == null) {

            Logger.getLogger(AberrationManager.class.getName()).log(Level.WARNING,
                    "No database connection");

            return null;
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(sOutfile));

        try {

            out.write("CaseId;Parameter;StartClone;EndClone;Count;CopyNumberType;Ratio;Quality;chrom;chromStart;chromEnd");
            out.newLine();
            /*
            AG0246_s_Cy3_713_Cy5_NA15510_251469344046_090608_CGH_v4_10_Apr08
            NA15510 -> RefDNA
            B713 -> PatientId */

            DecimalFormat N = (DecimalFormat) DecimalFormat.getInstance();
            DecimalFormatSymbols NS = DecimalFormatSymbols.getInstance();
            NS.setDecimalSeparator('.');
            NS.setGroupingSeparator(',');
            N.setDecimalFormatSymbols(NS);
            N.setMaximumFractionDigits(5);
            for (AberrantRegions abID : idList) {
                //EntityTransaction userTransaction = em.getTransaction();

                //userTransaction.begin();

                Query q = em.createNativeQuery(" select peakId, " +
                        " chrom, chromStart, chromEnd, ratio, quality" +
                        " type, count, firstPeakId, lastPeakId " +
                        " from " + abID.track.getTableData() +
                        " order by chrom, chromStart ", AberrationCNVCAT.class);
                List<AberrationCNVCAT> list = q.getResultList();

                for (AberrationCNVCAT a : list) {
                    out.write(
                            //CNVCATPropertiesMod.props().getMrnetAbr() + ";" + 
                            abID.getTrackId() + ";" + abID.getParamAsString() + ";" +
                            a.getFirstPeakId() + ";" + a.getLastPeakId() + ";" + a.getCount() + ";" +
                            //a.getChrom().substring(3) + ";" +
                            a.getType() + ";" +
                            N.format(a.getRatio()) + ";" + N.format(a.getQuality()) + ";" +
                            a.getChrom() + ";" + a.getChromStart() + ";" + a.getChromEnd());
                    out.newLine();
                }

            }
        } catch (Exception e) {
            throw e;
        } finally {
            out.close();
        //em.close();
        }

        return sOutfile;
    }

    @Override
    protected int getPosEnd(List<? extends Aberration> a, long posEnd) {
        return Collections.binarySearch(a, new AberrationCNVCAT(posEnd, (long) 0), Region.compByStart);
    }

    @Override
    protected int getPosStart(List<? extends Aberration> x, long posStart) {
        return Collections.binarySearch(x, new AberrationCNVCAT(0, posStart), Region.compByEnd);
    }
}
