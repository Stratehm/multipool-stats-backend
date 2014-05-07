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

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressStats {
	private Balances balances;
	private String error;
	private Number hash_rate;
	private String hash_rate_str;
	private List<Recent_payments> recent_payments;
	private List<Worker_hashrates> worker_hashrates;

	public Balances getBalances() {
		return this.balances;
	}

	public void setBalances(Balances balances) {
		this.balances = balances;
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Number getHash_rate() {
		return this.hash_rate;
	}

	public void setHash_rate(Number hash_rate) {
		this.hash_rate = hash_rate;
	}

	public String getHash_rate_str() {
		return this.hash_rate_str;
	}

	public void setHash_rate_str(String hash_rate_str) {
		this.hash_rate_str = hash_rate_str;
	}

	public List<Recent_payments> getRecent_payments() {
		return this.recent_payments;
	}

	public void setRecent_payments(List<Recent_payments> recent_payments) {
		this.recent_payments = recent_payments;
	}

	public List<Worker_hashrates> getWorker_hashrates() {
		return this.worker_hashrates;
	}

	public void setWorker_hashrates(List<Worker_hashrates> worker_hashrates) {
		this.worker_hashrates = worker_hashrates;
	}
}
