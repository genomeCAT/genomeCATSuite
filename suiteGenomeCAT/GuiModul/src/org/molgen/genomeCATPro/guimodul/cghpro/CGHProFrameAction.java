/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.cghpro;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.ImageIcon;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

public class CGHProFrameAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private Lookup context;
    Lookup.Result<Data> lkpInfo;

    public CGHProFrameAction() {
        this(Utilities.actionsGlobalContext());
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(CGHProTopComponent.ICON_PATH, true)));
    }

    // open with certain lookup context
    private CGHProFrameAction(Lookup context) {
        super("open in SingleView");
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
            CGHProFrameAction.openInCGHPRO(instance);
        }
        handle.finish();

    }

    private static void openInCGHPRO(Data s) {
        CGHProTopComponent win = CGHProTopComponent.findInstance(s);
        win.open();
        win.requestActive();
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new CGHProFrameAction(context);
    }
}