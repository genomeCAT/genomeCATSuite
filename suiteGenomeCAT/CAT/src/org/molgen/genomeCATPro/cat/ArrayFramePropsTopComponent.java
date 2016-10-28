package org.molgen.genomeCATPro.cat;

/**
 * @name ArrayFramePropsTopComponent
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
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.molgen.genomeCATPro.cat.maparr.ArrayFrame;
import org.molgen.genomeCATPro.cat.maparr.ArrayViewBase;
import org.molgen.genomeCATPro.guimodul.SwingUtils;
import org.openide.util.*;

import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * 281016 kt component closed empty (bug no how up after closing frame)
 * 260612 kt initComponents() rulerSpinner.stateChanged check if frame != null
 */
final class ArrayFramePropsTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result result = null;
    ArrayFrame frame;
    private static ArrayFramePropsTopComponent instance;
    private static final String PREFERRED_ID = "ArrayFramePropsTopComponent";
    BufferedImage colorImgRG = null;
    BufferedImage colorImgYB = null;

    private ArrayFramePropsTopComponent() {
        colorImgRG = new BufferedImage(100, 10, BufferedImage.TYPE_INT_ARGB_PRE);
        colorImgYB = new BufferedImage(100, 10, BufferedImage.TYPE_INT_ARGB_PRE);
        this.initComponents();
        this.initMyComponents();
        setName(NbBundle.getMessage(ArrayFramePropsTopComponent.class, "CTL_ArrayFramePropsTopComponent"));
        setToolTipText(NbBundle.getMessage(ArrayFramePropsTopComponent.class, "HINT_ArrayFramePropsTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    @Override
    public void componentOpened() {

        Lookup.Template<ArrayFrame> tpl = new Lookup.Template<ArrayFrame>(ArrayFrame.class);
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
            CATPropertiesMod.save();
        } catch (Exception e) {
            Logger.getLogger(ArrayFramePropsTopComponent.class.getName()).log(
                    Level.INFO, "", e);
        }
        */
    }

    public void resultChanged(LookupEvent lookupEvent) {

        Lookup.Result r = (Lookup.Result) lookupEvent.getSource();
        Collection _c = r.allInstances();

        if (!_c.isEmpty()) {
            this.frame = (ArrayFrame) _c.iterator().next();
        }
        Logger.getLogger(ArrayFramePropsTopComponent.class.getName()).log(
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
            ArrayViewBase.resetColorScale();
            ArrayViewBase.paintColorScale(colorImgRG, true, true);
            ArrayViewBase.resetColorScale();
            ArrayViewBase.paintColorScale(colorImgYB, false, true);

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
        Props = new javax.swing.JTabbedPane();
        jPanelColor = new javax.swing.JPanel();
        cbColorScaleRG = new javax.swing.JCheckBox();
        cbColorScaleYB = new javax.swing.JCheckBox();
        jLabelColorImgRG = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanelLayout = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        checkScale = new javax.swing.JCheckBox();
        SpinnerModel scaleModel = new SpinnerNumberModel(1.0, 0, 10000.0, 0.1);
        scaleSpinner = new javax.swing.JSpinner(scaleModel);
        jButtonApply = new javax.swing.JButton();

        setName("Properties CAT"); // NOI18N

        Props.setMinimumSize(new java.awt.Dimension(90, 80));
        Props.setPreferredSize(new java.awt.Dimension(100, 100));

        buttonGroupColorScale.add(cbColorScaleRG);
        cbColorScaleRG.setSelected(CATPropertiesMod.props().isColorScaleRedGreen() );
        org.openide.awt.Mnemonics.setLocalizedText(cbColorScaleRG, org.openide.util.NbBundle.getMessage(ArrayFramePropsTopComponent.class, "ArrayFramePropsTopComponent.cbColorScaleRG.text")); // NOI18N
        cbColorScaleRG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cbColorScaleRG.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cbColorScaleRG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbColorScaleRGActionPerformed(evt);
            }
        });

        buttonGroupColorScale.add(cbColorScaleYB);
        cbColorScaleYB.setSelected(!CATPropertiesMod.props().isColorScaleRedGreen());
        org.openide.awt.Mnemonics.setLocalizedText(cbColorScaleYB, org.openide.util.NbBundle.getMessage(ArrayFramePropsTopComponent.class, "ArrayFramePropsTopComponent.cbColorScaleYB.text")); // NOI18N
        cbColorScaleYB.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cbColorScaleYB.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cbColorScaleYB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbColorScaleYBActionPerformed(evt);
            }
        });

        jLabelColorImgRG.setIcon(new ImageIcon(this.colorImgRG));
        org.openide.awt.Mnemonics.setLocalizedText(jLabelColorImgRG, org.openide.util.NbBundle.getMessage(ArrayFramePropsTopComponent.class, "ArrayFramePropsTopComponent.jLabelColorImgRG.text")); // NOI18N
        jLabelColorImgRG.setMaximumSize(new java.awt.Dimension(100, 10));
        jLabelColorImgRG.setMinimumSize(new java.awt.Dimension(100, 10));
        jLabelColorImgRG.setPreferredSize(new java.awt.Dimension(100, 10));

        jLabel3.setIcon(new ImageIcon(colorImgYB));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ArrayFramePropsTopComponent.class, "ArrayFramePropsTopComponent.jLabel3.text")); // NOI18N
        jLabel3.setMaximumSize(new java.awt.Dimension(100, 10));
        jLabel3.setMinimumSize(new java.awt.Dimension(100, 10));
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 10));

        javax.swing.GroupLayout jPanelColorLayout = new javax.swing.GroupLayout(jPanelColor);
        jPanelColor.setLayout(jPanelColorLayout);
        jPanelColorLayout.setHorizontalGroup(
            jPanelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelColorLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbColorScaleYB)
                    .addGroup(jPanelColorLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cbColorScaleRG)
                    .addGroup(jPanelColorLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabelColorImgRG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );

        jPanelColorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cbColorScaleRG, cbColorScaleYB});

        jPanelColorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabelColorImgRG});

        jPanelColorLayout.setVerticalGroup(
            jPanelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelColorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbColorScaleRG)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelColorImgRG, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbColorScaleYB)
                    .addGroup(jPanelColorLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelColorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jLabelColorImgRG});

        Props.addTab("Color", jPanelColor);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("scale views"));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ArrayFramePropsTopComponent.class, "ArrayFramePropsTopComponent.jLabel1.text")); // NOI18N

        checkScale.setSelected(CATPropertiesMod.props().isGlobalScale());
        org.openide.awt.Mnemonics.setLocalizedText(checkScale, org.openide.util.NbBundle.getMessage(ArrayFramePropsTopComponent.class, "ArrayFramePropsTopComponent.checkScale.text")); // NOI18N
        checkScale.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        checkScale.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                frame.setGlobalScale(checkScale.isSelected());
                scaleSpinner.setEnabled(checkScale.isSelected());
            }
        });
        checkScale.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                checkScalePropertyChange(evt);
            }
        });

        scaleSpinner.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                System.out.println("state changed scaling spinner");
                JSpinner scaleSpinner = (JSpinner) (e.getSource());
                SpinnerNumberModel scaleModel = (SpinnerNumberModel) (scaleSpinner.getModel());
                double scaleFactor = scaleModel.getNumber().doubleValue();
                if(frame != null){
                    frame.setScaleFactor(scaleFactor);
                    frame.scaleArrayViewInterFrame(CATPropertiesMod.props().getScaleFactor());
                }
                else{
                    jButtonApply.setForeground(java.awt.Color.RED);
                }
            }
        });

        scaleSpinner.setValue(CATPropertiesMod.props().getScaleFactor());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkScale))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(checkScale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(scaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        checkScale.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ArrayFramePropsTopComponent.class, "ArrayFramePropsTopComponent.checkScale.AccessibleContext.accessibleName")); // NOI18N

        javax.swing.GroupLayout jPanelLayoutLayout = new javax.swing.GroupLayout(jPanelLayout);
        jPanelLayout.setLayout(jPanelLayoutLayout);
        jPanelLayoutLayout.setHorizontalGroup(
            jPanelLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelLayoutLayout.setVerticalGroup(
            jPanelLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayoutLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        Props.addTab("Layout", jPanelLayout);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonApply, "refresh view");
        jButtonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Props, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
            .addComponent(jButtonApply, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButtonApply)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Props, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jButtonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApplyActionPerformed

    if (this.frame == null) {
        return;
    }
    this.frame.repaint();
    this.jButtonApply.setForeground(java.awt.Color.BLACK);
}//GEN-LAST:event_jButtonApplyActionPerformed

