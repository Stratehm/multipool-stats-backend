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
package strat.mining.multipool.stats.client.services.rest;

import java.util.List;

import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommonServices {

	/**
	 * Retrieve all the exchange places supported
	 * 
	 * @param callback
	 */
	public void getExchangePlaces(AsyncCallback<List<ExchangePlaceDTO>> callback);

	/**
	 * Retrieve the currency ticker for the given currency and exchange place
	 * 
	 * @param exchangePlaceName
	 * @param currencyCode
	 * @param callback
	 */
	public void getCurrencyTicker(String exchangePlaceName, String currencyCode, AsyncCallback<CurrencyTickerDTO> callback);

	/**
	 * Retrive the given bitcoin address info from blockchain.
	 * 
	 * @param bitcoinAddress
	 * @param callback
	 */
	public void getBlockchainAddressInfo(String bitcoinAddress, AsyncCallback<BlockchainAddressInfoDTO> callback);

	/**
	 * Retrieve the donation details.
	 * 
	 * @param callback
	 */
	public void getDonationDetails(AsyncCallback<DonationDetailsDTO> callback);

	/**
	 * Return the donation details of the given address
	 * 
	 * @param address
	 * @param callback
	 */
	public void getDonationDetailsByAddress(String address, AsyncCallback<AddressDonationDetailsDTO> callback);

}
