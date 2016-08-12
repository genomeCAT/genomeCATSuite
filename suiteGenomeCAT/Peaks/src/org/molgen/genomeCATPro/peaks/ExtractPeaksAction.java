package org.molgen.genomeCATPro.peaks;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * @name ExtractPeaksAction
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
public final class ExtractPeaksAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Data> lkpInfo;
    private Lookup context;

    public ExtractPeaksAction() {
        this(Utilities.actionsGlobalContext());
        setIcon();
    }

    private ExtractPeaksAction(Lookup context) {
        super(NbBundle.getMessage(ExtractPeaksAction.class, "CTL_ExtractPeaksAction"));

        this.context = context;
        setIcon();
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
            Logger.getLogger(ExtractPeaksAction.class.getName()).log(
                    Level.INFO, "found lookup instance:"
                    + instance.getName());
            run(instance);
            return;

        }
        JOptionPane.showMessageDialog(null, this.getName() + ": "
                + "please select data in project tree");
        //run(null);
    }

    public void run(Data d) {

        ExtractPeakDialog.getExtractPeakDialog(d);

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public javax.swing.Action createContextAwareInstance(Lookup arg0) {
        return new ExtractPeaksAction(arg0);
    }

    public String getName() {
        return NbBundle.getMessage(ExtractPeaksAction.class, "CTL_ExtractPeaksAction");
    }

    private void setIcon() {
        java.net.URL imgURL = getClass().getResource(
                "findpeaks_16.png");
        ImageIcon newIcon = new ImageIcon(imgURL);
        putValue(Action.SMALL_ICON, newIcon);
    }
}
