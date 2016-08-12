package org.molgen.genomeCATPro.data;

import java.util.Comparator;

/**
 * extended version of spot, hold information about measured/imported channel
 * intensities 
 *
 * @author tebel
 */
public interface IOriginalSpot extends ISpot {

    
    public double getLog2Cy3();

    public void setLog2Cy3(double log2Cy3);

    public double getLog2Cy5();

    public void setLog2Cy5(double log2Cy5);

    /**
     * true if log2 ratio is calculated as ratio red/green channel, otherwise
     * false
     */
    public boolean getRG();

    public double getCy3Value();

    public double getCy5Value();

    public int getBlock();

    public int getRow();

    public int getColumn();

   
    /**
     * comparator used to sort spots according to f635
     */
    public static final Comparator<IOriginalSpot> comLog2Cy5 = new Comparator<IOriginalSpot>() {

        public int compare(IOriginalSpot r1, IOriginalSpot r2) {

            if (r1.getLog2Cy5() > r2.getLog2Cy5()) {
                return 1;
            } else if (r1.getLog2Cy5() == r2.getLog2Cy5()) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**
     * comparator used to sort spots according to f532
     */
    public static final Comparator<IOriginalSpot> comlog2Cy3 = new Comparator<IOriginalSpot>() {

        public int compare(IOriginalSpot r1, IOriginalSpot r2) {

            if (r1.getLog2Cy3() > r2.getLog2Cy3()) {
                return 1;
            } else if (r1.getLog2Cy3() == r2.getLog2Cy3()) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**
     * comparator used to sort spots according to sum of signal intensity at 2
     * channels
     */
    public static final Comparator<IOriginalSpot> comMvalue = new Comparator<IOriginalSpot>() {

        public int compare(IOriginalSpot r1, IOriginalSpot r2) {

            if ((r1.getLog2Cy3() + r1.getLog2Cy5()) > (r2.getLog2Cy3() + r2.getLog2Cy5())) {
                return 1;
            } else if ((r1.getLog2Cy3() + r1.getLog2Cy5()) == (r2.getLog2Cy3() + r2.getLog2Cy5())) {
                return 0;
            } else {
                return -1;
            }

        }
    };
    /**
     * comparator used to sort spots according to id
     */
    public static final Comparator<IOriginalSpot> comBlock = new Comparator<IOriginalSpot>() {

        public int compare(IOriginalSpot r1, IOriginalSpot r2) {

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
