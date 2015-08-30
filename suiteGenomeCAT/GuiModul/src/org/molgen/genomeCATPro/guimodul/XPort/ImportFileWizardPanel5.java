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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;
import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class ImportFileWizardPanel5 implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component component;
    private WizardDescriptor wd;
    private XPortExperimentFile importModul;
    private String filetype;
    private ExperimentData experimentdata;
    boolean done = false;
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.

    public Component getComponent() {
        if (component == null) {
            component = new ImportFileVisualPanel5();
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return done;
    // If it depends on some condition (form filled out...), then:
    // return someCondition();
    // and when this condition changes (last form field filled in...) then:
    // fireChangeEvent();
    // and uncomment the complicated stuff below.
    }
    /*
    public final void addChangeListener(ChangeListener l) {
    }
    
    public final void removeChangeListener(ChangeListener l) {
    }
     */
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


    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {


        try {
            this.wd = (WizardDescriptor) settings;
            this.importModul = (XPortExperimentFile) this.wd.getProperty("importModul");
            this.filetype = (String) this.wd.getProperty("filetype");
            ((ImportFileVisualPanel5) getComponent()).getJTextMsg().setText("");
            this.experimentdata = ((ImportFileVisualPanel5) getComponent()).getExperimentDataView1().getData();

            this.experimentdata.copy(this.importModul.getExperimentData());
            ((ImportFileVisualPanel5) getComponent()).getJButtonImport().addActionListener(actionListenerRunImport);
        } catch (Exception e) {
            wd.putProperty("WizardPanel_errorMessage", "error: see logfile");
        }
    /*
    if (importModul.getNofChannel() == 1) {
    ((ImportFileVisualPanel5) getComponent()).getJCheckBoxCenterMedian().setSelected(true);
    }*/
    }
    ActionListener actionListenerRunImport = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            //((ImportFileVisualPanel5) getComponent()).getJButtonImport().setEnabled(false);

            InformableHandler informable = new InformableHandler() {

                public void messageChanged(String message) {
                    ((ImportFileVisualPanel5) getComponent()).getJTextMsg().append(message + "\n");
                }
            };
            informable.messageChanged("Start Import....");
            //informable.messageChanged("Get Data for " + sample.getName());
            ImportWorker worker = new ImportWorker(importModul, informable);
            worker.execute();
        }
    };

    public void validate() throws WizardValidationException {
        this.experimentdata = ((ImportFileVisualPanel5) getComponent()).getExperimentDataView1().getData();
        if (this.experimentdata == null) {
            wd.putProperty("WizardPanel_errorMessage", "validate ExperimentData: not valid");
        }
    }

    public void storeSettings(Object settings) {
        ((WizardDescriptor) settings).putProperty("importModul", this.importModul);
        ((WizardDescriptor) settings).putProperty("filetype",
                this.filetype);
        ((WizardDescriptor) settings).putProperty("experimentdata",
                this.experimentdata);
        ((WizardDescriptor) settings).putProperty("experiment",
                this.importModul.getExperimentDetail());

        this.component = null;
    }

    public class ImportWorker extends SwingWorker<ExperimentData, String> {

        XPortExperimentFile importModul;
        private final InformableHandler informable;

        public ImportWorker(XPortExperimentFile _port, InformableHandler inf) {
            this.importModul = _port;
            this.informable = inf;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String message : chunks) {
                informable.messageChanged(message);
            }
        }

        protected ExperimentData doInBackground() throws Exception {
            publish("run Import in Background...");
            setProgress(0);


            setProgress(10);
            ExperimentData d = null;
            try {
                d = importModul.doImportFile(informable);
            } catch (Exception ex) {
                publish("error during import see logfile !");
                Logger.getLogger(ImportWorker.class.getName()).log(Level.WARNING,
                        "error during import !", ex);

            }
            setProgress(90);

            return d;


        }

        @Override
        protected void done() {
            try {
                // Get the number of matches. Note that the
                // method get will throw any exception thrown
                // during the execution of the worker.
                ExperimentData d = get();

                if (d != null) {
                    publish(" done ...........................");
                    ((ImportFileVisualPanel5) getComponent()).getExperimentDataView1().getData().copy(d);
                } else {
                    publish(" ERROR see above for further details");
                }
                done = true;
                fireChangeEvent();


            //progressBar.setVisible(false);
            } catch (Exception e) {
                Logger.getLogger(ImportWorker.class.getName()).log(Level.WARNING, "run import ", e);

            }
        }
    }
}

