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
import strat.mining.multipool.stats.jersey.TextHtmlJsonFilter;
import strat.mining.multipool.stats.jersey.client.iface.CurrencyTickerClient;
import strat.mining.multipool.stats.jersey.model.btce.BtceTicker;

@Component("btceCurrencyTickerClient")
public class BtceCurrencyTickerClient implements CurrencyTickerClient {

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger LOGGER = LoggerFactory.getLogger(BtceCurrencyTickerClient.class);

	private Client client;

	public BtceCurrencyTickerClient() {
		ClientConfig config = new ClientConfig().register(TextHtmlJsonFilter.class).register(JacksonFeature.class);
		client = ClientBuilder.newClient(config);
		client.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		client.property(ClientProperties.READ_TIMEOUT, 5000);
	}

	@Override
	public CurrencyTickerDTO getCurrencyTicker(String currencyCode) {
		CurrencyTickerDTO result = new CurrencyTickerDTO();

		try {
			LOGGER.debug("Retrieving ticker from BTC-e for currency {}.", currencyCode);
			long start = System.currentTimeMillis();
			BtceTicker response = client.target("https://btc-e.com").path("api/2/btc_" + currencyCode.toLowerCase() + "/ticker")
					.request(MediaType.APPLICATION_JSON).get(BtceTicker.class);
			PERF_LOGGER.info("Ticker retrieved from BTC-e for currency {} in {} ms.", currencyCode, System.currentTimeMillis() - start);

			if (response != null) {
				result.setCurrencyCode(currencyCode);
				result.setRefreshTime(new Date());
				result.setBuy(response.getTicker().getBuy());
				result.setHigh(response.getTicker().getHigh());
				result.setLast(response.getTicker().getLast());
				result.setLow(response.getTicker().getLow());
				result.setSell(response.getTicker().getSell());
				result.setVolume(response.getTicker().getVol_cur());
			} else {
				LOGGER.warn("Failed to retrieve the BTC-e ticker for currency {} with no reason.", currencyCode);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve the BTC-e ticker for currency {}.", currencyCode, e);
		}
		return result;
	}

}
