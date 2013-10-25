/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.datadb.dbentities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.persistence.CascadeType;
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
@IdClass(ExperimentAtStudyID.class)
@Entity
@Table(name = "ExperimentAtStudy")
public class ExperimentAtStudy {
    /*With the @IdClass annotation, you don't declare an instance variable of 
    type CompoundKey, but instead, just define instance variables for each of 
    the primary key fields. You then mark the corresponding getter tags with 
    standard @Id annotations. 
    http://jpa.ezhibernate.com/Javacode/learn.jsp?tutorial=15usingcompoundprimarykeys
     */

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    @Id
    public Long experimentDetailID = new Long(0);
    @Id
    public Long studyID = new Long(0);
    
    @ManyToOne()
    @JoinColumn(name = "experimentDetailID", insertable = false, updatable = false, nullable = false)
    //@PrimaryKeyJoinColumn(name = "experimentDetailID", referencedColumnName = "experimentDetailID")
    /* if this JPA model doesn't create a table for the "PROJ_EMP" entity,
     *  please comment out the @PrimaryKeyJoinColumn, and use the ff:
     *  @JoinColumn(name = "employeeId", updatable = false, insertable = false)
     * or @JoinColumn(name = "employeeId", updatable = false, insertable = false, referencedColumnName = "id")
     */
    private ExperimentDetail experiment;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "studyID", insertable = false, updatable = false, nullable = false)
    //@PrimaryKeyJoinColumn(name = "sampleDetailID",   referencedColumnName = "sampleDetailID")
    /* the same goes here:
     *  if this JPA model doesn't create a table for the "PROJ_EMP" entity,
     *  please comment out the @PrimaryKeyJoinColumn, and use the ff:
     *  @JoinColumn(name = "projectId", updatable = false, insertable = false)
     * or @JoinColumn(name = "projectId", updatable = false, insertable = false, referencedColumnName = "id")
     */
    private Study study;

    public ExperimentAtStudy() {
       
    }

    public Long getExperimentDetailID() {
        return experimentDetailID;
    }

    public void setExperimentDetailID(Long experimentDetailID) {
        this.experimentDetailID = experimentDetailID;
    }

    public Long getStudyID() {
        return this.studyID;
    }

    public void setStudyID(Long studyID) {
        this.studyID = studyID;
    }

    public ExperimentDetail getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentDetail experiment) {
        this.experiment = experiment;
        this.experimentDetailID = this.experiment.getExperimentDetailID();
    }

    

    

    

    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study s) {
        this.study  = s;
        this.studyID = s.getStudyID();
    }

    

    public String toFullString() {
        return new String(
                this.getStudyID() + ":" +
                (this.getStudy()!= null ? this.getStudy().getName() : "") + "," +
                this.getExperimentDetailID() + "," +
                (this.getExperiment() != null ? this.getExperiment().getName() : "") 
                );
    }
     public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
