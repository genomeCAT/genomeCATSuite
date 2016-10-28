package org.molgen.genomeCATPro.guimodul.project;
/**
 * @name ProjectTreeAction
 *
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows ExperimentTree component.
 */
@ActionID(id = "org.molgen.genomeCATPro.guimodul.project.ProjectTreeAction", 
        category = "DATA")
@ActionRegistration(displayName = "#CTL_ProjectTreeAction", lazy = false)
@ActionReference(path = "Menu/View", position = 110)
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
