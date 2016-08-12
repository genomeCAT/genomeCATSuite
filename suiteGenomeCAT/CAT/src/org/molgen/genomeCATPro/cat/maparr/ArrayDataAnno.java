package org.molgen.genomeCATPro.cat.maparr;

/**
 * * @(#)ArrayDataAnno.java
 * * * @author Katrin Tebel * This program is free software; you can
 * redistribute it and/or * modify it under the terms of the GNU General Public
 * License * as published by the Free Software Foundation; either version 2 * of
 * the License, or (at your option) any later version, * provided that any use
 * properly credits the author. * This program is distributed in the hope that
 * it will be useful, * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the *
 * GNU General Public License for more details at http://www.gnu.org * *
 */
import org.molgen.genomeCATPro.cat.util.*;
import org.molgen.genomeCATPro.datadb.dbentities.Data;

/**
 * maintain data to view array like filter, matched view class etc keeps
 * DataEntity from DB (like ExperimentData)
 */
public class ArrayDataAnno extends ArrayData {

    private String release;
    private String annoname;

    /**
     *
     * @param id temporary identifier for arrayview
     * @param clazz ArrayView.class
     * @param Data data entity
     */
    ArrayDataAnno(Long id, Class clazz, String release, String name) {
        this.id = id;

        this.clazz = clazz;
        this.release = release;
        this.annoname = name;
        System.out.println("construct anno data " + this.getName() + " " + this.getRelease());
    }

    /**
     * create new meta data with filter options
     *
     * @param id
     * @param clazz
     * @param d
     * @param filterPos
     * @param filterNeg
     */
    ArrayDataAnno(Long id, Class clazz, Data d, double filterPos,
            double filterNeg) {

        this(id, clazz, "", "");
        System.out.println("construct anno data " + this.getName() + " " + this.getRelease());

    }

    @Override
    public String getName() {
        return annoname;
    }

    public void setName(String annoname) {
        this.annoname = annoname;
    }

    @Override
    public String getRelease() {
        return this.release;
    }

    @Override
    public StringBuffer getText() {
        StringBuffer text = new StringBuffer();

        text.append(
                Defines.getViewTitelByClazzname(
                        this.getArrayClazz().getName()) + "\n");

        text.append(new String("id:\t"
                + this.id + " " + " \n"));
        //text.append(new String(this.data.) + " \n"));
        text.append(annoname);

        //text += clazz + "\n";
        // text += new String("pos threshold: " + this.posThreshold + "\n");
        // text += new String("neg threshold: " + this.negThreshold + "\n");
        return text;
    }

    public Data getData() {
        return null;
    }

    @Override
    public String toString() {
        return new String(this.id + "[" + super.getArrayClazz() + "]");
    }
}
