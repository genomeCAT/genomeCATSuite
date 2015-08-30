package org.molgen.genomeCATPro.guimodul.experiment;

/**
 * @name DeleteDetailAction
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package.
 * Katrin Tebel <tebel at molgen.mpg.de>.
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import org.molgen.genomeCATPro.guimodul.track.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * 
 * 
 */
public final class DeleteDetailAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<ExperimentDetail> lkpInfo;
    private Lookup context;

    public DeleteDetailAction() {
        this(Utilities.actionsGlobalContext());

    }

    private DeleteDetailAction(Lookup context) {
        super("delete");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ViewTrackDataAction.class, "CTL_DeleteDetailAction");
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
            Logger.getLogger(DeleteDetailAction.class.getName()).log(
                    Level.INFO, "found lookup instance: " +
                    instance.getName());
            run(instance);
        }
    }

    public void run(ExperimentDetail d) {
        if (JOptionPane.showConfirmDialog(null,
                "Really delete  " +
                d.toString() + " and all children?",
                "delete track data",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
            return;
        }
        try {

            ExperimentService.deleteExperimentDetail(d, null);

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
        return new DeleteDetailAction(arg0);
    }
}
