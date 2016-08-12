package org.molgen.genomeCATPro.data;

/**
 * @name Spot
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
import java.util.*;

public interface ISpot extends IFeature {

    public String getSQLtoPlattform(String tablePlatform, String tableData);

    public void scaleByFactor(double c, boolean dyeswap);

    public int getIfExcluded();

    public boolean isExcluded();

    public void setIfExcluded(int iExcluded);

    public List<? extends ISpot> getVector(List<? extends ISpot> v);

    public List<? extends ISpot> getVector();

    public void addTo(List<? extends ISpot> list);

    public boolean isControlSpot();

    public void setControlSpot(boolean controlSpot);

    public String getName();

    public void setName(String name);
    /**
     * comparator used to sort spots according to id
     */
    public static final Comparator<ISpot> comId = new Comparator<ISpot>() {

        public int compare(ISpot r1, ISpot r2) {

            if (r1.getId().compareTo(r2.getId()) > 0) {
                return 1;
            } else if (r1.getId().compareTo(r2.getId()) == 0) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**
     * comparator used to sort spots according to original ratio
     */
    public static final Comparator<ISpot> comLog2Ratio = new Comparator<ISpot>() {

        public int compare(ISpot r1, ISpot r2) {

            if (r1.getRatio() > r2.getRatio()) {
                return 1;
            } else if (r1.getRatio() == r2.getRatio()) {
                return 0;
            } else {
                return -1;
            }

        }
    };

}
