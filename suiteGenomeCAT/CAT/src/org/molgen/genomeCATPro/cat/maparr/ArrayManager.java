package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name ArrayManager
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.cat.ArrayRegionImpl;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;

public class ArrayManager {

    static long _id = 0;

    static Long getNextArrayId() {
        return ++_id;
    }
    static Lookup.Template<RegionArray> tmpRegionArray = new Lookup.Template<RegionArray>(org.molgen.genomeCATPro.annotation.RegionArray.class);

    public static RegionArray getClazz(String clazz) {

        //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
        Logger.getLogger(ArrayManager.class.getName()).log(Level.INFO,
                "looking for datatype clazz " + clazz);

        Result<RegionArray> rslt = Lookup.getDefault().lookup(tmpRegionArray);
        for (Lookup.Item item : rslt.allItems()) {
            if (item.getType().getName().contentEquals(clazz)) {
                Logger.getLogger(ArrayManager.class.getName()).log(Level.INFO,
                        "found ");
                return (RegionArray) item.getInstance();
            }
        }

        return new ArrayRegionImpl();
    }

}
