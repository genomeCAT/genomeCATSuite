package org.molgen.genomeCATPro.annotation;

import java.util.Comparator;

/**
 * @name Region
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
public interface Region extends java.io.Serializable, Comparable {

    public String getName();

    public String getChrom();

    public long getChromEnd();

    public long getChromStart();

    public void setName(String name);

    public void setChrom(String chrom);

    public void setChromEnd(long chromEnd);

    public void setChromStart(long chromStart);

    public String toHTMLString();

    /**
     *
     * @return
     */
    @Override
    public String toString();

    public boolean equalsByPos(Region r2);
    /**
     * compare regions by start and end
     */
    public static final Comparator<Region> compByPos = (Region r1, Region r2) -> {
        int rc = (int) (r1.getChromStart() - r2.getChromStart());
        if (rc >= 0) {
            long l1 = (r1.getChromStart() - r1.getChromEnd());
            long l2 = (r2.getChromStart() - r2.getChromEnd());

            return (int) (l1 - l2);
        } else {
            return rc;
        }
    };
    /**
     * compare regions by start
     */
    public static final Comparator<Region> compByStart
            = (Region r1, Region r2) -> (int) (r1.getChromStart() - r2.getChromStart());

    /**
     * compare regions by end
     */
    public static final Comparator<Region> compByEnd
            = (Region r1, Region r2) -> (int) (r1.getChromEnd() - r2.getChromEnd());
    /**
     * compare regions by chromosome order
     */
    public static final Comparator<Region> comChromStart = (Region r1, Region r2) -> {
        int chr1 = RegionLib.fromChrToInt(r1.getChrom());
        int chr2 = RegionLib.fromChrToInt(r2.getChrom());

        if (chr1 > chr2) {
            return 1;
        } else if (chr1 < chr2) {
            return -1;
        } else if (r1.getChromStart() > r2.getChromStart()) {
            return 1;
        } else if (r1.getChromStart() == r2.getChromStart()) {
            return 0;
        } else {
            return -1;
        }
    };

    public String getIconPath();
}
