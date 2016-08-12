/**
 * @name ImportPlatformGEO
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
package org.molgen.genomeCATPro.cghpro.xport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.datadb.dbentities.PlatformDetail;

/**
 * 020813 kt	XPortImport createNewImport(); 140513 kt handle empty catalognumber
 *
 */
public class ImportPlatformGEOSOFT extends ImportPlatformGEO implements XPortPlatform {

    public final static String platform_geo_soft = "GEO_GPL_SOFT";

    public ImportPlatformGEOSOFT createNewImport() {
        return new ImportPlatformGEOSOFT();
    }

    @Override
    public boolean isHasHeader() {
        return true;
    }

    @Override
    public String getName() {
        return new String("GEO SOFT");
    }
    String metaDataTag = "^PLATFORM";

    @Override
    public Vector<String> getImportType() {
        return new Vector<String>(
                Arrays.asList(new String[]{
            ImportPlatformGEOSOFT.platform_geo_soft
        }));
    }

    @Override
    protected String getEndMetaDataTag() {
        return new String("!platform_table_begin");
    }

    @Override
    protected boolean isCommentLine(String is) {
        if (is.indexOf("#") == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getEndDataTag() {
        return "!platform_table_end";
    }

    PlatformDetail readHeaderSOFTTXT() {

        boolean header = false;
        boolean data = false;
        String is = null;

        // reset
        String name = null;
        String titel = null;
        String technology = null;
        String organism = null;
        String manu = null;
        String manu_p = "";
        String catalog = null;
        String support = null;
        String coating = null;
        String distribution = null;
        String desc = null;
        String web = null;

        try {
            inBuffer = new BufferedReader(new FileReader(inFile));

            while (data == false && (is = inBuffer.readLine()) != null) {
                if (is == null) {
                    continue;
                }
                if (is.indexOf(this.metaDataTag) >= 0) {
                    header = true;

                    name = is.substring(this.metaDataTag.length());
                    name = name.trim();
                }
                if (header) {

                    if (is.indexOf("!Platform_geo_accession") >= 0) {
                        name = is.substring(is.indexOf("=") + 1);
                        name = name.trim();
                    }

                    if (is.indexOf("!Platform_title") >= 0) {
                        titel = is.substring(is.indexOf("=") + 1);
                        titel = titel.trim();
                    }
                    if (is.indexOf("!Platform_technology") >= 0) {
                        technology = is.substring(is.indexOf("=") + 1);
                        technology = technology.trim();
                    }
                    if (is.indexOf("!Platform_organism") >= 0) {
                        organism = is.substring(is.indexOf("=") + 1);
                        organism = organism.trim();
                    }
                    if (is.indexOf("!Platform_manufacturer") >= 0) {
                        manu = is.substring(is.indexOf("=") + 1);
                        manu = manu.trim();
                    }
                    if (is.indexOf("!Platform_manufacture_protocol") >= 0) {
                        String p = is.substring(is.indexOf("=") + 1);
                        manu_p = manu_p + "; " + p.trim();
                    }
                    if (is.indexOf("!Platform_catalog_number") >= 0) {
                        catalog = is.substring(is.indexOf("=") + 1);
                        catalog = catalog.trim();
                    }
                    if (is.indexOf("!Platform_support") >= 0) {
                        support = is.substring(is.indexOf("=") + 1);
                        support = support.trim();
                    }
                    if (is.indexOf("!Platform_coating") >= 0) {
                        coating = is.substring(is.indexOf("=") + 1);
                        coating = coating.trim();
                    }
                    if (is.indexOf("!Platform_distribution") >= 0) {
                        distribution = is.substring(is.indexOf("=") + 1);
                        distribution = distribution.trim();
                    }
                    if (is.indexOf("!Platform_description") >= 0) {
                        desc = is.substring(is.indexOf("=") + 1);
                        desc = desc.trim();
                    }
                    if (is.indexOf("!Platform_web_link") >= 0) {
                        web = is.substring(is.indexOf("=") + 1);
                        web = web.trim();
                    }
                    //continue;
                }

                if (is.indexOf(this.getEndMetaDataTag()) >= 0) {
                    header = false;
                    data = true;
                }

            }
            if (catalog == null) //140513    kt handle empty catalognumber
            {
                catalog = name;
            }
        } catch (Exception e) {
            Logger.getLogger(ImportPlatformGEOSOFT.class.getName()).log(Level.SEVERE, null, e);

        } finally {
            try {
                inBuffer.close();
            } catch (IOException ex) {
                Logger.getLogger(ImportPlatformGEOSOFT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PlatformDetail d = new PlatformDetail();
        d.setName(name);
        d.setCatalogNumber(catalog);
        d.setTitel(titel);
        d.setCoating(coating);
        d.setDistribution(distribution);
        d.setDescription(desc);
        d.setManufactureProtocol(manu_p);
        d.setManufacturer(manu);

        d.setOrganism(organism);
        d.setTechnology(technology);
        d.setWebLink(web);

        return d;
    }

    @Override
    public PlatformDetail getPlatformDetail() {
        PlatformDetail d = this.readHeaderSOFTTXT();
        String name = this.inFile.getName();
        if (d.getName() == null || name.contentEquals("")) {
            name.substring(0, name.indexOf(".soft"));
            d.setName(name);
        }
        //d.setType(Defaults.Type.BAC.toString());
        if (d.getDescription() != null) {
            d.setDescription(d.getDescription() + ";imported via " + this.getClass().getName());
        } else {
            d.setDescription("imported via " + this.getClass().getName());
        }
        return d;
    }
}
