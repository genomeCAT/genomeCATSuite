/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.molgen.genomeCATPro.datadb.service.MapService;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class MapCATDetailAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private Lookup context;
    Lookup.Result<MapDetail> lkpInfo;

    public MapCATDetailAction() {
        this(Utilities.actionsGlobalContext());
    }

    // open with certain lookup context
    private MapCATDetailAction(Lookup context) {
        super(NbBundle.getMessage(CATAction.class, "CTL_MapCATDataAction"));
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
            MapCATDetailAction.openInCAT(instance);
        }
        handle.finish();
    }

    private static void openInCAT(MapDetail s) {
        MapCATTopComponent win = MapCATTopComponent.findInstance(
                s.getMapName(), Defaults.GenomeRelease.toRelease(s.getGenomeRelease()));
        try {
            win.getArray().addData(MapService.getMapDataList(s));
        } catch (Exception ex) {
            Logger.getLogger(MapCATDetailAction.class.getName()).log(Level.SEVERE,
                    "openInCAT", ex);
        }

        win.open();
        win.requestActive();
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new MapCATDetailAction(context);
    }

    public static void showData(List<MapData> list) {
        if (list.size() <= 0) {
            return;
        }
        MapCATTopComponent win = MapCATTopComponent.findInstance(list.get(0).getMapName(),
                Defaults.GenomeRelease.toRelease(list.get(0).getGenomeRelease()));
        win.array.addData(list);
        win.open();
        win.requestActive();
    }
}