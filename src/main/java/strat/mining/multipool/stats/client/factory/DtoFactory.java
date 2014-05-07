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
package strat.mining.multipool.stats.client.factory;

import java.util.List;

import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionRequestDTO;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO;

/**
 * Factory implementation to parse Json String to POJO and to parse POJO as
 * String.
 * 
 * @author Strat
 * 
 */
public interface DtoFactory {

	/**
	 * Parse the given JSON string into a List of AddressStatsDTO.
	 * 
	 * @param json
	 * @return
	 */
	public List<AddressStatsDTO> parseJsonToMiddlecoinAddressStatsList(String json);

	/**
	 * Parse the given JSON into a List of GlobalStatsDTO
	 * 
	 * @param json
	 * @return
	 */
	public List<GlobalStatsDTO> parseJsonToMiddlecoinGlobalStatsList(String json);

	/**
	 * Parse the given JSON string into a AddressStatsDTO.
	 * 
	 * @param json
	 * @return
	 */
	public AddressStatsDTO parseJsonToMiddlecoinAddressStats(String json);

	/**
	 * Parse the given JSON string into a GlobalStatsDTO.
	 * 
	 * @param json
	 * @return
	 */
	public GlobalStatsDTO parseJsonToMiddlecoinGlobalStats(String json);

	/**
	 * Parse the given json to a list of AddressPaidoutDTO
	 * 
	 * @param json
	 * @return
	 */
	public List<AddressPaidoutDTO> parseJsonToAddressPaidoutList(String json);

	/**
	 * Parse the given json to AddressSuggestionDTO
	 * 
	 * @param json
	 * @return
	 */
	public AddressSuggestionDTO parseJsonToAddressSuggestion(String json);

	/**
	 * Parse the given DTO in json
	 * 
	 * @param request
	 * @return
	 */
	public String parseAddressSuggestionRequestToJson(AddressSuggestionRequestDTO request);

	/**
	 * Parse the given json to a list of ExchangePlaceDTO
	 * 
	 * @param json
	 * @return
	 */
	public List<ExchangePlaceDTO> parseJsonToExchangePlaceList(String json);

	/**
	 * Parse the given json to a CurrencyTickerDTO
	 * 
	 * @param json
	 * @return
	 */
	public CurrencyTickerDTO parseJsonToCurrencyTicker(String json);

	/**
	 * Parse the given json to a BlockchainAddressInfoDTO
	 * 
	 * @param json
	 * @return
	 */
	public BlockchainAddressInfoDTO parseJsonToBlockchainAddressInfo(String json);

	/**
	 * Parse the given json to a DonationDetailsDTO
	 * 
	 * @param json
	 * @return
	 */
	public DonationDetailsDTO parseJsonToDonationDetails(String json);

	/**
	 * Parse the given json to a AddressDonationDetailsDTO
	 * 
	 * @param json
	 * @return
	 */
	public AddressDonationDetailsDTO parseJsonToAddressDonationDetails(String json);

	/**
	 * Parse the given json to a list of address stats
	 * 
	 * @param json
	 * @return
	 */
	public List<strat.mining.multipool.stats.dto.waffle.AddressStatsDTO> parseJsonToWaffleAddressStatsList(String json);

	/**
	 * Parse the given json to a list of global stats
	 * 
	 * @param json
	 * @return
	 */
	public List<strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO> parseJsonToWaffleGlobalStatsList(String json);

	/**
	 * Parse the given json to an address stats
	 * 
	 * @param json
	 * @return
	 */
	public strat.mining.multipool.stats.dto.waffle.AddressStatsDTO parseJsonToWaffleAddressStats(String json);

	/**
	 * Parse the given json to a global stats
	 * 
	 * @param json
	 * @return
	 */
	public strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO parseJsonToWaffleGlobalStats(String json);

	/**
	 * Parse the given json to a list of address stats
	 * 
	 * @param json
	 * @return
	 */
	public List<strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO> parseJsonToCoinshiftAddressStatsList(String json);

	/**
	 * Parse the given json to a list of global stats
	 * 
	 * @param json
	 * @return
	 */
	public List<strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO> parseJsonToCoinshiftGlobalStatsList(String json);

	/**
	 * Parse the given json to an address stats
	 * 
	 * @param json
	 * @return
	 */
	public strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO parseJsonToCoinshiftAddressStats(String json);

	/**
	 * Parse the given json to a global stats
	 * 
	 * @param json
	 * @return
	 */
	public strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO parseJsonToCoinshiftGlobalStats(String json);

	/**
	 * Parse the given json to a list of address stats
	 * 
	 * @param json
	 * @return
	 */
	public List<strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO> parseJsonToCoinsolverAddressStatsList(String json);

	/**
	 * Parse the given json to a list of global stats
	 * 
	 * @param json
	 * @return
	 */
	public List<strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO> parseJsonToCoinsolverGlobalStatsList(String json);

	/**
	 * Parse the given json to an address stats
	 * 
	 * @param json
	 * @return
	 */
	public strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO parseJsonToCoinsolverAddressStats(String json);

	/**
	 * Parse the given json to a global stats
	 * 
	 * @param json
	 * @return
	 */
	public strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO parseJsonToCoinsolverGlobalStats(String json);

	/**
	 * Parse the given json to a list of AddressPaidoutDTO
	 * 
	 * @param json
	 * @return
	 */
	public List<strat.mining.multipool.stats.dto.coinsolver.AddressPaidoutDTO> parseJsonToCoinsolverAddressPaidoutList(String json);
}
