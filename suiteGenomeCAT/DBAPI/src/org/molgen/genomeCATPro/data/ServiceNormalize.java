/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.data;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 *
 * @author tebel
 *  Service-Factory for Normalization
 */
public abstract class ServiceNormalize {

    static Lookup.Template<INormalize> tmpl =
            new Lookup.Template<INormalize>(
            org.molgen.genomeCATPro.data.INormalize.class);

    public static Vector<String> getAvialableMethods() {
        Vector<String> listall = new Vector<String>();


        Lookup.Result<INormalize> rslt = Lookup.getDefault().lookup(
                ServiceNormalize.tmpl);
        for (INormalize impl : rslt.allInstances()) {
            Logger.getLogger(ServiceNormalize.class.getName()).log(Level.INFO,
                    "getAvialableMethods: Found Service: " + impl.getMethodName());

            listall.add(impl.getMethodName());
        }
        return listall;
    }

    /**
     * get normalization implementation, identified by methodname
     * @param type
     * @return
     */
    public static INormalize getNormalizationImpl(String method) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(ServiceNormalize.class.getName()).log(Level.INFO, "looking for: " + method);

        Result<INormalize> rslt = Lookup.getDefault().lookup(ServiceNormalize.tmpl);
        for (INormalize impl : rslt.allInstances()) {
            Logger.getLogger(ServiceNormalize.class.getName()).log(Level.INFO,
                    " get: Found Service: " + impl.getMethodName());

            if (impl.getMethodName().contentEquals(method)) {
                return impl;
            }
        }
        Logger.getLogger(ServiceNormalize.class.getName()).log(Level.INFO, "no service found ");
        return null;
    }
}
