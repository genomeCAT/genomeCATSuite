/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cghpro.xport;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * 260313 kt    getNoimp
 * 120313 kt	readFilenames throws Exception
 * 120313 kt    user setting if has header
 * 120313 kt    getError
 */
public interface XPortImport {

    int ind_db = 1;
    int ind_file = 0;
    public XPortImport createNewImport();

    public File getImportFile();

    String[] getDBColNames();

    public void setHasHeader(boolean hasHeader);

    List<String[]> getDefaultMappingFile2DBColNames();

    String[] getFileColNames() throws Exception;

    Vector<String> getImportType();

    List<String[]> getMappingFile2DBColNames();

    String getName();

    String getRelease();

    Vector<Vector<String>> readData(int i);

    String setMappingFile2DBColNames(List<String[]> map);

    public boolean hasSplitField();

    public void setSplitFieldName(String field);

    public String getSplitFieldName();

    public String[] getSplitFieldArray();

    public String getSplitPattern();

    int getError();

    int getNoimp();
}
