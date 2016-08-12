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
public class TrackAtStudyID implements Serializable {

    public Long trackID;
    public Long studyID;

    public TrackAtStudyID() {
    }

    public TrackAtStudyID(Long trackID, Long studyID) {
        this.trackID = trackID;
        this.studyID = studyID;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof TrackAtStudyID) {
            TrackAtStudyID otherId = (TrackAtStudyID) object;
            return (this.trackID.equals(otherId.trackID) && (this.studyID.equals(otherId.studyID)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.studyID != null ? this.studyID.hashCode() : 0);
        hash += (this.trackID != null ? this.trackID.hashCode() : 0);
        return hash;
    }

}
