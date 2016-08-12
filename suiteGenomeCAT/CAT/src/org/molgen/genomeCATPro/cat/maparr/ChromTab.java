package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name ChromTab
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import java.util.Vector;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.cat.util.Defines;
import org.molgen.genomeCATPro.common.Defaults;

/**
 * 081013 kt bug empty release class ChromTab tabular view for one chromsome,
 * containing n arrays
 */
public class ChromTab extends JPanel {

    /**
     * list of all chromosomes
     */
    private static Hashtable<String, String[]> chroms = new Hashtable<String, String[]>();
    /**
     * this chrom name
     */
    public String chrom;
    public Defaults.GenomeRelease release;
    /**
     * this chrom index
     */
    int i;
    long pos_first_x = Long.MAX_VALUE;
    long pos_max_x = 0;
    double scale_x = 0.0;
    long pos_ruler = 0;
    /**
     * space for legend (i.e. color scale)
     */
    static final int off_legend = Defines.ARRAY_WIDTH / 20;
    /**
     * view width
     */
    static final double view_max_x = Defines.ARRAY_WIDTH - (2 * off_legend);
    /**
     * maximal chromosomal position acc. to real data
     */
    double pos_real_max_x = 0;

    /**
     * create empty new chromtabs
     */
    public static Vector<ChromTab> createChromTabs(ArrayFrame parent) {

        ChromTab chromTab;

        Vector<ChromTab> vChromTabs = new Vector<ChromTab>();

        //parent.background.setVisible(false);
        //parent.tabbedPane.setVisible(true);
        initChroms(parent.release);

        for (int i = 0; i < ChromTab.chroms.get(parent.release.toString()).length; i++) {

            chromTab = new ChromTab(ChromTab.chroms.get(parent.release.toString())[i], parent);
            chromTab.setLayout(new BoxLayout(chromTab, BoxLayout.Y_AXIS));
            //chromTab.setLayout(new GridLayout(0, 1, 10, 10));
            //chromTab.add(chromTab.getRuler());
            vChromTabs.add(chromTab);
        }
        return vChromTabs;
    }

    /**
     * create new ChromTab for only one chromosome
     *
     * @param chrom
     * @param parent
     * @return
     */
    @Deprecated
    public static ChromTab createSingleChromTab(String chrom, ArrayFrame parent) {

        ChromTab chromTab = new ChromTab(chrom, parent);
        chromTab.setLayout(new BoxLayout(chromTab, BoxLayout.Y_AXIS));
        //chromTab.add(chromTab.getRuler());
        //parent.tabbedPane.add(chromTab);
        return chromTab;
    }

    RulerComponent getRuler(int height, ArrayFrame parent) {
        RulerComponent r = new RulerComponent(height, parent);
        r.addMouseMotionListener(r);
        return r;

    }

    /**
     * get all Chromosome names
     *
     * @return
     */
    public static String[] getChroms(Defaults.GenomeRelease release) {

        initChroms(release);

        return chroms.get(release.toString());
    }

    /**
     * add single new array
     *
     * @param array
     * @param minY
     * @param maxY
     * @param parent
     * @param type
     */
    public static ArrayView[] addArray(
            ArrayData d,
            Vector<ChromTab> vChromtabs,
            Defaults.GenomeRelease release) throws Exception {
        ChromTab chromTab;

        int size = Math.min(chroms.get(release.toString()).length, vChromtabs.size());
        ArrayView[] list = new ArrayView[size];
        initChroms(release);
        try {
            for (int i = 0; i < size; i++) {
                chromTab = vChromtabs.get(i);
                //Component[] clist = chromTab.getComponents();
                //int c = clist.length +1;
                list[i] = ArrayViewBase.getView(d, chromTab);

                //((java.awt.GridLayout) chromTab.getLayout()).setRows(c);
                chromTab.add(list[i]);

            }//chromTab.setPreferredSize(new Dimension(Defines.ARRAY_WIDTH, Defines.ARRAY_HEIGTH*c));

        } catch (Exception e) {
            Logger.getLogger(ChromTab.class.getName()).log(Level.SEVERE,
                    "addArray", e);
            throw e;

        }
        return list;

    }

