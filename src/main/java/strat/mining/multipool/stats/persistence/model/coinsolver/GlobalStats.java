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
package strat.mining.multipool.stats.persistence.model.coinsolver;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "GlobalStats")
public class GlobalStats {

	@Id
	private String idMongo;

	private Float megaHashesPerSeconds;

	private Float totalBalance;

	private Float totalUnexchanged;

	private Float totalImmature;

	private Map<String, Float> miningCoins;

	private Integer nbMiners;

	@Indexed(background = true)
	private Date refreshTime;

	public Float getMegaHashesPerSeconds() {
		return megaHashesPerSeconds;
	}

	public void setMegaHashesPerSeconds(Float megaHashesPerSeconds) {
		this.megaHashesPerSeconds = megaHashesPerSeconds;
	}

	public Float getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(Float totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Float getTotalUnexchanged() {
		return totalUnexchanged;
	}

	public void setTotalUnexchanged(Float totalUnexchanged) {
		this.totalUnexchanged = totalUnexchanged;
	}

	public Date getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(Date refreshTime) {
		this.refreshTime = refreshTime;
	}

	public Float getTotalImmature() {
		return totalImmature;
	}

	public void setTotalImmature(Float totalImmature) {
		this.totalImmature = totalImmature;
	}

	public Map<String, Float> getMiningCoins() {
		return miningCoins;
	}

	public void setMiningCoins(Map<String, Float> miningCoins) {
		this.miningCoins = miningCoins;
	}

	public Integer getNbMiners() {
		return nbMiners;
	}

	public void setNbMiners(Integer nbMiners) {
		this.nbMiners = nbMiners;
	}

}
