package org.molgen.genomeCATPro.peaks.cnvcat;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.beans.PropertyChangeEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

import org.molgen.genomeCATPro.annotation.CytoBandManagerImpl;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.guimodul.BasicFrame;
import org.molgen.genomeCATPro.guimodul.cghpro.PlotPanel;
import org.molgen.genomeCATPro.guimodul.data.AppInterface;
import org.molgen.genomeCATPro.peaks.Aberration;
import org.molgen.genomeCATPro.peaks.AberrationIds;
import org.molgen.genomeCATPro.peaks.CNVCATPropertiesMod;
import org.molgen.genomeCATPro.peaks.ExportFrequenciesDialog;
import org.molgen.genomeCATPro.peaks.cnvcat.AberrationManager;
import org.molgen.genomeCATPro.peaks.cnvcat.util.ColorEditor;
import org.molgen.genomeCATPro.peaks.cnvcat.util.ColorRenderer;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbPreferences;

/**
 * @name CNVCATFrame
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * This file is part of the GenomeCAT software package.
 * 
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
/**
 * 260912   kt  setPositionFromHistory() refresh display aberrations
 * 101012   kt  updateMatrixView Matrix scrollbar
 *
 */
public class CNVCATFrame extends BasicFrame implements AppInterface {

    final int colID = 0;
    final int colSelected = 1;
    private AberrationManager aberrationManager = null;
    private CytoBandManagerImpl cytoBandManager = null;
    private ScoreFilterManager scoreFilterManager = null;

    public CNVCATFrame() {
        super();



        this.dispAberrations = new Vector<Aberration>();
        this.AberrationManager();
        this.CytoBandManager();
        this.ScoreFilterManager();



        initComponents();
        super.initFrame();
    }

    public void setColorOverlap(Color c) {
        CNVCATPropertiesMod.props().setColorOverlap(c);
        this.refreshMatrixView();
    }

    public void setColorSelected(Color c) {
        CNVCATPropertiesMod.props().setColorSelected(c);
        this.refreshMatrixView();
    }

    public void setGap(int i) {

        CNVCATPropertiesMod.props().setGap(i);

        this.updateMatrixView();
    }

    public int getGap() {
        return CNVCATPropertiesMod.props().getGap();

    }

    public void setMaxQuality(double doubleValue) {
        CNVCATPropertiesMod.props().setMaxQuality(doubleValue);
        this.refreshMatrixView();

    }

    public void setMaxRatio(double d) {
        CNVCATPropertiesMod.props().setMaxRatio(d);
        this.refreshMatrixView();

    }

    public void setMinQuality(double d) {
        CNVCATPropertiesMod.props().setMinQuality(d);
        this.editExtendedSettings();
    }

    public void setMinRatio(double d) {
        CNVCATPropertiesMod.props().setMinRatio(d);
        this.editExtendedSettings();
    }

    public void setNofCols(int i) {

        this.updateMatrixView();
    }

    public void setProbeWidth(int i) {
        CNVCATPropertiesMod.props().setProbeWidth(i);
        this.refreshMatrixView();
    }

    @Override
    public void setRelease(String _release) {
        super.setRelease(_release);
        this.chromLength = CytoBandManagerImpl.getChromLength(GenomeRelease.toRelease(this.release));


    }

    public CNVCATFrame(String release) {

        super(release);



        this.dispAberrations = new Vector<Aberration>();
        this.AberrationManager();
        this.CytoBandManager();
        this.ScoreFilterManager();



        initComponents();
        super.initFrame();
    //manager.initProject(this);

    //Dimension dim = getToolkit().getScreenSize();
    //this.setMaximumSize(new Dimension(dim.width * 1, (int) (dim.height * 0.5)));


    }

    public AberrationManager getAberrationManager() {
        return this.AberrationManager();
    }

    public void setAberrationManager(AberrationManager a) {
    }

    @Override
    public void initMatrix() {
        this.matrix = new MatrixAberration(this);
        this.matrix.setOpaque(true);
        this.matrix.setBackground(Color.white);

    }

    @Override
    public PlotPanel getMatrix() {
        return this.matrix;
    }

    @Override
    public JScrollPane getScrollPanelMatrix() {
        return this.jScrollPaneMatrix;
    }

    public int getNofCols() {
        return CNVCATPropertiesMod.props().getNofCols();
    }

    public void setTolerance(int i) {
        CNVCATPropertiesMod.props().setSelectionTolerance(i);
        this.refreshMatrixView();
    }

    public void setTransAberration(double d) {
        CNVCATPropertiesMod.props().setTransAberrations(d);
        //this.updateMatrixView();
        this.refreshMatrixView();
    }

    public void setTransFrequency(double d) {
        CNVCATPropertiesMod.props().setTransFrequencies(d);
        //this.updateMatrixView();
        this.refreshMatrixView();
    }

    public void setTransVariations(double d) {
        CNVCATPropertiesMod.props().setTransVariations(d);
        //this.updateMatrixView();
        this.refreshMatrixView();
    }

    int getMinSpotHeight() {
        return CNVCATPropertiesMod.props().getMinHeight();
    }

    public void setMinHeight(int i) {
        CNVCATPropertiesMod.props().setMinHeight(i);
        this.refreshMatrixView();
    }

    void setStatusBar(String string) {
        //this.progressBar.setString(string);
    }

