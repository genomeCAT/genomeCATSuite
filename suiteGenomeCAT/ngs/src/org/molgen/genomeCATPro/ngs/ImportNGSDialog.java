package org.molgen.genomeCATPro.ngs;

/**
 * @name ImportNGSDialog
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.molgen.genomeCATPro.cghpro.xport.ImportTrack;
import org.molgen.genomeCATPro.cghpro.xport.ImportTrackWIG;
import org.molgen.genomeCATPro.cghpro.xport.ServiceXPort;
import org.molgen.genomeCATPro.cghpro.xport.XPortTrack;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.InformableHandler;

import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInTrack;
import org.molgen.genomeCATPro.datadb.dbentities.Track;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.TrackService;
import org.openide.util.NbPreferences;

public class ImportNGSDialog extends javax.swing.JDialog {

    private XPortNGS processNGSModul = null;
    String filetype = null;
    SampleInTrack sie1 = new SampleInTrack();
    SampleInTrack sie2 = new SampleInTrack();

    /**
     * default constructor
     * @param parent frame
     */
    public ImportNGSDialog(java.awt.Frame parent) {
        super(parent, false);
        this.processNGSModul = null;


        initComponents();
        cbFileType.setSelectedIndex(0);


        Logger.getLogger(ImportNGSDialog.class.getName()).log(
                Level.INFO, "constructor ");

        this.jButtonSelectSampleData.addActionListener(new ActionListenerSampleSelect(0));
        this.jButtonSelectSampleControl.addActionListener(new ActionListenerSampleSelect(1));
        this.jButtonEditSampleData.addActionListener(new ActionListenerSampleEdit(0));
        this.jButtonEditSampleControl.addActionListener(new ActionListenerSampleEdit(1));


        this.jPanelFile.setEnabled(true);
        this.jTabbedPaneMain.setEnabledAt(0, true);
        this.jTabbedPaneMain.setEnabledAt(1, false);
        this.jTabbedPaneMain.setEnabledAt(2, false);

        this.jButtonRun.setEnabled(false);
        this.jButtonImport.setEnabled(false);



        setLocationRelativeTo(null);
    }

    /**
     * constructor with preselected import module (mainly used for tests)
     * @param parent
     * @param mod - import module
     */
    public ImportNGSDialog(java.awt.Frame parent, XPortNGS mod) {
        super(parent, false);
        initComponents();
        this.processNGSModul = mod;
        Logger.getLogger(ImportNGSDialog.class.getName()).log(
                Level.INFO, "constructor with import module " + mod.getModulName());

        this.jButtonSelectSampleData.addActionListener(new ActionListenerSampleSelect(0));
        this.jButtonSelectSampleControl.addActionListener(new ActionListenerSampleSelect(1));
        this.jButtonEditSampleData.addActionListener(new ActionListenerSampleEdit(0));
        this.jButtonEditSampleControl.addActionListener(new ActionListenerSampleEdit(1));


        this.jPanelFile.setEnabled(true);
        this.jTabbedPaneMain.setEnabledAt(0, true);
        this.jTabbedPaneMain.setEnabledAt(1, false);
        this.jTabbedPaneMain.setEnabledAt(2, false);

        this.jButtonRun.setEnabled(false);
        this.jButtonImport.setEnabled(false);

        setLocationRelativeTo(null);
        this.initNGSProcParameter();
    }

    public ImportNGSDialog(java.awt.Frame parent, BAMImport bamimport) {
        super(parent, false);
        initComponents();


        //skip to import 

        Logger.getLogger(ImportNGSDialog.class.getName()).log(
                Level.INFO, "constructor with import module as test ");

        this.jButtonSelectSampleData.addActionListener(new ActionListenerSampleSelect(0));
        this.jButtonSelectSampleControl.addActionListener(new ActionListenerSampleSelect(1));
        this.jButtonEditSampleData.addActionListener(new ActionListenerSampleEdit(0));
        this.jButtonEditSampleControl.addActionListener(new ActionListenerSampleEdit(1));


        this.jPanelFile.setEnabled(true);
        this.jTabbedPaneMain.setEnabledAt(0, false);
        this.jTabbedPaneMain.setEnabledAt(1, false);
        this.jTabbedPaneMain.setEnabledAt(2, true);

        this.jButtonRun.setEnabled(false);
        this.jButtonImport.setEnabled(false);

        setLocationRelativeTo(null);

        this.processNGSModul = bamimport;
        this.initEditImport();
    }

    /**
     * error method - log error and set message as hint at dialog 
     * @param message
     * @param e
     */
    void error(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message);
        Logger.getLogger(ImportNGSDialog.class.getName()).log(
                Level.WARNING, message, e);

    }
///// tab #1 /////////////////////////////////////////////////////////////////

    /**
     * get & init selected NGS Import module
     */
    void initNGSModul() {

        if (this.cbFileType.getSelectedIndex() < 0 ||
                this.cbFileType.getSelectedItem().toString().contentEquals("")) {
            this.txtHint.setText("please select ngs proc type!");
            return;
        }

        filetype = this.cbFileType.getSelectedItem().toString();
        this.processNGSModul = ServiceNGS.getXPortNGS(filetype);
        if (processNGSModul == null) {
            error("no ngs proc found", null);
        }
        this.initNGSProcParameter();
    }

    /**
     * initialize defaults for NGSModule parameter
     */
    void initNGSProcParameter() {
        try {

            if (this.processNGSModul == null) {
                error("no  ngs proc modul selected", null);
                return;
            }
            this.processNGSModul.initImport();
            this.jTabbedPaneMain.setSelectedIndex(0);

            this.txtHint.setText("edit ngs import parameter");
        } catch (Exception e) {
            error("error import modul", e);

        }

    }

    /**
     * validate parameter fields if ready to proceed to next tab (run)
     */
    private boolean validateNGSProcParameter() {
        if (this.jTextFieldFileName.getText() == null || this.jTextFieldFileName.getText().contentEquals("")) {
            error("please set filepath!", null);
            return false;
        }
        if (this.jCheckBoxUseControl.isSelected() && (this.jTextFieldFileNameControl.getText() == null ||
                this.jTextFieldFileNameControl.getText().contentEquals(""))) {
            error("please set path for control file!", null);
            return false;
        }

        this.processNGSModul.initImport();
        this.processNGSModul.setDataPath(this.jTextFieldFileName.getText());
        if (this.jCheckBoxUseControl.isSelected()) {
            processNGSModul.setHasControl(true);
            processNGSModul.setControlPath(this.jTextFieldFileNameControl.getText());
        }
        // modul specific settings
        if (this.processNGSModul instanceof BAMImport) {
            BAMImport bamImport = (BAMImport) this.processNGSModul;
            if (!this.jRadioButtonImportBin.isSelected()) {

                if (this.jRadioButtonPeakPoisson.isSelected()) {
                    bamImport.setCalcPeaksPoisson(true);
                    bamImport.setCalcPeaksQuantile(false);

                }
                if (this.jRadioButtonPeakQuantile.isSelected()) {
                    bamImport.setCalcPeaksPoisson(false);
                    bamImport.setCalcPeaksQuantile(true);

                }
            } else {
                try {
                    bamImport.setBinsize(1000 * ((Integer) this.jComboBoxBinsize.getSelectedItem()).intValue());
                } catch (Exception e) {
                    error("wrong binsize", e);
                    return false;
                }
            }
            bamImport.setDataSorted(this.jCheckBoxSortedData.isSelected());
            bamImport.setControlSorted(this.jCheckBoxSortedControl.isSelected());
            bamImport.setResize(this.jRadioButtonResize.isSelected());
            bamImport.setShift(this.jRadioButtonShift.isSelected());


            if (this.jCheckBoxNormalize.isSelected()) {
                if (this.jRadioButtonNormControl.isSelected()) {
                    bamImport.setNormalizeWithControl(true);
                } else {
                    bamImport.setNormalizeWithControl(false);
                }
                if (this.jRadioButtonNormGCLoess.isSelected()) {
                    bamImport.setNormalizeWithGCLoess(true);
                } else {
                    bamImport.setNormalizeWithGCLoess(false);
                }
            }
        }

        return true;
    }

