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

import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.DonationTransactionDetailsDTO;
import strat.mining.multipool.stats.service.iface.DonationService;
import strat.mining.multipool.stats.service.iface.RequestStatsLoggingService;

@Component
@Path("donation")
@Produces("application/json")
@Consumes("application/json")
public class DonationResources {

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	@Resource
	private DonationService donationService;

	@Resource
	private RequestStatsLoggingService requestStatsLogger;

	/**
	 * Return the details of the donations.
	 * 
	 * @param userAgent
	 * @param request
	 */
	@GET
	@Path("details")
	public Response getDonationDetails(@HeaderParam("user-agent") String userAgent, @Context HttpServletRequest request) {
		requestStatsLogger.donnationRequest();
		USE_LOGGER.debug("Donnation details requested.");

		Response response = null;

		DonationDetailsDTO donationDetails = donationService.getDonationDetails();

		if (donationDetails != null) {
			response = Response.status(Response.Status.OK).entity(donationDetails).build();
		} else {
			response = Response.status(Response.Status.NO_CONTENT).entity("The donnations details are not available. Blockchain.info may be down.")
					.build();
		}

		return response;
	}

	@GET
	@Path("address/{bitcoinAddress}")
	public AddressDonationDetailsDTO getDonationDetailsByAddress(@PathParam("bitcoinAddress") String bitcoinAddress,
			@HeaderParam("user-agent") String userAgent, @Context HttpServletRequest request) {
		USE_LOGGER.debug("Donnation details for address {} requested.", bitcoinAddress);

		List<DonationTransactionDetailsDTO> transactions = donationService.getAddressDonationsNotThanked(bitcoinAddress);

		AddressDonationDetailsDTO result = new AddressDonationDetailsDTO();

		if (CollectionUtils.isNotEmpty(transactions)) {
			requestStatsLogger.donnatorRequest(bitcoinAddress);
			result.setDonator(true);
			result.setDonationsDetails(transactions);
		} else {
			result.setDonator(false);
		}

		return result;
	}

}