    public long getMousepos() {
        return this.pos_ruler;
    }

    /**
     * add single new array, create with data
     *
     * @param array
     * @param minY
     * @param maxY
     * @param names
     * @param chromStart
     * @param chromEnd
     * @param data
     * @param parent
     * @param type
     */
    public static ArrayView[] addArray(
            ArrayData d,
            Vector<String>[] names,
            Vector<Long>[] chromStart,
            Vector<Long>[] chromEnd,
            Vector<Double>[] data,
            Vector<ChromTab> vChromtabs,
            Defaults.GenomeRelease release) {

        ChromTab chromTab;

        initChroms(release);
        int size = Math.min(chroms.get(release.toString()).length, vChromtabs.size());
        ArrayView[] list = new ArrayView[size];

        for (int i = 0; i < size; i++) {
            chromTab = vChromtabs.get(i);
            try {
                list[i] = ArrayViewBase.getView(d,
                        names[i], chromStart[i], chromEnd[i], data[i], chromTab);
                chromTab.add(list[i]);

            } catch (Exception e) {
                Logger.getLogger(ChromTab.class.getName()).log(Level.SEVERE,
                        "addArray", e);
            }
        }
        return list;
    }

    /**
     * add single new array, create with 2 data vectors
     *
     * @param array
     * @param minY
     * @param maxY
     * @param names
     * @param chromStart
     * @param chromEnd
     * @param data
     * @param data2
     * @param parent
     * @param type
     */
    public static ArrayView addArray(
            ArrayData d,
            Vector names[],
            Vector[] chromStart,
            Vector[] chromEnd,
            Vector[] data,
            Vector[] data2,
            Vector<ChromTab> vChromtabs,
            Defaults.GenomeRelease release) {
        ChromTab chromTab;

        initChroms(release);
        int size = Math.min(chroms.get(release.toString()).length, vChromtabs.size());
        ArrayView[] list = new ArrayView[size];

        for (int i = 0; i < chroms.get(release.toString()).length; i++) {
            chromTab = vChromtabs.get(i);
            try {
                list[i] = ArrayViewBase.getView(d,
                        names[i], chromStart[i], chromEnd[i], data[i], data2[i], chromTab);
                chromTab.add(list[i]);

            } catch (Exception e) {
                Logger.getLogger(ChromTab.class.getName()).log(Level.SEVERE,
                        "addArray", e);
            }
        }
        return null;
    }

    /**
     *
     * @param v
     * @param iChromTab
     * @param parent
     */
    /*
    public static void filterArray(ArrayView v, String filterArrayId,
    double posThreshold, double negThreshold,
    int iChromTab, javax.swing.JTabbedPane parent) {
    initChroms();
    ChromTab chromTab = (ChromTab) parent.getComponentAt(iChromTab);
    ArrayView filterArray = v.filterArray(filterArrayId, posThreshold, negThreshold, chromTab);
    
    chromTab.add(v);
    
    }
     */
    /**
     * remove array by arrayId
     *
     * @param arrayId
     * @param parent
     */
    public static void removeArrayView(
            Long arrayId,
            Vector<ChromTab> vChromtabs,
            Defaults.GenomeRelease release) {
        ChromTab chromTab;
        ArrayView v1;
        initChroms(release);
        for (int i = 0; i < Math.min(chroms.get(release.toString()).length, vChromtabs.size()); i++) {
            chromTab = vChromtabs.get(i);
            Component[] clist = chromTab.getComponents();
            for (int j = 0; j < clist.length; j++) {
                v1 = (ArrayView) clist[j];
                if (v1.getArrayId().equals(arrayId)) {
                    chromTab.remove(v1);
                }
            }
        }
    }

