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
package strat.mining.multipool.stats.dto;

import java.util.Date;

import org.jsonmaker.gwt.client.Jsonizer;

public class CurrencyTickerDTO {

	public interface CurrencyTickerDTOJsonizer extends Jsonizer {
	}

	private String exchangePlaceName;
	private String exchangePlaceLabel;
	private String currencyCode;
	private Float high;
	private Float low;
	private Float last;
	private Float buy;
	private Float sell;
	private Float volume;
	private Date refreshTime;

	public String getExchangePlaceName() {
		return exchangePlaceName;
	}

	public void setExchangePlaceName(String exchangePlaceName) {
		this.exchangePlaceName = exchangePlaceName;
	}

	public String getExchangePlaceLabel() {
		return exchangePlaceLabel;
	}

	public void setExchangePlaceLabel(String exchangePlaceLabel) {
		this.exchangePlaceLabel = exchangePlaceLabel;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Float getHigh() {
		return high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getLow() {
		return low;
	}

	public void setLow(Float low) {
		this.low = low;
	}

	public Float getLast() {
		return last;
	}

	public void setLast(Float last) {
		this.last = last;
	}

	public Float getBuy() {
		return buy;
	}

	public void setBuy(Float buy) {
		this.buy = buy;
	}

	public Float getSell() {
		return sell;
	}

	public void setSell(Float sell) {
		this.sell = sell;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	public Date getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(Date refreshTime) {
		this.refreshTime = refreshTime;
	}

}
