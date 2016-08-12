package org.molgen.genomeCATPro.cghpro.chip;

/**
 * @name ChipFeature
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * @author Wei Chen
 *
 * This file is part of the CGHPRO software package. Copyright Jan 19, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import org.molgen.genomeCATPro.data.FeatureWithSpot;
import java.awt.Container;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.annotation.Region;
import org.molgen.genomeCATPro.common.MyMath;
import org.molgen.genomeCATPro.common.Univariate;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.data.DataService;
import org.molgen.genomeCATPro.data.FeatureWithSpotAnno;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.data.IFeature;
import org.molgen.genomeCATPro.data.ISpot;

/**
 * 100812 kt decide to create normal feature or anno feature
 */
public class ChipFeature extends ChipImpl implements Chip {

    public Hashtable<String, Vector<? extends IFeature>> chrFeatures;

    public ChipFeature(Data s) throws Exception {
        super(s);
        this.chrFeatures = new Hashtable<String, Vector<? extends IFeature>>();
    }

    /**
     * Constructor used for error handling
     */
    public ChipFeature(boolean error) {
        super(error);
    }

    public ChipFeature() {
        super();
        this.chrFeatures = new Hashtable<String, Vector<? extends IFeature>>();
    }

    /**
     * copy of chip (should do deep copy)
     *
     * @param f
     * @param datatype
     * @throws java.lang.Exception
     */
    public ChipFeature(ChipFeature f, String datatype) throws Exception {
        super(f.getDataEntity());
        this.getDataEntity().setDataType(Defaults.DataType.toDataType(datatype));

        this.chrFeatures = f.copyFeatureVector();
        Logger.getLogger(ChipFeature.class.getName()).log(Level.INFO,
                "New ChipFeature by clone "
                + this.getDataEntity().getName() + " "
                + this.getDataEntity().getGenomeRelease() + " " + this.getDataEntity().getDataType());

    }

    public Double getMAD() {
        @SuppressWarnings("unchecked")
        List<IFeature> data = (List<IFeature>) this.getData();

        Double median = this.getMedianLog2Ratio(data);
        double[] div = new double[data.size()];
        for (int i = 0; i < div.length; i++) {
            div[i] = Math.abs(data.get(i).getRatio() - median);
        }

        return MyMath.median(div);

    }

    /**
     * get the ratio of BACs
     *
     * @return a Univariate holding the ratio of BACs
     *
     */
    public Univariate getFeatureRatio() {
        int i = 0;
        double[] ratioData = new double[this.getFeaturesSize()];
        for (Vector<? extends IFeature> bacs : this.chrFeatures.values()) {
            for (IFeature currentBac : bacs) {
                ratioData[i++] = currentBac.getRatio();
            }

        }
        return (new Univariate(ratioData));
    }

    public void setError(boolean b) {
        this.error = b;
    }

    /*
    void loadChipDataFromDB(XPort xport) {
    
    // if s type raw??
    try {
    Chip chip = xport.loadChipFromDB(this.sample.getTableData(), this.sample.getClazz());
    } catch (Exception e) {
    this.error = true;
    Logger.getLogger(ChipFeature.class.getName()).log(Level.SEVERE, "ERROR: ", e);
    }
    
    }
     */
    /**
     * recognize the replicate spots by the unique ID, average the ratio between
     * the replicates create a list of unique BACs for the active spots called
     * from normalization methods
     *
     * @see Bac
     *
     */
    // idealerweise dynamisch variabel ob Genename, bac id etc
    public void dataFromSpots(List<? extends ISpot> spots) {
        if (spots == null || spots.size() == 0) {
            Logger.getLogger(ChipFeature.class.getName()).log(Level.INFO, "empty spots");
            this.error = true;
        }
        Hashtable<String, FeatureWithSpot> all = new Hashtable<String, FeatureWithSpot>();

        boolean checked = false;
        boolean anno = false;
        for (ISpot s : spots) {

            if (s.isExcluded()) {
                continue;
            }
            // 100812 kt decide to create normal feature or anno feature
            if (!checked) {
                anno = DataService.hasValue(s, "getAnnoValue");
                checked = true;
            }
            if (!all.containsKey(s.getId())) {
                if (anno) {
                    all.put(s.getId(), new FeatureWithSpotAnno(s.getId()));
                } else {
                    all.put(s.getId(), new FeatureWithSpot(s.getId()));
                }
            }
            all.get(s.getId()).addSpot(s);
        }

        this.chrFeatures.clear();
        for (FeatureWithSpot f : all.values()) {
            this.addFeatureWithSpot(f);
        }
    }

