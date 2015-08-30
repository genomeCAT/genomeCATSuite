/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.datadb.dbentities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author tebel
 */
@IdClass(SampleInTrackID.class)
@Entity
@Table(name = "SampleInTrack")
public class SampleInTrack {
    /*With the @IdClass annotation, you don't declare an instance variable of 
    type CompoundKey, but instead, just define instance variables for each of 
    the primary key fields. You then mark the corresponding getter tags with 
    standard @Id annotations. 
    http://jpa.ezhibernate.com/Javacode/learn.jsp?tutorial=15usingcompoundprimarykeys
     */

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    @Id
    public Long trackID = new Long(0);
    @Id
    public Long sampleDetailID = new Long(0);
    @Column(name = "inverse")
    private boolean inverse = false;
    @ManyToOne()
    @JoinColumn(name = "trackID", insertable = false, updatable = false, nullable = false)
    //@PrimaryKeyJoinColumn(name = "experimentDetailID", referencedColumnName = "experimentDetailID")
    /* if this JPA model doesn't create a table for the "PROJ_EMP" entity,
     *  please comment out the @PrimaryKeyJoinColumn, and use the ff:
     *  @JoinColumn(name = "employeeId", updatable = false, insertable = false)
     * or @JoinColumn(name = "employeeId", updatable = false, insertable = false, referencedColumnName = "id")
     */
    private Track track;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "sampleDetailID", insertable = false, updatable = false, nullable = false)
    //@PrimaryKeyJoinColumn(name = "sampleDetailID",   referencedColumnName = "sampleDetailID")
    /* the same goes here:
     *  if this JPA model doesn't create a table for the "PROJ_EMP" entity,
     *  please comment out the @PrimaryKeyJoinColumn, and use the ff:
     *  @JoinColumn(name = "projectId", updatable = false, insertable = false)
     * or @JoinColumn(name = "projectId", updatable = false, insertable = false, referencedColumnName = "id")
     */
    private SampleDetail sample;

    public SampleInTrack() {
        this.sample = new SampleDetail();
    }

    public Long getTrackID() {
        return this.trackID;
    }

    public void setTrackID(Long trackID) {
        this.trackID = trackID;
    }

    public Long getSampleDetailID() {
        return sampleDetailID;
    }

    public void setSampleDetailID(Long sampleDetailID) {
        this.sampleDetailID = sampleDetailID;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
        this.trackID = this.track.getTrackID();
    }

    public boolean isInverse() {
        return this.inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    public SampleDetail getSample() {
        return sample;
    }

    public void setSample(SampleDetail sample) {
        this.sample = sample;
        this.sampleDetailID = sample.getSampleDetailID();
    }

    public String getName() {
        return this.sample.getName();
    }

    public void setName(String name) {
        String oldName = this.getName();
        this.sample.setName(name);
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public String getSource() {
        return this.sample.getSource();
    }

    public void setSource(String source) {
        String oldSource = this.getSource();
        this.sample.setSource(source);
        changeSupport.firePropertyChange("source", oldSource, source);
    }

    public String getOrganism() {
        return this.sample.getOrganism();
    }

    public void setOrganism(String organism) {
        String oldOrganism = this.getOrganism();
        this.sample.setOrganism(organism);
        changeSupport.firePropertyChange("organism", oldOrganism, organism);
    }

    public String getCharacteristics() {
        return this.sample.getCharacteristics();
    }

    public void setCharacteristics(String characteristics) {
        String oldCharacteristics = this.sample.getCharacteristics();
        this.sample.setCharacteristics(characteristics);
        changeSupport.firePropertyChange(
                "characteristics", oldCharacteristics, characteristics);
    }

    public String getTreatment() {
        return this.sample.getTreatment();
    }

    public void setTreatment(String treatment) {
        String oldTreatment = this.getTreatment();
        this.sample.setTreatment(treatment);
        changeSupport.firePropertyChange("treatmen", oldTreatment, treatment);
    }

    public String getMolecule() {
        return this.sample.getMolecule();
    }

    public void setMolecule(String molecule) {
        String oldMolecule = this.getMolecule();
        this.sample.setMolecule(molecule);
        changeSupport.firePropertyChange("molecule", oldMolecule, molecule);
    }

    public String getPhenotype() {
        return this.sample.getPhenotype();
    }

    public void setPhenotype(String phenotype) {
        String old = this.getPhenotype();
        this.sample.getPhenotype();
        changeSupport.firePropertyChange("phenotype", old, phenotype);
    }

    public String toFullString() {
        return new String(
                this.getSampleDetailID() + ":" +
                (this.getSample() != null ? this.getSample().getName() : "") + "," +
                this.getTrackID() + "," +
                (this.getTrack() != null ? this.getTrack().getName() : "") + "," +
                (this.inverse ? " x " : " 1/x"));
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
