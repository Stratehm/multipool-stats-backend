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
package strat.mining.multipool.stats.dto.waffle;

import java.util.Date;

import org.jsonmaker.gwt.client.Jsonizer;

public class GlobalStatsDTO {

	public interface GlobalStatsDTOJsonizer extends Jsonizer {
	}

	private Float totalPaidOut;
	private Float totalMegahashesPerSecond;
	private Float totalBalance;
	private Date refreshTime;
	private Float totalUnexchangedBalance;
	private String currentMiningCoin;
	private Integer nbMiners;
	private String note;

	public Float getTotalPaidOut() {
		return totalPaidOut;
	}

	public void setTotalPaidOut(Float totalPaidOut) {
		this.totalPaidOut = totalPaidOut;
	}

	public Float getTotalMegahashesPerSecond() {
		return totalMegahashesPerSecond;
	}

	public void setTotalMegahashesPerSecond(Float totalMegahashesPerSecond) {
		this.totalMegahashesPerSecond = totalMegahashesPerSecond;
	}

	public Float getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(Float totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Date getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(Date time) {
		this.refreshTime = time;
	}

	public Float getTotalUnexchangedBalance() {
		return totalUnexchangedBalance;
	}

	public void setTotalUnexchangedBalance(Float totalUnexchangedBalance) {
		this.totalUnexchangedBalance = totalUnexchangedBalance;
	}

	public String getCurrentMiningCoin() {
		return currentMiningCoin;
	}

	public void setCurrentMiningCoin(String currentMiningCoin) {
		this.currentMiningCoin = currentMiningCoin;
	}

	public Integer getNbMiners() {
		return nbMiners;
	}

	public void setNbMiners(Integer nbMiners) {
		this.nbMiners = nbMiners;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
