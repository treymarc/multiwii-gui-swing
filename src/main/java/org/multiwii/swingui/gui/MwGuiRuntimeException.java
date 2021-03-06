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
package org.multiwii.swingui.gui;

public class MwGuiRuntimeException extends RuntimeException {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	public MwGuiRuntimeException(String msg) {
		super(msg);
	}

	/**
	 * create a new MwGuiExpception
	 * 
	 * @param msg
	 *            message
	 * @param e
	 *            the reason Exception or Error
	 */
	public MwGuiRuntimeException(String msg, Throwable e) {
		// TODO Auto-generated constructor stub
		super(msg, e);
	}
}
