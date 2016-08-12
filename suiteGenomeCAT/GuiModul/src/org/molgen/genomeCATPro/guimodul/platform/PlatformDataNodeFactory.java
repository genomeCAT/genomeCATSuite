package org.molgen.genomeCATPro.guimodul.platform;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.PlatformService;
import org.molgen.genomeCATPro.guimodul.tree.PlatformDataNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/*
 * @name PlatformDataNodeFactory
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * This file is part of the GenomeCATPro software package.
 * Katrin Tebel <tebel at molgen.mpg.de>.
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
public class PlatformDataNodeFactory extends ChildFactory<PlatformData>
        implements PropertyChangeListener {

    private PlatformDetail p;

    public PlatformDataNodeFactory(PlatformDetail e) {
        this.p = e;
        p.addPropertyChangeListener(this);

    }

    @Override
    protected boolean createKeys(List<PlatformData> list) {
        list.clear();
        try {
            Logger.getLogger(PlatformDataNodeFactory.class.getName()).log(Level.INFO,
                    "get Platforms");
            list.addAll(PlatformService.listPlatformData(p));
            //e.setSamples(resultList);

        } catch (Exception ex) {

            Logger.getLogger(PlatformDataNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

        return true;
    }

    @Override
    protected Node[] createNodesForKey(PlatformData c) {
        try {
            return new PlatformDataNode[]{new PlatformDataNode(c)};
        } catch (IntrospectionException ex) {
            Logger.getLogger(PlatformDataNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
            return null;
        }
    }

    @Override
    protected Node createNodeForKey(PlatformData c) {

        PlatformDataNode node = null;
        try {
            node = new PlatformDataNode(c);
        } catch (IntrospectionException ex) {
            Logger.getLogger(PlatformDataNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

        return node;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Logger.getLogger(PlatformDataNodeFactory.class.getName()).log(Level.INFO, "propertyChange");
        this.refresh(true);
    }
}
