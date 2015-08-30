package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name  FilterExperimentsDialog.java
 * Created on July 6, 2011, 2:57 PM
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * This file is part of the GenomeCA software package.
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.service.DBUtils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.TrackService;

/**
 * 250913   kt  add sort according to project
 * 250913   kt  Sample Fields Default %
 * 250913   kt  include project/study as filter criteria
 */
public class FilterExperimentsDialog extends javax.swing.JDialog {

    Vector arrayList;
    private String genomeRelease = null;
    private final String SOURCE_EXPERIMENT = "Experiment";
    private final String SOURCE_TRACK = "Track";

    /** Creates new form FilterExperimentsDialog */
    public FilterExperimentsDialog(java.awt.Frame parent, boolean modal, String release) {
        super(parent, modal);
        this.arrayList = new Vector();
        this.genomeRelease = release;
        initComponents();
        if (this.genomeRelease != null) {
            this.jComboBoxRelease.setModel(new DefaultComboBoxModel(new String[]{this.genomeRelease}));
            this.jComboBoxRelease.setEnabled(false);
        } else {
            this.jComboBoxRelease.setModel(new DefaultComboBoxModel(this.getReleaseList()));
            this.jComboBoxRelease.setEnabled(true);
            this.jComboBoxRelease.setSelectedIndex(2);
        }
        setLocationRelativeTo(null);
    }

    public String getGenomeRelease() {
        return genomeRelease;
    }

    public void setGenomeRelease(String genomeRelease) {
        this.genomeRelease = genomeRelease;
    }

    public static Data[] getDataList(Defaults.GenomeRelease release) {

        FilterExperimentsDialog l = new FilterExperimentsDialog(
                null, true,
                release != null ? release.toString() : null); // modal

        l.setVisible(true);
        return l.getArrayList();


    }

    @SuppressWarnings("unchecked")
    // todo doppelte Einträge entfernen - entstehen durch 1-2 Samples pro track
    public Data[] getArrayList() {

        Map<String, Data> ids = new HashMap<String, Data>();

        Vector v;
        int ind_last = -1;
        Logger.getLogger(FilterExperimentsDialog.class.getName()).log(
                Level.INFO, "Size: " + arrayList.size());
        Data d = null;
        for (int i = 0; i < arrayList.size(); i++) {
            v = (Vector) arrayList.get(i);
            Boolean selected = (Boolean) v.get(0);


            if (selected.booleanValue() == true) {
                v.remove(0); // selected = true	

                ind_last = v.size() - 1;
                // keep only one entryp per experimentId plus datattype

                if (!ids.containsKey(v.get(0))) {
                    if (v.get(ind_last) == this.SOURCE_EXPERIMENT) {
                        d = ExperimentService.getExperimentByDataId((Long) v.get(0));
                    }
                    if (v.get(ind_last) == this.SOURCE_TRACK) {
                        d = TrackService.getTrackById((Long) v.get(0));
                    }
                    if (d == null) {
                        throw new RuntimeException("Data " + v.get(0) + " not found");
                    }
                    Logger.getLogger(FilterExperimentsDialog.class.getName()).log(
                            Level.INFO, "FilterExperimentsDialog: " + d.toString());
                    // newList.add(v.clone());
                    ids.put(new String((String) v.get(ind_last) + v.get(0)), d);
                }
            }
        }


        Logger.getLogger(FilterExperimentsDialog.class.getName()).log(
                Level.INFO, "results: " + ids.size());

        return ids.values().toArray(new Data[ids.values().size()]);
    }

    public void setArrayList(Vector arrayList) {
        this.arrayList = arrayList;
    }

    Vector<String> getMethodList() {
        Vector<String> list = new Vector<String>();
        list.add("%");
        list.addAll(DBUtils.getAllArrayMethods());
        return list;
    }

    Vector<String> getTypeList() {
        Vector<String> list = new Vector<String>();
        list.add("%");
        list.addAll(DBUtils.getAllArrayTypes());
        return list;
    }

    Vector<String> getReleaseList() {
        Vector<String> list = new Vector<String>();

        list.addAll(DBUtils.getAllReleases());
        return list;
    }

