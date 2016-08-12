package org.molgen.genomeCATPro.cghpro.xport;

/**
 * @name XPortTrack
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 *
 *
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
import org.molgen.genomeCATPro.common.InformableHandler;
import org.molgen.genomeCATPro.datadb.dbentities.Track;

/**
 *
 *
 */
public interface XPortTrack extends XPortImport {

    public void newImportTrack(String filename) throws Exception;

    public Track getTrack();

    public Track doImportTrack(Track d, InformableHandler informable);

    public Track doImportTrack(Track d);

    public void setProject(String s);

}
