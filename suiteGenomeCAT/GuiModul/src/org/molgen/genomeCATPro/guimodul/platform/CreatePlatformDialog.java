package org.molgen.genomeCATPro.guimodul.platform;

/**
 * @name CreatePlatformDialog
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 *
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
import java.awt.Color;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import org.molgen.genomeCATPro.cghpro.xport.ServiceXPort;
import org.molgen.genomeCATPro.cghpro.xport.XPortImport;
import org.molgen.genomeCATPro.cghpro.xport.XPortPlatform;
import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.PlatformService;
import org.openide.util.NbPreferences;

/**
 * 140513       kt      set map only if file col really exists, otherwise highlight the column
 * 050413       kt      batch - set mapping added
 * 050413       kt      allow empty mapping
 * 120313	kt	readFilenames throws Exception 
 * 120313	kt	batch 
 */
public class CreatePlatformDialog extends javax.swing.JDialog {

    XPortPlatform importModul = null;
    String filetype;
    PlatformDetail detail = null;
    PlatformData data = null;

    /** Creates new form CreatePlatformDialog */
    public CreatePlatformDialog(java.awt.Frame parent) {
        super(parent, false);
        importModul = null;
        this.filetype = null;
        this.platformDetailView1 = new PlatformDetailView();
        this.platformDataView1 = new PlatformDataView();
        this.detail = this.platformDetailView1.getPlatform();
        this.data = this.platformDataView1.getPlatform();



        initComponents();
        this.jPanelFile.setEnabled(true);


        this.detailsOKButton.setEnabled(false);
        this.jButtonSave.setEnabled(false);
    }

    public CreatePlatformDialog(java.awt.Frame parent,
            XPortPlatform modul, String filetype) {
        this(parent);
        this.importModul = modul;

        this.filetype = filetype;


    // todo check if file is already choosen
    }

    public void setImportFile(String file, String filetype) {
        this.jTextFieldFileName.setText(file);
        if (this.filetype != null) {
            this.cbFileType.setSelectedItem(filetype);
        }
    }

    @Deprecated
    public void setPlatformDetail(PlatformDetail d) {
        this.initEditPlatform(d);
    }

    public PlatformData getPlatform() {
        return this.data;
    }

