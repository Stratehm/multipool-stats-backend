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
import strat.mining.multipool.stats.jersey.model.bitstamp.BitstampTicker;

@Component("bitstampCurrencyTickerClient")
public class BitstampCurrencyTickerClient implements CurrencyTickerClient {

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger LOGGER = LoggerFactory.getLogger(BitstampCurrencyTickerClient.class);

	private Client client;

	public BitstampCurrencyTickerClient() {
		ClientConfig config = new ClientConfig().register(JacksonFeature.class);
		client = ClientBuilder.newClient(config);
		client.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		client.property(ClientProperties.READ_TIMEOUT, 5000);
	}

	@Override
	public CurrencyTickerDTO getCurrencyTicker(String currencyCode) {
		CurrencyTickerDTO result = new CurrencyTickerDTO();
		try {
			LOGGER.debug("Retrieving ticker from Bitstamp for currency USD.");
			long start = System.currentTimeMillis();
			BitstampTicker response = client.target("https://www.bitstamp.net").path("api/ticker/").request(MediaType.APPLICATION_JSON)
					.get(BitstampTicker.class);
			PERF_LOGGER.info("Ticker retrieved from Bitstamp for currency USD in {} ms.", System.currentTimeMillis() - start);

			if (response != null) {
				result.setCurrencyCode("USD");
				result.setRefreshTime(new Date());
				result.setBuy(Float.parseFloat(response.getBid()));
				result.setHigh(Float.parseFloat(response.getHigh()));
				result.setLast(Float.parseFloat(response.getLast()));
				result.setLow(Float.parseFloat(response.getLow()));
				result.setSell(Float.parseFloat(response.getAsk()));
				result.setVolume(Float.parseFloat(response.getVolume()));
			} else {
				LOGGER.warn("Failed to retrieve the Bitstamp ticker for currency USD with no reason.", currencyCode);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve the Bitstamp ticker for currency USD.", currencyCode, e);
		}
		return result;
	}
}
