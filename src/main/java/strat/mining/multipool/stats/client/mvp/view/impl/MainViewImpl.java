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

import strat.mining.multipool.stats.client.mvp.view.MainView;
import strat.mining.multipool.stats.client.resources.ClientResources;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent.CloseHandler;

public class MainViewImpl implements MainView {

	private BorderLayoutContainer mainContainer;

	private TabPanel tabPanel;

	private Widget donationWidget;

	private Widget activeWidget;

	public MainViewImpl(final MainView.MainViewPresenter presenter) {
		mainContainer = new BorderLayoutContainer();

		tabPanel = new TabPanel();
		tabPanel.setCloseContextMenu(true);
		TabItemConfig config = new TabItemConfig();
		config.setIcon(ClientResources.INSTANCE.add());
		config.setClosable(false);
		config.setText("Open pool");
		final Label fakeLabel = new Label();
		tabPanel.add(fakeLabel, config);
		tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Widget>() {
			public void onBeforeSelection(BeforeSelectionEvent<Widget> event) {
				activeWidget = tabPanel.getActiveWidget();

				if (event.getItem() != fakeLabel && activeWidget != null) {
					presenter.deactivatePoolPresenter(tabPanel.getConfig(activeWidget).getText());
				}
			}
		});
		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			public void onSelection(SelectionEvent<Widget> event) {
				if (event.getSelectedItem() == fakeLabel) {
					if (activeWidget != null) {
						tabPanel.setActiveWidget(activeWidget);
					} else if (tabPanel.getWidgetCount() > 1) {
						// Open the first tab if the active widget is null (it
						// is a closed panel) and another pool tab exist
						tabPanel.setActiveWidget(tabPanel.getWidget(0));
					}
					presenter.openPoolSelectionView();
				} else {
					presenter.activatePoolPresenter(tabPanel.getConfig(event.getSelectedItem()).getText());
				}
			}
		});

		tabPanel.addCloseHandler(new CloseHandler<Widget>() {
			public void onClose(CloseEvent<Widget> event) {
				presenter.closePoolPresenter(tabPanel.getConfig(event.getItem()).getText());
			}
		});

		mainContainer.setWidget(tabPanel);

	}

	@Override
	public Widget asWidget() {
		return mainContainer;
	}

	@Override
	public void setDonationWidget(Widget donationWidget) {
		this.donationWidget = donationWidget;
		mainContainer.setSouthWidget(donationWidget, new BorderLayoutData(30));
	}

	@Override
	public void addPoolTab(String poolName, IsWidget poolView) {
		TabItemConfig config = new TabItemConfig(poolName, true);
		tabPanel.insert(poolView.asWidget(), tabPanel.getWidgetCount() - 1, config);
		tabPanel.setActiveWidget(poolView);
	}

	@Override
	public boolean showPoolTab(String poolName) {
		boolean isFound = false;
		for (int i = 0; i < tabPanel.getWidgetCount(); i++) {
			if (tabPanel.getWidget(i) != null) {
				TabItemConfig config = tabPanel.getConfig(tabPanel.getWidget(i));
				if (config != null && config.getText().equals(poolName)) {
					tabPanel.setActiveWidget(tabPanel.getWidget(i));
					isFound = true;
				}
			}
		}
		return isFound;
	}

	@Override
	public int getOpendPoolCount() {
		// Substract one due to the Add pool fake widget
		return tabPanel.getWidgetCount() - 1;

	}

}
