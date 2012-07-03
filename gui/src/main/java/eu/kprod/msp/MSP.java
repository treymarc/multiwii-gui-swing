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
package eu.kprod.msp;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

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


    public static final int UAVVERSION_KEY = 0;
    public static final int UAVTYPE_KEY = 1;
    public static final int RCRATE_KEY = 2;
    public static final int RCEXPO_KEY = 3;
    public static final int ROLLPITCHRATE_KEY = 4;
    public static final int YAWRATE_KEY = 5;
    public static final int MSPVERSION_KEY = 6;
    public static final int UAVCAPABILITY_KEY = 7;
    public static final int RCCURV_THRMID_KEY = 8;
    public static final int RCCURV_THREXPO_KEY = 9;
    public static final int POEWERTRIG_KEY = 10;
    public static final int DYNTHRPID_KEY = 11;
    
    public static final String IDALT = "alt";
    public static final String IDANGX = "angx";
    public static final String IDANGY = "angy";
    public static final String IDAX = "ax";
    public static final String IDAY = "az";
    public static final String IDAZ = "ay";
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
    
    private static final int MAXSERVO = 8;
    private static final int MAXMOTOR = 8;

    
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
    
   
    
    private static final Logger LOGGER = Logger.getLogger(MSP.class);
    private static final int MASK = 0xff;
    /**
     *  the model for holding the value decoded by the MSP
     */
    private static  MwDataModel model=new MwDataModel();
    private static final Character MSP_HEAD1 = new Character('$');

    private static final Character MSP_HEAD2 = new Character('M');

    private static final Character MSP_HEAD3 = new Character('>');
 
    private static final byte[] OUT   = { '$', 'M', '<' };


    private static final int BUFFER_SIZE = 128;

    /**
     * position in the reception inputBuffer
     */
    private static int bufferIndex;
    private static byte checksum = 0;
    private static int cmd = 0;


    /**
     * MSP decoder state
     * 
     */
    private static final int
    
    STATE_ERR = -1,
    STATE_IDLE = 0,
    STATE_START = 1,
    STATE_HEADER_BEGIN = 2,
    STATE_HEADER_END = 3,
    STATE_SIZE = 4,
    STATE_CMD = 5;
//    STATE_PAYLOAD = 6,
//    STATE_CHECKSUM = 6;
    
    private static int offset = 0, dataSize = 0, mspState = STATE_IDLE;

    /**
     * reception buffer
     */
    private static byte[] serialBuffer = new byte[BUFFER_SIZE];
   

  
    /**
     * Decode the byte
     *
     * @param input is a an int with the upper 24 bits set to zero and thusly
     *   this contains only 8 bits of information.
     */
    synchronized public static void decode(int input) {
        if (mspState == STATE_IDLE) {
            mspState = (MSP_HEAD1 == input) ? STATE_START : STATE_IDLE;
        } else if (mspState == STATE_START) {
            mspState = (MSP_HEAD2 == input) ? STATE_HEADER_BEGIN : STATE_IDLE;
        } else if (mspState == STATE_HEADER_BEGIN) {
            mspState = (MSP_HEAD3 == input) ? STATE_HEADER_END : STATE_IDLE;
        } else if (mspState == STATE_HEADER_END) {
            /* now we are expecting the payload size */
            dataSize = input;
            /* reset index variables */
            bufferIndex = 0;
            offset = 0;
            checksum = 0;
            checksum ^= input;
            /* the command is to follow */
            mspState = STATE_SIZE;
        } else if (mspState == STATE_SIZE) {
            cmd = input;
            checksum ^= input;
            mspState = STATE_CMD;
        } else if (mspState == STATE_CMD ) {

            if (offset < dataSize) {
                // we keep reading the payload
                checksum ^= input;
                serialBuffer[offset++] = (byte) input;
            } else {
                if ((checksum & MASK) != input) {
                    LOGGER.error("invalid checksum for command "
                            + cmd + ": " + (checksum & MASK)
                            + " expected, got " + input);
                    cmd = STATE_ERR;
                }

//                decodeMSPCommande(cmd, dataSize);
                decodeMSPCommande(cmd);
                mspState = STATE_IDLE;
            }
        }
    }

    synchronized static private void decodeMSPCommande(final int stateMSP) {
        final Date d = new Date();
        switch (stateMSP) {
            case IDENT:
                model.put(MSP.UAVVERSION_KEY, read8());
                model.put(MSP.UAVTYPE_KEY, read8());
                model.put(MSP.MSPVERSION_KEY, read8());
                model.put(MSP.UAVCAPABILITY_KEY, read32());
                break;
            case STATUS:

                int cycleTime = read16();
                int i2cError = read16();
                int present = read16();
                int mode = read32();
//                if ((present&1) >0) {buttonAcc.setColorBackground(green_);} else {buttonAcc.setColorBackground(red_);tACC_ROLL.setState(false); tACC_PITCH.setState(false); tACC_Z.setState(false);}
//                if ((present&2) >0) {buttonBaro.setColorBackground(green_);} else {buttonBaro.setColorBackground(red_); tBARO.setState(false); }
//                if ((present&4) >0) {buttonMag.setColorBackground(green_);} else {buttonMag.setColorBackground(red_); tMAGX.setState(false); tMAGY.setState(false); tMAGZ.setState(false); }
//                if ((present&8) >0) {buttonGPS.setColorBackground(green_);} else {buttonGPS.setColorBackground(red_); tHEAD.setState(false);}
//                if ((present&16)>0) {buttonSonar.setColorBackground(green_);} else {buttonSonar.setColorBackground(red_);}
                for(int i=0;i<model.getBoxNameCount();i++) {
//                  if ((mode&(1<<i))>0) buttonCheckbox[i].setColorBackground(green_); else buttonCheckbox[i].setColorBackground(red_);
                }

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
                for (int i = 0; i < MAXSERVO; i++) {
                    model.getRealTimeData().put(
                            d,
                            new StringBuffer().append("servo").append(i)
                            .toString(), Double.valueOf(read16()),
                            MwSensorClassServo.class);
                }
                break;
            case MOTOR:
                for (int i = 0; i < MAXMOTOR; i++) {
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
//                GPS_fix = read8();
//                GPS_numSat = read8();
//                GPS_latitude = read32();
//                GPS_longitude = read32();
//                GPS_altitude = read16();
//                GPS_speed = read16();
                break;
            case COMP_GPS:
//                GPS_distanceToHome = read16();
//                GPS_directionToHome = read16();
//                GPS_update = read8();
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
    public static ByteArrayOutputStream request(int msp) {
        return request(msp, null);
    }

    // send msp with payload
    public static ByteArrayOutputStream request(int msp, byte[] payload)
    {
        ByteArrayOutputStream bf = new ByteArrayOutputStream();

        bf.write( OUT, 0, OUT.length );

        int hash = 0;           // upper 24 bits will be ignored.
        int payloadz = 0;       // siZe

        if( payload != null )
            payloadz = payload.length;

        bf.write( payloadz );
        hash ^= payloadz;

        bf.write( msp );
        hash ^= msp;

        if (payload != null) {
            for ( byte b : payload) {
                bf.write( b );
                hash ^= b;
            }
        }

        bf.write( hash );
        return bf;
    }

    /** send multiple msp commands without payload
    public static ByteArrayOutputStream request(int[] msps) {
        ByteArrayOutputStream ret = new ByteArrayOutputStream();
        for (final int m : msps) {
            ret.write( request( m, null).toByteArray() );
        }
        return ret;
    }
    */

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
