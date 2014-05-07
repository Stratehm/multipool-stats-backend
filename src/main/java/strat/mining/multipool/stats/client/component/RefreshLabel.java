/**
 * multipool-stats-backend is a web application which collects statistics
 * on several Switching-profit crypto-currencies mining pools and display
 * then in a Browser.
 * Copyright (C) 2014  Stratehm (stratehm@hotmail.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with multipool-stats-backend. If not, see <http://www.gnu.org/licenses/>.
 */
package strat.mining.multipool.stats.client.component;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;

public class RefreshLabel extends Label {

	private static final DateTimeFormat timeFormatter = DateTimeFormat.getFormat("m:ss");

	private Timer refreshedTimer;

	private long startTime;

	public RefreshLabel() {
		addStyleName("refreshLabel");
		refreshedTimer = new Timer() {
			public void run() {
				updateLabel();
			}
		};

		startTimer();
	}

	private void startTimer() {
		startTime = System.currentTimeMillis();
		refreshedTimer.scheduleRepeating(1000);
	}

	public void resetTimer() {
		startTime = System.currentTimeMillis();
		updateLabel();
	}

	private void updateLabel() {
		long secondsCounter = (System.currentTimeMillis() - startTime) / 1000;
		String labelText = "Refreshed " + timeFormatter.format(new Date(secondsCounter * 1000)) + " ago";
		this.setText(labelText);
	}

}
