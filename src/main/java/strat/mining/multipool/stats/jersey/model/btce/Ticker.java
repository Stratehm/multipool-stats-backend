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
package strat.mining.multipool.stats.jersey.model.btce;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {
	private Float avg;
	private Float buy;
	private Float high;
	private Float last;
	private Float low;
	private Float sell;
	private Long server_time;
	private Long updated;
	private Float vol;
	private Float vol_cur;

	public Float getAvg() {
		return this.avg;
	}

	public void setAvg(Float avg) {
		this.avg = avg;
	}

	public Float getBuy() {
		return this.buy;
	}

	public void setBuy(Float buy) {
		this.buy = buy;
	}

	public Float getHigh() {
		return this.high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getLast() {
		return this.last;
	}

	public void setLast(Float last) {
		this.last = last;
	}

	public Float getLow() {
		return this.low;
	}

	public void setLow(Float low) {
		this.low = low;
	}

	public Float getSell() {
		return this.sell;
	}

	public void setSell(Float sell) {
		this.sell = sell;
	}

	public Long getServer_time() {
		return this.server_time;
	}

	public void setServer_time(Long server_time) {
		this.server_time = server_time;
	}

	public Long getUpdated() {
		return this.updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	public Float getVol() {
		return this.vol;
	}

	public void setVol(Float vol) {
		this.vol = vol;
	}

	public Float getVol_cur() {
		return this.vol_cur;
	}

	public void setVol_cur(Float vol_cur) {
		this.vol_cur = vol_cur;
	}
}
