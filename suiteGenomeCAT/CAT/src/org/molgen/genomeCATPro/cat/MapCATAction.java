/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows MapCAT component.
 */
public class MapCATAction extends AbstractAction {

    public MapCATAction() {
        super(NbBundle.getMessage(MapCATAction.class, "CTL_MapCATAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(MapCATTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = MapCATTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
