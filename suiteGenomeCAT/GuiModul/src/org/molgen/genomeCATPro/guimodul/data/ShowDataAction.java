/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.data;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JCheckBox;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

public class ShowDataAction extends AbstractAction implements
        LookupListener, ContextAwareAction, Presenter.Toolbar {

    private Lookup context;
    Lookup.Result<AppInterface> lkpInfo;
    static JCheckBox cb = null;

    public static boolean getState() {
        if (cb == null) {
            return false;
        }
        return cb.isSelected();
    }

    public ShowDataAction() {
        this(Utilities.actionsGlobalContext());

    }

    // open with certain lookup context
    private ShowDataAction(Lookup context) {
        super("show data");
        this.context = context;
    }

    @Override
    public Component getToolbarPresenter() {

        cb = new javax.swing.JCheckBox();

        cb.setText("<html>show<br/>data</html>");

        //cb.setMaximumSize(cb.getPreferredSize());
        cb.setAction(this);

        return cb;
    }

    void init() {

        if (lkpInfo != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(AppInterface.class);
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
        GlobalPositionDataPanel.setDetail(null);
        for (AppInterface instance : lkpInfo.allInstances()) {
            instance.showData(cb.isSelected());
        }

    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new ShowDataAction(context);
    }
}
