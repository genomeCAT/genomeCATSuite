/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.cghpro;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows CGHPROFrameProps component.
 */
@ActionID(id = "org.molgen.genomeCATPro.guimodul.cghpro.CGHPROFramePropsAction",
        category = "Window")
@ActionRegistration(displayName = "Properties", lazy = false)
@ActionReference(path = "Menu/Modules/SingleView",
        name = "CGHPROFramePropsAction", position = 200)
public class CGHPROFramePropsAction extends AbstractAction {

    public CGHPROFramePropsAction() {
        super(NbBundle.getMessage(CGHPROFramePropsAction.class, "CTL_CGHPROFramePropsAction"));
        java.net.URL imgURL = getClass().getResource("tools_16.png");
        ImageIcon newIcon = new ImageIcon(imgURL);
        putValue(SMALL_ICON, newIcon);

    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        TopComponent win = CGHPROFramePropsTopComponent.findInstance();
        win.open();
        win.requestActive();

    }
}
