package org.molgen.genomeCATPro.data;
/**
 * @name ServiceNormalize
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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 *
 * @author tebel Service-Factory for Normalization
 */
public abstract class ServiceNormalize {

    static Lookup.Template<INormalize> tmpl
            = new Lookup.Template<INormalize>(
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
     *
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
