/**
 * @name ConvertExperimentDialog.java
 * 
 * Created on August 5, 2011, 4:19 PM
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the GenomeCATPro software package.
 * Katrin Tebel <tebel at molgen.mpg.de>.
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
package org.molgen.genomeCATPro.guimodul.experiment;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.data.DataManager;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 *
 * @author  tebel
 */
public class ConvertExperimentDialog extends javax.swing.JDialog {

    ExperimentData newData = null;
    ExperimentData oldData = null;
    PlatformDetail pDetail = null;
    PlatformData pData = null;

    /** Creates new form ConvertExperimentDialog */
    public ConvertExperimentDialog(java.awt.Frame parent, boolean modal, ExperimentData d) {
        super(parent, modal);
        finish();
        this.oldData = d;
        this.newData = new ExperimentData();
        newData.setName(oldData.getName());
        newData.setExperiment(oldData.getExperiment());
        newData.setClazz(oldData.getClazz());
        newData.setDataType(oldData.getDataType());


        //newData.setGenomeRelease(Defaults.GenomeRelease.toRelease(data.getGenomeRelease()));


        this.experimentDataViewNew = new ExperimentDataView(newData, true);
        initComponents();

        this.jTextMessage.setText("please choose release!!");
        this.experimentDataViewNew.data.addPropertyChangeListener(listenerRelease);

    }

    static void error(String message, Exception e, JDialog d) {
        if (d != null) {
            JOptionPane.showMessageDialog(d, message);
        }
        Logger.getLogger(ConvertExperimentDialog.class.getName()).log(
                Level.WARNING, message, e);

    }

    public static boolean batch(ExperimentData _data, GenomeRelease _release) {

        try {

            ExperimentData _newData = new ExperimentData();
            _newData.setName(_data.getName());
            _newData.setExperiment(_data.getExperiment());
            _newData.setClazz(_data.getClazz());
            _newData.setDataType(_data.getDataType());
            _newData.setGenomeRelease(_release);
            PlatformData _pData = doCheckRelease(_newData, _data, null);
            if (_pData == null) {
                return false;
            }
            return doConvert(_newData, _data, _pData, null);
        } catch (Exception e) {
            error("Batch  " + _data.getName(), e, null);
            return false;
        }

    }

    public static PlatformData doCheckRelease(ExperimentData _newData, ExperimentData _oldData, JDialog d) {
        Logger.getLogger(ConvertExperimentDialog.class.getName()).log(Level.INFO,
                "listenerRelease: release changed  ");

        try {
            // check if name and release already exists
            ExperimentData test = ExperimentService.getExperimentData(
                    _newData.getName(), _newData.getGenomeRelease().toString());
            if (test != null) {
                error("already exists: " + _newData.getName() + " " +
                        _newData.getGenomeRelease().toString(), null, d);
            }
            // get platformdetail for existing data
            PlatformDetail _pDetail = PlatformService.getPlatformDetailForData(
                    _oldData.getPlatformdata());
            if (_pDetail == null) {
                error("no platform detail found: " + _oldData.getPlatformdata().toString(), null, d);
            }
            // get platform data for detail and release
            List<PlatformData> list = PlatformService.getPlatformDataByDetailId(
                    _pDetail.getPlatformID());

            for (PlatformData _pData : list) {
                if (_pData.getGenomeRelease().contentEquals(_newData.getGenomeRelease())) {
                    Logger.getLogger(ConvertExperimentDialog.class.getName()).log(Level.INFO,
                            "<convert> will create new  table with platform " +
                            _pData.toString());
                    return _pData;


                }
            }
            error("no platform data found: " + _pDetail.getName() + " " + _newData.getGenomeRelease(), null, d);

        } catch (Exception e) {
            error("error  - see logfile for more information: ", e, d);
        }
        return null;
    }

    public static boolean doConvert(ExperimentData _newData, ExperimentData _oldData, PlatformData _pData, JDialog d) {
        try {
            DataManager.convertExperiment(_oldData, _newData, _pData);
            return true;
        } catch (Exception ex) {
            error("error - doConvert", ex, d);
        }
        return false;
    }

    public static void convertExperiment(ExperimentData data) {
        if (data.getPlatformdata() == null) {
            Logger.getLogger(ConvertExperimentDialog.class.getName()).log(Level.SEVERE,
                    "not possible, " + data.getName() + " has no platform information");
            return;
        }
        ConvertExperimentDialog d = new ConvertExperimentDialog(null, true, data);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    PropertyChangeListener listenerRelease = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if (!evt.getPropertyName().contentEquals("genomeRelease")) {
                return;
            }
            pData = doCheckRelease(newData, oldData, ConvertExperimentDialog.this);

        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        experimentDataViewNew = experimentDataViewNew;
        jTextMessage = new javax.swing.JLabel();
        jButtonConvert = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(ConvertExperimentDialog.class, "ConvertExperimentDialog.title")); // NOI18N

        jTextMessage.setFont(new java.awt.Font("Dialog", 3, 14));
        jTextMessage.setForeground(new java.awt.Color(255, 0, 153));
        jTextMessage.setText(org.openide.util.NbBundle.getMessage(ConvertExperimentDialog.class, "ConvertExperimentDialog.jTextMessage.text")); // NOI18N

        jButtonConvert.setText(org.openide.util.NbBundle.getMessage(ConvertExperimentDialog.class, "ConvertExperimentDialog.jButtonConvert.text")); // NOI18N
        jButtonConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConvertActionPerformed(evt);
            }
        });

        jButtonCancel.setText(org.openide.util.NbBundle.getMessage(ConvertExperimentDialog.class, "ConvertExperimentDialog.jButtonCancel.text")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jTextMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(experimentDataViewNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(646, Short.MAX_VALUE)
                .addComponent(jButtonConvert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancel)
                .addGap(47, 47, 47))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(experimentDataViewNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextMessage)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConvert)
                    .addComponent(jButtonCancel))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    this.finish();
    this.dispose();
// TODO add your handling code here:
}//GEN-LAST:event_jButtonCancelActionPerformed

    void finish() {
        this.pData = null;
        this.pDetail = null;
        this.newData = null;
        this.oldData = null;

    }
private void jButtonConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConvertActionPerformed

    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    boolean success = doConvert(newData, oldData, pData, ConvertExperimentDialog.this);
    if (success) {
        ConvertExperimentDialog.this.jTextMessage.setText("done! ");
    }
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_jButtonConvertActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.molgen.genomeCATPro.guimodul.experiment.ExperimentDataView experimentDataViewNew;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonConvert;
    private javax.swing.JLabel jTextMessage;
    // End of variables declaration//GEN-END:variables
}
