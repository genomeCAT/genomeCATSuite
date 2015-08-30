/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.selector;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class SelectorAction  extends AbstractAction implements LookupListener, ContextAwareAction {

    Lookup.Result<Data> lkpInfo;
    private Lookup context;

    public SelectorAction() {
        this(Utilities.actionsGlobalContext());
    //setIcon();
    }

    private SelectorAction(Lookup context) {
        super(NbBundle.getMessage(SelectorAction.class, "CTL_SelectorAction"));

        this.context = context;

    }

    public String getName() {
        return NbBundle.getMessage(SelectorAction.class, "CTL_SelectorAction");
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
            Logger.getLogger(SelectorAction.class.getName()).log(
                    Level.INFO, "found lookup instance:",
                    instance.getName());
            run(instance);
            return;
        }
        JOptionPane.showMessageDialog(null, this.getName() + ": " +
                "please select data in project tree");
    }

    public void run(Data d) {
        // todo new thread
        // nachfrage ok
        // view log panel

        SelectorDialog s = new SelectorDialog(new javax.swing.JFrame(), true, d);
        s.setVisible(true);


    }

    public void resultChanged(LookupEvent arg0) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new SelectorAction(arg0);
    }

    
}

