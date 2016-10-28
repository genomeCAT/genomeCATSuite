package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name LegendPanel
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.text.Font;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.molgen.genomeCATPro.cat.util.Defines;

/**
 *
 * 280912 kt resp ArrayDataAnno
 */
public class LegendPanel extends JPanel {

    ArrayFrame arrayFrame = null;
    JPopupMenu popup = new JPopupMenu();
    JMenu menuMove = new JMenu("change order");
    JMenuItem menuItemMoveTop = new JMenuItem("<html>&#8593;&#8593;</html>");
    JMenuItem menuItemMoveUp = new JMenuItem("<html>&#8593;</html>");
    JMenuItem menuItemMoveDown = new JMenuItem("<html>&#8595;</html>");
    JMenuItem menuItemMoveBottom = new JMenuItem("<html>&#8595;&#8595;</html>");
    JMenuItem menuItemDelete = new JMenuItem("delete");
    JMenuItem menuItemFilter = new JMenuItem("filter");
    JMenuItem menuItemSave = new JMenuItem("save");

    public LegendPanel(ArrayFrame arrayFrame) {
        super();
        this.arrayFrame = arrayFrame;
        initMenues();
        this.setFont(this.getFont().deriveFont(10));

    }

    /**
     * create popup menues for legend panel.
     *
     */
    protected void initMenues() {
        menuItemMoveTop.addActionListener(new MenuListener());
        menuMove.add(menuItemMoveTop);
        menuItemMoveUp.addActionListener(new MenuListener());
        menuMove.add(menuItemMoveUp);
        menuItemMoveDown.addActionListener(new MenuListener());
        menuMove.add(menuItemMoveDown);
        menuItemMoveBottom.addActionListener(new MenuListener());
        menuMove.add(menuItemMoveBottom);
        popup.add(menuMove);

        menuItemDelete.addActionListener(new MenuListener());
        popup.add(menuItemDelete);
        menuItemFilter.addActionListener(new MenuListener());
        popup.add(menuItemFilter);
        menuItemSave.addActionListener(new MenuListener());
        popup.add(menuItemSave);
        /*
    MouseListener popupListener = new PopupListener();
    addMouseListener(popupListener);
         */
    }

    /**
     * allow or forbid user scaling
     *
     * @param allowed
     */
    void setUserScaling(boolean allowed) {
        ArrayLabel arraylabel = null;
        for (int i = 0; i < this.getComponentCount(); i++) {
            arraylabel = (LegendPanel.ArrayLabel) this.getComponent(i);
            arraylabel.setScaling(allowed);
        }
    }

    /**
     * action listener for popup menue
     */
    class MenuListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //System.out.println("menue action event at  " + e.getSource()

            Point currentPosition = LegendPanel.this.getMousePosition();
            //int i = LegendPanel.this.getIndexPosition(currentPosition);
            Component component = LegendPanel.this.getComponentAt(currentPosition);

