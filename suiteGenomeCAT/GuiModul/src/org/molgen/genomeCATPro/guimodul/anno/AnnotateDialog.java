package org.molgen.genomeCATPro.guimodul.anno;

/**
 * @name AnnotateDialog
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
import java.awt.Cursor;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.annotation.AnnotationManagerImpl;
import org.molgen.genomeCATPro.annotation.GeneImpl;
import org.molgen.genomeCATPro.annotation.RegionAnnotation;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.annotation.ServiceAnnotationManager;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.data.DataService;
import org.molgen.genomeCATPro.datadb.dbentities.AnnotationList;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.IFeature;

/**
 *
 *
 */
public class AnnotateDialog extends javax.swing.JDialog {

    Data data = null;
    private AnnotationManagerImpl am;
    private Vector<String> fieldList;
    private String field;

    /**
     * Creates new form AnnotateDialog
     */
    public AnnotateDialog(java.awt.Frame parent, boolean modal, Data d) {
        super(parent, modal);
        this.data = d;
        this.fieldList = new Vector<String>();
        this.am = null;
        this.field = null;
        initComponents();
        this.jComboBoxAnnoList.setSelectedIndex(-1);
        this.fieldDownstream.setValue(1000);
        this.fieldUpstream.setValue(1000);
        this.jRadioButtonDataWithinAnno.setSelected(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupQuery = new javax.swing.ButtonGroup();
        buttonGroupPosition = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        textData = new javax.swing.JLabel(this.data.getName());
        jComboBoxAnnoList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxField = new javax.swing.JComboBox();
        jButtonDoAnno = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMsg = new javax.swing.JTextArea();
        jRadioButtonDataContainsAnno = new javax.swing.JRadioButton();
        jRadioButtonDataWithinAnno = new javax.swing.JRadioButton();
        jCheckBoxExtended = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButtonPosAll = new javax.swing.JRadioButton();
        jRadioButtonPosMiddle = new javax.swing.JRadioButton();
        fieldUpstream = new javax.swing.JFormattedTextField();
        fieldDownstream = new javax.swing.JFormattedTextField();
        jRadioButtonQueryOverlap = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.title")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jLabel1.text")); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        textData.setFont(new java.awt.Font("Dialog", 0, 12));
        textData.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.textData.text")); // NOI18N

