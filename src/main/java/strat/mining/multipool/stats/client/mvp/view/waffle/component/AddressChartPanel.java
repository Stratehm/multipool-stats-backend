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
package strat.mining.multipool.stats.client.mvp.view.waffle.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import strat.mining.multipool.stats.client.component.cell.HashrateCell;
import strat.mining.multipool.stats.client.component.cell.StalerateCell;
import strat.mining.multipool.stats.client.component.cell.WorkerNameCell;
import strat.mining.multipool.stats.dto.waffle.AddressStatsDTO;
import strat.mining.multipool.stats.dto.waffle.WorkerStatsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.Head;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

public class AddressChartPanel implements IsWidget {

	private static final Float WARNING_STALERATE_THRESHOLD = 10f;

	private static final Float WARNING_HASHRATE_THRESHOLD = 0.001f;

	// The graph will shift all values when this delay is reached (7 days)
	private static final long DELAY_BEFORE_SHIFTING = 604800000L;

	private static final WorkerStatsProperties propertyAccess = GWT.create(WorkerStatsProperties.class);

	public interface WorkerStatsProperties extends PropertyAccess<WorkerStatsDTO> {
		@Path("username")
		ModelKeyProvider<WorkerStatsDTO> key();

		ValueProvider<WorkerStatsDTO, String> username();

		ValueProvider<WorkerStatsDTO, Float> hashrate();

		ValueProvider<WorkerStatsDTO, Float> staleRate();

	}

	private FramedPanel mainPanel;

	private RefreshLabel lastRefreshLabel;

	private VerticalLayoutContainer contentPanel;

	private SimpleContainer lastStatsPanelContainer;

	private BorderLayoutContainer lastStatsPanel;

	private SimpleContainer tableContainer;
	private SimpleContainer gridContainer;

	private Chart btcChart;
	private Series balanceSeries;
	private Series unexchangedSeries;

	private Chart powerChart;
	private Series acceptedMHSeries;

	private Anchor paidoutAnchor;

	private boolean displaySummary = true;
	private boolean displayBTCChart = true;
	private boolean displayPowerChart = true;

	private List<AddressStatsDTO> currentStats;

	private Map<String, Series> workerSeries;

	private Grid<WorkerStatsDTO> workerGrid;
	private ColumnModel<WorkerStatsDTO> columnModel;
	private GridView<WorkerStatsDTO> gridView;
	private ListStore<WorkerStatsDTO> workerStore;

