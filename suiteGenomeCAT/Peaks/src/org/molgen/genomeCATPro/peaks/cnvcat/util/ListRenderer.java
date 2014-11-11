package org.molgen.genomeCATPro.peaks.cnvcat.util;

/* 
 * ColorRenderer.java (compiles with releases 1.2, 1.3, and 1.4) is used by 
 * TableDialogEditDemo.java.
 */
import java.awt.Component;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ListRenderer extends DefaultTableCellRenderer {

    public ListRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object list,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (list != null && list instanceof List) {
            String slist = null;
            for (String a : (List<String>) list) {
                if (slist != null) {
                    slist += ",";
                } else {
                    slist = "";
                }
                slist += a;
            }
            setText(slist);
        }
        else
            setText("");
        
        return this;



    }
}
