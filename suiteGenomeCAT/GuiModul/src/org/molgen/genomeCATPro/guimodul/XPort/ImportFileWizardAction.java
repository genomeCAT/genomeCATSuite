/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.XPort;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Toolkit;
import java.text.MessageFormat;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.molgen.genomeCATPro.datadb.dbentities.ExperimentDetail;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Utilities;
import org.openide.util.actions.CallableSystemAction;

public final class ImportFileWizardAction extends CallableSystemAction {

    /* public void actionPerformed(ActionEvent e) {
    WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
    // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
    wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
    wizardDescriptor.setTitle("Import new File");
    Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
    dialog.setVisible(true);
    dialog.toFront();
    boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
    if (!cancelled) {
    // do something
    }
    }
     */
    public static ExperimentDetail doImport() {
        @SuppressWarnings("unchecked")
        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("create Experiment");
        wizardDescriptor.putProperty("WizardPanel_image",
                Toolkit.getDefaultToolkit().getImage("org/molgen/genomeCATPro/guimodul/XPort/folder_add_16.png"));

        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.setModalityType(ModalityType.MODELESS);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            return (ExperimentDetail) wizardDescriptor.getProperty("experiment");
        } else {
            return null;
        }
    }

    public void performAction() {
        doImport();
    }

    @Override
    protected String iconResource() {
        return "org/molgen/genomeCATPro/guimodul/XPort/folder_add_16.png";
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    static private WizardDescriptor.Panel[] getPanels() {

        WizardDescriptor.Panel[] panels = new WizardDescriptor.Panel[]{
            new ImportFileWizardPanel1(),
            new ImportFileWizardPanel3(),
            new ImportFileWizardPanel2(),
            new ImportFileWizardPanel4(),
            new ImportFileWizardPanel5()
        };
        String[] steps = new String[panels.length];
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            // Default step name to component name of panel. Mainly useful
            // for getting the name of the target chooser to appear in the
            // list of steps.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components

                JComponent jc = (JComponent) c;
                // Sets step number of a component
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Sets steps names for a panel
                jc.putClientProperty("WizardPanel_contentData", steps);
                // Turn on subtitle creation on each step
                jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                // Show steps on the left side with the image on the background
                jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                // Turn on numbering of all steps
                jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);

                jc.putClientProperty("WizardPanel_image",
                        new ImageIcon(Utilities.loadImage("org/molgen/genomeCATPro/guimodul/XPort/folder_add_16.png")));

            }
        }

        return panels;
    }

    @Override
    public String getName() {
        return "import Experiment";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}


