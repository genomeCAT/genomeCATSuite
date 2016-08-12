package org.molgen.genomeCATPro.guimodul.tree;

/**
 * @name PlatformDataNode
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.guimodul.data.ViewDataAction;
import org.molgen.genomeCATPro.guimodul.platform.ConvertPlatformAction;
import org.molgen.genomeCATPro.guimodul.platform.DeletePlatformAction;
import org.molgen.genomeCATPro.guimodul.platform.ExportPlatformAction;
import org.molgen.genomeCATPro.guimodul.platform.ViewPlatformDataAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * 141014 no menue item Properties
 */
public class PlatformDataNode extends BeanNode<PlatformData> {

    public PlatformDataNode(PlatformData bean) throws IntrospectionException {
        super(bean, Children.LEAF, Lookups.singleton(bean));
        setDisplayName(bean.getGenomeRelease() + "_" + bean.getName());
        setShortDescription(
                bean.toFullString());
        setIconBaseWithExtension("org/molgen/genomeCATPro/guimodul/data/array_16.png");
    }

    @Override
    public Action[] getActions(boolean arg0) {
        List<Action> actions = new ArrayList<Action>();
        //actions.addAll(Arrays.asList(super.getActions(arg0)));
        actions.add(new ViewPlatformDataAction());
        actions.add(new ViewDataAction());
        actions.add(new ConvertPlatformAction());
        actions.add(new ExportPlatformAction());
        actions.add(new DeletePlatformAction());
        return actions.toArray(
                new Action[]{});

    }

    @Override
    public Action getPreferredAction() {
        return new ViewPlatformDataAction();
    }
}
