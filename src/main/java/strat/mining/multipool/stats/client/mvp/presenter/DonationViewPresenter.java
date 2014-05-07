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

import strat.mining.multipool.stats.client.component.Notification;
import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.event.AddressDonationDetailsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.event.DonationDetailsLoadedEvent;
import strat.mining.multipool.stats.client.mvp.handler.AddressDonationDetailsLoadHandler;
import strat.mining.multipool.stats.client.mvp.handler.DonationDetailsLoadHandler;
import strat.mining.multipool.stats.client.mvp.view.DonationView;
import strat.mining.multipool.stats.client.mvp.view.impl.DonationViewImpl;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.DonationTransactionDetailsDTO;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;

public class DonationViewPresenter implements DonationView.DonationViewPresenter {

	private static final int REFRESH_TIMER_DELAY = 600000;

	private ClientFactory clientFactory;

	private DonationView view;

	private Timer refreshTimer;

	public DonationViewPresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = new DonationViewImpl(this);

		clientFactory.getMainDataManager().addDonationDetailsLoadHandler(new DonationDetailsLoadHandler() {
			public void donationDetailsLoaded(DonationDetailsLoadedEvent event) {
				if (event.getDonationDetails() == null) {
					view.setNotAvailable();
				} else {
					view.setDonationDetails(event.getDonationDetails());
				}
			}
		});

		clientFactory.getMainDataManager().addAddressDonationDetailsLoadHandler(new AddressDonationDetailsLoadHandler() {
			public void addressDonationDetailsLoaded(AddressDonationDetailsLoadedEvent event) {
				if (event.getDonationDetails() != null && event.getDonationDetails().isDonator()) {
					if (event.getDonationDetails().getDonationsDetails() != null && event.getDonationDetails().getDonationsDetails().size() > 0) {
						String result = "Thank you very much for your donation";
						result += event.getDonationDetails().getDonationsDetails().size() > 1 ? "s: " : ": ";

						for (DonationTransactionDetailsDTO tx : event.getDonationDetails().getDonationsDetails()) {
							result += "<br/>" + formatBTCValue(tx.getValue()) + " BTC";
						}

						result += "<br/>Cheers!";

						Notification.showNotification("Stratehm", result);
					}
				}
			}
		});

		DonationDetailsDTO donationDetails = clientFactory.getMainDataManager().getMainDataContainer().getDonationDetails();
		if (donationDetails == null) {
			clientFactory.getMainDataManager().loadDonationDetails();
		} else {
			view.setDonationDetails(donationDetails);
		}

		startRefreshTimer();
	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public DonationView getView() {
		return view;
	}

	private void startRefreshTimer() {
		if (refreshTimer == null) {
			refreshTimer = new Timer() {
				public void run() {
					clientFactory.getMainDataManager().loadDonationDetails();
				}
			};
			refreshTimer.scheduleRepeating(REFRESH_TIMER_DELAY);
		} else {
			refreshTimer.cancel();
			refreshTimer = null;
			startRefreshTimer();
		}
	}

	private String formatBTCValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.########");
		return nf.format(value);
	}
}
