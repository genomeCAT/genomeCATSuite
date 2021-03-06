/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.cat.maparr.ArrayFrame;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.guimodul.data.GlobalPositionDataPanel;
import org.molgen.genomeCATPro.guimodul.data.ShowDataAction;
import org.molgen.genomeCATPro.guimodul.data.ShowRulerAction;
import org.openide.awt.ToolbarPool;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Top component which displays something.
 */
final class CATTopComponent extends TopComponent {

    private static CATTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/molgen/genomeCATPro/cat/genomeCATLogo.jpg";
    private static final String PREFERRED_ID = "CATTopComponent";
    private static Map<Defaults.GenomeRelease, CATTopComponent> arrayList =
            new HashMap<Defaults.GenomeRelease, CATTopComponent>();
    private InstanceContent ic = new InstanceContent();
    static AbstractLookup al;

    private CATTopComponent() {
        this.array = new ArrayFrame();
        CATTopComponent.arrayList.put(null, this);
        initComponents();
        associateLookup(new AbstractLookup(ic));
        setName(NbBundle.getMessage(CATTopComponent.class, "CTL_CATTopComponent"));
        setToolTipText(NbBundle.getMessage(CATTopComponent.class, "HINT_CATTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
    }

    private CATTopComponent(Data d, GenomeRelease s) {
        this.array = new ArrayFrame(new Data[]{d}, s);

        CATTopComponent.arrayList.put(s, this);

        initComponents();
        associateLookup(new AbstractLookup(ic));
        setName(NbBundle.getMessage(CATTopComponent.class, "CTL_CATTopComponent"));
        setToolTipText(NbBundle.getMessage(CATTopComponent.class, "HINT_CATTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        array = array;

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(array, javax.swing.GroupLayout.DEFAULT_SIZE, 1252, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(array, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.molgen.genomeCATPro.cat.maparr.ArrayFrame array;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized CATTopComponent getDefault() {
        if (instance == null) {
            instance = new CATTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the CATTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized CATTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(CATTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof CATTopComponent) {
            return (CATTopComponent) win;
        }
        Logger.getLogger(CATTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public static CATTopComponent findInstance(Data s) {

        // arrayframe for release existing?
        CATTopComponent arrayFrame = arrayList.get(
                Defaults.GenomeRelease.toRelease(s.getGenomeRelease()));
        if (arrayFrame == null) {

            //empty frame existing?
            arrayFrame = arrayList.get(null);


            if (arrayFrame == null) {
                //create first one
                return new CATTopComponent(
                        s,
                        Defaults.GenomeRelease.toRelease(s.getGenomeRelease()));
            }
            // delete empty from list
            // add frame for release
            arrayFrame.array.setRelease(
                    Defaults.GenomeRelease.toRelease(s.getGenomeRelease()));
            arrayList.remove(null);
            arrayList.put(Defaults.GenomeRelease.toRelease(s.getGenomeRelease()),
                    arrayFrame);
        }
        // add new data to existing frame
        arrayFrame.array.addData(new Data[]{s});
        return arrayFrame;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        ic.add(this.array);
        TopComponent win = ArrayFramePropsTopComponent.findInstance();
        win.open();
        win.requestActive();
        Logger.getLogger(CATTopComponent.class.getName()).log(
                Level.INFO, " componentOpened");
        GlobalPositionDataPanel.setRelease(
                this.array.getRelease() != null ? this.array.getRelease().toString() : "");
    // ZoomYAction.resetFactor(0);
    // TODO add custom code on component opening
    }

    @Override
    public void componentShowing() {

        ToolbarPool.getDefault().setConfiguration("CATApp");

        if (this.array != null) {
            //ZoomYAction.getInstance().setFactor(this.array.getZoomY());
            GlobalPositionDataPanel.setRelease(
                    this.array.getRelease() != null ? this.array.getRelease().toString() : "");

            this.array.showData(ShowDataAction.getState());
            this.array.showRuler(ShowRulerAction.getState());
            this.array.getPositionToHistory();
            this.array.scaleArrays();   //210912 kt scale at view change
        }
    }

    @Override
    public void componentHidden() {
        //noch componente offen?
    }

    @Override
    public void componentClosed() {
        CATTopComponent.arrayList.remove(this.array.release);
        ic.remove(this.array);
        

    }

    public ArrayFrame getArray() {
        return array;
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return CATTopComponent.getDefault();
        }
    }
    //static List<CATTopComponent> list = new Vector<CATTopComponent>();
}
