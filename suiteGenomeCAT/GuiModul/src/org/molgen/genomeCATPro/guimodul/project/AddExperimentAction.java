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
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.guimodul.XPort.ImportFileWizardAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.project.AddExperimentAction", category = "DATA")
@ActionRegistration(displayName = "#CTL_AddExperiment", lazy = false)
public final class AddExperimentAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Study> lkpInfo;
    private Lookup context;

    public AddExperimentAction() {
        this(Utilities.actionsGlobalContext());

    }

    private AddExperimentAction(Lookup context) {
        super("add Experiment to Project.");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(AddExperimentAction.class, "CTL_AddExperiment");
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
        lkpInfo = context.lookupResult(Study.class);
        lkpInfo.addLookupListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        init();
        for (Study instance : lkpInfo.allInstances()) {
            Logger.getLogger(AddExperimentAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
        }
    }

    public void run(Study s) {

        ExperimentDetail e = ImportFileWizardAction.doImport();
        if (e != null) {
            try {
                DataManager.addExperiment2Project(s.getName(), e);
                //JOptionPane.showMessageDialog(null, "ok - experiment added ");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "error  - see logfile for more information: ");
            }
        }
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    @Override
    public Action createContextAwareInstance(Lookup arg0) {
        return new AddExperimentAction(arg0);
    }
}
