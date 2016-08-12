package org.molgen.genomeCATPro.annotation;

/**
 * @name RegionAnnotationImpl
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

@MappedSuperclass

public class RegionAnnotationImpl extends RegionImpl implements RegionAnnotation {

    @Column(name = "bin", nullable = false)
    long bin;
    // name of the region

    public RegionAnnotationImpl() {
    }

    public RegionAnnotationImpl(String name,
            String chrom, long start, long stop,
            long bin) {
        super(name, chrom, start, stop);

        this.bin = bin;
        //this.color = color;
    }

    @Override
    public String getChrom() {
        return chrom;
    }

    public long getBin() {
        return bin;
    }

    public void setBin(long bin) {
        this.bin = bin;
    }

    @Override
    public String toString() {
        return new String(getName());
    }

    public List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException {

        Connection con = null;

        con = Database.getDBConnection(CorePropertiesMod.props().getDb());

        Statement s = con.createStatement();

        ResultSet r = s.executeQuery(
                "SELECT name, chrom, chromStart, chromEnd, bin "
                + " from " + table + " where "
                + " chrom = \'" + chromId + "\'"
                + " order by chromEnd");

        Vector<RegionAnnotationImpl> _data = new Vector<RegionAnnotationImpl>();
        while (r.next()) {

            _data.add(new RegionAnnotationImpl(
                    r.getString("name"),
                    r.getString("chrom"),
                    r.getLong("chromStart"),
                    r.getLong("chromEnd"),
                    r.getLong("bin")));
        }
        return _data;
    }

    @Override
    public int compareTo(Object o) {
        RegionAnnotationImpl c;
        if (o instanceof RegionAnnotationImpl) {
            c = (RegionAnnotationImpl) o;
        } else {
            throw new java.lang.RuntimeException("No RegionAnnoationImpl");
        }

        if (this.getChromEnd() > c.getChromEnd()) {
            return 1;
        } else if (this.getChromEnd() < c.getChromEnd()) {
            return -1;
        } else {
            return 0;
        }
    }

    public boolean hasGeneView() {
        return false;
    }

    public boolean hasRegionView() {
        return true;
    }

    public boolean hasProbeView() {
        return false;
    }

    public String getRatioColName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getGeneColName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getProbeColName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getColorDesc() {

        return new String("<html>no schema</html>");
    }

    public Color getColor() {
        return null;
    }

    public Image getColorImage() {
        return null;
    }

    public static Image getDefaultImage(Color c) {

        BufferedImage _icon = new BufferedImage(RegionAnnotation.lengthImage,
                RegionAnnotation.heightImage,
                BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = _icon.createGraphics();

        g.setColor(c);

        //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)-y));
        g.fillRect(0, 0, _icon.getWidth(), _icon.getHeight());

        //g.fillRect(0, (Defines.ARRAY_HEIGTH / 2) + y, 10, 1);
        //System.out.println("j: " + j + " y: " + ((Defines.ARRAY_HEIGTH/2)+y));
        return _icon;
    }
    //
}
