package org.molgen.genomeCATPro.annotation;
/**
 * @name RegionAnnotation
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
import java.awt.Image;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * 
 * genomic region plus color and bin info
 */
public interface RegionAnnotation extends Region, RegionArray {

    static int lengthImage = 100;
    static int heightImage = 20;

    public Image getColorImage();

    public String getColorDesc();
    

    Color getColor();

    long getBin();

    void setBin(long bin);
    
    //String getAnnoField();
    

    public List<? extends RegionAnnotation> dbLoadRegions(String table, String chromId) throws SQLException;
}
