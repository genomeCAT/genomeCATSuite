/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul;

import org.molgen.dblib.DBService;

import org.molgen.dblib.Database;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod.CoreProperties;
import org.molgen.genomeCATPro.common.Defaults;
import org.openide.awt.ToolbarPool;
import org.openide.modules.ModuleInstall;


/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
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

        //JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
      


        //frame.setTitle(title);
       
        
}

    
}
