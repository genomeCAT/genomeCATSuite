/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.ngs;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.molgen.genomeCATPro.datadb.dbentities.Study;

import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class AddNGSDataAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Study> lkpInfo;
    private Lookup context;

    public AddNGSDataAction() {
        this(Utilities.actionsGlobalContext());
        setIcon();
    }

    private AddNGSDataAction(Lookup context) {
        super(NbBundle.getMessage(AddNGSDataAction.class, "CTL_ImportNGS2"));

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
        lkpInfo = context.lookupResult(Study.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (Study instance : lkpInfo.allInstances()) {
            Logger.getLogger(AddNGSDataAction.class.getName()).log(
                    Level.INFO, "found lookup instance:"
                    + instance.getName());
            run(instance);
            return;
        }
        run(null);
    }

    public void run(Study s) {

        ImportNGSDialog dialog = null;
        if (s == null) {

            dialog = new ImportNGSDialog(null);
        } else {
            dialog = new ImportNGSDialog(null);
            //dialog = new ImportTrackDialog(null, s);
        }
        dialog.setVisible(true);

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new AddNGSDataAction(arg0);
    }

    public String getName() {
        return NbBundle.getMessage(AddNGSDataAction.class, "CTL_ImportNGS2");
    }

    private void setIcon() {
        java.net.URL imgURL = getClass().getResource(
                "ngs16.png");
        ImageIcon newIcon = new ImageIcon(imgURL);
        putValue(Action.SMALL_ICON, newIcon);
    }
}
