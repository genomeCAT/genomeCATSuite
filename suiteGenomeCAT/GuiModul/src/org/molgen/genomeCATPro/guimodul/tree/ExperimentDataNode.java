package org.molgen.genomeCATPro.guimodul.tree;

/**
 * @name ExperimentDataNode
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
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.Data;


import org.molgen.genomeCATPro.guimodul.anno.AnnotateAction;
import org.molgen.genomeCATPro.guimodul.cghpro.CBSAction;
import org.molgen.genomeCATPro.guimodul.cghpro.CGHProFrameAction;
import org.molgen.genomeCATPro.guimodul.cghpro.HMMAction;
import org.molgen.genomeCATPro.guimodul.cghpro.RINGOAction;
import org.molgen.genomeCATPro.guimodul.data.ViewDataAction;
import org.molgen.genomeCATPro.guimodul.experiment.ConvertExperimentAction;
import org.molgen.genomeCATPro.guimodul.experiment.DeleteDataAction;
import org.molgen.genomeCATPro.guimodul.experiment.ExperimentDataNodeFactory;
import org.molgen.genomeCATPro.guimodul.experiment.ExportData;
import org.molgen.genomeCATPro.guimodul.experiment.FilterExperimentAction;
import org.molgen.genomeCATPro.guimodul.experiment.MoveDataAction;
import org.molgen.genomeCATPro.guimodul.experiment.NormalizeAction;
import org.molgen.genomeCATPro.guimodul.experiment.ViewExperimentDataAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

public class ExperimentDataNode extends BeanNode<Data> {

    public ExperimentDataNode(Data bean) throws IntrospectionException {


        super(bean,
                Children.create(
                new ExperimentDataNodeFactory(bean), true), Lookups.singleton(bean));
        setDisplayName(bean.getGenomeRelease().substring(0, 4) + ":" + bean.getName());

        setShortDescription(bean.getDescription());
        //sample.addPropertyChangeListener(this);
        setIconBaseWithExtension(bean.getIconPath());
    }
    static List<Action> actionsData = new ArrayList<Action>(
            Arrays.asList(
            new ViewExperimentDataAction(),
            new ViewDataAction(),
            new MoveDataAction(),
            new ExportData(),
            new DeleteDataAction(),
            null,
            new ConvertExperimentAction(),
            new NormalizeAction(),
            new FilterExperimentAction(),
            new AnnotateAction()));
    static List<Action> actionsApp = new ArrayList<Action>(
            Arrays.asList(
            new CGHProFrameAction()));
    static List<Action> actionsCalculate = new ArrayList<Action>(
            Arrays.asList(
            new CBSAction(),
            new HMMAction(),
            new RINGOAction()));

    static public void addCalcAction(Action a) {

        actionsCalculate.add(a);
    }

    static public void addDataAction(Action a) {

        actionsData.add(a);
    }

    static public void addAppAction(Action a) {

        actionsApp.add(a);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> _actions = new ArrayList<Action>();


        _actions.addAll(ExperimentDataNode.actionsApp);
        _actions.add(null);
        _actions.addAll(ExperimentDataNode.actionsCalculate);
        _actions.add(null);
        //_actions.addAll(Arrays.asList(super.getActions(arg0)));
        _actions.addAll(ExperimentDataNode.actionsData);
        _actions.add(null);



        return _actions.toArray(
                new Action[]{});

    }
    @Override
    public Action getPreferredAction(){
        return new ViewExperimentDataAction();
    }
}
