package org.molgen.genomeCATPro.peaks.cnvcat.util;

import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * @name MyComboBoxEditor
 * http://www.java2s.com/Tutorial/Java/0240__Swing/UsingaJComboBoxinaCellinaJTableComponent.htm
 * 
 */
public class MyComboBoxEditor extends DefaultCellEditor {

    public MyComboBoxEditor(Vector<?>  items) {
        super(new JComboBox(items));
        
    }
    public MyComboBoxEditor(JComboBox box) {
        super(box);        
    }
}
