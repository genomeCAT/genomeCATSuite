/**
 * @name PlatformDetailView.java
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Aug 25, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.molgen.genomeCATPro.guimodul.platform;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jdesktop.beansbinding.Converter;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;

import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.guimodul.SwingUtils;

public class PlatformDataView extends javax.swing.JPanel {

    private PlatformData platform = null;
    boolean edit = false;

    /**
     * Creates new form BeanForm
     */
    public PlatformDataView() {

        this.platform = new PlatformData();
        edit = false;

        initComponents();
        setAllFieldsEditable(edit);
        Logger.getLogger(PlatformDataView.class.getName()).log(Level.INFO,
                "constructor called");
    }

    public PlatformDataView(PlatformData p, boolean edit) {
        this.edit = edit;
        this.platform = p;

        initComponents();
        setAllFieldsEditable(edit);
        Logger.getLogger(PlatformDataView.class.getName()).log(Level.INFO,
                "constructor called");
    }

    public PlatformData getPlatform() {
        return this.platform;
    }

    public void setPlatform(PlatformData p) {
        this.platform.copy(p);
    }

    public void setPlatform(PlatformData p, boolean edit) {
        this.platform.copy(p);
        this.edit = edit;
        setAllFieldsEditable(edit);
        this.repaint();
        Logger.getLogger(
                PlatformDataView.class.getName()).log(Level.INFO,
                        "platform set to: " + p.toString() + " editable: "
                        + (this.edit ? "true" : "false"));

    }

    void setGenomeReleaseEditable(boolean b) {
        this.fieldRelease.setEditable(false);
        this.jButtonSelectRelease.setEnabled(false);
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
        name = new javax.swing.JLabel();
        fieldName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        fieldID = new javax.swing.JTextField();
        modifiedField = new javax.swing.JTextField();
        createdField = new javax.swing.JTextField();
        fieldFile = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fieldTable = new javax.swing.JTextField();
        nofSpotsLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButtonSelectRelease = new javax.swing.JButton();
        fieldRelease = new javax.swing.JTextField();
        fieldNofSpots = new javax.swing.JTextField();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        name.setFont(new java.awt.Font("Dialog", 0, 12));
        name.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        name.setText("Name:");

        fieldName.setEditable(false);
        fieldName.setMaximumSize(new java.awt.Dimension(20, 100));
        fieldName.setMinimumSize(new java.awt.Dimension(4, 50));
        fieldName.setPreferredSize(new java.awt.Dimension(10, 50));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.name}"), fieldName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("ID:");

        fieldID.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.platformListID}"), fieldID, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        modifiedField.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.modified}"), modifiedField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new Converter<Date, String>() {
            @Override
            public String convertForward(Date value) {
                return DateFormat.getDateInstance().format(value);
            }

            @Override
            public Date convertReverse(String value) {

                try {
                    return DateFormat.getDateInstance().parse(value);
                } catch (ParseException e) {
                    return Calendar.getInstance().getTime();
                }
            }
        });
        bindingGroup.addBinding(binding);

        createdField.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.created}"), createdField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new Converter<Date, String>() {
            @Override
            public String convertForward(Date value) {
                return DateFormat.getDateInstance().format(value);
            }

            @Override
            public Date convertReverse(String value) {

                try {
                    return DateFormat.getDateInstance().parse(value);
                } catch (ParseException e) {
                    return Calendar.getInstance().getTime();
                }
            }
        });
        bindingGroup.addBinding(binding);

        fieldFile.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.originalFile}"), fieldFile, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText("File:");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setText("data table:");

        fieldTable.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.tableData}"), fieldTable, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        fieldTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldTableActionPerformed(evt);
            }
        });

        nofSpotsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        nofSpotsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        nofSpotsLabel.setText("nof Spots:");

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText("genome release:");

        jButtonSelectRelease.setText("select");
        jButtonSelectRelease.setEnabled(false);
        jButtonSelectRelease.setMargin(new java.awt.Insets(2, 1, 2, 1));
        jButtonSelectRelease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectReleaseActionPerformed(evt);
            }
        });

        fieldRelease.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.genomeRelease}"), fieldRelease, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        fieldNofSpots.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${platform.nofSpots}"), fieldNofSpots, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nofSpotsLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(name, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldNofSpots, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(fieldName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fieldID, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                    .addComponent(fieldFile, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(fieldRelease)
                            .addComponent(fieldTable, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(createdField, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(modifiedField, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSelectRelease)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(fieldID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(name)
                    .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(fieldFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createdField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modifiedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(fieldTable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fieldRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectRelease, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nofSpotsLabel)
                    .addComponent(fieldNofSpots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void fieldTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldTableActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_fieldTableActionPerformed

private void jButtonSelectReleaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectReleaseActionPerformed
    final JPanel panel = new JPanel();
    Vector<String> possibilities = DBUtils.getAllReleases();
    final JComboBox combo = new JComboBox(possibilities);
    combo.setEditable(true);
    panel.add(combo);
    if (JOptionPane.showConfirmDialog(null, panel,
            "choose release", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        this.fieldRelease.setText((String) combo.getSelectedItem());
    }
}//GEN-LAST:event_jButtonSelectReleaseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField createdField;
    private javax.swing.JTextField fieldFile;
    private javax.swing.JTextField fieldID;
    private javax.swing.JTextField fieldName;
    private javax.swing.JTextField fieldNofSpots;
    private javax.swing.JTextField fieldRelease;
    private javax.swing.JTextField fieldTable;
    private javax.swing.JButton jButtonSelectRelease;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField modifiedField;
    private javax.swing.JLabel name;
    private javax.swing.JLabel nofSpotsLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

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
            if (field instanceof javax.swing.JButton) {
                ((javax.swing.JButton) field).setEnabled(b);
                continue;
            }

        }

        //never editable...
        this.fieldNofSpots.setEditable(false);
        this.modifiedField.setEditable(false);
        this.createdField.setEditable(false);
        this.fieldID.setEditable(false);
        this.fieldFile.setEditable(false);
        this.fieldTable.setEditable(false);
        this.fieldName.setEditable(false);

    }

    static void view(PlatformData p, boolean edit) {
        JDialog d = new JDialog();
        d.setLocationRelativeTo(null);

        PlatformDataView v = new PlatformDataView(p, edit);
        d.setTitle("platform data view");
        d.add(v);
        d.pack();
        d.setVisible(true);
        d.setLocationRelativeTo(null);
    }
}
