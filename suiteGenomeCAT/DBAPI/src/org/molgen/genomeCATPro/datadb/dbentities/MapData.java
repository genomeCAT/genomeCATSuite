package org.molgen.genomeCATPro.datadb.dbentities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.common.Defaults.DataType;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.common.Utils;

/**
 * @name MapList
 *
 * mapped data
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
@Entity
@Table(name = "MapList")
@NamedQueries({})
public class MapData implements Serializable, Data {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MapID", nullable = false)
    private Long mapID = null;
    @Column(name = "mapName", nullable = false)
    private String mapName;
    @Column(name = "mapType")
    private String mapType;
    @Column(name = "dataName", nullable = false)
    private String dataName;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "genomeRelease", nullable = false)
    private String genomeRelease;
    @Column(name = "tableData", nullable = false)
    private String tableData;
    @Column(name = "description")
    private String description;
    @Column(name = "nofData")
    private Integer nofData;
    @Column(name = "mean")
    private Double mean;
    @Column(name = "median")
    private Double median;
    @Column(name = "stddev")
    private Double stddev;
    @Column(name = "variance")
    private Double variance;
    @Column(name = "minRatio")
    private Double minRatio;
    @Column(name = "maxRatio")
    private Double maxRatio;
    @Transient
    String clazz = "";
    @ManyToOne()
    @JoinColumn(name = "idOwner")
    private User owner;

    public MapData() {
    }

    public MapData(Long ID) {
        this();
        this.mapID = ID;
    }

    public MapData(MapDetail dMapDetail, Data currData, int i) {
        this.dataName = i + "_" + currData.getName().substring(0, Math.min(61, currData.getName().length() - 1));
        this.clazz = currData.getClazz();
        this.owner = currData.getOwner();
        this.mapName = dMapDetail.getMapName();
        this.mapType = dMapDetail.getMapType();
        this.tableData = dMapDetail.getTableData();
        this.genomeRelease = dMapDetail.getGenomeRelease();
        this.description = dMapDetail.getDescription() + "\n" + currData.getMetaText().toString();

    }

    /*
     * copy - useful for GUI Entities with field attached listeners
     */
    public void copy(MapData s) {

        this.setMapID(s.getMapID());
        this.setMapName(s.getMapName());
        this.setDataName(s.getDataName());
        this.setDescription(s.getDescription());
        this.setGenomeRelease(s.getGenomeRelease());

        this.setTableData(s.getTableData());

        this.setModified(s.getModified());
        this.setClazz(s.getClazz());
        this.setCreated(s.getCreated());

        this.setNof(s.getNof());
        this.setMaxRatio(s.getMaxRatio());
        this.setMean(s.getMean());
        this.setMedian(s.getMedian());
        this.setMinRatio(s.getMinRatio());
        this.setMaxRatio(s.getMaxRatio());
        this.setStddev(s.getStddev());
        this.setVariance(s.getVariance());

        this.setOwner(s.getOwner());
        this.setParent(s.getParent());

    }

    public String getDataName() {
        return dataName;
    }

    public boolean allowSegmentation() {
        return false;
    }

    public void setDataName(String dataName) {
        String old = this.dataName;
        this.dataName = dataName;
        changeSupport.firePropertyChange("dataName", old, dataName);
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        String oldClazz = this.clazz;
        this.clazz = clazz;
        changeSupport.firePropertyChange("clazz", oldClazz, clazz);
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        String old = this.mapName;
        this.mapName = mapName;
        changeSupport.firePropertyChange("mapName", old, mapName);
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        String old = this.mapType;
        this.mapType = mapType;
        changeSupport.firePropertyChange("mapType", old, mapType);
    }

    public Long getMapID() {
        return mapID;
    }

    public void setMapID(Long mapID) {
        this.mapID = mapID;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        Date old = this.modified;
        this.modified = modified;
        changeSupport.firePropertyChange("modified", old, modified);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        Date old = this.created;
        this.created = created;
        changeSupport.firePropertyChange("created", old, created);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String old = this.description;
        this.description = description;
        changeSupport.firePropertyChange("description", old, description);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.mapID != null ? this.mapID.hashCode() : 0);
        return hash;
    }

    public String getGenomeRelease() {
        return genomeRelease;
    }

    public void setGenomeRelease(String genomeRelease) {
        String oldRelease = this.genomeRelease;
        this.genomeRelease = genomeRelease;
        changeSupport.firePropertyChange("genomeRelease", oldRelease, genomeRelease);
    }

    public String getTableData() {

        return tableData;
    }

    public void setTableData(String tableData) {

        this.tableData = tableData;

    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MapData)) {
            return false;
        }
        MapData other = (MapData) object;
        if ((this.mapID == null && other.mapID != null) || (this.mapID != null && !this.mapID.equals(other.mapID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MapData=" + this.mapID + "]";
    }

    public String toFullString() {
        return new String(
                this.getMapID() + ","
                + this.getCreated() + ","
                + this.getModified() + ","
                + this.getDataName());

    }

    @PrePersist
    protected void onCreate() {
        created = modified = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        modified = new Date();
    }

    public void setOwner(User o) {

        this.owner = o;

    }

    public User getOwner() {
        return this.owner;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public void removeChildData(Data d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addChildData(Data d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDataType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Long getId() {
        return this.getMapID();
    }

    public void setId(Long id) {
        this.setMapID(id);
    }

    public Integer getNof() {
        return nofData;
    }

    public void setNof(Integer nof) {
        this.setNofData(nof);
    }

    public Integer getNofData() {
        return nofData;
    }

    public void setNofData(Integer nof) {
        Integer oldNofData = this.nofData;
        this.nofData = nof;
        changeSupport.firePropertyChange("nofData", oldNofData, nof);
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        Double oldMean = this.mean;
        this.mean = mean;
        changeSupport.firePropertyChange("mean", oldMean, mean);
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        Double oldMedian = this.median;
        this.median = median;
        changeSupport.firePropertyChange("median", oldMedian, median);
    }

    public Double getStddev() {
        return stddev;
    }

    public void setStddev(Double stddev) {
        Double oldStddev = this.stddev;
        this.stddev = stddev;
        changeSupport.firePropertyChange("stddev", oldStddev, stddev);
    }

    public Double getMinRatio() {
        return minRatio;
    }

    public void setMinRatio(Double minRatio) {
        Double oldMinRatio = this.minRatio;
        this.minRatio = minRatio;
        changeSupport.firePropertyChange("minRatio", oldMinRatio, minRatio);
    }

    public Double getMaxRatio() {
        return maxRatio;
    }

    public void setMaxRatio(Double maxRatio) {
        Double oldMaxRatio = this.maxRatio;
        this.maxRatio = maxRatio;
        changeSupport.firePropertyChange("maxRatio", oldMaxRatio, maxRatio);
    }

    public void setVariance(Double var) {
        Double oldVar = this.getVariance();
        this.variance = var;
        changeSupport.firePropertyChange("variance", oldVar, var);
    }

    public Double getVariance() {
        return variance;
    }

    public Data getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public StringBuffer getMetaText() {
        StringBuffer text = new StringBuffer();

        text.append(this.getDataName() + "\n");
        text.append(this.getDescription() + "\n");

        return text;
    }

    public void setGenomeRelease(GenomeRelease genomeRelease) {
        this.setGenomeRelease(genomeRelease.toString());
    }

    public void setParent(Data o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setName(String name) {
        this.setDataName(name);
    }

    public void setProcProcessing(String txt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setParamProcessing(String txt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getIconPath() {
        try {

            Region d = RegionLib.getRegionClazz(this.getClazz());
            return d.getIconPath();

        } catch (Exception ex) {
            Logger.getLogger(ExperimentData.class.getName()).log(Level.WARNING,
                    "", ex);
        }
        return null;
    }

    public void copy(Data d) {
        if (d instanceof MapData) {
            this.copy((MapData) d);
        } else {
            throw new RuntimeException("MapData.copy: no valid copy source "
                    + d.getClass().getName());
        }
    }

    public void setDataType(DataType r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        return this.getDataName();
    }

    public String getProcProcessing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getParamProcessing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
