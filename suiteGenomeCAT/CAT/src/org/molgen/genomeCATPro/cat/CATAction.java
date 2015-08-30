/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cat;


import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows CAT component.
 */
public class CATAction extends AbstractAction {

    public CATAction() {
        super(NbBundle.getMessage(CATAction.class, "CTL_CATAction"));
        setIcon(); 
    }
     void setIcon() {
       putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(CATTopComponent.ICON_PATH, true)));
    }


    public void actionPerformed(ActionEvent evt) {
        TopComponent win = CATTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
