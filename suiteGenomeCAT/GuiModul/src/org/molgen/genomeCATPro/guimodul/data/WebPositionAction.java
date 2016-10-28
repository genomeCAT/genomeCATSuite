/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.data;

import java.awt.Component;
import java.awt.event.ActionEvent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.data.WebPositionAction", category = "DATA")
@ActionRegistration(displayName = "#CTL_GlobalPositionAction", lazy = false)
@ActionReference(path = "Toolbars/globalpos")
public final class WebPositionAction extends CallableSystemAction implements Presenter.Toolbar {

    @Override
    public Component getToolbarPresenter() {
        return WebPositionPanel.getWebPositionPanel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // not needed, because the GooglePanel handles the action
    }

    public String getName() {
        return NbBundle.getMessage(WebPositionAction.class, "CTL_GlobalPositionAction");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public void performAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