        jComboBoxAnnoList.setModel(new javax.swing.DefaultComboBoxModel(
            AnnotationManagerImpl.listAnnotationsNames(
                GenomeRelease.toRelease(this.data.getGenomeRelease()))));
    jComboBoxAnnoList.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jComboBoxAnnoListActionPerformed(evt);
        }
    });

    jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel2.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jLabel2.text")); // NOI18N
    jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

    jComboBoxField.setModel(
        new DefaultComboBoxModel(this.fieldList));
    jComboBoxField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jComboBoxFieldActionPerformed(evt);
        }
    });

    jButtonDoAnno.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jButtonDoAnno.text")); // NOI18N
    jButtonDoAnno.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonDoAnnoActionPerformed(evt);
        }
    });

    jButtonClose.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jButtonClose.text")); // NOI18N
    jButtonClose.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonCloseActionPerformed(evt);
        }
    });

    jTextAreaMsg.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
    jTextAreaMsg.setColumns(20);
    jTextAreaMsg.setEditable(false);
    jTextAreaMsg.setFont(new java.awt.Font("Dialog", 1, 12));
    jTextAreaMsg.setLineWrap(true);
    jTextAreaMsg.setRows(3);
    jTextAreaMsg.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jTextAreaMsg.text")); // NOI18N
    jTextAreaMsg.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    jTextAreaMsg.setMargin(new java.awt.Insets(0, 10, 0, 10));
    jTextAreaMsg.setOpaque(false);
    jScrollPane1.setViewportView(jTextAreaMsg);

    buttonGroupQuery.add(jRadioButtonDataContainsAnno);
    jRadioButtonDataContainsAnno.setFont(new java.awt.Font("Dialog", 0, 12));
    jRadioButtonDataContainsAnno.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jRadioButtonDataContainsAnno.text")); // NOI18N
    jRadioButtonDataContainsAnno.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButtonDataContainsAnnoActionPerformed(evt);
        }
    });

    buttonGroupQuery.add(jRadioButtonDataWithinAnno);
    jRadioButtonDataWithinAnno.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    jRadioButtonDataWithinAnno.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jRadioButtonDataWithinAnno.text")); // NOI18N
    jRadioButtonDataWithinAnno.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButtonDataWithinAnnoActionPerformed(evt);
        }
    });

    jCheckBoxExtended.setFont(new java.awt.Font("Dialog", 0, 12));
    jCheckBoxExtended.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jCheckBoxExtended.text")); // NOI18N
    jCheckBoxExtended.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jCheckBoxExtendedActionPerformed(evt);
        }
    });

    jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jLabel3.text")); // NOI18N

    jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jLabel4.text")); // NOI18N

    buttonGroupPosition.add(jRadioButtonPosAll);
    jRadioButtonPosAll.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    jRadioButtonPosAll.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jRadioButtonPosAll.text")); // NOI18N
    jRadioButtonPosAll.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButtonPosAllActionPerformed(evt);
        }
    });

    buttonGroupPosition.add(jRadioButtonPosMiddle);
    jRadioButtonPosMiddle.setFont(new java.awt.Font("Dialog", 0, 12));
    jRadioButtonPosMiddle.setSelected(true);
    jRadioButtonPosMiddle.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jRadioButtonPosMiddle.text")); // NOI18N
    jRadioButtonPosMiddle.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButtonPosMiddleActionPerformed(evt);
        }
    });

    fieldUpstream.setEditable(false);
    fieldUpstream.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
    fieldUpstream.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.fieldUpstream.text_1")); // NOI18N

    fieldDownstream.setEditable(false);
    fieldDownstream.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
    fieldDownstream.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.fieldDownstream.text_1")); // NOI18N

    buttonGroupQuery.add(jRadioButtonQueryOverlap);
    jRadioButtonQueryOverlap.setFont(new java.awt.Font("Dialog", 0, 12));
    jRadioButtonQueryOverlap.setText(org.openide.util.NbBundle.getMessage(AnnotateDialog.class, "AnnotateDialog.jRadioButtonQueryOverlap.text")); // NOI18N
    jRadioButtonQueryOverlap.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButtonQueryOverlapActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(168, 168, 168)
                    .addComponent(jButtonDoAnno)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButtonClose))
                .addGroup(layout.createSequentialGroup()
                    .addGap(51, 51, 51)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRadioButtonDataWithinAnno)
                                .addComponent(jRadioButtonDataContainsAnno)
                                .addComponent(jRadioButtonQueryOverlap))
                            .addGap(76, 76, 76)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRadioButtonPosMiddle)
                                .addComponent(jCheckBoxExtended)
                                .addComponent(jRadioButtonPosAll)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(fieldUpstream, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(fieldDownstream, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(68, 68, 68))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBoxAnnoList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBoxField, javax.swing.GroupLayout.Alignment.LEADING, 0, 165, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(textData, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                    .addGap(45, 45, 45))
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jComboBoxAnnoList, jComboBoxField});

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldDownstream, fieldUpstream});

    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(textData, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel1)
                .addComponent(jComboBoxAnnoList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel2)
                .addComponent(jComboBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(43, 43, 43)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jRadioButtonPosAll)
                .addComponent(jRadioButtonDataContainsAnno))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jRadioButtonDataWithinAnno)
                    .addGap(2, 2, 2)
                    .addComponent(jRadioButtonQueryOverlap))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jRadioButtonPosMiddle)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jCheckBoxExtended)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel4)
                .addComponent(fieldDownstream, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(2, 2, 2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel3)
                .addComponent(fieldUpstream, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButtonDoAnno)
                .addComponent(jButtonClose))
            .addContainerGap())
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

private void jComboBoxAnnoListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxAnnoListActionPerformed
    if (this.jComboBoxAnnoList.getSelectedIndex() < 0) {
        return;
    }
    this.am = new AnnotationManagerImpl(
            GenomeRelease.toRelease(this.data.getGenomeRelease()),
            this.jComboBoxAnnoList.getSelectedItem().toString());
    this.updateFieldList();
    jComboBoxField.setModel(new DefaultComboBoxModel(this.fieldList));

}//GEN-LAST:event_jComboBoxAnnoListActionPerformed

private void jComboBoxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFieldActionPerformed
    if (this.jComboBoxField.getSelectedIndex() < 0) {
        this.field = null;
    } else {
        this.field = "get" + jComboBoxField.getSelectedItem().toString();
    }
}//GEN-LAST:event_jComboBoxFieldActionPerformed

