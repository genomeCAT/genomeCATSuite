/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.tree;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.guimodul.platform.PlatformDataNodeFactory;
import org.molgen.genomeCATPro.guimodul.platform.ViewPlatformAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author tebel
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
        actions.addAll(Arrays.asList(super.getActions(arg0)));
        actions.add(new ViewPlatformAction());
       
        return actions.toArray(
                new Action[]{});

    }
}

