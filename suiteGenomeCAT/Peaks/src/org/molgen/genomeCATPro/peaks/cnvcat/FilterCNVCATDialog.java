package org.molgen.genomeCATPro.peaks.cnvcat;

import java.awt.Color;
import java.awt.Cursor;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.molgen.genomeCATPro.peaks.CNVCATTopComponent;
import org.molgen.genomeCATPro.peaks.cnvcat.util.ColorEditor;
import org.molgen.genomeCATPro.peaks.cnvcat.util.ColorRenderer;
import org.molgen.genomeCATPro.peaks.cnvcat.util.ListRenderer;
import org.openide.windows.TopComponent;

/**
 * @name FilterCNVCATDialog
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>.
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
 * 
 */
/**
 * 260612 kt    setGlobalPosition getActRegion
 * @author tebel
 */
public class FilterCNVCATDialog extends javax.swing.JDialog {

    private CNVCATFrame frame = null;
    private AberrationManagerCNVCAT aberrationManager = null;

    public AberrationManagerCNVCAT getAberrationManager() {
        return aberrationManager;
    }

    public void setAberrationManagerCGH(AberrationManagerCNVCAT _mng) {
        this.aberrationManager = _mng;
    }
    private String selectCaseId = "distinct for each track";
    private String selectPhenotype = "grouped phenotype";
    private String selectTrackParam = "grouped by processing param";
    private String selectSampleName = "grouped by sample name";
    private String selectNone = "all into one group";
    private List groupListNew = org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector());
    Vector colnamesGroupColorTable = new Vector();
    String release = Defaults.GenomeRelease.hg18.toString();

    /** Creates new form NewJDialog */
    public FilterCNVCATDialog(CNVCATFrame parent) {
        super((JFrame) null, true);

        if (parent != null) {
            this.frame = parent;
            this.release = parent.release;
            this.aberrationManager = (AberrationManagerCNVCAT) parent.AberrationManager();
        } else {
            this.release = null;
            this.frame = CNVCATTopComponent.findInstance(
                    Defaults.GenomeRelease.toRelease(release)).getFrame();
            this.aberrationManager = (AberrationManagerCNVCAT) frame.getAberrationManager();
        }
        this.initAberrationManager();
        colnamesGroupColorTable.add("Group");
        colnamesGroupColorTable.add("Color");
        colnamesGroupColorTable.add("Disabled");
        initComponents();

        //this.jComboBoxRelease.setSelectedIndex(2);
        jTextFieldTrackId.setText("");
        jTextFieldParameter.setText("");


        this.jComboBoxProc.setSelectedIndex(-1);
        this.jComboBoxRelease.setSelectedIndex(-1);

        //this.jComboBoxType.setSelectedIndex(1);
        setLocationRelativeTo(null);

    }

    void initAberrationManager() {

        this.aberrationManager.setGroupByCaseID(false);
        this.aberrationManager.setGroupByPhenotype(false);
        this.aberrationManager.setGroupByNone(true);
        aberrationManager.filterAberrationIds(new String[]{"", "", "", "", ""});
        this.groupListNew = this.aberrationManager.getColorGroupList();

    }

    String getRelease() {
        return this.release;
    }

    public void resetToRelease(String release) {
        this.release = release;
        this.frame = CNVCATTopComponent.findInstance(
                release != null ? Defaults.GenomeRelease.toRelease(release) : null).getFrame();
        this.aberrationManager = (AberrationManagerCNVCAT) frame.getAberrationManager();
        this.rebindTable();
        this.initAberrationManager();
        this.rebindTable();
        this.setChromList(this.release);
    }

    private void doFilter() {

        if (this.jComboBoxRelease.getSelectedIndex() < 0) {

            this.resetToRelease(null);

            JOptionPane.showMessageDialog(this, "WARNING:" +
                    "please select genome release!!!");
            return;
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            if (this.release == null //vorher alt
                    ||
                    // anderes release ausgewählt
                    !this.release.contentEquals(this.jComboBoxRelease.getSelectedItem().toString())) {
                this.resetToRelease(this.jComboBoxRelease.getSelectedItem().toString());

            }
            String trackId = this.jTextFieldTrackId.getText().isEmpty() ? "%" : new String(this.jTextFieldTrackId.getText());
            String param = this.jTextFieldParameter.getText().isEmpty() ? "%" : new String(this.jTextFieldParameter.getText());
            String proc = this.jComboBoxProc.getSelectedIndex() >= 0 ? this.jComboBoxProc.getSelectedItem().toString() : "%";

            if (!this.jCheckBoxInclSample.isSelected()) {

                aberrationManager.filterAberrationIds(new String[]{release, trackId, proc, param});
            } else {
                String sample = this.fieldSampleName.getText().isEmpty() ? "%" : new String(this.fieldSampleName.getText());
                String pheno = this.fieldSamplePhenotype.getText().isEmpty() ? "%" : new String(this.fieldSamplePhenotype.getText());
                aberrationManager.filterAberrationIdsPlusSample(new String[]{release, trackId, proc, param, sample, pheno});
            }
            if (this.jCheckBoxRegion.isSelected()) {
                this.aberrationManager.filterLocation(
                        this.jComboBoxChromList.getSelectedItem().toString(),
                        ((Number) this.jFormattedTextFieldStart.getValue()).longValue(),
                        ((Number) this.jFormattedTextFieldEnd.getValue()).longValue());
            }
            this.jLabelCount.setText("Items: " + this.aberrationManager.getAllAberrationIds().size());
        } catch (Exception e) {
            Logger.getLogger(FilterCNVCATDialog.class.getName()).log(
                    Level.SEVERE, "error: ", e);
            JOptionPane.showMessageDialog(this, "Error:" + e.getMessage());
        }


        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldParameter = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jComboBoxProc = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldTrackId = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jComboBoxChromList = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jFormattedTextFieldStart = new javax.swing.JFormattedTextField();
        jFormattedTextFieldEnd = new javax.swing.JFormattedTextField();
        jPanel7 = new javax.swing.JPanel();
        fieldSampleName = new javax.swing.JTextField();
        fieldSamplePhenotype = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabelCount = new javax.swing.JLabel();
        jCheckBoxRegion = new javax.swing.JCheckBox();
        jCheckBoxInclSample = new javax.swing.JCheckBox();
        jButtonFilter = new javax.swing.JToggleButton();
        jButtonReset = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonSelectAll = new javax.swing.JButton();
        jButtonDeselectAll = new javax.swing.JButton();
        jLabelCountSelected = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSetGroupColor = new javax.swing.JTable();
        jComboBoxGroupColor = new javax.swing.JComboBox(
            new javax.swing.DefaultComboBoxModel(
                new String[]{
                    selectPhenotype,
                    selectSampleName,
                    selectTrackParam,
                    selectCaseId,

                    selectNone}
            ) );
            jButtonSetColor = new javax.swing.JButton();
            jButtonResetColor = new javax.swing.JButton();
            jLabel7 = new javax.swing.JLabel();
            jPanel4 = new javax.swing.JPanel();
            jButtonOK = new javax.swing.JButton();
            jButtonCancel = new javax.swing.JButton();
            jLabel8 = new javax.swing.JLabel();
            jComboBoxRelease = new javax.swing.JComboBox();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("search database and load regions");
            setName("Form"); // NOI18N

            jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("select filter criteria"));
            jPanel1.setName("jPanel1"); // NOI18N

            jLabel1.setName("jLabel1"); // NOI18N

            jLabel2.setName("jLabel2"); // NOI18N

            jLabel3.setName("jLabel3"); // NOI18N

            jLabel4.setName("jLabel4"); // NOI18N

            jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "track specific criteria", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N
            jPanel5.setName("jPanel5"); // NOI18N

            jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
            jLabel6.setText("Parameter [Processing]:");
            jLabel6.setName("jLabel6"); // NOI18N

            jTextFieldParameter.setMinimumSize(new java.awt.Dimension(4, 15));
            jTextFieldParameter.setName("jTextFieldParameter"); // NOI18N
            jTextFieldParameter.setPreferredSize(new java.awt.Dimension(4, 18));
            jTextFieldParameter.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jTextFieldParameterActionPerformed(evt);
                }
            });

            jLabel10.setFont(new java.awt.Font("Dialog", 0, 12));
            jLabel10.setText("Processing Method:");
            jLabel10.setName("jLabel10"); // NOI18N

            jComboBoxProc.setModel(new javax.swing.DefaultComboBoxModel(
                TrackService.getAllProcs()
            ));
            jComboBoxProc.setName("jComboBoxProc"); // NOI18N

            jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
            jLabel5.setText("Track Name:");
            jLabel5.setName("jLabel5"); // NOI18N

            jTextFieldTrackId.setMinimumSize(new java.awt.Dimension(4, 15));
            jTextFieldTrackId.setName("jTextFieldTrackId"); // NOI18N
            jTextFieldTrackId.setPreferredSize(new java.awt.Dimension(4, 18));
            jTextFieldTrackId.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jTextFieldTrackIdActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
            jPanel5.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextFieldTrackId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldParameter, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addComponent(jComboBoxProc, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(jLabel6))
                    .addContainerGap())
            );

            jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jComboBoxProc, jTextFieldParameter, jTextFieldTrackId});

            jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jLabel5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextFieldTrackId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel10)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxProc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextFieldParameter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(75, 75, 75))
            );

            jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "genomic location", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N
            jPanel6.setName("jPanel6"); // NOI18N

            jComboBoxChromList.setModel(
                new DefaultComboBoxModel(
                    this.release == null ? new Vector() :
                    new Vector(CytoBandManagerImpl.stGetChroms(
                        GenomeRelease.toRelease(this.release)
                    )))
                );
                jComboBoxChromList.setName("jComboBoxChromList"); // NOI18N
                jComboBoxChromList.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jComboBoxChromListActionPerformed(evt);
                    }
                });
                jComboBoxChromList.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                    public void propertyChange(java.beans.PropertyChangeEvent evt) {
                        jComboBoxChromListPropertyChange(evt);
                    }
                });

                jLabel12.setFont(new java.awt.Font("Dialog", 0, 12));
                jLabel12.setText("Chromosome:");
                jLabel12.setName("jLabel12"); // NOI18N

                jLabel13.setFont(new java.awt.Font("Dialog", 0, 12));
                jLabel13.setText("Start:");
                jLabel13.setName("jLabel13"); // NOI18N

                jLabel14.setFont(new java.awt.Font("Dialog", 0, 12));
                jLabel14.setText("Stop:");
                jLabel14.setName("jLabel14"); // NOI18N

                jFormattedTextFieldStart.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
                jFormattedTextFieldStart.setText("0");
                jFormattedTextFieldStart.setMinimumSize(new java.awt.Dimension(4, 15));
                jFormattedTextFieldStart.setName("jFormattedTextFieldStart"); // NOI18N
                jFormattedTextFieldStart.setPreferredSize(new java.awt.Dimension(12, 18));

                jFormattedTextFieldEnd.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
                jFormattedTextFieldEnd.setText("0");
                jFormattedTextFieldEnd.setMinimumSize(new java.awt.Dimension(4, 15));
                jFormattedTextFieldEnd.setName("jFormattedTextFieldEnd"); // NOI18N
                jFormattedTextFieldEnd.setPreferredSize(new java.awt.Dimension(12, 18));

                javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
                jPanel6.setLayout(jPanel6Layout);
                jPanel6Layout.setHorizontalGroup(
                    jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addGap(67, 67, 67)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jFormattedTextFieldEnd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                    .addComponent(jFormattedTextFieldStart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                    .addComponent(jComboBoxChromList, 0, 165, Short.MAX_VALUE))))
                        .addContainerGap())
                );
                jPanel6Layout.setVerticalGroup(
                    jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jComboBoxChromList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jFormattedTextFieldStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jFormattedTextFieldEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(100, Short.MAX_VALUE))
                );

                jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "sample specific criteria", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N
                jPanel7.setName("jPanel7"); // NOI18N

                fieldSampleName.setToolTipText("set sample name or parts of it ");
                fieldSampleName.setMinimumSize(new java.awt.Dimension(4, 15));
                fieldSampleName.setName("fieldSampleName"); // NOI18N
                fieldSampleName.setPreferredSize(new java.awt.Dimension(4, 18));
                fieldSampleName.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        fieldSampleNameActionPerformed(evt);
                    }
                });

                fieldSamplePhenotype.setMinimumSize(new java.awt.Dimension(4, 15));
                fieldSamplePhenotype.setName("fieldSamplePhenotype"); // NOI18N
                fieldSamplePhenotype.setPreferredSize(new java.awt.Dimension(4, 18));
                fieldSamplePhenotype.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        fieldSamplePhenotypeActionPerformed(evt);
                    }
                });

                jLabel9.setFont(new java.awt.Font("Dialog", 0, 12));
                jLabel9.setText("Sample Name:");
                jLabel9.setName("jLabel9"); // NOI18N

                jLabel15.setFont(new java.awt.Font("Dialog", 0, 12));
                jLabel15.setText("Phenotype:");
                jLabel15.setName("jLabel15"); // NOI18N

                javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
                jPanel7.setLayout(jPanel7Layout);
                jPanel7Layout.setHorizontalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addGap(136, 136, 136))
                                    .addComponent(fieldSamplePhenotype, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addGap(80, 80, 80))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(fieldSampleName, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                .addGap(80, 80, 80))))
                );

                jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldSampleName, fieldSamplePhenotype});

                jPanel7Layout.setVerticalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(1, 1, 1)
                        .addComponent(fieldSampleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldSamplePhenotype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(102, Short.MAX_VALUE))
                );

                jPanel7Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {fieldSampleName, fieldSamplePhenotype});

                jLabelCount.setName("jLabelCount"); // NOI18N

                jCheckBoxRegion.setFont(new java.awt.Font("Dialog", 0, 12));
                jCheckBoxRegion.setText("search data for a genomic location");
                jCheckBoxRegion.setName("jCheckBoxRegion"); // NOI18N

                jCheckBoxInclSample.setFont(new java.awt.Font("Dialog", 0, 12));
                jCheckBoxInclSample.setText("search data for specific samples");
                jCheckBoxInclSample.setName("jCheckBoxInclSample"); // NOI18N

                jButtonFilter.setText("filter");
                jButtonFilter.setName("jButtonFilter"); // NOI18N
                jButtonFilter.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButtonFilterActionPerformed(evt);
                    }
                });

                jButtonReset.setText("Reset");
                jButtonReset.setName("jButtonReset"); // NOI18N
                jButtonReset.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButtonResetActionPerformed(evt);
                    }
                });

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxInclSample)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jCheckBoxRegion)
                                .addGap(1220, 1220, 1220))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButtonFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonReset, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(38, 38, 38)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(529, 529, 529)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(130, 130, 130)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel3)
                                                            .addComponent(jLabel4))
                                                        .addContainerGap())
                                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 395, Short.MAX_VALUE)
                                                .addComponent(jLabel2))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelCount, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                );

                jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonFilter, jButtonReset});

                jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(74, 74, 74)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(98, 98, 98)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2))
                                            .addComponent(jLabelCount, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel3)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxInclSample)
                            .addComponent(jCheckBoxRegion))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jButtonFilter)
                            .addComponent(jButtonReset)))
                );

                jPanel5.getAccessibleContext().setAccessibleName("FILTER");

                jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "data sets (tracks)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N
                jPanel2.setName("jPanel2"); // NOI18N

                jScrollPane1.setName("jScrollPane1"); // NOI18N

                jTable1.setName("jTable1"); // NOI18N

                org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${aberrationManager.allAberrationIds}");
                org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTable1);
                org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${trackId}"));
                columnBinding.setColumnName("Track Id");
                columnBinding.setColumnClass(String.class);
                columnBinding.setEditable(false);
                columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${countAberrations}"));
                columnBinding.setColumnName("#");
                columnBinding.setColumnClass(Integer.class);
                columnBinding.setEditable(false);
                columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sampleNames}"));
                columnBinding.setColumnName("Sample Names");
                columnBinding.setColumnClass(java.util.List.class);
                columnBinding.setEditable(false);
                columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${phenotypes}"));
                columnBinding.setColumnName("Phenotypes");
                columnBinding.setColumnClass(java.util.List.class);
                columnBinding.setEditable(false);
                columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${paramAsString}"));
                columnBinding.setColumnName("Processing");
                columnBinding.setColumnClass(String.class);
                columnBinding.setEditable(false);
                columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selected}"));
                columnBinding.setColumnName("Selected");
                columnBinding.setColumnClass(Boolean.class);
                columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${color}"));
                columnBinding.setColumnName("Color");
                columnBinding.setColumnClass(java.awt.Color.class);
                bindingGroup.addBinding(jTableBinding);
                jTableBinding.bind();
                jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                    public void propertyChange(java.beans.PropertyChangeEvent evt) {
                        jTable1PropertyChange(evt);
                    }
                });
                jScrollPane1.setViewportView(jTable1);
                jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
                jTable1.getColumnModel().getColumn(1).setPreferredWidth(50);
                jTable1.getColumnModel().getColumn(4).setPreferredWidth(20);
                jTable1.getColumnModel().getColumn(6).setMinWidth(40);
                jTable1.getColumnModel().getColumn(6).setPreferredWidth(40);
                jTable1.getColumnModel().getColumn(6).setMaxWidth(40);
                jTable1.setDefaultEditor(Color.class, new ColorEditor());
                jTable1.setDefaultRenderer(Color.class, new ColorRenderer(true));

                jButtonSelectAll.setText("Select All");
                jButtonSelectAll.setName("jButtonSelectAll"); // NOI18N
                jButtonSelectAll.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButtonSelectAllActionPerformed(evt);
                    }
                });

                jButtonDeselectAll.setText("Deselect All");
                jButtonDeselectAll.setName("jButtonDeselectAll"); // NOI18N
                jButtonDeselectAll.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButtonDeselectAllActionPerformed(evt);
                    }
                });

                jLabelCountSelected.setName("jLabelCountSelected"); // NOI18N

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabelCountSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 611, Short.MAX_VALUE)
                                .addComponent(jButtonSelectAll, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDeselectAll, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelCountSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jButtonDeselectAll)
                                .addComponent(jButtonSelectAll)))
                        .addContainerGap())
                );

                jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Group by Color", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N
                jPanel3.setName("jPanel3"); // NOI18N

                jScrollPane2.setName("jScrollPane2"); // NOI18N

                groupListNew = this.aberrationManager != null ? this.aberrationManager.getColorGroupList() : new Vector();
                jTableSetGroupColor.setModel(
                    new GroupColorTableModel((Vector) groupListNew)
                );
                jTableSetGroupColor.setDefaultEditor(Color.class, new ColorEditor());
                jTableSetGroupColor.setDefaultRenderer(Color.class, new ColorRenderer(true));
                jTableSetGroupColor.setName("jTableSetGroupColor");
                jScrollPane2.setViewportView(jTableSetGroupColor);

                jComboBoxGroupColor.setSelectedIndex(0);
                jComboBoxGroupColor.setName("jComboBoxGroupColor"); // NOI18N
                jComboBoxGroupColor.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jComboBoxGroupColorActionPerformed(evt);
                    }
                });

                jButtonSetColor.setText("Set Color");
                jButtonSetColor.setName("jButtonSetColor"); // NOI18N
                jButtonSetColor.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButtonSetColorActionPerformed(evt);
                    }
                });

                jButtonResetColor.setText("Reset Color");
                jButtonResetColor.setName("jButtonResetColor"); // NOI18N

                jLabel7.setText("set Color...");
                jLabel7.setName("jLabel7"); // NOI18N

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButtonSetColor, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonResetColor))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jComboBoxGroupColor, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 573, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxGroupColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonSetColor)
                            .addComponent(jButtonResetColor))
                        .addContainerGap(41, Short.MAX_VALUE))
                );

                jPanel4.setName("jPanel4"); // NOI18N

                jButtonOK.setText("OK");
                jButtonOK.setName("jButtonOK"); // NOI18N
                jButtonOK.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButtonOKActionPerformed(evt);
                    }
                });

                jButtonCancel.setText("Cancel");
                jButtonCancel.setName("jButtonCancel"); // NOI18N
                jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButtonCancelActionPerformed(evt);
                    }
                });

                javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
                jPanel4.setLayout(jPanel4Layout);
                jPanel4Layout.setHorizontalGroup(
                    jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(501, Short.MAX_VALUE)
                        .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(275, 275, 275))
                );

                jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonCancel, jButtonOK});

                jPanel4Layout.setVerticalGroup(
                    jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonOK)
                        .addComponent(jButtonCancel))
                );

                jLabel8.setText("  select genome release:");
                jLabel8.setName("jLabel8"); // NOI18N

                jComboBoxRelease.setModel(new javax.swing.DefaultComboBoxModel(
                    new String [] {

                        Defaults.GenomeRelease.hg17.toString(),
                        Defaults.GenomeRelease.hg18.toString(),
                        Defaults.GenomeRelease.hg19.toString(),
                    }));
                    jComboBoxRelease.setName("jComboBoxRelease"); // NOI18N
                    jComboBoxRelease.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            jComboBoxReleaseActionPerformed(evt);
                        }
                    });

                    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                    getContentPane().setLayout(layout);
                    layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jComboBoxRelease, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, 0, 946, Short.MAX_VALUE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                    layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(jComboBoxRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                    );

                    bindingGroup.bind();

                    pack();
                }// </editor-fold>//GEN-END:initComponents

