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
package strat.mining.multipool.stats.client.mvp.view.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import strat.mining.multipool.stats.client.component.ImageTip;
import strat.mining.multipool.stats.client.component.TwitterTimelineWidget;
import strat.mining.multipool.stats.client.mvp.view.DonationView;
import strat.mining.multipool.stats.client.resources.ClientResources;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.DonationTransactionDetailsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

public class DonationViewImpl implements DonationView {

	private static final int NB_DISPLAYED_LAST_TRANSACTIONS = 10;

	private static final String DEFAULT_BTC_ADDRESS = "19wv8FQKv3NkwTdzBCQn1AGsb9ghqBPWXi";

	private DonationViewPresenter presenter;

	private HtmlLayoutContainer mainContainer;

	private Label btcAddressLabel;
	private Label donationProgressLabel;
	private HTMLPanel notAvailablePanel;

	private ToolTip qrCodeTooltip;

	private ProgressBar progressBar;
	private ToolTipConfig tooltipConfig;

	private TextButton openStratehmTwitterTimelineButton;
	private com.sencha.gxt.widget.core.client.Window stratehmTwitterTimelineWindow;
	private Anchor contactAnchor;

	interface DonationTemplate extends XTemplates {
		@XTemplate("<div class=\"donationLabel\" style=\"float: left;\"></div><div class=\"donationProgressLabel\" style=\"float: left;\"></div><div class=\"donationProgressBar\" style=\"float: left;\"></div><div class=\"twitterButton\" style=\"float: right;\"></div><div class=\"contactAnchor\" style=\"float: right;\"></div>")
		SafeHtml getTemplate();
	}

