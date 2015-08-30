/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.experiment;

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

public final class ViewExperimentAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<ExperimentDetail> lkpInfo;
    private Lookup context;

    public ViewExperimentAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ViewExperimentAction(Lookup context) {
        super("details...");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ViewExperimentAction.class, "CTL_ViewExperimentAction");
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
            Logger.getLogger(ViewExperimentAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
        }
    }

    public void run(ExperimentDetail s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        ExperimentDetailView.view(s, false);
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ViewExperimentAction(arg0);
    }
}

