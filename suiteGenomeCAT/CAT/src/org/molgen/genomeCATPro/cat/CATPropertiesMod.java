package org.molgen.genomeCATPro.cat;

/**
 * @name CATProperties
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>. The contents of
 * props file are subject to the terms of either the GNU General Public License
 * Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use props file
 * except in compliance with the License. You can obtain a copy of the License
 * at http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. props program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
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

public class CATPropertiesMod implements Serializable {

    static CATProperties props = null;
    transient static String stfile = "catproperties.xml";

    public static CATProperties props() {
        if (CATPropertiesMod.props == null) {
            CATPropertiesMod.props = load();
        }
        if (CATPropertiesMod.props == null) {
            create();
        }
        return CATPropertiesMod.props;
    }

    private static CATProperties load() {

        try {
            Logger.getLogger(CATPropertiesMod.class.getName()).log(
                    Level.INFO, "load properties from " + stfile);
            File f = InstalledFileLocator.getDefault().locate(
                    CATPropertiesMod.stfile, "org.molgen.genomeCATPro.cat", false);
            //CATPropertiesMod.stfile, null, true);

            XMLDecoder d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream(f)));
            CATProperties _props = (CATProperties) d.readObject();
            d.close();
            return _props;
        } catch (Exception ex) {
            Logger.getLogger(CATPropertiesMod.class.getName()).log(
                    Level.SEVERE, "error ", ex);
            return null;
        }
    }

    @SuppressWarnings("empty-statement")
    private static void create() {
        Logger.getLogger(CATProperties.class.getName()).log(Level.INFO, "CATProperties created");
        CATPropertiesMod.props = new CATProperties();

        /*try {
    // save();
    ;
    } catch (FileNotFoundException ex) {
    Logger.getLogger(CATPropertiesMod.class.getName()).log(
    Level.SEVERE, "error ", ex);
    }*/
    }

    public static void save() throws FileNotFoundException {
        Logger.getLogger(CATProperties.class.getName()).log(
                Level.INFO, "save CATProperties");
        File f = InstalledFileLocator.getDefault().locate(
                CATPropertiesMod.stfile, "org.molgen.genomeCATPro.cat", false);
        //CATPropertiesMod.stfile, null, true);
        XMLEncoder e = new XMLEncoder(
                new BufferedOutputStream(
                        new FileOutputStream(f)));
        e.writeObject(CATPropertiesMod.props);
        e.close();
    }

    public static class CATProperties implements Serializable {

        boolean globalScale = false;
        double rulerStepSize = 1.0;
        boolean colorScaleRedGreen = true;
        double scaleFactor = 1.0;

        public double getScaleFactor() {
            return scaleFactor;
        }

        public void setScaleFactor(double scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        public boolean isGlobalScale() {
            return globalScale;
        }

        public void setGlobalScale(boolean globalScale) {
            this.globalScale = globalScale;
        }

        public boolean isColorScaleRedGreen() {
            return colorScaleRedGreen;
        }

        public void setColorScaleRedGreen(boolean colorScaleRedGreen) {
            this.colorScaleRedGreen = colorScaleRedGreen;
        }

        public double getRulerStepSize() {
            return rulerStepSize;
        }

        public void setRulerStepSize(double rulerStepSize) {
            this.rulerStepSize = rulerStepSize;
        }
    }
}
