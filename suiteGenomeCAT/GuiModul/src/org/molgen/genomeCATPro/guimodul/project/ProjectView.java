package org.molgen.genomeCATPro.guimodul.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.RollbackException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.service.ProjectService;
import org.molgen.genomeCATPro.datadb.service.ServiceListener;

/**
 * @name ProjectView
 *
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
public class ProjectView extends JDialog implements ServiceListener {

    String owner = "";

    public ProjectView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.owner = System.getProperty("user.name");
        ProjectService.addListener(this);

        initComponents();
        entityManager.getTransaction().begin();
        this.list.clear();
        try {
            this.list.addAll(ProjectService.listProjects(this.owner, this.entityManager));
        } catch (Exception e) {
            Logger.getLogger(ProjectView.class.getName()).log(Level.SEVERE, "", e);
        }
        /*this.list.addAll(ProjectService.listProjects(this.owner));*/
        // status bar initialization - message timeout, idle icon and busy animation, etc
        // ResourceMap resourceMap = getResourceMap();
        // int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");

        // connecting action tasks to status bar via TaskMonitor
        // tracking table selection
        projectTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    changeEditState();
                    //firePropertyChange("recordSelected", !isRecordSelected(), isRecordSelected());
                }
            }
        });

        // tracking changes to save
        /*  bindingGroup.addBindingListener(new AbstractBindingListener() {
        
         @Override
         public void targetChanged(Binding binding, PropertyStateEvent event) {
         // save action observes saveNeeded property
         setSaveNeeded(true);
         }
         });
         */
        // have a transaction started
        setLocationRelativeTo(null);
    }

    public boolean isSaveNeeded() {
        return saveNeeded;
    }

    private void setSaveNeeded(boolean saveNeeded) {
        if (saveNeeded != this.saveNeeded) {
            this.saveNeeded = saveNeeded;
            firePropertyChange("saveNeeded", !saveNeeded, saveNeeded);
        }

    }
    Study newStudy = null;

    public void newRecord() {
        try {

            newStudy = ProjectService.createProject(this.owner, this.entityManager);

            this.nameField.setEditable(true);
            this.summaryField.setEditable(true);
            this.descriptionField.setEditable(true);

            //entityManager.persist(s);
            list.add(newStudy);
            int row = list.size() - 1;
            projectTable.setRowSelectionInterval(row, row);
            projectTable.scrollRectToVisible(projectTable.getCellRect(row, 0, true));
            setSaveNeeded(true);
        } catch (Exception ex) {
            Logger.getLogger(ProjectView.class.getName()).log(Level.SEVERE, "", ex);
        }
    }

    public void changeEditState() {
        int selected = projectTable.getSelectedRow();
        if (selected == -1) {
            return;
        }
        Study s = list.get(projectTable.convertRowIndexToModel(selected));
        if (!s.equals(this.newStudy)) {
            this.nameField.setEditable(false);
            this.summaryField.setEditable(false);
            this.descriptionField.setEditable(false);
        } else {
            this.nameField.setEditable(true);
            this.summaryField.setEditable(true);
            this.descriptionField.setEditable(true);

        }

    }

    public void deleteRecord() {
        int[] selected = projectTable.getSelectedRows();
        List<Study> toRemove = new ArrayList<Study>(selected.length);
        for (int idx = 0; idx < selected.length; idx++) {
            try {
                Study s = list.get(projectTable.convertRowIndexToModel(selected[idx]));
                toRemove.add(s);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ProjectView.this, "Error see logfile");
            }
        }

        for (Study s : toRemove) {
            try {
                ProjectService.remove(s, this.entityManager);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ProjectView.this, "Error see logfile");
            }

        }
        list.removeAll(toRemove);

        setSaveNeeded(true);
    }

    class ProgressBarChanger implements PropertyChangeListener {

        private JProgressBar jpb;

        public ProgressBarChanger(JProgressBar myBar) {
            jpb = myBar;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                jpb.setValue((Integer) evt.getNewValue());
            }
        }
    }

    private class SaveTask extends SwingWorker<Void, String> {

        JLabel msg = new JLabel();

        SaveTask(JLabel _msg) {
            this.msg = _msg;

        }

        @Override
        protected void process(List<String> chunks) {
            for (String s : chunks) {
                this.msg.setText(s);
            }
        }

        @Override
        protected Void doInBackground() {
            try {

                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();

            } catch (RollbackException rex) {
                Logger.getLogger(ProjectView.class.getName()).log(
                        Level.SEVERE, "save", rex);

                entityManager.getTransaction().begin();
                List<Study> data = new Vector<Study>();
                try {
                    data = ProjectService.listProjects(owner, entityManager);
                } catch (Exception ex) {
                }
                list.clear();
                list.addAll(data);
                setSaveNeeded(false);
                JOptionPane.showMessageDialog(ProjectView.this, "Error see logfile");
            }
            return null;
        }
    }

    /**
     * An example action method showing how to create asynchronous tasks
     * (running on background) and how to show their progress. Note the
     * artificial 'Thread.sleep' calls making the task long enough to see the
     * progress visualization - remove the sleeps for real application.
     */
    private class RefreshTask extends SwingWorker<Void, String> {

        JLabel msg = new JLabel();

        RefreshTask(JLabel _msg) {
            this.msg = _msg;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String s : chunks) {
                this.msg.setText(s);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground() {
            try {
                setProgress(10);
                publish("Rolling back the current changes...");
                setProgress(20);
                entityManager.getTransaction().rollback();

                setProgress(30);

                publish("Starting a new transaction...");
                entityManager.getTransaction().begin();

                setProgress(40);

                publish("Fetching new data...");
                java.util.Collection data = null;

                data = ProjectService.listProjects(owner, entityManager);

                setProgress(70);

                list.clear();
                list.addAll(data);
                publish("Done.");
                setProgress(100);
                setSaveNeeded(false);
            } catch (Exception ignore) {
                Logger.getLogger(ProjectView.class.getName()).log(
                        Level.SEVERE, "save", ignore);
                JOptionPane.showMessageDialog(ProjectView.this, "Error see logfile");
            }

            return null;
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

        entityManager = java.beans.Beans.isDesignTime() ? null :
        DBService.getEntityManger()
        ;
        list =  (List<Study>) (java.beans.Beans.isDesignTime() ?java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<Study>() )  );
        mainPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        summaryLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        summaryField = new javax.swing.JTextField();
        descriptionField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectTable = new javax.swing.JTable();
        statusPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setTitle(org.openide.util.NbBundle.getMessage(ProjectView.class, "ProjectView.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 400));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        mainPanel.setAutoscrolls(true);
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(800, 350));

        nameLabel.setText("Name:"); // NOI18N
        nameLabel.setName("nameLabel"); // NOI18N

        summaryLabel.setText("Summary:"); // NOI18N
        summaryLabel.setName("summaryLabel"); // NOI18N

        descriptionLabel.setText("Description:"); // NOI18N
        descriptionLabel.setName("descriptionLabel"); // NOI18N

        nameField.setEditable(false);
        nameField.setName("nameField"); // NOI18N
        nameField.setPreferredSize(new java.awt.Dimension(100, 19));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, projectTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.name}"), nameField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, projectTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), nameField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        summaryField.setEditable(false);
        summaryField.setName("summaryField"); // NOI18N
        summaryField.setPreferredSize(new java.awt.Dimension(100, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, projectTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.summary}"), summaryField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, projectTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), summaryField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        descriptionField.setEditable(false);
        descriptionField.setName("descriptionField"); // NOI18N
        descriptionField.setPreferredSize(new java.awt.Dimension(100, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, projectTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.description}"), descriptionField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, projectTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), descriptionField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        saveButton.setText("save"); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        refreshButton.setText("refresh"); // NOI18N
        refreshButton.setName("refreshButton"); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        newButton.setText("new"); // NOI18N
        newButton.setName("newButton"); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("delete"); // NOI18N
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jButtonCancel.setText("close");
        jButtonCancel.setName("jButtonCancel"); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        projectTable.setName("projectTable"); // NOI18N

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, list, projectTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${studyID}"));
        columnBinding.setColumnName("Study ID");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${summary}"));
        columnBinding.setColumnName("Summary");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(projectTable);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(summaryLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(descriptionLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                                    .addComponent(descriptionField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(summaryField, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 351, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(newButton)
                        .addGap(36, 36, 36)
                        .addComponent(deleteButton)
                        .addGap(32, 32, 32)
                        .addComponent(refreshButton)
                        .addGap(65, 65, 65)
                        .addComponent(saveButton)
                        .addGap(43, 43, 43)
                        .addComponent(jButtonCancel)
                        .addGap(24, 24, 24))))
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {deleteButton, newButton, refreshButton, saveButton});

        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(summaryLabel)
                    .addComponent(summaryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionLabel)
                    .addComponent(descriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(newButton)
                    .addComponent(deleteButton)
                    .addComponent(refreshButton)
                    .addComponent(saveButton)
                    .addComponent(jButtonCancel))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        getContentPane().add(mainPanel);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(800, 50));

        progressBar.setName("progressBar"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        getContentPane().add(statusPanel);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
    this.newRecord();
}//GEN-LAST:event_newButtonActionPerformed

private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
    this.deleteRecord();// TODO add your handling code here:
}//GEN-LAST:event_deleteButtonActionPerformed

private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
    this.newStudy = null;
    SwingWorker w = new RefreshTask(this.statusMessageLabel);
    w.addPropertyChangeListener(new ProgressBarChanger(this.progressBar));
    w.execute();
}//GEN-LAST:event_refreshButtonActionPerformed

private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
    this.newStudy = null;
    SwingWorker w = new SaveTask(this.statusMessageLabel);
    w.addPropertyChangeListener(new ProgressBarChanger(this.progressBar));
    w.execute();
}//GEN-LAST:event_saveButtonActionPerformed

