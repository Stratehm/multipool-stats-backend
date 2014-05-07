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
package strat.mining.multipool.stats.service.impl.middlecoin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.service.iface.middlecoin.RequestStatsLoggingService;

@Component("middlecoinRequestStatsLoggingService")
public class RequestStatsLoggingServiceImpl implements RequestStatsLoggingService {

	private static final Logger LOGGER = LoggerFactory.getLogger("middlecoinRequestStats");

	private AtomicInteger nbAllGlobal;
	private AtomicInteger nbLastGlobal;
	private AtomicInteger nbAllAddress;
	private AtomicInteger nbLastAddress;
	private AtomicInteger nbPaidout;
	private AtomicInteger nbSuggestion;

	private Set<String> uniqueAddressesAllStats;
	private Set<String> uniqueAddressesLastStats;
	private Set<String> uniqueAddressesPaidout;

	public RequestStatsLoggingServiceImpl() {
		nbAllGlobal = new AtomicInteger(0);
		nbLastGlobal = new AtomicInteger(0);
		nbAllAddress = new AtomicInteger(0);
		nbLastAddress = new AtomicInteger(0);
		nbPaidout = new AtomicInteger(0);
		nbSuggestion = new AtomicInteger(0);

		uniqueAddressesAllStats = Collections.synchronizedSet(new HashSet<String>());
		uniqueAddressesLastStats = Collections.synchronizedSet(new HashSet<String>());
		uniqueAddressesPaidout = Collections.synchronizedSet(new HashSet<String>());
	}

	@Override
	public void allGlobalStatsRequest() {
		nbAllGlobal.incrementAndGet();
	}

	@Override
	public void lastGlobalStatsRequest() {
		nbLastGlobal.incrementAndGet();
	}

	@Override
	public void allAddressStatsRequest(String address) {
		nbAllAddress.incrementAndGet();
		uniqueAddressesAllStats.add(address);
	}

	@Override
	public void lastAddressStatsRequest(String address) {
		nbLastAddress.incrementAndGet();
		uniqueAddressesLastStats.add(address);
	}

	@Override
	public void paidoutRequest(String address) {
		nbPaidout.incrementAndGet();
		uniqueAddressesPaidout.add(address);
	}

	@Override
	public void suggestionRequest() {
		nbSuggestion.incrementAndGet();
	}

	@Scheduled(cron = "0 0 * * * *")
	private void logStats() {
		;

		LOGGER.debug("Nb all global requests: {}.", nbAllGlobal.intValue());
		LOGGER.debug("Nb last global requests: {}.", nbLastGlobal.intValue());
		LOGGER.debug("Nb all address requests: {}.", nbAllAddress.intValue());
		LOGGER.debug("Nb unique all address requests: {}.", uniqueAddressesAllStats.size());
		LOGGER.debug("Nb last address requests: {}.", nbLastAddress.intValue());
		LOGGER.debug("Nb unique last address requests: {}.", uniqueAddressesLastStats.size());
		LOGGER.debug("Nb paidout requests: {}.", nbPaidout.intValue());
		LOGGER.debug("Nb unique paidout requests: {}.", uniqueAddressesPaidout.size());
		LOGGER.debug("Nb suggestion requests: {}.", nbSuggestion.intValue());
		LOGGER.debug(" ");
		LOGGER.debug(" ");
		LOGGER.debug(" ");

		nbAllGlobal.set(0);
		nbLastGlobal.set(0);
		nbAllAddress.set(0);
		nbLastAddress.set(0);
		nbPaidout.set(0);
		nbSuggestion.set(0);

		uniqueAddressesAllStats.clear();
		uniqueAddressesLastStats.clear();
		uniqueAddressesPaidout.clear();
	}
}
