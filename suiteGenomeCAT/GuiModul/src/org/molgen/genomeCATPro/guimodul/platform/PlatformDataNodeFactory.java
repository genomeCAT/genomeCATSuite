/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author tebel
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



