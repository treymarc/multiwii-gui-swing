package eu.kprod;

import eu.kprod.gui.myDataSource;

public class DataMwiiModel  {

	// for real time data
	private myDataSource ds = new myDataSource();

	// global ident
	int version,
	multiType ;

	// rc conf
	int RC_RATE,
	RC_EXPO,
	RollPitchRate,
	yawRate,
	DynThrPID,
	ThrottleMID,
	ThrottleEXPO;


	public int getRC_RATE() {
		return RC_RATE;
	}

	public void setRC_RATE(int rC_RATE) {
		RC_RATE = rC_RATE;
	}

	public int getRC_EXPO() {
		return RC_EXPO;
	}

	public void setRC_EXPO(int rC_EXPO) {
		RC_EXPO = rC_EXPO;
	}

	public int getRollPitchRate() {
		return RollPitchRate;
	}

	public void setRollPitchRate(int rollPitchRate) {
		RollPitchRate = rollPitchRate;
	}

	public int getYawRate() {
		return yawRate;
	}

	public void setYawRate(int yawRate) {
		this.yawRate = yawRate;
	}

	public int getDynThrPID() {
		return DynThrPID;
	}

	public void setDynThrPID(int dynThrPID) {
		DynThrPID = dynThrPID;
	}

	public int getThrottleMID() {
		return ThrottleMID;
	}

	public void setThrottleMID(int throttleMID) {
		ThrottleMID = throttleMID;
	}

	public int getThrottleEXPO() {
		return ThrottleEXPO;
	}

	public void setThrottleEXPO(int throttleEXPO) {
		ThrottleEXPO = throttleEXPO;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getMultiType() {
		return multiType;
	}

	public void setMultiType(int multiType) {
		this.multiType = multiType;
	}

	public DataMwiiModel(){
		super();
	}

	public myDataSource getDs() {
		return  ds ;
	}


	//
	//  Map<String, Integer> getListforDs(){
	//
	//
	//    Map< String , Integer> result = new HashMap< String , Integer>();
	//
	//    result.put("version" ,version ) ;
	//    result.put("ax" ,ax ) ;
	//    result.put("ay" , ay) ;
	//    result.put("az", az) ;
	//    result.put("gx" , gx) ;
	//    result.put("gy" ,gy ) ;
	//
	//    result.put("gz" ,gz ) ;
	//    result.put("magx" ,magx ) ;
	//    result.put("magy",magy) ;
	//    result.put("magz" ,magz ) ;
	//    result.put("baro" ,baro ) ;
	//    result.put("head" ,head ) ;
	//
	//    for(int k = 0; k < 8; k++){
	//      result.put(""+"servo"+k ,servo[k] ) ;
	//    }
	//
	//    for(int k = 0; k < 8; k++){
	//      result.put(""+"motor"+k ,mot[k] ) ;
	//    }
	//
	//
	//
	//    result.put("rcRoll" ,rcRoll ) ;
	//    result.put("rcPitch" ,rcPitch ) ;
	//    result.put("rcYaw" ,rcYaw ) ;
	//    result.put("rcThrottle" ,rcThrottle ) ;
	//
	//    result.put("rcAUX1" ,rcAUX1 ) ;
	//    result.put("rcAUX2" ,rcAUX2 ) ;
	//    result.put("rcAUX3" ,rcAUX3 ) ;
	//    result.put("rcAUX4" ,rcAUX4 ) ;
	//
	//    result.put("cycleTime" ,cycleTime ) ;
	//    result.put("i2cError" ,i2cError ) ;
	//    result.put("angx" ,angx ) ;
	//    result.put("angy" ,angy ) ;
	//    result.put("multiType" ,multiType ) ;
	//
	//
	//
	//    for(int i1 = 0; i1 < PIDITEMS; i1++)
	//    {
	//
	//      result.put(i1 + "_byteP"  ,byteP[i1] ) ;
	//      result.put(i1 + "_byteI"  ,byteI[i1] ) ;
	//      result.put(i1 + "_byteD"  ,byteD[i1] ) ;
	//    }
	//
	//    result.put("byteRC_RATE" ,byteRC_RATE ) ;
	//    result.put("byteRC_EXPO" ,byteRC_EXPO ) ;
	//    result.put("byteRollPitchRate" ,byteRollPitchRate ) ;
	//    result.put("byteYawRate" ,byteYawRate ) ;
	//    result.put("byteDynThrPID" ,byteDynThrPID ) ;
	//
	//    result.put("GPS_distanceToHome" ,GPS_distanceToHome ) ;
	//    result.put("GPS_directionToHome" ,GPS_directionToHome ) ;
	//    result.put("GPS_numSat" ,GPS_numSat ) ;
	//    result.put("GPS_fix" ,GPS_fix ) ;
	//    result.put("GPS_update" ,GPS_update ) ; 
	//    result.put("pMeterSum ",pMeterSum ) ;
	//
	//
	//    result.put("intPowerTrigger" ,intPowerTrigger ) ;
	//    result.put("bytevbat" ,bytevbat ) ;
	//    result.put("debug1" ,debug1 ) ;
	//    result.put("debug2" ,debug2 ) ;
	//    result.put("debug3" ,debug3 ) ;
	//    result.put("debug4" ,debug4 ) ;
	//
	//
	//    result.put("nunchukPresent" ,nunchukPresent ? 1 : 0 ) ;
	//    result.put("i2cAccPresent" ,i2cAccPresent? 1 : 0 ) ;
	//    result.put("i2cBaroPresent" ,i2cBaroPresent ? 1 : 0) ;
	//    result.put("i2cMagnetoPresent" ,i2cMagnetoPresent? 1 : 0 ) ;
	//    result.put("GPSPresent" ,GPSPresent? 1 : 0 ) ;
	//    result.put("I2cAccActive" ,I2cAccActive ? 1 : 0) ;
	//    result.put("I2cBaroActive" ,I2cBaroActive ? 1 : 0) ;
	//    result.put("I2cMagnetoActive" ,I2cMagnetoActive ? 1 : 0) ;
	//    result.put("GPSActive" ,GPSActive ? 1 : 0) ;
	//
	//
	//    for(int k = 0; k < CHECKBOXITEMS; k++)
	//    {
	//
	//      for(int l = 0; l < 6; l++)
	//      {
	//
	//        result.put("item"+CHECKBOXITEMS+"_"+l ,(checkbox1[k][l] ) ? 1 : 0 ) ;
	//
	//      }
	//
	//      for(int l = 0; l < 6; l++)
	//      {
	//        result.put("item"+CHECKBOXITEMS+"_"+(6+l) ,(checkbox2[k][l]) ? 1 : 0) ;
	//
	//      }
	//
	//    }
	//    return result;
	//  }

}


