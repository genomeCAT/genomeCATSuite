/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.data;

import java.util.List;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 *
 * @author tebel
 */
public interface INormalize {

    public void normalize(Data d) throws Exception;

    public void normalize(Data d, List<? extends Feature> datalist) throws Exception;

    public String getMethodName() ;
}
