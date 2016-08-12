package org.molgen.genomeCATPro.selector;

/**
 * @name SelectorDialog
 *
 * Created on October 25, 2011, 12:56 PM
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @version "%I%, %G%
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
import java.awt.Cursor;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import org.jdesktop.beansbinding.Converter;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.annotation.RegionArray;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.guimodul.util.ImageCellRenderer;
import org.openide.util.NbPreferences;

/**
 * 030812 kt exportList set DecimalFormatSymbol explizit
 *
 */
public class SelectorDialog extends javax.swing.JDialog {

    Data data = null;
    List<SelectorRegion> list;
    //String path = "/project/Kopenhagen/Katrin/GenomeCATPro/data/regions_Example_for_Selector.bed";

    /**
     * Creates new form Selector
     */
    public SelectorDialog(java.awt.Frame parent, boolean modal, Data d) {
        super(parent, modal);
        this.data = d;
        this.list = org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<SelectorRegion>());

        initComponents();

        TableRowSorter sorter = new TableRowSorter(jTableRegions.getModel());
        jTableRegions.setRowSorter(sorter);
        sorter.setComparator(5, new Comparator<Double>() {

            @Override
            public int compare(Double s1, Double s2) {
                return Double.compare(Math.abs(s1), Math.abs(s2));
            }
        });
        this.jButtonHigh.setText(" > " + Double.toString(SelectorRegion.threshold_gain));
        this.jButtonLow.setText(" < " + Double.toString(SelectorRegion.threshold_loss));
        this.setLocationRelativeTo(null);
    }

    /**
     * load data from db for regions defined in list
     */
    private void loadData(boolean init) {

        Logger.getLogger(SelectorDialog.class.getName()).log(Level.INFO,
                "get data for " + data.toFullString());
        if (list.size() == 0) {
            return;
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            RegionArray _spot = RegionLib.getRegionArrayClazz(data.getClazz());

            Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
            PreparedStatement ps = con.prepareStatement(
                    "SELECT AVG(" + _spot.getRatioColName() + ") "
                    + " FROM " + data.getTableData()
                    + " WHERE chrom = ? "
                    + " AND chromStart <= ? AND chromEnd >= ?");

            for (SelectorRegion sr : this.list) {
                Long chromStart, chromEnd;
                Long widht = (sr.getChromEnd() - sr.getChromStart()) / SelectorRegion.colsIcon;
                List<Double> v = new Vector<Double>();
                //sr.setValues(new Vector<Double>());
                for (int i = 0; i < SelectorRegion.colsIcon; i++) {
                    ps.setString(1, sr.getChrom());
                    chromStart = sr.getChromStart() + (i * widht);
                    chromEnd = sr.getChromStart() + ((i + 1) * widht);
                    ps.setLong(3, chromStart);
                    ps.setLong(2, (chromEnd < sr.getChromEnd() ? chromEnd : sr.getChromEnd()));

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        v.add(new Double(rs.getDouble(1)));
                    } else {
                        v.add(new Double(0.0));
                    }
                }
                sr.setValues(v);

            }
            if (init) {
                double q_09 = DBUtils.getQuantile(data.getTableData(), _spot.getRatioColName(), 0.9);
                double q_01 = DBUtils.getQuantile(data.getTableData(), _spot.getRatioColName(), 0.1);
                SelectorRegion.setMax(q_09);
                SelectorRegion.setMin(q_01);
                SelectorRegion.setThreshold_gain(data.getMedian() + data.getStddev());
                SelectorRegion.setThreshold_loss(data.getMedian() - data.getStddev());
            }

            // sort
            DecimalFormat N = (DecimalFormat) DecimalFormat.getInstance();
            N.setMaximumFractionDigits(3);
            this.jButtonHigh.setText(" > " + N.format(SelectorRegion.threshold_gain));
            this.jButtonLow.setText(" < " + N.format(SelectorRegion.threshold_loss));
            this.jButtonLow.setBackground(SelectorRegion.getColor(SelectorRegion.threshold_loss));
            this.jButtonHigh.setBackground(SelectorRegion.getColor(SelectorRegion.threshold_gain));
            List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
            sortKeys.add(new RowSorter.SortKey(5, SortOrder.DESCENDING));
            this.jTableRegions.getRowSorter().setSortKeys(sortKeys);
            this.jTableRegions.repaint();

        } catch (Exception e) {
            Logger.getLogger(SelectorDialog.class.getName()).log(
                    Level.SEVERE, "", e);
            JOptionPane.showMessageDialog(this, "error  - see logfile for more information: "
                    + e.getMessage());

            //throw new RuntimeException(e);
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    @SuppressWarnings("empty-statement")
    /**
     * load relevant regions as list from given file
     */
    private void initList() {
        // read as bed
        BufferedReader inBuffer;
        String is;
        String[] iss;

        List<SelectorRegion> _list = new Vector<SelectorRegion>();
        List<Double> values = null;
        try {
            File inFile = new File(this.jTextFieldDataName.getText().toString());
            inBuffer = new BufferedReader(new FileReader(inFile));
            while ((is = inBuffer.readLine()) != null) {
                if (is == null) {
                    continue;
                }
                iss = is.split("\t");
                // chrom start stop name
                if (iss.length < 4) {
                    continue;
                }
                values = new Vector<Double>();
                /*for(int i = 0; i < SelectorRegion.colsIcon; i++){
                values.add(new Double( 
                Math.random() * (SelectorRegion.min + 
                (int) (Math.random() * ((SelectorRegion.max - SelectorRegion.min) + 1)))));
                }*/
                _list.add(new SelectorRegion(
                        iss[3],
                        iss[0],
                        Long.parseLong(iss[1]),
                        Long.parseLong(iss[2]), values));
            }

            this.setList(_list);
            this.jLabelList.setText(" #regions: " + this.list.size());
        } catch (Exception ex) {
            Logger.getLogger(SelectorDialog.class.getName()).log(Level.SEVERE, null, ex);
            try {
                throw ex;
            } catch (Exception ex1) {
                ;
            }
        }
    }

    private void exportList(String sOutfile) {
        BufferedWriter out = null;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            out = new BufferedWriter(new FileWriter(sOutfile));

            DecimalFormat N = (DecimalFormat) DecimalFormat.getInstance();
            DecimalFormatSymbols NS = DecimalFormatSymbols.getInstance();
            NS.setDecimalSeparator('.');
            NS.setGroupingSeparator(',');
            N.setDecimalFormatSymbols(NS);
            N.setMaximumFractionDigits(3);
            out.write("name\tchrom\tstart\tstop\tvalue\tstddev");
            out.newLine();
            for (SelectorRegion sr : this.list) {

                out.write(
                        sr.getName() + "\t"
                        + sr.getChrom() + "\t"
                        + sr.getChromStart() + "\t"
                        + sr.getChromEnd() + "\t"
                        + N.format(sr.getValue()) + "\t"
                        + N.format(sr.getStddev()));
                out.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(SelectorDialog.class.getName()).log(
                    Level.SEVERE, "", ex);
            JOptionPane.showMessageDialog(this, "error  - see logfile for more information: "
                    + ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Logger.getLogger(SelectorDialog.class.getName()).log(
                        Level.SEVERE, "", e);
            }
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<SelectorRegion> getList() {
        return list;
    }

    public void setList(List<SelectorRegion> _list) {
        Logger.getLogger(SelectorDialog.class.getName()).log(Level.INFO,
                " set list " + _list.size());
        this.list.clear();
        this.list.addAll(_list);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRegions = new javax.swing.JTable();
        jButtonLow = new javax.swing.JButton();
        jButtonHigh = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldDataName = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jButtonProps = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();
        jLabelList = new javax.swing.JLabel();
        jButtonPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.title")); // NOI18N

        jTableRegions.setAutoCreateRowSorter(true);
        jTableRegions.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jTableRegions.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTableRegions.setGridColor(new java.awt.Color(204, 204, 204));
        jTableRegions.setName("data"); // NOI18N
        jTableRegions.setRowHeight(30);
        jTableRegions.setShowVerticalLines(false);
        //jTable1.getDefaultRenderer(Long.class).  setHorizontalAlignment(JLabel.LEFT );

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${list}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, eLProperty, jTableRegions);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${chrom}"));
        columnBinding.setColumnName("Chrom");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${chromStart}"));
        columnBinding.setColumnName("Chrom Start");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${chromEnd}"));
        columnBinding.setColumnName("Chrom End");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${image}"));
        columnBinding.setColumnName("Image");
        columnBinding.setColumnClass(java.awt.Image.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${value}"));
        columnBinding.setColumnName("Value");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stddev}"));
        columnBinding.setColumnName("Stddev");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        jTableRegions.setDefaultRenderer(Long.class, renderer);
        jScrollPane1.setViewportView(jTableRegions);
        jTableRegions.getColumnModel().getColumn(0).setResizable(false);
        jTableRegions.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTableRegions.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jTable1.columnModel.title1")); // NOI18N
        jTableRegions.getColumnModel().getColumn(1).setResizable(false);
        jTableRegions.getColumnModel().getColumn(1).setPreferredWidth(30);
        jTableRegions.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jTable1.columnModel.title2")); // NOI18N
        jTableRegions.getColumnModel().getColumn(2).setResizable(false);
        jTableRegions.getColumnModel().getColumn(2).setPreferredWidth(30);
        jTableRegions.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jTable1.columnModel.title3")); // NOI18N
        jTableRegions.getColumnModel().getColumn(3).setMinWidth(300);
        jTableRegions.getColumnModel().getColumn(3).setMaxWidth(500);
        jTableRegions.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jTable1.columnModel.title5")); // NOI18N
        jTableRegions.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jTable1.columnModel.title6")); // NOI18N
        jTableRegions.getColumnModel().getColumn(4).setCellRenderer(new ImageCellRenderer());
        jTableRegions.getColumnModel().getColumn(5).setResizable(false);
        jTableRegions.getColumnModel().getColumn(5).setPreferredWidth(30);
        jTableRegions.getColumnModel().getColumn(5).setHeaderValue(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jTable1.columnModel.title0")); // NOI18N
        jTableRegions.getColumnModel().getColumn(6).setResizable(false);
        jTableRegions.getColumnModel().getColumn(6).setPreferredWidth(30);
        jTableRegions.getColumnModel().getColumn(6).setHeaderValue(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jTableRegions.columnModel.title6")); // NOI18N

        jButtonLow.setBackground(java.awt.Color.red);
        jButtonLow.setForeground(java.awt.Color.black);
        jButtonLow.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jButtonLow.text")); // NOI18N
        jButtonLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLowActionPerformed(evt);
            }
        });

        jButtonHigh.setBackground(java.awt.Color.green);
        jButtonHigh.setForeground(java.awt.Color.black);
        jButtonHigh.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jButtonHigh.text")); // NOI18N

        jButtonClose.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jButtonClose.text")); // NOI18N
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jLabel1.text")); // NOI18N

        jButtonBrowse.setFont(new java.awt.Font("Monospaced", 1, 12));
        jButtonBrowse.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jButtonBrowse.text")); // NOI18N
        jButtonBrowse.setMaximumSize(new java.awt.Dimension(80, 19));
        jButtonBrowse.setMinimumSize(new java.awt.Dimension(80, 19));
        jButtonBrowse.setPreferredSize(new java.awt.Dimension(80, 19));
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jButtonProps.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jButtonProps.text")); // NOI18N
        jButtonProps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPropsActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jLabel2.text")); // NOI18N

        jTextPane1.setEditable(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${data.metaText}"), jTextPane1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new Converter<StringBuffer, String>() {
            @Override
            public String convertForward(StringBuffer value) {
                return value.toString();
            }

            @Override
            public StringBuffer convertReverse(String value) {
                return new StringBuffer(value);
            }
        });
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextPane1);

        jButton2.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabelList.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabelList.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jLabelList.text")); // NOI18N

        jButtonPrint.setText(org.openide.util.NbBundle.getMessage(SelectorDialog.class, "SelectorDialog.jButtonPrint.text")); // NOI18N
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 912, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(29, 29, 29)
                        .addComponent(jButtonPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonClose, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabelList, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(283, 283, 283)
                                    .addComponent(jButtonLow)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButtonHigh)
                                    .addGap(42, 42, 42)
                                    .addComponent(jButtonProps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jTextFieldDataName, javax.swing.GroupLayout.PREFERRED_SIZE, 691, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButtonBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonHigh, jButtonLow});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldDataName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonLow)
                            .addComponent(jButtonHigh)
                            .addComponent(jButtonProps)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabelList, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButtonPrint)
                    .addComponent(jButtonClose))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
    String path = NbPreferences.forModule(SelectorDialog.class).get("pathPreference", "");
    JFileChooser importFileChooser = new JFileChooser(path);
    int returnVal = importFileChooser.showOpenDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }
    this.jTextFieldDataName.setText(importFileChooser.getSelectedFile().getPath());

    Logger.getLogger(SelectorDialog.class.getName()).log(Level.INFO,
            "You chose to import regions of interest from file: "
            + this.jTextFieldDataName.getText());
    this.initList();
    this.loadData(true);
    NbPreferences.forModule(SelectorDialog.class).put("pathPreference",
            importFileChooser.getSelectedFile().getPath());
}//GEN-LAST:event_jButtonBrowseActionPerformed
    private void printDataTable() {
        /* Fetch printing properties from the GUI components */

        Color c = this.jTableRegions.getBackground();
        this.jTableRegions.setBackground(Color.WHITE);
        boolean fitWidth = true;
        boolean showPrintDialog = true;
        boolean interactive = true;

        /* determine the print mode */
        JTable.PrintMode mode = fitWidth ? JTable.PrintMode.FIT_WIDTH
                : JTable.PrintMode.NORMAL;

        try {
            /* print the table */
            boolean complete = this.jTableRegions.print(mode, null, null,
                    showPrintDialog, null,
                    interactive, null);


            /* if printing completes */
            if (complete) {
                /* show a success message */
                JOptionPane.showMessageDialog(this,
                        "Printing Complete",
                        "Printing Result",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                /* show a message indicating that printing was cancelled */
                JOptionPane.showMessageDialog(this,
                        "Printing Cancelled",
                        "Printing Result",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException pe) {
            /* Printing failed, report to the user */
            JOptionPane.showMessageDialog(this,
                    "Printing Failed: " + pe.getMessage(),
                    "Printing Result",
                    JOptionPane.ERROR_MESSAGE);
        }
        this.jTableRegions.setBackground(c);
    }
private void jButtonLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLowActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jButtonLowActionPerformed

private void jButtonPropsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPropsActionPerformed
    SelectorPropertiesJDialog dialog = new SelectorPropertiesJDialog(new javax.swing.JFrame(), true);

    dialog.setVisible(true);
    this.loadData(false);

}//GEN-LAST:event_jButtonPropsActionPerformed

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.setVisible(false);
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    String path = NbPreferences.forModule(SelectorDialog.class).get("pathPreference", "");
    final JFileChooser fc = new JFileChooser(path);
    fc.setSelectedFile(new File(this.data.getTableData() + "_" + new File(this.jTextFieldDataName.getText()).getName() + ".txt"));

    int returnVal = fc.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        //This is where a real application would open the file.
        this.exportList(file.getPath());
    }

}//GEN-LAST:event_jButton2ActionPerformed

private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
    this.printDataTable();
}//GEN-LAST:event_jButtonPrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonHigh;
    private javax.swing.JButton jButtonLow;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonProps;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableRegions;
    private javax.swing.JTextField jTextFieldDataName;
    private javax.swing.JTextPane jTextPane1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
