/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.data;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

public class ZoomYAction extends AbstractAction implements
        LookupListener, ContextAwareAction, Presenter.Toolbar {

    private Lookup context;
    Lookup.Result<AppInterface> lkpInfo;
    private static ZoomYAction instance = null;

    @ActionID(id = "org.molgen.genomeCATPro.guimodul.data.ZoomYAction", category = "DATA")
    @ActionRegistration(displayName = "", lazy = false)
    @ActionReference(path = "Toolbars/Zoom")
    public static ZoomYAction getInstance() {
        if (ZoomYAction.instance == null) {
            instance = new ZoomYAction();
        }
        return instance;
    }

    public static Action getInstance(Map<String, ?> attributes) {
        return ZoomYAction.getInstance();
    }

    public ZoomYAction() {
        this.setLookup(Utilities.actionsGlobalContext());

    }

    void setLookup(Lookup lookup) {
        this.context = lookup;
    }

    // open with certain lookup context
    private ZoomYAction(Lookup context) {
        super("zoomY");
        this.context = context;
    }
    private static int factor = 0;
    public static int maxFactor = 3;
    public static int minFactor = 0;

    public void doZoom(int i) {

        boolean success = false;

        if (factor + i > maxFactor) {
            return;
        }
        if (factor + i < minFactor) {
            return;
        }
        init();
        for (AppInterface _instance : lkpInfo.allInstances()) {
            System.out.println("Zoom lookup: " + _instance.getClass().getName());
            success = _instance.doZoomY(i);

        }
        if (success) {
            this.setFactor(factor += i);
            //this.fieldFactor.setText(Double.toString(java.lang.Math.pow(2, factor)));
        }
    }
    JTextField fieldFactor;

    @Override
    public Component getToolbarPresenter() {
        JPanel panel = new JPanel();
        // panel.setBorder(javax.swing.BorderFactory.createTitledBorder(
        //          null, "zoom y",
        //         javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        panel.setOpaque(false);

        panel.setLayout(new java.awt.GridLayout(1, 3, 1, 1));
        panel.setPreferredSize(new java.awt.Dimension(100, 20));
        JButton zoomInButton = new javax.swing.JButton();
        zoomInButton.setIcon(
                new javax.swing.ImageIcon(Utilities.loadImage(
                        "org/molgen/genomeCATPro/guimodul/data/lupe_plus_16.png")));
        zoomInButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doZoom(+1);
            }
        });
        zoomInButton.setMaximumSize(new java.awt.Dimension(10, 20));
        JButton zoomOutButton = new javax.swing.JButton();
        zoomOutButton.setIcon(new javax.swing.ImageIcon(Utilities.loadImage(
                "org/molgen/genomeCATPro/guimodul/data/lupe_minus_16.png")));
        zoomOutButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doZoom(-1);
            }
        });
        zoomInButton.setMaximumSize(new java.awt.Dimension(10, 20));
        fieldFactor = new JTextField();
        panel.add(zoomOutButton);
        panel.add(fieldFactor);
        panel.add(zoomInButton);
        //startButton.setMaximumSize(startButton.getPreferredSize());
        //startButton.setAction(this);
        this.setFactor(ZoomYAction.factor);
        return panel;
    }

    public void setFactor(int _factor) {
        ZoomYAction.factor = _factor;
        if (this.fieldFactor != null) {
            this.fieldFactor.setText(Double.toString(java.lang.Math.pow(2, factor)));
        }
    }

    void init() {

        if (lkpInfo != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(AppInterface.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    public Action createContextAwareInstance(Lookup context) {
        ((ZoomYAction) ZoomYAction.getInstance()).setLookup(context);
        return instance;
    }
}