private void jTextFieldTrackIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTrackIdActionPerformed
    this.jComboBoxGroupColor.setSelectedItem(this.selectCaseId);
}//GEN-LAST:event_jTextFieldTrackIdActionPerformed

private void jButtonFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFilterActionPerformed
    doFilter();
    updateGroupColorList();
}//GEN-LAST:event_jButtonFilterActionPerformed

private void jButtonSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectAllActionPerformed
    List<AberrantRegions> aberrationList = (List<AberrantRegions>) this.aberrationManager.getAllAberrationIds();
    for (int i = 0; i < aberrationList.size(); i++) {
        aberrationList.get(i).setSelected(true);
    }
    updateGroupColorList();
}//GEN-LAST:event_jButtonSelectAllActionPerformed

private void jButtonDeselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeselectAllActionPerformed
    List<AberrantRegions> aberrationList = (List<AberrantRegions>) this.aberrationManager.getAllAberrationIds();
    for (int i = 0; i < aberrationList.size(); i++) {
        aberrationList.get(i).setSelected(false);
    }
    updateGroupColorList();
}//GEN-LAST:event_jButtonDeselectAllActionPerformed

private void jButtonSetColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetColorActionPerformed
    Color c;
    String id;
    boolean disabled;
    List<AberrantRegions> aberrationList = (List<AberrantRegions>) ((AberrationManagerCNVCAT) this.aberrationManager).getSelectedAberrationIds();


    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    for (AberrantRegions a : aberrationList) {
        for (int i = 0; i < this.groupListNew.size(); i++) {
            c = (Color) ((Vector) groupListNew.get(i)).get(1);
            id = (String) ((Vector) groupListNew.get(i)).get(0);
            disabled = (Boolean) ((Vector) groupListNew.get(i)).get(2);
            if (disabled) {
                continue;
            }
            System.out.println("set Group Color for " + id);
            if (this.aberrationManager.isGroupByCaseID()) {
                System.out.println("case id");
                if (a.getTrackId().compareTo(id) == 0) {
                    a.setColor(c);
                }
            } else if (this.aberrationManager.isGroupByParam()) {
                System.out.println("param");
                if (a.getParamAsString().compareTo(id) == 0) {
                    a.setColor(c);
                }
            } else if (this.aberrationManager.isGroupBySample()) {
                System.out.println("sample");
                System.out.println(a.getSampleNames().toString());
                for (String s : a.getSampleNames()) {
                    if (s.equalsIgnoreCase(id)) {
                        a.setColor(c);
                        break;
                    }

                }
            } else if (this.aberrationManager.isGroupByPhenotype()) {
                System.out.println("phenotype");
                System.out.println(a.getPhenotypes().toString());
                for (String p : a.getPhenotypes()) {
                    if (p.equalsIgnoreCase(id)) {
                        a.setColor(c);
                        break;
                    }
                }
            } else {//none

                a.setColor(c);
            }
        }
    }
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_jButtonSetColorActionPerformed

