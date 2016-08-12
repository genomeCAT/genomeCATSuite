package org.molgen.genomeCATPro.peaks.cnvcat.util;

/**
 * * @(#)TxtFilter.java * * Copyright (c) 2004 by Wei Chen
 * * @author Wei Chen * Email: wei@molgen.mpg.de * This program is free
 * software; you can redistribute it and/or * modify it under the terms of the
 * GNU General Public License * as published by the Free Software Foundation;
 * either version 2 * of the License, or (at your option) any later version, *
 * provided that any use properly credits the author. * This program is
 * distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY;
 * without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the * GNU General Public License for more details at
 * http://www.gnu.org * *
 */
import java.io.File;
import javax.swing.filechooser.*;
import org.molgen.genomeCATPro.common.Utils;

/**
 * Class GPRFilter is a FileFilter accepting only txt file in addtion to
 * directory.
 *
 */
public class CSVFilter extends FileFilter {

    /**
     * Test if the input file gpr file is gpr file or directory.
     *
     * @param f Input File
     * @return boolean indicating if the file is gpr file or directory
     *
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals("csv")) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * Describe the filter.
     *
     * @return "Just GPR file"
     *
     */
    public String getDescription() {
        return "Just txt file";
    }
}
