/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.data;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(id = "org.molgen.genomeCATPro.guimodul.data.LoadAction", category = "DATA")
@ActionRegistration(displayName = "", lazy = false)
@ActionReference(path = "Toolbars/globalmenu1", position = 1)
public class LoadAction extends AbstractAction implements
        LookupListener, ContextAwareAction, Presenter.Toolbar {

    private Lookup context;
    Lookup.Result<AppInterface> lkpInfo;

    public LoadAction() {
        this(Utilities.actionsGlobalContext());

    }

    // open with certain lookup context
    private LoadAction(Lookup context) {
        super();
        this.context = context;
    }

    @Override
    public Component getToolbarPresenter() {
        final JButton startButton = new JButton();
        //startButton.setText("<html>load</html>");
        //startButton.setMaximumSize(startButton.getPreferredSize());
        startButton.setAction(this);
        startButton.setIcon(new ImageIcon(Utilities.loadImage(
                "org/molgen/genomeCATPro/guimodul/data/database_search_24.png", true)));
        startButton.setText("");
        startButton.setToolTipText("search in database");
        return startButton;
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
        for (AppInterface instance : lkpInfo.allInstances()) {
            instance.load();
        }
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new LoadAction(context);
    }
}
