package org.molgen.genomeCATPro.datadb.dbentities;

/**
 * @name Track
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.annotation.RegionLib;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.Defaults.DataType;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.service.TrackService;

/**
 * 270313   kt  new transient member: nofImportData
 * 270313   kt  new transient member: nofImportErrors
 * @author tebel
 */
@Entity
@Table(name = "TrackList")
@NamedQueries({})
public class Track implements Serializable, Data {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TrackID", nullable = false)
    private Long trackID;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "genomeRelease", nullable = false)
    private String genomeRelease;
    @Column(name = "description")
    private String description;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date created;
    @Column(name = "tableData", nullable = false)
    private String tableData = "none";
    @Lob
    @Column(name = "procProcessing")
    private String procProcessing;
    @Lob
    @Column(name = "paramProcessing")
    private String paramProcessing;
    @Column(name = "dataType")
    private String dataType;
    @Column(name = "clazz", nullable = false)
    private String clazz;
    @Column(name = "nofPeaks")
    private Integer nofPeaks;
    @Column(name = "mean")
    private Double mean;
    @Column(name = "median")
    private Double median;
    @Column(name = "stddev")
    private Double stddev;
    @Column(name = "variance")
    private Double variance;
    @Column(name = "minRatio")
    private Double minRatio;
    @Column(name = "maxRatio")
    private Double maxRatio;
    @Lob
    @Column(name = "originalFile", nullable = false)
    private String originalFile;
    // link to original experiment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentExperimentID")
    private ExperimentData parentExperiment;

    @Transient
    private int nofImportErrors = 0;
    @Transient
    private int nofImportData = 0;
    public Track() {
        this.samples = org.jdesktop.observablecollections.ObservableCollections.observableList(new Vector<SampleInTrack>());

    }

    public void setParentExperiment(ExperimentData s) {
        this.parentExperiment = s;
    /*
    if (this.parentExperiment != null) {
    this.parentExperiment.getTracks().remove(this);
    }
    this.parentExperiment = s;
    if (this.parentExperiment != null) {
    this.parentExperiment.getTracks().add(this);
    }
    if (s != null) {
    //s.notifyAddChild();
    }
     */
    }

    public ExperimentData getParentExperiment() {
        return this.parentExperiment;
    }
    @ManyToOne
    @JoinColumn(name = "parentTrackID")
    private Track parentTrack;

    public Track getParentTrack() {
        return this.parentTrack;
    }

    public void setParentTrack(Track o) {
        this.parentTrack = o;
    /*
    if (this.parentTrack != null) {
    this.parentTrack.getChildren().remove(this);
    }
    this.parentTrack = o;
    if (this.parentTrack != null) {
    this.parentTrack.getChildren().add(this);
    }
     */

    }
    // sample children
    @OneToMany(mappedBy = "parentTrack", fetch = FetchType.LAZY)
    private List<Track> children = new Vector<Track>();

    public List<Track> getChildren() {
        return children;
    }

    public List<Track> getChildrenList() {
        List<Track> l = TrackService.listChildrenForTrack(this);
        return (l == null || l == Collections.EMPTY_LIST ? new Vector<Track>() : l);
    }

    public boolean allowSegmentation() {
        if (this.getDataType().contentEquals(Defaults.DataType.SEGMENTS.toString())) {
            return false;
        }     

        return true;

    }

    public void setChildren(List<Track> tracks) {
        if (this.children != null) {
            this.children.clear();
        } else {
            this.children = new Vector<Track>();
        }
        this.children.addAll(tracks);

    }
    @ManyToOne
    @JoinColumn(name = "idOwner")
    private User owner;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "track")
    private List<SampleInTrack> samples;

    public SampleInTrack addSample(SampleDetail sample, boolean inverse) {
        SampleInTrack association = new SampleInTrack();
        association.setSample(sample);
        association.setTrack(this);
        association.setTrackID(this.getTrackID());
        association.setSampleDetailID(sample.getSampleDetailID());
        association.setInverse(inverse);

        this.samples.add(association);
        // Also add the association object to the employee.
        //sample.getExperiments().add(association);
        //changeSupport.firePropertyChange("Samples", null, this.samples);
        return association;
    }

    public void removeSample(SampleInTrack s) {
        int index = -1;
        for (SampleInTrack sit : this.samples) {
            if (sit.equals(s)) {
                index = this.samples.indexOf(sit);
            }
        }
        if (index >= 0) {
            Logger.getLogger(Track.class.getName()).log(Level.INFO,
                    "remove: " + this.samples.get(index));
            this.samples.remove(index);
        }
    }

    public List<SampleInTrack> getSamples() {
        return this.samples;
    }

    public void setSamples(List<SampleInTrack> samples) {
        if (samples == this.samples) {
            return;
        }
        this.samples.clear();
        this.samples.addAll(samples);

    }

    public User getOwner() {
        return owner;
    }

    public Integer getNofPeaks() {
        return this.getNof();
    }

    public void setNofPeaks(Integer nofPeaks) {
        this.setNof(nofPeaks);
    }

    public void setOwner(User o) {
        if (this.owner != null) {
            this.owner.getTracks().remove(this);
        }
        this.owner = o;
        if (this.owner != null) {
            this.owner.getTracks().add(this);
        }
    }

    public int getNofImportData() {
        return nofImportData;
    }

    public void setNofImportData(int nofImportData) {
        this.nofImportData = nofImportData;
    }

    public int getNofImportErrors() {
        return nofImportErrors;
    }

    public void setNofImportErrors(int nofImportErrors) {
        this.nofImportErrors = nofImportErrors;
    }
    
    public String getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(String originalFile) {
        String oldoriginalFile = this.originalFile;
        this.originalFile = originalFile;

        changeSupport.firePropertyChange("originalFile", oldoriginalFile, originalFile);
    }

    public Track(Long regionID) {
        this.trackID = regionID;
    }

    public Track(String name, String release, String type) {
        this.name = name;
        this.genomeRelease = release;
        this.dataType = type;
        this.initTableData();

    }

    void copy(Track s) {
        this.setClazz(s.getClazz());
        this.setCreated(s.getCreated());
        this.setDataType(s.getDataType());
        this.setDescription(s.getDescription());
        this.setGenomeRelease(s.getGenomeRelease());
        this.setId(s.getId());
        this.setMaxRatio(s.getMaxRatio());
        this.setMean(s.getMean());
        this.setMedian(s.getMedian());
        this.setMinRatio(s.getMinRatio());
        this.setMaxRatio(s.getMaxRatio());
        this.setModified(s.getModified());
        this.setName(s.getName());
        this.setNof(s.getNof());
        this.setTableData(s.getTableData());
        this.setOwner(s.getOwner());
        this.setParentExperiment(s.getParentExperiment());
        this.setParentTrack(s.getParentTrack());
        this.setParamProcessing(s.getParamProcessing());
        this.setProcProcessing(s.getProcProcessing());
        this.setStddev(s.getStddev());
        this.setVariance(s.getVariance());
        this.setOriginalFile(s.getOriginalFile());
        List<SampleInTrack> list = new Vector<SampleInTrack>(s.getSamples());
        Collections.copy(list, s.getSamples());
        this.setSamples(list);

        Logger.getLogger(Track.class.getName()).log(Level.INFO, "copy: " + this.toString());
    }

    public Track(Long regionID, String name, String genomicRelease, Date modified, Date created, String tableData, String clazz, User owner) {
        this.trackID = regionID;
        this.name = name;
        this.genomeRelease = genomicRelease;

        this.modified = modified;
        this.created = created;
        this.tableData = tableData;
        this.clazz = clazz;
        this.setOwner(owner);
    }

    public Long getTrackID() {
        return trackID;
    }

    public void setTrackID(Long trackID) {
        Long oldTrackID = this.trackID;
        this.trackID = trackID;

        changeSupport.firePropertyChange("trackID", oldTrackID, trackID);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        //kt 170613 name without .
        String oldName = this.name;
        this.name = name.replace('.', '_');
        

        changeSupport.firePropertyChange("name", oldName, name);

        Logger.getLogger(Track.class.getName()).log(Level.INFO, "setName: " + this.name);
    }

    public String getGenomeRelease() {
        return genomeRelease;
    }

    public void setGenomeRelease(String genomicRelease) {
        String oldGenomeRelease = this.genomeRelease;
        this.genomeRelease = genomicRelease;

        changeSupport.firePropertyChange("genomeRelease", oldGenomeRelease, genomicRelease);


    }

    public String getDescription() {
        return description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        String olddatatype = this.dataType;
        this.dataType = dataType;

        changeSupport.firePropertyChange("dataType", olddatatype, dataType);
    }

    public void setDescription(String description) {
        String olddescription = this.description;
        this.description = description;

        changeSupport.firePropertyChange("description", olddescription, description);
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        Date oldmodified = this.modified;
        this.modified = modified;
        changeSupport.firePropertyChange("modified", oldmodified, modified);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        Date oldcreated = this.created;
        this.created = created;
        changeSupport.firePropertyChange("created", oldcreated, created);
    }

    public String getTableData() {
        if (tableData.contentEquals("none")) {
            this.setTableData(this.getName() + "_" + this.getTrackID());
        }
        return tableData;
    }

    public void setTableData(String tableData) {
        String oldtableData = this.tableData;
        this.tableData = tableData;

        changeSupport.firePropertyChange("tableData", oldtableData, tableData);
    }

    public String getProcProcessing() {
        return procProcessing;
    }

    public void setProcProcessing(String procProcessing) {
        String oldprocProcessing = this.procProcessing;
        this.procProcessing = procProcessing;

        changeSupport.firePropertyChange("procProcessing", oldprocProcessing, procProcessing);
    }

    public String getParamProcessing() {
        return paramProcessing;
    }

    public void setParamProcessing(String paramProcessing) {
        String oldparamProcessing = this.paramProcessing;
         this.paramProcessing = paramProcessing;

        changeSupport.firePropertyChange("paramProcessing", oldparamProcessing, paramProcessing);
    }

    public void addParamProcessing(String paramProcessing) {
        if (this.paramProcessing != null) {
            this.paramProcessing += "\n" + paramProcessing;
        } else {
            this.paramProcessing = paramProcessing;
        }
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        String oldClazz = this.clazz;
        this.clazz = clazz;
        changeSupport.firePropertyChange("clazz", oldClazz, clazz);

    }

    public Double getMaxRatio() {
        return maxRatio;
    }

    public void setMaxRatio(Double maxRatio) {
        Double oldmaxRatio = this.maxRatio;
        this.maxRatio = maxRatio;

        changeSupport.firePropertyChange("maxRatio", oldmaxRatio, maxRatio);
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        Double oldmean = this.mean;
        this.mean = mean;

        changeSupport.firePropertyChange("mean", oldmean, mean);
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        Double oldmedian = this.median;
        this.median = median;

        changeSupport.firePropertyChange("median", oldmedian, median);
    }

    public Double getMinRatio() {
        return minRatio;
    }

    public void setMinRatio(Double minRatio) {
        Double oldminRatio = this.minRatio;
        this.minRatio = minRatio;

        changeSupport.firePropertyChange("minRatio", oldminRatio, minRatio);
    }

    public Integer getNof() {
        return nofPeaks;
    }

    public void setNof(Integer nofPeaks) {
        Integer oldnof = this.nofPeaks;
        this.nofPeaks = nofPeaks;
        changeSupport.firePropertyChange("nof", oldnof, this.nofPeaks);
    }

    public Double getVariance() {
        return variance;
    }

    public void setVariance(Double variance) {
        Double oldvariance = this.variance;
        this.variance = variance;

        changeSupport.firePropertyChange("variance", oldvariance, variance);
    }

    public Double getStddev() {
        return stddev;
    }

    public void setStddev(Double stddev) {
        Double oldstddev = this.stddev;
        this.stddev = stddev;

        changeSupport.firePropertyChange("stddev", oldstddev, stddev);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (trackID != null ? trackID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Track)) {
            return false;
        }
        Track other = (Track) object;
        if ((this.trackID == null && other.trackID != null) || (this.trackID != null && !this.trackID.equals(other.trackID))) {
            return false;
        }
        return true;
    }

    public String toFullString() {
        return new String("iid: " + this.trackID + " name: " + name);
    }

    @Override
    public String toString() {
        return "entities.Track[trackID=" + trackID + "]";
    }

    @PrePersist
    protected void onCreate() {
        this.setCreated(new Date());
        this.setModified(this.getCreated());



        Logger.getLogger(Track.class.getName()).log(Level.INFO, "create: " + this.toString());
    }

    @PreUpdate
    protected void onUpdate() {
        this.setModified(new Date());
        Logger.getLogger(Track.class.getName()).log(Level.INFO, "update: " + this.toString());
    }

    public Long getId() {
        return this.getTrackID();
    }

    public Data getParent() {
        if (this.getParentExperiment() != null) {
            return this.getParentExperiment();
        } else {
            return this.getParentTrack();
        }
    }

    public StringBuffer getMetaText() {
        StringBuffer text;
        if (this.getParent() != null) {
            text = this.getParent().getMetaText();
        } else {
            text = new StringBuffer();
        }
        text.append(this.name + "\n");
        text.append(new String("NofPeaks: \t" + this.getNof() + "\n"));

        return text;
    }

    public void setDataType(DataType d) {
        this.setDataType(d.toString());
    }

    public void setGenomeRelease(GenomeRelease d) {
        this.setGenomeRelease(d.toString());
        this.initTableData();

    }

    public void setParent(Data o) {
        if (o instanceof Track) {
            this.setParentTrack((Track) o);
            this.setParentExperiment(((Track) o).getParentExperiment());
        }
        if (o instanceof ExperimentData) {
            this.setParentExperiment((ExperimentData) o);
        }

    }

    public void copy(Data d) {
        if (d instanceof Track) {
            this.copy((Track) d);
        } else {
            throw new RuntimeException("Track.copy: no valid copy source " +
                    d.getClass().getName());
        }
    }

    public void setId(Long id) {
        this.setTrackID(id);
    }

    public void initTableData() {
        this.setTableData(Utils.getUniquableName(this.getName()) + "_" +
                (this.genomeRelease != null ? GenomeRelease.toRelease(genomeRelease).toShortString() : "") + "_Spots");
        //this.setTableData(this.getName() + "_" + this.getGenomeRelease(). + "_Spots");
        this.setTableData(this.getTableData().replace("-", "_"));
        this.setTableData(this.getTableData().replace(" ", "_"));
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public String getIconPath() {
        try {

            Region d = RegionLib.getRegionClazz(this.getClazz());
            return d.getIconPath();



        } catch (Exception ex) {
            Logger.getLogger(Track.class.getName()).log(Level.WARNING,
                    "", ex);
        }
        return null;
    }

    public void addChildData(Data d) {
        if (d.getParent() != null) {
            if (d.getParent() != this) {
                d.getParent().removeChildData(d);
            }
        }
        d.setParent(this);
        Logger.getLogger(ExperimentData.class.getName()).log(Level.INFO, "addChild" +
                this.toFullString());

        changeSupport.firePropertyChange(Data.ADD, this, null);
    }

    public void removeChildData(Data d) {

        Logger.getLogger(ExperimentData.class.getName()).log(Level.INFO, "removeChild" +
                this.toFullString());
        changeSupport.firePropertyChange(Data.REMOVE, this, null);
    }
}
