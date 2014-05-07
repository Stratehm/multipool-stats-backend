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
package strat.mining.multipool.stats.jersey.model.waffle;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Balances {
	private Number confirmed;
	private Number sent;
	private Number unconverted;

	public Number getConfirmed() {
		return this.confirmed;
	}

	public void setConfirmed(Number confirmed) {
		this.confirmed = confirmed;
	}

	public Number getSent() {
		return this.sent;
	}

	public void setSent(Number sent) {
		this.sent = sent;
	}

	public Number getUnconverted() {
		return this.unconverted;
	}

	public void setUnconverted(Number unconverted) {
		this.unconverted = unconverted;
	}
}
