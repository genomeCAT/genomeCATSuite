package org.molgen.genomeCATPro.cat.maparr;

/** * @(#)ArrayData.java 
 * *  * @author Katrin Tebel
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License 
 * * as published by the Free Software Foundation; either version 2 
 * * of the License, or (at your option) any later version, 
 * * provided that any use properly credits the author. 
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * * */
import org.molgen.genomeCATPro.cat.util.*;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 * maintain data to view array like filter, 
 * matched view class etc
 * keeps DataEntity from DB (like ExperimentData)
 */
public class ArrayData {

    Long id;
    //boolean mappedData = false;
    boolean filteredData = false;
    double filterNeg;
    double filterPos;
    String filterArrayId;
    Data data;
    Class clazz; //array view class

    private double posThreshold = 0;
    private double negThreshold = 0;
    Integer nof = 0;

    public ArrayData() {
    }

    /**
     * 
     * @param id        temporary identifier for arrayview
     * @param clazz     ArrayView.class
     * @param Data      data entity 
     */
    ArrayData(Long id, Class clazz, Data d) {
        this.id = id;
        this.data = d;
        this.clazz = clazz;
    }

    /**
     * create new meta data with filter options
     * @param id
     * @param clazz
     * @param d
     * @param filterPos
     * @param filterNeg
     */
    ArrayData(Long id, Class clazz, Data d, double filterPos,
            double filterNeg) {

        this(id, clazz, d);


        this.filteredData = true;
        this.filterNeg = filterNeg;
        this.filterPos = filterPos;


    }

    public double getFilterNeg() {
        return filterNeg;
    }

    public void setFilterNeg(double filterNeg) {
        this.filterNeg = filterNeg;
    }

    public double getFilterPos() {
        return filterPos;
    }

    public void setFilterPos(double filterPos) {
        this.filterPos = filterPos;
    }

    public String getRelease() {
        return this.data.getGenomeRelease().toString();
    }

    public StringBuffer getText() {
        StringBuffer text = new StringBuffer();

        text.append((this.filteredData ? "filtered " : "") + Defines.getViewTitelByClazzname(
                this.getArrayClazz().getName()) + "\n");

        text.append(new String("id:\t" +
                this.id + " " + " \n"));
        //text.append(new String(this.data.) + " \n"));
        text.append(this.data.getMetaText());


        if (filteredData) {
            text.append(new String("Filter neg: \t" + this.filterNeg + "\n"));
            text.append(new String("Filter pos: \t" + this.filterPos + "\n"));
            text.append(new String("Filter nof: \t" + this.nof + "\n"));
        }
        //text += clazz + "\n";
        // text += new String("pos threshold: " + this.posThreshold + "\n");
        // text += new String("neg threshold: " + this.negThreshold + "\n");

        return text;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Class getArrayClazz() {
        return this.clazz;

    //return ArrayData.matchDataTypeToViewClazz(Defaults.DataType.toDataType(this.data.getDataType()));
    }

    public String getName() {
        return getData().getName();
    }

    Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return new String(this.id + "[" + this.getArrayClazz() + "]");
    }

    public boolean equals(Long arrayId) {
        return this.id.equals(arrayId);
    }

    void setNegThreshold(double d) {
        this.negThreshold = d;
    }

    public double getPosThreshold() {
        return posThreshold;
    }

    public double getNegThreshold() {
        return negThreshold;
    }

    void setPosThreshold(double d) {
        this.posThreshold = d;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    static ArrayData createArrayData(Class _clazz, Data d) {

        Long _id = ArrayManager.getNextArrayId();
        ArrayData m = new ArrayData(_id, _clazz, d);


        return m;

    }

    static ArrayData createArrayDataAnno(Class _clazz, String release, String name) {

        Long _id = ArrayManager.getNextArrayId();
        ArrayData m = new ArrayDataAnno(_id, _clazz, release, name);


        return m;

    }
}
