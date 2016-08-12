package org.molgen.genomeCATPro.annotation;

/**
 * @name RegionImpl
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Jan 19, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
/**
 *
 * log
 */
public class RegionImpl implements Region {

    @Id
    @Column(name = "name", nullable = false)
    public String name;
    @Column(name = "chrom", nullable = false)
    String chrom;
    @Column(name = "chromStart", nullable = false)
    long chromStart;
    @Column(name = "chromEnd", nullable = false)
    long chromEnd;

    public RegionImpl() {
    }

    public RegionImpl(String name, String chrom, long chromStart, long chromEnd) {
        this.name = name;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getChrom() {
        return chrom;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    public long getChromEnd() {
        return chromEnd;
    }

    public void setChromEnd(long chromEnd) {
        this.chromEnd = chromEnd;
    }

    public long getChromStart() {
        return chromStart;
    }

    public void setChromStart(long chromStart) {
        this.chromStart = chromStart;
    }

    public String toHTMLString() {
        return getName();
    }

    public int compareTo(Object o) {
        RegionImpl r;
        if (o instanceof RegionImpl) {
            r = (RegionImpl) o;
        } else {
            throw new java.lang.RuntimeException("No RegionImpl");
        }

        if (this.getChromEnd() > r.getChromEnd()) {
            return 1;
        } else if (this.getChromEnd() < r.getChromEnd()) {
            return -1;
        } else {
            return 0;
        }
    }
    public final static String ICON_PATH = "org/molgen/genomeCATPro/annotation/page_16.png";

    public String getIconPath() {
        return RegionImpl.ICON_PATH;
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
        if (!(object instanceof RegionImpl)) {
            return false;
        }
        RegionImpl other = (RegionImpl) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    public boolean equalsByPos(Region r2) {
        return (this.getChrom().contentEquals(r2.getChrom()) && this.getChromStart() == r2.getChromStart() && this.getChromEnd() == r2.getChromEnd());

    }

    public String toString() {
        return new String(getChrom() + ":" + getChromStart() + "-" + getChromEnd());
    }

    public String toFullString() {
        return new String(this.getName() + " " + this.toString());
    }
}
