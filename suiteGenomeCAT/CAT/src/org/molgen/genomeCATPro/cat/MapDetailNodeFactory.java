/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;

import java.beans.IntrospectionException;
import java.util.List;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.molgen.genomeCATPro.datadb.dbentities.MapDetail;
import org.molgen.genomeCATPro.datadb.service.MapService;
import org.molgen.genomeCATPro.datadb.service.ServiceListener;
import org.molgen.genomeCATPro.cat.MapDetailNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author tebel
 */
public class MapDetailNodeFactory extends ChildFactory<MapDetail>
        implements ServiceListener {

    private List<MapDetail> resultList;

    public MapDetailNodeFactory() {
        try {
            MapService.addListener(this);
            resultList = MapService.getMapList();
        } catch (Exception ex) {
            resultList = new Vector<MapDetail>();
            Logger.getLogger(MapDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }

    }

    @Override
    protected boolean createKeys(List<MapDetail> list) {
        for (MapDetail a : resultList) {
            list.add(a);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(MapDetail a) {
        /*
        Node node = new AbstractNode(Children.LEAF, Lookups.singleton(a));
        node.setDisplayName(a.getName());
        node.setShortDescription(a.getDescription());
        
        return node;
         */
        try {
            return new MapDetailNode(a);
        } catch (IntrospectionException ex) {
            Logger.getLogger(MapDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
            return null;
        }

    }

    public void dbChanged() {
        try {
            resultList = MapService.getMapList();
        } catch (Exception ex) {
            Logger.getLogger(MapDetailNodeFactory.class.getName()).log(Level.SEVERE, "", ex);
        }
        this.refresh(true);
    }
}
