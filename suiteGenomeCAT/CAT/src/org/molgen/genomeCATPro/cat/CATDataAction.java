/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

@ActionID(id = "org.molgen.genomeCATPro.cat.CATDataAction", category = "CAT")
@ActionRegistration(displayName = "add ComparativeView", lazy = false)
public class CATDataAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private Lookup context;
    Lookup.Result<Data> lkpInfo;

    public CATDataAction() {
        this(Utilities.actionsGlobalContext());
    }

    // open with certain lookup context
    private CATDataAction(Lookup context) {
        super(NbBundle.getMessage(CATAction.class, "CTL_CATDataAction"));
        this.context = context;
    }

    void init() {

        if (lkpInfo != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(Data.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    public void actionPerformed(ActionEvent e) {

        ProgressHandle handle = ProgressHandleFactory.createHandle("Get data...");
        handle.start(100);
        init();
        handle.progress(30);

        for (Data instance : lkpInfo.allInstances()) {
            CATDataAction.openInCAT(instance);
        }
        handle.finish();
    }

    private static void openInCAT(Data s) {
        CATTopComponent win = CATTopComponent.findInstance(s);
        win.open();
        win.requestActive();
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new CATDataAction(context);
    }
}
