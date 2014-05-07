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
package strat.mining.multipool.stats.jersey.model.coinsolver;

public class Currently_mining {
	private String blocks;
	private String blocktime;
	private Number btc_per_day_estimate;
	private String difficulty;
	private String enabled;
	private String fullname;
	private String hashrate;
	private String name;
	private Number poolhashrate;
	private String reward;
	private String tradePrice;
	private String unexchangedBalance;
	private Number usd_per_day_estimate;

	public String getBlocks() {
		return this.blocks;
	}

	public void setBlocks(String blocks) {
		this.blocks = blocks;
	}

	public String getBlocktime() {
		return this.blocktime;
	}

	public void setBlocktime(String blocktime) {
		this.blocktime = blocktime;
	}

	public Number getBtc_per_day_estimate() {
		return this.btc_per_day_estimate;
	}

	public void setBtc_per_day_estimate(Number btc_per_day_estimate) {
		this.btc_per_day_estimate = btc_per_day_estimate;
	}

	public String getDifficulty() {
		return this.difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String getEnabled() {
		return this.enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getHashrate() {
		return this.hashrate;
	}

	public void setHashrate(String hashrate) {
		this.hashrate = hashrate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getPoolhashrate() {
		return this.poolhashrate;
	}

	public void setPoolhashrate(Number poolhashrate) {
		this.poolhashrate = poolhashrate;
	}

	public String getReward() {
		return this.reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getTradePrice() {
		return this.tradePrice;
	}

	public void setTradePrice(String tradePrice) {
		this.tradePrice = tradePrice;
	}

	public String getUnexchangedBalance() {
		return this.unexchangedBalance;
	}

	public void setUnexchangedBalance(String unexchangedBalance) {
		this.unexchangedBalance = unexchangedBalance;
	}

	public Number getUsd_per_day_estimate() {
		return this.usd_per_day_estimate;
	}

	public void setUsd_per_day_estimate(Number usd_per_day_estimate) {
		this.usd_per_day_estimate = usd_per_day_estimate;
	}
}
