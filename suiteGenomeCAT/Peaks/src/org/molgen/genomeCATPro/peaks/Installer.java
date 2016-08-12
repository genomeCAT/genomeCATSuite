/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.peaks;

import org.molgen.genomeCATPro.guimodul.tree.ExperimentDataNode;
import org.molgen.genomeCATPro.guimodul.tree.TrackNode;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        ExperimentDataNode.addCalcAction(new ExtractPeaksAction());
        TrackNode.addActionCalculate(new ExtractPeaksAction());
    }
}
