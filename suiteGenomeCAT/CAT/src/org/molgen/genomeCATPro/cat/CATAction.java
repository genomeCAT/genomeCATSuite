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
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows CAT component.
 */
@ActionID(id = "org.molgen.genomeCATPro.cat.CATAction", category = "CAT")
@ActionRegistration(displayName = "ComparativeView", lazy = false)
@ActionReferences(value = {
    @ActionReference(path = "Toolbars/Modules", position = 40),
    @ActionReference(path = "Menu/Modules/ComparativeView", position = 1)})
public class CATAction extends AbstractAction {

    public CATAction() {
        super(NbBundle.getMessage(CATAction.class, "CTL_CATAction"));
        setIcon();
    }

    void setIcon() {
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(CATTopComponent.ICON_PATH, true)));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = CATTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
