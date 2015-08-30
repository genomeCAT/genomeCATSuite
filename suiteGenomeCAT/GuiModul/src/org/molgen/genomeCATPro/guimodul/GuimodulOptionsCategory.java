/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class GuimodulOptionsCategory extends OptionsCategory {

    @Override
    public Icon getIcon() {
        return new ImageIcon(Utilities.loadImage("org/molgen/genomeCATPro/guimodul/MPIMG_helix_4c.png"));
    }

    public String getCategoryName() {
        return NbBundle.getMessage(GuimodulOptionsCategory.class, "OptionsCategory_Name_Guimodul");
    }

    public String getTitle() {
        return NbBundle.getMessage(GuimodulOptionsCategory.class, "OptionsCategory_Title_Guimodul");
    }

    public OptionsPanelController create() {
        return new GuimodulOptionsPanelController();
    }
}
