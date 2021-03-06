package org.molgen.genomeCATPro.peaks;
/**
 * @name CATFramePropsTopComponent
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>. The contents of
 * props file are subject to the terms of either the GNU General Public License
 * Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use props file
 * except in compliance with the License. You can obtain a copy of the License
 * at http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. props program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JColorChooser;

import org.molgen.genomeCATPro.guimodul.SwingUtils;
import org.molgen.genomeCATPro.peaks.cnvcat.CNVCATFrame;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 * Top component which displays something.
 * 281016 component closed empty (bug no how up after closing frame)
 */
final class CATFramePropsTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result result = null;
    CNVCATFrame frame;
    private static CATFramePropsTopComponent instance;
    private static final String PREFERRED_ID = "CATFramePropsTopComponent";

    private CATFramePropsTopComponent() {
        this.initComponents();
        this.initMyComponents();
        //jTextFieldMRNETAbr.setText(CNVCATPropertiesMod.props().getMrnetAbr());
        jTextFieldMaxQuality.setValue(CNVCATPropertiesMod.props().getMaxQuality());
        jTextFieldMaxRatio.setValue(CNVCATPropertiesMod.props().getMaxRatio());
        jTextFieldCNVWidth.setValue(CNVCATPropertiesMod.props().getProbeWidth());
        jTextTolerance.setValue(CNVCATPropertiesMod.props().getSelectionTolerance());
        this.fieldMinCNVHeight.setValue(CNVCATPropertiesMod.props().getMinHeight());
        this.jTextGAP.setValue(CNVCATPropertiesMod.props().getGap());
        this.jTextNofCols.setValue(CNVCATPropertiesMod.props().getNofCols());
        this.jButtonColorOverlap.setBackground(CNVCATPropertiesMod.props().getColorOverlap());
        //buttonColorBackground.setBackground(CNVCATPropertiesMod.props().getColorBackGround());
        this.jButtonColorSelected.setBackground(CNVCATPropertiesMod.props().getColorSelected());
        jTextMinRatio.setValue(CNVCATPropertiesMod.props().getMinRatio());
        jTextMinQuality.setValue(CNVCATPropertiesMod.props().getMinQuality());
        this.jSliderFrequency.setValue((int) (CNVCATPropertiesMod.props().getTransFrequencies() * 100));
        this.jButtonColorOverlap.setBackground(CNVCATPropertiesMod.props().getColorOverlap());
        this.jButtonColorSelected.setBackground(CNVCATPropertiesMod.props().getColorSelected());
        this.jSliderAberrations.setValue((int) (CNVCATPropertiesMod.props().getTransAberrations() * 100));
        //setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    @Override
    public void componentOpened() {

        Lookup.Template tpl = new Lookup.Template(CNVCATFrame.class);
        result = Utilities.actionsGlobalContext().lookup(tpl);
        result.addLookupListener(this);
    }

    @Override
    public void open() {
        Mode mode = WindowManager.getDefault().findMode("navigator");
        if (mode != null) {
            mode.dockInto(this);
            super.open();
        }

    }

    @Override
    public void componentClosed() {
        /*
        result.removeLookupListener(this);
        result = null;
        try {
            CNVCATPropertiesMod.save();
        } catch (Exception e) {
            Logger.getLogger(CATFramePropsTopComponent.class.getName()).log(
                    Level.INFO, "", e);
        }
*/
    }

    public void resultChanged(LookupEvent lookupEvent) {

        Lookup.Result r = (Lookup.Result) lookupEvent.getSource();
        Collection _c = r.allInstances();
        if (!_c.isEmpty()) {
            this.frame = null;
        }
        if (!_c.isEmpty()) {
            this.frame = (CNVCATFrame) _c.iterator().next();
        }
        Logger.getLogger(CATFramePropsTopComponent.class.getName()).log(
                Level.INFO, " result changed");
        this.initMyComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initMyComponents() {
        if (this.frame != null) {

            List<javax.swing.JComponent> alist = SwingUtils.getDescendantsOfType(javax.swing.JComponent.class, this);
            for (javax.swing.JComponent field : alist) {
                field.setEnabled(true);
            }
            frame.repaint();
        } else {

            List<javax.swing.JComponent> _list = SwingUtils.getDescendantsOfType(javax.swing.JComponent.class, this);

            for (javax.swing.JComponent field : _list) {
                field.setEnabled(false);
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupColorScale = new javax.swing.ButtonGroup();
        jButtonApply = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jTextMinRatio = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextMinQuality = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldMaxRatio = new javax.swing.JFormattedTextField();
        jTextFieldMaxQuality = new javax.swing.JFormattedTextField();
        jTextTolerance = new javax.swing.JFormattedTextField();
        jTextFieldCNVWidth = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        fieldMinCNVHeight = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextNofCols = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextGAP = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSliderFrequency = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jSliderAberrations = new javax.swing.JSlider();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButtonColorOverlap = new javax.swing.JButton();
        jButtonColorSelected = new javax.swing.JButton();

        setName(org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.name")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonApply, "refresh view");
        jButtonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setForeground(new java.awt.Color(255, 0, 0));

        jTextMinRatio.setForeground(new java.awt.Color(204, 0, 0));
        jTextMinRatio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jTextMinRatio.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextMinRatioPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "hide CNV with quality less than:");

        jTextMinQuality.setForeground(new java.awt.Color(204, 0, 0));
        jTextMinQuality.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jTextMinQuality.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextMinQualityPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(120, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jTextMinQuality, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                    .addComponent(jTextMinRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(104, 104, 104))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextMinQuality, jTextMinRatio});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jTextMinRatio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jLabel5)
                .addGap(27, 27, 27)
                .addComponent(jTextMinQuality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jTextFieldMaxRatio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jTextFieldMaxRatio.setMinimumSize(new java.awt.Dimension(50, 10));
        jTextFieldMaxRatio.setPreferredSize(new java.awt.Dimension(50, 20));
        jTextFieldMaxRatio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMaxRatioActionPerformed(evt);
            }
        });
        jTextFieldMaxRatio.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextFieldMaxRatioPropertyChange(evt);
            }
        });

        jTextFieldMaxQuality.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jTextFieldMaxQuality.setMinimumSize(new java.awt.Dimension(50, 10));
        jTextFieldMaxQuality.setPreferredSize(new java.awt.Dimension(50, 20));
        jTextFieldMaxQuality.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextFieldMaxQualityPropertyChange(evt);
            }
        });

        jTextTolerance.setMinimumSize(new java.awt.Dimension(50, 10));
        jTextTolerance.setPreferredSize(new java.awt.Dimension(50, 20));
        jTextTolerance.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextTolerancePropertyChange(evt);
            }
        });

        jTextFieldCNVWidth.setMinimumSize(new java.awt.Dimension(50, 10));
        jTextFieldCNVWidth.setPreferredSize(new java.awt.Dimension(50, 20));
        jTextFieldCNVWidth.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextFieldCNVWidthPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel9.text")); // NOI18N

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel12.text")); // NOI18N
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        fieldMinCNVHeight.setText(org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.fieldMinCNVHeight.text")); // NOI18N
        fieldMinCNVHeight.setPreferredSize(new java.awt.Dimension(10, 20));
        fieldMinCNVHeight.setValue(0);
        fieldMinCNVHeight.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldMinCNVHeightPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel2.text")); // NOI18N

        jTextNofCols.setMinimumSize(new java.awt.Dimension(50, 10));
        jTextNofCols.setPreferredSize(new java.awt.Dimension(50, 20));
        jTextNofCols.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextNofColsPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel13.text")); // NOI18N

        jTextGAP.setMinimumSize(new java.awt.Dimension(50, 10));
        jTextGAP.setPreferredSize(new java.awt.Dimension(50, 20));
        jTextGAP.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextGAPPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel9)
                        .addComponent(jLabel12))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel13)
                        .addComponent(jLabel6)
                        .addComponent(jLabel7)
                        .addComponent(jLabel2)
                        .addComponent(jLabel8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldMaxRatio, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jTextFieldMaxQuality, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jTextNofCols, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jTextGAP, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jTextFieldCNVWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldMinCNVHeight, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jTextTolerance, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldMinCNVHeight, jTextFieldCNVWidth, jTextFieldMaxQuality, jTextFieldMaxRatio, jTextGAP, jTextNofCols, jTextTolerance});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldMaxRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldMaxQuality, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jTextNofCols, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13)
                    .addComponent(jTextGAP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldCNVWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(fieldMinCNVHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(jTextTolerance, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel4.text")); // NOI18N
        jPanel3.add(jLabel4);

        jSliderFrequency.setPaintLabels(true);
        jSliderFrequency.setPaintTicks(true);
        jSliderFrequency.setValue(10);
        jSliderFrequency.setPreferredSize(new java.awt.Dimension(200, 15));
        jSliderFrequency.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFrequencyStateChanged(evt);
            }
        });
        jSliderFrequency.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSliderFrequencyPropertyChange(evt);
            }
        });
        jPanel3.add(jSliderFrequency);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel1.text")); // NOI18N
        jPanel3.add(jLabel1);

        jSliderAberrations.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderAberrationsStateChanged(evt);
            }
        });
        jSliderAberrations.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSliderAberrationsPropertyChange(evt);
            }
        });
        jPanel3.add(jSliderAberrations);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jLabel11.text")); // NOI18N

        jButtonColorOverlap.setBackground(CNVCATPropertiesMod.props().getColorOverlap());
        org.openide.awt.Mnemonics.setLocalizedText(jButtonColorOverlap, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jButtonColorOverlap.text")); // NOI18N
        jButtonColorOverlap.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonColorOverlap.setMinimumSize(new java.awt.Dimension(34, 20));
        jButtonColorOverlap.setPreferredSize(new java.awt.Dimension(34, 20));
        jButtonColorOverlap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorOverlapActionPerformed(evt);
            }
        });

        jButtonColorSelected.setBackground(CNVCATPropertiesMod.props().getColorSelected());
        org.openide.awt.Mnemonics.setLocalizedText(jButtonColorSelected, org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jButtonColorSelected.text")); // NOI18N
        jButtonColorSelected.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonColorSelected.setMinimumSize(new java.awt.Dimension(34, 20));
        jButtonColorSelected.setPreferredSize(new java.awt.Dimension(34, 20));
        jButtonColorSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorSelectedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonColorSelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonColorOverlap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonColorOverlap, jButtonColorSelected});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jButtonColorOverlap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(jButtonColorSelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(160, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonColorOverlap, jButtonColorSelected});

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CATFramePropsTopComponent.class, "CATFramePropsTopComponent.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addComponent(jButtonApply))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonApply)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jButtonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApplyActionPerformed

    if (this.frame == null) {
        return;
    }
    this.frame.updateMatrixView();
}//GEN-LAST:event_jButtonApplyActionPerformed

