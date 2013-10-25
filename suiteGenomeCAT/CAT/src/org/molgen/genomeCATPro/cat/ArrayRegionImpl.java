package org.molgen.genomeCATPro.cat;
/**
 * @name ArrayRegionImpl
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
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.annotation.RegionImpl;

/**
 *
 * 010313   kt  return noe Geneview, noProbeView, trueRegionView, ratio,name,null
 */
public class ArrayRegionImpl extends RegionImpl implements RegionArray {

    public boolean hasGeneView() {
        return false;
    }

    public boolean hasRegionView() {
        return true;
    }

    public boolean hasProbeView() {
        return false;
    }

    public String getRatioColName() {
        return "ratio";
    }

    public String getGeneColName() {
        return null;
    }

    public String getProbeColName() {
        return "name";
    }
}
