/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul;

import java.io.Serializable;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
final class InfoTopComponent extends TopComponent {

    private static InfoTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "InfoTopComponent";

    private InfoTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(InfoTopComponent.class, "CTL_InfoTopComponent"));
        setToolTipText(NbBundle.getMessage(InfoTopComponent.class, "HINT_InfoTopComponent"));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelAck = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaLiterature = new javax.swing.JTextArea();
        jLabelIcon = new javax.swing.JLabel();
        Version = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        setOpaque(true);

        jLabelAck.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabelAck.setForeground(new java.awt.Color(0, 153, 153));
        jLabelAck.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelAck, org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.jLabelAck.text")); // NOI18N

        jTextAreaLiterature.setColumns(20);
        jTextAreaLiterature.setRows(5);
        jTextAreaLiterature.setText(org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.jTextAreaLiterature.text")); // NOI18N
        jScrollPane1.setViewportView(jTextAreaLiterature);

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/molgen/genomeCATPro/guimodul/CATLOGO.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabelIcon, org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.jLabelIcon.text")); // NOI18N

        Version.setFont(new java.awt.Font("Dialog", 3, 18));
        org.openide.awt.Mnemonics.setLocalizedText(Version, org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.Version.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelAck, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 465, Short.MAX_VALUE)
                    .addComponent(Version, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE))
                .addGap(91, 91, 91))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 923, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {Version, jLabelIcon});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabelAck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Version)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Version;
    private javax.swing.JLabel jLabelAck;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaLiterature;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized InfoTopComponent getDefault() {
        if (instance == null) {
            instance = new InfoTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the InfoTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized InfoTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(InfoTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof InfoTopComponent) {
            return (InfoTopComponent) win;
        }
        Logger.getLogger(InfoTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {

        org.openide.awt.Mnemonics.setLocalizedText(Version, org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.Version.text")); // NOI18N

    }

    @Override
    public void componentClosed() {
        
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
            return InfoTopComponent.getDefault();
        }
    }
}
