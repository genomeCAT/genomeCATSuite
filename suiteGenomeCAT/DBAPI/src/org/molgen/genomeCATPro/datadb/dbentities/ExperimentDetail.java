package org.molgen.genomeCATPro.datadb.dbentities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/**
 * @name Experiment
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
/**
 * change log
 * 280212 copy copy samples
 *
 */
@Entity
@Table(name = "ExperimentDetail")
public class ExperimentDetail implements Serializable, DataNode {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final Long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experimentDetailID", nullable = false)
    private Long experimentDetailID;
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
    @Column(name = "nofChannel", nullable = false)
    private Integer nofChannel;
    @Column(name = "hybProtocoll")
    private String hybProtocoll;
    @Lob
    @Column(name = "processing")
    private String processing;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "method", nullable = false)
    private String method;

    public void copy(ExperimentDetail e) {
        //this.getArray().copy(e.getArray());

        this.setCreated(e.getCreated());
        this.setDescription(e.getDescription());
        this.setExperimentDetailID(e.getExperimentDetailID());
        this.setModified(e.getModified());
        this.setName(e.getName());
        this.setNofChannel(e.getNofChannel());

        this.setSummary(e.getSummary());
        this.setProcessing(e.getProcessing());
        this.setHybProtocoll(e.getHybProtocoll());
        this.setMethod(e.getMethod());
        this.setType(e.getType());
        this.getPlatform().copy(e.getPlatform());
        // 280212
        List<SampleInExperiment> list = new Vector<SampleInExperiment>(e.getSamples());
        Collections.copy(list, e.getSamples());
        this.setSamples(list);
        //
    }
    @ManyToOne
    @JoinColumn(name = "platformDetailID")
    private PlatformDetail platform;

    public PlatformDetail getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformDetail p) {
        PlatformDetail oldP = this.platform;
        this.platform = p;

        changeSupport.firePropertyChange("PlatformDetail", oldP, p);
    }
    /**
     * The table of the owning entity contains the foreign key.
     * 
     * The mappedby element specifies that the samples field is an inverse field 
     * rather than a persistent field. 
     * The content of the samples set is not stored as part of Experiment entities
     * The mappedBy element defines a bidirectional relationship. 
     * In a bidirectional relationship, the side that stores the data 
     * (the Sample class in our example) is the owner
     * Only changes to the owner side affects the database, 
     * since the other side is not stored and calculated by a query.
     * 
     */
    //
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "experiment")
    private List<SampleInExperiment> samples;

    public SampleInExperiment addSample(SampleDetail sample, boolean isCy3, boolean isCy5) {
        SampleInExperiment association = new SampleInExperiment();
        association.setSample(sample);
        association.setExperiment(this);
        association.setExperimentDetailID(this.getExperimentDetailID());
        association.setSampleDetailID(sample.getSampleDetailID());
        association.setIsCy3(isCy3);
        association.setIsCy5(isCy5);

        this.samples.add(association);
        // Also add the association object to the employee.
        //sample.getExperiments().add(association);
        //changeSupport.firePropertyChange("Samples", null, this.samples);
        return association;
    }

    public void removeSample(SampleInExperiment s) {
        int index = -1;
        for (SampleInExperiment sie : this.samples) {
            if (sie.equals(s)) {
                index = this.samples.indexOf(sie);
            }
        }
        if (index >= 0) {
            Logger.getLogger(ExperimentDetail.class.getName()).log(Level.INFO,
                    "remove: " + this.samples.get(index));
            this.samples.remove(index);
        }
    }

    public List<SampleInExperiment> getSamples() {
        return this.samples;
    }

    public void setSamples(List<SampleInExperiment> samples) {
        if (samples == this.samples) {
            return;
        }
        this.samples.clear();
        this.samples.addAll(samples);

    }
    /*
    @ManyToMany(mappedBy = "experiments")
    private List<Study> studies;
    
    public List<Study> getStudies() {
    return studies;
    }
    
    public void addStudy(Study study) {
    if (!getStudies().contains(study)) {
    getStudies().add(study);
    }
    if (!study.getExperiments().contains(this)) {
    study.getExperiments().add(this);
    }
    }
     */

    public ExperimentDetail() {
        this.platform = new PlatformDetail();
        this.samples = org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<SampleInExperiment>());
    //this.studies = new Vector<Study>();

    }

    public ExperimentDetail(String string) {
        this();
        this.name = string;


        this.setPlatform(new PlatformDetail("empty"));

    //this.studies.add(new Study("empty study"));
    }

    public ExperimentDetail(Long id) {
        this();
        this.experimentDetailID = id;
    }

    public ExperimentDetail(Long id, String name, Date modified, Date created,
            String originalFile, int nofChannel, PlatformDetail p) {
        this.experimentDetailID = id;
        this.name = name;
        this.modified = modified;
        this.created = created;
        this.nofChannel = nofChannel;
        this.platform = p;
    }

    public Long getExperimentDetailID() {
        return experimentDetailID;
    }

    public void setExperimentDetailID(Long experimentDetailID) {
        Long oldId = this.experimentDetailID;
        this.experimentDetailID = experimentDetailID;
        changeSupport.firePropertyChange("experimentDetailID", oldId, experimentDetailID);
        for (SampleInExperiment sie : this.getSamples()) {
            sie.setExperimentDetailID(this.getExperimentDetailID());
        }
    }

    public List<ExperimentData> getDataList() {
        List<ExperimentData> l = ExperimentService.listTopLevelExperimentData(this);
        return (l == null || l == Collections.EMPTY_LIST ? new Vector<ExperimentData>() : l);
    }

    @SuppressWarnings("empty-statement")
    public void setDataList(List<ExperimentData> d) {
        ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public String getMethod() {
        return method;
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

    public String getHybProtocoll() {
        return this.hybProtocoll;
    }

    public void setHybProtocoll(String protocoll) {
        String oldProtocoll = this.hybProtocoll;
        this.hybProtocoll = protocoll;
        changeSupport.firePropertyChange("hybProtocoll", oldProtocoll, protocoll);
    }

    public String getProcessing() {
        return processing;
    }

    public void setProcessing(String processing) {
        String oldProcessing = this.processing;
        this.processing = processing;
        changeSupport.firePropertyChange("preprocessing", oldProcessing, processing);
    }

    public Integer getNofChannel() {
        return nofChannel;
    }

    public void setNofChannel(Integer nofChannel) {
        Integer oldNofChannel = this.nofChannel;
        this.nofChannel = nofChannel;
        changeSupport.firePropertyChange("nofChannel", oldNofChannel, nofChannel);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.experimentDetailID != null ? experimentDetailID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExperimentDetail)) {
            return false;
        }
        ExperimentDetail other = (ExperimentDetail) object;
        if ((this.experimentDetailID == null && other.experimentDetailID != null) || (this.experimentDetailID != null && !this.experimentDetailID.equals(other.experimentDetailID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ExperimentDetail[id=" + experimentDetailID + "]";
    }

    public static boolean validateExperimentArray(ExperimentDetail ex, List<PlatformDetail> arrayList) {
        if (!arrayList.contains(ex.getPlatform())) {
            String msg = "Experiment has different platform type than file ";
            Logger.getLogger(ExperimentDetail.class.getName()).log(Level.INFO, msg);

            return false;
        }
        return true;
    }

    @PrePersist
    protected void onCreate() {
        created = modified = new Date();
        if (this.getName() == null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            java.util.Date date = new java.util.Date();
            this.setName("Experiment_" + dateFormat.format(date));
        }
        Logger.getLogger(ExperimentDetail.class.getName()).log(Level.INFO, "create: " + this.toString());
    }

    @PreUpdate
    protected void onUpdate() {
        modified = new Date();
        Logger.getLogger(ExperimentDetail.class.getName()).log(Level.INFO, "update: " + this.toString());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public String toFullString() {
        return new String(
                this.getExperimentDetailID() + "," +
                this.getName() + "," +
                this.getDescription() + "," +
                this.getMethod() + "," +
                this.getType());
    }

    public void addExperimentData(ExperimentData d) {
        if (d.getExperiment() != null) {
            if (d.getExperiment() != this) {
                d.getExperiment().removeExperimentData(d);
            }
        }
        d.setExperiment(this);
        Logger.getLogger(ExperimentDetail.class.getName()).log(Level.INFO, "addExperiment" +
                this.toFullString());

        changeSupport.firePropertyChange(ExperimentDetail.ADD, this, null);
    }

    public void removeExperimentData(ExperimentData d) {

        Logger.getLogger(ExperimentDetail.class.getName()).log(Level.INFO, "removeExperiment" +
                this.toFullString());
        changeSupport.firePropertyChange(ExperimentDetail.REMOVE, this, null);
    }

    public void addChildData(Data d) {
        if (d instanceof ExperimentData) {
            this.addExperimentData((ExperimentData) d);
        }
    }
    public final static String ICON_PATH = "org/molgen/genomeCATPro/data/folder_page_16.png";

    public String getIconPath() {
        return ExperimentDetail.ICON_PATH;
    }

    public Long getId() {
        return this.getExperimentDetailID();
    }

    public User getOwner() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeChildData(Data d) {
        if (d instanceof ExperimentData) {
            this.removeExperimentData((ExperimentData) d);
        }
    }

    public void setId(Long id) {
        this.setExperimentDetailID(experimentDetailID);
    }

    public void setOwner(User o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
