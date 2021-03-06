package org.molgen.genomeCATPro.guimodul.cghpro;

/**
 * @name CGHPROFramePropsTopComponent
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import org.molgen.genomeCATPro.cghpro.chip.Chip;
import org.molgen.genomeCATPro.cghpro.chip.ChipFeature;
import org.molgen.genomeCATPro.guimodul.SwingUtils;
import org.molgen.genomeCATPro.guimodul.util.MyImageButtonCellEditor;
import org.molgen.genomeCATPro.guimodul.util.MyImageButtonCellRenderer;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

@TopComponent.Description(
        preferredID = "CGHPROFramePropsTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)

@TopComponent.Registration(mode = "navigator", openAtStartup = false)
/**
 * 281016 kt component closed empty (bug no how up after closing frame)
 *
 * Top component which displays something.
 */
final class CGHPROFramePropsTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result result = null;
    CGHPROFrame frame;
    private static final String PREFERRED_ID = "CGHPROFramePropsTopComponent";
    private static CGHPROFramePropsTopComponent instance;
    private final PropertyChangeListener loadedAnnosListener = new ListenerForAnnotations();

    private CGHPROFramePropsTopComponent() {

        this.initComponents();
        this.initMyComponents();
        setName(NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CTL_CGHPROFramePropsTopComponent"));
        setToolTipText(NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "HINT_CGHPROFramePropsTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    @Override
    public void componentOpened() {

        Lookup.Template<CGHPROFrame> tpl = new Lookup.Template<>(CGHPROFrame.class);
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
        /*
    
         //result = Utilities.actionsGlobalContext().lookup(tpl);
         //funktioniert nicht ^ , da this window zum active window wird - deshalb
         //lookup result verändert
         TopComponent t = WindowManager.getDefault().findTopComponent("CGHProTopComponent");
    
         if (t != null) {
         result = Utilities.actionsGlobalContext().lookup(tpl);
         //result = t.getLookup().lookup(tpl);
         result.addLookupListener(this);
         }
         else
         Logger.getLogger(CGHProTopComponent.class.getName()).log(
         Level.WARNING, " found no CGHProTop to lookup");
         */
    }

    @Override
    public void componentClosed() {
       /*
        result.removeLookupListener(this);
        result = null;
        */
    }

    public void resultChanged(LookupEvent lookupEvent) {

        Lookup.Result r = (Lookup.Result) lookupEvent.getSource();
        Collection _c = r.allInstances();
        if (!_c.isEmpty()) {
            this.frame = null;
        }
        if (!_c.isEmpty()) {
            this.frame = (CGHPROFrame) _c.iterator().next();
        }
        Logger.getLogger(CGHProTopComponent.class.getName()).log(
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
            this.frame.addPropertyChangeListener(this.loadedAnnosListener);
            this.setListAnno(this.getAnnotationList());
            this.fieldNegLine.setValue(this.frame.getNegThresholdLine());
            this.fieldPosLine.setValue(this.frame.getPosThresholdLine());
            fieldThresholdLoss.setValue(this.frame.getThresholdLoss());
            fieldThresholdGain.setValue(this.frame.getThresholdGain());
            jComboBoxChips.setModel(this.initChipList());

            jButtonColorAbbNegSpot.setBackground(this.frame.getAbberantNegColor());
            jButtonColorAbbPosSpot.setBackground(this.frame.getAbberantPosColor());
            jButtonColorChip1.setBackground(this.frame.getChip1Color());
            jButtonColorChip2.setBackground(this.frame.getChip2Color());
            jButtonColorTrack.setBackground(this.frame.getChipTrackColor());
            fieldSpotWidth.setValue(this.frame.getSpotWidth());
            fieldMinSpotHeight.setValue(this.frame.getMinSpotHeight());
            fieldAnnoWidth.setValue(this.frame.getPlotAnnoWidth());
            fieldPlotWidth.setValue(this.frame.getPlotPanelWidth());
            fieldPlotHeight.setValue(this.frame.getPlotPanelHeight());
            fieldAnnoGap.setValue(this.frame.getPlotAnnoGap());
            List<javax.swing.JComponent> alist = SwingUtils.getDescendantsOfType(javax.swing.JComponent.class, this);
            for (javax.swing.JComponent field : alist) {
                field.setEnabled(true);
            }
        } else {
            this.setListAnno(new Vector<AnnotationListOrdered>());
            this.fieldNegLine.setValue(0);
            this.fieldPosLine.setValue(0);
            fieldThresholdLoss.setValue(0);
            fieldThresholdGain.setValue(0);
            jComboBoxChips.setModel(new DefaultComboBoxModel());

            jButtonColorAbbNegSpot.setBackground(java.awt.Color.white);

            jButtonColorAbbPosSpot.setBackground(java.awt.Color.white);
            jButtonColorChip1.setBackground(java.awt.Color.white);
            jButtonColorChip2.setBackground(java.awt.Color.white);
            jButtonColorTrack.setBackground(java.awt.Color.white);
            fieldSpotWidth.setValue(0);
            fieldMinSpotHeight.setValue(0);
            fieldAnnoWidth.setValue(0);
            fieldPlotWidth.setValue(0);
            fieldPlotHeight.setValue(0);
            fieldAnnoGap.setValue(0);

            List<javax.swing.JComponent> _list = SwingUtils.getDescendantsOfType(javax.swing.JComponent.class, this);

            for (javax.swing.JComponent field : _list) {
                field.setEnabled(false);
            }
        }
    }

    public List<AnnotationListOrdered> getAnnotationList() {
        List<AnnotationListOrdered> list = new Vector<AnnotationListOrdered>();
        for (Integer no : frame.getListAnnotation().keySet()) {
            AnnotationListOrdered a = new AnnotationListOrdered(
                    frame.getListAnnotation().get(no),
                    no);
            Logger.getLogger(CGHPROFramePropsTopComponent.class.getName()).log(Level.INFO,
                    "add: " + a.getNo() + a.toString());
            list.add(a);
        }
        return list;
    }

    //String[] cols = new String[]{"No", "Name", "Color", "ColorSchema"};
    public void setListAnno(List<AnnotationListOrdered> list) {
        this.listA.clear();
        this.listA.addAll(list);
        //this.jTableListAnnotation.getRowSorter().toggleSortOrder(1);

        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));

        this.jTableListAnnotation.getRowSorter().setSortKeys(sortKeys);

    }

    private class ListenerForAnnotations implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (CGHPROFrame.PROP_CHANGE_ANNO.equals(evt.getPropertyName())) {
                Logger.getLogger(CGHPROFramePropsTopComponent.class.getName()).log(Level.INFO,
                        "ListenerForAnnotations");
                if (frame != null) {
                    setListAnno(getAnnotationList());
                } else {
                    setListAnno(new Vector<AnnotationListOrdered>());
                    //ic.set(newList, null);
                }
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        listA = (List<AnnotationListOrdered>) (java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() :  org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<AnnotationListOrdered>()) );
        Color = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        fieldNegLine = new javax.swing.JFormattedTextField();
        fieldPosLine = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        fieldThresholdLoss = new javax.swing.JFormattedTextField();
        fieldThresholdGain = new javax.swing.JFormattedTextField();
        jComboBoxChips = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButtonColorAbbNegSpot = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButtonColorAbbPosSpot = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButtonColorChip1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButtonColorChip2 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jButtonColorTrack = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        fieldSpotWidth = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        fieldMinSpotHeight = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        fieldAnnoWidth = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        fieldPlotWidth = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        fieldPlotHeight = new javax.swing.JFormattedTextField();
        fieldAnnoGap = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        jPanelAnno = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableListAnnotation = new javax.swing.JTable();
        jButtonApply = new javax.swing.JButton();

        setName("Properties CGHPro "); // NOI18N

        Color.setMinimumSize(new java.awt.Dimension(90, 80));
        Color.setPreferredSize(new java.awt.Dimension(100, 100));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jPanel4.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel13.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel14.text")); // NOI18N

        fieldNegLine.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        fieldNegLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldNegLinePropertyChange(evt);
            }
        });

        fieldPosLine.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        fieldPosLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldPosLinePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldPosLine, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNegLine, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(fieldNegLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPosLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jPanel5.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel11.text")); // NOI18N

        fieldThresholdLoss.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        fieldThresholdLoss.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldThresholdLoss.text")); // NOI18N
        fieldThresholdLoss.setValue(0);
        fieldThresholdLoss.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldThresholdLossPropertyChange(evt);
            }
        });

        fieldThresholdGain.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        fieldThresholdGain.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldThresholdGain.text")); // NOI18N
        fieldThresholdGain.setValue(0);
        fieldThresholdGain.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldThresholdGainPropertyChange(evt);
            }
        });

        jComboBoxChips.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxChipsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxChips, 0, 215, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fieldThresholdLoss)
                            .addComponent(fieldThresholdGain, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(fieldThresholdLoss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(fieldThresholdGain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxChips, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        Color.addTab(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel2.text")); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButtonColorAbbNegSpot.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jButtonColorAbbNegSpot, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jButtonColorAbbNegSpot.text")); // NOI18N
        jButtonColorAbbNegSpot.setMaximumSize(new java.awt.Dimension(30, 10));
        jButtonColorAbbNegSpot.setMinimumSize(new java.awt.Dimension(30, 10));
        jButtonColorAbbNegSpot.setPreferredSize(new java.awt.Dimension(10, 20));
        jButtonColorAbbNegSpot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorAbbNegSpotActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel3.text")); // NOI18N
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButtonColorAbbPosSpot.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jButtonColorAbbPosSpot, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jButtonColorAbbPosSpot.text")); // NOI18N
        jButtonColorAbbPosSpot.setMaximumSize(new java.awt.Dimension(30, 10));
        jButtonColorAbbPosSpot.setMinimumSize(new java.awt.Dimension(30, 10));
        jButtonColorAbbPosSpot.setPreferredSize(new java.awt.Dimension(10, 20));
        jButtonColorAbbPosSpot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorAbbPosSpotActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel4.text")); // NOI18N
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButtonColorChip1.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jButtonColorChip1, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jButtonColorChip1.text")); // NOI18N
        jButtonColorChip1.setMaximumSize(new java.awt.Dimension(30, 10));
        jButtonColorChip1.setMinimumSize(new java.awt.Dimension(30, 10));
        jButtonColorChip1.setPreferredSize(new java.awt.Dimension(10, 20));
        jButtonColorChip1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorChip1ActionPerformed(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel5.text")); // NOI18N
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButtonColorChip2.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jButtonColorChip2, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jButtonColorChip2.text")); // NOI18N
        jButtonColorChip2.setMaximumSize(new java.awt.Dimension(30, 10));
        jButtonColorChip2.setMinimumSize(new java.awt.Dimension(30, 10));
        jButtonColorChip2.setPreferredSize(new java.awt.Dimension(10, 20));
        jButtonColorChip2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorChip2ActionPerformed(evt);
            }
        });

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel12.text")); // NOI18N
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButtonColorTrack.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jButtonColorTrack, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jButtonColorTrack.text")); // NOI18N
        jButtonColorTrack.setPreferredSize(new java.awt.Dimension(10, 20));
        jButtonColorTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorTrackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonColorTrack, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonColorAbbNegSpot, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jButtonColorAbbPosSpot, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jButtonColorChip1, 0, 0, Short.MAX_VALUE)
                    .addComponent(jButtonColorChip2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonColorAbbNegSpot, jButtonColorAbbPosSpot, jButtonColorChip1, jButtonColorChip2, jButtonColorTrack});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jButtonColorAbbNegSpot, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(jButtonColorAbbPosSpot, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(jButtonColorChip1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jButtonColorChip2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(jButtonColorTrack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonColorAbbNegSpot, jButtonColorAbbPosSpot, jButtonColorChip1, jButtonColorChip2, jButtonColorTrack});

        Color.addTab(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel1.text")); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        fieldSpotWidth.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fieldSpotWidth.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldSpotWidth.text")); // NOI18N
        fieldSpotWidth.setPreferredSize(new java.awt.Dimension(10, 20));
        fieldSpotWidth.setValue(0);
        fieldSpotWidth.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldSpotWidthPropertyChange(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel6.text")); // NOI18N
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        fieldMinSpotHeight.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fieldMinSpotHeight.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldMinSpotHeight.text")); // NOI18N
        fieldMinSpotHeight.setPreferredSize(new java.awt.Dimension(10, 20));
        fieldMinSpotHeight.setValue(0);
        fieldMinSpotHeight.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldMinSpotHeightPropertyChange(evt);
            }
        });

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel7.text")); // NOI18N
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        fieldAnnoWidth.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fieldAnnoWidth.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldAnnoWidth.text")); // NOI18N
        fieldAnnoWidth.setPreferredSize(new java.awt.Dimension(10, 20));
        fieldAnnoWidth.setValue(0);
        fieldAnnoWidth.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldAnnoWidthPropertyChange(evt);
            }
        });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel8.text")); // NOI18N
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        fieldPlotWidth.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fieldPlotWidth.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldPlotWidth.text")); // NOI18N
        fieldPlotWidth.setPreferredSize(new java.awt.Dimension(10, 20));
        fieldPlotWidth.setValue(0);
        fieldPlotWidth.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldPlotWidthPropertyChange(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel9.text")); // NOI18N
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        fieldPlotHeight.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fieldPlotHeight.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldPlotHeight.text")); // NOI18N
        fieldPlotHeight.setPreferredSize(new java.awt.Dimension(10, 20));
        fieldPlotHeight.setValue(0);
        fieldPlotHeight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fieldPlotHeightMousePressed(evt);
            }
        });
        fieldPlotHeight.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldPlotHeightPropertyChange(evt);
            }
        });

        fieldAnnoGap.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fieldAnnoGap.setText(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.fieldAnnoGap.text")); // NOI18N
        fieldAnnoGap.setPreferredSize(new java.awt.Dimension(10, 20));
        fieldAnnoGap.setValue(0);
        fieldAnnoGap.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldAnnoGapPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel15.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(fieldAnnoGap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldSpotWidth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(fieldMinSpotHeight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(fieldPlotHeight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(fieldAnnoWidth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldPlotWidth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(87, 87, 87))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldAnnoWidth, fieldMinSpotHeight, fieldPlotHeight, fieldPlotWidth, fieldSpotWidth});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(fieldSpotWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(fieldMinSpotHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(fieldPlotHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(fieldPlotWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(fieldAnnoWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(fieldAnnoGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58))
        );

        jLabel9.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jLabel9.AccessibleContext.accessibleName")); // NOI18N

        Color.addTab(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jTableListAnnotation.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listA, jTableListAnnotation);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${schema}"));
        columnBinding.setColumnName("Schema");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${alphaNo}"));
        columnBinding.setColumnName("Alpha No");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${icon}"));
        columnBinding.setColumnName("Icon");
        columnBinding.setColumnClass(java.awt.Image.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(jTableListAnnotation);
        jTableListAnnotation.getColumnModel().getColumn(0).setMinWidth(0);
        jTableListAnnotation.getColumnModel().getColumn(0).setPreferredWidth(0);
        jTableListAnnotation.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableListAnnotation.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jTableListAnnotation.columnModel.title4_1")); // NOI18N
        jTableListAnnotation.getColumnModel().getColumn(0).setCellEditor(new MyImageButtonCellEditor(4));
        jTableListAnnotation.getColumnModel().getColumn(1).setResizable(false);
        jTableListAnnotation.getColumnModel().getColumn(1).setPreferredWidth(20);
        jTableListAnnotation.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jTableListAnnotation.columnModel.title0")); // NOI18N
        jTableListAnnotation.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jTableListAnnotation.columnModel.title1")); // NOI18N
        jTableListAnnotation.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jTableListAnnotation.columnModel.title2")); // NOI18N
        jTableListAnnotation.getColumnModel().getColumn(4).setResizable(false);
        jTableListAnnotation.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTableListAnnotation.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jTableListAnnotation.columnModel.title3")); // NOI18N
        jTableListAnnotation.getColumnModel().getColumn(4).setCellEditor(new MyImageButtonCellEditor(0));
        jTableListAnnotation.getColumnModel().getColumn(4).setCellRenderer(new MyImageButtonCellRenderer());
        TableRowSorter sorter = new TableRowSorter(
            jTableListAnnotation.getModel());

        jTableListAnnotation.setRowSorter(sorter);
        sorter.setComparator( 1, new Comparator<String>() {
            @Override public int compare( String s1, String s2 )
            {
                return s1.compareTo(s2);
            }
        } );

        javax.swing.GroupLayout jPanelAnnoLayout = new javax.swing.GroupLayout(jPanelAnno);
        jPanelAnno.setLayout(jPanelAnnoLayout);
        jPanelAnnoLayout.setHorizontalGroup(
            jPanelAnnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAnnoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelAnnoLayout.setVerticalGroup(
            jPanelAnnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAnnoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Color.addTab(org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jPanelAnno.TabConstraints.tabTitle"), jPanelAnno); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonApply, org.openide.util.NbBundle.getMessage(CGHPROFramePropsTopComponent.class, "CGHPROFramePropsTopComponent.jButtonApply.text")); // NOI18N
        jButtonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButtonApply, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
            .addComponent(Color, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButtonApply)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Color, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonColorAbbNegSpotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorAbbNegSpotActionPerformed

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setAbberantNegColor(
            JColorChooser.showDialog(
                    this,
                    "Pick a Color", this.jButtonColorAbbNegSpot.getBackground()));
    this.jButtonColorAbbNegSpot.setBackground(this.frame.getAbberantNegColor());
}//GEN-LAST:event_jButtonColorAbbNegSpotActionPerformed

private void jButtonColorAbbPosSpotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorAbbPosSpotActionPerformed

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }

    this.frame.setAberrantPosColor(
            JColorChooser.showDialog(
                    this,
                    "Pick a Color", this.jButtonColorAbbPosSpot.getBackground()));
    this.jButtonColorAbbPosSpot.setBackground(this.frame.getAbberantPosColor());
}//GEN-LAST:event_jButtonColorAbbPosSpotActionPerformed

private void jButtonColorChip1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorChip1ActionPerformed

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }

    this.frame.setChip1Color(
            JColorChooser.showDialog(
                    this,
                    "Pick a Color", this.jButtonColorChip1.getBackground()));
    this.jButtonColorChip1.setBackground(this.frame.getChip1Color());
}//GEN-LAST:event_jButtonColorChip1ActionPerformed

private void jButtonColorChip2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorChip2ActionPerformed

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }

    this.frame.setChip2Color(
            JColorChooser.showDialog(
                    this,
                    "Pick a Color", this.jButtonColorChip2.getBackground()));
    this.jButtonColorChip2.setBackground(this.frame.getChip2Color());
}//GEN-LAST:event_jButtonColorChip2ActionPerformed

private void fieldSpotWidthPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldSpotWidthPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setSpotWidth(((Number) fieldSpotWidth.getValue()).intValue());

}//GEN-LAST:event_fieldSpotWidthPropertyChange

private void fieldMinSpotHeightPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldMinSpotHeightPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setMinSpotHeight(((Number) this.fieldMinSpotHeight.getValue()).intValue());
}//GEN-LAST:event_fieldMinSpotHeightPropertyChange

    private DefaultComboBoxModel initChipList() {

        DefaultComboBoxModel listModel = new DefaultComboBoxModel();
        //listModel.addElement(CGHPROFrame.EMPTY_SAMPLE);
        for (Chip c : this.frame.getChipList()) {
            if (c instanceof ChipFeature) {
                listModel.addElement(c.getName());

            }
        }
        return listModel;
    }

    private Chip getChip() {
        String name = (String) jComboBoxChips.getSelectedItem();
        if (!name.contentEquals(CGHPROFrame.EMPTY_SAMPLE)) {
            for (Chip c : this.frame.getChipList()) {
                if (c.getName().contentEquals(name)) {
                    return c;
                }
            }
        }
        return null;
    }
