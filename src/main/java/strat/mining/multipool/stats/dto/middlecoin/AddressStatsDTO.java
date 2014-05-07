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
package strat.mining.multipool.stats.dto.middlecoin;

import java.util.Date;

import org.jsonmaker.gwt.client.Jsonizer;

public class AddressStatsDTO {

	public interface AddressStatsDTOJsonizer extends Jsonizer {
	}

	private String address;
	private Float megaHashesPerSeconds;
	private Float rejectedMegaHashesPerSeconds;
	private Integer lastHourShares;
	private Integer lastHourRejectedShares;
	private Date refreshTime;
	private Date updateTime;
	private Float balance;
	private Float unexchanged;
	private Float immature;
	private Float paidOut;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Float getMegaHashesPerSeconds() {
		return megaHashesPerSeconds;
	}

	public void setMegaHashesPerSeconds(Float megaHashesPerSeconds) {
		this.megaHashesPerSeconds = megaHashesPerSeconds;
	}

	public Float getRejectedMegaHashesPerSeconds() {
		return rejectedMegaHashesPerSeconds;
	}

	public void setRejectedMegaHashesPerSeconds(Float rejectedMegaHashesPerSeconds) {
		this.rejectedMegaHashesPerSeconds = rejectedMegaHashesPerSeconds;
	}

	public Integer getLastHourShares() {
		return lastHourShares;
	}

	public void setLastHourShares(Integer lastHourShares) {
		this.lastHourShares = lastHourShares;
	}

	public Integer getLastHourRejectedShares() {
		return lastHourRejectedShares;
	}

	public void setLastHourRejectedShares(Integer lastHourRejectedShares) {
		this.lastHourRejectedShares = lastHourRejectedShares;
	}

	public Date getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(Date time) {
		this.refreshTime = time;
	}

	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

	public Float getUnexchanged() {
		return unexchanged;
	}

	public void setUnexchanged(Float unexchanged) {
		this.unexchanged = unexchanged;
	}

	public Float getImmature() {
		return immature;
	}

	public void setImmature(Float immature) {
		this.immature = immature;
	}

	public Float getPaidOut() {
		return paidOut;
	}

	public void setPaidOut(Float paidOut) {
		this.paidOut = paidOut;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
