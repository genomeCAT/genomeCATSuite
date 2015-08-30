package org.molgen.genomeCATPro.cghpro.xport;

import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;

/**
 * @name XPortPlatform
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
public interface XPortPlatform extends XPortImport {

    public final static String placeholder_genomePosition = "genomePosition";

    PlatformData doImportPlatform(PlatformDetail detail, PlatformData data);

    public PlatformData doImportPlatform(PlatformDetail platformdetail,
            PlatformData platform, InformableHandler ifh);

    public void newImportPlatform(String filename) throws Exception;

    public PlatformDetail getPlatformDetail();

    public PlatformData getPlatformData(PlatformDetail d);
    
    
}
