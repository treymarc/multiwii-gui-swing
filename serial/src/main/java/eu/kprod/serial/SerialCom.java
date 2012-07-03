/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package eu.kprod.serial;

import java.io.ByteArrayOutputStream;

public class SerialCom {

    private static final String[] lineEnding={"\n","\r","\r\n"};
    private String deviceName;

    private SerialListener listener;

    private SerialDevice serial;

    private int serialRate;

    public SerialCom(final String device, final int serialRateIn) {

        this.deviceName = device;
        this.serialRate = serialRateIn;

    }

    public final void closeSerialPort() {
        if (serial != null) {

            serial.close();
            serial = null;
        }

    }

    public final String getDeviceName() {
        return deviceName;
    }

    public int getSerialRate() {
        return serialRate;
    }

    public boolean isOpen() {
        return (serial != null);
    }

    public final void openSerialPort() throws SerialException {
        if (serial != null){
            return;
        }
        serial = new SerialDevice(deviceName, getSerialRate());
        serial.addListener(listener);
    }

    public final void send( ByteArrayOutputStream bos ) throws SerialException {
        send( bos.toByteArray() );
    }

    public final void send( byte[] array ) throws SerialException {
        if (serial != null) {

            System.out.print( "cmd:" );
            // skip first 4 header bytes and trailing checksum
            for( int i=4; i<array.length-1; ++i )
                System.out.printf( " %02x", array[i] );
            System.out.println();

            serial.write( array );
        }
    }

    public final void send(final String s) throws SerialException {
        if (serial != null) {
            serial.write(s);
        }
    }

    public final void send(String s,final Integer d) throws SerialException {

        send(new StringBuffer().append(s).append(lineEnding[d]).toString());

    }

    public final void setDeviceName(final String deviceName1) {
        this.deviceName = deviceName1;
    }

    public final void setListener(SerialListener l) {
        listener = l;
        if (serial != null) {
            serial.addListener(l);
        }
    }

    public void setSerialRate(final int serialRate1) {
        this.serialRate = serialRate1;
    }

}