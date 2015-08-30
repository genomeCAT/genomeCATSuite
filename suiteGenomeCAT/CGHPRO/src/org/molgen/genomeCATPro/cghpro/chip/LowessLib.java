package org.molgen.genomeCATPro.cghpro.chip;

import java.util.Arrays;
import org.molgen.genomeCATPro.data.Feature;
import org.molgen.genomeCATPro.data.OriginalSpot;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.LowessAlgorithm;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.data.INormalize;
import org.molgen.genomeCATPro.data.Spot;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;

/** * @(#)LowessLib.java * * Copyright (c) 2004 by Wei Chen
 * * @author Wei Chen
 * * Email: wei@molgen.mpg.de
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License 
 * * as published by the Free Software Foundation; either version 2 
 * * of the License, or (at your option) any later version, 
 * * provided that any use properly credits the author. 
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * * */
/**
 *Class LowessLib is a class holding static methods to
 *normalize the chip by Lowess or Subgrid Lowess based on
 *Lowess Algorithm
 *@see cgh.util.LowessAlgorithm
 **/
/**
 * 170712   normalize set Channel Intensities
 * @author tebel
 */
public class LowessLib implements INormalize {

    /**
     *calculate the lowess fit for data
     *@param x[] an array of X coordinates
     *@param y[] an array of Y coordinates
     *@param f the fraction used to fit the value
     *@return the array of fit values
     **/
    private static float[] calculateLowess(float x[], float y[], float f) {
        Object result[] = LowessAlgorithm.lowess(x, y, x.length, f, 3, 0.0F);
        float y_fit[] = (float[]) result[0];
        return y_fit;
    }

    /**
     *normalize a vector of spots based on Lowess (original ratio - fit value)
     *@param spots spots to be normalized
     *@return the array of fit values
     **/
    private static List<? extends Spot> normalize(List<? extends OriginalSpot> spots,
            boolean dyeswap, float f) {

        float[] x_in = new float[spots.size()];
        float[] y_in = new float[spots.size()];

        Collections.sort(spots, OriginalSpot.comMvalue);
        int i = 0;
        for (OriginalSpot currentSpot : spots) {

            x_in[i] = (float) (currentSpot.getLog2Cy3() + currentSpot.getLog2Cy5());
            y_in[i] = (float) (currentSpot.getLog2Cy3() - currentSpot.getLog2Cy5());
            //x_in[i] = (float)(currentSpot.f532+currentSpot.f635);
            //y_in[i] =(float) (currentSpot.f532-currentSpot.f635);

            i++;


        }

        float[] ys = calculateLowess(x_in, y_in, f);
        List _normalized = Arrays.asList(new Object[spots.size()]);
        Collections.copy(_normalized, spots);
        Collections.sort(_normalized, OriginalSpot.comMvalue);
        i = 0;
        for (OriginalSpot currentSpot : (List<? extends OriginalSpot>) _normalized) {
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
     *normalize a chip based on Lowess
     *@param chip chip to be normalized
     **/
    /*
    public static void normalizeByGlobal(ChipSpot chip) {
    
    normalize(chip., 0.2f);
    
    
    // kt new chipBac.recalculate chip.addBacsWithAverageRatio();
    // kt new chip.setIfNormalize(true);
    
    
    
    }
     */
    /**
     *normalize a chip based on Subgrid Lowess
     *@param chip chip to be normalized
     **/
    public void normalizeBySubgrid(ChipBlock chip, boolean dyeswap) {
        for (Integer id : chip.blocks.keySet()) {
            List<? extends Spot> spots = chip.blocks.get(id);
            if (spots.size() >= 1) {
                chip.setBlock(id, normalize((List<OriginalSpot>) spots, dyeswap, 0.3f));
            }
        }
    }

    public void normalize(Data data, List<? extends Feature> datalist) {
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
            bchip.dataFromSpots((List<? extends Spot>) c.getSpots());
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
