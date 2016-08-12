package org.molgen.genomeCATPro.annotation;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

/**
 *
 * @author tebel
 */
@MappedSuperclass

public class LCRImpl extends RegionAnnotationImpl implements RegionAnnotation {

    @Transient
    final static DecimalFormat myFormatter = new DecimalFormat("0.###");

    @Transient
    double value;

    public LCRImpl() {
    }

    public LCRImpl(String name) {
        this.name = name;
    }

    public LCRImpl(String name, long bin,
            String chrom, long chromStart, long chromEnd, double value) {
        this.name = name;
        this.bin = bin;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
        this.value = value;

    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LCRImpl)) {
            return false;
        }
        LCRImpl other = (LCRImpl) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toHTMLString() {
        return new String("fracMatch: " + myFormatter.format(this.value));
    }

    /*
     * Light to dark gray: 90 - 98% similarity
     * Light to dark yellow: 98 - 99% similarity
     * Light to dark orange: greater than 99% similarity
     * Red: duplications of greater than 98% similarity 
    that lack sufficient Segmental Duplication Database evidence (most likely missed overlaps) 
    
     */
    @Override
    public Color getColor() {
        return LCRImpl.getColor(this.value);
    }

    @Override
    public List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException {

        Connection con = null;

        con = Database.getDBConnection(CorePropertiesMod.props().getDb());

        Statement s = con.createStatement();

        ResultSet r = s.executeQuery(
                "SELECT name, bin, chrom, chromStart, chromEnd,"
                + " fracMatch from "
                + table + " where "
                + " chrom = \'" + chromId + "\'"
                + " order by chromEnd");

        Vector<LCRImpl> _data = new Vector<LCRImpl>();
        while (r.next()) {

            _data.add(new LCRImpl(
                    r.getString("name"),
                    r.getLong("bin"),
                    r.getString("chrom"),
                    r.getLong("chromStart"),
                    r.getLong("chromEnd"),
                    r.getDouble("fracMatch")));
        }
        return _data;
    }

    @Override
    public int compareTo(Object o) {
        LCRImpl c;
        if (o instanceof LCRImpl) {
            c = (LCRImpl) o;
        } else {
            throw new java.lang.RuntimeException("No LCRImpl");
        }

        if (this.getChromEnd() > c.getChromEnd()) {
            return 1;
        } else if (this.getChromEnd() < c.getChromEnd()) {
            return -1;
        } else {
            return 0;
        }
    }

    public String getColorDesc() {

        return new String("<html>"
                + "Light to dark gray:   90 - 98%  similarity <br/>"
                + "Light to dark yellow: 98 - 99%  similarity <br/>"
                + "light to dark orange: 99 - 100% similarity</html>");

    }

    public static Color getColor(double _value) {

        double factor;
        //value = fracMatch
        //min: 0.89999997615814 max: 1 average: 0.94057893909976 
        try {
            if (_value >= 0.90 && _value < 0.98) {
                // * Light to dark gray: 90 - 98% similarity
                factor = ((0.98 - _value) * 100 / 8);
                return new Color(
                        (int) (64 + ((192 - 64) * factor)),
                        (int) (64 + ((192 - 64) * factor)),
                        (int) (64 + ((192 - 64) * factor)));
            } else if (_value >= 0.98 && _value < 0.99) {
                // yellow -> light to dark yellow 
                // blue 200 - 100
                return new Color(255, 255,
                        (int) (100 + ((0.99 - _value)) * 100 * 100));
                //return new Color(255, 255, 0);
            } else if (_value >= 0.99) {
                // orange -> light to dark
                // blue 0 ->green  200 - 100
                //255 	 204  	 153  
                //255 	 102   	 0 

                return new Color(255,
                        (int) (100 + ((1 - _value) * 100 * 100)), 0);
                //return new Color(255, 0, 100);
            }
        } catch (Exception e) {
            Logger.getLogger(LCRImpl.class.getName()).log(Level.INFO,
                    "getColor", e);
        }
        return Color.BLACK;

    }
    // static for implementation
    static BufferedImage _icon = null;

    @Override
    public Image getColorImage() {
        if (LCRImpl._icon == null) {
            LCRImpl.paintImage();
        }
        return LCRImpl._icon;
    }

    static void paintImage() {
        Logger.getLogger(LCRImpl.class.getName()).log(Level.INFO,
                "paintImage");
        _icon = new BufferedImage(RegionAnnotation.lengthImage,
                RegionAnnotation.heightImage,
                BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = _icon.createGraphics();

        int x = 0;
        Double values[] = new Double[]{
            0.90, 0.93, 0.96, 0.979,
            0.98, 0.983, 0.986, 0.989,
            0.99, 0.993, 0.996, 0.999
        };
        int width = RegionAnnotation.lengthImage / values.length;
        Color c = null;

        for (Double d : values) {

            c = LCRImpl.getColor(d.doubleValue());
            // print farbscale
            g.setColor(c);

            //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)-y));
            g.fillRect(x, 0, width, _icon.getHeight());
            x += width;

            //g.fillRect(0, (Defines.ARRAY_HEIGTH / 2) + y, 10, 1);
            //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)+y));
        }
    }
}
