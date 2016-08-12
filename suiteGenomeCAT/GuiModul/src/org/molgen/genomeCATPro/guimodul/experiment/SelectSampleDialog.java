package org.molgen.genomeCATPro.guimodul.experiment;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/**
 * @name SelectSampleDialog
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
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
public class SelectSampleDialog extends javax.swing.JDialog {

    List<SampleDetail> list = null;
    Vector<String> names = new Vector<String>();
    private SampleDetail sample = null;

    /**
     * Creates new form SelectSampleDialog
     */
    public SelectSampleDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        try {
            list = ExperimentService.listSampleDetails();

            if (list == null) {
                list = new Vector<SampleDetail>();
            }

            for (SampleDetail d : list) {
                names.add(d.getName());
            }
        } catch (Exception ex) {
            Logger.getLogger(SelectSampleDialog.class.getName()).log(Level.SEVERE,
                    "Error: ", ex);
        }
        initComponents();
        this.jComboBoxSamples.setSelectedIndex(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sampleDetailView1 = new SampleDetailView(new SampleDetail(), false);
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jComboBoxSamples = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButtonOK.setText(org.openide.util.NbBundle.getMessage(SelectSampleDialog.class, "SelectSampleDialog.jButtonOK.text")); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setText(org.openide.util.NbBundle.getMessage(SelectSampleDialog.class, "SelectSampleDialog.jButtonCancel.text")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jComboBoxSamples.setModel(new javax.swing.DefaultComboBoxModel(this.names));
        jComboBoxSamples.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxSamplesItemStateChanged(evt);
            }
        });
        jComboBoxSamples.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSamplesActionPerformed(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SelectSampleDialog.class, "SelectSampleDialog.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(727, Short.MAX_VALUE)
                .addComponent(jButtonOK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancel)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sampleDetailView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxSamples, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(360, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxSamples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(sampleDetailView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * instantiate dialog and return selected sample
     *
     * @return
     */
    public static SampleDetail getSampleSelection() {
        SelectSampleDialog d = new SelectSampleDialog(null, true);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
        //System.out.println("return " + (d.sample != null ? d.sample.toFullString() : "NULL"));
        return d.sample;

    }

    /**
     * return without selected sample
     */
private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    this.sample = null;
    this.setVisible(false);
    //System.out.println("cancel " );

}//GEN-LAST:event_jButtonCancelActionPerformed
    /**
     * return with selected sample
     */
private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed

    this.setVisible(false);
    //System.out.println("ok " + sample.toFullString());
}//GEN-LAST:event_jButtonOKActionPerformed

private void jComboBoxSamplesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxSamplesItemStateChanged
    if (this.jComboBoxSamples.getSelectedIndex() < 0) {
        return;
    }

    this.sample = this.list.get(this.jComboBoxSamples.getSelectedIndex());
    //System.out.println("changed " + sample.toFullString());
    this.sampleDetailView1.setSample(this.sample);

}//GEN-LAST:event_jComboBoxSamplesItemStateChanged

private void jComboBoxSamplesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSamplesActionPerformed
}//GEN-LAST:event_jComboBoxSamplesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox jComboBoxSamples;
    private javax.swing.JLabel jLabel1;
    private org.molgen.genomeCATPro.guimodul.experiment.SampleDetailView sampleDetailView1;
    // End of variables declaration//GEN-END:variables
}
