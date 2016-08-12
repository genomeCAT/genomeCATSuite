/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgen.genomeCATPro.cghpro.xport;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.appconf.CorePropertiesMod;
import org.molgen.genomeCATPro.dblib.Database;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformData;

/**
 *
 * @author tebel
 */
public class ExportPlatform {

    @SuppressWarnings("empty-statement")
    public static void doExportBEDPlus(PlatformData d, String filepath) throws Exception {
        // export chrom, start, stop id to external file
        Logger.getLogger(ExportPlatform.class.getName()).log(Level.INFO,
                "export " + d.getTableData() + " into " + filepath);
        filepath.replace(File.pathSeparator, "//");
        String sql
                = " SELECT chrom, "
                + " least(chromStart, chromEnd), greatest(chromStart,  chromEnd), "
                + " id FROM " + d.getTableData()
                + " where chrom is not null and chrom != \"\" "
                + " INTO OUTFILE \'" + filepath + "\'";
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        try {
            Statement s = con.createStatement();
            s.execute(sql);
        } catch (Exception ex) {
            Logger.getLogger(ExportPlatform.class.getName()).log(Level.SEVERE,
                    "doExportBED", ex);
            throw ex;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                ;
            }
        }
    }

    public static void doExportBED(PlatformData d, String filepath) throws Exception {
        // export chrom, start, stop id to external file
        Logger.getLogger(ExportPlatform.class.getName()).log(Level.INFO,
                "export " + d.getTableData() + " into " + filepath);
        filepath.replace(File.pathSeparator, "//");
        String sql
                = " SELECT chrom, "
                + " least(chromStart, chromEnd), greatest(chromStart,  chromEnd), "
                + " id FROM " + d.getTableData()
                + " where chrom is not null and chrom != \"\" "
                + " INTO OUTFILE \'" + filepath + "\'";
        Connection con = Database.getDBConnection(CorePropertiesMod.props().getDb());
        try {
            Statement s = con.createStatement();
            s.execute(sql);
        } catch (Exception ex) {
            Logger.getLogger(ExportPlatform.class.getName()).log(Level.SEVERE,
                    "doExportBED", ex);
            throw ex;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                ;
            }
        }
    }
}
