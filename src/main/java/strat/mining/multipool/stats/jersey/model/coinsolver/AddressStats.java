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
package strat.mining.multipool.stats.jersey.model.coinsolver;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressStats {
	private String account;
	private String btc_balance;
	private String btc_immature;
	private String btc_unexchanged;
	private Number combined_estimate;
	private String hashrate;
	private String shares_accepted;
	private String shares_rejected;
	private String total_btc_paid;

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getBtc_balance() {
		return this.btc_balance;
	}

	public void setBtc_balance(String btc_balance) {
		this.btc_balance = btc_balance;
	}

	public String getBtc_immature() {
		return this.btc_immature;
	}

	public void setBtc_immature(String btc_immature) {
		this.btc_immature = btc_immature;
	}

	public String getBtc_unexchanged() {
		return this.btc_unexchanged;
	}

	public void setBtc_unexchanged(String btc_unexchanged) {
		this.btc_unexchanged = btc_unexchanged;
	}

	public Number getCombined_estimate() {
		return this.combined_estimate;
	}

	public void setCombined_estimate(Number combined_estimate) {
		this.combined_estimate = combined_estimate;
	}

	public String getHashrate() {
		return this.hashrate;
	}

	public void setHashrate(String hashrate) {
		this.hashrate = hashrate;
	}

	public String getShares_accepted() {
		return this.shares_accepted;
	}

	public void setShares_accepted(String shares_accepted) {
		this.shares_accepted = shares_accepted;
	}

	public String getShares_rejected() {
		return this.shares_rejected;
	}

	public void setShares_rejected(String shares_rejected) {
		this.shares_rejected = shares_rejected;
	}

	public String getTotal_btc_paid() {
		return this.total_btc_paid;
	}

	public void setTotal_btc_paid(String total_btc_paid) {
		this.total_btc_paid = total_btc_paid;
	}
}
