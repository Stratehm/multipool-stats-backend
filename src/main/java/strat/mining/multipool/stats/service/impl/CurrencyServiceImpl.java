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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.service.iface.CurrencyService;
import strat.mining.multipool.stats.service.iface.place.ExchangePlaceProvider;

@Component("currencyService")
public class CurrencyServiceImpl implements CurrencyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyServiceImpl.class);

	private static final Logger LOGGER_PERF = LoggerFactory.getLogger("multipoolStatsPerf");

	@Resource
	private List<ExchangePlaceProvider> providers;

	@Resource(name = "currencyTickersExecutorService")
	private ExecutorService executorService;

	private List<ExchangePlaceDTO> exchangePlaces;

	private volatile Map<String, Map<String, CurrencyTickerDTO>> tickersByCurrencyByPlace;

	@PostConstruct
	public void init() {
		tickersByCurrencyByPlace = new HashMap<>();
		exchangePlaces = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(providers)) {
			for (ExchangePlaceProvider provider : providers) {
				exchangePlaces.add(provider.getExchangePlaceInfo());
			}
		}

		refreshTickers();

	}

	@Scheduled(cron = "0 0/1 * * * *")
	public void refreshTickers() {
		LOGGER_PERF.debug("Start of the retrieving of tickers.");
		long start = System.currentTimeMillis();

		Map<String, Map<String, CurrencyTickerDTO>> tickersByCurrencyByPlace = new HashMap<>();
		if (CollectionUtils.isNotEmpty(providers)) {
			List<Future<Pair<ExchangePlaceDTO, Map<String, CurrencyTickerDTO>>>> futures = new ArrayList<>();
			for (ExchangePlaceProvider provider : providers) {
				futures.add(executorService.submit(new ExchangePlaceTickersRunner(provider)));
			}

			for (Future<Pair<ExchangePlaceDTO, Map<String, CurrencyTickerDTO>>> future : futures) {
				try {
					tickersByCurrencyByPlace.put(future.get().getLeft().getName(), future.get().getRight());
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.error("Error during a callable result retrieving", e);
				}
			}

			LOGGER_PERF.debug("End of the tickers retrieving in {} ms.", System.currentTimeMillis() - start);
		}

		// Add all currency tickers that has not been refreshed in the new Map
		for (Entry<String, Map<String, CurrencyTickerDTO>> entry : this.tickersByCurrencyByPlace.entrySet()) {
			Map<String, CurrencyTickerDTO> oldTickersByCurrency = entry.getValue();

			// If the new map does not contains the place, add the old place to
			// the new map
			if (!tickersByCurrencyByPlace.containsKey(entry.getKey())) {
				tickersByCurrencyByPlace.put(entry.getKey(), oldTickersByCurrency);
			} else {
				// Else check if all currencies are present in the new place.
				Map<String, CurrencyTickerDTO> newTickersByCurrency = tickersByCurrencyByPlace.get(entry.getKey());
				for (Entry<String, CurrencyTickerDTO> entry2 : oldTickersByCurrency.entrySet()) {
					if (!newTickersByCurrency.containsKey(entry2.getKey())) {
						// If the currency is not in the new map, the new value
						// has not been retrieved. So add the old value.
						newTickersByCurrency.put(entry2.getKey(), entry2.getValue());
					}
				}
			}
		}

		this.tickersByCurrencyByPlace = tickersByCurrencyByPlace;
	}

	@Override
	public List<ExchangePlaceDTO> getExchangePlaces() {
		return exchangePlaces;
	}

	@Override
	public CurrencyTickerDTO getCurrencyTicker(String exchangePlaceName, String currencyCode) {
		CurrencyTickerDTO result = null;
		Map<String, CurrencyTickerDTO> tickersByCurrency = tickersByCurrencyByPlace.get(exchangePlaceName);
		if (tickersByCurrency != null) {
			result = tickersByCurrency.get(currencyCode);
		}
		return result;
	}

	/**
	 * A callable that will retrieve tickers from an exchange place and return a
	 * Map of currency code and the associated ticker.
	 * 
	 * @author Strat
	 * 
	 */
	private static class ExchangePlaceTickersRunner implements Callable<Pair<ExchangePlaceDTO, Map<String, CurrencyTickerDTO>>> {

		private ExchangePlaceProvider provider;

		public ExchangePlaceTickersRunner(ExchangePlaceProvider provider) {
			this.provider = provider;
		}

		@Override
		public Pair<ExchangePlaceDTO, Map<String, CurrencyTickerDTO>> call() throws Exception {
			LOGGER_PERF.debug("Retrieving tickers from {}.", provider.getExchangePlaceInfo().getLabel());
			long start = System.currentTimeMillis();

			List<CurrencyTickerDTO> tickers = provider.getTickers();

			Map<String, CurrencyTickerDTO> tickersByCurrency = new HashMap<>();
			if (CollectionUtils.isNotEmpty(tickers)) {
				for (CurrencyTickerDTO ticker : tickers) {
					ticker.setExchangePlaceName(provider.getExchangePlaceInfo().getName());
					ticker.setExchangePlaceLabel(provider.getExchangePlaceInfo().getLabel());
					tickersByCurrency.put(ticker.getCurrencyCode(), ticker);
				}
			}

			LOGGER_PERF.debug("Tickers retrieved from {} in {} ms.", provider.getExchangePlaceInfo().getLabel(), System.currentTimeMillis() - start);

			return new ImmutablePair<>(provider.getExchangePlaceInfo(), tickersByCurrency);
		}
	}
}
