/*
 * ChooseArrayViewDialog.java
 *
 * Created on July 13, 2011, 10:41 AM

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
 
package org.molgen.genomeCATPro.cat.maparr;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 * @name ChooseArrayViewDialog
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
/**
 * 21.05.12 kt ChooseArrayViewDialog.getData bool to false to minimize
 * preselected Views (for performance reasons)
 *
 */
public class ChooseArrayViewDialog extends javax.swing.JDialog {

    final static int columnDataOffSet = 2;

    /**
     * methods how to aggregate data tracks
     */
    public final static String[] rowNames = {
        "Ratio by ProbeId", // aggregate by data internal probeID
        "Ratio by Gene",  // aggregate by data interal gene field
        "Ratio by RefSeqGene", // aggregate by genomecats annotation track "gene"
        "Ratio per Region"  // aggregate by one of genomecats annotation tracks
    };
    RegionArray iSpot;
    Data[] list = null;
    List<String> colNames = new Vector<>();
    private ArrayClazzModel dataModel;
    private JTable rowHeaderTable;
    private JViewport jv;

    /**
     * Creates new form ChooseArrayViewDialog
     */
    public ChooseArrayViewDialog(java.awt.Frame parent, Data[] _list) {
        super(parent, true);
        list = _list;

        for (Data d : list) {
            colNames.add(d.getName());
        }

        // Create a column model for the main table. This model ignores the first
        // column added and sets a minimum width of 150 pixels for all others.
        Object[][] data = getData(list);
        this.dataModel = new ArrayClazzModel(data, colNames);

        TableColumnModel mainModel = new DefaultTableColumnModel() {

            int c = -1;
            //boolean first = true;

            @Override
            public void addColumn(TableColumn tc) {
                c++;
                // Drop the first column, which will be the row header.
                if (c == 0) {
                    //first = false;
                    return;
                }
                if (c == 1) {
                    tc.setMinWidth(120);
                } else {
                    tc.setMinWidth(200);
                }
                // Just for looks, really...
                super.addColumn(tc);
            }
        };

        // Create a column model that will serve as our row header table. This model
        // picks a maximum width and stores only the first column.
        TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {

            boolean first = true;

            @Override
            public void addColumn(TableColumn tc) {
                if (first) {
                    tc.setMaxWidth(120);
                    super.addColumn(tc);
                    first = false;
                }
                // Drop the rest of the columns; this is the header column only.
            }
        };

        this.dataTable = new JTable(dataModel, mainModel);

        // Set up the header column and hook it up to everything.
        this.rowHeaderTable = new JTable(dataModel, rowHeaderModel);
        this.rowHeaderTable.createDefaultColumnsFromModel();
        this.dataTable.createDefaultColumnsFromModel();

        // Make sure that selections between the main table and the header stay in sync
        // (by sharing the same model).
        this.dataTable.setSelectionModel(this.rowHeaderTable.getSelectionModel());
        // Make the header column look pretty.
        //    headerColumn.setBorder(BorderFactory.createEtchedBorder( ));
        this.rowHeaderTable.setBackground(Color.lightGray);
        this.rowHeaderTable.setColumnSelectionAllowed(false);
        this.rowHeaderTable.setCellSelectionEnabled(false);

        this.jv = new JViewport();
        jv.setView(this.rowHeaderTable);
        jv.setPreferredSize(this.rowHeaderTable.getMaximumSize());
        this.dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        initComponents();
        this.jScrollPane1.setRowHeaderView(jv);
        pack();
        setLocationRelativeTo(null);

    }

    static ArrayData[] getArrayDataList(Data[] list) {

        ChooseArrayViewDialog l = new ChooseArrayViewDialog(
                null, list);

        l.setVisible(true);
        return l.getArrayDataList();
    }

    ArrayData[] getArrayDataList() {
        List<ArrayData> newList = new Vector<>();

        Vector w;
        Class clazz;
        int i = -1;

        for (Data d : list) {
            i++;
            for (int j = 0; j < ChooseArrayViewDialog.rowNames.length; j++) {
                if (((Boolean) this.dataModel.getValueAt(j, i + ChooseArrayViewDialog.columnDataOffSet)) == null) {
                    continue;
                }
                if (((Boolean) this.dataModel.getValueAt(j, i + ChooseArrayViewDialog.columnDataOffSet)).booleanValue() == true) {
                    switch (j) {
                        case 0:
                            clazz = ArrayView.class;
                            break;
                        case 1:
                            clazz = ArrayGeneView.class;
                            break;
                        case 2:
                            clazz = ArrayRefseqGeneView.class;
                            break;

                        case 3:
                            clazz = ArrayTrackView.class;
                            break;
                        default:
                            clazz = ArrayView.class;
                    }

                    Logger.getLogger(ChooseArrayViewDialog.class.getName()).log(Level.INFO,
                            "Choosen: " + d.getName() + " " + clazz.getName());
                    ArrayData ad = ArrayData.createArrayData(clazz, d);

                    newList.add(ad);

                }
            }
        }
        return newList.toArray(new ArrayData[newList.size()]);
    }

