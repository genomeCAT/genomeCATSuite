/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.guimodul.data;

/**
 *
 * @author tebel
 */
public interface AppInterface {

    public void exportImage();

    public int getZoomX();

    public boolean doZoomX(int d);

    public boolean doZoomY(int d);

    public int getZoomY();

    public void load();

    public void showData(boolean show);

    public void showRuler(boolean show);
}
