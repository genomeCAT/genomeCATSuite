package org.molgen.genomeCATPro.peaks.cnvcat;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.peaks.Aberration;

/**
 * @name AberrationCNVCAT
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>.
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
/**
 * 110412kt   dont set ifAberrant at construction time 
 *  (attributed used only by chproframe-chip to display aberrant spots)
 * @author tebel
 */
@Entity(name = "AberrationCGH")
public class AberrationCNVCAT implements Aberration, RegionArray, java.io.Serializable {

    /**
     * default constructor
     */
    public AberrationCNVCAT() {
    }

    /**
     * create new AberrationCNVCAT with initial start, end
     * @param posStart
     * @param posEnd
     */
    AberrationCNVCAT(long posStart, long posEnd) {
        this.chromEnd = posEnd;
        this.chromStart = posStart;
    }

    /**
     * construct new AberrationCNVCAT 
     * @param peakId
     * @param trackId
     * @param type
     * @param chrom
     * @param chromStart
     * @param chromEnd
     * @param ratio
     * @param startFeature
     */
    public AberrationCNVCAT(
            String peakId, String trackId,
            String type,
            String chrom, Long chromStart, Long chromEnd,
            double ratio, String startFeature) {
        this.peakId = peakId;
        this.trackId = trackId;
        this.type = type;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
        this.firstPeakId = startFeature;
        this.count = 1;
        this.ratio = ratio;
        this.quality = 0.0;
    /*
    if (this.type.contentEquals(Aberration.DUPLICATION)) {
    this.iAberrant = 1;
    }
    if (this.type.contentEquals(Aberration.DELETION)) {
    this.iAberrant = -1;
    }*/

    }

    /**
     * construct new AberrationCNVCAT 
     * @param iid
     * @param peakId
     * @param type
     * @param chrom
     * @param chromStart
     * @param chromEnd
     * @param ratio
     * @param quality
     * @param count
     * @param firstPeakId
     * @param lastPeakId
     */
    public AberrationCNVCAT(
            Long iid, String peakId, String type,
            String chrom, long chromStart, long chromEnd,
            double ratio, double quality, int count,
            String firstPeakId, String lastPeakId) {
        this.iid = iid;
        this.peakId = peakId;
        this.type = type;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
        this.firstPeakId = firstPeakId;
        this.lastPeakId = lastPeakId;
        this.ratio = ratio;
        this.quality = quality;
        /*if (this.type.contentEquals(Aberration.DUPLICATION)) {
        this.iAberrant = 1;
        }
        if (this.type.contentEquals(Aberration.DELETION)) {
        this.iAberrant = -1;
        }*/
        this.count = count;
    }

    /**
     * construct new AberrationCNVCAT 
     * @param iid
     * @param peakId
     * @param chrom
     * @param chromStart
     * @param chromEnd
     * @param ratio
     * @param count
     */
    public AberrationCNVCAT(
            Long iid, String peakId,
            String chrom, long chromStart, long chromEnd,
            double ratio, int count) {
        this.iid = iid;
        this.peakId = peakId;
        if (this.peakId == null || this.peakId.contentEquals("")) {
            this.peakId = Long.toString(this.iid);
        }
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
        this.firstPeakId = "";
        this.lastPeakId = "";
        this.ratio = ratio;
        this.quality = 1.0;

        if (this.ratio < 0) {
            this.type = Aberration.DELETION;
        } else {
            this.type = Aberration.DUPLICATION;
        }
        /*
        if (this.type.contentEquals(Aberration.DUPLICATION)) {
        this.iAberrant = 1;
        }
        if (this.type.contentEquals(Aberration.DELETION)) {
        this.iAberrant = -1;
        }*/
        this.count = count;
    }
    /**
     * load records from database for a specific data instance
     * @param d  - object of type Data containing meta information
     * @return data from database - one object of type Feature for each region
     * @throws java.lang.Exception
     */
    public List<? extends Feature> loadFromDB(Data d) throws Exception {

        Logger.getLogger(AberrationCNVCAT.class.getName()).log(Level.INFO, d.getName());
        /* if (!(chipPeaks instanceof ChipPeaks) || !(chipPeaks instanceof ChipFeature)) {
        Logger.getLogger(AberrationCGH.class.getName()).log(Level.SEVERE,
        "dbLoadFeatures: Chip class not valid " + chipPeaks.getClass().getName());
        return;
        }*/
        AberrationCNVCAT acgh = null;
        Connection con = null;
        List<AberrationCNVCAT> list = new Vector<AberrationCNVCAT>();
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "Select " +
                    "id, name,  chrom, chromStart, chromEnd, " +
                    " ratio, quality, " +
                    " count, type, firstPeakId, lastPeakId " +
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by chrom, chromStart");


            while (rs.next()) {

                acgh = new AberrationCNVCAT(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getDouble("ratio"),
                        rs.getDouble("quality"),
                        rs.getInt("count"),
                        rs.getString("firstPeakId"),
                        rs.getString("lastPeakId"));
                list.add(acgh);
            }
            return list;
        } catch (Exception e) {
            Logger.getLogger(AberrationCNVCAT.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AberrationCNVCAT.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }
    }

