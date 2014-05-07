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
package strat.mining.multipool.stats.dto.coinshift;

import java.util.Date;

import org.jsonmaker.gwt.client.Jsonizer;

public class GlobalStatsDTO {

	public interface GlobalStatsDTOJsonizer extends Jsonizer {
	}

	private Float totalMegahashesPerSecond;
	private Float totalRejectedMegahashesPerSecond;
	private Float totalBalance;
	private Date refreshTime;
	private Float totalUnexchangedBalance;

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

	public Float getTotalRejectedMegahashesPerSecond() {
		return totalRejectedMegahashesPerSecond;
	}

	public void setTotalRejectedMegahashesPerSecond(Float totalRejectedMegahashesPerSecond) {
		this.totalRejectedMegahashesPerSecond = totalRejectedMegahashesPerSecond;
	}

}
