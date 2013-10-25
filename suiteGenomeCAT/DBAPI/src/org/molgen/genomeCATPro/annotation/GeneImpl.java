package org.molgen.genomeCATPro.annotation;

/**
 * @name GeneImpl
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package.
 * Katrin Tebel <tebel at molgen.mpg.de>.
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;

@Entity
public class GeneImpl extends RegionAnnotationImpl implements RegionAnnotation {

    public static String nameId = "Gene";
    @Column(name = "name2", nullable = false)
    private String name2;
    @Column(name = "txStart", nullable = false)
    private long txStart;
    @Column(name = "txEnd", nullable = false)
    private long txEnd;
    @Column(name = "kgID", nullable = false)
    private String kgID;
    @Column(name = "geneSymbol", nullable = false)
    private String geneSymbol;
    @Lob
    @Column(name = "description")
    private String description;

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public String getName2() {
        return name2;
    }

    public GeneImpl() {
    }

    public GeneImpl(String name) {
        this.name = name;
    }

    public GeneImpl(String name, String name2, String kgID, String description,
            long bin, String chrom, long txStart, long txEnd, String symbol) {
        this.name = name;
        this.name2 = name2;
        this.kgID = kgID;
        this.description = description;
        this.bin = bin;
        this.chrom = chrom;
        this.txStart = txStart;
        this.txEnd = txEnd;
        this.geneSymbol = symbol;


    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public long getChromEnd() {
        return this.txEnd;
    }

    @Override
    public long getChromStart() {
        return this.txStart;
    }

    @Override
    public void setChromEnd(long chromEnd) {
        this.txEnd = chromEnd;
    }

    @Override
    public void setChromStart(long chromStart) {
        this.txStart = chromStart;
    }

    @Override
    public String toHTMLString() {
        return new String(this.getName2() + " " + this.getGeneSymbol());
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneImpl)) {
            return false;
        }
        GeneImpl other = (GeneImpl) object;
        if ((this.name2 == null && other.name2 != null) || (this.name2 != null && !this.name2.equals(other.name2))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name2 != null ? name2.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        GeneImpl c;
        if (o instanceof GeneImpl) {
            c = (GeneImpl) o;
        } else {
            throw new java.lang.RuntimeException("No GeneImpl");
        }

        if (this.getChromEnd() > c.getChromEnd()) {
            return 1;
        } else if (this.getChromEnd() < c.getChromEnd()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException {
        Connection con = null;


        con = Database.getDBConnection(Defaults.localDB);

        Statement s = con.createStatement();

        ResultSet r = s.executeQuery(
                "SELECT name,name2,kgID,description, bin,strand, chrom, txStart, txEnd,  geneSymbol " +
                "  from " +
                table + " where " +
                " chrom = \'" + chromId + "\'" +
                " order by greatest(txStart, txEnd)");

        Vector<GeneImpl> _data = new Vector<GeneImpl>();
        while (r.next()) {

            _data.add(new GeneImpl(
                    r.getString("name"),
                    r.getString("name2"),
                    r.getString("kgID"),
                    r.getString("description"),
                    r.getLong("bin"),
                    r.getString("chrom"),
                    r.getLong("txStart"),
                    r.getLong("txEnd"),
                    r.getString("geneSymbol"))
                    );
        }
        return _data;

    }
}
