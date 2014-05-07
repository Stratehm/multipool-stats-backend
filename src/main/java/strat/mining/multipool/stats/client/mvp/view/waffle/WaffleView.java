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
package strat.mining.multipool.stats.client.mvp.view.waffle;

import java.util.List;

import strat.mining.multipool.stats.client.mvp.PlaceAwarePresenter;
import strat.mining.multipool.stats.client.mvp.view.PoolView;
import strat.mining.multipool.stats.client.place.WafflePoolPlaceDescriptor;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.dto.waffle.AddressStatsDTO;
import strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO;

import com.google.gwt.user.client.ui.IsWidget;

public interface WaffleView extends IsWidget, PoolView {

	public interface WaffleViewPresenter extends PlaceAwarePresenter, PoolViewPresenter<WaffleView, WafflePoolPlaceDescriptor> {

		public void loadAddressesStats(List<String> addresses);

		public void loadGlobalStats();

		public void setGlobalStatsCollapsed(boolean isCollapsed);

		public void onValidate(List<String> addresses);

		public void setDisplaySummary(boolean isDisplayed);

		public void setDisplayBTCChart(boolean isDisplayed);

		public void setDisplayPowerChart(boolean isDisplayed);

		public void onGlobalCollapse(boolean isCollapsed);

		public void setAutoRefresh(boolean isAutoRefresh);

		public void loadCurrencyDetails(ExchangePlaceDTO exchangePlace, String currencyCode);

		public void loadPaidout(List<String> addresses);

		public void loadTotalPaidout();

	}

	public void setTotalStats(List<AddressStatsDTO> stats);

	public void appendTotalStats(AddressStatsDTO stats);

	public void addAddressStats(String address, List<AddressStatsDTO> stats);

	public void appendAddressStats(AddressStatsDTO addressStats);

	public void displayAddressesLoader(boolean isDisplayed);

	public void clearAddressStats();

	public void clearTotalStats();

	public void setGlobalStatsCollapsed(boolean isCollapsed);

	public void displayGlobalLoader(boolean isDisplayed);

	public void setGlobalStats(List<GlobalStatsDTO> stats);

	public void appendGlobalStats(GlobalStatsDTO stats);

	public void displaySummary(boolean display);

	public void displayBTCChart(boolean display);

	public void displayPowerChart(boolean display);

	public void setAutoRefresh(boolean autoRefresh);

	public void setInputAddresses(List<String> addresses);

	public boolean isGlobalStatsCollapsed();

	public boolean isSummaryDisplayed();

	public boolean isBTCChartDisplayed();

	public boolean isPowerChartDisplayed();

	public boolean isAutoRefresh();

	public void setExchangePlaces(List<ExchangePlaceDTO> places);

	public void redrawAddressCharts();

}
