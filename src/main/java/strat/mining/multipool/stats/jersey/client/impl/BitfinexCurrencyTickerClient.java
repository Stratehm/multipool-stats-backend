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
import strat.mining.multipool.stats.jersey.model.bitfinex.BitfinexTicker;
import strat.mining.multipool.stats.jersey.model.bitfinex.BitfinexToday;

@Component("bitfinexCurrencyTickerClient")
public class BitfinexCurrencyTickerClient implements CurrencyTickerClient {

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger LOGGER = LoggerFactory.getLogger(BitfinexCurrencyTickerClient.class);

	private Client client;

	public BitfinexCurrencyTickerClient() {
		ClientConfig config = new ClientConfig().register(JacksonFeature.class);
		client = ClientBuilder.newClient(config);
		client.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		client.property(ClientProperties.READ_TIMEOUT, 5000);
	}

	@Override
	public CurrencyTickerDTO getCurrencyTicker(String currencyCode) {
		CurrencyTickerDTO result = new CurrencyTickerDTO();

		try {
			LOGGER.debug("Retrieving ticker from Bitfinex for currency USD.");
			long start = System.currentTimeMillis();
			BitfinexTicker responseTicker = client.target("https://api.bitfinex.com").path("v1/ticker/btcusd").request(MediaType.APPLICATION_JSON)
					.get(BitfinexTicker.class);
			PERF_LOGGER.info("Ticker retrieved from Bitfinex for currency USD in {} ms.", System.currentTimeMillis() - start);

			start = System.currentTimeMillis();
			BitfinexToday responseToday = client.target("https://api.bitfinex.com").path("v1/today/btcusd").request(MediaType.APPLICATION_JSON)
					.get(BitfinexToday.class);
			PERF_LOGGER.info("Today info retrieved from Bitfinex for currency USD in {} ms.", System.currentTimeMillis() - start);

			if (responseTicker != null) {
				result.setCurrencyCode(currencyCode);
				result.setRefreshTime(new Date());
				result.setLast(Float.valueOf(responseTicker.getLast_price()));
				result.setBuy(Float.valueOf(responseTicker.getBid()));
				result.setSell(Float.valueOf(responseTicker.getAsk()));

			} else {
				LOGGER.warn("Failed to retrieve the Bitfinex ticker for currency USD with no reason.");
			}

			if (responseToday != null) {
				result.setLow(Float.valueOf(responseToday.getLow()));
				result.setHigh(Float.valueOf(responseToday.getHigh()));
				result.setVolume(Float.valueOf(responseToday.getVolume()));
			} else {
				LOGGER.warn("Failed to retrieve the Bitfinex today for currency USD with no reason.");
			}
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve the Bitfinex ticker for currency USD.", e);
		}
		return result;
	}
}
