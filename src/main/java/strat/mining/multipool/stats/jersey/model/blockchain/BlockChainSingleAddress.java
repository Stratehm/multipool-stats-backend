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
package strat.mining.multipool.stats.jersey.model.blockchain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockChainSingleAddress {
	private String address;
	private Number final_balance;
	private String hash160;
	private Number n_tx;
	private Number total_received;
	private Number total_sent;
	private List<Txs> txs;

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Number getFinal_balance() {
		return this.final_balance;
	}

	public void setFinal_balance(Number final_balance) {
		this.final_balance = final_balance;
	}

	public String getHash160() {
		return this.hash160;
	}

	public void setHash160(String hash160) {
		this.hash160 = hash160;
	}

	public Number getN_tx() {
		return this.n_tx;
	}

	public void setN_tx(Number n_tx) {
		this.n_tx = n_tx;
	}

	public Number getTotal_received() {
		return this.total_received;
	}

	public void setTotal_received(Number total_received) {
		this.total_received = total_received;
	}

	public Number getTotal_sent() {
		return this.total_sent;
	}

	public void setTotal_sent(Number total_sent) {
		this.total_sent = total_sent;
	}

	public List<Txs> getTxs() {
		return this.txs;
	}

	public void setTxs(List<Txs> txs) {
		this.txs = txs;
	}
}
