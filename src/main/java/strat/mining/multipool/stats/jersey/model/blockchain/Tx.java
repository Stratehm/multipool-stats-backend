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
public class Tx {
	private String hash;
	private List<Inputs> inputs;
	private List<Out> out;
	private String relayed_by;
	private Number size;
	private Number time;
	private Number tx_index;
	private Number ver;
	private Number vin_sz;
	private Number vout_sz;

	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public List<Inputs> getInputs() {
		return this.inputs;
	}

	public void setInputs(List<Inputs> inputs) {
		this.inputs = inputs;
	}

	public List<Out> getOut() {
		return this.out;
	}

	public void setOut(List<Out> out) {
		this.out = out;
	}

	public String getRelayed_by() {
		return this.relayed_by;
	}

	public void setRelayed_by(String relayed_by) {
		this.relayed_by = relayed_by;
	}

	public Number getSize() {
		return this.size;
	}

	public void setSize(Number size) {
		this.size = size;
	}

	public Number getTime() {
		return this.time;
	}

	public void setTime(Number time) {
		this.time = time;
	}

	public Number getTx_index() {
		return this.tx_index;
	}

	public void setTx_index(Number tx_index) {
		this.tx_index = tx_index;
	}

	public Number getVer() {
		return this.ver;
	}

	public void setVer(Number ver) {
		this.ver = ver;
	}

	public Number getVin_sz() {
		return this.vin_sz;
	}

	public void setVin_sz(Number vin_sz) {
		this.vin_sz = vin_sz;
	}

	public Number getVout_sz() {
		return this.vout_sz;
	}

	public void setVout_sz(Number vout_sz) {
		this.vout_sz = vout_sz;
	}
}
