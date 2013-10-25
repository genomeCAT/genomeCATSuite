package org.molgen.genomeCATPro.cghpro.chip;

/** * @(#)BacInfo.java * * Copyright (c) 2004 by Wei Chen
 * * @author Wei Chen
 * * Email: wei@molgen.mpg.de
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License 
 * * as published by the Free Software Foundation; either version 2 
 * * of the License, or (at your option) any later version, 
 * * provided that any use properly credits the author. 
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * * */
import java.util.*;
import org.molgen.genomeCATPro.common.Utils;

/**
 * Class BacInfo is a class holding the id and chromosome position of BACs, 
 * these information is used in cgh.cluster.SummaryChips and cgh.cluster.l		this.gc = spot.gc;
oadChips
 **/
public class BacInfo {

    /** ID of the clone*/
    public String id;
    /** name of the chromosome*/
    public String chrom;
    /** base pair start position of the clone*/
    public long chromStart;
    /** base pair end position of the clone*/
    public long chromEnd;

    /**
     * Default Constructor, do nothing
     **/
    public BacInfo() {
    }

    /**
     * Constructor
     * @param id
     * @param chrom
     * @param chromStart
     * @param chromEnd
     **/
    public BacInfo(String id,
            String chrom,
            long chromStart,
            long chromEnd) {
        this.id = id;
        this.chrom = chrom;
        this.chromStart = chromStart;
        this.chromEnd = chromEnd;




    }

    /**
     * Override the Object's equal() method,
     * used to test if the 2 BACs are same based on unique ID
     **/
    @Override
    public boolean equals(Object obj) {

        if (id.equals(((BacInfo) obj).id)) {
            return true;
        }
        return false;

    }
    
}
