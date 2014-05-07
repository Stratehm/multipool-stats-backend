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
package strat.mining.multipool.stats.client.mvp.view.middlecoin.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import strat.mining.multipool.stats.client.component.RefreshLabel;
import strat.mining.multipool.stats.client.mvp.view.middlecoin.CurrencyView;
import strat.mining.multipool.stats.client.resources.ClientResources;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.DefaultScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;

public class CurrencyViewImpl implements CurrencyView {

	private CurrencyView.CurrencyViewPresenter presenter;

	private Window window;

	private VerticalLayoutContainer mainContainer;
	private FramedPanel currencyFramedPanel;

	private FramedPanel totalStatsFramedPanel;

	private Map<String, FramedPanel> addressStatsFramedPanels;
	private Map<String, VerticalLayoutContainer> addressStatsVerticalContainer;
	private Map<String, FramedPanel> addressBlockchainFramedPanels;

	private RefreshLabel lastRefreshLabel;

	private CurrencyTickerDTO currentTicker;
	private AddressStatsDTO currentTotalStats;
	private Map<String, AddressStatsDTO> currentAddressStats;

	private Map<String, BlockchainAddressInfoDTO> blockchainByAddress;
	private BlockchainAddressInfoDTO totalBlockchainInfo;
	private FramedPanel totalBlockchainFramedPanel;

	private boolean fireHideEvent;

	private boolean saveExpanded;

	public interface CurrencyPanelTemplate extends XTemplates {
		@XTemplate("<table width=\"100%\" height=\"50px\"><tbody><tr><td class=\"label1\" /><td class=\"value1\" /><td class=\"label5\" /><td class=\"value5\" /></tr><tr><td class=\"label2\" /><td class=\"value2\" /><td class=\"label6\" /><td class=\"value6\" /></tr><tr><td class=\"label3\" /><td class=\"value3\" /><td class=\"label7\" /><td class=\"value7\" /></tr><tr><td class=\"label4\" /><td class=\"value4\" /><td class=\"label8\" /><td class=\"value8\" /></tr></tbody></table>")
		SafeHtml getTemplate();
	}

	public interface StatsPanelTemplate extends XTemplates {
		@XTemplate("<table width=\"100%\" height=\"50px\"><tbody><tr><td class=\"label1\" /><td class=\"value1\" /><td class=\"label4\" /><td class=\"value4\" /></tr><tr><td class=\"label2\" /><td class=\"value2\" /><td class=\"label5\" /><td class=\"value5\" /></tr><tr><td class=\"label3\" /><td class=\"value3\" /><td class=\"label6\" /><td class=\"value6\" /></tr></tbody></table>")
		SafeHtml getTemplate();
	}

	public interface BlockchainPanelTemplate extends XTemplates {
		@XTemplate("<table width=\"100%\" height=\"50px\"><tbody><tr><td class=\"label1\" /><td class=\"value1\" /></tr><tr><td class=\"label2\" /><td class=\"value2\" /></tr><tr><td class=\"label3\" /><td class=\"value3\" /></tr></tbody></table>")
		SafeHtml getTemplate();
	}

