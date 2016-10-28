/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.experiment;

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
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.experiment.ExportData", category = "DATA")
@ActionRegistration(displayName = "#CTL_ExportData", lazy = false)
@ActionReference(path = "Menu/Experiment", position = 6)
public final class ExportData extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Data> lkpInfo;
    private Lookup context;

    public ExportData() {
        this(Utilities.actionsGlobalContext());
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(
                "org/molgen/genomeCATPro/guimodul/save_download_16.png", true)));

        //setIcon();
    }

    private ExportData(Lookup context) {
        super(NbBundle.getMessage(ExportData.class, "CTL_ExportData"));

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ExportData.class, "CTL_ExportData");
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
            Logger.getLogger(ExportData.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
            return;
        }
        JOptionPane.showMessageDialog(null, "Export Data: "
                + "please select data in project tree");
    }

    public void run(Data s) {
        // todo new thread
        // nachfrage ok
        // view log panel

        ExportDataDialog.exportData(s);

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ExportData(arg0);
    }
}
