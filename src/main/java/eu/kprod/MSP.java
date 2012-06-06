package eu.kprod;

import java.util.Date;

import eu.kprod.ds.MwSensorClassIMU;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassServo;

/**
 * Multiwii Serial Protocol
 * 
 * @author treym
 *
 */
public class MSP {

    /**
     *  the model for holding the value decoded by the MSP
     */
    private static MwDataModel model;

    public static MwDataModel getModel() {
        return model;
    }

    public static void setModel(final MwDataModel model1) {
        MSP.model = model1;
    }

    private static final int MASK = 0xff;
    private static final int BUFFERSize = 128;
    
    public static final String
    OUT   = "$M<",
    IN    = "$M>";

    public static final int
    IDENT                = 100,
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


    /**
     * position in the reception inputBuffer
     */
    private static int p;

    /**
     * reception buffer
     */
    private static byte[] inBuf = new byte[BUFFERSize ];
    /**
     * read 32byte from the inputBuffer
     */
    public static synchronized int read32() {
        return (inBuf[p++] & MASK) + ((inBuf[p++] & MASK) << 8)
                + ((inBuf[p++] & MASK) << 16) + ((inBuf[p++] & MASK) << 24);
    }

    /**
     * read 16byte from the inputBuffer
     */
    synchronized public static int read16() {
        return (inBuf[p++] & MASK) + ((inBuf[p++]) << 8);
    }

    /**
     * read 8byte from the inputBuffer
     */
    synchronized public static int read8() {
        return inBuf[p++] & MASK;
    }

    private static byte checksum = 0;
    private static int stateMSP = 0, 
            offset = 0, dataSize = 0;


    /**
     * Decode the byte 
     * @param input
     */
    synchronized public static void decode(final byte input) {
        char c = (char) input;

        if (stateMSP > 99) {
            if (offset <= dataSize) {
                if (offset < dataSize)
                    checksum ^= c;
                inBuf[offset++] = (byte) (c);
            } else {
                if (checksum == inBuf[dataSize]) {
                    decodeInBuf();
                }
                stateMSP = 0;
            }
        }

        if (stateMSP < 5) {

            if (stateMSP == 4) {
                if (c > 99) {
                    stateMSP = c;
                    offset = 0;
                    checksum = 0;
                    p = 0;
                } else {
                    stateMSP = 0;
                }
            }
            // with/without payload ?
            if (stateMSP == 3) {
                if (c < 100) {
                    stateMSP++;
                    dataSize = c;
                    if (dataSize > 63)
                        dataSize = 63;
                } else {
                    stateMSP = (int) c;
                }
            }

            // header detection $M>
            switch (c) {
                case '$':
                    if (stateMSP == 0)
                        stateMSP++;
                    break;
                case 'M':
                    if (stateMSP == 1)
                        stateMSP++;
                    break;
                case '>':
                    if (stateMSP == 2)
                        stateMSP++;
                    break;
            }

        }

    }

