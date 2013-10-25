/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.cghpro;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows CGHPROFrameProps component.
 */
public class CGHPROFramePropsAction extends AbstractAction {

    public CGHPROFramePropsAction() {
        super(NbBundle.getMessage(CGHPROFramePropsAction.class, "CTL_CGHPROFramePropsAction"));
        java.net.URL imgURL = getClass().getResource("tools_16.png");
        ImageIcon newIcon = new ImageIcon(imgURL);
        putValue(SMALL_ICON, newIcon);

    }

    public void actionPerformed(ActionEvent evt) {

        TopComponent win = CGHPROFramePropsTopComponent.findInstance();
        win.open();
        win.requestActive();


    }
}
