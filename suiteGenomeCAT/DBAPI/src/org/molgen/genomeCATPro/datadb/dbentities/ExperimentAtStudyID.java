/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.datadb.dbentities;

import java.io.Serializable;

/**
 *
 * @author tebel
 */
public class ExperimentAtStudyID implements Serializable {

   
    public Long experimentDetailID;
    public Long studyID;

    public ExperimentAtStudyID() {
    }

    public ExperimentAtStudyID(Long experimentDetailID, Long studyID) {
        this.experimentDetailID = experimentDetailID;
        this.studyID = studyID;
}
    @Override
    public boolean equals(Object object) {
        if (object instanceof ExperimentAtStudyID) {
            ExperimentAtStudyID otherId = (ExperimentAtStudyID) object;
            return (this.experimentDetailID.equals(otherId.experimentDetailID) && (this.studyID.equals(otherId.studyID)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.studyID != null ? this.studyID.hashCode() : 0);
        hash += (this.experimentDetailID != null ? this.experimentDetailID.hashCode() : 0);
        return hash;
    }

    
}