	public AddressChartPanel(String title, boolean isTotal) {
		mainPanel = new FramedPanel();
		mainPanel.setHeadingText(title);
		mainPanel.setCollapsible(true);

		workerSeries = new HashMap<String, Series>();

		if (!isTotal) {
			Anchor officialAnchor = new Anchor(false);
			officialAnchor.addStyleName("officialAnchor");
			officialAnchor.setText("Official");
			officialAnchor.setHref("http://wafflepool.com/miner/" + title);
			officialAnchor.setTarget("_blank");
			mainPanel.getHeader().addTool(officialAnchor);
		}

		lastRefreshLabel = new RefreshLabel();
		mainPanel.getHeader().addTool(lastRefreshLabel);

		contentPanel = new VerticalLayoutContainer();
		contentPanel.addStyleName("whiteBackground");
		mainPanel.add(contentPanel);

		lastStatsPanelContainer = new SimpleContainer();
		gridContainer = new SimpleContainer();
		gridContainer.addStyleName("whiteBackground");
		tableContainer = new SimpleContainer();
		tableContainer.addStyleName("whiteBackground");

		lastStatsPanel = new BorderLayoutContainer();
		lastStatsPanel.addStyleName("whiteBackground");
		lastStatsPanel.setWidget(tableContainer);

		BorderLayoutData data = new BorderLayoutData();
		data.setSize(400);
		lastStatsPanel.setEastWidget(gridContainer, data);

		lastStatsPanelContainer.setWidget(lastStatsPanel);

		if (!isTotal) {

			ColumnConfig<WorkerStatsDTO, String> nameColumn = new ColumnConfig<WorkerStatsDTO, String>(propertyAccess.username(), 250, "Worker name");
			nameColumn.setCell(new WorkerNameCell());
			nameColumn.setAlignment(HorizontalAlignmentConstant.startOf(Direction.DEFAULT));
			ColumnConfig<WorkerStatsDTO, Float> hashrateColumn = new ColumnConfig<WorkerStatsDTO, Float>(propertyAccess.hashrate(), 70, "MH/s");
			hashrateColumn.setCell(new HashrateCell(WARNING_HASHRATE_THRESHOLD));
			ColumnConfig<WorkerStatsDTO, Float> staleRateColumn = new ColumnConfig<WorkerStatsDTO, Float>(propertyAccess.staleRate(), 60, "Stale");
			staleRateColumn.setCell(new StalerateCell(WARNING_STALERATE_THRESHOLD));

			List<ColumnConfig<WorkerStatsDTO, ?>> columns = new ArrayList<ColumnConfig<WorkerStatsDTO, ?>>();
			columns.add(nameColumn);
			columns.add(hashrateColumn);
			columns.add(staleRateColumn);
			columnModel = new ColumnModel<WorkerStatsDTO>(columns);
			workerStore = new ListStore<WorkerStatsDTO>(propertyAccess.key());
			StoreSortInfo<WorkerStatsDTO> sortInfo = new StoreSortInfo<WorkerStatsDTO>(propertyAccess.staleRate(), SortDir.DESC);
			workerStore.addSortInfo(sortInfo);

			gridView = new GridView<WorkerStatsDTO>();
			workerGrid = new Grid<WorkerStatsDTO>(workerStore, columnModel, gridView);
			workerGrid.addViewReadyHandler(new ViewReadyHandler() {
				@SuppressWarnings("rawtypes")
				public void onViewReady(ViewReadyEvent event) {
					for (int i = 0; i < columnModel.getColumnCount(); i++) {
						final Head head = gridView.getHeader().getHead(i);
						head.getElement().getStyle().setHeight(25, Unit.PX);
						head.addDomHandler(new MouseOverHandler() {
							public void onMouseOver(MouseOverEvent event) {
								Scheduler.get().scheduleDeferred(new ScheduledCommand() {
									public void execute() {
										head.getElement().getStyle().setHeight(25, Unit.PX);
									}
								});
							}
						}, MouseOverEvent.getType());
					}
				}
			});

			gridContainer.setWidget(workerGrid);
		}

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
					result += "<br/><b>Total:</b> " + formatBTCValue(stat.getBalance() + stat.getUnexchanged());
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

		powerChart.setLinePlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false).setHoverState(
				new Marker().setEnabled(true).setRadius(5))));

		powerChart.setSeriesPlotOptions(new SeriesPlotOptions().setDataGrouping(new DataGrouping().setEnabled(true)));

		powerChart.getXAxis().setType(Axis.Type.DATE_TIME);
		powerChart.getXAxis().setAxisTitleText("Date");

		powerChart.getYAxis(0).setType(Axis.Type.LINEAR);
		powerChart.getYAxis(0).setAxisTitleText("Total");
		powerChart.getYAxis(0).setMinorTickIntervalAuto();
		powerChart.getYAxis(0).setGridLineWidth(2);

		powerChart.getYAxis(1).setType(Axis.Type.LINEAR);
		powerChart.getYAxis(1).setAxisTitleText("Shares");
		powerChart.getYAxis(1).setMinorTickIntervalAuto();
		powerChart.getYAxis(1).setGridLineWidth(2);
		powerChart.getYAxis(1).setOpposite(true);

		acceptedMHSeries = powerChart.createSeries();
		acceptedMHSeries.setType(Series.Type.AREA);
		acceptedMHSeries.setName("Total");
		acceptedMHSeries.setStack("mhs");
		acceptedMHSeries.setYAxis(0);
		acceptedMHSeries.setPlotOptions(new AreaPlotOptions().setShadow(false).setHoverStateLineWidth(2).setColor(new Color(0, 170, 0, 0.5))
				.setFillOpacity(0.5));
		powerChart.addSeries(acceptedMHSeries);

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");
		powerChart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			public String format(ToolTipData toolTipData) {
				String result = "";
				AddressStatsDTO stat = getStatsFromDate(toolTipData.getXAsLong());

				if (stat != null) {
					result = "<b>Total MH/s:</b> " + formatPowerValue(stat.getMegaHashesPerSeconds());
					if (stat.getWorkerStats() != null) {
						for (WorkerStatsDTO workerStats : stat.getWorkerStats()) {
							if (workerStats.getStaleRate() > WARNING_STALERATE_THRESHOLD || workerStats.getHashrate() < WARNING_HASHRATE_THRESHOLD) {
								result += "<span style=\"color: red;\">";
							}
							result += "<br/><b>" + formatUsername(workerStats.getUsername()) + "</b>: " + formatPowerValue(workerStats.getHashrate())
									+ "MH/s, " + workerStats.getStaleRate() + "% Staled";
							if (workerStats.getStaleRate() > WARNING_STALERATE_THRESHOLD || workerStats.getHashrate() < WARNING_HASHRATE_THRESHOLD) {
								result += "</span>";
							}
						}
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

	public void setAddressStats(List<AddressStatsDTO> stats) {
		lastRefreshLabel.resetTimer();

		currentStats = stats;

		for (AddressStatsDTO stat : stats) {
			long time = stat.getRefreshTime().getTime();

			balanceSeries.addPoint(time, stat.getBalance());
			unexchangedSeries.addPoint(time, stat.getUnexchanged());

			acceptedMHSeries.addPoint(time, stat.getMegaHashesPerSeconds());

			if (stat.getWorkerStats() != null && stat.getWorkerStats().size() > 0) {
				for (WorkerStatsDTO workerDto : stat.getWorkerStats()) {
					Series workerSerie = workerSeries.get(workerDto.getUsername());
					if (workerSerie == null) {
						workerSerie = createWorkerSerie(workerDto.getUsername());
						workerSeries.put(workerDto.getUsername(), workerSerie);
					}
					workerSerie.addPoint(time, workerDto.getHashrate());
				}
			}

		}

		AddressStatsDTO lastStats = null;
		if (stats != null && stats.size() > 0) {
			lastStats = stats.get(stats.size() - 1);
		}
		createLastStatsPanel(lastStats);

		updateLastStatsDisplay();
		updateBTCChartDisplay();
		updatePowerChartDisplay();
	}

	private Series createWorkerSerie(String username) {
		String name = formatUsername(username);
		Series workerSerie = powerChart.createSeries();
		workerSerie.setType(Series.Type.LINE).setName(new String(name.toCharArray()));
		workerSerie.setPlotOptions(new LinePlotOptions().setShadow(false).setHoverStateLineWidth(2)
				.setMarker(new Marker().setEnabled(false).setHoverState(new Marker().setEnabled(true).setRadius(5))));
		powerChart.addSeries(workerSerie);
		return workerSerie;
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
		@XTemplate("<table width=\"100%\" height=\"50px\"><tbody><tr><td class=\"label1\" /><td class=\"value1\" /><td class=\"label4\" /><td class=\"value4\" /></tr><tr><td class=\"label2\" /><td class=\"value2\" /><td class=\"label5\" /><td class=\"value5\" /></tr><tr><td class=\"label3\" /><td class=\"value3\" /><td class=\"label6\" /><td class=\"value6\" /></tr><tr><td class=\"warningLabel\"></td><td class=\"warningValue\" colspan=\"3\"></td></tr></tbody></table>")
		SafeHtml getTemplate();
	}

	public Widget createLastStatsPanel(AddressStatsDTO lastStats) {

		HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);

		HtmlLayoutContainer htmlLayout = new HtmlLayoutContainer(templates.getTemplate());

		final DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM HH:mm:ss");

		// Name labels
		Label balanceLabel = new Label("Balance: ");
		Label unexchangedLabel = new Label("Unexchanged: ");
		Label totalLabel = new Label("Total: ");
		Label paidoutLabel = new Label("Paid out: ");
		Label acceptedMegaHashLabel = new Label("Total MH/s: ");
		Label lastUpdateTimeLabel = new Label("Last update: ");
		Label warningLabel = new Label("Worker warning: ");

		// Values label
		Label balanceValue = new Label(lastStats != null ? formatBTCValue(lastStats.getBalance()) : "");
		Label unexchangedValue = new Label(lastStats != null ? formatBTCValue(lastStats.getUnexchanged()) : "");
		paidoutAnchor = new Anchor(lastStats != null ? formatBTCValue(lastStats.getPaidOut()) : "");
		Label totalValue = new Label(lastStats != null ? formatBTCValue(lastStats.getBalance() + lastStats.getUnexchanged()) : "");
		Label acceptedMegaHashValue = new Label(lastStats != null ? formatPowerValue(lastStats.getMegaHashesPerSeconds()) : "");
		Label lastUpdateTimeValue = new Label(lastStats != null ? dtf.format(lastStats.getRefreshTime()) : "");
		HTML warningValue = new HTML("<i style=\"margin-left: 15px;\">None</i>");
		warningValue.addStyleName("warningValue");

		htmlLayout.add(balanceLabel, new HtmlData(".label1"));
		htmlLayout.add(unexchangedLabel, new HtmlData(".label2"));
		htmlLayout.add(totalLabel, new HtmlData(".label3"));
		htmlLayout.add(paidoutLabel, new HtmlData(".label4"));
		htmlLayout.add(acceptedMegaHashLabel, new HtmlData(".label5"));
		htmlLayout.add(lastUpdateTimeLabel, new HtmlData(".label6"));
		htmlLayout.add(warningLabel, new HtmlData(".warningLabel"));

		htmlLayout.add(balanceValue, new HtmlData(".value1"));
		htmlLayout.add(unexchangedValue, new HtmlData(".value2"));
		htmlLayout.add(totalValue, new HtmlData(".value3"));
		htmlLayout.add(paidoutAnchor, new HtmlData(".value4"));
		htmlLayout.add(acceptedMegaHashValue, new HtmlData(".value5"));
		htmlLayout.add(lastUpdateTimeValue, new HtmlData(".value6"));
		htmlLayout.add(warningValue, new HtmlData(".warningValue"));

		tableContainer.setWidget(htmlLayout);

		workerStore.clear();
		if (lastStats.getWorkerStats() != null) {
			workerStore.addAll(lastStats.getWorkerStats());
		}
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			if (gridView.getHeader() != null) {
				@SuppressWarnings("rawtypes")
				final Head head = gridView.getHeader().getHead(i);
				head.getElement().getStyle().setHeight(25, Unit.PX);
				head.addDomHandler(new MouseOverHandler() {
					public void onMouseOver(MouseOverEvent event) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							public void execute() {
								head.getElement().getStyle().setHeight(25, Unit.PX);
							}
						});
					}
				}, MouseOverEvent.getType());
			}
		}

		ToolTipConfig config = new ToolTipConfig();
		String tooltip = "<b>" + balanceLabel.getText() + "</b>" + balanceValue.getText();
		tooltip += "<br/><b>" + unexchangedLabel.getText() + "</b>" + unexchangedValue.getText();
		tooltip += "<br/><b>" + totalLabel.getText() + "</b>" + totalValue.getText();
		tooltip += "<br/><b>" + paidoutLabel.getText() + "</b>" + paidoutAnchor.getText();
		tooltip += "<br/><b>" + acceptedMegaHashLabel.getText() + "</b>" + acceptedMegaHashValue.getText();
		tooltip += "<br/><b>" + lastUpdateTimeLabel.getText() + "</b>" + lastUpdateTimeValue.getText();
		if (lastStats.getWorkerStats() != null) {
			String warning = "<ul class=\"warningList\">";
			boolean isWarning = false;
			for (WorkerStatsDTO workerStats : lastStats.getWorkerStats()) {
				if (workerStats.getStaleRate() > WARNING_STALERATE_THRESHOLD || workerStats.getHashrate() < WARNING_HASHRATE_THRESHOLD) {
					tooltip += "<span style=\"color: red;\">";
				}
				tooltip += "<br/><b>" + formatUsername(workerStats.getUsername()) + ":</b> " + formatPowerValue(workerStats.getHashrate())
						+ " MH/s, " + workerStats.getStaleRate() + "% Staled";
				if (workerStats.getStaleRate() > WARNING_STALERATE_THRESHOLD || workerStats.getHashrate() < WARNING_HASHRATE_THRESHOLD) {
					tooltip += "</span>";

					warning += getWarning(workerStats);
					isWarning = true;
				}
			}
			warning += "</ul>";

			if (isWarning) {
				warningValue.setHTML(warning);
			}

		}
		config.setBodyHtml(tooltip);
		config.setTrackMouse(true);
		config.setDismissDelay(0);
		config.setHideDelay(0);
		htmlLayout.setToolTipConfig(config);

		lastStatsPanelContainer.setWidget(lastStatsPanel);

		return lastStatsPanel;
	}

	private String getWarning(WorkerStatsDTO workerStats) {
		String warning = "<li><span style=\"color: red;\">" + formatUsername(workerStats.getUsername()) + ": ";
		if (workerStats.getHashrate() < WARNING_HASHRATE_THRESHOLD) {
			warning += workerStats.getHashrate() + " MH/s";
		} else if (workerStats.getStaleRate() > WARNING_STALERATE_THRESHOLD) {
			warning += workerStats.getStaleRate() + " % of stales";
		}

		warning += "</span></li>";

		return warning;
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
		if (lastStatsPanelContainer != null) {
			if (displaySummary) {
				contentPanel.insert(lastStatsPanelContainer, 0, new VerticalLayoutData(850, 140));
				contentPanel.forceLayout();
			} else {
				contentPanel.remove(lastStatsPanelContainer);
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
			acceptedMHSeries.addPoint(addressStats.getRefreshTime().getTime(), addressStats.getMegaHashesPerSeconds(), true, hasToShift, true);

			if (addressStats.getWorkerStats() != null && addressStats.getWorkerStats().size() > 0) {
				for (WorkerStatsDTO workerDto : addressStats.getWorkerStats()) {
					Series workerSerie = workerSeries.get(workerDto.getUsername());
					if (workerSerie == null) {
						workerSerie = createWorkerSerie(workerDto.getUsername());
					}
					workerSerie.addPoint(addressStats.getRefreshTime().getTime(), workerDto.getHashrate());
				}
			}

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

	private String formatUsername(String rawName) {
		String[] usernameSplit = rawName.split("_");
		return (usernameSplit != null && usernameSplit.length > 1) ? usernameSplit[1] : rawName;
	}

	public void redraw() {
		btcChart.redraw();
		powerChart.redraw();
		lastStatsPanel.forceLayout();
		mainPanel.forceLayout();
	}

}