    Vector<String> getDataTypeList() {
        Vector<String> list = new Vector<String>();
        list.add("%");
        list.addAll(DBUtils.getAllDataTypes());
        return list;
    }
    final static String selectExperiment =
            " select r.experimentListID,   r.data, r.procProcessing, r.method,  " +
            " '','', r.std as std," +
            " r.experiment, r.genomeRelease " +
            " from " +
            "(select el.experimentListID, ed.experimentDetailID,  el.name as data, " +
            "ed.method, el.procProcessing, ed.name as experiment, el.genomeRelease, ess.name as std " +
            " from ExperimentDetail as ed inner join ExperimentList as el " +
            " on (ed.experimentDetailID = el.experimentDetailID ) " +
            " left outer join  " +
            " ( select e.experimentDetailID , s.name from ExperimentAtStudy as es, Study as s, ExperimentDetail as e " +
            " where s.StudyID = es.StudyID and e.experimentDetailID = es.experimentDetailID " +
            " and s.name like ? ) as ess " + //1
            " on (ess.experimentDetailID = ed.experimentDetailID) " +
            " where ed.name like ? " + //2
            " and ed.method like ? " + //3
            " and ed.type like ?     " + //4
            " and el.genomeRelease like ? " + //5
            " and el.name like ? " + //6
            " and ed.nofChannel like ? " + //7
            " and el.dataType like ? " + //8
            //" and r.std like ? " + //8
            ") as r" +
            //" where r.std like ? " //8 +
            "";
    final static String selectSampleExperiment =
            " select r.experimentListID,   r.data, r.procProcessing, r.method,  " +
            "sample.name as sample, sample.phenotype, r.std as std," +
            "r.experiment, r.genomeRelease " +
            "from SampleDetail as sample," +
            "(select el.experimentListID, ed.experimentDetailID,  el.name as data, " +
            "ed.method, el.procProcessing, ed.name as experiment, el.genomeRelease, ess.name as std " +
            " from ExperimentDetail as ed inner join ExperimentList as el " +
            " on (ed.experimentDetailID = el.experimentDetailID ) " +
            " left outer join  " +
            " ( select e.experimentDetailID , s.name from ExperimentAtStudy as es, Study as s, ExperimentDetail as e " +
            " where s.StudyID = es.StudyID and e.experimentDetailID = es.experimentDetailID" +
            " and s.name like ? ) as ess " + //1) as ess " +
            " on (ess.experimentDetailID = ed.experimentDetailID) " +
            " where ed.name like ? " + //2
            " and ed.method like ? " + //3
            "and ed.type like ?     " + //4
            " and el.genomeRelease like ? " + //5 
            " and el.name like ? " + //6
            " and ed.nofChannel like ? " + //7
            " and el.dataType like ? " + //8
            //" and ess.name like ? " + //8  
            ") as r , SampleInExperiment as sie " +
            " where sie.experimentDetailID = r.experimentDetailID " +
            " and sie.sampleDetailID = sample.sampleDetailID  " +
            //" and r.std like ? " + //8
            " and sample.name like ? " + // 9 
            " and sample.phenotype like ? " + // 10
            " group by r.experimentListID";
    final static String selectTrack =
            " select r.TrackID, r.name as track , r.procProcessing, r.dataType, '','', " +
            "r.std as study, r.exp as experiment, r.genomeRelease  " +
            "from (" +
            "(select '' as exp, null as experimentDetailID, " +
            " tas.name as std, t.* " +
            " from TrackList as t left join " +
            " (select  s.name, ts.* from Study as s, TrackAtStudy as ts " +
            " where  s.StudyID = ts.StudyID ) as tas " +
            " on (t.TrackID =  tas.trackID ) where t.parentExperimentID is null " +
            ") UNION (" +
            "select e.name as exp, e.experimentDetailID, ess.name as std, t.* " +
            "from TrackList as t inner join ExperimentDetail as e " +
            " on (t.parentExperimentID = e.experimentDetailID ) " +
            " left outer join ( select es.experimentDetailID , s.name " +
            " from ExperimentAtStudy as es ,Study as s where s.StudyID = es.StudyID) as ess " +
            " on (ess.experimentDetailID = e.experimentDetailID) " +
            " where  e.name like ? " + //1
            " and e.method like ? " + //2
            " and e.type like ? " + //3
            " and  e.nofChannel like ?  " + //4

