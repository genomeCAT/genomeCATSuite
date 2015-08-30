package org.molgen.genomeCATPro.datadb.dbentities;
/**
 * @name Study
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.datadb.service.ProjectService;


@Entity
@Table(name = "Study")
public class Study implements Serializable, DataNode {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "StudyID", nullable = false)
    private Long studyID;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "summary")
    private String summary;
    @Lob
    @Column(name = "description")
    private String description;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOwner")
    private User owner;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        User oldOwner = this.owner;
        this.owner = owner;
        changeSupport.firePropertyChange("Owner", oldOwner, owner);
    }
    // @ManyToMany(mappedBy="experiments")
    //Technically the database will be updated correctly if you only 
    //add/remove from the owning side of the relationship

    List<ExperimentDetail> getExperimentList() {
        List<ExperimentDetail> l = ProjectService.listExperimentsForProject(this);
        return (l == null || l == Collections.EMPTY_LIST ? new Vector<ExperimentDetail>() : l);
    }

    public List<Track> getTrackList() {
        List<Track> l = ProjectService.listTracksForProject(this);
        return (l == null || l == Collections.EMPTY_LIST ? new Vector<Track>() : l);
    }

    public Study() {
    }

    Study(String name) {
        this();
        this.setName(name);
    }

    public Study(Long id) {
        this.studyID = id;
    }

    public Study(Long id, String name, Date modified, Date created, User owner) {
        this.studyID = id;
        this.name = name;
        this.modified = modified;
        this.created = created;
        this.setOwner(owner);
    }

    public Long getStudyID() {
        return studyID;
    }

    public void setStudyID(Long id) {
        Long oldId = this.studyID;
        this.studyID = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        String oldSummary = this.summary;
        this.summary = summary;
        changeSupport.firePropertyChange("summary", oldSummary, summary);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        changeSupport.firePropertyChange("description", oldDescription, description);
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        Date oldModified = this.modified;
        this.modified = modified;
        changeSupport.firePropertyChange("modified", oldModified, modified);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        Date oldCreated = this.created;
        this.created = created;
        changeSupport.firePropertyChange("created", oldCreated, created);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyID != null ? studyID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the studyID fields are not set
        if (!(object instanceof Study)) {
            return false;
        }
        Study other = (Study) object;
        if ((this.studyID == null && other.studyID != null) || (this.studyID != null && !this.studyID.equals(other.studyID))) {
            return false;
        }
        return true;
    }

    @PrePersist
    protected void onCreate() {
        created = modified = new Date();

        Logger.getLogger(Study.class.getName()).log(Level.INFO,
                "create: " + this.toString());
    }

    @PreUpdate
    protected void onUpdate() {
        modified = new Date();
        Logger.getLogger(Study.class.getName()).log(Level.INFO, "update: " + this.toString());
    }

    @Override
    public String toString() {
        return "entities.Study[id=" + studyID + "]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public String toFullString() {
        return new String("id: " + this.studyID + " name: " + name);
    }

    public void addChildData(Data d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getIconPath() {
        return Study.ICON_PATH;
    }

    public Long getId() {
        return this.getStudyID();
    }

    public void removeChildData(Data d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setId(Long id) {
        this.setStudyID(id);
    }
    public final static String ICON_PATH = "org/molgen/genomeCATPro/data/book_16.png";
}
