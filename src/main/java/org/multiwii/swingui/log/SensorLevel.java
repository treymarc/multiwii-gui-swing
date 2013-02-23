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
package org.multiwii.swingui.log;

import org.apache.log4j.Level;

public class SensorLevel extends Level {

    public static final int SENSOR_INT = INFO_INT + 100;
    public static final Level SENSOR = new SensorLevel(SENSOR_INT, "SENSOR", 6);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Checks whether val is {@link MyTraceLevel#MY_TRACE_INT}. If yes then
     * returns {@link MyTraceLevel#MY_TRACE}, else calls
     * {@link MyTraceLevel#toLevel(int, Level)} passing it {@link Level#DEBUG}
     * as the defaultLevel
     * 
     * @see Level#toLevel(int)
     * @see Level#toLevel(int, org.apache.log4j.Level)
     */
    public static Level toLevel(final int val) {
        if (val == SENSOR_INT) {
            return SENSOR;
        }
        return toLevel(val, Level.DEBUG);
    }

    /**
     * Checks whether val is {@link MyTraceLevel#MY_TRACE_INT}. If yes then
     * returns {@link MyTraceLevel#MY_TRACE}, else calls
     * {@link Level#toLevel(int, org.apache.log4j.Level)}
     * 
     * @see Level#toLevel(int, org.apache.log4j.Level)
     */
    public static Level toLevel(final int val, final Level defaultLevel) {
        if (val == SENSOR_INT) {
            return SENSOR;
        }
        return Level.toLevel(val, defaultLevel);
    }

    /**
     * Checks whether sArg is "MY_TRACE" level. If yes then returns
     * {@link MyTraceLevel#MY_TRACE}, else calls
     * {@link MyTraceLevel#toLevel(String, Level)} passing it
     * {@link Level#DEBUG} as the defaultLevel.
     * 
     * @see Level#toLevel(java.lang.String)
     * @see Level#toLevel(java.lang.String, org.apache.log4j.Level)
     */
    public static Level toLevel(final String sArg) {
        if (sArg != null && sArg.toUpperCase().equals("MY_TRACE")) {
            return SENSOR;
        }
        return toLevel(sArg, Level.DEBUG);
    }

    /**
     * Checks whether sArg is "MY_TRACE" level. If yes then returns
     * {@link MyTraceLevel#MY_TRACE}, else calls
     * {@link Level#toLevel(java.lang.String, org.apache.log4j.Level)}
     * 
     * @see Level#toLevel(java.lang.String, org.apache.log4j.Level)
     */
    public static Level toLevel(final String sArg, final Level defaultLevel) {
        if (sArg != null && sArg.toUpperCase().equals("MY_TRACE")) {
            return SENSOR;
        }
        return Level.toLevel(sArg, defaultLevel);
    }

    protected SensorLevel(final int level, final String levelStr,
            final int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

}