            ") ) as r " +
            " where r.genomeRelease like ? " + //5
            " and r.std like ? " + //6
            " and r.name like ? " + //7
            " and r.dataType like ? " //8
            ;
    final static String selectSampleTrack =
            // kt 220512 update selects 
            "select r.TrackID, r.name as track , r.procProcessing, " +
            "r.dataType, sample.name as sample, sample.phenotype, " +
            "r.std as study, r.exp as experiment, r.genomeRelease " +
            "from SampleDetail as sample, (" +
            "(select '' as exp, '' as experimentDetailID, " +
            " tas.name as std, t.* " +
            " from TrackList as t left join " +
            " (select  s.name, ts.* from Study as s, TrackAtStudy as ts " +
            " where  s.StudyID = ts.StudyID ) as tas " +
            " on (t.TrackID =  tas.trackID ) where t.parentExperimentID is null " +
            ") UNION (" +
            "select e.name as exp, e.experimentDetailID, ess.name as std, t.* " +
            "from TrackList as t inner join ExperimentDetail as e " +
            " on (t.parentExperimentID = e.experimentDetailID ) " +
            " left outer join ( select es.experimentDetailID , s.name " +
            " from ExperimentAtStudy as es ,Study as s where s.StudyID = es.StudyID) as ess " +
            " on (ess.experimentDetailID = e.experimentDetailID) " +
            " where  e.name like ? " + //1
            " and e.method like ? " + //2
            " and e.type like ? " + //3
            " and  e.nofChannel like ?  " + //4
            ") " +
            ") as r " +
            " where r.genomeRelease like ? " + //5
            " and r.std like ? " + // 6
            " and r.name like ? " + //7
            " and r.dataType like ? " +//8

            " and  ( exists (  select 1 from SampleInTrack as sit  " +
            " where  sit.trackID = r.trackID   and sit.sampleDetailID = sample.sampleDetailID ) " +
            " or exists ( select 1 from SampleInExperiment as sie  " +
            " where sie.experimentDetailID = r.experimentDetailID " +
            " and sie.sampleDetailID = sample.sampleDetailID ) ) " +
            " and sample.name like ? " + // 9
            " and sample.phenotype like ? " + //10
            "group by r.TrackID";
    final static String[] colsExperiment = {
        "load",
        "ID",
        "Name",
        "ProcProcessing",
        "Method/Type",
        "Sample",
        "Phenotype",
        "Study",
        "Experiment",
        "Release",
        "Source"
    };

