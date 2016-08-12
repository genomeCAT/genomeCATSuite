/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.project;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class ProjectExperimentAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<ExperimentDetail> lkpInfo;
    private Lookup context;

    public ProjectExperimentAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ProjectExperimentAction(Lookup context) {
        super("add to project");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ProjectExperimentAction.class, "CTL_ProjectExperimentAction");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    void init() {

        if (lkpInfo != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(ExperimentDetail.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (ExperimentDetail instance : lkpInfo.allInstances()) {
            Logger.getLogger(ProjectExperimentAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
        }
    }

    public void run(ExperimentDetail s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        Project2ExperimentDialog.addExperiment2Project(s);

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ProjectExperimentAction(arg0);
    }
}