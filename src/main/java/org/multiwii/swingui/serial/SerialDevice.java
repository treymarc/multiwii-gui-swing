/* -*- mode: jde; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
 * PSerial - class for serial port goodness
 * Part of the Processing project - http://processing.org
 * Copyright (c) 2004 Ben Fry & Casey Reas
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

package org.multiwii.swingui.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.multiwii.swingui.msg.I18n;

public class SerialDevice implements SerialPortEventListener {
    private static final Logger LOGGER = Logger.getLogger(SerialDevice.class);

    public static final List<Integer> SERIAL_BAUD_RATE = initializeMap();

    public static List<String> getPortNameList() {
        final List<String> portNames = new ArrayList<String>();

        for (@SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> enumeration = CommPortIdentifier
                .getPortIdentifiers(); enumeration.hasMoreElements();) {
            CommPortIdentifier commportidentifier = enumeration
                    .nextElement();

            if (commportidentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                String curr_port = commportidentifier.getName();
                portNames.add(curr_port);
            }
        }

        return portNames;
    }

    private static List<Integer> initializeMap() {
        List<Integer> m = new ArrayList<Integer>();
        m.add(9600);
        m.add(38400);
        m.add(57600);
        m.add(115200);

        return Collections.unmodifiableList(m);
    }

    private final int databits;

    // read buffer and streams

    private InputStream input;
    private SerialListener listener;

    private OutputStream output;
    private int parity;
    private SerialPort port;

    private int rate;

    private int stopbits;

    private String deviceName;

    public SerialDevice(String device) throws SerialException {
        this(device, SerialDevice.SERIAL_BAUD_RATE.get(115200), 'N', 8, 1.0f);
    }

    public SerialDevice(String device, int rate1)
            throws SerialException {
        this(device, rate1, 'N', 8, 1.0f);

    }

    public SerialDevice(String device1, int irate,
            char iparity, int idatabits, float istopbits)
            throws SerialException {


        this.rate = irate;

        parity = SerialPort.PARITY_NONE;
        if (iparity == 'E') {
            parity = SerialPort.PARITY_EVEN;
        }
        if (iparity == 'O') {
            parity = SerialPort.PARITY_ODD;
        }
        this.databits = idatabits;

        stopbits = SerialPort.STOPBITS_1;
        if (istopbits == 1.5f) {
            stopbits = SerialPort.STOPBITS_1_5;
        }
        if (istopbits == 2) {
            stopbits = SerialPort.STOPBITS_2;
        }

        try {
            port = null;

            @SuppressWarnings("unchecked")
            Enumeration<CommPortIdentifier> portList = CommPortIdentifier
                    .getPortIdentifiers();
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId = portList.nextElement();

                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    LOGGER.trace("serial possibly " + portId.getName());
                    if (portId.getName().equals(device1)) {
                        LOGGER.trace("found "+device1);
                        port = (SerialPort) portId.open("open", 200);
                        input = port.getInputStream();
                        output = port.getOutputStream();
                        port.setSerialPortParams(rate, databits, stopbits,
                                parity);

                        port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

                        port.addEventListener(this);
                        port.notifyOnDataAvailable(true);
                        LOGGER.trace("opening, ready to roll");
                        break;
                    }
                }
            }
            if (port == null) {
                throw new SerialNotFoundException(I18n.format(
                        "Can not find ''{0}''.", device1));
            }
        } catch (PortInUseException e) {
            throw new SerialException(
                    I18n.format(
                            "Serial port ''{0}'' already in use. Try quiting any programs that may be using it.",
                            device1),e);
        } catch (Exception e) {
            throw new SerialException(I18n.format(
                    "Error opening serial port ''{0}''.", device1), e);
        }

        if (output == null || input == null) {
            throw new SerialNotFoundException(I18n.format(
                    "Serial port ''{0}'' has not output or input strem.", device1));
        }else if(LOGGER.isTraceEnabled()){
            LOGGER.trace(I18n.format(
                    "Serial port ''{0}'' belong to us.", device1));
        }
        deviceName = device1;
    }

    public final void addListener(SerialListener consumer) {
        this.listener = consumer;
    }

    public final void close() {
        try {
            // do io streams need to be closed first?
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        input = null;
        output = null;

        try {
            if (port != null) {
                port.close(); // close the port
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        port = null;
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(I18n.format("close SerialDevice : {0}", deviceName));
        }
    }
    public final SerialListener getListener() {
        return listener;
    }

    // /**
    // * Returns a number between 0 and 255 for the next byte that's
    // * waiting in the buffer.
    // * Returns -1 if there was no byte (although the user should
    // * first check available() to see if things are ready to avoid this)
    // */
    // public int read() {
    // if (bufferIndex == bufferLast) return -1;
    //
    // synchronized (buffer) {
    // int outgoing = buffer[bufferIndex++] & 0xff;
    // if (bufferIndex == bufferLast) { // rewind
    // bufferIndex = 0;
    // bufferLast = 0;
    // }
    // return outgoing;
    // }
    // }
    //
    //
    // /**
    // * Returns the next byte in the buffer as a char.
    // * Returns -1, or 0xffff, if nothing is there.
    // */
    // public char readChar() {
    // if (bufferIndex == bufferLast) return (char)(-1);
    // return (char) read();
    // }

    /**
     * Return a byte array of anything that's in the serial buffer. Not
     * particularly memory/speed efficient, because it creates a byte array on
     * each read, but it's easier to use than readBytes(byte b[]) (see below).
     */
    // public byte[] readBytes() {
    // if (bufferIndex == bufferLast) return null;
    //
    // synchronized (buffer) {
    // int length = bufferLast - bufferIndex;
    // byte outgoing[] = new byte[length];
    // System.arraycopy(buffer, bufferIndex, outgoing, 0, length);
    //
    // bufferIndex = 0; // rewind
    // bufferLast = 0;
    // return outgoing;
    // }
    // }

    /**
     * Grab whatever is in the serial buffer, and stuff it into a byte buffer
     * passed in by the user. This is more memory/time efficient than
     * readBytes() returning a byte[] array.
     * Returns an int for how many bytes were read. If more bytes are available
     * than can fit into the byte array, only those that will fit are read.
     */
    // public int readBytes(byte outgoing[]) {
    // if (bufferIndex == bufferLast) return 0;
    //
    // synchronized (buffer) {
    // int length = bufferLast - bufferIndex;
    // if (length > outgoing.length) length = outgoing.length;
    // System.arraycopy(buffer, bufferIndex, outgoing, 0, length);
    //
    // bufferIndex += length;
    // if (bufferIndex == bufferLast) {
    // bufferIndex = 0; // rewind
    // bufferLast = 0;
    // }
    // return length;
    // }
    // }

    /**
     * Reads from the serial port into a buffer of bytes up to and including a
     * particular character. If the character isn't in the serial buffer, then
     * 'null' is returned.
     */
    // public byte[] readBytesUntil(int interesting) {
    // if (bufferIndex == bufferLast) return null;
    // byte what = (byte)interesting;
    //
    // synchronized (buffer) {
    // int found = -1;
    // for (int k = bufferIndex; k < bufferLast; k++) {
    // if (buffer[k] == what) {
    // found = k;
    // break;
    // }
    // }
    // if (found == -1) return null;
    //
    // int length = found - bufferIndex + 1;
    // byte outgoing[] = new byte[length];
    // System.arraycopy(buffer, bufferIndex, outgoing, 0, length);
    //
    // bufferIndex = 0; // rewind
    // bufferLast = 0;
    // return outgoing;
    // }
    // }

    /**
     * Reads from the serial port into a buffer of bytes until a particular
     * character. If the character isn't in the serial buffer, then 'null' is
     * returned.
     * If outgoing[] is not big enough, then -1 is returned, and an error
     * message is printed on the console. If nothing is in the buffer, zero is
     * returned. If 'interesting' byte is not in the buffer, then 0 is returned.
     */
    // public int readBytesUntil(int interesting, byte outgoing[]) {
    // if (bufferIndex == bufferLast) return 0;
    // byte what = (byte)interesting;
    //
    // synchronized (buffer) {
    // int found = -1;
    // for (int k = bufferIndex; k < bufferLast; k++) {
    // if (buffer[k] == what) {
    // found = k;
    // break;
    // }
    // }
    // if (found == -1) return 0;
    //
    // int length = found - bufferIndex + 1;
    // if (length > outgoing.length) {
    // LOGGER.trace(
    // I18n.format(
    // I18n.format("readBytesUntil() byte buffer is too small for the {0} bytes up to and including char {1}"),
    // length,
    // interesting
    // ))
    // );
    // return -1;
    // }
    // //byte outgoing[] = new byte[length];
    // System.arraycopy(buffer, bufferIndex, outgoing, 0, length);
    //
    // bufferIndex += length;
    // if (bufferIndex == bufferLast) {
    // bufferIndex = 0; // rewind
    // bufferLast = 0;
    // }
    // return length;
    // }
    // }

    /**
     * Return whatever has been read from the serial port so far as a String. It
     * assumes that the incoming characters are ASCII.
     * If you want to move Unicode data, you can first convert the String to a
     * byte stream in the representation of your choice (i.e. UTF8 or two-byte
     * Unicode data), and send it as a byte array.
     */
    // public String readString() {
    // if (bufferIndex == bufferLast) return null;
    // return new String(readBytes());
    // }

    /**
     * Combination of readBytesUntil and readString. See caveats in each
     * function. Returns null if it still hasn't found what you're looking for.
     * If you want to move Unicode data, you can first convert the String to a
     * byte stream in the representation of your choice (i.e. UTF8 or two-byte
     * Unicode data), and send it as a byte array.
     */
    // public String readStringUntil(int interesting) {
    // byte b[] = readBytesUntil(interesting);
    // if (b == null) return null;
    // return new String(b);
    // }

    /**
     * General error reporting, all corraled here just in case I think of
     * something slightly more intelligent to do.
     *
     * @param msg
     * @throws SerialException
     */
    public final void reportErrorMessage(String where, String msg, Throwable e) {
        if (LOGGER.isTraceEnabled()) {
            e.printStackTrace();
        } else {
            LOGGER.trace(I18n.format("Error inside Serial.{0}()", where));
        }
        listener.reportSerial(new SerialException(msg, e));

    }

    @Override
    public final synchronized void serialEvent(SerialPortEvent serialEvent) {
        // LOGGER.trace("serial port event");

        if (serialEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                while (input.available() > 0) {
                    int cc = input.read();
                    // System.out.printf( " I:%02x", cc );
                    listener.readSerialByte(cc);
                }
            } catch (Exception e) {
                listener.reportSerial(e);
            }
        }
    }

    public final void write(byte[] bytes) throws SerialException {
        try {
            if (output == null) {
                reportErrorMessage("write", "output stream is null ",
                        new SerialException());
            }

            output.write(bytes);
            output.flush();
        } catch (Exception e) {

            reportErrorMessage("write", "failed to write to output stream ", e);
        }
    }

    /**
     * Output a byte.
     *
     * @throws SerialException
     */
    public final void write(int what) throws SerialException {
        try {
            if (output == null) {
                reportErrorMessage("write",
                        "failed to write to output stream ",
                        new SerialException());
            }
            output.write(what);
            output.flush();
        } catch (Exception e) {
            // close();
            reportErrorMessage("write", "failed to write to output stream ", e);
        }
    }

    /**
     * Write a String to the output. Note that this doesn't account for Unicode
     * (two bytes per char), nor will it send UTF8 characters.. It assumes that
     * you mean to send a byte buffer (most often the case for networking and
     * serial i/o) and will only use the bottom 8 bits of each char in the
     * string. (Meaning that internally it uses String.getBytes)
     * If you want to move Unicode data, you can first convert the String to a
     * byte stream in the representation of your choice (i.e. UTF8 or two-byte
     * Unicode data), and send it as a byte array.
     *
     * @throws SerialException
     */
    public final void write(String what) throws SerialException {

        try {
            write(what.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException a) {
            // Everything from 0x0000 through 0x007F are exactly the same as
            // ASCII.
            // Everything from 0x0000 through 0x00FF is the same as ISO Latin 1.
            try {

                write(what.getBytes("ASCII"));
            } catch (UnsupportedEncodingException a1) {

                throw new SerialException(
                        "ASCII encoding is required for serial communication",
                        a1);
            }
        }
    }
}