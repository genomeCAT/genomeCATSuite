package org.molgen.genomeCATPro.cghpro.chip;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.LowessAlgorithm;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.data.INormalize;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.ISpot;
import org.molgen.genomeCATPro.data.IOriginalSpot;

/**
 * @(#)LowessLib.java * * Copyright (c) 2004 by Wei Chen
 * @author Wei Chen * Email: wei@molgen.mpg.de
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
/**
 * Class LowessLib is a class holding static methods to normalize the chip by
 * Lowess or Subgrid Lowess based on Lowess Algorithm
 *
 * @see cgh.util.LowessAlgorithm
 *
 */


/**
 * 170712 normalize set Channel Intensities
 *
 * 
 */
public class LowessLib implements INormalize {

    /**
     * calculate the lowess fit for data
     *
     * @param x[] an array of X coordinates
     * @param y[] an array of Y coordinates
     * @param f the fraction used to fit the value
     * @return the array of fit values
     *
     */
    private static float[] calculateLowess(float x[], float y[], float f) {
        Object result[] = LowessAlgorithm.lowess(x, y, x.length, f, 3, 0.0F);
        float y_fit[] = (float[]) result[0];
        return y_fit;
    }

    /**
     * normalize a vector of spots based on Lowess (original ratio - fit value)
     *
     * @param spots spots to be normalized
     * @return the array of fit values
     *
     */
    private static List<? extends ISpot> normalize(List<? extends IOriginalSpot> spots,
            boolean dyeswap, float f) {

        float[] x_in = new float[spots.size()];
        float[] y_in = new float[spots.size()];

        Collections.sort(spots, IOriginalSpot.comMvalue);
        int i = 0;
        for (IOriginalSpot currentSpot : spots) {

            x_in[i] = (float) (currentSpot.getLog2Cy3() + currentSpot.getLog2Cy5());
            y_in[i] = (float) (currentSpot.getLog2Cy3() - currentSpot.getLog2Cy5());
            //x_in[i] = (float)(currentSpot.f532+currentSpot.f635);
            //y_in[i] =(float) (currentSpot.f532-currentSpot.f635);

            i++;

        }

        float[] ys = calculateLowess(x_in, y_in, f);
        List _normalized = Arrays.asList(new Object[spots.size()]);
        Collections.copy(_normalized, spots);
        Collections.sort(_normalized, IOriginalSpot.comMvalue);
        i = 0;
        for (IOriginalSpot currentSpot : (List<? extends IOriginalSpot>) _normalized) {
            currentSpot.scaleByFactor(ys[i], dyeswap);

            if (dyeswap) {
                currentSpot.setLog2Cy5(currentSpot.getLog2Cy5() - ys[i]);
            } else {
                currentSpot.setLog2Cy3(currentSpot.getLog2Cy3() - ys[i]);
            }
            i++;
            //currentSpot.setF532Norm(currentSpot.getF532() - ys[i]);
            //currentSpot.setF635Norm(currentSpot.getF635());
            //currentSpot.setNormalRatio(currentSpot.f532Norm - currentSpot.f635Norm);
        }
        return _normalized;
    }

    /**
     * normalize a chip based on Lowess
     *
     * @param chip chip to be normalized
     *
     */
    /*
    public static void normalizeByGlobal(ChipSpot chip) {
    
    normalize(chip., 0.2f);
    
    
    // kt new chipBac.recalculate chip.addBacsWithAverageRatio();
    // kt new chip.setIfNormalize(true);
    
    
    
    }
     */
    /**
     * normalize a chip based on Subgrid Lowess
     *
     * @param chip chip to be normalized
     *
     */
    public void normalizeBySubgrid(ChipBlock chip, boolean dyeswap) {
        for (Integer id : chip.blocks.keySet()) {
            List<? extends ISpot> spots = chip.blocks.get(id);
            if (spots.size() >= 1) {
                chip.setBlock(id, normalize((List<IOriginalSpot>) spots, dyeswap, 0.3f));
            }
        }
    }

    public void normalize(Data data, List<? extends IFeature> datalist) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static String methodName = "SubGrid Lowess";

    public String getMethodName() {
        return methodName;
    }

    /*
     * normalisieren
     * chip block erstellen
     * speichern
     */
    public void normalize(Data data) throws Exception {

        try {

            if (!(data instanceof ExperimentData)) {
                Logger.getLogger(LowessLib.class.getName()).log(Level.INFO, this.getMethodName() + " normalization only for experiment data");
                return;
            }
            ChipSpot c = (ChipSpot) ChipImpl.loadChipFromDB(ChipSpot.class, data);

            if (c.getSpots().size() <= 0) {
                Logger.getLogger(LowessLib.class.getName()).log(Level.INFO, this.getMethodName() + " no data");
                return;
            }
            /*if (!(datalist.get(0) instanceof Spot)) {
            Logger.getLogger(LowessLib.class.getName()).log(Level.INFO, this.getMethodName() + " normalization only for spot data");
            return;
            }*/
            // copy experiment entity
            ExperimentData newData = new ExperimentData();
            newData.setId(null);
            newData.setClazz(data.getClazz());
            newData.setDataType(Defaults.DataType.NORMALIZED);

            newData.setGenomeRelease(Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));
            //newData.setParent(data);
            newData.setOwner(ExperimentService.getUser());

            newData.setExperiment(((ExperimentData) data).getExperiment());
            newData.setProcProcessing(this.getMethodName());
            newData.setParamProcessing("");

            ((ExperimentData) newData).setPlatformdata(((ExperimentData) data).getPlatformdata());

            newData.setName(Utils.getUniquableName(data.getName()));
            data.addChildData(newData);

            // Collections.copy(newList, c.getSpots());
            ChipBlock bchip = new ChipBlock(newData);
            bchip.dataFromSpots((List<? extends ISpot>) c.getSpots());
            boolean dyeswap = (((ExperimentData) data).getParamProcessing() != null ? ((ExperimentData) data).getParamProcessing().contains(Defaults.DYESWAP) : false);

            this.normalizeBySubgrid(bchip,
                    dyeswap);
            bchip.saveExperimentToDB();
            data.addChildData(newData);
        } catch (Exception ex) {
            Logger.getLogger(LowessLib.class.getName()).log(
                    Level.SEVERE, LowessLib.class.getName(), ex);
            throw ex;

        }

    }
    // chipbac recalculate chip.addBacsWithAverageRatio();
    // kt new chip.setIfNormalize(true);
}