    void error(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message);
        Logger.getLogger(CreatePlatformDialog.class.getName()).log(
                Level.WARNING, message, e);

    }

    CreatePlatformDialog() {
        this.data = new PlatformData();
        this.detail = new PlatformDetail();
    }

    /**
     * summing up all necessary actions for batch processing
     * @param filetype
     * @param filename
     * @param method
     * @param type
     * @param release
     * @param splitPos
     * @return
     */
    public static PlatformData batch(String filetype, String filename,
            String method, String type, String release, List<String[]> mapping, String splitPos) {
        CreatePlatformDialog d = new CreatePlatformDialog();

        d.filetype = filetype;



        try {

            d.importModul = ServiceXPort.getXPortPlatform(filetype);
            d.importModul.newImportPlatform(filename);

            // platform detail
            d.detail.copy(d.importModul.getPlatformDetail());
            d.detail.setType(type);
            d.detail.setMethod(method);
            d.detail.setName(Utils.getUniquableName(d.detail.getName()));


            // mapping
            if (mapping == null) {
                mapping = d.importModul.getDefaultMappingFile2DBColNames();
            }
            d.importModul.setSplitFieldName(splitPos);




            d.importModul.setMappingFile2DBColNames(mapping);


            // data
            d.data.copy(d.importModul.getPlatformData(d.detail));
            d.data.setGenomeRelease(release);
            d.data.initTableData();

            PlatformData neud = d.importModul.doImportPlatform(d.detail, d.data);
            neud.setNofImportErrors(d.importModul.getError());
            neud.setNofImportData(d.importModul.getNoimp());
            return neud;


        } catch (Exception ex) {
            Logger.getLogger(CreatePlatformDialog.class.getName()).log(
                    Level.SEVERE, "", ex);
            return null;
        }

    }

    public CreatePlatformDialog(String filetype, String filename,
            String method, String type, String release, InformableHandler inf) throws Exception {

        this.filetype = filetype;


        try {

            this.importModul = ServiceXPort.getXPortPlatform(filetype);
            importModul.newImportPlatform(filename);

            // platform detail
            this.detail.copy(this.importModul.getPlatformDetail());
            this.detail.setType(type);
            this.detail.setMethod(method);
            this.detail.setName(Utils.getUniquableName(detail.getName()));


            // mapping
            List<String[]> mapping = this.importModul.getDefaultMappingFile2DBColNames();
            this.importModul.setMappingFile2DBColNames(mapping);


            // data
            this.data.copy(this.importModul.getPlatformData(this.detail));
            this.data.setGenomeRelease(release);
            this.data.initTableData();




            ImportWorker worker = new ImportWorker(importModul, detail, data, inf);
            worker.execute();
            while (!worker.isFinished()) {
                wait();
            }
        } catch (Exception ex) {
            Logger.getLogger(CreatePlatformDialog.class.getName()).log(
                    Level.SEVERE, "", ex);
            throw ex;
        }

    }

    boolean initImportModul() {
        if (this.jTextFieldFileName.getText() == null || this.jTextFieldFileName.getText().contentEquals("")) {
            error("please set filepath!", null);
            return false;
        }
        if (this.cbFileType.getSelectedIndex() < 0 ||
                this.cbFileType.getSelectedItem().toString().contentEquals("")) {
            this.textHint.setText("please select import type!");
            return false;
        }
        filetype = this.cbFileType.getSelectedItem().toString();
        this.importModul = ServiceXPort.getXPortPlatform(filetype);
        if (importModul == null) {
            error("no import modul found", null);
            return false;
        }
        try {
            importModul.newImportPlatform(this.jTextFieldFileName.getText());
        } catch (Exception e) {
            error(e.getMessage(), e);
            return false;
        }
        //this.initMapping();
        return this.initEditPlatform(null);
    }
    private static String emptyCol = "";

    boolean initMapping() {
        if (this.importModul == null) {
            error("no import modul found - please choose filepath and filetyp above!", null);
            return false;
        }
        Vector<String> listFileCols = new Vector<String>();
        Vector<Vector<String>> _data = new Vector<Vector<String>>();
        try {
            listFileCols = new Vector<String>(
                    Arrays.asList(importModul.getFileColNames()));


            _data.add(new Vector<String>());

            _data = importModul.readData(100);

            this.jComboBoxPosField.setModel(new DefaultComboBoxModel(listFileCols));
            if (this.importModul.hasSplitField()) {
                for (String col : listFileCols) {
                    if (col.contentEquals(this.importModul.getSplitFieldName())) {
                        this.jComboBoxPosField.setSelectedItem(col);
                    }
                }
            }

            this.tableData.setModel(new DefaultTableModel(_data, listFileCols));


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
                //050413    allow empty mapping
                Vector<String> filecols = new Vector(Arrays.asList(importModul.getFileColNames()));
                filecols.add(CreatePlatformDialog.emptyCol);
                JComboBox box = new JComboBox(filecols);
                //col.setMinWidth((int) box.getPreferredSize().getWidth());

                // get mapping init
                col.setCellEditor(new DefaultCellEditor(box));
                for (String[] m : mapping) {
                    if (m[XPortImport.ind_db].equalsIgnoreCase(this.tableMap.getColumnName(i))) {
                        //box.setSelectedItem(XPortImport.ind_file);
                        this.tableMap.getModel().setValueAt(CreatePlatformDialog.emptyCol, 0, i);
                        
                        //140513    kt  set map only if file col really exists, otherwise highlight the column
                        boolean found=false;
                        
                        for (String filecol : listFileCols) {
                            if (filecol.contentEquals(m[XPortImport.ind_file])) {
                                this.tableMap.getModel().setValueAt(m[XPortImport.ind_file], 0, i);
                                Logger.getLogger(
                                        CreatePlatformDialog.class.getName()).log(
                                        Level.INFO, "readSettings: found for col: " + m[XPortImport.ind_db] +
                                        " mapping: " + m[XPortImport.ind_file]);
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            this.tableMap.getColumnModel().getColumn(i).setCellRenderer(new  ColorColumnCellRenderer());
                        }

                    }
                }
            }
        } catch (Exception e) {
            error(e.getMessage(), e);
            return false;
        }

        this.jTabbedPaneMain.setSelectedIndex(1);
        this.textHint.setText("set for each column of database table a file column");
        return true;
    }

    class myTable extends JTable {

        @Override
        public Component prepareEditor(TableCellEditor editor, int row, int column) {
            Component c = super.prepareEditor(editor, row, column);
            this.getColumnModel().getColumn(column).setMinWidth((int) c.getPreferredSize().getWidth());
            return c;
        }
    }

    public class ColorColumnCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        
            c.setBackground( Color.RED );
            return c;
        }
        
    }

    boolean initEditPlatform(PlatformDetail platformdetail) {
        try {

            if (this.importModul == null) {
                error("no import type selected", null);
                return false;
            }

            if (platformdetail != null) {

                this.detail.copy(platformdetail);
                Logger.getLogger(
                        CreatePlatformDialog.class.getName()).log(
                        Level.INFO, "init with given platform detail: " + this.detail.toFullString());
            } else {

                //this.platformDetailView1 = new PlatformDetailView();
                //this.detail = this.platformDetailView1.getPlatform();

                this.detail.copy(this.importModul.getPlatformDetail());
                Logger.getLogger(
                        CreatePlatformDialog.class.getName()).log(
                        Level.INFO, "init with new platform detail: " + this.detail.toFullString());

            //this.platformDetailView1.setPlatform(this.importModul.getPlatformDetail());

            }
            this.platformDetailView1.setAllFieldsEditable(true);

            if (this.importModul.getRelease() != null) {
                this.cbRelease.setSelectedItem(this.importModul.getRelease());
            }
            this.jTabbedPaneMain.setSelectedIndex(0);
            this.detailsOKButton.setEnabled(true);

            this.textHint.setText("edit platform details");
        } catch (Exception e) {
            error("initPlatform", e);

        }
        return true;
    }

    boolean initImportPlatform(PlatformData d) {
        try {
            if (this.importModul == null) {
                error("no import type selected", null);
                return false;
            }
            if (d == null) {
                //this.platformDataView1 = new PlatformDataView();
                //this.data = this.platformDataView1.getPlatform();
                this.data.copy(this.importModul.getPlatformData(this.detail));
                Logger.getLogger(
                        CreatePlatformDialog.class.getName()).log(
                        Level.INFO, "init with new platform data: " + this.data.toFullString());

            } else {
                this.data.copy(d);
                Logger.getLogger(CreatePlatformDialog.class.getName()).log(
                        Level.INFO, "init with given platform data: " + this.data.toFullString());
            }
            this.data.setGenomeRelease(this.cbRelease.getSelectedItem().toString());
            this.data.initTableData();
            /**if (this.data.getGenomeRelease() != null) {
            this.platformDataView1.setGenomeReleaseEditable(false);
            }**/
            this.jTabbedPaneMain.setSelectedIndex(2);
            this.textHint.setText("press <import> to create new platform");
            this.jButtonSave.setEnabled(true);
        } catch (Exception e) {
            Logger.getLogger(
                    CreatePlatformDialog.class.getName()).log(
                    Level.SEVERE,
                    "initImportPlatform", e);
            error("error init import", null);
            return false;
        }
        return true;
    }

    /**
     * validate detail fields
     */
    private boolean saveEditPlatform() {

        try {
            PlatformDetail d = PlatformService.getPlatformDetailByName(detail.getName());
            if (d != null) {
                Logger.getLogger(CreatePlatformDialog.class.getName()).log(Level.SEVERE,
                        "validate PlatformDetail name: name already given, please change name");
                error("name already given, please change name for new platform", null);
                return false;
            }
        } catch (Exception e) {
            Logger.getLogger(CreatePlatformDialog.class.getName()).log(Level.SEVERE,
                    "validate PlatformDetail name: ", e);

        }
        if (this.cbRelease.getSelectedIndex() < 0) {
            error("no release set", null);
            return false;
        }
        if (this.detail.getMethod() == null) {
            error("no method set", null);
            return false;
        }
        if (this.detail.getName() == null) {
            error("no name set", null);
            return false;
        }
        if (this.detail.getType() == null) {
            error("no type set", null);
            return false;
        }


        return this.initMapping();

    }

    boolean saveMapping() {
        List<String[]> mapping = new Vector<String[]>();

        for (int i = 0; i < this.tableMap.getColumnCount(); i++) {
            String dbCol = this.tableMap.getColumnName(i);
            String fileCol = (String) this.tableMap.getValueAt(0, i);

            // get mapping init
            if (fileCol != null && !fileCol.contentEquals(CreatePlatformDialog.emptyCol)) {
                String[] m = new String[2];

                m[XPortImport.ind_db] = dbCol;
                m[XPortImport.ind_file] = fileCol;
                mapping.add(m);
                Logger.getLogger(CreatePlatformDialog.class.getName()).log(
                        Level.INFO, "validate: set mapping for col: " + m[1] + " mapping: " + m[0]);

            }
        }


        this.importModul.setMappingFile2DBColNames(mapping);
        return this.initImportPlatform(null);
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
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jPanelPlatform = new javax.swing.JPanel();
        jButtonResetDetail = new javax.swing.JButton();
        platformDetailView1 = platformDetailView1;
        detailsOKButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cbRelease = new javax.swing.JComboBox();
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
        jPanelPlatformData = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextMsg = new javax.swing.JTextArea();
        platformDataView1 = platformDataView1;
        jPanelButton = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel1 = new javax.swing.JButton();
        textHint = new javax.swing.JLabel();
        jPanelFile = new javax.swing.JPanel();
        jTextFieldFileName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonBrowse = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cbFileType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.title")); // NOI18N

        jTabbedPaneMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jTabbedPaneMain.setForeground(java.awt.Color.black);
        jTabbedPaneMain.setFont(new java.awt.Font("Dialog", 1, 14));

        jPanelPlatform.setEnabled(false);
        jPanelPlatform.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPanelPlatformFocusLost(evt);
            }
        });

        jButtonResetDetail.setForeground(new java.awt.Color(255, 51, 153));
        jButtonResetDetail.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jButtonResetDetail.text")); // NOI18N
        jButtonResetDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetDetailActionPerformed(evt);
            }
        });

        detailsOKButton.setForeground(new java.awt.Color(255, 51, 153));
        detailsOKButton.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.detailsOKButton.text")); // NOI18N
        detailsOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsOKButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jLabel4.text")); // NOI18N

        cbRelease.setModel(new javax.swing.DefaultComboBoxModel(DBUtils.getAllReleases()));

        javax.swing.GroupLayout jPanelPlatformLayout = new javax.swing.GroupLayout(jPanelPlatform);
        jPanelPlatform.setLayout(jPanelPlatformLayout);
        jPanelPlatformLayout.setHorizontalGroup(
            jPanelPlatformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPlatformLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(platformDetailView1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPlatformLayout.createSequentialGroup()
                .addContainerGap(659, Short.MAX_VALUE)
                .addComponent(detailsOKButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonResetDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanelPlatformLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbRelease, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(393, Short.MAX_VALUE))
        );
        jPanelPlatformLayout.setVerticalGroup(
            jPanelPlatformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPlatformLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(platformDetailView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelPlatformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelPlatformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonResetDetail)
                    .addComponent(detailsOKButton))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jPanelPlatform.TabConstraints.tabTitle"), jPanelPlatform); // NOI18N

        jPanelMap.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPanelMapFocusLost(evt);
            }
        });

        jPanelTableFile.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jPanelTableFile.border.title"))); // NOI18N

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE)
        );
        jPanelTableFileLayout.setVerticalGroup(
            jPanelTableFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
        );

        jPanelTableMap.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jPanelTableMap.border.title"))); // NOI18N

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
            .addGroup(jPanelTableMapLayout.createSequentialGroup()
                .addComponent(jScrollPaneMapping, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelTableMapLayout.setVerticalGroup(
            jPanelTableMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneMapping, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jButtonResetMapping.setForeground(new java.awt.Color(255, 51, 102));
        jButtonResetMapping.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jButtonResetMapping.text")); // NOI18N
        jButtonResetMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetMappingActionPerformed(evt);
            }
        });

        saveMapButton.setForeground(new java.awt.Color(255, 51, 102));
        saveMapButton.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.saveMapButton.text")); // NOI18N
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

        jCheckBoxPositionField.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxPositionField.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jCheckBoxPositionField.text")); // NOI18N

        javax.swing.GroupLayout jPanelMapLayout = new javax.swing.GroupLayout(jPanelMap);
        jPanelMap.setLayout(jPanelMapLayout);
        jPanelMapLayout.setHorizontalGroup(
            jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMapLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanelTableFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(jPanelMapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMapLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jCheckBoxPositionField)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxPosField, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMapLayout.createSequentialGroup()
                        .addGroup(jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelMapLayout.createSequentialGroup()
                                .addComponent(saveMapButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonResetMapping))
                            .addComponent(jPanelTableMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20))))
        );
        jPanelMapLayout.setVerticalGroup(
            jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelTableFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTableMap, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMapLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jComboBoxPosField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonResetMapping)
                            .addComponent(saveMapButton)))
                    .addGroup(jPanelMapLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxPositionField)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jPanelMap.TabConstraints.tabTitle"), jPanelMap); // NOI18N

        jTextMsg.setColumns(20);
        jTextMsg.setRows(5);
        jScrollPane2.setViewportView(jTextMsg);

        javax.swing.GroupLayout jPanelPlatformDataLayout = new javax.swing.GroupLayout(jPanelPlatformData);
        jPanelPlatformData.setLayout(jPanelPlatformDataLayout);
        jPanelPlatformDataLayout.setHorizontalGroup(
            jPanelPlatformDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPlatformDataLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanelPlatformDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPlatformDataLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 725, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(platformDataView1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelPlatformDataLayout.setVerticalGroup(
            jPanelPlatformDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPlatformDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(platformDataView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jPanelPlatformData.TabConstraints.tabTitle"), jPanelPlatformData); // NOI18N

        jPanelButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButtonSave.setForeground(new java.awt.Color(255, 51, 153));
        jButtonSave.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jButtonSave.text")); // NOI18N
        jButtonSave.setEnabled(false);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonCancel1.setForeground(java.awt.Color.black);
        jButtonCancel1.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jButtonCancel1.text")); // NOI18N
        jButtonCancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancel1ActionPerformed(evt);
            }
        });

        textHint.setFont(new java.awt.Font("Dialog", 3, 12));
        textHint.setForeground(new java.awt.Color(255, 51, 153));
        textHint.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.textHint.text")); // NOI18N

        javax.swing.GroupLayout jPanelButtonLayout = new javax.swing.GroupLayout(jPanelButton);
        jPanelButton.setLayout(jPanelButtonLayout);
        jPanelButtonLayout.setHorizontalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGap(301, 301, 301)
                .addComponent(jButtonSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textHint, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonCancel1)
                        .addComponent(jButtonSave))
                    .addComponent(textHint, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelFile.setBorder(javax.swing.BorderFactory.createTitledBorder("File"));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jLabel1.text")); // NOI18N

        jButtonBrowse.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jButtonBrowse.text")); // NOI18N
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jLabel2.text")); // NOI18N

        Vector<String> filetypes = ServiceXPort.getFileTypesPlatformImport();
        filetypes.add(0, "");
        cbFileType.setModel(new DefaultComboBoxModel(filetypes));
        cbFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFileTypeActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel3.setText(org.openide.util.NbBundle.getMessage(CreatePlatformDialog.class, "CreatePlatformDialog.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanelFileLayout = new javax.swing.GroupLayout(jPanelFile);
        jPanelFile.setLayout(jPanelFileLayout);
        jPanelFileLayout.setHorizontalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFileLayout.createSequentialGroup()
                .addContainerGap(89, Short.MAX_VALUE)
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelFileLayout.createSequentialGroup()
                        .addComponent(cbFileType, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)))
                .addGap(34, 34, 34)
                .addComponent(jButtonBrowse)
                .addContainerGap())
        );
        jPanelFileLayout.setVerticalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFileLayout.createSequentialGroup()
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(cbFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)))
            .addComponent(jButtonBrowse)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 801, Short.MAX_VALUE)
                    .addComponent(jPanelFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
    //doImportPlatform(PlatformDetail platformdetail, String release);

    //this.initPlatformData();
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    InformableHandler informable = new InformableHandler() {

        public void messageChanged(String message) {
            jTextMsg.append(message + "\n");
        }
    };
    informable.messageChanged("Start Import....");
    //informable.messageChanged("Get Data for " + sample.getName());
    ImportWorker worker = new ImportWorker(importModul, detail, data, informable);
    worker.execute();

    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

}//GEN-LAST:event_jButtonSaveActionPerformed
private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
    String path = NbPreferences.forModule(CreatePlatformDialog.class).get("pathPreference", "");
    JFileChooser importFileChooser = new JFileChooser(path);
    int returnVal = importFileChooser.showOpenDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }
    this.jTextFieldFileName.setText(importFileChooser.getSelectedFile().getPath());
    this.importModul = null;
    Logger.getLogger(CreatePlatformDialog.class.getName()).log(Level.INFO,
            "You chose to import as annotation file: " +
            this.jTextFieldFileName.getText());


    this.initImportModul();
    //ImportFileDialog.filePath = importFileChooser.getCurrentDirectory().toString();
    NbPreferences.forModule(CreatePlatformDialog.class).put("pathPreference",
            importFileChooser.getSelectedFile().getPath());
}//GEN-LAST:event_jButtonBrowseActionPerformed

