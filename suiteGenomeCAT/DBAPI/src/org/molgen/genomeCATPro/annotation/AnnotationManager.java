/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.annotation;

import java.awt.Graphics2D;
import java.util.List;
import org.molgen.genomeCATPro.datadb.dbentities.AnnotationList;

/**
 *
 * @author tebel
 */
public interface AnnotationManager {

    public String getNameId();

    public AnnotationList getAnnotation();

    public List<? extends RegionAnnotation> getData(String chromId);

    //public float getTransparency(GenomeRelease release, String name);

    //public void setTransparency();

    //public Color getColor(GenomeRelease release, String name);

    //public void setColor(Color c);
    //  public String getColorDesc(GenomeRelease release, String name);

    //public String getColorDesc(AnnotationList anno);
    public int getMinSpotHeight();

    public void setMinSpotHeight(int x);

    public void plot(Graphics2D g,
            String chromId,
            int left, int top,
            int width, double scale);

    public void plot(Graphics2D g,
            String chromId, boolean fullChrom, long start, long stop,
            int left, int top,
            int width, double scale);

    public RegionAnnotation getFirst(String chromId);

    public RegionAnnotation getLast(String chromId);

    public RegionAnnotation getDataAtPos( String chrom, Long pos, Long dist);
}
