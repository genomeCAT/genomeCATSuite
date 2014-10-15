package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name Map
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.AnnotationManager;
import org.molgen.genomeCATPro.annotation.AnnotationManagerImpl;
import org.molgen.genomeCATPro.annotation.CytoBand;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.annotation.GeneImpl;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionAnnotation;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.annotation.RegionImpl;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.cat.util.Defines;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.data.DataService;
import org.molgen.genomeCATPro.data.Spot;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.MapService;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 * 081013   kt  mapAtRegion bug group by ratio removed
 * 050613   kt  exception Spot clazz
 * 010313   kt  mapAtAnnotation use annotationfield
 * 130213   kt  mapAtAnnotation insteadof MapAtGene
 * 180912   kt  mapAtLocation use spatial index, Thread pro Chromome, temp table
                mapAtRegion use spatial index
 * 190912   kt  mapAtLocationBin new (old bsc version only center is binned,faster!!)
 */
public class Map {

    static Connection con = null;

    /**
     * read bed or bedplus chrom<tab>start<tab>end<tab>(name)
     * create list of regions
     * skip invalid file lines, report error number
     * 
     * @param pathname
     * @param error
     * @return
     * @throws java.lang.Exception
     */
    public static Integer importBED(List<RegionImpl> posList, String pathname) throws Exception {
        Integer error = 0;
        BufferedReader inBuffer = null;
        String is = "";
        try {
            inBuffer = new BufferedReader(new FileReader(pathname));

        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, "importBED", e);
            throw e;
        }

