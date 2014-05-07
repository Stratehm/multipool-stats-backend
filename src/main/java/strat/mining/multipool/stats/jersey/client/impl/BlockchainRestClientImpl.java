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
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.jersey.client.iface.BlockchainRestClient;
import strat.mining.multipool.stats.jersey.model.blockchain.BlockChainSingleAddress;
import strat.mining.multipool.stats.jersey.model.blockchain.BlockHeight;

@Component("blockchainRestClient")
public class BlockchainRestClientImpl implements BlockchainRestClient {

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainRestClientImpl.class);

	private Client client;

	public BlockchainRestClientImpl() {
		ClientConfig config = new ClientConfig().register(JacksonFeature.class);
		client = ClientBuilder.newClient(config);
	}

	@Override
	public BlockChainSingleAddress getBlockChainSingleAddress(String bitcoinAddress, int transactionNbLimit) {
		BlockChainSingleAddress result = null;
		try {
			LOGGER.debug("Retrieving address info from Blockchain for address {}.", bitcoinAddress);
			long start = System.currentTimeMillis();
			result = client.target("http://blockchain.info").path("address/" + bitcoinAddress).queryParam("format", "json")
					.queryParam("limit", transactionNbLimit).request(MediaType.APPLICATION_JSON).get(BlockChainSingleAddress.class);
			PERF_LOGGER.info("Address info retrieved from Blockchain for currency address {} in {} ms.", bitcoinAddress, System.currentTimeMillis()
					- start);
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve address info from Blockchain for address {}.", bitcoinAddress, e);
		}
		return result;
	}

	@Override
	public BlockChainSingleAddress getBlockChainSingleAddress(String bitcoinAddress, int nbTransactions, int offset) {
		BlockChainSingleAddress result = null;
		try {
			LOGGER.debug("Retrieving address info from Blockchain for address {} with {} transactions from {}.", bitcoinAddress, nbTransactions,
					offset);
			long start = System.currentTimeMillis();
			result = client.target("http://blockchain.info").path("address/" + bitcoinAddress).queryParam("format", "json")
					.queryParam("limit", nbTransactions).queryParam("offset", offset).request(MediaType.APPLICATION_JSON)
					.get(BlockChainSingleAddress.class);
			PERF_LOGGER.info("Address info retrieved from Blockchain for address {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - start);
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve address info from Blockchain for address {}.", bitcoinAddress, e);
		}
		return result;
	}

	@Override
	public BlockHeight getBlockHeight(String blockHeight) {
		BlockHeight result = null;
		try {
			LOGGER.debug("Retrieving the block height from Blockchain for height {}.", blockHeight);
			long start = System.currentTimeMillis();
			result = client.target("http://blockchain.info").path("block-height/" + blockHeight).queryParam("format", "json")
					.request(MediaType.APPLICATION_JSON).get(BlockHeight.class);
			PERF_LOGGER.info("Bloc height info retrieved from Blockchain for height in {} ms.", blockHeight, System.currentTimeMillis() - start);
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve block height info from Blockchain for height {}.", blockHeight, e);
		}
		return result;
	}

}
