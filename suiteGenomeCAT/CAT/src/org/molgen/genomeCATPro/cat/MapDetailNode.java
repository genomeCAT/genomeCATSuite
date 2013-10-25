package org.molgen.genomeCATPro.cat;
/**
 * @name  MapDetailNode
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
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;

import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;


public class MapDetailNode extends BeanNode<MapDetail> {

    public MapDetailNode(MapDetail bean) throws IntrospectionException {
        super(bean, Children.create(new MapDataNodeFactory(bean), true), Lookups.singleton(bean));

        setDisplayName(bean.getMapName());

    }

    static public void addAction(Action a) {
        actions.add(a);
    }
    static List<Action> actions = new ArrayList<Action>();

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> _actions = new ArrayList<Action>();

        _actions.addAll(Arrays.asList(super.getActions(arg0)));
        _actions.addAll(MapDetailNode.actions);
        _actions.add(new ViewMapAction());

        return _actions.toArray(
                new Action[]{});

    }
}