	public DonationViewImpl(DonationViewPresenter presenter) {
		this.presenter = presenter;

		DonationTemplate templates = GWT.create(DonationTemplate.class);

		mainContainer = new HtmlLayoutContainer(templates.getTemplate());

		btcAddressLabel = new Label("BTC: " + DEFAULT_BTC_ADDRESS);

		ImageTip tip = new ImageTip(btcAddressLabel, ClientResources.INSTANCE.donateQRCode());
		tip.setClosable(true);
		tip.setAnchor(ImageTip.Anchor.BOTTOM_LEFT);
		tip.setBottomOffset(-50);
		tip.setShowDelay(700);

		donationProgressLabel = new Label("Current month donations (used for the server rent): ");

		notAvailablePanel = new HTMLPanel("Not Available.");
		notAvailablePanel.addStyleName("notAvailableLabel");

		progressBar = new ProgressBar(new ProgressBarCell() {
			private Set<String> consumedEvents = new HashSet<String>();

			public Set<String> getConsumedEvents() {
				Set<String> superEvents = super.getConsumedEvents();
				if (superEvents != null) {
					consumedEvents.addAll(superEvents);
				}
				return consumedEvents;
			}

		});
		progressBar.setIncrement(100);
		tooltipConfig = new ToolTipConfig();
		tooltipConfig.setHideDelay(1000);
		tooltipConfig.setDismissDelay(0);
		progressBar.setToolTipConfig(tooltipConfig);

		contactAnchor = new Anchor();
		contactAnchor.setText("Contact");
		contactAnchor.setHref("https://bitcointalk.org/index.php?action=profile;u=214653");
		contactAnchor.setTarget("_blank");

		openStratehmTwitterTimelineButton = new TextButton("Open timeline @Stratehm");
		openStratehmTwitterTimelineButton.setIcon(ClientResources.INSTANCE.twitterBlue());
		openStratehmTwitterTimelineButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				openStratehmTwitterWindow();
			}
		});

		mainContainer.add(btcAddressLabel, new HtmlData(".donationLabel"));
		mainContainer.add(donationProgressLabel, new HtmlData(".donationProgressLabel"));
		mainContainer.add(notAvailablePanel, new HtmlData(".donationProgressBar"));
		mainContainer.add(openStratehmTwitterTimelineButton, new HtmlData(".twitterButton"));
		mainContainer.add(contactAnchor, new HtmlData(".contactAnchor"));

	}

	@Override
	public Widget asWidget() {
		return mainContainer;
	}

	@Override
	public void setDonationDetails(DonationDetailsDTO donationDetails) {

		float percent = (float) donationDetails.getDonationsInBTC() / (float) donationDetails.getDonationsNeeded();
		progressBar.updateProgress(percent, "{0} %");

		DateTimeFormat dtf = DateTimeFormat.getFormat("d MMM HH:mm:ss");
		String result = "Server rent price: " + formatCurrencyValue(donationDetails.getRentPriceInEuro()) + " &euro; ("
				+ formatBTCValue(donationDetails.getDonationsNeeded()) + " BTC)";
		result += "<br/>Current BTC price: " + formatCurrencyValue(donationDetails.getBtcPriceInEuro()) + " &euro;";
		result += "<br/>Total month donations: " + formatCurrencyValue(donationDetails.getDonationsInBTC() * donationDetails.getBtcPriceInEuro())
				+ " &euro; (" + formatBTCValue(donationDetails.getDonationsInBTC()) + " BTC)";
		result += "<br/>Last " + NB_DISPLAYED_LAST_TRANSACTIONS + " donations:";
		if (donationDetails.getTransactions() != null && donationDetails.getTransactions().size() > 0) {
			result += "<table class=\"donationTable\">";
			List<DonationTransactionDetailsDTO> transactions = donationDetails.getTransactions();
			for (int i = 0; i < transactions.size() && i < NB_DISPLAYED_LAST_TRANSACTIONS; i++) {
				DonationTransactionDetailsDTO transaction = transactions.get(i);
				result += "<tr><td class=\"donationDate\">" + dtf.format(transaction.getTime()) + ":</td><td class=\"donationValue\">"
						+ formatBTCValue(transaction.getValue()) + " BTC</td></tr>";
			}
			result += "</table>";
		} else {
			result += "<br/><i>None</i>";
		}
		result += "<br/>Last month donations: " + formatBTCValue(donationDetails.getLastMonthDonationsValue()) + " BTC";
		tooltipConfig.setBodyHtml(result);
		progressBar.getToolTip().update(tooltipConfig);

		btcAddressLabel.setText("BTC: " + donationDetails.getDonationBtcAddress());

		mainContainer.remove(notAvailablePanel);
		mainContainer.add(progressBar, new HtmlData(".donationProgressBar"));
	}

	@Override
	public void setNotAvailable() {
		mainContainer.remove(progressBar);
		mainContainer.remove(notAvailablePanel);
		notAvailablePanel = new HTMLPanel("Not Available. <a href=\"http://blockchain.info\" target=\"_blank\">Blockchain.info</a> may be down.");
		mainContainer.add(notAvailablePanel, new HtmlData(".donationProgressBar"));
	}

	private String formatCurrencyValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.##");
		return nf.format(value);
	}

	private String formatBTCValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.########");
		return nf.format(value);
	}

	public void openStratehmTwitterWindow() {
		if (stratehmTwitterTimelineWindow != null) {
			stratehmTwitterTimelineWindow.show();
		} else {
			stratehmTwitterTimelineWindow = new com.sencha.gxt.widget.core.client.Window();
			stratehmTwitterTimelineWindow.setHeadingText("Stratehm Twitter");
			final TwitterTimelineWidget timeline = new TwitterTimelineWidget("https://twitter.com/Stratehm", "437880877700243456");
			stratehmTwitterTimelineWindow.setWidget(timeline);
			stratehmTwitterTimelineWindow.setWidth(450);
			stratehmTwitterTimelineWindow.setHeight(490);
			stratehmTwitterTimelineWindow.setCollapsible(false);
			stratehmTwitterTimelineWindow.setResizable(true);
			stratehmTwitterTimelineWindow.addResizeHandler(new ResizeHandler() {
				public void onResize(ResizeEvent event) {
					timeline.setSize(event.getWidth(), event.getHeight());
				}
			});
			stratehmTwitterTimelineWindow.show();
			stratehmTwitterTimelineWindow.center();
			timeline.load();

			stratehmTwitterTimelineWindow.addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					stratehmTwitterTimelineWindow = null;
				}
			});

			timeline.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					stratehmTwitterTimelineWindow.toFront();
				}
			});

		}
	}
}
