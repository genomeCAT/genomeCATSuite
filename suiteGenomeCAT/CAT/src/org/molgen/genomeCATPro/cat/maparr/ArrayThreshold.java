package org.molgen.genomeCATPro.cat.maparr;

/**
 * @name ArrayThreshold
 *
 * just keep thresholds as upper and lower bound for a certain array
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Mar 24, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
public class ArrayThreshold {

    private String arrayName;
    private Double valueHigh;
    private Double valueLow;

    ArrayThreshold(String name, Double valueLow, Double valueHigh) {
        setArrayName(name);
        this.setValueHigh(valueHigh);
        this.setValueLow(valueLow);
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public Double getValueHigh() {
        return valueHigh;
    }

    public void setValueHigh(Double valueHigh) {
        this.valueHigh = valueHigh;
    }

    public Double getValueLow() {
        return valueLow;
    }

    public void setValueLow(Double valueLow) {
        this.valueLow = valueLow;
    }
}
