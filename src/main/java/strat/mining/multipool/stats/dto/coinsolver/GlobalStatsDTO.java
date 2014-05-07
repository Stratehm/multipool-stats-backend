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
package strat.mining.multipool.stats.dto.coinsolver;

import java.util.Date;
import java.util.List;

import org.jsonmaker.gwt.client.Jsonizer;

public class GlobalStatsDTO {

	public interface GlobalStatsDTOJsonizer extends Jsonizer {
	}

	private Float totalMegahashesPerSecond;
	private Float totalBalance;
	private Float totalUnexchanged;
	private Float totalImmature;
	private Date refreshTime;
	private List<CoinInfoDTO> miningCoins;
	private Integer nbMiners;

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

	public Float getTotalUnexchanged() {
		return totalUnexchanged;
	}

	public void setTotalUnexchanged(Float totalUnexchanged) {
		this.totalUnexchanged = totalUnexchanged;
	}

	public Float getTotalImmature() {
		return totalImmature;
	}

	public void setTotalImmature(Float totalImmature) {
		this.totalImmature = totalImmature;
	}

	public List<CoinInfoDTO> getMiningCoins() {
		return miningCoins;
	}

	public void setMiningCoins(List<CoinInfoDTO> miningCoins) {
		this.miningCoins = miningCoins;
	}

	public Integer getNbMiners() {
		return nbMiners;
	}

	public void setNbMiners(Integer nbMiners) {
		this.nbMiners = nbMiners;
	}

}
