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
package strat.mining.multipool.stats.client.mvp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import strat.mining.multipool.stats.client.util.Pair;
import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

/**
 * Contains all data of the application.
 * 
 * @author Strat
 * 
 */
public class MainDataContainer {

	private List<ExchangePlaceDTO> exchangePlaces;
	// Pair of exchangePlaceName/currencyCode
	private Map<Pair<String, String>, CurrencyTickerDTO> tickers;

	private DonationDetailsDTO donationDetails;

	private List<AddressDonationDetailsDTO> addressDonationDetails;

	public List<ExchangePlaceDTO> getExchangePlaces() {
		if (exchangePlaces == null) {
			exchangePlaces = new ArrayList<ExchangePlaceDTO>();
		}
		return exchangePlaces;
	}

	public Map<Pair<String, String>, CurrencyTickerDTO> getTickers() {
		if (tickers == null) {
			tickers = new HashMap<Pair<String, String>, CurrencyTickerDTO>();
		}
		return tickers;
	}

	public DonationDetailsDTO getDonationDetails() {
		return donationDetails;
	}

	public void setDonationDetails(DonationDetailsDTO donationDetails) {
		this.donationDetails = donationDetails;
	}

	public List<AddressDonationDetailsDTO> getAddressDonationDetails() {
		if (addressDonationDetails == null) {
			addressDonationDetails = new ArrayList<AddressDonationDetailsDTO>();
		}
		return addressDonationDetails;
	}

}
