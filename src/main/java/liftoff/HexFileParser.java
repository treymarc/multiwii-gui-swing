/****************************************************************
 *  Java based Intel hex file parser.
 *  Copyright 2012  Dick Hollenbeck <dick@softplc.com>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package liftoff;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Class ByteImage is a container to construct the byte array representing a
 * program image. It extends ByteArrayOutputStream by adding a @a seek()
 * operation and a @a baseAddress which is subtracted from any address passed to
 * seek(). It is expected to be used by class HexFileParser typically.
 */
class ByteImage extends ByteArrayOutputStream {
	private int baseAddress;
	private int maxPosition;

	/**
	 * Constructor
	 * 
	 * @param aBaseAddress
	 *            is the lowest address in the incoming hex image, it might not
	 *            be zero.
	 */
	public ByteImage(int aBaseAddress) {
		// initial byte array size equals 128 kbytes in STM32 flash
		super(128 * 1024);

		baseAddress = aBaseAddress;
	}

	// / capture the highest used index into the inherited byte array @a buf
	private final void checkPos() {
		if (count > maxPosition)
			maxPosition = count;
	}

	public synchronized void write(int b) {
		super.write(b);
		checkPos();
	}

	public synchronized void write(byte b[], int off, int len) {
		super.write(b, off, len);
		checkPos();
	}

	/*
	 * public synchronized void writeInt( int word ) { super.write( word >> 24
	 * ); super.write( word >> 16 ); super.write( word >> 8 ); super.write( word
	 * ); checkPos(); }
	 */

	void seek(int aAddress) throws IOException {
		int position = aAddress - baseAddress;

		if (position < 0)
			throw new IOException(
					"aAddress is less than the initial one, hex file is improperly built");

		if (position > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, position + 512));
		}

		if (position > count) {
			// if there is a gap, set bytes in the gap to 0xff
			Arrays.fill(buf, count, position, (byte) 0xff);
		}
		count = position;
	}

	public synchronized byte[] toByteArray() {
		// @Overload using maxPosition instead of count
		return Arrays.copyOf(buf, maxPosition);
	}
};

/**
 * Class HexFileParser parses an Intel HEX file and returns it as a byte array,
 * adjusted for the first address seen in the file. That is, the lowest
 * addressed byte will be at offset zero in the returned byte array.
 */
public class HexFileParser {
	// private static final boolean debug = true;
	private static final boolean debug = false;

	/**
	 * Method hexToInt converts a char in ascii hex form to a binary int in the
	 * range 0-15
	 * 
	 * @param c
	 *            is the char to convert
	 * @return int - The value 0-15
	 */
	private static final int hexToInt(char c) {
		if ('0' <= c && c <= '9')
			return c - '0';
		if ('a' <= c && c <= 'f')
			return 10 + (c - 'a');
		if ('A' <= c && c <= 'F')
			return 10 + (c - 'A');
		return -1;
	}

	/**
	 * Method parseHex converts a packed sequence of ASCII hex characters into a
	 * binary long integer.
	 * 
	 * @param hex
	 *            is the input String to look at.
	 * @param start
	 *            is the starting index into @a hex at which to begin parsing.
	 * @param end
	 *            is the stopping index into @a hex at which to stop parsing,
	 *            exclusive.
	 * @a end - @ start must be less than or equal to 8 here. This means the
	 *    returned value will have at most 32 bits of information in it.
	 * @return long - the converted binary integer and has at most 32 bits of
	 *         unsigned information in it with upper 32 bits being zero.
	 */
	public static long parseHex(String hex, int start, int end)
			throws ParseException {
		if (end - start > 8) // 8 hex characters translate to 4 binary bytes
		{
			String msg = String.format(
					"hex substring's length is %d, may not exceed 8", end
							- start);
			throw new ParseException(msg, start);
		}

		long ret = 0;

		for (int ndx = start; ndx < end; ++ndx) {
			int b = hexToInt(hex.charAt(ndx));
			if (b == -1)
				throw new ParseException("invalid hex input", ndx);

			ret = ret * 16 + b;
		}

		return ret;
	}

