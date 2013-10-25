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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * @name PlatformDetail
 * technical attributes of a platform 
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package.
 * Copyright Oct 6, 2010 Katrin Tebel <tebel at molgen.mpg.de>.
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
@Table(name = "PlatformDetail")
@NamedQueries({})
public class PlatformDetail implements Serializable {

    final static String ADD = "add";
    final static String REMOVE = "remove";
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platformID", nullable = false)
    private Long platformID;
    @Column(name = "titel", nullable = false)
    private String titel = "";
    @Column(name = "name", nullable = false)
    private String name;
    @Lob
    @Column(name = "description")
    private String description;
    @Column(name = "method", nullable = false)
    private String method;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "manufacturer", nullable = false)
    private String manufacturer = "";
    @Column(name = "catalog_number", nullable = false)
    private String catalogNumber = "";
    @Column(name = "organism", nullable = false)
    private String organism = "";
    @Column(name = "technology", nullable = false)
    private String technology = "";
    @Lob
    @Column(name = "manufacture_protocol")
    private String manufactureProtocol;
    @Lob
    @Column(name = "web_link")
    private String webLink;
    @Column(name = "support")
    private String support;
    @Column(name = "coating")
    private String coating;
    @Column(name = "distribution")
    private String distribution;

    public PlatformDetail() {
        //his.created = new Date();
        //his.modified = new Date();
    }

    public PlatformDetail(Long platformID) {
        this();
        this.platformID = platformID;

    }

    PlatformDetail(String name) {
        this();
        this.name = name;
    }

    public void copy(PlatformDetail plattform) {

        this.setPlatformID(plattform.platformID);
        this.setName(plattform.name);
        this.setMethod(plattform.method);
        this.setType(plattform.type);
        this.setTitel(plattform.titel);
        this.setTechnology(plattform.technology);
        this.setDescription(plattform.description);
        this.setDistribution(plattform.distribution);
        this.setOrganism(plattform.organism);
        this.setCoating(plattform.coating);
        this.setCatalogNumber(plattform.catalogNumber);
        this.setManufactureProtocol(plattform.manufactureProtocol);
        this.setManufacturer(plattform.manufacturer);
        this.setSupport(plattform.support);
        this.setCreated(plattform.created);
        this.setModified(plattform.modified);
        this.setWebLink(plattform.webLink);
    }

    public Long getPlatformID() {
        return platformID;
    }

    public void setPlatformID(Long platformID) {
        Long oldplatformID = this.platformID;
        this.platformID = platformID;
        changeSupport.firePropertyChange("platformID", oldplatformID, platformID);
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        String oldName = this.name;
        this.name = _name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public String getMethod() {
        return method;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        String oldName = this.catalogNumber;
        this.catalogNumber = catalogNumber;
        changeSupport.firePropertyChange("catalogNumber", oldName, catalogNumber);
    }

    public void setMethod(String method) {
        String oldMethod = this.method;
        this.method = method;
        changeSupport.firePropertyChange("method", oldMethod, method);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        String oldType = this.type;
        this.type = type;
        changeSupport.firePropertyChange("type", oldType, type);
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        String oldTitel = this.titel;
        this.titel = titel;
        changeSupport.firePropertyChange("titel", oldTitel, titel);
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        String oldManufacturer = this.manufacturer;
        this.manufacturer = manufacturer;
        changeSupport.firePropertyChange("manufacturer", oldManufacturer, manufacturer);
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        String oldOrganism = this.organism;
        this.organism = organism;
        changeSupport.firePropertyChange("organism", oldOrganism, organism);
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        String oldTechnology = this.technology;
        this.technology = technology;
        changeSupport.firePropertyChange("technology", oldTechnology, technology);
    }

    public String getManufactureProtocol() {
        return manufactureProtocol;
    }

    public void setManufactureProtocol(String manufactureProtocol) {
        String oldManufactureProtocol = this.manufactureProtocol;
        this.manufactureProtocol = manufactureProtocol;
        changeSupport.firePropertyChange("manufactureProtocol", oldManufactureProtocol, manufactureProtocol);
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        String oldWebLink = this.webLink;
        this.webLink = webLink;
        changeSupport.firePropertyChange("webLink", oldWebLink, webLink);
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        String oldSupport = this.support;
        this.support = support;
        changeSupport.firePropertyChange("support", oldSupport, support);
    }

    public String getCoating() {
        return coating;
    }

    public void setCoating(String coating) {
        String oldCoating = this.coating;
        this.coating = coating;
        changeSupport.firePropertyChange("coating", oldCoating, coating);
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        String oldDistribution = this.distribution;
        this.distribution = distribution;
        changeSupport.firePropertyChange("distribution", oldDistribution, distribution);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (platformID != null ? platformID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlatformDetail)) {
            return false;
        }
        PlatformDetail other = (PlatformDetail) object;
        if ((this.platformID == null && other.platformID != null) || (this.platformID != null && !this.platformID.equals(other.platformID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Plattform[platformID=" + platformID + "]";
    }

    public String toFullString() {
        return new String(
                this.getPlatformID() + "," +
                this.getName() + "," +
                this.getTitel() + "," +
                this.getType() + "," +
                this.getMethod() + "," +
                this.getTechnology() + "," +
                this.getDescription() + "," +
                this.getDistribution() + "," +
                this.getOrganism() + "," +
                this.getCoating() + "," +
                this.getManufactureProtocol() + "," +
                this.getManufacturer() + "," +
                this.getSupport() + "," +
                this.getCreated() + "," +
                this.getModified() + "," +
                this.getWebLink());
    }

    @PrePersist
    protected void onCreate() {
        this.setCreated(new Date());
        this.setModified(this.getCreated());

        Logger.getLogger(PlatformDetail.class.getName()).log(Level.INFO, "create: " + this.toString());
    }

    @PreUpdate
    protected void onUpdate() {
        this.setModified(new Date());
        Logger.getLogger(PlatformDetail.class.getName()).log(Level.INFO, "update: " + this.toString());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public void addPlatformData(PlatformData d) {
        if (d.getPlattform() != null) {
            if (d.getPlattform() != this) {
                d.getPlattform().removePlatformData(d);
            }
        }
        d.setPlatform(this);
        Logger.getLogger(PlatformDetail.class.getName()).log(Level.INFO, "addPlatform" +
                this.toFullString());

        changeSupport.firePropertyChange(PlatformDetail.ADD, this, null);
    }

    public void removePlatformData(PlatformData d) {

        Logger.getLogger(PlatformDetail.class.getName()).log(Level.INFO, "removePlatform" +
                this.toFullString());
        changeSupport.firePropertyChange(PlatformDetail.REMOVE, this, null);
    }
}
