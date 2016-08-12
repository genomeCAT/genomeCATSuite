/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows MapCAT component.
 */
@ActionID(id = "org.molgen.genomeCATPro.cat.MapCATAction", category = "CAT")
@ActionRegistration(displayName = "#open in MapView", lazy = false)
@ActionReference(path = "Menu/Modules/ComparativeView", position = 2)
public class MapCATAction extends AbstractAction {

    public MapCATAction() {
        super(NbBundle.getMessage(MapCATAction.class, "CTL_MapCATAction"));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(MapCATTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = MapCATTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
