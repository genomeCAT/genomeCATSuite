/**@name ScoreFilterManager
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * Copyright  2009 Katrin Tebel
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
package org.molgen.genomeCATPro.peaks.cnvcat;



import org.molgen.genomeCATPro.peaks.Aberration;
import org.molgen.genomeCATPro.peaks.CNVCATPropertiesMod;

/**
 *
 * provide methods to filter and set transparency for cnv
 * override for each edition 
 */
public class ScoreFilterManager {

    public boolean doHide(Aberration a) {
        boolean hide = true;
        if (Math.abs(a.getRatio()) >= CNVCATPropertiesMod.props().getMinRatio()) {
            if (Math.abs(a.getQuality()) >= CNVCATPropertiesMod.props().getMinQuality()) {
                hide = false;
            }
        }
        a.setHidden(hide);
        return hide;
    }

    public float getAlphaByQuality(Aberration a, double maxQuality) {
        float alpha = (float) 0.0;
        alpha = (float) (a.getQuality() / Math.max(maxQuality, CNVCATPropertiesMod.props().getMaxQuality()));
        return scaleAlpha(alpha);
    }

    public float getAlphaByRatio(Aberration a, double maxRatio) {
        float alpha = (float) 0.0;
        alpha = (float) ((a.getRatio()) / Math.max(maxRatio, CNVCATPropertiesMod.props().getMaxRatio()));
        return scaleAlpha(alpha);
    }

    protected float scaleAlpha(float alpha) {

        alpha =  (alpha < 0 ? alpha * -1 : alpha);   // not less 0

        alpha = (float) (alpha > 1.0 ? 1.0 : alpha);        // not greater than 1

        if (alpha > 0) {                                     // scale within opacity range defined by user

            alpha = (float) (CNVCATPropertiesMod.props().getTransAberrations() +
                    (alpha * (1 - CNVCATPropertiesMod.props().getTransAberrations())));
        }
        return alpha;
    }

    float getAlpha(Aberration currentAberration) {
        return scaleAlpha((float) 1.0);
    }
}
