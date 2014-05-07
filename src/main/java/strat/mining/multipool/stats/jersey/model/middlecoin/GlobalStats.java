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

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import strat.mining.multipool.stats.jersey.ReportDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalStats {

	private Float totalPaidOut;
	private Float totalRejectedMegahashesPerSecond;
	private Float totalImmatureBalance;
	private Float totalMegahashesPerSecond;
	private Float totalBalance;
	private Date time;
	private Float totalUnexchangedBalance;

	@JsonDeserialize(using = ReportDeserializer.class)
	private Map<String, AddressReport> report;

	public Float getTotalPaidOut() {
		return totalPaidOut;
	}

	public void setTotalPaidOut(Float totalPaidOut) {
		this.totalPaidOut = totalPaidOut;
	}

	public Float getTotalRejectedMegahashesPerSecond() {
		return totalRejectedMegahashesPerSecond;
	}

	public void setTotalRejectedMegahashesPerSecond(Float totalRejectedMegahashesPerSecond) {
		this.totalRejectedMegahashesPerSecond = totalRejectedMegahashesPerSecond;
	}

	public Float getTotalImmatureBalance() {
		return totalImmatureBalance;
	}

	public void setTotalImmatureBalance(Float totalImmatureBalance) {
		this.totalImmatureBalance = totalImmatureBalance;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Map<String, AddressReport> getReport() {
		return report;
	}

	public void setReport(Map<String, AddressReport> report) {
		this.report = report;
	}

	public Float getTotalUnexchangedBalance() {
		return totalUnexchangedBalance;
	}

	public void setTotalUnexchangedBalance(Float totalUnexchangedBalance) {
		this.totalUnexchangedBalance = totalUnexchangedBalance;
	}

}
