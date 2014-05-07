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
package strat.mining.multipool.stats.jersey.model.middlecoin;

public class AddressReport {

	private Integer lastHourShares;
	private Float immatureBalance;
	private Integer lastHourRejectedShares;
	private Float paidOut;
	private Float unexchangedBalance;
	private Float megahashesPerSecond;
	private Float bitcoinBalance;
	private Float rejectedMegahashesPerSecond;

	public Integer getLastHourShares() {
		return lastHourShares;
	}

	public void setLastHourShares(Integer lastHourShares) {
		this.lastHourShares = lastHourShares;
	}

	public Float getImmatureBalance() {
		return immatureBalance;
	}

	public void setImmatureBalance(Float immatureBalance) {
		this.immatureBalance = immatureBalance;
	}

	public Integer getLastHourRejectedShares() {
		return lastHourRejectedShares;
	}

	public void setLastHourRejectedShares(Integer lastHourRejectedShares) {
		this.lastHourRejectedShares = lastHourRejectedShares;
	}

	public Float getPaidOut() {
		return paidOut;
	}

	public void setPaidOut(Float paidOut) {
		this.paidOut = paidOut;
	}

	public Float getUnexchangedBalance() {
		return unexchangedBalance;
	}

	public void setUnexchangedBalance(Float unexchangedBalance) {
		this.unexchangedBalance = unexchangedBalance;
	}

	public Float getMegahashesPerSecond() {
		return megahashesPerSecond;
	}

	public void setMegahashesPerSecond(Float megahashesPerSecond) {
		this.megahashesPerSecond = megahashesPerSecond;
	}

	public Float getBitcoinBalance() {
		return bitcoinBalance;
	}

	public void setBitcoinBalance(Float bitcoinBalance) {
		this.bitcoinBalance = bitcoinBalance;
	}

	public Float getRejectedMegahashesPerSecond() {
		return rejectedMegahashesPerSecond;
	}

	public void setRejectedMegahashesPerSecond(Float rejectedMegahashesPerSecond) {
		this.rejectedMegahashesPerSecond = rejectedMegahashesPerSecond;
	}

}
