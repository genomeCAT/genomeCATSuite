package org.molgen.genomeCATPro.common;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @name KtTextFieldLengthListener
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Nov 12, 2009
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
public class KtTextFieldLengthListener implements DocumentListener {

    int length = 100;
    JTextField textfield;

    public KtTextFieldLengthListener(JTextField t, int length) {
        textfield = t;
        this.length = length;
    }

    public void changedUpdate(DocumentEvent e) {
        validate(e, e.getLength());
    }

    public void insertUpdate(DocumentEvent e) {
        validate(e, e.getLength());
    }
    // chars entfernen die nach der 9ten Position kommen
    //if (doc.getLength() > 9)

    public void removeUpdate(DocumentEvent e) {
        validate(e, e.getLength() * -1);
    }

    void validate(DocumentEvent e, int lenght) {
        int docl = e.getDocument().getLength();

        if (docl > this.length) {
            textfield.setBackground(Color.RED);
        } else {
            textfield.setBackground(Color.white);
        }
        System.out.println("doc textfield length " + docl);

    }
}