private void jComboBoxGroupColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGroupColorActionPerformed
    String selected = this.jComboBoxGroupColor.getSelectedItem().toString();

    if (selected.compareTo(this.selectCaseId) == 0) {
        this.aberrationManager.setGroupByCaseID(true);
        this.aberrationManager.setGroupByPhenotype(false);
        this.aberrationManager.setGroupByNone(false);
        this.aberrationManager.setGroupBySample(false);
        this.aberrationManager.setGroupByParam(false);
    } else if (selected.compareTo(this.selectPhenotype) == 0) {
        this.aberrationManager.setGroupByCaseID(false);
        this.aberrationManager.setGroupByPhenotype(true);
        this.aberrationManager.setGroupByNone(false);
        this.aberrationManager.setGroupBySample(false);
        this.aberrationManager.setGroupByParam(false);
    } else if (selected.compareTo(this.selectSampleName) == 0) {
        this.aberrationManager.setGroupByCaseID(false);
        this.aberrationManager.setGroupByPhenotype(false);
        this.aberrationManager.setGroupByNone(false);
        this.aberrationManager.setGroupBySample(true);
        this.aberrationManager.setGroupByParam(false);
    } else if (selected.compareTo(this.selectTrackParam) == 0) {
        this.aberrationManager.setGroupByCaseID(false);
        this.aberrationManager.setGroupByPhenotype(false);
        this.aberrationManager.setGroupByNone(false);
        this.aberrationManager.setGroupBySample(false);
        this.aberrationManager.setGroupByParam(true);
    } else if (selected.compareTo(this.selectNone) == 0) {
        this.aberrationManager.setGroupByCaseID(false);
        this.aberrationManager.setGroupByPhenotype(false);
        this.aberrationManager.setGroupByNone(true);
    }

    // kt 16.09.09 doFilter(); 
    // kt 16.09.09 reset all  color
    List<AberrantRegions> aberrationList = (List<AberrantRegions>) this.aberrationManager.getAllAberrationIds();
    for (AberrantRegions a : aberrationList) {
        a.setColor(Color.black);
    }

    updateGroupColorList();
}//GEN-LAST:event_jComboBoxGroupColorActionPerformed

