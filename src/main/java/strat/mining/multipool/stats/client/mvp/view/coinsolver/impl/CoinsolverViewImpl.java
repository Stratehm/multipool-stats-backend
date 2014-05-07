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
package strat.mining.multipool.stats.client.mvp.view.coinsolver.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import strat.mining.multipool.stats.client.mvp.view.coinsolver.CoinsolverView;
import strat.mining.multipool.stats.client.mvp.view.coinsolver.component.AddressChartPanel;
import strat.mining.multipool.stats.client.mvp.view.coinsolver.component.GlobalChartPanel;
import strat.mining.multipool.stats.client.mvp.view.impl.AbstractPoolView;
import strat.mining.multipool.stats.client.resources.ClientResources;
import strat.mining.multipool.stats.dto.coinsolver.AddressStatsDTO;
import strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class CoinsolverViewImpl extends AbstractPoolView implements CoinsolverView {

	private CoinsolverView.CoinsolverViewPresenter presenter;

	private ToggleButton displaySummaryButton;
	private ToggleButton displayEarningButton;
	private ToggleButton displayPowerButton;

	private TextButton openNewsButton;

	private Image loadingAddressesImage;
	private Image loadingGlobalImage;

	private Map<String, AddressChartPanel> addressCharts;
	private GlobalChartPanel globalChart;
	private AddressChartPanel totalChart;

	public CoinsolverViewImpl(final CoinsolverView.CoinsolverViewPresenter presenter) {
		super(presenter);
		this.presenter = presenter;
		this.addressCharts = new HashMap<String, AddressChartPanel>();

		displaySummaryButton = new ToggleButton("Display Summary");
		displaySummaryButton.setValue(true);
		displaySummaryButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				presenter.setDisplaySummary(event.getValue());
			}
		});

		displayEarningButton = new ToggleButton("Display BTC Graph");
		displayEarningButton.setValue(true);
		displayEarningButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				presenter.setDisplayBTCChart(event.getValue());
			}
		});

		displayPowerButton = new ToggleButton("Display Hash Graph");
		displayPowerButton.setValue(true);
		displayPowerButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				presenter.setDisplayPowerChart(event.getValue());
			}
		});

		getMainToolbar().add(displaySummaryButton);
		getMainToolbar().add(displayEarningButton);
		getMainToolbar().add(displayPowerButton);

		openNewsButton = new TextButton("Coinsolver News");
		openNewsButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				Window.open("http://coinsolver.com/blog/", "_blank", null);
			}
		});

		getGlobalToolbar().add(openNewsButton);

		loadingAddressesImage = new Image();
		loadingAddressesImage.setUrl(ClientResources.INSTANCE.loading().getSafeUri());
		loadingAddressesImage.setHeight("32px");
		loadingAddressesImage.setWidth("32px");

		loadingGlobalImage = new Image();
		loadingGlobalImage.setUrl(ClientResources.INSTANCE.loading().getSafeUri());
		loadingGlobalImage.setHeight("32px");
		loadingGlobalImage.setWidth("32px");
	}

	@Override
	public void setTotalStats(List<AddressStatsDTO> stats) {
		totalChart = new AddressChartPanel("Total of selected addresses", true);
		totalChart.setAddressStats(stats);

		int insertionIndex = 1;
		if (getGlobalContainer().getWidgetCount() > 1) {
			insertionIndex = 2;
		}

		totalChart.setDisplaySummary(isSummaryDisplayed());
		totalChart.setDisplayBTCChart(isBTCChartDisplayed());
		totalChart.setDisplayPowerChart(isPowerChartDisplayed());

		totalChart.addPaidoutClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				presenter.loadTotalPaidout();
			}
		});

		getGlobalContainer().insert(totalChart, insertionIndex, new VerticalLayoutData(1, -1, new Margins(5)));
	}

	@Override
	public void addAddressStats(final String address, List<AddressStatsDTO> stats) {
		AddressChartPanel chart = new AddressChartPanel(address, false);
		chart.setAddressStats(stats);
		chart.setDisplaySummary(isSummaryDisplayed());
		chart.setDisplayBTCChart(isBTCChartDisplayed());
		chart.setDisplayPowerChart(isPowerChartDisplayed());
		chart.addPaidoutClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				List<String> addresses = new ArrayList<String>();
				addresses.add(address);
				presenter.loadPaidout(addresses);
			}
		});
		getAddressesContainer().add(chart, new VerticalLayoutData(1, -1, new Margins(5)));
		addressCharts.put(address, chart);
	}

	@Override
	public void setGlobalStats(List<GlobalStatsDTO> stats) {
		globalChart = new GlobalChartPanel("Coinsolver");
		globalChart.setGlobalStats(stats);
		globalChart.setDisplaySummary(isSummaryDisplayed());
		globalChart.setDisplayBTCChart(isBTCChartDisplayed());
		globalChart.setDisplayPowerChart(isPowerChartDisplayed());
		getGlobalContainer().insert(globalChart, 1, new VerticalLayoutData(1, -1, new Margins(5)));
	}

	@Override
	public void clearAddressStats() {
		for (AddressChartPanel chart : addressCharts.values()) {
			getAddressesContainer().remove(chart);
		}
		addressCharts.clear();
	}

	@Override
	public void displayAddressesLoader(boolean isDisplayed) {
		if (isDisplayed) {
			getAddressesContainer().add(loadingAddressesImage);
		} else {
			getAddressesContainer().remove(loadingAddressesImage);
		}
	}

	@Override
	public void displayGlobalLoader(boolean isDisplayed) {
		if (isDisplayed) {
			getGlobalContainer().add(loadingGlobalImage);
		} else {
			getGlobalContainer().remove(loadingGlobalImage);
		}
	}

	@Override
	public void clearTotalStats() {
		if (totalChart != null) {
			getGlobalContainer().remove(totalChart);
		}
	}

	@Override
	public void displaySummary(boolean display) {
		displaySummaryButton.setValue(display);
		updateDisplaySummaryStates();
	}

	@Override
	public void displayBTCChart(boolean display) {
		displayEarningButton.setValue(display);
		updateDisplayBTCStates();
	}

	@Override
	public void displayPowerChart(boolean display) {
		displayPowerButton.setValue(display);
		updateDisplayPowerStates();
	}

	@Override
	public boolean isSummaryDisplayed() {
		return displaySummaryButton.getValue();
	}

	@Override
	public boolean isBTCChartDisplayed() {
		return displayEarningButton.getValue();
	}

	@Override
	public boolean isPowerChartDisplayed() {
		return displayPowerButton.getValue();
	}

	private void updateDisplaySummaryStates() {
		boolean displaySummary = displaySummaryButton.getValue();

		for (AddressChartPanel panel : addressCharts.values()) {
			if (displaySummary != panel.isDisplaySummary()) {
				panel.setDisplaySummary(displaySummary);
			}
		}

		if (totalChart != null) {
			totalChart.setDisplaySummary(displaySummary);
		}

		if (globalChart != null) {
			globalChart.setDisplaySummary(displaySummary);
		}
	}

	private void updateDisplayBTCStates() {
		boolean displayEarning = displayEarningButton.getValue();

		for (AddressChartPanel panel : addressCharts.values()) {
			if (displayEarning != panel.isDisplayBTCChart()) {
				panel.setDisplayBTCChart(displayEarning);
			}
		}

		if (totalChart != null) {
			totalChart.setDisplayBTCChart(displayEarning);
		}

		if (globalChart != null) {
			globalChart.setDisplayBTCChart(displayEarning);
		}
	}

	private void updateDisplayPowerStates() {
		boolean displayPower = displayPowerButton.getValue();

		for (AddressChartPanel panel : addressCharts.values()) {
			if (displayPower != panel.isDisplayPowerChart()) {
				panel.setDisplayPowerChart(displayPower);
			}
		}

		if (totalChart != null) {
			totalChart.setDisplayPowerChart(displayPower);
		}

		if (globalChart != null) {
			globalChart.setDisplayPowerChart(displayPower);
		}
	}

	@Override
	public void appendAddressStats(final AddressStatsDTO addressStats) {
		AddressChartPanel panel = addressCharts.get(addressStats.getAddress());

		if (panel != null) {
			panel.appendAddressStats(addressStats);
			panel.addPaidoutClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					List<String> addresses = new ArrayList<String>();
					addresses.add(addressStats.getAddress());
					presenter.loadPaidout(addresses);
				}
			});
		}
	}

	@Override
	public void appendGlobalStats(GlobalStatsDTO stats) {
		if (globalChart != null) {
			globalChart.appendGlobalStats(stats);
		}
	}

	@Override
	public void appendTotalStats(AddressStatsDTO stats) {
		if (totalChart != null) {
			totalChart.appendAddressStats(stats);
			totalChart.addPaidoutClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					presenter.loadPaidout(new ArrayList<String>(addressCharts.keySet()));
				}
			});
		}
	}

	@Override
	public void redrawAddressCharts() {
		getAddressesContainer().forceLayout();

		if (addressCharts != null) {
			for (AddressChartPanel chart : addressCharts.values()) {
				chart.redraw();
			}
		}
	}

}
