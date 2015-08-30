package org.molgen.genomeCATPro.datadb.dbentities;

/**
 * @name  Data.java
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
import java.io.Serializable;
import org.molgen.genomeCATPro.common.Defaults.DataType;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;

/**
 *
 *
 */
public interface Data extends Serializable, DataNode {

    String getClazz();

    String getDataType();

    String getGenomeRelease();

    Double getMaxRatio();

    Double getMean();

    Double getMedian();

    Double getMinRatio();

    Data getParent();

    Double getStddev();

    String getTableData();

    Double getVariance();

    StringBuffer getMetaText();

    void setClazz(String clazz);

    void setDataType(DataType RAW);

    void setGenomeRelease(GenomeRelease genomeRelease);

    void setParent(Data o);

    void setProcProcessing(String txt);

    void setParamProcessing(String txt);

    String getProcProcessing();

    String getParamProcessing();

    void setMaxRatio(Double maxRatio);

    void setMean(Double mean);

    void setMedian(Double median);

    void setMinRatio(Double minRatio);

    void setStddev(Double stddev);

    void setVariance(Double var);

    void setNof(Integer i);

    Integer getNof();

    void copy(Data d);

    boolean allowSegmentation();
}
