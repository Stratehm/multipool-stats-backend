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
public class Blocks {
	private Number bits;
	private Number block_index;
	private Number fee;
	private String hash;
	private Number height;
	private boolean main_chain;
	private String mrkl_root;
	private Number n_tx;
	private Number nonce;
	private String prev_block;
	private Number size;
	private Number time;
	private List<Tx> tx;
	private Number ver;

	public Number getBits() {
		return this.bits;
	}

	public void setBits(Number bits) {
		this.bits = bits;
	}

	public Number getBlock_index() {
		return this.block_index;
	}

	public void setBlock_index(Number block_index) {
		this.block_index = block_index;
	}

	public Number getFee() {
		return this.fee;
	}

	public void setFee(Number fee) {
		this.fee = fee;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Number getHeight() {
		return this.height;
	}

	public void setHeight(Number height) {
		this.height = height;
	}

	public boolean getMain_chain() {
		return this.main_chain;
	}

	public void setMain_chain(boolean main_chain) {
		this.main_chain = main_chain;
	}

	public String getMrkl_root() {
		return this.mrkl_root;
	}

	public void setMrkl_root(String mrkl_root) {
		this.mrkl_root = mrkl_root;
	}

	public Number getN_tx() {
		return this.n_tx;
	}

	public void setN_tx(Number n_tx) {
		this.n_tx = n_tx;
	}

	public Number getNonce() {
		return this.nonce;
	}

	public void setNonce(Number nonce) {
		this.nonce = nonce;
	}

	public String getPrev_block() {
		return this.prev_block;
	}

	public void setPrev_block(String prev_block) {
		this.prev_block = prev_block;
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

	public List<Tx> getTx() {
		return this.tx;
	}

	public void setTx(List<Tx> tx) {
		this.tx = tx;
	}

	public Number getVer() {
		return this.ver;
	}

	public void setVer(Number ver) {
		this.ver = ver;
	}
}
