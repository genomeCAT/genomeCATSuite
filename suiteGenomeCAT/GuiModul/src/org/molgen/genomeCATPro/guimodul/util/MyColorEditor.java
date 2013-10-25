/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author tebel
 */
public class MyColorEditor extends AbstractCellEditor
        implements TableCellEditor,
        ActionListener {

    String text = "test";
    Color currcolor;
    JButton button;
    JDialog dialog;
    protected static final String EDIT = "edit";

    public MyColorEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setBackground(this.currcolor);
    //Set up the dialog that the button brings up.



    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            JOptionPane.showMessageDialog(null, text, "ColorSchema",
                    JOptionPane.PLAIN_MESSAGE);

            //Make the renderer reappear.
            fireEditingStopped();

        }
    }

    public Object getCellEditorValue() {
        return currcolor;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currcolor = (Color) value;
        return button;
    }
}
