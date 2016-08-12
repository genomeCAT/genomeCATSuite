/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**
 *
 * @author tebel
 */
public class MyImageButtonCellEditor extends AbstractCellEditor
        implements TableCellEditor {

    String schema = null;
    int col;
    protected JButton button;

    public MyImageButtonCellEditor(int _col) {
        col = _col;
        button = new JButton();
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, schema, "ColorSchema",
                        JOptionPane.PLAIN_MESSAGE);

                fireEditingStopped();
            }
        });

        button.setBorderPainted(false);

    }

    public Object getCellEditorValue() {
        return null;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        Logger.getLogger(MyImageButtonCellEditor.class.getName()).log(
                Level.INFO,
                "getEditor: at row " + row + " col " + column);

        TableModel t = table.getModel();
        int rRow = table.convertRowIndexToModel(row);
        Logger.getLogger(MyImageButtonCellEditor.class.getName()).log(
                Level.INFO,
                "getValue: at row " + rRow + " col " + this.col);

        schema = (String) t.getValueAt(rRow, this.col);

        if (value instanceof Image) {
            button.setIcon(new ImageIcon((Image) value));
        } else {
            button.setText((value == null) ? "" : value.toString());
        }
        return button;
    }
}
