package org.molgen.genomeCATPro.data;

/**
 * @name DataManager
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package. Katrin Tebel
 * <tebel at molgen.mpg.de>. The contents of this file are subject to the terms
 * of either the GNU General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.annotation.AnnotationManager;
import org.molgen.genomeCATPro.annotation.AnnotationManagerImpl;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.annotation.GeneImpl;
import org.molgen.genomeCATPro.annotation.RegionAnnotation;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.ProjectService;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 * 310513 kt exception spotclazz 300512 doFilter() addSamples (of ParentTrack)
 * for Track doFilter() setName for new Data
 *
 * 010812 add annotateData 120912 reimplement annotateData, thread per
 * chromosome, use spatial index 161012 convertExperiment bugfix
 */
public class DataManager {

    public static void addExperiment2Project(String projectname, ExperimentDetail exp) throws Exception {
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO, " add experiment"
                + exp.getName() + " to " + projectname);

        Connection con = null;
        Statement s = null;
        EntityManager em = null;
        EntityTransaction userTransaction = null;
        try {
            em = DBService.getEntityManger();
            userTransaction = em.getTransaction();
            userTransaction.begin();
            Study p = ProjectService.getProjectByName(projectname, em);
            em.merge(exp);
            ProjectService.addExperiment(p, exp, em);
            userTransaction.commit();
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            if (userTransaction != null) {
                try {
                    userTransaction.rollback();
                } catch (Exception e) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            throw ex;
        } finally {
            em.close();
        }
        ExperimentService.notifyListener();
    }

    public enum AnnoQuery {

        DataContainsAnno, DataWithinAnno, Overlap;
    }

    public enum AnnoSubject {

        Middle, Whole;
    }

    /**
     *
     * @param data
     * @param newNameData
     * @param annoName
     * @param query
     * @param pos
     * @param field
     * @param downstream
     * @param upstream
     * @return
     */
    public static Data annotateData(final Data data,
            String annoName,
            final String field,
            final AnnoQuery query, final AnnoSubject pos,
            final int downstream,
            final int upstream) {

        final boolean extend_anno_region = (downstream == 0 && upstream == 0 ? false : true);
        String info = "Anno: " + annoName + " field: " + field
                + " QUERY: " + (query == AnnoQuery.DataContainsAnno ? "DataContainsAnno " : (query == AnnoQuery.DataWithinAnno ? " DataWithinAnno " : " Overlap"))
                + " SUBJECT: " + (pos == AnnoSubject.Middle ? " Middle " : " Within ")
                + (extend_anno_region ? ("extend AnnoRegion UP: " + upstream + " DOWN: " + downstream) : " ");
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO, "annotate " + data.getTableData() + "  " + info);

        String clazz = data.getClazz();

        String clazzAnno = "";

        if (annoName.contentEquals(GeneImpl.nameId)) {
            clazzAnno = clazz + Defaults.annoGeneClazzExtension;

        } else {
            clazzAnno = clazz + Defaults.annoClazzExtension;
        }
        IFeature f = DataService.getFeatureClazz(clazzAnno);