private void jTextFieldMaxRatioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMaxRatioActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextFieldMaxRatioActionPerformed
    public void chooseColorOverlap() {

        if (this.frame == null) {
            return;
        }
        frame.setColorOverlap(
                JColorChooser.showDialog(
                        this, "Pick a Color", this.jButtonColorOverlap.getBackground()));

        this.jButtonColorOverlap.setBackground(CNVCATPropertiesMod.props().getColorOverlap());

    }

    public void chooseColorSelected() {
        if (this.frame == null) {
            return;
        }

        frame.setColorSelected(
                JColorChooser.showDialog(this,
                        "Pick a Color", this.jButtonColorSelected.getBackground()));
        this.jButtonColorSelected.setBackground(CNVCATPropertiesMod.props().getColorSelected());

    }

private void jTextFieldMaxRatioPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextFieldMaxRatioPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setMaxRatio(((Number) this.jTextFieldMaxRatio.getValue()).doubleValue());

}//GEN-LAST:event_jTextFieldMaxRatioPropertyChange

private void jTextFieldMaxQualityPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextFieldMaxQualityPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setMaxQuality(((Number) this.jTextFieldMaxQuality.getValue()).doubleValue());

}//GEN-LAST:event_jTextFieldMaxQualityPropertyChange

private void jTextTolerancePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextTolerancePropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setTolerance(((Number) this.jTextTolerance.getValue()).intValue());

}//GEN-LAST:event_jTextTolerancePropertyChange

private void jTextFieldCNVWidthPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextFieldCNVWidthPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setProbeWidth(((Number) this.jTextFieldCNVWidth.getValue()).intValue());

}//GEN-LAST:event_jTextFieldCNVWidthPropertyChange

private void jTextMinRatioPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextMinRatioPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setMinRatio(((Number) this.jTextMinRatio.getValue()).doubleValue());

}//GEN-LAST:event_jTextMinRatioPropertyChange

private void jTextMinQualityPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextMinQualityPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setMinQuality(((Number) this.jTextMinQuality.getValue()).doubleValue());

}//GEN-LAST:event_jTextMinQualityPropertyChange

private void jSliderFrequencyPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSliderFrequencyPropertyChange
    System.out.println("change slider freq");
    if (this.frame == null) {
        return;
    }
    System.out.println("change slider freq");
    this.frame.setTransFrequency(((double) this.jSliderFrequency.getValue()) / 100);

}//GEN-LAST:event_jSliderFrequencyPropertyChange

private void jSliderAberrationsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSliderAberrationsPropertyChange
    System.out.println("change slider abberation");
    if (this.frame == null) {
        return;
    }
    System.out.println("change slider aberration");
    this.frame.setTransAberration(((double) this.jSliderAberrations.getValue()) / 100);

}//GEN-LAST:event_jSliderAberrationsPropertyChange

