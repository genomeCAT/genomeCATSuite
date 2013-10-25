package org.molgen.genomeCATPro.data;
/**
 * @name SpotImpl2
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
import java.text.DecimalFormat;
import java.util.*;
import java.util.Vector;




public abstract class SpotImpl2 implements Spot {

    long iid;
    //The data from data File;
    /**ID of Feature where the spot belong*/
    private String id;
    // name of the spot
    private String name;
    /**base pair position of end*/
    private String chrom;
    private long chromStart;
    private long chromEnd;
    private boolean controlSpot = false;
    //dynamic
    /** ratio log2(cy3/cy5) or log2(cy5/cy3) if dyeswap */
    private double ratio = Double.NaN;
    
    final static DecimalFormat myFormatter = new DecimalFormat("0.###");
    /**
     *indicator if the spot is excluded or not
     *0 means not excluded
     *-1 means exlcuded because of negative signal intensity
     *@see chip.excludeByNegSignal
     *1 means excluded by other criteria
     *@see chip.excludeSpots(double f635, double f532), chip.excludeSpots(double std, double size, double lcr)
     *@see also chip.excludeSpotsBySource(String source), chip.excludeSpotsByComment(String comment)
     **/
    private int iExcluded = 0;

    public int getIfExcluded() {
        return iExcluded;
    }

    public boolean isExcluded() {
        if (this.getIfExcluded() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setIfExcluded(int iExcluded) {
        this.iExcluded = iExcluded;
    }
    /**
     *indictor if the spot is aberrant or not,
     *zero means normal, positive means gain and negative means loss
     **/
    private int iAberrant = 0;

    public SpotImpl2() {
    }

    public long getIid() {
        return iid;
    }

    public void setIid(long iid) {
        this.iid = iid;
    }

    /**
     * 
     * Global normalization  by a constant factor, 
     * i.e. R  = kG, 
     * center of the distribution of log ratios is shifted to zero
     * log2R/G → log2R/G – c = log2R/(kG)
     * c = log2k 
     * @param log2Factor
     */
    public boolean isControlSpot() {
        return controlSpot;
    }

    public void setControlSpot(boolean controlSpot) {
        this.controlSpot = controlSpot;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRatio() {
        return this.ratio;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    public String getChrom() {
        return this.chrom;
    }

    public long getChromStart() {
        return chromStart;
    }

    public void setChromStart(long chromStart) {
        this.chromStart = chromStart;
    }

    public long getChromEnd() {
        return chromEnd;
    }

    public void setChromEnd(long chromEnd) {
        this.chromEnd = chromEnd;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public void addTo(List<? extends Spot> list) {
        this._addTo((List<SpotImpl2>) list);
    }

    void _addTo(List<? super SpotImpl2> list) {
        list.add(this);
    }

    public List<? extends Spot> getVector(List<? extends Spot> v) {

        return this._getVector((List<SpotImpl2>) v);
    }

    public List<? extends Spot> _getVector(List<SpotImpl2> v) {
        List<SpotImpl2> l = new Vector<SpotImpl2>(v);
        Collections.copy(l, v);
        return l;
    }

    public List<? extends Spot> getVector() {

        return this._getVector();
    }

    public Vector<? extends Spot> _getVector() {
        Vector<? extends Spot> v = new Vector<SpotImpl2>();

        return v;
    }

    public boolean isAberrant() {
        if (this.iAberrant == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setIfAberrant(int i) {
        this.iAberrant = i;
    }

    public int getIfAberrant() {
        return this.iAberrant;
    }

    public String toHTMLString() {
        return new String(getName() + " " + SpotImpl2.myFormatter.format(getRatio()) + " ");
    }

    public int compareTo(Object o) {
        Spot r;
        if (o instanceof Spot) {
            r = (Spot) o;
        } else {
            throw new java.lang.RuntimeException("No Spot");
        }
        return Double.compare(this.getRatio(), r.getRatio());
    }
    public final static String ICON_PATH = "org/molgen/genomeCATPro/guimodul/data/page_array_16.png";

    public String getIconPath() {
        return ICON_PATH;
    }

    public String toString() {
        return new String(getChrom() + ":" + getChromStart() + "-" + getChromEnd());
    }

    
}
