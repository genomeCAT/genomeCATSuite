package org.molgen.genomeCATPro.cat.maparr;

/** * @(#)ArrayView.java 
 * *  * @author Katrin Tebel
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License 
 * * as published by the Free Software Foundation; either version 2 
 * * of the License, or (at your option) any later version, 
 * * provided that any use properly credits the author. 
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * * */
import java.sql.*;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.Vector;
import java.util.Collections;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.annotation.PlotLib;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.cat.util.*;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.guimodul.data.ZoomYAction;

/**
 * Basic class to display array data
 * load data 
 *	todo: for bac arrays there are more than one row per id
 * 260612   kt  bug mouseMove showData
 * 280612   kt  add Menu (zoom  ShowData )  
 *                  
 * 
 */
public class ArrayView extends JPanel {

    String dataName = null;
    RegionArray spot = null;
    Long arrayId = new Long(0);
    String tableName = "";
    Defaults.GenomeRelease release = null;
    /* maximal ratio value */
    double pos_max_y = 0;
    /** maximal chromosomal position acc. to real data */
    double pos_real_max_x = 0;
    /** define view dimension */
    int view_max_y = Defines.ARRAY_HEIGTH - (Defines.ARRAY_OFFSET * 2);           // height

    static final int off_legend = Defines.ARRAY_WIDTH / 20;		// space for legend (i.e. color scale)

    static final double view_max_x = Defines.ARRAY_WIDTH - (2 * off_legend);                  // width

    final static DecimalFormat myFormatter = new DecimalFormat("0.###");
    /** define offset as real start of chromsomal region */
    //static double s_pos_off_x = 0;
    //double pos_off_x = 0;
    /** define maximal x position as end of chromosomal region acc. cytobands or subarea*/
    //static int s_pos_max_x = 0;
    //long pos_max_x = 0;
    /** center line is equivalent to ratio = 0 */
    int center = Math.round(Defines.ARRAY_HEIGTH / 2);
    /** extrem ratio values */
    double minRatio = 0;
    double maxRatio = 0;
    double scale_y = 1.0;
    boolean showThresholds = false;
    double negThreshold = 0.0;
    double posThreshold = 0.0;
    /** link to current chrom */
    ChromTab chromtab = null;
    /** color scale as legend */
    BufferedImage color = null;
    /** array data vectors - name */
    Vector<String> arrayName = new Vector<String>();
    /** array data vectors - chromosomal start position */
    Vector<Long> arrayStart = new Vector<Long>();
    /** array data vectors - chromosomal end position */
    Vector<Long> arrayStop = new Vector<Long>();
    /** array data vectors - ratio */
    Vector<Double> arrayRatio = new Vector<Double>();
    // edges for subarea
    long secondPixX = 0;
    long firstPixX = 0;
    long firstPixY = 0;
    // range of values
    long firstPos = 0;
    long secondPos = 0;
    private JPopupMenu popup;
    private JMenuItem menuShowData;
    private JMenuItem menuItemZoomIn;
    private JMenuItem menuItemZoomOut;

    // current user focus position at the image
    /**
     * default constructor 
     */
    public ArrayView() {
        super();

    }

    public Integer getDataSize() {
        return this.arrayName.size();
    }

    /**
     * construct and load data from db
     * @param minRatio
     * @param maxRatio
     * @param arrayId
     * @param chromtab 
     */
    public ArrayView(ArrayData d, ChromTab chromtab) throws Exception {
        super();
        try {


            Logger.getLogger(ArrayView.class.getName()).log(
                    Level.INFO, "create ArrayView from db for " + d.getName() +
                    " at " + chromtab.chrom);


            initView();

            this.arrayId = d.getId();
            this.dataName = d.getName();
            if (!(d instanceof ArrayDataAnno)) {
                this.spot = ArrayManager.getClazz(d.getData().getClazz());
                this.tableName = d.getData().getTableData();
                this.minRatio = d.getData().getMinRatio();
                this.maxRatio = d.getData().getMaxRatio();
            }
            this.release = GenomeRelease.toRelease(d.getRelease());
            this.chromtab = chromtab;
            LoadArrayChrom();
            setRange();
        } catch (Exception e) {
            Logger.getLogger(ArrayView.class.getName()).log(
                    Level.SEVERE, "ArrayView", e);
        }


    }

