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
package strat.mining.multipool.stats.client.mvp.view.middlecoin.component;

import java.util.Date;
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
import strat.mining.multipool.stats.client.mvp.model.middlecoin.GlobalAggregatedStats;
import strat.mining.multipool.stats.client.util.Pair;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
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
	private Series earningBTCByMHBalanceSeries;
	private Series earningBTCByMHTotalSeries;

	private Chart powerChart;
	private Series acceptedMHSeries;
	private Series rejectedMHSeries;

	private boolean displaySummary = true;
	private boolean displayBTCChart = true;
	private boolean displayPowerChart = true;

	private List<GlobalStatsDTO> currentStats;
	private GlobalAggregatedStats aggregatedStats;

	public GlobalChartPanel(String title) {
		mainPanel = new FramedPanel();
		mainPanel.setHeadingText(title);
		mainPanel.setCollapsible(true);

		Anchor officialAnchor = new Anchor(false);
		officialAnchor.addStyleName("officialAnchor");
		officialAnchor.setText("Official");
		officialAnchor.setHref("http://www.middlecoin.com");
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

		// btcChart.getYAxis(1).setType(Axis.Type.LINEAR);
		// btcChart.getYAxis(1).setAxisTitleText("BTC/Day/MH/s");
		// btcChart.getYAxis(1).setMinorTickIntervalAuto();
		// btcChart.getYAxis(1).setGridLineWidth(2);
		// btcChart.getYAxis(1).setOpposite(true);

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

		earningBTCByMHBalanceSeries = btcChart.createSeries();
		earningBTCByMHBalanceSeries.setType(Series.Type.COLUMN);
		earningBTCByMHBalanceSeries.setName("Balance earning by MH/s");
		earningBTCByMHBalanceSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2)
				.setColor(new Color(0, 170, 0, 0.5)).setFillOpacity(0.5));
		earningBTCByMHBalanceSeries.setYAxis(1);
		// btcChart.addSeries(earningBTCByMHBalanceSeries);

		earningBTCByMHTotalSeries = btcChart.createSeries();
		earningBTCByMHTotalSeries.setType(Series.Type.COLUMN);
		earningBTCByMHTotalSeries.setName("Total earning by MH/s");
		earningBTCByMHTotalSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 170, 0, 0.5))
				.setFillOpacity(0.5));
		earningBTCByMHTotalSeries.setYAxis(1);
		// btcChart.addSeries(earningBTCByMHTotalSeries);

		balanceSeries = btcChart.createSeries();
		balanceSeries.setType(Series.Type.LINE).setName("Balance");
		balanceSeries.setPlotOptions(new LinePlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor("red"));
		btcChart.addSeries(balanceSeries);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");
		btcChart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			public String format(ToolTipData toolTipData) {
				String result = "";
				GlobalStatsDTO stat = getStatsFromDate(toolTipData.getXAsLong());
				// Float earningBTCByMHBalance =
				// getEarningBTCByMHBalanceFromDate(toolTipData.getXAsLong());
				// Float earningBTCByMHTotal =
				// getEarningBTCByMHTotalFromDate(toolTipData.getXAsLong());

				if (stat != null) {
					result = "<b>Balance:</b> " + formatBTCValue(stat.getTotalBalance());
					result += "<br/><b>Unexchanged:</b> " + formatBTCValue(stat.getTotalUnexchangedBalance());
					result += "<br/><b>Immature:</b> " + formatBTCValue(stat.getTotalImmatureBalance());
					result += "<br/><b>Total:</b> "
							+ formatBTCValue(stat.getTotalBalance() + stat.getTotalUnexchangedBalance() + stat.getTotalImmatureBalance());
					result += "<br/><b>Paid out:</b> " + formatBTCValue(stat.getTotalPaidOut());
					// if (earningBTCByMHBalance != null) {
					// result += "<br/>Balance BTC/Day/MH/s: " +
					// formatBTCValue(earningBTCByMHBalance);
					// }
					// if (earningBTCByMHTotal != null) {
					// result += "<br/>Total BTC/Day/MH/s: " +
					// formatBTCValue(earningBTCByMHTotal);
					// }
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

		powerChart.setSeriesPlotOptions(new SeriesPlotOptions().setDataGrouping(new DataGrouping().setEnabled(true)));

		powerChart.getXAxis().setType(Axis.Type.DATE_TIME);
		powerChart.getXAxis().setAxisTitleText("Date");

		powerChart.getYAxis(0).setType(Axis.Type.LINEAR);
		powerChart.getYAxis(0).setAxisTitleText("MH/s");
		powerChart.getYAxis(0).setMinorTickIntervalAuto();
		powerChart.getYAxis(0).setGridLineWidth(2);

		acceptedMHSeries = powerChart.createSeries();
		acceptedMHSeries.setType(Series.Type.AREA);
		acceptedMHSeries.setName("Accepted MH/s");
		acceptedMHSeries.setStack("mhs");
		acceptedMHSeries.setYAxis(0);
		acceptedMHSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 170, 0, 0.5))
				.setFillOpacity(0.5));
		powerChart.addSeries(acceptedMHSeries);

		rejectedMHSeries = powerChart.createSeries();
		rejectedMHSeries.setType(Series.Type.AREA);
		rejectedMHSeries.setName("Rejected MH/s");
		rejectedMHSeries.setStack("mhs");
		rejectedMHSeries.setYAxis(0);
		rejectedMHSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(255, 0, 0, 0.5))
				.setFillOpacity(0.5));
		powerChart.addSeries(rejectedMHSeries);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");
		final NumberFormat nf = NumberFormat.getFormat("#.##");
		powerChart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			public String format(ToolTipData toolTipData) {
				String result = "";
				GlobalStatsDTO stat = getStatsFromDate(toolTipData.getXAsLong());

				if (stat != null) {
					result = "<b>Accepted MH/s:</b> " + formatPowerValue(stat.getTotalMegahashesPerSecond());
					result += "<br/><b>Rejected MH/s:</b> " + formatPowerValue(stat.getTotalRejectedMegahashesPerSecond());
					result += "<br/><b>Total MH/s:</b> "
							+ formatPowerValue(stat.getTotalMegahashesPerSecond() + stat.getTotalRejectedMegahashesPerSecond());
					result += "<br/><b>% of rejected shares:</b> "
							+ nf.format((((stat.getTotalRejectedMegahashesPerSecond()) * 100F) / (stat.getTotalMegahashesPerSecond() + stat
									.getTotalRejectedMegahashesPerSecond())));
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
	public void setGlobalStats(List<GlobalStatsDTO> stats, GlobalAggregatedStats aggregatedStats) {
		lastRefreshLabel.resetTimer();

		currentStats = stats;
		this.aggregatedStats = aggregatedStats;

		Number[][] balance = new Number[stats.size()][2];
		Number[][] unexchanged = new Number[stats.size()][2];
		Number[][] immature = new Number[stats.size()][2];
		Number[][] acceptedMH = new Number[stats.size()][2];
		Number[][] rejectedMH = new Number[stats.size()][2];

		Number[][] earningBTCByMHBalance = new Number[aggregatedStats.getEarningByMHBalance().size()][2];
		Number[][] earningBTCByMHTotal = new Number[aggregatedStats.getEarningByMHTotal().size()][2];

		int rowIndex = 0;
		for (GlobalStatsDTO stat : stats) {
			long time = stat.getRefreshTime().getTime();

			balance[rowIndex][0] = time;
			unexchanged[rowIndex][0] = time;
			immature[rowIndex][0] = time;
			acceptedMH[rowIndex][0] = time;
			rejectedMH[rowIndex][0] = time;

			balance[rowIndex][1] = stat.getTotalBalance();
			unexchanged[rowIndex][1] = stat.getTotalUnexchangedBalance();
			immature[rowIndex][1] = stat.getTotalImmatureBalance();
			acceptedMH[rowIndex][1] = stat.getTotalMegahashesPerSecond();
			rejectedMH[rowIndex][1] = stat.getTotalRejectedMegahashesPerSecond();

			rowIndex++;
		}

		rowIndex = 0;
		for (Pair<Date, Float> pair : aggregatedStats.getEarningByMHBalance()) {
			earningBTCByMHBalance[rowIndex][0] = pair.getLeft().getTime();
			earningBTCByMHBalance[rowIndex][1] = pair.getRight();
			rowIndex++;
		}

		rowIndex = 0;
		for (Pair<Date, Float> pair : aggregatedStats.getEarningByMHTotal()) {
			earningBTCByMHTotal[rowIndex][0] = pair.getLeft().getTime();
			earningBTCByMHTotal[rowIndex][1] = pair.getRight();
			rowIndex++;
		}

		balanceSeries.setPoints(balance);
		unexchangedSeries.setPoints(unexchanged);
		immatureSeries.setPoints(immature);
		earningBTCByMHBalanceSeries.setPoints(earningBTCByMHBalance);
		earningBTCByMHTotalSeries.setPoints(earningBTCByMHTotal);

		acceptedMHSeries.setPoints(acceptedMH);
		rejectedMHSeries.setPoints(rejectedMH);

		GlobalStatsDTO lastStats = null;
		if (stats != null && stats.size() > 0) {
			lastStats = stats.get(stats.size() - 1);
		}

		createLastStatsPanel(lastStats, aggregatedStats);

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
		@XTemplate("<table width=\"100%\" height=\"50px\"><tbody><tr><td class=\"label1\" /><td class=\"value1\" /><td class=\"label6\" /><td class=\"value6\" /><td class=\"label11\" /><td class=\"value11\" /></tr><tr><td class=\"label2\" /><td class=\"value2\" /><td class=\"label7\" /><td class=\"value7\" /><td class=\"label12\" /><td class=\"value12\" /></tr><tr><td class=\"label3\" /><td class=\"value3\" /><td class=\"label8\" /><td class=\"value8\" /><td class=\"label13\" /><td class=\"value13\" /></tr><tr><td class=\"label4\" /><td class=\"value4\" /><td class=\"label9\" /><td class=\"value9\" /><td class=\"label14\" /><td class=\"value14\" /></tr><tr><td class=\"label5\" /><td class=\"value5\" /><td class=\"label10\" /><td class=\"value10\" /><td class=\"label15\" /><td class=\"value15\" /></tr></tbody></table>")
		SafeHtml getTemplate();
	}

	public Widget createLastStatsPanel(GlobalStatsDTO lastStats, GlobalAggregatedStats aggregatedStats) {
		HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);

		// Remove the last panel
		if (lastStatsPanel != null) {
			contentPanel.remove(lastStatsPanel);
		}

		lastStatsPanel = new HtmlLayoutContainer(templates.getTemplate());
		lastStatsPanel.setWidth(650);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");

		// Name labels
		Label balanceLabel = new Label("Balance: ");
		Label unexchangedLabel = new Label("Unexchanged: ");
		Label immatureLabel = new Label("Immature: ");
		Label totalLabel = new Label("Total: ");
		Label paidoutLabel = new Label("Paid out: ");
		Label acceptedMegaHashLabel = new Label("Accepted MH/s: ");
		Label rejectedMegaHashLabel = new Label("Rejected MH/s: ");
		Label totalMegaHashLabel = new Label("Total MH/s: ");
		Label percentRejectedMegaHashLabel = new Label("% rejected MH/s: ");
		// Label balanceDailyEarningByMHLabel = new
		// Label("Balance BTC/Day/MH/s: ");
		// Label totalDailyEarningByMHLabel = new Label("Total BTC/Day/MH/s: ");
		Label lastUpdateTimeLabel = new Label("Last update: ");

		// Values calcul
		NumberFormat nf = NumberFormat.getFormat("#.##");
		String rejectedMHPercentValueString = "";
		if (lastStats != null) {
			rejectedMHPercentValueString = nf.format((((lastStats.getTotalRejectedMegahashesPerSecond()) * 100F) / (lastStats
					.getTotalRejectedMegahashesPerSecond() + lastStats.getTotalMegahashesPerSecond())));
		}

		// Values label
		Label balanceValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalBalance()) : "");
		Label unexchangedValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalUnexchangedBalance()) : "");
		Label immatureValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalImmatureBalance()) : "");
		Label paidoutValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalPaidOut()) : "");
		Label totalValue = new Label(lastStats != null ? formatBTCValue(lastStats.getTotalBalance() + lastStats.getTotalUnexchangedBalance()
				+ lastStats.getTotalImmatureBalance()) : "");
		Label acceptedMegaHashValue = new Label(lastStats != null ? formatPowerValue(lastStats.getTotalMegahashesPerSecond()) : "");
		Label rejectedMegaHashValue = new Label(lastStats != null ? formatPowerValue(lastStats.getTotalRejectedMegahashesPerSecond()) : "");
		Label totalMegaHashValue = new Label(
				lastStats != null ? formatPowerValue((lastStats.getTotalMegahashesPerSecond() + lastStats.getTotalRejectedMegahashesPerSecond()))
						: "");
		Label percentRejectedMegaHashValue = new Label(lastStats != null ? rejectedMHPercentValueString : "");
		// Label balanceDailyEarningByMHValue = new
		// Label(aggregatedStats.getEarningByMHBalance().size() > 0 ?
		// formatBTCValue(aggregatedStats
		// .getEarningByMHBalance().get(aggregatedStats.getEarningByMHBalance().size()
		// - 1).getRight()) : "");
		// Label totalDailyEarningByMHValue = new
		// Label(aggregatedStats.getEarningByMHTotal().size() > 0 ?
		// formatBTCValue(aggregatedStats
		// .getEarningByMHTotal().get(aggregatedStats.getEarningByMHTotal().size()
		// - 1).getRight()) : "");
		Label lastUpdateTimeValue = new Label(lastStats != null ? dtf.format(lastStats.getRefreshTime()) : "");

		lastStatsPanel.add(balanceLabel, new HtmlData(".label1"));
		lastStatsPanel.add(unexchangedLabel, new HtmlData(".label2"));
		lastStatsPanel.add(immatureLabel, new HtmlData(".label3"));
		lastStatsPanel.add(totalLabel, new HtmlData(".label4"));
		lastStatsPanel.add(paidoutLabel, new HtmlData(".label5"));
		lastStatsPanel.add(acceptedMegaHashLabel, new HtmlData(".label6"));
		lastStatsPanel.add(rejectedMegaHashLabel, new HtmlData(".label7"));
		lastStatsPanel.add(totalMegaHashLabel, new HtmlData(".label8"));
		lastStatsPanel.add(percentRejectedMegaHashLabel, new HtmlData(".label9"));
		lastStatsPanel.add(lastUpdateTimeLabel, new HtmlData(".label11"));
		// lastStatsPanel.add(balanceDailyEarningByMHLabel, new
		// HtmlData(".label11"));
		// lastStatsPanel.add(totalDailyEarningByMHLabel, new
		// HtmlData(".label12"));
		// lastStatsPanel.add(lastUpdateTimeLabel, new HtmlData(".label13"));

		lastStatsPanel.add(balanceValue, new HtmlData(".value1"));
		lastStatsPanel.add(unexchangedValue, new HtmlData(".value2"));
		lastStatsPanel.add(immatureValue, new HtmlData(".value3"));
		lastStatsPanel.add(totalValue, new HtmlData(".value4"));
		lastStatsPanel.add(paidoutValue, new HtmlData(".value5"));
		lastStatsPanel.add(acceptedMegaHashValue, new HtmlData(".value6"));
		lastStatsPanel.add(rejectedMegaHashValue, new HtmlData(".value7"));
		lastStatsPanel.add(totalMegaHashValue, new HtmlData(".value8"));
		lastStatsPanel.add(percentRejectedMegaHashValue, new HtmlData(".value9"));
		lastStatsPanel.add(lastUpdateTimeValue, new HtmlData(".value11"));
		// lastStatsPanel.add(balanceDailyEarningByMHValue, new
		// HtmlData(".value11"));
		// lastStatsPanel.add(totalDailyEarningByMHValue, new
		// HtmlData(".value12"));
		// lastStatsPanel.add(lastUpdateTimeValue, new HtmlData(".value13"));

		ToolTipConfig config = new ToolTipConfig();
		String tooltip = "<b>" + balanceLabel.getText() + "</b>" + balanceValue.getText();
		tooltip += "<br/><b>" + unexchangedLabel.getText() + "</b>" + unexchangedValue.getText();
		tooltip += "<br/><b>" + immatureLabel.getText() + "</b>" + immatureValue.getText();
		tooltip += "<br/><b>" + totalLabel.getText() + "</b>" + totalValue.getText();
		tooltip += "<br/><b>" + paidoutLabel.getText() + "</b>" + paidoutValue.getText();
		tooltip += "<br/><b>" + acceptedMegaHashLabel.getText() + "</b>" + acceptedMegaHashValue.getText();
		tooltip += "<br/><b>" + rejectedMegaHashLabel.getText() + "</b>" + rejectedMegaHashValue.getText();
		tooltip += "<br/><b>" + totalMegaHashLabel.getText() + "</b>" + totalMegaHashValue.getText();
		tooltip += "<br/><b>" + percentRejectedMegaHashLabel.getText() + "</b>" + percentRejectedMegaHashValue.getText();
		// tooltip += "<br/><b>" + balanceDailyEarningByMHLabel.getText() +
		// "</b>" + balanceDailyEarningByMHValue.getText();
		// tooltip += "<br/><b>" + totalDailyEarningByMHLabel.getText() + "</b>"
		// + totalDailyEarningByMHValue.getText();
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
				contentPanel.insert(lastStatsPanel, 0, new VerticalLayoutData(650, 100));
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

	public void appendGlobalStats(GlobalStatsDTO stats, GlobalAggregatedStats aggreagatedSats) {
		lastRefreshLabel.resetTimer();
		Number maxDate = btcChart.getXAxis().getExtremes().getDataMax();
		Number minDate = btcChart.getXAxis().getExtremes().getDataMin();
		boolean hasToShift = stats.getRefreshTime().getTime() > minDate.longValue() + DELAY_BEFORE_SHIFTING;
		if (stats.getRefreshTime().getTime() > maxDate.longValue()) {
			currentStats.add(stats);
			balanceSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalBalance(), true, hasToShift, true);
			unexchangedSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalUnexchangedBalance(), true, hasToShift, true);
			immatureSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalImmatureBalance(), true, hasToShift, true);
			acceptedMHSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalMegahashesPerSecond(), true, hasToShift, true);
			rejectedMHSeries.addPoint(stats.getRefreshTime().getTime(), stats.getTotalRejectedMegahashesPerSecond(), true, hasToShift, true);

			createLastStatsPanel(stats, aggreagatedSats);
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

	private Float getEarningBTCByMHTotalFromDate(long xAsLong) {
		Pair<Date, Float> result = aggregatedStats.getEarningByMHTotal().get(0);
		Pair<Date, Float> stat = null;
		for (int i = 0; i < aggregatedStats.getEarningByMHTotal().size(); i++) {
			stat = aggregatedStats.getEarningByMHTotal().get(i);
			// If the given date is after the current stat, then we take the
			// previous stat.
			// We are sure that it is the result until we find a stat that is
			// before the given date.
			if (stat.getLeft().getTime() < xAsLong) {
				break;
			} else {
				result = stat;
			}
		}
		return result != null ? result.getRight() : null;
	}

	private Float getEarningBTCByMHBalanceFromDate(long xAsLong) {
		Pair<Date, Float> result = aggregatedStats.getEarningByMHBalance().get(0);
		Pair<Date, Float> stat = null;
		for (int i = aggregatedStats.getEarningByMHBalance().size() - 1; i >= 0; i--) {
			stat = aggregatedStats.getEarningByMHBalance().get(i);
			// If the given date is after the current stat, then we take the
			// previous stat.
			// We are sure that it is the result until we find a stat that is
			// before the given date.
			if (stat.getLeft().getTime() < xAsLong) {
				break;
			} else {
				result = stat;
			}
		}
		return result != null ? result.getRight() : null;
	}
}
