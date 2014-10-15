/**
 * @name ArrayFrame
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * This file is part of the GenomeCAT software package.
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
package org.molgen.genomeCATPro.cat.maparr;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;


import org.molgen.genomeCATPro.annotation.AnnotationManagerImpl;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionImpl;
import org.molgen.genomeCATPro.cat.CATPropertiesMod;
import org.molgen.genomeCATPro.cat.MapCATDataAction;
import org.molgen.genomeCATPro.cat.util.*;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
import org.molgen.genomeCATPro.guimodul.data.AppInterface;
import org.molgen.genomeCATPro.guimodul.data.GlobalPositionDataPanel;
import org.molgen.genomeCATPro.guimodul.data.ShowDataAction;
import org.molgen.genomeCATPro.guimodul.data.ShowRulerAction;
import org.molgen.genomeCATPro.guimodul.data.WebPositionPanel;
import org.openide.util.NbPreferences;

/**
 * 200213   kt  modify chrom/region from history (bug)
 * 290612   kt  add  getChromRBList() + getCBHistory();
 * 190912   kt  initMyComponents scaleArrays()  
 * 210912   kt  bug scale at view change
 * 210912   kt  bug open map set position
 * 280912   kt  add addAnno
 * 
 */
public class ArrayFrame extends JPanel implements AppInterface {

    protected Vector<String> listAnnotations = new Vector<String>();
    public Hashtable<String, ArrayFrame> frames = new Hashtable<String, ArrayFrame>();
// panel for each array legend
    public LegendPanel arrayPanel;
    Vector<ChromTab> vChromTabs = new Vector<ChromTab>();
// displayed arrays
    Hashtable<Long, ArrayData> arrays = new Hashtable<Long, ArrayData>();
    //Vector<ArrayStats> arraysStats = new Vector<ArrayStats>();
// displayed array legends
    public GenomeRelease release = null;
    JMenuBar topMenu;
    JMenu jMenuView;
    JMenuItem menuViewArrays;
    JMenuItem menuCloseArrays;
    PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    static Color colorScale[] = null;
    
    
    

    /** Creates new form NewJFrame */
    public ArrayFrame(Data[] list, GenomeRelease release) {
        super();

        Logger.getLogger(ArrayFrame.class.getName()).log(
                Level.INFO, "create ArrayFrame for " + release.toShortString());

        //this.createMenu();
        initComponents();
        //this.jPanelMenue.add(this.topMenu);

        this.setRelease(release);
        
        this.vChromTabs = ChromTab.createChromTabs(this);
        initMyComponents();
        this.addData(list);
        this.scaleArrays();//210912   kt  bug scale at view change

        setVisible(true);


    }

    /** Creates new form NewJFrame */
    public ArrayFrame(List<ArrayData> list, GenomeRelease release) {
        super();

        Logger.getLogger(ArrayFrame.class.getName()).log(
                Level.INFO, "create ArrayFrame for " + release.toShortString());
        //this.createMenu();
        this.setRelease(release);
        this.listAnnotations = AnnotationManagerImpl.listAnnotationsNames(
                this.release);
        initComponents();
        //this.jPanelMenue.add(this.topMenu);


        this.vChromTabs = ChromTab.createChromTabs(this);
        initMyComponents();
        addArrays(list);

        setVisible(true);
        this.scaleArrays();//210912   kt  bug scale at view change

    }

    public ArrayFrame() {
        super();
        Logger.getLogger(ArrayFrame.class.getName()).log(
                Level.INFO, "create ArrayFrame default");
        //this.vChromTabs = ChromTab.createChromTabs(this);

        //this.createMenu();
        initComponents();
        // this.jPanelMenue.add(this.topMenu);
        initMyComponents();

        setVisible(true);

    }

    public void closeView() {
    }

    public List<Data> getDataList() {
        List<Data> list = new Vector<Data>();
        for (ArrayData ad : this.arrays.values()) {

            if (!(ad instanceof ArrayDataAnno) && !list.contains(ad.getData())) {
                list.add(ad.getData());
            }

        }
        return list;
    }

    public GenomeRelease getRelease() {
        return release;
    }

    public int getZoomY() {
        return 0;
    }

    public void setRelease(GenomeRelease release) {
        this.release = release;
        this.listAnnotations = AnnotationManagerImpl.listAnnotationsNames(
                this.release);
        this.jComboBoxAnno.setModel(new javax.swing.DefaultComboBoxModel(
                this.listAnnotations));

    //this.jLabelRelease.setText(this.release != null ? this.release.toString() : "");

    }

    public void setScaleFactor(double scaleFactor) {
        CATPropertiesMod.props().setScaleFactor(scaleFactor);
        this.scaleArrays();
    }

    public boolean isGlobalScale() {
        return CATPropertiesMod.props().isGlobalScale();
    }

    public void setGlobalScale(boolean globalScale) {
        CATPropertiesMod.props().setGlobalScale(globalScale);
        this.scaleArrays();
    }

    public void scaleArrays() {
        if (this.isGlobalScale()){
            this.scaleArrayViewInterFrame(CATPropertiesMod.props().getScaleFactor());
        } else {
            reScaleArrayViewInterChrom();
        }

        repaint();
        if (this.arrayPanel != null) {//210912   kt  bug scale at view change
            //070613 test
            this.arrayPanel.setUserScaling(!this.isGlobalScale());
        }
        repaint();

    }

