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
package strat.mining.multipool.stats.jersey.client.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.jersey.client.iface.CurrencyTickerClient;
import strat.mining.multipool.stats.jersey.model.kraken.KrakenTicker;

@Component("krakenCurrencyTickerClient")
public class KrakenCurrencyTickerClient implements CurrencyTickerClient {

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger LOGGER = LoggerFactory.getLogger(KrakenCurrencyTickerClient.class);

	private Client client;

	public KrakenCurrencyTickerClient() {
		ClientConfig config = new ClientConfig().register(JacksonFeature.class);
		client = ClientBuilder.newClient(config);
		client.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		client.property(ClientProperties.READ_TIMEOUT, 5000);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CurrencyTickerDTO getCurrencyTicker(String currencyCode) {
		CurrencyTickerDTO result = new CurrencyTickerDTO();

		try {
			LOGGER.debug("Retrieving ticker from Kraken for currency {}.", currencyCode);
			long start = System.currentTimeMillis();
			KrakenTicker response = client.target("https://api.kraken.com").path("0/public/Ticker").queryParam("pair", "XBT" + currencyCode)
					.request(MediaType.APPLICATION_JSON).get(KrakenTicker.class);
			PERF_LOGGER.info("Ticker retrieved from Kraken for currency {} in {} ms.", currencyCode, System.currentTimeMillis() - start);

			if (response != null) {
				result.setCurrencyCode(currencyCode);
				result.setRefreshTime(new Date());
				// Treat only the first value (there should be only one)
				for (Entry<String, Map<String, Object>> entry : response.getResult().entrySet()) {
					Map<String, Object> values = entry.getValue();
					List<String> list = (List<String>) values.get("a");
					result.setSell(Float.valueOf(list.get(0)));

					list = (List<String>) values.get("b");
					result.setBuy(Float.valueOf(list.get(0)));

					list = (List<String>) values.get("v");
					result.setVolume(Float.valueOf(list.get(0)));

					list = (List<String>) values.get("l");
					result.setLow(Float.valueOf(list.get(0)));

					list = (List<String>) values.get("h");
					result.setHigh(Float.valueOf(list.get(0)));

					list = (List<String>) values.get("c");
					result.setLast(Float.valueOf(list.get(0)));
				}
			} else {
				LOGGER.warn("Failed to retrieve the Kraken ticker for currency {} with no reason.", currencyCode);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve the Kraken ticker for currency {}.", currencyCode, e);
		}
		return result;
	}
}