private void jComboBoxChipsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxChipsActionPerformed
    if (c != null) {//GEN-LAST:event_jComboBoxChipsActionPerformed
            this.frame.resetAberrantSpots(c);
            c = null;
        } else {
            this.jButtonApply.setForeground(java.awt.Color.RED);
        }
    }
    Chip c = null;
private void jButtonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApplyActionPerformed

    if (this.frame == null) {

        return;
    }
    c = this.getChip();
    if (c != null) {
        this.frame.setAberrantSpots(c);
    }
    Logger.getLogger(CGHPROFrame.class.getName()).log(Level.INFO,
            "jButtonApplyActionPerformed"
            + (c != null ? c.getName() : "none"));
    this.frame.refreshMatrixView();
    this.jButtonApply.setForeground(java.awt.Color.BLACK);
}//GEN-LAST:event_jButtonApplyActionPerformed

private void fieldThresholdLossPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldThresholdLossPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setThresholdLoss(((Number) this.fieldThresholdLoss.getValue()).doubleValue());
}//GEN-LAST:event_fieldThresholdLossPropertyChange

private void fieldThresholdGainPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldThresholdGainPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setThresholdGain(((Number) this.fieldThresholdGain.getValue()).doubleValue());
}//GEN-LAST:event_fieldThresholdGainPropertyChange

