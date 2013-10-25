package org.molgen.genomeCATPro.annotation;

/**
 * @name  PlotLib
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 260612 kt remove bug getIDataAtPos 
 * 290612 kt  getDataAtPos/getIDataAtPos  pos liegt innerhalb der region
 */
public class PlotLib {

    /**
     * @Deprecated
     * return index position 
     * @param list
     * @param key
     * @return
     */
    public static int getIndexDataAtPos(List<Long> list, Long key) {


        int i = Collections.binarySearch(list, key);
        i = i < 0 ? i * -1 - 2 : i;     // i:= (-(insertion point) - 1)

        if (i < 0) {
            i = 0;
        }
        return i;
    }

    /**
     * @Deprecated
     * @param list
     * @param key
     * @return
     */
    public static int getIndexDataAtPos(List<? extends Region> list, Region key) {


        int i = Collections.binarySearch(list, key, Region.comChromStart);
        i = i < 0 ? i * -1 - 2 : i;     // i:= (-(insertion point) - 1)

        if (i < 0) {
            i = 0;
        }
        return i;
    }

    /**
     * get index for element with start (or stop if start not found) near current position
     * @param arrayStart
     * @param arrayEnd
     * @param pos
     * @return
     */
    public static int getIDataAtPos(List<Long> arrayStart, List<Long> arrayEnd, Long pos, Long dist) {
        if (arrayStart == null || arrayEnd == null) {
            return -1;
        }

        int ia = Collections.binarySearch(arrayStart, pos);
        ia = ia < 0 ? ia * -1 - 2 : ia;     // i:= (-(insertion point) - 1)

        if (ia < 0) {
            ia = 0;
        }
        //290612 kt pos liegt innerhalb region
        if(arrayStart.get(ia) <= pos && pos <= arrayEnd .get(ia) )
            return ia;
        
        int ie = Collections.binarySearch(arrayEnd, pos);
        ie = ie < 0 ? ie * -1 - 2 : ie;     // i:= (-(insertion point) - 1)

        if (ie < 0) {
            ie = 0;
        }

        int i = -1;
        long distA = Math.abs(arrayStart.get(ia) - pos);
        long distB = Math.abs(arrayEnd.get(ie) - pos);

        if (distA < dist && distA <= distB) {
            i = ia;
        }
        if (distB < dist && distA > distB) {
            i = ie;
        }
        return i;
    }

    /**
     * get element with start (or stop if start not found) near current position
     * @param list
     * @param chromId
     * @param pos
     * @return
     */
    public static Region getDataAtPos(List<? extends Region> list, String chromId, Long pos, Long dist) {
        if (list == null) {
            return null;
        }
        Region key = (Region) new RegionImpl("key", chromId, pos, pos);
        int ia = Collections.binarySearch(list, key, Region.compByStart);
        ia = ia < 0 ? ia * -1 - 2 : ia;     // i:= (-(insertion point) - 1)

        if (ia < 0) {
            ia = 0;
        }
        //290612 kt pos liegt innerhalb region
        if (list.get(ia).getChromStart() <= pos && pos <= list.get(ia).getChromEnd()) {
            return list.get(ia);
        }
        int ie = Collections.binarySearch(list, key, Region.compByEnd);
        ie = ie < 0 ? ie * -1 - 2 : ie;     // i:= (-(insertion point) - 1)

        if (ie < 0) {
            ie = 0;
        }
        int i = -1;

        long distA = pos - list.get(ia).getChromStart();
        long distB = list.get(ie).getChromEnd() - pos;

        // entscheiden, welche Region näher dran ist.

        if (Math.abs(distA) < dist && (Math.abs(distA) <= Math.abs(distB))) {
            i = ia;
        }

        if (Math.abs(distB) < dist && (Math.abs(distA) > Math.abs(distB))) {
            i = ie;

        }
        return i > 0 ? list.get(i) : null;
    }

    public static synchronized List<Long> getSublist(
            List<Long> list,
            long start, long end) {
        return null;
    }

    public static synchronized List<? extends Region> getSublist(
            List<? extends Region> list,
            String chromId, long start, long end) {
        /* chromStart < posEnd and chromEnd > posStart
         * indcludes
         *   [ ---- ]
         *   --[--  ]
         *   [  --]--
         *   --[--]--
         * excludes
         *   ---- [ ]
         *   [ ] ----
         */

        Logger.getLogger(PlotLib.class.getName()).log(Level.INFO,
                "get Sublist from " + start + " to " + end);

        // find ab. with greatest start position less than posEnd
        Collections.sort(list, Region.compByStart);

        Region key = (Region) new RegionImpl("key", chromId, end, end);

        int ind1 = Collections.binarySearch(
                list,
                key,
                Region.compByStart);


        //int ind1 = Collections.binarySearch(a, new Aberration(posEnd, (long) 0), Aberration.findByStart);
        ind1 = ind1 < 0 ? (ind1 * -1) - 2 : ind1;     // i:= (-(insertion point) - 1)

        if (ind1 < 0) {
            ind1 = 0;
        // go to the last element with chromStart == posEnd
        }

        Logger.getLogger(PlotLib.class.getName()).log(Level.INFO,
                "found end " + ind1 + " as " + list.get(ind1).toString());

        for (int i = ind1 + 1; i < list.size(); i++) {
            if (list.get(i).getChromStart() == list.get(ind1).getChromStart()) {
                ind1 = i;
            } else {
                break;
            }

        }
        Logger.getLogger(PlotLib.class.getName()).log(Level.INFO,
                "moved to " + ind1 + " as " + list.get(ind1).toString());
        // all elements with chromStart less than end position

        List<? extends Region> x = list.subList(0, ind1 + 1);
        Collections.sort(x, Region.compByEnd);


        // find ab with minimal end position greater than posStart
        key = (Region) new RegionImpl("key", chromId, start, start);
        int ind2 = Collections.binarySearch(
                x,
                key,
                Region.compByEnd);


        // int ind2 = Collections.binarySearch(x, new Aberration( (long) 0, posStart), Aberration.findByEnd);
        ind2 = ind2 < 0 ? (ind2 * -1) - 1 : ind2;     // i:= (-(insertion point) - 1), false!!
        // last value: 70, i = -72

        if (ind2 < 0) {
            ind2 = 0;
        }
        if (ind2 >= x.size()) {
            ind2 = x.size() - 1;
        }
        Logger.getLogger(PlotLib.class.getName()).log(Level.INFO,
                "found start " + ind2 + " as " + x.get(ind2).toString());

        for (int i = ind2 - 1; i >= 0; i--) {
            if (x.get(i).getChromEnd() == x.get(ind2).getChromEnd()) {
                ind2 = i;
            } else {
                break;
            }

        }
        Logger.getLogger(PlotLib.class.getName()).log(Level.INFO,
                "moved to " + ind2 + " as " + x.get(ind2).toString());


        Logger.getLogger(PlotLib.class.getName()).log(Level.INFO,
                "sublist " + x.get(ind2).toString() + " to " +
                x.get(x.size() - 1).toString());

        return x.subList(ind2, x.size());

    }
}