    synchronized private static void decodeInBuf() {
        final Date d = new Date();
        switch (stateMSP) {
            case IDENT:
                getModel().setVersion(read8());
                getModel().setMultiType(read8());
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
                // else
                // {buttonBaro.setColorBackground(red_); tBARO.setState(false);
                // }
                // if ((present&4) >0) {buttonMag.setColorBackground(green_);}
                // else
                // {buttonMag.setColorBackground(red_); tMAGX.setState(false);
                // tMAGY.setState(false); tMAGZ.setState(false); }
                // if ((present&8) >0) {buttonGPS.setColorBackground(green_);}
                // else
                // {buttonGPS.setColorBackground(red_); tHEAD.setState(false);}
                // if ((present&16)>0) {buttonSonar.setColorBackground(green_);}
                // else {buttonSonar.setColorBackground(red_);}
                // for(i=0;i<CHECKBOXITEMS;i++) {
                // if ((mode&(1<<i))>0)
                // buttonCheckbox[i].setColorBackground(green_); else
                // buttonCheckbox[i].setColorBackground(red_);
                // }
                break;
            case RAW_IMU:
                getModel().getDs().put(d, "ax", Double.valueOf(read16()),MwSensorClassIMU.class);
                getModel().getDs().put(d, "ay", Double.valueOf(read16()),MwSensorClassIMU.class);
                getModel().getDs().put(d, "az", Double.valueOf(read16()),MwSensorClassIMU.class);

                getModel().getDs().put(d, "gx", Double.valueOf(read16() / 8),MwSensorClassIMU.class);
                getModel().getDs().put(d, "gy", Double.valueOf(read16() / 8),MwSensorClassIMU.class);
                getModel().getDs().put(d, "gz", Double.valueOf(read16() / 8),MwSensorClassIMU.class);

                getModel().getDs().put(d, "magx", Double.valueOf(read16() / 3),MwSensorClassIMU.class);
                getModel().getDs().put(d, "magy", Double.valueOf(read16() / 3),MwSensorClassIMU.class);
                getModel().getDs().put(d, "magz", Double.valueOf(read16() / 3),MwSensorClassIMU.class);
                break;
            case SERVO:
                // for(i=0;i<8;i++) servo[i] = read16();
                for(int i=0;i<8;i++){
                    getModel().getDs().put(d, new StringBuffer().append("servo").append(i).toString(), 
                            Double.valueOf(read16()),
                            MwSensorClassServo.class);
                }
                break;
            case MOTOR:
                // for(i=0;i<8;i++) mot[i] = read16();
                for(int i=0;i<8;i++){
                    getModel().getDs().put(d, new StringBuffer().append("mot").append(i).toString(),
                            Double.valueOf(read16()),
                            MwSensorClassMotor.class);
                }
                break;
            case RC:
                // rcRoll = read16();rcPitch = read16();rcYaw =
                // read16();rcThrottle
                // = read16();
                // rcAUX1 = read16();rcAUX2 = read16();rcAUX3 = read16();rcAUX4
                // =
                // read16();
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
                // angx = read16()/10;angy = read16()/10;
                // head = read16();
                break;
            case ALTITUDE:
                // alt = read32();
                break;
            case BAT:
                // bytevbat = read8();
                // pMeterSum = read16();

                break;
            case RC_TUNING:
                getModel().setRCRATE((int) (read8() / 100.0));
                getModel().setRCEXPO((int) (read8() / 100.0));
                getModel().setRollPitchRate((int) (read8() / 100.0));
                getModel().setYawRate((int) (read8() / 100.0));
                getModel().setDynThrPID((int) (read8() / 100.0));
                getModel().setThrottleMID((int) (read8() / 100.0));
                getModel().setThrottleEXPO((int) (read8() / 100.0));
                break;
            case ACC_CALIBRATION:
                break;
            case MAG_CALIBRATION:
                break;
            case PID:

                // for(i=0;i<PIDITEMS;i++) {
                // byteP[i] = read8();byteI[i] = read8();byteD[i] = read8();
                // switch (i) {
                // case 0:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
                // break;
                // case 1:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
                // break;
                // case 2:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
                // break;
                // case 3:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
                // break;
                // case 7:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
                // break;
                // case 8:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
                // break;
                // //Different rates fot POS-4 POSR-5 NAVR-6
                // case 4:
                // confP[i].setValue(byteP[i]/100.0);confI[i].setValue(byteI[i]/100.0);confD[i].setValue(byteD[i]/1000.0);
                // break;
                // case 5:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/100.0);confD[i].setValue(byteD[i]/1000.0);
                // break;
                // case 6:
                // confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/100.0);confD[i].setValue(byteD[i]/1000.0);
                // break;
                // }
                // confP[i].setColorBackground(green_);
                // confI[i].setColorBackground(green_);
                // confD[i].setColorBackground(green_);
                // }
                //
                break;
            case BOX:
                // for( i=0;i<CHECKBOXITEMS;i++) {
                // activation[i] = read16();
                // for( aa=0;aa<12;aa++) {
                // if ((activation[i]&(1<<aa))>0) checkbox[i].activate(aa); else
                // checkbox[i].deactivate(aa);
                // }
                // }
                break;
            case MISC:
                // intPowerTrigger = read16();
                break;
            case MOTOR_PINS:
                // for( i=0;i<8;i++) {
                // byteMP[i] = read8();
                // }
                break;
            case DEBUG:
                // debug1 = read16();debug2 = read16();debug3 = read16();debug4
                // =
                // read16();
                break;
        }

    }

    // create msp request without payload
    public static String request(final int msp) {
        return request(msp, null);
    }

    // create multiple msp requests without payload
    public static String request(int[] msps) {
        StringBuffer bf = new StringBuffer();
        for (int m : msps) {
            bf.append(MSP.OUT).append((char) (m));
        }
        return (bf.toString());
    }

    // create msp request with payload
    public static String request(final int msp, final Character[] payload) {
        if (msp < 0) {
            return null;
        }
        StringBuffer bf = new StringBuffer().append(MSP.OUT);

        if (payload != null) {
            bf.append((char) (payload.length)).append((char) (msp));
            byte check = 0;
            for (char p : payload) {
                bf.append(p);
                check ^= (int) (p);
            }
            bf.append((char) ((int) (check)));
        } else {
            bf.append((char) (msp));
        }

        return (bf.toString());
    }
}
