package org.molgen.genomeCATPro.annotation;
/**
 * @name CytoBandManagerImpl
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
import java.awt.Color;
import java.awt.Graphics2D;
import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.molgen.dblib.Database;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.AnnotationList;


public class CytoBandManagerImpl extends AnnotationManagerImpl {

    public static String name = "Cytoband";

    //static Hashtable<AnnotationList, Hashtable<String, Vector<CytoBand>>> bands = new Hashtable<AnnotationList, Hashtable<String, Vector<CytoBand>>>();
    public CytoBandManagerImpl(GenomeRelease release) {
        super(release, name);
    /*AnnotationList anno = null;
    anno = AnnotationManagerImpl.getAnnotation(release, name);
    if (anno == null) {
    anno = AnnotationManagerImpl.loadAnnotation(release, name);
    }
    if (anno == null) {
    throw new RuntimeException("Not found " + name + " for " + release.toString());
    }
    AnnotationManagerImpl.data.put(anno, new Hashtable<String, Vector<? extends RegionAnnotation>>());
     */
    }

    public long getLength(List<? extends CytoBand> bands) {

        CytoBand first = (CytoBand) bands.get(bands.size() - 1);
        CytoBand last = (CytoBand) bands.get(0);
        return last.getChromEnd() - first.getChromStart();

    }

    @Override
    public String getNameId() {
        return CytoBandManagerImpl.name;
    }

    @Override
    public List<? extends RegionAnnotation> getData(String chromId) {
        return CytoBandManagerImpl.getBand(this.anno, chromId);
    }

    /*
    @SuppressWarnings("unchecked")
    public CytoBand getBandAtPos(String chrom, long chromPos) {
    
    CytoBand key = new CytoBandImpl(chrom, chromPos);
    java.util.List<CytoBand> _band = (List<CytoBand>) this.getBand(chrom);
    return this.getBandAtPos(_band, key);
    }
    
    public CytoBand getBandAtPos(java.util.List<? extends CytoBand> bands, CytoBand key) {
    //CytoBand key = new CytoBand(chrom,  chromPosition);
    
    int i = Collections.binarySearch(bands, key, CytoBand.findByStart);
    i = i < 0 ? i * -1 - 2 : i;     // i:= (-(insertion point) - 1)
    
    if (i < 0) {
    i = 0;
    }
    return bands.get(i);
    }
    
    public int getIndexBandAtPos(java.util.List<? extends CytoBand> bands, CytoBand key) {
    //CytoBand key = new CytoBand(chrom,  chromPosition);
    
    int i = Collections.binarySearch(bands, key, CytoBand.findByStart);
    i = i < 0 ? i * -1 - 2 : i;     // i:= (-(insertion point) - 1)
    
    if (i < 0) {
    i = 0;
    }
    return i;
    }
     */
    public static List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException {
        ResultSet r;
        Vector<CytoBand> __bands = new Vector<CytoBand>();

        Connection con = null;



        con = Database.getDBConnection(Defaults.localDB);

        Statement s = con.createStatement();


        r = s.executeQuery(
                "SELECT chrom, chromStart, chromEnd,name, gieStain from " +
                table + " where " +
                " chrom = \'" + chromId + "\'" +
                " order by chromEnd");


        while (r.next()) {

            __bands.add(new CytoBandImpl(r.getString("chrom"),
                    r.getLong("chromStart"),
                    r.getLong("chromEnd"),
                    r.getString("name"),
                    r.getString("gieStain")));
        }


        return __bands;
    }

    /**
     * Query the table cytoBand in the back-end database 
     * Fill the results into the empty vector 'bands' 
     * and at the local hashtable for chromId and genomerelease
     *
     **/
    public static List<CytoBand> getBand(AnnotationList anno, String chromId) {

        Logger.getLogger(CytoBandManagerImpl.class.getName()).log(Level.FINE,
                "get CytoBand for " + anno.getGenomeRelease() + " " + chromId);


        Hashtable<String, Vector<? extends RegionAnnotation>> _bands = data.get(anno);
        if (!_bands.containsKey(chromId)) {
            Vector<? extends CytoBand> d;
            try {
                d = (Vector<? extends CytoBand>) CytoBandManagerImpl.dbLoadRegions(anno.getTableData(), chromId);
                _bands.put(chromId, d);
            } catch (SQLException ex) {
                Logger.getLogger(CytoBandManagerImpl.class.getName()).log(
                        Level.SEVERE, "", ex);
                return Collections.EMPTY_LIST;
            }
        }
        return (List<CytoBand>) _bands.get(chromId);
    /**
    if (bands.size() > 0) {
    length = bands.get(bands.size() - 1).chromEnd - bands.get(0).chromStart;
    } else {
    length = 0;
    //length = ((CytoBand)bands.get(bands.size()-1)).chromEnd;		
    }
     **/
    }

    @Override
    public void plot(Graphics2D g, String chromId, int left, int top, int width, double scale) {
        this.plotCytoBand(g, chromId, true, 0, 0, left, top, width, scale);
    }

    public void plot(Graphics2D g, String chromId,
            boolean fullChrom, long firstPos, long secondPos,
            int x0, int y0, int width,
            double yScale) {
        this.plotCytoBand(g, chromId, fullChrom, firstPos, secondPos, x0, y0, width, yScale);
    }

    @SuppressWarnings("unchecked")
    public void plotCytoBand(Graphics2D g, String chromId,
            boolean fullChrom, long firstPos, long secondPos,
            int x0, int y0, int width, double yScale) {

        Logger.getLogger(CytoBandManagerImpl.class.getName()).log(
                Level.INFO, "Plot CytoBand for  " + anno.getGenomeRelease() + " " + chromId +
                " x0: " + x0 + " y0: " + y0 + " width: " + width +
                (fullChrom ? " " : new String(" firstPos: " + firstPos + " secondPos: " + secondPos + " yScale: " +
                yScale)));

        List<CytoBand> _band = (List<CytoBand>) this.getData(chromId);
        //Collections.sort(_band, CytoBand.maxEnd);
        //int length = getLength((List<? extends CytoBand>) _band);
        //int maxEnd = Collections.max(bands, CytoBand.maxEnd);

        List<? extends CytoBand> __band = _band;
        if (!fullChrom) {
            __band = (List<? extends CytoBand>) PlotLib.getSublist(
                    _band, chromId,
                    firstPos, secondPos);


        }

        int i = 0;


        Color c = Color.BLACK;

         
         
             
              int y1      ,  y2;

        for (CytoBand current : __band) {
            // current = (CytoBand) e.next();
            y1 = (int) (y0 + (((current.getChromStart() - firstPos) < 0 ? 0 : (current.getChromStart() - firstPos) / yScale)));
            y2 = (int) (y0 + (((current.getChromEnd() > secondPos ? secondPos : current.getChromEnd()) - firstPos) / yScale));

            c = current.getColor();
            // draw the centromere
            g.setColor(Color.black);
            if (current.getGieStain().indexOf("acen") != -1) {
                if (current.getName().matches("p.*")) {
                    g.drawLine(x0, y1, x0 + width / 2, y2);
                    g.drawLine(x0 + width, y1, x0 + width / 2, y2);

                } else {
                    g.drawLine(x0 + width / 2, y1, x0, y2);
                    g.drawLine(x0 + width / 2, y1, x0 + width, y2);
                }
                continue;
            }
            // draw stalk intented

            if (current.getGieStain().indexOf("stalk") != -1) {
                g.setColor(Color.black);
                g.drawRect(x0 + width / 4, y1, width / 2, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());
                continue;
            }
            g.setColor(c);
            // draw giestain positive band    
            if (current.getGieStain().indexOf("gpos") != -1) {
                g.setColor(Color.black);
                g.drawRect(x0, y1, width, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());
                g.setColor(c);
                g.fillRect(x0 + 1, y1, width - 1, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());
            } //draw gieStain negtive band   
            else {
                if (current.getGieStain().indexOf("gvar") != -1) {
                    g.setColor(Color.black);
                    g.drawRect(x0, y1, width, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());
                    g.setColor(c);
                    g.fillRect(x0 + 1, y1, width - 1, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());

                //draw gieStain negtive band
                } else {
                    g.setColor(Color.black);
                    g.drawRect(x0, y1, width, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());
                    g.setColor(c);
                    g.fillRect(x0 + 1, y1, width - 1, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());
                }
            }

            g.setColor(Color.BLACK);

            if (i == 0) {
                g.drawLine(x0, y0, x0 + width, y0);
            }
            if (i == (_band.size() - 1)) {
                g.drawLine(x0, y2, x0 + width, y2);
            }

            i++;

        }

    }

    public static CytoBand getBandAtPos(GenomeRelease release, String chrom, Long pos, Long dist) {
        AnnotationList anno = AnnotationManagerImpl.getAnnotation(release, name);
        if (anno == null) {
            anno = AnnotationManagerImpl.loadAnnotation(release, name);
        }
        if (anno == null) {
            throw new RuntimeException("Not found " + name + " for " + release.toString());
        }
        AnnotationManagerImpl.data.put(anno, new Hashtable<String, Vector<? extends RegionAnnotation>>());
        java.util.List<CytoBand> b = CytoBandManagerImpl.getBand(anno, chrom);
        return (CytoBand) PlotLib.getDataAtPos(b, chrom, pos, dist);
    }

    /**
     * get first element for chrom (min genome position)
     * @param release
     * @param chrom
     * @return
     */
    public static RegionAnnotation getFirst(GenomeRelease release, String chrom) {
        AnnotationList anno = AnnotationManagerImpl.getAnnotation(release, name);
        if (anno == null) {
            anno = AnnotationManagerImpl.loadAnnotation(release, name);
        }
        if (anno == null) {
            //throw new RuntimeException("Not found " + name + " for " + release.toString());
        }
        AnnotationManagerImpl.data.put(anno, new Hashtable<String, Vector<? extends RegionAnnotation>>());
        java.util.List<CytoBand> b = CytoBandManagerImpl.getBand(anno, chrom);

        RegionAnnotation first = Collections.min(b);
        return first;
    }

    /**
     * get last element for chrom (max genome position)
     * @param release
     * @param chrom
     * @return
     */
    public static RegionAnnotation getLast(GenomeRelease release, String chrom) {
        AnnotationList anno = AnnotationManagerImpl.getAnnotation(release, name);
        if (anno == null) {
            anno = AnnotationManagerImpl.loadAnnotation(release, name);
        }
        if (anno == null) {
            throw new RuntimeException("Not found " + name + " for " + release.toString());
        }
        AnnotationManagerImpl.data.put(anno, new Hashtable<String, Vector<? extends RegionAnnotation>>());
        java.util.List<CytoBand> b = CytoBandManagerImpl.getBand(anno, chrom);

        RegionAnnotation last = Collections.max(b);
        return last;
    }
    

    ////////////STATIC METHODS//////////////////////////////
    /**
     * get length for each of all chroms
     * @param release
     * @return
     */
    public static Hashtable<String, Long> getChromLength(GenomeRelease release) {
        AnnotationList anno = AnnotationManagerImpl.getAnnotation(release, name);
        if (anno == null) {
            anno = AnnotationManagerImpl.loadAnnotation(release, name);
        }
        if (anno == null) {
            throw new RuntimeException("Not found " + name + " for " + release.toString());
        }
        AnnotationManagerImpl.data.put(anno, new Hashtable<String, Vector<? extends RegionAnnotation>>());

        Hashtable<String, Long> list = new Hashtable<String, Long>();
        Connection con = null;

        ResultSet r;
        try {

            con = Database.getDBConnection(Defaults.localDB);
            Statement s = con.createStatement();

            r = s.executeQuery(
                    "select chrom, max(chromEnd) as ml from " + anno.getTableData() +
                    " group by chrom order by chrom");

            while (r.next()) {
                list.put(r.getString("chrom"), r.getLong("ml"));
            }
            if (list.size() == 0) {
                throw new RuntimeException("No Cytoband Data  found at: " +
                        anno.getTableData());
            }
            return list;

        } catch (Exception e) {
            Logger.getLogger(CytoBandManagerImpl.class.getName()).log(Level.INFO, "Error: ", e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }
        return list;
    }

    /**
     * get chroms by name
     * @param release
     * @return
     */
    public static List<String> stGetChroms(GenomeRelease release) {
        AnnotationList anno = AnnotationManagerImpl.getAnnotation(release, name);
        if (anno == null) {
            anno = AnnotationManagerImpl.loadAnnotation(release, name);
        }
        if (anno == null) {
            throw new RuntimeException("Not found " + name + " for " + release.toString());
        }
        AnnotationManagerImpl.data.put(anno, new Hashtable<String, Vector<? extends RegionAnnotation>>());

        List<String> list = new Vector<String>();
        Connection con = null;
        ResultSet r;
        try {
            con = Database.getDBConnection(Defaults.localDB);
            Statement s = con.createStatement();

            r = s.executeQuery(
                    // sort chroms
                    "select distinct chrom, " +
                    " replace(replace(chrom, 'X',999), 'Y', 999) as a  " +
                    " from " + anno.getTableData() + " order by right(a,2)+0;");

            while (r.next()) {
                list.add(r.getString("chrom"));
            }
            if (list.size() == 0) {
                throw new RuntimeException("No Cytoband Data  found at: " +
                        anno.getTableData());
            }
        } catch (Exception e) {
            Logger.getLogger(CytoBandManagerImpl.class.getName()).log(Level.INFO, "Error: ", e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }
        return list;

    }

    /**
     * 
     * @param release
     * @return
     */
    public static int getMaxLength(GenomeRelease release) {
        Hashtable<String, Long> htLengths = CytoBandManagerImpl.getChromLength(release);
        if (htLengths.size() > 0) {
            Collection<Long> cLengths = htLengths.values();
            return Collections.max(cLengths).intValue();
        }
        return 0;
    //Collections.sort((List<T>) (Collection) cLengths);
    // return cLengths.get(cLengths.size()-1).intValue();
    }
}