private void fieldAnnoWidthPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldAnnoWidthPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setPlotAnnoWidth(((Number) this.fieldAnnoWidth.getValue()).intValue());
}//GEN-LAST:event_fieldAnnoWidthPropertyChange

private void jButtonColorTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorTrackActionPerformed

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }

    this.frame.setChipTrackColor(
            JColorChooser.showDialog(
                    this,
                    "Pick a Color", this.jButtonColorTrack.getBackground()));
    this.jButtonColorTrack.setBackground(this.frame.getChipTrackColor());
}//GEN-LAST:event_jButtonColorTrackActionPerformed

private void fieldNegLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldNegLinePropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setNegThresholdLine(((Number) this.fieldNegLine.getValue()).doubleValue());

}//GEN-LAST:event_fieldNegLinePropertyChange

private void fieldPosLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldPosLinePropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setPosThresholdLine(((Number) this.fieldPosLine.getValue()).doubleValue());

}//GEN-LAST:event_fieldPosLinePropertyChange

private void fieldPlotHeightPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldPlotHeightPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setPlotPanelHeight(((Number) this.fieldPlotHeight.getValue()).intValue());
}//GEN-LAST:event_fieldPlotHeightPropertyChange

private void fieldPlotWidthPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldPlotWidthPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setPlotPanelWidth(((Number) this.fieldPlotWidth.getValue()).intValue());
}//GEN-LAST:event_fieldPlotWidthPropertyChange