    /** 
     * construct with given data
     *  @param minRatio
     * @param maxRatio
     * @param arrayId
     * @param names 
     * @param start
     * @param stop 
     * @param data 
     * @param chromtab
     */
    public ArrayView(ArrayData d,
            Vector<String> names, Vector<Long> start, Vector<Long> stop,
            Vector<Double> data, ChromTab chromtab) {

        super();
        try {
            Logger.getLogger(ArrayView.class.getName()).log(Level.INFO, "create ArrayView given data");
            initView();


            this.arrayId = d.getId();
            this.tableName = null;
            this.dataName = d.getName();
            if (!(d instanceof ArrayDataAnno)) {
                this.spot = ArrayManager.getClazz(d.getData().getClazz());
                this.minRatio = d.getData().getMinRatio();
                this.maxRatio = maxRatio = d.getData().getMaxRatio();
            }
            this.release = GenomeRelease.toRelease(d.getRelease());
            this.chromtab = chromtab;
            if (names != null) {
                arrayName = names;
            }
            if (start != null) {
                arrayStart = start;
            }
            if (stop != null) {
                arrayStop = stop;
            }
            if (data != null) {
                arrayRatio = data;
            }
            setRange();
        } catch (Exception e) {
            Logger.getLogger(ArrayView.class.getName()).log(
                    Level.SEVERE, "ArrayView", e);
        }
    }

    public Long getArrayId() {
        return arrayId;
    }

    public void setArrayId(Long arrayId) {
        this.arrayId = arrayId;
    }

