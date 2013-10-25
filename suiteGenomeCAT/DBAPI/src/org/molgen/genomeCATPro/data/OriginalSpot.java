/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.data;

import java.util.Comparator;

/**
 * extended version of spot, hold information about measured/imported channel intensities
 * implementations should be part of xport packages, 
 * because db structure differ for any implementation and the app could be extended to new 
 * array structures
 * @author tebel
 */
public interface OriginalSpot  extends Spot {

    /**
     * import spot from db, implemented as part of xport module
     * @param tablename
     * @return
     */
    //public List<? extends OriginalSpot> dbLoadSpots(String tablename);

    public double getLog2Cy3();

    public void setLog2Cy3(double log2Cy3);

    public double getLog2Cy5();

    public void setLog2Cy5(double log2Cy5);

     /** true if log2 ratio is calculated as ratio red/green channel, otherwise false */
   
    public boolean getRG();

    public double getCy3Value();

    public double getCy5Value();
    
    public int getBlock();
    public int getRow();
    public int getColumn();
    
    /**
     * 
     * Global normalization  by a constant factor, 
     * i.e. R  = kG, 
     * center of the distribution of log ratios is shifted to zero
     * log2R/G → log2R/G – c = log2R/(kG)
     * c = log2k 
     * @param log2Factor
     */
    /**comparator used to sort spots according to f635*/
    public static final Comparator<OriginalSpot> comLog2Cy5 = new Comparator<OriginalSpot>() {

        public int compare(OriginalSpot r1, OriginalSpot r2) {

            if (r1.getLog2Cy5() > r2.getLog2Cy5()) {
                return 1;
            } else if (r1.getLog2Cy5() == r2.getLog2Cy5()) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**comparator used to sort spots according to f532*/
    public static final Comparator<OriginalSpot> comlog2Cy3 = new Comparator<OriginalSpot>() {

        public int compare(OriginalSpot r1, OriginalSpot r2) {

            if (r1.getLog2Cy3() > r2.getLog2Cy3()) {
                return 1;
            } else if (r1.getLog2Cy3() == r2.getLog2Cy3()) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**comparator used to sort spots according to sum of signal intensity at 2 channels*/
    public static final Comparator<OriginalSpot> comMvalue = new Comparator<OriginalSpot>() {

        public int compare(OriginalSpot r1, OriginalSpot r2) {

            if ((r1.getLog2Cy3() + r1.getLog2Cy5()) > (r2.getLog2Cy3() + r2.getLog2Cy5())) {
                return 1;
            } else if ((r1.getLog2Cy3() + r1.getLog2Cy5()) == (r2.getLog2Cy3() + r2.getLog2Cy5())) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**comparator used to sort spots according to id*/
    public static final Comparator<OriginalSpot> comBlock = new Comparator<OriginalSpot>() {

        public int compare(OriginalSpot r1, OriginalSpot r2) {

            if (r1.getBlock() > r2.getBlock()) {
                return 1;
            } else if (r1.getBlock() == r2.getBlock()) {
                return 0;
            } else {
                return -1;
            }

        }
    };
}