    public void editExtendedSettings() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            //getManager().editExtendedSettings(null);
            AberrationManager().updateHidden(this.ScoreFilterManager());
        } catch (Exception e) {
            Logger.getLogger(CNVCATFrame.class.getName()).log(Level.WARNING, "", e);
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void writeFrequencies(int binsize, String filepath) throws Exception {
        //String filepath = "./test.txt";
        File outFile = new File(filepath);
        FileWriter out = null;
        try {
            out = new FileWriter(outFile);

        } catch (Exception exception) {

            Logger.getLogger(CNVCATFrame.class.getName()).log(Level.SEVERE, "ERROR: ", exception);

            throw exception;
        }
        boolean first = true;
        try {
            for (Enumeration e = this.getChromRBList(); e.hasMoreElements();) {
                AbstractButton d = (AbstractButton) e.nextElement();
                if (d.getActionCommand().contentEquals(BasicFrame.ALL_CHROMS)) {
                    continue;
                }
                this.chromId = d.getActionCommand();
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "writeFrequencies " + chromId);



                /*public void printFreqAberration(
                Graphics2D g, String chromId, int left,
                int right, int top, int fWidth, boolean fileout, int bin, FileWriter out, boolean first) {
                 */


                ((MatrixAberration) this.getMatrix()).printFreqAberration(
                        null, this.chromId, 0, 0, 0, 0, true, binsize, out, first);
                first = false;
            }

        } catch (Exception e) {
            //  JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "", e);
            throw e;
        }

        try {
            out.close();
        //System.out.println("DUP: " + sDup);
        //System.out.println("DEL: " + sDel);
        } catch (Exception ex) {
            Logger.getLogger(CNVCATFrame.class.getName()).log(Level.SEVERE, "ERROR: ", ex);
            throw ex;
            
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rbGroupView = new javax.swing.ButtonGroup();
        rbGroupChroms = new javax.swing.ButtonGroup();
        buttonGroupTransparency = new javax.swing.ButtonGroup();
        buttonGroupVariations = new javax.swing.ButtonGroup();
        listActiveCasesOld = (java.util.List<AberrationIds>)(java.beans.Beans.isDesignTime() || this.release == null ? java.util.Collections.emptyList() : AberrationManager().getActiveCases() );
        listDispAberrations = (java.util.List<AberrationCNVCAT>) (java.beans.Beans.isDesignTime() || this.release == null ? java.util.Collections.emptyList() : AberrationManager().getDispAberrations() );
        buttonGroupSelectedView = new javax.swing.ButtonGroup();
        jPanelTop = new javax.swing.JPanel();
        ModePanel = new javax.swing.JPanel();
        jRadioButtonViewStack = new javax.swing.JRadioButton();
        jRadioButtonViewTab = new javax.swing.JRadioButton();
        jRadioButtonViewFreq = new javax.swing.JRadioButton();
        TransparancyPanel = new javax.swing.JPanel();
        jRadioButtonTransNone = new javax.swing.JRadioButton();
        jRadioButtonTransRatio = new javax.swing.JRadioButton();
        jRadioButtonTransQuality = new javax.swing.JRadioButton();
        AnnotationPanel = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jPanelGenome = new javax.swing.JPanel();
        jComboBoxHistory = new javax.swing.JComboBox();
        jRadioButtonFullChrom = new javax.swing.JRadioButton();
        jRadioButtonRegion = new javax.swing.JRadioButton();
        jPanelMain = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        buttonAddActive = new javax.swing.JButton();
        buttonClearActive = new javax.swing.JButton();
        jButtonPrintFreq = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableData = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableHeader = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanelRight = new javax.swing.JPanel();
        jScrollPaneMatrix = new javax.swing.JScrollPane();
        jPanelPlot = new javax.swing.JPanel();
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
        jScrollPaneAnnotation = new javax.swing.JScrollPane();
        jPanelPlotAnno = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(650, 400));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jPanelTop.setMinimumSize(new java.awt.Dimension(800, 90));
        jPanelTop.setName("groupViewPanel"); // NOI18N
        jPanelTop.setPreferredSize(new java.awt.Dimension(800, 95));
        jPanelTop.setLayout(new java.awt.GridLayout(1, 1));

        ModePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Layout", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N
        ModePanel.setMinimumSize(new java.awt.Dimension(0, 0));
        ModePanel.setLayout(new javax.swing.BoxLayout(ModePanel, javax.swing.BoxLayout.Y_AXIS));

        rbGroupView.add(jRadioButtonViewStack);
        jRadioButtonViewStack.setSelected(true);
        jRadioButtonViewStack.setText("Stack");
        jRadioButtonViewStack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonViewStackActionPerformed(evt);
            }
        });
        ModePanel.add(jRadioButtonViewStack);

        rbGroupView.add(jRadioButtonViewTab);
        jRadioButtonViewTab.setText("Tabular");
        jRadioButtonViewTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonViewTabActionPerformed(evt);
            }
        });
        ModePanel.add(jRadioButtonViewTab);

        rbGroupView.add(jRadioButtonViewFreq);
        jRadioButtonViewFreq.setText("Frequency");
        jRadioButtonViewFreq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonViewFreqActionPerformed(evt);
            }
        });
        ModePanel.add(jRadioButtonViewFreq);

        jPanelTop.add(ModePanel);

        TransparancyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transparency", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N

        buttonGroupTransparency.add(jRadioButtonTransNone);
        jRadioButtonTransNone.setText("None");
        jRadioButtonTransNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonTransNoneActionPerformed(evt);
            }
        });

        buttonGroupTransparency.add(jRadioButtonTransRatio);
        jRadioButtonTransRatio.setSelected(true);
        jRadioButtonTransRatio.setText("by Ratio");
        jRadioButtonTransRatio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonTransRatioActionPerformed(evt);
            }
        });

        buttonGroupTransparency.add(jRadioButtonTransQuality);
        jRadioButtonTransQuality.setText("by Quality");
        jRadioButtonTransQuality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonTransQualityActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TransparancyPanelLayout = new javax.swing.GroupLayout(TransparancyPanel);
        TransparancyPanel.setLayout(TransparancyPanelLayout);
        TransparancyPanelLayout.setHorizontalGroup(
            TransparancyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TransparancyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TransparancyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonTransNone)
                    .addComponent(jRadioButtonTransRatio)
                    .addComponent(jRadioButtonTransQuality))
                .addContainerGap(149, Short.MAX_VALUE))
        );
        TransparancyPanelLayout.setVerticalGroup(
            TransparancyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TransparancyPanelLayout.createSequentialGroup()
                .addComponent(jRadioButtonTransNone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonTransRatio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonTransQuality))
        );

        jPanelTop.add(TransparancyPanel);

        AnnotationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Annotation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N

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

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel8.setText("add Annotation:");

    javax.swing.GroupLayout AnnotationPanelLayout = new javax.swing.GroupLayout(AnnotationPanel);
    AnnotationPanel.setLayout(AnnotationPanelLayout);
    AnnotationPanelLayout.setHorizontalGroup(
        AnnotationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(AnnotationPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(AnnotationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(15, Short.MAX_VALUE))
    );
    AnnotationPanelLayout.setVerticalGroup(
        AnnotationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(AnnotationPanelLayout.createSequentialGroup()
            .addGroup(AnnotationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(AnnotationPanelLayout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(28, Short.MAX_VALUE))
    );

    jPanelTop.add(AnnotationPanel);

    jPanelGenome.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "detail history", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), javax.swing.UIManager.getDefaults().getColor("Button.focus"))); // NOI18N
    jPanelGenome.setMaximumSize(new java.awt.Dimension(200, 100));
    jPanelGenome.setMinimumSize(new java.awt.Dimension(200, 100));
    jPanelGenome.setOpaque(false);

    jComboBoxHistory.setEditable(true);
    jComboBoxHistory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    jComboBoxHistory.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            jComboBoxHistoryItemStateChanged(evt);
        }
    });
    jComboBoxHistory.setEditable(true);

    buttonGroupSelectedView.add(jRadioButtonFullChrom);
    jRadioButtonFullChrom.setSelected(true);
    jRadioButtonFullChrom.setText("full chrom");

    buttonGroupSelectedView.add(jRadioButtonRegion);
    jRadioButtonRegion.setText("detail");
    jRadioButtonRegion.setEnabled(false);

    javax.swing.GroupLayout jPanelGenomeLayout = new javax.swing.GroupLayout(jPanelGenome);
    jPanelGenome.setLayout(jPanelGenomeLayout);
    jPanelGenomeLayout.setHorizontalGroup(
        jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanelGenomeLayout.createSequentialGroup()
            .addGroup(jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelGenomeLayout.createSequentialGroup()
                    .addComponent(jRadioButtonFullChrom)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jRadioButtonRegion))
                .addComponent(jComboBoxHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(69, 69, 69))
    );
    jPanelGenomeLayout.setVerticalGroup(
        jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGenomeLayout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jComboBoxHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanelGenomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jRadioButtonRegion)
                .addComponent(jRadioButtonFullChrom))
            .addGap(133, 133, 133))
    );

    /*JTextComponent editor = (JTextComponent) jComboBoxHistory.getEditor().getEditorComponent();
    // change the editor's document
    editor.setDocument(new RegionEditText(jComboBoxHistory));*/

    jPanelTop.add(jPanelGenome);

    add(jPanelTop);

    jPanelMain.setPreferredSize(new java.awt.Dimension(600, 550));
    jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.LINE_AXIS));

    jSplitPane1.setDividerLocation(420);
    jSplitPane1.setDividerSize(5);
    jSplitPane1.setMinimumSize(new java.awt.Dimension(600, 500));
    jSplitPane1.setOneTouchExpandable(true);
    jSplitPane1.setPreferredSize(new java.awt.Dimension(800, 600));

    buttonAddActive.setText("Add");
    buttonAddActive.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            buttonAddActiveActionPerformed(evt);
        }
    });

    buttonClearActive.setText("Remove");
    buttonClearActive.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            buttonClearActiveActionPerformed(evt);
        }
    });

    jButtonPrintFreq.setText("export Frequencies");
    jButtonPrintFreq.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonPrintFreqActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
            .addContainerGap(145, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jButtonPrintFreq, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(buttonAddActive, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonClearActive)))
            .addGap(111, 111, 111))
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(buttonAddActive, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonClearActive, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButtonPrintFreq)
            .addContainerGap())
    );

    jTabbedPane1.setBackground(new java.awt.Color(204, 204, 204));
    jTabbedPane1.setMinimumSize(new java.awt.Dimension(100, 400));

    jScrollPane2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    jTableData.setAutoCreateRowSorter(true);
    jTableData.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jTableData.getTableHeader().setReorderingAllowed(false);

    org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listDispAberrations, jTableData);
    org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${trackId}"));
    columnBinding.setColumnName("TrackID");
    columnBinding.setColumnClass(String.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${peakId}"));
    columnBinding.setColumnName("Peak Id");
    columnBinding.setColumnClass(String.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selected}"));
    columnBinding.setColumnName("Selected");
    columnBinding.setColumnClass(Boolean.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${chrom}"));
    columnBinding.setColumnName("Chrom");
    columnBinding.setColumnClass(String.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${chromEnd}"));
    columnBinding.setColumnName("Chrom End");
    columnBinding.setColumnClass(Long.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${chromStart}"));
    columnBinding.setColumnName("Chrom Start");
    columnBinding.setColumnClass(Long.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ratio}"));
    columnBinding.setColumnName("Ratio");
    columnBinding.setColumnClass(Double.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${quality}"));
    columnBinding.setColumnName("Quality");
    columnBinding.setColumnClass(Double.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${hidden}"));
    columnBinding.setColumnName("Hidden");
    columnBinding.setColumnClass(Boolean.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${XDispColumn}"));
    columnBinding.setColumnName("XDisp Column");
    columnBinding.setColumnClass(Integer.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${firstPeakId}"));
    columnBinding.setColumnName("First Peak Id");
    columnBinding.setColumnClass(String.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lastPeakId}"));
    columnBinding.setColumnName("Last Peak Id");
    columnBinding.setColumnClass(String.class);
    columnBinding.setEditable(false);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${count}"));
    columnBinding.setColumnName("Count");
    columnBinding.setColumnClass(Integer.class);
    columnBinding.setEditable(false);
    bindingGroup.addBinding(jTableBinding);
    jTableBinding.bind();
    jTableData.getModel().addTableModelListener(new TableModelListener() {

        public void tableChanged(TableModelEvent e) {

            int row = e.getFirstRow();
            int column = e.getColumn();
            //System.out.println("Data table source:  " + e.getSource() + " type " + e.getType() + " row " + row + " col " + column);

            CNVCATFrame.this.updateDispAberrations(e);
            CNVCATFrame.this.refreshMatrixView();
        }
    });
    jTableData.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            updateTableDataHandler(evt);
        }
    });
    jScrollPane2.setViewportView(jTableData);
    // selected Item propagating to tableHeader
    jTableData.getSelectionModel().addListSelectionListener(new DataListSelectionHandler());

    jTabbedPane1.addTab("Tracks", jScrollPane2);

    jTableHeader.setAutoCreateRowSorter(true);
    jTableHeader.setColumnSelectionAllowed(true);
    jTableHeader.setName("jTableHeader");
    jTableHeader.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

    org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${aberrationManager.activeCases}");
    jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableHeader);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${trackId}"));
    columnBinding.setColumnName("Track Id");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sampleNames}"));
    columnBinding.setColumnName("Sample Names");
    columnBinding.setColumnClass(java.util.List.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${phenotypes}"));
    columnBinding.setColumnName("Phenotypes");
    columnBinding.setColumnClass(java.util.List.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${paramAsString}"));
    columnBinding.setColumnName("Param As String");
    columnBinding.setColumnClass(String.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${color}"));
    columnBinding.setColumnName("Color");
    columnBinding.setColumnClass(java.awt.Color.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${countAberrations}"));
    columnBinding.setColumnName("Count Aberrations");
    columnBinding.setColumnClass(Integer.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${noHiddenCNV}"));
    columnBinding.setColumnName("No Hidden CNV");
    columnBinding.setColumnClass(Integer.class);
    columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${XDispColumn}"));
    columnBinding.setColumnName("XDisp Column");
    columnBinding.setColumnClass(Integer.class);
    bindingGroup.addBinding(jTableBinding);
    jTableBinding.bind();
    jTableHeader.getModel().addTableModelListener(new TableModelListener() {

        public void tableChanged(TableModelEvent e) {

            int row = e.getFirstRow();
            int column = e.getColumn();
            //System.out.println("source:  " + e.getSource() + " type " + e.getType() + " row " + row + " col " + column);
            //AberrationJFrame.this.updateDisplayMatrix();
        }
    });
    jTableHeader.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            jTableHeaderPropertyChange(evt);
        }
    });
    jTableHeader.setDefaultEditor(Color.class,  new ColorEditor());
    jTableHeader.setDefaultRenderer(Color.class, new ColorRenderer(true));
    jScrollPane1.setViewportView(jTableHeader);
    jTableHeader.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

    jTabbedPane1.addTab("Data", jScrollPane1);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 413, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 366, Short.MAX_VALUE)
    );

    jTabbedPane1.addTab("Regions", jPanel2);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    jSplitPane1.setLeftComponent(jPanel3);

    jSplitPane2.setDividerLocation(100);
    jSplitPane2.setDividerSize(5);
    jSplitPane2.setMinimumSize(new java.awt.Dimension(400, 550));
    jSplitPane2.setOneTouchExpandable(true);

    jPanelRight.setBackground(new java.awt.Color(255, 255, 255));
    jPanelRight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jPanelRight.setMaximumSize(new java.awt.Dimension(800, 550));
    jPanelRight.setMinimumSize(new java.awt.Dimension(300, 600));
    jPanelRight.setPreferredSize(new java.awt.Dimension(65, 550));
    jPanelRight.setLayout(new javax.swing.BoxLayout(jPanelRight, javax.swing.BoxLayout.LINE_AXIS));

    jScrollPaneMatrix.setBackground(new java.awt.Color(255, 255, 255));
    jScrollPaneMatrix.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPaneMatrix.setMaximumSize(new java.awt.Dimension(1000, 1000));
    jScrollPaneMatrix.setMinimumSize(new java.awt.Dimension(400, 500));

    jPanelPlot.setMinimumSize(new java.awt.Dimension(500, 500));
    jPanelPlot.setPreferredSize(new java.awt.Dimension(500, 800));
    jPanelPlot.setLayout(new javax.swing.BoxLayout(jPanelPlot, javax.swing.BoxLayout.LINE_AXIS));
    jScrollPaneMatrix.setViewportView(jPanelPlot);

    jPanelRight.add(jScrollPaneMatrix);

    jPanelChroms.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    jPanelChroms.setMaximumSize(new java.awt.Dimension(100, 32767));
    jPanelChroms.setMinimumSize(new java.awt.Dimension(100, 550));
    jPanelChroms.setPreferredSize(new java.awt.Dimension(61, 550));
    jPanelChroms.setLayout(new java.awt.GridLayout(25, 1));

    rbGroupChroms.add(rbChrom1);
    rbChrom1.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom1.setSelected(true);
    rbChrom1.setText("chr1");
    rbChrom1.setMaximumSize(new java.awt.Dimension(53, 23));
    rbChrom1.setMinimumSize(new java.awt.Dimension(53, 23));
    rbChrom1.setPreferredSize(new java.awt.Dimension(53, 23));
    rbChrom1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom1);

    rbGroupChroms.add(rbChrom2);
    rbChrom2.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom2.setText("chr2");
    rbChrom2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom2);

    rbGroupChroms.add(rbChrom3);
    rbChrom3.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom3.setText("chr3");
    rbChrom3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom3);

    rbGroupChroms.add(rbChrom4);
    rbChrom4.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom4.setText("chr4");
    rbChrom4.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom4);

    rbGroupChroms.add(rbChrom5);
    rbChrom5.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom5.setText("chr5");
    rbChrom5.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom5);

    rbGroupChroms.add(rbChrom6);
    rbChrom6.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom6.setText("chr6");
    rbChrom6.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom6);

    rbGroupChroms.add(rbChrom7);
    rbChrom7.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom7.setText("chr7");
    rbChrom7.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom7);

    rbGroupChroms.add(rbChrom8);
    rbChrom8.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom8.setText("chr8");
    rbChrom8.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom8);

    rbGroupChroms.add(rbChrom9);
    rbChrom9.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom9.setText("chr9");
    rbChrom9.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom9);

    rbGroupChroms.add(rbChrom10);
    rbChrom10.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom10.setText("chr10");
    rbChrom10.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom10);

    rbGroupChroms.add(rbChrom11);
    rbChrom11.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom11.setText("chr11");
    rbChrom11.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom11);

    rbGroupChroms.add(rbChrom12);
    rbChrom12.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom12.setText("chr12");
    rbChrom12.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom12);

    rbGroupChroms.add(rbChrom13);
    rbChrom13.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom13.setText("chr13");
    rbChrom13.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom13);

    rbGroupChroms.add(rbChrom14);
    rbChrom14.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom14.setText("chr14");
    rbChrom14.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom14);

    rbGroupChroms.add(rbChrom15);
    rbChrom15.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom15.setText("chr15");
    rbChrom15.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom15);

    rbGroupChroms.add(rbChrom16);
    rbChrom16.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom16.setText("chr16");
    rbChrom16.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom16);

    rbGroupChroms.add(rbChrom17);
    rbChrom17.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom17.setText("chr17");
    rbChrom17.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom17);

    rbGroupChroms.add(rbChrom18);
    rbChrom18.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom18.setText("chr18");
    rbChrom18.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom18);

    rbGroupChroms.add(rbChrom19);
    rbChrom19.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom19.setText("chr19");
    rbChrom19.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom19);

    rbGroupChroms.add(rbChrom20);
    rbChrom20.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom20.setText("chr20");
    rbChrom20.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom20);

    rbGroupChroms.add(rbChrom21);
    rbChrom21.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom21.setText("chr21");
    rbChrom21.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom21);

    rbGroupChroms.add(rbChrom22);
    rbChrom22.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChrom22.setText("chr22");
    rbChrom22.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChrom22);

    rbGroupChroms.add(rbChromX);
    rbChromX.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChromX.setText("chrX");
    rbChromX.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChromX);

    rbGroupChroms.add(rbChromY);
    rbChromY.setFont(new java.awt.Font("Dialog", 1, 14));
    rbChromY.setText("chrY");
    rbChromY.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            setRBMatrixAberration(evt);
        }
    });
    jPanelChroms.add(rbChromY);

    rbGroupChroms.add(rbAll);
    rbAll.setFont(new java.awt.Font("Dialog", 1, 14));
    rbAll.setText("ALL");
    rbAll.setActionCommand(this.ALL_CHROMS);
    rbAll.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbAllActionPerformed(evt);
        }
    });
    jPanelChroms.add(rbAll);

    jPanelRight.add(jPanelChroms);

    jSplitPane2.setRightComponent(jPanelRight);

    jScrollPaneAnnotation.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPaneAnnotation.setAlignmentX(0.0F);
    jScrollPaneAnnotation.setHorizontalScrollBar(null);
    jScrollPaneAnnotation.setMaximumSize(new java.awt.Dimension(500, 1000));
    jScrollPaneAnnotation.setMinimumSize(new java.awt.Dimension(100, 500));
    jScrollPaneAnnotation.setPreferredSize(new java.awt.Dimension(200, 800));

    jPanelPlotAnno.setMaximumSize(new java.awt.Dimension(0, 0));
    jPanelPlotAnno.setMinimumSize(new java.awt.Dimension(100, 500));
    jPanelPlotAnno.setPreferredSize(new java.awt.Dimension(200, 800));
    jPanelPlotAnno.setLayout(new javax.swing.BoxLayout(jPanelPlotAnno, javax.swing.BoxLayout.LINE_AXIS));
    jScrollPaneAnnotation.setViewportView(jPanelPlotAnno);

    jSplitPane2.setLeftComponent(jScrollPaneAnnotation);

    jSplitPane1.setRightComponent(jSplitPane2);

    jPanelMain.add(jSplitPane1);

    add(jPanelMain);

    bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void updateTableDataHandler(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_updateTableDataHandler
    System.out.println(evt.getPropertyName() + " " + evt.getSource().getClass());//GEN-LAST:event_updateTableDataHandler

    }

    void updateDispAberrations(TableModelEvent e) {
        int row = e.getLastRow();
        int col = e.getColumn();
        if (col != this.colSelected) {
            return;
        }
        if (this.AberrationManager() == null) {
            return;
        }
        TableModel t = this.jTableData.getModel();
        int rRow = this.jTableData.convertRowIndexToModel(row);
        if (t.getValueAt(row, col).equals(Boolean.TRUE)) {
            for (int i = 0; i < this.AberrationManager().getDispAberrations().size(); i++) {
                if (i != rRow) {
                    this.AberrationManager().getDispAberrations().get(i).setSelected(false);
                }
            }
        }

    }
