package org.molgen.genomeCATPro.guimodul;

/**
 * @name BasicFrame
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
import org.molgen.genomeCATPro.guimodul.cghpro.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import org.molgen.genomeCATPro.annotation.AnnotationManager;
import org.molgen.genomeCATPro.annotation.AnnotationManagerImpl;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionImpl;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.AnnotationList;
import org.molgen.genomeCATPro.guimodul.anno.PlotAnnotation;
import org.molgen.genomeCATPro.guimodul.data.GlobalPositionDataPanel;
import org.molgen.genomeCATPro.guimodul.data.ShowDataAction;
import org.molgen.genomeCATPro.guimodul.data.ShowRulerAction;
import org.molgen.genomeCATPro.guimodul.data.WebPositionPanel;
import org.molgen.genomeCATPro.guimodul.data.ZoomYAction;

/**
 *
 * 051012   kt  add all chrom view
 * 051012   kt  update/refresh thread wait changed
 */
public abstract class BasicFrame extends JPanel {

    public static final String PROP_CHANGE_ANNO = "AddAnno";
    public final static String ALL_CHROMS = "ALL";
    public String chromId = "chr1";
    public String release = null;
    // length for each chrom (max end - min start)    
    public Hashtable<String, Long> chromLength;
    // 
    protected Hashtable<Integer, AnnotationList> listAnno;
    protected Hashtable<Integer, PlotPanel> listAnnoLabels;
    protected Vector<String> listAnnotations = new Vector<String>();
    protected PropertyChangeSupport pss;
    //
    private int offX = 10;
    private int offY = 20;
    private int plotAnnoGap = 0;
    private int plotAnnoWidth = 0;
    private int plotPanelHeight = 0;
    private int plotPanelWidth = 0;
    private Integer zoomY = 0;
    private int _noAnno = -1;
    private int mouseAccuracy = 3;
    private long mousePos = 0;
    private boolean showdata = ShowDataAction.getState();
    private boolean showruler = ShowRulerAction.getState();
    // keep track of current position
    private Long currPos = new Long(0);
    private Region r = null;

    public BasicFrame() {
        //this.release = Defaults.GenomeRelease.hg19.toString();
        this.pss = new PropertyChangeSupport(this);

    }

    /**
     * triggered by region selection by mouse
     */
    public void getPositionToHistory() {
        Region rr = WebPositionPanel.getActPosition();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "addPositionToHistory: " + (rr != null ? rr.toString() : "--"));

