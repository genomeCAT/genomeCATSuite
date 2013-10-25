package org.molgen.genomeCATPro.cat.util;



/** * @(#)Utils.java * * Copyright (c) 2004 by Wei Chen
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
import java.io.File;

/**
 * Utils is a class containing some static methods for miscellaneous utilities.
**/
public class Utils {


    /**
     * Get the extension of a file.
     * @param f input file
     * @return the extension of the file
     **/
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
    * delete file, use system command depending on operating system
    * @param filename file to delete
    */
    public static void deleteFile(String filename) {
        try {
            // make shure that the file not existing for all
            String os = System.getProperty("os.name");
            String command;
            if (os == null || os.toLowerCase().startsWith("windows")) {
                command = "del " + filename;
            } else {
                command = "rm -f " + filename;
            }
            //Runtime.getRuntime().exec(command);
            Process p = Runtime.getRuntime().exec(command);
            try {
                p.waitFor();
            } catch (java.lang.InterruptedException e) {
                e.printStackTrace();
                throw (new RuntimeException(e));
            }
        } catch (Exception e) {
            e.printStackTrace();
        //throw new RuntimeException(e);
        }
    }
}
