package org.molgen.genomeCATPro.datadb.dbentities;

/**
 * @name SampleDetail
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * 270313   kt  copy constructor added
 */
@Entity
@Table(name = "SampleDetail")
public class SampleDetail {

    private static final Long serialVersionUID = 1L;
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sampleDetailID", nullable = false)
    private Long sampleDetailID;
    @Column(name = "name", nullable = false)
    private String name;
    @Lob
    @Column(name = "source", nullable = false)
    private String source;
    @Lob
    @Column(name = "organism", nullable = false)
    private String organism;
    @Lob
    @Column(name = "characteristics", nullable = false)
    private String characteristics;
    @Lob
    @Column(name = "treatment")
    private String treatment;
    @Column(name = "molecule", nullable = false)
    private String molecule;
    @Lob
    @Column(name = "phenotype", nullable = false)
    private String phenotype = "";
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public SampleDetail() {

        //this.experiments = new Vector<SampleInExperiment>();
    }

    public SampleDetail(Long id) {

        this();
        this.setSampleDetailID(id);

    }

    public SampleDetail(SampleDetail s) {
        // 270313   kt
        this.setName(s.getName());
        this.setCharacteristics((s.getCharacteristics()));
        this.setPhenotype(s.getPhenotype());
        this.setSource(s.getSource());
        this.setTreatment(s.getTreatment());
        this.setMolecule(s.getMolecule());
        this.setOrganism(s.getOrganism());
        this.setSampleDetailID(sampleDetailID);

    }

    public void copy(SampleDetail s) {
        this.setName(s.getName());
        this.setCharacteristics((s.getCharacteristics()));
        this.setPhenotype(s.getPhenotype());
        this.setSource(s.getSource());
        this.setTreatment(s.getTreatment());
        this.setMolecule(s.getMolecule());
        this.setOrganism(s.getOrganism());
    //this.setExperiments(s.getExperiments());

    }

    public Long getSampleDetailID() {
        return sampleDetailID;
    }

    public void setSampleDetailID(Long sampleDetailID) {
        Long old = this.sampleDetailID;
        this.sampleDetailID = sampleDetailID;
        changeSupport.firePropertyChange("sampleDetailID", old, sampleDetailID);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        String oldSource = this.source;
        this.source = source;
        changeSupport.firePropertyChange("source", oldSource, source);
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        String oldOrganism = this.organism;
        this.organism = organism;
        changeSupport.firePropertyChange("organism", oldOrganism, organism);
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        String oldCharacteristics = this.characteristics;
        this.characteristics = characteristics;
        changeSupport.firePropertyChange(
                "characteristics", oldCharacteristics, characteristics);
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        String oldTreatment = this.treatment;
        this.treatment = treatment;
        changeSupport.firePropertyChange("treatmen", oldTreatment, treatment);
    }

    public String getMolecule() {
        return molecule;
    }

    public void setMolecule(String molecule) {
        String oldMolecule = this.molecule;
        this.molecule = molecule;
        changeSupport.firePropertyChange("molecule", oldMolecule, molecule);
    }

    public String getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(String phenotype) {
        String old = this.phenotype;
        this.phenotype = phenotype;
        changeSupport.firePropertyChange("phenotype", old, phenotype);
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
        hash += (this.sampleDetailID != null ? this.sampleDetailID.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SampleDetail)) {
            return false;
        }
        SampleDetail other = (SampleDetail) object;
        if ((this.sampleDetailID == null && other.sampleDetailID != null) ||
                (this.sampleDetailID != null &&
                !this.sampleDetailID.equals(other.sampleDetailID))) {
            return false;
        }
        return true;
    }

    public String toFullString() {
        return new String(
                this.getSampleDetailID() + "," +
                this.getName() + "," +
                this.getPhenotype() + "," +
                this.getSource() + "," +
                this.getOrganism() + "," +
                this.getCharacteristics() + "," +
                this.getTreatment() + "," +
                this.getMolecule() + "," +
                this.getCreated() + "," +
                this.getModified());
    }

    @PrePersist
    protected void onCreate() {
        this.setCreated(new Date());
        this.setModified(this.getCreated());

        Logger.getLogger(SampleDetail.class.getName()).log(Level.INFO, "create: " + this.toString());
    }

    @PreUpdate
    protected void onUpdate() {
        this.setModified(new Date());
        Logger.getLogger(SampleDetail.class.getName()).log(Level.INFO, "update: " + this.toString());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}


