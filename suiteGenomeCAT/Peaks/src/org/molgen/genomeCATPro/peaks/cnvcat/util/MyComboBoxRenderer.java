package org.molgen.genomeCATPro.peaks.cnvcat.util;

import java.awt.Component;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @name MyComboBoxRenderer
 *
 * http://www.java2s.com/Tutorial/Java/0240__Swing/UsingaJComboBoxinaCellinaJTableComponent.htm
 */
public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {

    public MyComboBoxRenderer(Vector<?> items, boolean editable) {
        super(items);
        this.setEditable(editable);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelectedItem(value);
        return this;
    }
}
