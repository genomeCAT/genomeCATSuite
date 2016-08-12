package org.molgen.genomeCATPro.guimodul.track;

/**
 * @name ImportTrackDialog
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
import java.awt.Component;
import java.awt.Cursor;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import org.molgen.genomeCATPro.cghpro.xport.ImportTrackWIG;
import org.molgen.genomeCATPro.cghpro.xport.ServiceXPort;
import org.molgen.genomeCATPro.cghpro.xport.XPortImport;

import org.molgen.genomeCATPro.cghpro.xport.XPortTrack;
import org.molgen.genomeCATPro.common.InformableHandler;

import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInTrack;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.molgen.genomeCATPro.guimodul.experiment.SampleDetailView;
import org.molgen.genomeCATPro.guimodul.experiment.SelectSampleDialog;
import org.openide.util.NbPreferences;

/**
 * 271114 kt redesign add sample (new tab, select from list) 070513 kt skip
 * mapping (shoud be replaced be method isMappingRequired at ImportModul) 270313
 * kt batch 120313 kt has header user defined 120313 kt	readFilenames throws
 * Exception
 */
public class ImportTrackDialog extends javax.swing.JDialog {

    private XPortTrack importModul = null;
    Track track = null;
    String filetype = null;

    /**
     *
     * @param parent
     */
    public ImportTrackDialog(java.awt.Frame parent) {
        super(parent, false);
        this.importModul = null;

        this.trackDataView1 = new TrackDataView(true);
        this.track = this.trackDataView1.getTrack();
        initComponents();
        //this.cbFileType.setSelectedItem(ImportTrack.track_bedgraph_txt);
        Logger.getLogger(ImportTrackDialog.class.getName()).log(
                Level.INFO, "constructor ");

        this.jPanelFile.setEnabled(true);

        this.detailsOKButton.setEnabled(false);
        this.jButtonSave.setEnabled(false);
        setLocationRelativeTo(null);
    }

    /**
     *
     * @param parent
     * @param project
     */
    public ImportTrackDialog(java.awt.Frame parent, Study project) {
        super(parent, false);
        this.importModul = null;
        this.trackDataView1 = new TrackDataView(true);
        this.track = this.trackDataView1.getTrack();
        initComponents();
        Logger.getLogger(ImportTrackDialog.class.getName()).log(
                Level.INFO, "constructor with  " + project.getName());

        this.cbStudy.setModel(new DefaultComboBoxModel(new String[]{project.getName()}));
        this.cbStudy.setSelectedItem(project.getName());
        this.cbStudy.setEnabled(false);

        this.jPanelFile.setEnabled(true);

        this.detailsOKButton.setEnabled(false);
        this.jButtonSave.setEnabled(false);
        setLocationRelativeTo(null);
    }

    /**
     *
     * @param parent
     * @param modul
     * @param filetype
     */
    @Deprecated
    public ImportTrackDialog(java.awt.Frame parent,
            XPortTrack modul, String filetype) {
        this(parent);
        this.importModul = modul;
        this.filetype = filetype;

        this.initEditTrack();

    }

    /**
     *
     */
    ImportTrackDialog() {
        this.importModul = null;
        this.filetype = null;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public void setImportFile(String file, String filetype) {
        this.jTextFieldFileName.setText(file);
        this.filetype = filetype;

        this.importModul = null;
        this.cbFileType.setSelectedItem(this.filetype);
        this.initImportModul();
    }

    void error(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message);
        Logger.getLogger(ImportTrackDialog.class.getName()).log(
                Level.WARNING, message, e);

    }

    /**
     * start and reset all parameter
     */
    void initImportModul() {
        if (this.jTextFieldFileName.getText() == null || this.jTextFieldFileName.getText().contentEquals("")) {
            error("please set filepath!", null);
            return;
        }
        if (this.cbFileType.getSelectedIndex() < 0
                || this.cbFileType.getSelectedItem().toString().contentEquals("")) {
            this.textHint.setText("please select import type!");
            return;
        }
        filetype = this.cbFileType.getSelectedItem().toString();
        this.importModul = ServiceXPort.getXPortTrack(filetype);
        if (importModul == null) {
            error("no import modul found", null);
        }
        try {
            importModul.newImportTrack(this.jTextFieldFileName.getText());
        } catch (Exception e) {
            error(e.getMessage(), e);
        }
        //this.initMapping();
        this.jTabbedPaneMain.setEnabledAt(1, false);
        this.jTabbedPaneMain.setEnabledAt(2, false);
        this.lastSelTab = -1;

        this.initEditTrack();
    }