private void setRBMatrixAberration(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setRBMatrixAberration
    JRadioButton b = (JRadioButton) evt.getSource();
    this.chromId = b.getActionCommand();
    super.setPosition(0);
    System.out.println("setRBMatrixAberration: " + this.chromId);
    this.updateMatrixView();
}//GEN-LAST:event_setRBMatrixAberration

private void jTableHeaderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTableHeaderPropertyChange
    this.updateMatrixView();// TODO add your handling code here:
}//GEN-LAST:event_jTableHeaderPropertyChange

private void jRadioButtonViewStackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonViewStackActionPerformed
    this.updateMatrixView();
}//GEN-LAST:event_jRadioButtonViewStackActionPerformed

private void jRadioButtonViewTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonViewTabActionPerformed
    this.updateMatrixView();
}//GEN-LAST:event_jRadioButtonViewTabActionPerformed

private void jRadioButtonViewFreqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonViewFreqActionPerformed
    this.updateMatrixView();
}//GEN-LAST:event_jRadioButtonViewFreqActionPerformed

private void jRadioButtonTransNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonTransNoneActionPerformed
    this.updateMatrixView();// TODO add your handling code here:
}//GEN-LAST:event_jRadioButtonTransNoneActionPerformed

private void jRadioButtonTransRatioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonTransRatioActionPerformed
    this.updateMatrixView();// TODO add your handling code here:
}//GEN-LAST:event_jRadioButtonTransRatioActionPerformed

