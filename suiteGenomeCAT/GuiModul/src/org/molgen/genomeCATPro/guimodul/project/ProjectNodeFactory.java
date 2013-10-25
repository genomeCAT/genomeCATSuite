package org.molgen.genomeCATPro.guimodul.project;

/**
 * @name ProjectNodeFactory
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
import java.util.List;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.datadb.dbentities.DataNode;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.Study;

import org.molgen.genomeCATPro.datadb.service.ProjectService;
import org.molgen.genomeCATPro.datadb.service.ServiceListener;

import org.molgen.genomeCATPro.guimodul.tree.ExperimentDetailNode;
import org.molgen.genomeCATPro.guimodul.tree.ProjectNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
/**
 * 
 * 050612 kt update Notification
 */
public class ProjectNodeFactory extends ChildFactory<DataNode>
        implements ServiceListener {

    private List<DataNode> resultList = new Vector<DataNode>();

    public ProjectNodeFactory() {
        try {
            Logger.getLogger(ProjectNodeFactory.class.getName()).log(
                    Level.INFO, "get project tree");
            ProjectService.addListener(this);   // 050612 kt
            resultList.clear();
            if (!ProjectTreeTopComponent.isFilter) {
                resultList.addAll(ProjectService.listProjects());
                resultList.addAll(ProjectService.listExperimentsWoProject());
            } else {
                resultList.addAll(
                        ProjectService.listProjectsWithFilter(
                        ProjectTreeTopComponent.project,
                        ProjectTreeTopComponent.release,
                        ProjectTreeTopComponent.user,
                        ProjectTreeTopComponent.sample));
                resultList.addAll(ProjectService.listExperimentsWoProjectWithFilter(
                        ProjectTreeTopComponent.release,
                        ProjectTreeTopComponent.user,
                        ProjectTreeTopComponent.sample));

            }

        } catch (Exception ex) {
            resultList = new Vector<DataNode>();
            Logger.getLogger(ProjectNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

    }

    @Override
    protected boolean createKeys(List<DataNode> list) {
        for (DataNode a : resultList) {
            list.add(a);
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
            if (a instanceof Study) {
                return new ProjectNode((Study) a);
            }
            if (a instanceof ExperimentDetail) {
                return new ExperimentDetailNode((ExperimentDetail) a);
            }

            return null;
        } catch (IntrospectionException ex) {
            Logger.getLogger(ProjectNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
            return null;
        }

    }

    public void dbChanged() {
        try {
            this.refresh(true);
            Logger.getLogger(ProjectNodeFactory.class.getName()).log(
                    Level.INFO, "refresh project tree " );
        //resultList = ProjectService.listProjects();
        } catch (Exception ex) {
            Logger.getLogger(ProjectNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

    }
}




