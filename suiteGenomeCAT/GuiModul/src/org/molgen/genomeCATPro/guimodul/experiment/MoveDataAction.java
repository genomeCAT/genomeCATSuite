package org.molgen.genomeCATPro.guimodul.experiment;

/**
 * @name MoveDataAction
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class MoveDataAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<ExperimentData> lkpInfo;
    private Lookup context;

    public MoveDataAction() {
        this(Utilities.actionsGlobalContext());

    }

    private MoveDataAction(Lookup context) {
        super(NbBundle.getMessage(MoveDataAction.class, "CTL_MoveDataAction"));

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(MoveDataAction.class, "CTL_MoveDataAction");
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
            Logger.getLogger(MoveDataAction.class.getName()).log(
                    Level.INFO, "found lookup instance: "
                    + instance.getName());
            run(instance);
        }
    }

    public void run(ExperimentData d) {
        try {

            if (!ExperimentService.moveExperimentData(d, null)) {
                //if (!TrackService.moveTrack(s, true, null)) {
                JOptionPane.showMessageDialog(null, "data " + d.getName() + " not moveable");
                return;
                //}
            }
            ExperimentService.notifyListener(); //050612 kt
        } catch (Exception e) {
            Logger.getLogger(MoveDataAction.class.getName()).log(
                    Level.INFO, "", e);
            JOptionPane.showMessageDialog(null, "Error: move data " + d.getName() + " see logfile!");

        }
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new MoveDataAction(arg0);
    }
}
