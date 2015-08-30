package org.molgen.genomeCATPro.cat;

import java.beans.PropertyChangeEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.molgen.genomeCATPro.datadb.service.MapService;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 * @name MapDataNodeFactory
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
public class MapDataNodeFactory extends ChildFactory<MapData>
        implements PropertyChangeListener {

    MapDetail e = null;

    public MapDataNodeFactory(MapDetail e) {
        this.e = e;

    }

    @Override
    protected boolean createKeys(List<MapData> list) {
        list.clear();
        try {


            Logger.getLogger(MapDataNodeFactory.class.getName()).log(Level.INFO, "get Track Data for Parent");

            list.addAll(MapService.getMapDataList(e));
        } catch (Exception ex) {

            Logger.getLogger(MapDataNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(MapData c) {


        MapDataNode node = null;
        try {
            node = new MapDataNode(c);
        } catch (IntrospectionException ex) {
            Logger.getLogger(MapDataNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

        return node;
    }

    /* @Override
    protected Node[] createNodesForKey(Data c) {
    try {
    return new ExperimentDataNode[]{new ExperimentDataNode(c)};
    } catch (IntrospectionException ex) {
    Logger.getLogger(ExperimentDataNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
    return null;
    }
    }
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Logger.getLogger(MapDataNodeFactory.class.getName()).log(
                Level.INFO, "refresh node");
        this.refresh(true);
    }
}



