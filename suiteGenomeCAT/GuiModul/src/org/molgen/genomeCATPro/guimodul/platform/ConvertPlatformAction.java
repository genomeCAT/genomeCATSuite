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
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class ConvertPlatformAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<PlatformData> lkpInfo;
    private Lookup context;

    public ConvertPlatformAction() {
        this(Utilities.actionsGlobalContext());

    }

    private ConvertPlatformAction(Lookup context) {
        super("convert genome release");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(ConvertPlatformAction.class, "CTL_ConvertPlatformAction");
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
            Logger.getLogger(ConvertPlatformAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
            return;
        }
        JOptionPane.showMessageDialog(null, "Convert Plattform: "
                + "please select platform data to convert in platform tree");
    }

    public void run(PlatformData s) {
        // todo new thread
        // nachfrage ok
        // view log panel

        ConvertPlatformDialog.convertPlatformData(s);
    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new ConvertPlatformAction(arg0);
    }
}
