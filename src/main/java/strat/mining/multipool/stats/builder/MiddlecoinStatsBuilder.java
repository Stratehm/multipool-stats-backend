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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.jersey.client.iface.MiddlecoinRestClient;
import strat.mining.multipool.stats.jersey.model.middlecoin.AddressReport;
import strat.mining.multipool.stats.jersey.model.middlecoin.GlobalStats;
import strat.mining.multipool.stats.persistence.dao.middlecoin.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.middlecoin.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.dao.middlecoin.iface.GlobalStatsDAO;
import strat.mining.multipool.stats.persistence.model.middlecoin.Address;
import strat.mining.multipool.stats.persistence.model.middlecoin.AddressStats;

@Component("middlecoinStatsBuilderTask")
public class MiddlecoinStatsBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MiddlecoinStatsBuilder.class);

	private static final Logger PERF_LOGGER = LoggerFactory.getLogger("multipoolStatsPerf");

	@Resource
	private GlobalStatsDAO globalStatsDAO;

	@Resource
	private AddressDAO addressDAO;

	@Resource
	private AddressStatsDAO addressStatsDAO;

	@Resource
	private MiddlecoinRestClient middlecoinRestClient;

	private Set<String> currentAddresses;

	public MiddlecoinStatsBuilder() {
		currentAddresses = Collections.synchronizedSet(new HashSet<String>());
	}

	@PostConstruct
	public void init() {
		List<Address> addresses = addressDAO.getAllAddresses();
		if (CollectionUtils.isNotEmpty(addresses)) {
			for (Address address : addresses) {
				currentAddresses.add(address.getAddress());
			}
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
		int nbDeleted = globalStatsDAO.deleteGlobalStatsBefore(calendar.getTime());
		PERF_LOGGER.info("{} Global Stats cleaned done in {} ms.", nbDeleted, System.currentTimeMillis() - startTime);

		startTime = System.currentTimeMillis();
		nbDeleted = addressStatsDAO.deleteAddressStatsBefore(calendar.getTime());
		PERF_LOGGER.info("{} Addresses Stats cleaned done in {} ms.", nbDeleted, System.currentTimeMillis() - startTime);

	}

	/**
	 * Update the stats every 10 minutes
	 */
	@Scheduled(cron = "0 0/10 * * * *")
	public void updateStats() {
		try {
			GlobalStats globalStats = middlecoinRestClient.getRawStats();
			persistStats(globalStats);
		} catch (Exception e) {
			LOGGER.error("Error during stats update.", e);
		}
	}

	private void persistStats(GlobalStats globalStats) {
		Timestamp updateTime = new Timestamp(globalStats.getTime().getTime());
		Timestamp refreshTime = new Timestamp(System.currentTimeMillis());

		try {
			// Insert the global stats
			insertGlobalStats(globalStats, updateTime, refreshTime);
		} catch (Exception e) {
			LOGGER.warn("Failed to persist the global stats. {}", e);
		}

		long start = System.currentTimeMillis();

		// Then persist addresses stats
		int nbInserted = 0;
		List<AddressStats> addressStats = new ArrayList<>();
		for (Entry<String, AddressReport> entry : globalStats.getReport().entrySet()) {
			try {
				AddressStats createdAddressStats = createAddressStats(entry, updateTime, refreshTime);
				if (createdAddressStats != null) {
					addressStats.add(createdAddressStats);
				}
				nbInserted++;
			} catch (Exception e) {
				LOGGER.warn("Failed to persist the address stats {}. {}", entry.getKey(), e);
			}
		}

		addressStatsDAO.insertAddressStats(addressStats);

		PERF_LOGGER.info("{} addresses stats inserted in {} ms.", nbInserted, System.currentTimeMillis() - start);
	}

	public void insertGlobalStats(GlobalStats globalStats, Timestamp updateTime, Timestamp refreshTime) {
		// Persist global stats
		strat.mining.multipool.stats.persistence.model.middlecoin.GlobalStats persistenceGlobalStats = new strat.mining.multipool.stats.persistence.model.middlecoin.GlobalStats();
		persistenceGlobalStats.setBalance(globalStats.getTotalBalance());
		persistenceGlobalStats.setImmature(globalStats.getTotalImmatureBalance());
		persistenceGlobalStats.setMegaHashesPerSeconds(globalStats.getTotalMegahashesPerSecond());
		persistenceGlobalStats.setPaidOut(globalStats.getTotalPaidOut());
		persistenceGlobalStats.setRejectedMegaHashesPerSeconds(globalStats.getTotalRejectedMegahashesPerSecond());
		persistenceGlobalStats.setRefreshTime(refreshTime);
		persistenceGlobalStats.setUpdateTime(updateTime);
		persistenceGlobalStats.setUnexchanged(globalStats.getTotalUnexchangedBalance());

		globalStatsDAO.insertGlobalStats(persistenceGlobalStats);
		LOGGER.debug("Global stats persisted.");
	}

	public AddressStats createAddressStats(Entry<String, AddressReport> entry, Timestamp updateTime, Timestamp refreshTime) {
		AddressReport report = entry.getValue();
		LOGGER.debug("Persisting stats for address {}.", entry.getKey());

		Address address = addressDAO.getAddress(entry.getKey());

		if (address == null) {
			address = new Address();
			address.setAddress(entry.getKey());
			addressDAO.insertAddress(address);
		}

		AddressStats addressStats = new AddressStats();
		addressStats.setAddressId(address.getId());

		addressStats.setBalance(report.getBitcoinBalance());
		addressStats.setImmature(report.getImmatureBalance());
		addressStats.setLastHourRejectedShares(report.getLastHourRejectedShares());
		addressStats.setLastHourShares(report.getLastHourShares());
		addressStats.setMegaHashesPerSeconds(report.getMegahashesPerSecond());
		addressStats.setPaidOut(report.getPaidOut());
		addressStats.setRejectedMegaHashesPerSeconds(report.getRejectedMegahashesPerSecond());
		addressStats.setRefreshTime(refreshTime);
		addressStats.setUpdateTime(updateTime);
		addressStats.setUnexchanged(report.getUnexchangedBalance());

		currentAddresses.add(entry.getKey());

		return addressStats;
	}

	public Set<String> getCurrentAddresses() {
		return currentAddresses;
	}
}
