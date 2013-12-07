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
package org.multiwii.swingui.gui.setting;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.multiwii.swingui.ds.MwDataModel;
import org.multiwii.swingui.gui.comp.MwJLabel;
import org.multiwii.swingui.gui.comp.MwJPanel;
import org.multiwii.swingui.gui.comp.MwTextField;

public class MwPIDPanel extends MwChangeablePanel {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public MwPIDPanel(final String name) {
		super(name);
	}

	private Component build(final Map<String, List<Double>> piDs,
			final Map<Integer, String> index) {
		final MwJPanel mainPane = new MwJPanel();
		mainPane.setLayout(new GridLayout(
				1 + (index == null ? 0 : index.size()), 1));
		MwJPanel pane = new MwJPanel();

		if (piDs == null || index == null) {
			pane.setLayout(new GridLayout(1, 1));
			pane.add(new MwJLabel("PID - EMPTY"));
			mainPane.add(pane);
			return mainPane;
		}

		pane.setLayout(new GridLayout(1, 4));
		// pane.setBorder(new EmptyBorder(1, 1, 1, 1));
		pane.add(new MwJLabel());
		pane.add(new MwJLabel("P"));
		pane.add(new MwJLabel("I"));
		pane.add(new MwJLabel("D"));
		mainPane.add(pane);

		for (int i = 0; i < index.size(); i++) {
			final String name = index.get(i);
			pane = new MwJPanel();
			pane.setLayout(new GridLayout(1, 4));
			pane.setBorder(new EmptyBorder(1, 1, 1, 1));

			final List<Double> pidItem = piDs.get(name);
			pane.add(new JLabel(name));
			int j = 0;
			for (final Double double1 : pidItem) {

				// TODO get step and bound from msp

				pane.add(new MwTextField(double1, 0.1, j++));
			}
			mainPane.add(pane);
		}

		return mainPane;
	}

	@Override
	final void newModel(final MwDataModel model) {
		this.removeAll();
		super.setLayout(new GridLayout(1, 1));
		this.add(this.build(model.getPIDs(), model.getPidNameIndex()));
		this.revalidate();

	}

}
