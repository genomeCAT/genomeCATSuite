package org.molgen.genomeCATPro.guimodul.track;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.beansbinding.Converter;

import org.molgen.genomeCATPro.datadb.dbentities.SampleInTrack;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.guimodul.SwingUtils;
import org.molgen.genomeCATPro.guimodul.experiment.SampleDetailView;

/**
 * @name TrackDataView
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package.
 * Copyright Sep 1, 2010 Katrin Tebel <tebel at molgen.mpg.de>.
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
/**
 * 300512   new Method forwardSamples
 * @author tebel
 */
public class TrackDataView extends javax.swing.JPanel {

    private Track track;
    boolean edit = false;

    /** Creates new form TrackDataView */
    @Deprecated
    public TrackDataView() {
        this.track = new Track();
        edit = false;

        initComponents();
        setAllFieldsEditable(edit);
        Logger.getLogger(TrackDataView.class.getName()).log(Level.INFO,
                "constructor called");
    }

    /**
     * 
     * @param _edit
     */
    public TrackDataView(boolean _edit) {
        this.track = new Track();
        this.edit = _edit;
        initComponents();
        this.setAllFieldsEditable(edit);
        this.buttonShowSample.setText((edit ? "edit " : "show") + "selected Sample");
    }

    /**
     * 
     * @param _data
     * @param _edit
     */
    public TrackDataView(Track _data, boolean _edit) {
        this.track = new Track();
        this.track.copy(_data);
        this.edit = _edit;
        initComponents();
        Logger.getLogger(TrackDataView.class.getName()).log(Level.INFO,
                "constructor called " + _data.toString());
        this.setAllFieldsEditable(edit);
    }

    public Track getTrack() {
        return this.track;
    }

    public void setTrack(Track d) {
        this.track = d;
    }

    public JTextField getGenomeReleaseField() {
        return genomeReleaseField;
    }

    public void setGenomeReleaseField(JTextField genomeReleaseField) {
        this.genomeReleaseField = genomeReleaseField;
    }

