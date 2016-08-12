package org.molgen.genomeCATPro.ngs;

/**
 * @name ServiceNGS
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
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 *
 *
 * Service-Factory
 */
public abstract class ServiceNGS {

    static Lookup.Template<XPortNGS> tmplXPortNGS = new Lookup.Template<XPortNGS>(org.molgen.genomeCATPro.ngs.XPortNGS.class);

    public static Vector<String> getFileTypeNGSImport() {
        Vector<String> listall = new Vector<String>();

        Lookup.Result<XPortNGS> rslt = Lookup.getDefault().lookup(ServiceNGS.tmplXPortNGS);
        for (XPortNGS impl : rslt.allInstances()) {
            Logger.getLogger(ServiceNGS.class.getName()).log(Level.INFO, "getFileTypesNGS: Found Service: " + impl.getModulName());
            Vector<String> list = impl.getImportType();
            listall.addAll(list);
        }
        Collections.sort(listall);
        return listall;
    }

    public static XPortNGS getXPortNGS(String filetype) {
        Result<XPortNGS> rslt = Lookup.getDefault().lookup(ServiceNGS.tmplXPortNGS);
        for (XPortNGS impl : rslt.allInstances()) {
            Logger.getLogger(ServiceNGS.class.getName()).log(Level.INFO, " getXPortNGS: Found Service: " + impl.getModulName());
            Vector<String> list = impl.getImportType();
            if (list.contains(filetype)) {
                return impl;
            }
        }
        return null;
    }
}
