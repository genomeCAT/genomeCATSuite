package org.molgen.genomeCATPro.guimodul.cghpro;

/**
 * @name CGHPROFrame
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @author Wei Chen This file is part of the GenomeCAT software package. Katrin
 * Tebel <tebel at molgen.mpg.de>. The contents of this file are subject to the
 * terms of either the GNU General Public License Version 2 only ("GPL") or the
 * Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.molgen.genomeCATPro.guimodul.BasicFrame;
import org.molgen.genomeCATPro.guimodul.anno.PlotAnnotation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.cghpro.chip.Chip;
import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;
import org.molgen.genomeCATPro.cghpro.chip.ChipImpl;

import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.ServiceListener;
import org.molgen.genomeCATPro.guimodul.data.AppInterface;
import org.openide.util.NbPreferences;

/**
 *
 * 210912 kt exportImage new
 */
public class CGHPROFrame extends BasicFrame
        implements PropertyChangeListener, ServiceListener, AppInterface {

    final static String EMPTY_SAMPLE = "none";
    static final String PROP_ADD_CHIP = "AddChip";
    int genomeViewCols = 4;
    int genomeViewRows = 6;
    //loaded chips
    ChipFeature originalChip = null;
    ChipFeature overlayedChip;
    Chip trackChip;
    List<Chip> chipList = new Vector();
    PlotChipAnChrom matrixChip = null;
    CytoBandManagerImpl cytoBandManager = null;
    private List<ExperimentData> datalist = new Vector<ExperimentData>();
    private List<Track> tracklist = new Vector<Track>();
    //kt 040316  
    // protected final PropertyChangeSupport pss = new PropertyChangeSupport(this);

    public CGHPROFrame() {
        super(GenomeRelease.hg18.toString());
        ExperimentService.addListener(this);
    }

    public CGHPROFrame(Data d) {
        super(d.getGenomeRelease());
        ExperimentService.addListener(this);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "Init: " + d.toString());

        try {
            this.originalChip = (ChipFeature) ChipImpl.loadChipAsExperimentFromDB(
                    ChipFeature.class, d);
            this.chipList.add(this.originalChip);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                    "Error loading: " + d.toString(),
                    ex);

        }

        this.cytoBandManager = new CytoBandManagerImpl(GenomeRelease.toRelease(this.release));
        this.chromLength = CytoBandManagerImpl.getChromLength(GenomeRelease.toRelease(this.release));
        this.originalChip.getDataEntity().addPropertyChangeListener(this);
        initComponents();
        super.initFrame();
    }

    // display additional Chip as child of original data
    public ChipFeature getOverlayedChip() {
        return overlayedChip;
    }

    public ChipFeature getOriginalChip() {
        return originalChip;
    }

    public List<Chip> getChipList() {
        return this.chipList;
    }

    public JComboBox getCBHistory() {
        return this.jComboBoxHistory;
    }

    void setAberrantSpots(Chip c) {
        if (c instanceof ChipFeature) {
            int x = ((ChipFeature) c).setAberrantFeaturesByRatio(
                    this.getThresholdGain(), this.getThresholdLoss());

            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    x + " features are set aberrant");
        }
    }

    void resetAberrantSpots(Chip c) {
        if (c instanceof ChipFeature) {
            ((ChipFeature) c).unsetAberrantFeatures();

            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    "all features are unset aberrant");
        }
    }

    /**
     * void setOverlayedChip(String chipname) { if
     * (chipname.contentEquals(CGHPROFrame.EMPTY_SAMPLE)) { return; }
     * ChipFeature chip = null; for (Chip c : this.chipList) { if
     * (c.getName().contentEquals(chipname) && c instanceof ChipFeature) {
     *
     * chip = (ChipFeature) c; break; } } try { if (chip == null) { chip = new
     * ChipFeature(chipname, this.release, null); chip.loadFromDB(); if
     * (chip.getError()) {
     * Logger.getLogger(this.getClass().getName()).log(Level.INFO, "error
     * loading chip: " + chipname); } } } catch (Exception exception) {
     * Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "error: ",
     * exception);
     *
     * }
     * this.setOverlayedChip(chip); }
     */
    void setTrackChip(Chip newChip) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                newChip != null ? newChip.getName() : " empty track chip");

        this.trackChip = newChip;
        this.matrixChip.setChipTrack(this.trackChip);
        this.updateMatrixView();
    }

    void setOverlayedChip(Chip c) {

        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                c != null ? c.getName() : "empty overlayed chip");

        this.overlayedChip = (ChipFeature) c;
        this.matrixChip.setChip2(this.overlayedChip);
        this.updateMatrixView();
    }
    // display additional track as child of original data

    Vector<String> getTracksFromParentChip() {
        if (this.getOriginalChip().getDataEntity() instanceof ExperimentData) {

            List<Track> _list = ((ExperimentData) this.originalChip.getDataEntity()).getTrackList();
            this.tracklist.clear();
            this.tracklist.addAll(_list);
            for (ExperimentData d : this.datalist) {
                this.tracklist.addAll(d.getTrackList());
            }
            for (Track t : _list) {
                this.tracklist.addAll(t.getChildrenList());

            }

        }
        if (this.getOriginalChip().getDataEntity() instanceof Track) {
            this.tracklist = ((Track) this.originalChip.getDataEntity()).getChildrenList();
            //SampleService.getChildren(this.originalChip.getSample());
        }
        Vector<String> llistt = new Vector<String>();
        llistt.add(CGHPROFrame.EMPTY_SAMPLE);
        for (Track t : this.tracklist) {
            if (t.getGenomeRelease().contentEquals(this.getRelease())) {
                llistt.add(t.getName());
            }
        }
        return llistt;
    }

    Vector<String> getChildrenForChip() {
        if (this.getOriginalChip() != null
                && this.getOriginalChip().getDataEntity() instanceof ExperimentData) {
            this.datalist = ((ExperimentData) this.originalChip.getDataEntity()).getChilrenList();
        } else {
            this.datalist = new Vector<ExperimentData>();
            //SampleService.getChildren(this.originalChip.getSample());
        }
        Vector<String> llistt = new Vector<String>();
        llistt.add(CGHPROFrame.EMPTY_SAMPLE);
        for (ExperimentData t : this.datalist) {
            if (t.getGenomeRelease().contentEquals(this.getRelease())) {
                llistt.add(t.getName());
            }
        }
        return llistt;
    }

    /**
     * get chip from list of chips or create and load via xport
     *
     * @param chipname
     * @param isTrack
     * @return
     */
    Chip getChip(String chipname, boolean isTrack) {
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                "view " + (isTrack ? " Track " : " Chip ") + chipname);

        if (chipname.contentEquals(CGHPROFrame.EMPTY_SAMPLE)) {
            return null;
        }

        for (Chip c : this.chipList) {
            if (c.getName().contentEquals(chipname)) {
                return c;
            }
        }

        Data d = null;
        if (isTrack) {
            for (Track t : this.tracklist) {
                if (t.getName().contentEquals(chipname) && t.getGenomeRelease().contentEquals(this.getRelease())) {
                    d = t;
                    break;
                }
            }
        } else {
            for (ExperimentData s : this.datalist) {
                if (s.getName().contentEquals(chipname) && s.getGenomeRelease().contentEquals(this.getRelease())) {
                    d = s;
                    break;
                }
            }
        }

        Chip c = loadChip(isTrack, d);
        this.pss.firePropertyChange(CGHPROFrame.PROP_ADD_CHIP, null, c);
        return c;
    }

    /**
     * load data for chip
     *
     * @param chipname
     * @param clazz
     * @param datatype
     * @return
     */
    private Chip loadChip(boolean isTrack, Data d) {
        Chip chip = null;

        if (isTrack) {
            chip = ChipImpl.loadChipAsTrackFromDB(ChipFeature.class, d);
        } else {
            chip = ChipImpl.loadChipAsExperimentFromDB(ChipFeature.class, d);
        }
        this.chipList.add(chip);
        return chip;

    }

    public int getGenomeViewCols() {
        return genomeViewCols;
    }

    public void setGenomeViewCols(int genomeViewCols) {
        this.genomeViewCols = genomeViewCols;
    }

    public int getGenomeViewRows() {
        return genomeViewRows;
    }

    public void setGenomeViewRows(int genomeViewRows) {
        this.genomeViewRows = genomeViewRows;
    }

    /**
     * add newly created chip to current cghproview update combobox track and
     * view, set new chip as actual view
     *
     * @param newChip
     */
    public void addChip(Chip newChip) {
        if (newChip.getError()) {
            return;
        }
        if (!newChip.getRelease().toString().contentEquals(this.release)) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                    "genomerelease not allowed for this view: " + newChip.getRelease().toString());
            return;
        }

        this.chipList.add(newChip);

        jComboBoxChipViews.setModel(new DefaultComboBoxModel(
                this.getChildrenForChip()));
        this.jComboBoxTracks.setModel(new DefaultComboBoxModel(
                getTracksFromParentChip()));

        String samplename = newChip.getName();

        int j = -1;

        for (int i = 0; i < this.jComboBoxChipViews.getModel().getSize(); i++) {
            if (this.jComboBoxChipViews.getModel().getElementAt(i).toString().contentEquals(samplename)) {
                j = i;
                break;
            }
        }
        if (j >= 0) {
            this.jComboBoxChipViews.setSelectedIndex(j);
        } else {
            for (int i = 0; i < this.jComboBoxTracks.getModel().getSize(); i++) {
                if (this.jComboBoxTracks.getModel().getElementAt(i).toString().contentEquals(samplename)) {
                    j = i;
                    break;
                }
            }
            if (j >= 0) {

                this.jComboBoxTracks.setSelectedIndex(j);
            }
        }
    }

    @Override
    public void initAnno() {
        super.set_noAnno(super.get_noAnno() + 1);
        PlotAnnotation cytoband = new PlotAnnotation(this, this.cytoBandManager,
                super.get_noAnno());
        cytoband.setOpaque(true);
        cytoband.setBackground(Color.white);
        cytoband.updatePlot(this.chromId);

        this.listAnnoLabels.put(super.get_noAnno(), cytoband);
        this.listAnno.put(super.get_noAnno(), this.cytoBandManager.getAnnotation());

        this.jPanelPlotAnno.add(cytoband);
        this.jPanelPlotAnno.setPreferredSize(
                new Dimension(this.getPlotAnnoWidth(), this.getPlotPanelHeight()));

    }

    @Override
    public PlotPanel getMatrix() {
        return this.matrixChip;
    }

    @Override
    public void initMatrix() {

        this.matrixChip = new PlotChipAnChrom(this);
        this.matrixChip.setOpaque(true);
        this.matrixChip.setBackground(Color.white);
        this.matrixChip.setChip1(originalChip);
        this.matrixChip.setChip2(this.overlayedChip);
        this.matrixChip.updatePlot(this.chromId);

    }
    Color aberrantNegColor = Color.RED;

    public Color getAbberantNegColor() {
        return this.aberrantNegColor;
    }

    public void setAbberantNegColor(Color c) {
        this.aberrantNegColor = c;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                c.toString());

        super.refreshMatrixView();
    }
    Color aberrantPosColor = Color.GREEN;

    public Color getAbberantPosColor() {
        return aberrantPosColor;

    }

    public void setAberrantPosColor(Color aberrantPosColor) {
        this.aberrantPosColor = aberrantPosColor;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                aberrantPosColor.toString());

        super.refreshMatrixView();
    }
    Color chip1Color = Color.LIGHT_GRAY;

    public Color getChip1Color() {
        return chip1Color;
    }

    public void setChip1Color(Color chip1Color) {
        this.chip1Color = chip1Color;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                chip1Color.toString());

        super.refreshMatrixView();
    }
    Color chip2Color = Color.DARK_GRAY;

    public Color getChip2Color() {
        return chip2Color;
    }

    public void setChip2Color(Color chip2Color) {
        this.chip2Color = chip2Color;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                chip2Color.toString());
        this.refreshMatrixView();
    }
    Color chipTrackColor = Color.MAGENTA;

    public Color getChipTrackColor() {
        return chipTrackColor;
    }

    public void setChipTrackColor(Color chipTrackColor) {
        this.chipTrackColor = chipTrackColor;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                chipTrackColor.toString());

        this.refreshMatrixView();
    }
    int spotWidth = 2;

    public int getSpotWidth() {
        return spotWidth;
    }

    public void setSpotWidth(int spotWidth) {
        boolean update = (this.spotWidth == spotWidth);
        this.spotWidth = spotWidth;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                "set " + spotWidth);

        if (update) {
            this.updateMatrixView();
            super.updateAnnotation();
        }
    }
    int minSpotHeight = 1;

    public int getMinSpotHeight() {
        return minSpotHeight;
    }

    public void setMinSpotHeight(int minSpotHeight) {
        this.minSpotHeight = minSpotHeight;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                "set min Spot Height" + minSpotHeight);
        this.refreshMatrixView();
    }
    double negThresholdLine = -0.4;
    double posThresholdLine = 0.4;

    public double getNegThresholdLine() {
        return negThresholdLine;
    }

    public void setNegThresholdLine(double negThresholdLine) {
        this.negThresholdLine = negThresholdLine;
        super.refreshMatrixView();
    }

    public double getPosThresholdLine() {
        return posThresholdLine;
    }

    public void setPosThresholdLine(double posThresholdLine) {
        this.posThresholdLine = posThresholdLine;
        super.refreshMatrixView();
    }
    double thresholdLoss = 0.0;
    double thresholdGain = 0.0;

    public double getThresholdLoss() {
        return thresholdLoss;
    }

    public void setThresholdLoss(double negThreshold) {
        this.thresholdLoss = negThreshold;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                "set " + negThreshold);
    }

    public double getThresholdGain() {
        return thresholdGain;
    }

    public void setThresholdGain(double posThreshold) {
        this.thresholdGain = posThreshold;
        Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
                "set " + posThreshold);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupRBChrom = new javax.swing.ButtonGroup();
        buttonGroupSelectedView = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jPanelAddData = new javax.swing.JPanel();
        jPanelAnno = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jPanelTracks = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxTracks = new javax.swing.JComboBox();
        jPanelAddView = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxChipViews = new javax.swing.JComboBox();
        cbHideOriginalData = new javax.swing.JCheckBox();
        jPanelGenome = new javax.swing.JPanel();
        jComboBoxHistory = new javax.swing.JComboBox();
        jRadioButtonChrom = new javax.swing.JRadioButton();
        jRadioButtonRegion = new javax.swing.JRadioButton();
        jPanelChroms = new javax.swing.JPanel();
        rbChrom1 = new javax.swing.JRadioButton();
        rbChrom2 = new javax.swing.JRadioButton();
        rbChrom3 = new javax.swing.JRadioButton();
        rbChrom4 = new javax.swing.JRadioButton();
        rbChrom5 = new javax.swing.JRadioButton();
        rbChrom6 = new javax.swing.JRadioButton();
        rbChrom7 = new javax.swing.JRadioButton();
        rbChrom8 = new javax.swing.JRadioButton();
        rbChrom9 = new javax.swing.JRadioButton();
        rbChrom10 = new javax.swing.JRadioButton();
        rbChrom11 = new javax.swing.JRadioButton();
        rbChrom12 = new javax.swing.JRadioButton();
        rbChrom13 = new javax.swing.JRadioButton();
        rbChrom14 = new javax.swing.JRadioButton();
        rbChrom15 = new javax.swing.JRadioButton();
        rbChrom16 = new javax.swing.JRadioButton();
        rbChrom17 = new javax.swing.JRadioButton();
        rbChrom18 = new javax.swing.JRadioButton();
        rbChrom19 = new javax.swing.JRadioButton();
        rbChrom20 = new javax.swing.JRadioButton();
        rbChrom21 = new javax.swing.JRadioButton();
        rbChrom22 = new javax.swing.JRadioButton();
        rbChromX = new javax.swing.JRadioButton();
        rbChromY = new javax.swing.JRadioButton();
        rbAll = new javax.swing.JRadioButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPaneMatrix = new javax.swing.JScrollPane();
        jPanelPlot = new javax.swing.JPanel();
        jScrollPaneAnnotation = new javax.swing.JScrollPane();
        jPanelPlotAnno = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(930, 600));

        jToolBar1.setBorder(null);
        jToolBar1.setBorderPainted(false);
        jToolBar1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        jPanelAddData.setMaximumSize(new java.awt.Dimension(1200, 200));
        jPanelAddData.setLayout(new java.awt.GridLayout(1, 0, 10, 10));

        jPanelAnno.setMaximumSize(new java.awt.Dimension(200, 50));
        jPanelAnno.setMinimumSize(new java.awt.Dimension(200, 50));
        jPanelAnno.setOpaque(false);
        jPanelAnno.setPreferredSize(new java.awt.Dimension(200, 50));
        jPanelAnno.setLayout(new java.awt.GridLayout(3, 1));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("add Annotation:");
        jPanelAnno.add(jLabel2);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(
            this.listAnnotations));
    jComboBox2.setMaximumSize(new java.awt.Dimension(100, 25));
    jComboBox2.setMinimumSize(new java.awt.Dimension(100, 25));
    jComboBox2.setPreferredSize(new java.awt.Dimension(100, 25));
    jComboBox2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jComboBox2ActionPerformed(evt);
        }
    });
    jPanelAnno.add(jComboBox2);

    jPanelAddData.add(jPanelAnno);

    jPanelTracks.setMaximumSize(new java.awt.Dimension(200, 100));
    jPanelTracks.setMinimumSize(new java.awt.Dimension(200, 100));
    jPanelTracks.setOpaque(false);
    jPanelTracks.setPreferredSize(new java.awt.Dimension(200, 100));
    jPanelTracks.setLayout(new java.awt.GridLayout(3, 1, 20, 2));

    jLabel3.setText("add Track:");
    jPanelTracks.add(jLabel3);

    jComboBoxTracks.setModel(
        new javax.swing.DefaultComboBoxModel(
            this.getTracksFromParentChip()));
    jComboBoxTracks.setMinimumSize(new java.awt.Dimension(100, 25));
    jComboBoxTracks.setPreferredSize(new java.awt.Dimension(100, 25));
    jComboBoxTracks.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jComboBoxTracksActionPerformed(evt);
        }
    });
    jPanelTracks.add(jComboBoxTracks);

    jPanelAddData.add(jPanelTracks);

    jPanelAddView.setOpaque(false);
    jPanelAddView.setLayout(new java.awt.GridLayout(3, 1, 3, 3));

    jLabel1.setText("add View:");
    jPanelAddView.add(jLabel1);

    jComboBoxChipViews.setModel(
        new javax.swing.DefaultComboBoxModel(
            this.getChildrenForChip()));
    jComboBoxChipViews.setMaximumSize(new java.awt.Dimension(100, 25));
    jComboBoxChipViews.setMinimumSize(new java.awt.Dimension(100, 25));
    jComboBoxChipViews.setPreferredSize(new java.awt.Dimension(100, 25));
    jComboBoxChipViews.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jComboBoxChipViewsActionPerformed(evt);
        }
    });
    jPanelAddView.add(jComboBoxChipViews);

    cbHideOriginalData.setText("hide original data");
    cbHideOriginalData.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    cbHideOriginalData.setOpaque(false);
    cbHideOriginalData.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cbHideOriginalDataActionPerformed(evt);
        }
    });
    jPanelAddView.add(cbHideOriginalData);

    jPanelAddData.add(jPanelAddView);

    jPanelGenome.setBorder(javax.swing.BorderFactory.createTitledBorder("detail history"));
    jPanelGenome.setMaximumSize(new java.awt.Dimension(200, 100));
    jPanelGenome.setMinimumSize(new java.awt.Dimension(200, 100));
    jPanelGenome.setOpaque(false);

    jComboBoxHistory.setEditable(true);
    jComboBoxHistory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    /*JTextComponent editor = (JTextComponent) jComboBoxHistory.getEditor().getEditorComponent();
    // change the editor's document to our BadDocument
    editor.setDocument(new RegionEditText(jComboBoxHistory));*/
    jComboBoxHistory.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            ItemStateChanged(evt);
        }
    });

    buttonGroupSelectedView.add(jRadioButtonChrom);
    jRadioButtonChrom.setSelected(true);
    jRadioButtonChrom.setText("full chrom");
    jRadioButtonChrom.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButtonChromActionPerformed(evt);
        }
    });

    buttonGroupSelectedView.add(jRadioButtonRegion);
    jRadioButtonRegion.setText("detail");
    jRadioButtonRegion.setEnabled(false);

    javax.swing.GroupLayout jPanelGenomeLayout = new javax.swing.GroupLayout(jPanelGenome);
    jPanelGenome.setLayout(jPanelGenomeLayout);
    jPanelGenomeLayout.setHorizontalGroup(
        jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanelGenomeLayout.createSequentialGroup()
            .addGap(32, 32, 32)
            .addGroup(jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(jPanelGenomeLayout.createSequentialGroup()
                    .addComponent(jRadioButtonChrom, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jRadioButtonRegion))
                .addComponent(jComboBoxHistory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap(19, Short.MAX_VALUE))
    );
    jPanelGenomeLayout.setVerticalGroup(
        jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGenomeLayout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jComboBoxHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jRadioButtonChrom)
                .addComponent(jRadioButtonRegion))
            .addGap(12, 12, 12))
    );

    jPanelAddData.add(jPanelGenome);

    jToolBar1.add(jPanelAddData);

    jPanelChroms.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    jPanelChroms.setMaximumSize(new java.awt.Dimension(100, 32767));
    jPanelChroms.setMinimumSize(new java.awt.Dimension(100, 500));
    jPanelChroms.setPreferredSize(new java.awt.Dimension(61, 550));
    jPanelChroms.setLayout(new java.awt.GridLayout(25, 1));

    buttonGroupRBChrom.add(rbChrom1);
    rbChrom1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom1.setSelected(true);
    rbChrom1.setText("chr1");
    rbChrom1.setActionCommand("chr1");
    rbChrom1.setMaximumSize(new java.awt.Dimension(53, 23));
    rbChrom1.setMinimumSize(new java.awt.Dimension(53, 23));
    rbChrom1.setPreferredSize(new java.awt.Dimension(53, 23));
    rbChrom1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    rbChrom1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbChrom1ActionPerformed(evt);
        }
    });
    jPanelChroms.add(rbChrom1);

    buttonGroupRBChrom.add(rbChrom2);
    rbChrom2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom2.setText("chr2");
    rbChrom2.setActionCommand("chr2");
    rbChrom2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom2);

    buttonGroupRBChrom.add(rbChrom3);
    rbChrom3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom3.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom3.text")); // NOI18N
    rbChrom3.setActionCommand("chr3");
    rbChrom3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom3);

    buttonGroupRBChrom.add(rbChrom4);
    rbChrom4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom4.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom4.text")); // NOI18N
    rbChrom4.setActionCommand("chr4");
    rbChrom4.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom4);

    buttonGroupRBChrom.add(rbChrom5);
    rbChrom5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom5.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom5.text")); // NOI18N
    rbChrom5.setActionCommand("chr5");
    rbChrom5.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom5);

    buttonGroupRBChrom.add(rbChrom6);
    rbChrom6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom6.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom6.text")); // NOI18N
    rbChrom6.setActionCommand("chr6");
    rbChrom6.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom6);

    buttonGroupRBChrom.add(rbChrom7);
    rbChrom7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom7.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom7.text")); // NOI18N
    rbChrom7.setActionCommand("chr7");
    rbChrom7.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom7);

    buttonGroupRBChrom.add(rbChrom8);
    rbChrom8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom8.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom8.text")); // NOI18N
    rbChrom8.setActionCommand("chr8");
    rbChrom8.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom8);

    buttonGroupRBChrom.add(rbChrom9);
    rbChrom9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom9.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom9.text")); // NOI18N
    rbChrom9.setActionCommand("chr9");
    rbChrom9.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom9);

    buttonGroupRBChrom.add(rbChrom10);
    rbChrom10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom10.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom10.text")); // NOI18N
    rbChrom10.setActionCommand("chr10");
    rbChrom10.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom10);

    buttonGroupRBChrom.add(rbChrom11);
    rbChrom11.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom11.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom11.text")); // NOI18N
    rbChrom11.setActionCommand("chr11");
    rbChrom11.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom11);

    buttonGroupRBChrom.add(rbChrom12);
    rbChrom12.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom12.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom12.text")); // NOI18N
    rbChrom12.setActionCommand("chr12");
    rbChrom12.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom12);

    buttonGroupRBChrom.add(rbChrom13);
    rbChrom13.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom13.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom13.text")); // NOI18N
    rbChrom13.setActionCommand("chr13");
    rbChrom13.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom13);

    buttonGroupRBChrom.add(rbChrom14);
    rbChrom14.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom14.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom14.text")); // NOI18N
    rbChrom14.setActionCommand("chr14");
    rbChrom14.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom14);

    buttonGroupRBChrom.add(rbChrom15);
    rbChrom15.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom15.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom15.text")); // NOI18N
    rbChrom15.setActionCommand("chr15");
    rbChrom15.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom15);

    buttonGroupRBChrom.add(rbChrom16);
    rbChrom16.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom16.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom16.text")); // NOI18N
    rbChrom16.setActionCommand("chr16");
    rbChrom16.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom16);

    buttonGroupRBChrom.add(rbChrom17);
    rbChrom17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom17.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom17.text")); // NOI18N
    rbChrom17.setActionCommand("chr17");
    rbChrom17.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom17);

    buttonGroupRBChrom.add(rbChrom18);
    rbChrom18.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom18.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom18.text")); // NOI18N
    rbChrom18.setActionCommand("chr18");
    rbChrom18.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom18);

    buttonGroupRBChrom.add(rbChrom19);
    rbChrom19.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom19.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom19.text")); // NOI18N
    rbChrom19.setActionCommand("chr19");
    rbChrom19.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom19);

    buttonGroupRBChrom.add(rbChrom20);
    rbChrom20.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom20.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom20.text")); // NOI18N
    rbChrom20.setActionCommand("chr20");
    rbChrom20.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom20);

    buttonGroupRBChrom.add(rbChrom21);
    rbChrom21.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom21.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom21.text")); // NOI18N
    rbChrom21.setActionCommand("chr21");
    rbChrom21.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom21);

    buttonGroupRBChrom.add(rbChrom22);
    rbChrom22.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChrom22.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChrom22.text")); // NOI18N
    rbChrom22.setActionCommand("chr22");
    rbChrom22.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom22);

    buttonGroupRBChrom.add(rbChromX);
    rbChromX.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChromX.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChromX.text")); // NOI18N
    rbChromX.setActionCommand("chrX");
    rbChromX.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChromX);

    buttonGroupRBChrom.add(rbChromY);
    rbChromY.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbChromY.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbChromY.text")); // NOI18N
    rbChromY.setActionCommand("chrY");
    rbChromY.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChromY);

    buttonGroupRBChrom.add(rbAll);
    rbAll.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    rbAll.setText(org.openide.util.NbBundle.getMessage(CGHPROFrame.class, "CGHPROPanel.rbAll.text")); // NOI18N
    rbAll.setActionCommand(this.ALL_CHROMS);
    rbAll.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbAllActionPerformed(evt);
        }
    });
    jPanelChroms.add(rbAll);

    jSplitPane1.setDividerLocation(100);
    jSplitPane1.setDividerSize(5);
    jSplitPane1.setAlignmentX(0.5F);
    jSplitPane1.setAlignmentY(0.5F);
    jSplitPane1.setMinimumSize(new java.awt.Dimension(500, 505));
    jSplitPane1.setPreferredSize(new java.awt.Dimension(500, 505));

    jScrollPaneMatrix.setBackground(new java.awt.Color(255, 255, 255));
    jScrollPaneMatrix.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPaneMatrix.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPaneMatrix.setAlignmentX(1.0F);
    jScrollPaneMatrix.setMinimumSize(new java.awt.Dimension(300, 500));

    jPanelPlot.setPreferredSize(new java.awt.Dimension(200, 450));
    jPanelPlot.setLayout(new javax.swing.BoxLayout(jPanelPlot, javax.swing.BoxLayout.Y_AXIS));
    jScrollPaneMatrix.setViewportView(jPanelPlot);

    jSplitPane1.setRightComponent(jScrollPaneMatrix);

    jScrollPaneAnnotation.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPaneAnnotation.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPaneAnnotation.setAlignmentX(0.0F);
    jScrollPaneAnnotation.setMinimumSize(new java.awt.Dimension(100, 500));

    jPanelPlotAnno.setPreferredSize(new java.awt.Dimension(200, 450));

    javax.swing.GroupLayout jPanelPlotAnnoLayout = new javax.swing.GroupLayout(jPanelPlotAnno);
    jPanelPlotAnno.setLayout(jPanelPlotAnnoLayout);
    jPanelPlotAnnoLayout.setHorizontalGroup(
        jPanelPlotAnnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 200, Short.MAX_VALUE)
    );
    jPanelPlotAnnoLayout.setVerticalGroup(
        jPanelPlotAnnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 561, Short.MAX_VALUE)
    );

    jScrollPaneAnnotation.setViewportView(jPanelPlotAnno);

    jSplitPane1.setLeftComponent(jScrollPaneAnnotation);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 796, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanelChroms, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 906, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(7, 7, 7)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanelChroms, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE))
            .addContainerGap())
    );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * update based on modified parameter (chrom, add chip or annotation)
     */
