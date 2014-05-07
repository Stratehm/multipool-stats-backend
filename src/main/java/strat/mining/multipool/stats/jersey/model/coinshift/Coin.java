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
package strat.mining.multipool.stats.jersey.model.coinshift;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Coin {
	private String estimated_unexchanged_balance;
	private String exchanged_balance;
	private String value_24h;
	private String value_per_mh_24h;

	public String getEstimated_unexchanged_balance() {
		return this.estimated_unexchanged_balance;
	}

	public void setEstimated_unexchanged_balance(String estimated_unexchanged_balance) {
		this.estimated_unexchanged_balance = estimated_unexchanged_balance;
	}

	public String getExchanged_balance() {
		return this.exchanged_balance;
	}

	public void setExchanged_balance(String exchanged_balance) {
		this.exchanged_balance = exchanged_balance;
	}

	public String getValue_24h() {
		return this.value_24h;
	}

	public void setValue_24h(String value_24h) {
		this.value_24h = value_24h;
	}

	public String getValue_per_mh_24h() {
		return this.value_per_mh_24h;
	}

	public void setValue_per_mh_24h(String value_per_mh_24h) {
		this.value_per_mh_24h = value_per_mh_24h;
	}
}
