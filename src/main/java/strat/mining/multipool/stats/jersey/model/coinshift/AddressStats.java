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

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressStats {
	private String address;
	private String exchanged_balance;
	private Number hashrate;
	private Number id;
	private String payout_coin;
	private String payout_sum;
	private List<Payouts> payouts;
	private Number rejectrate;
	private String unexchanged_balance;

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getExchanged_balance() {
		return this.exchanged_balance;
	}

	public void setExchanged_balance(String exchanged_balance) {
		this.exchanged_balance = exchanged_balance;
	}

	public Number getHashrate() {
		return this.hashrate;
	}

	public void setHashrate(Number hashrate) {
		this.hashrate = hashrate;
	}

	public Number getId() {
		return this.id;
	}

	public void setId(Number id) {
		this.id = id;
	}

	public String getPayout_coin() {
		return this.payout_coin;
	}

	public void setPayout_coin(String payout_coin) {
		this.payout_coin = payout_coin;
	}

	public String getPayout_sum() {
		return this.payout_sum;
	}

	public void setPayout_sum(String payout_sum) {
		this.payout_sum = payout_sum;
	}

	public List<Payouts> getPayouts() {
		return this.payouts;
	}

	public void setPayouts(List<Payouts> payouts) {
		this.payouts = payouts;
	}

	public Number getRejectrate() {
		return this.rejectrate;
	}

	public void setRejectrate(Number rejectrate) {
		this.rejectrate = rejectrate;
	}

	public String getUnexchanged_balance() {
		return this.unexchanged_balance;
	}

	public void setUnexchanged_balance(String unexchanged_balance) {
		this.unexchanged_balance = unexchanged_balance;
	}
}
