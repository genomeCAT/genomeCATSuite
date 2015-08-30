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
public class SampleInExperimentID implements Serializable {

   
    public Long experimentDetailID;
    public Long sampleDetailID;

    public SampleInExperimentID() {
    }

    public SampleInExperimentID(Long experimentDetailID, Long sampleDetailID) {
        this.experimentDetailID = experimentDetailID;
        this.sampleDetailID = sampleDetailID;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SampleInExperimentID) {
            SampleInExperimentID otherId = (SampleInExperimentID) object;
            return (this.experimentDetailID.equals(otherId.experimentDetailID) && (this.sampleDetailID.equals(otherId.sampleDetailID)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.sampleDetailID != null ? this.sampleDetailID.hashCode() : 0);
        hash += (this.experimentDetailID != null ? this.experimentDetailID.hashCode() : 0);
        return hash;
    }

    
}
