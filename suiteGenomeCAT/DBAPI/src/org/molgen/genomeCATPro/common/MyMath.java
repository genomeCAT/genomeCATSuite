package org.molgen.genomeCATPro.common;


/** * @(#)MyMath.java * * Copyright (c) 2004 by Wei Chen
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

/**
 * Class MyMath is a class containing some math functions.
**/

public class MyMath {

	/**
	 *Calculate the logarithm (base 2) of a double value.
	 *@param a input
	 *@return log2(a)
	 **/
	
	public static double log2(double a){
		
		double x = Math.log(a);
		double y = Math.log(2);
		return x/y;
		
		
	}
	
	public static double log10(double a){
		
		double x = Math.log(a);
		double y = Math.log(10);
		return x/y;
		
		
	}	 
	 /** 
	   * Transform the double value to the specified precision
	   * @param value double
	   * @param x the number of precision
	   * @return the transformed double value
	*/
	 public static double formatDoubleValue(double value, int x){
    
    		return ( Math.round(Math.pow(10,x)*value)/Math.pow(10,x));
   	 }

    public static double median(Double[] tmp) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    	
        /** 
	   * Multiply an array of integer
	   * @param input an array of integer
	   * @return the resulted integer
	*/
        public static int multiply(float[] input){
		int result=1;
		for (int i=0; i<input.length;i++){
			result*=input[i];
		}
		return result;
	
	}
        /** 
	   * Multiply an array of integer
	   * @param input an array of integer
	   * @return the resulted integer
	*/
        public static int multiply(int[] input){
		int result=1;
		for (int i=0; i<input.length;i++){
			result*=input[i];
		}
		return result;
	
	}
	 /** 
	   * Calculate a to power b
	   * @param a base
	   * @param b power
	   * @return the resulted integer
	*/
	public static int power(int a, int b){
	
		int result=1;
		for(int i=0; i<b; i++){
			result*=a;
		}
		return result;
	}
	
       /** 
	   * Calculate the median of an array of double
	   * @param x input array of doubles
	   * @return the median
	*/
        public static double median(double[] x){
		double[] sortx=x.clone();
		int n=sortx.length;
    		int incr=(int)(n*.5);
    		while (incr >= 1) {
      			for (int i=incr;i<n;i++) {
        			double temp=sortx[i];
        			int j=i;
        			while (j>=incr && temp<sortx[j-incr]) {
         			 	sortx[j]=sortx[j-incr];
         			 	j-=incr;
        			}
        			sortx[j]=temp;
      				}
      			incr/=2;
    		}
   		double index=(n+1)*0.5;
      		if (index-(int)index == 0)
        		return sortx[(int)index - 1];
      		else
        		return 0.5*sortx[(int)Math.floor(index)-1]+(1-0.5)*sortx[(int)Math.ceil(index)-1];
		
	
	}
}
