package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportTrack
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.logging.SimpleFormatter;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.common.Defaults;

import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInTrack;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.ProjectService;
import org.molgen.genomeCATPro.datadb.service.TrackService;

/**
 * 020813 kt XPortImport createNewImport(); 030413 kt import 1...X,Y or
 * 1...23,24 as chr... 120313 kt user setting if has header 050612 kt update
 * notification 150812 kt extends Import_batch 051012 kt getTrack cut off all
 * endings like bed,txt
 *
 */
public class ImportTrack extends Import_batch implements XPortTrack {

    public static String track_bedgraph_txt = "BEDGRAPH";
    private String file_field_position = null;
    private Study project = null;

    /**
     * init Instance to import new file
     *
     * @param nameFile
     */
    public void newImportTrack(String filename) throws Exception {
        super.newImport(filename);
        this.hasHeader = false;
        this.file_field_position = null;

    }

    public ImportTrack createNewImport() {
        return new ImportTrack();
    }
    Logger logger;

    public Track doImportTrack(Track t, InformableHandler ifh) {

        logger = Logger.getLogger(ImportTrack.class.getName());
        ifh.setFormatter(new SimpleFormatter());

        logger.addHandler(ifh);
        return this.doImportTrack(t);
    }

    @SuppressWarnings("empty-statement")
    public Track doImportTrack(Track track) {

        if (track == null) {
            Logger.getLogger(ImportTrack.class.getName()).log(Level.SEVERE,
                    "doImportTrack: invalid track (null)");
            return null;
        } else {
            Logger.getLogger(ImportTrack.class.getName()).log(Level.INFO,
                    "doImportTrack:  " + track.toFullString());
        }
        em = DBService.getEntityManger();

        Statement s = null;
        try {
            con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            s = con.createStatement();
        } catch (Exception ex) {
            Logger.getLogger(ImportTrack.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            List<SampleInTrack> sampleList = new Vector<SampleInTrack>(track.getSamples());
            Collections.copy(sampleList, track.getSamples());
            track.setSamples(Collections.EMPTY_LIST);
            // ^ auto persist vermeiden
            em.getTransaction().begin();
            track.initTableData();
            Logger.getLogger(
                    ImportTrack.class.getName()).log(Level.INFO,
                            "doImportPlatform: create Track: " + track.toFullString());

            // make platformdetail persistent in db
            TrackService.persistsTrack(track, em);
            em.flush();
            em.refresh(track);

            //retrieve samples if ness
            // load sampleinexperiment and samples to persistent context
            track.setSamples(TrackService.importSamples(sampleList, track, true, em));

            //synch sample-lists with db
            //em.merge(this.experimentdetail);
            em.flush();

            // create  db table
            s.execute(
                    "DROP TABLE if EXISTS " + track.getTableData());
            s.execute(
                    this.getCreateTableSQL(track.getTableData()));

            // read data -> insert into table
            error = this.importData(track.getTableData());

            Logger.getLogger(
                    ImportTrack.class.getName()).log(Level.INFO,
                            "doImportTrack: read data, number of errors: " + error);

            //update track
            String sql = "SELECT count(*) from " + track.getTableData();
            ResultSet rs = s.executeQuery(sql);

            rs.next();

            track.setNof(rs.getInt(1));
            // TODO MEDIAN!
            sql = "SELECT  AVG(ratio), VAR_SAMP(ratio), STDDEV_SAMP(ratio), MIN(ratio), MAX(ratio) "
                    + " from " + track.getTableData();

            rs = s.executeQuery(sql);

            rs.next();
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            //otherSymbols.setGroupingSeparator(',');
            DecimalFormat myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
            track.setMean(new Double(myFormatter.format(rs.getDouble(
                    1))));
            track.setVariance(myFormatter.parse(rs.getString(2) != null ? rs.getString(2) : "0").doubleValue());
            track.setStddev(myFormatter.parse(rs.getString(3) != null ? rs.getString(3) : "0").doubleValue());
            track.setMinRatio(rs.getDouble(4));
            track.setMaxRatio(rs.getDouble(5));

            try {
                double median = DBUtils.getMedian(track.getTableData(), "ratio");
                track.setMedian(median);
            } catch (Exception e) {
                Logger.getLogger(ImportTrack.class.getName()).log(Level.SEVERE,
                        "get median", e);
            }
            PreparedStatement ps = con.prepareStatement(
                    "update " + track.getTableData()
                    + " set name = id  where name = \"\"");

            //ps.setDouble(1, factor);
            ps.executeUpdate();

            em.flush();
            em.refresh(track);

            ProjectService.addTrack(this.project, track, em);
            em.getTransaction().commit();
            ExperimentService.notifyListener(); //050612 kt

        } catch (Exception e) {
            Logger.getLogger(ImportTrack.class.getName()).log(Level.SEVERE, null, e);
            if (con != null) {
                try {
                    s = con.createStatement();
                    s.execute("DROP TABLE if EXISTS " + track.getTableData());
                } catch (Exception ie) {
                    Logger.getLogger(ImportTrack.class.getName()).log(Level.SEVERE, null, ie);
                }
            }

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            } else {
                try {
                    if (track.getTrackID() != null) {
                        Logger.getLogger(ImportTrack.class.getName()).log(Level.INFO,
                                "doImportTrack: find Track  " + track.getTrackID());
                        em.remove(em.find(Track.class, track.getTrackID()));
                    }

                } catch (Exception ex) {
                    Logger.getLogger(ImportTrack.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        } finally {
            try {

                em.close();
            } catch (Exception e) {
                Logger.getLogger(
                        ImportTrack.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        return track;
    }

    public String setMappingFile2DBColNames(List<String[]> _map) {

        this.map = _map;
        if (this.hasSplitField()) {
            this.map = this.setSplitFieldCols(this.map);
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

    @Override
    protected boolean isCommentLine(String is) {
        if (is.indexOf("#") == 0 || is.indexOf("track") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isHeaderLine(String is) {
        return false; // 120313 kt

    }

    @Override
    protected void readRelease() {
        this.release = null;
    }

    @Override
    protected String getEndMetaDataTag() {
        return null;
    }

    @Override
    protected String getEndDataTag() {
        return "END";
    }

    
    protected String getCreateTableSQL(String tableData) {
        String sql
                = " CREATE TABLE " + tableData + " ( "
                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "name varchar(255) NOT NULL, "
                + "chrom varChar(45) NOT NULL,"
                + "chromStart int(10) unsigned NOT NULL,"
                + "chromEnd int(10) unsigned NOT NULL,"
                + "ratio DOUBLE, "
                + "count int , "
                + "PRIMARY KEY (id),"
                + "INDEX (chrom (5) ), "
                + "INDEX (chromStart ), "
                + "INDEX (chromEnd),"
                + "INDEX (name)  ) "
                + "TYPE=MyISAM";
        return sql;
    }

    @Override
    public String[] getDBColNames() {
        return new String[]{
            "chrom", "chromStart", "chromEnd",
            "ratio", "name"
        };
    }

    @Override
    protected void readFileColNames() {

        try {
            super.readFileColNames();
        } catch (Exception e) {
            Logger.getLogger(ImportTrack.class.getName()).log(Level.INFO, e.getMessage(), e);
        }
        if (this.fileColNames == null) {
            Vector<Vector<String>> l = super.readData(10);
            int max = 0;
            for (Vector<String> el : l) {
                max = (el.size() > max ? el.size() : max);
            }
            this.fileColNames = new String[max];
            for (int i = 0; i < max; i++) {
                this.fileColNames[i] = "field" + (i + 1);
            }
        }

    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();
        String[] entry = new String[2];

        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = hasHeader ? (this.fileColNames.length < 1 ? "" : this.fileColNames[0]) : "field1";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromStart";
        entry[ind_file] = hasHeader ? (this.fileColNames.length < 2 ? "" : this.fileColNames[1]) : "field2";

        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = hasHeader ? (this.fileColNames.length < 3 ? "" : this.fileColNames[2]) : "field3";

        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "name";
        entry[ind_file] = hasHeader ? "" : "field5";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "ratio";
        entry[ind_file] = hasHeader ? (this.fileColNames.length < 4 ? "" : this.fileColNames[3]) : "field4";
        _map.add(entry);

        return _map;
    }
    int iratio = -1;
    int ichrom = -1;

    @Override
    protected List<String[]> extendMapping() {
        List<String[]> _map = this.getMappingFile2DBColNames();
        String[] e = new String[2];

        for (int i = 0; i < _map.size(); i++) {

            if (_map.get(i)[ind_db].contentEquals("ratio")) {
                iratio = i;
                continue;

            }
            if (_map.get(i)[ind_db].contentEquals("chrom")) {
                ichrom = i;
                continue;

            }

        }

        return _map;
    }

    protected String[] modify(List<String[]> map, String[] tmp) {

        // todo logratio aus r/g (abhängig ob dyeswap 
        double ratio = Double.parseDouble(tmp[iratio] != null ? tmp[iratio] : "0");

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        DecimalFormat myFormatter = new DecimalFormat("0.#####", otherSymbols);
        tmp[iratio] = myFormatter.format(ratio);

        // 030413 kt
        if (tmp[ichrom] != null && !tmp[ichrom].contentEquals("") && !tmp[ichrom].startsWith("chr")) {

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

    public String getName() {
        return ImportTrack.track_bedgraph_txt;
    }

    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{ImportTrack.track_bedgraph_txt}));
    }

    public Track getTrack() {
        Track t = new Track();
        String name = this.inFile.getName();
        if (name.indexOf(".") > 0) {
            name = name.substring(0, name.indexOf("."));     //051012 kt cut off all endings like bed,txt

        }
        t.setName(name);

        //d.setType(Defaults.Type.BAC.toString());
        t.setDescription("imported via " + this.getClass().getName());
        t.setClazz("org.molgen.genomeCATPro.data.FeatureImpl");
        t.setDataType(Defaults.DataType.PEAK);
        t.setOriginalFile(this.inFile.getPath());
        return t;
    }

    public void setProject(String s) {
        try {
            this.project = ProjectService.getProjectByName(s, null);
            if (this.project == null) {
                throw new RuntimeException("Project " + s + " not found in database!");
            }
        } catch (Exception e) {
            Logger.getLogger(ImportTrack.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
