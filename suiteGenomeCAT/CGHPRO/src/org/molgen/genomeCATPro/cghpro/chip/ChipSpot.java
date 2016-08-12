/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cghpro.chip;

import java.awt.Container;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.common.Univariate;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.ISpot;
import org.molgen.genomeCATPro.data.IOriginalSpot;

/**
 *
 * @author tebel
 */
public class ChipSpot extends ChipImpl implements Chip {
    // hold Chip data as original spot centred data

    public List<? extends ISpot> spots;

    public ChipSpot() throws Exception {
        super();

        this.spots = new Vector<ISpot>();

    }

    public ChipSpot(Data s) throws Exception {
        super(s);

        this.spots = new Vector<ISpot>();

    }

    public ChipSpot(ChipSpot c) {
        super(c);
        this.dataFromSpots(c.getSpots());

        //createFeaturesFromSpots(c.getSpots());
    }

    public List<? extends ISpot> getSpots() {
        return (List<? extends ISpot>) this.spots;
    }

    /**
     * impl inefficent
     */
    public List<? extends ISpot> getValidSpots() {

        if (this.spots == null || this.spots.size() == 0) {
            return null;
        }
        ISpot s = this.spots.get(0);

        List<? extends ISpot> valid = s.getVector(this.spots);

        for (ISpot spot : this.spots) {
            if (!spot.isExcluded()) {
                valid.remove(spot);
            }
        }
        return valid;
    }

    @Override
    public List<? extends Region> getData() {
        return this.getSpots();
    }

