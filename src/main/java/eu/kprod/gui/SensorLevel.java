package eu.kprod.gui;

import org.apache.log4j.Level;

public class SensorLevel extends Level{
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public static final int SENSOR_INT = INFO_INT +100; 
  
  public static final Level SENSOR = new SensorLevel(SENSOR_INT,"SENSOR",6);  
  
  
  protected SensorLevel(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
    // TODO Auto-generated constructor stub
  }
  
  /** 
   * Checks whether sArg is "MY_TRACE" level. If yes then returns  
   * {@link MyTraceLevel#MY_TRACE}, else calls  
   * {@link MyTraceLevel#toLevel(String, Level)} passing it  
   * {@link Level#DEBUG} as the defaultLevel. 
    * 
    * @see Level#toLevel(java.lang.String) 
    * @see Level#toLevel(java.lang.String, org.apache.log4j.Level) 
    * 
    */  
  public static Level toLevel(String sArg) {  
      if (sArg != null && sArg.toUpperCase().equals("MY_TRACE")) {  
          return SENSOR;  
      }  
      return (Level) toLevel(sArg, Level.DEBUG);  
  }  

  /** 
   * Checks whether val is {@link MyTraceLevel#MY_TRACE_INT}.  
* If yes then returns {@link MyTraceLevel#MY_TRACE}, else calls  
* {@link MyTraceLevel#toLevel(int, Level)} passing it {@link Level#DEBUG} 
* as the defaultLevel 
* 
    * @see Level#toLevel(int) 
    * @see Level#toLevel(int, org.apache.log4j.Level) 
    * 
    */  
  public static Level toLevel(int val) {  
      if (val == SENSOR_INT) {  
          return SENSOR;  
      }  
      return (Level) toLevel(val, Level.DEBUG);  
  }  

  /** 
   * Checks whether val is {@link MyTraceLevel#MY_TRACE_INT}.  
   * If yes then returns {@link MyTraceLevel#MY_TRACE}, 
   * else calls {@link Level#toLevel(int, org.apache.log4j.Level)} 
   * 
   * @see Level#toLevel(int, org.apache.log4j.Level) 
   */  
  public static Level toLevel(int val, Level defaultLevel) {  
      if (val == SENSOR_INT) {  
          return SENSOR;  
      }  
      return Level.toLevel(val,defaultLevel);  
  }  

  /** 
   * Checks whether sArg is "MY_TRACE" level.  
* If yes then returns {@link MyTraceLevel#MY_TRACE}, else calls 
* {@link Level#toLevel(java.lang.String, org.apache.log4j.Level)} 
* 
* @see Level#toLevel(java.lang.String, org.apache.log4j.Level) 
*/  
public static Level toLevel(String sArg, Level defaultLevel) {       
     if(sArg != null && sArg.toUpperCase().equals("MY_TRACE")) {  
         return SENSOR;  
     }  
     return Level.toLevel(sArg,defaultLevel);  
} 

}