///// tab #2 run Import ///////////////////////////////////////////////////////
    void doRun() {

        //setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.jButtonRun.setEnabled(false);
        this.jButtonImport.setEnabled(false);



        this.jTabbedPaneMain.setEnabledAt(0, false);
        this.jTabbedPaneMain.setEnabledAt(1, true);
        this.jTabbedPaneMain.setEnabledAt(2, false);
        this.jTabbedPaneMain.setSelectedIndex(1);

        this.jButtonNextAtRun.setEnabled(false);
        this.txtHint.setText("run ngs processing");
        InformableHandler informable = new InformableHandler() {

            public void messageChanged(String message) {
                jTextMsg.append(message + "\n");
            }
        };
        PropertyChangeListener listener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    //jProgressBar.setValue((Integer) evt.getNewValue());
                } else {
                    jTextMsg.append("import ngs data: " + evt.getNewValue() + "\n");
                }
                if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    jTextMsg.append("import ngs data: OK \n");
                    validateAfterRun();
                }
            }
        };

        processNGSModul.doRunImport(informable, listener);

    /*informable.messageChanged("Start ....");
    //informable.messageChanged("Get Data for " + sample.getName());
    ImportWorker worker = new ImportWorker(importModul, track, informable);
    worker.execute();
    
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));*/
    }

    /**
     * check return state if ready to proceed to next tab (import)
     */
    private void validateAfterRun() {

        BAMImport bamImport = (BAMImport) this.processNGSModul;
        if (bamImport.getError()) {
            this.txtHint.setText("error - see logfile for more information");
            return;
        }

        this.jButtonNextAtRun.setEnabled(true);
        this.jButtonSaveScript.setEnabled(true);
        this.jButtonRun.setEnabled(false);
        //this.jButtonImport.setEnabled(true);
        this.txtHint.setText("done - enter <next> to proceed with import");

    }
