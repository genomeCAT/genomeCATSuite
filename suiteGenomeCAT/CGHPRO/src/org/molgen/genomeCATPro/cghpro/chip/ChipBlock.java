/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cghpro.chip;

import java.awt.Container;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.ISpot;
import org.molgen.genomeCATPro.data.IOriginalSpot;

/**
 *
 * @author tebel class manages chip information blockwise
 */
public class ChipBlock extends ChipImpl implements Chip {

    /**
     * vector holding all the blocks
     */
    public Hashtable<Integer, Vector<ISpot>> blocks;

    public ChipBlock(Data s) throws Exception {
        super(s);

        this.blocks = new Hashtable<>();

    }

    public ChipBlock(ChipSpot c) {
        super(c);
        this.dataFromSpots(c.getSpots());

        //createFeaturesFromSpots(c.getSpots());
    }

    public void addBlockSpot(IOriginalSpot spot) {
        if (!this.blocks.containsKey(spot.getBlock())) {
            this.addBlock(spot.getBlock());
        }
        this.blocks.get(spot.getBlock()).add(spot);
    }

    public void addBlock(Integer id) {
        this.blocks.put(id, new Vector<>());
    }

    public void setBlock(Integer id, List<? extends ISpot> list) {
        this.blocks.put(id, new Vector<ISpot>());
        this.blocks.get(id).addAll(list);
    }

    /**
     * Normalize the all blocks by subgrid median todo: ? richtiger SpotTyp??
     *
     */
    /*
    public Hashtable<Integer, Vector<Spot>> normalizeBySubGridMedian(boolean includeExcluded) {
        Hashtable<Integer, Vector<Spot>> newBlocks = new Hashtable<Integer, Vector<Spot>>();

        for (Integer block : this.blocks.keySet()) {
            newBlocks.put(block, new Vector<Spot>());
            Collections.copy(newBlocks.get(block), this.blocks.get(block));
            double normalValue = this.getMedianLog2Ratio(newBlocks.get(block));
            for (Spot currentSpot : newBlocks.get(block)) {

                currentSpot.scaleByFactor(normalValue);

            }
        }
        return newBlocks;
    }
     */
    @Override
    public List<? extends Region> getData() {
        if (this.blocks == null || this.blocks.size() <= 0) {
            return Collections.emptyList();
        }
        List<ISpot> fList = new Vector<>();
        fList.clear();
        for (List<ISpot> list : this.blocks.values()) {
            fList.addAll(list);
        }
        return fList;
    }

    public List<? extends Region> getData(String chromId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataFromSpots(List<? extends ISpot> spots) {
        if (spots == null || spots.size() == 0) {
            Logger.getLogger(ChipBlock.class.getName()).log(Level.INFO, "empty spots");
            this.error = true;
        }

        for (ISpot s : spots) {
            if (s.isExcluded()) {
                continue;
            }
            if (s instanceof IOriginalSpot) {
                this.addBlockSpot((IOriginalSpot) s);
            }
        }
    }

    @Override
    public double getMedianLog2Ratio(List<? extends Object> data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void histogram(Container p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void scatterPlot(Container p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void boxPlot(Container p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void normalProbabilityPlot(Container p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void maPlot(Container p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setData(List<? extends Region> list) {

        if (list == null || list.size() == 0) {
            return;
        }
        if (list.get(0) instanceof ISpot) {
            this.dataFromSpots((List<? extends ISpot>) list);
        } else {
            this.error = true;
        }

    }
}
