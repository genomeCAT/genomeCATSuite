/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.platform;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.platform.CreatePlatformDetailAction", category = "DATA")
@ActionRegistration(displayName = "#CTL_CreatePlatformDetailAction", lazy = false)
@ActionReferences(value = {
    @ActionReference(path = "Toolbars/Experiment", position = -300),
    @ActionReference(path = "Menu/Experiment/Platform", position = 100)})
public final class CreatePlatformDetailAction extends CallableSystemAction {

    public void performAction() {
        CreatePlatformDialog dialog = new CreatePlatformDialog(new javax.swing.JFrame());
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public String getName() {
        return NbBundle.getMessage(CreatePlatformDetailAction.class, "CTL_CreatePlatformDetailAction");
    }

    @Override
    protected String iconResource() {
        return "org/molgen/genomeCATPro/guimodul/platform/new_array_16.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
