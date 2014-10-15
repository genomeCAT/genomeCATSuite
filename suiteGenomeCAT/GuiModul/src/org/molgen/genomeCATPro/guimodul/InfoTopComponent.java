/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul;

import java.awt.Color;
import java.io.Serializable;
import java.util.logging.Logger;
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
        this.setBackground(Color.white);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jLabelAck = new javax.swing.JLabel();
        jLabelIcon = new javax.swing.JLabel();
        Version = new javax.swing.JLabel();

        setBackground(javax.swing.UIManager.getDefaults().getColor("window"));
        setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        setForeground(new java.awt.Color(255, 255, 255));

        jTextPane1.setContentType(org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.jTextPane1.contentType")); // NOI18N
        jTextPane1.setEditable(false);
        jTextPane1.setText(org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.jTextPane1.text")); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        jPanel1.setBackground(java.awt.Color.white);

        jLabelAck.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabelAck.setForeground(new java.awt.Color(0, 102, 102));
        jLabelAck.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelAck, org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.jLabelAck.text")); // NOI18N
        jLabelAck.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/molgen/genomeCATPro/guimodul/CATLOGO.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabelIcon, org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.jLabelIcon.text")); // NOI18N
        jLabelIcon.setOpaque(true);

        Version.setFont(new java.awt.Font("Dialog", 3, 18));
        Version.setForeground(new java.awt.Color(0, 102, 102));
        org.openide.awt.Mnemonics.setLocalizedText(Version, org.openide.util.NbBundle.getMessage(InfoTopComponent.class, "InfoTopComponent.Version.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Version, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)))
                .addComponent(jLabelAck, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(Version))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(156, Short.MAX_VALUE)
                        .addComponent(jLabelAck, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Version;
    private javax.swing.JLabel jLabelAck;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
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