    @SuppressWarnings("unchecked")
    /**
     * build filter query resp user input criteria
     * 2 basic queries against db, one for tracks, one for experiments.
     * tracks must have study or experiment as parent, experiments can 
     * exist without such.
     * 
     * Sample names are only included into the query if really set
     * 
     * All criteria (except study) are complemented with pre/post "%"
     */
    public void doFilter() {
        arrayList.clear();
        String nofchannel = "%";
        if (this.jRadioButtonTwoChannel.isSelected()) {
            nofchannel = "2";
        }
        if (this.jRadioButtonOneChannel.isSelected()) {
            nofchannel = "1";
        }
        boolean inclSample = (this.fieldSampleName.getText().contentEquals("%") &&
                this.fieldSamplePhenotype.getText().contentEquals("%") ? false : true) || this.jCheckBoxInclSample.isSelected();
        //String[] cols = FilterExperimentsDialog.colsExperiment;

        try {

            // get data from experiments
            Connection con = Database.getDBConnection(Defaults.localDB);
            PreparedStatement s;
            ResultSet r;

            if (inclSample) {
                //cols = FilterExperimentsDialog.colsExperiment;
                s = con.prepareStatement(selectSampleExperiment);
            } else {
                s = con.prepareStatement(selectExperiment);
            }


            s.setString(2, "%" + this.fieldName.getText() + "%");
            s.setString(3, this.jComboBoxMethod.getSelectedItem().toString());
            s.setString(4, this.jComboBoxType.getSelectedItem().toString());
            s.setString(5, this.jComboBoxRelease.getSelectedItem().toString());
            s.setString(6, this.fieldDataName.getText());
            s.setString(7, nofchannel);
            s.setString(8, this.jComboBoxDatatype.getSelectedItem().toString());
            s.setString(1, this.jComboBoxStudy.getSelectedItem().toString());
            //s.setString(10, "%" + this.fieldDataName.getText() + "%");
            //s.setString(9, "%" + this.jComboBoxDatatype.getSelectedItem().toString() + "%");
            //s.setString(8, this.jComboBoxRelease.getSelectedItem().toString());
            if (inclSample) {
                s.setString(9, this.fieldSampleName.getText());
                s.setString(10, this.fieldSamplePhenotype.getText());
            }
            r = s.executeQuery();

            while (r.next()) {
                Vector v = new Vector();
                v.add(new Boolean(false));
                v.add(r.getLong(1));
                v.add(r.getString(2));
                v.add(r.getString(3) != null ? r.getString(3) : "");
                v.add(r.getString(4) != null ? r.getString(4) : "");
                v.add(r.getString(5) != null ? r.getString(5) : "");
                v.add(r.getString(6) != null ? r.getString(6) : "");
                v.add(r.getString(7) != null ? r.getString(7) : "");
                v.add(r.getString(8) != null ? r.getString(8) : "");
                v.add(r.getString(9) != null ? r.getString(9) : "");


                v.add(this.SOURCE_EXPERIMENT);
                System.out.println(v);
                arrayList.add(v);
            }

            r.close();
            if (inclSample) {

                //cols = FilterExperimentsDialog.colsExperiment;
                s = con.prepareStatement(selectSampleTrack);
            } else {
                s = con.prepareStatement(selectTrack);
            }

            // get Data from Tracks
            /* where  e.name like ? " + //1
            " and e.method like ? " + //2
            " and e.type like ? " + //3
            " and  e.nofChannel like ?  ) ) as r " + //4
            " where r.genomeRelease like ? " + //5
            " and r.name like ? " + //6
            " and r.dataType like ? " +//77*/
            s.setString(1, "%" + this.fieldName.getText() + "%");
            s.setString(2, this.jComboBoxMethod.getSelectedItem().toString());
            s.setString(3, this.jComboBoxType.getSelectedItem().toString());
            s.setString(5, this.jComboBoxRelease.getSelectedItem().toString());
            s.setString(7, this.fieldDataName.getText());
            s.setString(4, nofchannel);
            s.setString(8, this.jComboBoxDatatype.getSelectedItem().toString());
            s.setString(6, this.jComboBoxStudy.getSelectedItem().toString());
            //s.setString(10, "%" + this.fieldDataName.getText() + "%");
            //s.setString(9, "%" + this.jComboBoxDatatype.getSelectedItem().toString() + "%");
            //s.setString(8, this.jComboBoxRelease.getSelectedItem().toString());
            if (inclSample) {
                s.setString(9, this.fieldSampleName.getText());
                s.setString(10, this.fieldSamplePhenotype.getText());
            }
            r = s.executeQuery();

            while (r.next()) {
                Vector v = new Vector();
                v.add(new Boolean(false));
                v.add(r.getLong(1));
                v.add(r.getString(2));
                v.add(r.getString(3) != null ? r.getString(3) : "");
                v.add(r.getString(4) != null ? r.getString(4) : "");
                v.add(r.getString(5) != null ? r.getString(5) : "");
                v.add(r.getString(6) != null ? r.getString(6) : "");
                v.add(r.getString(7) != null ? r.getString(7) : "");
                v.add(r.getString(8) != null ? r.getString(8) : "");
                v.add(r.getString(9) != null ? r.getString(9) : "");

                v.add(this.SOURCE_TRACK);
                // System.out.println(v);
                arrayList.add(v);
            }
            r.close();

        } catch (Exception e) {
            Logger.getLogger(FilterExperimentsDialog.class.getName()).log(
                    Level.SEVERE, "doFilter", e);

            JOptionPane.showMessageDialog(this, "FilterExperimentsDialog Error getting Data see logfile: ");
        }
        this.jTableResults.setModel(
                new MyTableModel(
                arrayList,
                new Vector(Arrays.asList(FilterExperimentsDialog.colsExperiment))));

        //250913    kt  add sort according to project
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(7, SortOrder.DESCENDING));

        this.jTableResults.getRowSorter().setSortKeys(sortKeys);

