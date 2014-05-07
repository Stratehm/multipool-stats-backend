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
package strat.mining.multipool.stats.jersey.resources;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.jersey.client.iface.BlockchainRestClient;
import strat.mining.multipool.stats.jersey.model.blockchain.BlockChainSingleAddress;
import strat.mining.multipool.stats.service.iface.RequestStatsLoggingService;
import strat.mining.multipool.stats.utils.BitcoinAddressUtils;

@Component
@Path("blockchain")
@Produces("application/json")
@Consumes("application/json")
public class BlockchainResources {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainResources.class);

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	@Resource
	private BlockchainRestClient blockchainRestClient;

	@Resource
	private RequestStatsLoggingService requestStatsLogger;

	/**
	 * Get the info of the given address from blockchain
	 * 
	 * @return
	 */
	@GET
	@Path("{bitcoinAddress}/blockchainInfo")
	public Response getBlockchainAddressInfo(@PathParam("bitcoinAddress") String bitcoinAddress, @HeaderParam("user-agent") String userAgent,
			@Context HttpServletRequest request) {
		requestStatsLogger.blockchainDetailsRequest();
		USE_LOGGER.info("Request for blockchain info : {}", bitcoinAddress);

		long startTime = System.currentTimeMillis();

		Response response = null;

		if (BitcoinAddressUtils.validateAddress(bitcoinAddress)) {
			BlockChainSingleAddress blockchainInfo = blockchainRestClient.getBlockChainSingleAddress(bitcoinAddress, 0);

			if (blockchainInfo != null) {
				BlockchainAddressInfoDTO result = new BlockchainAddressInfoDTO();
				result.setAddress(blockchainInfo.getAddress());
				result.setCurrentBalance(blockchainInfo.getFinal_balance().intValue() / 100000000f);
				response = Response.status(Response.Status.OK).entity(result).build();
			} else {
				LOGGER.debug("Address {} not found.", bitcoinAddress);
				response = Response.status(Response.Status.NOT_FOUND)
						.entity("The address " + bitcoinAddress + " is not found. Blockchain.info may be down.").build();
			}

		} else {
			LOGGER.debug("Address {} not valid.", bitcoinAddress);
			response = Response.status(Response.Status.BAD_REQUEST).entity("The address " + bitcoinAddress + " is not a valid bitcoin address.")
					.build();
		}

		PERF_LOGGER.info("Request for blockchain info {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - startTime);

		return response;
	}

}
