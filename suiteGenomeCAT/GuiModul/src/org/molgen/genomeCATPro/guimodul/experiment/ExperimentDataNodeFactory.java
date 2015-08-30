package org.molgen.genomeCATPro.guimodul.experiment;

/**
 * @name ExperimentDataNodeFactory
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
import org.molgen.genomeCATPro.guimodul.tree.ExperimentDataNode;
import java.beans.IntrospectionException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.guimodul.tree.TrackNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.molgen.genomeCATPro.datadb.service.ServiceListener;

/**
 *
 * 050612 kt set own ServiceListener instead of PropertyChangeListener
 * 050612 kt update notification
 */
public class ExperimentDataNodeFactory extends ChildFactory<Data>
        implements ServiceListener {

    ExperimentDetail e = null;
    Data data = null;

    public ExperimentDataNodeFactory(ExperimentDetail e) {
        this.e = e;
        //this.e.addPropertyChangeListener(this);       // 050612 kt
        this.data = null;

        ExperimentService.addListener(this);            // 050612 kt
        // 050612 kt  TrackService.addListener(this);

        
    }

    public ExperimentDataNodeFactory(Data d) {
        this.data = d;
        //this.data.addPropertyChangeListener(this);    // 050612 kt
        ExperimentService.addListener(this);            // 050612 kt

        // 050612 kt  TrackService.addListener(this);

        this.e = null;
    }

    @Override
    protected boolean createKeys(List<Data> list) {
        list.clear();
        try {
            if (this.e != null) {
                Logger.getLogger(ExperimentDataNodeFactory.class.getName()).log(Level.INFO, "getExperiment Data for Detail");
                list.addAll(this.e.getDataList());

            } else if (this.data != null) {
                if (this.data instanceof ExperimentData) {
                    Logger.getLogger(ExperimentDataNodeFactory.class.getName()).log(Level.INFO, "getExperiment Data for Parent");
                    list.addAll(((ExperimentData) this.data).getChilrenList());
                    list.addAll(((ExperimentData) this.data).getTrackList());
                }
                if (this.data instanceof Track) {
                    Logger.getLogger(ExperimentDataNodeFactory.class.getName()).log(Level.INFO, "get Track Data for Parent");

                    list.addAll(((Track) this.data).getChildrenList());
                }
            } else {
            }

        //e.setSamples(resultList);

        } catch (Exception ex) {

            Logger.getLogger(ExperimentDataNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(Data c) {

        Node node = null;
        //ExperimentDataNode node = null;
        try {
            if (c instanceof Track) {
                node = new TrackNode((Track) c);
            } else {
                node = new ExperimentDataNode(c);
            }
        } catch (IntrospectionException ex) {
            Logger.getLogger(ExperimentDataNodeFactory.class.getName()).log(Level.SEVERE,
                    (e != null ? e.toString() : (data != null ? data.toString() : "")), ex);
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
    public void dbChanged() {
        Logger.getLogger(ExperimentDataNodeFactory.class.getName()).log(
                Level.INFO, "refresh data node " + (e != null ? e.toString() : data.toString()));
        this.refresh(true);
    }
}



