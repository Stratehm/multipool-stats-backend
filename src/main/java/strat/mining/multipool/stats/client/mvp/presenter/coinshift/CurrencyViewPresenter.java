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
package strat.mining.multipool.stats.client.mvp.presenter.coinshift;

import java.util.List;
import java.util.Map;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.event.AllTotalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.CurrencyTickerLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryAddressBlockchainInfoLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.EveryAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastAddressStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.LastTotalStatsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.TotalAddressBlockchainInfoLoadedEvent;
import strat.mining.multipool.stats.client.mvp.handler.AllTotalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.CurrencyTickerLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryAddressBlockchainInfoLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.EveryAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastAddressStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.LastTotalStatsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.TotalAddressBlockchainInfoLoadHandler;
import strat.mining.multipool.stats.client.mvp.view.coinshift.CurrencyView;
import strat.mining.multipool.stats.client.mvp.view.coinshift.impl.CurrencyViewImpl;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;
import strat.mining.multipool.stats.dto.coinshift.AddressStatsDTO;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class CurrencyViewPresenter implements CurrencyView.CurrencyViewPresenter {

	private static final int AUTO_REFRESH_DELAY = 60000;

	private ClientFactory clientFactory;

	private CurrencyView view;

	private ExchangePlaceDTO exchangePlace;
	private String currencyCode;

	private Timer autoRefreshTimer;

	public CurrencyViewPresenter(ClientFactory clientFactory, final ExchangePlaceDTO exchangePlace, final String currencyCode) {
		this.clientFactory = clientFactory;
		this.exchangePlace = exchangePlace;
		this.currencyCode = currencyCode;

		String title = exchangePlace.getLabel() + ": " + currencyCode;

		view = new CurrencyViewImpl(title, this);

		view.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				stopAutoRefresh();
			}
		});

		clientFactory.getMainDataManager().addCurrencyTickerLoadHandler(new CurrencyTickerLoadHandler() {
			public void currencyTickerLoaded(CurrencyTickerLoadedEvent event) {
				if (event.getCurrencyTicker().getExchangePlaceName().equals(exchangePlace.getName())
						&& event.getCurrencyTicker().getCurrencyCode().equals(currencyCode))
					view.setCurrencyTicker(event.getCurrencyTicker());
			}
		});

		clientFactory.getMainDataManager().getCoinshiftDataManager()
				.addEveryAddressStatsLoadHandler(new EveryAddressStatsLoadHandler<AddressStatsDTO>() {
					public void everyAddressStatsLoaded(EveryAddressStatsLoadedEvent<AddressStatsDTO> event) {
						for (List<AddressStatsDTO> stats : event.getStats().values()) {
							if (stats != null && stats.size() > 0) {
								view.setAddressStats(stats.get(stats.size() - 1));
							}
						}
					}
				});

		clientFactory.getMainDataManager().getCoinshiftDataManager().addAllTotalStatsLoadHandler(new AllTotalStatsLoadHandler<AddressStatsDTO>() {
			public void allTotalStatsLoaded(AllTotalStatsLoadedEvent<AddressStatsDTO> event) {
				if (event.getStats() != null && event.getStats().size() > 0) {
					view.setTotalStats(event.getStats().get(event.getStats().size() - 1));
				}
			}
		});

		clientFactory.getMainDataManager().getCoinshiftDataManager()
				.addLastAddressStatsLoadHandler(new LastAddressStatsLoadHandler<AddressStatsDTO>() {
					public void lastAddressStatsLoaded(LastAddressStatsLoadedEvent<AddressStatsDTO> event) {
						view.setAddressStats(event.getStats());
					}
				});

		clientFactory.getMainDataManager().getCoinshiftDataManager().addLastTotalStatsLoadHandler(new LastTotalStatsLoadHandler<AddressStatsDTO>() {
			public void lastTotalStatsLoaded(LastTotalStatsLoadedEvent<AddressStatsDTO> event) {
				view.setTotalStats(event.getStats());
			}
		});

		clientFactory.getMainDataManager().getCoinshiftDataManager()
				.addEveryAddressBlockchainInfoHandler(new EveryAddressBlockchainInfoLoadHandler() {
					public void everyAddressBlockchainInfoLoaded(EveryAddressBlockchainInfoLoadedEvent event) {
						view.setBlockchainInfo(event.getBlockchainInfo());
					}
				});

		clientFactory.getMainDataManager().getCoinshiftDataManager()
				.addTotalAddressBlockchainInfoLoadHandler(new TotalAddressBlockchainInfoLoadHandler() {
					public void totalAddressBlockchainInfoLoaded(TotalAddressBlockchainInfoLoadedEvent event) {
						view.setTotalBlockchainInfo(event.getBlockchainInfo());
					}
				});

		loadAddressesStats();

		refreshTicker();

		startAutoRefresh();

	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public CurrencyView getView() {
		return view;
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return view.addHideHandler(handler);
	}

	@Override
	public void bringToFront() {
		view.show();
	}

	@Override
	public void hide() {
		view.hide();
	}

	@Override
	public void clearStats() {
		view.clearStats();
	}

	private void loadAddressesStats() {
		Map<String, List<AddressStatsDTO>> allStats = clientFactory.getMainDataManager().getCoinshiftDataManager().getDataContainer()
				.getAddressesStats();
		if (clientFactory.getMainDataManager().getCoinshiftDataManager().getAddresses() != null) {
			for (String address : clientFactory.getMainDataManager().getCoinshiftDataManager().getAddresses()) {
				List<AddressStatsDTO> stats = allStats.get(address);
				if (stats != null && stats.size() > 0) {
					view.setAddressStats(stats.get(stats.size() - 1));
				}
			}

			List<AddressStatsDTO> totalStats = clientFactory.getMainDataManager().getCoinshiftDataManager().getDataContainer().getTotalStats();
			if (totalStats != null && totalStats.size() > 0) {
				view.setTotalStats(totalStats.get(totalStats.size() - 1));
			}
		}
	}

	private void refreshTicker() {
		clientFactory.getMainDataManager().loadCurrencyTicker(exchangePlace, currencyCode);
	}

	private void startAutoRefresh() {
		// If there is already a timer, stop it and recall this function.
		if (autoRefreshTimer != null) {
			stopAutoRefresh();
			startAutoRefresh();
		} else {
			autoRefreshTimer = new Timer() {
				public void run() {
					refreshTicker();
				}
			};
			autoRefreshTimer.scheduleRepeating(AUTO_REFRESH_DELAY);
		}
	}

	private void stopAutoRefresh() {
		if (autoRefreshTimer != null) {
			autoRefreshTimer.cancel();
			autoRefreshTimer = null;
		}
	}

	@Override
	public void loadBlockchainInfo() {
		// If data already available, just display it
		Map<String, BlockchainAddressInfoDTO> blockChainInfo = clientFactory.getMainDataManager().getCoinshiftDataManager().getDataContainer()
				.getBlockChainInfo();
		if (blockChainInfo != null && blockChainInfo.size() > 0) {
			view.setBlockchainInfo(blockChainInfo);
		} else {
			// If not available, request to load it.
			clientFactory.getMainDataManager().getCoinshiftDataManager().loadAllBlockchainInfo();
		}

		BlockchainAddressInfoDTO totalBlockchainInfo = clientFactory.getMainDataManager().getCoinshiftDataManager().getDataContainer()
				.getTotalBlockchainInfo();
		if (totalBlockchainInfo != null) {
			view.setTotalBlockchainInfo(totalBlockchainInfo);
		} else {
			// If not available, request to load it.
			clientFactory.getMainDataManager().getCoinshiftDataManager().loadAllBlockchainInfo();
		}
	}

	@Override
	public void activate() {
		view.activate();
	}

}
