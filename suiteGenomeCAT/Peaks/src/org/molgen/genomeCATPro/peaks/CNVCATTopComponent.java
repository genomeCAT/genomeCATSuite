/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.openide.awt.ToolbarPool;
import org.molgen.genomeCATPro.guimodul.data.GlobalPositionDataPanel;
import org.molgen.genomeCATPro.guimodul.data.ShowDataAction;
import org.molgen.genomeCATPro.guimodul.data.ShowRulerAction;
import org.molgen.genomeCATPro.guimodul.data.ZoomYAction;
import org.molgen.genomeCATPro.peaks.cnvcat.CNVCATFrame;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.Mode;

/**
 * Top component which displays something.
 */
public final class CNVCATTopComponent extends TopComponent {

    //private static CNVCATTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
    static final String ICON_PATH = "org/molgen/genomeCATPro/peaks/chart_16.png";
    private static final String PREFERRED_ID = "CNVCATTopComponent";
    private InstanceContent ic = new InstanceContent();
    static AbstractLookup al;
    private static Map<Defaults.GenomeRelease, CNVCATTopComponent> winList
            = new HashMap<Defaults.GenomeRelease, CNVCATTopComponent>();

    private CNVCATTopComponent() {
        this.cNVCATFrame1 = new CNVCATFrame();
        CNVCATTopComponent.winList.put(null, this);
        initComponents();
        associateLookup(new AbstractLookup(ic));
        setName(NbBundle.getMessage(CNVCATTopComponent.class, "CTL_CNVCATTopComponent"));
        setToolTipText(NbBundle.getMessage(CNVCATTopComponent.class, "HINT_CNVCATTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    private CNVCATTopComponent(GenomeRelease release) {
        this.cNVCATFrame1 = new CNVCATFrame(release.toString());
        CNVCATTopComponent.winList.put(release, this);
        initComponents();
        associateLookup(new AbstractLookup(ic));
        setName(NbBundle.getMessage(CNVCATTopComponent.class, "CTL_CNVCATTopComponent") + release);
        setToolTipText(NbBundle.getMessage(CNVCATTopComponent.class, "HINT_CNVCATTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    public CNVCATFrame getFrame() {
        return this.cNVCATFrame1;
    }

    public void open() {
        Mode mode = WindowManager.getDefault().findMode("editor");
        if (mode != null) {
            mode.dockInto(this);
            super.open();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cNVCATFrame1 = cNVCATFrame1;

        setBackground(java.awt.Color.black);
        setForeground(new java.awt.Color(255, 102, 102));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
        add(cNVCATFrame1);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.molgen.genomeCATPro.peaks.cnvcat.CNVCATFrame cNVCATFrame1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link findInstance}.
     */
    public static synchronized CNVCATTopComponent getDefault() {
        return new CNVCATTopComponent();

    }

    /**
     * Obtain the CNVCATTopComponent instance. Never call {@link #getDefault}
     * directly!
     */
    public static synchronized CNVCATTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(CNVCATTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof CNVCATTopComponent) {
            return (CNVCATTopComponent) win;
        }
        Logger.getLogger(CNVCATTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public static CNVCATTopComponent findInstance(GenomeRelease s) {

        // arrayframe for release existing?
        CNVCATTopComponent cnvFrame = winList.get(s);

        if (cnvFrame == null) {

            //empty frame existing?
            //cnvFrame = winList.get(null);
            //if (cnvFrame == null) {
            //create first one
            return new CNVCATTopComponent(s);
            //}
            // delete empty from list
            // add frame for release
            /*cnvFrame.cNVCATFrame1.setRelease(s.toString());
        
        winList.remove(null);
        winList.put(s,
        cnvFrame);*/
        }
        // add new data to existing frame

        return cnvFrame;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    protected void componentOpened() {
        ic.add(this.cNVCATFrame1);
        TopComponent win = CATFramePropsTopComponent.findInstance();
        win.open();
        win.requestActive();
        Logger.getLogger(CNVCATTopComponent.class.getName()).log(
                Level.INFO, " componentOpened");
        GlobalPositionDataPanel.setRelease(
                this.cNVCATFrame1.getRelease() != null ? this.cNVCATFrame1.getRelease().toString() : "");

    }

    @Override
    protected void componentClosed() {
        CNVCATTopComponent.winList.remove(this.cNVCATFrame1.release);
        ic.remove(this.cNVCATFrame1);
    }

    @Override
    public void componentShowing() {

        ToolbarPool.getDefault().setConfiguration("StandardApp");
        if (this.cNVCATFrame1 != null) {
            ZoomYAction.getInstance().setFactor(this.cNVCATFrame1.getZoomY());
            GlobalPositionDataPanel.setRelease(
                    this.cNVCATFrame1.getRelease() != null ? this.cNVCATFrame1.getRelease().toString() : "");

            this.cNVCATFrame1.showData(ShowDataAction.getState());
            this.cNVCATFrame1.showRuler(ShowRulerAction.getState());
            this.cNVCATFrame1.getPositionToHistory();
        }
    }

    @Override
    public void componentHidden() {
    }
}
