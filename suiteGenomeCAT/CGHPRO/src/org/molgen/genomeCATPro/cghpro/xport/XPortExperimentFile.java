/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cghpro.xport;

import java.util.List;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;

/**
 *
 * @author tebel
 */
public interface XPortExperimentFile extends XPortImport {

   

    public String getMethod();

    public String getType();

    // modul name
    public void newImportFile(String filename) throws Exception;

    public void setCenterMean(boolean selected);

    public void setCenterMedian(boolean selected);

    public ExperimentDetail getExperimentDetail();

    public void setExperimentDetail(ExperimentDetail experimentdetail);

    public void setMethod(Defaults.Method m);

    public void setPlatformdata(PlatformData data);

    public void setType(Defaults.Type t);

    public void setRelease(Defaults.GenomeRelease g);

    public List<PlatformDetail> getPlatformList(String method, String type) throws Exception;

    public String getFileInfoAsHTML();

    public PlatformDetail getPlatformdetail();

    public PlatformData getPlatformdata();

    public void setPlatformdetail(PlatformDetail platformdetail);

    public void initSampleList(ExperimentDetail detail);

    public ExperimentDetail initExperimentDetail();

    public boolean validateExperimentDetail(ExperimentDetail detail);

    //public ExperimentDetail findExperimentDetail(ExperimentDetail detail);
    public ExperimentData getExperimentData();

    public ExperimentData doImportFile();

    public ExperimentData doImportFile(InformableHandler informable);

    public void setDyeSwap(boolean d);

    public int getNofChannel();

    public boolean isDyeSwap();
}
