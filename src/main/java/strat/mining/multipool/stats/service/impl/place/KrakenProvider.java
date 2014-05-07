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
package strat.mining.multipool.stats.service.impl.place;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.jersey.client.iface.CurrencyTickerClient;
import strat.mining.multipool.stats.service.iface.place.ExchangePlaceProvider;

import com.google.common.collect.Lists;

@Component("krakenProvider")
public class KrakenProvider implements ExchangePlaceProvider {

	public static final String EXCHANGE_NAME = "kraken";

	private static final ExchangePlaceDTO exchangePlaceInfo = new ExchangePlaceDTO();

	@Resource(name = "krakenCurrencyTickerClient")
	private CurrencyTickerClient krakenClient;

	public KrakenProvider() {
		exchangePlaceInfo.setLabel("Kraken");
		exchangePlaceInfo.setName(EXCHANGE_NAME);
		exchangePlaceInfo.setCurrencyCodes(Lists.newArrayList("USD", "EUR"));

	}

	@Override
	public ExchangePlaceDTO getExchangePlaceInfo() {
		return exchangePlaceInfo;
	}

	@Override
	public CurrencyTickerDTO getTicker(String currencyCode) {
		return krakenClient.getCurrencyTicker(currencyCode);
	}

	@Override
	public List<CurrencyTickerDTO> getTickers() {
		List<CurrencyTickerDTO> result = new ArrayList<>();
		for (String currencyCode : exchangePlaceInfo.getCurrencyCodes()) {
			CurrencyTickerDTO response = krakenClient.getCurrencyTicker(currencyCode);
			if (response != null) {
				result.add(response);
			}
		}
		return result;
	}

}
