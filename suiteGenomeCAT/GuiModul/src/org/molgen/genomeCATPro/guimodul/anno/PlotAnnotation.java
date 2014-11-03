package org.molgen.genomeCATPro.guimodul.anno;

/**
 * @name PlotAnnotation
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package.
 * Copyright Jan 19, 2010 Katrin Tebel <tebel at molgen.mpg.de>.
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
import org.molgen.genomeCATPro.guimodul.BasicFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import org.molgen.genomeCATPro.annotation.AnnotationManager;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.guimodul.cghpro.PlotPanel;

/**
 * 
 * 080812   kt  add showDetails at mouseover
 */
public class PlotAnnotation extends JLabel
        implements PlotPanel, Scrollable {

    
    JPopupMenu popup;
    JMenuItem menuItemClose;
    //MouseListener popupListener = new MouseListener();
    BasicFrame parentFrame;
    public boolean alert = false;
    String chromId;
    private long firstPos;
    private long secondPos;
    private boolean isDetail = false;

    public String getAnnoName() {
        return this.name;
    }

    public String getChromId() {
        return chromId;
    }
    // current user zoom factor
    //private int xZoom = 1;
    private BufferedImage image;
    private AnnotationManager manager;

    public AnnotationManager getManager() {
        return manager;
    }

    //user setting
    public void setYZoom(double yZoom) {


        this.imageHeight = (int) (this.imageHeight * yZoom);
        this.rescale();
        Logger.getLogger(PlotAnnotation.class.getName()).log(Level.INFO,
                "ZoomY " + yZoom + " new height: " + this.imageHeight);
    }

    public void setXZoom(double xZoom) {
    }
    // current intern  scaling factors
    //private double xScale = 1;
    private double yScale = 1;
    // current user focus position at the image
    //private int xImagePosition;
    //private int yImagePosition;
    // size of scaled image
    private int imageHeight;
    private int imageWidht;
    // size of panel
    //private int displayHeight;
    //private int displayWidth;
    private final String name;
    private final Integer no;

    public PlotAnnotation() {
        super();
        this.name = "";
        this.no = 0;
        this.initMenues();
        addMouseListener(new MyMouseListener());
        addMouseMotionListener(new MyMouseListener());
    }

    public PlotAnnotation(BasicFrame parent, AnnotationManager mng, Integer no) {
        super();
        this.no = no;
        this.name = mng.getNameId();
        this.parentFrame = parent;
        //displayHeight = parentFrame.getPlotPanelHeight();
        //displayWidth = parentFrame.getPlotAnnoWidth() + parentFrame.getPlotAnnoGap();
        this.manager = mng;

        this.initMenues();
        addMouseListener(new MyMouseListener());
        addMouseMotionListener(new MyMouseListener());

    }

    public void refresh() {
        refreshPlot();
    }

    public Integer getNo() {
        return no;
    }

    /**
     * set chromid, reset plot view (scaling, zoom, maxvalues)
     * @param chromId
     */
    public void updatePlot(String chromId) {
        this.chromId = chromId;
        //displayHeight = parentFrame.getPlotPanelHeight();
        //displayWidth = parentFrame.getPlotAnnoWidth() + parentFrame.getPlotAnnoGap();
        this.image = null;
        if (this.chromId.compareToIgnoreCase(BasicFrame.ALL_CHROMS) == 0) {

            //this.xScale = 1.0;
            //this.scale = this.maxLength / this.compactHeight;
            //this.height = this.displayHeight;
            // this.zoom = 1.0;
            // this.yPosition = (int) ((this.displayHeight / 2) * this.scale);
            //compactGenomeImage();
        } else {


            // init values before any zoom was done
            //this.xZoom = 1;

            // berechne xscale aus max Ratio

            Logger.getLogger(PlotAnnotation.class.getName()).log(Level.INFO,
                    "displayHeight: " + parentFrame.getPlotPanelHeight() +
                    " displayWidth: " + parentFrame.getPlotAnnoWidth() + parentFrame.getPlotAnnoGap());
            this.imageHeight = parentFrame.getPlotPanelHeight();
            this.imageWidht = parentFrame.getPlotAnnoWidth() + parentFrame.getPlotAnnoGap();

            //this.xScale = 1;

            // berechen yScale aus max length
           /* this.yScale = this.parentFrame.chromLength.get(chromId) /
            (this.displayHeight - (this.parentFrame.getOffY() * 2));
             */
            this.firstPos = 0;
            this.secondPos = this.parentFrame.getChromLength(chromId);

            this.setXZoom(1.0);
            this.setYZoom(1.0);
            //this.rescale();

            //this.xImagePosition = this.imageHeight / 2;
            //this.yImagePosition = this.imageWidht / 2;

            refreshPlot();
        }
    }

    /*
     * plot all content panels,
     * called as first view and after zoom actions, 
     * or refreshed data (?? NÖ!!)
     */
    public void refreshPlot() {

        //reset focus ??? 
        //this.xImagePosition = (this.xImagePosition * this.xZoom);
        //this.yImagePosition = (this.yImagePosition * this.yZoom);


        // TODO recreate only if needed image == null , image.width != this.width

        if (image != null &&
                (image.getWidth() != this.imageWidht || image.getHeight() != this.imageHeight)) {
            image = null;
            System.gc();
        }
        if (image == null) {
            Logger.getLogger(PlotAnnotation.class.getName()).log(Level.INFO,
                    "create new image");
            image = new BufferedImage(this.imageWidht, this.imageHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        }

        //image = createVolatileImage(width, height);


        Graphics2D g = image.createGraphics();
        paint(g);
        setIcon(new ImageIcon());

        ((ImageIcon) this.getIcon()).setImage(image);
    }
    Font defFont;
    FontMetrics fm;
    String label;
    String labelFont = "";

    public String getRomanNo() {
        return Utils.intToRoman(this.getNo());
    }

    public void setRomanNo(String d) {
    }

    public String getAlpha() {
        return Utils.intToAlpha(this.getNo());
    }

    public void setAlphaNo(String d) {
    }

    public void paint(Graphics2D g) {

        Logger.getLogger(PlotAnnotation.class.getName()).log(Level.INFO,
                "Plot " + this.name + " " + this.chromId);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());


        // print number as label
        g.setColor(Color.BLACK);
        labelFont = "PLAIN-BOLD-" + (int) this.parentFrame.getOffY() *0.5;
        g.setFont(Font.decode(labelFont));
        defFont = g.getFont();
        fm = g.getFontMetrics();




        //java.awt.geom.Rectangle2D text = fm.getStringBounds(this.getAnnoName(), g);
        //
       /*  AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate( 
        this.imageWidht / 2 - fm.stringWidth(this.no.toString()) / 2,
        this.parentFrame.getOffY() * 3 / 4);
        affineTransform.rotate(-Math.toRadians(90));*/
        g.drawString(this.getAlpha(),
                this.imageWidht / 2 - fm.stringWidth(this.no.toString()) / 2,
                this.parentFrame.getOffY() * 3 / 4);

        /* affineTransform.translate(
        -this.imageWidht / 2 - fm.stringWidth(this.no.toString()) / 2,
        -this.parentFrame.getOffY() * 3 / 4);*/




        g.setFont(defFont);

        

        manager.plot((Graphics2D) g,
                this.chromId,
                this.parentFrame.isFullChrom(),
                this.firstPos, this.secondPos,
                (int) (this.parentFrame.getPlotAnnoGap() * 0.5), //left
                this.parentFrame.getOffY(), // top,
                this.parentFrame.getPlotAnnoWidth(), // width -- no zoom
                this.yScale);
        // print ruler
        if (this.parentFrame.isShowruler()) {
            g.setColor(Color.BLACK);
            long y1 = this.parentFrame.getMousePos();

            g.drawRect(0, (int) (y1 - (int) (this.parentFrame.getRulerHeight() / 2)),
                    this.parentFrame.getPlotAnnoGap() / 2,
                    this.parentFrame.getRulerHeight());
            g.drawRect(
                    (this.parentFrame.getPlotAnnoGap() / 2) + this.parentFrame.getPlotAnnoWidth(),
                    (int) (y1 - ((int) this.parentFrame.getRulerHeight() / 2)),
                    this.parentFrame.getPlotAnnoGap() / 2,
                    this.parentFrame.getRulerHeight());
            g.setColor(Color.BLACK);
        }





    }

    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(this.parentFrame.getPlotAnnoWidth(),
                this.parentFrame.getPlotPanelHeight());
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 0;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 0;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setFirstPos(long y) {
        this.firstPos = y;
    }

    public void setSecondPos(long y) {
        this.secondPos = y;
    }

    public void rescale() {
        this.yScale = (this.secondPos - this.firstPos) /
                (this.imageHeight - (this.parentFrame.getOffY() * 2));
    }

    /**
     * create popup menues for legend panel.
     * 
     */
    protected void initMenues() {
        this.popup = new JPopupMenu(this.name);
        this.menuItemClose = new JMenuItem("close");
        this.menuItemClose.addActionListener(new MenuListener());
        this.popup.add(menuItemClose);
    }

    /**
     * action listener for popup menue
     */
    class MenuListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //System.out.println("menue action event at  " + e.getSource()




            JMenuItem source = (JMenuItem) (e.getSource());
            if (source == menuItemClose) {
                PlotAnnotation.this.parentFrame.removeAnnotation(PlotAnnotation.this);

            }

        // component.setBackground(Color.WHITE);
        // ((JPanel) component).setOpaque(false);
        }
    }

    public long mapYPosition(long mousePos) {

        if (mousePos < parentFrame.getOffY()) {
            return CytoBandManagerImpl.getFirst(
                    GenomeRelease.toRelease(parentFrame.release),
                    parentFrame.chromId).getChromStart();
        }
        if (this.parentFrame.isFullChrom()) {
            return (long) ((mousePos - this.parentFrame.getOffY()) * yScale);
        } else {
            return (long) (this.firstPos + ((mousePos - this.parentFrame.getOffY()) * yScale));
        }
    }

    class MyMouseListener implements MouseListener, MouseMotionListener {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            System.out.println("popup listener");

            if (e.isPopupTrigger()) {
                System.out.println("show popup");
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            parentFrame.setMousePos(e.getPoint().y);
            long pos = mapYPosition(e.getPoint().y);
            parentFrame.setPosition(pos);
            if (!parentFrame.showDetailData()) {
                return;
            }
            if (parentFrame.showDetailData()) {
                showDetailData(pos);
            }

        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            maybeShowPopup(e);
        }
    }

    private void showDetailData(long pos) {
        long dist = (long) (2 * yScale);

        Region curr = this.getManager().getDataAtPos(chromId, pos, dist);

        this.parentFrame.showDetailData(curr);
    }

    public boolean isShowDetailFrame() {
        return this.isDetail;
    }

    public void setShowDetailFrame(boolean showDetailFrame) {
        this.isDetail = showDetailFrame;
    }

    public BufferedImage getImage() {
        return this.image;
    }
}
