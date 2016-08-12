/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.cghpro;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.cghpro.chip.Chip;
import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.guimodul.data.GlobalPositionDataPanel;
import org.molgen.genomeCATPro.guimodul.data.ShowDataAction;
import org.molgen.genomeCATPro.guimodul.data.ShowRulerAction;
import org.molgen.genomeCATPro.guimodul.data.ZoomYAction;
import org.openide.awt.ToolbarPool;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something. multiple instances allowed keep track
 * of instances with linked list (key is data) provide cghproframe as lookup
 */
public class CGHProTopComponent extends TopComponent {

    /**
     * path to the icon used by the component and its open action
     */
    static final String ICON_PATH = "org/molgen/genomeCATPro/cghpro/cgh_16.jpg";
    // maintain multiple instances
    private static Map<Data, CGHProTopComponent> cghList
            = new HashMap<Data, CGHProTopComponent>();
    public static final String PREFERRED_ID = "CGHProTopComponent";
    //lookup content        
    private InstanceContent ic = new InstanceContent();
    static AbstractLookup al;
    private PropertyChangeListener loadedChipsListener = new ListenerForLoadedChips();

    public static CGHProTopComponent findInstance(Data s) {
        CGHProTopComponent cghtop = cghList.get(s);
        if (cghtop == null) {
            cghtop = new CGHProTopComponent(s);
        }
        return cghtop;
    }

    public static synchronized CGHProTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(CGHProTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof CGHProTopComponent) {
            return (CGHProTopComponent) win;
        }

        return getDefault();

    }

    public static synchronized CGHProTopComponent getDefault() {
        return new CGHProTopComponent();
    }

    public CGHProTopComponent() {
        this.panelCGHPRO = new CGHPROFrame();
        initComponents();
        associateLookup(new AbstractLookup(ic));
        //this.associateLookup(Lookups.singleton(panelCGHPRO));
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    public CGHProTopComponent(Data d) {

        this.setName(d.getName());

        this.panelCGHPRO = new CGHPROFrame(d);

        cghList.put(d, this);
        initComponents();
        //this.associateLookup(Lookups.singleton(this.getChip()));+

        // provide cghproframe as lookup 
        associateLookup(new AbstractLookup(ic));
        //this.associateLookup(Lookups.singleton(panelCGHPRO));

        this.panelCGHPRO.addPropertyChangeListener(this.loadedChipsListener);
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    @Override
    public void open() {
        Mode mode = WindowManager.getDefault().findMode("editor");
        if (mode != null) {
            mode.dockInto(this);
            super.open();
        }
    }

    @Override
    protected void componentOpened() {
        ic.add(this.panelCGHPRO);
        TopComponent win = CGHPROFramePropsTopComponent.findInstance();
        win.open();
        win.requestActive();
        Logger.getLogger(CGHProTopComponent.class.getName()).log(
                Level.INFO, " componentOpened");
        GlobalPositionDataPanel.setRelease(this.panelCGHPRO.getRelease());

    }

    @Override
    protected void componentClosed() {
        ic.remove(this.panelCGHPRO);
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    public ChipFeature getChip() {
        return this.panelCGHPRO.getOriginalChip();
    }

    public void addChip(Chip newChip) {
        this.panelCGHPRO.addChip(newChip);

    }

    private class ListenerForLoadedChips implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (CGHPROFrame.PROP_ADD_CHIP.equals(evt.getPropertyName())) {
                List newList = panelCGHPRO.getChipList();
                //ic.set(newList, null);
            }

        }
    }

    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    /*@Override*/
    @Override
    public String preferredID() {
        return CGHProTopComponent.PREFERRED_ID;

    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return CGHProTopComponent.getDefault();
        }
    }

    public CGHPROFrame getCGHPROFrame() {
        return this.panelCGHPRO;
    }

    @Override
    public void componentShowing() {

        ToolbarPool.getDefault().setConfiguration("StandardApp");
        if (this.panelCGHPRO != null) {
            ZoomYAction.getInstance().setFactor(this.panelCGHPRO.getZoomY());
            GlobalPositionDataPanel.setRelease(
                    this.panelCGHPRO.getRelease());

            this.panelCGHPRO.showData(ShowDataAction.getState());
            this.panelCGHPRO.showRuler(ShowRulerAction.getState());
            this.panelCGHPRO.getPositionToHistory();
        }
    }

    @Override
    public void componentHidden() {
        //noch componente offen?

        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null || win == this) {
            TopComponent winProps = CGHPROFramePropsTopComponent.findInstance();
            winProps.close();
            Logger.getLogger(CGHProTopComponent.class.getName()).log(
                    Level.INFO, " component finally Closed");
            ToolbarPool.getDefault().setConfiguration("Standard");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCGHPRO = panelCGHPRO;

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCGHPRO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCGHPRO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.molgen.genomeCATPro.guimodul.cghpro.CGHPROFrame panelCGHPRO;
    // End of variables declaration//GEN-END:variables
}
