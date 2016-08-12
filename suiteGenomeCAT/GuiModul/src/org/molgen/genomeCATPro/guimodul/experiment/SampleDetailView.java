package org.molgen.genomeCATPro.guimodul.experiment;

/**
 * @name SampleDetailView
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package. Katrin Tebel
 * <tebel at molgen.mpg.de>. The contents of this file are subject to the terms
 * of either the GNU General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.awt.Dimension;
import javax.swing.JTextField;
import org.molgen.genomeCATPro.guimodul.*;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.beansbinding.BindingGroup;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;

/**
 * 270313 kt dialog to edit samples
 *
 */
public class SampleDetailView extends javax.swing.JPanel {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private BindingGroup myBindingGroup;
    private SampleDetail sample;
    boolean edit = false;

    /**
     * Creates new form SampleDetail
     */
    public SampleDetailView() {

        sample = new SampleDetail();

        edit = true;

        initComponents();
        createBinding();
        setAllFieldsEditable(edit);
        Logger.getLogger(SampleDetailView.class.getName()).log(Level.INFO,
                "constructor called");
    }

    public SampleDetailView(SampleDetail s, boolean edit) {

        this.sample = s;

        this.edit = edit;

        initComponents();
        createBinding();
        setAllFieldsEditable(edit);
        Logger.getLogger(SampleDetailView.class.getName()).log(Level.INFO,
                "constructor called with SampleDetail: " + this.sample.toString());
    }
    static SampleDetail oldSample;

