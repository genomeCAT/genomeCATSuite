package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ServiceXPort
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 *
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
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 *  020813   kt      XPortImport createNewImport();
 * 
 *  Service-Factory
 */
public abstract class ServiceXPort {

    static Lookup.Template<XPortExperimentFile> tmplXPortFile = new Lookup.Template<XPortExperimentFile>(org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile.class);
    static Lookup.Template<XPortPlatform> tmplXPortPlatform = new Lookup.Template<XPortPlatform>(org.molgen.genomeCATPro.cghpro.xport.XPortPlatform.class);
    static Lookup.Template<XPortTrack> tmplXPortTrack = new Lookup.Template<XPortTrack>(org.molgen.genomeCATPro.cghpro.xport.XPortTrack.class);

    /**
     * list of all filetypes for attached import modules  - Import
     * @return
     */
    public static Vector<String> getFileTypesImport() {
        Vector<String> listall = new Vector<String>();


        Lookup.Result<XPortExperimentFile> rslt = Lookup.getDefault().lookup(ServiceXPort.tmplXPortFile);
        for (XPortExperimentFile impl : rslt.allInstances()) {
            Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, "getFileTypesImport: Found Service: " + impl.getName());
            Vector<String> list = impl.getImportType();
            listall.addAll(list);
        }
        Collections.sort(listall);
        return listall;
    }

    /**
     * list of all filetypes for attached import modules  - platform
     * @return
     */
    public static Vector<String> getFileTypesPlatformImport() {
        Vector<String> listall = new Vector<String>();


        Lookup.Result<XPortPlatform> rslt = Lookup.getDefault().lookup(ServiceXPort.tmplXPortPlatform);
        for (XPortPlatform impl : rslt.allInstances()) {
            Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, "getFileTypesPlatformImport: Found Service: " + impl.getName());
            Vector<String> list = impl.getImportType();
            listall.addAll(list);
        }
        Collections.sort(listall);
        return listall;
    }

    /**
     * 
     * @return list of all filetypes for attached import modules  - track
     */
    public static Vector<String> getFileTypeTrackImport() {
        Vector<String> listall = new Vector<String>();


        Lookup.Result<XPortTrack> rslt = Lookup.getDefault().lookup(ServiceXPort.tmplXPortTrack);
        for (XPortTrack impl : rslt.allInstances()) {
            Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, "getFileTypesXPortTrack: Found Service: " + impl.getName());
            Vector<String> list = impl.getImportType();
            listall.addAll(list);
        }
        Collections.sort(listall);
        return listall;
    }

    /**
     * lookup import dependent on file type
     * @param filetype
     * @return
     */
    public static XPortExperimentFile getXPortImport(String filetype) {
        Result<XPortExperimentFile> rslt = Lookup.getDefault().lookup(ServiceXPort.tmplXPortFile);
        for (XPortExperimentFile impl : rslt.allInstances()) {
            Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, " getXPortImport: Found Service: " + impl.getName());
            Vector<String> list = impl.getImportType();
            if (list.contains(filetype)) {
                Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, "Found Service: " + impl.getName());

                return impl;
            }
        }
        return null;
    }

    /**
     * lookup track dependent on file type
     * @param filetype
     * @return
     */
    public static XPortTrack getXPortTrack(String filetype) {
        Result<XPortTrack> rslt = Lookup.getDefault().lookup(ServiceXPort.tmplXPortTrack);
        for (XPortTrack impl : rslt.allInstances()) {
            Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, " getXPortTrack: Found Service: " + impl.getName());
            Vector<String> list = impl.getImportType();
            if (list.contains(filetype)) {

                Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, " getXPortTrack: return Service: " + impl.getName());


                return (XPortTrack) impl.createNewImport();
            }
        }
        return null;
    }

    /**
     * lookup platform dependent on filetype
     * @param filetype
     * @return
     */
    public static XPortPlatform getXPortPlatform(String filetype) {
        Result<XPortPlatform> rslt = Lookup.getDefault().lookup(ServiceXPort.tmplXPortPlatform);
        for (XPortPlatform impl : rslt.allInstances()) {
            Logger.getLogger(ServiceXPort.class.getName()).log(Level.INFO, "getXPortPlatform: Found Service: " + impl.getName());
            Vector<String> list = impl.getImportType();
            if (list.contains(filetype)) {
                return impl;
            }
        }
        return null;
    }
}
