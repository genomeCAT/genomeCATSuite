package org.molgen.genomeCATPro.cat;

/**
 * @name MapCATDataAction
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
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class MapCATDataAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private Lookup context;
    Lookup.Result<MapData> lkpInfo;

    public MapCATDataAction() {
        this(Utilities.actionsGlobalContext());
    }

    // open with certain lookup context
    private MapCATDataAction(Lookup context) {
        super(NbBundle.getMessage(CATAction.class, "CTL_MapCATDataAction"));
        this.context = context;
    }

    void init() {

        if (lkpInfo != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(MapData.class);
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

        for (MapData instance : lkpInfo.allInstances()) {
            MapCATDataAction.openInCAT(instance);
        }
        handle.finish();
    }

    private static void openInCAT(MapData s) {
        MapCATTopComponent win = MapCATTopComponent.findInstance(s,
                Defaults.GenomeRelease.toRelease(s.getGenomeRelease()));
        win.open();
        win.requestActive();
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new MapCATDataAction(context);
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
