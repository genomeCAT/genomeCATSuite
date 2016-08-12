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
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class ViewPlatformDataAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<PlatformData> lkpInfo;
    private Lookup context;

    public ViewPlatformDataAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ViewPlatformDataAction(Lookup context) {
        super("details...");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ViewPlatformDataAction.class, "CTL_ViewPlatformDATAAction");
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
        lkpInfo = context.lookupResult(PlatformData.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (PlatformData instance : lkpInfo.allInstances()) {
            Logger.getLogger(ViewPlatformDataAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
        }
    }

    public void run(PlatformData s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        PlatformDataView.view(s, false);
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ViewPlatformDataAction(arg0);
    }
}
