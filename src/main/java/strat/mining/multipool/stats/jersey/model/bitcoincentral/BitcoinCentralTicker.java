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
package strat.mining.multipool.stats.jersey.model.bitcoincentral;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BitcoinCentralTicker {
	private Float ask;
	private Long at;
	private Float bid;
	private String currency;
	private Float high;
	private Float low;
	private Float midpoint;
	private Float price;
	private Float variation;
	private Float volume;

	public Float getAsk() {
		return this.ask;
	}

	public void setAsk(Float ask) {
		this.ask = ask;
	}

	public Long getAt() {
		return this.at;
	}

	public void setAt(Long at) {
		this.at = at;
	}

	public Float getBid() {
		return this.bid;
	}

	public void setBid(Float bid) {
		this.bid = bid;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Float getHigh() {
		return this.high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getLow() {
		return this.low;
	}

	public void setLow(Float low) {
		this.low = low;
	}

	public Float getMidpoint() {
		return this.midpoint;
	}

	public void setMidpoint(Float midpoint) {
		this.midpoint = midpoint;
	}

	public Float getPrice() {
		return this.price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getVariation() {
		return this.variation;
	}

	public void setVariation(Float variation) {
		this.variation = variation;
	}

	public Float getVolume() {
		return this.volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}
}
