package org.molgen.genomeCATPro.data;

/**
 * @name FeatureWithSpot
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
import org.molgen.genomeCATPro.common.MyMath;

/**
 * 
 * class containing data from experiment data
 * provides summarized logRatios for replicate spots 
 * 
 * 
 */
public class FeatureWithSpot extends FeatureImpl {

    /**
     * vector holding the all spots that belong to this feature
     */
    public List<? extends ISpot> spots = null;
    /**
     * Variance betweeen replicate spots
     */
    public double variance;
    /**
     * Standard deviation between replicate spots
     */
    public double std;

    /**
     * Default Constructor
     *
     */
    public FeatureWithSpot() {
        super();
    }

    public FeatureWithSpot(String id) {
        this.id = id;
    }

    /**
     * Add the spot to the vector spots, increment the size point spots to this
     * Feature/BAC
     *
     * @param spot spot to be added
     *
     */
    public void addSpot(ISpot spot) {
        if (spot.isExcluded()) {
            return;
        }
        if (this.spots == null) {
            this.spots = spot.getVector();
            //spot.setFeature(this);
        }

        if (this.chrom != null && this.chrom.compareToIgnoreCase(spot.getChrom()) != 0) {
            throw new RuntimeException("feature cannot include spots on different chroms");
        }
        if (this.chrom == null) {
            this.chrom = spot.getChrom();
        }

        spot.addTo(this.spots);
        if (this.chromStart == -1 || this.chromStart > spot.getChromStart()) {
            this.setChromStart(spot.getChromStart());
        }
        if (this.chromEnd < spot.getChromEnd()) {
            this.chromEnd = spot.getChromEnd();
        }
        calculateRatio();
    }

    /**
     * Calculate and set the ratio, variance, std after the a spot is added to
     * the vector spots, ratios and variance are formated to 3 decimal places
     *
     */
    public void calculateRatio() {

        double sum = 0;
        double sumSquare = 0;

        int size = this.spots.size();
        for (ISpot currentSpot : this.spots) {
            sum += currentSpot.getRatio();
            sumSquare += currentSpot.getRatio() * currentSpot.getRatio();
        }

        this.ratio = sum / size;
        this.variance = (sumSquare / size - ratio * ratio) > 0 ? MyMath.formatDoubleValue((sumSquare / size - ratio * ratio), 3) : 0;
        this.std = MyMath.formatDoubleValue(Math.sqrt(variance), 3);
        this.ratio = MyMath.formatDoubleValue(ratio, 3);

    }
}