private void setRBMatrixAberration(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setRBMatrixAberration
    JRadioButton b = (JRadioButton) evt.getSource();
    this.chromId = b.getActionCommand();
    super.setPosition(0);
    //System.out.println("setRBMatrixAberration: " + this.chromId);
    super.updateMatrixView();
}//GEN-LAST:event_setRBMatrixAberration

private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
    String anno = this.jComboBox2.getSelectedItem().toString();
    try {
        super.addAnnotation(anno);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));
    }
}//GEN-LAST:event_jComboBox2ActionPerformed

private void jComboBoxChipViewsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxChipViewsActionPerformed

    if (this.jComboBoxChipViews.getSelectedItem().toString().contentEquals(CGHPROFrame.EMPTY_SAMPLE)) {
        this.setOverlayedChip((ChipFeature) null);

    } // todo Chipmanager
    else {
        //this.jCheckBoxAddView.setSelected(true);
        Chip c = this.getChip(this.jComboBoxChipViews.getSelectedItem().toString(), false);
        this.setOverlayedChip(c);

    }

    this.updateMatrixView();
}//GEN-LAST:event_jComboBoxChipViewsActionPerformed

private void cbHideOriginalDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbHideOriginalDataActionPerformed
    if (this.cbHideOriginalData.isSelected()) {
        this.matrixChip.setChip1(null);
    } else {
        this.matrixChip.setChip1(this.originalChip);
    }

    this.updateMatrixView();
}//GEN-LAST:event_cbHideOriginalDataActionPerformed

