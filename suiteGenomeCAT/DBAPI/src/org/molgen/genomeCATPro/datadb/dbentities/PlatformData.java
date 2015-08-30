package org.molgen.genomeCATPro.datadb.dbentities;

/**
 * @name PlatformData
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.common.Utils;

/**
 * 260313   kt  new transient member: nofImportData
 * 130313   kt  new transient member: nofImportErrors
 * 120313   kt  unique init tabledata
 */
@Entity
@Table(name = "PlatformList")
@NamedQueries({})
public class PlatformData implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platformListID", nullable = false)
    private Long platformListID = null;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "clazz")
    private String clazz;
    @Column(name = "genomeRelease", nullable = false)
    private String genomeRelease;
    @Column(name = "tableData", nullable = false)
    private String tableData;
    @Column(name = "nofSpots", nullable = false)
    private int nofSpots;
    @Transient
    private int nofImportErrors = 0;
    @Transient
    private int nofImportData = 0;
    @Lob
    @Column(name = "originalFile", nullable = false)
    private String originalFile;
    @ManyToOne(fetch = FetchType.LAZY)
    //@ManyToOne
    @JoinColumn(name = "platformDetailID")
    private PlatformDetail platform = null;

    public PlatformData() {
        this.platform = new PlatformDetail();
    }

    public PlatformData(Long ID) {
        this();
        this.platformListID = ID;
    }

    /*
     * copy - useful for GUI Entities with field attached listeners
     */
    public void copy(PlatformData a) {
        this.setPlatformListID(a.platformListID);
        this.setName(a.name);
        this.setCreated(a.getCreated());
        this.setGenomeRelease(a.getGenomeRelease());
        this.setModified(a.getModified());
        this.setPlatform(a.getPlattform()); // copy??

        this.setClazz(a.getClazz());
        this.setOriginalFile(a.originalFile);
        this.setNofSpots(a.nofSpots);
        this.setTableData(a.tableData);
    }

    public Long getPlatformListID() {
        return platformListID;
    }

    public void initTableData() {
        if (this.getName() == null) {
            return;
        }
        this.setTableData(Utils.getUniquableName(this.getName()) + "_" +
                (this.genomeRelease != null ? GenomeRelease.toRelease(genomeRelease).toShortString() : "") + "_Spots");
        this.setTableData(this.getTableData().replace("-", "_"));
        this.setTableData(this.getTableData().replace(" ", "_"));
    }

    public void setPlatformListID(Long platformListID) {
        Long old = this.platformListID;
        this.platformListID = platformListID;
        changeSupport.firePropertyChange("platformListID", old, platformListID);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", old, name);
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

    public String getGenomeRelease() {
        return genomeRelease;
    }

    public void setGenomeRelease(String genomeRelease) {
        String oldRelease = this.genomeRelease;
        this.genomeRelease = genomeRelease;
        changeSupport.firePropertyChange("genomeRelease", oldRelease, genomeRelease);
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setPlatform(PlatformDetail plattform) {
        PlatformDetail oldPlattform = this.platform;
        this.platform = plattform;

        //this.setName(this.platform.getName());
        changeSupport.firePropertyChange("platformDetail", oldPlattform, plattform);
    }

    public int getNofSpots() {
        return nofSpots;
    }

    public void setNofSpots(int nofSpots) {
        int oldI = this.nofSpots;
        this.nofSpots = nofSpots;
        changeSupport.firePropertyChange("nofSpots", oldI, nofSpots);

    }

    public int getNofImportErrors() {
        return nofImportErrors;
    }

    public int getNofImportData() {
        return nofImportData;
    }

    public void setNofImportData(int nofImportData) {
        this.nofImportData = nofImportData;
    }

    public void setNofImportErrors(int nofImportErrors) {
        this.nofImportErrors = nofImportErrors;
    }

    public PlatformDetail getPlattform() {
        return this.platform;
    }

    public String getTableData() {
        if (this.tableData == null) {
            this.initTableData();
        }
        return tableData;
    }

    public void setTableData(String tableData) {
        String old = this.tableData;
        this.tableData = tableData;
        changeSupport.firePropertyChange("tableData", old, tableData);
    }

    public String getOriginalFile() {

        return originalFile;
    }

    public void setOriginalFile(String originalFile) {
        String oldFile = this.originalFile;
        this.originalFile = originalFile;
        changeSupport.firePropertyChange("originalFile", oldFile, originalFile);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.platformListID != null ? this.platformListID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlatformData)) {
            return false;
        }
        PlatformData other = (PlatformData) object;
        if ((this.platformListID == null && other.platformListID != null) || (this.platformListID != null && !this.platformListID.equals(other.platformListID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PlatformList=" + this.platformListID + "]";
    }

    public String toFullString() {
        return new String(
                this.getPlatformListID() + "," +
                this.getCreated() + "," +
                this.getModified() + "," +
                this.getPlattform() + "," +
                this.getTableData() + "," +
                this.getGenomeRelease());
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
