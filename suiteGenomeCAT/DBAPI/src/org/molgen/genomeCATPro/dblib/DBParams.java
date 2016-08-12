package org.molgen.genomeCATPro.dblib;

/**
 * * @(#)DBParams.java
 * * * @author Katrin Tebel * This program is free software; you can
 * redistribute it and/or * modify it under the terms of the GNU General Public
 * License * as published by the Free Software Foundation; either version 2 * of
 * the License, or (at your option) any later version, * provided that any use
 * properly credits the author. * This program is distributed in the hope that
 * it will be useful, * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the *
 * GNU General Public License for more details at http://www.gnu.org * *
 */
public class DBParams {

    public String port = "";
    public String host = "";
    public String database = "";
    public String user = "";
    public String password = "";

    public DBParams(String host, String database, String port, String user, String password) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
    }

}
