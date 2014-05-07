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
package strat.mining.multipool.stats.client.factory;

import strat.mining.multipool.stats.client.mvp.model.MainDataManager;
import strat.mining.multipool.stats.client.mvp.presenter.MainViewPresenter;
import strat.mining.multipool.stats.client.mvp.view.MainView;
import strat.mining.multipool.stats.client.services.rest.CoinshiftServices;
import strat.mining.multipool.stats.client.services.rest.CoinshiftServicesImpl;
import strat.mining.multipool.stats.client.services.rest.CoinsolverServices;
import strat.mining.multipool.stats.client.services.rest.CoinsolverServicesImpl;
import strat.mining.multipool.stats.client.services.rest.CommonServices;
import strat.mining.multipool.stats.client.services.rest.CommonServicesImpl;
import strat.mining.multipool.stats.client.services.rest.MiddlecoinServices;
import strat.mining.multipool.stats.client.services.rest.MiddlecoinServicesImpl;
import strat.mining.multipool.stats.client.services.rest.WaffleServices;
import strat.mining.multipool.stats.client.services.rest.WaffleServicesImpl;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public class ClientFactoryImpl implements ClientFactory {

	private EventBus eventBus;
	private PlaceController placeController;
	private DtoFactory dtoFactory;
	private CommonServices commonServices;
	private MiddlecoinServices middlecoinServices;
	private WaffleServices waffleServices;
	private CoinshiftServices coinshiftServices;
	private CoinsolverServices coinsolverServices;
	private MainView.MainViewPresenter mainPresenter;
	private MainDataManager dataManager;

	@Override
	public EventBus getEventBus() {
		if (eventBus == null) {
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		if (placeController == null) {
			placeController = new PlaceController(getEventBus());
		}
		return placeController;
	}

	@Override
	public DtoFactory getDtoFactory() {
		if (dtoFactory == null) {
			dtoFactory = new DtoFactoryImpl();
		}
		return dtoFactory;
	}

	@Override
	public MiddlecoinServices getMiddlecoinServices() {
		if (middlecoinServices == null) {
			middlecoinServices = new MiddlecoinServicesImpl(this);
		}
		return middlecoinServices;
	}

	@Override
	public MainView.MainViewPresenter getMainPresenter() {
		if (mainPresenter == null) {
			mainPresenter = new MainViewPresenter(this);
		}
		return mainPresenter;
	}

	@Override
	public MainDataManager getMainDataManager() {
		if (dataManager == null) {
			dataManager = new MainDataManager(this);
		}
		return dataManager;
	}

	@Override
	public CommonServices getCommonServices() {
		if (commonServices == null) {
			commonServices = new CommonServicesImpl(this);
		}
		return commonServices;
	}

	@Override
	public WaffleServices getWaffleServices() {
		if (waffleServices == null) {
			waffleServices = new WaffleServicesImpl(this);
		}
		return waffleServices;
	}

	@Override
	public CoinshiftServices getCoinshiftServices() {
		if (coinshiftServices == null) {
			coinshiftServices = new CoinshiftServicesImpl(this);
		}
		return coinshiftServices;
	}

	@Override
	public CoinsolverServices getCoinsolverServices() {
		if (coinsolverServices == null) {
			coinsolverServices = new CoinsolverServicesImpl(this);
		}
		return coinsolverServices;
	}
}
