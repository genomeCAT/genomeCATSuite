package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportPlatform
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.logging.SimpleFormatter;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;

import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 * 050413 kt import 1...X,Y or 1...23,24 as chr... 260313 kt remove already set
 * mapping for genomic position if we have split fields
 *
 * 150812 kt extends Import_batch
 *
 */
public abstract class ImportPlatform extends Import_batch implements XPortPlatform {

    private String file_field_position = null;

    
    @Override
    public PlatformData getPlatformData(PlatformDetail detail) {
        PlatformData d = new PlatformData();
        if (detail == null) {
            Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE,
                    "getPlatformData: invalid platformdetail or  (null)");
            return null;
        }
        d.setName(detail.getName());
        //detail.addPlatformData(d);
        d.setOriginalFile(this.inFile.getAbsolutePath());
        d.setGenomeRelease(this.getRelease());
        //d.initTableData();
        d.setClazz(this.getClass().getName());

        return d;
    }

    /**
     * init Instance to import new file
     *
     * @param nameFile
     */
    public void newImportPlatform(String filename) throws Exception {
        super.newImport(filename);
        this.hasHeader = true;
        this.file_field_position = null;

    }
    public int ichrom = -1;

    @Override
    protected List<String[]> extendMapping() {

        // 050413 kt
        List<String[]> _map = this.getMappingFile2DBColNames();

        for (int i = 0; i < _map.size(); i++) {

            if (_map.get(i)[ind_db].contentEquals("chrom")) {
                ichrom = i;
                continue;

            }

        }

        return _map;
    }

    protected String[] modify(List<String[]> map, String[] tmp) {

        // todo logratio aus r/g (abhängig ob dyeswap 
        // 050413 kt
        if (tmp[ichrom] != null && !tmp[ichrom].contentEquals("") && !tmp[ichrom].contentEquals("--") && !tmp[ichrom].startsWith("chr")) {

            if (tmp[ichrom].charAt(0) == 'X' || tmp[ichrom].charAt(0) == 'Y') {
                tmp[ichrom] = "chr" + tmp[ichrom];
            } else {
                try {
                    tmp[ichrom] = RegionLib.fromIntToChr(Integer.parseInt(tmp[ichrom]));
                } catch (NumberFormatException numberFormatException) {
                    tmp[ichrom] = "chr" + tmp[ichrom];
                }
            }
        }
        return tmp;

    }
    Logger logger;

    public PlatformData doImportPlatform(PlatformDetail platformdetail,
            PlatformData platform, InformableHandler ifh) {

        logger = Logger.getLogger(ImportPlatform.class.getName());
        ifh.setFormatter(new SimpleFormatter());

        logger.addHandler(ifh);
        return this.doImportPlatform(platformdetail, platform);
    }

    @SuppressWarnings("empty-statement")
    public PlatformData doImportPlatform(
            PlatformDetail platformdetail,
            PlatformData platform) {

        boolean isNewPlatformDetail = false;

        if (platformdetail == null || platform == null) {
            Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE,
                    "doImportPlatform: invalid platformdetail or platform (null)");
            return null;
        } else {
            Logger.getLogger(ImportPlatform.class.getName()).log(Level.INFO,
                    "doImportPlatform:  " + platformdetail.toFullString());
        }
        em = DBService.getEntityManger();

        Statement s = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
        } catch (Exception ex) {
            Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            em.getTransaction().begin();

            if (platformdetail.getPlatformID() == null
                    || em.find(PlatformDetail.class, platformdetail.getPlatformID()) == null) {
                isNewPlatformDetail = true;

                Logger.getLogger(
                        ImportPlatform.class.getName()).log(Level.INFO,
                                "doImportPlatform: create platformdetail " + platformdetail.toFullString());

                // make platformdetail persistent in db
                em.persist(platformdetail);
                em.flush();
            } else {
                Logger.getLogger(
                        ImportPlatform.class.getName()).log(Level.INFO,
                                "doImportPlatform: merge platformdetail " + platformdetail.toFullString());

                platformdetail = em.merge(platformdetail);
                em.flush();
            }

            // init platformdata
            platformdetail.addPlatformData(platform);
            //platform.setPlatform(platformdetail);
            platform.initTableData();
            //platform.setGenomeRelease(release);

            // make platformdata persistent in db
            em.persist(platform);
            em.flush();

            // create  db table
            s.execute(
                    "DROP TABLE if EXISTS " + platform.getTableData());
            s.execute(
                    this.getCreateTableSQL(platform.getTableData()));

            // read data -> insert into table
            error = this.importData(platform.getTableData());

            Logger.getLogger(
                    ImportPlatform.class.getName()).log(Level.INFO,
                            "doImportPlatform: read data, number of errors: " + error);

            //update array
            String sql = "SELECT count(*) from " + platform.getTableData();
            ResultSet rs = s.executeQuery(sql);

            rs.next();

            platform.setNofSpots(rs.getInt(1));

            platform = em.merge(platform);

            em.flush();

            // test get modified, id ...???
            platform = em.merge(platform);

            em.getTransaction().commit();
            PlatformService.notifyListener();

        } catch (Exception e) {
            Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE, null, e);
            if (con != null) {
                try {
                    s = con.createStatement();
                    s.execute("DROP TABLE if EXISTS " + platform.getTableData());
                } catch (Exception ie) {
                    Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE, null, ie);
                }
            }

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            } else {
                try {
                    if (platform.getPlatformListID() != null) {
                        Logger.getLogger(ImportPlatform.class.getName()).log(Level.INFO,
                                "doImportPlatform: find Plattform  " + platform.getPlatformListID());
                        em.remove(em.find(PlatformData.class, platform.getPlatformListID()));
                    }
                    if (isNewPlatformDetail && platformdetail.getPlatformID() != null) {
                        Logger.getLogger(ImportPlatform.class.getName()).log(Level.INFO,
                                "doImportPlatform: find Plattform  " + platform.getPlatformListID());
                        em.remove(em.find(PlatformData.class, platform.getPlatformListID()));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ImportPlatform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (platformdetail != null) {
                platformdetail.removePlatformData(platform);
            }
            return null;
        } finally {
            try {

                em.close();
            } catch (Exception e) {
                Logger.getLogger(
                        ImportPlatform.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return platform;
    }

    public String setMappingFile2DBColNames(List<String[]> _map) {

        this.map = _map;
        if (this.hasSplitField()) {
            this.map = this.setSplitFieldCols(this.map);
        }
        for (String[] m : this.map) {
            Logger.getLogger(
                    ImportPlatform.class.getName()).log(Level.INFO, "Mapping: "
                            + Arrays.deepToString(m));
        }
        return null;
    }

    public boolean hasSplitField() {
        return (this.file_field_position != null);

    }

    public void setSplitFieldName(String field) {
        this.file_field_position = field;

    }

    // set file_field_position;
    // map each db position column to this field
    public String getSplitFieldName() {
        return this.file_field_position;
    }

    protected String[] splitPositionValue(List<String[]> map, String[] tmp) {
        if (this.hasSplitField()) {
            //String[] newtmp = new String[tmp.length+2];

            for (int i = 0; i < map.size(); i++) {
                if (map.get(i)[ind_file].contentEquals(this.getSplitFieldName())) {
                    String[] position = tmp[i].split("[:-]");
                    if (position.length == 1) {
                        position = new String[]{"", "", ""};
                    }
                    tmp[i++] = position[0];
                    tmp[i++] = position[1];
                    tmp[i++] = position[2];
                    break;
                }
            }
        }
        return tmp;
    }

    public String[] getSplitFieldArray() {
        return new String[]{"", "", ""};
    }

    public String getSplitPattern() {
        return new String("[:-]");
    }

    @Override
    public List<String[]> setSplitFieldCols(List<String[]> _map) {
        if (!this.hasSplitField()) {
            return _map;
        }
        //kt 260313 remove already set mapping for genomic position if we have split fields
        Vector<String[]> _del = new Vector<String[]>();

        for (String[] m : _map) {
            if (m[ind_db].contentEquals("chrom")) {
                _del.add(m);
            }
            if (m[ind_db].contentEquals("chromStart")) {
                _del.add(m);
            }
            if (m[ind_db].contentEquals("chromEnd")) {
                _del.add(m);
            }
        }
        for (String[] m : _del) {
            Logger.getLogger(
                    ImportPlatform.class.getName()).log(Level.INFO, "Splitfields remove old: "
                            + Arrays.deepToString(m));
            _map.remove(m);
        }
        String[] entry;
        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = SPLITFIELD;
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromStart";
        entry[ind_file] = SPLITFIELD;
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = SPLITFIELD;
        _map.add(entry);

        return _map;
    }
}
