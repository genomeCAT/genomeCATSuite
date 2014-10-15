package org.molgen.genomeCATPro.cat.maparr;

/** * @(#)ArrayAnnoView.java 
 * 
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.annotation.AnnotationManager;
import org.molgen.genomeCATPro.annotation.AnnotationManagerImpl;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.cat.util.Defines;

/**
 * visualize Annotation Data with CAT VIEW
 * @author tebel
 */
public class ArrayAnnoView extends ArrayView {

    AnnotationManager am = null;

    public ArrayAnnoView(ArrayData d, ChromTab chromtab) throws Exception {
        super(d, chromtab);
        anno = new BufferedImage((int) (Defines.ARRAY_HEIGTH * 0.5), Defines.ARRAY_WIDTH, BufferedImage.TYPE_INT_ARGB_PRE);

        am = new AnnotationManagerImpl(this.release, this.dataName);
    }

    public ArrayAnnoView(ArrayData d,
            Vector<String> names, Vector<Long> start, Vector<Long> stop,
            Vector<Double> data, ChromTab chromtab) {
        super(d, names, start, stop, data, chromtab);
        anno = new BufferedImage((int) (Defines.ARRAY_HEIGTH * 0.5), Defines.ARRAY_WIDTH, BufferedImage.TYPE_INT_ARGB_PRE);

        am = new AnnotationManagerImpl(this.release, this.dataName);
    }

    @Override
    void showDetailData( long pos) {
        Long dist = (long) Math.floor(2 * chromtab.scale_x);

        Region curr = this.am.getDataAtPos(this.chromtab.chrom, pos, dist);


        //this.parentFrame.showDetailData(curr);

        if (parent != null) {
            if (curr != null) {
                parent.setDetailPos(
                        curr.toHTMLString(),
                        curr.getChrom(),
                        curr.getChromStart(),
                        curr.getChromEnd());
            } else {
                parent.setDetailPos("", "", 0, 0);
            }
        }
    }
    // geneId

    @Override
    void LoadArrayChrom() {
        try {
            // load Manager
            Logger.getLogger(ArrayAnnoView.class.getName()).log(Level.INFO,
                    "LoadArrayChrom");
        } catch ( Exception e) {
            e.printStackTrace();
        //e.printStackTrace();
        }
        if (arrayRatio.size() == 0) {
            if (arrayRatio.size() == 0) {
                arrayRatio.add(0.0);
                arrayStart.add(new Long(0));
                arrayStop.add(new Long(0));
                arrayName.add("no data");
            //throw new RuntimeException("no data found for " + arrayId + " chrom: " + chromtab.chrom);

            }
        }
    }

    @Override
    void initView() {
        super.initView();
    //setPreferredSize(new Dimension(Defines.ARRAY_WIDTH, (int) (Defines.ARRAY_HEIGTH * 0.5)));
    //setMaximumSize(new Dimension(Defines.ARRAY_WIDTH, (int) (Defines.ARRAY_HEIGTH * 0.5)));
    }

    static ArrayData getArrayDataAnno(String release, String name) {

        Vector w;
        Class clazz = ArrayAnnoView.class;
        ArrayData ad = ArrayData.createArrayDataAnno(clazz, release, name);
        return ad;
    }
    BufferedImage anno = null;

    @Override
    public void paint(Graphics g) {
        System.out.println("Anno paint");
        // paintColorScale(color);
        //g.setBackground(Color.black);
        //JPanel.this.paint(g);
        if (am == null) {
            System.out.println("no anno manager");
            return;
        }
        g.drawLine(ChromTab.off_legend, Defines.ARRAY_HEIGTH / 4, ChromTab.off_legend + (int) ChromTab.view_max_x, Defines.ARRAY_HEIGTH / 4);
        Graphics2D g2 = (Graphics2D) g;
        //gm.rotate(Math.toRadians(90), Defines.ARRAY_WIDTH/2, Defines.ARRAY_HEIGTH/2);

        Graphics2D gm = anno.createGraphics();
        gm.setBackground(this.getBackground());
        gm.clearRect(0, 0, anno.getWidth(), anno.getHeight());


        am.plot(gm,
                this.chromtab.chrom,
                true,
                this.chromtab.pos_off_x, chromtab.pos_max_x,
                Defines.ARRAY_HEIGTH / 8, //left
                0, // top,
                Defines.ARRAY_HEIGTH / 4, // width -- no zoom
                chromtab.scale_x);



        AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate(0, Defines.ARRAY_HEIGTH * 0.5 / 2);
        affineTransform.rotate(-Math.toRadians(90));
        
        affineTransform.translate(-Defines.ARRAY_HEIGTH * 0.5 / 2, ChromTab.off_legend);

        g2.drawImage(anno, affineTransform, this);
        if (this.parent.isShowRuler()) {
            g.drawLine(
                    (int) super.chromtab.getMousepos(), 0,
                    (int) super.chromtab.getMousepos(), (int) (Defines.ARRAY_HEIGTH * 0.5));
        }

    }
}	