    /**
     * load data from db
     * 
     */
    void LoadArrayChrom() throws Exception {
        Logger.getLogger(ArrayView.class.getName()).log(Level.INFO,
                "LoadArrayChrom");

        try {
            String sql =
                    " select distinct chromStart, chromEnd, " +
                    this.spot.getRatioColName() + ", " +
                    this.spot.getProbeColName() +
                    " from " + this.tableName +
                    " where chrom = ? order by chromStart";
            Connection con = Database.getDBConnection(Defaults.localDB);
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, chromtab.chrom);
            ResultSet rs = ps.executeQuery();
            int id = 0;
            while (rs.next()) {



                this.arrayStart.add(id, new Long(rs.getLong(1)));
                this.arrayStop.add(id, new Long(rs.getLong(2)));
                this.arrayRatio.add(id, new Double(rs.getDouble(3)));
                this.arrayName.add(id, rs.getString(4));
                id++;
            }
        }// start, ende (laenge) ratio
        catch (Exception e) {
            Logger.getLogger(ArrayView.class.getName()).log(
                    Level.SEVERE, "LoadArrayChrom", e);
            throw e;
        //throw new RuntimeException(e);
        }
        if (arrayRatio.size() == 0) {
            arrayRatio.add(0.0);
            arrayStart.add(new Long(0));
            arrayStop.add(new Long(0));
            arrayName.add("no data");
        //throw new RuntimeException("no data found for " + arrayId + " chrom: " + chromtab.chrom);

        }
    }

    /**
     * init visual components
     */
    void initView() {
        setPreferredSize(new Dimension(Defines.ARRAY_WIDTH, Defines.ARRAY_HEIGTH));
        //setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new Zoom());
        addMouseMotionListener(new Zoom());
        color = new BufferedImage(Defines.ARRAY_OFFSET,
                Defines.ARRAY_HEIGTH - 2 * Defines.ARRAY_OFFSET,
                BufferedImage.TYPE_INT_ARGB_PRE);
        paintColorScale(color);
        this.initMenues();
    }

    /**
     * initialise visual range acc. array data range 
     */
    void setRange() {

        pos_max_y = Math.max(Math.abs(minRatio), Math.abs(maxRatio));
        //pos_max_y = (pos_max_y > 1) ? pos_max_y : 1.0;

        chromtab.pos_max_x = Math.max(chromtab.pos_max_x, CytoBandManagerImpl.getLast(
                this.release, this.chromtab.chrom).getChromEnd());



        if (arrayStop.size() > 0) {
            pos_real_max_x = Collections.max(arrayStop);
        } else {
            pos_real_max_x = 1;
        }

        chromtab.pos_off_x = Math.min(chromtab.pos_off_x, CytoBandManagerImpl.getFirst(
                this.release, this.chromtab.chrom).getChromStart());

        setScale();
        //


        Logger.getLogger(ArrayView.class.getName()).log(
                Level.INFO, "set range: center: " + center + " pos_off_x: " + chromtab.pos_off_x + " pos_max_x: " + chromtab.pos_max_x + "  pos_max_y: " + pos_max_y);
    }

    /**
     * initialise visual y range acc. array data range 
     */
    void setRangeY() {

        pos_max_y = Math.max(Math.abs(minRatio), Math.abs(maxRatio));


        setScale();

    }

    public double getPosMaxY() {
        return pos_max_y;
    }

    /**
     * map visual range - data range for subview of data defined with begin - end
     * @param begin
     * @param end 
     */
    public void setRange(Long begin, Long end) {
        chromtab.pos_max_x = end;
        //      end >  chromtab.pos_max_x ? end : chromtab.pos_max_x;

        chromtab.pos_off_x = begin;
        //      begin <  chromtab.pos_off_x ? begin : chromtab.pos_off_x;

        pos_real_max_x = end;

        //pos_max_x = ((Integer) Collections.max(arrayStop)).intValue();	
        //pos_off_x = ((Integer) arrayStart.get(0)).intValue();	
        Logger.getLogger(ArrayView.class.getName()).log(
                Level.INFO, " set range_begin_end: pos_off_x: " + chromtab.pos_off_x + " pos_max_x: " + chromtab.pos_max_x);
        setScale();
    }

    /**
     * calculate scaling factor
     */
    void setScale() {
        // start-ende daten

        chromtab.scale_x = (chromtab.pos_max_x - (chromtab.pos_off_x)) / (view_max_x);
        scale_y = (this.view_max_y / 2) / pos_max_y;
        Logger.getLogger(ArrayView.class.getName()).log(
                Level.FINE, "set scale: scale_x = " + chromtab.scale_x + ", scale_y = " + scale_y);
        this.userScale = 1.0;
    }
    double userScale = 1.0;
    //user defined scaling

    void scaleHeight(double scaleFactor) {
        this.userScale = scaleFactor;
        // enhance view area
        this.view_max_y = (int) Math.round(scaleFactor * (Defines.ARRAY_HEIGTH - (Defines.ARRAY_OFFSET * 2)));

        // scaling factor
        this.scale_y = (this.view_max_y / 2) / Math.max(Math.abs(minRatio), Math.abs(maxRatio));

        this.pos_max_y = ((Defines.ARRAY_HEIGTH - (Defines.ARRAY_OFFSET * 2)) / 2) / this.scale_y;

    //setScale();
    //calculate upper and lower bound


    }

    public void scaleHeightFix(double fixY) {
        //reset to normal
        this.view_max_y = Math.round((Defines.ARRAY_HEIGTH - (Defines.ARRAY_OFFSET * 2)));
        this.scale_y = (this.view_max_y / 2) / fixY;
        this.pos_max_y = fixY;

    }

    /**
     * map absolute at relative scale position 
     */
    public Long mapPosition(long mousePos) {

        if (mousePos < off_legend) {
            return new Long(chromtab.pos_off_x);
        }
        if ((mousePos - off_legend) > view_max_x) {
            //return (pos_off_x + ((int) Math.floor(scale_x * (view_max_x ))));
            return new Long((long) Math.max(chromtab.pos_max_x, pos_real_max_x));
        }

        return new Long((long) (chromtab.pos_off_x + (chromtab.scale_x * new Double(mousePos - off_legend))));
    }

    Double getMinRatio() {
        return this.minRatio;
    }

    Double getMaxRatio() {
        return this.maxRatio;
    }

    /**
     * returns a new ArrayView that is a subset of the current view
     * @param begin
     * @param end
     * @param chromtab
     * @return 
     */
    /**
     * filter this array instance with given thresholds, create new instance
     * without synch to database
     * add new ArrayView to p chromtab
     * @param arrayId
     * @param posThreshold
     * @param negThreshold
     * @return
     */
    /**
     * do global painting
     * @param showThreshold
     * @param posThreshold
     * @param negThreshold 
     */
    public void setThreshold(boolean showThreshold, double posThreshold, double negThreshold) {
        this.showThresholds = showThreshold;
        this.posThreshold = posThreshold;
        this.negThreshold = negThreshold;
        Logger.getLogger(ArrayView.class.getName()).log(
                Level.INFO, "ArrayView.setThreshold  " + this.showThresholds + " + " +
                this.posThreshold + "  - " + this.negThreshold);
    }
    int detailY;

    @Override
    /**
     * paint static stuff (legend etc)
     */
    public void paint(Graphics g) {
        paintColorScale(color);
        setBackground(Color.white);
        super.paint(g);

        int iColor;
        if (this.showThresholds && this.posThreshold != 0.0) {
            iColor = ArrayViewBase.mapColorGradient(this.posThreshold, pos_max_y);
            g.setColor(ArrayViewBase.getColor(iColor,
                    parent != null ? parent.isColorScaleRedGreen() : true));
            g.drawLine(off_legend,
                    center - (int) (scale_y * this.posThreshold),
                    off_legend + (int) view_max_x,
                    center - (int) (scale_y * this.posThreshold));
        } 	// center line

        if (this.showThresholds && this.negThreshold != 0.0) {
            iColor = ArrayViewBase.mapColorGradient(this.negThreshold, pos_max_y);
            g.setColor(ArrayViewBase.getColor(iColor,
                    parent != null ? parent.isColorScaleRedGreen() : true));
            g.drawLine(off_legend,
                    center + (int) (scale_y * (-1 * this.negThreshold)),
                    off_legend + (int) view_max_x,
                    center + (int) (scale_y * (-1 * this.negThreshold)));
        }


        //g.drawRect(first, (Defines.ARRAY_OFFSET), second - first, view_max_y);
        //img x, y
        g.drawImage(color, (int) (off_legend + view_max_x + Defines.ARRAY_OFFSET), (Defines.ARRAY_OFFSET), this);
        paintArrayView(g);
        Font defFont = g.getFont();
        g.setColor(Color.black);
        g.setFont(Font.decode("PLAIN-10"));
        //iColor = ArrayViewBase.mapColorGradient(this.legend_maxRatio, pos_max_y);
        //g.setColor(ArrayViewBase.getColor(iColor));
        g.setColor(Color.lightGray);
        g.drawLine(
                off_legend, //x
                Defines.ARRAY_OFFSET, //y
                off_legend + (int) view_max_x,
                Defines.ARRAY_OFFSET);


        g.setColor(Color.black);

        //FontMetrics fm = g.getFontMetrics();
        DecimalFormat f = new DecimalFormat("#0.00");


        g.drawString(f.format(this.pos_max_y), ((off_legend / 2) + 1), (Defines.ARRAY_OFFSET));

        g.setColor(Color.black);
        g.drawLine(off_legend, center, off_legend + (int) view_max_x, center); 	// center line

        g.drawString("0", ((off_legend / 2) + 1), center + 10 / 2);

        //iColor = ArrayViewBase.mapColorGradient(this.legend_minRatio, pos_max_y);
        //g.setColor(ArrayViewBase.getColor(iColor));
        g.setColor(Color.lightGray);
        g.drawLine(
                off_legend,
                (Defines.ARRAY_HEIGTH - Defines.ARRAY_OFFSET), // height,
                off_legend + (int) view_max_x,
                (Defines.ARRAY_HEIGTH - Defines.ARRAY_OFFSET));
        g.setColor(Color.black);
        g.drawString(f.format(-this.pos_max_y),
                ((off_legend / 2) + 1),
                Defines.ARRAY_HEIGTH - (Defines.ARRAY_OFFSET) + 10);
        g.setFont(defFont);
        /*
         * x1 - the first point's x coordinate. y1 - the first point's y coordinate. x2 - the second point's x coordinate. y2 - the second point's y coordinate.
         */
        //int pix_x = ArrayView.off_legend + (int) Math.round((this.chromtab.pos_ruler - chromtab.pos_off_x) / this.chromtab.scale_x);
        if (this.parent.isShowRuler()) {
            g.drawLine(
                    (int) ArrayView.this.chromtab.getMousepos(), 0,
                    (int) ArrayView.this.chromtab.getMousepos(), Defines.ARRAY_HEIGTH);
        }
        // plot dragged detail window        
        if (showDetailFrame) {
            g.setColor(Color.RED);
            //x - the x coordinate of the rectangle to be drawn. 
            //y - the y coordinate of the rectangle to be drawn.
            //width - the width of the rectangle to be drawn. 
            //height - the height of the rectangle to be drawn.
            g.drawRect(
                    (int) this.firstPixX,
                    0, (int) (this.secondPixX - this.firstPixX),
                    Defines.ARRAY_HEIGTH //detailX < this.imageWidht / 2 ? (this.imageWidht / 2 - detailX) * 2 : (detailX - this.imageWidht / 2) * 2,
                    );

        }

    }

    /**
     * do painting / plotting
     * @param g 
     */
    public void paintArrayView(Graphics g) {
        try {

            long start, stop, _start, _width;

            double ratio;
            int iColor;
            //left handed legend

            for (int i = 0; i < arrayRatio.size(); i++) {
                /*
                x - the x coordinate of the rectangle to be drawn.
                y - the y coordinate of the rectangle to be drawn.
                width - the width of the rectangle to be drawn.
                height - the height of the rectangle to be drawn.
                 */
                start = arrayStart.get(i).longValue();
                stop = arrayStop.get(i).longValue();

                _start = (start - chromtab.pos_off_x) > 0 ? start - chromtab.pos_off_x : 1;
                _width = (stop > chromtab.pos_max_x ? (chromtab.pos_max_x - chromtab.pos_off_x) - _start : (stop - chromtab.pos_off_x) - _start);


                ratio = arrayRatio.get(i).doubleValue();
                if (stop < chromtab.pos_off_x) {
                    continue;
                }
                if (start > chromtab.pos_max_x) {
                    break;
                }
                iColor = ArrayViewBase.mapColorGradient(ratio, pos_max_y);

                g.setColor(ArrayViewBase.getColor(iColor,
                        parent != null ? parent.isColorScaleRedGreen() : true));
                if (ratio > 0) {

                    g.drawRect(off_legend + (int) Math.round(_start / chromtab.scale_x),
                            center - (int) (ratio * scale_y),
                            (int) Math.round(_width / chromtab.scale_x),
                            (int) Math.round(ratio * scale_y));
                    if (((int) ((stop - start) / chromtab.scale_x)) >= 1) {
                        g.fillRect(off_legend +
                                (int) Math.round(_start / chromtab.scale_x),
                                center - (int) (ratio * scale_y),
                                (int) Math.round(_width / chromtab.scale_x),
                                (int) Math.round(ratio * scale_y));
                    }
                } else {

                    g.drawRect(off_legend + (int) Math.round(_start / chromtab.scale_x),
                            center,
                            (int) Math.round(_width / chromtab.scale_x),
                            -(int) Math.round(ratio * scale_y));
                    if (((int) ((stop - start) / chromtab.scale_x)) >= 1) {
                        g.fillRect(off_legend + (int) Math.round(_start / chromtab.scale_x),
                                center,
                                (int) Math.round(_width / chromtab.scale_x),
                                -(int) Math.round(ratio * scale_y));
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ArrayView.class.getName()).log(
                    Level.SEVERE, "paintArrayView", e);
            JOptionPane.showMessageDialog(this, "Error visualizing data");
        }

    }

    public void paintColorScale(BufferedImage img) {
        ArrayViewBase.paintColorScale(img,
                parent != null ? parent.isColorScaleRedGreen() : true, false);
    }
    boolean showDetailFrame = false;
    int indexPointedFeature = -1;

    void setShowDetailFrame(boolean b) {
        this.showDetailFrame = b;
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

    private void initMenues() {
        this.popup = new JPopupMenu();
        this.menuShowData = new JMenuItem("show Data");
        this.menuShowData.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {

                        long pos = mapPosition(parent.getMousePos());
                        showDetailData(pos);
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

    void showDetailData(long pos) {
        Long dist = (long) Math.floor(2 * chromtab.scale_x);

        int ia = PlotLib.getIDataAtPos(arrayStart, arrayStop, pos, dist);
        if (parent != null) {
            if (ia >= 0) {
                parent.setDetailPos(
                        arrayName.get(ia) + " " + ArrayView.myFormatter.format(arrayRatio.get(ia)) + " ",
                        ArrayView.this.chromtab.chrom,
                        arrayStart.get(ia),
                        arrayStop.get(ia));
            } else {
                parent.setDetailPos("", "", 0, 0);
            }
        }
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            System.out.println("show popup");
            popup.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }

    /**
     * ActionListener
     */
    class Zoom implements MouseListener, MouseMotionListener {

        public void mouseExited(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {

            parent.setMousePos(e.getPoint().x);
            long pos = mapPosition(e.getPoint().x);
            parent.setPosition(ArrayView.this.chromtab.chrom, pos);
            if (!parent.isShowRuler() && !parent.isShowData()) {
                return;
            }
            if (parent.isShowData()) {
                showDetailData(pos);
            }
        }

        public void mouseClicked(MouseEvent e) {
            firstPixY = e.getPoint().y;
            maybeShowPopup(e);
        /*Logger.getLogger(ArrayView.class.getName()).log(
        Level.INFO, "Mouse clicked");*/
        }
// mouse event handling methods

        public void mousePressed(MouseEvent e) {

            firstPixX = secondPixX = e.getPoint().x;
            /* Logger.getLogger(ArrayView.class.getName()).log(
            Level.INFO, "Mouse pressed " + secondPixX); */
            maybeShowPopup(e);
            setShowDetailFrame(true);
            repaint();
        }

        public void mouseDragged(MouseEvent e) {
            //System.out.println("Mouse dragged");
            secondPixX = e.getPoint().x;
            if (showDetailFrame) {
                repaint();
            }
        }

        public void mouseReleased(MouseEvent e) {

            secondPixX = e.getPoint().x;
            maybeShowPopup(e);
            if (secondPixX - firstPixX < 1) {
                showDetailFrame = false;
                return;
            }
            /* Logger.getLogger(ArrayView.class.getName()).log(
            Level.INFO, "Mouse released " + secondPixX); */
            //int width = first - second;


            Long _firstPos = mapPosition(firstPixX);
            Long _secondPos = mapPosition(secondPixX);
            Logger.getLogger(ArrayView.class.getName()).log(Level.INFO,
                    "Detail: begin: " + _firstPos + " end: " + _secondPos);



            parent.showDetails(_firstPos, _secondPos, ArrayView.this.chromtab);
        //getSubView(firstPos, secondPos);



        }
    }

    public void addParent(ArrayFrame al) {
        this.parent = al;

    }

    public void removeParent(ArrayFrame al) {
        this.parent = null;
    }
    ArrayFrame parent = null;
}	