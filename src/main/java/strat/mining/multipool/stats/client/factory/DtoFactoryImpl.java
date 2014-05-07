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

import java.util.ArrayList;
import java.util.List;

import org.jsonmaker.gwt.client.JsonizerParser;
import org.jsonmaker.gwt.client.base.ArrayListJsonizer;

import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO;
import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO.AddressDonationDetailsDTOJsonizer;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO.AddressPaidoutDTOJsonizer;
import strat.mining.multipool.stats.dto.AddressSuggestionDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionDTO.AddressSuggestionDTOJsonizer;
import strat.mining.multipool.stats.dto.AddressSuggestionRequestDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionRequestDTO.AddressSuggestionRequestDTOJsonizer;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO.BlockchainAddressInfoDTOJsonizer;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO.CurrencyTickerDTOJsonizer;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO.DonationDetailsDTOJsonizer;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO.ExchangePlaceDTOJsonizer;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO.AddressStatsDTOJsonizer;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO.GlobalStatsDTOJsonizer;

import com.google.gwt.core.client.GWT;

/**
 * This implementation uses the gwt-jsonmaker library.
 * 
 * @author Strat
 * 
 */
@SuppressWarnings("unchecked")
public class DtoFactoryImpl implements DtoFactory {

	private static AddressStatsDTOJsonizer middlecoinAddressStatsDTOJsonizer = (AddressStatsDTOJsonizer) GWT.create(AddressStatsDTOJsonizer.class);

	private static GlobalStatsDTOJsonizer middlecoinGlobalStatsDTOJsonizer = (GlobalStatsDTOJsonizer) GWT.create(GlobalStatsDTOJsonizer.class);

	private static AddressPaidoutDTOJsonizer addressPaidoutDTOJsonizer = (AddressPaidoutDTOJsonizer) GWT.create(AddressPaidoutDTOJsonizer.class);

	private static strat.mining.multipool.stats.dto.waffle.AddressStatsDTO.AddressStatsDTOJsonizer waffleAddressStatsDTOJsonizer = (strat.mining.multipool.stats.dto.waffle.AddressStatsDTO.AddressStatsDTOJsonizer) GWT
			.create(strat.mining.multipool.stats.dto.waffle.AddressStatsDTO.AddressStatsDTOJsonizer.class);

	private static strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO.GlobalStatsDTOJsonizer waffleGlobalStatsDTOJsonizer = (strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO.GlobalStatsDTOJsonizer) GWT
			.create(strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO.GlobalStatsDTOJsonizer.class);

	private static strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO.AddressStatsDTOJsonizer coinshiftAddressStatsDTOJsonizer = (strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO.AddressStatsDTOJsonizer) GWT
			.create(strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO.AddressStatsDTOJsonizer.class);

	private static strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO.GlobalStatsDTOJsonizer coinshiftGlobalStatsDTOJsonizer = (strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO.GlobalStatsDTOJsonizer) GWT
			.create(strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO.GlobalStatsDTOJsonizer.class);

	private static strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO.AddressStatsDTOJsonizer coinsolverAddressStatsDTOJsonizer = (strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO.AddressStatsDTOJsonizer) GWT
			.create(strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO.AddressStatsDTOJsonizer.class);

	private static strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO.GlobalStatsDTOJsonizer coinsolverGlobalStatsDTOJsonizer = (strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO.GlobalStatsDTOJsonizer) GWT
			.create(strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO.GlobalStatsDTOJsonizer.class);

	private static strat.mining.multipool.stats.dto.coinsolver.AddressPaidoutDTO.AddressPaidoutDTOJsonizer addressCoinsolverPaidoutDTOJsonizer = (strat.mining.multipool.stats.dto.coinsolver.AddressPaidoutDTO.AddressPaidoutDTOJsonizer) GWT
			.create(strat.mining.multipool.stats.dto.coinsolver.AddressPaidoutDTO.AddressPaidoutDTOJsonizer.class);

