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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.service.iface.CurrencyService;
import strat.mining.multipool.stats.service.iface.RequestStatsLoggingService;

@Component
@Path("currency")
@Produces("application/json")
@Consumes("application/json")
public class CurrencyResources {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyResources.class);

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	@Resource
	private CurrencyService currencyService;

	@Resource
	private RequestStatsLoggingService requestStatsLogger;

	/**
	 * Get the globals statistics
	 * 
	 * @return
	 */
	@GET
	@Path("{exchangePlace}/{currencyCode}")
	public Response getCurrencyTicker(@HeaderParam("user-agent") String userAgent, @Context HttpServletRequest request,
			@PathParam("exchangePlace") String exchangePlace, @PathParam("currencyCode") String currencyCode) {
		requestStatsLogger.currencyTicker(exchangePlace, currencyCode);
		USE_LOGGER.debug("Get currency info for {} and {}.", exchangePlace, currencyCode);

		Response response = null;

		CurrencyTickerDTO result = currencyService.getCurrencyTicker(exchangePlace, currencyCode);

		if (result != null) {
			response = Response.status(Response.Status.OK).entity(result).build();
		} else {
			LOGGER.debug("Not supported: {}, {}", exchangePlace, currencyCode);
			response = Response.status(Response.Status.NOT_FOUND)
					.entity("The exchange place " + exchangePlace + " and/or currency " + currencyCode + " are not supported.").build();
		}

		return response;
	}

	@GET
	@Path("places")
	public List<ExchangePlaceDTO> getAllExchangePlaces() {
		requestStatsLogger.allExchangePlace();
		USE_LOGGER.debug("Get currency exchange places");
		return currencyService.getExchangePlaces();
	}

}
