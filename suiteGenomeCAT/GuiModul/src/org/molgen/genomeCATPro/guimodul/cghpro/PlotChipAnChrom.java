package org.molgen.genomeCATPro.guimodul.cghpro;

import org.molgen.genomeCATPro.annotation.PlotLib;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.cghpro.chip.Chip;
import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.guimodul.data.ZoomYAction;
import org.molgen.genomeCATPro.data.IFeature;

/**
 * @name PlotChipAnChrom.java
 * @author Wei Chen
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package. Katrin Tebel
 * <tebel at molgen.mpg.de>. The contents of this file are subject to the terms
 * of either the GNU General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * /**
 * 260612 kt initMenue remove globalPositionMenu 270612 kt updatePlot check if
 * features are empty 280612 kt initMenu add ShowData
 *
 */
public class PlotChipAnChrom extends JLabel
        implements PlotPanel, Scrollable, MouseListener, MouseMotionListener {

    CGHPROFrame parentFrame;
    ChipFeature chip1 = null;
    String chromId;
    private BufferedImage imageChip1;
    // current intern  scaling factors
    private double xScale = 1;
    private double yScale = 1;
    // current user focus position at the image
    //private long xImagePosition;
    //private long yImagePosition;
    // size of scaled image
    private int imageHeight;
    private int imageWidht;
    // size of panel
    private int displayHeight;
    private int displayWidth;
    private ChipFeature chip2 = null;
    private Chip chipTrack = null;
    double maxRatio;
    long firstPos = 0;
    long secondPos = 0;
    private boolean showDetailFrame = false;
    private long secondPixY = 0;
    private long firstPixY = 0;
    private long firstPixX = 0;
    private JPopupMenu popup;
    private JMenuItem menuShowData;
    private JMenuItem menuItemZoomIn;
    private JMenuItem menuItemZoomOut;

    public BufferedImage getImage() {
        return imageChip1;
    }

    public ChipFeature getChip1() {
        return chip1;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.imageWidht, this.imageHeight);
    }

    public String getChromId() {
        return chromId;
    }

    //user setting
    public void setXZoom(double xZoom) {

        this.xScale = (this.xScale / xZoom);
        this.imageWidht = (int) (this.imageWidht * xZoom);
        //this.xImagePosition = (int) (this.xImagePosition * xZoom);
    }

    //user setting
    public void setYZoom(double yZoom) {

        // this.yScale = (this.yScale / yZoom);
        /*this.yScale = ((this.parentFrame.chromLength.get(chromId) /
         (this.displayHeight - (this.parentFrame.getOffY() * 2))) / this.yZoom);
        
         */
        this.imageHeight = (int) (this.imageHeight * yZoom);

        this.rescale();
        //reset focus ??? 
        this.parentFrame.setMousePos((long) (this.parentFrame.getMousePos().intValue() * yZoom));
        //this.yImagePosition = (int) (this.yImagePosition * yZoom);

        Logger.getLogger(PlotChipAnChrom.class.getName()).log(Level.INFO,
                "ZoomY " + yZoom + " new height: " + this.imageHeight
                + " mousepos " + this.parentFrame.getMousePos());
    }

    public PlotChipAnChrom() {
        super();
    }

    public PlotChipAnChrom(CGHPROFrame parent) {
        super();
        this.parentFrame = parent;
        displayHeight = parentFrame.getPlotPanelHeight();
        displayWidth = parentFrame.getPlotPanelWidth();

        this.initMenues();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setChip1(ChipFeature chip) {
        this.chip1 = chip;

        //this.maxChromLength = this.parentFrame.cytoBandManager.getMaxLength(this.chip1.getRelease());
    }

    /**
     * set chromid, reset plot view (scaling, zoom, maxvalues)
     *
     * @param chromId
     */
    public void updatePlot(String chromId) {
        this.chromId = chromId;
        this.imageChip1 = null;
        displayHeight = parentFrame.getPlotPanelHeight();
        displayWidth = parentFrame.getPlotPanelWidth();
        if (this.chromId.compareToIgnoreCase(CGHPROFrame.ALL_CHROMS) == 0) {

            //this.xScale = 1.0;
            //this.scale = this.maxLength / this.compactHeight;
            //this.height = this.displayHeight;
            // this.zoom = 1.0;
            // this.yPosition = (int) ((this.displayHeight / 2) * this.scale);
            //compactGenomeImage();
        } else {

            // berechne xscale aus max Ratio
            List<? extends IFeature> copyBacs = null;

            if (this.chip1 != null && !this.chip1.getError()) {
                copyBacs = this.chip1.chrFeatures.get(chromId);
            } else if (this.chip2 != null && !this.chip2.getError()) {
                copyBacs = this.chip2.chrFeatures.get(chromId);
            } else if (this.chipTrack != null && !this.chipTrack.getError()) {
                copyBacs = (List<? extends IFeature>) this.chipTrack.getData(this.chromId);
            } else {
                maxRatio = -1;
            }
            // 270612 kt check if features are empty
            if (maxRatio != -1 && copyBacs != null && copyBacs.size() > 0) {
                Collections.sort(copyBacs, IFeature.comRatio);
                maxRatio = Math.max(
                        Math.abs(copyBacs.get(copyBacs.size() - 1).getRatio()),
                        Math.abs(copyBacs.get(0).getRatio()));

            } else {
                maxRatio = 10;
            }
            Logger.getLogger(PlotChipAnChrom.class.getName()).log(
                    Level.INFO, "update Plot: Max Ratio: " + maxRatio);

            this.imageHeight = (this.displayHeight);
            this.imageWidht = (this.displayWidth);

            this.xScale = (maxRatio * this.parentFrame.getSpotWidth()) * 2
                    / (this.displayWidth - (this.parentFrame.getOffX() * 2));

            // berechen yScale aus max length
            this.firstPos = 0;
            this.secondPos = this.parentFrame.getChromLength(chromId);
            this.rescale();
            /* this.yScale = this.parentFrame.chromLength.get(chromId) /
             (this.displayHeight - (this.parentFrame.getOffY() * 2));
            
             */

            Logger.getLogger(PlotChipAnChrom.class.getName()).log(
                    Level.INFO, "Update Plot: yScale: " + yScale);

            //this.xImagePosition = this.imageHeight / 2;
            //this.yImagePosition = this.imageWidht / 2;
            refreshPlot();
        }
    }

    public void rescale() {
        this.yScale = (this.secondPos - this.firstPos)
                / (this.imageHeight - (this.parentFrame.getOffY() * 2));
        //x, y, width, height

    }

    /*
     * plot all content panels,
     * called as first view and after zoom actions, 
     * or refreshed data (?? NÖ!!)
     */
    public void refreshPlot() {

        // TODO recreate only if needed image == null , image.width != this.width
        if (imageChip1 != null
                && (imageChip1.getWidth() != this.imageWidht || imageChip1.getHeight() != this.imageHeight)) {
            imageChip1 = null;
            System.gc();
        }
        if (imageChip1 == null) {
            Logger.getLogger(PlotChipAnChrom.class.getName()).log(Level.INFO,
                    "create new buffered Image");
            imageChip1 = new BufferedImage(this.imageWidht, this.imageHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        }

        //image = createVolatileImage(width, height);
        Graphics2D g = imageChip1.createGraphics();
        paint(g);
        setIcon(new ImageIcon());

        ((ImageIcon) this.getIcon()).setImage(imageChip1);

    }
    Font defFont;
    FontMetrics fm;
    String labelPosition = "";
    String labelFont = "";
    int detailX;
    int y1;

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageChip1.getWidth(), imageChip1.getHeight());
        //g.setColor(parentFrame.getChip1Color());
        g.setColor(Color.DARK_GRAY);
        labelFont = "PLAIN-" + (int) this.parentFrame.getOffY() / 2;
        g.setFont(Font.decode(labelFont));
        defFont = g.getFont();
        fm = g.getFontMetrics();

        labelPosition = chromId + (!this.parentFrame.isFullChrom() ? ("-" + this.firstPos + ":" + this.secondPos) : "");

        g.drawString(labelPosition,
                this.imageWidht / 2 - fm.stringWidth(labelPosition) / 2,
                (int) (this.parentFrame.getOffY() / 2));

        g.setFont(defFont);

        Color c = null;
        if (this.chip1 != null) {
            c = parentFrame.getChip1Color();
            plotRatios(chip1, c, g, this.imageWidht / 2, this.parentFrame.getOffY());
        }
        if (this.chip2 != null) {
            c = parentFrame.getChip2Color();
            plotRatios(chip2, c, g, this.imageWidht / 2, this.parentFrame.getOffY());
        }
        if (this.chipTrack != null) {
            c = parentFrame.getChipTrackColor();
            plotRatios(chipTrack, c, g, this.imageWidht / 2, this.parentFrame.getOffY());
        }
        g.setColor(Color.CYAN);
        g.drawLine(this.imageWidht / 2, this.parentFrame.getOffY(), this.imageWidht / 2,
                this.imageHeight - this.parentFrame.getOffY());
        g.setColor(this.parentFrame.getAbberantNegColor());
        int r = (int) ((this.imageWidht / 2) + (this.parentFrame.getNegThresholdLine() * this.parentFrame.getSpotWidth() / xScale));

        g.drawLine(r, this.parentFrame.getOffY(),
                r,
                this.imageHeight - this.parentFrame.getOffY());

        g.setColor(this.parentFrame.getAbberantPosColor());
        r = (int) ((this.imageWidht / 2) + (this.parentFrame.getPosThresholdLine() * this.parentFrame.getSpotWidth() / xScale));

        g.drawLine(r, this.parentFrame.getOffY(),
                r,
                this.imageHeight - this.parentFrame.getOffY());

        // plot dragged detail window        
        if (showDetailFrame) {
            g.setColor(Color.RED);
            //x - the x coordinate of the rectangle to be drawn. 
            //y - the y coordinate of the rectangle to be drawn.
            //width - the width of the rectangle to be drawn. 
            //height - the height of the rectangle to be drawn.
            detailX = (int) (((this.firstPixX < this.imageWidht / 2 - 100) || (this.firstPixX < (this.imageWidht / 2) - 100)) ? this.firstPixX : (this.imageWidht / 2) - 100);
            g.drawRect(
                    detailX,
                    (int) this.firstPixY,
                    detailX < this.imageWidht / 2 ? (this.imageWidht / 2 - detailX) * 2 : (detailX - this.imageWidht / 2) * 2,
                    (int) (this.secondPixY - this.firstPixY));

        } else if (this.parentFrame.isShowruler()) {
            g.setColor(Color.BLACK);
            y1 = this.parentFrame.getMousePos().intValue();

            g.drawLine(this.parentFrame.getOffX(), y1,
                    this.imageWidht - this.parentFrame.getOffX(), y1);
            g.setColor(Color.BLACK);
        }

        //parentFrame.setStatusBar("Chromosome: " + chromId);
    }

    /**
     * draw the plot
     */
    /**
     * draw the whole plot to an image, used for report results
     *
     * @see ReportChip.exportGenomeDisplay();
     * @param g
     * @param y0, the start y coordinate
     *
     */
    public void plotRatios(
            Chip chip,
            Color c,
            Graphics g,
            int x0, int y0) {

        Logger.getLogger(PlotChipAnChrom.class.getName()).log(Level.INFO,
                "Plot " + chip.getName() + " " + this.chromId);

        int bacWidth = parentFrame.getSpotWidth();

        //bacs = chip.chrFeatures[j];
        List<? extends IFeature> features = (List<? extends IFeature>) chip.getData(this.chromId);
        if (features == null || features.size() == 0) {
            return;
        }

        if (!this.parentFrame.isFullChrom()) {
            features = (List<? extends IFeature>) PlotLib.getSublist(features, chromId,
                    this.firstPos,
                    this.secondPos);
        } else {
            Collections.sort(features, Region.compByStart);
            //int length = (int) ((chromLength.get(chromId) / yScale) + 2 * bacWidth);
            //ColorOption.setColor();
        }
        g.setColor(c);
        int x1;

        int y2;
        for (IFeature aFeature : features) {

            //System.out.println("Feature: " + aFeature.toString());
            x1 = (int) (x0
                    + // center
                    //6 * bacWidth +  // gap to center
                    aFeature.getRatio() * bacWidth / xScale);        //?

            y1 = (int) (y0 + ((aFeature.getChromStart() - firstPos) / yScale));
            y2 = (int) (y0 + ((aFeature.getChromEnd() - firstPos) / yScale));

            if (aFeature.isAberrant()) {
                if (aFeature.getIfAberrant() > 0) {
                    g.setColor(parentFrame.getAbberantPosColor());
                } else {
                    g.setColor(parentFrame.getAbberantNegColor());
                }
            } else {
                g.setColor(c);
            }
            g.drawRect(x1, y1, bacWidth, y2 - y1 > 0 ? y2 - y1 : this.parentFrame.getMinSpotHeight());
            g.fillRect(x1, y1, bacWidth, y2 - y1 > 0 ? y2 - y1 : this.parentFrame.getMinSpotHeight());

        }

        /*
         if (this.pointedFeature != null) {
         x1 = (int) (x0 + // center
         //6 * bacWidth +  // gap to center
         pointedFeature.getRatio() * bacWidth / xScale);
         y1 = (int) (y0 + ((pointedFeature.getChromStart() - firstPos) / yScale));
         y2 = (int) (y0 + ((pointedFeature.getChromEnd() - firstPos) / yScale));
         g.setColor(Color.MAGENTA);
         g.drawOval(x1 - 10, y2 + 10, 20, y1 - 10);
         }
         */
    }

    public boolean isShowDetailFrame() {
        return showDetailFrame;
    }

    public void setShowDetailFrame(boolean showDetailFrame) {
        this.showDetailFrame = showDetailFrame;
    }

    void setChip2(ChipFeature overlayedChip) {
        this.chip2 = overlayedChip;
    }

    void setChipTrack(Chip trackChip) {
        this.chipTrack = trackChip;
    }

    private void initMenues() {
        this.popup = new JPopupMenu();
        this.menuShowData = new JMenuItem("show Data");
        this.menuShowData.addActionListener(
                new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                long pos = mapYPosition(parentFrame.getMousePos());
                PlotChipAnChrom.this.showDetailData(pos);
            }
        });

        popup.add(menuShowData);
        /*this.menuItemSetStart = new JMenuItem("set global start");
         this.menuItemSetStart.addActionListener(
         new ActionListener() {
        
         public void actionPerformed(ActionEvent e) {
         long pos = mapYPosition(firstPixY);
         WebPositionPanel.setWebPositionChrom(chromId);
         WebPositionPanel.setWebPositionStart(Long.toString(pos));
         }
         });
        
         popup.add(menuItemSetStart);
         this.menuItemSetEnd = new JMenuItem("set global end");
         this.menuItemSetEnd.addActionListener(
         new ActionListener() {
        
         public void actionPerformed(ActionEvent e) {
        
         long pos = mapYPosition(secondPixY);
         WebPositionPanel.setWebPositionEnd(Long.toString(pos));
         }
         });
         popup.add(menuItemSetEnd);
         */
        this.menuItemZoomIn = new JMenuItem("ZoomIn");
        this.menuItemZoomIn.addActionListener(
                new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                ZoomYAction.getInstance().doZoom(+1);
            }
        });

        popup.add(menuItemZoomIn);
        this.menuItemZoomOut = new JMenuItem("ZoomOut");
        this.menuItemZoomOut.addActionListener(
                new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                ZoomYAction.getInstance().doZoom(-1);
            }
        });

        popup.add(menuItemZoomOut);

    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return (AlphaComposite.getInstance(type, alpha));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(this.parentFrame.getPlotPanelWidth(),
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

    public void refresh() {
        this.refreshPlot();
    }
    // map mouse position (pixel) to genomic position (bp)

    public long mapYPosition(long mousePos) {

        if (mousePos < parentFrame.getOffY()) {
            return CytoBandManagerImpl.getFirst(
                    GenomeRelease.toRelease(parentFrame.release),
                    parentFrame.chromId).getChromStart();
        }/*
         if (mousePos > this.imageHeight - parentFrame.getOffY()) {
         return this.parentFrame.cytoBandManager.getLast(
         GenomeRelease.toRelease(parentFrame.release),
         parentFrame.chromId).getChromEnd();
         }
         */

        if (this.parentFrame.isFullChrom()) {
            return (long) ((mousePos - this.parentFrame.getOffY()) * yScale);
        } else {
            return (long) (this.firstPos + ((mousePos - this.parentFrame.getOffY()) * yScale));
        }
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            System.out.println("show popup");
            popup.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }
    /**
     * get current mouse position
     *
     * @param e
     */
    String text;
    //Feature pointedFeature = null;

    void showDetailData(long pos) {

        long dist = (long) (parentFrame.getMinSpotHeight() * yScale);
        if (chip1 == null) {
            return;
        }
        List<? extends IFeature> features = (List<? extends IFeature>) chip1.getData(this.chromId);
        if (features == null || features.size() == 0) {
            return;
        }
        IFeature curr = (IFeature) PlotLib.getDataAtPos(
                (List<? extends Region>) features, chromId, pos, dist);
        /*if (curr != null) {
         System.out.println(curr.toHTMLString());
         }*/
        this.parentFrame.showDetailData(curr);
    }

    public void mouseMoved(MouseEvent e) {

        //this.xImagePosition = e.getPoint().x;
        //this.yImagePosition = e.getPoint().y;
        this.parentFrame.setMousePos(e.getPoint().y);

        long pos = mapYPosition(e.getPoint().y);
        this.parentFrame.setPosition(pos);
        if (!this.parentFrame.isShowruler() && !this.parentFrame.showDetailData()) {
            return;
        }
        if (this.parentFrame.showDetailData()) {
            this.showDetailData(pos);
        }
        if (this.parentFrame.isShowruler()) {
            this.parentFrame.updateImages();
        }
    }

    public void mouseClicked(MouseEvent e) {
        this.firstPixX = e.getPoint().x;
        this.maybeShowPopup(e);
    }
// mouse event handling methods

    public void mousePressed(MouseEvent e) {

        firstPixY = secondPixY = ((Point) e.getPoint()).y;
        this.maybeShowPopup(e);

        this.setShowDetailFrame(true);

    }

    /**
     * paint frame by dragging mouse defines genomic region update stop position
     *
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse dragged");
        secondPixY = ((Point) e.getPoint()).y;
        if (this.showDetailFrame) {
            this.repaint();
        }

    }

    public long getFirstPos() {
        return firstPos;
    }

    public void setFirstPos(long firstPos) {
        this.firstPos = firstPos;
    }

    public long getSecondPos() {
        return secondPos;
    }

    public void setSecondPos(long secondPos) {
        this.secondPos = secondPos;
    }

    /**
     * paint frame by dragging mouse defines genomic region definition finished
     * - update genomic positions (start/stop) switch to detailled view
     *
     * @param e
     */
    public void mouseReleased(MouseEvent e) {

        secondPixY = ((Point) e.getPoint()).y;
        this.maybeShowPopup(e);
        if (secondPixY - firstPixY < 1) {
            this.showDetailFrame = false;
            return;

        }

        long _firstPos = mapYPosition(firstPixY);
        long _secondPos = mapYPosition(secondPixY);

        Logger.getLogger(PlotChipAnChrom.class.getName()).log(Level.INFO,
                "Detail: begin: " + _firstPos + " end: " + _secondPos);

        // yScale = secondPos - firstPos /
        //        (displayHeight - (parentFrame.getOffY() * 2));
        this.refreshPlot();

        parentFrame.showDetails(_firstPos, _secondPos);
    }
}
