/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks.cnvcat;

/**
 *
 * @author tebel
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import org.molgen.genomeCATPro.peaks.*;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.datadb.dbentities.Track;

/**
 *
 * @author tebel
 */
public class AberrantRegions implements Serializable, AberrationIds {

    Track track = null;
    public static final String DELETION = "Deletion";
    private int XDispColumn = 0;
    private Color color = Color.black;
    private Integer countA = new Integer(0);
    private Integer noHiddenCNV = new Integer(0);
    @Transient
    private boolean selected=false;
    /**
     * 
     */
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
            e.printStackTrace();                // color editor circle
        // 

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
}

