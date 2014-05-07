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

import java.util.List;

import org.jsonmaker.gwt.client.Jsonizer;

public class DonationDetailsDTO {

	public interface DonationDetailsDTOJsonizer extends Jsonizer {
	}

	private String donationBtcAddress;
	private float rentPriceInEuro;
	private float btcPriceInEuro;
	private float donationsInBTC;
	private float donationsNeeded;
	private List<DonationTransactionDetailsDTO> transactions;
	private float lastMonthDonationsValue;

	public String getDonationBtcAddress() {
		return donationBtcAddress;
	}

	public void setDonationBtcAddress(String donationBtcAddress) {
		this.donationBtcAddress = donationBtcAddress;
	}

	public float getRentPriceInEuro() {
		return rentPriceInEuro;
	}

	public void setRentPriceInEuro(float rentPriceInEuro) {
		this.rentPriceInEuro = rentPriceInEuro;
	}

	public float getBtcPriceInEuro() {
		return btcPriceInEuro;
	}

	public void setBtcPriceInEuro(float btcPriceInEuro) {
		this.btcPriceInEuro = btcPriceInEuro;
	}

	public float getDonationsInBTC() {
		return donationsInBTC;
	}

	public void setDonationsInBTC(float donationsInBTC) {
		this.donationsInBTC = donationsInBTC;
	}

	public float getDonationsNeeded() {
		return donationsNeeded;
	}

	public void setDonationsNeeded(float donationsNeeded) {
		this.donationsNeeded = donationsNeeded;
	}

	public List<DonationTransactionDetailsDTO> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<DonationTransactionDetailsDTO> transactions) {
		this.transactions = transactions;
	}

	public float getLastMonthDonationsValue() {
		return lastMonthDonationsValue;
	}

	public void setLastMonthDonationsValue(float lastMonthDonationsValue) {
		this.lastMonthDonationsValue = lastMonthDonationsValue;
	}

}