        /* String annoTableName = DBUtils.getAnnoTableForRelease(
        annoName,
        Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));*/
        final AnnotationManager am = new AnnotationManagerImpl(
                Defaults.GenomeRelease.toRelease(data.getGenomeRelease()), annoName);
        List<String> _chroms = CytoBandManagerImpl.stGetChroms(
                Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));

        //final AnnotationList anno = am.getAnnotation();
        //String annoClazz = anno.getClazz();
        EntityManager em = DBService.getEntityManger();
        EntityTransaction userTransaction = null;

        String newNameData = Utils.getUniquableName(data.getName());

        Connection con = null;

        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            Statement s = con.createStatement();
            Data newData = null;
            Data test = null;

            try {
                // add spatial position to query table 
                DBUtils.addPositionAtTable(data.getTableData());

                final int lengthData = DBUtils.getMeanLengthPosition(data.getTableData());

                if (data instanceof ExperimentData) {
                    newData = new ExperimentData();
                    newData.setName(newNameData);

                    newData.setClazz(clazzAnno);
                    newData.setDataType(Defaults.DataType.toDataType(data.getDataType()));

                    newData.setGenomeRelease(Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));

                    test = (Data) ExperimentService.getExperimentData(
                            newData.getName(), data.getGenomeRelease().toString());

                }
                if (data instanceof Track) {

                    newData = new Track(newNameData, data.getGenomeRelease(), data.getDataType());

                    newData.setClazz(clazzAnno);

                    test = TrackService.getTrack(newData.getName(), data.getGenomeRelease());
                    /*
                if (!(data instanceof ExperimentData)) {
                Logger.getLogger(DataManager.class.getName()).log(Level.WARNING,
                "filterData", " No ExperimentData - save failed!");
                return null;
                }
                     */
                }
                if (test != null) {
                    throw new RuntimeException("Name already given, please change");
                }

                newData.setProcProcessing(DataManager.procDoAnnotate);
                newData.setParamProcessing(info);
                if (newData instanceof ExperimentData) {
                    ((ExperimentData) newData).setPlatformdata(((ExperimentData) data).getPlatformdata());
                }

                data.addChildData(newData);
                userTransaction = em.getTransaction();
                userTransaction.begin();

                if (newData instanceof ExperimentData) {
                    ((ExperimentData) data).getExperiment().addExperimentData(
                            (ExperimentData) newData);

                    ExperimentService.persistsExperimentData((ExperimentData) newData, em);
                }
                if (newData instanceof Track) {
                    TrackService.persistsTrack((Track) newData, em);

                }
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "New Table - table data: " + newData.getTableData());

                // create Spot Tabelle
                //String sql = f.getCreateTableSQL(newData);
                //Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                //      "New Table - table data: " + sql);
                s.execute("DROP TABLE if EXISTS " + newData.getTableData());

                // copy table structure from original table
                s.execute("CREATE TABLE " + newData.getTableData() + " LIKE " + data.getTableData());
                String sql = "";
                // primary key cant be imported -  - Integrity violation
                // drop id, create new one without index
                sql = " ALTER TABLE " + newData.getTableData() + " DROP `id` ";
                s.execute(sql);

                sql = "ALTER TABLE " + newData.getTableData() + " ADD `id` BIGINT unsigned not null FIRST";
                s.execute(sql);

                sql = "ALTER TABLE " + newData.getTableData() + " ADD INDEX(`id`)";
                s.execute(sql);
                // add column "anno"
                s.execute("ALTER TABLE  " + newData.getTableData() + " ADD "
                        + Defaults.annoColName + " varchar(255) NOT NULL default \'\' ");

                final String newD = newData.getTableData();
                final String oldD = data.getTableData();
                Thread workers[] = new Thread[_chroms.size()];
                final Integer results[] = new Integer[_chroms.size()];
                Arrays.fill(results, 0);
                for (String chromId : _chroms) {
                    final String _chromId = chromId;
                    final int ii = _chroms.indexOf(chromId);

                    workers[ii] = new Thread(new Runnable() {

                        String _sql = "";
                        int ichrom = 0;

                        public void run() {
                            try {
                                Connection _con = Database.getDBConnection(CorePropertiesMod.props().getDb());
                                Statement _s = _con.createStatement();
                                List<? extends RegionAnnotation> _data = am.getData(_chromId);
                                if (_data == null || _data.size() == 0) {
                                    Logger.getLogger(AnnotationManagerImpl.class.getName()).log(
                                            Level.WARNING, " no data for " + _chromId);
                                    return;
                                }
                                for (RegionAnnotation annoR : _data) {
                                    Object o = DataService.getValue(annoR, field);
                                    if (o != null) {
                                        o = o.toString().replaceAll("\'", "");
                                    }
                                    ichrom = RegionLib.fromChrToInt(_chromId);
                                    if (extend_anno_region) {
                                        if (annoR.getChromStart() - downstream > 0) {
                                            annoR.setChromStart(annoR.getChromStart() - downstream);
                                        }
                                        annoR.setChromEnd(annoR.getChromEnd() + upstream);
                                    }
                                    if (query == AnnoQuery.Overlap) {
                                        _sql = "INSERT INTO  " + newD
                                                + "  SELECT d.*, \'"
                                                + (o != null ? o.toString() : "") + "\'"
                                                + " FROM " + oldD + " as d  "
                                                + " WHERE MBRIntersects(d.gc_position,"
                                                + "LineString("
                                                + "Point(" + ichrom + "," + annoR.getChromStart() + "), "
                                                + "Point(" + ichrom + ", " + annoR.getChromEnd() + "))"
                                                + ")";
                                    } else {
                                        switch (pos) {
                                            case Whole:

                                                // data query whole region inside
                                                //Contains returns t (TRUE) if 
                                                //the second geometry is completely 
                                                //contained by the first geometry. 
                                                // The contains predicate returns the 
                                                //exact opposite result of the within predicate.
                                                if (query == AnnoQuery.DataContainsAnno) {
                                                    _sql = "INSERT INTO  " + newD
                                                            + "  SELECT d.*, \'"
                                                            + (o != null ? o.toString() : "") + "\'"
                                                            + " FROM " + oldD + " as d  "
                                                            + " WHERE MBRContains( d.gc_position,"
                                                            + " LineString("
                                                            + " Point(" + ichrom + "," + annoR.getChromStart() + "), "
                                                            + " Point(" + ichrom + ", " + annoR.getChromEnd() + "))"
                                                            + " )";
                                                } else {
                                                    _sql = "INSERT INTO  " + newD
                                                            + "  SELECT d.*, \'"
                                                            + (o != null ? o.toString() : "") + "\'"
                                                            + " FROM " + oldD + " as d  "
                                                            + " WHERE MBRWithin( d.gc_position,"
                                                            + " LineString("
                                                            + " Point(" + ichrom + "," + annoR.getChromStart() + "), "
                                                            + " Point(" + ichrom + ", " + annoR.getChromEnd() + "))"
                                                            + " )";
                                                }
                                                break;
                                            case Middle:
                                                // genomic area in question restricted to centere point

                                                // inside
                                                if (query == AnnoQuery.DataContainsAnno) {
                                                    // anno within data
                                                    _sql = "INSERT INTO  " + newD
                                                            + "  SELECT d.*, \'"
                                                            + (o != null ? o.toString() : "") + "\'"
                                                            + " FROM " + oldD + " as d  "
                                                            + " WHERE MBRContains( "
                                                            + " d.gc_position,"
                                                            + " Point(" + ichrom + "," + ((annoR.getChromEnd() + annoR.getChromStart()) / 2) + ") "
                                                            + ")";
                                                    /*
                                                _sql = "INSERT INTO  " + newD +
                                                "  SELECT d.*, \'" +
                                                (o != null ? o.toString() : "") + "\'" +
                                                " FROM " + oldD + " as d  " +
                                                " WHERE d.chrom =  \'" + _chromId + "\'" +
                                                " AND (  d.chromStart  <= " + (annoR.getChromEnd() + annoR.getChromStart()) / 2 +
                                                " AND  d.chromEnd  >=  " + (annoR.getChromEnd() + annoR.getChromStart()) / 2 + " ) ";
                                                     */
                                                } else {
                                                    // data middle within annotation

                                                    // operation on gc_position avert using of spatial index
                                                    // therefor extend annotation area to half of mean data length 
                                                    _sql = "INSERT INTO  " + newD
                                                            + "  SELECT d.*, \'"
                                                            + (o != null ? o.toString() : "") + "\'"
                                                            + " FROM " + oldD + " as d  "
                                                            + " WHERE MBRWithin( d.gc_position,"
                                                            + " LineString("
                                                            + " Point(" + ichrom + "," + (annoR.getChromStart() - lengthData / 2) + "), "
                                                            + " Point(" + ichrom + ", " + (annoR.getChromEnd() + lengthData / 2) + "))"
                                                            + " )";

                                                    /* _sql = "INSERT INTO  " + newD +
                                                "  SELECT d.*, \'" +
                                                (o != null ? o.toString() : "") + "\'" +
                                                " FROM " + oldD + " as d  " +
                                                " WHERE d.chrom =  \'" + _chromId + "\'" +
                                                " AND  ( " + annoR.getChromStart() + "<=  ((d.chromStart + d.chromEnd)/2)  " +
                                                " AND " + annoR.getChromEnd() + "  >=  ((d.chromStart + d.chromEnd)/2)  ) ";
                                                     */
                                                }

                                                break;
                                        }
                                    }
                                    //Logger.getLogger(DataManager.class.getName()).log(Level.INFO, _sql);
                                    _s.execute(_sql);
                                    results[ii] += _s.getUpdateCount();
                                    //Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                                    //       "inserted for chrom  " + _chromId + " : " + results[ii]);
                                }
                            } catch (Exception e) {
                                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                                        _sql);
                                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                                        "", e);

                                results[ii] = -1;
                            }
                        }
                    ;
                    });
                    workers[ii].start();
                }
                for (int j = 0; j < workers.length; j++) {
                    workers[j].join(0);
                }
                int i = 0;
                for (int j = 0; j < results.length; j++) {
                    if (results[j] == -1) {
                        throw new Exception("Error");
                    } else {
                        i += results[j];
                    }
                }
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "inserted with anno " + i);

                // insert data wich doesnt contain to any gene
                sql = "INSERT INTO " + newData.getTableData() + " "
                        + " SELECT d.* , \'\' FROM " + data.getTableData() + "  as d  "
                        + " WHERE NOT EXISTS "
                        + " (SELECT * FROM " + newData.getTableData() + " as dneu"
                        + " WHERE dneu.id = d.id); ";
                s.execute(sql);
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "inserted without anno: " + s.getUpdateCount());

                sql = " ALTER TABLE " + newData.getTableData() + " DROP `id` ";
                s.execute(sql);
                sql = "ALTER TABLE " + newData.getTableData() + " ADD `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST";
                s.execute(sql);
                sql = "ALTER TABLE " + newData.getTableData() + " ADD SPATIAL INDEX(gc_position)";

                //update array
                sql = "SELECT count(*) from " + newData.getTableData();
                ResultSet rs = s.executeQuery(sql);

                rs.next();

                newData.setNof(rs.getInt(1));
                // TODO MEDIAN!
                sql = "SELECT  AVG(ratio), VAR_SAMP(ratio), STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) "
                        + " from " + newData.getTableData();

                rs = s.executeQuery(sql);

                rs.next();
                DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
                otherSymbols.setDecimalSeparator('.');
                //otherSymbols.setGroupingSeparator(',');
                DecimalFormat myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
                newData.setMean(new Double(myFormatter.format(rs.getDouble(
                        1))));
                newData.setVariance(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
                newData.setStddev(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
                newData.setMinRatio(rs.getDouble(4));
                newData.setMaxRatio(rs.getDouble(5));

                try {
                    double median = DBUtils.getMedian(newData.getTableData(), "ratio");
                    newData.setMedian(median);
                } catch (Exception e) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                            "get median", e);
                }

                //em.flush();
                // 300512 kt
                if (newData instanceof Track && data instanceof Track && ((Track) data).getSamples().size() > 0) {

                    TrackService.forwardSamples(((Track) data).getSamples(), (Track) newData, em);
                }
                userTransaction.commit();

                /*if (newData instanceof ExperimentData) {
                ((ExperimentData) data).getExperiment().addExperimentData(
                (ExperimentData) newData);
                
                }*/
                data.addChildData(newData);
                ExperimentService.notifyListener();
                return newData;

            } catch (Exception e) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, e);

                if (newData instanceof ExperimentData) {
                    ((ExperimentData) data).getExperiment().removeExperimentData(
                            (ExperimentData) newData);

                }

                //throw new RuntimeException(e);
                /*if (con != null) {
                try {
                s = con.createStatement();
                s.execute("DROP TABLE if EXISTS " + newData.getTableData());
                } catch (Exception ie) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ie);
                }
                }
                 */
                if (userTransaction != null && userTransaction.isActive()) {
                    userTransaction.rollback();
                }
                // transaction no longer active
                // merge would create entity, so use find!!
                Data d = null;
                em.getTransaction().begin();
                if (newData instanceof ExperimentData && newData != null && newData.getId() != null) {
                    d = em.find(ExperimentData.class, newData.getId());
                    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                            "Exception: find  experiment data " + newData.getId());
                }
                if (newData instanceof Track && newData != null && newData.getId() != null) {
                    d = em.find(Track.class, newData.getId());
                    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                            "Exception: find  track data " + newData.getId());
                }
                if (d != null) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                            "Exception: delete  data " + d.getId());
                    try {
                        em.remove(d);
                    } catch (Exception es) {
                        Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                                "delete data", es);
                    }
                }

                em.getTransaction().commit();
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param data
     * @param newNameData
     * @param annoName
     * @param field
     * @return
     */
    public static Data annotateDataOld(final Data data,
            String annoName,
            final String field,
            final AnnoQuery query, final AnnoSubject pos,
            final int downstream,
            final int upstream) {

        final boolean outside = (downstream == 0 && upstream == 0 ? false : true);
        String info = "Anno: " + annoName + " field: " + field
                + " QUERY: " + (query == AnnoQuery.DataContainsAnno ? " Data " : (query == AnnoQuery.DataWithinAnno ? " Anno " : " Overlap"))
                + " SUBJECT: " + (pos == AnnoSubject.Middle ? " Middle " : " Within ")
                + (outside ? (" UP: " + upstream + " DOWN: " + downstream) : " INSIDE ");
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO, "annotate " + data.getTableData() + "  " + info);

        String clazz = data.getClazz();

        String clazzAnno = "";

        if (annoName.contentEquals(GeneImpl.nameId)) {
            clazzAnno = clazz + Defaults.annoGeneClazzExtension;

        } else {
            clazzAnno = clazz + Defaults.annoClazzExtension;
        }
        IFeature f = DataService.getFeatureClazz(clazzAnno);


        /* String annoTableName = DBUtils.getAnnoTableForRelease(
        annoName,
        Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));*/
        final AnnotationManager am = new AnnotationManagerImpl(
                Defaults.GenomeRelease.toRelease(data.getGenomeRelease()), annoName);
        List<String> _chroms = CytoBandManagerImpl.stGetChroms(
                Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));

        //final AnnotationList anno = am.getAnnotation();
        //String annoClazz = anno.getClazz();
        EntityManager em = DBService.getEntityManger();
        EntityTransaction userTransaction = null;

        String newNameData = Utils.getUniquableName(data.getName());

        Connection con = null;

        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            Statement s = con.createStatement();
            Data newData = null;
            Data test = null;
            try {

                if (data instanceof ExperimentData) {
                    newData = new ExperimentData();
                    newData.setName(newNameData);

                    newData.setClazz(clazzAnno);
                    newData.setDataType(Defaults.DataType.toDataType(data.getDataType()));

                    newData.setGenomeRelease(Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));

                    test = (Data) ExperimentService.getExperimentData(
                            newData.getName(), data.getGenomeRelease().toString());

                }
                if (data instanceof Track) {

                    newData = new Track(newNameData, data.getGenomeRelease(), data.getDataType());

                    newData.setClazz(clazzAnno);

                    test = TrackService.getTrack(newData.getName(), data.getGenomeRelease());
                    /*
                if (!(data instanceof ExperimentData)) {
                Logger.getLogger(DataManager.class.getName()).log(Level.WARNING,
                "filterData", " No ExperimentData - save failed!");
                return null;
                }
                     */
                }
                if (test != null) {
                    throw new RuntimeException("Name already given, please change");
                }

                newData.setProcProcessing(DataManager.procDoAnnotate);
                newData.setParamProcessing(info);
                if (newData instanceof ExperimentData) {
                    ((ExperimentData) newData).setPlatformdata(((ExperimentData) data).getPlatformdata());
                }

                data.addChildData(newData);
                userTransaction = em.getTransaction();
                userTransaction.begin();

                if (newData instanceof ExperimentData) {
                    ((ExperimentData) data).getExperiment().addExperimentData(
                            (ExperimentData) newData);

                    ExperimentService.persistsExperimentData((ExperimentData) newData, em);
                }
                if (newData instanceof Track) {
                    TrackService.persistsTrack((Track) newData, em);

                }
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "New Table - table data: " + newData.getTableData());

                // create Spot Tabelle
                String sql = f.getCreateTableSQL(newData);
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "New Table - table data: " + sql);
                s.execute("DROP TABLE if EXISTS " + newData.getTableData());
                s.execute(sql);

                final String newD = newData.getTableData();
                final String oldD = data.getTableData();
                Thread workers[] = new Thread[_chroms.size()];
                final Integer results[] = new Integer[_chroms.size()];
                Arrays.fill(results, 0);
                for (String chromId : _chroms) {
                    final String _chromId = chromId;
                    final int ii = _chroms.indexOf(chromId);

                    workers[ii] = new Thread(new Runnable() {

                        String _sql = "";
                        int ichrom = 0;

                        public void run() {
                            try {
                                Connection _con = Database.getDBConnection(CorePropertiesMod.props().getDb());
                                Statement _s = _con.createStatement();
                                List<? extends RegionAnnotation> _data = am.getData(_chromId);
                                if (_data == null || _data.size() == 0) {
                                    Logger.getLogger(AnnotationManagerImpl.class.getName()).log(
                                            Level.WARNING, " no data for " + _chromId);
                                    return;
                                }
                                for (RegionAnnotation annoR : _data) {
                                    Object o = DataService.getValue(annoR, field);
                                    if (o != null) {
                                        o = o.toString().replaceAll("\'", "");
                                    }
                                    ichrom = RegionLib.fromChrToInt(_chromId);
                                    switch (query) {
                                        case Overlap:

                                            // data query whole region within
                                            _sql = "INSERT INTO  " + newD
                                                    + "  SELECT d.*, \'"
                                                    + (o != null ? o.toString() : "") + "\'"
                                                    + " FROM " + oldD + " as d  "
                                                    + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                    + " AND (  d.chromStart <= " + annoR.getChromEnd()
                                                    + " AND  d.chromEnd  >=  " + annoR.getChromStart() + " ) ";

                                            /*_sql = "INSERT INTO  " + newD +
                                            "  SELECT d.*, \'" +
                                            (o != null ? o.toString() : "") + "\'" +
                                            " FROM " + oldD + " as d  " +
                                            " WHERE MBRIntersects(" +
                                            "LineString("+
                                            "Point("+ichrom+ ","  +  annoR.getChromStart()  + "), " +
                                            "Point("+ichrom+", " + annoR.getChromEnd() +"))," +
                                            "d.position)";
                                            
                                             */
                                            break;
                                        case DataContainsAnno:
                                            switch (pos) {
                                                case Whole:

                                                    if (!outside) {
                                                        // data query whole region inside
                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND ( "
                                                                + "  d.chromStart   <= " + annoR.getChromStart()
                                                                + " AND  d.chromEnd  >=  " + annoR.getChromEnd() + " ) ";
                                                    } else {
                                                        // data query whole region outside, i.e. up or downstream
                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND ( "
                                                                + " ( (d.chromStart + " + downstream + " ) < " + annoR.getChromStart()
                                                                + " AND  d.chromStart  >  " + annoR.getChromEnd() + " ) "
                                                                + " OR "
                                                                + " ( d.chromEnd   < " + annoR.getChromStart()
                                                                + " AND  (d.chromEnd + " + upstream + " ) >  " + annoR.getChromEnd() + " ) "
                                                                + ")";
                                                    }
                                                    break;

                                                case Middle:
                                                    if (!outside) {
                                                        // data query middle within
                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND (  d.chromStart  <= " + (annoR.getChromEnd() + annoR.getChromStart()) / 2
                                                                + " AND  d.chromEnd  >=  " + (annoR.getChromEnd() + annoR.getChromStart()) / 2 + " ) ";
                                                    } else {
                                                        // data query middle, outside i.e. up oder downstream
                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND ( "
                                                                + "( (d.chromStart + " + downstream + " ) < " + ((annoR.getChromEnd() + annoR.getChromStart()) / 2)
                                                                + " AND d.chromStart  > " + ((annoR.getChromEnd() + annoR.getChromStart()) / 2) + " )"
                                                                + " OR "
                                                                + "( d.chromEnd  < " + ((annoR.getChromEnd() + annoR.getChromStart()) / 2)
                                                                + " AND (d.chromEnd + " + upstream + " ) > " + ((annoR.getChromEnd() + annoR.getChromStart()) / 2) + " )"
                                                                + " )";
                                                    }
                                            }
                                            break;
                                        case DataWithinAnno:
                                            switch (pos) {
                                                case Whole:

                                                    if (!outside) {
                                                        // anno query whole data region inside
                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND "
                                                                + " ( " + annoR.getChromStart() + " <=  d.chromStart "
                                                                + " AND " + annoR.getChromEnd() + " >=  d.chromEnd  ) ";
                                                    } else {
                                                        // anno query whole data region outside, i.e. up or downstream

                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND ( "
                                                                + " ( " + (annoR.getChromStart() + downstream) + " < d.chromStart "
                                                                + " AND  " + annoR.getChromStart() + " > d.chromEnd ) "
                                                                + " OR "
                                                                + " (" + annoR.getChromEnd() + " < d.chromStart  "
                                                                + " AND " + (annoR.getChromEnd() + upstream) + " > d.chromEnd   ) "
                                                                + ")";
                                                    }
                                                    break;

                                                case Middle:
                                                    if (!outside) {
                                                        // anno query middle within
                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND  ( " + annoR.getChromStart() + "<=  ((d.chromStart + d.chromEnd)/2)  "
                                                                + " AND " + annoR.getChromEnd() + "  >=  ((d.chromStart + d.chromEnd)/2)  ) ";
                                                        //" INTO OUTFILE \'/project/H1N1/Data/Katrin/" + newD + ".txt\'";
                                                    } else {
                                                        // anno query middle outside, i.e. up or downstream
                                                        _sql = "INSERT INTO  " + newD
                                                                + "  SELECT d.*, \'"
                                                                + (o != null ? o.toString() : "") + "\'"
                                                                + " FROM " + oldD + " as d  "
                                                                + " WHERE d.chrom =  \'" + _chromId + "\'"
                                                                + " AND ( "
                                                                + " ( " + (annoR.getChromStart() + downstream) + " <  ((d.chromStart + d.chromEnd)/2) "
                                                                + " AND " + annoR.getChromStart() + " > ((d.chromStart + d.chromEnd)/2)  )"
                                                                + " OR "
                                                                + "( " + annoR.getChromEnd() + " < ((d.chromStart + d.chromEnd)/2) "
                                                                + " AND " + (annoR.getChromEnd() + upstream) + " > ((d.chromStart + d.chromEnd)/2) )"
                                                                + " )";
                                                    }
                                            }
                                            //Logger.getLogger(DataManager.class.getName()).log(Level.INFO, _sql);
                                            break;
                                    }

                                    //Logger.getLogger(DataManager.class.getName()).log(Level.INFO, sql);
                                    _s.execute(_sql);
                                    results[ii] += _s.getUpdateCount();
                                }

                                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                                        "inserted for chrom  " + _chromId + " : " + results[ii]);

                            } catch (Exception e) {
                                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                                        _sql);
                                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                                        "", e);

                                results[ii] = -1;
                            }
                        }
                    ;
                    });
                    workers[ii].start();
                }
                for (int j = 0; j < workers.length; j++) {
                    workers[j].join(0);
                }
                int i = 0;
                for (int j = 0; j < results.length; j++) {
                    if (results[j] == -1) {
                        throw new Exception("Error");
                    } else {
                        i += results[j];
                    }
                }
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "inserted with anno " + i);

                // insert data wich doesnt contain to any gene
                sql = "INSERT INTO " + newData.getTableData() + " "
                        + " SELECT d.* , \'\' FROM " + data.getTableData() + "  as d  "
                        + " WHERE NOT EXISTS "
                        + " (SELECT * FROM " + newData.getTableData() + " as dneu"
                        + " WHERE dneu.id = d.id); ";
                s.execute(sql);
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "inserted without anno: " + s.getUpdateCount());

                sql = " ALTER TABLE " + newData.getTableData() + " DROP `id` ";
                s.execute(sql);
                sql = "ALTER TABLE " + newData.getTableData() + " ADD `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST";
                s.execute(sql);

                //update array
                sql = "SELECT count(*) from " + newData.getTableData();
                ResultSet rs = s.executeQuery(sql);

                rs.next();

                newData.setNof(rs.getInt(1));
                // TODO MEDIAN!
                sql = "SELECT  AVG(ratio), VAR_SAMP(ratio), STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) "
                        + " from " + newData.getTableData();

                rs = s.executeQuery(sql);

                rs.next();
                DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
                otherSymbols.setDecimalSeparator('.');
                //otherSymbols.setGroupingSeparator(',');
                DecimalFormat myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
                newData.setMean(new Double(myFormatter.format(rs.getDouble(
                        1))));
                newData.setVariance(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
                newData.setStddev(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
                newData.setMinRatio(rs.getDouble(4));
                newData.setMaxRatio(rs.getDouble(5));

                try {
                    double median = DBUtils.getMedian(newData.getTableData(), "ratio");
                    newData.setMedian(median);
                } catch (Exception e) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                            "get median", e);
                }

                //em.flush();
                // 300512 kt
                if (newData instanceof Track && data instanceof Track && ((Track) data).getSamples().size() > 0) {

                    TrackService.forwardSamples(((Track) data).getSamples(), (Track) newData, em);
                }
                userTransaction.commit();

                /*if (newData instanceof ExperimentData) {
                ((ExperimentData) data).getExperiment().addExperimentData(
                (ExperimentData) newData);
                
                }*/
                data.addChildData(newData);
                ExperimentService.notifyListener();
                return newData;

            } catch (Exception e) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, e);

                if (newData instanceof ExperimentData) {
                    ((ExperimentData) data).getExperiment().removeExperimentData(
                            (ExperimentData) newData);

                }

                //throw new RuntimeException(e);
                /*if (con != null) {
                try {
                s = con.createStatement();
                s.execute("DROP TABLE if EXISTS " + newData.getTableData());
                } catch (Exception ie) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ie);
                }
                }
                 */
                if (userTransaction != null && userTransaction.isActive()) {
                    userTransaction.rollback();
                }
                // transaction no longer active
                // merge would create entity, so use find!!
                Data d = null;
                em.getTransaction().begin();
                if (newData instanceof ExperimentData && newData != null && newData.getId() != null) {
                    d = em.find(ExperimentData.class, newData.getId());
                    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                            "Exception: find  experiment data " + newData.getId());
                }
                if (newData instanceof Track && newData != null && newData.getId() != null) {
                    d = em.find(Track.class, newData.getId());
                    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                            "Exception: find  track data " + newData.getId());
                }
                if (d != null) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                            "Exception: delete  data " + d.getId());
                    try {
                        em.remove(d);
                    } catch (Exception es) {
                        Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                                "delete data", es);
                    }
                }

                em.getTransaction().commit();
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            em.close();
        }
    }

    public static ExperimentData convertExperiment(
            ExperimentData data, ExperimentData newData, PlatformData pData) throws Exception {

        Logger.getLogger(DataManager.class.getName()).log(Level.INFO, "convert "
                + data.getName() + " to " + newData.getGenomeRelease() + " via " + pData.toFullString());

        Connection con = null;
        Statement s = null;
        EntityManager em = null;
        EntityTransaction userTransaction = null;
        try {
            em = DBService.getEntityManger();

            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();

        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ISpot iSpot = null;
        //310513    kt
        try {
            iSpot = DataManager.getSpotClazz(data.getClazz());
        } catch (Exception e) {
            Logger.getLogger(DataManager.class.getName()).log(Level.WARNING, null, e);
        }
        if (iSpot == null) {
            throw new RuntimeException("convertExperiment: invalid clazz " + data.getClazz());
        }
        try {

            newData.setProcProcessing(DataManager.procDoConvert);
            newData.setParamProcessing("");
            newData.initTableData();

            ((ExperimentData) newData).setPlatformdata(pData);
            userTransaction = em.getTransaction();
            userTransaction.begin();

            ExperimentService.persistsExperimentData((ExperimentData) newData, em);

            // create table for new release
            s.execute("DROP TABLE if EXISTS " + newData.getTableData());
            s.execute("CREATE TABLE " + newData.getTableData() + " LIKE " + data.getTableData());

            String sql = "INSERT INTO  " + newData.getTableData()
                    + "  SELECT distinct data.* FROM " + data.getTableData() + " as data, "
                    + pData.getTableData() + " as p "
                    + " WHERE " + iSpot.getSQLtoPlattform("p", "data");

            Logger.getLogger(DataManager.class.getName()).log(Level.INFO, sql);
            s.execute(sql);
            //161012    kt  bugfix 
            sql = "UPDATE  " + newData.getTableData() + " as newd, "
                    + pData.getTableData() + " as p "
                    + " SET  newd.chrom = p.chrom , newd.chromStart = p.chromStart,  "
                    + " newd.chromEnd = p.chromEnd "
                    + " WHERE " + iSpot.getSQLtoPlattform("p", "newd");

            int i = s.executeUpdate(sql);
            Logger.getLogger(DataManager.class.getName()).log(Level.INFO, "updated: " + i);

            //update array
            sql = "SELECT count(*) from " + newData.getTableData();
            ResultSet rs = s.executeQuery(sql);

            rs.next();

            newData.setNof(rs.getInt(1));
            if (i != newData.getNof()) {  //161012    kt  bug

                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                        " update failed: " + newData.getNof() + " (nof) != " + i + " (updated)");
                throw new RuntimeException(" update failed: " + newData.getNof() + " (nof) != " + i + " (updated)");

            }
            sql = "SELECT  AVG(ratio), VAR_SAMP(ratio), STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) "
                    + " from " + newData.getTableData();

            rs = s.executeQuery(sql);

            rs.next();
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            //otherSymbols.setGroupingSeparator(',');
            DecimalFormat myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
            newData.setMean(new Double(myFormatter.format(rs.getDouble(
                    1))));
            newData.setVariance(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
            newData.setStddev(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
            newData.setMinRatio(rs.getDouble(4));
            newData.setMaxRatio(rs.getDouble(5));
            try {
                double median = DBUtils.getMedian(newData.getTableData(), "ratio");
                newData.setMedian(median);
            } catch (Exception e) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                        "get median", e);
            }
            em.flush();

            userTransaction.commit();

            // notify
            data.addChildData(newData);
            ExperimentService.notifyListener();
            return newData;

        } catch (Exception e) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, e);

            ((ExperimentData) data).getExperiment().removeExperimentData(
                    (ExperimentData) newData);

            //throw new RuntimeException(e);
            /*if (con != null) {
            try {
            s = con.createStatement();
            s.execute("DROP TABLE if EXISTS " + newData.getTableData());
            } catch (Exception ie) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ie);
            }
            }
             */
            if (userTransaction != null && userTransaction.isActive()) {
                userTransaction.rollback();
            }
            // transaction no longer active
            // merge would create entity, so use find!!
            Data d = null;
            em.getTransaction().begin();
            if (newData != null && newData.getId() != null) {
                d = em.find(ExperimentData.class, newData.getId());
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "Exception: find  experiment data " + newData.getId());
            }

            if (d != null) {
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "Exception: delete  data " + d.getId());
                try {
                    em.remove(d);
                } catch (Exception es) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                            "delete data", es);
                }
            }

            em.getTransaction().commit();
            throw e;

        } finally {
            em.close();
        }
    }

    @SuppressWarnings("empty-statement")
    public static void exportBED(Data d, String header, String filepath) throws Exception {
        // export chrom, start, stop id to external file
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                "export " + d.getTableData() + " into " + filepath);
        //filepath = filepath.replace(File.pathSeparator, "//");
        filepath = filepath.replace("\\", "\\\\");
        String clazz = d.getClazz();
        String cytoTable = DBUtils.getAnnoTableForRelease(
                CytoBandManagerImpl.name,
                Defaults.GenomeRelease.toRelease(d.getGenomeRelease()));
        RegionArray r = RegionLib.getRegionArrayClazz(clazz);
        String sql
                = " SELECT \'" + header + " \',\"\",\"\",\"\",\"\" UNION "
                + " SELECT chrom, "
                + " least(chromStart, chromEnd), greatest(chromStart,  chromEnd), "
                + r.getProbeColName() + ", "
                + r.getRatioColName()
                + " FROM " + d.getTableData()
                + " where chrom in (select chrom from " + cytoTable + ")"
                + " INTO OUTFILE \'" + filepath + "\'";

        Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                sql);
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());

        try {
            Statement s = con.createStatement();
            s.execute(sql);
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                    "exportBEDGraph", ex);
            throw ex;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                ;
            }
        }
    }

    @SuppressWarnings("empty-statement")
    /**
     * export data as bed graph
     */
    public static void exportBEDGraph(Data d, String header, String filepath) throws Exception {
        // export chrom, start, stop id to external file
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                "export " + d.getTableData() + " into " + filepath);
        filepath = filepath.replace("\\", "\\\\");

        String clazz = d.getClazz();

        RegionArray r = RegionLib.getRegionArrayClazz(clazz);

        String cytoTable = DBUtils.getAnnoTableForRelease(
                CytoBandManagerImpl.name,
                Defaults.GenomeRelease.toRelease(d.getGenomeRelease()));
        String sql
                = " SELECT \'" + header + " \',\"\",\"\",\"\" UNION "
                + " SELECT chrom, "
                + " least(chromStart, chromEnd), greatest(chromStart,  chromEnd), "
                + r.getRatioColName()
                + " FROM " + d.getTableData()
                + " where chrom in (select chrom from " + cytoTable + ")"
                + " INTO OUTFILE \'" + filepath + "\'";
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                sql);
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        try {
            Statement s = con.createStatement();
            s.execute(sql);
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                    "exportBEDGraph", ex);
            throw ex;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                ;
            }
        }
    }

    public static Data doFilter(
            Data data, String name, String _sql) throws Exception {
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                "Save " + data.getName() + " into " + name + " sql:   " + _sql);

        EntityManager em = DBService.getEntityManger();
        EntityTransaction userTransaction = null;

        //if(experiment != null)
        //    if(experiment.getArray().getGenomeRelease().contains(this.release))
        Connection con = null;
        Statement s = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Data newData = null;
        Data test = null;
        try {
            if (data instanceof ExperimentData) {
                newData = new ExperimentData();
                newData.setName(name);  // 300512 kt

                newData.setClazz(data.getClazz());
                newData.setDataType(Defaults.DataType.TRANSFORMED);

                newData.setGenomeRelease(Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));

                test = (Data) ExperimentService.getExperimentData(
                        newData.getName(), data.getGenomeRelease().toString());

            }
            if (data instanceof Track) {
                //300512kt
                newData = new Track(name, data.getGenomeRelease(), Defaults.DataType.TRANSFORMED.toString());

                newData.setClazz(data.getClazz());

                test = TrackService.getTrack(newData.getName(), data.getGenomeRelease());
                /*
            if (!(data instanceof ExperimentData)) {
            Logger.getLogger(DataManager.class.getName()).log(Level.WARNING,
            "filterData", " No ExperimentData - save failed!");
            return null;
            }
                 */
            }
            if (test != null) {
                throw new RuntimeException("Name already given, please change");
            }

            newData.setProcProcessing(DataManager.procDoFilter);
            newData.setParamProcessing(_sql);
            if (newData instanceof ExperimentData) {
                ((ExperimentData) newData).setPlatformdata(((ExperimentData) data).getPlatformdata());
            }

            //newData.setName(name);// 300512 kt
            data.addChildData(newData);
            userTransaction = em.getTransaction();
            userTransaction.begin();

            if (newData instanceof ExperimentData) {
                ((ExperimentData) data).getExperiment().addExperimentData(
                        (ExperimentData) newData);

                ExperimentService.persistsExperimentData((ExperimentData) newData, em);
            }
            if (newData instanceof Track) {
                TrackService.persistsTrack((Track) newData, em);

            }
            Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                    "New Table - table data: " + newData.getTableData());

            // create Spot Tabelle
            s.execute("DROP TABLE if EXISTS " + newData.getTableData());
            s.execute("CREATE TABLE " + newData.getTableData() + " LIKE " + data.getTableData());

            String sql = "INSERT INTO  " + newData.getTableData()
                    + "  SELECT * FROM " + data.getTableData()
                    + " WHERE " + _sql;
            //ratio < 0 && ratio <= d.getNegThreshold()) || (ratio > 0 && ratio >= d.getPosThreshold()
            Logger.getLogger(DataManager.class.getName()).log(Level.INFO, sql);

            s.execute(sql);

            // read data -> insert into table
            // todo dye swap
            //em.merge(newData);
            //update array
            sql = "SELECT count(*) from " + newData.getTableData();
            ResultSet rs = s.executeQuery(sql);

            rs.next();

            newData.setNof(rs.getInt(1));
            // TODO MEDIAN!
            sql = "SELECT  AVG(ratio), VAR_SAMP(ratio), STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) "
                    + " from " + newData.getTableData();

            rs = s.executeQuery(sql);

            rs.next();
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            //otherSymbols.setGroupingSeparator(',');
            DecimalFormat myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
            newData.setMean(new Double(myFormatter.format(rs.getDouble(
                    1))));
            newData.setVariance(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
            newData.setStddev(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
            newData.setMinRatio(rs.getDouble(4));
            newData.setMaxRatio(rs.getDouble(5));

            try {
                double median = DBUtils.getMedian(newData.getTableData(), "ratio");
                newData.setMedian(median);
            } catch (Exception e) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                        "get median", e);
            }

            //em.flush();
            // 300512 kt
            if (newData instanceof Track && data instanceof Track && ((Track) data).getSamples().size() > 0) {

                TrackService.forwardSamples(((Track) data).getSamples(), (Track) newData, em);
            }
            userTransaction.commit();

            /*if (newData instanceof ExperimentData) {
            ((ExperimentData) data).getExperiment().addExperimentData(
            (ExperimentData) newData);
            
            }*/
            data.addChildData(newData);
            ExperimentService.notifyListener();
            return newData;
            // todo?? woher klasse struktur 

        } catch (Exception e) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, e);

            if (newData instanceof ExperimentData) {
                ((ExperimentData) data).getExperiment().removeExperimentData(
                        (ExperimentData) newData);

            }

            //throw new RuntimeException(e);
            /*if (con != null) {
            try {
            s = con.createStatement();
            s.execute("DROP TABLE if EXISTS " + newData.getTableData());
            } catch (Exception ie) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ie);
            }
            }
             */
            if (userTransaction != null && userTransaction.isActive()) {
                userTransaction.rollback();
            }
            // transaction no longer active
            // merge would create entity, so use find!!
            Data d = null;
            em.getTransaction().begin();
            if (newData instanceof ExperimentData && newData != null && newData.getId() != null) {
                d = em.find(ExperimentData.class, newData.getId());
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "Exception: find  experiment data " + newData.getId());
            }
            if (newData instanceof Track && newData != null && newData.getId() != null) {
                d = em.find(Track.class, newData.getId());
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "Exception: find  track data " + newData.getId());
            }
            if (d != null) {
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "Exception: delete  data " + d.getId());
                try {
                    em.remove(d);
                } catch (Exception es) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                            "delete data", es);
                }
            }

            em.getTransaction().commit();
            throw e;

        } finally {
            em.close();
        }

    }

    /**
     * save filtered data in db
     *
     * @param data
     * @param name
     * @param negThreshold
     * @param posThreshold
     * @return
     */
    static public Data filterData(
            Data data, String name, double negThreshold, double posThreshold) throws Exception {
        String sql = " WHERE (ratio < 0 AND ratio  <= " + negThreshold + ")"
                + " OR  (ratio > 0 AND ratio >= " + posThreshold + ")";
        return DataManager.doFilter(data, name, sql);

        /*
    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
    "Save " + data.getName() + " into " + name + " with negTreshold " + negThreshold + " posThreshold " + posThreshold);
    
    EntityManager em = DBService.getEntityManger();
    EntityTransaction userTransaction = null;
    
    
    
    
    String tableData = null;
    //if(experiment != null)
    //    if(experiment.getArray().getGenomeRelease().contains(this.release))
    
    Connection con = null;
    Statement s = null;
    try {
    con = Database.getDBConnection(CorePropertiesMod.props().getDb());
    s = con.createStatement();
    } catch (Exception ex) {
    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
    }
    Data newData = null;
    
    try {
    if (data instanceof ExperimentData) {
    newData = new ExperimentData();
    }
    if (data instanceof Track) {
    newData = new Track();
    
    }
    newData.copy(data);
    newData.setId(null);
    
    newData.setName(name);
    
    data.addChildData(newData);
    
    newData.setDataType(Defaults.DataType.TRANSFORMED);
    
    if (newData instanceof ExperimentData) {
    ((ExperimentData) data).getExperiment().addExperimentData(
    (ExperimentData) newData);
    
    ExperimentService.persistsExperimentData((ExperimentData) newData, em);
    }
    if (newData instanceof Track) {
    
    TrackService.persistsTrack((Track) newData, em);
    }
    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
    "New Table - table data: " + newData.getTableData());
    tableData = newData.getTableData();
    // create Spot Tabelle
    s.execute("DROP TABLE if EXISTS " + tableData);
    s.execute("CREATE TABLE " + newData.getTableData() + " LIKE " + data.getTableData());
    
    String sql = "INSERT INTO  " + newData.getTableData() +
    "  SELECT * FROM " + data.getTableData() +
    " WHERE (ratio < 0 AND ratio  <= " + negThreshold + ")" +
    " OR  (ratio > 0 AND ratio >= " + posThreshold + ")";
    //ratio < 0 && ratio <= d.getNegThreshold()) || (ratio > 0 && ratio >= d.getPosThreshold()
    Logger.getLogger(DataManager.class.getName()).log(Level.INFO, sql);
    
    
    s.execute(sql);
    
    
    // read data -> insert into table
    // todo dye swap
    userTransaction = em.getTransaction();
    userTransaction.begin();
    em.merge(newData);
    
    //update array
    sql = "SELECT count(*) from " + newData.getTableData();
    ResultSet rs = s.executeQuery(sql);
    
    rs.next();
    
    newData.setNof(rs.getInt(1));
    // TODO MEDIAN!
    sql = "SELECT  AVG(ratio), null, VAR_SAMP(ratio), STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) " +
    " from " + newData.getTableData();
    
    rs = s.executeQuery(sql);
    
    rs.next();
    DecimalFormat myFormatter = new DecimalFormat("0.#####E0");
    newData.setMean(new Double(myFormatter.format(rs.getDouble(
    1))));
    newData.setMedian(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
    newData.setVariance(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
    newData.setStddev(myFormatter.parse(rs.getString(4) != null ? rs.getString(4) : "0").doubleValue());
    newData.setMinRatio(rs.getDouble(5));
    newData.setMaxRatio(rs.getDouble(6));
    
    em.flush();
    
    userTransaction.commit();
    if (newData instanceof ExperimentData) {
    ((ExperimentData) data).getExperiment().addExperimentData(
    (ExperimentData) newData);
    
    }
    data.addChildData(newData);
    return newData;
    // todo?? woher klasse struktur 
    
    } catch (Exception e) {
    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, e);
    
    if (newData instanceof ExperimentData) {
    ((ExperimentData) data).getExperiment().removeExperimentData(
    (ExperimentData) newData);
    
    }
    
    //throw new RuntimeException(e);
    if (con != null) {
    try {
    s = con.createStatement();
    s.execute("DROP TABLE if EXISTS " + newData.getTableData());
    } catch (Exception ie) {
    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ie);
    }
    }
    if (userTransaction != null && userTransaction.isActive()) {
    userTransaction.rollback();
    }
    // transaction no longer active
    // merge would create entity, so use find!!
    Data d = null;
    em.getTransaction().begin();
    if (newData instanceof ExperimentData && newData != null && newData.getId() != null) {
    d = em.find(ExperimentData.class, newData.getId());
    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
    "Exception: find  experiment data " + newData.getId());
    }
    if (newData instanceof Track && newData != null && newData.getId() != null) {
    d = em.find(Track.class, newData.getId());
    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
    "Exception: find  track data " + newData.getId());
    }
    if (d != null) {
    Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
    "Exception: delete  data " + d.getId());
    try {
    em.remove(d);
    } catch (Exception es) {
    Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
    "delete data", es);
    }
    }
    
    em.getTransaction().commit();
    
    } finally {
    em.close();
    }
    return null; */
    }

    public static int doTestFilter(Data data, String _sql) throws Exception {
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                "Test " + data.getName() + " sql:   " + _sql);
        Connection con = null;
        Statement s = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
            String sql = "  SELECT count(*) FROM " + data.getTableData()
                    + " WHERE " + _sql;
            Logger.getLogger(DataManager.class.getName()).log(Level.INFO, sql);
            ResultSet rs = s.executeQuery(sql);
            rs.next();

            return (rs.getInt(1));
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, "doTestFilter", ex);
            throw ex;
        }
    }
    static Lookup.Template<ISpot> tmplSpot = new Lookup.Template<ISpot>(
            org.molgen.genomeCATPro.data.ISpot.class);
    final static String procDoFilter = DataManager.class.getName() + "Filtered";
    final static String procDoConvert = DataManager.class.getName() + "Converted";
    final static String procDoAnnotate = "Annotated";

    public static ISpot getSpotClazz(String clazz) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                "looking for datatype clazz " + clazz);

        Result<ISpot> rslt = Lookup.getDefault().lookup(tmplSpot);
        for (Lookup.Item item : rslt.allItems()) {
            if (item.getType().getName().contentEquals(clazz)) {
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        "found " + item.getDisplayName());
                return (ISpot) item.getInstance();
            }
        }

        return null;
    }

    public static void tidyUp(int userid) {
        Connection con = null;
        Statement s = null;

        try {

            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();

        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        try {
            String sql = "select "
                    + "a.experimentListID, a.tableData, "
                    + "b.experimentListID, b.tableData "
                    + "from ExperimentList as a, ExperimentList as b "
                    + "where a.experimentDetailID = b.experimentDetailID and "
                    + "(a.parentID is null and b.parentID is null)  "
                    + "and a.genomeRelease != b.genomeRelease and "
                    + "a.procProcessing is null and b.originalFile is null  "
                    + " and a.idOwner = " + userid;
            Logger.getLogger(DataManager.class.getName()).log(Level.INFO, sql);
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                long id = rs.getLong(3);

                ExperimentData d = ExperimentService.getExperimentByDataId((long) id);

                tidyUp(d);

            }
        } catch (Exception e) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, e);
            return;
        }

    }

    public static void tidyUp(ExperimentData d) {
        EntityManager em;
        Connection con = null;
        Statement s = null;
        Logger.getLogger(DataManager.class.getName()).log(Level.INFO, d.toFullString());
        try {

            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
            em = DBService.getEntityManger();

        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        EntityTransaction et;

        try {
            et = em.getTransaction();
            et.begin();
            List<Track> tlist = TrackService.listChildrenForExperimentData(d);
            for (Track t : tlist) {
                t = em.merge(t);
                t.setName("CONVERR_" + t.getName());
                t.setModified(new Date());
            }
            List<ExperimentData> dlist = ExperimentService.listChildrenExperimentData(d);
            for (ExperimentData e : dlist) {
                tidyUp(e);
            }
            ISpot iSpot = null;
            try {
                //310512    kt
                iSpot = DataManager.getSpotClazz(d.getClazz());
            } catch (Exception e) {
                Logger.getLogger(DataManager.class.getName()).log(Level.WARNING, null, e);
            }
            if (iSpot == null) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, "tidyUp: invalid clazz " + d.getClazz());
                return;
            }
            PlatformData pData = d.getPlatformdata();

            String sql = "UPDATE  " + d.getTableData() + " as newd, "
                    + pData.getTableData() + " as p "
                    + " SET  newd.chrom = p.chrom , newd.chromStart = p.chromStart,  "
                    + " newd.chromEnd = p.chromEnd "
                    + " WHERE " + iSpot.getSQLtoPlattform("p", "newd");
            int i = s.executeUpdate(sql);
            if (i != d.getNof()) {

                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE,
                        " update failed: " + d.getNof() + " (nof) != " + i + " (updated)");
                return;

            } else {
                Logger.getLogger(DataManager.class.getName()).log(Level.INFO,
                        " update : " + d.getNof() + " (nof) = " + i + " (updated)");
            }
            d = em.merge(d);
            d.setModified(new Date());
            em.flush();
            et.commit();
            em.close();
        } catch (Exception e) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, e);
            return;
        }
    }
}
