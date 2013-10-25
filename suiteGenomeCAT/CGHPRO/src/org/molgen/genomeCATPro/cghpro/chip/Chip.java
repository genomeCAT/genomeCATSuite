package org.molgen.genomeCATPro.cghpro.chip;

/** * @(#)Chip.java * * Copyright (c) 2004 by Wei Chen
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
import java.awt.Container;
import java.util.List;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 *Class Chip is a class to hold the data for the chip and methods to process these data.
 **/
public interface Chip {

    public Data getDataEntity();

    public void setDataEntity(Data d);

    /**
     * set inside the implementation class if somethingwent wrong  i.e. data loading 
     * @return
     */
    public boolean getError();

    public void setError(boolean b);

    /**
     * get link to data list
     * @return
     */
    public List<? extends Region> getData();

    public void setData(List<? extends Region> list);

    public List<? extends Region> getData(String chromId);

    /**
     * unique name for chip
     * @return
     */
    public String getName();

    /**
     * get genome release of data
     * @return
     */
    public GenomeRelease getRelease();

    /**
     *draw histogram for original ratios and normalized ratios in 2 different plots, then combine
     *them into 1 tabbedpane. If the chip remain not normalized, there will be only one plot.
     **/
    public void histogram(Container p);

    /**
     *draw scatterplot for original signal intensity and normalized signal intensity in 2 different plots, then combine
     *them into 1 tabbedpane. If the chip remain not normalized, there will be only one plot.
     **/
    public void scatterPlot(Container p);

    /**
     *draw boxplot for original ratios and normalized ratios in 2 different plots, then combine
     *them into 1 tabbedpane. If the chip remain not normalized, there will be only one plot.
     **/
    public void boxPlot(Container p);

    /**
     *draw QQplot for original ratios and normalized ratios in 2 different plots, then combine
     *them into 1 tabbedpane. If the chip remain not normalized, there will be only one plot.
     **/
    public void normalProbabilityPlot(Container p);

    /**
     *draw MAplot for original data and normalized data in 2 different plots, then combine
     *them into 1 tabbedpane. If the chip remain not normalized, there will be only one plot.
     **/
    public void maPlot(Container p);
}
