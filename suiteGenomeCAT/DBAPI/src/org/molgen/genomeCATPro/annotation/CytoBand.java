package org.molgen.genomeCATPro.annotation;

import java.util.Comparator;

/**
 * @name CytoBand
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>.
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
public interface CytoBand extends RegionAnnotation {

    Comparator<CytoBand> findByStart = new Comparator<CytoBand>() {

        public int compare(CytoBand r1, CytoBand r2) {

            return (int) (r1.getChromStart() - r2.getChromStart());
        }
    };
    Comparator<CytoBand> maxEnd = new Comparator<CytoBand>() {

        public int compare(CytoBand r1, CytoBand r2) {

            if (r1.getChromEnd() > r2.getChromEnd()) {
                return 1;
            } else if (r1.getChromEnd() < r2.getChromEnd()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    @Override
    boolean equals( Object object);

    String getGieStain();

    String getName();

    @Override
    int hashCode();

    void setGieStain( String gieStain);

    void setName(String name);

    @Override
    String toString();
}
