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
package strat.mining.multipool.stats.client.mvp.view.coinsolver.component;

import java.util.List;

import org.moxieapps.gwt.highcharts.client.Axis;
import org.moxieapps.gwt.highcharts.client.BaseChart;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Color;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.DataGrouping;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions.Stacking;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import strat.mining.multipool.stats.client.component.RefreshLabel;
import strat.mining.multipool.stats.dto.coinsolver.CoinInfoDTO;
import strat.mining.multipool.stats.dto.coinsolver.GlobalStatsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

public class GlobalChartPanel implements IsWidget {

	// The graph will shift all values when this delay is reached (7 days)
	private static final long DELAY_BEFORE_SHIFTING = 604800000L;

	private FramedPanel mainPanel;

	private RefreshLabel lastRefreshLabel;

	private VerticalLayoutContainer contentPanel;

	private HtmlLayoutContainer lastStatsPanel;

	private Chart btcChart;
	private Series balanceSeries;
	private Series unexchangedSeries;
	private Series immatureSeries;

	private Chart powerChart;
	private Series acceptedMHSeries;
	private Series nbMinersSeries;

	private boolean displaySummary = true;
	private boolean displayBTCChart = true;
	private boolean displayPowerChart = true;

	private List<GlobalStatsDTO> currentStats;

	public GlobalChartPanel(String title) {
		mainPanel = new FramedPanel();
		mainPanel.setHeadingText(title);
		mainPanel.setCollapsible(true);

		Anchor officialAnchor = new Anchor(false);
		officialAnchor.addStyleName("officialAnchor");
		officialAnchor.setText("Official");
		officialAnchor.setHref("http://coinsolver.com/poolstats.php");
		officialAnchor.setTarget("_blank");
		mainPanel.getHeader().addTool(officialAnchor);

		lastRefreshLabel = new RefreshLabel();
		mainPanel.getHeader().addTool(lastRefreshLabel);

		contentPanel = new VerticalLayoutContainer();
		contentPanel.addStyleName("whiteBackground");
		mainPanel.add(contentPanel);

		initBTCChart();
		initPowerChart();
	}

