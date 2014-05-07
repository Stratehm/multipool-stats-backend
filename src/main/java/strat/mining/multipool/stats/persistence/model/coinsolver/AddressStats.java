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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "AddressStats")
public class AddressStats {

	@Id
	private String id;

	@Indexed
	private Integer addressId;

	private Float hashRate;

	private Float balance;

	private Float unexchanged;

	private Float immature;

	private Float paidout;

	@Indexed(background = true)
	private Date refreshTime;

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public Float getHashRate() {
		return hashRate;
	}

	public void setHashRate(Float hashRate) {
		this.hashRate = hashRate;
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

	public Float getPaidout() {
		return paidout;
	}

	public void setPaidout(Float paidout) {
		this.paidout = paidout;
	}

	public Date getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(Date refreshTime) {
		this.refreshTime = refreshTime;
	}

	public Float getImmature() {
		return immature;
	}

	public void setImmature(Float immature) {
		this.immature = immature;
	}

}