    /**
     * initialize second panel (Mapping)
     */
    void initMapping() {
        if (this.importModul == null) {
            error("no import modul found - please choose filepath and filetyp above!", null);
        }
        //070513    skip mapping (shoud be replaced be method isMappingRequired at ImportModul)
        if (this.importModul instanceof ImportTrackWIG) {
            try {
                System.out.println("wig module");
                String[] tmp = this.importModul.getFileColNames();
                List<String[]> mapping = this.importModul.getDefaultMappingFile2DBColNames();
                this.importModul.setMappingFile2DBColNames(mapping);

                this.jTabbedPaneMain.setEnabledAt(2, true);
                this.initImport();
                return;

            } catch (Exception ex) {
                error(ex.getMessage(), ex);
            }
        }
        Vector<String> colsdata = new Vector<String>();
        Vector<Vector<String>> _data = new Vector<Vector<String>>();
        String[] tmp;
        try {
            tmp = importModul.getFileColNames();
            if (tmp == null) {
                error("error read file - see logfile", null);
                return;
            }
            colsdata = new Vector<String>(Arrays.asList(tmp));

            _data.add(new Vector<String>());
            _data = importModul.readData(100);

            this.jComboBoxPosField.setModel(new DefaultComboBoxModel(colsdata));
            if (this.importModul.hasSplitField()) {
                for (String col : colsdata) {
                    if (col.contentEquals(this.importModul.getSplitFieldName())) {
                        this.jComboBoxPosField.setSelectedItem(col);
                    }
                }
            }

            this.tableData.setModel(new DefaultTableModel(_data, colsdata));


            /* Vector<String> map = new Vector<String>(
             Arrays.asList(importModul.getImportFile4DBColNames(
             this.filetype)));
             */
            Vector<String> colsmap = new Vector<String>(
                    Arrays.asList(importModul.getDBColNames()));

            Vector<String> map = new Vector<String>();
            for (int i = 0; i < colsmap.size(); i++) {
                map.add("");
            }
            Vector<Vector<String>> datamap = new Vector<Vector<String>>();
            datamap.add(map);

            List<String[]> mapping = importModul.getDefaultMappingFile2DBColNames();

            tableMap.setModel(
                    new DefaultTableModel(
                            datamap, colsmap));

            this.tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            this.tableMap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            // content mapping
            for (int i = 0; i < this.tableMap.getColumnCount(); i++) {
                TableColumn col = this.tableMap.getColumnModel().getColumn(i);
                // get filecols 
                Vector<String> filecols = new Vector<String>(Arrays.asList(importModul.getFileColNames()));
                filecols.add(ImportTrackDialog.emptyCol);
                JComboBox box = new JComboBox(filecols);
                //JComboBox box = new JComboBox(importModul.getFileColNames());
                //col.setMinWidth((int) box.getPreferredSize().getWidth());

                // get mapping init
                col.setCellEditor(new DefaultCellEditor(box));
                for (String[] m : mapping) {
                    if (m[XPortImport.ind_db].equalsIgnoreCase(this.tableMap.getColumnName(i))) {
                        box.setSelectedItem(XPortImport.ind_file);
                        this.tableMap.getModel().setValueAt(m[XPortImport.ind_file], 0, i);
                        Logger.getLogger(
                                ImportTrackDialog.class.getName()).log(
                                        Level.INFO, "readSettings: found for col: " + m[XPortImport.ind_db]
                                        + " mapping: " + m[XPortImport.ind_file]);
                    }
                }
            }
        } catch (Exception e) {
            error(e.getMessage(), e);
        }
        this.jTabbedPaneMain.setSelectedIndex(1);
        this.textHint.setText("set for each column of database table a file column");

    }

    /**
     *
     */
    class myTable extends JTable {

        @Override
        public Component prepareEditor(TableCellEditor editor, int row, int column) {
            Component c = super.prepareEditor(editor, row, column);
            this.getColumnModel().getColumn(column).setMinWidth((int) c.getPreferredSize().getWidth());
            return c;
        }
    }

    /**
     * initialize first Panel (edit track parameter)
     */
    void initEditTrack() {
        try {

            if (this.importModul == null) {
                error("no import type selected", null);
                return;
            }

            //this.platformDetailView1 = new PlatformDetailView();
            //this.detail = this.platformDetailView1.getPlatform();
            this.track.copy(this.importModul.getTrack());

            Logger.getLogger(
                    ImportTrackDialog.class.getName()).log(
                            Level.INFO, "init with new track: " + this.track.toFullString());

            //this.platformDetailView1.setPlatform(this.importModul.getPlatformDetail());
            //this.trackDataView1.setAllFieldsEditable(true);
            this.jTabbedPaneMain.setSelectedIndex(0);
            this.detailsOKButton.setEnabled(true);

            this.textHint.setText("edit track details");
        } catch (Exception e) {
            error("error initTrack", e);

        }
    }