	private static AddressSuggestionDTOJsonizer addressSuggestionDTOJsonizer = (AddressSuggestionDTOJsonizer) GWT
			.create(AddressSuggestionDTOJsonizer.class);

	private static AddressSuggestionRequestDTOJsonizer addressSuggestionRequestDTOJsonizer = (AddressSuggestionRequestDTOJsonizer) GWT
			.create(AddressSuggestionRequestDTOJsonizer.class);

	private static ExchangePlaceDTOJsonizer exchangePlaceDTOJsonizer = (ExchangePlaceDTOJsonizer) GWT.create(ExchangePlaceDTOJsonizer.class);

	private static CurrencyTickerDTOJsonizer currencyTickerDTOJsonizer = (CurrencyTickerDTOJsonizer) GWT.create(CurrencyTickerDTOJsonizer.class);

	private static BlockchainAddressInfoDTOJsonizer blockchainAddressInfoDTOJsonizer = (BlockchainAddressInfoDTOJsonizer) GWT
			.create(BlockchainAddressInfoDTOJsonizer.class);

	private static DonationDetailsDTOJsonizer donationDetailsDTOJsonizer = (DonationDetailsDTOJsonizer) GWT.create(DonationDetailsDTOJsonizer.class);

	private static AddressDonationDetailsDTOJsonizer addressDonationDetailsDTOJsonizer = (AddressDonationDetailsDTOJsonizer) GWT
			.create(AddressDonationDetailsDTOJsonizer.class);

	private static ArrayListJsonizer listMiddlecoinAddressStatsJsonizer = new ArrayListJsonizer(middlecoinAddressStatsDTOJsonizer);
	private static ArrayListJsonizer listMiddlecoinGlobalStatsJsonizer = new ArrayListJsonizer(middlecoinGlobalStatsDTOJsonizer);
	private static ArrayListJsonizer listAddressPaidoutJsonizer = new ArrayListJsonizer(addressPaidoutDTOJsonizer);
	private static ArrayListJsonizer listExchangePlaceJsonizer = new ArrayListJsonizer(exchangePlaceDTOJsonizer);

	private static ArrayListJsonizer listWaffleAddressStatsJsonizer = new ArrayListJsonizer(waffleAddressStatsDTOJsonizer);
	private static ArrayListJsonizer listWaffleGlobalStatsJsonizer = new ArrayListJsonizer(waffleGlobalStatsDTOJsonizer);

	private static ArrayListJsonizer listCoinshiftAddressStatsJsonizer = new ArrayListJsonizer(coinshiftAddressStatsDTOJsonizer);
	private static ArrayListJsonizer listCoinshiftGlobalStatsJsonizer = new ArrayListJsonizer(coinshiftGlobalStatsDTOJsonizer);

	private static ArrayListJsonizer listCoinsolverAddressStatsJsonizer = new ArrayListJsonizer(coinsolverAddressStatsDTOJsonizer);
	private static ArrayListJsonizer listCoinsolverGlobalStatsJsonizer = new ArrayListJsonizer(coinsolverGlobalStatsDTOJsonizer);
	private static ArrayListJsonizer listCoinsolverAddressPaidoutJsonizer = new ArrayListJsonizer(addressCoinsolverPaidoutDTOJsonizer);

	@Override
	public List<AddressStatsDTO> parseJsonToMiddlecoinAddressStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listMiddlecoinAddressStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public List<GlobalStatsDTO> parseJsonToMiddlecoinGlobalStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listMiddlecoinGlobalStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public AddressStatsDTO parseJsonToMiddlecoinAddressStats(String json) {
		return (AddressStatsDTO) JsonizerParser.parse(middlecoinAddressStatsDTOJsonizer, json);
	}

	@Override
	public GlobalStatsDTO parseJsonToMiddlecoinGlobalStats(String json) {
		return (GlobalStatsDTO) JsonizerParser.parse(middlecoinGlobalStatsDTOJsonizer, json);
	}