    public static ArrayView[] getArrayViewsByPos(int pos,
            Vector<ChromTab> vChromtabs, Defaults.GenomeRelease release) {
        ChromTab chromTab;
        initChroms(release);
        int iActChroms = Math.min(chroms.get(release.toString()).length, vChromtabs.size());
        ArrayView[] arrrayList = new ArrayView[iActChroms];
        for (int i = 0; i < iActChroms; i++) {
            chromTab = vChromtabs.get(i);
            arrrayList[i] = (ArrayView) chromTab.getComponent(pos);
        }
        return arrrayList;
    }

    /**
     *
     * get all arrayviews (one per chrom) for one arrayid
     *
     * @param arrayId
     * @param vChromtabs
     * @return
     */
    public static ArrayView[] getArrayView(
            Long arrayId,
            Vector<ChromTab> vChromtabs,
            Defaults.GenomeRelease release) {
        ChromTab chromTab;
        initChroms(release);
        int chromCount = Math.min(chroms.get(release.toString()).length, vChromtabs.size());
        ArrayView v[] = new ArrayView[chromCount];
        for (int i = 0; i < chromCount; i++) {
            chromTab = vChromtabs.get(i);

            Component[] clist = chromTab.getComponents();
            for (int j = 0; j < clist.length; j++) {

                if ((((ArrayView) clist[j]).getArrayId().equals(arrayId))) {
                    v[i] = (ArrayView) clist[j];
                    break;
                }
            }
        }
        return v;
    }

    /**
     *
     * @param sourcePos
     * @param destPos
     * @param parent
     */
    static void changePosArrayView(
            int sourcePos,
            int destPos,
            Vector<ChromTab> vChromtabs,
            Defaults.GenomeRelease release) {
        ChromTab chromTab;
        initChroms(release);
        // all chroms
        for (int i = 0; i < Math.min(chroms.get(release.toString()).length, vChromtabs.size()); i++) {
            chromTab = vChromtabs.get(i);
            Component c = chromTab.getComponent(sourcePos);
            if (c instanceof ArrayView) {
                chromTab.setComponentZOrder(c, destPos);
            }
        }

    }

    /**
     * get list of all arrays in this chromtab
     *
     * @param chromtab
     * @return
     */
    public static ArrayView[] getArrayViews(ChromTab chromtab) {
        Vector<ArrayView> vArrays = new Vector<ArrayView>();

        Component[] clist = chromtab.getComponents();
        for (int i = 0; i < clist.length; i++) {
            if (clist[i] instanceof ArrayView) {
                vArrays.add((ArrayView) clist[i]);
            }
        }
        ArrayView[] arrays = new ArrayView[vArrays.size()];
        vArrays.toArray(arrays);
        return arrays;
    }

