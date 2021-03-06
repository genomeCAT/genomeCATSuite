package org.molgen.genomeCATPro.guimodul.data;
/**
 * @name ExportImageAction
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package. Katrin Tebel
 * <tebel at molgen.mpg.de>. The contents of this file are subject to the terms
 * of either the GNU General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
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

@ActionID(id = "org.molgen.genomeCATPro.guimodul.data.ExportImageAction", category = "DATA")
@ActionRegistration(displayName = "", lazy = false)
@ActionReference(path = "Toolbars/globalmenu1", position = 2)
public class ExportImageAction extends AbstractAction implements
        LookupListener, ContextAwareAction, Presenter.Toolbar {

    private Lookup context;
    Lookup.Result<AppInterface> lkpInfo;

    public ExportImageAction() {
        this(Utilities.actionsGlobalContext());

    }

    // open with certain lookup context
    private ExportImageAction(Lookup context) {
        super();
        this.context = context;
    }

    @Override
    public Component getToolbarPresenter() {
        final JButton startButton = new JButton();
        //startButton.setText("<html>image</html>");

        //startButton.setMaximumSize(startButton.getPreferredSize());
        startButton.setAction(this);
        startButton.setIcon(new ImageIcon(Utilities.loadImage(
                "org/molgen/genomeCATPro/guimodul/data/camera_24.png", true)));
        startButton.setText("");
        startButton.setToolTipText("export image");
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
            instance.exportImage();
        }
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        return new ExportImageAction(context);
    }
}
