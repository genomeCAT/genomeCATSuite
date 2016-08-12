package org.molgen.genomeCATPro.peaks.cnvcat;

/**
 * @name MatrixAberration
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import javax.swing.*;

import java.util.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.annotation.PlotLib;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;

import org.molgen.genomeCATPro.guimodul.cghpro.PlotPanel;
import org.molgen.genomeCATPro.guimodul.data.ZoomYAction;
import org.molgen.genomeCATPro.peaks.Aberration;
import org.molgen.genomeCATPro.peaks.AberrationIds;
import org.molgen.genomeCATPro.peaks.CNVCATPropertiesMod;
import org.openide.util.Exceptions;

/**
 * 260612 kt initMenue remove globalPositionMenu
 *
 * 280612 kt initMenu
 *
 * add ShowData
 *
 */
public class MatrixAberration extends JLabel
        implements PlotPanel, Scrollable, MouseListener, MouseMotionListener {

    /**
     * parent frame
     */
    CNVCATFrame parentFrame;
    /**
     * the parameters of the summarizing picture
     */
    long firstPos = 0;
    long secondPos = 0;
    private JPopupMenu popup;
    private JMenuItem menuShowData;
    private JMenuItem menuItemZoomIn;
    private JMenuItem menuItemZoomOut;
    /**
     * max values for displayed values, used to modify color with lightness
     */
    double maxRatio, maxQuality = 0.0;
    //int compactHeight = 300, compactWidth = 300;
    private double xScale = 1;
    private double yScale = 1;
    // size of scaled image
    private int imageHeight;
    private int imageWidht;
    /**
     * chromosome view initial size
     */
    private int displayHeight;
    private int displayWidth;
    /**
     * number of cases to be displayed
     */
    int iActivePhenotypes = 0;
    /**
     * currently displayed aberration, used by printAberration Methods
     */
    /**
     * width for ideogram
     */
    int ideogramWidth;
    /**
     * the image holding the aberration information
     */
    public BufferedImage image;
    /**
     * indicator if whole Genome view (24 Chromsome) or single chromosome view
     * is requiered
     */
    String chromId = null;

    @Override
    public Dimension getSize() {
        return new Dimension(this.imageWidht, this.imageHeight);
    }

    public String getChromId() {
        return chromId;
    }

    public void setXZoom(double xZoom) {

        this.xScale = (this.xScale / xZoom);

        this.setImageWidth();

    }

    public void setYZoom(double yZoom) {

        this.imageHeight = (int) (this.imageHeight * yZoom);

        this.rescale();
        //reset focus ??? 
        this.parentFrame.setMousePos((long) (this.parentFrame.getMousePos().intValue() * yZoom));
        //this.yImagePosition = (int) (this.yImagePosition * yZoom);

        Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                "ZoomY " + yZoom + " new height: " + this.imageHeight
                + " mousepos " + this.parentFrame.getMousePos());
    }

    public MatrixAberration() {
        super();
    }

    /**
     * Constructor
     *
     * @param parent parent frame
     * @param isGenome
     * @param isStacked
     *
     */
    public MatrixAberration(CNVCATFrame parent) {
        super();
        //this.chromId = chromId;
        this.parentFrame = parent;
        displayHeight = parentFrame.getPlotPanelHeight();
        displayWidth = parentFrame.getPlotPanelWidth();

        // init filtered aberration data dependend values
        this.initMenues();

        //tops = new int[25];
        //lefts = new int[25];
        addMouseListener(this);
        addMouseMotionListener(this);
        //this.addLayerdPane();
    }

    public BufferedImage getImage() {
        return this.image;
    }

    private void setImageWidth() {
        this.ideogramWidth = (int) (10 * this.xScale * CNVCATPropertiesMod.props().getProbeWidth());
        if (this.parentFrame.isDisplayFreq()) {
            this.imageWidht = this.displayWidth;
        } else if (this.parentFrame.isDisplayTabular()) {
            this.imageWidht
                    = (int) (2 * getColNumbers(iActivePhenotypes)
                    * (int) this.xScale
                    * (CNVCATPropertiesMod.props().getProbeWidth() + 2 * parentFrame.getGap())
                    + //2 * CNVCATPropertiesMod.props().getGap() +
                    this.ideogramWidth);
        } else {
            this.imageWidht
                    = (int) (2 * CNVCATPropertiesMod.props().getNofCols()
                    * (int) this.xScale
                    * (CNVCATPropertiesMod.props().getProbeWidth() + 2 * parentFrame.getGap())
                    + //2 * CNVCATPropertiesMod.props().getGap() +
                    this.ideogramWidth);
        }

        this.imageWidht = this.imageWidht < this.displayWidth ? this.displayWidth : this.imageWidht;

        Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                "ImageWidth: " + this.imageWidht);
    }

    public void updatePlot(String chrom) {
        this.image = null;
        this.chromId = chrom;
        displayHeight = parentFrame.getPlotPanelHeight();
        displayWidth = parentFrame.getPlotPanelWidth();

        Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                chrom);
        if (this.chromId.compareToIgnoreCase(CNVCATFrame.ALL_CHROMS) == 0) {
            /*this.xScale = 1.0;
            //this.scale = this.maxLength / this.compactHeight;
            this.height = this.displayHeight;
            this.zoom = 1.0;
            this.yPosition = (int) ((this.displayHeight / 2) * this.scale);
            compactGenomeImage(); */
        } else {
            Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                    "displayHeight: " + displayHeight + " displayWidth: " + displayWidth);
            this.imageHeight = (this.displayHeight);
            this.imageWidht = (this.displayWidth);
            this.firstPos = 0;
            this.secondPos = this.parentFrame.getChromLength(chromId);
            iActivePhenotypes = parentFrame.AberrationManager().getActiveCasesSize();

            this.maxRatio = parentFrame.AberrationManager().getMaxRatio();
            this.maxQuality = parentFrame.AberrationManager().getMaxQuality();
            this.setXZoom(1.0);
            this.setYZoom(1.0);

            Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                    "yScale: " + yScale + " xScale: " + xScale);

            refreshPlot();
        }
    }

    void refreshGenomePlot() {
        //         compactGenomeImage();
    }

    /**
     * print plot update image acc to current size if necessary
     */
    void refreshPlot() {

        // TODO recreate only if needed image == null , image.width != this.width
        if (image != null
                && (image.getHeight() != this.imageHeight
                || image.getWidth() != this.imageWidht)) {
            image = null;
            System.gc();
        }
        if (image == null) {
            Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                    "create new buffered Image");
            image = new BufferedImage(this.imageWidht, this.imageHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        }

        //image = createVolatileImage(width, height);
        Graphics2D g = image.createGraphics();
        matrixPaint(g);
        setIcon(new ImageIcon());
        ((ImageIcon) this.getIcon()).setImage(image);
        /*
     * set mouse to center
     * yCenter = (int) (this.yPosition / this.scale) + MatrixAberration.top;
    
    
    scrollRectToVisible(
    new Rectangle(
    (xCenter - (1 / 2 * displayWidth)) < 0 ? 0 : (xCenter - (1 / 2 * displayWidth)),
    (yCenter - (1 / 2 * displayHeight)) < 0 ? 0 : (yCenter - (1 / 2 * displayHeight)),
    this.displayWidth,
    this.displayHeight));
    
    
    parentFrame.setStatusBar("Chromosome: " + chromId);
         */
    }
    int detailX;
    int y1, y2;
    Font defFont;
    FontMetrics fm;
    String labelPosition = "";
    String labelFont = "";

    @Override
    public void paint(Graphics g) {
        if (this.parentFrame.isShowruler()) {
            this.matrixPaint(g);
        } else {
            super.paint(g);
        }
    }

    public void matrixPaint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);
        labelFont = "PLAIN-BOLD-" + (int) this.parentFrame.getOffY() * 0.5;
        //labelFont = "PLAIN-" + (int) this.parentFrame.getOffY() / 2;
        g.setFont(Font.decode(labelFont));
        defFont = g.getFont();
        fm = g.getFontMetrics();

        labelPosition = chromId + "-" + this.firstPos + ":" + this.secondPos;

        g.drawString(labelPosition,
                this.imageWidht / 2 - fm.stringWidth(labelPosition) / 2,
                this.parentFrame.getOffY() / 2);

        g.setFont(defFont);
        if (parentFrame.CytoBandManager() != null) {
            parentFrame.CytoBandManager().plot(
                    (Graphics2D) g,
                    chromId,
                    this.parentFrame.isFullChrom(),
                    this.firstPos, this.secondPos,
                    (int) ((this.imageWidht - ideogramWidth) / 2),
                    this.parentFrame.getOffY(),
                    this.ideogramWidth,
                    this.yScale);

        }
        if (this.parentFrame.isDisplayFreq()) {
            printFreqAberration((Graphics2D) g,
                    this.chromId,
                    (this.imageWidht - this.ideogramWidth) / 2,
                    (this.imageWidht + this.ideogramWidth) / 2,
                    parentFrame.getOffY(),
                    this.displayWidth / 4, false, 0, null, false);
        } else {

            printAberration(
                    (Graphics2D) g,
                    this.chromId,
                    (this.imageWidht / 2) - ideogramWidth,
                    (this.imageWidht / 2) + ideogramWidth,
                    parentFrame.getOffY(), this.getColNumbers(iActivePhenotypes));
        }

        if (showDetailFrame) {
            g.setColor(Color.RED);
            //x - the x coordinate of the rectangle to be drawn. 
            //y - the y coordinate of the rectangle to be drawn.
            //width - the width of the rectangle to be drawn. 
            //height - the height of the rectangle to be drawn.
            detailX = (int) (((this.firstPixX < this.imageWidht / 2 - 100)) ? this.firstPixX : (this.imageWidht / 2) - 100);
            g.drawRect(
                    detailX,
                    (int) this.firstPixY,
                    200,
                    (int) (this.secondPixY - this.firstPixY));

        } else if (this.parentFrame.isShowruler()) {
            g.setColor(Color.BLACK);
            y1 = this.parentFrame.getMousePos().intValue();

            g.drawLine(this.parentFrame.getOffX(), y1,
                    this.imageWidht - this.parentFrame.getOffX(), y1);
            g.setColor(Color.BLACK);
        }
    }

    /**
     * The aberrant regions are displayed as relative frequencies. The
     * frequencies are calculated for each chromosomal region represended by one
     * pixel. For each group (defined by the color) the number of aberrations at
     * this region is related to the total number of extraction cases for this
     * group. Overlapping aberrations from the same extraction are counted only
     * once.
     *
     * @param g
     * @param chromId
     * @param scale
     * @param left
     * @param right
     * @param top
     * @param fWidth
     */
    public void printFreqAberration(
            Graphics2D g, String chromId, int left,
            int right, int top, int fWidth, boolean fileout, int bin, FileWriter out, boolean first) {

        java.util.List<? extends Aberration> aberrations = (List<Aberration>) parentFrame.AberrationManager().getAberrationsAtChrom(chromId);
        Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                "found " + (aberrations != null ? aberrations.size() : " none ") + " for " + chromId);

        if (aberrations == null || aberrations.size() == 0) {
            return;
        }
        int length = 0;
        if (!this.parentFrame.isFullChrom()) {
            aberrations = (List<? extends Aberration>) PlotLib.getSublist(
                    aberrations, chromId,
                    this.firstPos,
                    this.secondPos);
            Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                    "sublist:  " + (aberrations != null ? aberrations.size() : 0));
            //length = (int) ((this.secondPos - this.firstPos) / this.yScale + 2);
        } else {

            //length = (int) ((parentFrame.getChromLength(chromId) / this.yScale) + 2);
        }
        if (!fileout) {
            length = (int) (Math.ceil((this.secondPos - this.firstPos) / this.yScale) + 3);
        } else {
            fWidth = 1;
            length = (int) (Math.ceil((this.secondPos) / bin));
        }
        Collections.sort(aberrations, Aberration.compByStart);
        //int length = (int) ((parentFrame.getChromLength(chromId) / this.yScale) + 2);
        Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                "Length: " + length + " BIN: " + bin + " yscale: " + this.yScale);
        java.util.List<AberrationIds> aberrationIds
                = (java.util.List<AberrationIds>) parentFrame.AberrationManager().getActiveCases();

        Hashtable<Color, Integer> groupFreq = new Hashtable<Color, Integer>();
        Vector<Color> groups = new Vector<Color>();
        int group = 0;

        for (AberrationIds a : aberrationIds) {
            if (!groups.contains(a.getColor())) {
                groupFreq.put(a.getColor(), 1);
                groups.add(group, a.getColor());
                group++;

            } else {
                groupFreq.put(a.getColor(), groupFreq.get(a.getColor()) + 1);
            }
        }
        //System.out.println("Groups: " + groupFreq.toString());

        //int length = (int) ((this.chromLength.get(chromId) / this.scale) + 2);
        int[][] casesDel = new int[aberrationIds.size()][length];
        int[][] casesDup = new int[aberrationIds.size()][length];
        for (int i = 0; i < aberrationIds.size(); i++) {
            Arrays.fill(casesDel[i], 0);
            Arrays.fill(casesDup[i], 0);
        }

        int length1 = 0, ind = 0;
        AberrationIds aberrationId = null;
        // check for each aberration if it is aberrant, 
        // if so set aberration count := 1 for each pos it covers
        for (Aberration a : aberrations) {

            aberrationId = parentFrame.AberrationManager().getIdForAberration(a);
            ind = aberrationIds.indexOf(aberrationId);
            if (!fileout) {
                y1 = (int) Math.floor((a.getChromStart() - firstPos) / yScale);
            } else {
                y1 = (int) Math.floor((a.getChromStart()) / bin);
            }
            y1 = (y1 < 0 ? 0 : y1);     // aberration with start before firstPos

            if (!fileout) {
                y2 = (int) Math.ceil((a.getChromEnd() - firstPos) / yScale);
            } else {
                y2 = (int) Math.ceil((a.getChromEnd()) / bin);
            }
            y2 = (y2 > length - 2 ? length - 2 : y2); // cnv with end behind secondPos

            if (!fileout) {
                length1 = y2 - y1 > this.parentFrame.getMinSpotHeight() ? (int) (y2 - y1) : this.parentFrame.getMinSpotHeight();
            }
            if (a.getRatio() < 0) {
                for (int pos = y1; pos <= y2; pos++) {
                    casesDel[ind][pos + 1] = 1;
                }
            } else {
                for (int pos = y1; pos <= y2; pos++) {
                    casesDup[ind][pos + 1] = 1;
                }
            }
            if (a.isSelected() && !fileout) {

                g.setColor(new Color(
                        CNVCATPropertiesMod.props().getColorSelected().getRed() / 255,
                        CNVCATPropertiesMod.props().getColorSelected().getGreen() / 255,
                        CNVCATPropertiesMod.props().getColorSelected().getBlue() / 255,
                        (float) 0.1));

                g.fillRect(
                        0,
                        //left-(currentWidth/2)+(right-left)/2, 
                        top + y1,
                        this.ideogramWidth,
                        length1);

            }
        }

        int[] averageDup = new int[groups.size()];
        int[] averageDel = new int[groups.size()];

        Arrays.fill(averageDup, 0);
        Arrays.fill(averageDel, 0);

        int absFreq, iDup,
                iDel;

        float[] rgb;

        if (!fileout) {
            labelFont = "PLAIN-" + (int) this.parentFrame.getOffY() / 2;
            g.setFont(Font.decode(labelFont));
            defFont = g.getFont();
            fm = g.getFontMetrics();

            g.setColor(Color.black);
            g.drawLine(left, top, left, top + (length));
            g.drawLine(left - fWidth, top, left - fWidth, top + (length));
            g.drawLine(left - fWidth / 2, top, left - fWidth / 2, top + (length));
            g.drawLine(right, top - 1, right, top + (length));
            g.drawLine(right + fWidth, top, right + fWidth, top + (length));
            g.drawLine(right + fWidth / 2, top, right + fWidth / 2, top + (length));

            g.drawString("100", left - fWidth - fm.stringWidth("100") / 2, this.imageHeight - 1);
            g.drawString("50", left - fWidth / 2 - fm.stringWidth("50") / 2, this.imageHeight - 1);
            g.drawString("0", left - fm.stringWidth("0") / 2, this.imageHeight - 1);

            g.drawString("50", right + fWidth / 2 - fm.stringWidth("50") / 2, this.imageHeight - 1);
            g.drawString("100", right + fWidth - fm.stringWidth("100") / 2, this.imageHeight - 1);
            g.drawString("0", right - fm.stringWidth("0") / 2, this.imageHeight - 1);

        }

        double aMinDup, aMinDel;

        if (fileout && first) {
            try {
                int ci = 0;
                String groupname = "";
                out.write("chrom\tstart\tstop");
                for (int i = 0; i < groups.size(); i++) {
                    groupname = this.parentFrame.getColorName(groups.get(i));
                    //absFreq = groupFreq.get(groups.get(i));
                    if (groupname == null) {
                        groupname = "group" + Integer.toString(++ci);
                    }
                    out.write("\tnof_all_" + groupname + "\tnof_pos_" + groupname + "\tfreq_pos_" + groupname + "\tnof_neg_" + groupname + "\tfreq_neg_" + groupname);

                }
                out.write("\tpos_overlap\tneg_overlap");
                out.write("\n");
            } catch (IOException exception) {

                Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                return;
            }
        }
        absFreq = 0;
        for (int j = 0; j < length; j++) {
            absFreq = iDup = iDel = 0;
            Arrays.fill(averageDup, 0);
            Arrays.fill(averageDel, 0);
            aMinDup = Double.MAX_VALUE;
            aMinDel = Double.MAX_VALUE;

            // get cumulated frequencies per group and pos
            for (AberrationIds a : aberrationIds) {
                averageDup[groups.indexOf(a.getColor())] += casesDup[aberrationIds.indexOf(a)][j];
                averageDel[groups.indexOf(a.getColor())] += casesDel[aberrationIds.indexOf(a)][j];
            }

            if (fileout) {
                try {
                    out.write(chromId + "\t" + (this.firstPos + (j * bin)) + "\t" + (this.firstPos + (j * bin) + bin));
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            // get relative frequencies per group and pos

            for (int i = 0; i < groups.size(); i++) {

                absFreq = groupFreq.get(groups.get(i));

                if (fileout) {
                    try {
                        out.write("\t" + absFreq);
                    } catch (IOException exception) {

                        Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                    }
                }
                rgb = groups.get(i).getRGBColorComponents(null);
                if (!fileout) {
                    g.setColor(new Color(rgb[0], rgb[1], rgb[2], (float) CNVCATPropertiesMod.props().getTransFrequencies()));
                }
                if (averageDup[i] > 0) {
                    iDup++;
                    //sDup += "\t[" + (int) j * scale + top + "] " + averageDup[i] + "/" + absFreq;
                    //sDup += " (" + (int) (averageDup[i] * 100 / absFreq) + ")";
                    aMinDup = Math.min(aMinDup, (new Double(averageDup[i]) * fWidth / absFreq));

                    if (fileout) {
                        try {
                            out.write("\t" + averageDup[i] + "\t" + Double.toString(new Double(averageDup[i]) / absFreq));
                        } catch (IOException exception) {

                            Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                        }
                    } else {
                        g.fillRect(
                                right,
                                (top + j),
                                (averageDup[i] * fWidth / absFreq),
                                1);
                    }
                    //System.out.println("Freq Duplication: " +   (averageDel[i]  / absFreq) );
                    //System.out.println("Freq Duplication: " +  (int) (averageDel[i] * fWidth / absFreq) );
                } else if (fileout) {
                    try {
                        out.write("\t0\t0");
                    } catch (IOException exception) {

                        Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                    }
                }
                if (averageDel[i] > 0) {
                    iDel++;
                    //sDel += "\t[" + (int) j * scale + top + "] " + averageDel[i] + "/" + absFreq;
                    //sDel += " (" + (int) (averageDel[i] * 100 / absFreq) + ")";
                    aMinDel = Math.min(aMinDel, (new Double(averageDel[i]) * fWidth / absFreq));
                    if (fileout) {
                        try {
                            out.write("\t" + averageDel[i] + "\t" + Double.toString(new Double(averageDel[i]) / absFreq));
                        } catch (IOException exception) {

                            Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                        }
                    } else {
                        g.fillRect(
                                left - (averageDel[i] * fWidth / absFreq),
                                (top + j),
                                (averageDel[i] * fWidth / absFreq),
                                1);
                        //System.out.println("Freq Deletion: " +   (averageDel[i]  / absFreq) );
                        //System.out.println("Freq Deletion: " +  (int) (averageDel[i] * fWidth / absFreq) );
                    }
                } else if (fileout) {
                    try {
                        out.write("\t0\t0");
                    } catch (IOException exception) {

                        Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                    }
                }
            }

            if (iDup > 1) {
                if (fileout) {
                    try {
                        out.write("\t" + Double.toString(aMinDup / fWidth));
                    } catch (IOException exception) {

                        Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                    }
                } else {
                    g.setColor(CNVCATPropertiesMod.props().getColorOverlap());
                    g.fillRect(
                            right,
                            (top + j), (int) aMinDup,
                            1);
                }
            } else if (fileout) {
                try {
                    out.write("\t0");
                } catch (IOException exception) {

                    Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                }
            }
            if (iDel > 1) {

                if (fileout) {
                    try {
                        out.write("\t" + Double.toString(aMinDel / fWidth));
                    } catch (IOException exception) {

                        Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                    }
                } else {
                    g.setColor(CNVCATPropertiesMod.props().getColorOverlap());
                    g.fillRect(
                            left - (int) aMinDel,
                            (top + j),
                            (int) aMinDel,
                            1);
                }

            } else if (fileout) {
                try {
                    out.write("\t0");
                } catch (IOException exception) {

                    Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                }
            }
            if (fileout) {
                try {
                    out.write("\n");
                } catch (IOException exception) {

                    Logger.getLogger(MatrixAberration.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

                }
            }
        }

//System.out.println("DUP: " + sDup);
//System.out.println("DEL: " + sDel);
    }

    /**
     *
     * @param g
     * @param chromId
     * @param scale
     * @param left = ideogramLeft - bacWidth
     * @param right = ideogramLeft + ideogramWidth + bacWidth;
     * @param top
     * @param bacWidth - x width
     */
    public void printAberration(Graphics2D g,
            String chromId,
            int x1, int x2, int y0,
            int colNumbers) {

        boolean isDeletion = false;

        int dispColumnDeletion = 1;
        int dispColumnDuplication = 1;
        int length1;
        Color c;

        float ratioAlpha;
        float[] rgb;
        AberrationIds aberrationId;

        Logger.getLogger(MatrixAberration.class.getName()).log(
                Level.INFO, "Plot Data for  " + chromId + " y0: " + y0);

        java.util.List<? extends Aberration> aberrations = (List<Aberration>) parentFrame.AberrationManager().getAberrationsAtChrom(chromId);
        Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                "found " + (aberrations != null ? aberrations.size() : " none ") + " for " + chromId);

        if (aberrations == null || aberrations.size() == 0) {
            return;
        }

        if (!this.parentFrame.isFullChrom()) {
            aberrations = (List<? extends Aberration>) PlotLib.getSublist(
                    aberrations, chromId,
                    this.firstPos,
                    this.secondPos);
            Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                    "sublist:  " + (aberrations != null ? aberrations.size() : 0));

        }

        Collections.sort(aberrations, Aberration.compByStart);

        Vector<Aberration> stack;
        Vector<Aberration> stackDel = new Stack();
        Vector<Aberration> stackDup = new Stack();
        int _lastCol = 0;
        boolean stackend = false;
        Aberration lastAb;
        for (Aberration currentAberration : aberrations) {

            if (currentAberration.isHidden()) {
                continue;
            }
            isDeletion = currentAberration.getRatio() < 0;
            aberrationId = parentFrame.AberrationManager().getIdForAberration(currentAberration);

            if (this.parentFrame.isDisplayStacked()) {

                if (isDeletion) {
                    stack = stackDel;
                    //lastCol = dispColumnDeletion;
                } else {
                    stack = stackDup;
                    // lastCol = dispColumnDuplication;
                }
                stackend = true;
                // stack contains last printed aberration for each column
                for (_lastCol = 0;
                        _lastCol < (stack.size() < colNumbers ? stack.size() : colNumbers);
                        _lastCol++) {
                    lastAb = stack.get(_lastCol);
                    //chromStart < posEnd and chromEnd > posStart --- check overlap
                    if (((currentAberration.getChromStart() / yScale) < (lastAb.getChromEnd() / yScale) + 1)) {
                        // overlap -> next col
                        continue;
                    } else {
                        if (_lastCol > stack.size()) {
                            stack.add(_lastCol, currentAberration);
                        } else {
                            stack.set(_lastCol, currentAberration);
                        }
                        stackend = false;
                        break;
                    }
                }
                // new column
                if (stackend) {
                    // check if max number of columns is reached
                    if (_lastCol >= parentFrame.getNofCols()) {
                        _lastCol = 0;
                        stack = new Stack();
                    }

                    stack.add(_lastCol, currentAberration);
                }

                if (isDeletion) {
                    dispColumnDeletion = _lastCol + 1;
                    stackDel = stack;

                } else {
                    dispColumnDuplication = _lastCol + 1;
                    stackDup = stack;
                }

            } else if (isDeletion) {
                dispColumnDeletion = aberrationId.getXDispColumn();
            } else {
                dispColumnDuplication = aberrationId.getXDispColumn();
            }

            c = aberrationId.getColor();
            ratioAlpha = (float) 1.0;
            if (this.parentFrame.isTransRatio()) {
                ratioAlpha = parentFrame.ScoreFilterManager().getAlphaByRatio(currentAberration, maxRatio);
            } else if (this.parentFrame.isTransQuality()) {
                ratioAlpha = parentFrame.ScoreFilterManager().getAlphaByQuality(currentAberration, maxQuality);
            } else {
                ratioAlpha = parentFrame.ScoreFilterManager().getAlpha(currentAberration);
            }

            rgb = c.getRGBColorComponents(null);

            //g.setColor(new Color(rgb[0], rgb[1], rgb[2], ratioAlpha));
            // kt 05112014
            y1 = (int) ((currentAberration.getChromStart() > firstPos ? (currentAberration.getChromStart() - firstPos) : 0)
                    / yScale);

            y2 = (int) +((currentAberration.getChromEnd() < secondPos ? (currentAberration.getChromEnd() - firstPos) : secondPos - firstPos)
                    / yScale);

            length1 = (y2 - y1) > this.parentFrame.getMinSpotHeight() ? (int) (y2 - y1) : this.parentFrame.getMinSpotHeight();
            y1 += y0;
            y2 += y0;
            if (isDeletion) {

                currentAberration.setXDispColumn(-dispColumnDeletion);
                if (currentAberration.isSelected()) {
                    // print highlightning bars

                    g.setColor(new Color(
                            CNVCATPropertiesMod.props().getColorSelected().getRed() / 255,
                            CNVCATPropertiesMod.props().getColorSelected().getGreen() / 255,
                            CNVCATPropertiesMod.props().getColorSelected().getBlue() / 255,
                            (float) 0.1));

                    g.fillRect(
                            x1 - (dispColumnDeletion * (int) (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + this.parentFrame.getGap()))),
                            y0,
                            (int) (this.xScale * CNVCATPropertiesMod.props().getProbeWidth()),
                            this.imageHeight);
                    //g.fillRect(x, y, width, length)
                    g.fillRect(
                            // left ideogram , half width, half ideogram width
                            0,
                            //center-(1/2*ideogramWidth),
                            y1,
                            this.imageWidht,
                            length1);
                }

                g.setColor(new Color(rgb[0], rgb[1], rgb[2], ratioAlpha));
                g.fillRect(
                        x1 - (dispColumnDeletion * (int) (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + this.parentFrame.getGap()))),
                        y1,
                        (int) (this.xScale * CNVCATPropertiesMod.props().getProbeWidth()),
                        length1);
            } else {
                currentAberration.setXDispColumn(dispColumnDuplication);
                if (currentAberration.isSelected()) {

                    g.setColor(new Color(
                            CNVCATPropertiesMod.props().getColorSelected().getRed() / 255,
                            CNVCATPropertiesMod.props().getColorSelected().getGreen() / 255,
                            CNVCATPropertiesMod.props().getColorSelected().getBlue() / 255,
                            (float) 0.1));

                    g.fillRect(
                            x2 + (dispColumnDuplication * (int) (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + parentFrame.getGap()))),
                            y0,
                            (int) (this.xScale * CNVCATPropertiesMod.props().getProbeWidth()),
                            this.imageHeight);
                    g.fillRect(0,
                            //center-(1/2*ideogramWidth),
                            y1,
                            this.imageWidht,
                            length1);
                }

                g.setColor(new Color(rgb[0], rgb[1], rgb[2], ratioAlpha));
                g.fillRect(
                        x2 + (dispColumnDuplication * (int) (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + parentFrame.getGap()))),
                        y1,
                        (int) (this.xScale * CNVCATPropertiesMod.props().getProbeWidth()),
                        length1);
            }
        }
    }

    /**
     * draw the image at the genome level
     */
    /* public void compactGenomeImage() {
    this.isGenome = true;
    
    
    
    
    iActivePhenotypes =
    aberrationManager.getActiveCasesSize();
    this.ideogramWidth = (3 * CNVCATPropertiesMod.props().getProbeWidth());
    int colNumber = 0;
    if (!this.parentFrame.isDisplayFreq()) {
    
    colNumber = getColNumbers(iActivePhenotypes);
    
    
    this.compactWidth =
    2 * colNumber * (int) (this.xScale * CNVCATPropertiesMod.props().getProbeWidth()) +
    (int) (this.xScale * this.ideogramWidth) +
    MatrixAberration.left + MatrixAberration.right;
    } else {
    this.compactWidth =
    (2 * 100) +
    (int) (this.xScale * this.ideogramWidth) +
    MatrixAberration.left + MatrixAberration.right;
    }
    
    //this.width =  MatrixAberration.genomeViewCols * this.compactWidth;
    //this.height = (this.compactHeight) * this.genomeViewRows;
    try {
    image = new BufferedImage(
    this.compactWidth * MatrixAberration.genomeViewCols,
    this.compactHeight * MatrixAberration.genomeViewRows,
    BufferedImage.TYPE_INT_ARGB_PRE);
    
    } catch (Error e) {
    throw new RuntimeException(e.getMessage());
    }
    
    Graphics2D g = image.createGraphics();
    g.setColor(CNVCATPropertiesMod.props().getColorBackGround());
    g.fillRect(0, 0, image.getWidth(), image.getHeight());
    
    Font defFont = g.getFont();
    FontMetrics fm;
    
    long length = 0;
    
    for (int row = 0; row <
    MatrixAberration.genomeViewRows; row++) {
    for (int column = 0; column <
    MatrixAberration.genomeViewCols; column++) {
    int k = row * MatrixAberration.genomeViewCols + column;
    
    if ((k + 1) == 23) {
    chromId = "chrX";
    } else {
    if ((k + 1) == 24) {
    chromId = "chrY";
    } else {
    chromId = "chr" + (k + 1);
    }
    
    }
    length = this.chromLength.get(chromId);
    this.scale = length / (this.compactHeight - MatrixAberration.top - MatrixAberration.bottom);
    
    
    this.tops[k] = (row * this.compactHeight) + MatrixAberration.top;
    
    this.lefts[k] = (column * this.compactWidth) + (MatrixAberration.left);
    
    this.xCenter = this.lefts[k] +
    (this.compactWidth - MatrixAberration.left - MatrixAberration.right) / 2;
    
    g.setColor(Color.white);
    g.fill3DRect(this.lefts[k] - (MatrixAberration.left / 2),
    this.tops[k] - MatrixAberration.top * 3 / 4,
    this.compactWidth - (MatrixAberration.left / 2) - (MatrixAberration.right / 2),
    this.compactHeight - (MatrixAberration.top / 4),
    true);
    
    cytoBandManager.plot(g,
    chromId,
    xCenter - ((int) (this.xScale * ideogramWidth)) / 2,
    this.tops[k],
    (int) (this.xScale * this.ideogramWidth),
    this.scale);
    
    
    
    
    
    
    if (this.parentFrame.isDisplayFreq()) {
    printFreqAberration(g,
    this.chromId,
    this.scale,
    xCenter - (int) (this.xScale * ideogramWidth),
    xCenter + (int) (this.xScale * ideogramWidth),
    this.tops[k],
    this.compactWidth / 4);
    } else {
    printAberration(g,
    this.chromId,
    this.scale,
    xCenter - (int) (this.xScale * ideogramWidth),
    xCenter + (int) (this.xScale * ideogramWidth),
    this.tops[k], colNumber);
    }
    
    g.setColor(Color.black);
    g.setFont(Font.decode("PLAIN-BOLD-14"));
    fm =
    g.getFontMetrics();
    g.drawString(chromId.substring(3),
    this.xCenter - fm.stringWidth(chromId.substring(3)) / 2,
    this.tops[k] + (int) (length / this.scale) + MatrixAberration.bottom / 2);
    
    g.setFont(defFont);
    
    }
    
    setIcon(new ImageIcon());
    
    ((ImageIcon) this.getIcon()).setImage(image);
    
    
    this.chromId = CNVCATFrame.ALL_CHROMS;
    
    parentFrame.setStatusBar("Chromosome: ALL");
    
    
    }
    
    }*/
    int getColNumbers(int iActivePhenotypes) {
        int colNumber = 0;
        int maxOverlapNo = 0;
        if (this.parentFrame.isDisplayStacked()) {
            /*
            if (iActivePhenotypes <= 5) {
            maxOverlapNo = 60;
            } else {
            if (iActivePhenotypes <= 10) {
            maxOverlapNo = 40;
            } else {
            if (iActivePhenotypes <= 20) {
            maxOverlapNo = 20;
            } else {
            if (iActivePhenotypes <= 40) {
            maxOverlapNo = 10;
            } else {
            maxOverlapNo = 5;
            }
            
            }
            }
            }
             */
            colNumber = CNVCATPropertiesMod.props().getNofCols();
        } else {
            colNumber = iActivePhenotypes;
        }

        return colNumber;
    }

    public void viewAberrationInfo() {
        this.viewAberrationInfo(this.chromId, firstPos, this.secondPos, 0);
    }

    public void viewAberrationInfo(String chromId, long y1, long y2, long xPos) {
        parentFrame.AberrationManager().clearDispAberrations();
        java.util.List<? extends Aberration> aberrations = (List<Aberration>) parentFrame.AberrationManager().getAberrationsAtChrom(chromId);
        if (aberrations == null) {
            return;
        }

        aberrations = (List<? extends Aberration>) PlotLib.getSublist(
                aberrations, chromId, y1, y2);
        //yChrPosition - (int) this.yScale * CNVCATPropertiesMod.props().getSelectionTolerance(),
        //yChrPosition + (int) this.yScale * CNVCATPropertiesMod.props().getSelectionTolerance());

        for (Aberration a : aberrations) {
            if ((a.getXDispColumn() == xPos)
                    && !a.isHidden() && Math.signum(xPos) == Math.signum(a.getRatio())) {
                a.setSelected(true);
            } else {
                a.setSelected(false);
            }

        }
        parentFrame.AberrationManager().setDispAberrations(aberrations);

        this.parentFrame.jTabbedPane1.setSelectedIndex(1);
    }

    private boolean maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            System.out.println("show popup");
            popup.show(e.getComponent(),
                    e.getX(), e.getY());
            return true;
        } else {
            return false;
        }

    }

    /**
     * map mouse position to cnv position
     *
     * @param x
     * @return
     */
    public long mapXPosition(long x) {
        long col = 0;

        //int x1 =  (this.imageWidht / 2) -/+ ideogramWidth;
        //x =  x1 - (col * (int) (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + xGap);
        // x1-x =  (col * (int) (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + xGap);
        //col =  (x1-x)/(int) (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + xGap ;
        if (Math.abs(((this.imageWidht / 2) - x)) < this.ideogramWidth) {
            return 0;
        }

        x -= (this.imageWidht / 2);
        if (x < 0) {
            x += this.ideogramWidth;
        } else {
            x -= this.ideogramWidth;
        }

        col = (long) (x / (this.xScale * (CNVCATPropertiesMod.props().getProbeWidth() + this.parentFrame.getGap())));
        if (col < -1) {
            col--; //??!!

        }

        System.out.println("xCol: " + col);
        //return isDel * col;
        return col;

    }

    /**
     * map mouse position to chromosomal location
     *
     * @param mousePos
     * @return
     */
    public long mapYPosition(long mousePos, boolean getFirst) {

        if (mousePos < parentFrame.getOffY() && parentFrame.release != null) {
            if (getFirst) {

                return CytoBandManagerImpl.getFirst(
                        GenomeRelease.toRelease(parentFrame.release),
                        parentFrame.chromId).getChromStart();
            } else {
                return -1;
            }

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
    private boolean showDetailFrame = false;
    private long secondPixY = 0;
    private long firstPixY = 0;
    private long firstPixX = 0;

    private void initMenues() {
        this.popup = new JPopupMenu();
        this.menuShowData = new JMenuItem("show Data");
        this.menuShowData.addActionListener(
                new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Long xPos = mapXPosition(xMousePosition);
                Long yPos = mapYPosition(parentFrame.getMousePos(), true);
                MatrixAberration.this.showDetailData(xPos, yPos);
            }
        });
        popup.add(menuShowData);
        /*this.menuItemSetStart = new JMenuItem("set global start");
        this.menuItemSetStart.addActionListener(
        new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
        long pos = mapYPosition(firstPixY, true);
        WebPositionPanel.setWebPositionChrom(chromId);
        WebPositionPanel.setWebPositionStart(Long.toString(pos));
        }
        });
        
        popup.add(menuItemSetStart);
        this.menuItemSetEnd = new JMenuItem("set global end");
        this.menuItemSetEnd.addActionListener(
        new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
        
        long pos = mapYPosition(secondPixY, true);
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

    public void mouseClicked(MouseEvent e) {
        this.firstPixX = e.getPoint().x;
        if (!this.maybeShowPopup(e)) {
            // select CNV

            long xPos = this.mapXPosition(e.getPoint().x);
            long yPos = mapYPosition(e.getPoint().y, false);
            Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                    "xPos: " + xPos + " yPos: " + yPos);

            if (yPos
                    < 0) {
                return;
            }

            this.parentFrame.setPosition(yPos);

            viewAberrationInfo(
                    this.chromId, yPos - (int) this.yScale * CNVCATPropertiesMod.props().getSelectionTolerance(),
                    yPos + (int) this.yScale * CNVCATPropertiesMod.props().getSelectionTolerance(),
                    xPos);
            /*
        isgenome!!
        int chrNo =
        y / (this.compactHeight) * MatrixAberration.genomeViewCols +
        x / (this.compactWidth) + 1;
        //System.out.println("MouseClicked ALL " + chrNo);
        lChromId =
        RegionLib.fromIntToChr(chrNo);
        this.scale = this.chromLength.get(lChromId) / (this.compactHeight - MatrixAberration.top - MatrixAberration.bottom);
        
        yChrPosition =
        (long) ((y - tops[chrNo - 1]) * scale);
        x =
        (int) (long) ((x - lefts[chrNo - 1]));
             */

        }
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        firstPixY = secondPixY = ((Point) e.getPoint()).y;
        this.maybeShowPopup(e);
        this.setShowDetailFrame(true);
    }

    public void mouseReleased(MouseEvent e) {

        secondPixY = ((Point) e.getPoint()).y;
        this.maybeShowPopup(e);
        if (secondPixY - firstPixY < 1) {
            this.showDetailFrame = false;
            return;

        }

        long _firstPos = mapYPosition(firstPixY, true);
        long _secondPos = mapYPosition(secondPixY, true);

        Logger.getLogger(MatrixAberration.class.getName()).log(Level.INFO,
                "Detail: begin: " + _firstPos + " end: " + _secondPos);

        //this.refreshPlot();
        parentFrame.showDetails(_firstPos, _secondPos);
    }
    long xMousePosition = 0;

    void showDetailData(long xPos, long yPos) {

        java.util.List<? extends Aberration> aberrations = parentFrame.AberrationManager().getAberrationsAtChrom(chromId);

        if (aberrations == null || aberrations.size() == 0) {
            return;
        }

        long dist = (long) (yScale * CNVCATPropertiesMod.props().getSelectionTolerance());
        java.util.List<? extends Aberration> curr = (List<? extends Aberration>) PlotLib.getSublist(aberrations, chromId,
                yPos - dist, yPos + dist);

        this.parentFrame.showDetailData(null);
        for (Aberration a : curr) {
            if ((a.getXDispColumn() == (xPos)) && !a.isHidden() && Math.signum(xPos) == Math.signum(a.getRatio())) {
                this.parentFrame.showDetailData(a);
                break;

            }

        }
    }

    public void mouseMoved(MouseEvent e) {

        this.parentFrame.setMousePos(e.getPoint().y);
        this.xMousePosition = e.getPoint().x;

        Long xPos = this.mapXPosition(e.getPoint().x);
        Long yPos = mapYPosition(e.getPoint().y, true);
        this.parentFrame.setPosition(yPos);
        if (!this.parentFrame.isShowruler() && !this.parentFrame.showDetailData()) {
            return;
        }

        if (this.parentFrame.showDetailData()) {
            this.showDetailData(xPos, yPos);
        }

        if (this.parentFrame.isShowruler()) {
            this.parentFrame.updateImages();
        }

    }

    public void mouseDragged(MouseEvent e) {
        secondPixY = ((Point) e.getPoint()).y;
        if (this.showDetailFrame) {
            this.repaint();
        }

    }

    public void rescale() {
        this.yScale = (this.secondPos - this.firstPos)
                / (this.imageHeight - (this.parentFrame.getOffY() * 2));
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

    public void setFirstPos(long y) {
        this.firstPos = y;
    }

    public void setSecondPos(long y) {
        this.secondPos = y;
    }

    public boolean isShowDetailFrame() {
        return this.showDetailFrame;
    }

    public void setShowDetailFrame(boolean b) {
        this.showDetailFrame = b;
    }
}