	public CurrencyViewImpl(String title, CurrencyViewPresenter presenter) {
		this.presenter = presenter;
		this.currentAddressStats = new HashMap<String, AddressStatsDTO>();
		this.addressStatsFramedPanels = new HashMap<String, FramedPanel>();
		this.addressStatsVerticalContainer = new HashMap<String, VerticalLayoutContainer>();
		this.addressBlockchainFramedPanels = new HashMap<String, FramedPanel>();
		this.fireHideEvent = true;

		lastRefreshLabel = new RefreshLabel();

		window = new Window();
		window.setHeaderVisible(true);
		window.setHeadingText(title);
		window.setHeight(500);
		window.setWidth(450);
		window.setMaximizable(true);
		window.setCollapsible(true);
		window.setResizable(true);
		window.getHeader().addTool(lastRefreshLabel);

		saveExpanded = true;

		window.addMaximizeHandler(new MaximizeHandler() {
			public void onMaximize(MaximizeEvent event) {
				window.expand();
			}
		});

		mainContainer = new VerticalLayoutContainer();
		mainContainer.setScrollSupport(new DefaultScrollSupport(mainContainer.getElement()));
		mainContainer.setScrollMode(ScrollMode.AUTOY);
		mainContainer.addStyleName("whiteBackground");
		mainContainer.addStyleName("textCenter");

		currencyFramedPanel = new FramedPanel();
		currencyFramedPanel.setHeadingText("Exchange details");
		currencyFramedPanel.setCollapsible(true);

		Image loadingImage = new Image();
		loadingImage.setUrl(ClientResources.INSTANCE.loading().getSafeUri());
		loadingImage.setHeight("32px");
		loadingImage.setWidth("32px");
		FlowLayoutContainer simpleContainer = new FlowLayoutContainer();
		simpleContainer.addStyleName("textCenter");
		simpleContainer.add(loadingImage);
		window.setWidget(simpleContainer);

		window.show();
		window.center();
	}

	@Override
	public Widget asWidget() {
		return window;
	}