        this.getCBHistory().setModel(new DefaultComboBoxModel(
                WebPositionPanel.getPositionList()));
        this.getCBHistory().setSelectedItem(rr);
    }
    boolean changePosition = false;

    @SuppressWarnings("empty-statement")
    /**
     * triggered by user selected item change
     */
    public void setPositionFromHistory() {
        if (changePosition) {
            return;
        }
        Object rr = this.getCBHistory().getSelectedItem();
        Region ract = null;
        if (rr == null) {
            return;
        }
        if (rr instanceof String) {
            String str = (String) rr;
            System.out.println("isString: "+ str);

            if (str.matches("chr[1-9XY]{1,2}:[0-9]{1,20}-[0-9]{1,20}")) {
                try {
                    String chrom = str.substring(0, str.indexOf(":"));
                    Long from = Long.parseLong(str.substring(str.indexOf(":") + 1, str.indexOf("-")));
                    Long to = Long.parseLong(str.substring(str.indexOf("-") + 1, str.length()));

                    if ((from >= 0 )&& to > from && to <= getChromLength(chrom)) {
                        ract = new RegionImpl("", chrom, from, to);
                    } else {
                        System.out.println("chr: " + chrom + " from: " + from + " to: " + to + 
                                "("+getChromLength(chrom)+")");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                return;
            }
        } else {
            ract = (Region) rr;
        }
        changePosition = true;
        System.out.println("setPositionFromHistory " + this.changePosition);
        if (!this.chromId.contentEquals(ract.getChrom())) {
            // first set new chrom
            System.out.println(this.getChromRBList().toString());
            for (Enumeration e = this.getChromRBList(); e.hasMoreElements();) {
                AbstractButton d = (AbstractButton) e.nextElement();
                //System.out.println(d.getActionCommand());
                if (d.getActionCommand().contentEquals(ract.getChrom())) {
                    System.out.println("gotcha " + d.getActionCommand());

                    d.doClick();
                    /*try {
                    while (doneSignal == null);
                    doneSignal.await();
                    } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    
                    JOptionPane.showMessageDialog(this,
                    "Error: " +
                    (ex.getMessage() != null ? ex.getMessage() : "undefined"));
                    
                    }*/
                    break;
                }
            }
        }

        System.out.println("setPositionFromHistory " + ract.toString());
        this.setPosition(ract.getChromStart());
        // combobox hat position durch udpateView vergessen
        this.getCBHistory().setSelectedItem(ract);
        this.rescaleView(false, ract.getChrom(), ract.getChromStart(), ract.getChromEnd());
        this.changePosition = false;
        System.out.println("setPositionFromHistory " + this.changePosition);

    }

    public void setRelease(String release) {
        this.release = release;
        this.listAnnotations = AnnotationManagerImpl.listAnnotationsNames(GenomeRelease.toRelease(this.release));
        GlobalPositionDataPanel.setRelease(release);
    }

    public BasicFrame(String _release) {
        this.setRelease(_release);
        this.pss = new PropertyChangeSupport(this);

    }

    public long getChromLength(String chrom) {
        if (this.chromLength != null) {
            return chromLength.get(chrom);
        } else {
            return 0;
        }
    }

    protected void initFrame() {

        this.getPlot().setPreferredSize(
                new Dimension(this.getPlotPanelWidth(), this.getPlotPanelHeight()));
        this.getPlot().setLayout(
                new BoxLayout(this.getPlot(), BoxLayout.X_AXIS));
        this.getPlotAnno().setPreferredSize(
                new Dimension(100, this.getPlotPanelHeight()));
        this.getPlotAnno().setLayout(
                new BoxLayout(this.getPlotAnno(), BoxLayout.X_AXIS));
        this.listAnnoLabels = new Hashtable<Integer, PlotPanel>();
        this.listAnno = new Hashtable<Integer, AnnotationList>();
        this.initMatrix();

        this.getPlot().setBackground(Color.WHITE);
        this.getPlot().add((JLabel) this.getMatrix());

        this.initAnno();

        Dimension d2 = this.getMatrix().getSize();
        this.getPlot().setPreferredSize(new Dimension((int) d2.getWidth(), (int) d2.getHeight()));

        this.getPlotAnno().setPreferredSize(
                new Dimension(this.getPlotAnnoWidth(), this.getPlotPanelHeight()));

        this.getPlotAnno().setBackground(Color.WHITE);
        this.getScrollPanelMatrix().setViewportView(this.getPlot());
        this.getScrollPanelMatrix().getViewport().setBackground(Color.WHITE);
        this.getScrollPanelAnno().setViewportView(this.getPlotAnno());
        this.getScrollPanelAnno().getViewport().setBackground(Color.WHITE);
        //this.jScrollPaneMatrix.setVerticalScrollBar(this.jScrollPaneAnnotation.getVerticalScrollBar());
        this.getScrollPanelMatrix().getVerticalScrollBar().setModel(
                this.getScrollPanelAnno().getVerticalScrollBar().getModel());

        //jScrollPaneMatrix.setViewportView(this.matrixChip);
        //this.jScrollPaneMatrix.getViewport().setBackground(Color.BLACK);
        this.getPositionToHistory();

        this.updateMatrixView();
    }

    public int get_noAnno() {
        return _noAnno;
    }

    public void set_noAnno(int _noAnno) {
        this._noAnno = _noAnno;
    }

    public int getZoomY() {
        return zoomY.intValue();
    }

    public void setZoomY(Integer zoomY) {
        this.zoomY = zoomY;
    }

    protected void addAnnotation(String anno) {
        if (this.listAnnoLabels.containsKey(anno)) {
            return;
        }
        AnnotationManager am = new AnnotationManagerImpl(GenomeRelease.toRelease(this.release), anno);
        this.addAnnotation(am);
    }
    /*
     * create Matrix
     **/

    abstract public void initMatrix();

    /**
     * create  initial Annotation
     */
    abstract public void initAnno();

    abstract public JPanel getPlotAnno();

    abstract public JPanel getPlot();

    abstract public JScrollPane getScrollPanelAnno();

    abstract public JComboBox getCBHistory();

    abstract public PlotPanel getMatrix();

    abstract public JScrollPane getScrollPanelMatrix();

    abstract public JRadioButton getCBFullChrom();
    abstract public JRadioButton  getCBRegion();

    abstract public void propertyChange(PropertyChangeEvent evt);

    abstract public int getRulerHeight();

    abstract public Enumeration<javax.swing.AbstractButton> getChromRBList();

    void addAnnotation(AnnotationManager am) {
        PlotAnnotation amPlot = new PlotAnnotation(this, am, ++this._noAnno);
        amPlot.setOpaque(true);
        amPlot.setBackground(Color.white);
        amPlot.updatePlot(this.chromId);
        this.listAnnoLabels.put(this._noAnno, amPlot);
        this.listAnno.put(this._noAnno, am.getAnnotation());

        this.getPlotAnno().add(amPlot);
        this.getPlotAnno().setPreferredSize(new Dimension((this.getPlotAnnoWidth() + this.getPlotAnnoGap()) * this.listAnnoLabels.size(), this.getPlotPanelHeight()));
        this.getPlotAnno().revalidate();
        this.getScrollPanelAnno().revalidate();
        this.pss.firePropertyChange(BasicFrame.PROP_CHANGE_ANNO, null, null);
        this.updateMatrixView();
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pss.addPropertyChangeListener(listener);
    }

    protected void displayChromPositions(boolean wholeGenome, Region r) {

        if (!wholeGenome && r != null) {

            WebPositionPanel.setActPosition(r);


        } else {
            this.getCBHistory().setSelectedItem(null);
        }
        setFullChrom(wholeGenome);
    }

    /**
     * change view to cutout
     * @param firstPos
     * @param secondPos
     */
    protected void rescaleView(boolean fullChrom, String chromId, long firstPos, long secondPos) {
        // alle panels benachrichtigen

        this.displayChromPositions(fullChrom, new RegionImpl("", this.chromId, firstPos, secondPos));


        this.getMatrix().setFirstPos(firstPos);
        this.getMatrix().setSecondPos(secondPos);
        this.getMatrix().rescale();

        for (final PlotPanel anno : this.listAnnoLabels.values()) {
            anno.setFirstPos(firstPos);
            anno.setSecondPos(secondPos);
            anno.rescale();
        }

        this.refreshMatrixView();

    // start stop berücksichtigen
    // neuen scale Factor berücksichtigen
    // imagegröße neu berechnen
    // this.showDetailFrame =false;
    }

    void doZoom(int zoom, boolean y) {
        double zoomFactor = java.lang.Math.pow(2, zoom);
        this.zoomY += zoom;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Zoom: " + zoom);
        if (y) {
            this.getMatrix().setYZoom(zoomFactor);
            for (PlotPanel anno : this.listAnnoLabels.values()) {
                anno.setYZoom(zoomFactor);
            }
        } else {
            this.getMatrix().setXZoom(zoomFactor);
        }

        this.refreshMatrixView();
        Dimension d2 = this.getMatrix().getSize();
        this.getPlot().setPreferredSize(new Dimension((int) d2.getWidth(), (int) d2.getHeight()));
        this.getPlotAnno().setPreferredSize(new Dimension((int) this.getPlotAnno().getPreferredSize().getWidth(), (int) d2.getHeight()));


        this.getPlot().revalidate();
        this.getPlotAnno().revalidate();
        final Point newViewPos = new Point(0, this.getMousePos().intValue() - (int) (this.getPlotPanelHeight() * 0.5));
        if (newViewPos.getY() < 0) {
            newViewPos.setLocation(new Point(0, 0));
        }

        this.getScrollPanelMatrix().getViewport().setViewPosition(newViewPos);
        //System.out.println("set view position: " + newViewPos.getY());

        this.getScrollPanelMatrix().getVerticalScrollBar().setModel(this.getScrollPanelAnno().getVerticalScrollBar().getModel());

        revalidate();
    }

    public boolean doZoomX(int d) {
        return false;
    }

    public boolean doZoomY(int d) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.doZoom(d, true);
        this.setCursor(null);
        return true;
    }
    /*
    public List<AnnotationListOrdered> getListAnno() {
    List<AnnotationListOrdered> list = new Vector<AnnotationListOrdered>();
    for (Integer no : this.listAnno.keySet()) {
    list.add(new AnnotationListOrdered(this.listAnno.get(no), no));
    }
    
    return list;
    }
     */

    public int getMouseAccuracy() {
        return this.mouseAccuracy;
    }

    public Long getMousePos() {
        return this.mousePos;
    }

    public int getOffX() {
        return this.offX;
    }

    public int getOffY() {
        return this.offY;
    }

    public int getPlotAnnoGap() {
        if (this.plotAnnoGap == 0) {
            this.plotAnnoGap = (int) (this.getPlotAnnoWidth());
        }
        return this.plotAnnoGap;
    }

    public int getPlotAnnoWidth() {
        if (this.plotAnnoWidth == 0) {
            this.plotAnnoWidth = 30;
        }
        return this.plotAnnoWidth;
    }

    public int getPlotPanelHeight() {
        if (this.plotPanelHeight == 0) {
            Dimension dim = getToolkit().getScreenSize();
            this.plotPanelHeight = 600;
        }
        return this.plotPanelHeight;
    }

    public int getPlotPanelWidth() {
        if (this.plotPanelWidth == 0) {
            this.plotPanelWidth = (int) (this.getPreferredSize().width * 0.5);
        }
        return this.plotPanelWidth;
    }

    public String getRelease() {
        return release;
    }

    public int getZoomX() {
        return this.zoomY;
    }

    public Hashtable<Integer, AnnotationList> getListAnnotation() {
        return listAnno;
    }

    public boolean isFullChrom() {
        return this.getCBFullChrom().isSelected();
    }

    public boolean isShowruler() {
        return showruler;
    }

    public void load() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "not implemented yet");
    }

    public void removeAnnotation(PlotAnnotation amPlot) {

        this.listAnnoLabels.remove(amPlot.getNo());
        this.listAnno.remove(amPlot.getNo());

        this.getPlotAnno().remove(amPlot);
        this.updateAnnotation();
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pss.removePropertyChangeListener(listener);
    }

    public void showDetailData(Region curr) {
        r = curr;
        GlobalPositionDataPanel.setDetail(r);
    }

    public void setFullChrom(boolean check) {
        this.getCBFullChrom().setSelected(check);
        this.getCBRegion().setSelected(!check);
    }

    public void setListAnno(List<AnnotationList> l) {
    }

    public void setMouseAccuracy(int x) {
        this.mouseAccuracy = x;
    }

    public void setMousePos(long currPos) {
        this.mousePos = currPos;
    }

    public void setOffX(int offX) {
        this.offX = offX;
    }

    public void setOffY(int offY) {
        this.offY = offY;
    }

    public void setPlotAnnoGap(int g) {
        boolean update = this.plotAnnoGap != g;
        this.plotAnnoGap = g;
        if (update) {
            this.updateMatrixView();
            this.updateAnnotation();
        }
    }

    public void setPlotAnnoWidth(int w) {
        boolean update = this.plotAnnoWidth != w;
        this.plotAnnoWidth = w;
        if (update) {
            this.updateMatrixView();
            this.updateAnnotation();
        }
    }

    public void setPlotPanelHeight(int h) {
        boolean update = this.plotPanelHeight != h;
        this.plotPanelHeight = h;
        if (update) {
            this.updateMatrixView();
            this.updateAnnotation();
        }
    }

    public void setPlotPanelWidth(int w) {
        boolean update = this.plotPanelWidth != w;
        this.plotPanelWidth = w;
        if (update) {
            this.updateMatrixView();
        }
    }

    public void setPosition(long pos) {
        this.currPos = pos;
        GlobalPositionDataPanel.setCurrPosition(this.chromId + ": " + this.currPos.toString());
    }

    public void showData(boolean show) {
        this.showdata = show;
    }

    public boolean showDetailData() {
        return this.showdata;
    }

    /**
     * change view to cut-out
     * @param firstPos
     * @param secondPos
     */
    public void showDetails(long firstPos, long secondPos) {
        int n = JOptionPane.showConfirmDialog(this,
                "would you like to zoom in to region?  \n" + Long.toString(firstPos) + "-" + Long.toString(secondPos), "Show Details", JOptionPane.YES_NO_OPTION);
        this.getMatrix().setShowDetailFrame(false);
        if (n == JOptionPane.YES_OPTION) {
            this.rescaleView(false, this.chromId, firstPos, secondPos);
            this.getPositionToHistory();
        } else {
            this.refreshMatrixView();
        }
    }

    public void showRuler(boolean show) {
        this.showruler = show;
    }

    protected void updateAnnotation() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        List<PlotAnnotation> list = new Vector(this.listAnnoLabels.values());
        Collections.sort((List<PlotAnnotation>) list, new Comparator() {

            public int compare(Object o1, Object o2) {
                Integer no1 = ((PlotAnnotation) o1).getNo();
                Integer no2 = ((PlotAnnotation) o2).getNo();
                return no1.compareTo(no2);
            }
        });
        this.listAnnoLabels.clear();
        this.listAnno.clear();
        this._noAnno = -1;
        for (PlotAnnotation aplot : (List<PlotAnnotation>) list) {
            this.getPlotAnno().remove((Component) aplot);
            this.addAnnotation(aplot.getManager());
        }


        this.getPlotAnno().revalidate();
        this.getScrollPanelAnno().revalidate();
        this.pss.firePropertyChange(CGHPROFrame.PROP_CHANGE_ANNO, null, null);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /* public class AnnotationListOrdered extends AnnotationList {
    
    private int no = 0;
    
    AnnotationListOrdered(AnnotationList anno, Integer no) {
    super(anno);
    this.no = no;
    
    }
    
    public int getNo() {
    return no;
    }
    
    public void setNo(int no) {
    this.no = no;
    }
    }*/
    /**
     * scale image for all chrom view
     * @param _src
     * @param scaleX
     * @param scaleY
     * @return
     * @throws java.io.IOException
     */
    public void setAllChromsMatrix(java.awt.event.ActionEvent evt) {

        AllChromViewDialog allview = new AllChromViewDialog(null, false);
        JPanel chromview = allview.getJPanelChromView();

        for (int i = chromview.getComponentCount() - 1; i >= 0; i--) {
            chromview.remove(i);
        }
        String _chromId = this.chromId;
        try {
            for (Enumeration e = this.getChromRBList(); e.hasMoreElements();) {
                AbstractButton d = (AbstractButton) e.nextElement();
                if (d.getActionCommand().contentEquals(BasicFrame.ALL_CHROMS)) {
                    continue;
                }
                this.chromId = d.getActionCommand();
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "allview " + chromId);
                this.setPosition(0);
                this.updateMatrixView();
                JLabel l = new JLabel();



                BufferedImage img1 = this.getMatrix().getImage();

                BufferedImage img2 = CGHPROFrame.scale(img1, 0.7, 0.5);

                l.setIcon(new ImageIcon(img2));
                chromview.add(l);
            }
        } catch (Exception e) {
            //  JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
        }
        this.chromId = _chromId;
        this.setPosition(0);
        //System.out.println("setRBMatrixAberration: " + this.chromId);
        this.updateMatrixView();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        allview.setSize(dim.width, dim.height);

        allview.setVisible(true);
    }

    public static BufferedImage scale(BufferedImage _src, double scaleX, double scaleY)
            throws IOException {


        int newW = (int) (_src.getWidth() * scaleX);
        int newH = (int) (_src.getHeight() * scaleY);
        BufferedImage bdest =
                new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bdest.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(_src, 0, 0, newW, newH, null);
        //Image src = _src.getScaledInstance((int) _width, (int) _height, Image.SCALE_SMOOTH);


        /*AffineTransform at =
        AffineTransform.getScaleInstance((double) width / src.getWidth(),
        (double) height / src.getHeight());*/
        //g.drawRenderedImage(src, at);

        g2.dispose();
        return bdest;
    }

    public void updateImages() {
        for (PlotPanel anno : this.listAnnoLabels.values()) {
            anno.refresh();
        }

        this.getScrollPanelMatrix().repaint();
    }

    /**
     * refresh view, nothing in display (area etc) changes
     */
    public void refreshMatrixView() {
        try {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));

            //int n = this.listAnnoLabels.values().size() + 1;
            Thread t = new Thread(new WorkerRefresh(this.getMatrix()));
            t.start();
            Thread[] tlist = new Thread[this.listAnnoLabels.values().size()];
            int ii = 0;
            for (final PlotPanel anno : this.listAnnoLabels.values()) {
                tlist[ii] = new Thread(new WorkerRefresh(anno));
                tlist[ii].start();
                ii++;

            }
            t.join(0);
            for (Thread _t : tlist) {
                if (_t != null) {
                    _t.join(0);
                }
            }
            // wait for all to end
            revalidate();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            doneSignal = null;
        } catch (Exception e) {
            e.printStackTrace();

            //System.out.println("updateMatrixView error pop up");
            JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));
            System.gc();
        }
    }
    CountDownLatch startSignal = null;
    CountDownLatch doneSignal = null;

    /**
     * reset matrix, zoom out i.e., change chromosome
     */
    public void updateMatrixView() {


        try {
            //this.displayChromPositions(true, 0, zoomY);
            if (chromId != null) {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                int n = this.listAnnoLabels.values().size() + 1;
                //is a start signal that prevents any worker 
                //from proceeding until the driver is ready for them to proceed
                //startSignal = new CountDownLatch(1);
                //is a completion signal that allows the driver to wait until 
                //all workers have completed
                //doneSignal = new CountDownLatch(n);

                Thread t = new Thread(new WorkerUpdate(this.getMatrix()));
                t.start();

                //new Thread(new WorkerUpdate(this.getMatrix(), startSignal, doneSignal)).start();

                Thread[] tlist = new Thread[this.listAnnoLabels.values().size()];
                int ii = 0;
                for (final PlotPanel anno : this.listAnnoLabels.values()) {
                    tlist[ii] = new Thread(new WorkerUpdate(anno));
                    tlist[ii].start();
                    ii++;

                }
                t.join(0);
                for (Thread _t : tlist) {
                    if (_t != null) {
                        _t.join(0);
                    }
                }

                //startSignal.countDown(); //start

                //doneSignal.await();     // wait for all to end

                //super.zoomX = 0;
                this.setZoomY(0);
                ZoomYAction.getInstance().setFactor(getZoomY());
                //jSliderZoomY.setValue(0);
                this.getPlot().setPreferredSize(new Dimension(getPlotPanelWidth(), getPlotPanelHeight()));
                /*for (Component c : this.jPanelPlotAnno.getComponents()) {
                c.setPreferredSize(
                new Dimension(this.getPlotAnnoGap() + this.getPlotAnnoWidth(),
                this.getPlotPanelHeight()));
                }*/
                this.getPlotAnno().setPreferredSize(
                        new Dimension(
                        (int) this.getPlotAnno().getPreferredSize().getWidth(), this.getPlotPanelHeight()));
                //this.jPanelPlotAnno.revalidate();
                //this.jScrollPaneAnnotation.revalidate();
                this.displayChromPositions(true, null);
                final Point newViewPos = new Point(this.getPlotPanelWidth() / 2,
                        (int) (this.getPlotPanelHeight() * 0.5));
                this.getScrollPanelMatrix().getViewport().setViewPosition(newViewPos);

                this.getScrollPanelMatrix().getVerticalScrollBar().setModel(
                        this.getScrollPanelAnno().getVerticalScrollBar().getModel());

                //this.updateImages();

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            //System.out.println("End UpdateMatrixView");
            }

        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("updateMatrixView error pop up");
            JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));

            System.gc();
        }

    }

    class WorkerUpdate implements Runnable {

        private PlotPanel pane = null;
        //private final CountDownLatch startSignal;
        //private final CountDownLatch doneSignal;

        WorkerUpdate(PlotPanel panel) {
            this.pane = panel;
        // this.startSignal = startSignal;
        // this.doneSignal = doneSignal;
        }

        public void run() {
            try {
                //startSignal.await();

                pane.updatePlot(chromId);


            //  doneSignal.countDown();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        "", ex);
            } // return;

        }
    }

    class WorkerRefresh implements Runnable {

        private final PlotPanel pane;
        //private final CountDownLatch startSignal;
        // private final CountDownLatch doneSignal;

        WorkerRefresh(PlotPanel panel) {
            this.pane = panel;
        //this.startSignal = startSignal;
        //this.doneSignal = doneSignal;
        }

        public void run() {
            try {
                //startSignal.await();
                pane.refresh();
            //doneSignal.countDown();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        "", ex);
            } // return;

        }
    }
}
