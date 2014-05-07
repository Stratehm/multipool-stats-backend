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
package strat.mining.multipool.stats.service.impl.coinsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.builder.CoinsolverStatsBuilder;
import strat.mining.multipool.stats.persistence.dao.coinsolver.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.coinsolver.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.model.coinsolver.Address;
import strat.mining.multipool.stats.service.iface.SuggestionService;

@Component("coinsolverAddressSuggestionService")
public class AddressSuggestionService implements SuggestionService<String, String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AddressSuggestionService.class);

	@Resource
	private AddressDAO addressDAO;

	@Resource
	private AddressStatsDAO addressStatsDAO;

	@Resource
	private CoinsolverStatsBuilder statsBuilder;

	private Set<String> currentAddresses;

	public AddressSuggestionService() {
	}

	@PostConstruct
	public void init() {
		LOGGER.info("Initialize coinsolverAddressSuggestionService with database addresses.");

		long startTime = System.currentTimeMillis();

		synchronized (LOGGER) {
			long startTimeGetAllAddresses = System.currentTimeMillis();

			List<Address> addresses = addressDAO.getAllAddresses();

			currentAddresses = new HashSet<>();

			if (CollectionUtils.isNotEmpty(addresses)) {
				for (Address address : addresses) {
					currentAddresses.add(address.getAddress());
				}
			}

			LOGGER.debug("coinsolver Addresses list update done in {} ms for {} addresses.", System.currentTimeMillis() - startTimeGetAllAddresses,
					currentAddresses.size());
		}

		LOGGER.info("coinsolver Initialization done in " + (System.currentTimeMillis() - startTime) + " ms. Nb addresses found: "
				+ currentAddresses.size());
	}

	@Scheduled(cron = "0 2/10 * * * *")
	private void updateAddresses() {
		synchronized (LOGGER) {
			long startTime = System.currentTimeMillis();

			currentAddresses.addAll(statsBuilder.getCurrentAddresses());

			LOGGER.debug("coinsolver Addresses list update done in {} ms for {} addresses.", System.currentTimeMillis() - startTime,
					currentAddresses.size());
		}
	}

	@Override
	public List<String> getSuggestions(String parameter) {
		Set<String> addresses = null;
		synchronized (LOGGER) {
			addresses = currentAddresses;
		}

		return searchSuggestions(parameter, addresses);
	}

	private List<String> searchSuggestions(String addressToSearch, Set<String> addresses) {
		List<String> result = new ArrayList<>();

		for (String address : currentAddresses) {
			if (address.toLowerCase().indexOf(addressToSearch.toLowerCase()) > -1) {
				result.add(address);
			}
		}

		return result;
	}

}
