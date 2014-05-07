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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Out {
	private String addr;
	private Number n;
	private Number tx_index;
	private Number type;
	private Number value;
	private String addr_tag;
	private String addr_tag_link;

	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public Number getN() {
		return this.n;
	}

	public void setN(Number n) {
		this.n = n;
	}

	public Number getTx_index() {
		return this.tx_index;
	}

	public void setTx_index(Number tx_index) {
		this.tx_index = tx_index;
	}

	public Number getType() {
		return this.type;
	}

	public void setType(Number type) {
		this.type = type;
	}

	public Number getValue() {
		return this.value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	public String getAddr_tag() {
		return addr_tag;
	}

	public void setAddr_tag(String addr_tag) {
		this.addr_tag = addr_tag;
	}

	public String getAddr_tag_link() {
		return addr_tag_link;
	}

	public void setAddr_tag_link(String addr_tag_link) {
		this.addr_tag_link = addr_tag_link;
	}

}
