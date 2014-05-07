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
package strat.mining.multipool.stats.client.mvp.activity;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.event.LoadFailureEvent;
import strat.mining.multipool.stats.client.mvp.handler.LoadFailureHandler;
import strat.mining.multipool.stats.client.mvp.presenter.coinshift.CoinshiftViewPresenter;
import strat.mining.multipool.stats.client.mvp.presenter.coinsolver.CoinsolverViewPresenter;
import strat.mining.multipool.stats.client.mvp.presenter.middlecoin.MiddlecoinViewPresenter;
import strat.mining.multipool.stats.client.mvp.presenter.waffle.WaffleViewPresenter;
import strat.mining.multipool.stats.client.mvp.view.MainView;
import strat.mining.multipool.stats.client.place.MainPlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MainActivity extends AbstractActivity {

	private ClientFactory clientFactory;
	private MainView.MainViewPresenter presenter;
	private MainPlace place;

	public MainActivity(MainPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		presenter = clientFactory.getMainPresenter();
		clientFactory.getMainDataManager().addLoadFailureHandler(new LoadFailureHandler() {
			public void loadFailed(LoadFailureEvent event) {
				Window.alert(event.getMessage() + "\n" + event.getThrowable().getMessage());
			}
		});
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter.getView());
		MainView.MainViewPresenter presenter = clientFactory.getMainPresenter();

		boolean atLeastOneOpened = false;

		if (place.getMiddlecoinDescriptor() != null) {
			MiddlecoinViewPresenter middlecoinPresenter = new MiddlecoinViewPresenter(clientFactory);
			middlecoinPresenter.setDescriptor(place.getMiddlecoinDescriptor());
			presenter.addPoolPresenter(middlecoinPresenter);
			atLeastOneOpened = true;
		}

		if (place.getWaffleDescriptor() != null) {
			WaffleViewPresenter wafflePresenter = new WaffleViewPresenter(clientFactory);
			wafflePresenter.setDescriptor(place.getWaffleDescriptor());
			presenter.addPoolPresenter(wafflePresenter);
			atLeastOneOpened = true;
		}

		if (place.getCoinshiftDescriptor() != null) {
			CoinshiftViewPresenter coinshiftPresenter = new CoinshiftViewPresenter(clientFactory);
			coinshiftPresenter.setDescriptor(place.getCoinshiftDescriptor());
			presenter.addPoolPresenter(coinshiftPresenter);
			atLeastOneOpened = true;
		}

		if (place.getCoinsolverDescriptor() != null) {
			CoinsolverViewPresenter coinsolverPresenter = new CoinsolverViewPresenter(clientFactory);
			coinsolverPresenter.setDescriptor(place.getCoinsolverDescriptor());
			presenter.addPoolPresenter(coinsolverPresenter);
			atLeastOneOpened = true;
		}

		if (!atLeastOneOpened) {
			presenter.openPoolSelectionView();
		}
	}
}
