package org.molgen.genomeCATPro.guimodul.experiment;

/**
 * @name ExperimentDetailNodeFactory
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
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.datadb.dbentities.DataNode;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;

import org.molgen.genomeCATPro.datadb.dbentities.Study;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.ProjectService;
import org.molgen.genomeCATPro.datadb.service.ServiceListener;
import org.molgen.genomeCATPro.guimodul.tree.ExperimentDetailNode;
import org.molgen.genomeCATPro.guimodul.tree.TrackNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * 050612 kt update notification
 */
public class ExperimentDetailNodeFactory extends ChildFactory<DataNode>
        implements ServiceListener {

    Study project = null;

    public ExperimentDetailNodeFactory(Study p) {
        this.project = p;
        //this.e.addPropertyChangeListener(this);   // 050612 kt

        ExperimentService.addListener(this);        // 050612 kt
        // 050612 kt  TrackService.addListener(this);

    }

    @Override
    protected boolean createKeys(List<DataNode> list) {
        list.clear();
        try {
            if (this.project != null) {
                Logger.getLogger(ExperimentDetailNodeFactory.class.getName()).log(Level.INFO, "getExperimentDetail for Project");
                list.clear();
                list.addAll(ProjectService.listExperimentsForProject(this.project));
                list.addAll(ProjectService.listTracksForProject(this.project));

            }

            //e.setSamples(resultList);
        } catch (Exception ex) {

            Logger.getLogger(ExperimentDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(DataNode a) {
        /*
         Node node = new AbstractNode(Children.LEAF, Lookups.singleton(a));
         node.setDisplayName(a.getName());
         node.setShortDescription(a.getDescription());
        
         return node;
         */
        try {
            if (a instanceof ExperimentDetail) {
                return new ExperimentDetailNode((ExperimentDetail) a);
            }
            if (a instanceof Track) {
                return new TrackNode((Track) a);
            }
            return null;
        } catch (IntrospectionException ex) {
            Logger.getLogger(ExperimentDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
            return null;
        }

    }

    public void dbChanged() {
        /*try {
        
         if (this.project != null) {
         Logger.getLogger(ExperimentDetailNodeFactory.class.getName()).log(Level.INFO, "getExperimentDetail for Project");
         list.addAll(ProjectService.listExperimentsForProject(this.project));
        
         } 
         } catch (Exception ex) {
         Logger.getLogger(ExperimentDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
         }*/
        Logger.getLogger(ExperimentDetailNodeFactory.class.getName()).log(
                Level.INFO, "refresh tree for " + this.project.toString());
        this.refresh(true);
    }
}