private void cbFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFileTypeActionPerformed
    this.importModul = null;

    this.jButtonSave.setEnabled(false);
    if (!this.cbFileType.getSelectedItem().toString().equalsIgnoreCase("")) {
        this.initImportModul();
    }
}//GEN-LAST:event_cbFileTypeActionPerformed

private void jButtonResetDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetDetailActionPerformed

    this.jButtonSave.setEnabled(false);
    this.initEditPlatform(null);
}//GEN-LAST:event_jButtonResetDetailActionPerformed

private void jButtonResetMappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetMappingActionPerformed

    this.jButtonSave.setEnabled(false);
    this.initMapping();

}//GEN-LAST:event_jButtonResetMappingActionPerformed

private void jButtonCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancel1ActionPerformed
    this.setVisible(false);
}//GEN-LAST:event_jButtonCancel1ActionPerformed

private void saveMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMapButtonActionPerformed

    this.jButtonSave.setEnabled(false);
    this.saveMapping();
}//GEN-LAST:event_saveMapButtonActionPerformed

private void detailsOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsOKButtonActionPerformed

    this.jButtonSave.setEnabled(false);
    this.saveEditPlatform();
}//GEN-LAST:event_detailsOKButtonActionPerformed

private void jComboBoxPosFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPosFieldActionPerformed
    if (this.jComboBoxPosField.getSelectedIndex() >= 0) {
        this.jCheckBoxPositionField.setSelected(true);
    } else {
        this.jCheckBoxPositionField.setSelected(false);
    }

    if (this.jCheckBoxPositionField.isSelected()) {
        Logger.getLogger(CreatePlatformDialog.class.getName()).log(
                Level.INFO, "set Split Field: " + this.jComboBoxPosField.getSelectedItem().toString());

        this.importModul.setSplitFieldName(this.jComboBoxPosField.getSelectedItem().toString());
    }
    this.initMapping();
}//GEN-LAST:event_jComboBoxPosFieldActionPerformed