    protected void initMyComponents() {

        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                tabbedPanePropertyChange(e);
            }
        });
        this.jPanelRuler.setPreferredSize(new Dimension(Defines.ARRAY_WIDTH, Defines.ARRAY_HEIGTH / 20));
        sPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.jSplitPane.setDividerSize(1);
        this.jSplitPane.setOneTouchExpandable(true);
        jSplitPane.setDividerLocation(Defines.ARRAY_WIDTH);
        jSplitPane.setDividerSize(1);

        panelChroms.setPreferredSize(new Dimension(Defines.ARRAY_WIDTH, Defines.ARRAY_HEIGTH));

        /*
        ImageIcon back = new ImageIcon(new String(Defines.RPrefix + "genomeCAT.jpg"));
        
        c.gridwidth = 2;
        background = new JLabel(back);
        cp.add(background, c);
         */

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);




        // attach chromtabs to parent panel
        // create tabs at tabbed pane

        this.initChromPane();

        legendPanel.setLayout(new GridLayout(1, 2));
        this.arrayPanel = new LegendPanel(this);
        this.arrayPanel.setLayout(new GridLayout(0, 1, 10, 10));
        legendPanel.add(arrayPanel);

        this.getPositionToHistory();
    //this.scaleArrays();



    }

    void initChromPane() {
        for (int i = 0; i < vChromTabs.size(); i++) {
            this.panelChroms.add(vChromTabs.get(i), vChromTabs.get(i).chrom);
            this.jPanelRuler.add(vChromTabs.get(i).getRuler(Defines.ARRAY_HEIGTH / 20, this), vChromTabs.get(i).chrom);

            //setPreferredSize(new Dimension(0, 0));
            this.tabbedPane.add(vChromTabs.get(i).chrom, null);
        }
    }

    void createMenu() {
        //this.jPopupMenuData = new JPopupMenu();

        this.topMenu = new JMenuBar();
        this.jMenuView = new javax.swing.JMenu();
        this.jMenuView.setPreferredSize(new Dimension(100, 10));
        this.jMenuView.setText("view");
        this.jMenuView.setName("jMenuView");

        menuViewArrays = new JMenuItem("Add Array");
        menuViewArrays.setName("menuViewArrays");
        menuViewArrays.setText("add Data");
        menuViewArrays.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        jMenuView.add(menuViewArrays);


        menuCloseArrays = new JMenuItem("Close Array");
        menuCloseArrays.addActionListener(new CloseArray());
        menuCloseArrays.setText("close");
        menuCloseArrays.setName("menuCloseArrays");
        jMenuView.add(menuCloseArrays);

        this.topMenu.add(jMenuView);

    /*
    this.jButtonMenueData = DropDownButtonFactory.createDropDownButton(
    new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)),
    this.jPopupMenuData);
    this.jButtonMenueData.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    jButtonMenueData.setIcon(
    new javax.swing.ImageIcon(getClass().getResource(
    "/org/molgen/genomeCATPro/cat/maparr/genomeCATLogo.jpg")));
     */
    /*
    JMenu menuImport = new JMenu("Import");
    menuImport.getAccessibleContext().setAccessibleDescription("Import");
    menuBar.add(menuImport);
     */




    /*JMenuItem menuImportNew = new JMenuItem("new Import FE");
    menuImportNew.addActionListener(new ImportNewFE());
    
    menuImport.add(menuImportNew);
     */
    /*
    JMenu menuMap = new JMenu("Mapping");
    menuBar.add(menuMap);
    
    JMenuItem menuMapArrays = new JMenuItem("map arrays");
    //update menuMapArrays.addActionListener(new MapFilterArrays());
    
    menuMap.add(menuMapArrays);
    
    JMenuItem menuMapOpen = new JMenuItem("map open arrays");
    //update menuMapOpen.addActionListener(new MapArrays());
     */
    //menuMap.add(menuMapOpen);
        /*
    JMenuItem menuExportMap = new JMenuItem("Export Mapping");
    menuExportMap.addActionListener(new ExportMap());
    menuMap.add(menuExportMap);
     */
    /*
    JMenu menuExport = new JMenu("Export");
    menuBar.add(menuExport);
    
    JMenuItem menuExportImage = new JMenuItem("export Image");
    menuExportImage.addActionListener(new ActionListener() {
    
    public void actionPerformed(ActionEvent e) {
    ArrayFrame.this.exportImage();
    }
    });
    
    menuExport.add(menuExportImage);
    
    JMenu menuCalc = new JMenu("Calculate");
    menuBar.add(menuCalc);
    
    
    
     */


    }

    public void addData(Data[] list) {
        try {
            if (list == null || list.length == 0) {
                return;

            }
            setRelease((Defaults.GenomeRelease.toRelease(list[0].getGenomeRelease())));

            ArrayData[] adList = ChooseArrayViewDialog.getArrayDataList(list);
            ArrayFrame.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            for (ArrayData ad : adList) {
                ArrayFrame.this.addArray(ad);
            }
        } catch (Exception ex) {
            Logger.getLogger(ArrayFrame.class.getName()).log(Level.SEVERE,
                    "AddData:", ex);

        } finally {
            ArrayFrame.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void addAnno(String release, String name) {
        try {

            setRelease((Defaults.GenomeRelease.toRelease(release)));

            ArrayData ad = ArrayAnnoView.getArrayDataAnno(release, name);
            ArrayFrame.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            ArrayFrame.this.addArray(ad);

        } catch (Exception ex) {
            Logger.getLogger(ArrayFrame.class.getName()).log(Level.SEVERE,
                    "AddAnno:", ex);

        } finally {
            ArrayFrame.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void load() {
        try {
            Data[] list = FilterExperimentsDialog.getDataList(ArrayFrame.this.release);
            // check wich view for each clazz is available
            // lets user choose which view to load
            //
            addData(list);

        } catch (Exception ex) {
            Logger.getLogger(ArrayFrame.class.getName()).log(Level.SEVERE,
                    "load:", ex);

        } finally {
            //ArrayFrame.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
    }
    long mousePosition = 0;

    void setMousePos(long x) {
        this.mousePosition = x;
    }

    public long getMousePos() {
        return mousePosition;
    }

    class CloseArray implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Set<Long> arrayIds = arrays.keySet();
            Long[] sArrayList = arrayIds.toArray(new Long[0]);
            Long arrayId = (Long) JOptionPane.showInputDialog(ArrayFrame.this,
                    "Select Array to close :\n", "Close Array",
                    JOptionPane.PLAIN_MESSAGE, null,
                    sArrayList, "");
            if (arrayId != null) {
                System.out.println(arrayId);
                removeArray(arrayId);
            }
        }
    }

    /*update
    class MapArrays implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
    System.out.println("Map Arrays");
    Vector<Data> arrayList = new Vector();
    
    ArrayData m;
    for (Enumeration a = arrays.elements(); a.hasMoreElements();) {
    m = (ArrayData) a.nextElement();
    arrayList.add(m.getData());
    }
    if (arrayList != null && arrayList.size() > 0) {
    ArrayMapFrame.doMapping(ArrayFrame.this, arrayList);
    }
    
    }
    }
     */
    /*update
    class MapFilterArrays implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
    Data[] list = FilterExperimentsDialog.getDataList(ArrayFrame.this.release);
    //Map<Long, Data> list = FilterExperimentsDialog.getDataList(ArrayFrame.this.release);
    if (list != null && list.length > 0) {
    ArrayMapFrame.doMapping(ArrayFrame.this, list);
    }
    }
    }
     */
    void removeArray(Long arrayId) {
        Logger.getLogger(ArrayFrame.class.getName()).log(Level.INFO,
                "remove array view " + arrayId);
        // remove array legend view
        this.arrayPanel.removeArray(arrayId);
        // remove from list
        arrays.remove(arrayId);
        // remove array data view
        if (this.vChromTabs.size() > 0) {
            ChromTab.removeArrayView(arrayId, this.vChromTabs, this.release);
        }

        repaint();
    }

    public void removeArrayView(Long id) {
        ArrayView[] list = ChromTab.getArrayView(id, vChromTabs, release);
        for (ArrayView v : list) {
            v.removeParent(this);
        }
        ChromTab.removeArrayView(id, this.vChromTabs, this.release);
        arrays.remove(id);

        repaint();
    }

    public void exportImage() {

        ChromTab c = this.getSelectedChromTab();
        if (c == null) {
            return;
        }
        Color oldcolor = c.getBackground();
        c.setBackground(Color.white);
        String tmpname = c.chrom + ".png";
        // Create image
        System.out.println("export Image: " + tmpname);
        final BufferedImage img =
                new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
        c.paint(img.getGraphics());
        String path = NbPreferences.forModule(ArrayFrame.class).get("pathPreference", "");
        JFileChooser fileExportChooser = new javax.swing.JFileChooser(path);
        fileExportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String[] extensions = ImageIO.getWriterFileSuffixes();
        fileExportChooser.setFileFilter(new FileNameExtensionFilter("Image files", extensions));
        fileExportChooser.setSelectedFile(new File(tmpname));
        int br = fileExportChooser.showSaveDialog(this);
        if (br != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File f = fileExportChooser.getSelectedFile();

        try {
            String name = f.getName();
            String suffix = name.substring(name.lastIndexOf('.') + 1);
            if (suffix.compareToIgnoreCase("jpeg") == 0 || suffix.compareToIgnoreCase("jpg") == 0) {
                OutputStream output = new BufferedOutputStream(new FileOutputStream(f));

            }

            ImageIO.write(img, suffix, f);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting image: " +
                    e.getMessage());
        }
        c.setBackground(oldcolor);
        NbPreferences.forModule(ArrayFrame.class).put("pathPreference", f.getPath());
    }

    /**
     * add arrays to the main array frame to be displayed
     * @param arrays List of identifier for arrays
     * @param type Class for each of the arrays
     */
    protected void addArrays(List<ArrayData> list) {
        System.out.println("ArrayFrame addArrays");
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        for (ArrayData d : list) {
            addArray(d);
        }
    }

    /**
     * add new Array
     * @param d 
     */
    void addArray(ArrayData d) {
        Logger.getLogger(ArrayFrame.class.getName()).log(Level.INFO,
                "ArrayFrame addArray " + d.getName() + " [" + d.getArrayClazz() + "]");

        if (this.vChromTabs.size() == 0) {
            this.vChromTabs = ChromTab.createChromTabs(this);
            this.initChromPane();
        }




        try {

            ArrayView[] list = ChromTab.addArray(d, this.vChromTabs, this.release);
            for (ArrayView v : list) {
                v.addParent(this);
            // create array legend view
            }
            this.arrayPanel.addArray(d);

            this.arrays.put(d.getId(), d);
        } catch (Exception e) {
            Logger.getLogger(ArrayFrame.class.getName()).log(
                    Level.SEVERE, "addArray", e);
            JOptionPane.showMessageDialog(this,
                    "Add Array Error", e.getMessage(),
                    JOptionPane.ERROR_MESSAGE);

        }

        repaint();
    }

    public void changePosArrayView(int sourcePos, int destPos) {
        ChromTab.changePosArrayView(sourcePos, destPos, this.vChromTabs, this.release);

        repaint();
        sPane.getViewport().scrollRectToVisible(new Rectangle(new Point(Defines.ARRAY_HEIGTH * destPos, 0)));

    }

    /**
     * 
     * @param id
     */
    public void filterArray(Long id) {
        Double negThreshold = -0.5;
        String sThreshold = (String) JOptionPane.showInputDialog(ArrayFrame.this,
                "include all items with ratio less than:\n",
                "set negative threshold",
                JOptionPane.QUESTION_MESSAGE,
                null, null, negThreshold);
        if (sThreshold == null) {
            return;
        }
        negThreshold = Double.valueOf(sThreshold);
        Double posThreshold = 0.5;
        sThreshold = (String) JOptionPane.showInputDialog(ArrayFrame.this,
                "include all items with ratio greater than:\n",
                "set positive threshold",
                JOptionPane.QUESTION_MESSAGE,
                null, null, posThreshold);
        if (sThreshold == null) {
            return;
        }
        posThreshold = Double.valueOf(sThreshold);
        //String filterArrayId =  + "_filter_" + negThreshold + "-" + posThreshold;
        try {
            ArrayData ad = this.arrays.get(id);
            if (ad instanceof ArrayDataAnno) {
                return;
            }
            ArrayFrame.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            ArrayView[] vList = ChromTab.getArrayView(id, this.vChromTabs, this.release);
            Logger.getLogger(ArrayFrame.class.getName()).log(
                    Level.INFO, "filterArray", ad.getName() + " is " + vList.length);

            ArrayData fm = ArrayData.createArrayData(ad.getArrayClazz(), ad.getData());
            fm.filteredData = true;
            fm.setFilterNeg(negThreshold);
            fm.setFilterPos(posThreshold);
            this.arrays.put(fm.getId(), fm);
            ArrayFrame.this.arrayPanel.addArray(fm);
            fm.nof = 0;
            for (ArrayView v : vList) {
                ArrayView vNeu = this.filterArrayView(v, fm);
                fm.nof += vNeu.getDataSize();
                vNeu.addParent(this);
                v.chromtab.add(vNeu);
            }


        //ArrayStats s = new ArrayStats(0.0, 0.0, posThreshold, negThreshold);





        } catch (Exception ex) {
            Logger.getLogger(ArrayFrame.class.getName()).log(
                    Level.SEVERE, "filterArray", ex);
            JOptionPane.showMessageDialog(ArrayFrame.this, ex.getMessage());
        }

        repaint();
        ArrayFrame.this.setCursor(null);
    }

    ArrayView filterArrayView(ArrayView v, ArrayData d) {
        Vector<Double> filterRatio = new Vector<Double>();
        Vector<Long> filterStart = new Vector<Long>();
        Vector<Long> filterStop = new Vector<Long>();
        Vector<String> filterName = new Vector<String>();

        //filter data
        Double ratio = 0.0;

        for (int i = 0; i < v.arrayRatio.size(); i++) {
            ratio = v.arrayRatio.get(i);
            if ((ratio < 0 && ratio <= d.getFilterNeg()) ||
                    (ratio > 0 && ratio >= d.getFilterPos())) {
                filterRatio.add(new Double(ratio));
                filterName.add(new String(v.arrayName.get(i)));
                filterStart.add(new Long(v.arrayStart.get(i)));
                filterStop.add(new Long(v.arrayStop.get(i)));
            }

        }


        Double minY = 0.0;
        try {
            minY = Collections.min(filterRatio, org.molgen.genomeCATPro.common.Utils.DoubleMinComparator);
        } catch (java.util.NoSuchElementException e) {
            Logger.getLogger(ArrayFrame.class.getName()).log(
                    Level.SEVERE, "ArrayView.filterArray empty ratio vector");
        }

        Double maxY = 0.0;
        try {
            maxY = Collections.max(filterRatio, org.molgen.genomeCATPro.common.Utils.DoubleMaxComparator);
        } catch (java.util.NoSuchElementException e) {
            Logger.getLogger(ArrayFrame.class.getName()).log(
                    Level.SEVERE, "ArrayView.filterArray empty ratio vector");
        }
        ArrayView a = ArrayViewBase.getView(d,
                filterName, filterStart, filterStop, filterRatio, v.chromtab);


        return a;
    }

    /**
     * filter data by thresholds persistent to db
     * @param id
     * @param d
     * @return
     */
    public void filterArrayAtDB(Long id, ArrayData d) {
        if (d instanceof ArrayDataAnno) {
            return;
        }
        String newName = org.molgen.genomeCATPro.common.Utils.getUniquableName(d.getName());


        newName = (String) JOptionPane.showInputDialog(null,
                "enter name for filtered data :\n" +
                "note: must be less than " + Defaults.MAX_TABLE_NAME + " characters ",
                " save filtered data",
                JOptionPane.QUESTION_MESSAGE,
                null, null, newName);
        if (newName != null) {
            Logger.getLogger(ArrayFrame.class.getName()).log(
                    Level.INFO, "save filtered data into db",
                    newName);
        //return arrayFrame.filterArrayAtDB(pos, id, arraylabel.m);

        }
        try {
            Data newData = DataManager.filterData(d.getData(), newName,
                    d.filterNeg, d.filterPos);
            this.arrays.remove(id);
            ArrayData ad = ArrayData.createArrayData(d.getArrayClazz(), newData);
            this.addArray(ad);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filter data: " +
                    e.getMessage());
        }

    }

    public void showThresholds(boolean b, Long id) {
        ArrayData d = this.arrays.get(id);

        ArrayView[] arrayList = ChromTab.getArrayView(id, this.vChromTabs, this.release);

        for (int j = 0; j < arrayList.length; j++) {

            arrayList[j].setThreshold(b, d.getPosThreshold(), d.getNegThreshold());
        }

        repaint();
    }

    /**
     * scale all ArrayViews within the frame moreover for each Chromosome
     * to a fix maximum y value
     * @param fixY
     */
    public void scaleArrayViewInterFrame(double fixY) {

        for (ChromTab c : this.vChromTabs) {
            ArrayView[] arrayList = ChromTab.getArrayViews(c);

            for (int i = 0; i < arrayList.length; i++) {
                arrayList[i].scaleHeightFix(fixY);
            }

        }
    }

    /**
     * reset scale all ArrayViews within one Array, i.e. for each Chromosome
     * optimized by the arraywide maximum y value
     * 
     */
    void reScaleArrayViewInterChrom() {

        for (ChromTab c : this.vChromTabs) {
            ArrayView[] arrayList = ChromTab.getArrayViews(c);
            for (int i = 0; i < arrayList.length; i++) {
                arrayList[i].setRangeY();
            }

        }
    }

    /**
     * scale all ArrayViews within one Array, i.e. for each Chromosome 
     * with a given factor
     * @param pos               Position of Array in frame
     * @param scaleFactor       Factor to scale arrays
     */
    public void scaleArrayViewInterChrom(int pos, double scaleFactor) {
        
        ArrayView[] arrayList = ChromTab.getArrayViewsByPos(pos, this.vChromTabs, this.release);
        for (int i = 0; i < arrayList.length; i++) {
            arrayList[i].scaleHeight(scaleFactor);
        }
        sPane.getViewport().scrollRectToVisible(new Rectangle(new Point(Defines.ARRAY_HEIGTH * pos, 0)));
        repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuData = new javax.swing.JPopupMenu();
        buttonGroupSelectedChrom = new javax.swing.ButtonGroup();
        sPane = new javax.swing.JScrollPane();
        jSplitPane = new javax.swing.JSplitPane();
        panelChroms = new javax.swing.JPanel();
        legendPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jComboBoxHistory = new javax.swing.JComboBox();
        jRadioButtonChrom = new javax.swing.JRadioButton();
        jRadioButtonRegion = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxAnno = new javax.swing.JComboBox();
        jPanelRuler = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();

        jSplitPane.setDividerLocation(900);
        jSplitPane.setDividerSize(1);
        jSplitPane.setLastDividerLocation(900);

        panelChroms.setMinimumSize(new Dimension(org.molgen.genomeCATPro.cat.util.Defines.ARRAY_WIDTH, (int) ( org.molgen.genomeCATPro.cat.util.Defines.ARRAY_HEIGTH*0.5) ));
        panelChroms.setPreferredSize(new Dimension(org.molgen.genomeCATPro.cat.util.Defines.ARRAY_WIDTH, org.molgen.genomeCATPro.cat.util.Defines.ARRAY_HEIGTH));
        panelChroms.setLayout(new java.awt.CardLayout());
        jSplitPane.setLeftComponent(panelChroms);

        javax.swing.GroupLayout legendPanelLayout = new javax.swing.GroupLayout(legendPanel);
        legendPanel.setLayout(legendPanelLayout);
        legendPanelLayout.setHorizontalGroup(
            legendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 115, Short.MAX_VALUE)
        );
        legendPanelLayout.setVerticalGroup(
            legendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 703, Short.MAX_VALUE)
        );

        jSplitPane.setRightComponent(legendPanel);

        sPane.setViewportView(jSplitPane);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("detail history"));

        jComboBoxHistory.setEditable(true);
        jComboBoxHistory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        buttonGroupSelectedChrom.add(jRadioButtonChrom);
        jRadioButtonChrom.setSelected(true);
        jRadioButtonChrom.setText("full chrom");
        jRadioButtonChrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonChromActionPerformed(evt);
            }
        });

        buttonGroupSelectedChrom.add(jRadioButtonRegion);
        jRadioButtonRegion.setText("detail");
        jRadioButtonRegion.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jRadioButtonChrom, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButtonRegion))
                    .addComponent(jComboBoxHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jComboBoxHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonChrom)
                    .addComponent(jRadioButtonRegion)))
        );

        jComboBoxHistory.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxHistoryItemStateChanged(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("add Annotation:");

        jComboBoxAnno.setModel(new javax.swing.DefaultComboBoxModel(
            this.listAnnotations));
    jComboBoxAnno.setMaximumSize(new java.awt.Dimension(100, 25));
    jComboBoxAnno.setMinimumSize(new java.awt.Dimension(100, 25));
    jComboBoxAnno.setPreferredSize(new java.awt.Dimension(100, 25));

    javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
    topPanel.setLayout(topPanelLayout);
    topPanelLayout.setHorizontalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addGap(77, 77, 77)
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jComboBoxAnno, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(361, 361, 361)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(180, Short.MAX_VALUE))
    );
    topPanelLayout.setVerticalGroup(
        topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(topPanelLayout.createSequentialGroup()
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxAnno, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );

    jComboBoxAnno.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jComboBoxAnnoActionPerformed(evt);
        }
    });

    jPanelRuler.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
    jPanelRuler.setPreferredSize(new java.awt.Dimension(600, 20));
    jPanelRuler.setLayout(new java.awt.CardLayout());

    tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanelRuler, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addComponent(sPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1020, Short.MAX_VALUE)))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanelRuler, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                .addComponent(sPane, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)))
    );
    }// </editor-fold>//GEN-END:initComponents

