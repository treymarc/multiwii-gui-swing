package eu.kprod.msp;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeListener;

import eu.kprod.ds.MwDataModel;
import eu.kprod.ds.MwDataSource;
import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.ds.MwSensorClassCompas;
import eu.kprod.ds.MwSensorClassHUD;
import eu.kprod.ds.MwSensorClassIMU;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassPower;
import eu.kprod.ds.MwSensorClassRC;
import eu.kprod.ds.MwSensorClassServo;

/**
 * Multiwii Serial Protocol
 * 
 * @author treym
 *
 */
public final class MSP {

    private static final int BUFFER_SIZE = 128;
    /**
     * position in the reception inputBuffer
     */
    private static int bufferIndex;


    private static byte checksum = 0;
    private static int cmd = 0;

    public final static int DYNTHRPID_KEY = 6;
    /* processing does not accept enums? */
    public static final int
    ERR = -1,
    IDLE = 0,
    HEADER_START = 1,
    HEADER_M = 2,
    HEADER_ARROW = 3,
    HEADER_SIZE = 4,
    HEADER_CMD = 5,
    HEADER_PAYLOAD = 6,
    HEADER_CHK = 6
    ;
    public static final String IDALT = "alt";

    public static final String IDANGX = "angx";

    public static final String IDANGY = "angy";



    public static final String IDAX = "ax";

    public static final String IDAY = "az";
    public static final String IDAZ = "ay";
    public static final int
    IDENT               = 100,
    STATUS               = 101,
    RAW_IMU              = 102,
    SERVO                = 103,
    MOTOR                = 104,
    RC                   = 105,
    RAW_GPS              = 106,
    COMP_GPS             = 107,
    ATTITUDE             = 108,
    ALTITUDE             = 109,
    BAT                  = 110,
    RC_TUNING            = 111,
    PID                  = 112,
    BOX                  = 113,
    MISC                 = 114,
    MOTOR_PINS           = 115,
    BOXNAMES             = 116,
    PIDNAMES             = 117,

    SET_RAW_RC           = 200,
    SET_RAW_GPS          = 201,
    SET_PID              = 202,
    SET_BOX              = 203,
    SET_RC_TUNING        = 204,
    ACC_CALIBRATION      = 205,
    MAG_CALIBRATION      = 206,
    SET_MISC             = 207,
    RESET_CONF           = 208,

    EEPROM_WRITE         = 250,

    DEBUG                = 254;
    public static final String IDGX = "gx";
    public static final String IDGY = "gy";
    public static final String IDGZ = "gz";

    public static final String IDHEAD = "head";
    public static final String IDMAGX = "magx";
    public static final String IDMAGY = "magy";
    public static final String IDMAGZ = "magz";
    public static final String IDPOWERMETERSUM = "powerMeterSum";
    public static final String IDRCAUX1 = "aux1";
    public static final String IDRCAUX2 = "aux2";
    public static final String IDRCAUX3 = "aux3";
    public static final String IDRCAUX4 = "aux4";
    public static final String IDRCPITCH = "pitch";
    public static final String IDRCROLL = "roll";
    public static final String IDRCTHROTTLE = "throttle";
    public static final String IDRCYAW = "yaw";
    public static final String IDVBAT = "vbat";
    private static final int MASK = 0xff;
    /**
     *  the model for holding the value decoded by the MSP
     */
    private static  MwDataModel model=new MwDataModel();
    private static final Character MSP_HEAD1 = new Character('$');

    private static final Character MSP_HEAD2 = new Character('M');

    private static final Character MSP_HEAD3 = new Character('>');

    private static int offset = 0, dataSize = 0, mspState = IDLE;

    public static final String
    OUT   = "$M<";

    public final static int POEWERTRIG_KEY = 9;

    public final static int RCCURV_THREXPO_KEY = 8;
    public final static int RCCURV_THRMID_KEY = 7;

    public final static int RCEXPO_KEY = 3;

