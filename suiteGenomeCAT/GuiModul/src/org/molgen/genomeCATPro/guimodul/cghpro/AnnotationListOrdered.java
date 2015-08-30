package org.molgen.genomeCATPro.guimodul.cghpro;

import java.awt.Color;
import java.awt.Image;
import org.molgen.genomeCATPro.annotation.RegionAnnotation;
import org.molgen.genomeCATPro.annotation.RegionAnnotationImpl;
import org.molgen.genomeCATPro.annotation.ServiceAnnotationManager;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.AnnotationList;

/**
 * @name AnnotationListOrdered
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
public class AnnotationListOrdered extends AnnotationList {

    private int no = 0;
    RegionAnnotation singleRegion = null;

    public AnnotationListOrdered() {
    }

    public AnnotationListOrdered(AnnotationList anno, Integer no) {
        super(anno);
        this.no = no;


    }

    private RegionAnnotation getSingleRegion() {
        if (this.singleRegion == null) {
            this.singleRegion = ServiceAnnotationManager.getRegionInstance(
                    getClazz());
        }
        return this.singleRegion;
    }
    String schema = null;

    public String getSchema() {
        if (schema == null) {

            this.schema = this.getSingleRegion().getColorDesc();
        }
        return schema;
    }

    public void setSchema(String schema) {
    }
    Image icon = null;

    public Image getIcon() {
        if (this.icon == null) {

            this.icon = this.getSingleRegion().getColorImage();
            if (this.icon == null) {
                this.icon = RegionAnnotationImpl.getDefaultImage(
                        new Color(this.getColor().intValue()));
            }
        }
        return this.icon;
    }

    public void setIcon(Image icon) {
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
    public String getAlphaNo(){
        return Utils.intToAlpha(no);
    }
    public void seAlphaNo(String d){
        
    }
}
