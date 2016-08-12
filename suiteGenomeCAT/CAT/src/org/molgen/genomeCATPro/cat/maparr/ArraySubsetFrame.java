/**
 * * @(#)ExtractAberrationView.java * * Copyright (c) 2007 by Katrin Tebel
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License
 * * as published by the Free Software Foundation; either version 2
 * * of the License, or (at your option) any later version,
 * * provided that any use properly credits the author.
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * *
 */
package org.molgen.genomeCATPro.cat.maparr;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Defaults.GenomeRelease;

public class ArraySubsetFrame extends ArrayFrame {

    public ChromTab chromtab;
    int begin;
    int end;
    String chrom;

    /**
     *
     * @param chrom
     * @param begin
     * @param end
     */
    @Deprecated
    public ArraySubsetFrame(String chrom, int begin, int end, GenomeRelease release) {
        super();

        Logger.getLogger(ArraySubsetFrame.class.getName()).log(
                Level.INFO, "new ArraySubSetFrame for " + chrom + " [" + begin + "-" + end + "]");
        this.begin = begin;
        this.end = end;
        this.chrom = chrom;
        this.setRelease(release);
        this.chromtab = ChromTab.createSingleChromTab(chrom, this);
        this.vChromTabs.add(chromtab);
        super.initChromPane();
        //setTitle("View Subset Array for " + chrom + " [" + begin + "-" + end + "]");

    }

    /**
     * create Menu Bar for subview of arrays
     *
     * @see ArrayFrame
     * @return MenuBar
     */
    /*protected  createMenu() {
        //Where the GUI is created:

        JMenuBar _menuBar = new JMenuBar();
        
        JMenu menuMap = new JMenu("Mapping");
        menuBar.add(menuMap);
        
        
        JMenuItem menuMapOpen = new JMenuItem("map open arrays");
        menuMapOpen.addActionListener(new MapArrays());
        menuMap.add(menuMapOpen);
         
        JMenu menuCalc = new JMenu("Calculate");
        _menuBar.add(menuCalc);




        return _menuBar;
    }*/
}
