/*  This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program;
 */

package eu.kprod.serial;

/**
 *
 * @author treym
 *
 */
public interface SerialListener {

    /**
     * @param aByte holds the received byte in the least significant 8 bits.
     */
    void readSerialByte(int aByte);

    /**
     * Report an error
     * @param e the error
     */
    void reportSerial(Throwable e);
}