    public final static int RCRATE_KEY = 2;
    public final static int ROLLPITCHRATE_KEY = 4;
    /**
     * reception buffer
     */
    private static byte[] serialBuffer = new byte[BUFFER_SIZE];
    public final static int UAVTYPEKEY = 1;
    public final static int VERSIONKEY = 0;
    public final static int YAWRATE_KEY = 5;
    /**
     * Decode the byte
     * 
     * @param input
     */
    synchronized public static void decode(final byte input) {
        final char c = (char) input;
        if (mspState == IDLE) {
            mspState = (MSP_HEAD1.equals(c)) ? HEADER_START : IDLE;
        } else if (mspState == HEADER_START) {
            mspState = (MSP_HEAD2.equals(c)) ? HEADER_M : IDLE;
        } else if (mspState == HEADER_M) {
            mspState = (MSP_HEAD3.equals(c)) ? HEADER_ARROW : IDLE;
        } else if (mspState == HEADER_ARROW) {
            /* now we are expecting the payload size */
            dataSize = (c & MASK);
            /* reset index variables */
            bufferIndex = 0;
            offset = 0;
            checksum = 0;
            checksum ^= (c & MASK);
            /* the command is to follow */
            mspState = HEADER_SIZE;
        } else if (mspState == HEADER_SIZE) {
            cmd = (c & MASK);
            checksum ^= (c & MASK);
            mspState = HEADER_CMD;
        } else if (mspState == HEADER_CMD ) {

            if (offset < dataSize) {
                // we keep reading the payload
                checksum ^= (c & MASK);
                serialBuffer[offset++] = (byte) (c & MASK);
            } else {
                if ((checksum & MASK) != (c & MASK)) {
                    System.err.println("invalid checksum for command "
                            + (cmd & MASK) + ": " + (checksum & MASK)
                            + " expected, got " + (c & MASK));
                    cmd = ERR;
                }

                decodeMSPCommande(cmd, dataSize);
                mspState = IDLE;
            }

        }

    }
    synchronized static private void decodeMSPCommande(final int stateMSP,
            final int dataSize2) {
        final Date d = new Date();
        switch (stateMSP) {
            case IDENT:
                model.put(MSP.VERSIONKEY, read8());
                model.put(MSP.UAVTYPEKEY, read8());
                break;
            case STATUS:

                // cycleTime = read16();
                // i2cError = read16();
                // present = read16();
                // mode = read16();
                // if ((present&1) >0) {buttonAcc.setColorBackground(green_);}
                // else
                // {buttonAcc.setColorBackground(red_);tACC_ROLL.setState(false);
                // tACC_PITCH.setState(false); tACC_Z.setState(false);}
                // if ((present&2) >0) {buttonBaro.setColorBackground(green_);}
                // else {buttonBaro.setColorBackground(red_);
                // tBARO.setState(false); }
                // if ((present&4) >0) {buttonMag.setColorBackground(green_);}
                // else {buttonMag.setColorBackground(red_);
                // tMAGX.setState(false); tMAGY.setState(false);
                // tMAGZ.setState(false); }
                // if ((present&8) >0) {buttonGPS.setColorBackground(green_);}
                // else {buttonGPS.setColorBackground(red_);
                // tHEAD.setState(false);}
                // if ((present&16)>0) {buttonSonar.setColorBackground(green_);}
                // else {buttonSonar.setColorBackground(red_);}
                // for(i=0;i<CHECKBOXITEMS;i++) {
                // if ((mode&(1<<i))>0)
                // buttonCheckbox[i].setColorBackground(green_); else
                // buttonCheckbox[i].setColorBackground(red_);
                // }
                break;
            case RAW_IMU:
                model.getRealTimeData().put(d, IDAX, Double.valueOf(read16()),
                        MwSensorClassIMU.class);
                model.getRealTimeData().put(d, IDAY, Double.valueOf(read16()),
                        MwSensorClassIMU.class);
                model.getRealTimeData().put(d, IDAZ, Double.valueOf(read16()),
                        MwSensorClassIMU.class);

                model.getRealTimeData().put(d, IDGX,
                        Double.valueOf(read16() / 8), MwSensorClassIMU.class);
                model.getRealTimeData().put(d, IDGY,
                        Double.valueOf(read16() / 8), MwSensorClassIMU.class);
                model.getRealTimeData().put(d, IDGZ,
                        Double.valueOf(read16() / 8), MwSensorClassIMU.class);

                model.getRealTimeData().put(d, IDMAGX,
                        Double.valueOf(read16() / 3), MwSensorClassIMU.class);
                model.getRealTimeData().put(d, IDMAGY,
                        Double.valueOf(read16() / 3), MwSensorClassIMU.class);
                model.getRealTimeData().put(d, IDMAGZ,
                        Double.valueOf(read16() / 3), MwSensorClassIMU.class);
                break;
            case SERVO:
                for (int i = 0; i < 8; i++) {
                    model.getRealTimeData().put(
                            d,
                            new StringBuffer().append("servo").append(i)
                            .toString(), Double.valueOf(read16()),
                            MwSensorClassServo.class);
                }
                break;
            case MOTOR:
                for (int i = 0; i < 8; i++) {
                    model.getRealTimeData().put(
                            d,
                            new StringBuffer().append("mot").append(i)
                            .toString(), Double.valueOf(read16()),
                            MwSensorClassMotor.class);
                }
                break;
            case RC:

                model.getRealTimeData().put(d, IDRCROLL,
                        Double.valueOf(read16()), MwSensorClassRC.class);
                model.getRealTimeData().put(d, IDRCPITCH,
                        Double.valueOf(read16()), MwSensorClassRC.class);
                model.getRealTimeData().put(d, IDRCYAW, Double.valueOf(read16()),
                        MwSensorClassRC.class);
                model.getRealTimeData().put(d, IDRCTHROTTLE,
                        Double.valueOf(read16()), MwSensorClassRC.class);
                model.getRealTimeData().put(d, IDRCAUX1,
                        Double.valueOf(read16()), MwSensorClassRC.class);
                model.getRealTimeData().put(d, IDRCAUX2,
                        Double.valueOf(read16()), MwSensorClassRC.class);
                model.getRealTimeData().put(d, IDRCAUX3,
                        Double.valueOf(read16()), MwSensorClassRC.class);
                model.getRealTimeData().put(d, IDRCAUX4,
                        Double.valueOf(read16()), MwSensorClassRC.class);

                break;
            case RAW_GPS:
                // GPS_fix = read8();
                // GPS_numSat = read8();
                // GPS_latitude = read32();
                // GPS_longitude = read32();
                // GPS_altitude = read16();
                // GPS_speed = read16();
                break;
            case COMP_GPS:
                // GPS_distanceToHome = read16();
                // GPS_directionToHome = read16();
                // GPS_update = read8();
                break;
            case ATTITUDE:
                model.getRealTimeData().put(d, IDANGX,
                        Double.valueOf(read16() / 10), MwSensorClassHUD.class);
                model.getRealTimeData().put(d, IDANGY,
                        Double.valueOf(read16() / 10), MwSensorClassHUD.class);
                model.getRealTimeData().put(d, IDHEAD,
                        Double.valueOf(read16()), MwSensorClassCompas.class);
                break;
            case ALTITUDE:
                model.getRealTimeData().put(d, IDALT,
                        Double.valueOf(read32()) / 100,
                        MwSensorClassCompas.class);
                break;
            case BAT: // TODO SEND
                model.getRealTimeData().put(d, IDVBAT, Double.valueOf(read8()),
                        MwSensorClassPower.class);
                model.getRealTimeData().put(d, IDPOWERMETERSUM,
                        Double.valueOf(read16()), MwSensorClassPower.class);
                break;
            case RC_TUNING:
                model.put(MSP.RCRATE_KEY, (int) (read8() / 100.0));
                model.put(MSP.RCEXPO_KEY, (int) (read8() / 100.0));
                model.put(MSP.ROLLPITCHRATE_KEY, (int) (read8() / 100.0));
                model.put(MSP.YAWRATE_KEY, (int) (read8() / 100.0));
                model.put(MSP.DYNTHRPID_KEY, (int) (read8() / 100.0));
                model.put(MSP.RCCURV_THRMID_KEY, (int) (read8() / 100.0));
                model.put(MSP.RCCURV_THREXPO_KEY, (int) (read8() / 100.0));
                break;
            case ACC_CALIBRATION:
                break;
            case MAG_CALIBRATION:
                break;
            case PID:
                for (int index = 0; index < model.getPidNameCount(); index++) {
                    model.setPidValue(index, read8(), read8(), read8());
                }
                model.pidChanged();
                break;
            case BOX:
                for (int index = 0; index < model.getBoxNameCount(); index++) {
                    final int bytread = read16();
                    model.setBoxNameValue(index, bytread);
                }
                model.boxChanged();
                break;
            case MISC: // TODO SEND
                model.put(MSP.POEWERTRIG_KEY, read16());
                break;
            case MOTOR_PINS:// TODO SEND
                for (int i = 0; i < 8; i++) {
                    model.setMotorPin(i, read8());
                }
                break;
            case DEBUG:// TODO SEND
                for (int i = 1; i < 5; i++) {
                    model.getRealTimeData().put(d, "debug" + i,
                            Double.valueOf(read16()), MwSensorClassIMU.class);
                }

                break;
            case BOXNAMES:
                model.removeAllBoxName();
                int i = 0;
                for (final String name : new String(serialBuffer, 0, dataSize).split(";")) {
                    model.addBoxName(name, i++);
                }
                break;
            case PIDNAMES:
                model.removeAllPIDName();
                i = 0;
                for (final String name : new String(serialBuffer, 0, dataSize).split(";")) {
                    model.addPIDName(name, i++);
                }
                break;
        }

    }
    public static MwDataSource getRealTimeData() {
        return model.getRealTimeData();
    }
    /**
     * read 16byte from the inputBuffer
     */
    public static synchronized int read16() {
        return (serialBuffer[bufferIndex++] & MASK) + ((serialBuffer[bufferIndex++]) << 8);
    }


