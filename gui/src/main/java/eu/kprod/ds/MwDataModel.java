package eu.kprod.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MwDataModel {

    private static final int ITEM_PID_COUNT = 3;

    private static final int ITEM_BOX_COUNT = 12;

    // for real time data
    // TODO get impl
    private MwDataSource ds = new MwDataSourceImpl();

    // ------ TODO
    // global ident
    int version = -1, multiType = -1;

    // // rc conf
    // int rcRate, rcExpo, rollPitchRate, yawRate, dynThrPID, throttleMID,
    // throttleEXPO;

    // ------

    private HashMap<Integer, Integer> uavSettings = new HashMap<Integer, Integer>();

    private Map<Integer, String> boxNameIndex = new HashMap<Integer, String>();

    public Map<Integer, String> getBoxNameIndex() {
        return boxNameIndex;
    }

    public Map<Integer, String> getPidNameIndex() {
        return pidNameIndex;
    }

    private Map<Integer, String> pidNameIndex = new HashMap<Integer, String>();

    private Map<String, List<Boolean>> boxs = new HashMap<String, List<Boolean>>();

    private Map<String, List<Double>> pids = new HashMap<String, List<Double>>();

    private ChangeListener boxChangeListener;

    public ChangeListener getBoxChangeListener() {
        return boxChangeListener;
    }

    public void setBoxChangeListener(ChangeListener boxChangeListener1) {
        this.boxChangeListener = boxChangeListener1;
    }

    public ChangeListener getPidChangeListener() {
        return pidChangeListener;
    }

    public void setPidChangeListener(ChangeListener pidChangeListener1) {
        this.pidChangeListener = pidChangeListener1;
    }

    private ChangeListener pidChangeListener;

    // private int powerTrigger;

    private int[] motorPins = new int[8];

    private MwDataSourceListener uavChangeListener;

   

    public void setUavChangeListener(MwDataSourceListener uavChangeListener) {
        this.uavChangeListener = uavChangeListener;
    }

    public MwDataModel() {
        super();
    }

    synchronized public MwDataSource getRealTimeData() {
        return ds;
    }

    public void addBoxName(String name, int i) {
        this.boxs.put(name, null);
        this.boxNameIndex.put(i, name);
    }

    public void removeAllBoxName() {
        this.boxs.clear();
        this.boxNameIndex.clear();
    }

    public void removeAllPIDName() {
        // TODO Auto-generated method stub
        this.pids.clear();
        this.pidNameIndex.clear();
    }

    public void addPIDName(String name, int i) {
        // TODO Auto-generated method stub
        this.pids.put(name, null);
        this.pidNameIndex.put(i, name);
    }

    public int getBoxNameCount() {
        if (boxNameIndex == null) {
            return 0;
        } else {
            return boxNameIndex.size();
        }

    }

    public int getPidNameCount() {
        if (pidNameIndex == null) {
            return 0;
        } else {
            return pidNameIndex.size();
        }
    }

    public void setBoxNameValue(int index, int bytread) {
        // TODO Auto-generated method stub
        List<Boolean> boxItem = new ArrayList<Boolean>(ITEM_BOX_COUNT);
        for (int i = 0; i < ITEM_BOX_COUNT; i++) {

            boxItem.add(i, ((bytread & (1 << i)) > 0));

        }
        boxs.put(boxNameIndex.get(index), boxItem);

    }

    public void setPidValue(int index, int p, int i, int d) {
        // TODO Auto-generated method stub
        List<Double> pidItem = new ArrayList<Double>(ITEM_PID_COUNT);
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
            }

            pidItem.add(f++, fp);
            pidItem.add(f++, fi);
            pidItem.add(f++, fd);

        }
        pids.put(pidNameIndex.get(index), pidItem);
    }

    public void pidChanged() {
        // TODO Auto-generated method stub
        if (pidChangeListener != null) {
            pidChangeListener.stateChanged(new ChangeEvent(this));
        }
    }

    public void boxChanged() {
        // TODO Auto-generated method stub
        if (boxChangeListener != null) {
            boxChangeListener.stateChanged(new ChangeEvent(this));
        }
    }

    public Map<String, List<Double>> getPIDs() {
        // TODO Auto-generated method stub
        return pids;
    }

    public Map<String, List<Boolean>> getBOXs() {
        // TODO Auto-generated method stub
        return boxs;
    }

    // public void setPowerTrigger(int read16) {
    // powerTrigger = read16;
    //
    // }

    /**
     * 
     * @param i
     *            the motor number
     * @param read8
     *            the number of the pin
     */
    public void setMotorPin(int i, int read8) {
        // TODO Auto-generated method stub
        motorPins[i] = read8;

    }

    public void put(Integer string, int i) {
        // TODO Auto-generated method stub
        uavChangeListener.readNewValue(string, i);
    }
}
