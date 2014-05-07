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
package strat.mining.multipool.stats.client.mvp.view.coinsolver;

import java.util.Map;

import strat.mining.multipool.stats.client.mvp.ViewPresenter;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public interface CurrencyView extends IsWidget {

	public interface CurrencyViewPresenter extends ViewPresenter<CurrencyView> {

		public HandlerRegistration addHideHandler(HideHandler handler);

		public void bringToFront();

		public void clearStats();

		public void loadBlockchainInfo();

		public void hide();

		public void activate();

	}

	public HandlerRegistration addHideHandler(HideHandler handler);

	public void show();

	public void setCurrencyTicker(CurrencyTickerDTO ticker);

	public void setTotalStats(AddressStatsDTO addressStats);

	public void clearStats();

	public void setAddressStats(AddressStatsDTO addressStats);

	public void setBlockchainInfo(Map<String, BlockchainAddressInfoDTO> blockchainByAddress);

	public void setTotalBlockchainInfo(BlockchainAddressInfoDTO totalBlockchain);

	public void hide();

	public void activate();

}