private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
    try {
        jTextFieldTrackId.setText("");
        jTextFieldParameter.setText("");

        this.resetToRelease(null);

        this.rebindTable();

        //this.initAberrationManager();
        this.jComboBoxProc.setSelectedIndex(-1);
        this.jComboBoxRelease.setSelectedIndex(-1);
        this.fieldSampleName.setText("");
        this.fieldSamplePhenotype.setText("");

        this.jCheckBoxInclSample.setSelected(false);
        this.jCheckBoxRegion.setSelected(false);
        updateGroupColorList();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error:" + e.getMessage());
        Logger.getLogger(FilterCNVCATDialog.class.getName()).log(
                Level.SEVERE, "error: ", e);
    }
}//GEN-LAST:event_jButtonResetActionPerformed

private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed

    this.frame.updateAberrationLists();
    setVisible(false);
    TopComponent win = CNVCATTopComponent.findInstance(
            Defaults.GenomeRelease.toRelease(this.frame.release));
    win.open();
    win.requestActive();


}//GEN-LAST:event_jButtonOKActionPerformed

private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    //aberrationManager.setAllAberrationIds(new Vector()); //clear


    setVisible(false);
}//GEN-LAST:event_jButtonCancelActionPerformed

private void jTextFieldParameterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldParameterActionPerformed
    this.jComboBoxGroupColor.setSelectedItem(this.selectPhenotype);
}//GEN-LAST:event_jTextFieldParameterActionPerformed

