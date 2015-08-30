/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.platform;

import java.beans.IntrospectionException;
import java.util.List;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.PlatformService;
import org.molgen.genomeCATPro.datadb.service.ServiceListener;
import org.molgen.genomeCATPro.guimodul.tree.PlatformDetailNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author tebel
 */
public class PlatformDetailNodeFactory extends ChildFactory<PlatformDetail>
        implements ServiceListener {

    private List<PlatformDetail> resultList;

    public PlatformDetailNodeFactory() {
        try {
            PlatformService.addListener(this);
            resultList = PlatformService.listPlatformDetail();
        } catch (Exception ex) {
            resultList = new Vector<PlatformDetail>();
            Logger.getLogger(PlatformDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

    }

    public PlatformDetailNodeFactory(List<PlatformDetail> resultList) {
        this.resultList = resultList;
    }

    @Override
    protected boolean createKeys(List<PlatformDetail> list) {
        for (PlatformDetail a : resultList) {
            list.add(a);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(PlatformDetail a) {

        /*Node node = new AbstractNode(Children.LEAF, Lookups.singleton(a));
        node.setDisplayName(a.getName());
        node.setShortDescription(a.getMethod());
        
        return node;*/
        try {
            return new PlatformDetailNode(a);
        } catch (IntrospectionException ex) {
            Logger.getLogger(PlatformDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
            return null;
        }

    }

    public void dbChanged() {
        try {
            resultList = PlatformService.listPlatformDetail();
        } catch (Exception ex) {
            Logger.getLogger(PlatformDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }
        this.refresh(true);
    }
}




