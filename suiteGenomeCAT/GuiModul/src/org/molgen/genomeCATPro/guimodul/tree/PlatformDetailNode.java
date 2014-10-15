package org.molgen.genomeCATPro.guimodul.tree;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.guimodul.platform.PlatformDataNodeFactory;
import org.molgen.genomeCATPro.guimodul.platform.ViewPlatformAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
/*
 * @name PlatformDetailNode
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
/**
 *
 *
 * 141014 no menue item Properties
 */
public class PlatformDetailNode extends BeanNode<PlatformDetail> {

    public PlatformDetailNode(PlatformDetail bean) throws IntrospectionException {
        super(bean, Children.create(new PlatformDataNodeFactory(bean), true),Lookups.singleton(bean));
       
        setDisplayName(bean.getName());
        setIconBaseWithExtension("org/molgen/genomeCATPro/guimodul/data/folder_array_16.png");
    }
    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        //actions.addAll(Arrays.asList(super.getActions(arg0)));
        actions.add(new ViewPlatformAction());
       
        return actions.toArray(
                new Action[]{});

    }
    @Override
    public Action getPreferredAction(){
        return new ViewPlatformAction();
    }
}