private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
    updateGroupColorList();
}//GEN-LAST:event_jTable1PropertyChange

private void jComboBoxChromListPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jComboBoxChromListPropertyChange
    this.setPosition();//GEN-LAST:event_jComboBoxChromListPropertyChange
    }

    public void rebindTable() {
        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${aberrationManager.allAberrationIds}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTable1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("ID");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${trackId}"));
        columnBinding.setColumnName("Track Id");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);

        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${countAberrations}"));
        columnBinding.setColumnName("#");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);

        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${paramAsString}"));
        columnBinding.setColumnName("Processing");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sampleNames}"));
        columnBinding.setColumnName("Samples");
        columnBinding.setColumnClass(List.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${phenotypes}"));
        columnBinding.setColumnName("Phenotypes");
        columnBinding.setColumnClass(List.class);

        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selected}"));
        columnBinding.setColumnName("Selected");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${color}"));
        columnBinding.setColumnName("Color");
        columnBinding.setColumnClass(java.awt.Color.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTable1PropertyChange(evt);
            }
        });
        jTable1.setDefaultRenderer(List.class, new ListRenderer());
    }

    public void setChromList(String release) {
        this.jComboBoxChromList.setModel(new DefaultComboBoxModel(
                this.release == null ? new Vector() : new Vector(CytoBandManagerImpl.stGetChroms(
                GenomeRelease.toRelease(release)))));

    }

    public void setPosition() {

        this.jFormattedTextFieldStart.setValue(0);
        if (this.release != null) {
            this.jFormattedTextFieldEnd.setValue(
                    CytoBandManagerImpl.getLast(
                    Defaults.GenomeRelease.toRelease(release),
                    this.jComboBoxChromList.getSelectedItem().toString()).getChromEnd());
        } else {
            this.jFormattedTextFieldEnd.setValue(0);
        }
    }
