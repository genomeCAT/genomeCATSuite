/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.XPort;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.molgen.genomeCATPro.guimodul.experiment.ExperimentDetailView;

public final class ImportFileVisualPanel4 extends JPanel {

    public final static String newSample1 = "newSample1";
    public final static String changeSample1 = "changeSample1";
    public final static String oldSample1 = "newSample1";
    public final static String newSample2 = "newSample2";
    public final static String changeSample2 = "changeSample2";
    public final static String oldSample2 = "newSample2";
    public final static String newExp = "newExp";
    public final static String oldExp = "oldExp";

    /**
     * Creates new form ImportFileVisualPanel4
     */
    public ImportFileVisualPanel4() {
        this.experimentDetailView = new ExperimentDetailView();
        initComponents();
    }

    @Override
    public String getName() {
        return "Edit Experiment";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupExperiment = new javax.swing.ButtonGroup();
        buttonGroupSample1 = new javax.swing.ButtonGroup();
        buttonGroupSample2 = new javax.swing.ButtonGroup();
        experimentDetailView = this.experimentDetailView;
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        fieldExperimentName = new javax.swing.JTextField();
        jLabelSampl1 = new javax.swing.JLabel();
        fieldSample1 = new javax.swing.JTextField();
        jLabelSample2 = new javax.swing.JLabel();
        fieldSample2 = new javax.swing.JTextField();
        jButtonSelectSample1 = new javax.swing.JButton();
        jButtonSelectSample2 = new javax.swing.JButton();
        jButtonEditSample1 = new javax.swing.JButton();
        jButtonEditSample2 = new javax.swing.JButton();
        hintSample1 = new javax.swing.JLabel();
        hintSample2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jCheckBoxCenterMean = new javax.swing.JCheckBox();
        jCheckBoxCenterMedian = new javax.swing.JCheckBox();
        jCheckBoxDyeSwap = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(850, 590));

        experimentDetailView.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jLabel1.text")); // NOI18N

