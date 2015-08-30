/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.XPort;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;

import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.PlatformService;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 * 
 * @author tebel
 *  Step 3: select Platform or select new one
 */
public class ImportFileWizardPanel3
        implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel,
        DocumentListener {

    /**
     * listen to changes on platformdetail, release, platformdata (combobox)
     * validateChanges: 
     *      platformDetail != null && platformName or release != null
     *      if release and platformName == null
     *          check if platform has release
     *          -> set data
     * validate: 
     *      get platformdata
     *      ggf reset release
     * readSettings: 
     *      importModul ggf release, type, method
     *      get platformList
     *      get detai, get Data
     * storeSettings:
     *      importModul.setDetail, setData
     * 
     * change platform (button, actionlistener)
     *      set this.detail
     *      set list platformdata (combobox)
     */
    private Component component;
    //XPortPlatform importModulPlatform = null;
    XPortExperimentFile importModul = null;
    private WizardDescriptor wd = null;
    //private String filetype = null;
    String platformName = "";
    String platformDataName = "";
    String method = "";
    String type = "";

    public Component getComponent() {
        if (component == null) {
            component = new ImportFileVisualPanel3();
            // listen for changes at required fields: platform, platformdata
            ((ImportFileVisualPanel3) getComponent()).getFieldPlatform().getDocument().addDocumentListener(this);
            ((ImportFileVisualPanel3) getComponent()).getFieldRelease().getDocument().addDocumentListener(this);

            final JTextComponent tc = (JTextComponent) ((ImportFileVisualPanel3) getComponent()).getJComboBoxPlatformData().getEditor().getEditorComponent();
            tc.getDocument().addDocumentListener(this);
            ((ImportFileVisualPanel3) getComponent()).getJComboBoxPlatformData().setModel(
                    new DefaultComboBoxModel(new String[]{""}));
            // set action listener

            ((ImportFileVisualPanel3) getComponent()).getJButtonSelectType().addActionListener(
                    actionListenerSelectType);

            ((ImportFileVisualPanel3) getComponent()).getJButtonSelectMethod().addActionListener(
                    actionListenerSelectMethod);
            // listening to change platform
            ((ImportFileVisualPanel3) getComponent()).getJButtonSelectPlatform().addActionListener(
                    actionListenerPlatforms);

        }

        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }
    List<PlatformDetail> platforms = new Vector<PlatformDetail>();
    PlatformDetail detail = null;
    PlatformData data = null;

    public String[] convertPlatformList(java.util.List<PlatformDetail> list) {

        String[] namelist = new String[list != null ? list.size() : 0];
        for (int i = 0; i < (list != null ? list.size() : 0); i++) {
            namelist[i] = (list.get(i).getName());
        }
        return namelist;
    }

    public String[] convertPlatformDataList(java.util.List<PlatformData> list) {

        String[] namelist = new String[list != null ? list.size() : 0];
        for (int i = 0; i < (list != null ? list.size() : 0); i++) {
            namelist[i] = (list.get(i).getName() + list.get(i).getGenomeRelease());
        }
        return namelist;
    }
    /**
     * create new platform
     * create and init create platform dialog
     * initiated by pressing "create"
     * 
     */
    /**
     * switch new PlatformDetail
     */
    ActionListener actionListenerPlatforms = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO,
                    "actionListenerCreate: select new platform ");

            try {
                final JPanel panel = new JPanel();
                //platforms = importModul.getPlatformList();
                String[] possibilities = convertPlatformList(platforms);
                Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO,
                        "actionListenerCreate: platformdetails: " + possibilities.toString());
                final JComboBox combo = new JComboBox(possibilities);
                combo.setEditable(true);
                panel.add(combo);
                if (JOptionPane.showConfirmDialog(null, panel,
                        "choose platform", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                    setPlatform(combo.getSelectedItem().toString());

                }
            } catch (Exception ex) {
                wd.putProperty("WizardPanel_errorMessage",
                        ex.getMessage());

            }

        }
    };

    /**
     * set release with warning if diff from importModul
     * @param release
     */
    /**
     * set platform field and platform data combobox
     * check if type and method (if chosen) match
     * check if release (if chosen) matches
     * @param d
     * @return
     * @throws java.lang.Exception
     */
    boolean setPlatform(String platformName) throws Exception {
        this.platformName = platformName;

        this.detail = findDetailByName(platformName);


        ((ImportFileVisualPanel3) getComponent()).getFieldPlatform().setText(
                platformName);
        //type = ((ImportFileVisualPanel3) getComponent()).getFieldType().getText();
        String release = ((ImportFileVisualPanel3) getComponent()).getFieldRelease().getText();
        //method = ((ImportFileVisualPanel3) getComponent()).getFieldMethod().getText();
        //check type
        /*if (type != null && !type.equals("") && !d.getType().contentEquals(type)) {
        wd.putProperty("WizardPanel_errorMessage",
        "platform type (" + d.getType() + ") not same as " + type);
        }
        //check method
        if (method != null && !method.equals("") && !d.getMethod().contentEquals(method)) {
        wd.putProperty("WizardPanel_errorMessage",
        "platform method (" + d.getMethod() + ") not same as " + method);
        }*/
        //((ImportFileVisualPanel3) getComponent()).getFieldPlatform().setText(d.getName());
        //check release
        checkPlatformRelease(this.detail.getPlatformID(), release);

        List<PlatformData> list = PlatformService.getPlatformDataByDetailId(this.detail.getPlatformID());


        // set platformlist
        ((ImportFileVisualPanel3) getComponent()).getJComboBoxPlatformData().setModel(
                new DefaultComboBoxModel(convertPlatformDataList(list)));
        return true;

    }

    /**
     * check if platform has the choosen release
     * @param d
     * @return
     * @throws java.lang.Exception
     */
   PlatformData checkPlatformRelease(Long platformid, String release) throws Exception {

        //String release = ((ImportFileVisualPanel3) getComponent()).getFieldRelease().getText();
        if (release != null && !release.equals("")) {
            PlatformData d = PlatformService.getPlatformForRelease(platformid, release);
            if (d == null) {
                this.wd.putProperty("WizardPanel_errorMessage",
                        "warning:, platform has no data for " + release + " !");
            }
            else return d;
        }

        return null;
    }

    /**
     * find Object in certain list by Name
     * @param arrayName
     * @return
     */
    PlatformDetail findDetailByName(String arrayName) {
        for (PlatformDetail a : this.platforms) {
            if (a.getName().contentEquals(arrayName)) {
                Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO,
                        "findDetailByName: " + a.toFullString());

                return a;
            }
        }
        this.wd.putProperty("WizardPanel_errorMessage",
                "findDetailByName: not found  -" + arrayName);
        return null;
    }

    PlatformData findDataByName(String name, List<PlatformData> list) {
        PlatformData ra;
        for (PlatformData a : list) {
            if (name.contains(a.getName()) && name.contains(a.getGenomeRelease())) {
                Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO,
                        "findDataByName: " + a.toFullString());

                return a;
            }
        }
        wd.putProperty("WizardPanel_errorMessage",
                "findDataByName: not found  -" + name);
        return null;
    }

    /**
     * init global variables from from passed visual panels
     * init relevant fields from gui (visual panel)
     * @param settings
     */
    public void readSettings(Object settings) {
        //init locals
        this.wd = (WizardDescriptor) settings;
        this.importModul = (XPortExperimentFile) this.wd.getProperty("importModul");

        importModul.readData(1);
        this.type = importModul.getType();
        this.method = importModul.getMethod();
        // 131211
        this.detail = importModul.getPlatformdetail();
        this.data = importModul.getPlatformdata();
        if (detail != null) {
            try {
                //workaround for back button
                this.setPlatform(detail.getName());
            } catch (Exception ex) {
                Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.SEVERE,
                        " ", ex);
                this.detail = null;

            }
        } else {
            this.platformName = "";
        }
        String release;
        if (data != null) {

            this.platformDataName = data.getName();
            release = data.getGenomeRelease();
        } else {
            release = (this.importModul.getRelease());

            if (release == null) {
                release = Defaults.GenomeRelease.hg18.toString();
            }
            this.platformDataName = "";
        }
        try {
            this.platforms = importModul.getPlatformList(null, null);
        } catch (Exception ex) {
            wd.putProperty("WizardPanel_errorMessage", ex.getMessage());
        }

        //init field content

        ((ImportFileVisualPanel3) getComponent()).getFieldRelease().setText(release);

        ((ImportFileVisualPanel3) getComponent()).getFieldMethod().setText(method);


        ((ImportFileVisualPanel3) getComponent()).getFieldType().setText(type);

        ((ImportFileVisualPanel3) getComponent()).getJTextFileInfo().setText(
                this.importModul.getFileInfoAsHTML());
        ((ImportFileVisualPanel3) getComponent()).getFieldPlatform().setText(platformName);


        ((ImportFileVisualPanel3) getComponent()).getJComboBoxPlatformData().setSelectedItem(platformName);


    //this.validateChanges();
    }
    ActionListener actionListenerSelectType = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            final JPanel panel = new JPanel();
            Vector<String> possibilities = DBUtils.getAllArrayTypes();
            final JComboBox combo = new JComboBox(possibilities);
            combo.setEditable(true);
            panel.add(combo);
            if (JOptionPane.showConfirmDialog(null, panel,
                    "choose method", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                type = (String) combo.getSelectedItem();
                ((ImportFileVisualPanel3) getComponent()).getFieldType().setText(type);
                //importModul.setType(Defaults.Type.toType(type));
                try {
                    platforms = importModul.getPlatformList(type, method);
                } catch (Exception ex) {
                    wd.putProperty("WizardPanel_errorMessage", ex.getMessage());
                }
            }
        }
    };
    ActionListener actionListenerSelectMethod = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            final JPanel panel = new JPanel();
            Vector<String> possibilities = DBUtils.getAllArrayMethods();
            final JComboBox combo = new JComboBox(possibilities);
            combo.setEditable(true);
            panel.add(combo);
            if (JOptionPane.showConfirmDialog(null, panel,
                    "choose method", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                method = (String) combo.getSelectedItem();
                ((ImportFileVisualPanel3) getComponent()).getFieldMethod().setText(method);
                //importModul.setMethod(Defaults.Method.toMethod(method));
                try {
                    platforms = importModul.getPlatformList(type, method);
                } catch (Exception ex) {
                    wd.putProperty("WizardPanel_errorMessage", ex.getMessage());
                }
            }
        }
    };

    public void validate() throws WizardValidationException {
        Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO, "validate");
        this.isValid = false;
        try {

            if (this.data == null) {
                // choose data by given name
                List<PlatformData> list;

                list = PlatformService.getPlatformDataByDetailId(detail.getPlatformID());
                Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO,
                        "validate: ",
                        "platform data:" + platformDataName);
                this.data = this.findDataByName(platformDataName, list);

            }

        } catch (Exception ex) {
            wd.putProperty("WizardPanel_errorMessage", ex.getMessage());
            throw new WizardValidationException(null, "validate", ex.getMessage());

        }
        if (this.detail == null || this.data == null) {
            wd.putProperty("WizardPanel_errorMessage", "no platform or release version selected!");

            throw new WizardValidationException(null, "validate", "no platform or release version selected!");
        //wd.putProperty("WizardPanel_errorMessage", "no platform or release version selected!");
        } else {
            isValid = true;
        }
    }

    public void storeSettings(Object settings) {

        this.importModul.setPlatformdetail(detail);
        this.importModul.setPlatformdata(data);
        if (data != null) {
            this.importModul.setRelease(Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));
        }
        ((WizardDescriptor) settings).putProperty("importModul", this.importModul);
        this.component = null;

    }
    boolean isValid = false;

    public boolean isValid() {
        return this.isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
        this.fireChangeEvent();
    }

    public void validateChanges() {
        String _release = (((ImportFileVisualPanel3) getComponent()).getFieldRelease().getText());

        Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO,
                "validate changes (release: " + _release);
        // method = ((ImportFileVisualPanel3) getComponent()).getFieldMethod().getText();
        platformDataName = ((ImportFileVisualPanel3) getComponent()).getJComboBoxPlatformData().getSelectedItem().toString();

        if (this.detail == null) {

            this.wd.putProperty("WizardPanel_errorMessage",
                    "empty platform, please one of the list via button <select>");
            this.setIsValid(false);
            return;
        }
        if (platformDataName == null || platformDataName.equals("")) {
            // no platform version selected (platformlist)
            if (_release == null || _release.equals("")) {
                this.wd.putProperty("WizardPanel_errorMessage",
                        "empty platform version, please choose one of the list or select release!");
                this.setIsValid(false);
                return;

            } // try to find a suitable platform version

        }
        //try to find a suitable platform version
        try {

            this.data = this.checkPlatformRelease(this.detail.getPlatformID(), _release);
            if (this.data == null) {
                Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.SEVERE,
                        "wrong release, platform has no data for " + _release + " !");
                this.wd.putProperty("WizardPanel_errorMessage",
                        "wrong release, platform has no data for " + _release + " !");
                this.setIsValid(false);
                return;
            }
            this.setIsValid(true);

            this.wd.putProperty("WizardPanel_errorMessage", null);

        /*((ImportFileVisualPanel3) getComponent()).getFieldRelease().getDocument().removeDocumentListener(this);
        ((ImportFileVisualPanel3) getComponent()).getFieldRelease().setText(data.getGenomeRelease().toString());
        ((ImportFileVisualPanel3) getComponent()).getFieldRelease().getDocument().addDocumentListener(this);
         */
        } catch (Exception ex) {
            Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.SEVERE,
                    ex.getMessage(), ex);
            this.data = null;
            this.setIsValid(false);
        }





    //return this.isValid;
    }

    public void insertUpdate(DocumentEvent e) {
        this.validateChanges();
    }

    public void removeUpdate(DocumentEvent e) {
        this.validateChanges();
    }

    public void changedUpdate(DocumentEvent e) {
        this.validateChanges();
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

    private void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
}

   

