package org.molgen.genomeCATPro.guimodul.cghpro;

/**
 * @name HMMRunDialog.java
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>. The contents of
 * props file are subject to the terms of either the GNU General Public License
 * Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use props file
 * except in compliance with the License. You can obtain a copy of the License
 * at http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. props program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;

import org.molgen.genomeCATPro.cghpro.chip.HMMWorker;
import org.molgen.genomeCATPro.common.Informable;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 *
 * @author tebel
 */
public class HMMRunDialog extends javax.swing.JDialog {

    Data data;
    CGHProTopComponent win = null;

    /**
     * Creates new form CBSRunDialog
     */
    public HMMRunDialog(java.awt.Frame parent, boolean modal, Data s) {
        super(parent, modal);
        this.data = s;

        initComponents();
        setLocationRelativeTo(null);
        this.jButtonShow.setEnabled(false);

        this.jTextFieldSampleName.setText(Utils.getUniquableName(this.data.getName()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMsg = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldSampleName = new javax.swing.JTextField();
        jButtonRun = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jProgressBar = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jLabelPrompt = new javax.swing.JLabel();
        jButtonShow = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "HMMRunDialog.title")); // NOI18N

        jTextAreaMsg.setColumns(20);
        jTextAreaMsg.setEditable(false);
        jTextAreaMsg.setRows(5);
        jScrollPane1.setViewportView(jTextAreaMsg);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "CBSRunDialog.jLabel1.text")); // NOI18N

        jTextFieldSampleName.setFont(new java.awt.Font("Dialog", 1, 12));
        jTextFieldSampleName.setText(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "CBSRunDialog.jTextFieldSampleName.text")); // NOI18N
        jTextFieldSampleName.setFocusable(false);
        jTextFieldSampleName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSampleNameActionPerformed(evt);
            }
        });

        jButtonRun.setText(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "CBSRunDialog.jButtonRun.text")); // NOI18N
        jButtonRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });

        jButtonClose.setText(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "CBSRunDialog.jButtonClose.text")); // NOI18N
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "CBSRunDialog.jLabel2.text")); // NOI18N

        jLabelPrompt.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        jLabelPrompt.setText("Press <Run> to start HMM !");

        jButtonShow.setText(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "CBSRunDialog.jButtonShow.text")); // NOI18N
        jButtonShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(HMMRunDialog.class, "HMMRunDialog.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(jButtonRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonShow)
                        .addGap(113, 113, 113)
                        .addComponent(jButtonClose))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSampleName, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addComponent(jLabelPrompt)
                            .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonClose, jButtonRun, jButtonShow});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldSampleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPrompt)
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRun)
                    .addComponent(jButtonClose)
                    .addComponent(jButtonShow))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jTextFieldSampleNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSampleNameActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextFieldSampleNameActionPerformed

private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed
    this.jLabelPrompt.setText("Running .... please wait");

    this.jButtonClose.setEnabled(false);
    this.jButtonRun.setEnabled(false);
    this.jButtonShow.setEnabled(false);
    Logger.getLogger(HMMRunDialog.class.getName()).log(
            Level.INFO, "Start Module HMM ");
    Informable informable = new Informable() {

        @Override
        public void messageChanged(String message) {

            jTextAreaMsg.append(message + "\n");
        }
    };
    informable.messageChanged("Start aCGH HMM....");
    informable.messageChanged("Get Data for " + data.getName());
    this.repaint();

    win = CGHProTopComponent.findInstance(data);

    Logger.getLogger(HMMRunDialog.class.getName()).log(
            Level.INFO, "Start aCGH HMM for: "
            + win.getChip().getDataEntity().getName());

    Logger.getLogger(HMMWorker.class.getName()).setLevel(Level.INFO);

    HMMWorker worker = new HMMWorkerImpl(win.getChip(), this.jTextFieldSampleName.getText(), informable);
    PropertyChangeListener listener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                jProgressBar.setValue((Integer) evt.getNewValue());
            }
        }
    };
    worker.addPropertyChangeListener(listener);

    // Start the worker. Note that control is 
    // returned immediately
    worker.execute();

}//GEN-LAST:event_jButtonRunActionPerformed

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.dispose();

}//GEN-LAST:event_jButtonCloseActionPerformed

private void jButtonShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowActionPerformed

    win.open();
    win.requestActive();
}//GEN-LAST:event_jButtonShowActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                HMMRunDialog dialog = new HMMRunDialog(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JButton jButtonShow;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelPrompt;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaMsg;
    private javax.swing.JTextField jTextFieldSampleName;
    // End of variables declaration//GEN-END:variables

    private class HMMWorkerImpl extends HMMWorker {

        public HMMWorkerImpl(ChipFeature c, String name, Informable inf) {
            super(c, name, inf);
        }

        // This method is invoked when the worker is finished
        // its task
        @Override
        protected void done() {
            try {
                // Get the number of matches. Note that the
                // method get will throw any exception thrown
                // during the execution of the worker.
                ChipFeature newChip = get();
                if (!newChip.getError()) {
                    win.addChip(newChip);
                    jLabelPrompt.setText("Finished ... close or rerun!");
                } else {
                    jLabelPrompt.setText("Error ... see logfile!");
                }
                jButtonClose.setEnabled(true);
                jButtonRun.setEnabled(true);
                jButtonShow.setEnabled(true);
                //progressBar.setVisible(false);
            } catch (Exception e) {
                Logger.getLogger(HMMRunDialog.class.getName()).log(Level.WARNING, "do aCGH HMM ", e);
                JOptionPane.showMessageDialog(null, "Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
