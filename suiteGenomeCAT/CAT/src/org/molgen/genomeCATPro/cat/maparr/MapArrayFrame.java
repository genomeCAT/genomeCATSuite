package org.molgen.genomeCATPro.cat.maparr;
/**
 * @name MapArrayFrame
 *
 * 
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * 
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.molgen.genomeCATPro.common.Defaults;
import org.molgen.genomeCATPro.datadb.dbentities.Data;
import org.molgen.genomeCATPro.datadb.dbentities.MapData;
/*
 * 
 */
public class MapArrayFrame extends ArrayFrame {

    String mapName = "";
    //addMapping();

    public MapArrayFrame() {
        super();
        Logger.getLogger(ArrayFrame.class.getName()).log(
                Level.INFO, "create MapArrayFrame default");



    }

    @Override
    public void mapArrays() {
    }
    /*
    public MapArrayFrame(Data[] list) {
    super(list, null);
    Logger.getLogger(ArrayFrame.class.getName()).log(
    Level.INFO, "create MapArrayFrame with data");
    
    
    //addMapping();
    
    }
     */
    /*
    public MapArrayFrame(List<ArrayData> list) {
    super(list, null);
    Logger.getLogger(ArrayFrame.class.getName()).log(
    Level.INFO, "create MapArrayFrame with array data");
    
    
    //addMapping();
    
    }
     */

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * present view to filter arrays to map
     * 
     */
    @Override
    public void load() {
        if (super.arrays != null && super.arrays.size() > 0) {
            return;
        }
        try {
            Data[] list = FilterExperimentsDialog.getDataList(null);
            // check wich view for each clazz is available
            // lets user choose which view to load
            //
            List<MapData> mlist = MapDialog.getMapping(new ArrayList<Data>(Arrays.asList(list)), "");
            if (mlist != null) {
                this.addData(mlist);
            }
        } catch (Exception ex) {
            Logger.getLogger(MapArrayFrame.class.getName()).log(Level.SEVERE,
                    "load:", ex);

        } finally {
            //ArrayFrame.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
    }

    @Override
    public void addData(Data[] list) {

        this.addData(new ArrayList(Arrays.asList(list)));

    }

    @Override
    public void filterArray(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    ArrayView filterArrayView( ArrayView v, ArrayData d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void filterArrayAtDB(Long id, ArrayData d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void jButtonMapActionPerformed(java.awt.event.ActionEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addData(List<MapData> list) {

        try {
            if (list == null || list.size() == 0) {
                return;
            // set check name
            }
            setRelease(Defaults.GenomeRelease.toRelease(
                     list.get(0).getGenomeRelease()));

            List<ArrayData> adList = MapDialog.getArrayMappedData(list);
            MapArrayFrame.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            for (ArrayData ad : adList) {

                MapArrayFrame.this.addArray(ad);
            }
           
        } catch (Exception ex) {
            Logger.getLogger(ArrayFrame.class.getName()).log(Level.SEVERE,
                    "AddData:", ex);

        } finally {
            MapArrayFrame.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
