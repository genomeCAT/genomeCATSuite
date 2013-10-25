package org.molgen.genomeCATPro.appconf;


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.modules.InstalledFileLocator;


/**
 * @name CoreProperties
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 * Copyright Apr 7, 2009 Katrin Tebel <tebel at molgen.mpg.de>.
 * The contents of props file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use props file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * props program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
/*
 * 090712 kt    relocate coreproperties.xml
 * 100812 kt    add filepath as global parameter
 */
public class CorePropertiesMod implements Serializable {

    static CoreProperties props = null;
    transient static String stfile = "coreproperties.xml";

    public static CoreProperties props() {
        if (CorePropertiesMod.props == null) {
            CorePropertiesMod.props = load();
        }
        if (CorePropertiesMod.props == null) {
            create();
        }
        return CorePropertiesMod.props;
    }

    private static CoreProperties load() {
        
        try {
            Logger.getLogger(CorePropertiesMod.class.getName()).log(
                    Level.INFO, "load properties from " + stfile);
            File f = InstalledFileLocator.getDefault().locate(
                    CorePropertiesMod.stfile, "org.molgen.genomeCATPro.appconf", false);
            //CorePropertiesMod.stfile, null, true);

            Logger.getLogger(CorePropertiesMod.class.getName()).log(
                    Level.INFO, "coreproperties path " + f.getPath());
            XMLDecoder d = new XMLDecoder(
                    new BufferedInputStream(
                    new FileInputStream(f)));
            CoreProperties _props = (CoreProperties) d.readObject();
            d.close();
            return _props;
        } catch (Exception ex) {
            Logger.getLogger(CorePropertiesMod.class.getName()).log(
                    Level.SEVERE, "error ", ex);
            return null;
        }
    }

    @SuppressWarnings("empty-statement")
    public static void create() {
        Logger.getLogger(CoreProperties.class.getName()).log(Level.INFO, "CoreProperties created");
        CorePropertiesMod.props = new CoreProperties();


        try {
             save();
            ;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CorePropertiesMod.class.getName()).log(
                    Level.SEVERE, "error ", ex);
        }
    }

    public static void save() throws FileNotFoundException {
        Logger.getLogger(CoreProperties.class.getName()).log(
                Level.INFO, "save CoreProperties");
        File f = InstalledFileLocator.getDefault().locate(
                CorePropertiesMod.stfile, "org.molgen.genomeCATPro.appconf", false);
        //CorePropertiesMod.stfile, null, true);
        XMLEncoder e = new XMLEncoder(
                new BufferedOutputStream(
                new FileOutputStream(f)));
        e.writeObject(CorePropertiesMod.props);
        e.close();
    }

    public void saveWithoutPWD() throws FileNotFoundException {
        String oldpwd = props.pwd;
        Logger.getLogger(CorePropertiesMod.class.getName()).log(
                Level.INFO, "save CoreProperties without pwd");
        try {
            props.pwd = "";
            File f = InstalledFileLocator.getDefault().locate(
                    CorePropertiesMod.stfile, "org.molgen.genomeCATPro.appconf", false);
            // CorePropertiesMod.stfile, null, true);
            XMLEncoder e = new XMLEncoder(
                    new BufferedOutputStream(
                    new FileOutputStream(f)));
            e.writeObject(props);
            e.close();
        } finally {
            Logger.getLogger(CorePropertiesMod.class.getName()).log(Level.SEVERE,
                    "restore set pwd");
            props.setPwd(oldpwd);
        }
        Logger.getLogger(CorePropertiesMod.class.getName()).log(
                Level.INFO, "PWD: " + props.getPwd());
    }

    public static class CoreProperties implements Serializable {

        private String appVersion = "";
        private Integer appBuild = 0;
        private String host;
        private String port;
        private String db;
        private String user;
        private String pwd;
        

        public CoreProperties() {
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public Integer getAppBuild() {
            return appBuild;
        }

        public void setAppBuild(Integer appBuild) {
            this.appBuild = appBuild;
        }

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getDb() {
            return this.db;
        }

        public void setDb(String db) {
            this.db = db;
        }

        public String getUser() {
            return this.user;
        }

        public void setUser(String user) {
            System.out.println("set user");
            this.user = user;
        }

        public String getPwd() {
            return this.pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;

        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

       
    }
}

