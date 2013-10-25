package org.molgen.genomeCATPro.datadb.dbentities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * @name User
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package.
 * Copyright Aug 24, 2010 Katrin Tebel <tebel at molgen.mpg.de>.
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
@Entity
@Table(name = "User")
public class User implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "UserID", nullable = false)
    private Long idUser;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "isAdmin", nullable = false)
    private boolean isAdmin;
    @Column(name = "modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @OneToMany(mappedBy = "owner")
    @OrderBy("name ASC")
    private List<Track> tracks;
    

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track t) {
        if (!getTracks().contains(t)) {
            getTracks().add(t);
            if (t.getOwner() != null) {
                t.getOwner().getTracks().remove(t);
            }
            t.setOwner(this);
        }
    }
    
    @OneToMany(mappedBy = "owner")
    @OrderBy("name ASC")
    private List<ExperimentData> samples;
    

    public List<ExperimentData> getSamples() {
        return samples;
    }

    public void addSample(ExperimentData s) {
        if (!getSamples().contains(s)) {
            getSamples().add(s);
            if (s.getOwner() != null) {
                s.getOwner().getSamples().remove(s);
            }
            s.setOwner(this);
        }
    }
    @OneToMany(mappedBy = "owner")
    @OrderBy("name ASC")
    private List<Study> studies;

    public List<Study> getStudies() {
        return studies;
    }

    public void addStudy(Study s) {
        if (!getStudies().contains(s)) {
            getStudies().add(s);
            if (s.getOwner() != null) {
                s.getOwner().getStudies().remove(s);
            }
            s.setOwner(this);
        }
    }

    public User() {
    }

    public User(Long idUser) {
        this.idUser = idUser;
    }

    public User(Long idUser, String name, String password, boolean isAdmin, Date modified, Date created) {
        this.idUser = idUser;
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
        this.modified = modified;
        this.created = created;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        Long oldIdUser = this.idUser;
        this.idUser = idUser;
        changeSupport.firePropertyChange("idUser", oldIdUser, idUser);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String oldPassword = this.password;
        this.password = password;
        changeSupport.firePropertyChange("password", oldPassword, password);
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        boolean oldIsAdmin = this.isAdmin;
        this.isAdmin = isAdmin;
        changeSupport.firePropertyChange("isAdmin", oldIsAdmin, isAdmin);
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
        hash += (idUser != null ? idUser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.idUser == null && other.idUser != null) || (this.idUser != null && !this.idUser.equals(other.idUser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.User[idUser=" + idUser + "]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
