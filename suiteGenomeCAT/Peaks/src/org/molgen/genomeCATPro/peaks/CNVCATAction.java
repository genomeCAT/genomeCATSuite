/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows CNVCAT component.
 */
public class CNVCATAction extends AbstractAction {

    public CNVCATAction() {
        super(NbBundle.getMessage(CNVCATAction.class, "CTL_CNVCATAction"));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(CNVCATTopComponent.ICON_PATH, true)));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = CNVCATTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
