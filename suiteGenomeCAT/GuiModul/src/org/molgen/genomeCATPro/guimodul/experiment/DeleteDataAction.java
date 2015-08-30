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
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class DeleteDataAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Data> lkpInfo;
    private Lookup context;

    public DeleteDataAction() {
        this(Utilities.actionsGlobalContext());

    }

    private DeleteDataAction(Lookup context) {
        super("delete");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(DeleteDataAction.class, "CTL_DeleteDataAction");
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
        lkpInfo = context.lookupResult(Data.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (Data instance : lkpInfo.allInstances()) {
            Logger.getLogger(DeleteDataAction.class.getName()).log(
                    Level.INFO, "found lookup instance: " +
                    instance.getName());
            run(instance);
        }
    }

    public void run(Data d) {
        if (JOptionPane.showConfirmDialog(null,
                "Really delete  " +
                d.toString() + " and all children?",
                "delete track data",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return;
        }
        try {
            if (d instanceof Track) {
                TrackService.deleteTrack((Track) d, false, null);
            }
            if (d instanceof ExperimentData) {
                ExperimentService.deleteExperimentData((ExperimentData) d, null);
            }
            ExperimentService.notifyListener();
        } catch (Throwable e) {
            Logger.getLogger(DeleteDataAction.class.getName()).log(
                    Level.INFO, "", e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());

        }

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new DeleteDataAction(arg0);
    }
}
