/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.project;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.molgen.genomeCATPro.datadb.dbentities.Study;

import org.molgen.genomeCATPro.guimodul.track.ImportTrackDialog;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.project.AddTrackAction", category = "Experiment")
@ActionRegistration(displayName = "#CTL_AddTrackAction", lazy = false)
@ActionReferences(value = {
    @ActionReference(path = "Menu/Experiment", position = 3),
    @ActionReference(path = "Toolbars/Experiment", position = -400)})
public final class AddTrackAction extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Study> lkpInfo;
    private Lookup context;

    public AddTrackAction() {
        this(Utilities.actionsGlobalContext());
        setIcon();
    }

    private AddTrackAction(Lookup context) {
        super(NbBundle.getMessage(AddTrackAction.class, "CTL_AddTrackAction"));

        this.context = context;
        setIcon();
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
        lkpInfo = context.lookupResult(Study.class);
        lkpInfo.addLookupListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (Study instance : lkpInfo.allInstances()) {
            Logger.getLogger(AddExperimentAction.class.getName()).log(
                    Level.INFO, "found lookup instance:"
                    + instance.getName());
            run(instance);
            return;
        }
        run(null);
    }

    public void run(Study s) {

        ImportTrackDialog dialog = null;
        if (s == null) {

            dialog = new ImportTrackDialog(null);
        } else {
            dialog = new ImportTrackDialog(null, s);
        }
        dialog.setVisible(true);

    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new AddTrackAction(arg0);
    }

    public String getName() {
        return NbBundle.getMessage(AddTrackAction.class, "CTL_AddTrackAction");
    }

    private void setIcon() {
        java.net.URL imgURL = getClass().getResource(
                "page_blank_add_16.png");
        ImageIcon newIcon = new ImageIcon(imgURL);
        putValue(Action.SMALL_ICON, newIcon);
    }
}