    //    private static final int ERR = 0;

    /**
     * read 32byte from the inputBuffer
     */

    synchronized public static int read32() {
        return (serialBuffer[bufferIndex++] & MASK) + ((serialBuffer[bufferIndex++] & MASK) << 8)
                + ((serialBuffer[bufferIndex++] & MASK) << 16)
                + ((serialBuffer[bufferIndex++] & MASK) << 24);
    }

    /**
     * read 8byte from the inputBuffer
     */
    public static synchronized int read8() {
        return serialBuffer[bufferIndex++] & MASK;
    }

    // send msp without payload
    public static List<Byte> request(int msp) {
        return request(msp, null);
    }

    // send msp with payload
    public static List<Byte> request(int msp, Character[] payload) {

        if (msp < 0) {
            return null;
        }
        final List<Byte> bf = new LinkedList<Byte>();
        for (final byte c : OUT.getBytes()) {
            bf.add(c);
        }

        byte hash = 0;
        final byte pl_size = (byte) ((payload != null ? (payload.length) : 0) & MASK);
        bf.add(pl_size);
        hash ^= (pl_size & MASK);

        bf.add((byte) (msp & MASK));
        hash ^= (msp & MASK);

        if (payload != null) {
            for (final char c : payload) {
                bf.add((byte) (c & MASK));
                hash ^= (c & MASK);
            }
        }

        bf.add(hash);
        return (bf);
    }

    // send multiple msp without payload
    public static List<Byte> request(int[] msps) {
        final List<Byte> s = new LinkedList<Byte>();
        for (final int m : msps) {
            s.addAll(request(m, null));
        }
        return s;
    }

    public static void setBoxChangeListener(final ChangeListener boxPane) {
        model.setBoxChangeListener(boxPane);

    }

    public static void setPidChangeListener(final ChangeListener pidPane) {
        model.setPidChangeListener(pidPane);
    }

    public static void setUavChangeListener(
            final MwDataSourceListener uavChangeListener) {
        model.setUavChangeListener(uavChangeListener);
    }

    private MSP(){

    }
}
