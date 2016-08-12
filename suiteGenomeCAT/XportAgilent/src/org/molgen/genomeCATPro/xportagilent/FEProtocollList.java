package org.molgen.genomeCATPro.xportagilent;

/**
 * @name FEProtocollList
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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Defaults.Method;
import org.molgen.genomeCATPro.common.Defaults.Type;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * 130313 kt dont save if file not found
 */
public class FEProtocollList implements Serializable {
    //An inner class contains an implicit reference to the outer class, 
    //so for an inner class to be serializable its outer class must be as well.

    static HashMap<String, FEProtocoll> list;
    transient static String file = "feprotokoll.xml";

    static public class FEProtocoll implements Serializable {

        String name;
        String text;
        public Type type;
        public Method method;
        public String hybridisation;
        public String processing;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public FEProtocoll() {
            // xml serializer??
        }

        ;

        public String getHybridisation() {
            return hybridisation;
        }

        public void setHybridisation(String hybridisation) {
            this.hybridisation = hybridisation;
        }

        public String getProcessing() {
            return processing;
        }

        public void setProcessing(String processing) {
            this.processing = processing;
        }

        public FEProtocoll(String name, String text, Type type, Method method, String hyb, String proc) {
            this.name = name;
            this.text = text;
            this.method = method;
            this.type = type;
            this.hybridisation = hyb;
        }

        public String toFullString() {
            return new String(
                    this.getName() + ","
                    + this.getText() + ", "
                    + this.getType() + ","
                    + this.getMethod() + ","
                    + this.getHybridisation() + ","
                    + this.getProcessing());
        }
    }

    /**
     * initial create content for propertie file
     */
    @SuppressWarnings("unchecked")
    private static void load() throws FileNotFoundException {

        File f = InstalledFileLocator.getDefault().locate(
                FEProtocollList.file, "org.molgen.genomeCATPro.xportagilent", false);
        Logger.getLogger(FEProtocollList.class.getName()).log(Level.INFO, "load FEProtocoll List from " + f.getPath());
        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                        new FileInputStream(f.getPath())));
        FEProtocollList.list = (HashMap<String, FEProtocoll>) d.readObject();

        d.close();
    }

    static public void save() throws FileNotFoundException {

        File f = InstalledFileLocator.getDefault().locate(
                FEProtocollList.file, "org.molgen.genomeCATPro.xportagilent", false);
        if (f == null) {
            Logger.getLogger(FEProtocollList.class.getName()).log(Level.WARNING,
                    "file not found " + FEProtocollList.file);
        } else {
            Logger.getLogger(FEProtocollList.class.getName()).log(Level.INFO, "save FEProtocoll List to " + f.getPath());
            XMLEncoder e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream(f.getPath())));
            e.writeObject(FEProtocollList.list);

            e.flush();
            e.close();
        }
    }

    static void create() {
        Logger.getLogger(FEProtocollList.class.getName()).log(Level.INFO, "create FEProtocoll List");

        list = new HashMap<String, FEProtocoll>();
        String s = new String();

        FEProtocoll p = new FEProtocoll(
                "ChIP_105_Jan09",
                "Agilent Mammalian ChIP-on-Chip v.10 (P/N G4481-90010)",
                Type.Oligo,
                Method.ChIPChip, "", "");

        list.put(p.name, p);
        p = new FEProtocoll(
                "GE1-v5_95_Feb07",
                "One-Color Microarray-Based Gene Expression Analysis v.5.5.x (P/N G4140-90040)",
                Type.Oligo,
                Method.GE, "", "");

        list.put(p.name, p);
        p = new FEProtocoll(
                "GE1-v5_95_Feb07 (Read Only)",
                "One-Color Microarray-Based Gene Expression Analysis v.5.5.x (P/N G4140-90040)",
                Type.Oligo,
                Method.GE, "", "");
        list.put(p.name, p);
        p = new FEProtocoll(
                "CGH_105_Dec08 (Read Only)",
                "Agilent Oligonucleotide Array-Based CGH for Genomic DNA",
                Type.Oligo,
                Method.aCGH,
                "www.genomics.agilent.com/files/Manual/G4410-90010_CGH_Enzymatic_Protocol_v6.2.1.pdf",
                "TIFF image processing with Agilent Feature Extraction  ");
        list.put(p.name, p);

        try {
            save();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FEProtocollList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public void add(FEProtocoll p) {
        if (FEProtocollList.list == null) {
            try {
                load();
            } catch (Exception ex) {
                Logger.getLogger(FEProtocollList.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());

            }
        }
        if (FEProtocollList.list == null || FEProtocollList.list.size() == 0) {
            create();
        }
        list.put(p.name, p);
        Logger.getLogger(FEProtocollList.class.getName()).log(Level.INFO, "add: "
                + p.toFullString());
        try {
            save();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FEProtocollList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public FEProtocoll get(String protokollName) {
        if (FEProtocollList.list == null) {
            try {
                load();
            } catch (Exception ex) {
                Logger.getLogger(FEProtocollList.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());

            }
        }
        if (FEProtocollList.list == null || FEProtocollList.list.size() == 0) {
            create();
        }
        return FEProtocollList.list.get(protokollName);

    }
}