private void jButtonColorOverlapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorOverlapActionPerformed
    this.chooseColorOverlap();
}//GEN-LAST:event_jButtonColorOverlapActionPerformed

private void jButtonColorSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorSelectedActionPerformed
    this.chooseColorSelected();
}//GEN-LAST:event_jButtonColorSelectedActionPerformed

private void fieldMinCNVHeightPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldMinCNVHeightPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setMinHeight(((Number) this.fieldMinCNVHeight.getValue()).intValue());
}//GEN-LAST:event_fieldMinCNVHeightPropertyChange

private void jTextNofColsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextNofColsPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setNofCols(((Number) this.jTextNofCols.getValue()).intValue());
}//GEN-LAST:event_jTextNofColsPropertyChange

private void jTextGAPPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextGAPPropertyChange
    if (this.frame == null) {
        return;
    }
    this.frame.setGap(((Number) this.jTextGAP.getValue()).intValue());
}//GEN-LAST:event_jTextGAPPropertyChange

private void jSliderFrequencyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderFrequencyStateChanged
    System.out.println("change slider freq");
    if (this.frame == null) {
        return;
    }
    System.out.println("change slider freq");
    this.frame.setTransFrequency(((double) this.jSliderFrequency.getValue()) / 100);

}//GEN-LAST:event_jSliderFrequencyStateChanged

private void jSliderAberrationsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderAberrationsStateChanged
    System.out.println("change slider abberation");
    if (this.frame == null) {
        return;
    }
    System.out.println("change slider aberration");
    this.frame.setTransAberration(((double) this.jSliderAberrations.getValue()) / 100);

}//GEN-LAST:event_jSliderAberrationsStateChanged

    public CNVCATFrame getFrame() {
        return frame;
    }

    public void setFrame(CNVCATFrame frame) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupColorScale;
    private javax.swing.JFormattedTextField fieldMinCNVHeight;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JButton jButtonColorOverlap;
    private javax.swing.JButton jButtonColorSelected;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSlider jSliderAberrations;
    private javax.swing.JSlider jSliderFrequency;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JFormattedTextField jTextFieldCNVWidth;
    private javax.swing.JFormattedTextField jTextFieldMaxQuality;
    private javax.swing.JFormattedTextField jTextFieldMaxRatio;
    private javax.swing.JFormattedTextField jTextGAP;
    private javax.swing.JFormattedTextField jTextMinQuality;
    private javax.swing.JFormattedTextField jTextMinRatio;
    private javax.swing.JFormattedTextField jTextNofCols;
    private javax.swing.JFormattedTextField jTextTolerance;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link findInstance}.
     */
    public static synchronized CATFramePropsTopComponent getDefault() {
        if (instance == null) {
            instance = new CATFramePropsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ArrayFramePropsTopComponent instance. Never call
     * {@link #getDefault} directly!
     */
    public static synchronized CATFramePropsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(CATFramePropsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof CATFramePropsTopComponent) {
            return (CATFramePropsTopComponent) win;
        }
        Logger.getLogger(CATFramePropsTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();

    }

    @Override
    public void requestVisible() {
        Mode mode = WindowManager.getDefault().findMode("navigator");
        if (mode != null) {
            mode.dockInto(this);
            this.open();
        }
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    /**
     * replaces this in object stream
     */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return CATFramePropsTopComponent.getDefault();
        }
    }
}
