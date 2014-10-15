package org.molgen.genomeCATPro.guimodul.experiment;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import javax.swing.JTextField;
import org.jdesktop.beansbinding.Converter;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInExperiment;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.guimodul.SwingUtils;
import org.molgen.genomeCATPro.guimodul.platform.PlatformDetailView;
/**
 * @name ExperimentDetailView
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
/**
 * 21.05.12 kt ExperimentDetailView.buttonShowSampleActionPerformed
	update Sample (remove/add) abhängig vom edit state
 * 
 */
public class ExperimentDetailView extends javax.swing.JPanel {

    private ExperimentDetail experiment = null;
    boolean edit = false;
    List<ExperimentData> listExperimentData = null;
    List<SampleInExperiment> listSamples = null;
    public JPopupMenu menuPlatform = null;

    /** Creates new form ExperimentDetailView */
    public ExperimentDetailView() {
        this.experiment = new ExperimentDetail();
        edit = true;
        listExperimentData = (List<ExperimentData>) (java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : this.experiment.getDataList());
        listSamples = (List<SampleInExperiment>) (java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : this.experiment.getSamples());


        initComponents();
        myInit();
        Logger.getLogger(ExperimentDetailView.class.getName()).log(Level.INFO,
                "constructor called: " + this.experiment.toFullString());
                
    }

    public void clear() {
        this.experiment = new ExperimentDetail();
        edit = true;
        listExperimentData = (List<ExperimentData>) (java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : this.experiment.getDataList());
        listSamples = (List<SampleInExperiment>) (java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : this.experiment.getSamples());
        initComponents();
        myInit();
    }

