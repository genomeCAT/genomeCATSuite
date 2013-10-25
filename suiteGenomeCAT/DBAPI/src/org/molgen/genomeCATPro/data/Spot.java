package org.molgen.genomeCATPro.data;

/**
 * @name Spot
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
import java.util.*;



public interface Spot extends Feature {

    /**
     * 
     * Global normalization  by a constant factor, 
     * i.e. R  = kG, 
     * center of the distribution of log ratios is shifted to zero
     * log2R/G → log2R/G – c = log2R/(kG)
     * c = log2k 
     * @param log2Factor
     */
    /**
     * currentSpot.setF635Norm(currentSpot.f635);
     * currentSpot.setF532Norm(currentSpot.f532 - normalValue);
     * currentSpot.setNormalRatio(currentSpot.f532Norm - currentSpot.f635Norm)
     */
    public void scaleByFactor(double c, boolean dyeswap);

    public int getIfExcluded();

    public boolean isExcluded();

    public void setIfExcluded(int iExcluded);

    public List<? extends Spot> getVector(List<? extends Spot> v);

    public List<? extends Spot> getVector();

    public String getSQLtoPlattform(String tablePlatform, String tableData);
    
    public void addTo(List<? extends Spot> list);

   
    public boolean isControlSpot();

    public void setControlSpot(boolean controlSpot);

    public String getName();

    public void setName(String name);
    /**comparator used to sort spots according to id*/
    public static final Comparator<Spot> comId = new Comparator<Spot>() {

        public int compare(Spot r1, Spot r2) {

            if (r1.getId().compareTo(r2.getId()) > 0) {
                return 1;
            } else if (r1.getId().compareTo(r2.getId()) == 0) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**comparator used to sort spots according to original ratio*/
    public static final Comparator<Spot> comLog2Ratio = new Comparator<Spot>() {

        public int compare(Spot r1, Spot r2) {

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