/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.XPort;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.molgen.genomeCATPro.cghpro.xport.ServiceXPort;
import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;

import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;
// controler for one step1 (Panel1) in ImportFileWizard

public class ImportFileWizardPanel1 implements
        WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel,
        DocumentListener {

    /**
     * select file & filetye (validate)
     * choose import modul
     * save import modul
     */
    private Component component;
    XPortExperimentFile importModul = null;
    private WizardDescriptor wd = null;
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.

    public Component getComponent() {
        if (component == null) {
            component = new ImportFileVisualPanel1();
            ((ImportFileVisualPanel1) getComponent()).getJTextFieldFileName().getDocument().addDocumentListener(this);

        } else {
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }

    public void validateChanges() {

        if (((ImportFileVisualPanel1) getComponent()).getJTextFieldFileName().getText() == null || ((ImportFileVisualPanel1) getComponent()).getJTextFieldFileName().getText().toString().contentEquals("")) {

            this.wd.putProperty("WizardPanel_errorMessage", "empty filepath, please choose file");
            this.setIsValid(false);
            return;
        }
        /*if (((ImportFileVisualPanel1) getComponent()).getCbFileType().getSelectedIndex() < 0) {
        this.wd.putProperty("WizardPanel_errorMessage", "empty filetype, please choose type");
        return false;
        }*/
        this.setIsValid(true);

        this.wd.putProperty("WizardPanel_errorMessage", null);

    //return this.isValid;
    }
    boolean isValid = false;

    @Override
    public boolean isValid() {

        return isValid;

    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
        this.fireChangeEvent();
    }

    public void validate() throws WizardValidationException {
        String filetype = ((ImportFileVisualPanel1) getComponent()).getCbFileType().getSelectedItem().toString();
        importModul = ServiceXPort.getXPortImport(filetype);

        /* #Überprüfen, ob Filetyp zum File paßt
        zB. variiert das FileFormat entspr. der VersionsNummer der Analysesoftware
        
         */
        if (importModul == null) {
            throw new WizardValidationException(null, "no import modul found", null);
        }
        try {
            importModul.newImportFile((((ImportFileVisualPanel1) getComponent()).getJTextFieldFileName().getText().toString()));
        } catch (Exception e) {
            throw new WizardValidationException(null, e.getMessage(), "file empty?");
        }

    }

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    //initialize step
    @SuppressWarnings("empty-statement")
    public void readSettings(Object settings) {

        this.importModul = null;
        this.wd = (WizardDescriptor) settings;
        ((ImportFileVisualPanel1) getComponent()).getJTextFieldFileName().setText("");

        ((ImportFileVisualPanel1) getComponent()).getCbFileType().setModel(new javax.swing.DefaultComboBoxModel(ServiceXPort.getFileTypesImport()));
        try {
            ((ImportFileVisualPanel1) getComponent()).setFileDirectory(
                    NbPreferences.forModule(ImportFileWizardPanel1.class).get("pathPreference", ""));
        } catch (java.lang.Exception e) {
            ;
        }
    }

    // set properties to  WizardDescriptor
    public void storeSettings(Object settings) {
        ((WizardDescriptor) settings).putProperty("filename",
                ((ImportFileVisualPanel1) getComponent()).getJTextFieldFileName().getText());

        ((WizardDescriptor) settings).putProperty("filetype",
                ((ImportFileVisualPanel1) getComponent()).getCbFileType().getSelectedItem().toString());

        ((WizardDescriptor) settings).putProperty("importModul", this.importModul);

        //   ((WizardDescriptor) settings).putProperty("name", ((DemoVisualPanel1)getComponent()).getNameField());
        // DemoWizardAction.java: String name = (String) wizardDescriptor.getProperty("name");
        // make propertie persistent
        NbPreferences.forModule(ImportFileWizardPanel1.class).put("pathPreference",
                ((ImportFileVisualPanel1) getComponent()).getFileDirectory());
    //this.component = null;

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


