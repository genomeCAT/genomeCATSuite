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
import org.molgen.genomeCATPro.cghpro.xport.PlatformManager;
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

@ActionID(id = "org.molgen.genomeCATPro.guimodul.platform.DeletePlatformAction", category = "DATA")
@ActionRegistration(displayName = "delete release", lazy = false)
@ActionReference(path = "Menu/Experiment/Platform", position = 300)
public final class DeletePlatformAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<PlatformData> lkpInfo;
    private Lookup context;

    public DeletePlatformAction() {
        this(Utilities.actionsGlobalContext());

    }

    private DeletePlatformAction(Lookup context) {
        super("delete release");

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(DeletePlatformAction.class, "CTL_DeletePlatformAction");
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
            Logger.getLogger(DeletePlatformAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
            return;
        }
        JOptionPane.showMessageDialog(null, "Delete Plattform: "
                + "please select platform data to delete in platform tree");
    }

    public void run(PlatformData s) {
        // todo new thread
        // nachfrage ok
        // view log panel
        int nn = JOptionPane.showConfirmDialog(null,
                "Really delete this platform? "
                + s.toFullString(),
                "delete platform data",
                JOptionPane.YES_NO_OPTION);

        if (nn == JOptionPane.YES_OPTION) {
            try {
                PlatformManager.doDelete(s);
                JOptionPane.showMessageDialog(null, "succesfully deleted");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new DeletePlatformAction(arg0);
    }
}
