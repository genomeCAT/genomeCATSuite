package org.molgen.genomeCATPro.guimodul.platform;

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
 * @name FEProtocoll
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Oct 20, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
public class GEOProtocollList implements Serializable {
    //An inner class contains an implicit reference to the outer class, 
    //so for an inner class to be serializable its outer class must be as well.

    static HashMap<String, FEProtocoll> list;
    transient static String file = "feprotokoll.xml";

    static public class FEProtocoll implements Serializable {

        String name;

        String text;
        public Type type;
        public Method method;

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

        public FEProtocoll(String name, String text, Type type, Method method) {
            this.name = name;
            this.text = text;
            this.method = method;
            this.type = type;
        }

        public String toFullString() {
            return new String(
                    this.getName() + ","
                    + this.getText() + ", "
                    + this.getType() + ","
                    + this.getMethod());
        }
    }

    /**
     * initial create content for propertie file
     */
    @SuppressWarnings("unchecked")
    private static void load() throws FileNotFoundException {

        File f = InstalledFileLocator.getDefault().locate(
                GEOProtocollList.file, "org.molgen.genomeCATPro.xportagilent", false);
        Logger.getLogger(GEOProtocollList.class.getName()).log(Level.INFO, "load FEProtocoll List from " + f.getPath());
        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                        new FileInputStream(f.getPath())));
        GEOProtocollList.list = (HashMap<String, FEProtocoll>) d.readObject();

        d.close();
    }

    static public void save() throws FileNotFoundException {

        File f = InstalledFileLocator.getDefault().locate(
                GEOProtocollList.file, "org.molgen.genomeCATPro.xportagilent", false);
        Logger.getLogger(GEOProtocollList.class.getName()).log(Level.INFO, "save FEProtocoll List to " + f.getPath());
        XMLEncoder e = new XMLEncoder(
                new BufferedOutputStream(
                        new FileOutputStream(f.getPath())));
        e.writeObject(GEOProtocollList.list);

        e.flush();
        e.close();
    }

    static void create() {
        Logger.getLogger(GEOProtocollList.class.getName()).log(Level.INFO, "create FEProtocoll List");

        list = new HashMap<String, FEProtocoll>();
        String s = new String();

        FEProtocoll p = new FEProtocoll(
                "ChIP_105_Jan09",
                "Agilent Mammalian ChIP-on-Chip v.10 (P/N G4481-90010)",
                Type.Oligo,
                Method.ChIPChip);

        list.put(p.name, p);
        p = new FEProtocoll(
                "GE1-v5_95_Feb07",
                "One-Color Microarray-Based Gene Expression Analysis v.5.5.x (P/N G4140-90040)",
                Type.Oligo,
                Method.GE);

        list.put(p.name, p);
        p = new FEProtocoll(
                "GE1-v5_95_Feb07 (Read Only)",
                "One-Color Microarray-Based Gene Expression Analysis v.5.5.x (P/N G4140-90040)",
                Type.Oligo,
                Method.GE);
        list.put(p.name, p);
        p = new FEProtocoll(
                "CGH_105_Dec08 (Read Only)",
                "Agilent Oligonucleotide Array-Based CGH for Genomic DNA",
                Type.Oligo,
                Method.aCGH);
        list.put(p.name, p);

        try {
            save();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GEOProtocollList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public void add(FEProtocoll p) {
        if (GEOProtocollList.list == null) {
            try {
                load();
            } catch (Exception ex) {
                Logger.getLogger(GEOProtocollList.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
                create();
            }
        }
        list.put(p.name, p);
        Logger.getLogger(GEOProtocollList.class.getName()).log(Level.INFO, "add: "
                + p.toFullString());
        try {
            save();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GEOProtocollList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public FEProtocoll get(String protokollName) {
        if (GEOProtocollList.list == null) {
            try {
                load();
            } catch (Exception ex) {
                Logger.getLogger(GEOProtocollList.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
                create();
            }
        }
        return GEOProtocollList.list.get(protokollName);

    }
}