    void myInit() {
        this.menuPlatform = new JPopupMenu();
        JMenuItem detailMenuItem = new JMenuItem("Detail");
        JMenuItem selectMenuItem = new JMenuItem("Select");
        this.menuPlatform.add(detailMenuItem);
        this.menuPlatform.add(selectMenuItem);

        this.fieldPlatformName.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                switch (e.getModifiers()) {
                    case InputEvent.BUTTON3_MASK: {
                        // JDialog d = new JDialog((JFrame) null, true);
                        // d.add(new PlatformDetailView(experiment.getPlatform(), false));
                        // d.setVisible(edit);
                        //menuPlatform.show(e.getComponent(), e.getX(), e.getY());
                        // menuPlatform.setInvoker(e.getComponent());

                        break;
                    }
                }
            }
        });


        this.setAllFieldsEditable(edit);
        this.buttonShowSample.setText((edit ? "edit " : "show") + "selected Sample");
    }

    public ExperimentDetailView(ExperimentDetail e, boolean edit) {
        this.experiment = e;
        this.edit = edit;
        listExperimentData = (List<ExperimentData>) (java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : this.experiment.getDataList());
        listSamples = (List<SampleInExperiment>) (java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : this.experiment.getSamples());



        initComponents();
        myInit();
        Logger.getLogger(ExperimentDetailView.class.getName()).log(Level.INFO,
                "constructor called with experimentdetail: " +
                e.toString() + (edit ? "editable" : ""));
    }

    static void view(ExperimentDetail e, boolean edit) {
        JDialog d = new JDialog();
        d.setTitle("Experiment Detail View");
       


        ExperimentDetailView v = new ExperimentDetailView(e, false);
        d.add(v);
        d.pack();
        d.setVisible(true);
         d.setLocationRelativeTo(null);
    }

    public ExperimentDetail getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentDetail experiment) {
        this.experiment = experiment;
    }

    public List<ExperimentData> getListExperimentData() {
        return listExperimentData;
    }

    public void setListExperimentData(List<ExperimentData> listExperimentData) {
        this.listExperimentData = listExperimentData;
    }

    public JTextField getFieldName() {
        return fieldName;
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
        this.hintReq.setEnabled(b);

        this.fieldModified.setEditable(false);
        this.fieldCreated.setEditable(false);
        this.fieldNofChannel.setEditable(false);

        this.fieldID.setEditable(false);

        this.buttonShowSample.setEnabled(true);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        name = new javax.swing.JLabel();
        fieldName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        fieldID = new javax.swing.JTextField();
        jLabelSummary = new javax.swing.JLabel();
        fieldSummary = new javax.swing.JTextField();
        fieldModified = new javax.swing.JTextField();
        fieldCreated = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        fieldProcessing = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        fieldHybProtocoll = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        fieldNofChannel = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fieldDescription = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableSamples = new javax.swing.JTable();
        buttonShowSample = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        type = new javax.swing.JLabel();
        fieldType = new javax.swing.JTextField();
        descriptionLabel1 = new javax.swing.JLabel();
        methodField = new javax.swing.JTextField();
        jButtonSelectType = new javax.swing.JButton();
        jButtonSelectMethod = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        fieldPlatformName = new javax.swing.JTextField();
        buttonPlatform = new javax.swing.JButton();
        hintReq = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        name.setFont(new java.awt.Font("Dialog", 0, 12));
        name.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        name.setText("Name:");
        name.setName("req"); // NOI18N

        fieldName.setForeground(new java.awt.Color(0, 0, 0));
        fieldName.setMaximumSize(new java.awt.Dimension(20, 100));
        fieldName.setMinimumSize(new java.awt.Dimension(4, 50));
        fieldName.setPreferredSize(new java.awt.Dimension(10, 50));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.name}"), fieldName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("ID:");

        fieldID.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.experimentDetailID}"), fieldID, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelSummary.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabelSummary.setText("Summary:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.summary}"), fieldSummary, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        fieldModified.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.modified}"), fieldModified, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.created}"), fieldCreated, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(24, 24, 24)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabelSummary, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(name, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addComponent(fieldCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                    .addComponent(fieldModified, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(fieldSummary, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addComponent(fieldName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addComponent(fieldID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
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
                .addComponent(jLabelSummary)
                .addComponent(fieldSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(fieldCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(fieldModified, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

    fieldProcessing.setColumns(20);
    fieldProcessing.setRows(5);
    fieldProcessing.setToolTipText("comments on processing handling of the data.");

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.processing}"), fieldProcessing, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jScrollPane2.setViewportView(fieldProcessing);

    jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel2.setText("Pre-Processing:");

    fieldHybProtocoll.setColumns(20);
    fieldHybProtocoll.setRows(5);
    fieldHybProtocoll.setToolTipText("comments on processing handling of the data.");

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.hybProtocoll}"), fieldHybProtocoll, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jScrollPane3.setViewportView(fieldHybProtocoll);

    jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("<html>hybridisation<br/>protocoll:</html>");

    jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel5.setText("Nof Channel:");

    fieldNofChannel.setEditable(false);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.nofChannel}"), fieldNofChannel, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel7.setText("Description:");

    fieldDescription.setColumns(20);
    fieldDescription.setRows(5);

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.description}"), fieldDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jScrollPane1.setViewportView(fieldDescription);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGap(39, 39, 39)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addComponent(fieldNofChannel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldNofChannel, jScrollPane1, jScrollPane2, jScrollPane3});

    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel3))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel2)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(31, 31, 31)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(fieldNofChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(16, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Samples")));

    org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${experiment.samples}");
    org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableSamples);
    org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${isCy3}"));
    columnBinding.setColumnName("Is Cy3");
    columnBinding.setColumnClass(Boolean.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${isCy5}"));
    columnBinding.setColumnName("Is Cy5");
    columnBinding.setColumnClass(Boolean.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
    columnBinding.setColumnName("Name");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${phenotype}"));
    columnBinding.setColumnName("Phenotype");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${characteristics}"));
    columnBinding.setColumnName("Characteristics");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${molecule}"));
    columnBinding.setColumnName("Molecule");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${organism}"));
    columnBinding.setColumnName("Organism");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${source}"));
    columnBinding.setColumnName("Source");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${treatment}"));
    columnBinding.setColumnName("Treatment");
    columnBinding.setColumnClass(String.class);
    jTableBinding.setSourceNullValue(Collections.EMPTY_LIST);
    jTableBinding.setSourceUnreadableValue(Collections.EMPTY_LIST);
    bindingGroup.addBinding(jTableBinding);
    jTableBinding.bind();
    jScrollPane4.setViewportView(tableSamples);

    buttonShowSample.setText("show selected sample ");
    buttonShowSample.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonShowSample.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            buttonShowSampleActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                .addComponent(buttonShowSample))
            .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonShowSample))
    );

    jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

    type.setFont(new java.awt.Font("Dialog", 0, 12));
    type.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    type.setText("Type:");
    type.setName("req"); // NOI18N

    fieldType.setToolTipText("Oligo, BAC, ...");
    fieldType.setMaximumSize(new java.awt.Dimension(20, 100));
    fieldType.setMinimumSize(new java.awt.Dimension(4, 50));
    fieldType.setName("req"); // NOI18N
    fieldType.setPreferredSize(new java.awt.Dimension(4, 50));

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.type}"), fieldType, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    descriptionLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
    descriptionLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    descriptionLabel1.setText("Method:");
    descriptionLabel1.setName("req"); // NOI18N

    methodField.setMaximumSize(new java.awt.Dimension(20, 100));
    methodField.setMinimumSize(new java.awt.Dimension(4, 50));
    methodField.setPreferredSize(new java.awt.Dimension(4, 50));

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.method}"), methodField, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    jButtonSelectType.setText("change");
    jButtonSelectType.setMargin(new java.awt.Insets(2, 1, 2, 1));
    jButtonSelectType.setName("req"); // NOI18N
    jButtonSelectType.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonSelectTypeActionPerformed(evt);
        }
    });

    jButtonSelectMethod.setText("change");
    jButtonSelectMethod.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jButtonSelectMethod.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonSelectMethodActionPerformed(evt);
        }
    });

    jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel6.setText("Platform:");
    jLabel6.setName("req"); // NOI18N

    binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${experiment.platform.name}"), fieldPlatformName, org.jdesktop.beansbinding.BeanProperty.create("text"));
    bindingGroup.addBinding(binding);

    buttonPlatform.setText("details");
    buttonPlatform.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonPlatform.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            buttonPlatformActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(31, 31, 31)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(type, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                .addComponent(descriptionLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(fieldType, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addComponent(methodField, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addComponent(fieldPlatformName, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jButtonSelectMethod)
                .addComponent(buttonPlatform, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonSelectType))
            .addContainerGap())
    );

    jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldPlatformName, fieldType, methodField});

    jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonPlatform, jButtonSelectMethod, jButtonSelectType});

    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(1, 1, 1)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(type)
                .addComponent(fieldType, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonSelectType, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(descriptionLabel1)
                .addComponent(methodField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonSelectMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(fieldPlatformName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel6)
                .addComponent(buttonPlatform, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonPlatform, jButtonSelectMethod, jButtonSelectType});

    hintReq.setFont(new java.awt.Font("Dialog", 2, 10));
    hintReq.setText("* required fields");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                    .addGap(102, 102, 102)
                    .addComponent(hintReq)))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(7, 7, 7)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
            .addComponent(hintReq))
    );

    bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void buttonShowSampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowSampleActionPerformed
    int index = this.tableSamples.getSelectedRow();
    if (index < 0) {
        return;
    }
    SampleInExperiment sie = this.experiment.getSamples().get(
            this.tableSamples.convertRowIndexToModel(index));
    JDialog d = new JDialog((JFrame) null, true);
    d.setTitle("Sample Details");

    d.setSize(new Dimension(900, 600));
    d.setLocationRelativeTo(null);
    SampleDetailView sview = new SampleDetailView(sie.getSample(), this.edit);
    sview.getNameField1().setEditable(false);
    d.add(sview);
    d.setVisible(true);
    if (this.edit){//workaround  to update list view

        boolean isCy3 = sie.isIsCy3();
        this.experiment.removeSample(sie);
        this.experiment.addSample(sview.getSample(), isCy3, !isCy3);
    }
}//GEN-LAST:event_buttonShowSampleActionPerformed

