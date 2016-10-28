package org.molgen.genomeCATPro.guimodul.cghpro;

/**
 * @name CBSAction
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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.cghpro.CBSAction", category = "DATA")
@ActionRegistration(displayName = "#CTL_CBSAction", lazy = false)
@ActionReferences(value = {
    @ActionReference(path = "Menu/Calculate", position = 10),
    @ActionReference(path = "Toolbars/Experiment", position = 10)})
public final class CBSAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Data> lkpInfo;
    private Lookup context;

    public CBSAction() {
        this(Utilities.actionsGlobalContext());
        setIcon();
    }

    private CBSAction(Lookup context) {
        super(NbBundle.getMessage(CBSAction.class, "CTL_CBSAction"));

        this.context = context;
        setIcon();
    }

    void setIcon() {
        java.net.URL imgURL = getClass().getResource("cbs.gif");
        ImageIcon newIcon = new ImageIcon(imgURL);
        putValue(Action.SMALL_ICON, newIcon);
    }

    public String getName() {
        return NbBundle.getMessage(CBSAction.class, "CTL_CBSAction");
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
            Logger.getLogger(CBSAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
            return;
        }
        JOptionPane.showMessageDialog(null, this.getName() + ": "
                + "please select data in project tree");
    }

    public void run(Data s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        if (!s.allowSegmentation()) {
            JOptionPane.showMessageDialog(null,
                    "data not suitable for CBS", "Run CBS",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        CBSRunDialog cbs = new CBSRunDialog(null, false, s);
        cbs.setVisible(true);

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new CBSAction(arg0);
    }
}
