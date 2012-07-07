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

public class SerialRuntimeException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SerialRuntimeException() {
        super();
    }

    public SerialRuntimeException(final String message) {
        super(message);
    }

    public SerialRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SerialRuntimeException(final Throwable cause) {
        super(cause);
    }
}