    /**
     * initialize last panel (do import)
     */
    void initImport() {
        // validate before Import

        try {
            if (this.importModul == null) {
                error("no import type selected", null);
                return;
            }

            this.trackDataView1.setAllFieldsEditable(true);
            this.trackDataView1.getJButtonSelectRelease().setVisible(false);
            this.trackDataView1.getGenomeReleaseField().setEditable(false);

            this.track.setGenomeRelease(this.cbRelease.getSelectedItem().toString());

            /**
             * if (this.data.getGenomeRelease() != null) {
             * this.platformDataView1.setGenomeReleaseEditable(false); }*
             */
            Track t = TrackService.getTrack(
                    this.track.getName(),
                    this.track.getGenomeRelease().toString());
            if (t != null) {
                // older experiment with same name exists, create unique name, report error

                this.track.setName(Utils.getUniquableName(this.track.getName()));
            }

            this.jTabbedPaneMain.setSelectedIndex(2);
            this.textHint.setText("press <import> to import data");
            this.jButtonSave.setEnabled(true);
        } catch (Exception e) {
            Logger.getLogger(
                    ImportTrackDialog.class.getName()).log(
                            Level.SEVERE,
                            "initImport", e);
            error("error init import", null);
        }
    }

    /**
     * validate detail fields
     */
    private boolean validateEditTrack() {
        if (this.cbRelease.getSelectedIndex() < 0) {
            error("no release selected", null);
            return false;
        } else if (this.cbStudy.getSelectedIndex() < 0) {
            error("no study selected", null);
            return false;
        }
        this.importModul.setProject(this.cbStudy.getSelectedItem().toString());

        return true;
    }
    private static String emptyCol = "";

    /**
     *
     * @return
     */
    boolean validateMapping() {
        List<String[]> mapping = new Vector<String[]>();

        for (int i = 0; i < this.tableMap.getColumnCount(); i++) {
            String dbCol = this.tableMap.getColumnName(i);
            String fileCol = (String) this.tableMap.getValueAt(0, i);

            // get mapping init
            if (fileCol != null && !fileCol.contentEquals(ImportTrackDialog.emptyCol)) {
                String[] m = new String[2];

                m[XPortImport.ind_db] = dbCol;
                m[XPortImport.ind_file] = fileCol;
                mapping.add(m);
                Logger.getLogger(ImportTrackDialog.class.getName()).log(
                        Level.INFO, "validate: set mapping for col: " + m[1] + " mapping: " + m[0]);

            }
        }

        this.importModul.setMappingFile2DBColNames(mapping);
        return true;
        // this.initImport();
    }

    private SampleDetail createSample() {
        SampleDetail _d = null;
        try {

            String sampleName = "";
            sampleName = (String) JOptionPane.showInputDialog(null,
                    "enter new name",
                    "create sample",
                    JOptionPane.QUESTION_MESSAGE,
                    null, null, sampleName);
            if (sampleName == null) {
                return _d;
            }
            _d = ExperimentService.getSampleDetailByName(sampleName);

            if (_d == null) {
                _d = new SampleDetail();
                _d.setName(sampleName);
                _d = SampleDetailView.SampleDetailViewDialog(_d, true);
                return _d;
            }

        } catch (Exception ex) {
            Logger.getLogger(ImportTrackDialog.class.getName()).log(Level.SEVERE,
                    "Error: ", ex);
            error("error create Sample", ex);
        }
        return _d;
    }

    private void updateSamples(SampleDetail d) {
        try {
            boolean isInverse = false;
            if (track.getSamples().size() > 0) {
                isInverse = true;
            }
            SampleInTrack sie = track.addSample(d, isInverse);
            for (SampleInTrack s : track.getSamples()) {
                Logger.getLogger(ImportTrackDialog.class.getName()).log(Level.FINE,
                        s.toFullString());
            }

        } catch (Exception ex) {
            Logger.getLogger(ImportTrackDialog.class.getName()).log(Level.SEVERE,
                    "Error: ", ex);
            error("Update Sample", ex);
        }
    }

