package org.molgen.genomeCATPro.data;

/**
 * @name FeatureWithSpotAnno
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
public class FeatureWithSpotAnno extends FeatureWithSpot {

    String annoValue = "";

    public FeatureWithSpotAnno(String id) {
        super(id);
    }

    public String getAnnoValue() {
        return annoValue;
    }

    public void setAnnoValue(String annoValue) {
        this.annoValue = annoValue;
    }

    /**
     * Add the spot to the vector spots, increment the size point spots to this
     * Feature/BAC
     *
     * @param spot spot to be added
     *
     */
    @Override
    public void addSpot(ISpot spot) {
        super.addSpot(spot);
        if (this.annoValue.contentEquals("") && DataService.hasValue(spot, "getAnnoValue")) {
            this.setAnnoValue(DataService.getValue(spot, "getAnnoValue").toString());
        }

    }

    /**
     *
     * @return
     */
    @Override
    public String toHTMLString() {
        return super.toHTMLString() + " " + this.getAnnoValue();
    }
}