	@Override
	public HandlerRegistration addHideHandler(final HideHandler handler) {
		return window.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if (fireHideEvent) {
					handler.onHide(event);
				}
			}
		});
	}

	@Override
	public void show() {
		window.show();
		window.expand();
		window.toFront();
	}

	private Widget createCurrencyPanel(CurrencyTickerDTO ticker) {
		CurrencyPanelTemplate templates = GWT.create(CurrencyPanelTemplate.class);

		HtmlLayoutContainer result = new HtmlLayoutContainer(templates.getTemplate());
		result.setWidth(500);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");

		Label priceLabel = new Label("Last Price:");
		Label highLabel = new Label("High:");
		Label lowLabel = new Label("Low:");
		Label buyLabel = new Label("Buy:");
		Label sellLabel = new Label("Sell:");
		Label volumeLabel = new Label("Volume:");
		Label updateTimeLabel = new Label("Refreshed:");

		Label priceValue = new Label(formatCurrencyValue(ticker.getLast()) + " " + ticker.getCurrencyCode());
		Label highValue = new Label(formatCurrencyValue(ticker.getHigh()) + " " + ticker.getCurrencyCode());
		Label lowValue = new Label(formatCurrencyValue(ticker.getLow()) + " " + ticker.getCurrencyCode());
		Label buyValue = new Label(formatCurrencyValue(ticker.getBuy()) + " " + ticker.getCurrencyCode());
		Label sellValue = new Label(formatCurrencyValue(ticker.getSell()) + " " + ticker.getCurrencyCode());
		Label volumeValue = new Label(Float.toString(ticker.getVolume()) + " BTC");
		Label updateTimeValue = new Label(dtf.format(ticker.getRefreshTime()));

		result.add(priceLabel, new HtmlData(".label1"));
		result.add(highLabel, new HtmlData(".label2"));
		result.add(lowLabel, new HtmlData(".label3"));
		result.add(updateTimeLabel, new HtmlData(".label4"));
		result.add(buyLabel, new HtmlData(".label5"));
		result.add(sellLabel, new HtmlData(".label6"));
		result.add(volumeLabel, new HtmlData(".label7"));

		result.add(priceValue, new HtmlData(".value1"));
		result.add(highValue, new HtmlData(".value2"));
		result.add(lowValue, new HtmlData(".value3"));
		result.add(updateTimeValue, new HtmlData(".value4"));
		result.add(buyValue, new HtmlData(".value5"));
		result.add(sellValue, new HtmlData(".value6"));
		result.add(volumeValue, new HtmlData(".value7"));

		return result;
	}

	private Widget createStatsPanel(AddressStatsDTO stats) {
		StatsPanelTemplate templates = GWT.create(StatsPanelTemplate.class);

		HtmlLayoutContainer result = new HtmlLayoutContainer(templates.getTemplate());
		result.setWidth(500);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");

		Label balanceLabel = new Label("Balance:");
		Label unexchangedLabel = new Label("Unexchanged:");
		Label immatureLabel = new Label("Immature:");
		Label totalLabel = new Label("Total:");
		Label paidoutLabel = new Label("Paidout:");
		Label updateTimeLabel = new Label("Refreshed:");

		Label balanceValue = new Label(formatCurrencyValue(stats.getBalance() * currentTicker.getLast()) + " " + currentTicker.getCurrencyCode());
		Label unexchangedValue = new Label(formatCurrencyValue(stats.getUnexchanged() * currentTicker.getLast()) + " "
				+ currentTicker.getCurrencyCode());
		Label immatureValue = new Label(formatCurrencyValue(stats.getImmature() * currentTicker.getLast()) + " " + currentTicker.getCurrencyCode());
		Label totalValue = new Label(formatCurrencyValue((stats.getBalance() + stats.getUnexchanged() + stats.getImmature())
				* currentTicker.getLast())
				+ " " + currentTicker.getCurrencyCode());
		Label paidoutValue = new Label(formatCurrencyValue(stats.getPaidOut() * currentTicker.getLast()) + " " + currentTicker.getCurrencyCode());
		Label updateTimeValue = new Label(dtf.format(stats.getRefreshTime()));

		result.add(balanceLabel, new HtmlData(".label1"));
		result.add(unexchangedLabel, new HtmlData(".label2"));
		result.add(immatureLabel, new HtmlData(".label3"));
		result.add(totalLabel, new HtmlData(".label4"));
		result.add(paidoutLabel, new HtmlData(".label5"));
		result.add(updateTimeLabel, new HtmlData(".label6"));

		result.add(balanceValue, new HtmlData(".value1"));
		result.add(unexchangedValue, new HtmlData(".value2"));
		result.add(immatureValue, new HtmlData(".value3"));
		result.add(totalValue, new HtmlData(".value4"));
		result.add(paidoutValue, new HtmlData(".value5"));
		result.add(updateTimeValue, new HtmlData(".value6"));

		return result;
	}

	private String formatCurrencyValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.##");
		return nf.format(value);
	}

	@Override
	public void setCurrencyTicker(CurrencyTickerDTO ticker) {
		lastRefreshLabel.resetTimer();
		this.currentTicker = ticker;
		currencyFramedPanel.setWidget(createCurrencyPanel(ticker));
		currencyFramedPanel.forceLayout();

		mainContainer.insert(currencyFramedPanel, 0, new VerticalLayoutData(1, -1, new Margins(5)));

		setTotalStats(currentTotalStats);

		for (AddressStatsDTO stats : currentAddressStats.values()) {
			setAddressStats(stats);
		}

		updateBlockchainPanels();

		// Set the main container in the window only if not already done.
		if (window.getWidgetIndex(mainContainer) < 0) {
			window.setWidget(mainContainer);
			window.forceLayout();
		}
	}

	@Override
	public void setTotalStats(AddressStatsDTO totalStats) {
		if (totalStats != null) {

			currentTotalStats = totalStats;

			if (totalStatsFramedPanel != null) {
				mainContainer.remove(totalStatsFramedPanel);
			}

			if (currentTicker != null) {
				totalStatsFramedPanel = new FramedPanel();
				totalStatsFramedPanel.setHeadingText("Selected addresses total");
				totalStatsFramedPanel.setCollapsible(true);
				mainContainer.insert(totalStatsFramedPanel, 1, new VerticalLayoutData(1, -1, new Margins(5)));
				mainContainer.forceLayout();

				VerticalLayoutContainer container = new VerticalLayoutContainer();

				totalBlockchainFramedPanel = new FramedPanel();
				totalBlockchainFramedPanel.setHeadingText("Total Blockchain");
				totalBlockchainFramedPanel.setCollapsible(true);
				totalBlockchainFramedPanel.collapse();
				Image loadingImage = new Image();
				loadingImage.setUrl(ClientResources.INSTANCE.loading().getSafeUri());
				loadingImage.setHeight("32px");
				loadingImage.setWidth("32px");
				FlowLayoutContainer simpleContainer = new FlowLayoutContainer();
				simpleContainer.addStyleName("textCenter");
				simpleContainer.add(loadingImage);
				totalBlockchainFramedPanel.setWidget(simpleContainer);
				totalBlockchainFramedPanel.addExpandHandler(new ExpandHandler() {
					public void onExpand(ExpandEvent event) {
						presenter.loadBlockchainInfo();
					}
				});

				container.add(totalBlockchainFramedPanel, new VerticalLayoutData(1, -1, new Margins(5, 0, 0, 0)));

				updateTotalBlockchainPanels();

				container.insert(createStatsPanel(totalStats), 0, new VerticalLayoutData(1, -1));
				// Remove the old statsPanel if it exists
				if (container.getWidgetCount() > 2) {
					container.remove(1);
				}
				container.forceLayout();
				totalStatsFramedPanel.setWidget(container);
				totalStatsFramedPanel.forceLayout();
			}
		}
	}

	@Override
	public void clearStats() {
		addressStatsFramedPanels.clear();
		addressStatsVerticalContainer.clear();
		addressBlockchainFramedPanels.clear();

		if (blockchainByAddress != null) {
			blockchainByAddress.clear();
		}

		currentAddressStats.clear();

		if (mainContainer != null) {
			mainContainer.clear();
			mainContainer.insert(currencyFramedPanel, 0, new VerticalLayoutData(1, -1, new Margins(5)));
			currentTotalStats = null;
		}
	}

	@Override
	public void setAddressStats(AddressStatsDTO addressStats) {
		if (addressStats != null) {

			currentAddressStats.put(addressStats.getAddress(), addressStats);

			if (currentTicker != null) {
				FramedPanel mainFramedPanel = addressStatsFramedPanels.get(addressStats.getAddress());
				VerticalLayoutContainer container = addressStatsVerticalContainer.get(addressStats.getAddress());

				if (mainFramedPanel == null) {
					mainFramedPanel = new FramedPanel();
					mainFramedPanel.setHeadingText(addressStats.getAddress());
					mainFramedPanel.setCollapsible(true);
					addressStatsFramedPanels.put(addressStats.getAddress(), mainFramedPanel);
					mainContainer.add(mainFramedPanel, new VerticalLayoutData(1, -1, new Margins(10, 5, 5, 5)));
					mainContainer.forceLayout();

					container = new VerticalLayoutContainer();
					addressStatsVerticalContainer.put(addressStats.getAddress(), container);

					FramedPanel blockchainFramedPanel = new FramedPanel();
					blockchainFramedPanel.setHeadingText("Blockchain");
					blockchainFramedPanel.setCollapsible(true);
					blockchainFramedPanel.collapse();
					Image loadingImage = new Image();
					loadingImage.setUrl(ClientResources.INSTANCE.loading().getSafeUri());
					loadingImage.setHeight("32px");
					loadingImage.setWidth("32px");
					FlowLayoutContainer simpleContainer = new FlowLayoutContainer();
					simpleContainer.addStyleName("textCenter");
					simpleContainer.add(loadingImage);
					blockchainFramedPanel.setWidget(simpleContainer);
					blockchainFramedPanel.addExpandHandler(new ExpandHandler() {
						public void onExpand(ExpandEvent event) {
							presenter.loadBlockchainInfo();
						}
					});

					addressBlockchainFramedPanels.put(addressStats.getAddress(), blockchainFramedPanel);

					container.add(blockchainFramedPanel, new VerticalLayoutData(1, -1, new Margins(5, 0, 0, 0)));

					mainFramedPanel.setWidget(container);

				}

				updateBlockchainPanels();

				container.insert(createStatsPanel(addressStats), 0, new VerticalLayoutData(1, -1));
				// Remove the old statsPanel if it exists
				if (container.getWidgetCount() > 2) {
					container.remove(1);
				}
				container.forceLayout();
				mainFramedPanel.forceLayout();
			}
		}
	}

	private void updateBlockchainPanels() {
		if (blockchainByAddress != null) {
			for (Entry<String, BlockchainAddressInfoDTO> entry : blockchainByAddress.entrySet()) {
				FramedPanel blockchainFramedPanel = addressBlockchainFramedPanels.get(entry.getKey());
				blockchainFramedPanel.setWidget(createBlockchainPanel(entry.getValue()));
			}
		}

	}

	private Widget createBlockchainPanel(BlockchainAddressInfoDTO blockchainDto) {
		return createBlockchainPanel(blockchainDto, false);
	}

	private Widget createBlockchainPanel(BlockchainAddressInfoDTO blockchainDto, boolean isTotalStats) {
		BlockchainPanelTemplate templates = GWT.create(BlockchainPanelTemplate.class);

		Widget result = null;
		if (blockchainDto != null) {
			HtmlLayoutContainer htmlPanel = new HtmlLayoutContainer(templates.getTemplate());
			htmlPanel.setWidth(500);

			Label balanceLabel = new Label("Current balance:");
			balanceLabel.setTitle("The balance associated to\n" + blockchainDto.getAddress() + "\nin the blockchain.");
			Label nextBalanceLabel = new Label("Next balance:");
			nextBalanceLabel.setTitle("The next balance value based\non the current value added to\nthe balance still in the pool.");
			Label allLabel = new Label("All:");
			allLabel.setTitle("The total of all these items:\n-Current balance\n-Pool balance\n-Pool unexchanged\n-Pool immature");

			AddressStatsDTO stats = currentAddressStats.get(blockchainDto.getAddress());
			if (isTotalStats) {
				stats = currentTotalStats;
			}

			float nextBalance = 0;
			float total = 0;
			if (stats != null) {
				nextBalance = blockchainDto.getCurrentBalance() + stats.getBalance();
				total = nextBalance + stats.getUnexchanged() + stats.getImmature();
			}

			Label balanceValue = new Label(formatCurrencyValue(blockchainDto.getCurrentBalance() * currentTicker.getLast()) + " "
					+ currentTicker.getCurrencyCode() + " (" + formatBTCValue(blockchainDto.getCurrentBalance()) + " BTC)");
			Label nextBalanceValue = new Label(formatCurrencyValue(nextBalance * currentTicker.getLast()) + " " + currentTicker.getCurrencyCode()
					+ " (" + formatBTCValue(nextBalance) + " BTC)");
			Label allValue = new Label(formatCurrencyValue(total * currentTicker.getLast()) + " " + currentTicker.getCurrencyCode() + " ("
					+ formatBTCValue(total) + " BTC)");

			htmlPanel.add(balanceLabel, new HtmlData(".label1"));
			htmlPanel.add(nextBalanceLabel, new HtmlData(".label2"));
			htmlPanel.add(allLabel, new HtmlData(".label3"));

			htmlPanel.add(balanceValue, new HtmlData(".value1"));
			htmlPanel.add(nextBalanceValue, new HtmlData(".value2"));
			htmlPanel.add(allValue, new HtmlData(".value3"));

			result = htmlPanel;
		} else {
			result = new Label("Not available");
		}

		return result;
	}

	@Override
	public void setBlockchainInfo(Map<String, BlockchainAddressInfoDTO> blockchainByAddress) {
		this.blockchainByAddress = blockchainByAddress;
		updateBlockchainPanels();
	}

	private String formatBTCValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.########");
		return nf.format(value);
	}

	@Override
	public void setTotalBlockchainInfo(BlockchainAddressInfoDTO totalBlockchain) {
		this.totalBlockchainInfo = totalBlockchain;
		updateTotalBlockchainPanels();
	}

	private void updateTotalBlockchainPanels() {
		if (totalBlockchainInfo != null) {
			totalBlockchainFramedPanel.setWidget(createBlockchainPanel(totalBlockchainInfo, true));
		}
	}

	@Override
	public void hide() {
		fireHideEvent = false;
		saveExpanded = window.isExpanded();
		if (!saveExpanded) {
			window.expand();
		}
		window.hide();
		fireHideEvent = true;
	}

	@Override
	public void activate() {
		window.show();
		if (!saveExpanded) {
			window.setAnimCollapse(false);
			window.collapse();
			window.setAnimCollapse(true);
		}
	}
}
