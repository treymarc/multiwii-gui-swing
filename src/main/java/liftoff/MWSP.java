/**
 * Copyright 2012 Dick Hollenbeck <dick@softplc.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package liftoff;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;

/**
 * Class MWSP is a transaction oriented class that allows a single thread,
 * probably running as a swing worker thread or a thread running in a gui-less
 * test program, to execute MultiWii Serial Protocol (MWSP) commands and receive
 * replies. It is mostly intended to write test programs and to experiment with
 * the MWSP without breaking the mainline GUI code. No GUI is assumed present
 * here, so it can run from the command line just fine as a console application.
 */
public class MWSP extends SerialTransact {
	private final static boolean debug = true;

	protected ByteArrayOutputStream bos = new ByteArrayOutputStream();
	protected PrintStream ps;

	/*
	 * static final int ERR = -1, IDLE = 0, HEADER_START = 1, HEADER_M = 2,
	 * HEADER_ARROW = 3, HEADER_SIZE = 4, HEADER_CMD = 5, HEADER_PAYLOAD = 6,
	 * HEADER_CHK = 6 ;
	 */

	protected static final int IDENT = 100, STATUS = 101, RAW_IMU = 102,
			SERVO = 103, MOTOR = 104, RC = 105, RAW_GPS = 106, COMP_GPS = 107,
			ATTITUDE = 108, ALTITUDE = 109, BAT = 110, RC_TUNING = 111,
			PID = 112, BOX = 113, MISC = 114, MOTOR_PINS = 115, BOXNAMES = 116,
			PIDNAMES = 117,

			SET_RAW_RC = 200, SET_RAW_GPS = 201, SET_PID = 202, SET_BOX = 203,
			SET_RC_TUNING = 204, ACC_CALIBRATION = 205, MAG_CALIBRATION = 206,
			SET_MISC = 207, RESET_CONF = 208, EEPROM_WRITE = 250, DEBUG = 254;

	/**
	 * Function outSend send the data contained in the bos.
	 * 
	 * @return int - the number of bytes sent
	 */
	public int outSend() throws IOException {
		// add dummy place holder for xor checksum in the output, change it
		// later.
		bos.write(0);

		byte[] buf = bos.toByteArray();

		// compute checksum, change last byte, which is the zeroed place holder
		// above
		int csum = 0;
		int i;
		for (i = 3; i < buf.length - 1; ++i) {
			csum ^= buf[i];
		}
		buf[i] = (byte) csum;

		send(buf, 0, buf.length);
		return buf.length;
	}

	/**
	 * Function outFormat printf()s a string message to the serial port.
	 */
	void outFormat(String format, Object... args) throws IOException {
		ps.printf(format, args);
		ps.flush(); // into serport.getOutputStream()
	}

	void outClear() {
		bos.reset();
	}

	public void recvMsg() throws IOException {
		System.out.print("recv:");

		ByteArrayOutputStream buf = new ByteArrayOutputStream();

		int cc;
		while ((cc = recv()) != -1) {
			buf.write(cc);
		}

		byte[] inpacket = buf.toByteArray();

		// opportunity to customize output for each command:
		switch (0xff & inpacket[4]) {
		case BOXNAMES:
			System.out.print("BOXNAMES:");
			for (String name : new String(inpacket, 5, inpacket.length - 6)
					.split(";"))
				System.out.print(" " + name);
			break;

		default:
			for (int i = 0; i < inpacket.length; ++i)
				System.out.printf(" %02x", 0xff & inpacket[i]);
		}

		System.out.println();
	}

	public void test() throws NoSuchPortException, PortInUseException {
		CommPortIdentifier comident = CommPortIdentifier
				.getPortIdentifier("/dev/ttyUSB1");

		try {
			serport = (SerialPort) comident.open("MWSP", 200);

			serport.setSerialPortParams(115200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);

			serport.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

			setSerialPort(serport, 250);

			try {
				// This encoding is chosen because it is a single byte character
				// set, and so 8 bit
				// bytes are simply promoted to 16 bit chars by zeroing upper
				// byte.
				ps = new PrintStream(bos, false, "ISO-8859-1");
			} catch (Exception ex) {
			} // it's not going to happen

			for (int i = 1; i < 5; ++i) {
				recvClear();
				outClear();
				outFormat("$M<%c", IDENT);
				outSend();
				recvMsg();

				recvClear();
				outClear();
				outFormat("$M<%c", BOXNAMES);
				outSend();
				recvMsg();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			close();
		}
	}

	public static void main(String[] args) throws NoSuchPortException,
			PortInUseException {
		System.out.println("Hello World!\n");

		Enumeration ports = CommPortIdentifier.getPortIdentifiers();

		while (ports.hasMoreElements()) {
			CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
			String type;

			switch (port.getPortType()) {
			case CommPortIdentifier.PORT_PARALLEL:
				type = "Parallel";
				break;

			case CommPortIdentifier.PORT_SERIAL:
				type = "Serial";
				break;

			default: // / Shouldn't happen
				type = "Unknown";
				break;
			}

			System.out.println(port.getName() + ": " + type);
		}

		MWSP mwsp = new MWSP();

		mwsp.test();
	}
};
