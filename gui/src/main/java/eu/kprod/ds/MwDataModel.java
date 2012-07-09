/**
 * Copyright (C) 2012 @author treym (Trey Marc)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.kprod.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MwDataModel {

    private static final int MAX_BOX_COUNT = 20;

    private static final int ITEM_BOX_COUNT = 12;
    private static final int ITEM_PID_COUNT = 3;

    private ChangeListener pidChangeListener;
    private ChangeListener boxChangeListener;

    private final Map<String, List<Boolean>> boxs = new HashMap<String, List<Boolean>>();
    private final Map<Integer, String> boxNameIndex = new HashMap<Integer, String>();
    private static boolean[] boxsState = new boolean[MAX_BOX_COUNT];

    // for real time data
    // TODO get impl
    private final MwDataSource ds = new MwDataSourceImpl();

    private final int[] motorPins = new int[8];

    private final Map<Integer, String> pidNameIndex = new HashMap<Integer, String>();

    private final Map<String, List<Double>> pids = new HashMap<String, List<Double>>();

    private MwDataSourceListener uavChangeListener;

    // ------ TODO
    // global ident
    private final int version = -1, multiType = -1;

    public MwDataModel() {
        super();
    }

    public final void addBoxName(final String name, final int i) {
        this.boxs.put(name, null);
        this.boxNameIndex.put(i, name);
    }

    public final void addPIDName(final String name, final int i) {
        // TODO Auto-generated method stub
        this.pids.put(name, null);
        this.pidNameIndex.put(i, name);
    }

    public final void boxChanged() {
        // TODO Auto-generated method stub
        if (boxChangeListener != null) {
            boxChangeListener.stateChanged(new ChangeEvent(this));
        }
    }

    // private int powerTrigger;

    public final ChangeListener getBoxChangeListener() {
        return boxChangeListener;
    }

    public final int getBoxNameCount() {
        if (boxNameIndex == null) {
            return 0;
        } else {
            return boxNameIndex.size();
        }

    }

    public final Map<Integer, String> getBoxNameIndex() {
        return boxNameIndex;
    }

    public final Map<String, List<Boolean>> getBOXs() {
        // TODO Auto-generated method stub
        return boxs;
    }

    public final ChangeListener getPidChangeListener() {
        return pidChangeListener;
    }

    public final int getPidNameCount() {
        if (pidNameIndex == null) {
            return 0;
        } else {
            return pidNameIndex.size();
        }
    }

    public final Map<Integer, String> getPidNameIndex() {
        return pidNameIndex;
    }

    public final Map<String, List<Double>> getPIDs() {
        // TODO Auto-generated method stub
        return pids;
    }

    public final MwDataSource getRealTimeData() {
        return ds;
    }

    public final void pidChanged() {
        // TODO Auto-generated method stub
        if (pidChangeListener != null) {
            pidChangeListener.stateChanged(new ChangeEvent(this));
        }
    }

    public final void put(final Integer string, final int i) {
        // TODO Auto-generated method stub
        uavChangeListener.readNewValue(string, i);
    }

    public final void removeAllBoxName() {
        this.boxs.clear();
        this.boxNameIndex.clear();
    }

    public final void removeAllPIDName() {
        // TODO Auto-generated method stub
        this.pids.clear();
        this.pidNameIndex.clear();
    }

    public final void setBoxChangeListener(
            final ChangeListener boxChangeListener1) {
        this.boxChangeListener = boxChangeListener1;
    }

    public final void setBoxNameValue(final int index, final int bytread) {
        // TODO Auto-generated method stub
        final List<Boolean> boxItem = new ArrayList<Boolean>(ITEM_BOX_COUNT);
        for (int i = 0; i < ITEM_BOX_COUNT; i++) {

            boxItem.add(i, ((bytread & (1 << i)) > 0));

        }
        boxs.put(boxNameIndex.get(index), boxItem);
    }

    /**
     * @param i
     *            the motor number
     * @param read8
     *            the number of the pin
     */
    public final void setMotorPin(final int i, final int read8) {
        // TODO Auto-generated method stub
        motorPins[i] = read8;

    }

    public final void setPidChangeListener(
            final ChangeListener pidChangeListener1) {
        this.pidChangeListener = pidChangeListener1;
    }

    public final void setPidValue(final int index, final int p, final int i,
            final int d) {
        final List<Double> pidItem = new ArrayList<Double>(ITEM_PID_COUNT);
        for (int f = 0; f < ITEM_PID_COUNT; f++) {
            double fp = 0, fd = 0, fi = 0;
            switch (index) {
                case 0:
                    fp = p / 10.0;
                    fi = i / 1000.0;
                    fd = d;
                    break;
                case 1:
                    fp = p / 10.0;
                    fi = i / 1000.0;
                    fd = d;
                    break;
                case 2:
                    fp = p / 10.0;
                    fi = i / 1000.0;
                    fd = d;
                    break;
                case 3:
                    fp = p / 10.0;
                    fi = i / 1000.0;
                    fd = d;
                    break;
                case 7:
                    fp = p / 10.0;
                    fi = i / 1000.0;
                    fd = d;
                    break;
                case 8:
                    fp = p / 10.0;
                    fi = i / 1000.0;
                    fd = d;
                    break;
                case 9:
                    fp = p / 10.0;
                    fi = i / 1000.0;
                    fd = d;
                    break;
                // Different rates fot POS-4 POSR-5 NAVR-6
                case 4:
                    fp = p / 100.0;
                    fi = i / 100.0;
                    fd = d / 1000.0;
                    break;
                case 5:
                    fp = p / 10.0;
                    fi = i / 100.0;
                    fd = d / 1000.0;
                    break;
                case 6:
                    fp = p / 10.0;
                    fi = i / 100.0;
                    fd = d / 1000.0;
                    break;
                default:
                    break;
            }

            pidItem.add(f++, fp);
            pidItem.add(f++, fi);
            pidItem.add(f++, fd);

        }
        pids.put(pidNameIndex.get(index), pidItem);
    }

    public final void setUavChangeListener(
            final MwDataSourceListener uavChangeListener1) {
        this.uavChangeListener = uavChangeListener1;
    }

    public boolean getBoxNameState(int index) {
        return boxsState[index];
    }

    public void setBoxNameState(int index, boolean b) {
        boxsState[index] = b;
    }

    public boolean[] getBoxNameState() {
        // TODO Auto-generated method stub
        return boxsState;
    }
}
