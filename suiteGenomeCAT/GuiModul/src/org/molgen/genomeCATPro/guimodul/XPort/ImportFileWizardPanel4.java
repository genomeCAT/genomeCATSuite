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
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInExperiment;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class ImportFileWizardPanel4 implements WizardDescriptor.Panel, DocumentListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component component;
    private WizardDescriptor wd;
    private XPortExperimentFile importModul;
    private String filetype;
    private ExperimentDetail experimentdetail;
    private SampleInExperiment sie1 = null;
    private SampleInExperiment sie2 = null;

    // ExperimentDetailView view = null;
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new ImportFileVisualPanel4();
            ((ImportFileVisualPanel4) getComponent()).getFieldExperimentName().getDocument().addDocumentListener(this);
            ((ImportFileVisualPanel4) getComponent()).getFieldSample1().getDocument().addDocumentListener(this);
            ((ImportFileVisualPanel4) getComponent()).getFieldSample2().getDocument().addDocumentListener(this);

            ((ImportFileVisualPanel4) getComponent()).getExperimentDetailView1().getFieldName().setEditable(false);

            ((ImportFileVisualPanel4) getComponent()).getJCheckBoxDyeSwap().addActionListener(actionListenerDyeSwap);
            ((ImportFileVisualPanel4) getComponent()).getJCheckBoxCenterMean().addActionListener(actionListenerCenterMean);
            ((ImportFileVisualPanel4) getComponent()).getJCheckBoxCenterMedian().addActionListener(actionListenerCenterMedian);
            ((ImportFileVisualPanel4) getComponent()).getJButtonSelectSample1().addActionListener(new ActionListenerSampleSelect(0));
            ((ImportFileVisualPanel4) getComponent()).getJButtonEditSample1().addActionListener(new ActionListenerSampleEdit(0));
            ((ImportFileVisualPanel4) getComponent()).getJButtonSelectSample2().addActionListener(new ActionListenerSampleSelect(1));
            ((ImportFileVisualPanel4) getComponent()).getJButtonEditSample2().addActionListener(new ActionListenerSampleEdit(1));

        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }
    boolean isValid = false;

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return this.isValid;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
        this.fireChangeEvent();
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

    public void validateChanges() {
        Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.INFO,
                "validate changes ");

        //this.experimentdetail = ((ImportFileVisualPanel4) getComponent()).getExperimentDetailView1().getExperiment();
        // check if name is not null or already given
        String experimentName = ((ImportFileVisualPanel4) getComponent()).getFieldExperimentName().getText().toString();

        if (experimentName == null || experimentName.contentEquals("")) {
            wd.putProperty("WizardPanel_errorMessage", "experimentname cannot be null! ");
            this.setIsValid(false);
            return;
        } else {
            this.experimentdetail.setName(experimentName);
        }
        ExperimentDetail d;
        try {
            d = ExperimentService.getExperimentDetailByName(this.experimentdetail.getName());
            if (d != null) {
                // older experiment with same name exists, create unique name, report error

                this.experimentdetail.setName(Utils.getUniquableName(this.experimentdetail.getName()));

                // avoid circle
                Runnable updateName = new Runnable() {

                    public void run() {
                        try {
                            ((ImportFileVisualPanel4) getComponent()).getFieldExperimentName().setText(experimentdetail.getName());

                        } catch (Exception e) {
                            Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.SEVERE,
                                    "error:", e);
                        }
                    }
                };

                SwingUtilities.invokeLater(updateName);

                //wd.putProperty("WizardPanel_errorMessage",
                //         "experiment " + d.getName() + " already exists, name changed.");
            }
        } catch (Exception ex) {
            Logger.getLogger(ImportFileWizardPanel3.class.getName()).log(Level.SEVERE,
                    "error:", ex);
        }

        if (this.experimentdetail.getSamples().size() == 0
                || (this.experimentdetail.getNofChannel() > 1 && this.experimentdetail.getSamples().size() <= 1)) {
            wd.putProperty("WizardPanel_errorMessage", "only " + this.experimentdetail.getSamples().size()
                    + " sample(s) set, but experiment has   " + this.experimentdetail.getNofChannel() + "channel!");
            this.setIsValid(false);
            return;
        }

        this.setIsValid(true);
        this.wd.putProperty("WizardPanel_errorMessage", null);

        //return this.isValid;
    }

    public void validate() throws WizardValidationException {
        this.experimentdetail = ((ImportFileVisualPanel4) getComponent()).getExperimentDetailView1().getExperiment();
        if (!this.importModul.validateExperimentDetail(experimentdetail)) {
            wd.putProperty("WizardPanel_errorMessage", "validateExperimentDetail: not valid");
        }

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

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(
                Level.INFO, "readSettings");

        //((ImportFileVisualPanel4) getComponent()).setExperimentDetailView1(new ExperimentDetailView());
        this.wd = (WizardDescriptor) settings;
        this.importModul = (XPortExperimentFile) this.wd.getProperty("importModul");
        this.filetype = (String) this.wd.getProperty("filetype");

        this.experimentdetail = ((ImportFileVisualPanel4) getComponent()).getExperimentDetailView1().getExperiment();

        boolean newDetail = (this.importModul.getExperimentDetail() == null);
        if (newDetail || !(this.importModul.getExperimentDetail().getPlatform().equals(this.importModul.getPlatformdetail()))) {
            Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(
                    Level.INFO, "init experiment detail");

            this.experimentdetail.copy(this.importModul.initExperimentDetail());

        } else {
            Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(
                    Level.INFO, "old experiment detail");
            this.experimentdetail.copy(this.importModul.getExperimentDetail());
        }

        // experiment managment
        try {

            ((ImportFileVisualPanel4) getComponent()).getFieldExperimentName().setText(this.experimentdetail.getName());

            // sample managment
            if (newDetail) {
                importModul.initSampleList(this.experimentdetail);
            }
            //todo check if samples exists
            List<SampleInExperiment> list = this.experimentdetail.getSamples();

            this.setSamples();
            this.updateSamples(true, true);

            getComponent().repaint();

        } catch (Exception ex) {
            Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(Level.SEVERE,
                    "Error: ", ex);
            wd.putProperty("WizardPanel_errorMessage", ex.getMessage());
        }
    }
    boolean centerMean = false;
    boolean centerMedian = false;
    boolean dyeSwap = false;
    ActionListener actionListenerCenterMedian = new ActionListener() {

        public void actionPerformed(ActionEvent actionEvent) {
            AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
            centerMedian = abstractButton.getModel().isSelected();
            importModul.setCenterMedian(centerMedian);
            importModul.setCenterMean(!centerMedian);
            ((ImportFileVisualPanel4) getComponent()).getJCheckBoxCenterMean().setSelected(!centerMedian);
        }
    };
    ActionListener actionListenerCenterMean = new ActionListener() {

        public void actionPerformed(ActionEvent actionEvent) {
            AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
            centerMean = abstractButton.getModel().isSelected();
            importModul.setCenterMean(centerMean);
            importModul.setCenterMedian(!centerMean);
            ((ImportFileVisualPanel4) getComponent()).getJCheckBoxCenterMedian().setSelected(!centerMean);
        }
    };
    ActionListener actionListenerDyeSwap = new ActionListener() {

        public void actionPerformed(ActionEvent actionEvent) {
            AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
            dyeSwap = abstractButton.getModel().isSelected();
            importModul.setDyeSwap(dyeSwap);

        }
    };

    /**
     *
     * @param list
     * @throws java.lang.Exception
     */
    private void setSamples() throws Exception {
        if (this.experimentdetail.getSamples() != null && this.experimentdetail.getSamples().size() > 0) {
            this.sie1 = this.experimentdetail.getSamples().get(0);
        } else {
            this.sie1 = null;
        }
        if (this.experimentdetail.getSamples() != null && this.experimentdetail.getSamples().size() > 1) {
            this.sie2 = this.experimentdetail.getSamples().get(1);
        } else {
            this.sie2 = null;
        }
        ((ImportFileVisualPanel4) getComponent()).getHintSample1().setText("");
        ((ImportFileVisualPanel4) getComponent()).getHintSample2().setText("");

        //check if sample already exists
        SampleDetail d = null;
        if (sie1 != null) {
            d = ExperimentService.getSampleDetailByName(this.sie1.getName());
        }
        if (d != null) {
            wd.putProperty("WizardPanel_errorMessage", "sample " + sie1.getName() + " already exists");
            ((ImportFileVisualPanel4) getComponent()).getHintSample1().setText(
                    "<html>sample " + sie1.getName() + " <br/>already exists.</html>");
            this.experimentdetail.removeSample(sie1);
            SampleInExperiment sie = experimentdetail.addSample(d, sie1.isIsCy3(), sie1.isIsCy5());

            //this.sie1.setName(sie1.getName() + "_" + Calendar.getInstance().getTimeInMillis());
        }
        if (sie2 != null) {
            d = ExperimentService.getSampleDetailByName(this.sie2.getName());

            if (d != null) {
                wd.putProperty("WizardPanel_errorMessage", "sample " + sie1.getName() + " already exists, name changed.");

                ((ImportFileVisualPanel4) getComponent()).getHintSample2().setText(
                        "<html>sample " + sie2.getName() + " <br/>already exists</html>");
                this.experimentdetail.removeSample(sie2);
                SampleInExperiment sie = experimentdetail.addSample(d, sie2.isIsCy3(), sie2.isIsCy5());

                //this.sie2.setName(sie2.getName() + "_" + Calendar.getInstance().getTimeInMillis());
            }
        }
    }

    private void updateSamples(boolean edit1, boolean edit2) throws Exception {
        ((ImportFileVisualPanel4) getComponent()).getFieldSample1().setText("");
        ((ImportFileVisualPanel4) getComponent()).getFieldSample2().setText("");
        ((ImportFileVisualPanel4) getComponent()).getJLabelSampl1().setText("Sample1");
        ((ImportFileVisualPanel4) getComponent()).getJLabelSample2().setText("Sample2");

        ((ImportFileVisualPanel4) getComponent()).getFieldSample1().setText(this.sie1.getName());

        if (this.sie1.isIsCy3()) {
            ((ImportFileVisualPanel4) getComponent()).getJLabelSampl1().setText("Cy3*:");
        }
        if (this.sie1.isIsCy5()) {
            ((ImportFileVisualPanel4) getComponent()).getJLabelSampl1().setText("Cy5*:");
        }

        ((ImportFileVisualPanel4) getComponent()).getJButtonSelectSample1().setVisible(edit1);

        if (this.experimentdetail.getNofChannel() >= 2) {
            ((ImportFileVisualPanel4) getComponent()).getFieldSample2().setText(sie2.getName());

            if (sie2.isIsCy3()) {
                ((ImportFileVisualPanel4) getComponent()).getJLabelSample2().setText("Cy3*:");
            }
            if (sie2.isIsCy5()) {
                ((ImportFileVisualPanel4) getComponent()).getJLabelSample2().setText("Cy5*:");
            }

            ((ImportFileVisualPanel4) getComponent()).getJButtonSelectSample2().setVisible(edit2);
            ((ImportFileVisualPanel4) getComponent()).getJButtonEditSample2().setVisible(edit2);
        } else {
            ((ImportFileVisualPanel4) getComponent()).getJLabelSample2().setText("");
            ((ImportFileVisualPanel4) getComponent()).getJButtonSelectSample2().setVisible(false);
            ((ImportFileVisualPanel4) getComponent()).getJButtonEditSample2().setVisible(false);
        }

        // todo edit in experimentdetailview for sample 
    }

    class ActionListenerSampleEdit implements ActionListener {

        int index = -1;

        ActionListenerSampleEdit(int s) {
            index = s;
        }

        public void actionPerformed(ActionEvent e) {
            boolean isCy3 = false, isCy5 = false;

            try {

                SampleInExperiment sieOld = null;
                String sampleName = (index == 0 ? ((ImportFileVisualPanel4) getComponent()).getFieldSample1().getText() : ((ImportFileVisualPanel4) getComponent()).getFieldSample2().getText());
                sampleName = (String) JOptionPane.showInputDialog(null,
                        "enter new name",
                        "create/edit sample",
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, sampleName);
                if (sampleName == null) {
                    return;
                }

                Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(Level.INFO,
                        "change value for sample name " + index + " to " + sampleName);

                SampleDetail _d = ExperimentService.getSampleDetailByName(sampleName);

                String old_name = null;
                if (index == 1 && sie2 != null) {
                    isCy3 = sie2.isIsCy3();
                    isCy5 = sie2.isIsCy5();
                    old_name = sie2.getName();

                    //experimentdetail.getSamples().remove(sie2);
                }
                if (index == 0 && sie1 != null) {
                    isCy3 = sie1.isIsCy3();
                    isCy5 = sie1.isIsCy5();
                    old_name = sie1.getName();
                    //experimentdetail.getSamples().remove(sie1);
                }

                // remove by name and channel
                if (old_name != null) {
                    for (SampleInExperiment sie : experimentdetail.getSamples()) {
                        if (sie.getName().contentEquals(old_name) && sie.isIsCy3() == isCy3) {
                            sieOld = sie;

                            break;
                        }
                    }
                }
                //experimentdetail.getSamples().remove(sieOld);
                if (sieOld != null) {
                    experimentdetail.removeSample(sieOld);
                }
                if (_d == null) {
                    _d = new SampleDetail();
                    _d.setName(sampleName);
                }
                SampleInExperiment sie = experimentdetail.addSample(_d, isCy3, isCy5);

                if (index == 0) {
                    sie1 = sie;
                    ((ImportFileVisualPanel4) getComponent()).getHintSample1().setText("");

                } else {
                    sie2 = sie;
                    ((ImportFileVisualPanel4) getComponent()).getHintSample2().setText("");
                }
                updateSamples(true, true);

            } catch (Exception ex) {
                Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(Level.SEVERE,
                        "Error: ", ex);
                wd.putProperty(
                        "WizardPanel_errorMessage", ex.getMessage());
            }
        }
    };

    class ActionListenerSampleSelect implements ActionListener {

        int index = -1;

        ActionListenerSampleSelect(int s) {
            index = s;
        }

        public void actionPerformed(ActionEvent e) {

            final JPanel panel = new JPanel();
            List<SampleDetail> list = null;
            Vector<String> names = new Vector<String>();
            try {
                list = ExperimentService.listSampleDetails();
                if (list == null) {
                    list = new Vector<SampleDetail>();
                }

                for (SampleDetail d : list) {
                    names.add(d.getName());
                }
                final JComboBox combo = new JComboBox(names);
                combo.setEditable(true);
                panel.add(combo);
                boolean isCy3 = false, isCy5 = false;

                if (JOptionPane.showConfirmDialog(null, panel,
                        "choose sample", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                    Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(Level.INFO,
                            "select new sample");
                    String name = combo.getSelectedItem().toString();
                    for (SampleDetail d : list) {
                        if (d.getName().contentEquals(name)) {

                            String old_name = "";
                            if (index == 1 && sie2 != null) {
                                isCy3 = sie2.isIsCy3();
                                isCy5 = sie2.isIsCy5();
                                old_name = sie2.getName();

                                //experimentdetail.getSamples().remove(sie2);
                            }
                            if (index == 0 && sie1 != null) {
                                isCy3 = sie1.isIsCy3();
                                isCy5 = sie1.isIsCy5();
                                old_name = sie1.getName();
                                //experimentdetail.getSamples().remove(sie1);
                            }
                            SampleInExperiment rSie = null;
                            for (SampleInExperiment sie : experimentdetail.getSamples()) {
                                if (sie.getName().contentEquals(old_name) && sie.isIsCy3() == isCy3) {
                                    rSie = sie;

                                    break;
                                    //experimentdetail.getSamples().remove(sie);
                                }
                            }
                            // experimentdetail.getSamples().remove(rSie);
                            if (rSie != null) {
                                experimentdetail.removeSample(rSie);
                            }
                            SampleInExperiment sie = experimentdetail.addSample(d, isCy3, isCy5);

                            if (index == 0) {
                                sie1 = sie;
                                ((ImportFileVisualPanel4) getComponent()).getHintSample1().setText("");
                            } else {
                                sie2 = sie;
                                ((ImportFileVisualPanel4) getComponent()).getHintSample2().setText("");
                            }
                            updateSamples(true, true);
                        }
                    }
                }
            } catch (Exception ex) {
                wd.putProperty("WizardPanel_errorMessage", "Error set sample name");
                Logger.getLogger(ImportFileWizardPanel4.class.getName()).log(Level.SEVERE,
                        "Error: ", ex);

            }
        }
    };

    public void storeSettings(Object settings) {
        //this.experimentdetail = ((ImportFileVisualPanel4) getComponent()).getExperimentDetailView1().getExperiment();
        this.importModul.setExperimentDetail(this.experimentdetail);
        ((WizardDescriptor) settings).putProperty("importModul", this.importModul);
        ((WizardDescriptor) settings).putProperty("filetype",
                this.filetype);

    }
}