        if (arrayList.size() == 0) {
            JOptionPane.showMessageDialog(this, " no experiments found");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupChannel = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableResults = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        fieldName = new javax.swing.JTextField();
        jComboBoxMethod = new javax.swing.JComboBox();
        jComboBoxType = new javax.swing.JComboBox();
        jRadioButtonOneChannel = new javax.swing.JRadioButton();
        jRadioButtonTwoChannel = new javax.swing.JRadioButton();
        jRadioButtonBoth = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        fieldSampleName = new javax.swing.JTextField();
        fieldSamplePhenotype = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jCheckBoxInclSample = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jComboBoxDatatype = new javax.swing.JComboBox();
        fieldDataName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButtonReset = new javax.swing.JButton();
        jButtonFilter = new javax.swing.JButton();
        jButtonSelectALL = new javax.swing.JButton();
        jButtonDeselectALL = new javax.swing.JButton();
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jComboBoxStudy = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxRelease = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.title")); // NOI18N
        setName("Filter Experiments"); // NOI18N

        jTableResults.setAutoCreateRowSorter(true);
        jTableResults.setModel(new MyTableModel(this.arrayList, new Vector(Arrays.asList(this.colsExperiment))));
        jScrollPane1.setViewportView(jTableResults);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jPanel1.border.title_2_1"))); // NOI18N

        fieldName.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.fieldName.text_1_1")); // NOI18N

        jComboBoxMethod.setModel(new DefaultComboBoxModel(this.getMethodList()));

        jComboBoxType.setModel(new DefaultComboBoxModel(this.getTypeList()));

        buttonGroupChannel.add(jRadioButtonOneChannel);
        jRadioButtonOneChannel.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonOneChannel.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jRadioButtonOneChannel.text_2")); // NOI18N

        buttonGroupChannel.add(jRadioButtonTwoChannel);
        jRadioButtonTwoChannel.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonTwoChannel.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jRadioButtonTwoChannel.text_2")); // NOI18N

        buttonGroupChannel.add(jRadioButtonBoth);
        jRadioButtonBoth.setFont(new java.awt.Font("Dialog", 0, 12));
        jRadioButtonBoth.setSelected(true);
        jRadioButtonBoth.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jRadioButtonBoth.text_2")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel1.text_2_1")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel2.text_2_1")); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel3.text_2_1")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonBoth)
                    .addComponent(jRadioButtonOneChannel)
                    .addComponent(jRadioButtonTwoChannel)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBoxType, javax.swing.GroupLayout.Alignment.LEADING, 0, 233, Short.MAX_VALUE)
                            .addComponent(jComboBoxMethod, javax.swing.GroupLayout.Alignment.LEADING, 0, 233, Short.MAX_VALUE)
                            .addComponent(fieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jRadioButtonBoth, jRadioButtonOneChannel, jRadioButtonTwoChannel});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addComponent(jRadioButtonOneChannel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonTwoChannel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonBoth)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jPanel2.border.title_2_1"))); // NOI18N

        fieldSampleName.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.fieldSampleName.text_1_1")); // NOI18N
        fieldSampleName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldSampleNameActionPerformed(evt);
            }
        });

        fieldSamplePhenotype.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.fieldSamplePhenotype.text_1_1")); // NOI18N
        fieldSamplePhenotype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldSamplePhenotypeActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel4.text_2_1")); // NOI18N

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel5.text_2_1")); // NOI18N

        jCheckBoxInclSample.setFont(new java.awt.Font("Dialog", 0, 12));
        jCheckBoxInclSample.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jCheckBoxInclSample.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(fieldSamplePhenotype)
                    .addComponent(jCheckBoxInclSample, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldSampleName, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(fieldSampleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(fieldSamplePhenotype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jCheckBoxInclSample))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jPanel3.border.title_2_1"))); // NOI18N

        jComboBoxDatatype.setModel(new DefaultComboBoxModel(this.getDataTypeList()));

        fieldDataName.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.fieldDataName.text_1")); // NOI18N

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel6.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel6.text_2_1")); // NOI18N

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel7.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel7.text_2_1")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldDataName, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(jComboBoxDatatype, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fieldDataName, jComboBoxDatatype});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldDataName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxDatatype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(42, 42, 42))
        );

        jButtonReset.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jButtonReset.text_2_1")); // NOI18N
        jButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });

        jButtonFilter.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jButtonFilter.text_2_1")); // NOI18N
        jButtonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFilterActionPerformed(evt);
            }
        });

        jButtonSelectALL.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jButtonSelectALL.text_2")); // NOI18N
        jButtonSelectALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectALLActionPerformed(evt);
            }
        });

        jButtonDeselectALL.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jButtonDeselectALL.text_2")); // NOI18N
        jButtonDeselectALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeselectALLActionPerformed(evt);
            }
        });

        jButtonOK.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jButtonOK.text_2")); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jButtonCancel.text_2")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jPanel4.border.title"))); // NOI18N

        jComboBoxStudy.setEditable(true);
        jComboBoxStudy.setModel((new javax.swing.DefaultComboBoxModel(DBUtils.getStudies())));
        jComboBoxStudy.setSelectedItem(new String("%"));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel9.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel9.text")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxStudy, 0, 198, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxStudy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jPanel5.border.title"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel8.setText(org.openide.util.NbBundle.getMessage(FilterExperimentsDialog.class, "FilterExperimentsDialog.jLabel8.text_2_1")); // NOI18N

        jComboBoxRelease.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxRelease, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jComboBoxRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(654, Short.MAX_VALUE)
                        .addComponent(jButtonSelectALL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDeselectALL)
                        .addGap(30, 30, 30)
                        .addComponent(jButtonOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(418, 418, 418)
                        .addComponent(jButtonFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonReset)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel1, jPanel2, jPanel3, jPanel4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, 0, 109, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonReset)
                    .addComponent(jButtonFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButtonDeselectALL)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonSelectALL)
                    .addComponent(jButtonOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
    this.fieldDataName.setText("%");
    this.fieldName.setText("%");
    this.fieldSampleName.setText("%");
    this.fieldSamplePhenotype.setText("%");
    this.jComboBoxDatatype.setSelectedItem(0);
    this.jComboBoxMethod.setSelectedIndex(0);
    this.jComboBoxType.setSelectedIndex(0);
    this.jComboBoxDatatype.setSelectedIndex(0);
    this.jComboBoxStudy.setSelectedItem(new String("%"));
    if (this.genomeRelease == null) {
        this.jComboBoxRelease.setSelectedIndex(2);
    }
    arrayList.clear();
    this.jTableResults.setModel(new MyTableModel(
            arrayList,
            new Vector<String>(Arrays.asList(FilterExperimentsDialog.colsExperiment))));
// TODO add your handling code here:
}//GEN-LAST:event_jButtonResetActionPerformed

