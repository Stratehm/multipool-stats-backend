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
package strat.mining.multipool.stats.builder;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.jersey.client.iface.CoinsolverRestClient;
import strat.mining.multipool.stats.jersey.model.coinsolver.Currently_mining;
import strat.mining.multipool.stats.persistence.dao.coinsolver.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.coinsolver.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.dao.coinsolver.iface.GlobalStatsDAO;
import strat.mining.multipool.stats.persistence.model.coinsolver.Address;
import strat.mining.multipool.stats.persistence.model.coinsolver.AddressStats;
import strat.mining.multipool.stats.persistence.model.coinsolver.GlobalStats;

@Component("coinsolverStatsBuilderTask")
public class CoinsolverStatsBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoinsolverStatsBuilder.class);

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	@Resource
	private CoinsolverRestClient coinsolverRestClient;

	@Resource
	private GlobalStatsDAO globalStatsDao;

	@Resource
	private AddressDAO addressDao;

	@Resource
	private AddressStatsDAO addressStatsDao;

	private Set<String> currentAddresses;

	public CoinsolverStatsBuilder() {
		currentAddresses = Collections.synchronizedSet(new HashSet<String>());
	}

	@PostConstruct
	public void init() {
		List<Address> addresses = addressDao.getAllAddresses();
		if (CollectionUtils.isNotEmpty(addresses)) {
			for (Address address : addresses) {
				currentAddresses.add(address.getAddress());
			}
		}
	}

	// @PostConstruct
	public void arf() {
		updateGlobalStats();
	}

	/**
	 * Clean the stats all 10 minutes. Clean the stats that are older than 5
	 * days.
	 */
	@Scheduled(cron = "0 5/10 * * * *")
	public void cleanStats() {
		LOGGER.debug("Clean the stats.");
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.DAY_OF_WEEK, -7);

		long startTime = System.currentTimeMillis();
		int nbDeleted = globalStatsDao.deleteGlobalStatsBefore(calendar.getTime());
		PERF_LOGGER.info("{} Global Stats cleaned done in {} ms.", nbDeleted, System.currentTimeMillis() - startTime);

		startTime = System.currentTimeMillis();
		nbDeleted = addressStatsDao.deleteAddressStatsBefore(calendar.getTime());
		PERF_LOGGER.info("{} Addresses Stats cleaned done in {} ms.", nbDeleted, System.currentTimeMillis() - startTime);

	}

	/**
	 * Update the stats every 10 minutes
	 */
	@Scheduled(cron = "0 0/10 * * * *")
	public void updateStats() {
		updateGlobalStats();
		updateAddressStats();
	}

	private void updateGlobalStats() {
		try {
			USE_LOGGER.debug("Updating coinsolver GlobalStats...");

			long start = System.currentTimeMillis();

			strat.mining.multipool.stats.jersey.model.coinsolver.GlobalStats rawGlobalStats = coinsolverRestClient.getGlobalStats();

			if (rawGlobalStats != null) {
				GlobalStats globalStats = new GlobalStats();
				globalStats.setRefreshTime(new Date());
				globalStats.setMegaHashesPerSeconds(Float.valueOf(rawGlobalStats.getPool_hashrate()) / 1000);
				globalStats.setTotalBalance(Float.valueOf(rawGlobalStats.getBalance()));
				globalStats.setTotalUnexchanged(rawGlobalStats.getUnexchanged().floatValue());
				globalStats.setTotalImmature(rawGlobalStats.getImmature().floatValue());
				globalStats.setNbMiners(Integer.valueOf(rawGlobalStats.getPool_workers()));

				Map<String, Float> miningCoins = new HashMap<String, Float>();
				if (CollectionUtils.isNotEmpty(rawGlobalStats.getCurrently_mining())) {
					for (Currently_mining coin : rawGlobalStats.getCurrently_mining()) {
						miningCoins.put(coin.getFullname(), coin.getPoolhashrate().floatValue() / 1000);
					}
				}
				globalStats.setMiningCoins(miningCoins);

				globalStatsDao.insertGlobalStats(globalStats);
				PERF_LOGGER.info("coinsolver globalStats updated in {} ms.", System.currentTimeMillis() - start);
			}
		} catch (Exception e) {
			LOGGER.error("Error during global stats update.", e);
		}
	}

	private void updateAddressStats() {
		try {
			List<Address> addresses = addressDao.getAllAddresses();
			Date refreshDate = new Date();
			if (CollectionUtils.isNotEmpty(addresses)) {
				long start = System.currentTimeMillis();
				USE_LOGGER.debug("Update coinsolver addressStats for {} addresses.", addresses.size());

				for (Address address : addresses) {
					try {
						updateAddressStats(address, refreshDate);
					} catch (Exception e) {
						LOGGER.error("Error during address stats update for the address {}.", address.getAddress(), e);
					}
				}
				PERF_LOGGER.info("{} coinsolver addresses updated in {} ms.", addresses.size(), System.currentTimeMillis() - start);
			} else {
				LOGGER.debug("Do not update coinsolver addressStats sine no registered.");
			}
		} catch (Exception e) {
			LOGGER.error("Error during address stats update.", e);
		}
	}

	private AddressStats updateAddressStats(Address address, Date refreshDate) throws Exception {
		LOGGER.debug("Update addressStats for coinsolver address {}.", address.getAddress());
		long start = System.currentTimeMillis();

		strat.mining.multipool.stats.jersey.model.coinsolver.AddressStats rawAddressStats = coinsolverRestClient
				.getAddressStats(address.getAddress());

		AddressStats result = null;
		if (rawAddressStats != null) {
			result = new AddressStats();
			result.setAddressId(address.getId());
			result.setBalance(rawAddressStats.getBtc_balance() == null ? 0 : Float.valueOf(rawAddressStats.getBtc_balance()));
			result.setUnexchanged(rawAddressStats.getBtc_unexchanged() == null ? 0 : Float.valueOf(rawAddressStats.getBtc_unexchanged()));
			result.setImmature(rawAddressStats.getBtc_immature() == null ? 0 : Float.valueOf(rawAddressStats.getBtc_immature()));
			result.setPaidout(rawAddressStats.getTotal_btc_paid() == null ? 0 : Float.valueOf(rawAddressStats.getTotal_btc_paid()));
			result.setHashRate(rawAddressStats.getHashrate() == null ? 0 : Float.valueOf(rawAddressStats.getHashrate()));
			result.setRefreshTime(refreshDate);

			addressStatsDao.insertAddressStats(result);

		} else {
			throw new Exception("Unable to retrieve coinsolver raw stats for address " + address);
		}

		PERF_LOGGER.debug("coinsolver address {} updated in {} ms.", address.getAddress(), System.currentTimeMillis() - start);

		return result;
	}

	public Collection<String> getCurrentAddresses() {
		return currentAddresses;
	}

	public AddressStats initializeAddressStats(String bitcoinAddress) throws Exception {
		long start = System.currentTimeMillis();
		USE_LOGGER.info("Registering coinsolver new address {}", bitcoinAddress);

		Address address = new Address();
		AddressStats updateAddressStats = null;
		try {
			address = new Address();
			address.setAddress(bitcoinAddress);
			address = addressDao.insertAddress(address);

			updateAddressStats = updateAddressStats(address, new Date());

			currentAddresses.add(bitcoinAddress);

			PERF_LOGGER.info("New coinsolver address {} registered in {} ms.", bitcoinAddress, System.currentTimeMillis() - start);
		} catch (Exception e) {
			addressDao.removeAddress(address);
			throw e;
		}

		return updateAddressStats;
	}

}
