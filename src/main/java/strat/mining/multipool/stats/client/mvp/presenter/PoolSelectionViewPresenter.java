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

import java.util.List;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.view.PoolSelectionView;
import strat.mining.multipool.stats.client.mvp.view.impl.PoolSelectionViewImpl;

public class PoolSelectionViewPresenter implements PoolSelectionView.PoolSelectionViewPresenter {

	private ClientFactory clientFactory;

	private PoolSelectionView view;

	public PoolSelectionViewPresenter(ClientFactory clientFactory, List<String> poolNames, List<String> poolDescriptions) {
		this.clientFactory = clientFactory;
		view = new PoolSelectionViewImpl(poolNames, poolDescriptions);
	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public PoolSelectionView getView() {
		return view;
	}

	@Override
	public String getSelectedPoolName() {
		return view.getSelectedPoolName();
	}

}
