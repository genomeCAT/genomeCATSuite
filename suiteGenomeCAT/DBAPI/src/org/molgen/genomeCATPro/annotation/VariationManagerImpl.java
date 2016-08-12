/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.annotation;

import java.awt.Color;
import java.util.Hashtable;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;

/**
 *
 * @author tebel
 */
@Deprecated

public class VariationManagerImpl extends AnnotationManagerImpl implements VariationManager {

    public VariationManagerImpl(GenomeRelease release, String name) {
        super(release, name);
    }

    public Hashtable<Integer, Color> getCnpFilter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Color getColor(Variation v) {
        return v.getColor();
    }

    public void setCnpFilter(Hashtable<Integer, Color> cnpFilter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
