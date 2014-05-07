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
package strat.mining.multipool.stats.client.mvp.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.presenter.coinshift.CoinshiftViewPresenter;
import strat.mining.multipool.stats.client.mvp.presenter.coinsolver.CoinsolverViewPresenter;
import strat.mining.multipool.stats.client.mvp.presenter.middlecoin.MiddlecoinViewPresenter;
import strat.mining.multipool.stats.client.mvp.presenter.waffle.WaffleViewPresenter;
import strat.mining.multipool.stats.client.mvp.view.MainView;
import strat.mining.multipool.stats.client.mvp.view.PoolSelectionView;
import strat.mining.multipool.stats.client.mvp.view.PoolView;
import strat.mining.multipool.stats.client.mvp.view.PoolView.PoolViewPresenter;
import strat.mining.multipool.stats.client.mvp.view.impl.MainViewImpl;
import strat.mining.multipool.stats.client.place.MainPlace;
import strat.mining.multipool.stats.client.util.HistoryUtils;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class MainViewPresenter implements MainView.MainViewPresenter {

	private static final List<String> poolNames = new ArrayList<String>();
	private static final List<String> poolDescriptions = new ArrayList<String>();

	private ClientFactory clientFactory;

	private MainView view;

	private Map<String, PoolViewPresenter<? extends PoolView, ?>> poolPresenters;

	public MainViewPresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

		poolPresenters = new HashMap<String, PoolViewPresenter<? extends PoolView, ?>>();

		poolNames.add(MiddlecoinViewPresenter.POOL_NAME);
		poolDescriptions.add(MiddlecoinViewPresenter.POOL_DESCRIPTION);
		poolNames.add(WaffleViewPresenter.POOL_NAME);
		poolDescriptions.add(WaffleViewPresenter.POOL_DESCRIPTION);
		poolNames.add(CoinshiftViewPresenter.POOL_NAME);
		poolDescriptions.add(CoinshiftViewPresenter.POOL_DESCRIPTION);
		poolNames.add(CoinsolverViewPresenter.POOL_NAME);
		poolDescriptions.add(CoinsolverViewPresenter.POOL_DESCRIPTION);

		view = new MainViewImpl(this);

		DonationViewPresenter donationPresenter = new DonationViewPresenter(clientFactory);
		view.setDonationWidget(donationPresenter.getView().asWidget());

	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public MainView getView() {
		return view;
	}

	@Override
	public void addPoolPresenter(PoolViewPresenter<? extends PoolView, ?> presenter) {
		poolPresenters.put(presenter.getPoolName(), presenter);
		view.addPoolTab(presenter.getPoolName(), presenter.getView());
	}

	private void addPoolTab(String poolName) {
		if (MiddlecoinViewPresenter.POOL_NAME.equals(poolName)) {
			addPoolPresenter(new MiddlecoinViewPresenter(clientFactory));
		} else if (WaffleViewPresenter.POOL_NAME.equals(poolName)) {
			addPoolPresenter(new WaffleViewPresenter(clientFactory));
		} else if (CoinshiftViewPresenter.POOL_NAME.equals(poolName)) {
			addPoolPresenter(new CoinshiftViewPresenter(clientFactory));
		} else if (CoinsolverViewPresenter.POOL_NAME.equals(poolName)) {
			addPoolPresenter(new CoinsolverViewPresenter(clientFactory));
		}
	}

	@Override
	public void openPoolSelectionView() {
		final PoolSelectionView.PoolSelectionViewPresenter selectionPresenter = new PoolSelectionViewPresenter(clientFactory, poolNames,
				poolDescriptions);

		final Dialog selectionDialog = new Dialog();
		selectionDialog.setHeadingText("Pool selection");
		selectionDialog.setWidget(selectionPresenter.getView());
		selectionDialog.setModal(true);
		selectionDialog.setHideOnButtonClick(false);
		selectionDialog.setResizable(false);
		selectionDialog.setHeight(250);
		selectionDialog.setWidth(300);
		selectionDialog.getButtonBar().addStyleName("whiteBackground");
		selectionDialog.getBody().addClassName("whiteBackground");

		if (view.getOpendPoolCount() > 0) {
			selectionDialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
			selectionDialog.getButtonById(PredefinedButton.CANCEL.name()).addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {
					selectionDialog.hide();
				}
			});
		} else {
			selectionDialog.setClosable(false);
			selectionDialog.setPredefinedButtons(PredefinedButton.OK);
		}

		selectionDialog.getButtonById(PredefinedButton.OK.name()).addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				String poolName = selectionPresenter.getSelectedPoolName();

				// Do not close the dialog if the selection is not good
				if (poolName != null && !poolName.isEmpty()) {
					// Do not add a tab if the tab for the given pool name is
					// already open. Just show it.
					if (poolName != null && !view.showPoolTab(poolName)) {
						addPoolTab(poolName);
					}
					selectionDialog.hide();
				}
			}
		});

		selectionDialog.show();
		selectionDialog.center();
	}

	@Override
	public void activatePoolPresenter(String poolName) {
		PoolViewPresenter<?, ?> presenter = poolPresenters.get(poolName);
		if (presenter != null) {
			presenter.activate();
		}
	}

	@Override
	public void deactivatePoolPresenter(String poolName) {
		PoolViewPresenter<?, ?> presenter = poolPresenters.get(poolName);
		if (presenter != null) {
			presenter.deactivate();
		}
	}

	@Override
	public void closePoolPresenter(String poolName) {
		poolPresenters.remove(poolName);

		MainPlace currentPlace = (MainPlace) clientFactory.getPlaceController().getWhere();
		if (MiddlecoinViewPresenter.POOL_NAME.equals(poolName)) {
			currentPlace.setMiddlecoinDescriptor(null);
			clientFactory.getMainDataManager().getMiddlecoinDataManager().setAddresses(null);
		} else if (WaffleViewPresenter.POOL_NAME.equals(poolName)) {
			currentPlace.setWaffleDescriptor(null);
			clientFactory.getMainDataManager().getWaffleDataManager().setAddresses(null);
		} else if (CoinshiftViewPresenter.POOL_NAME.equals(poolName)) {
			currentPlace.setCoinshiftDescriptor(null);
			clientFactory.getMainDataManager().getCoinshiftDataManager().setAddresses(null);
		} else if (CoinsolverViewPresenter.POOL_NAME.equals(poolName)) {
			currentPlace.setCoinsolverDescriptor(null);
			clientFactory.getMainDataManager().getCoinsolverDataManager().setAddresses(null);
		}
		HistoryUtils.updateHistoryWithPlace(currentPlace);
	}

}