private void checkScalePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_checkScalePropertyChange
    if (this.frame == null) {
        this.jButtonApply.setForeground(java.awt.Color.RED);
        return;
    }
    this.frame.setGlobalScale(this.checkScale.isSelected());
}//GEN-LAST:event_checkScalePropertyChange

private void cbColorScaleRGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbColorScaleRGActionPerformed
    if (this.frame != null) {
        this.frame.setColorScaleRedGreen(this.cbColorScaleRG.isSelected());
    } else {
        this.jButtonApply.setForeground(java.awt.Color.RED);
    }
}//GEN-LAST:event_cbColorScaleRGActionPerformed

private void cbColorScaleYBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbColorScaleYBActionPerformed
    if (this.frame != null) {
        this.frame.setColorScaleRedGreen(this.cbColorScaleRG.isSelected());
    } else {
        this.jButtonApply.setForeground(java.awt.Color.RED);
    }
}//GEN-LAST:event_cbColorScaleYBActionPerformed

    public ArrayFrame getFrame() {
        return frame;
    }

    public void setFrame(ArrayFrame frame) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Props;
    private javax.swing.ButtonGroup buttonGroupColorScale;
    private javax.swing.JCheckBox cbColorScaleRG;
    private javax.swing.JCheckBox cbColorScaleYB;
    public javax.swing.JCheckBox checkScale;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelColorImgRG;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelColor;
    private javax.swing.JPanel jPanelLayout;
    public javax.swing.JSpinner scaleSpinner;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link findInstance}.
     */
    public static synchronized ArrayFramePropsTopComponent getDefault() {
        if (instance == null) {
            instance = new ArrayFramePropsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ArrayFramePropsTopComponent instance. Never call
     * {@link #getDefault} directly!
     */
    public static synchronized ArrayFramePropsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ArrayFramePropsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ArrayFramePropsTopComponent) {
            return (ArrayFramePropsTopComponent) win;
        }
        Logger.getLogger(ArrayFramePropsTopComponent.class.getName()).warning(
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
            return ArrayFramePropsTopComponent.getDefault();
        }
    }
}