    /*void savePlatformData() {
    
     if (this.data.getGenomeRelease() == null) {
     error("no release set", null);
     return;
     } else {
     this.data.initTableData();
     }
     this.jButtonSave.setEnabled(true);
     this.textHint.setText("press <save> to start import");
    
     }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jPanelTrack = new javax.swing.JPanel();
        jButtonResetDetail = new javax.swing.JButton();
        detailsOKButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cbRelease = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cbStudy = new javax.swing.JComboBox();
        jPanelFile = new javax.swing.JPanel();
        jTextFieldFileName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonBrowse = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cbFileType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanelMap = new javax.swing.JPanel();
        jPanelTableFile = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableData = new javax.swing.JTable();
        jPanelTableMap = new javax.swing.JPanel();
        jScrollPaneMapping = new javax.swing.JScrollPane();
        tableMap = new myTable();
        jButtonResetMapping = new javax.swing.JButton();
        saveMapButton = new javax.swing.JButton();
        jComboBoxPosField = new javax.swing.JComboBox();
        jCheckBoxPositionField = new javax.swing.JCheckBox();
        jCheckBoxHasHeader = new javax.swing.JCheckBox();
        jPanelSample = new javax.swing.JPanel();
        addSample = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableSamples = new javax.swing.JTable();
        jButtonSampleNext = new javax.swing.JButton();
        jButtonResetSample = new javax.swing.JButton();
        jButtonCreateSample = new javax.swing.JButton();
        jPanelImport = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextMsg = new javax.swing.JTextArea();
        trackDataView1 = trackDataView1;
        jPanelButton = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel1 = new javax.swing.JButton();
        textHint = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import Track");

        jTabbedPaneMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jTabbedPaneMain.setForeground(java.awt.Color.black);
        jTabbedPaneMain.setAutoscrolls(true);
        jTabbedPaneMain.setFont(new java.awt.Font("Dialog", 1, 14));
        jTabbedPaneMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneMainStateChanged(evt);
            }
        });

        jPanelTrack.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPanelTrackFocusLost(evt);
            }
        });

        jButtonResetDetail.setForeground(new java.awt.Color(255, 51, 153));
        jButtonResetDetail.setText("reset");
        jButtonResetDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetDetailActionPerformed(evt);
            }
        });

        detailsOKButton.setForeground(new java.awt.Color(255, 51, 153));
        detailsOKButton.setText("next");
        detailsOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsOKButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jLabel4.text")); // NOI18N

        cbRelease.setModel(new javax.swing.DefaultComboBoxModel(DBUtils.getAllReleases()));
        cbRelease.setSelectedIndex(1);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setText(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jLabel5.text")); // NOI18N

        cbStudy.setModel(new javax.swing.DefaultComboBoxModel(DBUtils.getStudies()));

        jPanelFile.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jPanelFile.border.title"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Path for Input File:");

        jButtonBrowse.setText("Browse");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Import Type:");

        Vector<String> filetypes = ServiceXPort.getFileTypeTrackImport();
        cbFileType.setModel(new DefaultComboBoxModel(filetypes));
        cbFileType.setSelectedIndex(1);
        cbFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFileTypeActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel3.setText("<html>select one of the supported files <br/> i.e. the appropriate import module</html> ");

        javax.swing.GroupLayout jPanelFileLayout = new javax.swing.GroupLayout(jPanelFile);
        jPanelFile.setLayout(jPanelFileLayout);
        jPanelFileLayout.setHorizontalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFileLayout.createSequentialGroup()
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFileLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jButtonBrowse))
                    .addGroup(jPanelFileLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbFileType, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addContainerGap(113, Short.MAX_VALUE))
        );
        jPanelFileLayout.setVerticalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFileLayout.createSequentialGroup()
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel1)
                        .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel2)
                        .addComponent(cbFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelTrackLayout = new javax.swing.GroupLayout(jPanelTrack);
        jPanelTrack.setLayout(jPanelTrackLayout);
        jPanelTrackLayout.setHorizontalGroup(
            jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTrackLayout.createSequentialGroup()
                .addGroup(jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTrackLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTrackLayout.createSequentialGroup()
                        .addGap(219, 219, 219)
                        .addGroup(jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbStudy, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelTrackLayout.createSequentialGroup()
                        .addContainerGap(735, Short.MAX_VALUE)
                        .addComponent(detailsOKButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonResetDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanelTrackLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cbRelease, cbStudy});

        jPanelTrackLayout.setVerticalGroup(
            jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTrackLayout.createSequentialGroup()
                .addComponent(jPanelFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbStudy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 283, Short.MAX_VALUE)
                .addGroup(jPanelTrackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonResetDetail)
                    .addComponent(detailsOKButton))
                .addGap(76, 76, 76))
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jPanelTrack.TabConstraints.tabTitle"), jPanelTrack); // NOI18N

        jPanelMap.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPanelMapFocusLost(evt);
            }
        });

        jPanelTableFile.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jPanelTableFile.border.title"))); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        tableData.setModel(new javax.swing.table.DefaultTableModel(
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
        tableData.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tableData);

        javax.swing.GroupLayout jPanelTableFileLayout = new javax.swing.GroupLayout(jPanelTableFile);
        jPanelTableFile.setLayout(jPanelTableFileLayout);
        jPanelTableFileLayout.setHorizontalGroup(
            jPanelTableFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
        );
        jPanelTableFileLayout.setVerticalGroup(
            jPanelTableFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTableFileLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelTableMap.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jPanelTableMap.border.title"))); // NOI18N

        tableMap.setFont(new java.awt.Font("Dialog", 1, 12));
        tableMap.setModel(new javax.swing.table.DefaultTableModel(
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
        tableMap.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tableMap.setSurrendersFocusOnKeystroke(true);
        jScrollPaneMapping.setViewportView(tableMap);

        javax.swing.GroupLayout jPanelTableMapLayout = new javax.swing.GroupLayout(jPanelTableMap);
        jPanelTableMap.setLayout(jPanelTableMapLayout);
        jPanelTableMapLayout.setHorizontalGroup(
            jPanelTableMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneMapping, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE)
        );
        jPanelTableMapLayout.setVerticalGroup(
            jPanelTableMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTableMapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneMapping, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jButtonResetMapping.setForeground(new java.awt.Color(255, 51, 102));
        jButtonResetMapping.setText("Reset");
        jButtonResetMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetMappingActionPerformed(evt);
            }
        });

        saveMapButton.setForeground(new java.awt.Color(255, 51, 102));
        saveMapButton.setText("next");
        saveMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMapButtonActionPerformed(evt);
            }
        });

        jComboBoxPosField.setMaximumSize(new java.awt.Dimension(100, 100));
        jComboBoxPosField.setMinimumSize(new java.awt.Dimension(100, 100));
        jComboBoxPosField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPosFieldActionPerformed(evt);
            }
        });

        jCheckBoxPositionField.setText("<html>if chrom,start,stop is included in one single field <br/>select it here:</html>");

        jCheckBoxHasHeader.setText(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jCheckBoxHasHeader.text")); // NOI18N
        jCheckBoxHasHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxHasHeaderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMapLayout = new javax.swing.GroupLayout(jPanelMap);
        jPanelMap.setLayout(jPanelMapLayout);
        jPanelMapLayout.setHorizontalGroup(
            jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMapLayout.createSequentialGroup()
                        .addComponent(jPanelTableFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jPanelTableMap, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelMapLayout.createSequentialGroup()
                        .addComponent(jCheckBoxPositionField, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxPosField, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(319, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMapLayout.createSequentialGroup()
                        .addComponent(saveMapButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonResetMapping))
                    .addGroup(jPanelMapLayout.createSequentialGroup()
                        .addComponent(jCheckBoxHasHeader)
                        .addContainerGap(758, Short.MAX_VALUE))))
        );
        jPanelMapLayout.setVerticalGroup(
            jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelTableFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxHasHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTableMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxPositionField)
                    .addGroup(jPanelMapLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxPosField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addGroup(jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonResetMapping)
                    .addComponent(saveMapButton))
                .addContainerGap())
        );

        jTabbedPaneMain.addTab("step 2: map file content", jPanelMap);

        addSample.setText(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.addSample.text")); // NOI18N
        addSample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSampleActionPerformed(evt);
            }
        });

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${track.samples}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableSamples);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${inverse}"));
        columnBinding.setColumnName("Inverse");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${phenotype}"));
        columnBinding.setColumnName("Phenotype");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${organism}"));
        columnBinding.setColumnName("Organism");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${characteristics}"));
        columnBinding.setColumnName("Characteristics");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${source}"));
        columnBinding.setColumnName("Source");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${molecule}"));
        columnBinding.setColumnName("Molecule");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${treatment}"));
        columnBinding.setColumnName("Treatment");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane3.setViewportView(jTableSamples);
        jTableSamples.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title3")); // NOI18N
        jTableSamples.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title1")); // NOI18N
        jTableSamples.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title5")); // NOI18N
        jTableSamples.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title4")); // NOI18N
        jTableSamples.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title0")); // NOI18N
        jTableSamples.getColumnModel().getColumn(5).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title8")); // NOI18N
        jTableSamples.getColumnModel().getColumn(6).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title2")); // NOI18N
        jTableSamples.getColumnModel().getColumn(7).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTableSamples.columnModel.title11")); // NOI18N

        jButtonSampleNext.setForeground(new java.awt.Color(255, 51, 102));
        jButtonSampleNext.setText(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jButtonSampleNext.text")); // NOI18N
        jButtonSampleNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSampleNextActionPerformed(evt);
            }
        });

        jButtonResetSample.setForeground(new java.awt.Color(255, 51, 102));
        jButtonResetSample.setText(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jButtonResetSample.text")); // NOI18N
        jButtonResetSample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetSampleActionPerformed(evt);
            }
        });

        jButtonCreateSample.setText(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jButtonCreateSample.text")); // NOI18N
        jButtonCreateSample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateSampleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSampleLayout = new javax.swing.GroupLayout(jPanelSample);
        jPanelSample.setLayout(jPanelSampleLayout);
        jPanelSampleLayout.setHorizontalGroup(
            jPanelSampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSampleLayout.createSequentialGroup()
                .addGroup(jPanelSampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSampleLayout.createSequentialGroup()
                        .addContainerGap(743, Short.MAX_VALUE)
                        .addComponent(jButtonSampleNext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonResetSample))
                    .addGroup(jPanelSampleLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE))
                    .addGroup(jPanelSampleLayout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(addSample)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonCreateSample)))
                .addContainerGap())
        );
        jPanelSampleLayout.setVerticalGroup(
            jPanelSampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSampleLayout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelSampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addSample)
                    .addComponent(jButtonCreateSample))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 279, Short.MAX_VALUE)
                .addGroup(jPanelSampleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonResetSample)
                    .addComponent(jButtonSampleNext))
                .addContainerGap())
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jPanelSample.TabConstraints.tabTitle"), jPanelSample); // NOI18N

        jPanelImport.setNextFocusableComponent(jButtonSave);
        jPanelImport.setRequestFocusEnabled(false);
        jPanelImport.setVerifyInputWhenFocusTarget(false);

        jTextMsg.setColumns(20);
        jTextMsg.setRows(5);
        jScrollPane2.setViewportView(jTextMsg);

        javax.swing.GroupLayout jPanelImportLayout = new javax.swing.GroupLayout(jPanelImport);
        jPanelImport.setLayout(jPanelImportLayout);
        jPanelImportLayout.setHorizontalGroup(
            jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelImportLayout.createSequentialGroup()
                .addGroup(jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelImportLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 871, Short.MAX_VALUE))
                    .addComponent(trackDataView1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 883, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanelImportLayout.setVerticalGroup(
            jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelImportLayout.createSequentialGroup()
                .addComponent(trackDataView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneMain.addTab("step 3: import track", jPanelImport);

        jPanelButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelButton.setAlignmentX(1.0F);
        jPanelButton.setMinimumSize(new java.awt.Dimension(900, 20));

        jButtonSave.setForeground(new java.awt.Color(255, 51, 102));
        jButtonSave.setText("import");
        jButtonSave.setEnabled(false);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonCancel1.setForeground(java.awt.Color.black);
        jButtonCancel1.setText("close");
        jButtonCancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancel1ActionPerformed(evt);
            }
        });

        textHint.setFont(new java.awt.Font("Dialog", 3, 12));
        textHint.setForeground(new java.awt.Color(255, 51, 153));

        javax.swing.GroupLayout jPanelButtonLayout = new javax.swing.GroupLayout(jPanelButton);
        jPanelButton.setLayout(jPanelButtonLayout);
        jPanelButtonLayout.setHorizontalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textHint, javax.swing.GroupLayout.PREFERRED_SIZE, 675, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButtonSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancel1))
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jButtonSave)
                        .addComponent(jButtonCancel1))
                    .addComponent(textHint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
            .addComponent(jPanelButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPaneMain.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ImportTrackDialog.class, "ImportTrackDialog.jTabbedPaneMain.AccessibleContext.accessibleName")); // NOI18N

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * start import
     *
     * @param evt
     */
private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    InformableHandler informable = new InformableHandler() {

        public void messageChanged(String message) {
            jTextMsg.append(message + "\n");
        }
    };
    informable.messageChanged("Start Import....");
    //informable.messageChanged("Get Data for " + sample.getName());
    ImportWorker worker = new ImportWorker(importModul, track, informable);
    worker.execute();

    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

}//GEN-LAST:event_jButtonSaveActionPerformed
    /**
     * select import file
     *
     * @param evt
     */