        fieldExperimentName.setText(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.fieldExperimentName.text")); // NOI18N

        jLabelSampl1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabelSampl1, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jLabelSampl1.text")); // NOI18N

        fieldSample1.setEditable(false);
        fieldSample1.setText(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.fieldSample1.text")); // NOI18N
        fieldSample1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldSample1ActionPerformed(evt);
            }
        });

        jLabelSample2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabelSample2, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jLabelSample2.text")); // NOI18N

        fieldSample2.setEditable(false);
        fieldSample2.setText(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.fieldSample2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonSelectSample1, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jButtonSelectSample1.text")); // NOI18N
        jButtonSelectSample1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonSelectSample1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectSample1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonSelectSample2, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jButtonSelectSample2.text")); // NOI18N
        jButtonSelectSample2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonSelectSample2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectSample2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonEditSample1, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jButtonEditSample1.text")); // NOI18N
        jButtonEditSample1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(jButtonEditSample2, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jButtonEditSample2.text")); // NOI18N
        jButtonEditSample2.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(hintSample1, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.hintSample1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(hintSample2, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.hintSample2.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabelSampl1)
                    .addComponent(jLabelSample2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fieldSample2)
                            .addComponent(fieldSample1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hintSample1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                            .addComponent(hintSample2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButtonEditSample1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSelectSample1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButtonEditSample2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSelectSample2))))
                    .addComponent(fieldExperimentName))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(fieldExperimentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(jLabelSampl1)
                                .addComponent(fieldSample1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(hintSample1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(jButtonEditSample1)
                                .addComponent(jButtonSelectSample1)))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(fieldSample2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hintSample2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonEditSample2)
                            .addComponent(jButtonSelectSample2)))
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jLabelSample2)))
                .addGap(15, 15, 15))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCenterMean, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jCheckBoxCenterMean.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCenterMedian, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jCheckBoxCenterMedian.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxDyeSwap, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel4.class, "ImportFileVisualPanel4.jCheckBoxDyeSwap.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxDyeSwap)
                    .addComponent(jCheckBoxCenterMedian)
                    .addComponent(jCheckBoxCenterMean))
                .addContainerGap(492, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jCheckBoxCenterMean)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxCenterMedian)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDyeSwap))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(experimentDetailView, javax.swing.GroupLayout.PREFERRED_SIZE, 834, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(experimentDetailView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void fieldSample1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldSample1ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_fieldSample1ActionPerformed

private void jButtonSelectSample1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectSample1ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jButtonSelectSample1ActionPerformed

private void jButtonSelectSample2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectSample2ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jButtonSelectSample2ActionPerformed

    public JCheckBox getJCheckBoxCenterMean() {
        return jCheckBoxCenterMean;
    }

    public void setJCheckBoxCenterMean(JCheckBox jCheckBoxCenterMean) {
        this.jCheckBoxCenterMean = jCheckBoxCenterMean;
    }

    public JCheckBox getJCheckBoxCenterMedian() {
        return jCheckBoxCenterMedian;
    }

    public void setJCheckBoxCenterMedian(JCheckBox jCheckBoxCenterMedian) {
        this.jCheckBoxCenterMedian = jCheckBoxCenterMedian;
    }

    public JCheckBox getJCheckBoxDyeSwap() {
        return jCheckBoxDyeSwap;
    }

    public void setJCheckBoxDyeSwap(JCheckBox jCheckBoxDyeSwap) {
        this.jCheckBoxDyeSwap = jCheckBoxDyeSwap;
    }

    public JLabel getHintSample1() {
        return hintSample1;
    }

    public JButton getJButtonSelectSample1() {
        return jButtonSelectSample1;
    }

    public JButton getJButtonSelectSample2() {
        return jButtonSelectSample2;
    }

    public JLabel getHintSample2() {
        return hintSample2;
    }

    public ExperimentDetailView getExperimentDetailView1() {
        return experimentDetailView;
    }

    public void setExperimentDetailView1(ExperimentDetailView experimentDetailView1) {
        this.experimentDetailView = experimentDetailView1;
        //this.initComponents();
    }

    public JTextField getFieldExperimentName() {
        return fieldExperimentName;
    }

    public JTextField getFieldSample1() {
        return fieldSample1;
    }

    public JTextField getFieldSample2() {
        return fieldSample2;
    }

    public JLabel getJLabelSampl1() {
        return jLabelSampl1;
    }

    public JLabel getJLabelSample2() {
        return jLabelSample2;
    }

    public JButton getJButtonEditSample1() {
        return jButtonEditSample1;
    }

    public void setJButtonEditSample1(JButton jButtonEditSample1) {
        this.jButtonEditSample1 = jButtonEditSample1;
    }

    public JButton getJButtonEditSample2() {
        return jButtonEditSample2;
    }

    public void setJButtonEditSample2(JButton jButtonEditSample2) {
        this.jButtonEditSample2 = jButtonEditSample2;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupExperiment;
    private javax.swing.ButtonGroup buttonGroupSample1;
    private javax.swing.ButtonGroup buttonGroupSample2;
    private org.molgen.genomeCATPro.guimodul.experiment.ExperimentDetailView experimentDetailView;
    private javax.swing.JTextField fieldExperimentName;
    private javax.swing.JTextField fieldSample1;
    private javax.swing.JTextField fieldSample2;
    private javax.swing.JLabel hintSample1;
    private javax.swing.JLabel hintSample2;
    private javax.swing.JButton jButtonEditSample1;
    private javax.swing.JButton jButtonEditSample2;
    private javax.swing.JButton jButtonSelectSample1;
    private javax.swing.JButton jButtonSelectSample2;
    private javax.swing.JCheckBox jCheckBoxCenterMean;
    private javax.swing.JCheckBox jCheckBoxCenterMedian;
    private javax.swing.JCheckBox jCheckBoxDyeSwap;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelSampl1;
    private javax.swing.JLabel jLabelSample2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
