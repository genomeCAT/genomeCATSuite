package org.molgen.genomeCATPro.guimodul.XPort;

/**
 * @name ImportExperimentBatch
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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.cghpro.xport.ServiceXPort;
import org.molgen.genomeCATPro.cghpro.xport.XPortExperimentFile;
import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentData;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleDetail;
import org.molgen.genomeCATPro.datadb.dbentities.SampleInExperiment;
import org.molgen.genomeCATPro.datadb.service.ExperimentService;
import org.molgen.genomeCATPro.datadb.service.PlatformService;

/**
 *
 * @author tebel
 */
public class ImportExperimentBatch {

    public static ExperimentData importExperiment(
            String filetype,
            String filename,
            List<String[]> map,
            String platformname,
            String release,
            String type, String method,
            String sampleCy3,
            String sampleCy5) {
        XPortExperimentFile importModul = null;
        PlatformDetail detail = null;
        PlatformData data = null;
        ExperimentDetail experimentdetail = null;
        ExperimentData experimentdata = null;
        try {
            importModul = ServiceXPort.getXPortImport(filetype);
            importModul.newImportFile(filename);
            Vector<Vector<String>> datafile = importModul.readData(100);
            List<String[]> mapping = importModul.getMappingFile2DBColNames();
            if (map == null) {
                map = importModul.getMappingFile2DBColNames();
            }
            String msg = importModul.setMappingFile2DBColNames(mapping);
            if (msg != null) {
                Logger.getLogger(ImportExperimentBatch.class.getName()).log(
                        Level.WARNING, msg);
            }
            detail = PlatformService.getPlatformDetailByName(platformname);
            if (detail == null) {
                Logger.getLogger(ImportExperimentBatch.class.getName()).log(
                        Level.SEVERE, " no platform detail found: " + platformname);
                return null;
            }
            data = PlatformService.getPlatformForRelease(detail.getPlatformID(), release);
            if (data == null) {
                Logger.getLogger(ImportExperimentBatch.class.getName()).log(Level.SEVERE, 
                        " no platform data found: {0} {1}", new String[]{platformname, release});
                return null;
            }
            importModul.setPlatformdetail(detail);
            importModul.setPlatformdata(data);
            experimentdetail = importModul.initExperimentDetail();
            experimentdetail.setName(Utils.getUniquableName(experimentdetail.getName()));
            if (method != null) {
                experimentdetail.setMethod(method);
            }
            if (type != null) {
                experimentdetail.setType(type);
            }
            importModul.initSampleList(experimentdetail);
            SampleInExperiment s1 = experimentdetail.getSamples().get(0);
            SampleInExperiment s2 = experimentdetail.getSamples().get(1);
            if (sampleCy3 != null) {

                if (s1 != null) {
                    experimentdetail.removeSample(s1);
                }
                SampleDetail sdCy3 = ExperimentService.getSampleDetailByName(sampleCy3);
                if (sdCy3 == null) {
                    sdCy3 = new SampleDetail();
                    sdCy3.setName(sampleCy3);
                }
                SampleInExperiment sieCy3 = experimentdetail.addSample(sdCy3, true, false);
            }
            if (sampleCy5 != null) {

                if (s2 != null) {
                    experimentdetail.removeSample(s2);
                }
                SampleDetail sdCy5 = ExperimentService.getSampleDetailByName(sampleCy5);
                if (sdCy5 == null) {
                    sdCy5 = new SampleDetail();
                    sdCy5.setName(sampleCy5);
                }
                SampleInExperiment sieCy5 = experimentdetail.addSample(sdCy5, false, true);
            }
            importModul.setExperimentDetail(experimentdetail);

            experimentdata = importModul.getExperimentData();

            InformableHandler informable = new InformableHandler() {

                @SuppressWarnings("empty-statement")
                public void messageChanged(String message) {
                    ;
                }
            };
            experimentdata = importModul.doImportFile(informable);

            experimentdata.setNofImportErrors(importModul.getError());
            experimentdata.setNofImportData(importModul.getNoimp());
            return experimentdata;

        } catch (Exception ex) {
            Logger.getLogger(ImportExperimentBatch.class.getName()).log(
                    Level.SEVERE, "", ex);
            return null;
        }

    }
}