    public JButton getJButtonSelectRelease() {
        return jButtonSelectRelease;
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
            if (field instanceof javax.swing.JButton) {
                ((javax.swing.JButton) field).setEnabled(b);
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
        //never editable...
        this.parentIDField.setEditable(false);
        this.parentTrackIDField.setEditable(false);
        this.jTextFieldOwner.setEditable(false);
        this.tableDataField.setEditable(false);
        this.clazzField.setEditable(false);
        this.dataTypeField.setEditable(false);
        this.fieldCreated.setEditable(false);
        this.fieldModified.setEditable(false);
        this.maxRatioField.setEditable(false);
        this.minRatioField.setEditable(false);
        this.meanField.setEditable(false);
        this.medianField.setEditable(false);
        this.varianceField.setEditable(false);
        this.stddevField.setEditable(false);
        this.nofSpotsField.setEditable(false);
        this.originalFileField.setEditable(false);
    // this.fieldDesc.setEditable(b);



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

        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        nameLabel_req = new javax.swing.JLabel();
        genomeReleaseLabel_req = new javax.swing.JLabel();
        parentIDLabel = new javax.swing.JLabel();
        tableDataLabel = new javax.swing.JLabel();
        dataTypeLabel = new javax.swing.JLabel();
        clazzLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        genomeReleaseField = new javax.swing.JTextField();
        fieldModified = new javax.swing.JTextField();
        fieldCreated = new javax.swing.JTextField();
        parentIDField = new javax.swing.JTextField();
        tableDataField = new javax.swing.JTextField();
        dataTypeField = new javax.swing.JTextField();
        clazzField = new javax.swing.JTextField();
        jButtonSelectRelease = new javax.swing.JButton();
        jTextFieldOwner = new javax.swing.JTextField();
        parentTrackIDLabel = new javax.swing.JLabel();
        parentTrackIDField = new javax.swing.JTextField();
        jLabelRequired = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        nofSpotsLabel = new javax.swing.JLabel();
        stddevField = new javax.swing.JTextField();
        nofSpotsField = new javax.swing.JTextField();
        meanLabel = new javax.swing.JLabel();
        meanField = new javax.swing.JTextField();
        medianLabel = new javax.swing.JLabel();
        medianField = new javax.swing.JTextField();
        varianceLabel = new javax.swing.JLabel();
        varianceField = new javax.swing.JTextField();
        stddevLabel = new javax.swing.JLabel();
        minRatioLabel = new javax.swing.JLabel();
        maxRatioLabel = new javax.swing.JLabel();
        minRatioField = new javax.swing.JTextField();
        maxRatioField = new javax.swing.JTextField();
        originalFileLabel = new javax.swing.JLabel();
        originalFileField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fieldDesc = new javax.swing.JTextArea();
        procProcessingLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        fieldParamProc = new javax.swing.JTextArea();
        paramProcessingLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        fieldProc = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableSamples = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        buttonShowSample = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTable1);

        setMaximumSize(new java.awt.Dimension(800, 500));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setMaximumSize(new java.awt.Dimension(300, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 100));

        nameLabel_req.setFont(new java.awt.Font("Dialog", 0, 12));
        nameLabel_req.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nameLabel_req.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.nameLabel.text")); // NOI18N

        genomeReleaseLabel_req.setFont(new java.awt.Font("Dialog", 0, 12));
        genomeReleaseLabel_req.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        genomeReleaseLabel_req.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.genomeReleaseLabel.text")); // NOI18N

        parentIDLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        parentIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        parentIDLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.parentIDLabel.text")); // NOI18N

        tableDataLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        tableDataLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tableDataLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableDataLabel.text")); // NOI18N

        dataTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        dataTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dataTypeLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.dataTypeLabel.text")); // NOI18N

        clazzLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        clazzLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        clazzLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.clazzLabel.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.name}"), nameField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.genomeRelease}"), genomeReleaseField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        fieldModified.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.modified}"), fieldModified, org.jdesktop.beansbinding.BeanProperty.create("text"));
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
        }
    );
    bindingGroup.addBinding(binding);

    fieldCreated.setEditable(false);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.created}"), fieldCreated, org.jdesktop.beansbinding.BeanProperty.create("text"));
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
    }
    );
    bindingGroup.addBinding(binding);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.parentExperiment.name}"), parentIDField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.tableData}"), tableDataField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.dataType}"), dataTypeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.clazz}"), clazzField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jButtonSelectRelease.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.jButtonSelectRelease.text")); // NOI18N
    jButtonSelectRelease.setMargin(new java.awt.Insets(2, 1, 2, 1));
    jButtonSelectRelease.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonSelectReleaseActionPerformed(evt);
        }
    });

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.owner.name}"), jTextFieldOwner, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    parentTrackIDLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    parentTrackIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    parentTrackIDLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.parentTrackLabel.text")); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.parentTrack.name}"), parentTrackIDField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jLabelRequired.setFont(new java.awt.Font("Dialog", 2, 10));
    jLabelRequired.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.jLabelRequired.text")); // NOI18N

    jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel1.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.jLabel1.text")); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(nameLabel_req, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(parentIDLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(parentTrackIDLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(genomeReleaseLabel_req, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(tableDataLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(dataTypeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(clazzLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabelRequired, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(fieldModified, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(44, 44, 44)
                    .addComponent(fieldCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(clazzField, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addComponent(dataTypeField, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addComponent(tableDataField, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addComponent(jTextFieldOwner, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(parentTrackIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(parentIDField, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(genomeReleaseField, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                    .addComponent(jButtonSelectRelease, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(150, 150, 150))
    );

    jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {clazzField, dataTypeField, jTextFieldOwner, nameField, parentIDField, parentTrackIDField, tableDataField});

    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(nameLabel_req)
                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(parentIDLabel)
                .addComponent(parentIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(parentTrackIDLabel)
                .addComponent(parentTrackIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(genomeReleaseLabel_req)
                .addComponent(genomeReleaseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonSelectRelease, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(fieldModified, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(fieldCreated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jTextFieldOwner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(tableDataLabel)
                .addComponent(tableDataField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(dataTypeLabel)
                .addComponent(dataTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(clazzLabel)
                .addComponent(clazzField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabelRequired)
            .addContainerGap(22, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

    nofSpotsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    nofSpotsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    nofSpotsLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.nofSpotsLabel.text")); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.stddev}"), stddevField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    stddevField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            stddevFieldActionPerformed(evt);
        }
    });

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.nof}"), nofSpotsField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    meanLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    meanLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    meanLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.meanLabel.text")); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.mean}"), meanField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    medianLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    medianLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    medianLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.medianLabel.text")); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.median}"), medianField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    varianceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    varianceLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    varianceLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.varianceLabel.text")); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.variance}"), varianceField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    stddevLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    stddevLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.stddevLabel.text")); // NOI18N

    minRatioLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    minRatioLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.minRatioLabel.text")); // NOI18N

    maxRatioLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    maxRatioLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.maxRatioLabel.text")); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.minRatio}"), minRatioField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.maxRatio}"), maxRatioField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(nofSpotsLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(nofSpotsField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(46, 46, 46)
                    .addComponent(minRatioLabel))
                .addComponent(maxRatioLabel))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(maxRatioField)
                .addComponent(minRatioField))
            .addGap(19, 19, 19)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(meanLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(medianLabel, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(medianField, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addComponent(meanField, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(varianceLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(stddevLabel, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(varianceField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(stddevField, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {meanField, medianField, stddevField, varianceField});

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {maxRatioField, minRatioField, nofSpotsField});

    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(nofSpotsLabel)
                .addComponent(minRatioLabel)
                .addComponent(varianceLabel)
                .addComponent(varianceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(nofSpotsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(minRatioField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(maxRatioLabel)
                .addComponent(stddevLabel)
                .addComponent(stddevField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(maxRatioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(meanLabel)
                .addComponent(meanField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(medianLabel)
                .addComponent(medianField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    originalFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    originalFileLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    originalFileLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.originalFileLabel")); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.originalFile}"), originalFileField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    jPanel3.setMaximumSize(new java.awt.Dimension(300, 100));
    jPanel3.setPreferredSize(new java.awt.Dimension(200, 100));

    descriptionLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    descriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    descriptionLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.descriptionLabel.text")); // NOI18N

    fieldDesc.setColumns(20);
    fieldDesc.setRows(5);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.description}"), fieldDesc, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jScrollPane1.setViewportView(fieldDesc);

    procProcessingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    procProcessingLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    procProcessingLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.procProcessingLabel.text")); // NOI18N

    fieldParamProc.setColumns(20);
    fieldParamProc.setRows(5);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.paramProcessing}"), fieldParamProc, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jScrollPane2.setViewportView(fieldParamProc);

    paramProcessingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    paramProcessingLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    paramProcessingLabel.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.paramProcessingLabel.text")); // NOI18N

    fieldProc.setColumns(20);
    fieldProc.setRows(5);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${track.procProcessing}"), fieldProc, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jScrollPane3.setViewportView(fieldProc);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(25, 25, 25)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(descriptionLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(procProcessingLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(paramProcessingLabel, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(19, Short.MAX_VALUE))
    );

    jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jScrollPane1, jScrollPane2, jScrollPane3});

    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(descriptionLabel)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(procProcessingLabel)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(paramProcessingLabel)
                    .addGap(63, 63, 63))
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addContainerGap())))
    );

    org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${track.samples}");
    org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableSamples);
    org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
    columnBinding.setColumnName("Name");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${inverse}"));
    columnBinding.setColumnName("Inverse");
    columnBinding.setColumnClass(Boolean.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${phenotype}"));
    columnBinding.setColumnName("Phenotype");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${characteristics}"));
    columnBinding.setColumnName("Characteristics");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${organism}"));
    columnBinding.setColumnName("Organism");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${source}"));
    columnBinding.setColumnName("Source");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${molecule}"));
    columnBinding.setColumnName("Molecule");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${treatment}"));
    columnBinding.setColumnName("Treatment");
    columnBinding.setColumnClass(String.class);
    bindingGroup.addBinding(jTableBinding);
    jTableBinding.bind();
    jScrollPane5.setViewportView(tableSamples);
    tableSamples.getColumnModel().getColumn(0).setResizable(false);
    tableSamples.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title0")); // NOI18N
    tableSamples.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title7_1")); // NOI18N
    tableSamples.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title2")); // NOI18N
    tableSamples.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title3")); // NOI18N
    tableSamples.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title4")); // NOI18N
    tableSamples.getColumnModel().getColumn(5).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title5")); // NOI18N
    tableSamples.getColumnModel().getColumn(6).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title6")); // NOI18N
    tableSamples.getColumnModel().getColumn(7).setHeaderValue(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.tableSamples.columnModel.title7")); // NOI18N

    jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel2.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.jLabel2.text")); // NOI18N

    buttonShowSample.setText(org.openide.util.NbBundle.getMessage(TrackDataView.class, "TrackDataView.buttonShowSample.text")); // NOI18N
    buttonShowSample.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonShowSample.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            buttonShowSampleActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(43, 43, 43)
                    .addComponent(originalFileLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(originalFileField, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE))
                .addComponent(jLabel2)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
                .addComponent(buttonShowSample, javax.swing.GroupLayout.Alignment.TRAILING))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(originalFileLabel)
                .addComponent(originalFileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonShowSample)
            .addContainerGap())
    );

    bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonSelectReleaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectReleaseActionPerformed
    final JPanel panel = new JPanel();
    Vector<String> possibilities = DBUtils.getAllReleases();
    final JComboBox combo = new JComboBox(possibilities);
    combo.setEditable(true);
    panel.add(combo);
    if (JOptionPane.showConfirmDialog(null, panel,
            "choose release", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        this.genomeReleaseField.setText(combo.getSelectedItem().toString());
    }

}//GEN-LAST:event_jButtonSelectReleaseActionPerformed

