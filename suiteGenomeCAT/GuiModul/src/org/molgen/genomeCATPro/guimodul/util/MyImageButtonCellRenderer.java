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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author tebel
 */
public class MyImageButtonCellRenderer extends JButton implements TableCellRenderer {

    

    public MyImageButtonCellRenderer() {
       
        setOpaque(true);
       
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        //setForeground(table.getForeground());
        
        if (value instanceof Image) {
            setIcon(new ImageIcon((Image) value));
        } else {
            setText((value == null) ? "" : value.toString());


        }
        return this;
    }
}

    