    /**
     * get array columns (%ratio%) from db -> set possible array-views
     *
     */
    Object[][] getData(Data[] list) {

        Object[][] newData;

        newData = new Object[list.length + ChooseArrayViewDialog.columnDataOffSet][];
        newData[0] = new Boolean[ChooseArrayViewDialog.rowNames.length + 1];
        newData[1] = new Boolean[ChooseArrayViewDialog.rowNames.length + 1];
        Arrays.fill(newData[1], false);
        int i = ChooseArrayViewDialog.columnDataOffSet;

        // create structure: all, hasProbeView, hasGeneView, hasRegionView
        for (Data d : list) {
            newData[i] = new Boolean[ChooseArrayViewDialog.rowNames.length + 1];
            //newData[i][0] = true;

            iSpot = ArrayManager.getClazz(d.getClazz());
            if (iSpot.hasProbeView()) {
                newData[i][0] = true;
            } else {
                newData[i][0] = null;
            }
            if (iSpot.hasGeneView()) {
                newData[i][1] = false;//
                newData[i][2] = false;
            } else {
                newData[i][1] = null;
                newData[i][2] = null;
            }
            if (iSpot.hasRegionView()) {
                newData[i][3] = true;
            } else {
                newData[i][3] = null;
            }
            i++;
        }

        return newData;
    }

    class MyTableModel extends DefaultTableModel {

        public MyTableModel(java.util.Vector data, java.util.Vector cols) {
            super(data, cols);
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            Object o = getValueAt(row, col);
            if (o == null) {
                return false;
            } else {
                return true;
            }
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane(this.dataTable);
        dataTable = this.dataTable ;
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ChooseArrayViewDialog.class, "ChooseArrayViewDialog.jLabel1.text")); // NOI18N

        jScrollPane1.setRowHeaderView(this.jv);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setViewportView(dataTable);

        jButtonOK.setText(org.openide.util.NbBundle.getMessage(ChooseArrayViewDialog.class, "ChooseArrayViewDialog.jButtonOK.text")); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setText(org.openide.util.NbBundle.getMessage(ChooseArrayViewDialog.class, "ChooseArrayViewDialog.jButtonCancel.text")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(272, 272, 272)
                        .addComponent(jButtonOK)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(49, 49, 49)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonCancel))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    this.list = new Data[0];
    dispose();
}//GEN-LAST:event_jButtonCancelActionPerformed

private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
    dispose();
}//GEN-LAST:event_jButtonOKActionPerformed

    class ArrayClazzModel extends AbstractTableModel {

        private final String[] rowNames = ChooseArrayViewDialog.rowNames;
        private final Object[][] data;
        private final String[] colNames;

        ArrayClazzModel(Object[][] data, List<String> arrayIds) {
            super();
            this.data = data;

            this.colNames = new String[arrayIds.size() + ChooseArrayViewDialog.columnDataOffSet];
            this.colNames[0] = "View";
            this.colNames[1] = "Set All";
            int i = -1;
            for (String id : arrayIds) {
                i++;
                this.colNames[i + ChooseArrayViewDialog.columnDataOffSet] = id;
                Logger.getLogger(ArrayFrame.class.getName()).log(Level.INFO,
                        this.colNames[i + ChooseArrayViewDialog.columnDataOffSet]);
            }

        }

        @Override
        public int getColumnCount() {
            return this.colNames.length;
        }

        @Override
        public int getRowCount() {
            return this.rowNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return colNames[col];
        }

        /*public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
        }
        
         */
        @Override
        public Object getValueAt(int row, int column) {

            if (column == 0) {
                return ChooseArrayViewDialog.rowNames[row];
            }
            /*
            if (row == 0) {
            return false;
            }
             */
            return ((Boolean) data[column][row]);
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (!this.isCellEditable(row, column)) {
                return;
            }
            if (column == 0) {
                return;
            }
            if (column == 1) {
                fireTableCellUpdated(row, 1);

                data[column][row] = value;
                for (int i = ChooseArrayViewDialog.columnDataOffSet; i < this.colNames.length; i++) {
                    setValueAt(value, row, i);
                }
            } else {
                data[column][row] = value;
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class getColumnClass(int column) {
            if (column > 0) {
                return Boolean.class;
            } else {
                return String.class;
                //return getValueAt(1, column).getClass();
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 0) {
                return false;
            }
            if (col == 1) {
                return true;
            }
            Object o = getValueAt(row, col);
            if (o == null) {
                return false;
            } else {
                return true;
            }

        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable dataTable;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
