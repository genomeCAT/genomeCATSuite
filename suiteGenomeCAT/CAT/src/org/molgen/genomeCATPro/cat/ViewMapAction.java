package org.molgen.genomeCATPro.cat;

/**
 * @name ViewExperimentDataAction
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class ViewMapAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Object> lkpInfo;
    private Lookup context;

    public ViewMapAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ViewMapAction(Lookup context) {
        super(NbBundle.getMessage(ViewMapAction.class, "CTL_ViewDataAction"));

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ViewMapAction.class, "CTL_ViewDataAction");
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
        lkpInfo = context.lookupResult(Object.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (Object instance : lkpInfo.allInstances()) {

            run(instance);
        }
    }

    public void run(Object s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        String table = "";
        String name = "";
        if (s instanceof MapDetail) {

            ViewMapDetail.view((MapDetail) s);
        }
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ViewMapAction(arg0);
    }
}
