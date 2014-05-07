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

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.jersey.ObjectMapperResolver;
import strat.mining.multipool.stats.jersey.TextHtmlJsonFilter;
import strat.mining.multipool.stats.jersey.client.iface.WaffleRestClient;
import strat.mining.multipool.stats.jersey.model.waffle.AddressStats;
import strat.mining.multipool.stats.persistence.model.waffle.GlobalStats;

@Component("waffleRestClient")
public class WaffleRestClientImpl implements WaffleRestClient {

	private static final String WAFFLE_POOL_GLOBAL_STATS_URL = "http://wafflepool.com/stats";

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger LOGGER = LoggerFactory.getLogger(WaffleRestClientImpl.class);

	private Client client;

	public WaffleRestClientImpl() {
		ClientConfig config = new ClientConfig().register(TextHtmlJsonFilter.class).register(JacksonFeature.class);
		config.register(ObjectMapperResolver.class);
		client = ClientBuilder.newClient(config);
		client.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		client.property(ClientProperties.READ_TIMEOUT, 5000);
	}

	@Override
	public GlobalStats getGlobalStats() {
		GlobalStats result = null;
		try {
			LOGGER.debug("Start to get the waffle global stats.");
			long startTime = System.currentTimeMillis();
			Document statsPage = Jsoup.connect(WAFFLE_POOL_GLOBAL_STATS_URL)
					.userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)").get();
			PERF_LOGGER.info("Retrieved Wafflepool stats page in {} ms.", System.currentTimeMillis() - startTime);

			result = new GlobalStats();
			try {
				Elements noteElements = statsPage.select("#note");
				if (noteElements != null && !noteElements.isEmpty()) {
					String style = noteElements.get(0).attr("style");
					if (style == null || (!style.contains("display:none") && !style.contains("visibility:hidden"))) {
						String note = noteElements.get(0).html();
						result.setNote(note);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Failed to get the last note.", e);
			}

			Elements headersElements = statsPage.select("#pool_stats");
			String[] splitted = headersElements.get(0).text().split("\\s");

			result.setMegaHashesPerSeconds(parsePower(splitted[1], splitted[2]));
			result.setNbMiners(Integer.parseInt(splitted[6]));
			result.setMiningCoin(splitted[10]);

			Elements contentElements = statsPage.select("#content");
			splitted = contentElements.get(0).text().split("Bitcoins sent to miners:");
			String[] splitted2 = splitted[1].split("Bitcoins earned \\(not yet sent\\):");
			String rawPaidout = splitted2[0];
			splitted = splitted2[1].split("Bitcoins unconverted \\(approximate\\):");
			String rawBalance = splitted[0];
			splitted2 = splitted[1].split("Date BTC");
			String rawUnexchanged = splitted2[0];
			result.setTotalPaidout(Float.parseFloat(rawPaidout.replaceAll(",", "")));
			result.setTotalBalance(Float.parseFloat(rawBalance.replaceAll(",", "")));
			result.setTotalUnexchanged(Float.parseFloat(rawUnexchanged.replaceAll(",", "")));

			LOGGER.debug("Global stats from waffle retreived.");

		} catch (IOException e) {
			LOGGER.error("Failed to retrieve the stats page of Wafflepool.", e);
		}
		return result;
	}

	/**
	 * Convert the power in MH/s
	 * 
	 * @param rawPower
	 * @return
	 */
	private Float parsePower(String powerValue, String powerUnit) {
		Float power = Float.parseFloat(powerValue);

		if (powerUnit.trim().startsWith("G")) {
			power = power * 1000;
		} else if (powerUnit.trim().startsWith("K")) {
			power = power / 1000;
		}

		return power;
	}

	@Override
	public AddressStats getAddressStats(String address) {
		long start = System.currentTimeMillis();
		LOGGER.debug("Retrieving raw address stats from waffle for address {}.", address);
		AddressStats response = client.target("http://wafflepool.com").path("tmp_api").queryParam("address", address)
				.request(MediaType.APPLICATION_JSON).get(AddressStats.class);
		PERF_LOGGER.info("Raw stats for address {} retrieved from waffle in {} ms.", address, System.currentTimeMillis() - start);
		return response;
	}
}