///// tab #3 import results ////////////////////////////////////////////////////
    Track dataBin,dataPeak ,dataPeakVsControl ,controlBin ,controlPeak  = null;

    /**
     * init dialog tab with 
     */
    void initEditImport() {
        this.jTabbedPaneMain.setEnabledAt(0, false);
        this.jTabbedPaneMain.setEnabledAt(1, false);
        this.jTabbedPaneMain.setEnabledAt(2, true);
        this.jTabbedPaneMain.setSelectedIndex(2);
        this.jButtonImport.setEnabled(true);
        this.jButtonRun.setEnabled(false);

        try {
            this.jCheckBoxImportBinControl.setEnabled(false);
            this.jCheckBoxImportBinData.setEnabled(false);
            this.jCheckBoxImportPeaksControl.setEnabled(false);
            this.jCheckBoxImportPeaksDataVsControl.setEnabled(false);
            this.jCheckBoxImportPeaksData.setEnabled(false);
            BAMImport bamImport = (BAMImport) this.processNGSModul;


            if (bamImport.isHasBinFile()) {
                dataBin = new Track();
                dataBin.setOriginalFile(bamImport.getDataAsBin());
                this.jCheckBoxImportBinData.setEnabled(true);
            } else {
                this.jCheckBoxImportBinData.setEnabled(false);
            }
            if (bamImport.isHasPeakFile()) {
                dataPeak = new Track();
                dataPeak.setOriginalFile(bamImport.getDataAsPeak());
                this.jCheckBoxImportPeaksData.setEnabled(true);

            } else {
                this.jCheckBoxImportPeaksData.setEnabled(false);
            }
            if (bamImport.isHasPeakVsControlFile()) {
                dataPeakVsControl = new Track();
                dataPeakVsControl.setOriginalFile(bamImport.getDataAsPeakVsControl());
                this.jCheckBoxImportPeaksDataVsControl.setEnabled(true);
            } else {
                this.jCheckBoxImportPeaksDataVsControl.setEnabled(false);
            }
            if (bamImport.isHasBinControlFile()) {

                controlBin = new Track();
                controlBin.setOriginalFile(bamImport.getControlAsBin());

                this.jCheckBoxImportBinControl.setEnabled(true);
            } else {
                this.jCheckBoxImportBinControl.setEnabled(false);
            }
            if (bamImport.isHasPeakControlFile()) {
                controlPeak = new Track();
                controlPeak.setOriginalFile(bamImport.getControlAsPeak());
                this.jCheckBoxImportPeaksControl.setEnabled(true);
            } else {
                this.jCheckBoxImportPeaksControl.setEnabled(false);
            }
            this.txtHint.setText("select files to import, genome release, study and hit <import> to import data");

        } catch (Exception e) {
            Logger.getLogger(
                    ImportNGSDialog.class.getName()).log(
                    Level.SEVERE,
                    "initImport", e);
            error("error init import", null);
        }
    }

    private boolean validateEditImport() {
        if (this.jComboBoxGenomeRelease.getSelectedIndex() < 0) {
            error("no release selected", null);
            return false;
        } else if (this.jComboBoxProject.getSelectedIndex() < 0) {
            error("no study selected", null);
            return false;
        }

        return true;
    }

    /**
     * import R output files either as bed or wig
     * 
     */
    private void runImport() {

        setPrevDone(true);
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.jButtonCancel1.setEnabled(false);
        this.jButtonImport.setEnabled(false);
        String release = this.jComboBoxGenomeRelease.getSelectedItem().toString();
        String project = this.jComboBoxProject.getSelectedItem().toString();
        txtHint.setText("running import");
        if (this.dataBin != null && this.jCheckBoxImportBinData.isSelected()) {
            this.dataBin.setGenomeRelease(release);
            this.dataBin.setId(null);
            this.dataBin.setClazz(FeatureNGS.class.getName());
            this.dataBin.setOwner(ExperimentService.getUser());
            this.dataBin.setDescription("imported via " + this.getClass().getName());
            this.dataBin.setDataType(Defaults.DataType.BIN);
            //d.setProcProcessing(PeakWorker.methodName);
            //d.setParamProcessing("\n" + param.trim());


            String d = this.dataBin.getOriginalFile();
            this.dataBin.setName(d.substring(d.lastIndexOf(File.separator) + 1, d.lastIndexOf(".") - 1));
            if (sie1.getSample().getName() != null) {
                this.sie1 = this.dataBin.addSample(sie1.getSample(), false);
            }
            XPortTrack mod = ServiceXPort.getXPortTrack(ImportTrackWIG.track_wig_txt);
            if (mod == null) {
                error("no import modul found", null);
                return;
            }
            mod.setProject(project);
            processImportTask(this.dataBin, mod);

        }
        if (this.controlBin != null && this.jCheckBoxImportBinControl.isSelected()) {
            this.controlBin.setGenomeRelease(release);
            String d = this.controlBin.getOriginalFile();
            this.controlBin.setName(d.substring(d.lastIndexOf(File.separator) + 1, d.lastIndexOf(".") - 1));
            this.controlBin.setId(null);
            this.controlBin.setClazz(FeatureNGS.class.getName());
            this.controlBin.setOwner(ExperimentService.getUser());
            this.controlBin.setDescription("imported via " + this.getClass().getName());
            this.controlBin.setDataType(Defaults.DataType.BIN);
            if (sie2.getSample().getName() != null) {
                this.sie2 = this.controlBin.addSample(sie2.getSample(), false);
            }
            XPortTrack mod = ServiceXPort.getXPortTrack(ImportTrackWIG.track_wig_txt);
            if (mod == null) {
                error("no import modul found", null);
                return;
            }
            mod.setProject(project);
            processImportTask(this.controlBin, mod);

        }

        if (this.dataPeak != null && this.jCheckBoxImportPeaksData.isSelected()) {
            this.dataPeak.setGenomeRelease(release);
            String d = this.dataPeak.getOriginalFile();
            this.dataPeak.setName(d.substring(d.lastIndexOf(File.separator) + 1, d.lastIndexOf(".") - 1));
            this.dataPeak.setId(null);
            this.dataPeak.setClazz(FeatureNGS.class.getName());
            this.dataPeak.setOwner(ExperimentService.getUser());
            this.dataPeak.setDescription("imported via " + this.getClass().getName());
            this.dataPeak.setDataType(Defaults.DataType.PEAK);

            if (sie1.getSample().getName() != null) {
                this.sie1 = this.dataPeak.addSample(sie1.getSample(), false);
            }
            XPortTrack mod = ServiceXPort.getXPortTrack(ImportTrack.track_bedgraph_txt);
            if (mod == null) {
                error("no import modul found", null);
                return;
            }
            mod.setProject(project);
            processImportTask(this.dataPeak, mod);
        }
        if (this.controlPeak != null && this.jCheckBoxImportPeaksControl.isSelected()) {
            this.controlPeak.setGenomeRelease(release);
            String d = this.controlPeak.getOriginalFile();
            this.controlPeak.setName(d.substring(d.lastIndexOf(File.separator) + 1, d.lastIndexOf(".") - 1));
            this.controlPeak.setId(null);
            this.controlPeak.setClazz(FeatureNGS.class.getName());
            this.controlPeak.setOwner(ExperimentService.getUser());
            this.controlPeak.setDescription("imported via " + this.getClass().getName());
            this.controlPeak.setDataType(Defaults.DataType.PEAK);

            if (sie2.getSample().getName() != null) {
                this.sie2 = this.controlPeak.addSample(sie2.getSample(), false);
            }
            XPortTrack mod = ServiceXPort.getXPortTrack(ImportTrack.track_bedgraph_txt);
            if (mod == null) {
                error("no import modul found", null);
                return;
            }
            mod.setProject(project);
            processImportTask(this.controlPeak, mod);
        }
        if (this.dataPeakVsControl != null && this.jCheckBoxImportPeaksDataVsControl.isSelected()) {
            this.dataPeakVsControl.setGenomeRelease(release);
            String d = this.dataPeakVsControl.getOriginalFile();
            this.dataPeakVsControl.setName(d.substring(d.lastIndexOf(File.separator) + 1, d.lastIndexOf(".") - 1));
            this.dataPeakVsControl.setId(null);
            this.dataPeakVsControl.setClazz(FeatureNGS.class.getName());
            this.dataPeakVsControl.setOwner(ExperimentService.getUser());
            this.dataPeakVsControl.setDescription("imported via " + this.getClass().getName());
            this.dataPeakVsControl.setDataType(Defaults.DataType.PEAK);
            if (sie1.getSample().getName() != null) {
                this.sie1 = this.dataPeakVsControl.addSample(sie1.getSample(), false);
            }
            if (sie2.getSample().getName() != null) {
                this.sie2 = this.dataPeakVsControl.addSample(sie2.getSample(), true);
            }
            XPortTrack mod = ServiceXPort.getXPortTrack(ImportTrack.track_bedgraph_txt);
            if (mod == null) {
                error("no import modul found", null);
                return;
            }
            mod.setProject(project);
            processImportTask(this.dataPeakVsControl, mod);
        }

    }
    private boolean done = true;

    synchronized boolean isPrevDone() {
        return done;
    }

    synchronized void setPrevDone(boolean done) {
        this.done = done;
    }

    @SuppressWarnings("empty-statement")
    protected void processImportTask(Track t, XPortTrack mod) {


        try {

            mod.newImportTrack(t.getOriginalFile());
            String[] tmp = mod.getFileColNames();   // read file col names

            List<String[]> mapping = mod.getDefaultMappingFile2DBColNames();
            mod.setMappingFile2DBColNames(mapping);

        } catch (Exception e) {
            error(e.getMessage(), e);
            return;
        }

        Track _t = TrackService.getTrack(
                t.getName(),
                t.getGenomeRelease().toString());
        if (_t != null) {
            // older experiment with same name exists, create unique name, report error

            t.setName(Utils.getUniquableName(t.getName()));
        }
        //setDone(false);
        InformableHandler informable = new InformableHandler() {

            public void messageChanged(String message) {
                ImportNGSDialog.this.jTextImportMsg.append(message + "\n");
            }
        };

        ImportNGSWorker worker = new ImportNGSWorker(mod, t, informable);
        worker.execute();
    }
    Integer nofImports = 0;

    public class ImportNGSWorker extends SwingWorker<Track, String> {

        XPortTrack importModul;
        private final InformableHandler informable;
        Track data = null;

        public ImportNGSWorker(XPortTrack _port, Track _data, InformableHandler inf) {
            this.importModul = _port;
            this.informable = inf;

            this.data = _data;
        }

        @Override
        protected void process(List<String> chunks) {


            for (String message : chunks) {
                informable.messageChanged(message);
            }
        }

        protected Track doInBackground() throws Exception {
            synchronized (nofImports) {
                nofImports++;
            }
            synchronized (this) {
                while (!isPrevDone()) {
                    this.wait(500);
                }
            }
            setPrevDone(false);
            publish("run Import " + data.getOriginalFile());
            setProgress(0);


            setProgress(10);
            Track d = null;
            try {

                d = importModul.doImportTrack(this.data, this.informable);



            } catch (Exception ex) {
                publish("error during import see logfile !");
                Logger.getLogger(ImportNGSWorker.class.getName()).log(Level.WARNING,
                        "error during import !", ex);
                error("error during import see logfile !", null);

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
                data = get();
                if (data != null) {
                    //initImportPlatform(data);
                    publish(" finished ...........................");
                    setPrevDone(true);



                } else {
                    publish(" ERROR see logfile for further details");
                    ImportNGSDialog.this.jTextMsg.setText(" ERROR see logfile for further details");
                }
                synchronized (nofImports) {
                    nofImports--;

                    if (nofImports == 0) {
                        jButtonCancel1.setEnabled(true);
                        ImportNGSDialog.this.txtHint.setText(" ok - import finished");
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            //progressBar.setVisible(false);
            } catch (Exception e) {
                Logger.getLogger(ImportNGSWorker.class.getName()).log(Level.WARNING, "do cbs ", e);
                error("error during import see logfile !", null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupPeakDetMethod = new javax.swing.ButtonGroup();
        buttonGroupType = new javax.swing.ButtonGroup();
        buttonGroupNorm = new javax.swing.ButtonGroup();
        buttonGroupExtend = new javax.swing.ButtonGroup();
        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jPanelParameter = new javax.swing.JPanel();
        jPanelFile = new javax.swing.JPanel();
        jTextFieldFileName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonBrowse = new javax.swing.JButton();
        jTextFieldFileNameControl = new javax.swing.JTextField();
        jButtonBrowseControl = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jCheckBoxSortedData = new javax.swing.JCheckBox();
        jCheckBoxPairedEndData = new javax.swing.JCheckBox();
        jCheckBoxSortedControl = new javax.swing.JCheckBox();
        jCheckBoxPairedEndControl = new javax.swing.JCheckBox();
        jCheckBoxUseControl = new javax.swing.JCheckBox();
        jPanelParam = new javax.swing.JPanel();
        jPanelExtend = new javax.swing.JPanel();
        jRadioButtonShift = new javax.swing.JRadioButton();
        jRadioButtonResize = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jPanelImport = new javax.swing.JPanel();
        jRadioButtonImportBin = new javax.swing.JRadioButton();
        jRadioButtonImportPeak = new javax.swing.JRadioButton();
        jRadioButtonPeakPoisson = new javax.swing.JRadioButton();
        jRadioButtonPeakQuantile = new javax.swing.JRadioButton();
        jLabelHintPeakBasedOnControl = new javax.swing.JLabel();
        jComboBoxBinsize = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanelNormalize = new javax.swing.JPanel();
        jRadioButtonNormGCLoess = new javax.swing.JRadioButton();
        jRadioButtonNormControl = new javax.swing.JRadioButton();
        jCheckBoxNormalize = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        cbFileType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanelProcessing = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextMsg = new javax.swing.JTextArea();
        jButtonNextAtRun = new javax.swing.JButton();
        jButtonSaveScript = new javax.swing.JButton();
        jPanelImportFiles = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxProject = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxGenomeRelease = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxImportBinData = new javax.swing.JCheckBox();
        jCheckBoxImportPeaksData = new javax.swing.JCheckBox();
        jCheckBoxImportPeaksDataVsControl = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jButtonEditSampleData = new javax.swing.JButton();
        jButtonSelectSampleData = new javax.swing.JButton();
        jTextFieldSampleData = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jCheckBoxImportBinControl = new javax.swing.JCheckBox();
        jCheckBoxImportPeaksControl = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jButtonEditSampleControl = new javax.swing.JButton();
        jButtonSelectSampleControl = new javax.swing.JButton();
        jTextFieldSampleControl = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextImportMsg = new javax.swing.JTextArea();
        jPanelButton = new javax.swing.JPanel();
        jButtonRun = new javax.swing.JButton();
        jButtonCancel1 = new javax.swing.JButton();
        txtHint = new javax.swing.JLabel();
        jButtonImport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.title")); // NOI18N

        jTabbedPaneMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jTabbedPaneMain.setForeground(java.awt.Color.black);
        jTabbedPaneMain.setAutoscrolls(true);
        jTabbedPaneMain.setFont(new java.awt.Font("Dialog", 1, 14));
        jTabbedPaneMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneMainStateChanged(evt);
            }
        });

        jPanelParameter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPanelParameterFocusLost(evt);
            }
        });

        jPanelFile.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel1.text")); // NOI18N

        jButtonBrowse.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonBrowse.text")); // NOI18N
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jButtonBrowseControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonBrowseControl.text")); // NOI18N
        jButtonBrowseControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseControlActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel7.text")); // NOI18N

        jCheckBoxSortedData.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxSortedData.setSelected(true);
        jCheckBoxSortedData.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxSortedData.text")); // NOI18N

        jCheckBoxPairedEndData.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxPairedEndData.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxPairedEndData.text")); // NOI18N
        jCheckBoxPairedEndData.setEnabled(false);

        jCheckBoxSortedControl.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxSortedControl.setSelected(true);
        jCheckBoxSortedControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxSortedControl.text")); // NOI18N

        jCheckBoxPairedEndControl.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxPairedEndControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxPairedEndControl.text")); // NOI18N
        jCheckBoxPairedEndControl.setEnabled(false);

        jCheckBoxUseControl.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxUseControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxUseControl.text")); // NOI18N

        javax.swing.GroupLayout jPanelFileLayout = new javax.swing.GroupLayout(jPanelFile);
        jPanelFile.setLayout(jPanelFileLayout);
        jPanelFileLayout.setHorizontalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFileLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFileLayout.createSequentialGroup()
                        .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFileLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelFileLayout.createSequentialGroup()
                                        .addComponent(jCheckBoxSortedData)
                                        .addGap(31, 31, 31)
                                        .addComponent(jCheckBoxPairedEndData))
                                    .addGroup(jPanelFileLayout.createSequentialGroup()
                                        .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(34, 34, 34)
                                        .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButtonBrowseControl)
                                            .addComponent(jButtonBrowse)))))
                            .addGroup(jPanelFileLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelFileLayout.createSequentialGroup()
                                        .addComponent(jCheckBoxSortedControl)
                                        .addGap(31, 31, 31)
                                        .addComponent(jCheckBoxPairedEndControl))
                                    .addComponent(jTextFieldFileNameControl, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jCheckBoxUseControl))
                .addContainerGap(417, Short.MAX_VALUE))
        );

        jPanelFileLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonBrowse, jButtonBrowseControl});

        jPanelFileLayout.setVerticalGroup(
            jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFileLayout.createSequentialGroup()
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowse))
                .addGap(12, 12, 12)
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jCheckBoxSortedData)
                    .addComponent(jCheckBoxPairedEndData))
                .addGap(50, 50, 50)
                .addComponent(jCheckBoxUseControl)
                .addGap(18, 18, 18)
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldFileNameControl, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jButtonBrowseControl, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jCheckBoxSortedControl)
                    .addComponent(jCheckBoxPairedEndControl))
                .addContainerGap())
        );

        jPanelFileLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonBrowse, jButtonBrowseControl});

        jPanelParam.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanelExtend.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonShift.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupExtend.add(jRadioButtonShift);
        jRadioButtonShift.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonShift.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonShift.text")); // NOI18N

        jRadioButtonResize.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupExtend.add(jRadioButtonResize);
        jRadioButtonResize.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonResize.setSelected(true);
        jRadioButtonResize.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonResize.text")); // NOI18N

        buttonGroupExtend.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButton1.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButton1.text")); // NOI18N

        javax.swing.GroupLayout jPanelExtendLayout = new javax.swing.GroupLayout(jPanelExtend);
        jPanelExtend.setLayout(jPanelExtendLayout);
        jPanelExtendLayout.setHorizontalGroup(
            jPanelExtendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelExtendLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelExtendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonShift)
                    .addComponent(jRadioButtonResize)
                    .addComponent(jRadioButton1))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanelExtendLayout.setVerticalGroup(
            jPanelExtendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelExtendLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButtonShift)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonResize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton1)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jPanelImport.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonImportBin.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupType.add(jRadioButtonImportBin);
        jRadioButtonImportBin.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonImportBin.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonImportBin.text")); // NOI18N
        jRadioButtonImportBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonImportBinActionPerformed(evt);
            }
        });

        jRadioButtonImportPeak.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupType.add(jRadioButtonImportPeak);
        jRadioButtonImportPeak.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonImportPeak.setSelected(true);
        jRadioButtonImportPeak.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonImportPeak.text")); // NOI18N
        jRadioButtonImportPeak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonImportPeakActionPerformed(evt);
            }
        });
        jRadioButtonImportPeak.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jRadioButtonImportPeakPropertyChange(evt);
            }
        });

        jRadioButtonPeakPoisson.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupPeakDetMethod.add(jRadioButtonPeakPoisson);
        jRadioButtonPeakPoisson.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonPeakPoisson.setSelected(true);
        jRadioButtonPeakPoisson.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonPeakPoisson.text")); // NOI18N
        jRadioButtonPeakPoisson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPeakPoissonActionPerformed(evt);
            }
        });

        jRadioButtonPeakQuantile.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupPeakDetMethod.add(jRadioButtonPeakQuantile);
        jRadioButtonPeakQuantile.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonPeakQuantile.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonPeakQuantile.text")); // NOI18N
        jRadioButtonPeakQuantile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPeakQuantileActionPerformed(evt);
            }
        });

        jLabelHintPeakBasedOnControl.setFont(new java.awt.Font("Dialog", 2, 12));
        jLabelHintPeakBasedOnControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabelHintPeakBasedOnControl.text")); // NOI18N

        jComboBoxBinsize.setModel(new javax.swing.DefaultComboBoxModel(new Integer[] { 1, 20, 100 }));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel9.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel9.text")); // NOI18N

        javax.swing.GroupLayout jPanelImportLayout = new javax.swing.GroupLayout(jPanelImport);
        jPanelImport.setLayout(jPanelImportLayout);
        jPanelImportLayout.setHorizontalGroup(
            jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelImportLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelImportLayout.createSequentialGroup()
                        .addGroup(jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelImportLayout.createSequentialGroup()
                                .addComponent(jRadioButtonImportBin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9))
                            .addComponent(jRadioButtonImportPeak, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxBinsize, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelImportLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButtonPeakQuantile)
                            .addComponent(jRadioButtonPeakPoisson))))
                .addContainerGap(61, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelImportLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabelHintPeakBasedOnControl, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelImportLayout.setVerticalGroup(
            jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelImportLayout.createSequentialGroup()
                .addGroup(jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelImportLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jRadioButtonImportBin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE))
                    .addGroup(jPanelImportLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxBinsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jRadioButtonImportPeak)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPeakPoisson)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPeakQuantile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelHintPeakBasedOnControl, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelNormalize.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonNormGCLoess.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupNorm.add(jRadioButtonNormGCLoess);
        jRadioButtonNormGCLoess.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonNormGCLoess.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonNormGCLoess.text")); // NOI18N
        jRadioButtonNormGCLoess.setEnabled(false);

        jRadioButtonNormControl.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupNorm.add(jRadioButtonNormControl);
        jRadioButtonNormControl.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonNormControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jRadioButtonNormControl.text")); // NOI18N
        jRadioButtonNormControl.setEnabled(false);

        jCheckBoxNormalize.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxNormalize.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxNormalize.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxNormalize.text")); // NOI18N
        jCheckBoxNormalize.setEnabled(false);

        javax.swing.GroupLayout jPanelNormalizeLayout = new javax.swing.GroupLayout(jPanelNormalize);
        jPanelNormalize.setLayout(jPanelNormalizeLayout);
        jPanelNormalizeLayout.setHorizontalGroup(
            jPanelNormalizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNormalizeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelNormalizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelNormalizeLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanelNormalizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButtonNormControl)
                            .addComponent(jRadioButtonNormGCLoess)))
                    .addComponent(jCheckBoxNormalize))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanelNormalizeLayout.setVerticalGroup(
            jPanelNormalizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNormalizeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxNormalize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonNormGCLoess)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonNormControl)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelParamLayout = new javax.swing.GroupLayout(jPanelParam);
        jPanelParam.setLayout(jPanelParamLayout);
        jPanelParamLayout.setHorizontalGroup(
            jPanelParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelParamLayout.createSequentialGroup()
                .addComponent(jPanelExtend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelNormalize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(366, Short.MAX_VALUE))
        );
        jPanelParamLayout.setVerticalGroup(
            jPanelParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelParamLayout.createSequentialGroup()
                .addGroup(jPanelParamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelImport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelExtend, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelNormalize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelParamLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanelExtend, jPanelNormalize});

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel2.text")); // NOI18N

        Vector<String> filetypes = ServiceNGS.getFileTypeNGSImport();
        cbFileType.setModel(new DefaultComboBoxModel(filetypes));
        cbFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFileTypeActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 2, 10));
        jLabel3.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanelParameterLayout = new javax.swing.GroupLayout(jPanelParameter);
        jPanelParameter.setLayout(jPanelParameterLayout);
        jPanelParameterLayout.setHorizontalGroup(
            jPanelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelParameterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelParameterLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel2)
                        .addGap(11, 11, 11)
                        .addComponent(cbFileType, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jLabel3))
                    .addComponent(jPanelParam, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelParameterLayout.setVerticalGroup(
            jPanelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelParameterLayout.createSequentialGroup()
                .addGroup(jPanelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelParameterLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(jPanelParameterLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel2)))
                .addGap(43, 43, 43)
                .addComponent(jPanelFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jPanelParameter.TabConstraints.tabTitle"), jPanelParameter); // NOI18N

        jTextMsg.setColumns(20);
        jTextMsg.setRows(5);
        jScrollPane2.setViewportView(jTextMsg);

        jButtonNextAtRun.setForeground(new java.awt.Color(255, 51, 153));
        jButtonNextAtRun.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonNextAtRun.text")); // NOI18N
        jButtonNextAtRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextAtRunActionPerformed(evt);
            }
        });

        jButtonSaveScript.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonSaveScript.text")); // NOI18N
        jButtonSaveScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveScriptActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelProcessingLayout = new javax.swing.GroupLayout(jPanelProcessing);
        jPanelProcessing.setLayout(jPanelProcessingLayout);
        jPanelProcessingLayout.setHorizontalGroup(
            jPanelProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProcessingLayout.createSequentialGroup()
                .addContainerGap(606, Short.MAX_VALUE)
                .addComponent(jButtonSaveScript)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNextAtRun)
                .addContainerGap())
            .addGroup(jPanelProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelProcessingLayout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                    .addGap(4, 4, 4)))
        );
        jPanelProcessingLayout.setVerticalGroup(
            jPanelProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProcessingLayout.createSequentialGroup()
                .addContainerGap(471, Short.MAX_VALUE)
                .addGroup(jPanelProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNextAtRun)
                    .addComponent(jButtonSaveScript))
                .addContainerGap())
            .addGroup(jPanelProcessingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelProcessingLayout.createSequentialGroup()
                    .addGap(17, 17, 17)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(99, Short.MAX_VALUE)))
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jPanelProcessing.TabConstraints.tabTitle"), jPanelProcessing); // NOI18N

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel6.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel6.text")); // NOI18N

        jComboBoxProject.setModel(new javax.swing.DefaultComboBoxModel(DBUtils.getStudies()));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel8.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel8.text")); // NOI18N

        jComboBoxGenomeRelease.setModel(new javax.swing.DefaultComboBoxModel(DBUtils.getAllReleases() ));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jCheckBoxImportBinData.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxImportBinData.text")); // NOI18N

        jCheckBoxImportPeaksData.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxImportPeaksData.text")); // NOI18N

        jCheckBoxImportPeaksDataVsControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxImportPeaksDataVsControl.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel4.text")); // NOI18N

        jButtonEditSampleData.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonEditSampleData.text")); // NOI18N

        jButtonSelectSampleData.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonSelectSampleData.text")); // NOI18N

        jTextFieldSampleData.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jTextFieldSampleData.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxImportBinData)
                    .addComponent(jCheckBoxImportPeaksData)
                    .addComponent(jCheckBoxImportPeaksDataVsControl)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jTextFieldSampleData, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEditSampleData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonSelectSampleData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addContainerGap(30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jCheckBoxImportBinData)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxImportPeaksData)
                .addGap(12, 12, 12)
                .addComponent(jCheckBoxImportPeaksDataVsControl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEditSampleData)
                    .addComponent(jButtonSelectSampleData)
                    .addComponent(jTextFieldSampleData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jCheckBoxImportBinControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxImportBinControl.text")); // NOI18N

        jCheckBoxImportPeaksControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jCheckBoxImportPeaksControl.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jLabel5.text")); // NOI18N

        jButtonEditSampleControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonEditSampleControl.text")); // NOI18N

        jButtonSelectSampleControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonSelectSampleControl.text")); // NOI18N

        jTextFieldSampleControl.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jTextFieldSampleControl.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxImportBinControl)
                    .addComponent(jCheckBoxImportPeaksControl)
                    .addComponent(jLabel5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldSampleControl, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEditSampleControl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSelectSampleControl)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxImportBinControl)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxImportPeaksControl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEditSampleControl)
                    .addComponent(jButtonSelectSampleControl)
                    .addComponent(jTextFieldSampleControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
        );

        jTextImportMsg.setColumns(20);
        jTextImportMsg.setRows(5);
        jScrollPane1.setViewportView(jTextImportMsg);

        javax.swing.GroupLayout jPanelImportFilesLayout = new javax.swing.GroupLayout(jPanelImportFiles);
        jPanelImportFiles.setLayout(jPanelImportFilesLayout);
        jPanelImportFilesLayout.setHorizontalGroup(
            jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelImportFilesLayout.createSequentialGroup()
                .addGroup(jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelImportFilesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelImportFilesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelImportFilesLayout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addGroup(jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addGap(27, 27, 27)
                        .addGroup(jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBoxProject, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxGenomeRelease, 0, 155, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanelImportFilesLayout.setVerticalGroup(
            jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelImportFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addGroup(jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBoxGenomeRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelImportFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPaneMain.addTab(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jPanelImportFiles.TabConstraints.tabTitle"), jPanelImportFiles); // NOI18N

        jPanelButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelButton.setAlignmentX(1.0F);
        jPanelButton.setMinimumSize(new java.awt.Dimension(700, 20));

        jButtonRun.setForeground(new java.awt.Color(255, 51, 102));
        jButtonRun.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonRun.text")); // NOI18N
        jButtonRun.setEnabled(false);
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });

        jButtonCancel1.setForeground(java.awt.Color.black);
        jButtonCancel1.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonCancel1.text")); // NOI18N
        jButtonCancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancel1ActionPerformed(evt);
            }
        });

        txtHint.setForeground(new java.awt.Color(255, 51, 204));
        txtHint.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.txtHint.text")); // NOI18N

        jButtonImport.setText(org.openide.util.NbBundle.getMessage(ImportNGSDialog.class, "ImportNGSDialog.jButtonImport.text")); // NOI18N
        jButtonImport.setEnabled(false);
        jButtonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButtonLayout = new javax.swing.GroupLayout(jPanelButton);
        jPanelButton.setLayout(jPanelButtonLayout);
        jPanelButtonLayout.setHorizontalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtHint, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addGroup(jPanelButtonLayout.createSequentialGroup()
                        .addComponent(jButtonRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonImport)))
                .addGap(118, 118, 118)
                .addComponent(jButtonCancel1))
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonLayout.createSequentialGroup()
                .addComponent(txtHint, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRun)
                    .addComponent(jButtonImport)
                    .addComponent(jButtonCancel1)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(jPanelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed
    if (this.validateNGSProcParameter()) {
        doRun();
    }
}//GEN-LAST:event_jButtonRunActionPerformed

