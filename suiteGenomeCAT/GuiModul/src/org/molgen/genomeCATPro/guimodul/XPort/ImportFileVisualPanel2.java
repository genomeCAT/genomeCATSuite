/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.XPort;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public final class ImportFileVisualPanel2 extends JPanel {

    /**
     * Creates new form ImportFileVisualPanel2
     */
    public ImportFileVisualPanel2() {
        initComponents();
    }

    @Override
    public String getName() {
        return "Set Mapping";
    }

    public JTable getJTableData() {
        return jTableData;
    }

    public void setJTableData(JTable jTableData) {
        this.jTableData = jTableData;
    }

    public JTable getJTableMap() {
        return jTableMap;
    }

    public void setJTableMap(JTable jTableMap) {
        this.jTableMap = jTableMap;
    }

    class myTable extends JTable {

        @Override
        public Component prepareEditor(TableCellEditor editor, int row, int column) {
            Component c = super.prepareEditor(editor, row, column);
            this.getColumnModel().getColumn(column).setMinWidth((int) c.getPreferredSize().getWidth());
            return c;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelFile = new javax.swing.JPanel();
        jScrollPaneFileContent = new javax.swing.JScrollPane();
        jTableData = new javax.swing.JTable();
        jLabelMappingHint = new javax.swing.JLabel();
        jScrollPaneMapping = new javax.swing.JScrollPane();
        jTableMap = new myTable();
        jLabelFile = new javax.swing.JLabel();
        jLabelMapping = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(750, 500));

        jTableData.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPaneFileContent.setViewportView(jTableData);

        jLabelMappingHint.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabelMappingHint, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel2.class, "ImportFileVisualPanel2.jLabelMappingHint.text")); // NOI18N

        jTableMap.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jTableMap.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPaneMapping.setViewportView(jTableMap);
        if (jTableMap.getColumnModel().getColumnCount() > 0) {
            jTableMap.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel2.class, "ImportFileVisualPanel2.jTableMap.columnModel.title0")); // NOI18N
            jTableMap.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel2.class, "ImportFileVisualPanel2.jTableMap.columnModel.title1")); // NOI18N
            jTableMap.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel2.class, "ImportFileVisualPanel2.jTableMap.columnModel.title2")); // NOI18N
            jTableMap.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(ImportFileVisualPanel2.class, "ImportFileVisualPanel2.jTableMap.columnModel.title3")); // NOI18N
        }

        org.openide.awt.Mnemonics.setLocalizedText(jLabelFile, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel2.class, "ImportFileVisualPanel2.jLabelFile.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabelMapping, org.openide.util.NbBundle.getMessage(ImportFileVisualPanel2.class, "ImportFileVisualPanel2.jLabelMapping.text")); // NOI18N

        javax.swing.GroupLayout jPanelFileLayout = new javax.swing.GroupLayout(jPanelFile);
        jPanelFile.setLayout(jPanelFileLayout);
        jPanelFileLayout.setHorizontalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFileLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabelFile)
                        .addComponent(jLabelMapping)
                        .addComponent(jScrollPaneFileContent, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                        .addComponent(jScrollPaneMapping))
                    .addComponent(jLabelMappingHint, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFileLayout.setVerticalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFile)
                .addGap(7, 7, 7)
                .addComponent(jScrollPaneFileContent, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabelMapping)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneMapping, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMappingHint, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 748, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanelFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 475, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanelFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelFile;
    private javax.swing.JLabel jLabelMapping;
    private javax.swing.JLabel jLabelMappingHint;
    private javax.swing.JPanel jPanelFile;
    private javax.swing.JScrollPane jScrollPaneFileContent;
    private javax.swing.JScrollPane jScrollPaneMapping;
    private javax.swing.JTable jTableData;
    private javax.swing.JTable jTableMap;
    // End of variables declaration//GEN-END:variables
}
