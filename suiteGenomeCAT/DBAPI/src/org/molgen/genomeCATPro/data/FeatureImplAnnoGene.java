/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.molgen.genomeCATPro.data;

import org.molgen.genomeCATPro.common.Defaults;

/**
 *
 * @author tebel
 */
public class FeatureImplAnnoGene extends FeatureImplAnno{
    @Override
    public String getGeneColName() {
        return Defaults.annoColName;
    }

    @Override
    public boolean hasGeneView() {
        return true;
    }


}
