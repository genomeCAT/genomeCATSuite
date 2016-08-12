package org.molgen.genomeCATPro.data;

import java.util.List;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
/**
 * @name INormalize
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
public interface INormalize {

    public void normalize(Data d) throws Exception;

    public void normalize(Data d, List<? extends IFeature> datalist) throws Exception;

    public String getMethodName();
}
