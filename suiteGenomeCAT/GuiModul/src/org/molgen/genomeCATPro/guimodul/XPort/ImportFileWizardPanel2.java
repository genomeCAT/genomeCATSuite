package org.molgen.genomeCATPro.guimodul.XPort;

/**
 * @name ImportFileWizardPanel2
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;

import org.molgen.genomeCATPro.cghpro.xport.XPortImport;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 * set mapping for import file in generell, no userinput nessacary -> valid =
 * true mapping is checked when form is left -> throws error load component ==
 * null
 *
 * 120313	kt	readFilenames throws Exception
 *
 */
public class ImportFileWizardPanel2 implements
        WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel,
        PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component component;
    private static String emptyCol = "";
    XPortExperimentFile importModul = null;
    private WizardDescriptor wd = null;
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.

    public Component getComponent() {
        if (component == null) {
            component = new ImportFileVisualPanel2();

        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }
    boolean valid = true;

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return valid;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    void validateMapping() throws WizardValidationException {
        try {
            // check if all fields are set
            List<String[]> mapping = new Vector<String[]>();

            for (int i = 0; i < this.tableMap.getColumnCount(); i++) {
                String dbCol = this.tableMap.getColumnName(i);
                String fileCol = (String) this.tableMap.getValueAt(0, i);

                // get mapping init
                if (fileCol != null && !fileCol.contentEquals("")) {
                    String[] m = new String[2];

                    m[XPortImport.ind_db] = dbCol;
                    m[XPortImport.ind_file] = fileCol;
                    mapping.add(m);
                    Logger.getLogger(ImportFileWizardPanel2.class.getName()).log(
                            Level.INFO, "validate: set mapping for col: " + m[1] + " mapping: " + m[0]);

                }
            }

            String msg = this.importModul.setMappingFile2DBColNames(mapping);
            if (msg != null) {
                Logger.getLogger(ImportFileWizardPanel2.class.getName()).log(
                        Level.WARNING, msg);
                throw new WizardValidationException(null, msg, null);

            }

        } catch (Exception e) {
            Logger.getLogger(ImportFileWizardPanel2.class.getName()).log(
                    Level.SEVERE, "validate", e);
            throw new WizardValidationException(null, "validate", e.getMessage());
        }
    }

    public void validate() throws WizardValidationException {
        this.validateMapping();
    }
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    JTable tableData = ((ImportFileVisualPanel2) getComponent()).getJTableData();
    JTable tableMap = ((ImportFileVisualPanel2) getComponent()).getJTableMap();
    String filetype = "";

    public void readSettings(Object settings) {

        this.wd = (WizardDescriptor) settings;
        this.importModul = (XPortExperimentFile) this.wd.getProperty("importModul");

        this.filetype = (String) this.wd.getProperty("filetype");
        try {

            Vector<Vector<String>> datafile = importModul.readData(100);

            Vector<String> colsdata = new Vector<String>(
                    Arrays.asList(importModul.getFileColNames()));

            tableData.setModel(
                    new DefaultTableModel(
                            datafile,
                            colsdata));

            Vector<String> colsmap = new Vector<String>(
                    Arrays.asList(importModul.getDBColNames()));

            Vector<String> map = new Vector<String>();
            for (int i = 0; i < colsmap.size(); i++) {
                map.add("");
            }
            Vector<Vector<String>> datamap = new Vector<Vector<String>>();

            datamap.add(map);
            List<String[]> mapping = importModul.getMappingFile2DBColNames();

            tableMap.setModel(
                    new DefaultTableModel(
                            datamap, colsmap));

            this.tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            this.tableMap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            // content mapping
            for (int i = 0; i < this.tableMap.getColumnCount(); i++) {
                TableColumn col = this.tableMap.getColumnModel().getColumn(i);
                // get filecols 
                Vector<String> filecols = new Vector(Arrays.asList(importModul.getFileColNames()));
                filecols.add(ImportFileWizardPanel2.emptyCol);
                JComboBox box = new JComboBox(filecols);
                box.setPreferredSize(new Dimension(200, 20));
                // get mapping init
                col.setCellEditor(new DefaultCellEditor(box));
                for (String[] m : mapping) {
                    if (m[XPortImport.ind_db].equalsIgnoreCase(this.tableMap.getColumnName(i))) {
                        //box.setSelectedItem(m[XPortImport.ind_file]);
                         this.tableMap.getModel().setValueAt(ImportFileWizardPanel2.emptyCol, 0, i);
                        //this.tableMap.getModel().setValueAt(m[XPortImport.ind_file], 0, i);
                        //140513    kt  set map only if file col really exists, otherwise highlight the column
                        boolean found = false;

                        for (String filecol : colsdata) {
                            if (filecol.contentEquals(m[XPortImport.ind_file])) {
                                this.tableMap.getModel().setValueAt(m[XPortImport.ind_file], 0, i);
                                Logger.getLogger(
                                        ImportFileWizardPanel2.class.getName()).log(
                                                Level.INFO, "readSettings: found for col: " + m[XPortImport.ind_db]
                                                + " mapping: " + m[XPortImport.ind_file]);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            this.tableMap.getColumnModel().getColumn(i).setCellRenderer(new ColorColumnCellRenderer());
                        }

                        Logger.getLogger(ImportFileWizardPanel2.class.getName()).log(
                                Level.INFO, "readSettings: found for col: "
                                + m[XPortImport.ind_db] + " mapping: "
                                + m[XPortImport.ind_file]);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ImportFileWizardPanel2.class.getName()).log(
                    Level.SEVERE, "validate", e);
            this.setValid(false);
        }
    }
  public class ColorColumnCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            c.setBackground(Color.RED);
            return c;
        }

    }

    public void storeSettings(Object settings) {

        ((WizardDescriptor) settings).putProperty("filetype",
                this.filetype);

        ((WizardDescriptor) settings).putProperty("importModul", this.importModul);

    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
    }
}
