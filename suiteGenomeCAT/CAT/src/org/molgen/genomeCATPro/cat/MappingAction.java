/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.cat.maparr.ArrayFrame;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

public class MappingAction extends AbstractAction implements
        LookupListener, ContextAwareAction, Presenter.Toolbar {

    private Lookup context;
    Lookup.Result<ArrayFrame> lkpInfo;

    public MappingAction() {
        this(Utilities.actionsGlobalContext());

    }

    // open with certain lookup context
    private MappingAction(Lookup context) {
        super(NbBundle.getMessage(CATAction.class, "CTL_MappingAction"));
        this.context = context;
    }

    @Override
    public Component getToolbarPresenter() {
        final JButton startButton = new JButton();
        //startButton.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        //startButton.setBorderPainted(true);
        startButton.setText("<html><b>MAP</b></html>");
        startButton.setMaximumSize(startButton.getPreferredSize());
        startButton.setAction(this);

        return startButton;
    }

    void init() {

        if (lkpInfo != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(ArrayFrame.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    public void actionPerformed(ActionEvent e) {
        init();
        for (ArrayFrame instance : lkpInfo.allInstances()) {
            instance.mapArrays();
            return;
        }
        JOptionPane.showMessageDialog(null, this.getName() + ": " +
                " open data in " +
                NbBundle.getMessage(MappingAction.class, "CTL_CATTopComponent") + " !");
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new MappingAction(context);
    }

    private String getName() {
        return NbBundle.getMessage(CATAction.class, "CTL_MappingAction");
    }
}