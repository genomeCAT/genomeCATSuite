/*
 * ExportPlatformDialog.java
 *
 * Created on July 29, 2011, 4:35 PM
 */
package org.molgen.genomeCATPro.guimodul.platform;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.cghpro.xport.ExportPlatform;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.openide.util.NbPreferences;

/**
 *
 * @author  tebel
 */
public class ExportPlatformDialog extends javax.swing.JDialog {

    PlatformData data = null;

    /** Creates new form ExportPlatformDialog */
    public ExportPlatformDialog(java.awt.Frame parent, PlatformData d) {
        super(parent, true);
        this.data = d;
        this.platformDataView1 = new PlatformDataView(d, false);

        initComponents();
    }

    static void exportPlatformData(PlatformData data) {
        ExportPlatformDialog d = new ExportPlatformDialog(null, data);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        platformDataView1 = platformDataView1;
        jLabel1 = new javax.swing.JLabel();
        jTextFieldFileName = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jButtonExport = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(ExportPlatformDialog.class, "ExportPlatformDialog.jLabel1.text")); // NOI18N

        jButtonBrowse.setText(org.openide.util.NbBundle.getMessage(ExportPlatformDialog.class, "ExportPlatformDialog.jButtonBrowse.text")); // NOI18N
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jButtonExport.setText(org.openide.util.NbBundle.getMessage(ExportPlatformDialog.class, "ExportPlatformDialog.jButtonExport.text")); // NOI18N
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });

        jButtonCancel.setText(org.openide.util.NbBundle.getMessage(ExportPlatformDialog.class, "ExportPlatformDialog.jButtonCancel.text")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(platformDataView1, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)
                        .addComponent(jButtonBrowse)
                        .addGap(54, 54, 54))))
            .addGroup(layout.createSequentialGroup()
                .addGap(290, 290, 290)
                .addComponent(jButtonExport)
                .addGap(18, 18, 18)
                .addComponent(jButtonCancel)
                .addContainerGap(251, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(platformDataView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBrowse)
                    .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonExport)
                    .addComponent(jButtonCancel))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed

    String path = NbPreferences.forModule(ExportPlatformDialog.class).get("pathPreference", "");
    JFileChooser importFileChooser = new JFileChooser(path);
    importFileChooser.setSelectedFile(new File(this.data.getTableData() + ".bed"));

    int returnVal = importFileChooser.showSaveDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }
    this.jTextFieldFileName.setText(importFileChooser.getSelectedFile().getPath());

    Logger.getLogger(ExportPlatformDialog.class.getName()).log(Level.INFO,
            "You chose to export to: " +
            this.jTextFieldFileName.getText());


    NbPreferences.forModule(ExportPlatformDialog.class).put("pathPreference",
            importFileChooser.getSelectedFile().getPath());

}//GEN-LAST:event_jButtonBrowseActionPerformed

private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    this.dispose();
}//GEN-LAST:event_jButtonCancelActionPerformed

private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed
    try {
        ExportPlatform.doExportBED(this.data, this.jTextFieldFileName.getText());//GEN-LAST:event_jButtonExportActionPerformed
            JOptionPane.showMessageDialog(this, "exported into " +
                    this.jTextFieldFileName.getText());
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "error  - see logfile for more information: " +
                    ex.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextFieldFileName;
    private org.molgen.genomeCATPro.guimodul.platform.PlatformDataView platformDataView1;
    // End of variables declaration//GEN-END:variables
}