            if (component != null && component instanceof ArrayLabel) {
                //((JPanel) component).setOpaque(true);
                //component.setBackground(Color.MAGENTA);
                int i = LegendPanel.this.getComponentZOrder(component);
                ArrayLabel a = ((ArrayLabel) LegendPanel.this.getComponent(i));
                Logger.getLogger(LegendPanel.class.getName()).log(Level.INFO,
                        "action performed on array pos : " + i + " id: " + a.arraydata.id);

                JMenuItem source = (JMenuItem) (e.getSource());
                if (source == menuItemDelete) {
                    int n = JOptionPane.showConfirmDialog(null,
                            new String("Do you really want to delete this view for \n"
                                    + a.arraydata.getName()),
                            "Delete Array View",
                            JOptionPane.YES_NO_OPTION);

                    if (n == JOptionPane.YES_OPTION) {
                        //boolean filtered = ((ArrayLabel) LegendPanel.this.getComponent(i)).arraydata.filteredData;
                        if (a.arraydata.filteredData) {
                            int nn = JOptionPane.showConfirmDialog(null,
                                    "Would you like to save the filtered data as new "
                                    + " data table into the database? ",
                                    "save filtered data",
                                    JOptionPane.YES_NO_OPTION);

                            if (nn == JOptionPane.YES_OPTION) {
                                arrayFrame.filterArrayAtDB(a.arraydata.getId(), a.arraydata);
                            }
                        }
                        arrayFrame.removeArray(a.arraydata.getId());
                    }
                }
                if (source == menuItemFilter) {
                    arrayFrame.filterArray(a.arraydata.getId());
                }
                if (source == menuItemSave) {
                    if (a.arraydata.filteredData) {
                        arrayFrame.filterArrayAtDB(a.arraydata.getId(), a.arraydata);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Only useful for filtered data");
                    }
                }

                if (source == menuItemMoveUp) {
                    LegendPanel.this.changePosition(component, i, --i < 0 ? 0 : i);
                }
                if (source == menuItemMoveTop) {
                    LegendPanel.this.changePosition(component, i, 0);
                }
                if (source == menuItemMoveDown) {
                    int max = LegendPanel.this.getComponentCount() - 1;
                    LegendPanel.this.changePosition(component, i, ++i > max ? max : i);
                }
                if (source == menuItemMoveBottom) {
                    LegendPanel.this.changePosition(component, i, LegendPanel.this.getComponentCount() - 1);
                }
                // component.setBackground(Color.WHITE);
                // ((JPanel) component).setOpaque(false);
            }
        }
    }

    /**
     * mouse listener for popup menue
     */
    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            System.out.println("popup listener");

            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    /**
     * create new array legend view
     *
     * @param m
     * @param s
     */
    void addArray(ArrayData d) {
        add(new ArrayLabel(d));
    }

    ArrayLabel getArrayLabel(Long arrayId) {
        boolean next = true;
        ArrayLabel arraylabel = null;

        for (int i = 0; i < this.getComponentCount(); i++) {
            arraylabel = (LegendPanel.ArrayLabel) this.getComponent(i);
            if (arraylabel.arraydata.getId().equals(arrayId)) {

                return arraylabel;
            }
        }
        return null;

    }

    /**
     * move position for component inside legendpanel
     *
     * @param arrayLabel
     * @param sourcePos
     * @param destPos
     */
    protected void changePosition(Component arrayLabel, int sourcePos, int destPos) {
        this.setComponentZOrder(arrayLabel, destPos);
        arrayFrame.changePosArrayView(sourcePos, destPos);
    }

    /**
     * remove component from legendpanel at given position
     *
     * @param pos
     */
    protected void removeArray(Long id) {
        ArrayLabel arraylabel = this.getArrayLabel(id);
        this.remove(arraylabel);
    }

    protected int getIndexPosition(Point p) {
        Component component = this.getComponentAt(p);
        if (component != null && component instanceof ArrayLabel) {
            return (LegendPanel.this.getComponentZOrder(component));
        } else {
            return -1;
        }

    }

    /**
     * create new array component
     *
     * @return
     */
    protected class ArrayLabel extends JPanel {

        public ArrayData arraydata;
        JCheckBox checkThresholds;
        JSpinner negThresholdSpinner, posThresholdSpinner;
        JSpinner scaleSpinner;
        MouseListener popupListener = new PopupListener();

        /**
         *
         * @param m ArrayData
         * @param s ArrayStats
         */
        public ArrayLabel(ArrayData d) {
            super();

            this.arraydata = d;

            initView();

            addMouseListener(popupListener);
        }

        /**
         * init visual components
         */
        void initView() {
            JTabbedPane tabbedPane = new JTabbedPane();
            
            this.add(tabbedPane);

            tabbedPane.addMouseListener(popupListener);
            StringBuffer arrayText = new StringBuffer("<html><PRE>");
            arrayText.append(this.arraydata.getText());

            //arrayText.append(m.getAsText());
            //arrayText.append("</PRE>");
            //this.texts.put(m.viewId, arrayText);
            if (!(arraydata instanceof ArrayDataAnno)) {

                arrayText.append("median: \t" + this.arraydata.getData().getMedian() + "\n");
                arrayText.append("mean:   \t" + this.arraydata.getData().getMean() + "\n");
                arrayText.append("min ratio: \t" + this.arraydata.getData().getMinRatio() + "\n");
                arrayText.append("max ratio: \t" + this.arraydata.getData().getMaxRatio() + "\n");
            }
            arrayText.append("</PRE>");
            arrayText.append("</html>");
            //arrayText.setLineWrap(true);
            //arrayText.setWrapStyleWord(true);
            JLabel arrayTextL = new JLabel(arrayText.toString());
            arrayTextL.setHorizontalTextPosition(SwingConstants.LEFT);
            
            if (!(arraydata instanceof ArrayDataAnno)) {
                tabbedPane.setPreferredSize(new Dimension((int) (Defines.ARRAY_WIDTH * 0.3), 
                                (int) (Defines.ARRAY_HEIGTH)));
                arrayTextL.setPreferredSize(
                        new Dimension((int) (Defines.ARRAY_WIDTH * 0.3), 
                                (int) (Defines.ARRAY_HEIGTH*0.8)));
            } else {
                tabbedPane.setPreferredSize(new Dimension((int) (Defines.ARRAY_WIDTH * 0.3), 
                                (int) (Defines.ARRAY_HEIGTH *0.5)));
                arrayTextL.setPreferredSize(
                        new Dimension((int) (Defines.ARRAY_WIDTH * 0.3),
                                (int) (Defines.ARRAY_HEIGTH * 0.5)));
            }
            tabbedPane.addTab(
                    this.arraydata.getName().substring(0,
                            Math.min(15, this.arraydata.getName().length())),
                    null, arrayTextL, "information about the array");

            JPanel arrayPanel = new JPanel();
            //arrayPanel.setLayout(new FlowLayout());
            arrayPanel.setLayout(new BoxLayout(arrayPanel, BoxLayout.Y_AXIS));
            if (!(arraydata instanceof ArrayDataAnno)) {
                SpinnerModel negThresholdModel = new SpinnerNumberModel(0,
                        this.arraydata.getData().getMinRatio().doubleValue() > 0 ? 0 : this.arraydata.getData().getMinRatio().doubleValue(),
                        0,
                        0.1);
                negThresholdSpinner = new JSpinner(negThresholdModel);
                negThresholdSpinner.addChangeListener(new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSpinner spinner = (JSpinner) (e.getSource());
                        SpinnerNumberModel model = (SpinnerNumberModel) (spinner.getModel());
                        ArrayLabel.this.arraydata.setNegThreshold(model.getNumber().doubleValue());
                        //int i = LegendPanel.this.getComponentZOrder(ArrayLabel.this);
                        LegendPanel.this.arrayFrame.showThresholds(
                                ArrayLabel.this.checkThresholds.isSelected(),
                                ArrayLabel.this.arraydata.getId());
                    }
                });
                negThresholdSpinner.setEnabled(false);
                negThresholdSpinner.setSize((int) (Defines.ARRAY_WIDTH * 0.23), 10);
                JPanel pNegThreshold = new JPanel(new java.awt.BorderLayout());
                pNegThreshold.setBorder(new javax.swing.border.TitledBorder("negative threshold"));
                pNegThreshold.add(negThresholdSpinner, java.awt.BorderLayout.CENTER);

                SpinnerModel posThresholdModel = new SpinnerNumberModel(0,
                        0,
                        arraydata.getData().getMaxRatio().doubleValue() < 0 ? 0 : arraydata.getData().getMaxRatio().doubleValue(),
                        0.1);
                posThresholdSpinner = new JSpinner(posThresholdModel);

                posThresholdSpinner.addChangeListener(new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        JSpinner spinner = (JSpinner) (e.getSource());
                        SpinnerNumberModel model = (SpinnerNumberModel) (spinner.getModel());
                        ArrayLabel.this.arraydata.setPosThreshold(model.getNumber().doubleValue());
                        //int i = LegendPanel.this.getComponentZOrder(ArrayLabel.this);
                        LegendPanel.this.arrayFrame.showThresholds(
                                ArrayLabel.this.checkThresholds.isSelected(),
                                ArrayLabel.this.arraydata.getId());

                    }
                });
                posThresholdSpinner.setEnabled(false);
                posThresholdSpinner.setSize((int) (Defines.ARRAY_WIDTH * 0.23), 10);
                JPanel pPosThreshold = new JPanel(new java.awt.BorderLayout());
                pPosThreshold.setBorder(new javax.swing.border.TitledBorder("positive threshold"));
                pPosThreshold.add(posThresholdSpinner, java.awt.BorderLayout.CENTER);

                checkThresholds = new JCheckBox("show thresholds:");
                checkThresholds.addItemListener(new ItemListener() {

                    public void itemStateChanged(ItemEvent e) {
                        //int i = getIndexPosition(LegendPanel.this.getMousePosition());
                        //int i = LegendPanel.this.getComponentZOrder(ArrayLabel.this);
                        boolean selected = e.getStateChange() == ItemEvent.SELECTED;

                        LegendPanel.this.arrayFrame.showThresholds(selected,
                                ArrayLabel.this.arraydata.getId());

                        ArrayLabel.this.negThresholdSpinner.setEnabled(selected);
                        ArrayLabel.this.posThresholdSpinner.setEnabled(selected);

                    }
                });
                checkThresholds.setSize((int) (Defines.ARRAY_WIDTH * 0.23), 10);
                JPanel pCheckThresholds = new JPanel(new java.awt.BorderLayout());
                //pCheckThresholds.setBorder(new javax.swing.border.TitledBorder("display tresholds"));
                pCheckThresholds.add(checkThresholds, java.awt.BorderLayout.LINE_START);
                arrayPanel.add(pCheckThresholds);

                arrayPanel.add(pNegThreshold);

                arrayPanel.add(pPosThreshold);

                // create scaling spinner
                SpinnerModel scaleModel = new SpinnerNumberModel(1.0, 0, 100.0, 0.1);
                //SpinnerModel scaleModel = new SpinnerNumberModel(1, 1, 10, 0.1);
                scaleSpinner = new JSpinner(scaleModel);
                scaleSpinner.addChangeListener(new MyChangeListener());

                JPanel pScaleSpinner = new JPanel(new java.awt.BorderLayout());
                pScaleSpinner.setBorder(new javax.swing.border.TitledBorder("scale ratios"));
                pScaleSpinner.add(scaleSpinner, java.awt.BorderLayout.CENTER);
                arrayPanel.add(pScaleSpinner);
            }
            tabbedPane.addTab("settings", null, arrayPanel, "display settings");

        }

        class MyChangeListener implements ChangeListener {

            public void stateChanged(ChangeEvent e) {
                System.out.println("state changed scaling spinner");
                JSpinner scaleSpinner = (JSpinner) (e.getSource());
                SpinnerNumberModel scaleModel = (SpinnerNumberModel) (scaleSpinner.getModel());
                double scaleFactor = scaleModel.getNumber().doubleValue();
                int i = getIndexPosition(LegendPanel.this.getMousePosition());
                if (i > -1) {
                    arrayFrame.scaleArrayViewInterChrom(i, scaleFactor);
                    //scale(i, scaleFactor);
                }
            }
        };

        void setScaling(boolean allowed) {
            // kt 170613
            /*
            if(this.scaleSpinner == null)
                return;
            this.scaleSpinner.setEnabled(allowed);
            if (!allowed) {
                try {

                    this.scaleSpinner.setModel(new SpinnerNumberModel(1.0, 0, 100.0, 0.1));
                } catch (Exception e) {
                }
            }
            this.repaint();
             */
        }
    }
}
