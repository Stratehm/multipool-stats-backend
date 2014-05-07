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

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.dto.AddressSuggestionDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionRequestDTO;
import strat.mining.multipool.stats.service.iface.SuggestionService;
import strat.mining.multipool.stats.service.iface.middlecoin.RequestStatsLoggingService;

@Component(value = "middlecoinSuggestionResources")
@Path("middlecoin/suggest")
@Produces("application/json")
@Consumes("application/json")
public class SuggestionResources {

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	@Resource(name = "middlecoinAddressSuggestionService")
	private SuggestionService<String, String> addressSuggestionService;

	@Resource
	private RequestStatsLoggingService requestStatsLogger;

	/**
	 * Get a suggestion from the given pattern
	 * 
	 * @return
	 */
	@POST
	@Path("address")
	public AddressSuggestionDTO getAddressSuggestions(@HeaderParam("user-agent") String userAgent, @Context HttpServletRequest request,
			AddressSuggestionRequestDTO requestData) {
		requestStatsLogger.suggestionRequest();

		USE_LOGGER.debug("Get middlecoin address suggestions for {}.", requestData.getAddressPattern());

		AddressSuggestionDTO result = new AddressSuggestionDTO();

		List<String> suggestions = addressSuggestionService.getSuggestions(requestData.getAddressPattern());
		if (CollectionUtils.isNotEmpty(suggestions)) {
			int limit = requestData.getLimit();

			result.setHasMoreSuggestion(limit < suggestions.size());

			int moreSuggestionCount = suggestions.size() - limit;
			result.setMoreSuggestionCount(moreSuggestionCount > 0 ? moreSuggestionCount : 0);

			result.setSuggestions(suggestions.subList(0, result.isHasMoreSuggestion() ? limit : suggestions.size()));
		}

		return result;
	}
}
