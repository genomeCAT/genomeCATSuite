package org.molgen.genomeCATPro.cat.util;

import org.molgen.genomeCATPro.cat.maparr.ArrayAnnoView;
import org.molgen.genomeCATPro.cat.maparr.ArrayTrackView;
import org.molgen.genomeCATPro.cat.maparr.ArrayGeneView;
import org.molgen.genomeCATPro.cat.maparr.ArrayRefseqGeneView;
import org.molgen.genomeCATPro.cat.maparr.ArrayView;

/**
 * @name Defines
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
 * 
 * 020413   kt  new segment intervall
 */
public class Defines {

    
    public final static String COL_RATIO = "ratio";
    public final static String COL_RATIO_CBS = "ratioCBS";
    public final static String COL_PVALUE_CBS = "ratioCBS";
    public final static String COL_RATIO_LOC = "ratioLOC";
    public final static String COL_PVALUE_LOC = "pLOC";
    public final static String COL_RATIO_GEN = "ratioGEN";
    public final static String COL_PVALUE_GEN = "pGEN";
//public final static String tempPrefix = "/project/Kopenhagen/Katrin/export";
    
    public final static int ARRAY_HEIGTH = 200;
    public final static int ARRAY_OFFSET = 10;
    public final static int ARRAY_WIDTH = 900;
    public final static String SOURCE_AGILENT = "Agilent";
    public final static String SOURCE_CGHPRO = "CGHPRO";
    public final static String SOURCE_USER = "USERDEF";
    public static final String METHOD_CGH = "ArrayCGH";
    public static final String METHOD_LEGEND = "legend";
    public static final String METHOD_GE = "GenExpression";
    public static final String METHOD_MCA = "Methylation by MCA";
    public static final String METHOD_MEDIP = "Methylation by MEDIP";
    public static final String METHOD_MEDIP_CPG = "Methylation by MEDIP/CPG";
    public static final String METHOD_CHIP = "ChipOnChip";
    public final static String MAP_LOCATION = "Location";
    public final static String MAP_ID = "ID";
    public final static String MAP_GENE = "Gene";
    public final static String MAP_REGION = "Region";
    public final static String MAP_ANNO = "Annotation";
   
    public final static double MAX_PVALUE = 0.05;
    public static final Integer[] SEGMENT_GENE_INTS = {new Integer(500),
        new Integer(250),
        new Integer(100),
        new Integer(50),
        new Integer(10)
    };
    public static final String[] TITEL_SEGMENT_INTS = {"1 MB", "500 kB", "300 kB", "100 kB", "10 kB", " 1 kB"};
    public static final Integer[] SEGMENT_INTS = {
        new Integer(1000000),
        new Integer(500000),
        new Integer(300000),
        new Integer(100000),
        new Integer(10000),
        new Integer(1000)
    };

    
    /*@Deprecated 
    public static String getClazzname(String colName) {

        if (colName.compareTo(Defines.COL_RATIO) == 0) {
            return new String("maparr.ArrayView");
        }

        if (colName.compareTo(Defines.COL_PVALUE_CBS) == 0) {
            return new String("maparr.ArrayTrackView");
        }
        if (colName.compareTo(Defines.COL_RATIO_CBS) == 0) {
            return new String("maparr.ArrayCBSView");
        }
        if (colName.compareTo(Defines.COL_PVALUE_GEN) == 0) {
            return new String("maparr.EnrichmentGeneView");
        }
        if (colName.compareTo(Defines.COL_RATIO_GEN) == 0) {
            return new String("maparr.EnrichmentGeneView");
        }
        if (colName.compareTo(Defines.COL_PVALUE_LOC) == 0) {
            return new String("maparr.EnrichmentView");
        }
        if (colName.compareTo(Defines.COL_RATIO_LOC) == 0) {
            return new String("maparr.EnrichmentView");
        }

        return "";
    }*/

    public static String getViewTitelByClazzname(String clazz) {
        if (clazz.compareToIgnoreCase(ArrayView.class.getName()) == 0) {
            return "original data";
        }
        if (clazz.compareToIgnoreCase(ArrayGeneView.class.getName()) == 0) {
            return "mean ratios by gene";
        }
        if (clazz.compareToIgnoreCase(ArrayRefseqGeneView.class.getName()) == 0) {
            return "mean ratios by refseq gene";
        }
        if (clazz.compareToIgnoreCase(ArrayTrackView.class.getName()) == 0) {
            return "genomic regions";
        }
        if (clazz.compareToIgnoreCase(ArrayAnnoView.class.getName()) == 0) {
            return "annotation";
        }




        return "";
    }

    /**
     * get intervall size by titel
     * @param sIntervall titel
     * @return intervall if valid titel, otherwise -1
     */
    public static Integer getSegmentIntByTitel(String sIntervall) {


        if (sIntervall != null) {
            for (int i = 0; i < Defines.TITEL_SEGMENT_INTS.length; i++) {
                if (Defines.TITEL_SEGMENT_INTS[i].equals(sIntervall)) {
                    return (Defines.SEGMENT_INTS[i]);
                }
            }
        }
        return new Integer(-1);
    }
}