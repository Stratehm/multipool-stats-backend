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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.jersey.client.iface.WaffleRestClient;
import strat.mining.multipool.stats.jersey.model.waffle.Recent_payments;
import strat.mining.multipool.stats.jersey.model.waffle.Worker_hashrates;
import strat.mining.multipool.stats.persistence.dao.waffle.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.waffle.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.dao.waffle.iface.GlobalStatsDAO;
import strat.mining.multipool.stats.persistence.dao.waffle.iface.TransactionDAO;
import strat.mining.multipool.stats.persistence.model.waffle.Address;
import strat.mining.multipool.stats.persistence.model.waffle.AddressStats;
import strat.mining.multipool.stats.persistence.model.waffle.GlobalStats;
import strat.mining.multipool.stats.persistence.model.waffle.Transaction;
import strat.mining.multipool.stats.persistence.model.waffle.WorkerStats;

@Component("waffleStatsBuilderTask")
public class WaffleStatsBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(WaffleStatsBuilder.class);

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	private static final Logger USE_LOGGER = LoggerFactory.getLogger("multipoolStatsUse");

	private static final String PAYEMENT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	@Resource
	private WaffleRestClient waffleRestClient;

	@Resource
	private GlobalStatsDAO globalStatsDao;

	@Resource
	private AddressDAO addressDao;

	@Resource
	private AddressStatsDAO addressStatsDao;

	@Resource
	private TransactionDAO transactionDao;

	private Random random = new Random();

	private Set<String> currentAddresses;

	public WaffleStatsBuilder() {
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
		try {
			updateGlobalStats();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			USE_LOGGER.debug("Updating waffle GlobalStats...");

			Integer waitTime = random.nextInt(30000);
			USE_LOGGER.debug("Waiting for {} ms before requesting for jamming.", waitTime);
			Thread.sleep(waitTime);

			long start = System.currentTimeMillis();

			GlobalStats globalStats = waffleRestClient.getGlobalStats();

			Date refreshDate = new Date(System.currentTimeMillis() - random.nextInt(15000));
			globalStats.setRefreshTime(refreshDate);
			if (globalStats != null) {
				globalStatsDao.insertGlobalStats(globalStats);
				PERF_LOGGER.info("Waffle globalStats updated in {} ms.", System.currentTimeMillis() - start);
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
				USE_LOGGER.debug("Update Waffle addressStats for {} addresses.", addresses.size());

				for (Address address : addresses) {
					try {
						updateAddressStats(address, refreshDate);
					} catch (Exception e) {
						LOGGER.error("Error during address stats update for the address {}.", address.getAddress(), e);
					}
				}
				PERF_LOGGER.info("{} Waffle addresses updated in {} ms.", addresses.size(), System.currentTimeMillis() - start);
			} else {
				LOGGER.debug("Do not update Waffle addressStats sine no registered.");
			}
		} catch (Exception e) {
			LOGGER.error("Error during address stats update.", e);
		}
	}

	private AddressStats updateAddressStats(Address address, Date refreshDate) throws Exception {
		LOGGER.debug("Update addressStats for Waffle address {}.", address.getAddress());
		long start = System.currentTimeMillis();

		strat.mining.multipool.stats.jersey.model.waffle.AddressStats rawAddressStats = waffleRestClient.getAddressStats(address.getAddress());

		AddressStats result = null;
		if (rawAddressStats != null && StringUtils.isEmpty(rawAddressStats.getError())) {
			result = new AddressStats();
			result.setAddressId(address.getId());
			result.setBalance(rawAddressStats.getBalances() == null || rawAddressStats.getBalances().getConfirmed() == null ? 0 : rawAddressStats
					.getBalances().getConfirmed().floatValue());
			result.setUnexchanged(rawAddressStats.getBalances() == null || rawAddressStats.getBalances().getUnconverted() == null ? 0
					: rawAddressStats.getBalances().getUnconverted().floatValue());
			result.setPaidout(rawAddressStats.getBalances() == null || rawAddressStats.getBalances().getSent() == null ? 0 : rawAddressStats
					.getBalances().getSent().floatValue());
			result.setHashRate(rawAddressStats.getHash_rate() == null ? 0 : rawAddressStats.getHash_rate().floatValue());
			result.setRefreshTime(refreshDate);

			List<WorkerStats> workersStats = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(rawAddressStats.getWorker_hashrates())) {
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -7);
				for (Worker_hashrates workerHasrate : rawAddressStats.getWorker_hashrates()) {
					// Keep the worker stats only if it has been seen in the 7
					// previous days
					Date lastSeen = new Date(workerHasrate.getLast_seen().longValue() * 1000);
					if (lastSeen.after(calendar.getTime())) {
						WorkerStats worker = new WorkerStats();
						if (workerHasrate.getUsername() != null) {
							worker.setUsername(workerHasrate.getUsername());
							worker.setHashrate(workerHasrate.getHashrate() == null ? 0 : workerHasrate.getHashrate().floatValue());
							worker.setStaleRate(workerHasrate.getStalerate() == null ? 0 : workerHasrate.getStalerate().floatValue());
							workersStats.add(worker);
						}
					}
				}
			}
			result.setWorkerStats(workersStats);

			addressStatsDao.insertAddressStats(result);

			Transaction lastTransaction = transactionDao.getLastTransaction(address.getId());
			SimpleDateFormat dateFormat = new SimpleDateFormat(PAYEMENT_DATE_PATTERN);
			if (CollectionUtils.isNotEmpty(rawAddressStats.getRecent_payments())) {
				for (Recent_payments payement : rawAddressStats.getRecent_payments()) {
					Date payementDate = dateFormat.parse(payement.getTime());
					if (lastTransaction == null || lastTransaction.getDate().before(payementDate)) {
						Transaction transaction = new Transaction();
						transaction.setAddressId(address.getId());
						transaction.setAmount(payement.getAmount() == null ? 0 : Float.valueOf(payement.getAmount()));
						transaction.setDate(payementDate);
						transaction.setTransactionId(payement.getTxn());

						transactionDao.insertTransaction(transaction);
					} else {
						// When all last transactions are inserted, just break
						break;
					}
				}
			}
		} else {
			throw new Exception(rawAddressStats == null ? "Unable to retrieve Waffle raw stats for address " + address : rawAddressStats.getError());
		}

		PERF_LOGGER.debug("Waffle address {} updated in {} ms.", address.getAddress(), System.currentTimeMillis() - start);

		return result;
	}

	public AddressStats initializeAddressStats(String addressString) throws Exception {
		long start = System.currentTimeMillis();
		USE_LOGGER.info("Registering new Waffle address {}", addressString);

		Address address = new Address();
		AddressStats updateAddressStats = null;
		try {
			address.setAddress(addressString);
			address = addressDao.insertAddress(address);

			updateAddressStats = updateAddressStats(address, new Date());

			currentAddresses.add(addressString);

			PERF_LOGGER.info("New Waffle address {} registered in {} ms.", addressString, System.currentTimeMillis() - start);
		} catch (Exception e) {
			addressDao.removeAddress(address);
			throw e;
		}

		return updateAddressStats;
	}

	public Set<String> getCurrentAddresses() {
		return currentAddresses;
	}
}