private void jComboBoxChromListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxChromListActionPerformed
    this.setPosition();
}//GEN-LAST:event_jComboBoxChromListActionPerformed

private void fieldSampleNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldSampleNameActionPerformed
    this.jCheckBoxInclSample.setSelected(this.fieldSampleName.getText() == null ? false : true);
}//GEN-LAST:event_fieldSampleNameActionPerformed

private void fieldSamplePhenotypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldSamplePhenotypeActionPerformed
    this.jCheckBoxInclSample.setSelected(this.fieldSamplePhenotype.getText() == null ? false : true);
}//GEN-LAST:event_fieldSamplePhenotypeActionPerformed

private void jComboBoxReleaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxReleaseActionPerformed
    if (this.jComboBoxRelease.getSelectedIndex() >= 0) {
        this.setChromList(this.jComboBoxRelease.getSelectedItem().toString());
    }
}//GEN-LAST:event_jComboBoxReleaseActionPerformed
    @Deprecated
    private void setGlobalPosition() {
        System.out.println("check");
        this.jCheckBoxRegion.setSelected(true);
    /*  if (this.jCheckBoxGlobalRegion.isSelected()) {
    
    System.out.println("check true");
    Region r = WebPositionPanel.getActPosition();
    
    if (r != null) {
    this.jComboBoxChromList.setSelectedItem(r.getChrom());
    
    this.jFormattedTextFieldStart.setValue(r.getChromStart());
    this.jFormattedTextFieldEnd.setValue(r.getChromEnd());
    } else {
    this.setPosition();
    }
    }*/
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fieldSampleName;
    private javax.swing.JTextField fieldSamplePhenotype;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDeselectAll;
    private javax.swing.JToggleButton jButtonFilter;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JButton jButtonResetColor;
    private javax.swing.JButton jButtonSelectAll;
    private javax.swing.JButton jButtonSetColor;
    private javax.swing.JCheckBox jCheckBoxInclSample;
    private javax.swing.JCheckBox jCheckBoxRegion;
    private javax.swing.JComboBox jComboBoxChromList;
    private javax.swing.JComboBox jComboBoxGroupColor;
    private javax.swing.JComboBox jComboBoxProc;
    private javax.swing.JComboBox jComboBoxRelease;
    private javax.swing.JFormattedTextField jFormattedTextFieldEnd;
    private javax.swing.JFormattedTextField jFormattedTextFieldStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCount;
    private javax.swing.JLabel jLabelCountSelected;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTableSetGroupColor;
    private javax.swing.JTextField jTextFieldParameter;
    private javax.swing.JTextField jTextFieldTrackId;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private void updateGroupColorList() {
        this.groupListNew.clear();
        this.groupListNew.addAll(
                this.aberrationManager != null ? this.aberrationManager.getColorGroupList() : Collections.EMPTY_LIST);
        this.jTableSetGroupColor.setModel(new GroupColorTableModel((Vector) groupListNew));
        this.jLabelCountSelected.setText("Selected: " +
                (this.aberrationManager != null ? this.aberrationManager.getSelectedAberrationIds().size() : 0));
    }

    class GroupColorTableModel extends DefaultTableModel {

        public GroupColorTableModel(java.util.Vector data) {

            super(data, colnamesGroupColorTable);
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }
    }
}
