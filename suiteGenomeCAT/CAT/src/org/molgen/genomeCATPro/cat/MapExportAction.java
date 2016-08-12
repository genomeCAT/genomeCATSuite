/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JDialog;
import org.molgen.genomeCATPro.cat.maparr.ExportMapDialog;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

public class MapExportAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private Lookup context;
    Lookup.Result<MapDetail> lkpInfo;

    public MapExportAction() {
        this(Utilities.actionsGlobalContext());
    }

    // open with certain lookup context
    private MapExportAction(Lookup context) {
        super("export...");
        this.context = context;
    }

    void init() {

        if (lkpInfo != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(MapDetail.class);
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

        for (MapDetail instance : lkpInfo.allInstances()) {
            MapExportAction.export(instance);
        }
        handle.finish();
    }

    private static void export(MapDetail s) {
        JDialog d = new ExportMapDialog(s);
        d.setVisible(true);

    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new MapExportAction(context);
    }

}