    /**
     * create chromtabs, init with arrays
     */
    /*
    public static void createChromTabs(String[] arrays, double minY, double maxY, javax.swing.JTabbedPane  parent, Class type){
    
    initChroms();
    
    ChromTab chromTab;
    for(int i = 0; i < chroms.length; i++){
    
    chromTab = new ChromTab(chroms[i]);
    chromTab.setLayout(new GridLayout(0,1,10,10));
    
    ArrayView v1;
    for(int j = 0; j < arrays.length; j++){
    v1 = ArrayView.getView(type, minY, maxY, arrays[j], chromTab);
    chromTab.add(v1);
    }
    parent.add(chroms[i], chromTab);
    }
    //return chromTab;
    }
     */
    /**
     * create chromtabs, init with arrays, created with data
     */
    /*
    public static void createChromTabs(String array, double minY, double maxY, Vector[] chromStart, Vector[] chromEnd, Vector[] data, 
    javax.swing.JTabbedPane parent, Class type){
    
    initChroms();
    
    
    ChromTab chromTab;
    for(int i = 0; i < chroms.length; i++){
    String[] arrays = new String[1];
    arrays[0] = array;	
    chromTab = new ChromTab(chroms[i]);
    chromTab.setLayout(new GridLayout(0,1,10,10));
    
    chromTab.add(ArrayView.getView(type, minY, maxY, array,
    chromStart[i], chromEnd[i], data[i], chromTab));
    //chromTab.add();
    parent.add(chroms[i], chromTab);
    }
    //return chromTab;
    }
    
    
     */
    /**
     * construct new chromtab for one chromosome
     */
    ChromTab(String chrom, ArrayFrame parent) {
        super();
        parent.addPropertyChangeListener(
                new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ArrayFrame.PROP_SHOWRULER)) {

                    ChromTab.this.repaint();
                }
                if (evt.getPropertyName().equals(ArrayFrame.PROP_CHANGE_RULER)) {

                    ChromTab.this.repaint();
                }
            }
        });

        this.release = parent.release;
        this.chrom = chrom;

        //List<String> chroms = ChromTab.getCytoBandManager().getChroms(release);
        for (int j = 0; j < chroms.get(release.toString()).length; j++) {
            if (chroms.get(release.toString())[j].compareTo(chrom) == 0) {
                this.i = j;
                break;
            }
        }
        this.repaint();

        Logger.getLogger(ChromTab.class.getName()).log(Level.INFO,
                "create chromtab: " + chrom + " id: " + i);

    }

    /*
     * map mouse to genome position
     */
    public Long mapPosition(long mousePos) {

        if (mousePos < off_legend) {
            return new Long(pos_first_x);
        }
        if ((mousePos - off_legend) > view_max_x) {
            //return (pos_off_x + ((int) Math.floor(scale_x * (view_max_x ))));
            return new Long((long) Math.max(this.pos_max_x, pos_real_max_x));
        }

        return new Long((long) (pos_first_x + (scale_x * new Double(mousePos - off_legend))));
    }

    /**
     * initialize names for chromosomes
     */
    static void initChroms(Defaults.GenomeRelease release) {
        //081013    kt  bug empty release
        if (ChromTab.chroms == null || release == null) {
            return;
        }
        if (ChromTab.chroms.get(release.toString()) == null) {
            List<String> _chroms = CytoBandManagerImpl.stGetChroms(release);

            ChromTab.chroms.put(release.toString(), _chroms.toArray(new String[_chroms.size()]));
        }
    }

    /**
     * ruler to show vertical position, red line at top bar, moved by mouse
     * manages vertical dark line for each view
     */
    class RulerComponent extends JPanel implements MouseMotionListener {

        //boolean rulerSelected = false;
        //double stepsize = 1;
        ArrayFrame parent = null;
        long genomPosStart = 0;
        long genomPosEnd = 0;

        RulerComponent(int height, ArrayFrame parent) {

            super();
            this.parent = parent;
            //this.stepsize = parent.getRulerStepSize();
            /*parent.addPropertyChangeListener(
            new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ArrayFrame.PROP_SHOWRULER)) {
            rulerSelected = (Boolean) evt.getNewValue();
            Logger.getLogger(RulerComponent.class.getName()).log(Level.INFO,
            "ruler " + (rulerSelected ? " yes " : "no"));
            ChromTab.this.repaint();
            
            }
            if (evt.getPropertyName().equals(ArrayFrame.PROP_CHANGE_RULER)) {
            //stepsize = ((Double) evt.getNewValue()).doubleValue();
            // Logger.getLogger(ChromTab.class.getName()).log(Level.INFO,
            //       "ruler stepsize " + stepsize);
            ChromTab.this.repaint();
            
            }
            }
            });
             */
            this.setPreferredSize(new Dimension(Defines.ARRAY_WIDTH, height));
        }

        @Override
        /**
         * adapt ruler and ruler legend to detail range
         */
        public void paint(Graphics g) {
            Color c = this.getBackground();
            setBackground(c);
            super.paint(g);
            //long l = 1000 * 1000; // 

            System.out.println("paint ruler");

            g.setColor(Color.black);
            g.setFont(new Font("Monospaced", Font.BOLD, 9));

            this.genomPosStart = (long) ChromTab.this.pos_first_x;
            this.genomPosEnd = (long) Math.max(pos_max_x, pos_real_max_x);

            int stepLog = (int) Math.log10(this.genomPosEnd - this.genomPosStart);

            String unity = (stepLog < 6 ? "[kbp]" : "[MB]");
            int unityLog = (stepLog < 6 ? 3 : 6);

            g.drawString(
                    unity,
                    Defines.ARRAY_WIDTH - (off_legend - 2), (int) (this.getHeight() * 0.3));
            g.drawString(Long.toString(stepLog),
                    Defines.ARRAY_WIDTH - (off_legend - 2), (int) (this.getHeight() * 0.8));
            g.setColor(Color.black);
            this.genomPosStart = (long) (Math.floor(this.genomPosStart / Math.pow(10, stepLog)));
            this.genomPosEnd = (long) (Math.ceil(this.genomPosEnd / Math.pow(10, stepLog)));
            int pix_x;
            int pix_xx;
            int pix_xxx;

            for (long i = this.genomPosStart; i < this.genomPosEnd; i++) {

                for (int j = 0; j < 10; j++) {
                    if (Math.pow(10, stepLog - 2) / scale_x > 3) {
                        for (int k = 0; k < 10; k++) {

                            pix_xxx = off_legend + (int) (((i * Math.pow(10, stepLog)) + (j * Math.pow(10, stepLog - 1) + (k * Math.pow(10, stepLog - 2)) - ChromTab.this.pos_first_x)) / scale_x);

                            if (pix_xxx > (pos_real_max_x - off_legend)) {
                                break;
                            }
                            g.drawLine(pix_xxx, (int) (this.getHeight() * 0.95), pix_xxx, this.getHeight());
                        }
                    }
                    pix_xx = off_legend + (int) (((i * Math.pow(10, stepLog)) + ((j * Math.pow(10, stepLog - 1)) - ChromTab.this.pos_first_x)) / scale_x);
                    if (pix_xx > (pos_real_max_x - off_legend)) {
                        break;
                    }
                    g.drawLine(pix_xx, (int) (this.getHeight() * 0.8), pix_xx, this.getHeight());
                    // if resolution is low enough  print labels
                    if (Math.pow(10, stepLog - 1) / scale_x > 50) {

                        g.drawString(
                                Long.toString(
                                        (long) (stepLog > unityLog ? (long) ((i * Math.pow(10, stepLog - unityLog))
                                                + (j * Math.pow(10, stepLog - 1 - unityLog)))
                                                : (j * Math.pow(10, stepLog - 1)))),
                                pix_xx, (int) (this.getHeight() * 0.3));
                    }
                }
                pix_x = off_legend + (int) (((i * Math.pow(10, stepLog)) - ChromTab.this.pos_first_x) / scale_x);
                // print Labels
                if (pix_x > (pos_real_max_x - off_legend)) {
                    break;
                }
                g.drawString(
                        Long.toString(
                                (long) (stepLog >= unityLog ? (i * Math.pow(10, stepLog - unityLog)) : i * Math.pow(10, stepLog))),
                        pix_x, (int) (this.getHeight() * 0.3));
                /*g.drawString(
                Long.toString((long) (i * Math.pow(10, stepLog - unityLog))),
                pix_x - 2, (int) (this.getHeight() * 0.3));
                 */
                g.drawLine(pix_x, (int) (this.getHeight() * 0.5), pix_x, this.getHeight());
            }

            g.setColor(Color.red);
            if (//ChromTab.this.pos_ruler > ArrayView.off_legend 
                    // && ChromTab.this.pos_ruler <= (Defines.ARRAY_WIDTH - ArrayView.off_legend) 
                    parent.showRuler) {
                g.drawLine((int) ChromTab.this.pos_ruler,
                        0, (int) ChromTab.this.pos_ruler, this.getHeight());

            }
        }

        public void mouseDragged(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseMoved(MouseEvent e) {
            if (!parent.showRuler) {
                return;
            }

            ChromTab.this.pos_ruler = (long) e.getPoint().getX();
            parent.setPosition(ChromTab.this.chrom, mapPosition(ChromTab.this.pos_ruler));

            //if (r != null) {
            //    if (r.getWidth() < containerPoint.x) {
            //System.out.println("we are not inside tabbed block");
            for (Component a : ChromTab.this.getComponents()) {
                a.repaint();

            }
            this.repaint();
            ChromTab.this.repaint();

        }
    }
}
