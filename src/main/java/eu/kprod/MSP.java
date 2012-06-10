package eu.kprod;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeListener;

import eu.kprod.ds.MwDataSource;
import eu.kprod.ds.MwSensorClassIMU;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassRC;
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

//    public static MwDataModel getModel() {
//        return model;
//    }

    public static void setModel(final MwDataModel model1) {
        MSP.model = model1;
    }

    private static final int MASK = 0xff;
    private static final int BUFFER_SIZE = 128;
    
    private static final Character char$ = new Character('$');
    private static final Character charM = new Character('M');
    private static final Character charArrow = new Character('>');
    
    public static final String
    OUT   = "$M<";

            /* processing does not accept enums? */
            public static final int
               IDLE = 0,
               HEADER_START = 1,
               HEADER_M = 2,
               HEADER_ARROW = 3,
               HEADER_SIZE = 4,
               HEADER_CMD = 5,
               HEADER_PAYLOAD = 6,
               HEADER_CHK = 6
            ;

            

    public static final int
    IDENT                =100,
   STATUS               =101,
   RAW_IMU              =102,
   SERVO                =103,
   MOTOR                =104,
   RC                   =105,
   RAW_GPS              =106,
   COMP_GPS             =107,
   ATTITUDE             =108,
   ALTITUDE             =109,
   BAT                  =110,
   RC_TUNING            =111,
   PID                  =112,
   BOX                  =113,
   MISC                 =114,
   MOTOR_PINS           =115,
   BOXNAMES             =116,
   PIDNAMES             =117,

   SET_RAW_RC           =200,
   SET_RAW_GPS          =201,
   SET_PID              =202,
   SET_BOX              =203,
   SET_RC_TUNING        =204,
   ACC_CALIBRATION      =205,
   MAG_CALIBRATION      =206,
   SET_MISC             =207,
   RESET_CONF           =208,

   EEPROM_WRITE         =250,

   DEBUG                =254;

    /**
     * position in the reception inputBuffer
     */
    private static int p;

    /**
     * reception buffer
     */
    private static byte[] inBuf = new byte[BUFFER_SIZE ];
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

    
    private static byte checksum = 0,cmd=0;
    
    private static int offset = 0, dataSize = 0, mspState = IDLE;


    /**
     * Decode the byte 
     * @param input
     */
    synchronized public static void decode(final byte input) {
        char c = (char) input;
        if (mspState == IDLE) {
            mspState = (char$.equals(c)) ? HEADER_START : IDLE;
          } else if (mspState == HEADER_START) {
            mspState =  (charM.equals(c)) ? HEADER_M : IDLE;
          } else if (mspState == HEADER_M) {
            mspState =  (charArrow.equals(c)) ? HEADER_ARROW : IDLE;
          } else if (mspState == HEADER_ARROW) {
            /* now we are expecting the payload size */
            dataSize = (c & MASK);
            /* reset index variables */
            p = 0;
            offset = 0;
            checksum = 0;
            checksum ^= (c & MASK);
            /* the command is to follow */
            mspState = HEADER_SIZE;
          } else if (mspState == HEADER_SIZE) {
            cmd = (byte)(c & MASK);
            checksum ^= (c & MASK);
            mspState = HEADER_CMD;
          } else if (mspState == HEADER_CMD && offset < dataSize) {
              checksum ^= (c & MASK);
              inBuf[offset++] = (byte)(c & MASK);
          } else if (mspState == HEADER_CMD && offset >= dataSize) {
            /* compare calculated and transferred checksum */
            if ((checksum & MASK) == (c & MASK)) {
              /* we got a valid response packet, evaluate it */
                decodeInBuf(cmd, (int)dataSize);
            } else {
              System.out.println("invalid checksum for command "+((int)(cmd & MASK))+": "+(checksum & MASK)+" expected, got "+(int)(c & MASK));
            }
            mspState = IDLE;
          }

    }


        
    synchronized private static void decodeInBuf(final int stateMSP, final int dataSize2) {
        final Date d = new Date();
        switch (stateMSP) {
            case IDENT:
                model.setVersion(read8());
                model.setMultiType(read8());
                break;
            case STATUS:

//                cycleTime = read16();
//                i2cError = read16();
//                present = read16();
//                mode = read16();
//                if ((present&1) >0) {buttonAcc.setColorBackground(green_);} else {buttonAcc.setColorBackground(red_);tACC_ROLL.setState(false); tACC_PITCH.setState(false); tACC_Z.setState(false);}
//                if ((present&2) >0) {buttonBaro.setColorBackground(green_);} else {buttonBaro.setColorBackground(red_); tBARO.setState(false); }
//                if ((present&4) >0) {buttonMag.setColorBackground(green_);} else {buttonMag.setColorBackground(red_); tMAGX.setState(false); tMAGY.setState(false); tMAGZ.setState(false); }
//                if ((present&8) >0) {buttonGPS.setColorBackground(green_);} else {buttonGPS.setColorBackground(red_); tHEAD.setState(false);}
//                if ((present&16)>0) {buttonSonar.setColorBackground(green_);} else {buttonSonar.setColorBackground(red_);}
//                for(i=0;i<CHECKBOXITEMS;i++) {
//                  if ((mode&(1<<i))>0) buttonCheckbox[i].setColorBackground(green_); else buttonCheckbox[i].setColorBackground(red_);
//                }
                break;
            case RAW_IMU:
                model.getRealTimeData().put(d, "ax", Double.valueOf(read16()),MwSensorClassIMU.class);
                model.getRealTimeData().put(d, "ay", Double.valueOf(read16()),MwSensorClassIMU.class);
                model.getRealTimeData().put(d, "az", Double.valueOf(read16()),MwSensorClassIMU.class);

                model.getRealTimeData().put(d, "gx", Double.valueOf(read16() / 8),MwSensorClassIMU.class);
                model.getRealTimeData().put(d, "gy", Double.valueOf(read16() / 8),MwSensorClassIMU.class);
                model.getRealTimeData().put(d, "gz", Double.valueOf(read16() / 8),MwSensorClassIMU.class);

                model.getRealTimeData().put(d, "magx", Double.valueOf(read16() / 3),MwSensorClassIMU.class);
                model.getRealTimeData().put(d, "magy", Double.valueOf(read16() / 3),MwSensorClassIMU.class);
                model.getRealTimeData().put(d, "magz", Double.valueOf(read16() / 3),MwSensorClassIMU.class);
                break;
            case SERVO:
                for(int i=0;i<8;i++){
                    model.getRealTimeData().put(d, new StringBuffer().append("servo").append(i).toString(), 
                            Double.valueOf(read16()),
                            MwSensorClassServo.class);
                }
                break;
            case MOTOR:
                for(int i=0;i<8;i++){
                    model.getRealTimeData().put(d, new StringBuffer().append("mot").append(i).toString(),
                            Double.valueOf(read16()),
                            MwSensorClassMotor.class);
                }
                break;
            case RC:

                model.getRealTimeData().put(d, "roll",  Double.valueOf(read16()), MwSensorClassRC.class) ;
                model.getRealTimeData().put(d, "pitch",  Double.valueOf(read16()), MwSensorClassRC.class) ;
                model.getRealTimeData().put(d, "yaw",  Double.valueOf(read16()), MwSensorClassRC.class) ;
                model.getRealTimeData().put(d, "throttle",  Double.valueOf(read16()), MwSensorClassRC.class) ;
                model.getRealTimeData().put(d, "aux1",  Double.valueOf(read16()), MwSensorClassRC.class) ;
                model.getRealTimeData().put(d, "aux2",  Double.valueOf(read16()), MwSensorClassRC.class) ;
                model.getRealTimeData().put(d, "aux3",  Double.valueOf(read16()), MwSensorClassRC.class) ;
                model.getRealTimeData().put(d, "aux4",  Double.valueOf(read16()), MwSensorClassRC.class) ;

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
                model.setRcRate((int) (read8() / 100.0));
                model.setRcExpo((int) (read8() / 100.0));
                model.setRollPitchRate((int) (read8() / 100.0));
                model.setYawRate((int) (read8() / 100.0));
                model.setDynThrPID((int) (read8() / 100.0));
                model.setThrottleMID((int) (read8() / 100.0));
                model.setThrottleEXPO((int) (read8() / 100.0));
                break;
            case ACC_CALIBRATION:
                break;
            case MAG_CALIBRATION:
                break;
            case PID:
                for( int index = 0;index<model.getPidNameCount();index++) { 
                    model.setPidValue(index,read8(),read8(),read8()); 
                  }
                model.pidChanged();
                break;
            case BOX:         
                for( int index = 0;index<model.getBoxNameCount();index++) {
                    int bytread = read16();
                   model.setBoxNameValue(index,bytread);    
                  } 
                model.boxChanged();
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
            case BOXNAMES:
                model.removeAllBoxName();
                int i = 0;
                for (String name : new String(inBuf, 0, dataSize).split(";")) {
                    model.addBoxName(name,i++);
                }
                break;
            case PIDNAMES:
                model.removeAllPIDName();
                i = 0;
                for (String name : new String(inBuf, 0, dataSize).split(";")) {
                    model.addPIDName(name,i++);
                }
                break;
        }

    }

  //send msp without payload
    public static List<Byte> request(int msp) {
        return  request( msp, null);
    }

    //send multiple msp without payload
    public static List<Byte> request (int[] msps) {
    List<Byte> s = new LinkedList<Byte>();
    for (int m : msps) {
     s.addAll(request(m, null));
    }
    return s;
    }

    //send msp with payload
     public static List<Byte> request (int msp, Character[] payload) {
    if(msp < 0) {
     return null;
    }
    List<Byte> bf = new LinkedList<Byte>();
    for (byte c : OUT.getBytes()) {
     bf.add( c );
    }

    byte checksum=0;
    byte pl_size = (byte)((payload != null ? (payload.length) : 0)&0xFF);
    bf.add(pl_size);
    checksum ^= (pl_size&0xFF);

    bf.add((byte)(msp & 0xFF));
    checksum ^= (msp&0xFF);

    if (payload != null) {
     for (char c :payload){
       bf.add((byte)(c&0xFF));
       checksum ^= (c&0xFF);
     }
    }

    bf.add(checksum);
    return (bf);
    }
   
    public static MwDataSource getRealTimeData() {
        // TODO Auto-generated method stub
        return model.getRealTimeData();
    }

    public static void setPidChangeListener(final ChangeListener pidPane) {
        // TODO Auto-generated method stub
        model.setPidChangeListener(pidPane);
    }

    public static void setBoxChangeListener(final ChangeListener boxPane) {
        // TODO Auto-generated method stub
        model.setBoxChangeListener(boxPane);
        
    }
}
