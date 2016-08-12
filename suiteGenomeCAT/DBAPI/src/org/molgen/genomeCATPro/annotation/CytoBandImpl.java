package org.molgen.genomeCATPro.annotation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * * @(#)CytoBand.java * * Copyright (c) 2004 by Wei Chen
 * * @author Wei Chen * Email: wei@molgen.mpg.de * This program is free
 * software; you can redistribute it and/or * modify it under the terms of the
 * GNU General Public License * as published by the Free Software Foundation;
 * either version 2 * of the License, or (at your option) any later version, *
 * provided that any use properly credits the author. * This program is
 * distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY;
 * without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the * GNU General Public License for more details at
 * http://www.gnu.org * *
 */
/**
 * Class CytoBand is class holding the description of cytoband, chrom, the name
 * of chromosome chromStart, the base pair position of start chromEnd, the base
 * pair position of end name, the name of the cytoband gieStain, the giestain
 * status of the cytoband
 *
 *
 */
public class CytoBandImpl implements CytoBand {

    public String chrom;
    public long chromStart;
    public long chromEnd;
    public String name;
    public String gieStain;

    public CytoBandImpl() {
    }

    /**
     * Constructor.
     *
     */
    public CytoBandImpl(String chrom, long chromStart,
            long chromEnd, String name, String gieStain) {

        this.chrom = chrom;
        this.name = name;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
        this.gieStain = gieStain;
    }

    public CytoBandImpl(String chrom, long chromStart) {
        this.chrom = chrom;
        this.chromStart = chromStart;
    }

    public String getChrom() {
        return this.chrom;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    public long getChromStart() {
        return this.chromStart;
    }

    public void setChromStart(long chromStart) {
        this.chromStart = chromStart;
    }

    public long getChromEnd() {
        return this.chromEnd;
    }

    public void setChromEnd(long chromEnd) {
        this.chromEnd = chromEnd;
    }

    public String getGieStain() {
        return this.gieStain;
    }

    public String getName() {
        return this.name;
    }

    public void setGieStain(String gieStain) {
        this.gieStain = gieStain;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toHTMLString() {
        return new String(getName());
    }

    public String toFullString() {
        return new String(this.getName() + " " + this.toString());
    }

    public String getIconPath() {
        return RegionImpl.ICON_PATH;
    }

    public String getColorDesc() {
        return new String("<html>"
                + "white: giestain negative band</br>"
                + "light gray to black: giestain positive band</html>");
    }

    public Color getColor() {

        if (this.getGieStain().contentEquals("gneg")) {
            return Color.WHITE;
        }
        if (this.getGieStain().contentEquals("gpos25")) {
            return Color.LIGHT_GRAY;
        }
        if (this.getGieStain().contentEquals("gpos50")) {
            return Color.GRAY;
        }
        if (this.getGieStain().contentEquals("gpos75")) {
            return Color.DARK_GRAY;
        }
        if (this.getGieStain().contentEquals("gpos100")) {
            return Color.BLACK;
        }
        return Color.black;

    }

    public long getBin() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBin(long bin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException {
        return CytoBandManagerImpl.dbLoadRegions(table, chromId);
    }

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getGeneColName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getProbeColName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean equalsByPos(Region r2) {
        return (this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd());

    }

    @Override
    public int compareTo(Object o) {
        CytoBandImpl c;
        if (o instanceof CytoBandImpl) {
            c = (CytoBandImpl) o;
        } else {
            throw new java.lang.RuntimeException("No CytoBandImpl");
        }

        if (this.getChromEnd() > c.getChromEnd()) {
            return 1;
        } else if (this.getChromEnd() < c.getChromEnd()) {
            return -1;
        } else {
            return 0;
        }
    }

    public Image getColorImage() {
        if (icon == null) {
            paintImage();
        }
        return icon;
    }
    //
    static BufferedImage icon = null;

    static void paintImage() {
        Logger.getLogger(CytoBandImpl.class.getName()).log(Level.INFO,
                "paintImage");
        icon = new BufferedImage(RegionAnnotation.lengthImage,
                RegionAnnotation.heightImage,
                BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = icon.createGraphics();

        Color values[] = new Color[]{
            Color.WHITE, Color.LIGHT_GRAY,
            Color.GRAY, Color.DARK_GRAY, Color.BLACK
        };
        int width = RegionAnnotation.lengthImage / values.length;
        Color c = null;
        int x = 0;
        for (Color d : values) {

            // print farbscale
            g.setColor(d);

            //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)-y));
            g.fillRect(x, 0, width, icon.getHeight());
            x += width;

            //g.fillRect(0, (Defines.ARRAY_HEIGTH / 2) + y, 10, 1);
            //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)+y));
        }
    }

    @Override
    public String toString() {
        return new String(getChrom() + ":" + getChromStart() + "-" + getChromEnd());
    }
}