private void jRadioButtonTransQualityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonTransQualityActionPerformed
    this.updateMatrixView();// TODO add your handling code here:
}//GEN-LAST:event_jRadioButtonTransQualityActionPerformed

private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
}//GEN-LAST:event_formWindowClosed

private void buttonAddActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActiveActionPerformed
    this.showAberrationFilter();
}//GEN-LAST:event_buttonAddActiveActionPerformed

private void buttonClearActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearActiveActionPerformed
    this.clearActiveCases();
}//GEN-LAST:event_buttonClearActiveActionPerformed

    private void jRadioButtonChromActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.jRadioButtonFullChrom.isSelected()) {

            this.rescaleView(true, this.chromId, 0, this.chromLength.get(this.chromId));
        }
    }
private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
    String anno = this.jComboBox2.getSelectedItem().toString();
    try {
        this.addAnnotation(anno);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));
    }
}//GEN-LAST:event_jComboBox2ActionPerformed

private void jComboBoxHistoryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxHistoryItemStateChanged
    setPositionFromHistory();
}//GEN-LAST:event_jComboBoxHistoryItemStateChanged

private void rbAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAllActionPerformed
    super.setAllChromsMatrix(evt);
}//GEN-LAST:event_rbAllActionPerformed

private void jButtonPrintFreqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintFreqActionPerformed
    ExportFrequenciesDialog.ExportFrequencies(this);
}//GEN-LAST:event_jButtonPrintFreqActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel AnnotationPanel;
    javax.swing.JPanel ModePanel;
    javax.swing.JPanel TransparancyPanel;
    javax.swing.JButton buttonAddActive;
    javax.swing.JButton buttonClearActive;
    javax.swing.ButtonGroup buttonGroupSelectedView;
    javax.swing.ButtonGroup buttonGroupTransparency;
    javax.swing.ButtonGroup buttonGroupVariations;
    javax.swing.JButton jButtonPrintFreq;
    javax.swing.JComboBox jComboBox2;
    javax.swing.JComboBox jComboBoxHistory;
    javax.swing.JLabel jLabel8;
    javax.swing.JPanel jPanel2;
    javax.swing.JPanel jPanel3;
    javax.swing.JPanel jPanel5;
    javax.swing.JPanel jPanelChroms;
    javax.swing.JPanel jPanelGenome;
    javax.swing.JPanel jPanelMain;
    javax.swing.JPanel jPanelPlot;
    javax.swing.JPanel jPanelPlotAnno;
    javax.swing.JPanel jPanelRight;
    javax.swing.JPanel jPanelTop;
    javax.swing.JRadioButton jRadioButtonFullChrom;
    javax.swing.JRadioButton jRadioButtonRegion;
    javax.swing.JRadioButton jRadioButtonTransNone;
    javax.swing.JRadioButton jRadioButtonTransQuality;
    javax.swing.JRadioButton jRadioButtonTransRatio;
    javax.swing.JRadioButton jRadioButtonViewFreq;
    javax.swing.JRadioButton jRadioButtonViewStack;
    javax.swing.JRadioButton jRadioButtonViewTab;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JScrollPane jScrollPaneAnnotation;
    javax.swing.JScrollPane jScrollPaneMatrix;
    javax.swing.JSplitPane jSplitPane1;
    javax.swing.JSplitPane jSplitPane2;
    javax.swing.JTabbedPane jTabbedPane1;
    javax.swing.JTable jTableData;
    javax.swing.JTable jTableHeader;
    public java.util.List<AberrationIds> listActiveCasesOld;
    private java.util.List<AberrationCNVCAT> listDispAberrations;
    javax.swing.JRadioButton rbAll;
    javax.swing.JRadioButton rbChrom1;
    javax.swing.JRadioButton rbChrom10;
    javax.swing.JRadioButton rbChrom11;
    javax.swing.JRadioButton rbChrom12;
    javax.swing.JRadioButton rbChrom13;
    javax.swing.JRadioButton rbChrom14;
    javax.swing.JRadioButton rbChrom15;
    javax.swing.JRadioButton rbChrom16;
    javax.swing.JRadioButton rbChrom17;
    javax.swing.JRadioButton rbChrom18;
    javax.swing.JRadioButton rbChrom19;
    javax.swing.JRadioButton rbChrom2;
    javax.swing.JRadioButton rbChrom20;
    javax.swing.JRadioButton rbChrom21;
    javax.swing.JRadioButton rbChrom22;
    javax.swing.JRadioButton rbChrom3;
    javax.swing.JRadioButton rbChrom4;
    javax.swing.JRadioButton rbChrom5;
    javax.swing.JRadioButton rbChrom6;
    javax.swing.JRadioButton rbChrom7;
    javax.swing.JRadioButton rbChrom8;
    javax.swing.JRadioButton rbChrom9;
    javax.swing.JRadioButton rbChromX;
    javax.swing.JRadioButton rbChromY;
    javax.swing.ButtonGroup rbGroupChroms;
    javax.swing.ButtonGroup rbGroupView;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    MatrixAberration matrix;
    private List<Aberration> dispAberrations;

    @Override
    public void showDetails(long firstPos, long secondPos) {
        super.showDetails(firstPos, secondPos);
        if (!isFullChrom()) {
            ((MatrixAberration) this.getMatrix()).viewAberrationInfo();

        }
    }

    // refresh view for current Chrom
    @Override
    public void refreshMatrixView() {
        super.refreshMatrixView();

        try {
           
            if (this.AberrationManager() == null) {
                return;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + (e.getMessage() != null ? e.getMessage() : "undefined"));

        }

    }

    @Override
    //kt 260912 refresh display aberrations
    public void setPositionFromHistory() {
        System.out.println("CNVCATFrame set History");
        super.setPositionFromHistory();
        if (!isFullChrom()) {
            this.matrix.viewAberrationInfo();
        }
    }
    // view new chrom

    @Override
    public void updateMatrixView() {

        super.updateMatrixView();
       

        if (this.AberrationManager() == null) {
            return;
        }
        AberrationManager().setDispAberrations(new Vector());
        // kt 101012
        Dimension d2 = this.getMatrix().getSize();
        this.getPlot().setPreferredSize(new Dimension((int) d2.getWidth(), (int) d2.getHeight()));


    }
