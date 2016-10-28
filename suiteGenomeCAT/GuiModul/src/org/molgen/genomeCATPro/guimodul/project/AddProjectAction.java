/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.project;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.project.AddProjectAction", category = "Experiment")
@ActionRegistration(displayName = "#CTL_AddProject", lazy = false)
@ActionReferences(value = {
    @ActionReference(path = "Menu/Experiment", position = 1),
    @ActionReference(path = "Toolbars/Experiment", position = -100)})
public final class AddProjectAction extends CallableSystemAction {

    public void performAction() {
        ProjectView p = new ProjectView(null, true);
        p.setLocationRelativeTo(null);
        p.setVisible(true);
    }

    public String getName() {
        return NbBundle.getMessage(AddProjectAction.class, "CTL_AddProject");
    }

    @Override
    protected String iconResource() {
        return "org/molgen/genomeCATPro/guimodul/project/book_add_16.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
