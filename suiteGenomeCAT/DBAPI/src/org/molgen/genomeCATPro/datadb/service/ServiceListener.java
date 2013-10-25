/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.molgen.genomeCATPro.datadb.service;

import java.util.EventListener;

/**
 *
 * @author tebel
 */
public interface ServiceListener extends EventListener {
    public void dbChanged();

}
