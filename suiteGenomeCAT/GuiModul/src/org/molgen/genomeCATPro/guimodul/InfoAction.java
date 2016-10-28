/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows Info component.
 */
@ActionID(id = "org.molgen.genomeCATPro.guimodul.InfoAction", category = "Window")
@ActionRegistration(displayName = "INFO", lazy = false)
public class InfoAction extends AbstractAction {

    public InfoAction() {
      //  super(NbBundle.getMessage(InfoAction.class, "CTL_InfoAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(InfoTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = InfoTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
