package org.molgen.genomeCATPro.peaks.cnvcat;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Utils;
import org.molgen.genomeCATPro.peaks.Aberration;
import org.molgen.genomeCATPro.peaks.AberrationIds;

/**
 * @name AberrationManager
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>.
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
public abstract class AberrationManager {

    protected List<? extends AberrationIds> activeCases;
    protected List<? extends AberrationIds> allAberrationIds;
    //protected Hashtable<String, Hashtable<String,  List<? extends Aberration>>> 
    //        aberrationTable = new Hashtable();
    protected Hashtable<String, List<? extends Aberration>> aberrationTable = new Hashtable();
    protected Vector<String> chroms = new Vector<String>();
    protected Color[] colors;
    protected List<? extends Aberration> dispAberrations;
    protected EntityManagerFactory emf;
    protected HashMap<Object, Object> properties = new HashMap<Object, Object>();

    public AberrationManager() {
        initColors();
    }
    ;

    public abstract void deleteAberrations(List<? extends AberrationIds> alist) throws Exception;

    public abstract String exportIntoFile(List<? extends AberrationIds> idList, String sOutfile) throws IOException, Exception;

    public abstract void filterAberrationIds(String[] filter);

    protected abstract void loadAberrationForActiveCaseIds();

    /**
     * get all possible aberrations filtered by distinct caseId and phenotype
     * @return
     */
    public abstract List<? extends AberrationIds> findAberrationIds();

    /**
     * select distinct values for spezified column (group)
     * @return
     */
    public abstract Vector getColorGroupList();

    public abstract AberrationIds getIdForAberration(Aberration ab);

    public abstract int getIndexAberrationId(Aberration a);

    /**
     * load selected cases, refresh aberrations
     * @param replace   true - replace older aberrationIds with new content (color)
     *                  false - redundant aberrationIs plus aberration loaded
     */
    public void loadActiveCases(boolean replace) {
        Logger.getLogger(this.getClass().getName()).log(
                Level.SEVERE, "loadActiveCases");

        List<AberrationIds> newActiveCases = (List<AberrationIds>) this.getSelectedAberrationIds();
        List<AberrationIds> removeCases = new Vector();

        for (AberrationIds a : newActiveCases) {
            for (AberrationIds b : (List<AberrationIds>) this.activeCases) {
                if (a.compareTo(b) == 0) {
                    removeCases.add(b);
                }
            }
        }
        if (replace) { // remove the old one

            for (AberrationIds r : removeCases) {
                this.activeCases.remove(r);
            }


        } else { //keep the old one

            for (AberrationIds r : removeCases) {
                newActiveCases.remove(r);
            }
        }
        // remove formerly loaded indentical aberrationid 

        //clean up old aberrations and aberrationIds which will be replaced
        this.aberrationTable.clear();

        ((List<AberrationIds>) this.activeCases).addAll(newActiveCases);
        //load aberration for updated active cases
        loadAberrationForActiveCaseIds();
        //clean up list of available aberrationIds (for reduced memory purpose)
        setAllAberrationIds(new Vector());
    }

    /**
     * check, if list contains a single caseId more than once
     * @param conflict1
     * 
     * @return vector of aberrations with same caseIds, null if no doubled caseId was found
     */
    public abstract List<? extends AberrationIds> conflictCaseIds(
            List<? extends AberrationIds> list);

    /**
     * check, if 2 lists share same AberrationIds. 
     * i.e. if one of the aberrationIds from newList is already contained in oldList
     * attribute to compare is CaseId
     */
    public List<? extends AberrationIds> conflictActiveCases(
            List<? extends AberrationIds> newList, List<? extends AberrationIds> oldList) {

        List<AberrationIds> resultList = new Vector();

        for (AberrationIds a : newList) {
            for (AberrationIds oldA : oldList) {
                if (a.compareByCase(oldA) == 0) {

                    if (!resultList.contains(a)) {
                        resultList.add(a);
                    }
                }
            }
        }
        if (resultList.size() == 0) {
            return null;
        } else {
            return resultList;
        }
    }
    ;
    // basic methods

    public Collection<String> getChroms() {
        Collections.sort(chroms, Utils.orderChroms);
        System.out.println("AberationManager.getChroms:" + chroms.toString());
        return this.chroms;
    }

    protected void addChrom(String chrom) {
        this.chroms.add(chrom);
    }

    protected void initColors() {
        this.colors = new Color[10];
        colors[0] = Color.red;
        colors[1] = Color.blue;
        colors[2] = Color.green;
        colors[3] = new Color(140, 49, 45);
        colors[4] = Color.pink;
        colors[5] = Color.cyan;
        colors[6] = Color.orange;
        colors[7] = Color.magenta;
        colors[8] = new Color(0, 153, 153);
        colors[9] = Color.yellow;
    }

    public List<? extends AberrationIds> getSelectedAberrationIds() {
        List<AberrationIds> list = new Vector();
        for (AberrationIds a : this.allAberrationIds) {
            if (a.isSelected()) {
                list.add(a);
            }
        }
        return list;
    }

    public List<? extends AberrationIds> getAllAberrationIds() {
        return (List<AberrationIds>) this.allAberrationIds;
    }

    public void setAllAberrationIds(List<? extends AberrationIds> list) {
        this.allAberrationIds.clear();
        ((java.util.List<AberrationIds>) this.allAberrationIds).addAll((List<AberrationIds>) list);
    }

    public List<? extends Aberration> getAberrationsAtChrom(String chromId) {
        return this.aberrationTable.get(chromId);
    }

    public int getActiveCasesSize() {
        return this.getActiveCases().size();
    }

    public void clearActiveCases() {
        this.aberrationTable.clear();
        this.chroms.clear();
        this.activeCases.clear();
    }

    public void setActiveCases(List<? extends AberrationIds> newCases) {
        throw new UnsupportedOperationException(getClass().getName() + " Not supported yet.");
    }

    public List<? extends AberrationIds> getActiveCases() {
        //System.out.println("get active " + this.activeCases.toString());
        return (List<AberrationIds>) activeCases;
    }

    public void setAberrationIds(List<AberrationIds> list) {
        throw new UnsupportedOperationException(getClass().getName() + " Not supported yet.");
    }

    public int getIndexAberrationId(AberrationIds a) {
        return this.activeCases.indexOf(a);
    }

    public List<? extends Aberration> getDispAberrations() {
        return dispAberrations;
    }

    public void clearDispAberrations() {

        for (Aberration a : (List<Aberration>) this.dispAberrations) {
            a.setSelected(false);
        }
        this.dispAberrations.clear();

    }

    public void setDispAberrations(List<? extends Aberration> dispAberrations) {
        this.clearDispAberrations();
        ((List<Aberration>) this.dispAberrations).addAll((List<Aberration>) dispAberrations);
    }

    public void setMarkedDispAberrations(List<Aberration> list) {
        throw new UnsupportedOperationException(getClass().getName() + " Not supported yet.");
    }

    public List<Aberration> getMarkedDispAberrations() {
        Vector<Aberration> list = new Vector();
        for (Aberration a : dispAberrations) {
            if (a.isSelected()) {
                list.add(a);
            }
        }
        return list;
    }

    public double getMaxQuality() {
        try {
            Collection<List<? extends Aberration>> c = this.aberrationTable.values();
            if (c == null || c.isEmpty()) {
                return 0.0;
            }

            Vector all = new Vector();
            for (List l : c) {
                if (l != null) {
                    all.addAll(l);
                }

            }
            Aberration a = (Aberration) Collections.max(all, Aberration.compByQuality);
            return a.getQuality();

        } catch (Exception e) {
            Logger.getLogger(AberrationManager.class.getName()).log(
                    Level.SEVERE, "getMaxQuality", e);


        }
        return 0;
    }

    public double getMaxRatio() {

        Collection<List<? extends Aberration>> c = this.aberrationTable.values();
        if (c == null || c.isEmpty()) {
            return 0.0;
        }

        Vector all = new Vector();
        for (List l : c) {
            if (l != null) {
                all.addAll(l);
            }

        }
        Aberration a = (Aberration) Collections.max(all, Aberration.compByRatio);
        return a.getRatio();
    }

    /**
     * search for aberrations which cover intervall given by posStart, posEnd
     *
     * @param chromId
     * @param chromStart
     * @param chromEnd
     * @return
     */
    public List<? extends Aberration> getAberrationsAtChromPos(String chromId, long posStart, long posEnd) {
        /* chromStart < posEnd and chromEnd > posStart
         * indcludes
         *   [ ---- ]
         *   --[--  ]
         *   [  --]--
         *   --[--]--
         * excludes
         *   ---- [ ]
         *   [ ] ----
         */

        List<? extends Aberration> a = this.aberrationTable.get(chromId);
        if (a == null || a.size() == 0) {
            return new Vector();
        }

        Collections.sort(a, Aberration.compByStart);
        int ind1 = this.getPosEnd(a, posEnd);
        // find ab. with greatest start position less than posEnd
        //int ind1 = Collections.binarySearch(a, new Aberration(posEnd, (long) 0), Aberration.findByStart);
        ind1 = ind1 < 0 ? (ind1 * -1) - 2 : ind1;     // i:= (-(insertion point) - 1)

        if (ind1 < 0) {
            ind1 = 0;
        // go to the last element with chromStart == posEnd
        }

        for (int i = ind1 + 1; i <
                a.size(); i++) {
            if (a.get(i).getChromStart() == a.get(ind1).getChromStart()) {
                ind1 = i;
            } else {
                break;
            }

        }

        // all elements with chromStart less than end position

        List<? extends Aberration> x = a.subList(0, ind1 + 1);
        Collections.sort(x, Aberration.compByEnd);


        // find ab with minimal end position greater than posStart
        int ind2 = getPosStart(x, posStart);
        // int ind2 = Collections.binarySearch(x, new Aberration( (long) 0, posStart), Aberration.findByEnd);
        ind2 = ind2 < 0 ? (ind2 * -1) - 1 : ind2;     // i:= (-(insertion point) - 1), false!!
        // last value: 70, i = -72

        if (ind2 < 0) {
            ind2 = 0;
        }

        for (int i = ind2 - 1; i >=
                0; i--) {
            if (x.get(i).getChromEnd() == x.get(ind2).getChromEnd()) {
                ind2 = i;
            } else {
                break;
            }

        }
        return x.subList(ind2, x.size());




    /*
    Collections.sort(a, Aberration.findByStart);
    int start = Collections.binarySearch(a, new Aberration(chromStart, 0), Aberration.findByStart);
    start = start < 0 ? (start * -1) - 1 : start;     // i:= (-(insertion point) - 1)
    
    if (start < 0)
    //if((a.get(0).getChromStart() - chromStart) > tolerance)
    //return new Vector(0);
    //else  {
    start = 0;
    }
    Collections.sort(a, Aberration.findByEnd);
    int end = Collections.binarySearch(a, new Aberration(0, chromEnd), Aberration.findByEnd);
    end = end < 0 ? (end * -1) - 1 : end;     // i:= (-(insertion point) - 1)
    
    if (end < 0) {
    return new Vector(0);
    } else {
    if (end < start) {
    end = start;
    }
    return a.subList(start, end);
    }
    List<Aberration> b = a.subList(0, start);
    Collections.sort(b, Aberration.findPos);
    for(Aberration ab: b){
    if(b.start )
    
    }
     */
    }

    protected abstract int getPosEnd(List<? extends Aberration> a, long posEnd);

    protected abstract int getPosStart(List<? extends Aberration> a, long posStart);

    void updateHidden(ScoreFilterManager scorefilterManager) {
        Logger.getLogger(AberrationManager.class.getName()).log(Level.INFO, " update hidden cnv");
        for (AberrationIds aid : this.activeCases) {
            aid.setNoHiddenCNV(new Integer(0));
        }

        AberrationIds aid;
        for (String c : this.aberrationTable.keySet()) {
            List<Aberration> list = (List<Aberration>) this.aberrationTable.get(c);
            for (Aberration a : list) {
                aid = this.getIdForAberration(a);
                if (scorefilterManager.doHide(a)) {
                    aid.setNoHiddenCNV(aid.getNoHiddenCNV() + 1);
                }
            }
        }


    }
}