private void stddevFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stddevFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_stddevFieldActionPerformed

private void buttonShowSampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowSampleActionPerformed
    int index = this.tableSamples.getSelectedRow();
    if (index < 0) {
        return;
    }
    SampleInTrack sie = this.track.getSamples().get(
            this.tableSamples.convertRowIndexToModel(index));

    SampleDetailView.SampleDetailViewDialog(sie.getSample(), edit);
    /*JDialog d = new JDialog((JFrame) null, true);
    d.setTitle("Sample Details");
    
    d.setSize(new Dimension(900, 600));
    d.setLocationRelativeTo(null);
    SampleDetailView sview = new SampleDetailView(sie.getSample(), this.edit);
    sview.getNameField1().setEditable(false);
    d.add(sview);
    d.setVisible(true);
     */

    if (this.edit) {     //workaround to update the list

        boolean isInverse = sie.isInverse();
        this.track.removeSample(sie);
        // 270313
        this.track.addSample(sie.getSample(), isInverse);
    //this.track.addSample(sview.getSample(), isInverse);
    }
}//GEN-LAST:event_buttonShowSampleActionPerformed
    static void view(Track e, boolean edit) {
        JDialog d = new JDialog();
        d.setTitle("Track Data View ");
        TrackDataView v = new TrackDataView(e, false);
        d.add(v);
        d.pack();
        d.setVisible(true);
        d.setLocationRelativeTo(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonShowSample;
    private javax.swing.JTextField clazzField;
    private javax.swing.JLabel clazzLabel;
    private javax.swing.JTextField dataTypeField;
    private javax.swing.JLabel dataTypeLabel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextField fieldCreated;
    private javax.swing.JTextArea fieldDesc;
    private javax.swing.JTextField fieldModified;
    private javax.swing.JTextArea fieldParamProc;
    private javax.swing.JTextArea fieldProc;
    private javax.swing.JTextField genomeReleaseField;
    private javax.swing.JLabel genomeReleaseLabel_req;
    private javax.swing.JButton jButtonSelectRelease;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelRequired;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldOwner;
    private javax.swing.JTextField maxRatioField;
    private javax.swing.JLabel maxRatioLabel;
    private javax.swing.JTextField meanField;
    private javax.swing.JLabel meanLabel;
    private javax.swing.JTextField medianField;
    private javax.swing.JLabel medianLabel;
    private javax.swing.JTextField minRatioField;
    private javax.swing.JLabel minRatioLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel_req;
    private javax.swing.JTextField nofSpotsField;
    private javax.swing.JLabel nofSpotsLabel;
    private javax.swing.JTextField originalFileField;
    private javax.swing.JLabel originalFileLabel;
    private javax.swing.JLabel paramProcessingLabel;
    private javax.swing.JTextField parentIDField;
    private javax.swing.JLabel parentIDLabel;
    private javax.swing.JTextField parentTrackIDField;
    private javax.swing.JLabel parentTrackIDLabel;
    private javax.swing.JLabel procProcessingLabel;
    private javax.swing.JTextField stddevField;
    private javax.swing.JLabel stddevLabel;
    private javax.swing.JTextField tableDataField;
    private javax.swing.JLabel tableDataLabel;
    private javax.swing.JTable tableSamples;
    private javax.swing.JTextField varianceField;
    private javax.swing.JLabel varianceLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
