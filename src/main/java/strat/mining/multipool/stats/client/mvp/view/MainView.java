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

import strat.mining.multipool.stats.client.mvp.ViewPresenter;
import strat.mining.multipool.stats.client.mvp.view.PoolView.PoolViewPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface MainView extends IsWidget {

	public interface MainViewPresenter extends ViewPresenter<MainView> {

		public void addPoolPresenter(PoolViewPresenter<? extends PoolView, ?> presenter);

		public void openPoolSelectionView();

		public void activatePoolPresenter(String poolName);

		public void deactivatePoolPresenter(String poolName);

		public void closePoolPresenter(String poolName);

	}

	public void addPoolTab(String poolName, IsWidget poolView);

	public void setDonationWidget(Widget donationWidget);

	/**
	 * Return true if the pool tab has been found
	 * 
	 * @param poolName
	 * @return
	 */
	public boolean showPoolTab(String poolName);

	public int getOpendPoolCount();

}
