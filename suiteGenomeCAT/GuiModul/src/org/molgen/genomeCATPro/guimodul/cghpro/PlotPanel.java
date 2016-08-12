/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.cghpro;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 *
 * @author tebel
 */
public interface PlotPanel {

    public void repaint();

    public void rescale();

    public void setXZoom(double x);

    public void setYZoom(double y);

    public void updatePlot(String chrom);

    public void refresh();

    public void setFirstPos(long y);

    public void setSecondPos(long y);

    public Dimension getSize();

    public boolean isShowDetailFrame();

    public void setShowDetailFrame(boolean showDetailFrame);

    public BufferedImage getImage();
}
