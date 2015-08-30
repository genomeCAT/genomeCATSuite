package org.molgen.genomeCATPro.selector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Vector;
import org.molgen.genomeCATPro.annotation.RegionImpl;
import org.molgen.genomeCATPro.common.Univariate;

/**
 * @name SelectorRegion
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
public class SelectorRegion extends RegionImpl {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private List<Double> values;
    Univariate statValues;
    BufferedImage icon;
    public static int lengthImage = 100;
    public static int colsIcon = 10;
    public static double min = -4;
    public static double max = 4;
    public static double threshold_gain = 0.4;
    public static double threshold_loss = -0.4;

    public SelectorRegion(String name, String chrom, Long start, Long stop, List<Double> v) {
        super(name, chrom, start, stop);


        icon = new BufferedImage(SelectorRegion.lengthImage,
                20,
                BufferedImage.TYPE_INT_ARGB_PRE);
        this.setValues(v);
    }

    public double getValue() {

        return statValues.mean();
    }

    public void setValue() {
        changeSupport.firePropertyChange("value", 0, this.getValue());
    }

    public void setStddev() {
        changeSupport.firePropertyChange("stddev", 0, this.getStddev());
    }

    public double getStddev() {

        return statValues.stdev();
    }

    public List<Double> getValues() {
        return this.values;
    }

    public void setValues(List<Double> values) {
        List<Double> old = this.values;
        if (this.values != null) {
            this.values.clear();
        } else {
            this.values = new Vector<Double>();
        }
        this.values.addAll(values);
        this.statValues = new Univariate(
                this.values.toArray(new Double[this.values.size()]));


        // dummies for property changed calling
        this.setValue();
        this.setStddev();
        this.setImage();
    //

    //changeSupport.firePropertyChange("values", old, values);

    }

    public Color getColor() {
        return SelectorRegion.getColor(this.getValue());
    }

    public void setColor() {
    }

    public void setImage() {
        Image old = this.icon;
        changeSupport.firePropertyChange("image", old, this.getImage());

    }

    public Image getImage() {
        SelectorRegion.paintImage(this.icon, values);
        return this.icon;
    }

    static void paintImage(BufferedImage img, List<Double> values) {
        if (img == null) {
            return;
        }
        Graphics2D g = img.createGraphics();

        int x = 0;
        int width = SelectorRegion.lengthImage / SelectorRegion.colsIcon;
        Color c = null;
        for (Double d : values) {

            c = SelectorRegion.getColor(d.doubleValue());
            // print farbscale
            g.setColor(c);

            //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)-y));
            g.fillRect(x, 0, width, img.getHeight());
            x += width;

        //g.fillRect(0, (Defines.ARRAY_HEIGTH / 2) + y, 10, 1);
        //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)+y));
        }
    }

    public void setImage(Image icon) {
        this.icon = (BufferedImage) icon;
    }

    static Color getColor(double v) {
        // v im Bereich von min max
        if (v == 0.0) {
            return Color.WHITE;
        }
        if (v > SelectorRegion.threshold_loss && v < SelectorRegion.threshold_gain) {
            return Color.lightGray;
        }
        if (v < SelectorRegion.min) {
            return Color.RED;
        }
        if (v > SelectorRegion.max) {
            return Color.GREEN;
        }
        int factor = 0;
        if (v >= SelectorRegion.threshold_gain) {
            factor = (int) (((max - v) / (max - threshold_gain)) * 100);
            return new Color(0, 255 - factor, 0);
        }
        if (v <= SelectorRegion.threshold_loss) {
            factor = (int) (((min - v) / (min - threshold_loss)) * 100);

            return new Color(255 - factor, 0, 0);
        }
        return Color.white;
    }

    public static void setMax(double max) {
        SelectorRegion.max = max;
    }

    public static void setMin(double min) {
        SelectorRegion.min = min;
    }

    public static double getMax() {
        return max;
    }

    public static double getMin() {
        return min;
    }

    public static double getThreshold_loss() {
        return threshold_loss;
    }

    public static void setThreshold_loss(double threshold_loss) {
        SelectorRegion.threshold_loss = threshold_loss;
    }

    public static int getColsIcon() {
        return colsIcon;
    }

    public static void setColsIcon(int colsIcon) {
        SelectorRegion.colsIcon = colsIcon;
    }

    public static int getLengthImage() {
        return lengthImage;
    }

    public static void setLengthImage(int lengthIcon) {
        SelectorRegion.lengthImage = lengthIcon;
    }

    public static double getThreshold_gain() {
        return threshold_gain;
    }

    public static void setThreshold_gain(double threshold_gain) {
        SelectorRegion.threshold_gain = threshold_gain;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
