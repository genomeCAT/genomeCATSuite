package org.molgen.genomeCATPro.datadb.dbentities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
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
public class MapDetail implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapName", nullable = false)
    private String mapName;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "mapType")
    private String mapType;
    @Column(name = "description")
    private String description;
    @Column(name = "genomeRelease", nullable = false)
    private String genomeRelease;
    @Column(name = "tableData", nullable = false)
    private String tableData;

    public MapDetail() {
    }

    public MapDetail(String mapId, String type, String release, String desc) {
        this();
        this.mapName = mapId;
        this.mapType = type;
        this.genomeRelease = release;
        this.description = desc;
    }

    /*
     * copy - useful for GUI Entities with field attached listeners
     */
    public void copy(MapDetail a) {
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
        hash += (this.mapName != null ? this.mapName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MapDetail)) {
            return false;
        }
        MapDetail other = (MapDetail) object;
        if ((this.mapName == null && other.mapName != null) || (this.mapName != null && !this.mapName.equals(other.mapName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MapDetail=" + this.mapName + "]";
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
        if (this.tableData == null) {
            this.initTableData();
        }
        return tableData;
    }

    public void initTableData() {
        if (this.getMapName() == null) {
            return;
        }
        this.setTableData(Utils.getUniquableName(this.getMapName()) + "_Mapping");
        this.setTableData(this.getTableData().replace("-", "_"));
        this.setTableData(this.getTableData().replace(" ", "_"));
    }

    public void setTableData(String tableData) {
        String old = this.tableData;
        this.tableData = tableData;
        changeSupport.firePropertyChange("tableData", old, tableData);
    }

    public String toFullString() {
        return new String(
                this.getMapName() + ","
                + this.getCreated() + ","
                + this.getModified() + ","
                + this.getMapName() + ","
                + this.getMapType() + ","
                + this.getGenomeRelease());
    }

    @PrePersist
    protected void onCreate() {
        created = modified = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        modified = new Date();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