private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
    String path = NbPreferences.forModule(ImportTrackDialog.class).get("pathPreference", "");
    JFileChooser importFileChooser = new JFileChooser(path);
    int returnVal = importFileChooser.showOpenDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }

    this.jTextFieldFileName.setText(importFileChooser.getSelectedFile().getPath());
    this.importModul = null;
    Logger.getLogger(ImportTrackDialog.class.getName()).log(Level.INFO,
            "You chose to import as annotation file: "
            + this.jTextFieldFileName.getText());

    this.initImportModul();

    NbPreferences.forModule(ImportTrackDialog.class).put("pathPreference",
            importFileChooser.getSelectedFile().getPath());

}//GEN-LAST:event_jButtonBrowseActionPerformed
    /**
     * select import type
     *
     * @param evt
     */
private void cbFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFileTypeActionPerformed
    this.importModul = null;

    this.jButtonSave.setEnabled(false);
    if (!this.cbFileType.getSelectedItem().toString().equalsIgnoreCase("")) {
        this.initImportModul();
    }
}//GEN-LAST:event_cbFileTypeActionPerformed

    /**
     * reset edit track
     *
     * @param evt
     */
private void jButtonResetDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetDetailActionPerformed

    this.jButtonSave.setEnabled(false);
    this.initEditTrack();
}//GEN-LAST:event_jButtonResetDetailActionPerformed
    /**
     * reset mapping file -> track
     *
     * @param evt
     */