private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    if (this.saveNeeded) {
        Object[] options = {"Yes, please",
            "abort changes!"
        };
        SwingWorker w = null;
        int n = JOptionPane.showOptionDialog(
                this,
                "save changes ?",
                "close window",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]);
        if (n == 1) {
            w = new RefreshTask(this.statusMessageLabel);
        } else {
            w = new SaveTask(this.statusMessageLabel);
        }

        w.addPropertyChangeListener(new ProgressBarChanger(this.progressBar));
        w.execute();
    }
    this.setVisible(false);
}//GEN-LAST:event_jButtonCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton deleteButton;
    javax.swing.JTextField descriptionField;
    javax.swing.JLabel descriptionLabel;
    javax.persistence.EntityManager entityManager;
    javax.swing.JButton jButtonCancel;
    javax.swing.JScrollPane jScrollPane1;
    private java.util.List<Study> list;
    javax.swing.JPanel mainPanel;
    javax.swing.JTextField nameField;
    javax.swing.JLabel nameLabel;
    javax.swing.JButton newButton;
    javax.swing.JProgressBar progressBar;
    javax.swing.JTable projectTable;
    javax.swing.JButton refreshButton;
    javax.swing.JButton saveButton;
    javax.swing.JLabel statusMessageLabel;
    javax.swing.JPanel statusPanel;
    javax.swing.JTextField summaryField;
    javax.swing.JLabel summaryLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private boolean saveNeeded;

    public void dbChanged() {
    }
}
