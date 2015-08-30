/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.datadb.dbentities;

import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 *
 * @author tebel
 */
public interface DataNode {

    String ADD = "Add";
    String REMOVE = "Remove";

    void addChildData(Data d);

    void addPropertyChangeListener(PropertyChangeListener listener);

    Date getCreated();

    String getDescription();

    String getIconPath();

    Long getId();

    Date getModified();

    String getName();

    User getOwner();

    void removeChildData(Data d);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void setId(Long id);

    void setName(String name);

    void setOwner(User o);

    String toFullString();
}
