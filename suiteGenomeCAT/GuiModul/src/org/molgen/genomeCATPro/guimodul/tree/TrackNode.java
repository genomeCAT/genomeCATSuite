package org.molgen.genomeCATPro.guimodul.tree;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.guimodul.anno.AnnotateAction;
import org.molgen.genomeCATPro.guimodul.cghpro.CBSAction;
import org.molgen.genomeCATPro.guimodul.cghpro.CGHProFrameAction;
import org.molgen.genomeCATPro.guimodul.cghpro.HMMAction;
import org.molgen.genomeCATPro.guimodul.data.ViewDataAction;
import org.molgen.genomeCATPro.guimodul.experiment.DeleteDataAction;
import org.molgen.genomeCATPro.guimodul.experiment.ExperimentDataNodeFactory;
import org.molgen.genomeCATPro.guimodul.experiment.ExportData;
import org.molgen.genomeCATPro.guimodul.experiment.FilterExperimentAction;
import org.molgen.genomeCATPro.guimodul.track.MoveTrackAction;
import org.molgen.genomeCATPro.guimodul.track.ViewTrackDataAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 * @name TrackNode
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
public class TrackNode extends BeanNode<Track> {

    public TrackNode(Track bean) throws IntrospectionException {
        //super(new TrackChildren(track), Lookups.singleton(track));
        super(bean,
                Children.create(
                new ExperimentDataNodeFactory(bean), true), Lookups.singleton(bean));

        setDisplayName("Track: " + bean.getName());
        setShortDescription(bean.getDescription());
        setIconBaseWithExtension(bean.getIconPath());
    }
    static List<Action> actionsData = new ArrayList<Action>(
            Arrays.asList(
            new ViewTrackDataAction(),
            new ViewDataAction(),
            new MoveTrackAction(),
            new ExportData(),
            new DeleteDataAction(),
            null,
            new FilterExperimentAction(),
            new AnnotateAction()));
    static List<Action> actionsApp = new ArrayList<Action>(
            Arrays.asList(new CGHProFrameAction()));
    static List<Action> actionsCalculate = new ArrayList<Action>(
            Arrays.asList(           
            new CBSAction(),
            new HMMAction()));

    static public void addActionCalculate(Action a) {
        actionsCalculate.add(a);
    }

    static public void addActionData(Action a) {
        actionsData.add(a);
    }

    static public void addActionApp(Action a) {
        actionsApp.add(a);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> _actions = new ArrayList<Action>();

       
        _actions.addAll(TrackNode.actionsApp);
        _actions.add(null);
        _actions.addAll(TrackNode.actionsCalculate);
        _actions.add(null);
        _actions.addAll(Arrays.asList(super.getActions(arg0)));
        _actions.addAll(TrackNode.actionsData);
        _actions.add(null);


        return _actions.toArray(
                new Action[]{});

    }
}
