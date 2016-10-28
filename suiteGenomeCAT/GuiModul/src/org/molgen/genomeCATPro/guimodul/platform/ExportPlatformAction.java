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
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.platform.ExportPlatformAction", category = "DATA")
@ActionRegistration(displayName = "#CTL_ExportPlatformAction", lazy = false)
@ActionReference(path = "Menu/Experiment/Platform", position = 400)
public final class ExportPlatformAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<PlatformData> lkpInfo;
    private Lookup context;

    public ExportPlatformAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ExportPlatformAction(Lookup context) {
        super("export as bed");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ExportPlatformAction.class, "CTL_ExportPlatformAction");
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
            Logger.getLogger(ExportPlatformAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
            return;
        }
        JOptionPane.showMessageDialog(null, "Export Plattform: "
                + "please select platform data to export in platform tree");
    }

    public void run(PlatformData s) {
        // todo new thread
        // nachfrage ok
        // view log panel

        ExportPlatformDialog.exportPlatformData(s);
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ExportPlatformAction(arg0);
    }
}