private void buttonPlatformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPlatformActionPerformed
    JDialog d = new JDialog((JFrame) null, true);
    d.setSize(new Dimension(800, 300));
    d.setLocationRelativeTo(this);
    d.add(new PlatformDetailView(this.experiment.getPlatform(), false));
    d.setVisible(true);
}//GEN-LAST:event_buttonPlatformActionPerformed

private void jButtonSelectTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectTypeActionPerformed
    final JPanel panel = new JPanel();
    Vector<String> possibilities = DBUtils.getAllArrayTypes();
    final JComboBox combo = new JComboBox(possibilities);
    combo.setEditable(true);
    panel.add(combo);
    if (JOptionPane.showConfirmDialog(null, panel,
            "choose method", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        this.fieldType.setText((String) combo.getSelectedItem());
    }
}//GEN-LAST:event_jButtonSelectTypeActionPerformed

private void jButtonSelectMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectMethodActionPerformed
    final JPanel panel = new JPanel();
    Vector<String> possibilities = DBUtils.getAllArrayMethods();
    final JComboBox combo = new JComboBox(possibilities);
    combo.setEditable(true);
    panel.add(combo);
    if (JOptionPane.showConfirmDialog(null, panel,
            "choose method", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        this.methodField.setText((String) combo.getSelectedItem());
    }
}//GEN-LAST:event_jButtonSelectMethodActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton buttonPlatform;
    public javax.swing.JButton buttonShowSample;
    public javax.swing.JLabel descriptionLabel1;
    public javax.swing.JTextField fieldCreated;
    public javax.swing.JTextArea fieldDescription;
    public javax.swing.JTextArea fieldHybProtocoll;
    public javax.swing.JTextField fieldID;
    public javax.swing.JTextField fieldModified;
    public javax.swing.JTextField fieldName;
    public javax.swing.JTextField fieldNofChannel;
    public javax.swing.JTextField fieldPlatformName;
    public javax.swing.JTextArea fieldProcessing;
    public javax.swing.JTextField fieldSummary;
    public javax.swing.JTextField fieldType;
    public javax.swing.JLabel hintReq;
    public javax.swing.JButton jButtonSelectMethod;
    public javax.swing.JButton jButtonSelectType;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabelSummary;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jPanel3;
    public javax.swing.JPanel jPanel5;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JTextField methodField;
    public javax.swing.JLabel name;
    public javax.swing.JTable tableSamples;
    public javax.swing.JLabel type;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
