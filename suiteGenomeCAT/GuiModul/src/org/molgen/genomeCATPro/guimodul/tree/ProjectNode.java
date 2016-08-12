package org.molgen.genomeCATPro.guimodul.tree;

/**
 * @name ProjectNode
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
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
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;

import org.molgen.genomeCATPro.datadb.dbentities.Study;

import org.molgen.genomeCATPro.guimodul.experiment.ExperimentDetailNodeFactory;
import org.molgen.genomeCATPro.guimodul.project.AddExperimentAction;
import org.molgen.genomeCATPro.guimodul.project.AddTrackAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * 230413 kt addAction 141014 kt no menue item Properties
 */
public class ProjectNode extends BeanNode<Study> {

    public ProjectNode(Study bean) throws IntrospectionException {
        super(bean, Children.create(new ExperimentDetailNodeFactory(bean), true), Lookups.singleton(bean));

        setDisplayName(bean.getName());
        setShortDescription(bean.getDescription());
        //sample.addPropertyChangeListener(this);
        setIconBaseWithExtension(bean.getIconPath());
    }
    static List<Action> actionsData = new ArrayList<Action>(
            Arrays.asList(
                    new AddExperimentAction(),
                    new AddTrackAction()));

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        //actions.addAll(Arrays.asList(super.getActions(arg0)));
        actions.addAll(actionsData);
        return actions.toArray(
                new Action[]{});

    }

    @Override
    public Action getPreferredAction() {
        return new AddExperimentAction();
    }

    static public void addAction(Action a) {
        actionsData.add(a);
        Logger.getLogger(ProjectNode.class.getName()).log(
                Level.INFO, "action added: " + a.toString());

    }
}
