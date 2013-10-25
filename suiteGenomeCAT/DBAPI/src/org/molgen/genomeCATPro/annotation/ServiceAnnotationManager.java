/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.annotation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

/**
 *
 * @author tebel
 *  Service-Factory
 */
public abstract class ServiceAnnotationManager {

    static Lookup.Template<RegionAnnotation> tmpl =
            new Lookup.Template<RegionAnnotation>(
            org.molgen.genomeCATPro.annotation.RegionAnnotation.class);

    /**
     * get special xport implementation, identified by type
     * @param type
     * @return
     */
    public static RegionAnnotation getRegionInstance(String clazz) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(ServiceAnnotationManager.class.getName()).log(Level.INFO, "looking for: " + clazz);


        Lookup.Result<RegionAnnotation> rslt = Lookup.getDefault().lookup(tmpl);
        for (RegionAnnotation impl : rslt.allInstances()) {
            Logger.getLogger(ServiceAnnotationManager.class.getName()).log(Level.INFO, 
                    "Found Service: " + impl.getClass().getName());
            if (impl.getClass().getName().equalsIgnoreCase(clazz)) {
                Logger.getLogger(ServiceAnnotationManager.class.getName()).log(Level.INFO, 
                        "Found Service: " + impl.getClass().getName());

                return impl;
            }
        }
        Logger.getLogger(ServiceAnnotationManager.class.getName()).log(Level.INFO, "no service found ");
        return new RegionAnnotationImpl();
    }
}
