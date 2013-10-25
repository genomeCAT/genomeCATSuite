package org.molgen.genomeCATPro.cghpro.chip;
/**
 * @name FeatureCBS
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
import org.molgen.genomeCATPro.data.FeatureImpl;


public class FeatureCBS extends FeatureImpl {

    public final static String ICON_PATH_CBS = "org/molgen/genomeCATPro/cghpro/chip/cbs16.gif";

    @Override
    public String getIconPath() {
        return ICON_PATH_CBS;
    }
}
