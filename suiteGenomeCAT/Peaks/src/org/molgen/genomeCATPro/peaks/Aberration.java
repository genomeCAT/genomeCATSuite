/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks;

/**
 *
 * @author tebel
 */
import java.io.Serializable;
import java.util.Comparator;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.data.IFeature;

/**
 * @name Aberration
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>. The contents of
 * this file are subject to the terms of either the GNU General Public License
 * Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * at http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
public interface Aberration extends IFeature, Serializable {

    @Transient
    String DELETION = "Deletion";
    @Transient
    String DUPLICATION = "Duplication";
    public static final Comparator<Aberration> compByQuality = new Comparator<Aberration>() {

        public int compare(Aberration a1, Aberration a2) {

            return Double.compare(a1.getQuality(), a2.getQuality());
        }
    };
    public static final Comparator<Aberration> compByRatio = new Comparator<Aberration>() {

        public int compare(Aberration a1, Aberration a2) {
            return Double.compare(a1.getRatio(), a2.getRatio());
        }
    };
    public static final Comparator<Aberration> compByLengthDesc = new Comparator<Aberration>() {

        public int compare(Aberration a1, Aberration a2) {

            long l1 = (a1.getChromStart() - a1.getChromEnd());
            long l2 = (a2.getChromStart() - a2.getChromEnd());

            return (int) (l2 - l1);
        }
    };
    public static final Comparator<Aberration> compByLength = new Comparator<Aberration>() {

        public int compare(Aberration a1, Aberration a2) {

            long la1 = a1.getChromEnd() - a1.getChromStart();
            long la2 = a2.getChromEnd() - a2.getChromStart();
            if (la1 > la2) {
                return -1;
            }
            if (la1 < la2) {
                return 1;
            }
            int rc = (int) (a1.getChromStart() - a2.getChromStart());
            if (rc == 0) {
                return (int) (a1.getChromEnd() - a2.getChromEnd());
            } else {
                return 0;
            }
        }
    };

    void addFeature(String cloneId, Long chromEnd, double ratio);

    int compareByAberrationId(Aberration a);

    String getPeakId();

    Integer getCount();

    String getFirstPeakId();

    String getType();

    String getTrackId();

    double getQuality();

    double getRatio();

    String getLastPeakId();

    int getXDispColumn();

    boolean isSelected();

    void setPeakId(String caseId);

    void setCount(Integer count);

    void setLastPeakId(String endPeak);

    void setType(String genotype);

    void setTrackId(String id);

    void setQuality(double quality);

    void setRatio(double r);

    void setSelected(boolean selected);

    void setFirstPeakId(String startId);

    void setXDispColumn(int XDispColumn);

    boolean isHidden();

    void setHidden(boolean hide);
}
