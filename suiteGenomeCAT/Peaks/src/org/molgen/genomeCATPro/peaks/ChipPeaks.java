/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;
import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;
import org.molgen.genomeCATPro.datadb.dbentities.Track;

/**
 *
 * @author tebel
 */
public class ChipPeaks extends ChipFeature {

    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
    DecimalFormat myFormatter = null;

    ChipPeaks(boolean b) {
        super(b);
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        this.myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
    }

    ChipPeaks(Track track) {
        super();
        this.dataEntity = track;
        otherSymbols.setDecimalSeparator('.');
        //otherSymbols.setGroupingSeparator(',');
        this.myFormatter = new DecimalFormat("0.#####E0", otherSymbols);
    }

    public void addPeak(Aberration abCurrent, double qualityFactor) {

        double sumRatio = abCurrent.getRatio();
        abCurrent.setRatio((abCurrent.getCount() == 0 || sumRatio == 0 ) ? 0 : sumRatio / abCurrent.getCount());
        abCurrent.setQuality(new Double(myFormatter.format(abCurrent.getRatio() / ((qualityFactor == 0) ? 1 : qualityFactor))));
        this.addPeak(abCurrent);
    }

    public void addPeak(Aberration abCurrent) {
        if (!this.chrFeatures.containsKey(abCurrent.getChrom())) {

            this.chrFeatures.put(abCurrent.getChrom(), new Vector<Aberration>());
        //int chr = Utils.fromChrToInt(newBac.chrom)-1;
        }

        ((Vector<Aberration>) this.chrFeatures.get(abCurrent.getChrom())).add(abCurrent);
    }
}
