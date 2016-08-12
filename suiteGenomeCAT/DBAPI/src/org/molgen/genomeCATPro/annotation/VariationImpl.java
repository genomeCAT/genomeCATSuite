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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

/**
 *
 * @author tebel
 */
@Deprecated
public class VariationImpl extends RegionAnnotationImpl implements Variation {

    @Column(name = "varType", nullable = false)
    private String type;
    @Column(name = "method", nullable = false)
    private String method;
    @Column(name = "refrence", nullable = false)
    private String refrence;
    @Column(name = "itemRgb", nullable = false)
    private long color;

    public VariationImpl() {
    }

    public VariationImpl(String name) {
        this.name = name;
    }

    public VariationImpl(String name, long bin,
            String chrom, long chromStart, long chromEnd,
            String type, String method, String refrence, long color) {
        this.name = name;
        this.bin = bin;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;

        this.type = type;
        this.method = method;
        this.refrence = refrence;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRefrence() {
        return refrence;
    }

    public void setRefrence(String refrence) {
        this.refrence = refrence;
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
        if (!(object instanceof VariationImpl)) {
            return false;
        }
        VariationImpl other = (VariationImpl) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String id) {
        this.name = id;
    }

    public void setColor(long color) {
        this.color = color;
    }

    public List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException {

        Connection con = null;

        con = Database.getDBConnection(CorePropertiesMod.props().getDb());

        Statement s = con.createStatement();

        ResultSet r = s.executeQuery(
                "SELECT name, chrom, chromStart, chromEnd,"
                + " varType, reference, method, "
                + " bin, itemRgb from "
                + table + " where "
                + " chrom = \'" + chromId + "\'"
                + " order by chromEnd");

        Vector<VariationImpl> _data = new Vector<VariationImpl>();
        while (r.next()) {

            _data.add(new VariationImpl(
                    r.getString("name"),
                    r.getLong("bin"),
                    r.getString("chrom"),
                    r.getLong("chromStart"),
                    r.getLong("chromEnd"),
                    r.getString("varType"),
                    r.getString("method"),
                    r.getString("reference"),
                    r.getLong("itemRGB")));
        }
        return _data;
    }

    public Color getColor() {
        return new Color((int) this.color);
    }

    @Override
    public int compareTo(Object o) {
        Variation r;
        if (o instanceof Variation) {
            r = (Variation) o;
        } else {
            throw new java.lang.RuntimeException("No Region");
        }

        if (this.getChromEnd() > r.getChromEnd()) {
            return 1;
        } else if (this.getChromEnd() < r.getChromEnd()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toHTMLString() {
        return new String(this.getRefrence() + " " + this.getType());
    }
    static String _colorDesc = new String("<html>  inversions and inversion breakpoints are purple.<br/>"
            + "CNVs and InDels are blue if there is a gain in size relative to the reference.<br/>"
            + "CNVs and InDels are red if there is a loss in size relative to the reference.<br/>"
            + "CNVs and InDels are brown if there are reports of both a loss and a gain in size relative to the reference.</html>");

    @Override
    public String getColorDesc() {
        return VariationImpl._colorDesc;

    }
    static BufferedImage _icon = null;

    @Override
    public Image getColorImage() {
        if (_icon == null) {
            VariationImpl.paintImage();
        }
        return _icon;
    }
    //

    static void paintImage() {
        Logger.getLogger(VariationImpl.class.getName()).log(Level.INFO,
                "paintImage");
        _icon = new BufferedImage(RegionAnnotation.lengthImage,
                RegionAnnotation.heightImage,
                BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = _icon.createGraphics();

        int x = 0;
        long values[] = new long[]{0, 200, 9127187, 13107200, 13107400};
        int width = RegionAnnotation.lengthImage / values.length;
        Color c = null;

        for (long d : values) {

            c = new Color((int) d);
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
