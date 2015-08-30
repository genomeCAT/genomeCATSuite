/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.platform;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows PlatformTree component.
 */
public class PlatformTreeAction extends AbstractAction {

    public PlatformTreeAction() {
        super(NbBundle.getMessage(PlatformTreeAction.class, "CTL_PlatformTreeAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(PlatformTreeTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = PlatformTreeTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
