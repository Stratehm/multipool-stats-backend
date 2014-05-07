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
package strat.mining.multipool.stats.client.mvp.model;

import java.util.List;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.event.AddressDonationDetailsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.CurrencyTickerLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.DonationDetailsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryExchangePlaceLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LoadFailureEvent;
import strat.mining.multipool.stats.client.mvp.handler.AddressDonationDetailsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.CurrencyTickerLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.DonationDetailsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryExchangePlaceLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LoadFailureHandler;
import strat.mining.multipool.stats.client.mvp.model.coinshift.CoinshiftDataManager;
import strat.mining.multipool.stats.client.mvp.model.coinsolver.CoinsolverDataManager;
import strat.mining.multipool.stats.client.mvp.model.middlecoin.MiddlecoinDataManager;
import strat.mining.multipool.stats.client.mvp.model.waffle.WaffleDataManager;
import strat.mining.multipool.stats.client.util.Pair;
import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MainDataManager {

	private ClientFactory clientFactory;

	private MainDataContainer dataContainer;

	private MiddlecoinDataManager middlecoinDataManager;

	private WaffleDataManager waffleDataManager;

	private CoinshiftDataManager coinshiftDataManager;

	private CoinsolverDataManager coinsolverDataManager;

	private boolean isExchangePlaceLoadInProgress;

	public MainDataManager(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		dataContainer = new MainDataContainer();
		middlecoinDataManager = new MiddlecoinDataManager(clientFactory);
		waffleDataManager = new WaffleDataManager(clientFactory);
		coinshiftDataManager = new CoinshiftDataManager(clientFactory);
		coinsolverDataManager = new CoinsolverDataManager(clientFactory);
		isExchangePlaceLoadInProgress = false;
	}

	/**
	 * Load all the supported exchange places.
	 */
	public void loadExchangePlaces() {
		if (!isExchangePlaceLoadInProgress) {
			isExchangePlaceLoadInProgress = true;
			clientFactory.getCommonServices().getExchangePlaces(new AsyncCallback<List<ExchangePlaceDTO>>() {
				public void onSuccess(List<ExchangePlaceDTO> result) {
					dataContainer.getExchangePlaces().addAll(result);
					isExchangePlaceLoadInProgress = false;
					fireEvent(new EveryExchangePlaceLoadedEvent(result));
				}

				public void onFailure(Throwable caught) {
					isExchangePlaceLoadInProgress = false;
					fireEvent(new LoadFailureEvent("Failed to load exchange places.", caught));

				}
			});
		}
	}

	public void loadAddressDonationDetails(final String address) {
		clientFactory.getCommonServices().getDonationDetailsByAddress(address, new AsyncCallback<AddressDonationDetailsDTO>() {
			public void onFailure(Throwable caught) {
				// Do nothing on failure
			}

			public void onSuccess(AddressDonationDetailsDTO result) {
				fireEvent(new AddressDonationDetailsLoadedEvent(address, result));
			}
		});
	}

	/**
	 * Load the currency ticker for the given exchange place and currency code.
	 * 
	 * @param exchangePlace
	 * @param currencyCode
	 */
	public void loadCurrencyTicker(final ExchangePlaceDTO exchangePlace, final String currencyCode) {
		clientFactory.getCommonServices().getCurrencyTicker(exchangePlace.getName(), currencyCode, new AsyncCallback<CurrencyTickerDTO>() {
			public void onSuccess(CurrencyTickerDTO result) {
				dataContainer.getTickers().put(new Pair<String, String>(exchangePlace.getName(), currencyCode), result);
				fireEvent(new CurrencyTickerLoadedEvent(result));
			}

			public void onFailure(Throwable caught) {
				fireEvent(new LoadFailureEvent("Failed to load currency for " + exchangePlace.getLabel() + " and currency " + currencyCode, caught));
			}
		});
	}

	/**
	 * Loads the donation details.
	 */
	public void loadDonationDetails() {
		clientFactory.getCommonServices().getDonationDetails(new AsyncCallback<DonationDetailsDTO>() {
			public void onFailure(Throwable caught) {
				dataContainer.setDonationDetails(null);
				fireEvent(new DonationDetailsLoadedEvent(null));
			}

			@Override
			public void onSuccess(DonationDetailsDTO result) {
				dataContainer.setDonationDetails(result);
				fireEvent(new DonationDetailsLoadedEvent(result));
			}
		});
	}

	/**
	 * Fire the given event
	 * 
	 * @param event
	 */
	protected void fireEvent(GwtEvent<?> event) {
		clientFactory.getEventBus().fireEvent(event);
	}

	public MainDataContainer getMainDataContainer() {
		return dataContainer;
	}

	public MiddlecoinDataManager getMiddlecoinDataManager() {
		return middlecoinDataManager;
	}

	public WaffleDataManager getWaffleDataManager() {
		return waffleDataManager;
	}

	public CoinshiftDataManager getCoinshiftDataManager() {
		return coinshiftDataManager;
	}

	public CoinsolverDataManager getCoinsolverDataManager() {
		return coinsolverDataManager;
	}

	public HandlerRegistration addLoadFailureHandler(LoadFailureHandler handler) {
		return clientFactory.getEventBus().addHandler(LoadFailureEvent.TYPE, handler);
	}

	public HandlerRegistration addEveryExchangePlaceLoadHandler(EveryExchangePlaceLoadHandler handler) {
		return clientFactory.getEventBus().addHandler(EveryExchangePlaceLoadedEvent.TYPE, handler);
	}

	public HandlerRegistration addCurrencyTickerLoadHandler(CurrencyTickerLoadHandler handler) {
		return clientFactory.getEventBus().addHandler(CurrencyTickerLoadedEvent.TYPE, handler);
	}

	public HandlerRegistration addDonationDetailsLoadHandler(DonationDetailsLoadHandler handler) {
		return clientFactory.getEventBus().addHandler(DonationDetailsLoadedEvent.TYPE, handler);
	}

	public HandlerRegistration addAddressDonationDetailsLoadHandler(AddressDonationDetailsLoadHandler handler) {
		return clientFactory.getEventBus().addHandler(AddressDonationDetailsLoadedEvent.TYPE, handler);
	}

}
