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
package eu.kprod.serial;

public class SerialCom {

    private int cr = 0;

    private String deviceName;

    private SerialListener listener;

    private SerialDevice serial;

    private int serialRate;

    public SerialCom(final String device, final int serialRateIn) {

        this.deviceName = device;
        this.setSerialRate(serialRateIn);

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
        if (serial != null) {
            return;
        }

        serial = new SerialDevice(deviceName, getSerialRate());
        serial.addListener(listener);
    }

    public final void send(byte []  s) throws SerialException {
        if (serial != null) {
            serial.write(s);
        }
    }

    public final void send(final String s) throws SerialException {
        if (serial != null) {
            serial.write(s);
        }
    }

    public final void send(String s,final  Integer d) throws SerialException {
        cr = d;
        switch (cr) {
            case 1:
                s += "\n";
                break;
            case 2:
                s += "\r";
                break;
            case 3:
                s += "\r\n";
                break;
        }
        send(s);
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