private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
    String path = NbPreferences.forModule(ImportNGSDialog.class).get("pathPreference", "");
    JFileChooser importFileChooser = new JFileChooser(path);
    int returnVal = importFileChooser.showOpenDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }

    this.jTextFieldFileName.setText(importFileChooser.getSelectedFile().getPath());

    Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.INFO,
            "You chose to import as NGS file: " +
            this.jTextFieldFileName.getText());
    String name = importFileChooser.getSelectedFile().getName();



    NbPreferences.forModule(ImportNGSDialog.class).put("pathPreference",
            importFileChooser.getSelectedFile().getPath());
    this.jButtonRun.setEnabled(true);

}//GEN-LAST:event_jButtonBrowseActionPerformed

private void cbFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFileTypeActionPerformed
    this.processNGSModul = null;

    this.jButtonRun.setEnabled(false);
    if (!this.cbFileType.getSelectedItem().toString().equalsIgnoreCase("")) {
        this.initNGSModul();
    }
}//GEN-LAST:event_cbFileTypeActionPerformed

private void jButtonCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancel1ActionPerformed
    this.setVisible(false);
}//GEN-LAST:event_jButtonCancel1ActionPerformed

private void jPanelParameterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanelParameterFocusLost
}//GEN-LAST:event_jPanelParameterFocusLost
    int lastSelTab = -1;
