package org.molgen.genomeCATPro.common;

import java.awt.Color;

/**
 * @name Defaults
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
/**
 * 020413 kt    add method.SNP
 * 310712 kt    add batchFileCMD
 * 010812 kt    remove geneTableName
 *              add annoClazzExtension
 */
public class Defaults {
    public final static String annoColName = "anno";
    
    public final static String DYESWAP = "dyeswap";
    public final static String batchfile = "executeR";
    public final static String batchfileCMD = "executeRCMD";
    public final static String annoClazzExtension = "Anno";
    public final static String annoGeneClazzExtension = "AnnoGene";

    public static enum DataType {

        RAW("unprocessed"),
        SEGMENTS("smoothed segments"),
        NORMALIZED("normalized"),
        TRANSFORMED("processed"),
        PEAK("genomic region"),
        BIN("binned genomic region");
        private String value;

        DataType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static DataType toDataType(String text) {
            if (text != null) {

                for (DataType b : DataType.values()) {
                    if (text.contains(b.value)) {
                        return b;
                    }
                }

            }
            return null;
        }
    };

    public static enum GenomeRelease {

        hg17("hg17:NCBI35:May2004"),
        hg18("hg18:NCBI36:Mar2006"),
        hg19("hg19:NCBI37:Feb2009");
        private String value;

        GenomeRelease(String value) {
            this.value = value;
        }

        public static GenomeRelease toRelease(String text) {
            if (text != null) {
                /*if(text.toLowerCase().contains("hg17"))
                return hg17;
                if(text.toLowerCase().contains("hg18"))
                return hg18;
                 */
                for (GenomeRelease b : GenomeRelease.values()) {
                    if (text.toLowerCase().contains(b.name())) {
                        return b;
                    }
                }
            /*
            for (GenomeRelease b : GenomeRelease.values()) {
            if (text.equalsIgnoreCase(b.value)) {
            return b;
            }
            }
             */
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }

        public String toShortString() {
            return value.substring(0, value.indexOf(":"));
        }
    }

    public static enum Method {

        aCGH("aCGH"),
        GE("GE Genexpression"),
        MEDIP("MEDIP"),
        ChIPChip("ChIPChip"),
        ChIPSeq("ChIPSeq"),
        SNP("SNP");
        private String value;

        Method(String value) {
            this.value = value;
        }

        public static Method toMethod(String text) {
            if (text != null) {

                for (Method b : Method.values()) {
                    if (text.contains(b.name())) {
                        return b;
                    }
                }

            }
            return null;
        }
    };

    public static enum Type {

        Oligo("Oligo"),
        BAC("BAC");
        private String value;

        Type(String value) {
            this.value = value;
        }

        public static Type toType(String text) {
            if (text != null) {

                for (Type b : Type.values()) {
                    if (text.contains(b.name())) {
                        return b;
                    }
                }

            }
            return null;
        }
    };
    public final static int MAX_TABLE_NAME = 20;
    public static final String localDB = "genomeCAT";
    public static final Color fieldInputRequiered = new Color(175, 205, 212);
    public static final Color buttonImportant = new Color(40, 155, 182);
}
