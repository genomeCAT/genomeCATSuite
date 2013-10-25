/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.project;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows ExperimentTree component.
 */
public class ProjectTreeAction extends AbstractAction {

    public ProjectTreeAction() {
        super(NbBundle.getMessage(ProjectTreeAction.class, "CTL_ProjectTreeAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(ProjectTreeTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = ProjectTreeTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
