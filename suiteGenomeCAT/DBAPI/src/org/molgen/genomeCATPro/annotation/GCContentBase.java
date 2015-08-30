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
import javax.persistence.Transient;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

/**
 *
 * @author tebel
 */
@Entity
public class GCContentBase extends RegionAnnotationImpl implements RegionAnnotation {

    @Transient
    double value;
    @Transient
    final static DecimalFormat myFormatter = new DecimalFormat("0.###");

    public GCContentBase() {
    }

    public GCContentBase(String name) {
        this.name = name;
    }

    public GCContentBase(String name, long bin,
            String chrom, long chromStart, long chromEnd, double value) {
        this.name = name;
        this.bin = bin;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
        this.value = value;

    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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
        if (!(object instanceof GCContentBase)) {
            return false;
        }
        GCContentBase other = (GCContentBase) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public Color getColor() {
        return GCContentBase.getColor(this.value);
    }

    @Override
    public String toHTMLString() {
        return new String("GCContent: " + myFormatter.format(this.getValue()));
    }

    @Override
    public List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException {


        Connection con = null;


        con = Database.getDBConnection(Defaults.localDB);

        Statement s = con.createStatement();

        ResultSet r = s.executeQuery(
                "SELECT name, bin, chrom, chromStart, chromEnd," +
                " sumData, validCount from " +
                table + " where " +
                " chrom = \'" + chromId + "\'" +
                " order by chromEnd");

        Vector<GCContentBase> _data = new Vector<GCContentBase>();
        while (r.next()) {

            _data.add(new GCContentBase(
                    r.getString("name"),
                    r.getLong("bin"),
                    r.getString("chrom"),
                    r.getLong("chromStart"),
                    r.getLong("chromEnd"),
                    r.getDouble("sumData") / r.getLong("validCount")));
        }
        return _data;
    }

    @Override
    public int compareTo(Object o) {
        GCContentBase r;
        if (o instanceof GCContentBase) {
            r = (GCContentBase) o;
        } else {
            throw new java.lang.RuntimeException("No GCContentBase");
        }
        return Double.compare(this.getValue(), r.getValue());
    }

    @Override
    public String getColorDesc() {
        /**
         *   if (this.value < 40) {
        factor = 0;
        } else if (this.value > 50) {
        factor = 1;
        } else {
        factor = (int) (50 - this.value / 10);
        }
         */
        return new String("<html> " +
                "gray : < 40% GC content <br/>" +
                "gray to light green : 40 - 50% GC content <br/> " +
                "light green: > 50% GC content</html>");
    }

    public static Color getColor(double _value) {
        double factor;
        if (_value < 40) {
            factor = 1;
        } else if (_value >= 50) {
            factor = 0;
        } else {
            factor = ((50 - _value) / 10);
        }
        return new Color(155, (int) (255 - (100 * factor)), 155);

    }
    // static for implementation
    static BufferedImage _icon = null;

    @Override
    public Image getColorImage() {
        if (_icon == null) {
            GCContentBase.paintImage();
        }
        return _icon;
    }

    static void paintImage() {
        Logger.getLogger(GCContentBase.class.getName()).log(Level.INFO,
                "paintImage");
        _icon = new BufferedImage(RegionAnnotation.lengthImage,
                RegionAnnotation.heightImage,
                BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = _icon.createGraphics();

        int x = 0;
        Double values[] = new Double[]{39.0, 40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0, 47.0, 48.0, 49.0, 50.0};
        int width = RegionAnnotation.lengthImage / values.length;
        Color c = null;

        for (Double d : values) {

            c = GCContentBase.getColor(d.doubleValue());
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
    
