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
package strat.mining.multipool.stats.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.service.iface.RequestStatsLoggingService;

@Component("multipoolRequestStatsLoggingService")
public class RequestStatsLoggingServiceImpl implements RequestStatsLoggingService {

	private static final Logger LOGGER = LoggerFactory.getLogger("multipoolRequestStats");

	private AtomicInteger nbCurrencyTicker;
	private AtomicInteger nbDonnation;
	private AtomicInteger nbBlockchainDetails;
	private AtomicInteger nbAllExchangePlace;

	private volatile Map<String, Map<String, AtomicInteger>> currencyTickerRequest;
	private volatile Map<String, AtomicInteger> donatorsRequests;

	public RequestStatsLoggingServiceImpl() {
		nbCurrencyTicker = new AtomicInteger(0);
		nbDonnation = new AtomicInteger(0);
		nbBlockchainDetails = new AtomicInteger(0);
		nbAllExchangePlace = new AtomicInteger(0);

		currencyTickerRequest = Collections.synchronizedMap(new HashMap<String, Map<String, AtomicInteger>>());
		donatorsRequests = Collections.synchronizedMap(new HashMap<String, AtomicInteger>());
	}

	@Override
	public void currencyTicker(String exchangePlace, String currencyCode) {
		nbCurrencyTicker.incrementAndGet();

		Map<String, AtomicInteger> currencies = currencyTickerRequest.get(exchangePlace);

		if (currencies == null) {
			currencies = Collections.synchronizedMap(new HashMap<String, AtomicInteger>());
			currencyTickerRequest.put(exchangePlace, currencies);
		}

		AtomicInteger currencyCounter = currencies.get(currencyCode);

		if (currencyCounter == null) {
			currencyCounter = new AtomicInteger(0);
			currencies.put(currencyCode, currencyCounter);
		}

		currencyCounter.incrementAndGet();
	}

	@Override
	public void donnationRequest() {
		nbDonnation.incrementAndGet();
	}

	@Override
	public void donnatorRequest(String bitcoinAddress) {
		AtomicInteger counter = donatorsRequests.get(bitcoinAddress);
		if (counter == null) {
			counter = new AtomicInteger(0);
			donatorsRequests.put(bitcoinAddress, counter);
		}
		counter.incrementAndGet();
	}

	@Scheduled(cron = "0 0 * * * *")
	private void logStats() {
		Map<String, Map<String, AtomicInteger>> currencyTickersOldMap = currencyTickerRequest;
		currencyTickerRequest = Collections.synchronizedMap(new HashMap<String, Map<String, AtomicInteger>>());

		Map<String, AtomicInteger> donatorsRequestOld = donatorsRequests;
		donatorsRequests = Collections.synchronizedMap(new HashMap<String, AtomicInteger>());

		LOGGER.debug("Nb donnation requests: {}.", nbDonnation.intValue());
		LOGGER.debug("Nb blockchain details requests: {}.", nbBlockchainDetails.intValue());
		LOGGER.debug("Nb all exchange place requests: {}.", nbAllExchangePlace.intValue());
		LOGGER.debug("Nb currency ticker requests: {}.", nbCurrencyTicker.intValue());
		for (Entry<String, Map<String, AtomicInteger>> entry1 : currencyTickersOldMap.entrySet()) {
			for (Entry<String, AtomicInteger> entry2 : entry1.getValue().entrySet()) {
				LOGGER.debug("Nb currency ticker requests for exchange {} and currency {}: {}.", entry1.getKey(), entry2.getKey(), entry2.getValue()
						.intValue());
			}
		}
		for (Entry<String, AtomicInteger> entry : donatorsRequestOld.entrySet()) {
			LOGGER.debug("Nb donators requests for {}: {}.", entry.getKey(), entry.getValue().get());
		}
		LOGGER.debug(" ");
		LOGGER.debug(" ");
		LOGGER.debug(" ");

		nbCurrencyTicker.set(0);

		nbBlockchainDetails.set(0);
		nbAllExchangePlace.set(0);
		nbDonnation.set(0);
	}

	@Override
	public void blockchainDetailsRequest() {
		nbBlockchainDetails.incrementAndGet();
	}

	@Override
	public void allExchangePlace() {
		nbAllExchangePlace.incrementAndGet();
	}

}
