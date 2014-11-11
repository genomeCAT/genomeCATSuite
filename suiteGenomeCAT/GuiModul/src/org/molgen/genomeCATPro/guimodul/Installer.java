package org.molgen.genomeCATPro.guimodul;
/**
 * @name Installer
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
import org.molgen.dblib.DBService;

import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod.CoreProperties;
import org.molgen.genomeCATPro.common.Defaults;
import org.openide.awt.ToolbarPool;
import org.openide.modules.ModuleInstall;


/**
 * initialize database, Toolbar
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        CoreProperties core = CorePropertiesMod.props();
        Database.setDBParams(
                Defaults.localDB,
                core.getDb(),
                core.getHost(),
                core.getPort(),
                core.getUser(),
                core.getPwd());

        DBService.setConnection(
                core.getHost(),
                core.getPort(),
                core.getDb(),
                core.getUser(),
                core.getPwd());
        ToolbarPool.getDefault().setConfiguration("Standard");
       
        
}

    
}
