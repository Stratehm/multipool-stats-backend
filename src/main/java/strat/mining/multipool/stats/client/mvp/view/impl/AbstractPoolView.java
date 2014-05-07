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

import java.util.ArrayList;
import java.util.List;

import strat.mining.multipool.stats.client.mvp.view.PoolView;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.dom.DefaultScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public abstract class AbstractPoolView implements PoolView {

	private PoolView.PoolViewPresenter<? extends PoolView, ?> presenter;

	private BorderLayoutContainer mainContainer;

	private ContentPanel addressesContentPanel;
	private VerticalLayoutContainer addressesContainer;

	private TextArea addressesTextArea;
	private SuggestBox addressSuggestBox;

	private ToolBar mainToolbar;
	private TextButton okAddressButton;
	private TextButton currencyButton;
	private Menu currencyMenu;
	private ToggleButton autoRefreshButton;

	private ContentPanel globalContentPanel;
	private VerticalLayoutContainer globalContainer;
	private ToolBar globalToolBar;

	private boolean revertPixelResize;

	public AbstractPoolView(final PoolView.PoolViewPresenter<? extends PoolView, ?> presenter) {
		this.presenter = presenter;

		mainContainer = new BorderLayoutContainer();

		addressesContentPanel = new ContentPanel();
		addressesContentPanel.setHeadingText("Addresses stats");
		mainContainer.add(addressesContentPanel);

		addressesContainer = new VerticalLayoutContainer();
		addressesContainer.setScrollSupport(new DefaultScrollSupport(addressesContainer.getElement()));
		addressesContainer.setScrollMode(ScrollMode.AUTOY);
		addressesContainer.addStyleName("textCenter");
		addressesContentPanel.add(addressesContainer);

		addressesTextArea = new TextArea();
		addressesTextArea.setValue("");
		addressesTextArea.setOriginalValue("Addresses");
		addressesTextArea.addStyleName("addressesTextArea");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				addressesTextArea.getElement().getElementsByTagName("textarea").getItem(0).setAttribute("placeholder", "Line separated addresses");
			}
		});
		addressesContainer.add(addressesTextArea, new VerticalLayoutData(0.99, 80, new Margins(5)));

		addressSuggestBox = new SuggestBox(presenter.getAddressesSuggestOracle());
		addressSuggestBox.setLimit(5);
		addressSuggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			public void onSelection(SelectionEvent<Suggestion> event) {
				if (event.getSelectedItem() != null && !event.getSelectedItem().getReplacementString().isEmpty()) {
					suggestionSelected(event.getSelectedItem().getReplacementString());
				}
			}
		});
		addressSuggestBox.addStyleName("suggestBox");
		addressSuggestBox.getElement().setAttribute("placeholder", "Address suggest box");
		addressesContainer.add(addressSuggestBox, new VerticalLayoutData(0.99, 30, new Margins(0, 5, 0, 5)));

		mainToolbar = new ToolBar();

		okAddressButton = new TextButton("Let's go !");
		okAddressButton.setBorders(true);
		okAddressButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				List<String> addresses = parseAddresses();
				presenter.onValidate(addresses);
			}
		});

		currencyMenu = new Menu();

		currencyButton = new TextButton("Currency");
		currencyButton.setEnabled(false);
		currencyButton.setMenu(currencyMenu);

		autoRefreshButton = new ToggleButton("Auto Refresh");
		autoRefreshButton.setValue(true);
		autoRefreshButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				presenter.setAutoRefresh(event.getValue());
			}
		});

		mainToolbar.add(okAddressButton);
		mainToolbar.add(new FillToolItem());
		mainToolbar.add(currencyButton);
		mainToolbar.add(new SeparatorToolItem());
		mainToolbar.add(autoRefreshButton);
		mainToolbar.add(new SeparatorToolItem());

		addressesContainer.add(mainToolbar, new VerticalLayoutData(0.99, 40, new Margins(5)));

		globalContentPanel = new ContentPanel();
		globalContentPanel.setHeadingText("Global stats");
		BorderLayoutData eastBorderLayoutData = new BorderLayoutData(Window.getClientWidth() / 3);
		eastBorderLayoutData.setCollapsible(true);
		eastBorderLayoutData.setSplit(true);
		eastBorderLayoutData.setCollapseMini(false);
		eastBorderLayoutData.setMargins(new Margins(0, 5, 0, 5));
		eastBorderLayoutData.setMaxSize(2000);
		mainContainer.setEastWidget(globalContentPanel, eastBorderLayoutData);
		mainContainer.addCollapseHandler(new CollapseItemHandler<ContentPanel>() {
			public void onCollapse(CollapseItemEvent<ContentPanel> event) {
				presenter.onGlobalCollapse(isGlobalStatsCollapsed());
			}
		});
		mainContainer.addExpandHandler(new ExpandItemHandler<ContentPanel>() {
			public void onExpand(ExpandItemEvent<ContentPanel> event) {
				presenter.onGlobalCollapse(isGlobalStatsCollapsed());
			}
		});

		globalContainer = new VerticalLayoutContainer();
		globalContainer.setScrollSupport(new DefaultScrollSupport(globalContainer.getElement()));
		globalContainer.setScrollMode(ScrollMode.AUTOY);
		globalContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		globalContentPanel.add(globalContainer);

		globalToolBar = new ToolBar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				globalContainer.insert(globalToolBar, 0, new VerticalLayoutData(1, -1));
			}
		});
	}

	@Override
	public void setExchangePlaces(List<ExchangePlaceDTO> places) {
		if (places != null && places.size() > 0) {
			for (final ExchangePlaceDTO place : places) {
				MenuItem placeItem = new MenuItem(place.getLabel());
				Menu subMenu = new Menu();
				placeItem.setSubMenu(subMenu);
				currencyMenu.add(placeItem);

				for (final String currencyCode : place.getCurrencyCodes()) {
					MenuItem currencyItem = new MenuItem(currencyCode);
					currencyItem.addSelectionHandler(new SelectionHandler<Item>() {
						public void onSelection(SelectionEvent<Item> event) {
							presenter.loadCurrencyDetails(place, currencyCode);
						}
					});
					subMenu.add(currencyItem);
				}
			}
			currencyButton.setEnabled(true);
		}
	}

	/**
	 * Add the suggestion to the addresses text area
	 */
	private void suggestionSelected(String suggestion) {
		List<String> addresses = parseAddresses();
		String result = "";
		for (String address : addresses) {
			result += address + "\n";
		}
		result += suggestion + "\n";

		addressesTextArea.setValue(result);
	}

	/**
	 * Format the addresses contained in the address text area into a list of
	 * addresses
	 */
	private List<String> parseAddresses() {
		String rawAddresses = addressesTextArea.getValue();
		rawAddresses = rawAddresses == null ? "" : rawAddresses;
		String[] addresses = rawAddresses.split("\n");
		List<String> addressesList = new ArrayList<String>();

		int nbAddresses = 0;
		for (String address : addresses) {
			if (!address.trim().isEmpty()) {
				addressesList.add(address.trim());

				// Process only 10 addresses max at the moment.
				if (nbAddresses > 9) {
					break;
				}

				nbAddresses++;
			}
		}
		return addressesList;
	}

	public ToolBar getMainToolbar() {
		return mainToolbar;
	}

	public ToolBar getGlobalToolbar() {
		return globalToolBar;
	}

	@Override
	public Widget asWidget() {
		return mainContainer;
	}

	public VerticalLayoutContainer getGlobalContainer() {
		return globalContainer;
	}

	public VerticalLayoutContainer getAddressesContainer() {
		return addressesContainer;
	}

	@Override
	public void setGlobalStatsCollapsed(boolean isCollapsed) {
		if (isCollapsed) {
			mainContainer.collapse(LayoutRegion.EAST);
		} else {
			mainContainer.expand(LayoutRegion.EAST);
		}
		mainContainer.forceLayout();
	}

	@Override
	public void setInputAddresses(List<String> addresses) {
		addressesTextArea.clear();
		String value = "";
		if (addresses != null) {
			for (String address : addresses) {
				value += address + "\n";
			}
		}
		addressesTextArea.setValue(value);
	}

	@Override
	public boolean isGlobalStatsCollapsed() {
		BorderLayoutData layoutData = (BorderLayoutData) globalContentPanel.getLayoutData();
		return layoutData.isCollapsed();
	}

	@Override
	public void setAutoRefresh(boolean autoRefresh) {
		autoRefreshButton.setValue(autoRefresh);
	}

	@Override
	public boolean isAutoRefresh() {
		return autoRefreshButton.getValue();
	}

	@Override
	public void forceResize() {
		BorderLayoutData layoutData = (BorderLayoutData) globalContentPanel.getLayoutData();
		layoutData.setSize(layoutData.getSize() + (revertPixelResize ? 1 : -1));
		revertPixelResize = !revertPixelResize;
		mainContainer.forceLayout();
	}

}
