package org.molgen.genomeCATPro.guimodul.track;
/**
 * @name ViewTrackDataAction
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 *
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
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class ViewTrackDataAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Track> lkpInfo;
    private Lookup context;

    public ViewTrackDataAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ViewTrackDataAction(Lookup context) {
        super("detail...");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ViewTrackDataAction.class, "CTL_ViewExperimentDataAction");
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
            Logger.getLogger(ViewTrackDataAction.class.getName()).log(
                    Level.INFO, "found lookup instance: "+
                    instance.getName());
            run(instance);
        }
    }

    public void run(Track s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        TrackDataView.view(s, false);
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ViewTrackDataAction(arg0);
    }
}

