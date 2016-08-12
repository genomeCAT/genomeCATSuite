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
public class SampleInTrackID implements Serializable {

    public Long trackID;
    public Long sampleDetailID;

    public SampleInTrackID() {
    }

    public SampleInTrackID(Long trackID, Long sampleDetailID) {
        this.trackID = trackID;
        this.sampleDetailID = sampleDetailID;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SampleInTrackID) {
            SampleInTrackID otherId = (SampleInTrackID) object;
            return (this.trackID.equals(otherId.trackID) && (this.sampleDetailID.equals(otherId.sampleDetailID)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.sampleDetailID != null ? this.sampleDetailID.hashCode() : 0);
        hash += (this.trackID != null ? this.trackID.hashCode() : 0);
        return hash;
    }
}
