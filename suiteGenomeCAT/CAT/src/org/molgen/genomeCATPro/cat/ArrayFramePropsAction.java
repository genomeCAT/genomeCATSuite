package org.molgen.genomeCATPro.cat;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows CGHPROFrameProps component.
 */
public class ArrayFramePropsAction extends AbstractAction {

    public ArrayFramePropsAction() {
        super(NbBundle.getMessage(ArrayFramePropsAction.class, "CTL_ArrayFramePropsAction"));
        java.net.URL imgURL = getClass().getResource("tools_16.png");
        ImageIcon newIcon = new ImageIcon(imgURL);
        putValue(SMALL_ICON, newIcon);
    }

    public void actionPerformed(ActionEvent evt) {

        TopComponent win = ArrayFramePropsTopComponent.findInstance();
        win.open();
        win.requestActive();


    }
}