private void fieldPlotHeightMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fieldPlotHeightMousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_fieldPlotHeightMousePressed

private void fieldAnnoGapPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldAnnoGapPropertyChange

    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setPlotAnnoGap(((Number) this.fieldAnnoGap.getValue()).intValue());

}//GEN-LAST:event_fieldAnnoGapPropertyChange

    public CGHPROFrame getFrame() {
        return frame;
    }

    public void setFrame(CGHPROFrame frame) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Color;
    private javax.swing.JFormattedTextField fieldAnnoGap;
    private javax.swing.JFormattedTextField fieldAnnoWidth;
    private javax.swing.JFormattedTextField fieldMinSpotHeight;
    private javax.swing.JFormattedTextField fieldNegLine;
    private javax.swing.JFormattedTextField fieldPlotHeight;
    private javax.swing.JFormattedTextField fieldPlotWidth;
    private javax.swing.JFormattedTextField fieldPosLine;
    private javax.swing.JFormattedTextField fieldSpotWidth;
    private javax.swing.JFormattedTextField fieldThresholdGain;
    private javax.swing.JFormattedTextField fieldThresholdLoss;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JButton jButtonColorAbbNegSpot;
    private javax.swing.JButton jButtonColorAbbPosSpot;
    private javax.swing.JButton jButtonColorChip1;
    private javax.swing.JButton jButtonColorChip2;
    private javax.swing.JButton jButtonColorTrack;
    private javax.swing.JComboBox jComboBoxChips;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelAnno;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableListAnnotation;
    private java.util.List<AnnotationListOrdered> listA;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link findInstance}.
     */
    public static synchronized CGHPROFramePropsTopComponent getDefault() {
        if (instance == null) {
            instance = new CGHPROFramePropsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the CGHPROFramePropsTopComponent instance. Never call
     * {@link #getDefault} directly!
     */
    public static synchronized CGHPROFramePropsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(CGHPROFramePropsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof CGHPROFramePropsTopComponent) {
            return (CGHPROFramePropsTopComponent) win;
        }
        Logger.getLogger(CGHPROFramePropsTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();

    }

    public void requestVisible() {
        Mode mode = WindowManager.getDefault().findMode("navigator");
        if (mode != null) {
            mode.dockInto(this);
            this.open();
        }
    }
    /*
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    */

    /**
     * replaces this in object stream
     */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return CGHPROFramePropsTopComponent.getDefault();
        }
    }
}
