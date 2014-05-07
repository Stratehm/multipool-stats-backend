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
package strat.mining.multipool.stats.client.mvp.view;

import java.util.List;

import strat.mining.multipool.stats.client.mvp.PlaceAwarePresenter;
import strat.mining.multipool.stats.client.mvp.ViewPresenter;
import strat.mining.multipool.stats.client.mvp.presenter.PoolPresenter;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * The interface for a PoolView
 * 
 * @author Strat
 * 
 */
public interface PoolView extends IsWidget {

	public interface PoolViewPresenter<T extends PoolView, V> extends ViewPresenter<T>, PlaceAwarePresenter, PoolPresenter<V> {

		public SuggestOracle getAddressesSuggestOracle();

		public void onValidate(List<String> addresses);

		public void setAutoRefresh(boolean value);

		public void onGlobalCollapse(boolean globalStatsCollapsed);

		public void loadCurrencyDetails(ExchangePlaceDTO place, String currencyCode);

		public void activate();

		public void deactivate();

	}

	public boolean isGlobalStatsCollapsed();

	public void setAutoRefresh(boolean autoRefresh);

	public boolean isAutoRefresh();

	public void setInputAddresses(List<String> addresses);

	public void setGlobalStatsCollapsed(boolean isCollapsed);

	public void setExchangePlaces(List<ExchangePlaceDTO> places);

	public void forceResize();

}
