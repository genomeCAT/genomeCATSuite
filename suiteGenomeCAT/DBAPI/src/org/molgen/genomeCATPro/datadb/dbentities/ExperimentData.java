package org.molgen.genomeCATPro.datadb.dbentities;

/**
 * @name ExperimentData
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;
import org.molgen.genomeCATPro.common.Defaults.DataType;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.TrackService;

/**
 * 090413 kt new transient member: nofImportData 090413 kt new transient member:
 * nofImportErrors
 */
@Entity
@Table(name = "ExperimentList")
public class ExperimentData implements Serializable, Data {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experimentListID", nullable = false)
    private Long experimentListID;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "genomeRelease", nullable = false)
    private String genomeRelease;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Lob
    @Column(name = "description")
    private String description;
    @Column(name = "tableData", nullable = false)
    private String tableData = "none";
    @Lob
    @Column(name = "procProcessing")
    private String procProcessing;
    @Lob
    @Column(name = "paramProcessing")
    private String paramProcessing;
    @Column(name = "dataType", nullable = false)
    private String dataType;
    @Column(name = "clazz")
    private String clazz;
    @Column(name = "nofSpots")
    private Integer nofSpots;
    @Column(name = "nofBadSpots")
    private Integer nofBadSpots;
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

    @Transient
    private int nofImportErrors = 0;
    @Transient
    private int nofImportData = 0;

    @ManyToOne()
    @JoinColumn(name = "idOwner")
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experimentDetailID")
    private ExperimentDetail experiment;
    @ManyToOne
    @JoinColumn(name = "platformListID")
    private PlatformData platformdata;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentID")
    private ExperimentData parent;

    @Override
    public ExperimentData getParent() {
        return this.parent;
    }

    public PlatformData getPlatformdata() {
        return platformdata;
    }

    public void setPlatformdata(PlatformData platformdata) {
        this.platformdata = platformdata;
    }

    public void setParent(ExperimentData o) {

        this.parent = o;

    }
    // sample children

    public List<ExperimentData> getChilrenList() {
        List<ExperimentData> l = ExperimentService.listChildrenExperimentData(this);
        return (l == null || l == Collections.EMPTY_LIST ? new Vector<ExperimentData>() : l);
    }
    // track children
    @OneToMany(mappedBy = "parentExperiment", fetch = FetchType.LAZY)
    private List<Track> tracks;

    public List<Track> getTracks() {
        if (tracks == null) {
            tracks = this.getTrackList();
        }

        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        if (this.tracks != null) {
            this.tracks.clear();
        } else {
            this.tracks = new Vector<Track>();
        }
        this.tracks.addAll(tracks);
    }

    /*
    public void addTrack(Track t) {
    if (!this.getTracks().contains(t)) {
    this.getTracks().add(t);
    }
    if (!t.getParentExperiment().equals(owner)) {
    t.setParentExperiment(this);
    }
    }
     */
    public List<Track> getTrackList() {
        List<Track> l = TrackService.listChildrenForExperimentData(this);
        return (l == null || l == Collections.EMPTY_LIST ? new Vector<Track>() : l);
    }

    public ExperimentDetail getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentDetail experiment) {
        ExperimentDetail o = experiment;
        this.experiment = experiment;

    }

    public void copy(ExperimentData s) {
        //this.setChildren(s.getChildren());
        this.setClazz(s.getClazz());
        this.setCreated(s.getCreated());
        this.setDataType(s.getDataType());
        this.setDescription(s.getDescription());
        if (this.getExperiment() == null) {
            this.setExperiment(new ExperimentDetail());
        }
        this.getExperiment().copy(s.getExperiment());
        if (this.getPlatformdata() == null) {
            this.setPlatformdata(new PlatformData());
        }
        this.getPlatformdata().copy(s.getPlatformdata());

        this.setGenomeRelease(s.getGenomeRelease());
        this.setId(s.getId());
        this.setMaxRatio(s.getMaxRatio());
        this.setMean(s.getMean());
        this.setMedian(s.getMedian());
        this.setMinRatio(s.getMinRatio());
        this.setMaxRatio(s.getMaxRatio());
        this.setModified(s.getModified());
        this.setName(s.getName());
        this.setNofBadSpots(s.getNofBadSpots());
        this.setNof(s.getNof());
        this.setTableData(s.getTableData());
        this.setOriginalFile(s.getOriginalFile());
        this.setOwner(s.getOwner());
        this.setParent(s.getParent());
        this.setParamProcessing(s.getParamProcessing());
        this.setProcProcessing(s.getProcProcessing());
        this.setStddev(s.getStddev());
        this.setVariance(s.getVariance());

    }

    /**
     *
     * @return
     */
    @Override
    public String toFullString() {
        try {
            return this.getId() + ","
                    + this.getName() + ","
                    + this.getCreated() + ", "
                    + this.getModified() + ","
                    + this.getGenomeRelease() + ","
                    + this.getDataType() + ","
                    + this.getDescription() + ","
                    + this.getOriginalFile() + ","
                    + this.getOwner() != null ? this.getOwner().toString() : "no owner " + ","
                            + this.getParent() != null ? this.getParent().toString() : " no parent " + ","
                                    + this.getParamProcessing() + ","
                                    + this.getProcProcessing() + ","
                                    + this.getExperiment() != null ? this.getExperiment().toFullString() : "no detail";
        } catch (Exception e) {
            return this.toString();
        }
    }

    /**
     *
     * @param RAW
     */
    @Override
    public void setDataType(DataType RAW) {
        this.setDataType(RAW.toString());
    }

    /**
     *
     * @param o
     */
    @Override
    public void setOwner(User o) {
        if (this.owner != null) {
            this.owner.getSamples().remove(this);
        }
        this.owner = o;
        if (this.owner != null) {
            this.owner.getSamples().add(this);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public User getOwner() {
        return this.owner;
    }

    public int getNofImportData() {
        return nofImportData;
    }

    public void setNofImportData(int _nofImportData) {
        this.nofImportData = _nofImportData;
    }

    public int getNofImportErrors() {
        return nofImportErrors;
    }

    public void setNofImportErrors(int nofImportErrors) {
        this.nofImportErrors = nofImportErrors;
    }

    public ExperimentData() {
        this.experiment = new ExperimentDetail();
    }

    ExperimentData(String string) {
        this();
        this.name = string;
    }

    public ExperimentData(Long id) {
        this();
        this.experimentListID = id;
    }

    /**
     *
     * @return
     */
    @Override
    public Long getId() {
        return this.experimentListID;
    }

    /**
     *
     * @param id
     */
    @Override
    public void setId(Long id) {
        this.experimentListID = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        this.initTableData();
        changeSupport.firePropertyChange("name", oldName, name);
    }

    @Override
    public String getGenomeRelease() {
        return genomeRelease;
    }

    @Override
    public void setGenomeRelease(GenomeRelease genomeRelease) {
        String oldGenomeRelease = this.genomeRelease;
        this.genomeRelease = genomeRelease.toString();
        changeSupport.firePropertyChange("genomeRelease", oldGenomeRelease, genomeRelease.toString());
    }

    public void setGenomeRelease(String genomeRelease) {
        String oldGenomeRelease = this.genomeRelease;
        this.genomeRelease = genomeRelease;
        changeSupport.firePropertyChange("genomeRelease", oldGenomeRelease, genomeRelease);
    }

    @Override
    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        Date oldModified = this.modified;
        this.modified = modified;
        changeSupport.firePropertyChange("modified", oldModified, modified);
    }

    @Override
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        Date oldCreated = this.created;
        this.created = created;
        changeSupport.firePropertyChange("created", oldCreated, created);
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        changeSupport.firePropertyChange("description", oldDescription, description);
    }

    @Override
    public String getTableData() {

        if (this.tableData == null) {
            this.initTableData();
        }
        return tableData;
    }

    public void initTableData() {
        this.setTableData(Utils.getUniquableName(this.getName()) + "_"
                + (this.genomeRelease != null ? GenomeRelease.toRelease(genomeRelease).toShortString() : "") + "_Spots");
        //this.setTableData(this.getName() + "_" + this.getGenomeRelease(). + "_Spots");
        this.setTableData(this.getTableData().replace("-", "_"));
        this.setTableData(this.getTableData().replace(" ", "_"));
    }

    public void setTableData(String tableData) {

        this.tableData = tableData;

    }

    @Override
    public String getProcProcessing() {
        return procProcessing;
    }

    public void addProcProcessing(String procProcessing) {
        String oldProcProcessing = this.procProcessing;
        if (this.procProcessing == null) {
            this.procProcessing = procProcessing;
        } else {
            this.procProcessing += procProcessing;
        }
        changeSupport.firePropertyChange("procProcessing", oldProcProcessing, procProcessing);
    }

    @Override
    public void setProcProcessing(String procProcessing) {
        String oldProcProcessing = this.procProcessing;
        this.procProcessing = procProcessing;
        changeSupport.firePropertyChange("procProcessing", oldProcProcessing, procProcessing);
    }

    @Override
    public String getParamProcessing() {
        return paramProcessing;
    }

    public void setParamProcessing(String paramProcessing) {
        String oldParamProcessing = this.paramProcessing;
        this.paramProcessing = paramProcessing;
        changeSupport.firePropertyChange("paramProcessing", oldParamProcessing, paramProcessing);
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        String oldDataType = this.dataType;
        this.dataType = dataType;

        changeSupport.firePropertyChange("dataType", oldDataType, dataType);
    }

    @Override
    public String getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(String clazz) {
        String oldClazz = this.clazz;
        this.clazz = clazz;
        changeSupport.firePropertyChange("clazz", oldClazz, clazz);
    }

    public Integer getNof() {
        return nofSpots;
    }

    public void setNof(Integer nofSpots) {
        this.setNofSpots(nofSpots);
    }

    public Integer getNofSpots() {
        return nofSpots;
    }

    public void setNofSpots(Integer nofSpots) {
        Integer oldNofSpots = this.nofSpots;
        this.nofSpots = nofSpots;
        changeSupport.firePropertyChange("nofSpots", oldNofSpots, nofSpots);
    }

    public Integer getNofBadSpots() {
        return nofBadSpots;
    }

    public void setNofBadSpots(Integer nofBadSpots) {
        Integer oldNofBadSpots = this.nofBadSpots;
        this.nofBadSpots = nofBadSpots;
        changeSupport.firePropertyChange("nofBadSpots", oldNofBadSpots, nofBadSpots);
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        Double oldMean = this.mean;
        this.mean = mean;
        changeSupport.firePropertyChange("mean", oldMean, mean);
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        Double oldMedian = this.median;
        this.median = median;
        changeSupport.firePropertyChange("median", oldMedian, median);
    }

    public Double getStddev() {
        return stddev;
    }

    public void setStddev(Double stddev) {
        Double oldStddev = this.stddev;
        this.stddev = stddev;
        changeSupport.firePropertyChange("stddev", oldStddev, stddev);
    }

    public Double getMinRatio() {
        return minRatio;
    }

    public void setMinRatio(Double minRatio) {
        Double oldMinRatio = this.minRatio;
        this.minRatio = minRatio;
        changeSupport.firePropertyChange("minRatio", oldMinRatio, minRatio);
    }

    public Double getMaxRatio() {
        return maxRatio;
    }

    public void setMaxRatio(Double maxRatio) {
        Double oldMaxRatio = this.maxRatio;
        this.maxRatio = maxRatio;
        changeSupport.firePropertyChange("maxRatio", oldMaxRatio, maxRatio);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.experimentListID != null ? this.experimentListID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExperimentData)) {
            return false;
        }
        ExperimentData other = (ExperimentData) object;
        if ((this.experimentListID == null && other.experimentListID != null) || (this.experimentListID != null
                && !this.experimentListID.equals(other.experimentListID))) {
            return false;
        }
        return true;
    }

    public void setVariance(Double var) {
        Double oldVar = this.getVariance();
        this.variance = var;
        changeSupport.firePropertyChange("variance", oldVar, var);
    }

    public String getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(String path) {
        String oldFile = this.originalFile;
        this.originalFile = path;
        changeSupport.firePropertyChange("originalFile", oldFile, path);
    }

    @Override
    public String toString() {
        return "entities.ExperimentData[id=" + this.experimentListID + "]";
    }

    @PrePersist
    protected void onCreate() {
        created = modified = new Date();

        Logger.getLogger(ExperimentData.class.getName()).log(Level.INFO,
                "create: {0}", this.toString());
    }

    @PreUpdate
    protected void onUpdate() {
        modified = new Date();
        Logger.getLogger(ExperimentData.class.getName()).log(Level.INFO,
                "update: {0}", this.toString());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public Double getVariance() {
        return variance;
    }

    public boolean allowSegmentation() {
        if (this.dataType.contentEquals(Defaults.DataType.SEGMENTS.toString())) {
            return false;
        }

        return true;

    }

    public StringBuffer getMetaText() {
        StringBuffer text = new StringBuffer();
        text.append(new String(this.getExperiment().getName() + "\n"));
        text.append(new String("Method: \t" + this.getExperiment().getMethod() + "\n"));
        text.append(new String("Type: \t" + this.getExperiment().getType() + "\n"));

        for (SampleInExperiment sie : this.getExperiment().getSamples()) {
            if (sie.isIsCy3()) {
                text.append(new String("Cy3 (green): \t" + sie.getSample().getName() + "\n"));
            }
        }
        for (SampleInExperiment sie : this.getExperiment().getSamples()) {
            if (sie.isIsCy5()) {
                text.append(new String("Cy5 (red): \t" + sie.getSample().getName() + "\n"));
            }
        }
        return text;
    }

    public void setParent(Data o) {
        if (o instanceof ExperimentData) {
            this.setParent((ExperimentData) o);
        }

    }

    public void copy(Data d) {
        if (d instanceof ExperimentData) {
            this.copy((ExperimentData) d);
        } else {
            throw new RuntimeException("ExperimentData.copy: no valid copy source "
                    + d.getClass().getName());
        }
    }

    public String getIconPath() {
        try {

            Region d = RegionLib.getRegionClazz(this.getClazz());
            return d.getIconPath();

        } catch (Exception ex) {
            Logger.getLogger(ExperimentData.class.getName()).log(Level.WARNING,
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
        Logger.getLogger(ExperimentData.class.getName()).log(Level.INFO, "addChild "
                + d.toFullString());

        changeSupport.firePropertyChange(Data.ADD, this, null);
    }

    public void removeChildData(Data d) {

        Logger.getLogger(ExperimentData.class.getName()).log(Level.INFO, "removeChild"
                + this.toFullString());
        changeSupport.firePropertyChange(Data.REMOVE, this, null);
    }
}
