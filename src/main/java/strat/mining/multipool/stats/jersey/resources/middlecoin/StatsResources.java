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
package strat.mining.multipool.stats.jersey.resources.middlecoin;

import java.util.ArrayList;
import java.util.Date;
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

import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO;
import strat.mining.multipool.stats.jersey.client.iface.MiddlecoinRestClient;
import strat.mining.multipool.stats.jersey.model.middlecoin.AddressPaidout;
import strat.mining.multipool.stats.jersey.model.middlecoin.AddressPaidoutReport;
import strat.mining.multipool.stats.persistence.dao.middlecoin.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.middlecoin.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.dao.middlecoin.iface.GlobalStatsDAO;
import strat.mining.multipool.stats.persistence.model.middlecoin.Address;
import strat.mining.multipool.stats.persistence.model.middlecoin.AddressStats;
import strat.mining.multipool.stats.persistence.model.middlecoin.GlobalStats;
import strat.mining.multipool.stats.service.iface.middlecoin.RequestStatsLoggingService;
import strat.mining.multipool.stats.utils.BitcoinAddressUtils;

@Component(value = "middlecoinStatsResource")
@Path("middlecoin/stats")
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
	private MiddlecoinRestClient middlecoinRestClient;

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
		USE_LOGGER.info("Request for middlecoin last globalStats");

		long startTime = System.currentTimeMillis();

		GlobalStatsDTO result = new GlobalStatsDTO();
		GlobalStats gs = globalStatsDAO.getLastGlobalStats();

		result.setRefreshTime(gs.getRefreshTime());
		result.setUpdateTime(gs.getUpdateTime());
		result.setTotalBalance(gs.getBalance() == null ? 0 : gs.getBalance());
		result.setTotalImmatureBalance(gs.getImmature() == null ? 0 : gs.getImmature());
		result.setTotalMegahashesPerSecond(gs.getMegaHashesPerSeconds() == null ? 0 : gs.getMegaHashesPerSeconds());
		result.setTotalPaidOut(gs.getPaidOut() == null ? 0 : gs.getPaidOut());
		result.setTotalRejectedMegahashesPerSecond(gs.getRejectedMegaHashesPerSeconds() == null ? 0 : gs.getRejectedMegaHashesPerSeconds());
		result.setTotalUnexchangedBalance(gs.getUnexchanged() == null ? 0 : gs.getUnexchanged());

		PERF_LOGGER.info("Request for middlecoin last globalStats in {} ms.", System.currentTimeMillis() - startTime);

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
		USE_LOGGER.info("Request for middlecoin all globalStats");

		long startTime = System.currentTimeMillis();

		List<GlobalStatsDTO> result = new ArrayList<GlobalStatsDTO>();
		List<GlobalStats> gss = globalStatsDAO.getAllGlobalStats();

		if (CollectionUtils.isNotEmpty(gss)) {
			for (GlobalStats gs : gss) {
				GlobalStatsDTO dto = new GlobalStatsDTO();

				dto.setRefreshTime(gs.getRefreshTime());
				dto.setUpdateTime(gs.getUpdateTime());
				dto.setTotalBalance(gs.getBalance() == null ? 0 : gs.getBalance());
				dto.setTotalImmatureBalance(gs.getImmature() == null ? 0 : gs.getImmature());
				dto.setTotalMegahashesPerSecond(gs.getMegaHashesPerSeconds() == null ? 0 : gs.getMegaHashesPerSeconds());
				dto.setTotalPaidOut(gs.getPaidOut() == null ? 0 : gs.getPaidOut());
				dto.setTotalRejectedMegahashesPerSecond(gs.getRejectedMegaHashesPerSeconds() == null ? 0 : gs.getRejectedMegaHashesPerSeconds());
				dto.setTotalUnexchangedBalance(gs.getUnexchanged() == null ? 0 : gs.getUnexchanged());

				result.add(dto);
			}
		}

		PERF_LOGGER.info("Request for middlecoin all globalStats in {} ms.", System.currentTimeMillis() - startTime);

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
		USE_LOGGER.info("Request for middlecoin last addressStats : {}", bitcoinAddress);

		long startTime = System.currentTimeMillis();

		Response response = null;

		if (BitcoinAddressUtils.validateAddress(bitcoinAddress)) {

			Address address = addressDAO.getAddress(bitcoinAddress);

			AddressStatsDTO result = new AddressStatsDTO();

			if (address != null) {
				AddressStats as = addressStatsDAO.getLastAddressStats(address.getId());
				result.setAddress(address.getAddress());
				result.setBalance(as.getBalance() == null ? 0 : as.getBalance());
				result.setImmature(as.getImmature() == null ? 0 : as.getImmature());
				result.setLastHourRejectedShares(as.getLastHourRejectedShares() == null ? 0 : as.getLastHourRejectedShares());
				result.setLastHourShares(as.getLastHourShares() == null ? 0 : as.getLastHourShares());
				result.setMegaHashesPerSeconds(as.getMegaHashesPerSeconds() == null ? 0 : as.getMegaHashesPerSeconds());
				result.setPaidOut(as.getPaidOut() == null ? 0 : as.getPaidOut());
				result.setRejectedMegaHashesPerSeconds(as.getRejectedMegaHashesPerSeconds() == null ? 0 : as.getRejectedMegaHashesPerSeconds());
				result.setRefreshTime(as.getRefreshTime());
				result.setUpdateTime(as.getUpdateTime());
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

		PERF_LOGGER.info("Request for middlecoin last address {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - startTime);

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
		USE_LOGGER.info("Request for middlecoin all addressStats : {}", bitcoinAddress);

		long startTime = System.currentTimeMillis();

		Response response = null;

		if (BitcoinAddressUtils.validateAddress(bitcoinAddress)) {

			Address address = addressDAO.getAddress(bitcoinAddress);

			List<AddressStatsDTO> result = new ArrayList<AddressStatsDTO>();

			if (address != null) {
				List<AddressStats> addressStats = addressStatsDAO.getAddressStats(address.getId());

				if (CollectionUtils.isNotEmpty(addressStats)) {
					for (AddressStats as : addressStats) {
						AddressStatsDTO dto = new AddressStatsDTO();
						dto.setAddress(address.getAddress());
						dto.setBalance(as.getBalance() == null ? 0 : as.getBalance());
						dto.setImmature(as.getImmature() == null ? 0 : as.getImmature());
						dto.setLastHourRejectedShares(as.getLastHourRejectedShares() == null ? 0 : as.getLastHourRejectedShares());
						dto.setLastHourShares(as.getLastHourShares() == null ? 0 : as.getLastHourShares());
						dto.setMegaHashesPerSeconds(as.getMegaHashesPerSeconds() == null ? 0 : as.getMegaHashesPerSeconds());
						dto.setPaidOut(as.getPaidOut() == null ? 0 : as.getPaidOut());
						dto.setRejectedMegaHashesPerSeconds(as.getRejectedMegaHashesPerSeconds() == null ? 0 : as.getRejectedMegaHashesPerSeconds());
						dto.setRefreshTime(as.getRefreshTime());
						dto.setUpdateTime(as.getUpdateTime());
						dto.setUnexchanged(as.getUnexchanged() == null ? 0 : as.getUnexchanged());

						result.add(dto);
					}

					response = Response.status(Response.Status.OK).entity(result).build();
				} else {
					LOGGER.debug("Address {} not found.", bitcoinAddress);
					response = Response.status(Response.Status.NOT_FOUND).entity("The address " + bitcoinAddress + " has no data.").build();
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

		PERF_LOGGER.info("Request for middlecoin all address {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - startTime);

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

			AddressPaidout rawResult = middlecoinRestClient.getAddressPaidout(bitcoinAddress);

			if (rawResult != null) {
				if (rawResult.getReport() != null) {
					for (AddressPaidoutReport apr : rawResult.getReport()) {
						AddressPaidoutDTO dto = new AddressPaidoutDTO();

						dto.setAmount(apr.getAmount() == null ? 0 : apr.getAmount());
						dto.setTime(new Date(apr.getTime() * 1000));
						dto.setTransactionId(apr.getTransactionId() == null ? "" : apr.getTransactionId());

						result.add(dto);
					}
				}

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

		PERF_LOGGER.info("Request for middlecoin paidout {} in {} ms.", bitcoinAddress, System.currentTimeMillis() - startTime);

		return response;
	}

}