private void jButtonResetMappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetMappingActionPerformed

    this.jButtonSave.setEnabled(false);
    this.initMapping();

}//GEN-LAST:event_jButtonResetMappingActionPerformed
    /**
     * cancel import
     *
     * @param evt
     */
private void jButtonCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancel1ActionPerformed
    this.setVisible(false);
}//GEN-LAST:event_jButtonCancel1ActionPerformed
    /**
     * save mapping file -> track
     *
     * @param evt
     */
private void saveMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMapButtonActionPerformed

    this.jButtonSave.setEnabled(false);
    if (this.validateMapping()) {
        this.jTabbedPaneMain.setEnabledAt(2, true);
        this.initImport();
    }
}//GEN-LAST:event_saveMapButtonActionPerformed
    /**
     *
     * @param evt
     */
private void detailsOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsOKButtonActionPerformed

    this.jButtonSave.setEnabled(false);
    if (this.validateEditTrack()) {

        this.jTabbedPaneMain.setEnabledAt(1, true);
        this.initMapping();
    }
}//GEN-LAST:event_detailsOKButtonActionPerformed

private void jComboBoxPosFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPosFieldActionPerformed
    if (this.jComboBoxPosField.getSelectedIndex() >= 0) {
        this.jCheckBoxPositionField.setSelected(true);
    } else {
        this.jCheckBoxPositionField.setSelected(false);
    }

    if (this.jCheckBoxPositionField.isSelected()) {
        Logger.getLogger(ImportTrackDialog.class.getName()).log(
                Level.INFO, "set Split Field: " + this.jComboBoxPosField.getSelectedItem().toString());

        this.importModul.setSplitFieldName(this.jComboBoxPosField.getSelectedItem().toString());
    }
    this.initMapping();
}//GEN-LAST:event_jComboBoxPosFieldActionPerformed