private void jTabbedPaneMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneMainStateChanged
    /*if (lastSelTab != -1) {
    if (lastSelTab == 0) {
    if (!this.validateEditTrack()) {
    this.jTabbedPaneMain.setSelectedIndex(0);
    }
    }
    if (lastSelTab == 1) {
    //if (!this.validateMapping()) {
    this.jTabbedPaneMain.setSelectedIndex(1);
    //  }
    }
    }
    this.lastSelTab = this.jTabbedPaneMain.getSelectedIndex();*/
}//GEN-LAST:event_jTabbedPaneMainStateChanged

private void jButtonBrowseControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseControlActionPerformed
    String path = NbPreferences.forModule(ImportNGSDialog.class).get("pathPreference", "");
    JFileChooser importFileChooser = new JFileChooser(path);
    int returnVal = importFileChooser.showOpenDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }
    this.jCheckBoxUseControl.setSelected(true);
    this.jTextFieldFileNameControl.setText(importFileChooser.getSelectedFile().getPath());

    Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.INFO,
            "You chose to import as control file: " +
            this.jTextFieldFileNameControl.getText());


    NbPreferences.forModule(ImportNGSDialog.class).put("pathPreference",
            importFileChooser.getSelectedFile().getPath());
}//GEN-LAST:event_jButtonBrowseControlActionPerformed