    /**
     * load records from database for a specific data instance
     * @param d  - object of type Data containing meta information
     * @return data from database - one object of type AberrationCNVCAT for each region
     * @throws java.lang.Exception
     */
    public static List<AberrationCNVCAT> loadCNVFromDB(Data d) throws Exception {
        Logger.getLogger(AberrationCNVCAT.class.getName()).log(Level.INFO, d.getName());

        AberrationCNVCAT acgh = null;
        Connection con = null;
        List<AberrationCNVCAT> list = new Vector<AberrationCNVCAT>();
        try {
            con = Database.getDBConnection(Defaults.localDB);

            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(
                    "Select * " +
                    /*
                    "id, name, chrom, chromStart, chromEnd, " +
                    " ratio, " +
                    // check if quality is defined
                    " if( exists (SELECT * FROM INFORMATION_SCHEMA.COLUMNS " +
                    " WHERE TABLE_NAME = \'" + d.getTableData() + "\' " +
                    " AND COLUMN_NAME = \'quality\') , \'quality\'  , 0 ), " +
                    " count, type, firstPeakId, lastPeakId " +
                     */
                    " from " + d.getTableData() +
                    " where chrom != \'\' " +
                    " order by chrom, chromStart");

            boolean hasCount = rs.findColumn("count") > 0;

            while (rs.next()) {

                acgh = new AberrationCNVCAT(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("chrom"),
                        rs.getLong("chromStart"),
                        rs.getLong("chromEnd"),
                        rs.getDouble("ratio"),
                        hasCount ? rs.getInt("count") : 0);
                list.add(acgh);
            }
            return list;
        } catch (Exception e) {
            Logger.getLogger(AberrationCNVCAT.class.getName()).log(Level.INFO, "Error: ", e);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AberrationCNVCAT.class.getName()).log(Level.INFO, "Error: ", ex);
                }
            }
        }
    }

    /**
     * table creation sql statement for this type
     * @param d object of type Data containing meta information
     * @return
     */
    public String getCreateTableSQL(Data d) {
        String sql = "CREATE  TABLE IF NOT EXISTS " + d.getTableData() + " (" +
                "id INT NOT NULL AUTO_INCREMENT ," +
                "name VARCHAR(255) NULL ," +
                "type VARCHAR(255) , " +
                "chrom VARCHAR(15) NULL ," +
                "chromStart INT NULL , " +
                " chromEnd INT NULL , " +
                " ratio DOUBLE NULL , " +
                " quality DOUBLE NULL , " +
                " count int , " +
                " firstPeakId VARCHAR(255), " +
                " lastPeakId VARCHAR(255), " +
                " PRIMARY KEY (id) ) ";
        return sql;
    }

    /**
     * insert table sql statement for this type
     * @param d object of type Data containing meta information
     * @return
     */
    public String getInsertSQL(Data d) {
        String sql = "INSERT INTO " + d.getTableData() +
                " (name, chrom, chromStart, chromEnd, ratio, quality," +
                "type, count, firstPeakId, lastPeakId ) " +
                "values (\'" + this.peakId + "\',\'" + this.chrom + "\',\'" +
                this.chromStart + "\',\'" + this.chromEnd + "\',\'" +
                this.ratio + "\',\'" + this.quality + "\',\'" +
                this.type + "\',\'" + (this.count == null ? 0 : this.count.intValue()) + "\',\'" +
                this.firstPeakId + "\',\'" + this.lastPeakId + "\')";
        return sql;
    }
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", nullable = false)
    private Long iid = 0L;
    @Column(name = "name", nullable = false)
    private String peakId;
    @Column(name = "chrom", nullable = false)
    private String chrom;
    @Column(name = "chromStart", nullable = false)
    private long chromStart;
    @Column(name = "chromEnd", nullable = false)
    private long chromEnd;
    @Column(name = "ratio")
    private Double ratio = 0.0;
    @Column(name = "quality")
    private Double quality = 0.0;
    @Column(name = "type")
    private String type;
    @Column(name = "count")
    private Integer count;
    @Column(name = "firstPeakId", nullable = false)
    private String firstPeakId;
    @Column(name = "lastPeakId", nullable = false)
    private String lastPeakId;
    @Transient
    private int XDispColumn = 0;
    @Transient
    private boolean selected = false;
    @Transient
    private boolean hidden = false;
    @Transient
    private int iAberrant = 0;
    @Transient
    String trackId;

    public Long getIid() {
        return iid;
    }

    public void setIid(Long iid) {
        this.iid = iid;
    }

    public String getPeakId() {
        return peakId;
    }

    public void setPeakId(String peakId) {
        this.peakId = peakId;
    }

    public String getName() {
        return this.getPeakId();
    }

    public String getChrom() {
        return chrom;
    }

    public void setChrom(String chrom) {
        String oldChrom = this.chrom;
        this.chrom = chrom;
        changeSupport.firePropertyChange("chrom", oldChrom, chrom);
    }

    public long getChromStart() {
        return chromStart;
    }

    public void setChromStart(long chromStart) {
        this.chromStart = chromStart;
    }

    public long getChromEnd() {
        return chromEnd;
    }

    public void setChromEnd(long chromEnd) {
        this.chromEnd = chromEnd;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getFirstPeakId() {
        return firstPeakId;
    }

    public void setFirstPeakId(String firstPeakId) {
        this.firstPeakId = firstPeakId;
    }

    public String getLastPeakId() {
        return lastPeakId;
    }

    public void setLastPeakId(String lastPeakId) {
        this.lastPeakId = lastPeakId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iid != null ? iid.hashCode() : 0);
        hash += (trackId != null ? trackId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AberrationCNVCAT)) {
            return false;
        }
        AberrationCNVCAT other = (AberrationCNVCAT) object;
        if ((this.iid == null && other.iid != null && this.trackId == null && other.trackId != null) ||
                (this.iid != null && !this.iid.equals(other.iid) &&
                this.trackId != null && !this.trackId.equals(other.trackId))) {
            return false;
        }
        return true;
    }

    public void addClone(String cloneId, Long chromEnd, double ratio) {
    }

    @Override
    /*
    public String toString() {
    return "cgh.Aberration[id=" + id + "]";
    }
     */
    public int getXDispColumn() {
        return XDispColumn;
    }

    public void setXDispColumn(int XDispColumn) {
        this.XDispColumn = XDispColumn;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    // compare aberration by phenotype, caseId
    // order compare caseId, phenotype
    public int compareByAberrationId(Aberration a) {

        return this.getPeakId().compareTo(a.getPeakId());

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        try {
            boolean old = this.selected;
            this.selected = selected;
            changeSupport.firePropertyChange("selected", old, selected);
        } catch (Exception e) {
        }

    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        boolean old = this.hidden;
        this.hidden = hidden;
        changeSupport.firePropertyChange("hidden", old, hidden);
    }

    public void addFeature(String cloneId, Long chromEnd, double ratio) {
        this.lastPeakId = cloneId;
        this.chromEnd = chromEnd;
        this.ratio += ratio;
        this.count++;
    }

    public void setTrackId(String id) {
        this.trackId = id;
    }

    public String getTrackId() {
        return this.trackId;
    }

    public double getQuality() {

        return this.quality;

    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getRatio() {
        return this.ratio;
    }

    public boolean isAberrant() {
        return (this.iAberrant != 0);
    }

    public void setIfAberrant(int i) {
        this.iAberrant = i;
    }

    public int getIfAberrant() {
        return this.iAberrant;
    }

    public String getId() {
        return this.getPeakId();
    }

    public void setId(String id) {
        this.setPeakId(id);
    }

    public String toHTMLString() {
        return new String(this.getPeakId() +
                String.format(" %10.2f %10.2f ", this.getRatio(), this.getQuality()));
    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public final static String ICON_PATH_PEAK = "org/molgen/genomeCATPro/peaks/page_blank_chart_16.png";

    public String getIconPath() {
        return AberrationCNVCAT.ICON_PATH_PEAK;
    }

    public boolean hasGeneView() {
        return false;
    }

    public boolean hasRegionView() {
        return true;
    }

    public boolean hasProbeView() {
        return false;
    }

    public String getRatioColName() {
        return "ratio";
    }

    public String getGeneColName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getProbeColName() {
        return "name";
    }

    @Override
    public String toString() {
        return new String(getChrom() + ":" + getChromStart() + "-" + getChromEnd());
    }

    public void setName(String name) {
        this.setPeakId(name);
    }

    public boolean equalsByPos(Region r2) {
        return (this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd());

    }
}
    
