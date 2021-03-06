package org.molgen.genomeCATPro.cat;

/**
 * @name ViewDataDialog.java
 *
 * Created on October 10, 2012, 3:10 PM
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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.beansbinding.BindingGroup;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.MapService;

/**
 *
 *
 */
public class ViewMapDetail extends javax.swing.JDialog {

    MapDetail d = null;

    /**
     * Creates new form ViewDataDialog
     */
    public ViewMapDetail(java.awt.Frame parent, boolean modal, MapDetail d) {
        super(parent, modal);
        this.d = d;

        initComponents();
        this.initInfo();
        this.initTable(100);
    }

    void error(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message);
        Logger.getLogger(ViewMapDetail.class.getName()).log(
                Level.WARNING, message, e);

    }

    public String getDataName() {
        return this.d.getMapName();
    }

    public void setDataName(String dataName) {

    }

    public String getTableName() {
        return this.d.getTableData();
    }

    public void setTableName(String tableName) {

    }

    static void view(MapDetail _d) {
        Logger.getLogger(ViewMapDetail.class.getName()).log(
                Level.INFO, "view " + _d.toFullString());
        JDialog d = new ViewMapDetail(null, false, _d);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    void initTable(int nof) {

        Vector<String> colsdata = new Vector<String>();
        Vector<Vector<String>> _data = new Vector<Vector<String>>();
        try {
            colsdata = new Vector<String>(DBUtils.getCols(d.getTableData()));

            _data.add(new Vector<String>());

            _data = DBUtils.getData(nof, d.getTableData());
        } catch (Exception e) {
            error(e.getMessage(), e);
        }

        this.tableData.setModel(new DefaultTableModel(_data, colsdata));
        this.tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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

        jLabelName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableData = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabelTable = new javax.swing.JLabel();
        jButtonAll = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextInfo = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabelName.setFont(new java.awt.Font("Dialog", 1, 14));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataName}"), jLabelName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

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
        jScrollPane1.setViewportView(tableData);

        jButton1.setText("close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabelTable.setFont(new java.awt.Font("Dialog", 1, 14));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${tableName}"), jLabelTable, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jButtonAll.setText("show all");
        jButtonAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllActionPerformed(evt);
            }
        });

        jTextInfo.setColumns(20);
        jTextInfo.setEditable(false);
        jTextInfo.setRows(5);
        jTextInfo.setText(org.openide.util.NbBundle.getMessage(ViewMapDetail.class, "ViewMapDetail.jTextInfo.text")); // NOI18N
        jScrollPane2.setViewportView(jTextInfo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 871, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 871, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(724, Short.MAX_VALUE)
                        .addComponent(jButtonAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelTable, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTable, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButtonAll))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jScrollPane1, jScrollPane2});

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initInfo() {
        try {
            StringBuffer text = new StringBuffer();
            List<MapData> list = MapService.getMapDataList(this.d);
            for (MapData dd : list) {
                text.append("\n" + dd.getDataName() + "\n\n");
                text.append(dd.getDescription());
                text.append("******************************************************************************************************\n");

            }
            this.jTextInfo.setText(text.toString());
        } catch (Exception ex) {
            Logger.getLogger(ViewMapDetail.class.getName()).log(Level.SEVERE,
                    "initInfo", ex);
        }
    }

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    this.setVisible(false);
}//GEN-LAST:event_jButton1ActionPerformed

private void jButtonAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAllActionPerformed
    this.initTable(-1);
}//GEN-LAST:event_jButtonAllActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAll;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextInfo;
    private javax.swing.JTable tableData;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