	private void initBTCChart() {
		btcChart = new Chart();
		btcChart.setChartTitleText("BTC");
		btcChart.setHeight(350);
		btcChart.setShadow(false);
		btcChart.setAnimation(false);
		btcChart.setZoomType(BaseChart.ZoomType.X);
		btcChart.setOption("/plotOptions/series/turboThreshold", 10);
		btcChart.setAreaPlotOptions(new AreaPlotOptions().setOption("turboThreshold", 10).setStacking(Stacking.NORMAL)
				.setMarker(new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));

		btcChart.setLinePlotOptions(new LinePlotOptions().setOption("turboThreshold", 10).setMarker(
				new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));

		btcChart.setSeriesPlotOptions(new SeriesPlotOptions().setDataGrouping(new DataGrouping().setEnabled(true)));

		btcChart.getXAxis().setType(Axis.Type.DATE_TIME);
		btcChart.getXAxis().setAxisTitleText("Date");

		btcChart.getYAxis(0).setType(Axis.Type.LINEAR);
		btcChart.getYAxis(0).setAxisTitleText("BTC");
		btcChart.getYAxis(0).setMinorTickIntervalAuto();
		btcChart.getYAxis(0).setGridLineWidth(2);

		unexchangedSeries = btcChart.createSeries();
		unexchangedSeries.setType(Series.Type.AREA);
		unexchangedSeries.setName("Unexchanged");
		unexchangedSeries.setStack("btc");
		unexchangedSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 55, 255, 0.5))
				.setFillOpacity(0.5));
		btcChart.addSeries(unexchangedSeries);

		immatureSeries = btcChart.createSeries();
		immatureSeries.setType(Series.Type.AREA);
		immatureSeries.setName("Immature");
		immatureSeries.setStack("btc");
		immatureSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 190, 0, 0.5))
				.setFillOpacity(0.5));
		btcChart.addSeries(immatureSeries);

		balanceSeries = btcChart.createSeries();
		balanceSeries.setType(Series.Type.LINE).setName("Balance");
		balanceSeries.setPlotOptions(new LinePlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor("red"));
		btcChart.addSeries(balanceSeries);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");
		btcChart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			public String format(ToolTipData toolTipData) {
				String result = "";
				GlobalStatsDTO stat = getStatsFromDate(toolTipData.getXAsLong());

				if (stat != null) {
					result = "<b>Balance:</b> " + formatBTCValue(stat.getTotalBalance());
					result += "<br/><b>Unexchanged:</b> " + formatBTCValue(stat.getTotalUnexchanged());
					result += "<br/><b>Immature:</b> " + formatBTCValue(stat.getTotalImmature());
					result += "<br/><b>Total:</b> " + formatBTCValue(stat.getTotalBalance() + stat.getTotalUnexchanged() + stat.getTotalImmature());
					result += "<br/><b>Date:</b> " + dtf.format(stat.getRefreshTime());
				}

				return result;
			}
		}).setFollowPointer(true).setShadow(false).setUseHTML(true));

		contentPanel.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						btcChart.setSizeToMatchContainer();
					}
				});
			}
		});

	}

	private void initPowerChart() {
		powerChart = new Chart();
		powerChart.setChartTitleText("Power");
		powerChart.setHeight(350);
		powerChart.setZoomType(BaseChart.ZoomType.X);
		powerChart.setShadow(false);
		powerChart.setAnimation(false);
		powerChart.setOption("/plotOptions/series/turboThreshold", 10);
		powerChart.setAreaPlotOptions(new AreaPlotOptions().setStacking(Stacking.NORMAL).setMarker(
				new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));
		powerChart.setLinePlotOptions(new LinePlotOptions().setStacking(Stacking.NORMAL).setMarker(
				new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));

		powerChart.setSeriesPlotOptions(new SeriesPlotOptions().setDataGrouping(new DataGrouping().setEnabled(true)));

		powerChart.getXAxis().setType(Axis.Type.DATE_TIME);
		powerChart.getXAxis().setAxisTitleText("Date");

		powerChart.getYAxis(0).setType(Axis.Type.LINEAR);
		powerChart.getYAxis(0).setAxisTitleText("MH/s");
		powerChart.getYAxis(0).setMinorTickIntervalAuto();
		powerChart.getYAxis(0).setGridLineWidth(2);

		powerChart.getYAxis(1).setType(Axis.Type.LINEAR);
		powerChart.getYAxis(1).setAxisTitleText("Workers");
		powerChart.getYAxis(1).setMinorTickIntervalAuto();
		powerChart.getYAxis(1).setGridLineWidth(2);
		powerChart.getYAxis(1).setMin(0);
		powerChart.getYAxis(1).setOpposite(true);

		acceptedMHSeries = powerChart.createSeries();
		acceptedMHSeries.setType(Series.Type.AREA);
		acceptedMHSeries.setName("MH/s");
		acceptedMHSeries.setStack("mhs");
		acceptedMHSeries.setYAxis(0);
		acceptedMHSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 170, 0, 0.5))
				.setFillOpacity(0.5));
		powerChart.addSeries(acceptedMHSeries);

		nbMinersSeries = powerChart.createSeries();
		nbMinersSeries.setType(Series.Type.LINE);
		nbMinersSeries.setName("Number of Workers");
		nbMinersSeries.setStack("workers");
		nbMinersSeries.setYAxis(1);
		nbMinersSeries.setPlotOptions(new LinePlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(170, 0, 0, 0.5)));
		powerChart.addSeries(nbMinersSeries);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");
		powerChart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			public String format(ToolTipData toolTipData) {
				String result = "";
				GlobalStatsDTO stat = getStatsFromDate(toolTipData.getXAsLong());

				if (stat != null) {
					result = "<b>MH/s:</b> " + formatPowerValue(stat.getTotalMegahashesPerSecond());
					if (stat.getMiningCoins() != null) {
						NumberFormat nf = NumberFormat.getFormat("#.##");
						for (CoinInfoDTO coin : stat.getMiningCoins()) {
							result += "<br/><b>" + coin.getFullname() + ":</b> " + formatPowerValue(coin.getPoolHashrate()) + " MH/s, "
									+ nf.format((coin.getPoolHashrate() / stat.getTotalMegahashesPerSecond() * 100)) + " %";
						}
					}
					if (stat.getNbMiners() != null) {
						result += "<br/><b>Workers:</b> " + stat.getNbMiners();
					}
					result += "<br/><b>Date:</b> " + dtf.format(stat.getRefreshTime());
				}

				return result;
			}
		}).setFollowPointer(true).setShadow(false).setUseHTML(true));

		contentPanel.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						powerChart.setSizeToMatchContainer();
					}
				});
			}
		});

	}

	@Override
	public Widget asWidget() {
		return mainPanel;
	}

	/**
	 * @param stats
	 */
	public void setGlobalStats(List<GlobalStatsDTO> stats) {
		lastRefreshLabel.resetTimer();

		currentStats = stats;

		for (GlobalStatsDTO stat : stats) {
			long time = stat.getRefreshTime().getTime();

			balanceSeries.addPoint(time, stat.getTotalBalance());
			unexchangedSeries.addPoint(time, stat.getTotalUnexchanged());
			immatureSeries.addPoint(time, stat.getTotalImmature());

			acceptedMHSeries.addPoint(time, stat.getTotalMegahashesPerSecond());

			if (stat.getNbMiners() != null) {
				nbMinersSeries.addPoint(time, stat.getNbMiners());
			}
		}

		GlobalStatsDTO lastStats = null;
		if (stats != null && stats.size() > 0) {
			lastStats = stats.get(stats.size() - 1);
		}

		createLastStatsPanel(lastStats);

		updateLastStatsDisplay();
		updateBTCChartDisplay();
		updatePowerChartDisplay();

	}

	private String formatBTCValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.########");
		return nf.format(value);
	}

	private String formatPowerValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.###");
		return nf.format(value);
	}

	public interface HtmlLayoutContainerTemplate extends XTemplates {
		@XTemplate("<table width=\"100%\" height=\"50px\"><tbody><tr><td class=\"label1\" /><td class=\"value1\" /><td class=\"label5\" /><td class=\"value5\" /></tr><tr><td class=\"label2\" /><td class=\"value2\" /><td class=\"label6\" /><td class=\"value6\" /></tr><tr><td class=\"label3\" /><td class=\"value3\" /><td class=\"label7\" /><td class=\"value7\" /></tr><tr><td class=\"label4\" /><td class=\"value4\" /><td class=\"label8\" /><td class=\"value8\" /></tr><tr><td class=\"coinLabel\" /><td class=\"coinValue\" colspan=\"3\"/></tr></tbody></table>")
		SafeHtml getTemplate();
	}

	public Widget createLastStatsPanel(GlobalStatsDTO lastStats) {
		HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);

		// Remove the last panel
		if (lastStatsPanel != null) {
			contentPanel.remove(lastStatsPanel);
		}

		lastStatsPanel = new HtmlLayoutContainer(templates.getTemplate());
		lastStatsPanel.setWidth(460);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");

		// Name labels
		Label balanceLabel = new Label("Balance: ");
		Label unexchangedLabel = new Label("Unexchanged: ");
		Label immatureLabel = new Label("Immature: ");
		Label totalLabel = new Label("Total: ");
		Label acceptedMegaHashLabel = new Label("MH/s: ");
		Label nbMinersLabel = new Label("Workers: ");
		Label lastUpdateTimeLabel = new Label("Last update: ");
		Label currentCoinsLabel = new Label("Current coins: ");

		// Values label
		Label balanceValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalBalance()) : "");
		Label unexchangedValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalUnexchanged()) : "");
		Label immatureValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalImmature()) : "");
		Label totalValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalBalance() + lastStats.getTotalUnexchanged()
				+ lastStats.getTotalImmature()) : "");
		Label acceptedMegaHashValue = new Label(lastStats != null ? formatPowerValue(lastStats.getTotalMegahashesPerSecond()) : "");
		Label nbMinersValue = new Label(lastStats != null && lastStats.getNbMiners() != null ? lastStats.getNbMiners().toString() : "");
		Label lastUpdateTimeValue = new Label(lastStats != null ? dtf.format(lastStats.getRefreshTime()) : "");
		HTML currentCoinsValue = new HTML("");

		if (lastStats.getMiningCoins() != null) {
			NumberFormat nf = NumberFormat.getFormat("#.##");
			String coinsValue = "<ul class=\"miningCoinList\">";
			for (CoinInfoDTO coin : lastStats.getMiningCoins()) {
				coinsValue += "<li><b>" + coin.getFullname() + ":</b> " + formatPowerValue(coin.getPoolHashrate()) + " MH/s, "
						+ nf.format((coin.getPoolHashrate() / lastStats.getTotalMegahashesPerSecond() * 100)) + " %</li>";
			}
			coinsValue += "</ul>";
			currentCoinsValue.setHTML(coinsValue);
		}

		lastStatsPanel.add(balanceLabel, new HtmlData(".label1"));
		lastStatsPanel.add(unexchangedLabel, new HtmlData(".label2"));
		lastStatsPanel.add(immatureLabel, new HtmlData(".label3"));
		lastStatsPanel.add(totalLabel, new HtmlData(".label4"));
		lastStatsPanel.add(acceptedMegaHashLabel, new HtmlData(".label5"));
		lastStatsPanel.add(nbMinersLabel, new HtmlData(".label6"));
		lastStatsPanel.add(lastUpdateTimeLabel, new HtmlData(".label7"));
		lastStatsPanel.add(currentCoinsLabel, new HtmlData(".coinLabel"));

		lastStatsPanel.add(balanceValue, new HtmlData(".value1"));
		lastStatsPanel.add(unexchangedValue, new HtmlData(".value2"));
		lastStatsPanel.add(immatureValue, new HtmlData(".value3"));
		lastStatsPanel.add(totalValue, new HtmlData(".value4"));
		lastStatsPanel.add(acceptedMegaHashValue, new HtmlData(".value5"));
		lastStatsPanel.add(nbMinersValue, new HtmlData(".value6"));
		lastStatsPanel.add(lastUpdateTimeValue, new HtmlData(".value7"));
		lastStatsPanel.add(currentCoinsValue, new HtmlData(".coinValue"));

		ToolTipConfig config = new ToolTipConfig();
		String tooltip = "<b>" + balanceLabel.getText() + "</b>" + balanceValue.getText();
		tooltip += "<br/><b>" + unexchangedLabel.getText() + "</b>" + unexchangedValue.getText();
		tooltip += "<br/><b>" + totalLabel.getText() + "</b>" + totalValue.getText();
		tooltip += "<br/><b>" + acceptedMegaHashLabel.getText() + "</b>" + acceptedMegaHashValue.getText();
		tooltip += "<br/><b>" + currentCoinsLabel.getText() + "</b>" + currentCoinsValue.getHTML();
		tooltip += "<br/><b>" + nbMinersLabel.getText() + "</b>" + nbMinersValue.getText();
		tooltip += "<br/><b>" + lastUpdateTimeLabel.getText() + "</b>" + lastUpdateTimeValue.getText();
		config.setBodyHtml(tooltip);
		config.setTrackMouse(true);
		config.setDismissDelay(0);
		config.setHideDelay(0);
		lastStatsPanel.setToolTipConfig(config);

		return lastStatsPanel;
	}

	public boolean isDisplaySummary() {
		return displaySummary;
	}

	public void setDisplaySummary(boolean displaySummary) {
		this.displaySummary = displaySummary;
		updateLastStatsDisplay();
	}

	public boolean isDisplayBTCChart() {
		return displayBTCChart;
	}

	public void setDisplayBTCChart(boolean displayBTCChart) {
		this.displayBTCChart = displayBTCChart;
		updateBTCChartDisplay();
	}

	public boolean isDisplayPowerChart() {
		return displayPowerChart;
	}

	public void setDisplayPowerChart(boolean displayPowerChart) {
		this.displayPowerChart = displayPowerChart;
		updatePowerChartDisplay();
	}

	private void updateLastStatsDisplay() {
		if (lastStatsPanel != null) {
			if (displaySummary) {
				contentPanel.insert(lastStatsPanel, 0, new VerticalLayoutData(460, 140));
			} else {
				contentPanel.remove(lastStatsPanel);
			}
		}
	}

	private void updateBTCChartDisplay() {
		if (btcChart != null) {
			if (displayBTCChart) {
				int index = 0;
				if (displaySummary) {
					index = 1;
				}
				contentPanel.insert(btcChart, index, new VerticalLayoutData(1, 380));
			} else {
				contentPanel.remove(btcChart);
			}
		}
	}

	private void updatePowerChartDisplay() {
		if (powerChart != null) {
			if (displayPowerChart) {
				int index = 0;
				if (displaySummary && displayBTCChart) {
					index = 2;
				} else if (displaySummary || displayBTCChart) {
					index = 1;
				}
				contentPanel.insert(powerChart, index, new VerticalLayoutData(1, 380));
			} else {
				contentPanel.remove(powerChart);
			}
		}
	}

	public void appendGlobalStats(GlobalStatsDTO stats) {
		lastRefreshLabel.resetTimer();
		Number maxDate = btcChart.getXAxis().getExtremes().getDataMax();
		Number minDate = btcChart.getXAxis().getExtremes().getDataMin();
		boolean hasToShift = stats.getRefreshTime().getTime() > minDate.longValue() + DELAY_BEFORE_SHIFTING;
		if (stats.getRefreshTime().getTime() > maxDate.longValue()) {
			currentStats.add(stats);
			balanceSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalBalance(), true, hasToShift, true);
			unexchangedSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalUnexchanged(), true, hasToShift, true);
			immatureSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalImmature(), true, hasToShift, true);
			acceptedMHSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalMegahashesPerSecond(), true, hasToShift, true);

			if (stats.getNbMiners() != null) {
				nbMinersSeries.addPoint(stats.getRefreshTime().getTime(), stats.getNbMiners(), true, hasToShift, true);
			}

			createLastStatsPanel(stats);
			updateLastStatsDisplay();
		}
	}

	private GlobalStatsDTO getStatsFromDate(long date) {
		GlobalStatsDTO result = null;
		for (GlobalStatsDTO stat : currentStats) {
			if (stat.getRefreshTime().getTime() == date) {
				result = stat;
				break;
			}
		}
		return result;
	}
}