	@Override
	public List<AddressPaidoutDTO> parseJsonToAddressPaidoutList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listAddressPaidoutJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public AddressSuggestionDTO parseJsonToAddressSuggestion(String json) {
		return (AddressSuggestionDTO) JsonizerParser.parse(addressSuggestionDTOJsonizer, json);
	}

	@Override
	public String parseAddressSuggestionRequestToJson(AddressSuggestionRequestDTO request) {
		return addressSuggestionRequestDTOJsonizer.asString(request);
	}

	@Override
	public List<ExchangePlaceDTO> parseJsonToExchangePlaceList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listExchangePlaceJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public CurrencyTickerDTO parseJsonToCurrencyTicker(String json) {
		return (CurrencyTickerDTO) JsonizerParser.parse(currencyTickerDTOJsonizer, json);
	}

	@Override
	public BlockchainAddressInfoDTO parseJsonToBlockchainAddressInfo(String json) {
		return (BlockchainAddressInfoDTO) JsonizerParser.parse(blockchainAddressInfoDTOJsonizer, json);
	}

	@Override
	public DonationDetailsDTO parseJsonToDonationDetails(String json) {
		return (DonationDetailsDTO) JsonizerParser.parse(donationDetailsDTOJsonizer, json);
	}

	@Override
	public List<strat.mining.multipool.stats.dto.waffle.AddressStatsDTO> parseJsonToWaffleAddressStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listWaffleAddressStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public List<strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO> parseJsonToWaffleGlobalStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listWaffleGlobalStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public strat.mining.multipool.stats.dto.waffle.AddressStatsDTO parseJsonToWaffleAddressStats(String json) {
		return (strat.mining.multipool.stats.dto.waffle.AddressStatsDTO) JsonizerParser.parse(waffleAddressStatsDTOJsonizer, json);
	}

	@Override
	public strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO parseJsonToWaffleGlobalStats(String json) {
		return (strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO) JsonizerParser.parse(waffleGlobalStatsDTOJsonizer, json);
	}

	@Override
	public List<strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO> parseJsonToCoinshiftAddressStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listCoinshiftAddressStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public List<strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO> parseJsonToCoinshiftGlobalStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listCoinshiftGlobalStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO parseJsonToCoinshiftAddressStats(String json) {
		return (strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO) JsonizerParser.parse(coinshiftAddressStatsDTOJsonizer, json);
	}

	@Override
	public strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO parseJsonToCoinshiftGlobalStats(String json) {
		return (strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO) JsonizerParser.parse(coinshiftGlobalStatsDTOJsonizer, json);
	}

	@Override
	public List<strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO> parseJsonToCoinsolverAddressStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listCoinsolverAddressStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public List<strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO> parseJsonToCoinsolverGlobalStatsList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listCoinsolverGlobalStatsJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO parseJsonToCoinsolverAddressStats(String json) {
		return (strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO) JsonizerParser.parse(coinsolverAddressStatsDTOJsonizer, json);
	}

	@Override
	public strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO parseJsonToCoinsolverGlobalStats(String json) {
		return (strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO) JsonizerParser.parse(coinsolverGlobalStatsDTOJsonizer, json);
	}

	@Override
	public List<strat.mining.multipool.stats.dto.coinsolver.AddressPaidoutDTO> parseJsonToCoinsolverAddressPaidoutList(String json) {
		List<Object> parsedObjects = (List<Object>) JsonizerParser.parse(listCoinsolverAddressPaidoutJsonizer, json);
		return convertList(parsedObjects);
	}

	@Override
	public AddressDonationDetailsDTO parseJsonToAddressDonationDetails(String json) {
		return (AddressDonationDetailsDTO) JsonizerParser.parse(addressDonationDetailsDTOJsonizer, json);
	}

	/**
	 * Convert the given list of Objects in a list of another type.
	 * 
	 * @param listToConvert
	 * @return
	 */
	private <T> List<T> convertList(List<Object> listToConvert) {
		List<T> result = new ArrayList<T>();
		if (listToConvert != null) {
			for (Object object : listToConvert) {
				result.add((T) object);
			}
		}
		return result;
	}

}
