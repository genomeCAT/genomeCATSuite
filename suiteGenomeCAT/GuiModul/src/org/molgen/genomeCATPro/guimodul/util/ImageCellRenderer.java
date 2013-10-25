package org.molgen.genomeCATPro.guimodul.util;
/**
 * @name ImageCellRenderer
 *
 * Created on October 25, 2011, 12:56 PM
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @version "%I%, %G%
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * 261012   kt  image with border
 */
public class ImageCellRenderer extends JLabel implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); //kt 261012
        // setFocusPainted(false);

        if (value instanceof Image) {
            ImageIcon i = new ImageIcon((Image) value);

            setIcon(i);
        }





        return this;
    }
}


