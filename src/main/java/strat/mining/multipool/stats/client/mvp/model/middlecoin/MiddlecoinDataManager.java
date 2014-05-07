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
package strat.mining.multipool.stats.client.mvp.model.middlecoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.event.AddressBlockchainInfoLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.AddressPaidoutLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.AllAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.AllGlobalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.AllTotalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryAddressBlockchainInfoLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryAddressPaidoutLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryLastAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastGlobalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastTotalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LoadFailureEvent;
import strat.mining.multipool.stats.client.mvp.event.TotalAddressBlockchainInfoLoadedEvent;
import strat.mining.multipool.stats.client.mvp.handler.AddressBlockchainInfoLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.AddressPaidoutLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.AllAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.AllGlobalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.AllTotalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryAddressBlockchainInfoLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryAddressPaidoutLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryLastAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastGlobalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastTotalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LoadFailureHandler;
import strat.mining.multipool.stats.client.mvp.handler.TotalAddressBlockchainInfoLoadHandler;
import strat.mining.multipool.stats.client.util.Pair;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MiddlecoinDataManager {

	private static final int AUTO_REFRESH_DELAY_STATS = 600000;

	private static final long SECONDS_IN_24H = 86400;

	private ClientFactory clientFactory;

	private MiddlecoinDataContainer dataContainer;

	private int nextAddressStatsLoadingIndex;
	private int nbAddressStatsLoaded;

	private int nextAddressStatsRefreshingIndex;
	private boolean isAddressStatsRefreshInProgess;

	private int nextAddressPaidoutLoadingIndex;
	private boolean isAddressPaidoutLoadInProgess;

	private int nextAddressBlockchainLoadingIndex;
	private boolean isAddressBlockchainLoadInProgess;

	// An ID used to avoid conflict between two quick search of addresses.
	private int currentAddressLoadRequestId;

	private Timer autoRefreshStatsTimer;

	private boolean isAutoRefresh;

	public MiddlecoinDataManager(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		dataContainer = new MiddlecoinDataContainer();
		currentAddressLoadRequestId = 0;
		isAutoRefresh = true;
	}

	/**
	 * Set the addresses to work on. Reset all data dependant of this list.
	 * 
	 * @param addresses
	 */
	public void setAddresses(List<String> addresses) {
		dataContainer.setAddresses(addresses);
		setAutoRefresh(isAutoRefresh);
	}

	/**
	 * Return the list of currently used addresses
	 * 
	 * @return
	 */
	public List<String> getAddresses() {
		return dataContainer.getAddresses();
	}

	/**
	 * Load all the stats of all addresses.
	 */
	public void loadAllAddressStats() {
		nextAddressStatsLoadingIndex = 0;
		nbAddressStatsLoaded = 0;
		currentAddressLoadRequestId++;
		isAddressStatsRefreshInProgess = false;
		isAddressPaidoutLoadInProgess = false;

		loadAllAddressesStats(getNextAddressStatsToLoad(), currentAddressLoadRequestId);
	}

	/**
	 * Load the last stats of all addresses.
	 */
	public void loadLastAddressStats() {
		// Accept the load request only if not already running.
		if (!isAddressStatsRefreshInProgess) {
			nextAddressStatsRefreshingIndex = 0;
			isAddressStatsRefreshInProgess = true;
			loadLastAddressStats(getNextAddressStatsToRefresh(), currentAddressLoadRequestId);
		}
	}

	/**
	 * Load all the GlobalStats
	 */
	public void loadAllGlobalStats() {
		clientFactory.getMiddlecoinServices().getAllGlobalStats(new AsyncCallback<List<GlobalStatsDTO>>() {

			@Override
			public void onSuccess(List<GlobalStatsDTO> result) {
				dataContainer.getGlobalStats().addAll(result);
				computeGlobalAggregatedStats();
				fireEvent(new AllGlobalStatsLoadedEvent<GlobalStatsDTO>(result));
			}

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new LoadFailureEvent("Failed to load global stats.", caught));
			}
		});
	}

	/**
	 * Loads the last GlobalStats
	 */
	public void loadLastGlobalStats() {
		clientFactory.getMiddlecoinServices().getLastGlobalStats(new AsyncCallback<GlobalStatsDTO>() {
			public void onFailure(Throwable caught) {
				fireEvent(new LoadFailureEvent("Failed to load last global stats.", caught));
			}

			public void onSuccess(GlobalStatsDTO result) {
				dataContainer.getGlobalStats().add(result);
				fireEvent(new LastGlobalStatsLoadedEvent<GlobalStatsDTO>(result));
			}
		});
	}

	/**
	 * Load the paidout for all the addresses
	 */
	public void loadAllPaidout() {
		// Accept the load request only if not already running
		if (!isAddressPaidoutLoadInProgess && getAddresses() != null && getAddresses().size() > 0) {
			nextAddressPaidoutLoadingIndex = 0;
			isAddressPaidoutLoadInProgess = true;
			loadPaidout(getNextAddressPaidoutToLoad(), currentAddressLoadRequestId);
		}
	}

	/**
	 * Load the blockchain info for all addresses.
	 */
	public void loadAllBlockchainInfo() {
		// Accept the load request only if not already running.
		if (!isAddressBlockchainLoadInProgess && getAddresses() != null && getAddresses().size() > 0) {
			nextAddressBlockchainLoadingIndex = 0;
			isAddressBlockchainLoadInProgess = true;
			loadBlockchainInfo(getNextAddressBlockchainToLoad(), currentAddressLoadRequestId);
		}
	}

	/**
	 * Load all the stats for the given address. Once done, request for the next
	 * address.
	 * 
	 * @param address
	 * @param requestId
	 */
	private void loadAllAddressesStats(final String address, final int requestId) {
		if (address != null) {
			clientFactory.getMiddlecoinServices().getAllAddressStats(address, new AsyncCallback<List<AddressStatsDTO>>() {

				@Override
				public void onSuccess(List<AddressStatsDTO> result) {
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						nbAddressStatsLoaded++;
						dataContainer.getAddressesStats().put(address, result);
						fireEvent(new AllAddressStatsLoadedEvent<AddressStatsDTO>(address, result));
						loadAllAddressesStats(getNextAddressStatsToLoad(), requestId);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					fireEvent(new LoadFailureEvent("Failed to load stats for address " + address, caught));
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						loadAllAddressesStats(getNextAddressStatsToLoad(), requestId);
					}
				}
			});
		}
	}

	/**
	 * Return the next address to load. Return null if no more address has to be
	 * loaded.
	 * 
	 * @return
	 */
	private String getNextAddressStatsToLoad() {
		String result = null;
		if (dataContainer.getAddresses() != null && dataContainer.getAddresses().size() > 0) {
			if (nextAddressStatsLoadingIndex < dataContainer.getAddresses().size()) {
				result = dataContainer.getAddresses().get(nextAddressStatsLoadingIndex);
				nextAddressStatsLoadingIndex++;

				if (result != null && result.isEmpty()) {
					result = null;
				}
			}

			if (result == null) {
				onAddressesStatsLoadEnd();
			}
		}

		return result;
	}

	/**
	 * Called when all addresses stats are loaded
	 */
	private void onAddressesStatsLoadEnd() {
		fireEvent(new EveryAddressStatsLoadedEvent<AddressStatsDTO>(dataContainer.getAddressesStats()));
		// Load the total only if more than one address loaded
		if (dataContainer.getAddressesStats().size() > 0 && nbAddressStatsLoaded > 1) {
			computeAllTotalStats();
		}
	}

	/**
	 * Load all the total stats
	 */
	private void computeAllTotalStats() {
		Map<String, Map<Date, AddressStatsDTO>> dtoByDateAndAddress = new HashMap<String, Map<Date, AddressStatsDTO>>();
		if (dataContainer.getAddressesStats() != null && !dataContainer.getAddressesStats().isEmpty()) {
			for (Entry<String, List<AddressStatsDTO>> entry : dataContainer.getAddressesStats().entrySet()) {
				if (entry.getValue() != null && !entry.getValue().isEmpty()) {
					Map<Date, AddressStatsDTO> dtoByDate = new HashMap<Date, AddressStatsDTO>();
					for (AddressStatsDTO statsDto : entry.getValue()) {
						dtoByDate.put(statsDto.getRefreshTime(), statsDto);
					}
					dtoByDateAndAddress.put(entry.getKey(), dtoByDate);
				}
			}
		}

		Map<Date, AddressStatsDTO> resultMap = new HashMap<Date, AddressStatsDTO>();

		for (Map<Date, AddressStatsDTO> singleAddressMap : dtoByDateAndAddress.values()) {
			for (Entry<Date, AddressStatsDTO> entry : singleAddressMap.entrySet()) {
				AddressStatsDTO totalStatsForTheGivenDate = resultMap.get(entry.getKey());
				AddressStatsDTO singleStatsForTheGivenDate = entry.getValue();

				if (totalStatsForTheGivenDate == null) {
					totalStatsForTheGivenDate = new AddressStatsDTO();
					resultMap.put(entry.getKey(), totalStatsForTheGivenDate);
				}

				totalStatsForTheGivenDate.setAddress("");

				totalStatsForTheGivenDate.setBalance((totalStatsForTheGivenDate.getBalance() == null ? 0 : totalStatsForTheGivenDate.getBalance())
						+ (singleStatsForTheGivenDate.getBalance() == null ? 0 : singleStatsForTheGivenDate.getBalance()));

				totalStatsForTheGivenDate.setImmature((totalStatsForTheGivenDate.getImmature() == null ? 0 : totalStatsForTheGivenDate.getImmature())
						+ (singleStatsForTheGivenDate.getImmature() == null ? 0 : singleStatsForTheGivenDate.getImmature()));

				totalStatsForTheGivenDate.setLastHourRejectedShares((totalStatsForTheGivenDate.getLastHourRejectedShares() == null ? 0
						: totalStatsForTheGivenDate.getLastHourRejectedShares())
						+ (singleStatsForTheGivenDate.getLastHourRejectedShares() == null ? 0 : singleStatsForTheGivenDate
								.getLastHourRejectedShares()));

				totalStatsForTheGivenDate.setLastHourShares((totalStatsForTheGivenDate.getLastHourShares() == null ? 0 : totalStatsForTheGivenDate
						.getLastHourShares())
						+ (singleStatsForTheGivenDate.getLastHourShares() == null ? 0 : singleStatsForTheGivenDate.getLastHourShares()));

				totalStatsForTheGivenDate.setMegaHashesPerSeconds((totalStatsForTheGivenDate.getMegaHashesPerSeconds() == null ? 0
						: totalStatsForTheGivenDate.getMegaHashesPerSeconds())
						+ (singleStatsForTheGivenDate.getMegaHashesPerSeconds() == null ? 0 : singleStatsForTheGivenDate.getMegaHashesPerSeconds()));

				totalStatsForTheGivenDate.setPaidOut((totalStatsForTheGivenDate.getPaidOut() == null ? 0 : totalStatsForTheGivenDate.getPaidOut())
						+ (singleStatsForTheGivenDate.getPaidOut() == null ? 0 : singleStatsForTheGivenDate.getPaidOut()));

				totalStatsForTheGivenDate.setRejectedMegaHashesPerSeconds((totalStatsForTheGivenDate.getRejectedMegaHashesPerSeconds() == null ? 0
						: totalStatsForTheGivenDate.getRejectedMegaHashesPerSeconds())
						+ (singleStatsForTheGivenDate.getRejectedMegaHashesPerSeconds() == null ? 0 : singleStatsForTheGivenDate
								.getRejectedMegaHashesPerSeconds()));

				totalStatsForTheGivenDate.setUnexchanged((totalStatsForTheGivenDate.getUnexchanged() == null ? 0 : totalStatsForTheGivenDate
						.getUnexchanged()) + (singleStatsForTheGivenDate.getUnexchanged() == null ? 0 : singleStatsForTheGivenDate.getUnexchanged()));

				totalStatsForTheGivenDate.setRefreshTime(singleStatsForTheGivenDate.getRefreshTime());

				totalStatsForTheGivenDate.setUpdateTime(singleStatsForTheGivenDate.getUpdateTime());
			}
		}

		List<AddressStatsDTO> result = new ArrayList<AddressStatsDTO>(resultMap.values());
		Collections.sort(result, new Comparator<AddressStatsDTO>() {
			public int compare(AddressStatsDTO o1, AddressStatsDTO o2) {
				return o1.getRefreshTime().compareTo(o2.getRefreshTime());
			}
		});

		dataContainer.getTotalStats().addAll(result);
		fireEvent(new AllTotalStatsLoadedEvent<AddressStatsDTO>(result));
	}

	/**
	 * Get the last stats for the given address
	 * 
	 * @param address
	 */
	private void loadLastAddressStats(final String address, final int requestId) {
		if (address != null && dataContainer.getAddresses() != null && dataContainer.getAddresses().size() > 0) {
			clientFactory.getMiddlecoinServices().getLastAddressStats(address, new AsyncCallback<AddressStatsDTO>() {
				public void onFailure(Throwable caught) {
					fireEvent(new LoadFailureEvent("Failed to refresh the address " + address, caught));
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						loadLastAddressStats(getNextAddressStatsToRefresh(), requestId);
					}
				}

				public void onSuccess(AddressStatsDTO result) {
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						dataContainer.getAddressesStats().get(address).add(result);
						fireEvent(new LastAddressStatsLoadedEvent<AddressStatsDTO>(address, result));
						loadLastAddressStats(getNextAddressStatsToRefresh(), requestId);
					}
				}
			});
		}
	}

	/**
	 * Return the next address to load. Return null if no more address has to be
	 * loaded.
	 * 
	 * @return
	 */
	private String getNextAddressStatsToRefresh() {
		String result = null;
		if (dataContainer.getAddresses() != null && dataContainer.getAddresses().size() > 0) {
			if (nextAddressStatsRefreshingIndex < dataContainer.getAddresses().size()) {
				result = dataContainer.getAddresses().get(nextAddressStatsRefreshingIndex);
				nextAddressStatsRefreshingIndex++;

				if (result != null && result.isEmpty()) {
					result = null;
				}
			}

			if (result == null) {
				onAddressesStatsRefreshEnd();
			}
		}

		return result;
	}

	/**
	 * Called at the end of the refresh of all addresses.
	 */
	private void onAddressesStatsRefreshEnd() {
		isAddressStatsRefreshInProgess = false;
		// Load the total only if more than one address loaded
		if (dataContainer.getAddressesStats().size() > 0 && nbAddressStatsLoaded > 1) {
			computeLastTotalStats();
		}
	}

	/**
	 * Compute the last total stats.
	 */
	private void computeLastTotalStats() {
		AddressStatsDTO totalStats = new AddressStatsDTO();
		for (List<AddressStatsDTO> addressStats : dataContainer.getAddressesStats().values()) {
			AddressStatsDTO lastAddressStats = addressStats.get(addressStats.size() - 1);
			totalStats.setAddress("");

			totalStats.setBalance((totalStats.getBalance() == null ? 0 : totalStats.getBalance())
					+ (lastAddressStats.getBalance() == null ? 0 : lastAddressStats.getBalance()));

			totalStats.setImmature((totalStats.getImmature() == null ? 0 : totalStats.getImmature())
					+ (lastAddressStats.getImmature() == null ? 0 : lastAddressStats.getImmature()));

			totalStats.setLastHourRejectedShares((totalStats.getLastHourRejectedShares() == null ? 0 : totalStats.getLastHourRejectedShares())
					+ (lastAddressStats.getLastHourRejectedShares() == null ? 0 : lastAddressStats.getLastHourRejectedShares()));

			totalStats.setLastHourShares((totalStats.getLastHourShares() == null ? 0 : totalStats.getLastHourShares())
					+ (lastAddressStats.getLastHourShares() == null ? 0 : lastAddressStats.getLastHourShares()));

			totalStats.setMegaHashesPerSeconds((totalStats.getMegaHashesPerSeconds() == null ? 0 : totalStats.getMegaHashesPerSeconds())
					+ (lastAddressStats.getMegaHashesPerSeconds() == null ? 0 : lastAddressStats.getMegaHashesPerSeconds()));

			totalStats.setPaidOut((totalStats.getPaidOut() == null ? 0 : totalStats.getPaidOut())
					+ (lastAddressStats.getPaidOut() == null ? 0 : lastAddressStats.getPaidOut()));

			totalStats.setRejectedMegaHashesPerSeconds((totalStats.getRejectedMegaHashesPerSeconds() == null ? 0 : totalStats
					.getRejectedMegaHashesPerSeconds())
					+ (lastAddressStats.getRejectedMegaHashesPerSeconds() == null ? 0 : lastAddressStats.getRejectedMegaHashesPerSeconds()));

			totalStats.setUnexchanged((totalStats.getUnexchanged() == null ? 0 : totalStats.getUnexchanged())
					+ (lastAddressStats.getUnexchanged() == null ? 0 : lastAddressStats.getUnexchanged()));

			totalStats.setRefreshTime(lastAddressStats.getRefreshTime());

			totalStats.setUpdateTime(lastAddressStats.getUpdateTime());
		}

		dataContainer.getTotalStats().add(totalStats);
		fireEvent(new LastTotalStatsLoadedEvent<AddressStatsDTO>(totalStats));
	}

	/**
	 * Load the paidout for the given address
	 * 
	 * @param address
	 */
	private void loadPaidout(final String address, final int requestId) {
		if (address != null) {
			clientFactory.getMiddlecoinServices().getAllAddressPaidout(address, new AsyncCallback<List<AddressPaidoutDTO>>() {
				public void onSuccess(List<AddressPaidoutDTO> result) {
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						dataContainer.getAddressesPaidout().put(address, result);
						fireEvent(new AddressPaidoutLoadedEvent<AddressPaidoutDTO>(address, result));
						loadPaidout(getNextAddressPaidoutToLoad(), requestId);
					}
				}

				public void onFailure(Throwable caught) {
					fireEvent(new LoadFailureEvent("Failed to load paidout for address " + address, caught));
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						loadPaidout(getNextAddressPaidoutToLoad(), requestId);
					}
				}
			});
		}
	}

	/**
	 * Return the next address to load for paidout. Return null if no more
	 * address has to be loaded.
	 * 
	 * @return
	 */
	private String getNextAddressPaidoutToLoad() {
		String result = null;
		if (dataContainer.getAddresses() != null && dataContainer.getAddresses().size() > 0) {
			if (nextAddressPaidoutLoadingIndex < dataContainer.getAddresses().size()) {
				result = dataContainer.getAddresses().get(nextAddressPaidoutLoadingIndex);
				nextAddressPaidoutLoadingIndex++;

				if (result != null && result.isEmpty()) {
					result = null;
				}
			}

			if (result == null) {
				onAddressesPaidoutLoadEnd();
			}
		}

		return result;
	}

	/**
	 * Called when all paidout are loaded
	 */
	private void onAddressesPaidoutLoadEnd() {
		isAddressStatsRefreshInProgess = false;
		fireEvent(new EveryAddressPaidoutLoadedEvent<AddressPaidoutDTO>(dataContainer.getAddressesPaidout()));
	}

	/**
	 * Load the blockchain info for the given address
	 * 
	 * @param address
	 */
	private void loadBlockchainInfo(final String address, final int requestId) {
		if (address != null) {
			clientFactory.getCommonServices().getBlockchainAddressInfo(address, new AsyncCallback<BlockchainAddressInfoDTO>() {
				public void onSuccess(BlockchainAddressInfoDTO result) {
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						dataContainer.getBlockChainInfo().put(address, result);
						fireEvent(new AddressBlockchainInfoLoadedEvent(address, result));
						loadBlockchainInfo(getNextAddressBlockchainToLoad(), requestId);
					}
				}

				public void onFailure(Throwable caught) {
					fireEvent(new LoadFailureEvent("Failed to load blockchain info for address " + address, caught));
					// Continue only if the request ID is the same as the
					// current one. If not the same, we have received a response
					// to an old request, so just break the chain.
					if (requestId == currentAddressLoadRequestId) {
						loadBlockchainInfo(getNextAddressBlockchainToLoad(), requestId);
					}
				}
			});
		}
	}

	/**
	 * Return the next address to load for blockchain info. Return null if no
	 * more address has to be loaded.
	 * 
	 * @return
	 */
	private String getNextAddressBlockchainToLoad() {
		String result = null;
		if (dataContainer.getAddresses() != null && dataContainer.getAddresses().size() > 0) {
			if (nextAddressBlockchainLoadingIndex < dataContainer.getAddresses().size()) {
				result = dataContainer.getAddresses().get(nextAddressBlockchainLoadingIndex);
				nextAddressBlockchainLoadingIndex++;

				if (result != null && result.isEmpty()) {
					result = null;
				}
			}

			if (result == null) {
				onAddressesBlockchainLoadEnd();
			}
		}

		return result;
	}

	/**
	 * Called when all blockchain info are loaded
	 */
	private void onAddressesBlockchainLoadEnd() {
		isAddressBlockchainLoadInProgess = false;
		fireEvent(new EveryAddressBlockchainInfoLoadedEvent(dataContainer.getBlockChainInfo()));

		computeTotalBlockchainInfo();
	}

	/**
	 * Compute the total blockchain info from all loaded blockchain info.
	 */
	private void computeTotalBlockchainInfo() {
		// Compute the total blockchain info only if more than one address.
		if (dataContainer.getAddressesStats().size() > 0 && nbAddressStatsLoaded > 1) {
			BlockchainAddressInfoDTO totalInfo = new BlockchainAddressInfoDTO();
			totalInfo.setAddress("Total");

			float totalBalance = 0F;
			if (dataContainer.getBlockChainInfo() != null) {
				for (BlockchainAddressInfoDTO info : dataContainer.getBlockChainInfo().values()) {
					totalBalance += info.getCurrentBalance() != null ? info.getCurrentBalance() : 0;
				}
			}

			totalInfo.setCurrentBalance(totalBalance);
			dataContainer.setTotalBlockchainInfo(totalInfo);
			fireEvent(new TotalAddressBlockchainInfoLoadedEvent(totalInfo));
		}
	}

	/**
	 * Fire the given event
	 * 
	 * @param event
	 */
	protected void fireEvent(GwtEvent<?> event) {
		clientFactory.getEventBus().fireEventFromSource(event, this);
	}

	/**
	 * Start autorefresh for all stats
	 */
	private void startAutoRefreshStats() {
		// If there is already a timer, stop it and recall this function.
		if (autoRefreshStatsTimer != null) {
			stopAutoRefreshStats();
			startAutoRefreshStats();
		} else {
			autoRefreshStatsTimer = new Timer() {
				public void run() {
					loadLastGlobalStats();
					loadLastAddressStats();
				}
			};
			autoRefreshStatsTimer.scheduleRepeating(AUTO_REFRESH_DELAY_STATS);
		}
	}

	public void setAutoRefresh(boolean isAutoRefresh) {
		this.isAutoRefresh = isAutoRefresh;
		if (isAutoRefresh) {
			startAutoRefreshStats();
		} else {
			stopAutoRefreshStats();
		}
	}

	/**
	 * Stop autorefresh for all stats
	 */
	private void stopAutoRefreshStats() {
		if (autoRefreshStatsTimer != null) {
			autoRefreshStatsTimer.cancel();
			autoRefreshStatsTimer = null;
		}
	}

	/**
	 * Compute all the aggregated stats.
	 */
	private void computeGlobalAggregatedStats() {
		List<GlobalStatsDTO> globalStats = dataContainer.getGlobalStats();

		GlobalStatsDTO lastPaidoutStats = null;
		GlobalStatsDTO previousGlobalStats = globalStats.size() > 0 ? globalStats.get(0) : null;
		float totalMHSinceLastPayout = 0;
		int nbStats = 0;
		int index = 0;
		boolean firstPayoutDetected = false;
		boolean isFirstPayout = true;
		for (GlobalStatsDTO stats : globalStats) {

			// Check for the first payout to start to compute the earning by MH.
			if (!firstPayoutDetected) {
				firstPayoutDetected = previousGlobalStats.getTotalBalance() > stats.getTotalBalance();
				lastPaidoutStats = stats;
			}

			// Begin to compute the earning by MH only when the first payout is
			// detected.
			if (firstPayoutDetected && !isFirstPayout) {
				// If a new payout is detected or we are on the last stats,
				// compute values and reset all counters.
				if (previousGlobalStats.getTotalBalance() > stats.getTotalBalance() || index + 1 >= globalStats.size()) {
					float averageMH = totalMHSinceLastPayout / nbStats;
					float totalBalance = previousGlobalStats.getTotalBalance() - lastPaidoutStats.getTotalBalance();
					float totalBTC = previousGlobalStats.getTotalBalance() + previousGlobalStats.getTotalImmatureBalance()
							+ previousGlobalStats.getTotalUnexchangedBalance() - stats.getTotalBalance() - stats.getTotalImmatureBalance()
							- stats.getTotalUnexchangedBalance();

					// Time elapsed between the last payout and the current
					// stats in seconds.
					long timeBetweenPaidout = (stats.getRefreshTime().getTime() - lastPaidoutStats.getRefreshTime().getTime()) / 1000;
					float balanceEarningIn24H = totalBalance / timeBetweenPaidout * SECONDS_IN_24H;
					float totalEarningIn24H = totalBTC / timeBetweenPaidout * SECONDS_IN_24H;

					Pair<Date, Float> totalBalanceEarningByMH = new Pair<Date, Float>(stats.getRefreshTime(), averageMH > 0 ? balanceEarningIn24H
							/ averageMH : 0);
					dataContainer.getGlobalAggregatedStats().getEarningByMHBalance().add(totalBalanceEarningByMH);

					Pair<Date, Float> totalEarningByMH = new Pair<Date, Float>(stats.getRefreshTime(), averageMH > 0 ? totalEarningIn24H / averageMH
							: 0);
					dataContainer.getGlobalAggregatedStats().getEarningByMHTotal().add(totalEarningByMH);

					lastPaidoutStats = stats;
					totalMHSinceLastPayout = 0;
					nbStats = 0;
				}

				totalMHSinceLastPayout += stats.getTotalMegahashesPerSecond() + stats.getTotalRejectedMegahashesPerSecond();
				nbStats++;

			}

			// Once the first payout has been detected, this is no more the
			// first payout.
			isFirstPayout = !firstPayoutDetected;

			previousGlobalStats = stats;
			index++;
		}

	}

	public HandlerRegistration addLoadFailureHandler(LoadFailureHandler handler) {
		return clientFactory.getEventBus().addHandlerToSource(LoadFailureEvent.TYPE, this, handler);
	}

	public HandlerRegistration addAddressBlockchainInfoHandler(AddressBlockchainInfoLoadHandler handler) {
		return clientFactory.getEventBus().addHandlerToSource(AddressBlockchainInfoLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addEveryAddressBlockchainInfoHandler(EveryAddressBlockchainInfoLoadHandler handler) {
		return clientFactory.getEventBus().addHandlerToSource(EveryAddressBlockchainInfoLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addTotalAddressBlockchainInfoLoadHandler(TotalAddressBlockchainInfoLoadHandler handler) {
		return clientFactory.getEventBus().addHandlerToSource(TotalAddressBlockchainInfoLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addAllAddressStatsLoadHandler(AllAddressStatsLoadHandler<AddressStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(AllAddressStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addLastAddressStatsLoadHandler(LastAddressStatsLoadHandler<AddressStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(LastAddressStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addAllGlobalStatsLoadHandler(AllGlobalStatsLoadHandler<GlobalStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(AllGlobalStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addLastGlobalStatsLoadHandler(LastGlobalStatsLoadHandler<GlobalStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(LastGlobalStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addAllTotalStatsLoadHandler(AllTotalStatsLoadHandler<AddressStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(AllTotalStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addLastTotalStatsLoadHandler(LastTotalStatsLoadHandler<AddressStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(LastTotalStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addEveryAddressStatsLoadHandler(EveryAddressStatsLoadHandler<AddressStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(EveryAddressStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addEveryLastAddressStatsLoadHandler(EveryLastAddressStatsLoadHandler<AddressStatsDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(EveryLastAddressStatsLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addAddressPaidoutLoadHandler(AddressPaidoutLoadHandler<AddressPaidoutDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(AddressPaidoutLoadedEvent.TYPE, this, handler);
	}

	public HandlerRegistration addEveryAddressPaidoutLoadHandler(EveryAddressPaidoutLoadHandler<AddressPaidoutDTO> handler) {
		return clientFactory.getEventBus().addHandlerToSource(EveryAddressPaidoutLoadedEvent.TYPE, this, handler);
	}

	public MiddlecoinDataContainer getDataContainer() {
		return dataContainer;
	}

}
