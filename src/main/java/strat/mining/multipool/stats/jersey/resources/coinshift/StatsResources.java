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
package strat.mining.multipool.stats.jersey.resources.coinshift;

import java.util.ArrayList;
import java.util.List;

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

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.builder.CoinshiftStatsBuilder;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO;
import strat.mining.multipool.stats.dto.coinshift.GlobalStatsDTO;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.GlobalStatsDAO;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.TransactionDAO;
import strat.mining.multipool.stats.persistence.model.coinshift.Address;
import strat.mining.multipool.stats.persistence.model.coinshift.AddressStats;
import strat.mining.multipool.stats.persistence.model.coinshift.GlobalStats;
import strat.mining.multipool.stats.persistence.model.coinshift.Transaction;
import strat.mining.multipool.stats.service.iface.coinshift.RequestStatsLoggingService;
import strat.mining.multipool.stats.utils.BitcoinAddressUtils;

import com.google.common.collect.Lists;

@Component(value = "coinshiftStatsResource")
@Path("coinshift/stats")
@Produces("application/json")
@Consumes("application/json")
public class StatsResources {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatsResources.class);

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	@Resource
	private GlobalStatsDAO globalStatsDAO;

	@Resource
	private AddressDAO addressDAO;

	@Resource
	private AddressStatsDAO addressStatsDAO;

	@Resource
	private TransactionDAO transactionDAO;

	@Resource
	private CoinshiftStatsBuilder statsBuilder;

	@Resource
	private RequestStatsLoggingService requestStatsLogger;

	/**
	 * Get the globals statistics
	 * 
	 * @return
	 */
	@GET
	@Path("global/last")
	public GlobalStatsDTO getLastGlobalStats(@HeaderParam("user-agent") String userAgent, @Context HttpServletRequest request) {
		requestStatsLogger.lastGlobalStatsRequest();
		USE_LOGGER.info("Request for coinshift last globalStats");

		long startTime = System.currentTimeMillis();

		GlobalStatsDTO result = new GlobalStatsDTO();
		GlobalStats gs = globalStatsDAO.getLastGlobalStats();

		result.setRefreshTime(gs.getRefreshTime());
		result.setTotalBalance(gs.getTotalBalance() == null ? 0 : gs.getTotalBalance());
		result.setTotalMegahashesPerSecond(gs.getMegaHashesPerSeconds() == null ? 0 : gs.getMegaHashesPerSeconds());
		result.setTotalRejectedMegahashesPerSecond(gs.getRejectedMegaHashesPerSeconds() == null ? 0 : gs.getRejectedMegaHashesPerSeconds());
		result.setTotalUnexchangedBalance(gs.getTotalUnexchanged() == null ? 0 : gs.getTotalUnexchanged());

		PERF_LOGGER.info("Request for coinshift last globalStats in {} ms.", System.currentTimeMillis() - startTime);

		return result;
	}

	/**
	 * Get the globals statistics
	 * 
	 * @return
	 */
	@GET
	@Path("global/all")
	public List<GlobalStatsDTO> getAllGlobalStats(@HeaderParam("user-agent") String userAgent, @Context HttpServletRequest request) {
		requestStatsLogger.allGlobalStatsRequest();
		USE_LOGGER.info("Request for coinshift all globalStats");

		long startTime = System.currentTimeMillis();

		List<GlobalStatsDTO> result = new ArrayList<GlobalStatsDTO>();
		List<GlobalStats> gss = globalStatsDAO.getAllGlobalStats();

		if (CollectionUtils.isNotEmpty(gss)) {
			for (GlobalStats gs : gss) {
				GlobalStatsDTO dto = new GlobalStatsDTO();

				dto.setRefreshTime(gs.getRefreshTime());
				dto.setTotalBalance(gs.getTotalBalance() == null ? 0 : gs.getTotalBalance());
				dto.setTotalMegahashesPerSecond(gs.getMegaHashesPerSeconds() == null ? 0 : gs.getMegaHashesPerSeconds());
				dto.setTotalRejectedMegahashesPerSecond(gs.getRejectedMegaHashesPerSeconds() == null ? 0 : gs.getRejectedMegaHashesPerSeconds());
				dto.setTotalUnexchangedBalance(gs.getTotalUnexchanged() == null ? 0 : gs.getTotalUnexchanged());

				result.add(dto);
			}
		}

		PERF_LOGGER.info("Request for coinshift all globalStats in {} ms.", System.currentTimeMillis() - startTime);

		return result;
	}

	/**
	 * Get the last address statistics
	 * 
	 * @return
	 */
	@GET
	@Path("{bitcoinAddress}/last")
	public Response getLastAddressStats(@PathParam("bitcoinAddress") String bitcoinAddress, @HeaderParam("user-agent") String userAgent,
			@Context HttpServletRequest request) {
		requestStatsLogger.lastAddressStatsRequest(bitcoinAddress);
		USE_LOGGER.info("Request for coinshift last addressStats : {}", bitcoinAddress);

		long startTime = System.currentTimeMillis();

		Response response = null;

		if (BitcoinAddressUtils.validateAddress(bitcoinAddress)) {

			Address address = addressDAO.getAddress(bitcoinAddress);

			AddressStatsDTO result = new AddressStatsDTO();

			if (address != null) {
				AddressStats as = addressStatsDAO.getLastAddressStats(address.getId());
				result.setAddress(address.getAddress());
				result.setBalance(as.getBalance() == null ? 0 : as.getBalance());
				result.setMegaHashesPerSeconds(as.getHashRate() == null ? 0 : as.getHashRate() / 1000);
				result.setRejectedMegaHashesPerSeconds(as.getRejectedHashRate() == null ? 0 : as.getRejectedHashRate() / 1000);
				result.setPaidOut(as.getPaidout() == null ? 0 : as.getPaidout());
				result.setRefreshTime(as.getRefreshTime());
				result.setUnexchanged(as.getUnexchanged() == null ? 0 : as.getUnexchanged());

				response = Response.status(Response.Status.OK).entity(result).build();
			} else {
				LOGGER.debug("Address {} not found.", bitcoinAddress);
				response = Response.status(Response.Status.NOT_FOUND).entity("The address " + bitcoinAddress + " is not found.").build();
			}
		} else {
			LOGGER.debug("Address {} not valid.", bitcoinAddress);
			response = Response.status(Response.Status.BAD_REQUEST).entity("The address " + bitcoinAddress + " is not a valid bitcoin address.")
					.build();
		}

		PERF_LOGGER.info("Request for coinshift last address {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - startTime);

		return response;
	}

	/**
	 * Get the all address statistics
	 * 
	 * @return
	 */
	@GET
	@Path("{bitcoinAddress}/all")
	public Response getAllAddressStats(@PathParam("bitcoinAddress") String bitcoinAddress, @HeaderParam("user-agent") String userAgent,
			@Context HttpServletRequest request) {
		requestStatsLogger.allAddressStatsRequest(bitcoinAddress);
		USE_LOGGER.info("Request for coinshift all addressStats : {}", bitcoinAddress);

		long startTime = System.currentTimeMillis();

		Response response = null;

		if (BitcoinAddressUtils.validateAddress(bitcoinAddress)) {

			try {
				Address address = addressDAO.getAddress(bitcoinAddress);

				List<AddressStatsDTO> result = new ArrayList<AddressStatsDTO>();

				List<AddressStats> addressStats = null;

				if (address == null) {
					requestStatsLogger.initializeAddress();
					addressStats = Lists.newArrayList(statsBuilder.initializeAddressStats(bitcoinAddress));
				} else {
					addressStats = addressStatsDAO.getAddressStats(address.getId());
				}

				if (CollectionUtils.isNotEmpty(addressStats)) {
					for (AddressStats as : addressStats) {
						AddressStatsDTO dto = new AddressStatsDTO();
						dto.setAddress(bitcoinAddress);
						dto.setBalance(as.getBalance() == null ? 0 : as.getBalance());
						dto.setMegaHashesPerSeconds(as.getHashRate() == null ? 0 : as.getHashRate() / 1000);
						dto.setRejectedMegaHashesPerSeconds(as.getRejectedHashRate() == null ? 0 : as.getRejectedHashRate() / 1000);
						dto.setPaidOut(as.getPaidout() == null ? 0 : as.getPaidout());
						dto.setRefreshTime(as.getRefreshTime());
						dto.setUnexchanged(as.getUnexchanged() == null ? 0 : as.getUnexchanged());

						result.add(dto);
					}

					response = Response.status(Response.Status.OK).entity(result).build();
				} else {
					LOGGER.debug("Address {} with no data.", bitcoinAddress);
					response = Response.status(Response.Status.NOT_FOUND).entity("The address " + bitcoinAddress + " has no data.").build();
				}
			} catch (Exception e) {
				LOGGER.debug("Address {} not found.", bitcoinAddress);
				response = Response.status(Response.Status.NOT_FOUND)
						.entity("The address " + bitcoinAddress + " has is not found." + "coinshift response: " + e.getMessage()).build();
			}
		} else {
			LOGGER.debug("Address {} not valid.", bitcoinAddress);
			response = Response.status(Response.Status.BAD_REQUEST).entity("The address " + bitcoinAddress + " is not a valid bitcoin address.")
					.build();
		}

		PERF_LOGGER.info("Request for coinshift all address {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - startTime);

		return response;
	}

	/**
	 * Get all the address paidout
	 * 
	 * @return
	 */
	@GET
	@Path("{bitcoinAddress}/paidout")
	public Response getAllAddressPaidout(@PathParam("bitcoinAddress") String bitcoinAddress, @HeaderParam("user-agent") String userAgent,
			@Context HttpServletRequest request) {
		requestStatsLogger.paidoutRequest(bitcoinAddress);
		List<AddressPaidoutDTO> result = new ArrayList<>();

		long startTime = System.currentTimeMillis();

		USE_LOGGER.info("Request for middlecoin paidout : {}", bitcoinAddress);

		Response response = null;

		if (BitcoinAddressUtils.validateAddress(bitcoinAddress)) {

			Address address = addressDAO.getAddress(bitcoinAddress);

			if (address != null) {

				List<Transaction> transactions = transactionDAO.getAllTransactions(address.getId());

				if (CollectionUtils.isNotEmpty(transactions)) {
					for (Transaction transaction : transactions) {
						AddressPaidoutDTO dto = new AddressPaidoutDTO();

						dto.setAmount(transaction.getAmount() == null ? 0 : transaction.getAmount());
						dto.setTime(transaction.getDate());
						dto.setTransactionId(transaction.getTransactionId() == null ? "" : transaction.getTransactionId());

						result.add(dto);
					}

					response = Response.status(Response.Status.OK).entity(result).build();
				} else {
					LOGGER.debug("Address {} not found.", bitcoinAddress);
					response = Response.status(Response.Status.NO_CONTENT).entity("The address " + bitcoinAddress + " has no data.").build();
				}
			} else {
				LOGGER.debug("Address {} not found.", bitcoinAddress);
				response = Response.status(Response.Status.NOT_FOUND).entity("The address " + bitcoinAddress + " is not found.").build();
			}
		} else {
			LOGGER.debug("Address {} not valid.", bitcoinAddress);
			response = Response.status(Response.Status.BAD_REQUEST).entity("The address " + bitcoinAddress + " is not a valid bitcoin address.")
					.build();
		}

		PERF_LOGGER.info("Request for middlecoin paidout {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - startTime);

		return response;
	}

}
