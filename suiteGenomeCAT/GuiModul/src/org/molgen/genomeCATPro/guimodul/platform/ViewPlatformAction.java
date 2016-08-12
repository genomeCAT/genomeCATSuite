/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.platform;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class ViewPlatformAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<PlatformDetail> lkpInfo;
    private Lookup context;

    public ViewPlatformAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ViewPlatformAction(Lookup context) {
        super("details...");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ViewPlatformAction.class, "CTL_ViewPlatformAction");
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
        lkpInfo = context.lookupResult(PlatformDetail.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (PlatformDetail instance : lkpInfo.allInstances()) {
            Logger.getLogger(ViewPlatformAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
        }
    }

    public void run(PlatformDetail s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        PlatformDetailView.view(s, false);
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ViewPlatformAction(arg0);
    }
}
