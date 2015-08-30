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
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class ConvertExperimentAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<ExperimentData> lkpInfo;
    private Lookup context;

    public ConvertExperimentAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ConvertExperimentAction(Lookup context) {
        super("convert...");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ConvertExperimentAction.class, "CTL_ConvertExperimentAction");
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
        lkpInfo = context.lookupResult(ExperimentData.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (ExperimentData instance : lkpInfo.allInstances()) {
            Logger.getLogger(ConvertExperimentAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
        }
    }

    public void run(ExperimentData s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        ConvertExperimentDialog.convertExperiment(s);
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ConvertExperimentAction(arg0);
    }
}

