/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.util;

import javax.print.attribute.AttributeSet;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.PlainDocument;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionImpl;

/**
 *
 * @author tebel
 */
@Deprecated

public class RegionEditText extends PlainDocument {

    JComboBox box = null;

    public RegionEditText(JComboBox _box) {
        box = _box;
    }

    public void insertString(int offs, String str, AttributeSet a) {

        System.out.println(str);
        ComboBoxModel c;
        if (str.matches("chr[1-9XY]{1,2}:[0-9]{1,20}-[0-9]{1,20}")) {
            String chrom = str.substring(0, str.indexOf(":"));
            Long from = Long.parseLong(str.substring(str.indexOf(":") + 1, str.indexOf("_")));
            Long to = Long.parseLong(str.substring(str.indexOf("-") + 1, str.length()));
            Region r = new RegionImpl("", chrom, from, to);

            box.setSelectedItem(r);
        }

    }
}
