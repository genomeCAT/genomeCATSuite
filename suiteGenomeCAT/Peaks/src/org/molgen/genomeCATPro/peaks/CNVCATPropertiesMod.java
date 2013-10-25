package org.molgen.genomeCATPro.peaks;
/**
 * @name CNVCATProperties
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>.
 * The contents of props file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use props file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * props program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.awt.Color;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.modules.InstalledFileLocator;

/**
 * 
 * 101012   kt  valide setMinHeight,setGap
 */
public class CNVCATPropertiesMod implements Serializable {

    static CNVCATProperties props = null;
    transient static String stfile = "cnvcatprops.xml";

    public static CNVCATProperties props() {
        if (CNVCATPropertiesMod.props == null) {
            CNVCATPropertiesMod.props = load();
        }
        if (CNVCATPropertiesMod.props == null) {
            create();
        }
        return CNVCATPropertiesMod.props;
    }

    private static CNVCATProperties load() {

        try {
            Logger.getLogger(CNVCATPropertiesMod.class.getName()).log(
                    Level.INFO, "load properties from " + stfile);
            File f = InstalledFileLocator.getDefault().locate(
                    // CorePropertiesMod.stfile, "org.molgen.genomeCATPro.guimodul", false);
                    CNVCATPropertiesMod.stfile, null, false);
            XMLDecoder d = new XMLDecoder(
                    new BufferedInputStream(
                    new FileInputStream(f)));
            CNVCATProperties _props = (CNVCATProperties) d.readObject();
            d.close();
            return _props;
        } catch (Exception ex) {
            Logger.getLogger(CNVCATPropertiesMod.class.getName()).log(
                    Level.SEVERE, "error ", ex);
            return null;
        }
    }

    @SuppressWarnings("empty-statement")
    public static void create() {
        Logger.getLogger(CNVCATProperties.class.getName()).log(Level.SEVERE,
                "CNVCATProperties created");
        CNVCATPropertiesMod.props = new CNVCATProperties();

        //props.setMrnetAbr("BE");
        props.setProbeWidth(4);
        props.setMaxRatio(2.0);
        props.setMinRatio(0);
        props.setMaxQuality(2.0);
        props.setMinQuality(0.0);
        props.setSelectionTolerance(10);
        props.setColorOverlap(Color.BLACK);
        props.setColorBackGround(Color.BLACK);
        props.setColorSelected(Color.BLACK);
        props.setTransVariations(0.2);
        props.setTransAberrations(0.2);
        props.setTransFrequencies(0.8);
        props.setGap(10);
        props.setNofCols(20);
        props.setMinHeight(1);


    /*
    try {
    save();
    
    } catch (Exception ex) {
    Logger.getLogger(CNVCATPropertiesMod.class.getName()).log(
    Level.SEVERE, "error ", ex);
    }
     */
    }

    public static void save() throws FileNotFoundException {
        Logger.getLogger(CNVCATProperties.class.getName()).log(
                Level.INFO, "save CNVCATProperties");
        File f = InstalledFileLocator.getDefault().locate(
                // CorePropertiesMod.stfile, "org.molgen.genomeCATPro.guimodul", false);
                CNVCATPropertiesMod.stfile, "org.molgen.genomeCATPro.peaks", false);
        XMLEncoder e = new XMLEncoder(
                new BufferedOutputStream(
                new FileOutputStream(f)));
        e.writeObject(CNVCATPropertiesMod.props);
        e.close();
    }

    public static class CNVCATProperties implements Serializable {

        private int probeWidth;
        private double maxRatio;
        private double minRatio;
        private double maxQuality;
        private double minQuality;
        private int selectionTolerance;
        private Color colorOverlap;
        private Color colorBackGround;
        private Color colorSelected;
        private double transVariations;
        private double transAberrations;
        private double transFrequencies;
        private int gap;
        private int minHeight;
        private int nofCols;

        //private boolean exportMRNET;
        public CNVCATProperties() {
        }

        public int getNofCols() {
            return nofCols;
        }

        public void setNofCols(int nofCols) {
            this.nofCols = nofCols;
        }

        public int getMinHeight() {
            return minHeight;
        }

        public void setMinHeight(int minHeight) {
            if (minHeight > 1) {
                this.minHeight = minHeight;
            } else {
                this.minHeight = 1;
            }
        }

        public int getGap() {
            return this.gap;
        }

        public void setGap(int g) {
            if (g > 1) {
                this.gap = g;
            } else {
                this.gap = 1;
            }
        }

        /*
        public String getMrnetAbr() {
        return this.mrnetAbr;
        }
        
        public void setMrnetAbr(String mrnetAbr) {
        this.mrnetAbr = mrnetAbr;
        }
         */
        public int getProbeWidth() {
            return this.probeWidth;
        }

        public void setProbeWidth(int probeWidth) {
            this.probeWidth = probeWidth;
        }

        public double getMaxRatio() {
            return this.maxRatio;
        }

        public void setMaxRatio(double maxRatio) {
            this.maxRatio = maxRatio;
        }

        public double getMaxQuality() {
            return this.maxQuality;
        }

        public void setMaxQuality(double maxQuality) {
            this.maxQuality = maxQuality;
        }

        public int getSelectionTolerance() {
            return this.selectionTolerance;
        }

        public void setSelectionTolerance(int selectionTolerance) {
            this.selectionTolerance = selectionTolerance;
        }

        public Color getColorOverlap() {
            return this.colorOverlap;
        }

        public void setColorOverlap(Color colorOverlap) {
            this.colorOverlap = colorOverlap;
        }

        public Color getColorBackGround() {
            return this.colorBackGround;
        }

        public void setColorBackGround(Color colorBackGround) {
            this.colorBackGround = colorBackGround;
        }

        public Color getColorSelected() {
            return this.colorSelected;
        }

        public void setColorSelected(Color colorSelected) {
            this.colorSelected = colorSelected;
        }

        public double getTransVariations() {
            return this.transVariations;
        }

        public void setTransVariations(double transVariations) {
            this.transVariations = transVariations;
        }

        public double getTransAberrations() {
            return this.transAberrations;
        }

        public void setTransAberrations(double transAberration) {
            this.transAberrations = transAberration;
        }

        public double getMinRatio() {
            return this.minRatio;
        }

        public void setMinRatio(double minRatio) {
            this.minRatio = minRatio;
        }

        public double getMinQuality() {
            return this.minQuality;
        }

        public void setMinQuality(double minQuality) {
            this.minQuality = minQuality;
        }

        public double getTransFrequencies() {
            return this.transFrequencies;
        }

        public void setTransFrequencies(double transFrequencies) {
            this.transFrequencies = transFrequencies;
        }
        /*
        public boolean isExportMRNET() {
        return this.exportMRNET;
        }
        
        public void setExportMRNET(boolean exportMRNET) {
        this.exportMRNET = exportMRNET;
        }
         */
    }
}

