package org.molgen.genomeCATPro.cghpro.chip;

import org.molgen.genomeCATPro.common.Defaults;

/**
 * @name SpotBACAnnoGene
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
public class SpotBACAnnoGene extends SpotXAnno {

    @Override
    public String getGeneColName() {
        return Defaults.annoColName;
    }

    @Override
    public boolean hasGeneView() {
        return true;
    }

    public SpotBACAnnoGene() {
        super();
    }

    SpotBACAnnoGene(
            int id, String probeId, String name,
            int block, int row, int col,
            String chrom, long start, long stop,
            int control, int ifExcluded,
            double f635Mean, double b635Mean, double b635sd,
            double f532Mean, double b532Mean, double b532sd,
            double snr635, double snr532, double f635, double f532, double ratio, String anno) {
        super(id, probeId, name, block, row, col, chrom, start, stop, control,
                ifExcluded, f635Mean, b635Mean, b635sd, f532Mean, b532Mean, b532sd, snr635, snr532,
                f635, f532, ratio, anno);
    }
}
