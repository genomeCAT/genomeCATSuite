package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name ImportTrackWIG
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 *
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 020813 kt XPortImport createNewImport(); 070513 kt map from read content to
 * db content in wig file case just here, fixed 030513 kt import variabel wig
 * format 240413 kt hasHeader: false 020412 kt auch erste Zeile (info zeile) als
 * Datenzeile lesen
 *
 *
 */
public class ImportTrackWIG extends ImportTrack implements XPortTrack {

    public static String track_wig_txt = "WIG";
    final static String myWigSep = "\n";

    public ImportTrackWIG createNewImport() {
        return new ImportTrackWIG();
    }

    @Override
    public String getMySep() {
        return myWigSep;
    }

    @Override
    protected boolean isCommentLine(String is) {
        if (is.indexOf("#") == 0 || is.indexOf("track") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isHasHeader() {
        return false;
    }

    @Override
    public String getName() {
        return ImportTrackWIG.track_wig_txt;
    }

    @Override
    protected boolean isHeaderLine(String is) {
        return false;
    }

    @Override
    //060513    kt  wig file type depening fileColNames
    protected void readFileColNames() {

        Vector<Vector<String>> l = super.readData(100);

        for (Vector<String> el : l) {
            if (el.get(0).startsWith(ImportTrackWIG.tagFixedStep)) {
                this.fileColNames = new String[1];
                this.fileColNames[0] = "dataValue";
                break;
            }

            if (el.get(0).startsWith(ImportTrackWIG.tagVariableStep)) {
                this.fileColNames = new String[2];
                this.fileColNames[0] = "chromStart";

                this.fileColNames[1] = "dataValue";
                break;
            }

        }

    }

    @Override
    public List<String[]> getDefaultMappingFile2DBColNames() {

        List<String[]> _map = new Vector<String[]>();
        String[] entry = new String[2];
        if (this.fileColNames.length > 1) {
            entry[ind_db] = "chromStart";
            entry[ind_file] = "chromStart";
            _map.add(entry);

            entry = new String[2];
            entry[ind_db] = "ratio";
            entry[ind_file] = "dataValue";
            _map.add(entry);
        } else {
            entry = new String[2];
            entry[ind_db] = "ratio";
            entry[ind_file] = "dataValue";
            _map.add(entry);
        }
        return _map;
    }
    int i_chromEnd = -1;
    int i_chromStart = -1;
    int i_chrom = -1;
    int i_ratio = -1;

    @Override
    protected List<String[]> extendMapping() {
        List<String[]> _map = this.getMappingFile2DBColNames();
        String[] entry = new String[2];

        if (this.fileColNames.length == 1) {
            entry[ind_db] = "chromStart";
            entry[ind_file] = "chromStart";
            _map.add(entry);
        }
        entry = new String[2];
        entry[ind_db] = "chrom";
        entry[ind_file] = "";
        _map.add(entry);

        entry = new String[2];
        entry[ind_db] = "chromEnd";
        entry[ind_file] = "";

        _map.add(entry);

        for (int i = 0; i < _map.size(); i++) {
            if (_map.get(i)[ind_db].contentEquals("chromEnd")) {
                i_chromEnd = i;

            }
            if (_map.get(i)[ind_db].contentEquals("chromStart")) {
                i_chromStart = i;

            }
            if (_map.get(i)[ind_db].contentEquals("chrom")) {
                i_chrom = i;

            }
            if (_map.get(i)[ind_db].contentEquals("ratio")) {
                i_ratio = i;

            }

        }

        return _map;
    }
    String chrom = "";
    long pos = 0;
    long step = 0;
    long span = 0;
    Pattern wigpattern;
    Matcher wigmatcher;
    final static String tagFixedStep = "fixedStep";
    final static String tagVariableStep = "variableStep";
    final static String tagChrom = "chrom=";
    boolean fixed;

    @Override
    /**
     * 070513 kt map from read content to db content in wig file case just here,
     * fixed
     */
    protected String[] modify(List<String[]> map, String[] tmp) {

        if (tmp[0] != null && tmp[0].indexOf(tagVariableStep) >= 0) {
            // #variableStep  chrom=chrN  [span=windowSize]
            String line = Arrays.toString(tmp);

            tmp = new String[3];
            System.out.println(line);
            wigpattern = Pattern.compile(
                    tagVariableStep
                    + "\\s+chrom=(\\w+)(?:\\s*span=(\\d+))?", Pattern.CASE_INSENSITIVE);
            wigmatcher = wigpattern.matcher(line);
            if (wigmatcher.find()) {
                System.out.println(wigmatcher.group(0));
                this.chrom = wigmatcher.group(1);

                System.out.println("Group1: " + wigmatcher.group(1));

                this.span = this.step;
                if (wigmatcher.groupCount() >= 1) {
                    System.out.println("Group2: " + wigmatcher.group(2));
                    if (wigmatcher.group(2) != null) {
                        this.span = new Integer(wigmatcher.group(2)).intValue();
                    }
                } else {
                    this.span = 1;
                }
                this.fixed = false;
                return null;
            } else {
                throw new RuntimeException("variableStep Format Error ");
            }
        }
        if (tmp[0] != null && tmp[0].indexOf(tagFixedStep) >= 0) {
            //fixedStep  chrom=chrN  start=position  step=stepInterval  [span=windowSize]

            String line = Arrays.toString(tmp);
            tmp = new String[3];
            System.out.println(line);
            wigpattern = Pattern.compile(
                    tagFixedStep
                    + "\\s+chrom=(\\w+)(?:\\s*start=)(\\d+)(?:\\s*step=(\\d+))?(?:\\s*span=(\\d+))?",
                    // "\\s+chrom=(\\w+).*start=(\\d+)(?:\\s)(?:step=)(\\d+)?",
                    //.*(span=(\\d+))?",
                    Pattern.CASE_INSENSITIVE);
            wigmatcher = wigpattern.matcher(line);

            //[]
            if (wigmatcher.find()) {
                System.out.println(wigmatcher.group(0));
                this.chrom = wigmatcher.group(1);

                System.out.println("Group1: " + wigmatcher.group(1));
                this.pos = new Integer(wigmatcher.group(2)).longValue();
                System.out.println("Group2: " + wigmatcher.group(2));
                this.step = 1;
                if (wigmatcher.groupCount() >= 3) {
                    System.out.println("Group3: " + wigmatcher.group(3));
                    if (wigmatcher.group(3) != null) {
                        this.step = new Integer(wigmatcher.group(3)).intValue();
                    }
                }

                this.span = this.step;
                if (wigmatcher.groupCount() >= 4) {
                    System.out.println("Group4: " + wigmatcher.group(4));
                    if (wigmatcher.group(4) != null) {
                        this.span = new Integer(wigmatcher.group(4)).intValue();
                    }
                }
                this.fixed = true;
                return null;
            } else {
                throw new RuntimeException("fixedStep Format Error ");
            }
        }
        if (fixed) {
            tmp[i_ratio] = tmp[0];
            tmp[i_chrom] = this.chrom;
            tmp[i_chromStart] = Long.toString(this.pos);
            tmp[i_chromEnd] = Long.toString(this.pos + this.span - 1); // normal line

            this.pos += step;
        } else {
            String[] _tmp = tmp[0].split("\t", 2);
            tmp[i_chromStart] = _tmp[0];
            tmp[i_ratio] = _tmp[1];
            tmp[i_chrom] = this.chrom;
            tmp[i_chromEnd] = Long.toString(new Long(_tmp[0]).longValue() + this.span - 1);
            ;
            //55638	0.558
        }

        //System.out.println(Arrays.deepToString(tmp));
        return tmp;
    }

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{ImportTrackWIG.track_wig_txt}));
    }
}
