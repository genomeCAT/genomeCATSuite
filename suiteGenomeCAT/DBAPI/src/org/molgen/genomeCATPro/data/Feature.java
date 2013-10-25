package org.molgen.genomeCATPro.data;

import java.util.Comparator;
import java.util.List;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 * @name Feature
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

public interface Feature extends Region {

    public List<? extends Feature> loadFromDB(Data d) throws Exception;

    public String getId();

    public void setId(String id);

    public void setRatio(double ratio);

    public double getRatio();

    public boolean isAberrant();

    public void setIfAberrant(int i);

    public int getIfAberrant();
    /**comparator used to sort Feature/BACs according to ratio*/
    public static final Comparator<Feature> comRatio = new Comparator<Feature>() {

        public int compare(Feature r1, Feature r2) {

            if (r1.getRatio() > r2.getRatio()) {
                return 1;
            } else if (r1.getRatio() == r2.getRatio()) {
                return 0;
            } else {
                return -1;
            }
        }
    };

    public String getCreateTableSQL(Data d);

    public String getInsertSQL(Data d);
}