	/**
	 * Method parseFile parses a hexfile and returns an array of bytes that
	 * contain what should be burnt into the target device. The position of
	 * bytes in the array is determined by the specified address. If a byte
	 * element doesn't have any code specified, it will contain a 0xff.
	 * 
	 * @param file
	 *            is the hex File to parse.
	 * 
	 * @return byte[] - array of code bytes which came from the hex file,
	 *         arranged such that at byte index 0, this corresponds to the firs
	 *         address (assumed lowest) seen in the hex file. Addresses below
	 *         this first address will not be present in the returned byte
	 *         array.
	 */
	public static byte[] parseFile(File file) throws IOException,
			ParseException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		int lineNum = 0;
		int upper = 0; // ULBA or USBA
		ByteImage byteImage = null;

		try {
			String record;
			while ((record = br.readLine()) != null) {
				++lineNum;

				int recLen = (int) parseHex(record, 1, 3);
				int loadOffset = (int) parseHex(record, 3, 7);
				int recTyp = (int) parseHex(record, 7, 9);

				if (debug)
					System.out.printf(
							"Record[%d] len:%d loadOffset:%04x type:%d\n",
							lineNum, record.length(), loadOffset, recTyp);

				// process each record type, most have a payload

				int limit = 9 + 2 * recLen;
				int mysum = recLen + loadOffset + (loadOffset >> 8) + recTyp;

				switch (recTyp) {
				case 0:
					if (byteImage == null) {
						// edge triggered on first recType 0, which is assumed
						// to be the
						// lowest address. This lets us omit the portion of the
						// image which
						// is not present in the hex file by establishing a
						// baseAddress.
						byteImage = new ByteImage(upper + loadOffset);
					}
					byteImage.seek(upper + loadOffset);

					limit = 9 + 2 * recLen;
					for (int i = 9; i < limit; i += 2) {
						int dataByte = (int) parseHex(record, i, i + 2);
						mysum += dataByte;
						byteImage.write(dataByte);
					}
					break;

				case 1: // EOF record.
					return byteImage.toByteArray();

				case 2:
					upper = (int) parseHex(record, 9, limit);
					mysum += upper;
					mysum += upper >> 8;
					upper <<= 4;
					break;

				case 4:
					upper = (int) parseHex(record, 9, limit);
					mysum += upper;
					mysum += upper >> 8;
					upper <<= 16;
					break;

				case 3:
				case 5:
					int eip = (int) parseHex(record, 9, limit);
					mysum += eip >> 0;
					mysum += eip >> 8;
					mysum += eip >> 16;
					mysum += eip >> 24;
					// toss eip, not needed here.
					break;
				}
				if (byteImage!=null){
					byteImage.close();
				}
				int theirsum = (int) parseHex(record, limit, limit + 2);

				if ((0xff & (mysum + theirsum)) != 0) {
					String msg = String.format(
							"invalid checksum, mysum:%02x theirsum:%02x\n",
							mysum, theirsum);
					throw new ParseException(msg, limit);
				}
			}
		} catch (ParseException pe) {
			// add the line number, make as human friendly as we can
			int errOffs = pe.getErrorOffset();
			String msg = String.format("on file line %d @ offset %d: %s",
					lineNum, errOffs, pe.getMessage());
			throw new ParseException(msg, errOffs);
		} finally {
			
			
			br.close();
		}

		throw new ParseException("file has no EOF record", 0);
	}

	public static void dump(byte[] bytes, int count) {
		for (int i = 0; i < count; ++i) {
			if (i != 0 && (i % 16) == 0)
				System.out.println();

			System.out.printf(" %02x", 0xff & bytes[i]);
		}

		System.out.println();
	}

	public static void main(String[] args) {
		try {
			File f = new File(args[0]);

			byte[] image = HexFileParser.parseFile(f);

			dump(image, image.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
