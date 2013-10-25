/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat.maparr;

import java.util.List;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgen.dblib.DBService;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionImpl;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.MapService;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 *
 * @author tebel
 */
public class MapTest {

    public MapTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Database.setDBParams(Defaults.localDB, "genomeCAT", "localhost", "3306", "user", "user");
        DBService.setConnection("localhost", "3306", "genomeCAT", "user", "user");

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    // @Test
    @Deprecated
    public void testMapAtGene() throws Exception {
        List<Data> list = new Vector<Data>();
        Data d1 = ExperimentService.getExperimentByDataId((long) 234);
        Data d2 = ExperimentService.getExperimentByDataId((long) 233);

        list.add(d1);
        list.add(d2);

        //PlatformDetail p = PlatformService.getPlatformDetailById((long) 60);

        Map.mapAtGene(Defaults.GenomeRelease.hg18, list, "testMapGene1");
    }

    /*@Test
    public void testMapAtLocation() throws Exception {
    System.setProperty("user.dir", "/project/H1N1/");
    List<Data> list = new Vector<Data>();
    Data d1 = ExperimentService.getExperimentByDataId((long) 617);
    Data d2 = ExperimentService.getExperimentByDataId((long) 674);
    
    list.add(d1);
    list.add(d2);
    
    //PlatformDetail p = PlatformService.getPlatformDetailById((long) 60);
    
    Map.mapAtLocation(list, 10000, Defaults.GenomeRelease.hg19, "testMap_test_1q");
    }*/
    //@Test
    public void testMapAtLocation() throws Exception {
        System.setProperty("user.dir", "/project/H1N1/");
        List<Data> list = new Vector<Data>();
        Data d1 = ExperimentService.getExperimentByDataId((long) 377);
        Data d2 = ExperimentService.getExperimentByDataId((long) 352);

        list.add(d1);
        list.add(d2);

        //PlatformDetail p = PlatformService.getPlatformDetailById((long) 60);

        Map.mapAtLocation(list, 10000, Defaults.GenomeRelease.hg18, "testMap_400acgh");
    }
    //@Test

    public void testMapAtLocationBin() throws Exception {
        System.setProperty("user.dir", "/project/H1N1/");
        List<Data> list = new Vector<Data>();
        Data d1 = ExperimentService.getExperimentByDataId((long) 377);
        Data d2 = ExperimentService.getExperimentByDataId((long) 352);

        list.add(d1);
        list.add(d2);

        //PlatformDetail p = PlatformService.getPlatformDetailById((long) 60);

        Map.mapAtLocationBin(list, 1000, Defaults.GenomeRelease.hg18, "testMap6_1000");
    }
    //@Test

    public void testMapAtId() throws Exception {

        List<Data> list = new Vector<Data>();
        Data d1 = ExperimentService.getExperimentByDataId((long) 234);
        Data d2 = ExperimentService.getExperimentByDataId((long) 233);

        list.add(d1);
        list.add(d2);

        PlatformDetail p = PlatformService.getPlatformDetailById((long) 64);

        Map.mapAtId(Defaults.GenomeRelease.hg18, list, p, "testMap3");
    }

    //@Test
    public void testMapAtRegion() throws Exception {
        List<Data> list = new Vector<Data>();
        Data d1 = ExperimentService.getExperimentByDataId((long) 377);
        Data d2 = ExperimentService.getExperimentByDataId((long) 234);

        list.add(d1);
        list.add(d2);

        String bedfile = "/project/Kopenhagen/Katrin/GenomeCATPro/data/regions_Example_for_Selector.bed";


        List<? extends Region> posList = new Vector<RegionImpl>();
        Integer error = Map.importBED((List<RegionImpl>) posList, bedfile);
        Logger.getLogger(MapTest.class.getName()).log(Level.INFO,
                "imported: " + posList.size() + " error: " + error);

        //assertEquals(error, 0);

        Map.mapAtRegion(
                Defaults.GenomeRelease.hg17, posList, bedfile, list,
                "test55");

    }

    /**
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testMapAtAnnotation() throws Exception {
        List<Data> list = new Vector<Data>();

        Data d1 = ExperimentService.getExperimentByDataId((long) 531);
        list.add(d1);
        Data d2 = ExperimentService.getExperimentByDataId((long) 761);
        list.add(d2);
        Data d3 = ExperimentService.getExperimentByDataId((long) 612);
        list.add(d3);
        //Data d2 = ExperimentService.getExperimentByDataId((long) 234);




        //assertEquals(error, 0);
        
        Map.mapAtAnnotation(
                Defaults.GenomeRelease.hg18, "gene", "geneSymbol", list,
                "testAnno_thread");

    }

    //@Test
    public void testexportMapping() throws Exception {
        MapDetail m = MapService.getMapDetailByName("test55");

        List<MapData> list = MapService.getMapDataList(m);
        /**
         * MapDetail mapDetail, 
        List<MapData> list,
        boolean inclId,
        boolean inclPos,
        boolean inclNull,
        String chrom, int start, int end, String filename
         */
        //Map.exportMapping(m, list, true, false, false, null, 0, 0, "/project/H1N1/mapping__091132080911.txt");
        //chr1:162400000-162500000
        /*Map.exportMapping(m, list, 
        false, true, false, "chr1", 162400000, 242700000, 
        "/project/H1N1/mapping__091132080911_sub.txt");*/
        Map.exportMapping(m, list, true, true, true, null, 0, 0,
                "/project/H1N1/map55.txt");
    // Map.exportMapping(m, list, true, true, true, "chr1", 162400000, 242700000,
    //         "/project/H1N1/map55.txt");

    }
}