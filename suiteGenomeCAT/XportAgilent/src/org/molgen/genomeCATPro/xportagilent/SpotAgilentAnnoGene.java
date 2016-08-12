package org.molgen.genomeCATPro.xportagilent;

import org.molgen.genomeCATPro.common.Defaults;

/**
 * @name SpotAgilentwoGeneAnno
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
public class SpotAgilentAnnoGene extends SpotAgilentwoGeneAnno {

    @Override
    public boolean hasGeneView() {
        return true;
    }

    @Override
    public String getGeneColName() {
        return Defaults.annoColName;
    }

    public SpotAgilentAnnoGene() {
        super();
    }

    SpotAgilentAnnoGene(
            int iid,
            int probeID,
            String probeName,
            String chrom,
            long chromStart,
            long chromEnd,
            boolean controlType,
            String DESCRIPTION,
            String SystematicName,
            double rSignal,
            double gSignal,
            double rgRatio10,
            double rgRatio10PValue,
            double ratio,
            String anno) {

        super(iid, probeID, probeName, chrom, chromStart, chromEnd, controlType, DESCRIPTION,
                SystematicName, rSignal, gSignal, rgRatio10, rgRatio10PValue, ratio, anno);

    }

    @Override
    boolean hasAnnoValue() {
        return true;
    }

    @Override
    public String getAnnoValue() {
        return annoValue;
    }

    @Override
    public void setAnnoValue(String annoValue) {
        this.annoValue = annoValue;
    }

    @Override
    public String toHTMLString() {
        return (super.toHTMLString() + this.getAnnoValue() + " ");
    }

}