private void jPanelTrackFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanelTrackFocusLost
    this.detailsOKButtonActionPerformed(null);
}//GEN-LAST:event_jPanelTrackFocusLost

private void jPanelMapFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanelMapFocusLost
    this.saveMapButtonActionPerformed(null);
}//GEN-LAST:event_jPanelMapFocusLost
    int lastSelTab = -1;
private void jTabbedPaneMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneMainStateChanged
    if (lastSelTab != -1) {
        if (lastSelTab == 0) {
            if (!this.validateEditTrack()) {
                this.jTabbedPaneMain.setSelectedIndex(0);
            }
        }
        if (lastSelTab == 1) {
            if (!this.validateMapping()) {
                this.jTabbedPaneMain.setSelectedIndex(1);
            }
        }
    }
    this.lastSelTab = this.jTabbedPaneMain.getSelectedIndex();

}//GEN-LAST:event_jTabbedPaneMainStateChanged

private void jCheckBoxHasHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxHasHeaderActionPerformed
    if (this.importModul != null) {
        this.importModul.setHasHeader(jCheckBoxHasHeader.isSelected());
        this.initMapping();
    }
}//GEN-LAST:event_jCheckBoxHasHeaderActionPerformed

private void addSampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSampleActionPerformed
    SampleDetail d = SelectSampleDialog.getSampleSelection();
    if (d != null) {
        this.updateSamples(d);
    }
}//GEN-LAST:event_addSampleActionPerformed
    /**
     * finish sample , next tab
     *
     * @param evt
     */
private void jButtonSampleNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSampleNextActionPerformed
    this.jTabbedPaneMain.setEnabledAt(3, true);
    this.jTabbedPaneMain.setSelectedIndex(3);
//this.repaint();
}//GEN-LAST:event_jButtonSampleNextActionPerformed
    /**
     * reset sample
     *
     * @param evt
     */
private void jButtonResetSampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetSampleActionPerformed
    this.track.removeSamples();
    for (SampleInTrack s : track.getSamples()) {
        Logger.getLogger(ImportTrackDialog.class.getName()).log(Level.INFO,
                s.toFullString());
    }
}//GEN-LAST:event_jButtonResetSampleActionPerformed