    // 270313   kt  dialog to edit sample details
    public static SampleDetail SampleDetailViewDialog(SampleDetail s, boolean edit) {

        final JDialog d = new JDialog((JFrame) null, true);
        d.setTitle("Sample Details");

        d.setSize(new Dimension(900, 600));
        d.setLocationRelativeTo(null);

        if (s == null) {
            s = new SampleDetail();
        }
        oldSample = new SampleDetail(s);

        final SampleDetailView sview = new SampleDetailView(s, edit);

        JPanel pp = new JPanel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.PAGE_AXIS));
        d.add(pp);
        pp.add(sview);
        JPanel p = new JPanel();

        pp.add(p);
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));

        final JButton okButton = new JButton();
        okButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt, d, sview);
            }
        });

        okButton.setText("ok");
        okButton.setToolTipText("save changes and quit");
        p.add(okButton);

        final JButton cancelButton = new JButton();
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt, d);
            }
        });

        cancelButton.setText("cancel");
        cancelButton.setToolTipText("undo changes and quit");
        p.add(cancelButton);

        d.setVisible(true);
        return oldSample;
    }

    static void okButtonActionPerformed(java.awt.event.ActionEvent evt, JDialog d, SampleDetailView sview) {
        oldSample = sview.getSample();
        Logger.getLogger(SampleDetailView.class.getName()).log(Level.FINE,
                "ok: " + oldSample.toFullString());
        d.setVisible(false);

    }

    static void cancelButtonActionPerformed(java.awt.event.ActionEvent evt, JDialog d) {

        Logger.getLogger(SampleDetailView.class.getName()).log(Level.FINE,
                "cancel: " + oldSample.toFullString());
        d.setVisible(false);
    }

    void createBinding() {
        myBindingGroup = new org.jdesktop.beansbinding.BindingGroup();
        org.jdesktop.beansbinding.Binding binding;

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sample,
                org.jdesktop.beansbinding.ELProperty.create("${moleculeChannel1}"),
                fieldMolecule,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        myBindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sample,
                org.jdesktop.beansbinding.ELProperty.create("${treatmentChannel1}"),
                fieldTreatment,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        myBindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sample,
                org.jdesktop.beansbinding.ELProperty.create("${characteristicsChannel1}"),
                fieldCharacteristics,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        myBindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sample,
                org.jdesktop.beansbinding.ELProperty.create("${organismChannel1}"),
                fieldOrganism,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        myBindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sample,
                org.jdesktop.beansbinding.ELProperty.create("${sourceChannel1}"),
                fieldSource,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        myBindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sample,
                org.jdesktop.beansbinding.ELProperty.create("${name}"),
                nameField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        myBindingGroup.addBinding(binding);
        myBindingGroup.bind();
    }

    public JTextField getNameField1() {
        return nameField1;
    }

    public void setAllFieldsEditable(boolean b) {

        List<javax.swing.JComponent> list = SwingUtils.getDescendantsOfType(javax.swing.JComponent.class, this);
        for (javax.swing.JComponent field : list) {
            if (field instanceof javax.swing.text.JTextComponent) {
                ((javax.swing.text.JTextComponent) field).setEditable(b);
                continue;
            }
            if (field instanceof javax.swing.JComboBox) {
                ((javax.swing.JComboBox) field).setEditable(b);
                continue;
            }

            if (field instanceof javax.swing.JLabel) {
                if (field.getName() != null && field.getName().indexOf("req") >= 0) {
                    if (b) {
                        ((javax.swing.JLabel) field).setText(((javax.swing.JLabel) field).getText().replace(":", "*:"));
                    } else {
                        ((javax.swing.JLabel) field).setText(((javax.swing.JLabel) field).getText().replace("*:", ":"));
                    }

                }
                continue;
            }
        }
    }

    public void setAllFieldsEnabled(boolean b) {

        List<javax.swing.JComponent> list = SwingUtils.getDescendantsOfType(javax.swing.JComponent.class, this);
        for (javax.swing.JComponent field : list) {
            field.setEnabled(b);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        sourceChannel1Label1 = new javax.swing.JLabel();
        organismChannel1Label1 = new javax.swing.JLabel();
        characteristicsChannel1Label1 = new javax.swing.JLabel();
        treatmentChannel1Label1 = new javax.swing.JLabel();
        moleculeChannel1Label1 = new javax.swing.JLabel();
        fieldMolecule = new javax.swing.JTextField();
        fieldTreatment = new javax.swing.JTextField();
        fieldCharacteristics = new javax.swing.JTextField();
        fieldOrganism = new javax.swing.JTextField();
        fieldSource = new javax.swing.JTextField();
        nameField1 = new javax.swing.JTextField();
        labelName_req = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Sample Details"));

        sourceChannel1Label1.setFont(new java.awt.Font("Dialog", 0, 12));
        sourceChannel1Label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        sourceChannel1Label1.setText("Source :");

        organismChannel1Label1.setFont(new java.awt.Font("Dialog", 0, 12));
        organismChannel1Label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        organismChannel1Label1.setText("Organism:");

        characteristicsChannel1Label1.setFont(new java.awt.Font("Dialog", 0, 12));
        characteristicsChannel1Label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        characteristicsChannel1Label1.setText("Characteristics:");

        treatmentChannel1Label1.setFont(new java.awt.Font("Dialog", 0, 12));
        treatmentChannel1Label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        treatmentChannel1Label1.setText("Treatment:");

        moleculeChannel1Label1.setFont(new java.awt.Font("Dialog", 0, 12));
        moleculeChannel1Label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        moleculeChannel1Label1.setText("Molecule:");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sample.molecule}"), fieldMolecule, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sample.treatment}"), fieldTreatment, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sample.characteristics}"), fieldCharacteristics, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sample.organism}"), fieldOrganism, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sample.source}"), fieldSource, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelName_req.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        labelName_req.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelName_req.setText("Name:");

        jLabel1.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel1.setText("Briefly identify the biological material and the experimental variable(s), e.g., vastus lateralis muscle, exercised, 60 min.");

        jLabel2.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel2.setText("<html>Use standard NCBI Taxonomy nomenclature <br/>Identify the organism(s) from which the biological material was derived.</html>");

        jLabel3.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel3.setText("<html>Describe all available characteristics of the biological source, <br/>including factors not necessarily under investigation.<br/>\n Provide in 'Tag: Value;' format, where 'Tag'  <br/>Include as many characteristics fields as necessary to thoroughly describe your Samples.<br/>\n");

        jLabel4.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel4.setText("Describe any treatments applied to the biological material prior to extract preparation.");

        jLabel5.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel5.setText("<html>Specify the type of molecule that was extracted from the biological material.<br/> total RNA, polyA RNA, cytoplasmic RNA, nuclear RNA, genomic DNA, protein, or other. </html>");

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel6.setText("Phenotype:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sample.phenotype}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(labelName_req)
                    .addComponent(sourceChannel1Label1)
                    .addComponent(organismChannel1Label1)
                    .addComponent(characteristicsChannel1Label1)
                    .addComponent(treatmentChannel1Label1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldTreatment, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(fieldCharacteristics, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(fieldOrganism, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(fieldSource, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addComponent(nameField1, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(46, 46, 46)
                    .addComponent(moleculeChannel1Label1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(fieldMolecule, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                        .addComponent(jLabel5))
                    .addContainerGap()))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldCharacteristics, fieldMolecule, fieldOrganism, fieldSource, fieldTreatment, nameField1});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelName_req)
                    .addComponent(nameField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(sourceChannel1Label1)
                    .addComponent(fieldSource, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(organismChannel1Label1)
                    .addComponent(fieldOrganism, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(characteristicsChannel1Label1)
                    .addComponent(fieldCharacteristics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(treatmentChannel1Label1)
                    .addComponent(fieldTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(90, 90, 90))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(367, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(moleculeChannel1Label1)
                        .addComponent(fieldMolecule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel5)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel characteristicsChannel1Label1;
    private javax.swing.JTextField fieldCharacteristics;
    private javax.swing.JTextField fieldMolecule;
    private javax.swing.JTextField fieldOrganism;
    private javax.swing.JTextField fieldSource;
    private javax.swing.JTextField fieldTreatment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelName_req;
    private javax.swing.JLabel moleculeChannel1Label1;
    private javax.swing.JTextField nameField1;
    private javax.swing.JLabel organismChannel1Label1;
    private javax.swing.JLabel sourceChannel1Label1;
    private javax.swing.JLabel treatmentChannel1Label1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public SampleDetail getSample() {
        return sample;
    }

    public void setSample(SampleDetail s) {
        this.sample.copy(s);
    }
}
