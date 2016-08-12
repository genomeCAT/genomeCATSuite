package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name SpotXAnnoGene
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
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.ISpot;

/**
 * 100716 kt redesign spot with anno 050613 kt bug constructor ratio
 */
public class SpotXAnnoGene extends SpotXwoGeneAnno implements ISpot, RegionArray {

    public SpotXAnnoGene(int iid, String probeID, String probeName, String chrom, long start, long stop, String desc, double ratio, String anno) {
        super(iid, probeID, probeName, chrom, start, stop, desc, ratio, anno);
    }

    @Override
    public boolean hasGeneView() {
        return true;
    }

    @Override
    public String getGeneColName() {
        return Defaults.annoColName;
    }

}
