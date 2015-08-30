package org.molgen.genomeCATPro.guimodul.tree;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.guimodul.experiment.DeleteDetailAction;
import org.molgen.genomeCATPro.guimodul.experiment.ExperimentDataNodeFactory;
import org.molgen.genomeCATPro.guimodul.project.ProjectExperimentAction;
import org.molgen.genomeCATPro.guimodul.experiment.ViewExperimentAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
/**
 * @name ExperimentDetailNode
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
/**
 * 141014   kt    no menue item Properties
 * 
 */
public class ExperimentDetailNode extends BeanNode<ExperimentDetail> {

    public ExperimentDetailNode(ExperimentDetail bean) throws IntrospectionException {
        super(bean, Children.create(new ExperimentDataNodeFactory(bean), true), Lookups.singleton(bean));

        setDisplayName(bean.getName());
        setShortDescription(bean.getDescription());
        //sample.addPropertyChangeListener(this);
        setIconBaseWithExtension(bean.getIconPath());
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        //actions.addAll(Arrays.asList(super.getActions(arg0)));
        actions.add(new ViewExperimentAction());
        actions.add(new ProjectExperimentAction());
        actions.add(new DeleteDetailAction());
        return actions.toArray(
                new Action[]{});

    }
    @Override
    public Action getPreferredAction(){
        return new ViewExperimentAction();
    }
}