private void jButtonDoAnnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDoAnnoActionPerformed

    DataManager.AnnoQuery q = DataManager.AnnoQuery.Overlap;
    DataManager.AnnoSubject s = DataManager.AnnoSubject.Whole;

    if (this.jRadioButtonDataContainsAnno.isSelected()) {
        q = DataManager.AnnoQuery.DataContainsAnno;
    }

    if (this.jRadioButtonDataWithinAnno.isSelected()) {
        q = DataManager.AnnoQuery.DataWithinAnno;
    }
    if (this.jRadioButtonPosMiddle.isSelected()) {
        s = DataManager.AnnoSubject.Middle;
    }
    int upstream = 0;
    int downstream = 0;

    if (this.jCheckBoxExtended.isSelected()) {
        if (this.fieldDownstream.getValue() == null || this.fieldUpstream.getValue() == null) {
            JOptionPane.showMessageDialog(this, "please set up- and downstream values! ");
            return;
        }
        upstream = ((Number) this.fieldUpstream.getValue()).intValue();
        downstream = ((Number) this.fieldDownstream.getValue()).intValue();

    }
    if (this.field == null) {
        JOptionPane.showMessageDialog(this, "Please choose annotation and field! ");
        return;
    }
    IFeature oldF = DataService.getFeatureClazz(data.getClazz());

    // we already have annotated data
    if (oldF instanceof RegionArray) {
        if (((RegionArray) oldF).getGeneColName() != null && am.getAnnotation().getName().contentEquals(GeneImpl.nameId)) {
            // has already gene annotation
            int n = JOptionPane.showConfirmDialog(null,
                    "data already has gene annotation - continue anyway? ",
                    "annotate with gene",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.NO_OPTION) {
                return;
            }
        }
    } else if (DataService.hasValue(oldF, "getAnnoValue")) {
        int n = JOptionPane.showConfirmDialog(null,
                "data already has annotation - continue anyway? ",
                "annotate",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION) {
            return;

        }
    }

    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    Data d = DataManager.annotateData(
            this.data,
            am.getAnnotation().getName(),
            this.field,
            q, s, downstream, upstream);
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    if (d == null) {
        JOptionPane.showMessageDialog(this, "error  - see logfile for more information: ");
    } else {
        JOptionPane.showMessageDialog(this, "Ok, the new annotated data table " + d.getName()
                + " contains " + d.getNof() + " entries!");
    }
}//GEN-LAST:event_jButtonDoAnnoActionPerformed

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.am = null;
    this.field = null;
    this.fieldList.clear();
    this.data = null;

    this.dispose();

}//GEN-LAST:event_jButtonCloseActionPerformed

private void jCheckBoxExtendedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxExtendedActionPerformed
    this.fieldDownstream.setEditable(this.jCheckBoxExtended.isSelected());
    this.fieldUpstream.setEditable(this.jCheckBoxExtended.isSelected());
}//GEN-LAST:event_jCheckBoxExtendedActionPerformed

private void jRadioButtonQueryOverlapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonQueryOverlapActionPerformed
    if (jRadioButtonQueryOverlap.isSelected()) {
        this.jRadioButtonPosAll.setEnabled(false);
        this.jRadioButtonPosMiddle.setEnabled(false);
    }

}//GEN-LAST:event_jRadioButtonQueryOverlapActionPerformed

private void jRadioButtonDataContainsAnnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDataContainsAnnoActionPerformed
    if (this.jRadioButtonDataContainsAnno.isSelected()) {
        this.jRadioButtonPosAll.setEnabled(true);
        this.jRadioButtonPosMiddle.setEnabled(true);
    }
}//GEN-LAST:event_jRadioButtonDataContainsAnnoActionPerformed

private void jRadioButtonDataWithinAnnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDataWithinAnnoActionPerformed
    if (this.jRadioButtonDataWithinAnno.isSelected()) {
        this.jRadioButtonPosAll.setEnabled(true);
        this.jRadioButtonPosMiddle.setEnabled(true);
    }
}//GEN-LAST:event_jRadioButtonDataWithinAnnoActionPerformed

private void jRadioButtonPosAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPosAllActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jRadioButtonPosAllActionPerformed

private void jRadioButtonPosMiddleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPosMiddleActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jRadioButtonPosMiddleActionPerformed
    public void updateFieldList() {
        AnnotationList anno = this.am.getAnnotation();
        RegionAnnotation ar = ServiceAnnotationManager.getRegionInstance(
                anno.getClazz());
        this.setFieldList(DataService.getFieldList(ar));
        for (String f : this.getFieldList()) {
            int i = this.getFieldList().indexOf(f);
            f = f.substring(0, 1).toUpperCase() + f.substring(1);
            this.getFieldList().set(i, f);
        }
        this.getFieldList().add("Name");

    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> _fieldList) {
        if (this.fieldList != null) {
            this.fieldList.clear();
        }
        this.fieldList.addAll(_fieldList);
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static void annotateDataExperiment(Data data) {
        if (data == null) {
            JOptionPane.showMessageDialog(null,
                    "data is null");
            return;
        }
        AnnotateDialog d = new AnnotateDialog(null, true, data);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupPosition;
    private javax.swing.ButtonGroup buttonGroupQuery;
    private javax.swing.JFormattedTextField fieldDownstream;
    private javax.swing.JFormattedTextField fieldUpstream;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDoAnno;
    private javax.swing.JCheckBox jCheckBoxExtended;
    private javax.swing.JComboBox jComboBoxAnnoList;
    private javax.swing.JComboBox jComboBoxField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JRadioButton jRadioButtonDataContainsAnno;
    private javax.swing.JRadioButton jRadioButtonDataWithinAnno;
    private javax.swing.JRadioButton jRadioButtonPosAll;
    private javax.swing.JRadioButton jRadioButtonPosMiddle;
    private javax.swing.JRadioButton jRadioButtonQueryOverlap;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaMsg;
    private javax.swing.JLabel textData;
    // End of variables declaration//GEN-END:variables
}