private void jButtonFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFilterActionPerformed

    this.doFilter();
}//GEN-LAST:event_jButtonFilterActionPerformed

private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
    this.dispose();
}//GEN-LAST:event_jButtonOKActionPerformed

private void jButtonDeselectALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeselectALLActionPerformed

    for (int i = 0; i < this.jTableResults.getModel().getRowCount(); i++) {
        this.jTableResults.getModel().setValueAt(new Boolean(false), i, 0);
    }
    return;
}//GEN-LAST:event_jButtonDeselectALLActionPerformed

private void jButtonSelectALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectALLActionPerformed
    for (int i = 0; i < this.jTableResults.getModel().getRowCount(); i++) {
        this.jTableResults.getModel().setValueAt(new Boolean(true), i, 0);
    }
    return;
}//GEN-LAST:event_jButtonSelectALLActionPerformed

private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    this.arrayList.removeAllElements();
    this.dispose();
    return;
}//GEN-LAST:event_jButtonCancelActionPerformed

private void fieldSampleNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldSampleNameActionPerformed
    this.jCheckBoxInclSample.setSelected(this.fieldSampleName.getText().contentEquals("%") &&
            this.fieldSamplePhenotype.getText().contentEquals("%") ? false : true);
}//GEN-LAST:event_fieldSampleNameActionPerformed

private void fieldSamplePhenotypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldSamplePhenotypeActionPerformed
    this.jCheckBoxInclSample.setSelected(this.fieldSampleName.getText().contentEquals("%") &&
            this.fieldSamplePhenotype.getText().contentEquals("%") ? false : true);
}//GEN-LAST:event_fieldSamplePhenotypeActionPerformed
    /**
     * my table model, add method to contain rendered fields
     */
    class MyTableModel extends DefaultTableModel {

        public MyTableModel(java.util.Vector data, java.util.Vector cols) {
            super(data, cols);
        }

        @Override
        public Class getColumnClass(int c) {
            if (this.getRowCount() <= 0) {
                return Object.class;
            }
            Object value = this.getValueAt(0, c);
            return (value == null ? Object.class : value.getClass());
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col > 0) {
                return false;
            } else {
                return true;
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupChannel;
    private javax.swing.JTextField fieldDataName;
    private javax.swing.JTextField fieldName;
    private javax.swing.JTextField fieldSampleName;
    private javax.swing.JTextField fieldSamplePhenotype;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDeselectALL;
    private javax.swing.JButton jButtonFilter;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JButton jButtonSelectALL;
    private javax.swing.JCheckBox jCheckBoxInclSample;
    private javax.swing.JComboBox jComboBoxDatatype;
    private javax.swing.JComboBox jComboBoxMethod;
    private javax.swing.JComboBox jComboBoxRelease;
    private javax.swing.JComboBox jComboBoxStudy;
    private javax.swing.JComboBox jComboBoxType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButtonBoth;
    private javax.swing.JRadioButton jRadioButtonOneChannel;
    private javax.swing.JRadioButton jRadioButtonTwoChannel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableResults;
    // End of variables declaration//GEN-END:variables
    }
