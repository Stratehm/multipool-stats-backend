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
package strat.mining.multipool.stats.client.mvp.presenter.waffle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import strat.mining.multipool.stats.client.component.Notification;
import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.event.AllGlobalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.AllTotalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryExchangePlaceLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastGlobalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastTotalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.handler.AllGlobalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.AllTotalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryExchangePlaceLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastGlobalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastTotalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.model.MainDataManager;
import strat.mining.multipool.stats.client.mvp.presenter.AbstractPlaceAwarePresenter;
import strat.mining.multipool.stats.client.mvp.view.waffle.WaffleView;
import strat.mining.multipool.stats.client.mvp.view.waffle.impl.WaffleViewImpl;
import strat.mining.multipool.stats.client.place.MainPlace;
import strat.mining.multipool.stats.client.place.WafflePoolPlaceDescriptor;
import strat.mining.multipool.stats.client.util.HistoryUtils;
import strat.mining.multipool.stats.client.util.Pair;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.dto.waffle.AddressStatsDTO;
import strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class WaffleViewPresenter extends AbstractPlaceAwarePresenter implements WaffleView.WaffleViewPresenter {

	public static final String POOL_NAME = "Wafflepool";
	public static final String POOL_DESCRIPTION = "Paste your address in the address area. Stats starts to record at the first time an address is requested. Then the update is done every 10 minutes (even if the page is closed). The suggest box will only suggest addresses that have some stats to display.";

	private WaffleView view;

	private MainDataManager dataManager;

	private Map<List<String>, PaidoutViewPresenter> openedPaidout;

	private Map<Pair<String, String>, CurrencyViewPresenter> openedCurrencies;

	public WaffleViewPresenter(final ClientFactory clientFactory) {
		super(clientFactory);
		this.dataManager = clientFactory.getMainDataManager();
		openedCurrencies = new HashMap<Pair<String, String>, CurrencyViewPresenter>();
		view = new WaffleViewImpl(this);
		openedPaidout = new HashMap<List<String>, PaidoutViewPresenter>();

		dataManager.getWaffleDataManager().addEveryAddressStatsLoadHandler(new EveryAddressStatsLoadHandler<AddressStatsDTO>() {
			public void everyAddressStatsLoaded(EveryAddressStatsLoadedEvent<AddressStatsDTO> event) {
				for (Entry<String, List<AddressStatsDTO>> entry : event.getStats().entrySet()) {
					view.addAddressStats(entry.getKey(), entry.getValue());
					clientFactory.getMainDataManager().loadAddressDonationDetails(entry.getKey());
				}
				view.displayAddressesLoader(false);
			}
		});

		dataManager.getWaffleDataManager().addAllGlobalStatsLoadHandler(new AllGlobalStatsLoadHandler<GlobalStatsDTO>() {
			public void allGlobalStatsLoaded(AllGlobalStatsLoadedEvent<GlobalStatsDTO> event) {
				view.displayGlobalLoader(false);
				view.setGlobalStats(event.getStats());
				if (event.getStats() != null && event.getStats().size() > 0) {
					String notification = event.getStats().get(event.getStats().size() - 1).getNote();
					if (notification != null && notification.length() > 0) {
						Notification.showNotification("Wafflepool", notification);
					}
				}
			}
		});

		dataManager.getWaffleDataManager().addAllTotalStatsLoadHandler(new AllTotalStatsLoadHandler<AddressStatsDTO>() {
			public void allTotalStatsLoaded(AllTotalStatsLoadedEvent<AddressStatsDTO> event) {
				view.setTotalStats(event.getStats());
			}
		});

		dataManager.getWaffleDataManager().addLastGlobalStatsLoadHandler(new LastGlobalStatsLoadHandler<GlobalStatsDTO>() {
			public void lastGlobalStatsLoaded(LastGlobalStatsLoadedEvent<GlobalStatsDTO> event) {
				view.appendGlobalStats(event.getStats());
				if (event.getStats() != null) {
					String notification = event.getStats().getNote();
					if (notification != null && notification.length() > 0) {
						Notification.showNotification("Wafflepool", notification);
					}
				}
			}
		});

		dataManager.getWaffleDataManager().addLastAddressStatsLoadHandler(new LastAddressStatsLoadHandler<AddressStatsDTO>() {
			public void lastAddressStatsLoaded(LastAddressStatsLoadedEvent<AddressStatsDTO> event) {
				view.appendAddressStats(event.getStats());
				clientFactory.getMainDataManager().loadAddressDonationDetails(event.getAddress());
			}
		});

		dataManager.getWaffleDataManager().addLastTotalStatsLoadHandler(new LastTotalStatsLoadHandler<AddressStatsDTO>() {
			public void lastTotalStatsLoaded(LastTotalStatsLoadedEvent<AddressStatsDTO> event) {
				view.appendTotalStats(event.getStats());
			}
		});

		loadGlobalStats();

		loadExchangePlaces();

		updateToken();

		dataManager.getWaffleDataManager().setAutoRefresh(view.isAutoRefresh());
	}

	@Override
	public WaffleView getView() {
		return view;
	}

	@Override
	public void loadAddressesStats(List<String> addresses) {
		dataManager.getWaffleDataManager().setAddresses(addresses);
		clearCurrencyStats();
		view.setInputAddresses(addresses);
		view.clearAddressStats();
		view.clearTotalStats();

		if (addresses != null && addresses.size() > 0) {
			view.displayAddressesLoader(true);
		}

		dataManager.getWaffleDataManager().loadAllAddressStats();
	}

	@Override
	public void setGlobalStatsCollapsed(boolean isCollapsed) {
		setGlobalStatsCollapsed(isCollapsed, true);
	}

	private void setGlobalStatsCollapsed(boolean isCollapsed, boolean updateToken) {
		view.setGlobalStatsCollapsed(isCollapsed);
		if (updateToken) {
			updateToken();
		}
	}

	@Override
	public void loadGlobalStats() {
		view.displayGlobalLoader(true);
		dataManager.getWaffleDataManager().loadAllGlobalStats();
	}

	@Override
	public void onValidate(List<String> addresses) {
		loadAddressesStats(addresses);
		updateToken();
	}

	@Override
	public void setDisplaySummary(boolean isDisplayed) {
		setDisplaySummary(isDisplayed, true);
	}

	private void setDisplaySummary(boolean isDisplayed, boolean updateToken) {
		view.displaySummary(isDisplayed);
		if (updateToken) {
			updateToken();
		}
	}

	@Override
	public void setDisplayBTCChart(boolean isDisplayed) {
		setDisplayBTCChart(isDisplayed, true);
	}

	private void setDisplayBTCChart(boolean isDisplayed, boolean updateToken) {
		view.displayBTCChart(isDisplayed);
		if (updateToken) {
			updateToken();
		}
	}

	@Override
	public void setDisplayPowerChart(boolean isDisplayed) {
		setDisplayPowerChart(isDisplayed, true);
	}

	private void setDisplayPowerChart(boolean isDisplayed, boolean updateToken) {
		view.displayPowerChart(isDisplayed);
		if (updateToken) {
			updateToken();
		}
	}

	protected void updateToken() {
		MainPlace currentPlace = (MainPlace) clientFactory.getPlaceController().getWhere();
		WafflePoolPlaceDescriptor placeDescriptor = currentPlace.getWaffleDescriptor();
		if (placeDescriptor == null) {
			placeDescriptor = new WafflePoolPlaceDescriptor();
			currentPlace.setWaffleDescriptor(placeDescriptor);
		}

		if (dataManager.getWaffleDataManager().getAddresses() != null) {
			placeDescriptor.setAddresses(dataManager.getWaffleDataManager().getAddresses());
		}
		placeDescriptor.setGlobalCollapsed(view.isGlobalStatsCollapsed());
		placeDescriptor.setDisplaySummary(view.isSummaryDisplayed());
		placeDescriptor.setDisplayBTC(view.isBTCChartDisplayed());
		placeDescriptor.setDisplayPower(view.isPowerChartDisplayed());
		HistoryUtils.updateHistoryWithPlace(currentPlace);
	}

	@Override
	public void setAutoRefresh(boolean isAutoRefresh) {
		view.setAutoRefresh(isAutoRefresh);
		clientFactory.getMainDataManager().getWaffleDataManager().setAutoRefresh(isAutoRefresh);
	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public String getPoolName() {
		return POOL_NAME;
	}

	@Override
	public String getDescription() {
		return POOL_DESCRIPTION;
	}

	@Override
	public void setDescriptor(WafflePoolPlaceDescriptor poolDescriptor) {
		setGlobalStatsCollapsed(poolDescriptor.isGlobalCollapsed(), false);
		setDisplaySummary(poolDescriptor.isDisplaySummary(), false);
		setDisplayBTCChart(poolDescriptor.isDisplayBTC(), false);
		setDisplayPowerChart(poolDescriptor.isDisplayPower(), false);
		loadAddressesStats(poolDescriptor.getAddresses());
	}

	@Override
	public SuggestOracle getAddressesSuggestOracle() {
		return new AddressSuggestOracle(clientFactory);
	}

	@Override
	public void loadCurrencyDetails(ExchangePlaceDTO exchangePlace, String currencyCode) {
		final Pair<String, String> key = new Pair<String, String>(exchangePlace.getName(), currencyCode);
		CurrencyViewPresenter currencyPresenter = openedCurrencies.get(key);

		// If no window opened for the given place and currency, then open it.
		if (currencyPresenter == null) {
			currencyPresenter = new CurrencyViewPresenter(clientFactory, exchangePlace, currencyCode);
			openedCurrencies.put(key, currencyPresenter);
			currencyPresenter.addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					openedCurrencies.remove(key);
				}
			});

		} else {
			// Else bring it to front.
			currencyPresenter.bringToFront();
		}
	}

	protected void clearCurrencyStats() {
		for (CurrencyViewPresenter presenter : openedCurrencies.values()) {
			presenter.clearStats();
		}
	}

	@Override
	public void onGlobalCollapse(boolean isCollapsed) {
		updateToken();
	}

	@Override
	public void activate() {
		for (CurrencyViewPresenter presenter : openedCurrencies.values()) {
			presenter.activate();
		}

		for (PaidoutViewPresenter presenter : openedPaidout.values()) {
			presenter.activate();
		}

		view.redrawAddressCharts();
	}

	@Override
	public void deactivate() {
		for (CurrencyViewPresenter presenter : openedCurrencies.values()) {
			presenter.hide();
		}

		for (PaidoutViewPresenter presenter : openedPaidout.values()) {
			presenter.hide();
		}
	}

	private void loadExchangePlaces() {
		List<ExchangePlaceDTO> places = dataManager.getMainDataContainer().getExchangePlaces();
		if (places != null && places.size() > 0) {
			view.setExchangePlaces(places);
		} else {
			dataManager.loadExchangePlaces();
			dataManager.addEveryExchangePlaceLoadHandler(new EveryExchangePlaceLoadHandler() {
				public void everyExchangePlaceLoaded(EveryExchangePlaceLoadedEvent event) {
					view.setExchangePlaces(event.getExchangePlaces());
				}
			});
		}
	}

	@Override
	public void loadPaidout(final List<String> addresses) {
		PaidoutViewPresenter paidoutPresenter = openedPaidout.get(addresses);

		// If no window opened for the given addresses, then open it.
		if (paidoutPresenter == null) {
			boolean isAllAddresses = (addresses.size() == dataManager.getWaffleDataManager().getAddresses().size() && dataManager
					.getWaffleDataManager().getDataContainer().getAddressesStats().size() > 1);
			paidoutPresenter = new PaidoutViewPresenter(clientFactory, addresses, isAllAddresses);
			openedPaidout.put(addresses, paidoutPresenter);
			paidoutPresenter.addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					openedPaidout.remove(addresses);
				}
			});
		} else {
			// Else bring it to front.
			paidoutPresenter.bringToFront();
		}
	}

	@Override
	public void loadTotalPaidout() {
		loadPaidout(dataManager.getWaffleDataManager().getAddresses());
	}

}