    public List<? extends Region> getData(String chromId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataFromSpots(List<? extends ISpot> spots) {
        this.spots = spots;
    }

    /**
     * store data into the database, it consists of 2 steps. Step 1, create a
     * new table with the name of chipId, delete if it alrealy existed. Step 2,
     * add a new entry to the table availabelChips
     *
     * @return indicator if it suceeds or not
     */
    /* kt new 
    public boolean insertDatabase() {
    
    String insertChip = "";
    String insertSpot = "";
    
    try {
    Connection con = Database.getDBConnection("cgh");
    Statement s = con.createStatement();
    String checkTable = "DROP TABLE IF EXISTS " + this.sample.getName();
    System.out.println(checkTable);
    s.execute(checkTable);
    String createTable = "CREATE TABLE " + this.sample.getName() + "(block int(4) NOT NULL default '0'," +
    "\ncol int(4) NOT NULL default '0'," +
    "\nrow int(4) NOT NULL default '0'," +
    "\nid varchar(255) NOT NULL default ''," +
    "\nf635Mean float NOT NULL default '0'," +
    "\nb635Mean float NOT NULL default '0'," +
    "\nf532Mean float NOT NULL default '0'," +
    "\nb532Mean float NOT NULL default '0'," +
    "\nsnr635 double NOT NULL default '0'," +
    "\nsnr532 double NOT NULL default '0'," +
    "\nf635 double NOT NULL default '0'," +
    "\nf532 double NOT NULL default '0'," +
    "\nf635Norm double NOT NULL default '0'," +
    "\nf532Norm double NOT NULL default '0'," +
    "\nnormalRatio double NOT NULL default '0'," +
    "\nratio double NOT NULL default '0'," +
    "\nsmoothRatioByMA double NOT NULL default '0'," +
    "\nsmoothRatioByCbs double NOT NULL default '0'," +
    "\nsmoothRatioByHmm double NOT NULL default '0'," +
    "\nstd double NOT NULL default '0'," +
    "\nchrom varchar(255)  default ''," +
    "\n chromStart int(10) unsigned NOT NULL default '0'," +
    "\n chromEnd int(10) unsigned NOT NULL default '0'," +
    "\n lcr double unsigned NOT NULL default '0'," +
    "\ncomment varchar(255)  default ''," +
    "\nsource varchar(255)  default ''," +
    "\n ifExcluded int(1)  NOT NULL default '0'," +
    "\n ifAberrant  int(1) NOT NULL default '0'" +
    ") TYPE=MyISAM";
    
    System.out.println(createTable);
    s.execute(createTable);
    
    for (Spot currentSpot : this.spots) {
    
    
    try {
    insertSpot = "INSERT INTO " + this.sample.getName() + " VALUES(" +
    currentSpot.block + "," +
    currentSpot.column + "," +
    currentSpot.row + "," +
    "'" + currentSpot.id + "'" + "," +
    currentSpot.f635Mean + "," +
    currentSpot.b635Mean + "," +
    currentSpot.f532Mean + "," +
    currentSpot.b532Mean + "," +
    currentSpot.snr635 + "," +
    currentSpot.snr532 + "," +
    currentSpot.f635 + "," +
    currentSpot.f532 + "," +
    currentSpot.f635Norm + "," +
    currentSpot.f532Norm + "," +
    currentSpot.normalRatio + "," +
    currentSpot.bac.ratio + "," +
    currentSpot.bac.smoothRatioByMA + "," +
    currentSpot.bac.smoothRatioByCbs + "," +
    currentSpot.bac.smoothRatioByHmm + "," +
    currentSpot.bac.std + "," +
    //    "'" + currentSpot.chrom + "'" + "," +
    //    currentSpot.chromStart + "," +
    //    currentSpot.chromEnd + "," +
    //    currentSpot.lcr + "," +
    //    "'" + currentSpot.comment + "'" + "," +
    //    "'" + currentSpot.source + "'" + "," +
    currentSpot.ifExcluded + "," +
    currentSpot.bac.ifAberrant + ")";
    
    //System.out.println(insertSpot);
    s.execute(insertSpot);
    } catch (SQLException sQLException) {
    Logger.getLogger(ChipSpot.class.getName()).log(Level.WARNING, currentSpot.id, sQLException);
    }
    }
    
    
    String deleteChip = "DELETE FROM analyzedChips where id like '" + this.sample.getName() + "'";
    s.execute(deleteChip);
    //kt new int normalizeIf = this.ifNormalize ? 1 : 0;
    //kt newint maIf = this.ifMa ? 1 : 0;
    //kt newint cbsIf = this.ifCbs ? 1 : 0;
    //kt newint hmmIf = this.ifHmm ? 1 : 0;
    //kt newint backgroundSubIf = this.ifBackgroundSub ? 1 : 0;
    
    
    insertChip = "INSERT INTO analyzedChips VALUES ( '" + this.id + "'," + this.spotNo + "," + this.badSpotNo + "," + this.bacNo + "," + this.aberrantBacNo + "," + this.excludedSpotNo + ",'" + this.phenotype + "'," + "'" + this.genotype + "'," +
    "'" + this.normalizePar + "'," + "'" + this.excludedPar + "'," + "'" + this.aberrantPar + "'," +
    "'" + this.experimenter + "'," + "'" + this.comment + "'," + "'" + this.protocal + "'," +
    "'" + this.time + "'," + normalizeIf + "," + maIf + "," + cbsIf + "," + hmmIf +
    "," + backgroundSubIf + "," + this.maWindow + "," + super.madCbs + "," + this.madHmm + ")";
    
    System.out.println(insertChip);
    s.execute(insertChip);
    return true;
    
    
    } catch (Exception e) {
    Logger.getLogger(ChipSpot.class.getName()).log(Level.WARNING, "", e);
    return false;
    }
    }
     */
    /**
     * public int exportIntensity(File outFile) { try {
     *
     * int i = 0; int chrom = 0; FileWriter out = new FileWriter(outFile);
     * out.write("ID\tClone\tTarget\tChrom\tstart\tend\tf532\tf635\tlog2Ratio\n");
     * Collection<Vector<? extends Feature>> all = this.chrBacs.values();
     *
     * for (Vector<FeatureWithSpot> bacs : all) {
     * Collections.sort((Vector<Feature>) bacs, Feature.comChromStart); for
     * (FeatureWithSpot currentBac : bacs) {
     *
     * if (currentBac.chrom.contains("andom")) { continue; }
     *
     * float f532 = currentBac.spots.get(0).get; float f635 =
     * currentBac.spots.get(0).f635Mean; chrom =
     * Utils.fromChrToInt(currentBac.chrom); out.write("" + ++i + "\t" +
     * currentBac.id + "\t" + i + "\t" + chrom + "\t" + currentBac.chromStart +
     * "\t" + currentBac.chromEnd + "\t" + +f532 + "\t" + f635 + "\t" +
     * currentBac.ratio + "\n");
     *
     *
     * }
     * }
     *
     * out.close(); return i; } catch (IOException exception) {
     *
     * System.err.println("" + exception); return -1; }
     *
     */
    /**
     * get the original ratios of spots
     *
     * @param includeExcluded - true: include excluded, false: no excluded
     * @return a Univariate holding the ratios of all spots
     */
    public Univariate getSpotLog2Ratios(boolean includeExcluded) {
        List<? extends ISpot> _spots;

        if (includeExcluded) {
            _spots = this.getSpots();
        } else {
            _spots = this.getValidSpots();
        }
        if (_spots != null && _spots.size() > 0) {

            try {
                double[] data = new double[_spots.size()];
                int i = 0;
                for (ISpot currentSpot : _spots) {
                    if (!currentSpot.isExcluded()) {
                        data[i] = currentSpot.getRatio();
                        i++;
                    }
                }
                return (new Univariate(data));
            } catch (Exception e) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.WARNING, "", e);
            }
        }
        return null;
    }

    /**
     * get the sum of original signal intensity of 2 channels for all active
     * spots, used for MAplot
     *
     * @param includeExcluded - true: include excluded, false: no excluded
     * (default)
     * @see maPlot()
     * @return a Univariate holding the sums of the active spots
     *
     */
    public Univariate getSpotF635PlusF532(boolean includeExcluded) {
        List<IOriginalSpot> _spots;

        if (includeExcluded) {
            _spots = (List<IOriginalSpot>) this.getSpots();
        } else {
            _spots = (List<IOriginalSpot>) this.getValidSpots();
        }
        if (_spots != null && _spots.size() > 0) {
            try {
                double[] data = new double[_spots.size()];
                int i = 0;
                for (IOriginalSpot currentSpot : _spots) {
                    if (!currentSpot.isExcluded()) {
                        data[i] = (currentSpot.getCy3Value() + currentSpot.getCy5Value());
                        i++;
                    }
                }
                return (new Univariate(data));
            } catch (Exception e) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO, "", e);
            }
        }
        return null;
    }

    /**
     * get the original signal intensity at 532 for all active spots
     *
     * @param includeExcluded - true: include excluded, false: no excluded
     * (default)
     * @return a Univariate holding the signal intensity of the active spots
     *
     */
    public Univariate getSpotF532(boolean includeExcluded) {
        List<IOriginalSpot> _spots;

        if (includeExcluded) {
            _spots = (List<IOriginalSpot>) this.getSpots();
        } else {
            _spots = (List<IOriginalSpot>) this.getValidSpots();
        }
        if (_spots != null && _spots.size() > 0) {
            try {
                double[] data = new double[_spots.size()];
                int i = 0;
                for (IOriginalSpot currentSpot : _spots) {
                    if (!currentSpot.isExcluded()) {
                        data[i] = (currentSpot.getCy3Value());
                        i++;
                    }
                }
                return (new Univariate(data));
            } catch (Exception e) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO, "", e);
            }
        }
        return null;
    }

    /**
     * get the original signal intensity at 635 for all active spots
     *
     * @param includeExcluded - true: include excluded, false: no excluded
     * (default)
     * @return a Univariate holding the signal intensity of the active spots
     *
     */
    public Univariate getSpotF635(boolean includeExcluded) {
        List<IOriginalSpot> _spots;

        if (includeExcluded) {
            _spots = (List<IOriginalSpot>) this.getSpots();
        } else {
            _spots = (List<IOriginalSpot>) this.getValidSpots();
        }
        if (_spots != null && _spots.size() > 0) {
            try {
                double[] data = new double[_spots.size()];
                int i = 0;
                for (IOriginalSpot currentSpot : _spots) {
                    if (!currentSpot.isExcluded()) {
                        data[i] = (currentSpot.getCy5Value());
                        i++;
                    }
                }
                return (new Univariate(data));
            } catch (Exception e) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO, "", e);
            }
        }
        return null;
    }

    /**
     * get the log2 signal intensity at 532 for all active spots
     *
     * @param includeExcluded - true: include excluded, false: no excluded
     * (default)
     * @return a Univariate holding the signal intensity of the active spots
     *
     */
    public Univariate getSpotLog2Cy3(boolean includeExcluded) {
        List<IOriginalSpot> _spots;

        if (includeExcluded) {
            _spots = (List<IOriginalSpot>) this.getSpots();
        } else {
            _spots = (List<IOriginalSpot>) this.getValidSpots();
        }
        if (_spots != null && _spots.size() > 0) {
            try {
                double[] data = new double[_spots.size()];
                int i = 0;
                for (IOriginalSpot currentSpot : _spots) {
                    if (!currentSpot.isExcluded()) {
                        data[i] = currentSpot.getLog2Cy3();
                        i++;
                    }
                }
                return (new Univariate(data));
            } catch (Exception e) {
                Logger.getLogger(ChipImpl.class.getName()).log(Level.INFO, "", e);
            }
        }
        return null;
    }

    /**
     * get the normalized signal intensity at 635 for all active spots
     *
     * @return a Univariate holding the signal intensity of the active spots
     *
     */
    public Univariate getSpotLog2Cy5(boolean includeExcluded) {
        List<IOriginalSpot> _spots;

        if (includeExcluded) {
            _spots = (List<IOriginalSpot>) this.getSpots();
        } else {
            _spots = (List<IOriginalSpot>) this.getValidSpots();
        }
        if (_spots != null && _spots.size() > 0) {
            try {
                double[] data = new double[_spots.size()];
                int i = 0;
                for (IOriginalSpot currentSpot : _spots) {
                    if (!currentSpot.isExcluded()) {
                        data[i] = currentSpot.getLog2Cy5();
                        i++;
                    }
                }
                return (new Univariate(data));
            } catch (Exception e) {
                Logger.getLogger(ChipSpot.class.getName()).log(Level.INFO, "", e);
            }
        }
        return null;
    }

    public double getMedianLog2Ratio(List<? extends Object> spots) {
        List<? extends ISpot> _spots = (List<? extends ISpot>) spots;
        Collections.sort(_spots, ISpot.comLog2Ratio);
        double median = 0.0;
        if ((_spots.size() % 2) == 0) {
            ISpot left = _spots.get(_spots.size() / 2 - 1);
            ISpot right = _spots.get(_spots.size() / 2);
            median = 0.5 * (left.getRatio() + right.getRatio());
        } else {
            ISpot spotmedian = _spots.get((_spots.size() - 1) / 2);
            median = spotmedian.getRatio();
        }
        return median;
    }

    /**
     * Normalize the log2 ratios by global median
     *
     */
    /*
    public List<? extends Spot> normalizeByGlobalMedian(boolean includeExcluded) {
        List<? extends Spot> _spots;

        if (includeExcluded) {
            _spots = this.getSpots();
        } else {
            _spots = this.getValidSpots();
        }

        if (_spots == null || _spots.size() == 0) {
            return null;
        }
        double normalValue = this.getMedianLog2Ratio(_spots);

        Spot s = _spots.get(0);
        List<? extends Spot> _normalized = s.getVector(_spots);

        // normalize Spots
        for (Spot currentSpot : _normalized) {
            currentSpot.scaleByFactor(normalValue);
        }
        return _normalized;
    // kt new super super.setIfNormalize(true);
    //chipBac.recalculate

    }*/
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
            this.spots = (List<ISpot>) list;
        } else {
            this.error = true;
        }
    }
}