private void jRadioButtonChromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonChromActionPerformed
    if (this.jRadioButtonChrom.isSelected()) {

        this.showAll();
        this.getCBHistory().setSelectedItem(null);
    }
}//GEN-LAST:event_jRadioButtonChromActionPerformed

    public JRadioButton getCBRegion() {
        return this.jRadioButtonRegion;
    }

    public JRadioButton getCBFullChrom() {
        return this.jRadioButtonChrom;
    }

    void jComboBoxHistoryItemStateChanged(java.awt.event.ItemEvent evt) {
        this.setPositionFromHistory();
    }

    void jComboBoxAnnoActionPerformed(java.awt.event.ActionEvent evt) {
        String anno = this.jComboBoxAnno.getSelectedItem().toString();
        try {
            this.addAnno(this.release.toString(), anno);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));
        }
    }

    public void mapArrays() {
        List<Data> list = this.getDataList();
        if (list == null || list.size() == 0) {
            return;
        }
        List<MapData> mapList = MapDialog.getMapping(list, this.release.toString());
        if (mapList != null) {
            MapCATDataAction.showData(mapList);
        }
    }

    void tabbedPanePropertyChange(ChangeEvent evt) {
        this.updateView(true, "");

    }

    void updateView(boolean fullChrom, String text) {
        ArrayViewBase.resetColorScale();
        int i = tabbedPane.getSelectedIndex();
        if (i < 0) {
            return;
        }
        System.out.println("ArrayFrame: update view Tab Selected: " + i + " fullChrom: " + fullChrom);

        if (fullChrom) {
            for (ArrayView a : ChromTab.getArrayViews(vChromTabs.get(i))) {
                a.setRange();
            }
            //this.getCBHistory().setSelectedItem(null);
        }
        CardLayout cl = (CardLayout) (this.panelChroms.getLayout());
        cl.show(panelChroms, vChromTabs.get(i).chrom);
        CardLayout cl2 = (CardLayout) (this.jPanelRuler.getLayout());
        cl2.show(this.jPanelRuler, vChromTabs.get(i).chrom);
        this.jRadioButtonChrom.setSelected(fullChrom);
        this.jRadioButtonRegion.setSelected(!fullChrom);
        this.scaleArrays(); //210912   kt  bug scale at view change

        repaint();
    }

    public ChromTab getSelectedChromTab() {
        int i = tabbedPane.getSelectedIndex();
        if (i < 0) {
            return null;
        } else {
            return this.vChromTabs.get(i);
        }
    }
    /*
    public ArrayView cropArrayView(ArrayView v, ArrayData d, Integer begin, Integer end, ChromTab chromtab) {
    int beginIndex = Collections.binarySearch(v.arrayStart, begin);
    int endIndex = Collections.binarySearch(v.arrayStart, end);
    beginIndex = (beginIndex < 0) ? beginIndex *= -1 : beginIndex;
    endIndex = (endIndex < 0) ? endIndex *= -1 : endIndex;
    if (endIndex <= 1) {
    endIndex = v.arrayStart.size() - 1;
    }
    beginIndex = (beginIndex == 0) ? beginIndex : --beginIndex;
    endIndex = (endIndex == 0) ? endIndex : --endIndex;
    System.out.println("getSubView Index : begin " + beginIndex + " end " + endIndex);
    
    Vector<String> name = new Vector<String>(v.arrayName.subList(beginIndex, endIndex));
    Vector<Integer> start = new Vector<Integer>(v.arrayStart.subList(beginIndex, endIndex));
    Vector<Integer> stop = new Vector<Integer>(v.arrayStop.subList(beginIndex, endIndex));
    Vector<Double> data = new Vector<Double>(v.arrayRatio.subList(beginIndex, endIndex));
    //ArrayView a = ArrayView.getView(this.getClass(), minRatio, maxRatio, this.arrayId, start, stop, data, chromtab);
    
    
    ArrayView a = ArrayViewBase.getView(d, name, start, stop, data, chromtab);
    
    a.setRange(begin.intValue(), end.intValue());
    
    return a;
    }
     */

    /**
     * create new chromtab (just one chromosome) as a result of a zooming event
     *
     * @param chrom
     * @param begin
     * @param end
     * @param parent 
     */
    @Deprecated
    public void cropChromTab(Integer begin, Integer end, ChromTab chromtab) {
        Logger.getLogger(ArrayFrame.class.getName()).log(
                Level.INFO, "create zoom chrom tab for chrom " + chromtab.chrom);

        // get all arrays attached to the current chromtab
        ArrayView[] vList = ChromTab.getArrayViews(chromtab);

        ArrayData[] adList = new ArrayData[vList.length];
        //Arrays.fill(clazzId, ArrayView.class);

        for (int i = 0; i < vList.length; i++) {
            adList[i] = this.arrays.get(vList[i].getArrayId());

        }
        // new frame to keep new array views

        ArraySubsetFrame newFrame = null;
        //update if (this instanceof ArrayMapFrame) {
        //newFrame = new ArrayMapSubsetFrame(chrom, begin, end, ((ArrayMapFrame) parent.parentFrame).mapId);
        // } else {
        newFrame = new ArraySubsetFrame(chromtab.chrom, begin, end, this.getRelease());
        //newFrame.addArrays(arraysId, clazzId, begin, end);
        // }
        //newFrame.addArrays(arraysId, clazzId, begin, end);


        ArrayView newView = null;

        for (int i = 0; i < vList.length; i++) {
            ArrayView v = vList[i];
            ArrayData fm = null;
            if (!(adList[i] instanceof ArrayDataAnno)) {
                fm = ArrayData.createArrayData(adList[i].getArrayClazz(), adList[i].getData());
            } else {
                fm = ArrayData.createArrayDataAnno(adList[i].getArrayClazz(), adList[i].getRelease(),
                        ((ArrayDataAnno) adList[i]).getName());
            }
            newFrame.arrays.put(fm.getId(), fm);

            newFrame.arrayPanel.addArray(fm);


            //newView = this.cropArrayView(v, fm, begin, end, newFrame.chromtab);
            newView.addParent(newFrame);

            newFrame.chromtab.add(newView);


        }

        newFrame.repaint();
    }
    /*
    public static XPortArray getXPortArray(String datatype, String clazz) {
    
    //XPort api = Lookup.getDefault().lookup(org.molgen.genomeCATPro.xport.XPort.class);
    Logger.getLogger(ArrayFrame.class.getName()).log(Level.INFO,
    "looking for datatype " + datatype + " clazz " + clazz);
    
    
    Lookup.Result<XPortArray> rslt = Lookup.getDefault().lookup(tmplXPort);
    for (Lookup.Item item : rslt.allItems()) {
    if(item.getType().getName().equalsIgnoreCase(datatype))
    Logger.getLogger(ArrayFrame.class.getName()).log(Level.INFO, "Found Service: " + impl.getName());
    //Vector<String> list = impl.getType();
    
    return impl;
    
    }
    
    return null;
    }
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSelectedChrom;
    protected javax.swing.JComboBox jComboBoxAnno;
    protected javax.swing.JComboBox jComboBoxHistory;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelRuler;
    private javax.swing.JPopupMenu jPopupMenuData;
    javax.swing.JRadioButton jRadioButtonChrom;
    javax.swing.JRadioButton jRadioButtonRegion;
    protected javax.swing.JSplitPane jSplitPane;
    private javax.swing.JPanel legendPanel;
    private javax.swing.JPanel panelChroms;
    protected javax.swing.JScrollPane sPane;
    public javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    void showAll() {
        for (ChromTab chromtab : this.vChromTabs) {
            for (ArrayView a : ChromTab.getArrayViews(chromtab)) {
                a.setRange();
                a.repaint();
            }
        }
    }

    JComboBox getCBHistory() {
        return this.jComboBoxHistory;
    }
    boolean changePosition = false;

    void setPositionFromHistory() {
        if (changePosition) {
            return;
        }

        Region ract = (Region) this.getCBHistory().getSelectedItem();
        if (ract == null) {
            return;
        }
        if (this.vChromTabs.size() == 1) {
            return; //subview with one chrom no change possible

        }
        changePosition = true;

        System.out.println("ArrayFrame: setPositionFromHistory " + this.changePosition);
        if (this.getSelectedChromTab() == null || !this.getSelectedChromTab().chrom.contentEquals(ract.getChrom())) {
            // 210912 kt bug open map set position
            // 200213 kt but set position at component showing
            // first set new chrom

            String[] chroms = ChromTab.getChroms(release);
            for (int i = 0; i < chroms.length; i++) {
                if (chroms[i].contentEquals(ract.getChrom())) {

                    this.tabbedPane.setSelectedIndex(i);
                    // trigger update view global für aktuelles Chromosome
                    break;
                }
            }
        }

        System.out.println("ArrayFrame:  setPositionFromHistory " + ract.toString());

        // combobox hat position durch updateView vergessen
        //this.getCBHistory().setSelectedItem(ract);

        ChromTab c = this.getSelectedChromTab();
        if (c == null) {
            this.changePosition = false;
            System.out.println("ArrayFrame:  setPositionFromHistory no chrom selected -> return");
            return;// 210912 kt bug open map set position

        }
        for (ArrayView a : ChromTab.getArrayViews(c)) {
            a.setRange(ract.getChromStart(), ract.getChromEnd());
        }
        this.setPosition(c.chrom, ract.getChromStart());
        this.updateView(false, ract.getChromStart() + "-" + ract.getChromEnd());
        //this.jCheckBoxFullChrom.setSelected(false);
        this.changePosition = false;
        //System.out.println("ArrayFrame:  setPositionFromHistory " + this.changePosition);

    }

    /**
     * triggered by region selection by mouse
     */
    public void getPositionToHistory() {
        Region rr = WebPositionPanel.getActPosition();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "addPositionToHistory: " + (rr != null ? rr.toString() : "--"));

        if (this.getCBHistory() != null) // 210912 kt
        {
            Vector l = WebPositionPanel.getPositionList();

            this.getCBHistory().setModel(new DefaultComboBoxModel((l != null ? l : new Vector())));
        }
        this.getCBHistory().setSelectedItem(rr);
    }

    /**
     * change view to cut-out
     * @param firstPos
     * @param secondPos
     */
    void showDetails(long firstPos, long secondPos, ChromTab chromtab) {
        int n = JOptionPane.showConfirmDialog(
                this,
                "would you like to zoom in to region?  \n" +
                Long.toString(firstPos) + "-" + Long.toString(secondPos),
                "Show Details",
                JOptionPane.YES_NO_OPTION);
        for (ArrayView a : ChromTab.getArrayViews(chromtab)) {
            a.setShowDetailFrame(false);
        }

        if (n == JOptionPane.YES_OPTION) {
            //this.jCheckBoxFullChrom.setSelected(false);
            //this.jLabelCutOut.setText(firstPos + "-" + secondPos);
            for (ArrayView a : ChromTab.getArrayViews(chromtab)) {
                a.setRange(firstPos, secondPos);
            }
            WebPositionPanel.setActPosition(
                    new RegionImpl("", chromtab.chrom, firstPos, secondPos));
            this.updateView(false, firstPos + "-" + secondPos);
            this.getPositionToHistory();
        } else {
            this.updateView(true, "");
        }
    }
    boolean showData = ShowDataAction.getState();
    final static String PROP_SHOWDATA = "showData";

    public boolean isShowData() {
        return showData;
    }

    public void setShowData(boolean showData) {
        boolean old = this.showData;
        this.showData = showData;
        changeSupport.firePropertyChange(PROP_SHOWDATA, old, this.showData);
    }

    public void showData(boolean show) {
        this.setShowData(show);
    }
    boolean showRuler = ShowRulerAction.getState();
    final static String PROP_SHOWRULER = "showRuler";

    public boolean isShowRuler() {
        return showRuler;
    }

    public void setShowRuler(boolean showRuler) {
        boolean old = this.showRuler;
        this.showRuler = showRuler;

        changeSupport.firePropertyChange(PROP_SHOWRULER, old, this.showRuler);
        this.repaint();
    }

    public void showRuler(boolean show) {
        this.setShowRuler(show);
    }
    final static String PROP_CHANGE_RULER = "changeRuler";

    public double getRulerStepSize() {
        return CATPropertiesMod.props().getRulerStepSize();
    }

    public void setRulerStepSize(double rulerStepSize) {
        /*double old = CATPropertiesMod.props().getRulerStepSize();
        CATPropertiesMod.props().setRulerStepSize(rulerStepSize);

        changeSupport.firePropertyChange(PROP_CHANGE_RULER, old, CATPropertiesMod.props().getRulerStepSize());
        this.repaint();*/
        
    }
    Region r = new RegionImpl();

    void setDetailPos(String name, String chrom, long start, long end) {
        if (chrom.contentEquals("")) {
            GlobalPositionDataPanel.setDetail(null);
        }
        r.setName(name);
        r.setChrom(chrom);
        r.setChromStart(start);
        r.setChromEnd(end);
        GlobalPositionDataPanel.setDetail(r);

    }

    void setPosition(String chrom, long pos) {
        GlobalPositionDataPanel.setCurrPosition(chrom + ": " + pos);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public int getZoomX() {
        return 0;
    }

    public boolean doZoomX(int d) {
        return false;
    }

    public boolean doZoomY(int d) {
        return false;
    }

    public boolean isColorScaleRedGreen() {
        return CATPropertiesMod.props().isColorScaleRedGreen();
    }

    public void setColorScaleRedGreen(boolean _colorScaleRedGreen) {
        CATPropertiesMod.props().setColorScaleRedGreen(_colorScaleRedGreen);

        Logger.getLogger(ArrayFrame.class.getName()).log(
                Level.INFO, "set " +
                (CATPropertiesMod.props().isColorScaleRedGreen() ? "red/green " : "yellow/blue") + " color ");
        ArrayViewBase.resetColorScale();
        this.updateView(true, "");
    }

    public static Color[] getColorScale() {
        return colorScale;
    }
}
