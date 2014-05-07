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
import org.moxieapps.gwt.highcharts.client.plotOptions.ColumnPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.DataGrouping;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions.Stacking;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import strat.mining.multipool.stats.client.component.RefreshLabel;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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

public class AddressChartPanel implements IsWidget {

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
	private Series rejectedMHSeries;
	private Series acceptedShareSeries;
	private Series rejectedShareSeries;

	private Anchor paidoutAnchor;

	private boolean displaySummary = true;
	private boolean displayBTCChart = true;
	private boolean displayPowerChart = true;

	private List<AddressStatsDTO> currentStats;

	public AddressChartPanel(String title, boolean isTotal) {
		mainPanel = new FramedPanel();
		mainPanel.setHeadingText(title);
		mainPanel.setCollapsible(true);

		if (!isTotal) {
			Anchor officialAnchor = new Anchor(false);
			officialAnchor.addStyleName("officialAnchor");
			officialAnchor.setText("Official");
			officialAnchor.setHref("http://www.middlecoin.com/reports/" + title + ".html");
			officialAnchor.setTarget("_blank");
			mainPanel.getHeader().addTool(officialAnchor);
		}

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
		btcChart.setAreaPlotOptions(new AreaPlotOptions().setStacking(Stacking.NORMAL).setMarker(
				new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));

		btcChart.setLinePlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false).setHoverState(
				new Marker().setEnabled(true).setRadius(5))));

		btcChart.setSeriesPlotOptions(new SeriesPlotOptions().setDataGrouping(new DataGrouping().setEnabled(true)));

		btcChart.getXAxis().setType(Axis.Type.DATE_TIME);
		btcChart.getXAxis().setAxisTitleText("Date");

		btcChart.getYAxis().setType(Axis.Type.LINEAR);
		btcChart.getYAxis().setAxisTitleText("BTC");
		btcChart.getYAxis().setMinorTickIntervalAuto();
		btcChart.getYAxis().setGridLineWidth(2);

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
		immatureSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 170, 0, 0.5))
				.setFillOpacity(0.5));
		btcChart.addSeries(immatureSeries);

		balanceSeries = btcChart.createSeries();
		balanceSeries.setType(Series.Type.LINE).setName("Balance");
		balanceSeries.setPlotOptions(new SeriesPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor("red"));
		btcChart.addSeries(balanceSeries);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");
		btcChart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			public String format(ToolTipData toolTipData) {
				String result = "";
				AddressStatsDTO stat = getStatsFromDate(toolTipData.getXAsLong());

				if (stat != null) {
					result = "<b>Balance:</b> " + formatBTCValue(stat.getBalance());
					result += "<br/><b>Unexchanged:</b> " + formatBTCValue(stat.getUnexchanged());
					result += "<br/><b>Immature:</b> " + formatBTCValue(stat.getImmature());
					result += "<br/><b>Total:</b> " + formatBTCValue(stat.getBalance() + stat.getUnexchanged() + stat.getImmature());
					result += "<br/><b>Paid out:</b> " + formatBTCValue(stat.getPaidOut());
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
		powerChart.setAnimation(false);
		powerChart.setShadow(false);
		powerChart.setOption("/plotOptions/series/turboThreshold", 10);
		powerChart.setAreaPlotOptions(new AreaPlotOptions().setStacking(Stacking.NORMAL).setMarker(
				new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));

		powerChart.setColumnPlotOptions(new ColumnPlotOptions().setStacking(Stacking.NORMAL).setMarker(
				new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));

		powerChart.setSeriesPlotOptions(new SeriesPlotOptions().setDataGrouping(new DataGrouping().setEnabled(true)));

		powerChart.getXAxis().setType(Axis.Type.DATE_TIME);
		powerChart.getXAxis().setAxisTitleText("Date");

		powerChart.getYAxis(0).setType(Axis.Type.LINEAR);
		powerChart.getYAxis(0).setAxisTitleText("MH/s");
		powerChart.getYAxis(0).setMinorTickIntervalAuto();
		powerChart.getYAxis(0).setGridLineWidth(2);

		powerChart.getYAxis(1).setType(Axis.Type.LINEAR);
		powerChart.getYAxis(1).setAxisTitleText("Shares");
		powerChart.getYAxis(1).setMinorTickIntervalAuto();
		powerChart.getYAxis(1).setGridLineWidth(2);
		powerChart.getYAxis(1).setOpposite(true);

		acceptedShareSeries = powerChart.createSeries();
		acceptedShareSeries.setType(Series.Type.COLUMN);
		acceptedShareSeries.setName("Accepted Shares");
		acceptedShareSeries.setStack("shares");
		acceptedShareSeries.setYAxis(1);
		acceptedShareSeries.setPlotOptions(new ColumnPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 170, 0, 0.5)));
		powerChart.addSeries(acceptedShareSeries);

		rejectedShareSeries = powerChart.createSeries();
		rejectedShareSeries.setType(Series.Type.COLUMN);
		rejectedShareSeries.setName("Rejected Shares");
		rejectedShareSeries.setStack("shares");
		rejectedShareSeries.setYAxis(1);
		rejectedShareSeries.setPlotOptions(new ColumnPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(255, 0, 0, 0.5)));
		powerChart.addSeries(rejectedShareSeries);

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
				AddressStatsDTO stat = getStatsFromDate(toolTipData.getXAsLong());

				if (stat != null) {
					result = "<b>Accepted MH/s:</b> " + formatPowerValue(stat.getMegaHashesPerSeconds());
					result += "<br/><b>Rejected MH/s:</b> " + formatPowerValue(stat.getRejectedMegaHashesPerSeconds());
					result += "<br/><b>Total MH/s: " + formatPowerValue(stat.getRejectedMegaHashesPerSeconds() + stat.getMegaHashesPerSeconds());
					result += "<br/><b>Accepted shares last hour:</b> " + stat.getLastHourShares();
					result += "<br/><b>Rejected shares last hour:</b> " + stat.getLastHourRejectedShares();
					result += "<br/><b>Total shares last hour:</b> " + (stat.getLastHourRejectedShares() + stat.getLastHourShares());
					result += "<br/><b>% of rejected shares:</b> "
							+ nf.format(((((float) stat.getLastHourRejectedShares()) * 100F) / ((float) stat.getLastHourRejectedShares() + (float) stat
									.getLastHourShares())));
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

	public void setAddressStats(List<AddressStatsDTO> stats) {
		lastRefreshLabel.resetTimer();

		currentStats = stats;

		Number[][] balance = new Number[stats.size()][2];
		Number[][] unexchanged = new Number[stats.size()][2];
		Number[][] immature = new Number[stats.size()][2];
		Number[][] acceptedMH = new Number[stats.size()][2];
		Number[][] rejectedMH = new Number[stats.size()][2];
		Number[][] acceptedShares = new Number[stats.size()][2];
		Number[][] rejectedShares = new Number[stats.size()][2];

		int rowIndex = 0;
		for (AddressStatsDTO stat : stats) {
			long time = stat.getRefreshTime().getTime();

			balance[rowIndex][0] = time;
			unexchanged[rowIndex][0] = time;
			immature[rowIndex][0] = time;
			acceptedMH[rowIndex][0] = time;
			rejectedMH[rowIndex][0] = time;
			acceptedShares[rowIndex][0] = time;
			rejectedShares[rowIndex][0] = time;

			balance[rowIndex][1] = stat.getBalance();
			unexchanged[rowIndex][1] = stat.getUnexchanged();
			immature[rowIndex][1] = stat.getImmature();
			acceptedMH[rowIndex][1] = stat.getMegaHashesPerSeconds();
			rejectedMH[rowIndex][1] = stat.getRejectedMegaHashesPerSeconds();
			acceptedShares[rowIndex][1] = stat.getLastHourShares();
			rejectedShares[rowIndex][1] = stat.getLastHourRejectedShares();

			rowIndex++;
		}

		balanceSeries.setPoints(balance);
		unexchangedSeries.setPoints(unexchanged);
		immatureSeries.setPoints(immature);

		acceptedMHSeries.setPoints(acceptedMH);
		rejectedMHSeries.setPoints(rejectedMH);
		acceptedShareSeries.setPoints(acceptedShares);
		rejectedShareSeries.setPoints(rejectedShares);

		AddressStatsDTO lastStats = null;
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
		NumberFormat nf = NumberFormat.getFormat("0.000");
		return nf.format(value);
	}

	public interface HtmlLayoutContainerTemplate extends XTemplates {
		@XTemplate("<table width=\"100%\" height=\"50px\"><tbody><tr><td class=\"label1\" /><td class=\"value1\" /><td class=\"label6\" /><td class=\"value6\" /><td class=\"label11\" /><td class=\"value11\" /><td class=\"label16\" /><td class=\"value16\" /></tr><tr><td class=\"label2\" /><td class=\"value2\" /><td class=\"label7\" /><td class=\"value7\" /><td class=\"label12\" /><td class=\"value12\" /><td class=\"label17\" /><td class=\"value17\" /></tr><tr><td class=\"label3\" /><td class=\"value3\" /><td class=\"label8\" /><td class=\"value8\" /><td class=\"label13\" /><td class=\"value13\" /><td class=\"label18\" /><td class=\"value18\" /></tr><tr><td class=\"label4\" /><td class=\"value4\" /><td class=\"label9\" /><td class=\"value9\" /><td class=\"label14\" /><td class=\"value14\" /><td class=\"label19\" /><td class=\"value19\" /></tr><tr><td class=\"label5\" /><td class=\"value5\" /><td class=\"label10\" /><td class=\"value10\" /><td class=\"label15\" /><td class=\"value15\" /><td class=\"label20\" /><td class=\"value20\" /></tr></tbody></table>")
		SafeHtml getTemplate();
	}

	public Widget createLastStatsPanel(AddressStatsDTO lastStats) {
		HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);

		// Remove the last panel
		if (lastStatsPanel != null) {
			contentPanel.remove(lastStatsPanel);
		}

		lastStatsPanel = new HtmlLayoutContainer(templates.getTemplate());
		lastStatsPanel.setWidth(900);

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
		Label acceptedSharesLabel = new Label("Accepted shares: ");
		Label rejectedSharesLabel = new Label("Rejected shares: ");
		Label totalSharesLabel = new Label("Total shares: ");
		Label percentRejectedSharesLabel = new Label("% of rejected shares: ");
		Label lastUpdateTimeLabel = new Label("Last update: ");

		// Values calcul
		NumberFormat nf = NumberFormat.getFormat("#.##");
		String rejectedMHPercentValueString = "";
		if (lastStats != null) {
			rejectedMHPercentValueString = nf.format((((lastStats.getRejectedMegaHashesPerSeconds()) * 100F) / (lastStats
					.getRejectedMegaHashesPerSeconds() + lastStats.getMegaHashesPerSeconds())));
		}
		String rejectedSharePercentValueString = "";
		if (lastStats != null) {
			rejectedSharePercentValueString = nf.format(((((float) lastStats.getLastHourRejectedShares()) * 100F) / ((float) lastStats
					.getLastHourRejectedShares() + (float) lastStats.getLastHourShares())));
		}

		// Values label
		Label balanceValue = new Label(lastStats != null ? formatBTCValue(lastStats.getBalance()) : "");
		Label unexchangedValue = new Label(lastStats != null ? formatBTCValue(lastStats.getUnexchanged()) : "");
		Label immatureValue = new Label(lastStats != null ? formatBTCValue(lastStats.getImmature()) : "");
		paidoutAnchor = new Anchor(lastStats != null ? formatBTCValue(lastStats.getPaidOut()) : "");
		Label totalValue = new Label(
				lastStats != null ? formatBTCValue(lastStats.getBalance() + lastStats.getUnexchanged() + lastStats.getImmature()) : "");
		Label acceptedMegaHashValue = new Label(lastStats != null ? formatPowerValue(lastStats.getMegaHashesPerSeconds()) : "");
		Label rejectedMegaHashValue = new Label(lastStats != null ? formatPowerValue(lastStats.getRejectedMegaHashesPerSeconds()) : "");
		Label totalMegaHashValue = new Label(
				lastStats != null ? formatPowerValue((lastStats.getMegaHashesPerSeconds() + lastStats.getRejectedMegaHashesPerSeconds())) : "");
		Label percentRejectedMegaHashValue = new Label(lastStats != null ? rejectedMHPercentValueString : "");
		Label acceptedSharesValue = new Label(lastStats != null ? lastStats.getLastHourShares().toString() : "");
		Label rejectedSharesValue = new Label(lastStats != null ? lastStats.getLastHourRejectedShares().toString() : "");
		Label totalSharesValue = new Label(lastStats != null ? Integer.toString((lastStats.getLastHourShares() + lastStats
				.getLastHourRejectedShares())) : "");
		Label percentRejectedSharesValue = new Label(rejectedSharePercentValueString);
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
		lastStatsPanel.add(acceptedSharesLabel, new HtmlData(".label11"));
		lastStatsPanel.add(rejectedSharesLabel, new HtmlData(".label12"));
		lastStatsPanel.add(totalSharesLabel, new HtmlData(".label13"));
		lastStatsPanel.add(percentRejectedSharesLabel, new HtmlData(".label14"));
		lastStatsPanel.add(lastUpdateTimeLabel, new HtmlData(".label16"));

		lastStatsPanel.add(balanceValue, new HtmlData(".value1"));
		lastStatsPanel.add(unexchangedValue, new HtmlData(".value2"));
		lastStatsPanel.add(immatureValue, new HtmlData(".value3"));
		lastStatsPanel.add(totalValue, new HtmlData(".value4"));
		lastStatsPanel.add(paidoutAnchor, new HtmlData(".value5"));
		lastStatsPanel.add(acceptedMegaHashValue, new HtmlData(".value6"));
		lastStatsPanel.add(rejectedMegaHashValue, new HtmlData(".value7"));
		lastStatsPanel.add(totalMegaHashValue, new HtmlData(".value8"));
		lastStatsPanel.add(percentRejectedMegaHashValue, new HtmlData(".value9"));
		lastStatsPanel.add(acceptedSharesValue, new HtmlData(".value11"));
		lastStatsPanel.add(rejectedSharesValue, new HtmlData(".value12"));
		lastStatsPanel.add(totalSharesValue, new HtmlData(".value13"));
		lastStatsPanel.add(percentRejectedSharesValue, new HtmlData(".value14"));
		lastStatsPanel.add(lastUpdateTimeValue, new HtmlData(".value16"));

		ToolTipConfig config = new ToolTipConfig();
		String tooltip = "<b>" + balanceLabel.getText() + "</b>" + balanceValue.getText();
		tooltip += "<br/><b>" + unexchangedLabel.getText() + "</b>" + unexchangedValue.getText();
		tooltip += "<br/><b>" + immatureLabel.getText() + "</b>" + immatureValue.getText();
		tooltip += "<br/><b>" + totalLabel.getText() + "</b>" + totalValue.getText();
		tooltip += "<br/><b>" + paidoutLabel.getText() + "</b>" + paidoutAnchor.getText();
		tooltip += "<br/><b>" + acceptedMegaHashLabel.getText() + "</b>" + acceptedMegaHashValue.getText();
		tooltip += "<br/><b>" + rejectedMegaHashLabel.getText() + "</b>" + rejectedMegaHashValue.getText();
		tooltip += "<br/><b>" + totalMegaHashLabel.getText() + "</b>" + totalMegaHashValue.getText();
		tooltip += "<br/><b>" + percentRejectedMegaHashLabel.getText() + "</b>" + percentRejectedMegaHashValue.getText();
		tooltip += "<br/><b>" + acceptedSharesLabel.getText() + "</b>" + acceptedSharesValue.getText();
		tooltip += "<br/><b>" + rejectedSharesLabel.getText() + "</b>" + rejectedSharesValue.getText();
		tooltip += "<br/><b>" + totalSharesLabel.getText() + "</b>" + totalSharesValue.getText();
		tooltip += "<br/><b>" + percentRejectedSharesLabel.getText() + "</b>" + percentRejectedSharesValue.getText();
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
				contentPanel.insert(lastStatsPanel, 0, new VerticalLayoutData(900, 100));
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

	public void appendAddressStats(AddressStatsDTO addressStats) {
		lastRefreshLabel.resetTimer();
		Number maxDate = btcChart.getXAxis().getExtremes().getDataMax();
		Number minDate = btcChart.getXAxis().getExtremes().getDataMin();
		boolean hasToShift = addressStats.getRefreshTime().getTime() > minDate.longValue() + DELAY_BEFORE_SHIFTING;
		if (addressStats.getRefreshTime().getTime() > maxDate.longValue()) {
			currentStats.add(addressStats);
			balanceSeries.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getBalance(), true, hasToShift, true);
			unexchangedSeries.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getUnexchanged(), true, hasToShift, true);
			immatureSeries.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getImmature(), true, hasToShift, true);
			acceptedMHSeries.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getMegaHashesPerSeconds(), true, hasToShift, true);
			rejectedMHSeries
					.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getRejectedMegaHashesPerSeconds(), true, hasToShift, true);
			acceptedShareSeries.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getLastHourShares(), true, hasToShift, true);
			rejectedShareSeries.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getLastHourRejectedShares(), true, hasToShift, true);

			createLastStatsPanel(addressStats);
			updateLastStatsDisplay();
		}
	}

	private AddressStatsDTO getStatsFromDate(long date) {
		AddressStatsDTO result = null;
		for (AddressStatsDTO stat : currentStats) {
			if (stat.getRefreshTime().getTime() == date) {
				result = stat;
				break;
			}
		}
		return result;
	}

	public HandlerRegistration addPaidoutClickHandler(ClickHandler clickHandler) {
		return paidoutAnchor.addClickHandler(clickHandler);
	}

	public void redraw() {
		btcChart.redraw();
		powerChart.redraw();
		mainPanel.forceLayout();
	}

}