private void jComboBoxTracksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTracksActionPerformed
    if (this.jComboBoxTracks.getSelectedItem().toString().contentEquals(CGHPROFrame.EMPTY_SAMPLE)) {
        this.setTrackChip(null);

    } // todo Chipmanager
    else {
        //this.jCheckBoxAddView.setSelected(true);
        Chip c = this.getChip(this.jComboBoxTracks.getSelectedItem().toString(), true);
        this.setTrackChip(c);

    }

    this.updateMatrixView();
}//GEN-LAST:event_jComboBoxTracksActionPerformed

private void rbChrom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbChrom1ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_rbChrom1ActionPerformed

    /**
     *
     * @param evt
     */
private void ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ItemStateChanged
    super.setPositionFromHistory();

}//GEN-LAST:event_ItemStateChanged

private void rbAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAllActionPerformed
    super.setAllChromsMatrix(evt);

}//GEN-LAST:event_rbAllActionPerformed

private void jRadioButtonChromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonChromActionPerformed
    if (this.jRadioButtonChrom.isSelected()) {

        super.rescaleView(true, this.chromId, 0, this.chromLength.get(this.chromId));
    }
}//GEN-LAST:event_jRadioButtonChromActionPerformed
    /*
     *  if (!this.jCheckBoxViewOriginalData.isSelected()) {
     this.matrixChip.setChip1(null);
     } else {
     this.matrixChip.setChip1(this.originalChip);
     }
     this.updateMatrixView();
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupRBChrom;
    private javax.swing.ButtonGroup buttonGroupSelectedView;
    private javax.swing.JCheckBox cbHideOriginalData;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBoxChipViews;
    private javax.swing.JComboBox jComboBoxHistory;
    private javax.swing.JComboBox jComboBoxTracks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanelAddData;
    private javax.swing.JPanel jPanelAddView;
    private javax.swing.JPanel jPanelAnno;
    private javax.swing.JPanel jPanelChroms;
    private javax.swing.JPanel jPanelGenome;
    private javax.swing.JPanel jPanelPlot;
    private javax.swing.JPanel jPanelPlotAnno;
    private javax.swing.JPanel jPanelTracks;
    private javax.swing.JRadioButton jRadioButtonChrom;
    private javax.swing.JRadioButton jRadioButtonRegion;
    private javax.swing.JScrollPane jScrollPaneAnnotation;
    private javax.swing.JScrollPane jScrollPaneMatrix;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbChrom1;
    private javax.swing.JRadioButton rbChrom10;
    private javax.swing.JRadioButton rbChrom11;
    private javax.swing.JRadioButton rbChrom12;
    private javax.swing.JRadioButton rbChrom13;
    private javax.swing.JRadioButton rbChrom14;
    private javax.swing.JRadioButton rbChrom15;
    private javax.swing.JRadioButton rbChrom16;
    private javax.swing.JRadioButton rbChrom17;
    private javax.swing.JRadioButton rbChrom18;
    private javax.swing.JRadioButton rbChrom19;
    private javax.swing.JRadioButton rbChrom2;
    private javax.swing.JRadioButton rbChrom20;
    private javax.swing.JRadioButton rbChrom21;
    private javax.swing.JRadioButton rbChrom22;
    private javax.swing.JRadioButton rbChrom3;
    private javax.swing.JRadioButton rbChrom4;
    private javax.swing.JRadioButton rbChrom5;
    private javax.swing.JRadioButton rbChrom6;
    private javax.swing.JRadioButton rbChrom7;
    private javax.swing.JRadioButton rbChrom8;
    private javax.swing.JRadioButton rbChrom9;
    private javax.swing.JRadioButton rbChromX;
    private javax.swing.JRadioButton rbChromY;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getPlotAnno() {
        return this.jPanelPlotAnno;
    }

    @Override
    public JPanel getPlot() {
        return this.jPanelPlot;
    }

    @Override
    public JScrollPane getScrollPanelAnno() {
        return this.jScrollPaneAnnotation;
    }

    @Override
    public JScrollPane getScrollPanelMatrix() {
        return this.jScrollPaneMatrix;
    }

    @Override
    public JRadioButton getCBFullChrom() {
        return this.jRadioButtonChrom;
    }

    public Enumeration<javax.swing.AbstractButton> getChromRBList() {
        return this.buttonGroupRBChrom.getElements();
        /*     List<javax.swing.JComponent> list = SwingUtils.getDescendantsOfType(javax.swing.JComponent.class, this);
         for (javax.swing.JComponent field : list) {
         if (field instanceof javax.swing.text.JTextComponent) {
         ((javax.swing.text.JTextComponent) field).setEditable(b);
         continue;
         } } */    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "PropertyChanged reload track and chiplist" + this.originalChip.getDataEntity().toString());
        jComboBoxChipViews.setModel(new DefaultComboBoxModel(this.getChildrenForChip()));
        this.jComboBoxTracks.setModel(new DefaultComboBoxModel(getTracksFromParentChip()));
    }

    @Override
    public int getRulerHeight() {
        return this.getMinSpotHeight();
    }

    public void dbChanged() {
        if (this.originalChip == null) {
            return;
        }

        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "DB Changed reload track and chiplist");
        jComboBoxChipViews.setModel(new DefaultComboBoxModel(
                this.getChildrenForChip()));

        this.jComboBoxTracks.setModel(new DefaultComboBoxModel(
                getTracksFromParentChip()));
    }

    /**
     *
     */
    @Override
    public void exportImage() {

        //Color oldcolor = c.getBackground();
        //c.setBackground(Color.white);
        String tmpname = this.chromId + ".png";
        // Create image
        System.out.println("export Image: " + tmpname);
        final BufferedImage img
                = new BufferedImage(
                        this.getMatrix().getSize().width,
                        this.getMatrix().getSize().height, BufferedImage.TYPE_INT_RGB);
        ((PlotChipAnChrom) this.getMatrix()).paint(img.getGraphics());
        String path = NbPreferences.forModule(CGHPROFrame.class).get("pathPreference", "");
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
            /*if (suffix.compareToIgnoreCase("jpeg") == 0 || suffix.compareToIgnoreCase("jpg") == 0) {
             OutputStream output = new BufferedOutputStream(new FileOutputStream(f));
            
             }*/

            ImageIO.write(img, suffix, f);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting image: "
                    + e.getMessage());
        }
        //c.setBackground(oldcolor);
        NbPreferences.forModule(CGHPROFrame.class).put("pathPreference", f.getPath());

    }

    @Override
    public int getZoomY() {
        return super.getZoomY();

    }

    @Override
    public JRadioButton getCBRegion() {
        return this.jRadioButtonRegion;
    }
}
