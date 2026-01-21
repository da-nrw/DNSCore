/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.uzk.hki.da.gui.panels;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * HistoriePanel: Hier soll eine Historie der eingelieferten SIP's dargestellt
 * werden.
 */
public class HistoriePanel extends JPanel {

	private static final long serialVersionUID = 2201673485393042516L;

	public HistoriePanel(boolean enabled, ClassLoader classloader) {
		this.setEnabled(enabled);

	}

	public void defineElementsPosition(int startPanelWidth, int startPanelHeight, int jhOffsetY,
			JLabel backgroundHistorieImageLabel) {

	}

}
