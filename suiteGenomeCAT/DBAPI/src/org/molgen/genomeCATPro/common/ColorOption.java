package org.molgen.genomeCATPro.common;


/** * @(#)ColorOption.java * * Copyright (c) 2004 by Wei Chen
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

import java.awt.Color;

/**
* Class ColorOption is a class used to generate an array of colors to represent
* LCR ranges.
**/

public class ColorOption{

	public static Color[] colors;
	public static Color[] colors2;
	
     
	 /**
	 * Generate an array containing 8 colors to represent the LCR ranges.
	 * gradually while yellow decrease gradually.
	 **/
	public static void setColor(){
	
	
		colors = new Color[9];
		colors[0] = Color.white;
		colors[1] = new Color(129,2,2);
		colors[2] = new Color(129,86,2);
		colors[3] = new Color(119,129,2);
		colors[4] = new Color(2,129,14);
		colors[5] = new Color(2,129,110);
		colors[6] = new Color(2,71,129);
		colors[7] = new Color(62,2,129);
		colors[8] = Color.white;
		
		colors2 = new Color[9];
		colors2[0] = Color.red;
		colors2[1] = Color.blue;
		colors2[2] = Color.green;
		colors2[3] = Color.black;
		colors2[4] = Color.pink;
		colors2[5] = Color.orange;
		colors2[6] = Color.cyan;
		colors2[7] = Color.magenta;
		colors2[8] = Color.white;	
	
	
	
	}

}
