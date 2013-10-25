/**
 * @name NormalizeDialog
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package.
 * Katrin Tebel <tebel at molgen.mpg.de>.
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
package org.molgen.genomeCATPro.guimodul.experiment;

import java.awt.Cursor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.data.INormalize;
import org.molgen.genomeCATPro.data.OriginalSpot;
import org.molgen.genomeCATPro.data.ServiceNormalize;
import org.molgen.genomeCATPro.data.Spot;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.guimodul.cghpro.CGHProTopComponent;

/**
 *
 * 050613   exception spot clazz
 */
public class NormalizeDialog extends javax.swing.JDialog {

    ExperimentData data = null;
    ExperimentData newData = null;

    /** Creates new form NormalizeDialog */
    public NormalizeDialog(ExperimentData d) {
        super((JFrame) null, false);
        this.data = d;
        initComponents();
    }

    public ExperimentData getD() {
        return data;
    }

    public void setD(ExperimentData d) {
        this.data = d;
    }

    public static boolean batch(ExperimentData data, String method) {
        try {
            INormalize app = ServiceNormalize.getNormalizationImpl(
                    method);
            // win = CGHProTopComponent.findInstance(data);
            Logger.getLogger(NormalizeDialog.class.getName()).log(
                    Level.INFO, "Normalize: " + data.getName());

            app.normalize(data);
            return true;
        } catch (Exception e) {
            Logger.getLogger(NormalizeDialog.class.getName()).log(
                    Level.SEVERE, "Normalize: ", e);
            return false;
        }
    }

    public static void normalizeExperiment(ExperimentData data) {
        Spot currSpotClazz = null;
        try {
            //050613    kt
            currSpotClazz = DataManager.getSpotClazz(data.getClazz());
        } catch (Exception e) {
            Logger.getLogger(DataManager.class.getName()).log(Level.WARNING, null, e);
        }

        if (!(currSpotClazz instanceof OriginalSpot)) {
            JOptionPane.showMessageDialog(null,
                    "not possilbe, " + data.getName() + " because of clazz:" +
                    currSpotClazz.getClass().getName());
            return;
        }
        NormalizeDialog d = new NormalizeDialog(data);
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbNormMethod = new javax.swing.JComboBox();
        jButtonRun = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jLabelMsg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(NormalizeDialog.class, "NormalizeDialog.jLabel1.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${d.name}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(NormalizeDialog.class, "NormalizeDialog.jLabel2.text")); // NOI18N

        cbNormMethod.setModel(new javax.swing.DefaultComboBoxModel(ServiceNormalize.getAvialableMethods()));

        jButtonRun.setText(org.openide.util.NbBundle.getMessage(NormalizeDialog.class, "NormalizeDialog.jButtonRun.text")); // NOI18N
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });

        jButtonClose.setText(org.openide.util.NbBundle.getMessage(NormalizeDialog.class, "NormalizeDialog.jButtonClose.text")); // NOI18N
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jLabelMsg.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        jLabelMsg.setForeground(java.awt.Color.red);
        jLabelMsg.setText(org.openide.util.NbBundle.getMessage(NormalizeDialog.class, "NormalizeDialog.jLabelMsg.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(87, 87, 87)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonRun)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonClose))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cbNormMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)))))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbNormMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jLabelMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRun)
                    .addComponent(jButtonClose))
                .addGap(43, 43, 43))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    newData = null;
    this.setVisible(false);

}//GEN-LAST:event_jButtonCloseActionPerformed
    CGHProTopComponent win = null;
private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed
// TODO add your handling code here:

    // get Implementation
    // normalize
    // save

    this.jButtonClose.setEnabled(false);
    this.jLabelMsg.setText("Running .... please wait");
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
        if (this.cbNormMethod.getSelectedIndex() < 0) {
            return;
        }
        INormalize app = ServiceNormalize.getNormalizationImpl(
                this.cbNormMethod.getSelectedItem().toString());
        // win = CGHProTopComponent.findInstance(data);
        Logger.getLogger(NormalizeDialog.class.getName()).log(
                Level.INFO, "Normalize: " + data.getName());

        app.normalize(data);
        this.jLabelMsg.setText("done");
    } catch (Exception e) {
        this.jLabelMsg.setText("error");
        JOptionPane.showMessageDialog(this, "error  - see logfile for more information: ");
    }
    this.jButtonClose.setEnabled(true);
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

}//GEN-LAST:event_jButtonRunActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbNormMethod;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelMsg;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