// PUBLIC METHODS 

    /* Vector list = FilterAnalyzedChipDialog.getFilteredChipList(
    this, PropertiesDialog.host, PropertiesDialog.db, PropertiesDialog.user, PropertiesDialog.pwd);
    System.out.println("Result: " + list.toString());
    if (list != null && !list.isEmpty()) {
    //l.login();
    ExtractAberrationView ex = new ExtractAberrationView(this, list);
    ex.setVisible(true);
    
     */
    /*
    public void showAberrationFilterAdd() {
    
    try {
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    this.doShowAberrationFilter(this.release);
    } catch (Exception e) {
    Logger.getLogger(CNVCATFrame.class.getName()).log(Level.SEVERE,
    "", e);
    JOptionPane.showMessageDialog(this, "Error:" + e.getMessage());
    } finally {
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    // tableHeader is bound to aberrationManager.activeCases
    
    AberrationManager().updateHidden(this.ScoreFilterManager());
    
    
    
    this.updateMatrixView();
    }
     */
    public void showAberrationFilter() {

        try {

            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));


            FilterCNVCATDialog f = new FilterCNVCATDialog(this);
            f.setVisible(true);



        } catch (Exception e) {
            Logger.getLogger(CNVCATFrame.class.getName()).log(Level.SEVERE,
                    "", e);
            JOptionPane.showMessageDialog(this, "Error:" + e.getMessage());

        } finally {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        // tableHeader is bound to aberrationManager.activeCases



        this.updateMatrixView();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    /**
     * load cnv for selected Tracks
     * combine new selected cnv with formerly loaded cnv,
     * update cnv hiding state according to score filter
     */
    public void updateAberrationLists() {



        List<? extends AberrationIds> list =
                AberrationManager().conflictActiveCases(
                AberrationManager().getSelectedAberrationIds(),
                AberrationManager().getActiveCases());


        if (list == null) {
            AberrationManager().loadActiveCases(false);
        } else {

            int rc = JOptionPane.showConfirmDialog(this,
                    "\nFor at least one CaseId/Exctraction  the CNV are already loaded. " +
                    "\n To replace the CNV proceed with YES. " +
                    "\n To keep the older CNV proceed with NO. " +
                    "\n To cancel the loading hit CANCEL!",
                    "Replace CNV",
                    JOptionPane.YES_NO_CANCEL_OPTION);


            if (rc == JOptionPane.YES_OPTION) {
                AberrationManager().loadActiveCases(true);
            }
            if (rc == JOptionPane.NO_OPTION) {
                AberrationManager().loadActiveCases(false);
            }
        }
        AberrationManager().updateHidden(this.ScoreFilterManager());
    }

    public void clearActiveCases() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (this.AberrationManager() != null) {
            AberrationManager().clearActiveCases();
        }

        this.updateMatrixView();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void setDisplayModeNormal() {

        this.refreshMatrixView();
    }

    public void setDisplayModeStacked() {
        this.refreshMatrixView();

    }

    public void setDisplayModeTabular() {
        this.refreshMatrixView();

    }

    public void setDisplayModeFreq() {
        this.refreshMatrixView();
    }

    public void exportAberration() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            //
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting data: " +
                    e.getMessage());
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void deleteAberration() {

        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //deleteAberration(this);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " +
                    e.getMessage());
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        this.updateMatrixView();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    public void setTextStatusBar(String info) {
        //this.statusBar.setText(info);
    }

    public boolean isDisplayStacked() {
        return jRadioButtonViewStack.isSelected();
    }

    public boolean isDisplayTabular() {
        return jRadioButtonViewTab.isSelected();
    }

    boolean isDisplayFreq() {
        return jRadioButtonViewFreq.isSelected();
    }

    public boolean isTransNone() {
        return jRadioButtonTransNone.isSelected();
    }

    public boolean isTransRatio() {
        return jRadioButtonTransRatio.isSelected();
    }

    public boolean isTransQuality() {
        return jRadioButtonTransQuality.isSelected();
    }

    public void setTransparency() {
        this.refreshMatrixView();
    }

    public List<Aberration> getDispAberrations() {
        return dispAberrations;
    }

    public void setDispAberrations(List<Aberration> dispAberrations) {
        this.dispAberrations = dispAberrations;

    }

    class DataListSelectionHandler implements ListSelectionListener {

        String trackId;
        String param;
        Vector<Vector<String>> idSelected = new Vector<Vector<String>>();

        public void valueChanged(ListSelectionEvent e) {
            System.out.println("list selection event");
            idSelected.clear();
            int j;
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            // clear all selections
            if (jTableHeader.getRowCount() > 0) {
                jTableHeader.removeRowSelectionInterval(0, jTableHeader.getRowCount() - 1);
            }
            RowSorter rHeader = jTableHeader.getRowSorter();
            RowSorter rData = jTableData.getRowSorter();
            //boolean isAdjusting = e.getValueIsAdjusting();

            //System.out.println(jTableData.getModel().getColumnName(colID));
            //System.out.println(jTableData.getModel().getColumnName(colPhenotype));
            if (!lsm.isSelectionEmpty()) {

                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                int ii;
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        if (rData != null) {
                            ii = rData.convertRowIndexToModel(i);
                        } else {
                            ii = i;
                        }
                        Aberration a = (Aberration) CNVCATFrame.this.listDispAberrations.get(ii);

                        j = AberrationManager().getIndexAberrationId(a);
                        if (j < 0) {
                            continue;
                        }
                        if (rHeader != null) {
                            j = rHeader.convertRowIndexToView(j);
                        }
                        jTableHeader.addRowSelectionInterval(j, j);
                    }
                }
            }
        }
    }

    public void exportImage() {
        BufferedImage img = this.matrix.getImage();
        String path = NbPreferences.forModule(CNVCATFrame.class).get("pathPreference", "");
        JFileChooser fileExportChooser = new javax.swing.JFileChooser(path);
        fileExportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String[] extensions = ImageIO.getWriterFileSuffixes();
        fileExportChooser.setFileFilter(new FileNameExtensionFilter("Image files", extensions));
        int r = fileExportChooser.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fileExportChooser.getSelectedFile();

        try {
            String name = f.getName();
            String suffix = name.substring(name.lastIndexOf('.') + 1);
            if (suffix.compareToIgnoreCase("jpeg") == 0 || suffix.compareToIgnoreCase("jpg") == 0) {
                OutputStream output = new BufferedOutputStream(new FileOutputStream(f));
                //JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output);

                BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                ColorConvertOp conv = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);


                conv.filter(img, dest);
                img = dest;
            //encoder.encode( dest );   
            }
            ImageIO.write(img, suffix, f);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting image: " +
                    e.getMessage());
        }
        NbPreferences.forModule(CNVCATFrame.class).put("pathPreference", f.getName());
    }

    @Override
    public int getZoomY() {
        return super.getZoomY();
    }

    @Override
    public void load() {
        this.showAberrationFilter();
    }

    @Override
    public void initAnno() {
    }

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
    public JRadioButton getCBFullChrom() {
        return this.jRadioButtonFullChrom;
    }

    public JRadioButton getCBRegion() {
        return this.jRadioButtonRegion;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRulerHeight() {
        return 1;
    }

    public AberrationManager AberrationManager() {
        if (this.aberrationManager == null) {

            try {
                this.aberrationManager = new AberrationManagerCNVCAT();
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "", e);
                return null;
            }
        }
        return this.aberrationManager;
    }

    public ScoreFilterManager ScoreFilterManager() {
        if (this.scoreFilterManager == null) {
            if (this.release == null) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "no release set");
                return null;
            }
            try {
                this.scoreFilterManager = new ScoreFilterManager();

            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "", e);
            //JOptionPane.showMessageDialog(null, AberrationFrameBack.warnNoConnection);
            }
        }
        return this.scoreFilterManager;
    }

    public CytoBandManagerImpl CytoBandManager() {
        if (this.cytoBandManager == null) {
            if (this.release == null) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "no release set");
                return null;
            }
            try {

                this.cytoBandManager = new CytoBandManagerImpl(GenomeRelease.toRelease(release));
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "", e);
            //JOptionPane.showMessageDialog(null, " No Database Connection. Please set parameter at \"edit Properties\"");
            }
        }
        return this.cytoBandManager;
    }

    @Override
    public JComboBox getCBHistory() {
        return this.jComboBoxHistory;
    }

    @Override
    public Enumeration<AbstractButton> getChromRBList() {
        return rbGroupChroms.getElements();
    }
    static HashMap<String, String> colorlist = null;

    public String getColorName(Color c) {
        if (colorlist == null) {
            //URL colfileurl = getClass().getResource("colorlist.hex.txt");
            try {
                //String path = colfileurl.getPath();
                //URI colfileuri = colfileurl.toURI();
                File colfile = InstalledFileLocator.getDefault().locate(
                        "colorlisthex.txt", "org.molgen.genomeCATPro.peaks", false);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Colorlist: " + colfile.getAbsolutePath());

                //File colfile = new File(colfileurl.toURI());

                BufferedReader input = new BufferedReader(new FileReader(colfile));
                String line;


                colorlist = new HashMap<String, String>();


                while ((line = input.readLine()) != null) {
                    String[] par = line.split("\t");
                    colorlist.put(par[1], par[0]);
                }
                input.close();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "", ex);
            }
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Colorlist: " + colorlist.size());

        try {
            String rgb = Integer.toHexString(c.getRGB());
            System.out.println(rgb);
            rgb = rgb.substring(2, rgb.length()).toUpperCase();
            System.out.println(rgb);
            return colorlist.get(rgb);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "", e);
            return null;
        }
    }
}