private void jButtonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportActionPerformed
    if (this.validateEditImport()) {
        this.runImport();
    }
}//GEN-LAST:event_jButtonImportActionPerformed

private void jButtonNextAtRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextAtRunActionPerformed
    this.initEditImport();

}//GEN-LAST:event_jButtonNextAtRunActionPerformed

private void jRadioButtonPeakQuantileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPeakQuantileActionPerformed
    if (this.jRadioButtonPeakQuantile.isSelected()) {
        this.jRadioButtonImportPeak.setSelected(true);
    }
}//GEN-LAST:event_jRadioButtonPeakQuantileActionPerformed

private void jRadioButtonPeakPoissonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPeakPoissonActionPerformed
    if (this.jRadioButtonPeakPoisson.isSelected()) {
        this.jRadioButtonImportPeak.setSelected(true);
    }
}//GEN-LAST:event_jRadioButtonPeakPoissonActionPerformed

private void jRadioButtonImportBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonImportBinActionPerformed
    this.jRadioButtonPeakPoisson.setEnabled(!this.jRadioButtonImportBin.isSelected());//GEN-LAST:event_jRadioButtonImportBinActionPerformed
        this.jRadioButtonPeakQuantile.setEnabled(!this.jRadioButtonImportBin.isSelected());


    }