        try {
            String[] iss;
            int i = 0;
            RegionImpl r = null;
            while ((is = inBuffer.readLine()) != null) {
                iss = is.split("\t");
                if (iss.length < 3) {
                    Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                            "invalid format", is);
                    error++;
                    continue;
                }
                if (iss.length >= 4) {
                    r = new RegionImpl(iss[3], iss[0], Long.parseLong(iss[1]),
                            Long.parseLong(iss[2]));
                } else {
                    r = new RegionImpl("Region" + ++i, iss[0], Long.parseLong(iss[1]),
                            Long.parseLong(iss[2]));
                }
                posList.add(r);
            }
        } catch (Exception e) {
            error++;
            Logger.getLogger(Map.class.getName()).log(Level.WARNING, "importBED", e);
        }

        return error;

    }

    /**
     * validate if mapping is allowed for all data entities
     *  with each other -> same release
     *  
     * @param arrayList
     * @param release
     * @return
     */
    static List<Data> validateMapAtRegion(
            List<Data> arrayList,
            GenomeRelease release) {
        List<Data> d = new Vector<Data>();
        // instances all of experimentdata with given platform?
        for (Data currData : arrayList) {
            if (!(currData.getGenomeRelease().contentEquals(release.toString()))) {
                Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                        "mapAtRegion: not valid (wrong release) for " + currData.toFullString());
            //removeList.add(currData);

            } else {
                d.add(currData);
            }
        }
        return arrayList;
    }

    /**
     * 
     * @param release
     * @param annoTableName
     * @param arrayList
     * @param mapId
     * @return
     * @throws java.lang.Exception
     */
    public static List<MapData> mapAtAnnotation(
            GenomeRelease release,
            String annoTableName,
            final String annoFieldName,
            List<Data> arrayList,
            String mapId) throws Exception {



        List<MapData> mapList = new Vector<MapData>();


        final List<Data> _arrayList = Map.validateMapAtAnnotate(arrayList, release);


        try {
            //final long _size = size;
            final GenomeRelease _release = release;
            con = Database.getDBConnection(Defaults.localDB);



            Statement s = con.createStatement();




            final MapDetail dMapDetail = new MapDetail(
                    mapId,
                    Defines.MAP_ANNO,
                    release.toString(),
                    "table: " + annoTableName + " field: " + annoFieldName);
            dMapDetail.initTableData();

            s.execute("DROP TABLE IF EXISTS " + dMapDetail.getTableData());
            s.execute("CREATE TABLE " + dMapDetail.getTableData() + "(	" +
                    "name VARCHAR(255), " +
                    "chrom VARCHAR(255) NOT NULL, " +
                    "chromStart int(10) unsigned NOT NULL, " +
                    "chromEnd int(10) unsigned NOT NULL, " +
                    "PRIMARY KEY (chrom, chromStart, chromEnd) " +
                    ")");



            //int insert_count = 0;

            final List<String> _chroms = CytoBandManagerImpl.stGetChroms(_release);
            final AnnotationManager am = new AnnotationManagerImpl(release, annoTableName);

            Thread workers[][] = new Thread[_arrayList.size()][];

            final Integer results[][] = new Integer[_arrayList.size()][];

            final MapData dMap[] = new MapData[_arrayList.size()];
            int i = 0;
            final EntityManager em = DBService.getEntityManger();
            EntityTransaction userTransaction = em.getTransaction();
            userTransaction.begin();

            for (final Data currData : arrayList) {
                int id = arrayList.indexOf(currData);
                dMap[id] = new MapData(dMapDetail, currData, ++i);

                RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

                final String colName = currClazz.getRatioColName();

                results[id] = new Integer[_chroms.size()];
                Arrays.fill(results[id], 0);
                workers[id] = new Thread[_chroms.size()];

                // add spatial index if not already there
                DBUtils.addPositionAtTable(currData.getTableData());

                s.execute("ALTER TABLE " + dMapDetail.getTableData() +
                        " ADD COLUMN " + dMap[id].getDataName() + " DOUBLE DEFAULT 0");

                for (String chromId : _chroms) {
                    final String _chromId = chromId;
                    final int ii = _chroms.indexOf(chromId);
                    final String dataName = currData.getTableData();
                    Logger.getLogger(Map.class.getName()).log(Level.INFO,
                            "MapAtAnnotation: " + currData.getName() + " " + colName);
                    workers[id][ii] = new Thread(new Runnable() {

                        String insert = "";
                        int ichrom = RegionLib.fromChrToInt(_chromId);

                        public void run() {
                            int iid = _arrayList.indexOf(currData);
                            int insert_count = 0;
                            try {

                                final String tableName = Utils.getUniquableName(dMap[iid].getDataName());
                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        "MapAnnotation: Start processing " + dMap[iid].getDataName() + " " + _chromId + " tmpTable " + tableName);
                                Connection _con = Database.getDBConnection(Defaults.localDB);
                                Statement _s = _con.createStatement();
                                _s.execute("DROP TABLE IF EXISTS " + (tableName + "_" + _chromId));
                                _s.execute(
                                        "CREATE TABLE " + (tableName + "_" + _chromId) +
                                        "( " +
                                        "name VARCHAR(255) NOT NULL," +
                                        "chrom VARCHAR(255) NOT NULL, " +
                                        "chromStart int(10) unsigned, " +
                                        "chromEnd int(10) unsigned, " +
                                        dMap[iid].getDataName() + " DOUBLE DEFAULT 0 )");
                                List<? extends RegionAnnotation> list = am.getData(_chromId);
                                for (RegionAnnotation r : list) {
                                    Object o = DataService.getValue(r, annoFieldName);
                                    if (o != null) {
                                        o = o.toString().replaceAll("\'", "");
                                    }
                                    insert = "INSERT INTO " + tableName + "_" + _chromId +
                                            " (" + dMap[iid].getDataName() +
                                            ", name,  chrom, chromStart, chromEnd ) " +
                                            " (SELECT  avg(" + colName + " ) as avgratio, " +
                                            "\'" + (o != null ? o.toString() : "") + "\'," +
                                            "\'" + r.getChrom() + "\'," +
                                            r.getChromStart() + " , " + r.getChromEnd() +
                                            //" , avg(" + dcolName + ") as ratio " +
                                            " FROM " + dataName +
                                            " FORCE INDEX (gc_position) " + // kt 250912
                                            " WHERE " +
                                            " MBRIntersects( gc_position," +
                                            " LineString(" +
                                            " Point(" + ichrom + "," + r.getChromEnd() + "), " +
                                            " Point(" + ichrom + ", " + r.getChromStart() + "))" +
                                            ") " +
                                            //" group by chrom, " + colName +
                                            //" HAVING " +
                                            /*
                                            " chrom = \'" + r.getChrom() + "\' " +
                                            " AND (  chromStart < " + r.getChromEnd() +
                                            " AND  chromEnd  >  " + r.getChromStart() + " ) " + // segment border region
                                             */
                                            ") ";
                                    //"ON DUPLICATE KEY UPDATE " + dMap[iid].getDataName() + "  =   values(" + dMap[iid].getDataName() + ")";
                                    //System.out.println(insert);
                                    // Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                    //        "Insert: " + insert);
                                    _s.addBatch(insert);

                                    insert_count++;

                                    if (insert_count > 1000) {

                                        //_s.execute("ALTER TABLE " + (tableName + "_" + _chromId + "_" + i) + " DROP PRIMARY KEY");
                                        _s.executeBatch();
                                        _s.clearBatch();
                                        results[iid][ii] += insert_count;
                                        /* Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        "mapAtLocation: " + dMap[id].getDataName() + " " + _chromId + " inserted:  " + insert_count);
                                         */
                                        Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                                "mapAtAnnotation tmpTable " + (tableName + "_" + _chromId) +
                                                " with " + insert_count + " items");

                                        insert_count = 0;
                                    }

                                } //end chrom pos

                                if (insert_count > 0) {


                                    _s.executeBatch();
                                    _s.clearBatch();
                                    Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                            "mapAtAnnotation tmpTable " + (tableName + "_" + _chromId) +
                                            " with " + insert_count + " items");
                                    results[iid][ii] += insert_count;


                                }
                                _s.execute("INSERT INTO " + dMapDetail.getTableData() +
                                        " (name,  chrom, chromStart, chromEnd , " + dMap[iid].getDataName() + " ) " +
                                        " ( SELECT * FROM " + ((tableName + "_" + _chromId)) + ")" +
                                        " ON DUPLICATE KEY UPDATE " + dMap[iid].getDataName() +
                                        "  =   values(" + dMap[iid].getDataName() + ")");
                                _s.execute("DROP TABLE IF EXISTS " + (tableName + "_" + _chromId));
                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        "mapAtAnnotation temp table " + (tableName + "_" + _chromId) + " " +
                                        insert_count + " copied into " + dMapDetail.getTableData());
                            } catch (Exception e) {
                                /*Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                insert);*/
                                Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                        "", e);

                                results[iid][ii] = -1;
                            }
                        } // end run

                        ;
                    }); // end thread

                    workers[id][ii].start();
                }//end one chrom

            }//end data list

            for (int id = 0; id < arrayList.size(); id++) {
                for (int j = 0; j < workers.length; j++) {
                    workers[id][j].join(0);
                }
            }
            for (int id = 0; id < arrayList.size(); id++) {
                int insert_count = 0;
                for (int j = 0; j < results[id].length; j++) {
                    if (results[id][j] == -1) {
                        throw new Exception("Error");
                    } else {
                        insert_count += results[id][j];
                    }
                }
                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                        "mapAtAnnotation: " + dMap[id].getDataName() + " inserted  " + insert_count);
                MapService.perstistData(dMap[id], em);
                Map.updateStats(dMap[id]);
                em.flush();
                mapList.add(dMap[id]);
            }

            userTransaction.commit();

            return mapList;
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, " mapAtAnnotation", e);
            throw e;
        }
    }

    /**
     * map at region of interest
     * 
     * @param release       genome release
     * @param positionTable table containing bed positions
     * @param arrayList     data tables to map
     * @param mapId         name for map table
     * @return
     */
    public static List<MapData> mapAtRegion(
            GenomeRelease release,
            List<? extends Region> positionTable, String filename, List<Data> arrayList,
            String mapId) throws Exception {


        List<MapData> mapList = new Vector<MapData>();
        String colName = null;

        arrayList = Map.validateMapAtRegion(arrayList, release);
        // check, if all ratios based on segments

        // position table

        /*
        
        // Defines.COL_RATIO_CBS, Defines.COL_RATIO_LOC, Defines.COL_RATIO_GEN
        if ((colName.compareTo(Defines.COL_RATIO_CBS) != 0) && (colName.compareTo(Defines.COL_RATIO_LOC) != 0) && (colName.compareTo(Defines.COL_RATIO_GEN) != 0)) {
        throw new RuntimeException("mapAtRegion: ratio must be calculated by segmentation method!");
        }
        
         */

        EntityTransaction userTransaction = null;
        int ichrom;

        try {

            con = Database.getDBConnection(Defaults.localDB);


            Statement s = con.createStatement();

            // key - chrom, values Vector with segment borders	

            // segment borders (start, stop)
            MapDetail dMapDetail = new MapDetail(
                    mapId,
                    Defines.MAP_REGION,
                    release.toString(),
                    "mapAtRegion file: " + filename +
                    ", release:" + release);
            dMapDetail.initTableData();
            //MapService.perstistDetail(dMapDetail);

            s.execute("DROP TABLE IF EXISTS " + dMapDetail.getTableData());
            s.execute("CREATE TABLE " + dMapDetail.getTableData() + "(	" +
                    "name VARCHAR(255), " +
                    "chrom VARCHAR(255) NOT NULL, " +
                    "chromStart int(10) unsigned NOT NULL, " +
                    "chromEnd int(10) unsigned NOT NULL, " +
                    "PRIMARY KEY (chrom, chromStart, chromEnd) " +
                    ")");


            EntityManager em = DBService.getEntityManger();
            userTransaction = em.getTransaction();
            userTransaction.begin();
            int insert_count = 0;
            MapData dMap = null;
            int i = 0;
            for (Data currData : arrayList) {// each array

                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                        "MapAtRegion: " + currData.getName() + " " + colName);

                dMap = new MapData(dMapDetail, currData, ++i);
                DBUtils.addPositionAtTable(currData.getTableData());
                s.execute("ALTER TABLE " + dMapDetail.getTableData() +
                        " ADD COLUMN " + dMap.getDataName() + " DOUBLE");

                RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

                colName = currClazz.getRatioColName();

                String insert = "";



                for (Region r : positionTable) {

                    /*if (r.getChromStart() == r.getChromEnd()) {
                    continue;
                    }*/
                    ichrom = RegionLib.fromChrToInt(r.getChrom());
                    insert =
                            "INSERT INTO " + dMapDetail.getTableData() +
                            " (" + dMap.getDataName() +
                            ", name,  chrom, chromStart, chromEnd ) " +
                            " (SELECT  avg(" + colName + " ) as avgratio, " +
                            "\'" + r.getName() + "\'," +
                            "\'" + r.getChrom() + "\'," +
                            r.getChromStart() + " , " + r.getChromEnd() +
                            //" , avg(" + dcolName + ") as ratio " +
                            " FROM " + currData.getTableData() +
                            " WHERE " +
                            " MBRIntersects( gc_position," +
                            " LineString(" +
                            " Point(" + ichrom + "," + r.getChromEnd() + "), " +
                            " Point(" + ichrom + ", " + r.getChromStart() + "))" +
                            ") " +
                            //081013    kt  bug group by ratio removed
                            " group by chrom " +
                            //" HAVING " +
                            /*
                            " chrom = \'" + r.getChrom() + "\' " +
                            " AND (  chromStart < " + r.getChromEnd() +
                            " AND  chromEnd  >  " + r.getChromStart() + " ) " + // segment border region
                             */
                            ") " +
                            "ON DUPLICATE KEY UPDATE " + dMap.getDataName() + "  =   values(" + dMap.getDataName() + ")";
                    //System.out.println(insert);
                    // Logger.getLogger(Map.class.getName()).log(Level.INFO,
                    //        "Insert: " + insert);
                    s.addBatch(insert);

                    insert_count++;

                    if (insert_count > 10) {
                        s.executeBatch();
                        s.clearBatch();
                        insert_count = 0;
                    }

                } //end positionTable

                s.executeBatch();
                s.clearBatch();
                insert_count = 0;
                MapService.perstistData(dMap, em);
                Map.updateStats(dMap);
                em.flush();
                mapList.add(dMap);

            }
            userTransaction.commit();
            return mapList;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                        "mapAtRegion rollback:", ex);
            }
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                    "mapAtRegion:", e);
            throw e;
        }
    }

    /**
     * @deprecated 
     * get segment borders from data table containing genomic regions
     * connects nested and overlapping segments with same ratios
     * @param release           genome release
     * @param positionTable     defined genomic regions, listed for chroms
     * @param arrayId           data table to map
     * @param colName           colname for ratio column for current data table
     * @param type              (oligo, bac, etc see Defaults.Type)         
     * @return
     */
    @Deprecated
    static HashMap<String, List<Long>> getSegmentBorders(
            GenomeRelease release,
            HashMap<String, List<Long>> positionTable,
            String arrayId,
            String colName,
            String type) throws Exception {

        List<String> chroms = CytoBandManagerImpl.stGetChroms(release);

        Double ratio = 0.0;
        Long lastEnd, lastStart;

        try {



            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            //Vector oldPosition;
            List<Long> vPosition;
            for (String chrom : chroms) {

                vPosition = positionTable.get(chrom);
                if (vPosition == null) {
                    vPosition = new Vector<Long>();
                    positionTable.put(chrom, vPosition);
                }
                //vPosition = new Vector();

                String sql = "SELECT  distinct chrom, " + colName + " , chromStart as start,  " +
                        " chromEnd as end from  " + arrayId +
                        " where chrom = \'" + chrom + "\' " +
                        " order by start ";

                Logger.getLogger(Map.class.getName()).log(Level.INFO, sql);
                ResultSet r = s.executeQuery(sql);
                lastEnd = new Long(0);
                lastStart = new Long(0);
                while (r.next()) {
                    if (lastStart.compareTo(new Long(0)) == 0) {
                        ratio = r.getDouble(2);
                        lastStart = r.getLong(3);
                        lastEnd = r.getLong(4);
                        continue;
                    }
                    // overlapping or nested segment 

                    if (Double.compare(r.getDouble(2), ratio) == 0) {
                        if (type.contentEquals(Defaults.Type.Oligo.toString())) {
                            // short oligo
                            lastEnd = r.getLong(4);
                            continue;
                        }
                        // real overlapping with last segment?
                        if (r.getInt(3) <= lastEnd) {
                            lastEnd = Math.max(lastEnd, r.getInt(4));
                            continue;
                        }
                        // nested with same start
                        if (lastStart.compareTo(r.getLong(3)) == 0) {
                            lastEnd = Math.max(lastEnd, r.getLong(4));
                            continue;
                        }

                    }

                    vPosition.add(lastStart);
                    vPosition.add(lastEnd);
                    ratio = r.getDouble(2);
                    lastStart = r.getLong(3);
                    lastEnd = r.getLong(4);


                /*if(r.getInt(3) != lastPos)
                vPosition.add(lastPos);*/
                }
                r.close();
                vPosition.add(lastStart);
                vPosition.add(lastEnd);

            /*oldPosition = (Vector) positionTable.get(chrom);
            if (oldPosition == null) {
            positionTable.put(chrom, (Vector) vPosition.clone());
            }
            else {
            oldPosition.addAll((Vector) vPosition.clone());
            }
             */
            }// end chrom

            return positionTable;
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                    "getSegmentBorders:", e);
            throw e;
        }
    }

    /**
     * validate if mapping is allowed for all data entities
     *      check if all data have same release
     * data entities not suitable will be removed from the list
     * @param arrayList
     * @return valid data list
     */
    static List<Data> validateMapAtAnnotate(List<Data> arrayList, GenomeRelease release) {
        List<Data> d = new Vector<Data>();
        for (Data currData : arrayList) {
            if (!(currData.getGenomeRelease().contentEquals(release.toString()))) {
                Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                        "mapAtLocation: not valid (wrong release) for " + currData.toFullString());
            // removeList.add(currData);

            } else {
                d.add(currData);
            }
        }
        return d;
    }

    /**
     * validate if mapping is allowed for all data entities
     *      clazz must implement interface RegionArray
     *      getGeneColName not null
     * data entities not suitable will be removed from the list
     * @param arrayList
     * @return valid data list
     */
    @Deprecated
    static List<Data> validateMapAtGene(List<Data> arrayList) {


        List<Data> d = new Vector<Data>();
        // instances all of experimentdata with given platform?
        for (Data currData : arrayList) {
            RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

            if (currClazz.getGeneColName() == null) {
                Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                        "mapAtGene: not valid for " + currData.toFullString());

            //removeList.add(currData);
            } else {
                d.add(currData);
            }
        }

        return d;
    }

    /**
     * base for mapping is Gene, that means new table (mapId) is created, 
     * reference id is Gene, for each array extra column
     */
    
    @Deprecated
    public static List<MapData> mapAtGene(
            GenomeRelease release,
            List<Data> arrayList,
            String mapId) throws Exception {

        List<MapData> mapList = new Vector<MapData>();

        // all array with same release?
        try {


            arrayList = Map.validateMapAtGene(arrayList);
            if (arrayList.size() <= 0) {
                Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                        "mapAtGene: nothing to map");
                return null;
            }
            String geneTable = "";
            geneTable = DBUtils.getAnnoTableForRelease(
                    GeneImpl.nameId,
                    release);
            MapDetail dMapDetail = new MapDetail(
                    mapId,
                    Defines.MAP_GENE,
                    release.toString(),
                    geneTable);
            dMapDetail.initTableData();
            //MapService.perstistDetail(dMapDetail);
            con = Database.getDBConnection(Defaults.localDB);


            Statement s = con.createStatement();


            s.execute("DROP TABLE IF EXISTS " + dMapDetail.getTableData());
            s.execute("CREATE TABLE " + dMapDetail.getTableData() + "(	" +
                    " name VARCHAR(255), " +
                    " chrom VARCHAR(255) NOT NULL, " +
                    " chromStart int(10) unsigned NOT NULL, " +
                    " chromEnd int(10) unsigned NOT NULL, " +
                    " PRIMARY KEY (name) )");

            String query = "";
            //String view_arrayId = "";
            String colName = "";
            String geneColName = "";

            MapData dMap = null;
            EntityManager em = DBService.getEntityManger();
            EntityTransaction userTransaction = em.getTransaction();
            userTransaction.begin();
            int i = 0;
            for (Data currData : arrayList) {
                dMap = new MapData(dMapDetail, currData, ++i);

                //dMap.setDescription(geneTable);

                RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

                colName = currClazz.getRatioColName();
                geneColName = currClazz.getGeneColName();


                // add to map table as new column	  
                s.execute("ALTER TABLE " + dMapDetail.getTableData() +
                        " ADD COLUMN " + dMap.getDataName() + " DOUBLE");
                /* query = ("INSERT INTO " + dMapDetail.getTableData() +
                " (name, " + dMap.getDataName() + ", chrom, chromStart, chromEnd )" +
                " (SELECT DISTINCT geneName, " + colName + ", chrom, chromStart, chromEnd FROM " + view_arrayId + ") " +
                " ON DUPLICATE KEY UPDATE " + dMap.getDataName() + "  = " + colName);*/
                query = ("INSERT INTO " + dMapDetail.getTableData() +
                        " (name,  chrom, chromStart, chromEnd, " + dMap.getDataName() + ")" +
                        " (SELECT " + geneColName + " as geneName, " +
                        " gene.chrom, gene.txStart as chromStart, gene.txEnd as chromEnd,  " +
                        "AVG(a." + colName + ") AS " + colName +
                        " FROM " + currData.getTableData() + " AS a, " +
                        geneTable + " as gene " +
                        " WHERE a." + geneColName + " = gene.name2 " +
                        " and a.chrom  = gene.chrom " +
                        " GROUP BY a." + geneColName + " )" +
                        " ON DUPLICATE KEY UPDATE " + dMap.getDataName() + "= values(" + dMap.getDataName() + ")");
                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                        "mapAtGene:" + query);
                s.execute(query);


                MapService.perstistData(dMap, em);
                Map.updateStats(dMap);
                em.flush();
                mapList.add(dMap);
            }
            userTransaction.commit();
            return mapList;
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                    "mapAtGene:", e);
            throw e;
        }
    }

    /**
     * validate if mapping is allowed for all data entities
     *      must be instance of ExperimentData
     *      must be linked to same PlatformDetail
     * data entities not suitable will be removed from the list
     * @param arrayList
     * @param platform
     * @return valid data list
     */
    static List<Data> validateMapAtId(
            List<Data> arrayList,
            PlatformDetail platform) throws Exception {
        List<Data> d = new Vector<Data>();
        // instances all of experimentdata with given platform?
        for (Data currData : arrayList) {
            if (!(currData instanceof ExperimentData)) {
                Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                        "mapAtId: not valid (wrong data type) for " + currData.toFullString());
                //removeList.add(currData);
                continue;
            } else {
                if (((ExperimentData) currData).getPlatformdata() == null) {
                    PlatformService.getPlatformDataByExperimentData((ExperimentData) currData);
                }
                if (!((ExperimentData) currData).getPlatformdata().getPlattform().equals(platform)) {
                    Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                            "mapAtId: not valid (wrong platform) for " + currData.toFullString() +
                            " platform " + ((ExperimentData) currData).getPlatformdata().getPlattform());
                    //removeList.add(currData);
                    continue;
                }
            }
            d.add(currData);
        }

        return d;
    }

    /**
     * base for mapping is the probeName, that means new table (mapId) is created, 
     * reference id is probeName (Oligo or bac), for each array extra column 
     * [AG0009, ratio, Methylation by MCA, Agilent, Oligo, false, SCLC_A_XMA, SCLC_A_XMA]
     */
    public static List<MapData> mapAtId(
            GenomeRelease release,
            List<Data> arrayList,
            PlatformDetail platform,
            String mapId) throws Exception {



        arrayList = Map.validateMapAtId(arrayList, platform);

        if (arrayList.size() <= 0) {
            Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                    "mapAtId: nothing to map");
            return null;
        }
        List<MapData> mapList = new Vector<MapData>();
        try {
            MapDetail dMapDetail = new MapDetail(
                    mapId,
                    Defines.MAP_ID,
                    release.toString(),
                    platform.toFullString());
            dMapDetail.initTableData();

            PlatformData pdata = PlatformService.getPlatformData(platform.getName(), release.toString());
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();


            s.execute("DROP TABLE IF EXISTS " + dMapDetail.getTableData());
            //s.execute("CREATE TABLE " + mapId + "(	probeName VARCHAR(255), PRIMARY KEY (probeName) )");
            s.execute("CREATE TABLE " + dMapDetail.getTableData() + "(	" +
                    " name VARCHAR(255), " +
                    " chrom VARCHAR(255) NOT NULL, " +
                    " chromStart int(10) unsigned NOT NULL, " +
                    " chromEnd int(10) unsigned NOT NULL, " +
                    " PRIMARY KEY (name) )");
            String colName = "";
            //String view_arrayId;
            MapData dMap = null;
            EntityManager em = DBService.getEntityManger();
            EntityTransaction userTransaction = em.getTransaction();
            userTransaction.begin();
            int i = 0;
            for (Data currData : arrayList) {
                dMap = new MapData(dMapDetail, currData, ++i);

                s.execute("ALTER TABLE " + dMapDetail.getTableData() +
                        " ADD COLUMN " + dMap.getDataName() + " DOUBLE");


                RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

                colName = currClazz.getRatioColName();
                //view_arrayId = "vid_" + currData.getTableData();
                Spot currSpotClazz = null;
                try {
                    //050613    kt
                    currSpotClazz = DataManager.getSpotClazz(currData.getClazz());
                } catch (Exception e) {
                    Logger.getLogger(DataManager.class.getName()).log(Level.WARNING, null, e);
                }

                String sql = currSpotClazz.getSQLtoPlattform(
                        "p", "d");
                // create view, average ratios over same oligos
                /*s.execute("CREATE OR REPLACE ALGORITHM = TEMPTABLE VIEW " + view_arrayId + " AS " +
                "SELECT  probeName, chrom, chromStart, chromEnd, " +
                " AVG(" + colName + ") AS " + colName +
                " FROM " + currData.getTableData() + " as d" +
                ", " + pdata.getTableData() + " as p " + 
                " WHERE " + sql +
                " GROUP BY probeName, chrom");
                 */
                // add to map table as new column	  

                /* s.execute("INSERT INTO " + dMapDetail.getTableData() +
                " (name, chrom, chromStart, chromEnd, " + dMap.getDataName() + " )" +
                " (SELECT DISTINCT probeName, chrom, chromStart, chromEnd, " +
                colName + " FROM " + view_arrayId + ") " +
                " ON DUPLICATE KEY UPDATE " + dMap.getDataName() + "  = " + colName);
                 */
                String query = new String("INSERT INTO " + dMapDetail.getTableData() +
                        " (name, chrom, chromStart, chromEnd, " + dMap.getDataName() + " )" +
                        " (SELECT  p.probeName, p.chrom, p.chromStart, p.chromEnd, " +
                        " AVG(d." + colName + ") AS " + colName +
                        " FROM " + currData.getTableData() + " as d " +
                        ", " + pdata.getTableData() + " as p " +
                        " WHERE " + sql +
                        " GROUP BY probeName, chrom) " +
                        " ON DUPLICATE KEY UPDATE " + dMap.getDataName() + "= values(" + dMap.getDataName() + ")");
                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                        "mapAtID:" + query);
                s.execute(query);
                MapService.perstistData(dMap, em);
                Map.updateStats(dMap);
                em.flush();
                mapList.add(dMap);
            }
            userTransaction.commit();
            return mapList;
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, " mapAtId", e);
            throw e;
        }
    }

    /**
     * validate if mapping is allowed for all data entities
     *      must be same release
     *      
     * data entities not suitable will be removed from the list
     * @param arrayList
     * @param platform
     * @return valid data list
     */
    static List<Data> validateMapAtLocation(
            List<Data> arrayList,
            GenomeRelease release) {
        List<Data> d = new Vector<Data>();
        // instances all of experimentdata with given platform?
        for (Data currData : arrayList) {
            if (!(currData.getGenomeRelease().contentEquals(release.toString()))) {
                Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                        "mapAtLocation: not valid (wrong release) for " + currData.toFullString());
            // removeList.add(currData);

            } else {
                d.add(currData);
            }
        }
        return d;
    }

    /**
     * binnig
     * new table  is created, 
     * reference id  chrom + location, for each array extra column
     * 
     *  summarize features whose centre lies within a certain bin (intervall region)
     *  for resolution less than 10k very fast, but less accurate for features with greater than resolution
     */
    static List<MapData> mapAtLocationBin(final List<Data> arrayList, int size, GenomeRelease release, String mapId) throws Exception {
        final List<Data> _arrayList = Map.validateMapAtLocation(arrayList, release);
        if (_arrayList.size() <= 0) {
            Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                    "mapAtLocation: nothing to map");
            return null;
        }

        final List<MapData> mapList = new Vector<MapData>();

        try {
            final long _size = size;
            final GenomeRelease _release = release;
            con = Database.getDBConnection(Defaults.localDB);
            Statement s = con.createStatement();


            // create entry mapList
            final MapDetail dMapDetail = new MapDetail(
                    mapId,
                    Defines.MAP_LOCATION,
                    release.toString(),
                    "mapAtLocation size: " + size +
                    ", release:" + release);
            dMapDetail.initTableData();


            //MapService.perstistDetail(dMapDetail);


            s.execute("DROP TABLE IF EXISTS " + dMapDetail.getTableData());
            s.execute("CREATE TABLE " + dMapDetail.getTableData() + "( " +
                    "name VARCHAR(255) NOT NULL," +
                    "chrom VARCHAR(255) NOT NULL, " +
                    "chromStart int(10) unsigned, " +
                    "chromEnd int(10) unsigned, " +
                    " PRIMARY KEY (chrom, chromStart, chromEnd) " +
                    " )");

            final List<String> _chroms = CytoBandManagerImpl.stGetChroms(release);
            Thread workers[][] = new Thread[_arrayList.size()][];

            final Integer results[][] = new Integer[_arrayList.size()][];
            final MapData dMap[] = new MapData[_arrayList.size()];
            //final MapData dMap = null;
            final EntityManager em = DBService.getEntityManger();
            EntityTransaction userTransaction = em.getTransaction();
            userTransaction.begin();

            int i = 0;
            for (final Data currData : arrayList) {
                int id = arrayList.indexOf(currData);
                dMap[id] = new MapData(dMapDetail, currData, ++i);

                RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

                final String colName = currClazz.getRatioColName();

                results[id] = new Integer[_chroms.size()];
                Arrays.fill(results[id], 0);
                workers[id] = new Thread[_chroms.size()];

                // add spatial index if not already there
                //DBUtils.addPositionAtTable(currData.getTableData());


                s.execute("ALTER TABLE " + dMapDetail.getTableData() +
                        " ADD COLUMN " + dMap[id].getDataName() + " DOUBLE DEFAULT 0");
                for (String chromId : _chroms) {

                    final String _chromId = chromId;
                    final int ii = _chroms.indexOf(chromId);


                    final String dataName = currData.getTableData();


                    //workers[id][ii] = new Thread(new Runnable() {
                    workers[id][ii] = new Thread(new Runnable() {

                        String insert = "";
                        int ichrom = RegionLib.fromChrToInt(_chromId);

                        public void run() {
                            int iid = arrayList.indexOf(currData);

                            try {
                                final String tableName = Utils.getUniquableName(dMap[iid].getDataName());

                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        " Start processing " + dMap[iid].getDataName() + " " + _chromId);

                                Connection _con = Database.getDBConnection(Defaults.localDB);
                                Statement _s = _con.createStatement();
                                _s.execute("DROP VIEW IF EXISTS " + (tableName + "_" + _chromId));

                                _s.execute("CREATE OR REPLACE ALGORITHM = TEMPTABLE VIEW " +
                                        tableName + "_" + _chromId + " AS " +
                                        "SELECT  chrom, " +
                                        "floor(((chromStart+chromEnd)/2)/" + _size + ") as loc, " +
                                        "AVG(" + colName + ") AS  " + dMap[iid].getDataName() +
                                        " FROM " + dataName + " GROUP BY chrom, loc");


                                _s.execute(
                                        "INSERT INTO " + dMapDetail.getTableData() +
                                        " (name, chrom, chromStart, chromEnd, " + dMap[iid].getDataName() + " )" +
                                        " (SELECT loc, chrom,  (loc*" + _size + "), ((loc*" + _size + ")+" + (_size - 1) + "), " +
                                        dMap[iid].getDataName() + " FROM " + ((tableName + "_" + _chromId)) + ") " +
                                        " ON DUPLICATE KEY UPDATE " + dMap[iid].getDataName() +
                                        "  =   values(" + dMap[iid].getDataName() + ")");

                                _s.execute("DROP TABLE IF EXISTS " + (tableName + "_" + _chromId));
                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        "temp table " + (tableName + "_" + _chromId) + " " +
                                        " copied into " + dMapDetail.getTableData());

                            } catch (Exception e) {
                                /*Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                insert);*/
                                Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                        "", e);

                                results[iid][ii] = -1;
                            }


                        } // end run

                        ;
                    }); // end thread

                    workers[id][ii].start();
                }//end one chrom



            }//end data list

            for (int id = 0; id < arrayList.size(); id++) {
                for (int j = 0; j < workers.length; j++) {
                    workers[id][j].join(0);
                }
            }
            for (int id = 0; id < arrayList.size(); id++) {
                int insert_count = 0;
                for (int j = 0; j < results[id].length; j++) {
                    if (results[id][j] == -1) {
                        throw new Exception("Error");
                    } else {
                        insert_count += results[id][j];
                    }
                }
                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                        "mapAtLocationBin: " + dMap[id].getDataName() + " inserted  " + insert_count);
                MapService.perstistData(dMap[id], em);
                Map.updateStats(dMap[id]);
                em.flush();
                mapList.add(dMap[id]);
            }

            userTransaction.commit();

            return mapList;
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, " mapAtLocation", e);
            throw e;
        }
    }

    /**
     *
     * new table  is created, 
     * reference id  chrom + location, for each array extra column
     * 
     * summarize features overlapping bin (intervall region)
     * for resolution less than 10k very time consuming
     */
    static List<MapData> mapAtLocation(final List<Data> arrayList, int size, GenomeRelease release, String mapId) throws Exception {
        final List<Data> _arrayList = Map.validateMapAtLocation(arrayList, release);
        if (_arrayList.size() <= 0) {
            Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                    "mapAtLocation: nothing to map");
            return null;
        }

        final List<MapData> mapList = new Vector<MapData>();

        try {
            final long _size = size;
            final GenomeRelease _release = release;
            con = Database.getDBConnection(Defaults.localDB);
            Statement s = con.createStatement();


            // create entry mapList
            final MapDetail dMapDetail = new MapDetail(
                    mapId,
                    Defines.MAP_LOCATION,
                    release.toString(),
                    "mapAtLocation size: " + size +
                    ", release:" + release);
            dMapDetail.initTableData();


            //MapService.perstistDetail(dMapDetail);


            s.execute("DROP TABLE IF EXISTS " + dMapDetail.getTableData());
            s.execute("CREATE TABLE " + dMapDetail.getTableData() + "( " +
                    "name VARCHAR(255) NOT NULL," +
                    "chrom VARCHAR(255) NOT NULL, " +
                    "chromStart int(10) unsigned, " +
                    "chromEnd int(10) unsigned, " +
                    " PRIMARY KEY (chrom, chromStart, chromEnd) " +
                    " )");

            final List<String> _chroms = CytoBandManagerImpl.stGetChroms(release);
            Thread workers[][] = new Thread[_arrayList.size()][];

            final Integer results[][] = new Integer[_arrayList.size()][];
            final MapData dMap[] = new MapData[_arrayList.size()];
            //final MapData dMap = null;
            final EntityManager em = DBService.getEntityManger();
            EntityTransaction userTransaction = em.getTransaction();
            userTransaction.begin();

            int i = 0;
            for (final Data currData : arrayList) {
                int id = arrayList.indexOf(currData);
                dMap[id] = new MapData(dMapDetail, currData, ++i);

                RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

                final String colName = currClazz.getRatioColName();

                results[id] = new Integer[_chroms.size()];
                Arrays.fill(results[id], 0);
                workers[id] = new Thread[_chroms.size()];

                // add spatial index if not already there
                DBUtils.addPositionAtTable(currData.getTableData());


                s.execute("ALTER TABLE " + dMapDetail.getTableData() +
                        " ADD COLUMN " + dMap[id].getDataName() + " DOUBLE DEFAULT 0");
                for (String chromId : _chroms) {

                    final String _chromId = chromId;
                    final int ii = _chroms.indexOf(chromId);
                    final long _chr_start = ((CytoBand) CytoBandManagerImpl.getFirst(_release, chromId)).getChromStart();
                    final long _chr_end = ((CytoBand) CytoBandManagerImpl.getLast(_release, chromId)).getChromEnd();


                    final String dataName = currData.getTableData();


                    //workers[id][ii] = new Thread(new Runnable() {
                    workers[id][ii] = new Thread(new Runnable() {

                        String insert = "";
                        int ichrom = RegionLib.fromChrToInt(_chromId);

                        public void run() {
                            int iid = arrayList.indexOf(currData);
                            int insert_count = 0;
                            try {
                                final String tableName = Utils.getUniquableName(dMap[iid].getDataName());

                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        " Start processing " + dMap[iid].getDataName() + " " + _chromId);

                                Connection _con = Database.getDBConnection(Defaults.localDB);
                                Statement _s = _con.createStatement();
                                _s.execute("DROP TABLE IF EXISTS " + (tableName + "_" + _chromId));
                                _s.execute(
                                        "CREATE TABLE " + (tableName + "_" + _chromId) +
                                        "( " +
                                        "name VARCHAR(255) NOT NULL," +
                                        "chrom VARCHAR(255) NOT NULL, " +
                                        "chromStart int(10) unsigned, " +
                                        "chromEnd int(10) unsigned, " +
                                        dMap[iid].getDataName() + " DOUBLE DEFAULT 0 )");
                                for (long pos = _chr_start; pos < _chr_end; pos += _size) {
                                    //all position

                                    insert = "INSERT INTO " + (tableName + "_" + _chromId) +
                                            " (" + dMap[iid].getDataName() + ", name,  chrom, chromStart, chromEnd ) " +
                                            " ( " +
                                            " SELECT  avg(" + colName + " ) as aratio , " +
                                            "\'" + (_chromId + ":" + pos + "-" + (pos + _size)) + "\' , " +
                                            "\'" + _chromId + "\'," + pos + " , " + ((pos + _size) < _chr_end ? (pos + _size) : _chr_end) +
                                            //" , avg(" + dcolName + ") as ratio " +
                                            " FROM " + dataName +
                                            " FORCE INDEX (gc_position) " + // kt 250912
                                            " WHERE " +
                                            " MBRIntersects( gc_position," +
                                            " LineString(" +
                                            " Point(" + ichrom + "," + pos + "), " +
                                            " Point(" + ichrom + ", " + ((pos + _size) < _chr_end ? (pos + _size) : _chr_end) + "))" +
                                            " ) " +
                                            /*"chrom = \'" + chromId + "\' " +
                                            //" group by chrom, " + colName +*/
                                            " HAVING " + "avg(" + colName + " ) is not null" +
                                            ")";
                                    _s.addBatch(insert);
                                    insert_count++;

                                    if (insert_count > ((250000000) / _size / 50)) {
                                        Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                                insert);
                                        //_s.execute("ALTER TABLE " + (tableName + "_" + _chromId + "_" + i) + " DROP PRIMARY KEY");
                                        _s.executeBatch();
                                        _s.clearBatch();
                                        results[iid][ii] += insert_count;
                                        /* Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        "mapAtLocation: " + dMap[id].getDataName() + " " + _chromId + " inserted:  " + insert_count);
                                         */
                                        Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                                "mapAtLocation tmpTable " + (tableName + "_" + _chromId) +
                                                " with " + insert_count + " items");

                                        insert_count = 0;
                                    }
                                } // end all pos

                                if (insert_count > 0) {


                                    _s.executeBatch();
                                    _s.clearBatch();
                                    Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                            "mapAtLocation tmpTable " + (tableName + "_" + _chromId) +
                                            " with " + insert_count + " items");
                                    results[iid][ii] += insert_count;


                                }

                                //s.execute("LOAD DATA INFILE \'" + filePath + "_" + _chromId + "_" + _i + " \' INTO TABLE " +   tableName);
                                _s.execute("INSERT INTO " + dMapDetail.getTableData() +
                                        " (name,  chrom, chromStart, chromEnd , " + dMap[iid].getDataName() + " ) " +
                                        " ( SELECT * FROM " + ((tableName + "_" + _chromId)) + ")" +
                                        " ON DUPLICATE KEY UPDATE " + dMap[iid].getDataName() +
                                        "  =   values(" + dMap[iid].getDataName() + ")");
                                _s.execute("DROP TABLE IF EXISTS " + (tableName + "_" + _chromId));
                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        "temp table " + (tableName + "_" + _chromId) + " " +
                                        insert_count + " copied into " + dMapDetail.getTableData());

                            } catch (Exception e) {
                                /*Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                insert);*/
                                Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                        "", e);

                                results[iid][ii] = -1;
                            }


                        } // end run

                        ;
                    }); // end thread

                    workers[id][ii].start();
                }//end one chrom



            }//end data list

            for (int id = 0; id < arrayList.size(); id++) {
                for (int j = 0; j < workers.length; j++) {
                    workers[id][j].join(0);
                }
            }
            for (int id = 0; id < arrayList.size(); id++) {
                int insert_count = 0;
                for (int j = 0; j < results[id].length; j++) {
                    if (results[id][j] == -1) {
                        throw new Exception("Error");
                    } else {
                        insert_count += results[id][j];
                    }
                }
                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                        "mapAtLocation: " + dMap[id].getDataName() + " inserted  " + insert_count);
                MapService.perstistData(dMap[id], em);
                Map.updateStats(dMap[id]);
                em.flush();
                mapList.add(dMap[id]);
            }

            userTransaction.commit();

            return mapList;
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, " mapAtLocation", e);
            throw e;
        }
    }

    /**
     * @deprecated 
     **/
    @Deprecated
    static List<MapData> mapAtLocationOld(final List<Data> arrayList, int size, GenomeRelease release, String mapId) throws Exception {
        final List<Data> _arrayList = Map.validateMapAtLocation(arrayList, release);
        if (_arrayList.size() <= 0) {
            Logger.getLogger(Map.class.getName()).log(Level.WARNING,
                    "mapAtLocation: nothing to map");


            return null;
        }

        final List<MapData> mapList = new Vector<MapData>();

        try {
            final long _size = size;
            final GenomeRelease _release = release;
            con =
                    Database.getDBConnection(Defaults.localDB);
            Statement s = con.createStatement();


            // create entry mapList
            final MapDetail dMapDetail = new MapDetail(
                    mapId,
                    Defines.MAP_LOCATION,
                    release.toString(),
                    "mapAtLocation size: " + size +
                    ", release:" + release);
            dMapDetail.initTableData();
            //MapService.perstistDetail(dMapDetail);


            s.execute("DROP TABLE IF EXISTS " + dMapDetail.getTableData());
            s.execute("CREATE TABLE " + dMapDetail.getTableData() + "( " +
                    "name VARCHAR(255) NOT NULL," +
                    "chrom VARCHAR(255) NOT NULL, " +
                    "chromStart int(10) unsigned, " +
                    "chromEnd int(10) unsigned, " +
                    " PRIMARY KEY (chrom, chromStart, chromEnd) " +
                    " )");

            final List<String> _chroms = CytoBandManagerImpl.stGetChroms(release);
            Thread workers[][] = new Thread[_arrayList.size()][];

            final Integer results[][] = new Integer[_arrayList.size()][];
            final MapData dMap[] = new MapData[_arrayList.size()];
            //final MapData dMap = null;
            final EntityManager em = DBService.getEntityManger();
            EntityTransaction userTransaction = em.getTransaction();
            userTransaction.begin();

            int i = 0;
            for (final Data currData : arrayList) {
                int id = arrayList.indexOf(currData);
                dMap[id] = new MapData(dMapDetail, currData, ++i);

                RegionArray currClazz = RegionLib.getRegionArrayClazz(currData.getClazz());

                final String colName = currClazz.getRatioColName();

                results[id] = new Integer[_chroms.size()];
                Arrays.fill(results[id], 0);
                workers[id] = new Thread[_chroms.size()];

                // add spatial index if not already there
                DBUtils.addPositionAtTable(currData.getTableData());

                s.execute("ALTER TABLE " + dMapDetail.getTableData() +
                        " ADD COLUMN " + dMap[id].getDataName() + " DOUBLE DEFAULT 0");
                for (String chromId : _chroms) {
                    final String _chromId = chromId;
                    final int ii = _chroms.indexOf(chromId);
                    final long _chr_start = ((CytoBand) CytoBandManagerImpl.getFirst(_release, chromId)).getChromStart();
                    final long _chr_end = ((CytoBand) CytoBandManagerImpl.getLast(_release, chromId)).getChromEnd();

                    final String mapDetailName = dMapDetail.getTableData();
                    final String mapName = dMap[id].getDataName();
                    final String dataName = currData.getTableData();


                    workers[id][ii] = new Thread(new Runnable() {

                        String insert = "";
                        int ichrom = RegionLib.fromChrToInt(_chromId);
                        int insert_count = 0;

                        public void run() {
                            int iid = arrayList.indexOf(currData);
                            try {

                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        " Start processing " + dMap[iid].getDataName() + " " + _chromId);

                                Connection _con = Database.getDBConnection(Defaults.localDB);
                                Statement _s = _con.createStatement();

                                for (long pos = _chr_start; pos < _chr_end; pos += _size) {
                                    //all position

                                    insert =
                                            "INSERT INTO " + mapDetailName +
                                            " (" + mapName + ", name,  chrom, chromStart, chromEnd ) " +
                                            " ( " +
                                            " SELECT  avg(" + colName + " ) as aratio , " +
                                            "\'" + (_chromId + ":" + pos + "-" + (pos + _size)) + "\' , " +
                                            "\'" + _chromId + "\'," + pos + " , " + ((pos + _size) < _chr_end ? (pos + _size) : _chr_end) +
                                            //" , avg(" + dcolName + ") as ratio " +
                                            " FROM " + dataName +
                                            " WHERE " +
                                            " MBRIntersects( gc_position," +
                                            " LineString(" +
                                            " Point(" + ichrom + "," + pos + "), " +
                                            " Point(" + ichrom + ", " + ((pos + _size) < _chr_end ? (pos + _size) : _chr_end) + "))" +
                                            " ) " +
                                            /*"chrom = \'" + chromId + "\' " +
                                            //" group by chrom, " + colName +*/
                                            " HAVING " + "avg(" + colName + " ) is not null" +
                                            /*" AND (  chromStart < " + ((pos + size) < chr_end ? (pos + size) : chr_end) +
                                            " AND  chromEnd  >  " + pos + " ) " + // segment border region */
                                            ") " +
                                            " ON DUPLICATE KEY UPDATE " + mapName + "  =   values(" + mapName + ")";
                                    //Logger.getLogger(Map.class.getName()).log(Level.INFO, insert);
                                    _s.addBatch(insert);
                                    insert_count++;

                                    if (insert_count > (Math.ceil(_chr_end / _size) / 1000)) {
                                        _s.executeBatch();
                                        _s.clearBatch();
                                        results[iid][ii] += insert_count;
                                        Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                                "mapAtLocation: " + dMap[iid].getDataName() + " " + _chromId + " inserted:  " + insert_count);

                                        insert_count = 0;
                                    }
                                }//end one chrom

                                _s.executeBatch();

                                _s.clearBatch();
                                results[iid][ii] += insert_count;
                                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                                        "mapAtLocation: " + dMap[iid].getDataName() + " " + _chromId + " inserted:  " + insert_count);

                                insert_count = 0;
                            } catch (Exception e) {
                                Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                        insert);
                                Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                                        "", e);

                                results[iid][ii] = -1;
                            }
                        } // end run

                        ;
                    }); // end thread


                    workers[id][ii].start();
                }//end chroms

            }//end data list

            for (int id = 0; id <
                    arrayList.size(); id++) {
                for (int j = 0; j <
                        workers.length; j++) {
                    workers[id][j].join(0);
                }

            }
            for (int id = 0; id <
                    arrayList.size(); id++) {
                int insert_count = 0;
                for (int j = 0; j <
                        results.length; j++) {
                    if (results[id][j] == -1) {
                        throw new Exception("Error");
                    } else {
                        insert_count += results[id][j];
                    }



                }

                Logger.getLogger(Map.class.getName()).log(Level.INFO,
                        "mapAtLocation: " + dMap[id].getDataName() + " inserted  " + insert_count);


                MapService.perstistData(dMap[id], em);
                Map.updateStats(dMap[id]);
                em.flush();
                mapList.add(dMap[id]);
            }

            userTransaction.commit();
            return mapList;
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, " mapAtLocation", e);
            throw e;
        }

    }

    /**
     * set statistics for map data 
     * @param newData
     * @throws java.lang.Exception
     */
    static void updateStats(MapData newData) throws Exception {
        try {
            //update array
            con = Database.getDBConnection(Defaults.localDB);
            Statement s = con.createStatement();

            String sql = "SELECT count(*) from " + newData.getTableData();
            ResultSet rs = s.executeQuery(sql);

            rs.next();

            newData.setNof(rs.getInt(1));
            // TODO MEDIAN!
            sql =
                    "SELECT  AVG(" + newData.getDataName() + "), null, VAR_SAMP(" + newData.getDataName() + "), STDDEV_SAMP(" + newData.getDataName() + "), MIN(" + newData.getDataName() + "), MAX(" + newData.getDataName() + ") " + " from " + newData.getTableData();

            rs =
                    s.executeQuery(sql);

            rs.next();
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            //otherSymbols.setGroupingSeparator(',');
            DecimalFormat myFormatter = new DecimalFormat("0.#####", otherSymbols);
            newData.setMean(new Double(myFormatter.format(rs.getDouble(1))));
            newData.setMedian(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
            newData.setVariance(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
            newData.setStddev(myFormatter.parse(rs.getString(4) != null ? rs.getString(4) : "0").doubleValue());
            newData.setMinRatio(rs.getDouble(5));
            newData.setMaxRatio(rs.getDouble(6));
            try {
                double median = DBUtils.getMedian(newData.getTableData(), newData.getDataName());
                newData.setMedian(median);
            } catch (Exception e) {
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE,
                        "get median", e);
            }
        } catch (Exception ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, " updateStats", ex);

            throw ex;
        }
    }

    /**
     * export with unique identifier chrom:start:stop
     *  @param mapDetail
     * @param list
     * @param inclId  include name, otherwise  chrom:start:stop
     * @param inclPos export with position 
     * @param inclNull if set to false rows with all cols = null will be skipped
     * @param chrom only set if subset to be exported, otherwise null
     * @param start only set if subset to be exported, otherwise null
     * @param end only set if subset to be exported, otherwise null
     * @param filename
     */
    public static void exportMapping(
            MapDetail mapDetail,
            List<MapData> list,
            boolean inclId,
            boolean inclPos,
            boolean inclNull,
            String chrom, long start, long end, String filename) {


        try {
            java.util.Map<String, String> meta = new java.util.HashMap<String, String>();

            Utils.deleteFile(filename);
            Vector<String> arrays = new Vector<String>();

            con =
                    Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();
            //String colname;
            ResultSet rs;
//= s.executeQuery("desc " + mapId);

            String sqlHead = "";
            String sql = "";
            String sqlNull = null;


            if (inclId) {
                sql = " name";
                sqlHead =
                        "\'name\'";
            } else {
                // create id aus position
                sqlHead += "\'chrom:start-stop\'";
                sql +=
                        "CONCAT(chrom, \':\', chromStart, \'-\', chromEnd )";
            }

            if (inclPos) {
                sqlHead += ", \'chrom\', \'chromStart\',\'chromEnd\'";
                sql +=
                        ", chrom, chromStart,chromEnd";
            }

            for (MapData d : list) {

                sql += " , ";
                sqlHead += " , ";



                sqlHead +=
                        "\'" + d.getDataName() + "\'";


                sql +=
                        "cast(" + d.getDataName() + " as decimal(15,7))";

                if (!inclNull) {
                    if (sqlNull == null) {
                        sqlNull = "";
                    } else {
                        sqlNull += " AND ";
                    }

                    sqlNull += (d.getDataName() + " is not null");
                }

            }
            String sqlPosition = null;
            String sqlWhere = null;

            if (chrom != null && start + end > 0) {
                sqlPosition = "chrom = \'" + chrom + "\' and " +
                        " chromStart < " + end + " AND chromEnd >  " + start;
            }

            String ssql = "SELECT  " + sqlHead + " UNION " +
                    "  SELECT  " +
                    sql + " FROM " + mapDetail.getTableData();

            if (sqlNull != null) {
                if (sqlWhere == null) {
                    sqlWhere = " WHERE ";
                } else {
                    sqlWhere += " AND ";
                }

                sqlWhere += "(" + sqlNull + ")";
            }

            if (sqlPosition != null) {
                if (sqlWhere == null) {
                    sqlWhere = " WHERE ";
                } else {
                    sqlWhere += " AND ";
                }

                sqlWhere += "(" + sqlPosition + ")";
            }

            ssql += sqlWhere != null ? sqlWhere : "";
            ssql +=
                    " into outfile \'" + filename + "\' ";
            System.out.println(ssql);
            s.execute(ssql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
