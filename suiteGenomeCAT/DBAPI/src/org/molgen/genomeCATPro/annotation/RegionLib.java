package org.molgen.genomeCATPro.annotation;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.data.FeatureImpl;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

/**
 * @name RegionLib
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
/**
 *
 * log 240412 getRegionArrayClazz with Default FeatureImpl
 */
public class RegionLib {

    static Lookup.Template<Region> tmplRegion = new Lookup.Template<Region>(org.molgen.genomeCATPro.annotation.Region.class);
    static Lookup.Template<RegionArray> tmplRegionArray = new Lookup.Template<RegionArray>(org.molgen.genomeCATPro.annotation.RegionArray.class);

    public static Region getRegionClazz(String clazz) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(RegionLib.class.getName()).log(Level.INFO,
                "looking for datatype " + tmplRegion.toString() + " clazz " + clazz);

        Result<Region> rslt = Lookup.getDefault().lookup(tmplRegion);
        for (Lookup.Item item : rslt.allItems()) {
            if (item.getType().getName().contentEquals(clazz)) {
                Logger.getLogger(RegionLib.class.getName()).log(Level.INFO,
                        "found " + item.getType().getName());
                return (Region) item.getInstance();
            }
        }

        return new RegionImpl();
    }

    /**
     *
     * @param clazz
     * @return
     */
    public static RegionArray getRegionArrayClazz(String clazz) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(RegionLib.class.getName()).log(Level.INFO,
                "looking for datatype clazz " + clazz);

        Result<RegionArray> rslt = Lookup.getDefault().lookup(tmplRegionArray);
        for (Lookup.Item item : rslt.allItems()) {
            if (item.getType().getName().contentEquals(clazz)) {
                Logger.getLogger(RegionLib.class.getName()).log(Level.INFO,
                        "found ");
                return (RegionArray) item.getInstance();
            }
        }

        return new FeatureImpl();
    }

    public static int fromChrToInt(String chr) {

        if (chr.matches("chr\\d+")) {

            int i = (new Integer(chr.substring(3))).intValue();
            return i;

        } else if (chr.equalsIgnoreCase("chrX")) {

            return 23;

        } else if (chr.equalsIgnoreCase("chrY")) {

            return 24;

        } else {

            return 0;

        }

    }

    public static String fromIntToChr(int no) {

        if ((no > 0) && (no < 23)) {

            return "chr" + no;

        } else if (no == 23) {

            return "chrX";

        } else if (no == 24) {

            return "chrY";

        } else {

            return "null";

        }

    }
}