private void jRadioButtonImportPeakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonImportPeakActionPerformed

    this.jRadioButtonPeakPoisson.setEnabled(this.jRadioButtonImportPeak.isSelected());
    this.jRadioButtonPeakQuantile.setEnabled(this.jRadioButtonImportPeak.isSelected());



}//GEN-LAST:event_jRadioButtonImportPeakActionPerformed

private void jRadioButtonImportPeakPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jRadioButtonImportPeakPropertyChange
    // TODO add your handling code here:
}//GEN-LAST:event_jRadioButtonImportPeakPropertyChange

private void jButtonSaveScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveScriptActionPerformed
    String path = NbPreferences.forModule(ImportNGSDialog.class).get("pathPreference", "");
    JFileChooser exportFileChooser = new JFileChooser(path);
    int returnVal = exportFileChooser.showSaveDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
    }

    String filesz = exportFileChooser.getSelectedFile().getPath();

    Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.INFO,
            "You chose to export script to file: " +
            filesz);
    //String name = exportFileChooser.getSelectedFile().getName();
    try {

        FileWriter out = new FileWriter(filesz);
        out.write(this.jTextMsg.getText());
        out.close();
    } catch (Exception ex) {
        Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.WARNING, null, ex);
    }


    NbPreferences.forModule(ImportNGSDialog.class).put("pathPreference",
            exportFileChooser.getSelectedFile().getPath());
    this.jButtonRun.setEnabled(true);
}//GEN-LAST:event_jButtonSaveScriptActionPerformed

    class ActionListenerSampleEdit implements ActionListener {

        int index = -1;

        ActionListenerSampleEdit(int s) {
            index = s;
        }

        public void actionPerformed(ActionEvent e) {
            boolean isInverse = false;


            try {

                SampleInTrack sieOld = null;
                String sampleName = (index == 0 ? ImportNGSDialog.this.jTextFieldSampleData.getText() : ImportNGSDialog.this.jTextFieldSampleControl.getText());
                sampleName = (String) JOptionPane.showInputDialog(null,
                        "enter new name",
                        "edit sample name",
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, sampleName);
                if (sampleName == null) {
                    return;
                }

                Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.INFO,
                        "change value for sample name " + index + " to " + sampleName);

                SampleDetail _d = ExperimentService.getSampleDetailByName(sampleName);

                if (_d == null) {
                    _d = new SampleDetail();
                    _d.setName(sampleName);
                }

                if (index == 0) {
                    sie1.setSample(_d);
                } else {
                    sie2.setSample(_d);
                }
                updateSampleView();

            } catch (Exception ex) {
                Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.SEVERE,
                        "Error: ", ex);
                error("error edit Sample", ex);
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
                combo.setSelectedIndex(0);
                combo.setEditable(true);
                panel.add(combo);


                if (JOptionPane.showConfirmDialog(null, panel,
                        "choose sample", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    String name = combo.getSelectedItem().toString();
                    Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.INFO,
                            "select new sample " + index + " " + name);

                    for (SampleDetail d : list) {
                        if (d.getName().contentEquals(name)) {

                            if (index == 0) {
                                sie1.setSample(d);
                            //fieldSample1.setText("");
                            } else {
                                sie2.setSample(d);
                            //fieldSample2.setText("");
                            }
                            updateSampleView();
                        }
                    }
                }
            } catch (Exception ex) {
                error("error set Sample " + index, ex);
                Logger.getLogger(ImportNGSDialog.class.getName()).log(Level.SEVERE,
                        "Error: ", ex);

            }
        }
    };

    void updateSampleView() throws Exception {
        this.jTextFieldSampleData.setText("");
        this.jTextFieldSampleControl.setText("");
        this.jTextFieldSampleData.setText("SampleData");
        this.jTextFieldSampleControl.setText("SampleControl");


        if (this.sie1 != null) {
            this.jTextFieldSampleData.setText(this.sie1.getName());
        }
        if (this.sie2 != null) {
            this.jTextFieldSampleControl.setText(this.sie2.getName());
        }

    // todo edit in experimentdetailview for sample 
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupExtend;
    private javax.swing.ButtonGroup buttonGroupNorm;
    private javax.swing.ButtonGroup buttonGroupPeakDetMethod;
    private javax.swing.ButtonGroup buttonGroupType;
    private javax.swing.JComboBox cbFileType;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonBrowseControl;
    private javax.swing.JButton jButtonCancel1;
    private javax.swing.JButton jButtonEditSampleControl;
    private javax.swing.JButton jButtonEditSampleData;
    private javax.swing.JButton jButtonImport;
    private javax.swing.JButton jButtonNextAtRun;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JButton jButtonSaveScript;
    private javax.swing.JButton jButtonSelectSampleControl;
    private javax.swing.JButton jButtonSelectSampleData;
    private javax.swing.JCheckBox jCheckBoxImportBinControl;
    private javax.swing.JCheckBox jCheckBoxImportBinData;
    private javax.swing.JCheckBox jCheckBoxImportPeaksControl;
    private javax.swing.JCheckBox jCheckBoxImportPeaksData;
    private javax.swing.JCheckBox jCheckBoxImportPeaksDataVsControl;
    private javax.swing.JCheckBox jCheckBoxNormalize;
    private javax.swing.JCheckBox jCheckBoxPairedEndControl;
    private javax.swing.JCheckBox jCheckBoxPairedEndData;
    private javax.swing.JCheckBox jCheckBoxSortedControl;
    private javax.swing.JCheckBox jCheckBoxSortedData;
    private javax.swing.JCheckBox jCheckBoxUseControl;
    private javax.swing.JComboBox jComboBoxBinsize;
    private javax.swing.JComboBox jComboBoxGenomeRelease;
    private javax.swing.JComboBox jComboBoxProject;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelHintPeakBasedOnControl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelExtend;
    private javax.swing.JPanel jPanelFile;
    private javax.swing.JPanel jPanelImport;
    private javax.swing.JPanel jPanelImportFiles;
    private javax.swing.JPanel jPanelNormalize;
    private javax.swing.JPanel jPanelParam;
    private javax.swing.JPanel jPanelParameter;
    private javax.swing.JPanel jPanelProcessing;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButtonImportBin;
    private javax.swing.JRadioButton jRadioButtonImportPeak;
    private javax.swing.JRadioButton jRadioButtonNormControl;
    private javax.swing.JRadioButton jRadioButtonNormGCLoess;
    private javax.swing.JRadioButton jRadioButtonPeakPoisson;
    private javax.swing.JRadioButton jRadioButtonPeakQuantile;
    private javax.swing.JRadioButton jRadioButtonResize;
    private javax.swing.JRadioButton jRadioButtonShift;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    private javax.swing.JTextField jTextFieldFileName;
    private javax.swing.JTextField jTextFieldFileNameControl;
    private javax.swing.JTextField jTextFieldSampleControl;
    private javax.swing.JTextField jTextFieldSampleData;
    private javax.swing.JTextArea jTextImportMsg;
    private javax.swing.JTextArea jTextMsg;
    private javax.swing.JLabel txtHint;
    // End of variables declaration//GEN-END:variables
    }
