package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportTrackCNV
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package. Katrin Tebel
 * <tebel at molgen.mpg.de>. The contents of this file are subject to the terms
 * of either the GNU General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import org.molgen.genomeCATPro.annotation.RegionLib;

/**
 * import track with cnv code (deletion, amplification, LOH), transfer to ratios
 *
 */
public class ImportTrackAffy extends ImportTrack implements XPortTrack {

    public static String track_affy_cnv_txt = "AFFY_CNV";

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{ImportTrackAffy.track_affy_cnv_txt}));
    }

    @Override
    public String getName() {
        return ImportTrackAffy.track_affy_cnv_txt;
    }

    @Override
    public void newImportTrack(String filename) throws Exception {
        super.newImport(filename);
        this.hasHeader = true;

    }

    @Override
    //ID_REF  VALUE   CHROMOSOME      POSITION        LOG2 RATIO      SMOOTH SIGNAL   LOH     ALLELE DIFFERENCE
    public List<String[]> getDefaultMappingFile2DBColNames() {
        List<String[]> _map = new Vector<String[]>();
        String[] entry = new String[2];

        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = hasHeader ? (this.fileColNames[2]) : "field1";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromStart";
        entry[ind_file] = hasHeader ? (this.fileColNames[3]) : "field2";

        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = hasHeader ? (this.fileColNames[3]) : "field3";

        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "name";
        entry[ind_file] = hasHeader ? this.fileColNames[0] : "field5";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "ratio";
        entry[ind_file] = hasHeader ? (this.fileColNames[1]) : "field4";
        _map.add(entry);

        return _map;
    }

    @Override
    protected String[] modify(List<String[]> map, String[] _tmp) {
        String[] tmp = super.modify(map, _tmp);
        // todo logratio aus r/g (abhängig ob dyeswap 
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat myFormatter = new DecimalFormat("0.#####", otherSymbols);

        int ratio = (int) Double.parseDouble(tmp[iratio] != null ? tmp[iratio] : "2");
        switch (ratio) {
            case 0:
                tmp[iratio] = myFormatter.format(-1);
                break;
            case 1:
                tmp[iratio] = myFormatter.format(-0.5);
                break;
            case 2:
                tmp[iratio] = myFormatter.format(0);
                break;
            case 3:
                tmp[iratio] = myFormatter.format(0.5);
                break;
            default:
                tmp[iratio] = myFormatter.format(1);
                break;

        }
        // 030413 kt
        if (tmp[ichrom] != null && !tmp[ichrom].contentEquals("") && !tmp[ichrom].startsWith("chr")) {

            if (tmp[ichrom].charAt(0) == 'X' || tmp[ichrom].charAt(0) == 'Y') {
                tmp[ichrom] = "chr" + tmp[ichrom];
            } else {
                try {
                    tmp[ichrom] = RegionLib.fromIntToChr(Integer.parseInt(tmp[ichrom]));
                } catch (NumberFormatException numberFormatException) {
                    tmp[ichrom] = "chr" + tmp[ichrom];
                }
            }
        }

        return tmp;

    }
}