private void jPanelPlatformFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanelPlatformFocusLost
    //this.detailsOKButtonActionPerformed(null);
    // TODO add your handling code here:
}//GEN-LAST:event_jPanelPlatformFocusLost

private void jPanelMapFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanelMapFocusLost
// TODO add your handling code here: 
    //this.saveMapButtonActionPerformed(null);
}//GEN-LAST:event_jPanelMapFocusLost
    public class ImportWorker extends SwingWorker<PlatformData, String> {

        XPortPlatform importModul;
        private final InformableHandler informable;
        PlatformDetail detail = null;
        PlatformData data = null;
        boolean finished = false;

        synchronized public boolean isFinished() {
            return finished;
        }

        synchronized public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public ImportWorker(XPortPlatform _port, PlatformDetail _detail, PlatformData _data, InformableHandler inf) {
            this.importModul = _port;
            this.informable = inf;
            this.detail = _detail;
            this.data = _data;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String message : chunks) {
                informable.messageChanged(message);
            }
        }

        protected PlatformData doInBackground() throws Exception {
            publish("run Import in Background...");
            setProgress(0);


            setProgress(10);
            PlatformData d = null;
            try {

                d = importModul.doImportPlatform(this.detail, this.data, this.informable);



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
                    initImportPlatform(data);

                    publish(" done ...........................");
                    setFinished(true);
                    textHint.setText("done");
                } else {
                    publish(" ERROR see above for further details");
                }

            //progressBar.setVisible(false);
            } catch (Exception e) {
                Logger.getLogger(ImportWorker.class.getName()).log(Level.WARNING, "do cbs ", e);
                error("error during import see logfile !", null);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbFileType;
    private javax.swing.JComboBox cbRelease;
    private javax.swing.JButton detailsOKButton;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonCancel1;
    private javax.swing.JButton jButtonResetDetail;
    private javax.swing.JButton jButtonResetMapping;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxPositionField;
    private javax.swing.JComboBox jComboBoxPosField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelFile;
    private javax.swing.JPanel jPanelMap;
    private javax.swing.JPanel jPanelPlatform;
    private javax.swing.JPanel jPanelPlatformData;
    private javax.swing.JPanel jPanelTableFile;
    private javax.swing.JPanel jPanelTableMap;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneMapping;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    private javax.swing.JTextField jTextFieldFileName;
    private javax.swing.JTextArea jTextMsg;
    private org.molgen.genomeCATPro.guimodul.platform.PlatformDataView platformDataView1;
    private org.molgen.genomeCATPro.guimodul.platform.PlatformDetailView platformDetailView1;
    private javax.swing.JButton saveMapButton;
    private javax.swing.JTable tableData;
    private javax.swing.JTable tableMap;
    private javax.swing.JLabel textHint;
    // End of variables declaration//GEN-END:variables
}
