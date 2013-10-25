package org.molgen.genomeCATPro.annotation;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @name Region
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


public interface Region extends Serializable, Comparable {

    public String getName();

    public String getChrom();

    public long getChromEnd();

    public long getChromStart();

    public void setName(String name);

    public void setChrom(String chrom);

    public void setChromEnd(long chromEnd);

    public void setChromStart(long chromStart);

    public String toHTMLString();

    public String toString();

    public boolean equalsByPos(Region r2);
    /**
     * compare 
     */
    public static final Comparator<Region> compByPos = new Comparator<Region>() {

        public int compare(Region r1, Region r2) {


            int rc = (int) (r1.getChromStart() - r2.getChromStart());
            if (rc >= 0) {
                long l1 = (r1.getChromStart() - r1.getChromEnd());
                long l2 = (r2.getChromStart() - r2.getChromEnd());

                return (int) (l1 - l2);
            } else {
                return rc;
            }

        }
    };
    public static final Comparator<Region> compByStart = new Comparator<Region>() {

        public int compare(Region r1, Region r2) {

            return (int) (r1.getChromStart() - r2.getChromStart());

        }
    };
    public static final Comparator<Region> compByEnd = new Comparator<Region>() {

        public int compare(Region r1, Region r2) {

            return (int) (r1.getChromEnd() - r2.getChromEnd());

        }
    };
    public static final Comparator<Region> comChromStart = new Comparator<Region>() {

        public int compare(Region r1, Region r2) {

            int chr1 = RegionLib.fromChrToInt(r1.getChrom());
            int chr2 = RegionLib.fromChrToInt(r2.getChrom());

            if (chr1 > chr2) {
                return 1;
            } else if (chr1 < chr2) {
                return -1;
            } else {
                if (r1.getChromStart() > r2.getChromStart()) {
                    return 1;
                } else if (r1.getChromStart() == r2.getChromStart()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
    };

    public String getIconPath();
}
