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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.jersey.BinaryJsonFilter;
import strat.mining.multipool.stats.jersey.ObjectMapperResolver;
import strat.mining.multipool.stats.jersey.client.iface.MiddlecoinRestClient;
import strat.mining.multipool.stats.jersey.model.middlecoin.AddressPaidout;
import strat.mining.multipool.stats.jersey.model.middlecoin.GlobalStats;

@Component("middlecoinRestClient")
public class MiddlecoinRestClientJersey implements MiddlecoinRestClient {

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger LOGGER = LoggerFactory.getLogger(MiddlecoinRestClientJersey.class);

	private Client client;

	public MiddlecoinRestClientJersey() {
		ClientConfig config = new ClientConfig().register(BinaryJsonFilter.class).register(JacksonFeature.class);
		config.register(ObjectMapperResolver.class);
		client = ClientBuilder.newClient(config);
		client.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		client.property(ClientProperties.READ_TIMEOUT, 5000);
	}

	@Override
	public GlobalStats getRawStats() {
		long start = System.currentTimeMillis();
		LOGGER.debug("Retrieving raw stats from middlecoin.");
		GlobalStats response = client.target("http://www.middlecoin.com").path("json").request(MediaType.APPLICATION_JSON).get(GlobalStats.class);
		PERF_LOGGER.info("Raw stats Retrieved in {} ms.", System.currentTimeMillis() - start);
		return response;
	}

	@Override
	public AddressPaidout getAddressPaidout(String address) {
		long start = System.currentTimeMillis();
		LOGGER.debug("Retrieving raw paidout stats from middlecoin.");
		AddressPaidout response = client.target("http://www.middlecoin.com").path("reports/" + address + ".json").request(MediaType.APPLICATION_JSON)
				.get(AddressPaidout.class);
		PERF_LOGGER.info("Raw paidout stats Retrieved in {} ms.", System.currentTimeMillis() - start);
		return response;
	}

}
