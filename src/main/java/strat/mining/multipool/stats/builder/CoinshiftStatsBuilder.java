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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.jersey.client.iface.CoinshiftRestClient;
import strat.mining.multipool.stats.jersey.model.coinshift.Coin;
import strat.mining.multipool.stats.jersey.model.coinshift.Payouts;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.GlobalStatsDAO;
import strat.mining.multipool.stats.persistence.dao.coinshift.iface.TransactionDAO;
import strat.mining.multipool.stats.persistence.model.coinshift.Address;
import strat.mining.multipool.stats.persistence.model.coinshift.AddressStats;
import strat.mining.multipool.stats.persistence.model.coinshift.GlobalStats;
import strat.mining.multipool.stats.persistence.model.coinshift.Transaction;

@Component("coinshiftStatsBuilderTask")
public class CoinshiftStatsBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoinshiftStatsBuilder.class);

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	private static final String PAYEMENT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ssXXX";

	@Resource
	private CoinshiftRestClient coinshiftRestClient;

	@Resource
	private GlobalStatsDAO globalStatsDao;

	@Resource
	private AddressDAO addressDao;

	@Resource
	private AddressStatsDAO addressStatsDao;

	@Resource
	private TransactionDAO transactionDao;

	private Set<String> currentAddresses;

	public CoinshiftStatsBuilder() {
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
		updateStats();
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
			USE_LOGGER.debug("Updating coinshift GlobalStats...");

			long start = System.currentTimeMillis();

			strat.mining.multipool.stats.jersey.model.coinshift.GlobalStats rawGlobalStats = coinshiftRestClient.getGlobalStats();

			Date refreshDate = new Date();
			GlobalStats globalStats = new GlobalStats();
			globalStats.setMegaHashesPerSeconds(Float.valueOf(rawGlobalStats.getHashrate()));
			globalStats.setRejectedMegaHashesPerSeconds(Float.valueOf(rawGlobalStats.getRejectrate()));

			if (MapUtils.isNotEmpty(rawGlobalStats.getCoins())) {
				for (Entry<String, Coin> entry : rawGlobalStats.getCoins().entrySet()) {
					if ("BTC".equalsIgnoreCase(entry.getKey())) {
						globalStats.setTotalBalance(Float.valueOf(entry.getValue().getExchanged_balance()));
						globalStats.setTotalUnexchanged(Float.valueOf(entry.getValue().getEstimated_unexchanged_balance()));
						break;
					}
				}
			}

			globalStats.setRefreshTime(refreshDate);
			if (globalStats != null) {
				globalStatsDao.insertGlobalStats(globalStats);
				PERF_LOGGER.info("Coinshift globalStats updated in {} ms.", System.currentTimeMillis() - start);
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
				USE_LOGGER.debug("Update coinshift addressStats for {} addresses.", addresses.size());

				for (Address address : addresses) {
					try {
						updateAddressStats(address, refreshDate);
					} catch (Exception e) {
						LOGGER.error("Error during address stats update for the address {}.", address.getAddress(), e);
					}
				}
				PERF_LOGGER.info("{} coinshift addresses updated in {} ms.", addresses.size(), System.currentTimeMillis() - start);
			} else {
				LOGGER.debug("Do not update coinshift addressStats sine no registered.");
			}
		} catch (Exception e) {
			LOGGER.error("Error during address stats update.", e);
		}
	}

	private AddressStats updateAddressStats(Address address, Date refreshDate) throws Exception {
		LOGGER.debug("Update addressStats for coinshift address {}.", address.getAddress());
		long start = System.currentTimeMillis();

		strat.mining.multipool.stats.jersey.model.coinshift.AddressStats rawAddressStats = coinshiftRestClient.getAddressStats(address.getAddress());

		AddressStats result = null;
		if (rawAddressStats != null) {
			result = new AddressStats();
			result.setAddressId(address.getId());
			result.setBalance(rawAddressStats.getExchanged_balance() == null ? 0 : Float.valueOf(rawAddressStats.getExchanged_balance()));
			result.setUnexchanged(rawAddressStats.getUnexchanged_balance() == null ? 0 : Float.valueOf(rawAddressStats.getUnexchanged_balance()));
			result.setPaidout(rawAddressStats.getPayout_sum() == null ? 0 : Float.valueOf(rawAddressStats.getPayout_sum()));
			result.setHashRate(rawAddressStats.getHashrate() == null ? 0 : rawAddressStats.getHashrate().floatValue());
			result.setRejectedHashRate(rawAddressStats.getRejectrate() == null ? 0 : rawAddressStats.getRejectrate().floatValue());
			result.setRefreshTime(refreshDate);

			addressStatsDao.insertAddressStats(result);

			Transaction lastTransaction = transactionDao.getLastTransaction(address.getId());
			SimpleDateFormat dateFormat = new SimpleDateFormat(PAYEMENT_DATE_PATTERN);
			if (CollectionUtils.isNotEmpty(rawAddressStats.getPayouts())) {
				for (Payouts payement : rawAddressStats.getPayouts()) {
					Date payementDate = dateFormat.parse(payement.getTimestamp());
					if (lastTransaction == null || lastTransaction.getDate().before(payementDate)) {
						Transaction transaction = new Transaction();
						transaction.setAddressId(address.getId());
						transaction.setAmount(payement.getValue() == null ? 0 : Float.valueOf(payement.getValue()));
						transaction.setDate(payementDate);
						transaction.setTransactionId(payement.getTx());

						transactionDao.insertTransaction(transaction);
					} else {
						// When all last transactions are inserted, just break
						break;
					}
				}
			}
		} else {
			throw new Exception("Unable to retrieve coinshift raw stats for address " + address);
		}

		PERF_LOGGER.debug("coinshift address {} updated in {} ms.", address.getAddress(), System.currentTimeMillis() - start);

		return result;
	}

	public Collection<String> getCurrentAddresses() {
		return currentAddresses;
	}

	public AddressStats initializeAddressStats(String bitcoinAddress) throws Exception {
		long start = System.currentTimeMillis();
		USE_LOGGER.info("Registering Coinshift new address {}", bitcoinAddress);

		Address address = new Address();
		AddressStats updateAddressStats = null;
		try {
			address = new Address();
			address.setAddress(bitcoinAddress);
			address = addressDao.insertAddress(address);

			updateAddressStats = updateAddressStats(address, new Date());

			currentAddresses.add(bitcoinAddress);

			PERF_LOGGER.info("New Coinshift address {} registered in {} ms.", bitcoinAddress, System.currentTimeMillis() - start);
		} catch (Exception e) {
			addressDao.removeAddress(address);
			throw e;
		}

		return updateAddressStats;
	}

}