private void jButtonCreateSampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateSampleActionPerformed
    SampleDetail d = this.createSample();
    if (d != null) {
        this.updateSamples(d);
    }
}//GEN-LAST:event_jButtonCreateSampleActionPerformed

    /**
     * validate sampledetails, add them to list if not already there
     *
     * @param index
     * @param sie
     */
    public class ImportWorker extends SwingWorker<Track, String> {

        XPortTrack importModul;
        private final InformableHandler informable;
        Track data = null;

        public ImportWorker(XPortTrack _port, Track _data, InformableHandler inf) {
            this.importModul = _port;
            this.informable = inf;

            this.data = _data;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String message : chunks) {
                informable.messageChanged(message);
            }
        }

        protected Track doInBackground() throws Exception {
            publish("run Import in Background...");
            setProgress(0);

            setProgress(10);
            Track d = null;
            try {

                d = importModul.doImportTrack(this.data, informable);

            } catch (Exception ex) {
                publish("error during import see logfile !");
                Logger.getLogger(ImportWorker.class.getName()).log(Level.WARNING,
                        "error during import !", ex);
                error("error during import see logfile !", null);

            }
            setProgress(90);

            return d;

        }

        @Override
        protected void done() {
            try {
                // Get the number of matches. Note that the
                // method get will throw any exception thrown
                // during the execution of the worker.
                data = get();
                if (data != null) {
                    //initImportPlatform(data);
                    publish(" done ...........................");
                    ImportTrackDialog.this.textHint.setText(" ok - import finished");

                } else {
                    publish(" ERROR see logfile for further details");
                    ImportTrackDialog.this.textHint.setText(" ERROR see logfile for further details");
                }

                //progressBar.setVisible(false);
            } catch (Exception e) {
                Logger.getLogger(ImportWorker.class.getName()).log(Level.WARNING, "do cbs ", e);
                error("error during import see logfile !", null);
            }
        }
    }

    /**
     * batch import for track
     *
     * @param filetype
     * @param filename
     * @param release
     * @param hasHeader
     * @param s1
     * @param s2
     * @param project
     * @param splitPos
     * @return
     */
    public static Track batch(String filetype, String filename, String release,
            boolean hasHeader,
            SampleDetail s1, SampleDetail s2, String project, String splitPos) {

        ImportTrackDialog d = new ImportTrackDialog();

        d.filetype = filetype;

        try {

            d.importModul = ServiceXPort.getXPortTrack(filetype);
            d.importModul.newImportTrack(filename);
            d.importModul.setHasHeader(hasHeader);

            // track detail
            d.track = new Track();
            d.track.copy(d.importModul.getTrack());
            d.track.setGenomeRelease(release);
            d.track.setOwner(ExperimentService.getUser());

            // add Samples
            SampleInTrack sie1 = null, sie2 = null;
            if (s1 != null && s1.getName() != null) {
                sie1 = d.track.addSample(s1, false);
            }
            if (s2 != null && s2.getName() != null) {
                sie2 = d.track.addSample(s2, false);
            }
            d.importModul.setProject(project);

            // mapping 
            String[] tmp = d.importModul.getFileColNames();

            List<String[]> mapping = d.importModul.getDefaultMappingFile2DBColNames();

            d.importModul.setSplitFieldName(splitPos);

            d.importModul.setMappingFile2DBColNames(mapping);

            // check if track already there
            Track _t = TrackService.getTrack(
                    d.track.getName(),
                    d.track.getGenomeRelease().toString());
            if (_t != null) {
                // older experiment with same name exists, create unique name, report error

                d.track.setName(Utils.getUniquableName(d.track.getName()));
            }

            Track neu = d.importModul.doImportTrack(d.track);

            neu.setNofImportErrors(d.importModul.getError());
            neu.setNofImportData(d.importModul.getNoimp());
            return neu;

        } catch (Exception ex) {
            Logger.getLogger(ImportTrackDialog.class.getName()).log(
                    Level.SEVERE, "", ex);
            return null;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSample;
    private javax.swing.JComboBox cbFileType;
    private javax.swing.JComboBox cbRelease;
    private javax.swing.JComboBox cbStudy;
    private javax.swing.JButton detailsOKButton;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonCancel1;
    private javax.swing.JButton jButtonCreateSample;
    private javax.swing.JButton jButtonResetDetail;
    private javax.swing.JButton jButtonResetMapping;
    private javax.swing.JButton jButtonResetSample;
    private javax.swing.JButton jButtonSampleNext;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxHasHeader;
    private javax.swing.JCheckBox jCheckBoxPositionField;
    private javax.swing.JComboBox jComboBoxPosField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelFile;
    private javax.swing.JPanel jPanelImport;
    private javax.swing.JPanel jPanelMap;
    private javax.swing.JPanel jPanelSample;
    private javax.swing.JPanel jPanelTableFile;
    private javax.swing.JPanel jPanelTableMap;
    private javax.swing.JPanel jPanelTrack;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPaneMapping;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    private javax.swing.JTable jTableSamples;
    private javax.swing.JTextField jTextFieldFileName;
    private javax.swing.JTextArea jTextMsg;
    private javax.swing.JButton saveMapButton;
    private javax.swing.JTable tableData;
    private javax.swing.JTable tableMap;
    private javax.swing.JLabel textHint;
    private org.molgen.genomeCATPro.guimodul.track.TrackDataView trackDataView1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
