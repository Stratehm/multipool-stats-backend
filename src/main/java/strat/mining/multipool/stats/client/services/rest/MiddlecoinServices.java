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

import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionRequestDTO;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The available services on the server.
 * 
 * @author Strat
 * 
 */
public interface MiddlecoinServices {

	/**
	 * Retrieve all global stats from the server.
	 * 
	 * @param callback
	 */
	public void getAllGlobalStats(AsyncCallback<List<GlobalStatsDTO>> callback);

	/**
	 * Retrieve last address stats from the server.
	 * 
	 * @param callback
	 */
	public void getLastAddressStats(String address, AsyncCallback<AddressStatsDTO> callback);

	/**
	 * Retrieve all address stats from the server.
	 * 
	 * @param callback
	 */
	public void getAllAddressStats(String address, AsyncCallback<List<AddressStatsDTO>> callback);

	/**
	 * Retrieve the last global stats from the server.
	 * 
	 * @param callback
	 */
	public void getLastGlobalStats(AsyncCallback<GlobalStatsDTO> callback);

	/**
	 * Retrieve all the paidou from the server for the given address.
	 * 
	 * @param address
	 * @param callback
	 */
	public void getAllAddressPaidout(String address, AsyncCallback<List<AddressPaidoutDTO>> callback);

	/**
	 * Retrieve the address suggestion based on the given address pattern.
	 * 
	 * @param addressPattern
	 * @param callback
	 */
	public void getSuggestions(AddressSuggestionRequestDTO request, AsyncCallback<AddressSuggestionDTO> callback);

}
