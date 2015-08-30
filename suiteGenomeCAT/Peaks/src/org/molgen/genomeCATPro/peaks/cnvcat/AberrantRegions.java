package org.molgen.genomeCATPro.peaks.cnvcat;

import java.util.List;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInTrack;
import org.molgen.genomeCATPro.peaks.*;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.datadb.dbentities.Track;

/**
 * @name AberrantRegions
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * This file is part of the GenomeCAT software package.
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
public class AberrantRegions implements Serializable, AberrationIds {

    Track track = null;
    public static final String DELETION = "Deletion";
    private int XDispColumn = 0;
    private Color color = Color.black;
    private Integer countA = new Integer(0);
    private Integer noHiddenCNV = new Integer(0);
    @Transient
    private boolean selected = false;
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public AberrantRegions() {
    }

    public AberrantRegions(Track t) {
        this.track = t;
        this.selected = false;
    }

    public int getXDispColumn() {
        return XDispColumn;
    }

    public void setXDispColumn(int XDispColumn) {
        this.XDispColumn = XDispColumn;
    }

    public Color getColor() {

        return color;
    }

    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        try {
            changeSupport.firePropertyChange("color", oldColor, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldSelected = this.selected;
        this.selected = selected;
        changeSupport.firePropertyChange("selected", oldSelected, selected);

    }

    @Override
    public void setCountAberrations(Integer c) {
        this.countA = c;
    }

    @Override
    public Integer getCountAberrations() {
        return this.track.getNof();
    }

    @Override
    public Integer getNoHiddenCNV() {
        return this.noHiddenCNV;
    }

    @Override
    public void setNoHiddenCNV(Integer c) {
        Integer old = this.noHiddenCNV;
        if (old == null) {
            old = new Integer(0);
        }
        this.noHiddenCNV = c;
        //System.out.print("Has Listener : " + this.changeSupport.hasListeners("noHiddenCNV"));
        try {
            changeSupport.firePropertyChange("noHiddenCNV", old, c);
        } catch (Exception e) {
            Logger.getLogger(AberrantRegions.class.getName()).log(Level.SEVERE,
                    " update hidden cnv for " + this.track.getTrackID(), e);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public String getTrackId() {
        return this.track.getName();
    }

    public void setTrackId(String caseId) {
        String oldCaseId = this.track.getName();
        this.track.setName(caseId);
        changeSupport.firePropertyChange("caseId", oldCaseId, caseId);
    }

    public void setId(Long id) {
        this.track.setTrackID(id);
    }

    public Long getId() {
        return this.track.getTrackID();
    }

    public List<String> getSampleNames() {
        List<String> l = new Vector<String>();
        for (SampleInTrack sit : this.track.getSamples()) {
            l.add(sit.getName());
        }
        return l;
    }

    public List<String> getPhenotypes() {
        List<String> l = new Vector<String>();
        for (SampleInTrack sit : this.track.getSamples()) {
            l.add(sit.getPhenotype());
        }
        return l;
    }

    public String getParamAsString() {

        String param = this.track.getProcProcessing() + "(" +
                this.track.getParamProcessing() + ")";
        param = param.replace("\n", ";");
        return param;

    }

    public int compareByCase(AberrationIds o2) {
        AberrantRegions c2 = (AberrantRegions) o2;
        int iCaseId = this.getTrackId().compareToIgnoreCase(c2.getTrackId());
        if (iCaseId != 0) {
            return iCaseId;
        } else {
            return this.getParamAsString().compareToIgnoreCase(c2.getParamAsString());
        }
    }

    public int compareTo(Object o2) {

        AberrantRegions c2 = (AberrantRegions) o2;

        int iCaseId = this.getTrackId().compareToIgnoreCase(c2.getTrackId());
        if (iCaseId != 0) {
            return iCaseId;
        } else {
            return this.getParamAsString().compareToIgnoreCase(c2.getParamAsString());
        }
    }

    public void setSampleNames(List<String> d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPhenotypes(List<String> d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

