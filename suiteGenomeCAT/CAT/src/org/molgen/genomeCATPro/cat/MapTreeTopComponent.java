package org.molgen.genomeCATPro.cat;

import java.io.Serializable;
import java.util.logging.Logger;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * @name MapTreeTopComponent
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
final class MapTreeTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static ExplorerManager em = new ExplorerManager();
    private static MapTreeTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "MapTreeTopComponent";

    private MapTreeTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(MapTreeTopComponent.class, "CTL_MapTreeTopComponent"));
        setToolTipText(NbBundle.getMessage(MapTreeTopComponent.class, "HINT_MapTreeTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        em.setRootContext(new AbstractNode(Children.create(new MapDetailNodeFactory(), true)));
        associateLookup(ExplorerUtils.createLookup(em, getActionMap()));
        this.beanTreeView2.setRootVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        beanTreeView2 = new org.openide.explorer.view.BeanTreeView();

        setLayout(new java.awt.BorderLayout());
        add(beanTreeView2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView2;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link findInstance}.
     */
    public static synchronized MapTreeTopComponent getDefault() {
        if (instance == null) {
            instance = new MapTreeTopComponent();
        }
        return instance;
    }

    @Override
    public void open() {
        Mode mode = WindowManager.getDefault().findMode("explorer");
        if (mode != null) {
            mode.dockInto(this);
            super.open();
        }
    }

    /**
     * Obtain the MapTreeTopComponent instance. Never call {@link #getDefault}
     * directly!
     */
    public static synchronized MapTreeTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(MapTreeTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof MapTreeTopComponent) {
            return (MapTreeTopComponent) win;
        }
        Logger.getLogger(MapTreeTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /**
     * replaces this in object stream
     */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public ExplorerManager getExplorerManager() {
        return MapTreeTopComponent.em;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return MapTreeTopComponent.getDefault();
        }
    }
}