    /*
     * recalculate the ratios
     * no change in mapping spot -> bac!!
     */
    void recalculateRatios() {
        for (Vector<? extends IFeature> bacs : this.chrFeatures.values()) {
            for (IFeature bac : bacs) {
                if (bac instanceof FeatureWithSpot) {
                    ((FeatureWithSpot) bac).calculateRatio();
                }

            }
        }
    }

    public int getFeaturesSize() {
        int size = 0;
        Collection<Vector<? extends IFeature>> c = this.chrFeatures.values();
        for (Vector<? extends IFeature> v : c) {
            size += v.size();
        }
        return size;
    }

//kt new
// todo get clone position
// todo FeatureWithSpots??
    @SuppressWarnings("unchecked")
    public void addFeature(IFeature f) {
        if (!this.chrFeatures.containsKey(f.getChrom())) {

            this.chrFeatures.put(f.getChrom(), new Vector<IFeature>());
            //int chr = Utils.fromChrToInt(newBac.chrom)-1;
        }

        ((Vector<IFeature>) this.chrFeatures.get(f.getChrom())).add(f);

    }

    @SuppressWarnings("unchecked")
    public void addFeatureWithSpot(FeatureWithSpot f) {
        if (!this.chrFeatures.containsKey(f.getChrom())) {

            this.chrFeatures.put(f.getChrom(), new Vector<FeatureWithSpot>());
            //int chr = Utils.fromChrToInt(newBac.chrom)-1;
        }

        ((Vector<FeatureWithSpot>) this.chrFeatures.get(f.chrom)).add(f);

    }

    /**
     * /**get the bacSum of the chip
     */
    /**
     * public BacsSummary getBacsSummary() {
     * Collection<Vector<? extends Feature>> bacs = this.chrFeatures.values();
     * Vector<? extends Feature> allbacs = new Vector<Feature>();
     * allbacs.ensureCapacity(this.getFeaturesSize() / 10);
     *
     * for (Vector<? extends Feature> v : bacs) { allbacs.addAll( v); } return
     * new BacsSummary(allbacs);
     *
     * }
     *
     * @return
     *
     */
    public Hashtable<String, Vector<? extends IFeature>> copyFeatureVector() {
        Hashtable<String, Vector<? extends IFeature>> newBacs = (Hashtable<String, Vector<? extends IFeature>>) this.chrFeatures.clone();
        Vector<? extends IFeature> v = null;
        IFeature f = null;
        for (String chrom : this.chrFeatures.keySet()) {
            if (this.chrFeatures.get(chrom) != null && this.chrFeatures.get(chrom).size() > 0) {
                f = this.chrFeatures.get(chrom).get(0);
                v = this.chrFeatures.get(chrom);
                v = (Vector<? extends IFeature>) v.clone();
                this.chrFeatures.put(chrom, v);
            }
        }
        return newBacs;
    }

    /**
     * Smooth the ratios of BACs ordered along the chromosome by Moving Average
     *
     * @param maWindow, the window size used for Moving Average
     *
     */
    public Hashtable<String, Vector<? extends IFeature>> smoothByMovingAverage(int maWindow) {
        // kt new super.setMaWindow(maWindow);
        int smooth = (maWindow - 1) / 2;
        Hashtable<String, Vector<? extends IFeature>> newBacs = this.copyFeatureVector();

        // copy
        for (String chrom : newBacs.keySet()) {
            // if the number of BACs on a chromosome is less than the window size, ignore the smoothing
            if (newBacs.get(chrom).size() <= maWindow) {
                continue;
            }

            Collections.sort(newBacs.get(chrom), IFeature.comChromStart);
            double sum = 0;
            for (int j = 0; j < maWindow; j++) {
                sum += newBacs.get(chrom).get(j).getRatio();

            }

            newBacs.get(chrom).get(smooth).setRatio(MyMath.formatDoubleValue(sum / maWindow, 3));

            for (int j = smooth + 1; j
                    < newBacs.get(chrom).size() - smooth; j++) {

                sum = sum - newBacs.get(chrom).get(j - smooth - 1).getRatio() + newBacs.get(chrom).get(j + smooth).getRatio();
                newBacs.get(chrom).get(j).setRatio(MyMath.formatDoubleValue(sum / maWindow, 3));

            }

        }
        return newBacs;

    }

