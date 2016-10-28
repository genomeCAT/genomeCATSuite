package org.molgen.genomeCATPro.guimodul.track;

/**
 * @name MoveTrackAction
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package. Katrin Tebel
 * <tebel at molgen.mpg.de>. The contents of this file are subject to the terms
 * of either the GNU General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html or
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
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * 050612 kt update Notification
 */
@ActionID(id = "org.molgen.genomeCATPro.guimodul.track.MoveTrackAction", category = "DATA")
@ActionRegistration(displayName = "#CTL_MoveTrack", lazy = false)
public final class MoveTrackAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Track> lkpInfo;
    private Lookup context;

    public MoveTrackAction() {
        this(Utilities.actionsGlobalContext());

    }

    private MoveTrackAction(Lookup context) {
        super("move up");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ViewTrackDataAction.class, "CTL_MoveTrack");
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
        lkpInfo = context.lookupResult(Track.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (Track instance : lkpInfo.allInstances()) {
            Logger.getLogger(MoveTrackAction.class.getName()).log(
                    Level.INFO, "found lookup instance: "
                    + instance.getName());
            run(instance);
        }
    }

    public void run(Track s) {
        // todo new thread
        // nachfrage ok
        // view log panel

        try {

            /* 060612 kt
             if (s.getParentTrack() == null) { 
             JOptionPane.showMessageDialog(null, " track " + s.getName() + " is already at top level. ");
             return;
             }*/
            if (!TrackService.moveTrack(s, false, null)) {
                //if (!TrackService.moveTrack(s, true, null)) {
                JOptionPane.showMessageDialog(null, "track " + s.getName() + " not moveable");
                return;
                //}
            }

            ExperimentService.notifyListener(); //050612 kt

        } catch (Exception e) {
            Logger.getLogger(MoveTrackAction.class.getName()).log(
                    Level.INFO, "", e);
            JOptionPane.showMessageDialog(null, "Error: move track " + s.getName() + " see logfile!");

        }

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new MoveTrackAction(arg0);
    }
}
