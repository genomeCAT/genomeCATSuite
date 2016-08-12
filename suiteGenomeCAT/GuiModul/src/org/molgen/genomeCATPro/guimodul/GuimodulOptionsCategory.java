/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.*;

public final class GuimodulOptionsCategory extends OptionsCategory {

    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage("org/molgen/genomeCATPro/guimodul/genomeCATLogo32.png"));
    }

    @Override
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