    public void unsetAberrantFeatures() {
        for (Vector<? extends IFeature> bacs : this.chrFeatures.values()) {
            for (IFeature currentBac : bacs) {

                currentBac.setIfAberrant(0);

            }
        }
    }

    /**
     * detect the aberrant BACs based on the ratio
     *
     * @return the number of aberrant BACs
     */
    public int setAberrantFeaturesByRatio(double positiveValue, double negativeValue) {

        int no = 0;
        for (Vector<? extends IFeature> bacs : this.chrFeatures.values()) {
            for (IFeature currentBac : bacs) {
                if (currentBac.getRatio() > positiveValue) {
                    no++;
                    currentBac.setIfAberrant(1);
                } else if (currentBac.getRatio() < negativeValue) {
                    no++;
                    currentBac.setIfAberrant(-1);

                } else {
                    currentBac.setIfAberrant(0);

                }

            }
        }
        // kt new super.setAberrantBacNo(no);
        return no;
    }

    /**
     * detect the aberrant BACs based on the ratio of spots in a window
     *
     * @return the number of aberrant BACs
     */
    public int aberrantFeaturesByRatioWindow(double positiveValue, double negativeValue, int window) {
        int no = 0;
        for (String chrom : this.chrFeatures.keySet()) {
            // if the number of BACs on a chromosome is less than the window size, ignore the smoothing
            if (chrFeatures.get(chrom).size() <= window) {
                continue;
            }

            Collections.sort(chrFeatures.get(chrom), IFeature.comChromStart);

            for (int j = 0; j
                    < chrFeatures.get(chrom).size() - window; j++) {
                int sum = 0;
                for (int k = 0; k
                        < window; k++) {
                    if (chrFeatures.get(chrom).get(j + k).getRatio() < negativeValue) {
                        sum += -1;

                    } else if (chrFeatures.get(chrom).get(j + k).getRatio() > positiveValue) {
                        sum += 1;
                    }

                }
                if (sum == -1 * window) {
                    for (int k = 0; k
                            < window; k++) {
                        chrFeatures.get(chrom).get(j + k).setIfAberrant(-1);
                        no++;

                    }

                } else if (sum == window) {

                    for (int k = 0; k
                            < window; k++) {
                        chrFeatures.get(chrom).get(j + k).setIfAberrant(1);
                        no++;

                    }

                } else {
                    for (int k = 0; k
                            < window; k++) {
                        chrFeatures.get(chrom).get(j + k).setIfAberrant(0);
                    }

                }

            }
        }
        // kt new super.setAberrantBacNo(no);
        return no;
    }

    public final Hashtable<String, Vector<? extends IFeature>> getFeatures() {
        return this.chrFeatures;
    }

    /**
     *
     */
    public List<? extends Region> getData() {
        if (this.chrFeatures == null || this.chrFeatures.size() <= 0) {
            return Collections.emptyList();
        }
        List<IFeature> fList = new Vector<IFeature>();
        fList.clear();
        for (List<? extends IFeature> list : this.chrFeatures.values()) {
            fList.addAll(list);
        }
        return fList;
    }

    public List<? extends Region> getData(String chromId) {
        if (this.chrFeatures == null || this.chrFeatures.size() <= 0 || this.chrFeatures.get(chromId) == null) {
            return Collections.emptyList();
        }
        return (List<? extends Region>) this.chrFeatures.get(chromId);
    }

    @Override
    public double getMedianLog2Ratio(List<? extends Object> data) {

        List<? extends IFeature> bacs = (List<? extends IFeature>) data;
        Collections.sort(bacs, IFeature.comRatio);
        double median = 0.0;
        if ((bacs.size() % 2) == 0) {
            IFeature left = bacs.get(bacs.size() / 2 - 1);
            IFeature right = bacs.get(bacs.size() / 2);
            median = 0.5 * (left.getRatio() + right.getRatio());
        } else {
            IFeature spotmedian = bacs.get((bacs.size() - 1) / 2);
            median = spotmedian.getRatio();
        }
        return median;
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

    @Override
    public String toString() {
        return this.dataEntity.toFullString();
    }

    public void setFeatureData(List<? extends IFeature> list) {
        for (IFeature f : list) {
            this.addFeature(f);
        }
    }

    public void setData(List<? extends Region> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (list.get(0) instanceof ISpot) {
            this.dataFromSpots((List<? extends ISpot>) list);
        } else {
            this.setFeatureData((List<? extends IFeature>) list);
        }
    }
}
