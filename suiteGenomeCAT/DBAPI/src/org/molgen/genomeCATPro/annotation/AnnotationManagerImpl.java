package org.molgen.genomeCATPro.annotation;

/**
 * @name AnnotationManagerImpl
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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.DBService;
import org.molgen.genomeCATPro.dblib.Database;

import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.AnnotationList;

public class AnnotationManagerImpl implements AnnotationManager {

    static Hashtable<AnnotationList, Hashtable<String, Vector<? extends RegionAnnotation>>> data = new Hashtable<AnnotationList, Hashtable<String, Vector<? extends RegionAnnotation>>>();
    AnnotationList anno = null;

    public AnnotationList getAnnotation() {
        return anno;
    }

    public void setAnnotation(AnnotationList anno) {
    }

    public static AnnotationList getAnnotation(GenomeRelease release, String name) {

        for (AnnotationList anno : AnnotationManagerImpl.data.keySet()) {
            if (anno.getGenomeRelease().contentEquals(release.toString()) && anno.getName().contentEquals(name)) {
                return anno;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static AnnotationList loadAnnotation(GenomeRelease release, String name) {

        AnnotationList anno = null;

        EntityManager em = DBService.getEntityManger();
        if (em == null) {
            return null;
        }

        try {
            Query query = em.createQuery(
                    "SELECT s FROM AnnotationList s "
                    + " where s.name = ?1 "
                    + " and s.genomeRelease = ?2 ");

            query.setParameter(1, name);
            query.setParameter(2, release.toString());

            List<AnnotationList> list = query.getResultList();
            if (list.size() > 0) {
                anno = list.get(0);
            } else {
                return null;
            }

        } catch (Exception e) {
            Logger.getLogger(AnnotationManagerImpl.class.getName()).log(Level.WARNING,
                    "Error: ", e);
            return null;
        } finally {
            em.close();
        }

        //mod = new AnnotationManagerImpl();
        //mod.addKey(release, name, clazz, table);
        //AnnKey key = new AnnKey(release, name, clazz, table);
        Logger.getLogger(AnnotationManagerImpl.class.getName()).log(Level.INFO,
                "attach: " + anno.getGenomeRelease() + " " + anno.getName() + " " + anno.getTableData() + " " + anno.getClazz());

        return anno;
    }

    public AnnotationManagerImpl(GenomeRelease release, String name) {

        anno = AnnotationManagerImpl.getAnnotation(release, name);
        if (anno == null) {
            anno = AnnotationManagerImpl.loadAnnotation(release, name);
        }
        if (anno == null) {
            throw new RuntimeException("Not found " + name + " for " + release.toString());
        }
        AnnotationManagerImpl.data.put(anno, new Hashtable<String, Vector<? extends RegionAnnotation>>());

    }

    public String getNameId() {
        return "";
    }

    public static Vector<String> listAnnotationsNames(GenomeRelease release) {
        Vector<String> list = new Vector<String>();
        Connection con = null;
        try {

            con = Database.getDBConnection(CorePropertiesMod.props().getDb());

            Statement s = con.createStatement();

            ResultSet r = s.executeQuery(
                    "select name from AnnotationList "
                    + " where  genomeRelease = \'" + release.toString() + "\'");
            while (r.next()) {
                list.add(r.getString("name"));
            }
            //return list;

        } catch (Exception e) {
            Logger.getLogger(AnnotationManagerImpl.class.getName()).log(Level.WARNING,
                    "Error: ", e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnnotationManagerImpl.class.getName()).log(Level.WARNING,
                            "Error: ", ex);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<? extends RegionAnnotation> getData(String chromId) {
        Logger.getLogger(AnnotationManagerImpl.class.getName()).log(
                Level.INFO,
                "get data for " + anno.getName() + " " + anno.getGenomeRelease() + " " + chromId);

        //Hashtable<String, Vector<CytoBand>> _data = data.get(release);
        if (!data.get(anno).containsKey(chromId)) {

            try {
                Logger.getLogger(AnnotationManagerImpl.class.getName()).log(Level.INFO,
                        "load data: " + anno.getTableData() + " " + chromId);
                RegionAnnotation singleRegion = ServiceAnnotationManager.getRegionInstance(
                        anno.getClazz());
                AnnotationManagerImpl.data.get(anno).put(chromId,
                        (Vector<? extends RegionAnnotation>) singleRegion.dbLoadRegions(
                                anno.getTableData(), chromId));
            } catch (Exception e) {
                Logger.getLogger(AnnotationManagerImpl.class.getName()).log(Level.WARNING,
                        "Error: ", e);
                return Collections.EMPTY_LIST;
            }
        }
        return AnnotationManagerImpl.data.get(anno).get(chromId);
    }

    public RegionAnnotation getFirst(String chromId) {

        java.util.List<? extends RegionAnnotation> b = this.getData(chromId);

        RegionAnnotation first = Collections.min(b);
        return first;

    }

    public RegionAnnotation getLast(String chromId) {

        java.util.List<? extends RegionAnnotation> b = this.getData(chromId);

        RegionAnnotation last = Collections.max(b);
        return last;

    }

    /*
    public RegionAnnotation getRegionAtPos(java.util.List<? extends RegionAnnotation> list, RegionAnnotation key) {
    //CytoBand key = new CytoBand(chrom,  chromPosition);
    
    int i = Collections.binarySearch(list, key, RegionAnnotation.comChromStart);
    i = i < 0 ? i * -1 - 2 : i;     // i:= (-(insertion point) - 1)
    
    if (i < 0) {
    i = 0;
    }
    return list.get(i);
    }
    
    public int getIndexRegionAtPos(java.util.List<? extends RegionAnnotation> list, RegionAnnotation key) {
    //CytoBand key = new CytoBand(chrom,  chromPosition);
    
    int i = Collections.binarySearch(list, key, RegionAnnotation.comChromStart);
    i = i < 0 ? i * -1 - 2 : i;     // i:= (-(insertion point) - 1)
    
    if (i < 0) {
    i = 0;
    }
    return i;
    }*/
    public void plot(Graphics2D g, String chromId, int left, int top, int width, double scale) {
        this.plot(g, chromId, true, 0, 0, left, top, width, scale);
    }

    @SuppressWarnings("unchecked")
    public void plot(Graphics2D g, String chromId,
            boolean fullChrom, long firstPos, long secondPos,
            int x0, int y0, int width,
            double yScale) {

        Logger.getLogger(AnnotationManagerImpl.class.getName()).log(
                Level.INFO, "Plot Data for  " + anno.getName() + " " + anno.getGenomeRelease() + " " + chromId
                + " x0: " + x0 + " y0: " + y0 + " width: " + width
                + (fullChrom ? " " : new String(" firstPos: " + firstPos + " secondPos: " + secondPos + " yScale: "
                                + yScale)));

        List<? extends RegionAnnotation> _data = this.getData(chromId);
        if (_data == null || _data.size() == 0) {
            Logger.getLogger(AnnotationManagerImpl.class.getName()).log(
                    Level.WARNING, " no data");
            return;
        }
        if (!fullChrom) {
            _data = (List<? extends RegionAnnotation>) PlotLib.getSublist(
                    _data, chromId,
                    firstPos, secondPos);

        }

        Composite originalComposite = g.getComposite();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, anno.getTransparency().floatValue()));

        int y1, y2;
        // todo color
        Color color = new Color(anno.getColor().intValue());
        Color _color = null;
        for (RegionAnnotation current : _data) {
            if (current.getChromEnd() < firstPos) {
                continue;
            }
            if (current.getChromStart() > secondPos) {
                continue;
            }
            y1 = (int) (y0 + (((current.getChromStart() - firstPos) < 0 ? 0 : (current.getChromStart() - firstPos) / yScale)));
            // 021012 bug draw behind second border
            y2 = (int) (y0 + (((current.getChromEnd() > secondPos ? secondPos - firstPos : current.getChromEnd()) - firstPos) / yScale));
            _color = current.getColor();
            // auslassen??
            if (_color == null) {
                _color = color;
            }
            g.setColor(_color);
            g.drawRect(x0, y1, width, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());
            g.fillRect(x0, y1, width, y2 - y1 > 0 ? y2 - y1 : this.getMinSpotHeight());

        }
        g.setComposite(originalComposite);

    }

    public int getMinSpotHeight() {
        return 1;
    }

    public void setMinSpotHeight(int x) {
    }

    public RegionAnnotation getDataAtPos(String chrom, Long pos, Long dist) {

        java.util.List<? extends RegionAnnotation> b = this.getData(chrom);
        return (RegionAnnotation) PlotLib.getDataAtPos(b, chrom, pos, dist);
    }
    /*
    public void setTransparency() {
    }
    
    public float getTransparency(GenomeRelease release, String name) {
    AnnotationList anno = this.getAnnotation(release, name);
    return anno.getTransparency().floatValue();
    }
    
    public Color getColor(GenomeRelease release, String name) {
    AnnotationList anno = this.getAnnotation(release, name);
    return this.getColor(anno);
    }
    
    public Color getColor(AnnotationList anno) {
    return new Color((int) anno.getColor().longValue());
    }
    
    public void setColor(Color c) {
    throw new UnsupportedOperationException("Not supported yet.");
    }
    public String defaultColorDesc(AnnotationList anno) {
    return new String("<html><font color=\"" +
    this.getColor(anno) + "\"> all values </font></html>");
    }
    
    public String getColorDesc(AnnotationList anno) {
    RegionAnnotation iAnno = ServiceAnnotationManager.getRegionInstance(
    anno.getClazz());
    String htmlText = iAnno.getColorDesc();
    if (htmlText != null) {
    return htmlText;
    } else {
    return defaultColorDesc(anno);
    }
    }
    
    public String getColorDesc(GenomeRelease release, String name) {
    AnnotationList anno = this.getAnnotation(release, name);
    return this.getColorDesc(anno);
    }
     */
 /*
    public class AnnKey {
    
    public final GenomeRelease release;
    public final String name;
    public final String clazz;
    public final String table;
    
    public AnnKey(GenomeRelease release, String name, String clazz, String table) {
    this.release = release;
    this.name = name;
    this.clazz = clazz;
    this.table = table;
    }
    
    public String getName() {
    return name;
    }
    
    public GenomeRelease getRelease() {
    return release;
    }
    
    public String getClazz() {
    return clazz;
    }
    
    public String getTable() {
    return table;
    }
    
    @Override
    public boolean equals(Object o) {
    if (!(o instanceof AnnKey)) {
    return false;
    }
    return this.getName().equals(((AnnKey) o).getName()) &&
    this.getRelease().equals(((AnnKey) o).getRelease());
    //return true if all four fields are equal
    }
    
    @Override
    public int hashCode() {
    return release.toString().concat(name).hashCode();
    }
    }
     */
}
