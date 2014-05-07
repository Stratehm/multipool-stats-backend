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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moxieapps.gwt.highcharts.client.Axis;
import org.moxieapps.gwt.highcharts.client.BaseChart;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions.Stacking;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import strat.mining.multipool.stats.client.component.cell.BlockChainCell;
import strat.mining.multipool.stats.client.component.cell.DateCell;
import strat.mining.multipool.stats.client.mvp.view.middlecoin.PaidoutView;
import strat.mining.multipool.stats.client.resources.ClientResources;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.grid.AggregationNumberSummaryRenderer;
import com.sencha.gxt.widget.core.client.grid.AggregationRowConfig;
import com.sencha.gxt.widget.core.client.grid.AggregationSafeHtmlRenderer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.Head;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.SummaryType.SumSummaryType;

public class PaidoutViewImpl implements PaidoutView {

	private static final AddressPaidoutProperties propertyAccess = GWT.create(AddressPaidoutProperties.class);

	private Window window;

	private BorderLayoutContainer mainContainer;

	private VerticalLayoutContainer chartsContainer;
	private Chart paidoutChart;

	private Grid<AddressPaidoutDTO> paidoutGrid;
	private ListStore<AddressPaidoutDTO> paidoutStore;

	private Image loadingImage;

	private static final DateTimeFormat dateNormalizer = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

	private int nbSeries = 0;

	private Map<String, List<AddressPaidoutDTO>> paidoutByAddress;

	private boolean fireHideEvent;

	private boolean saveExpanded;

	public interface AddressPaidoutProperties extends PropertyAccess<AddressPaidoutDTO> {
		@Path("transactionId")
		ModelKeyProvider<AddressPaidoutDTO> key();

		ValueProvider<AddressPaidoutDTO, Date> time();

		ValueProvider<AddressPaidoutDTO, Float> amount();

		ValueProvider<AddressPaidoutDTO, String> transactionId();
	}

	public PaidoutViewImpl(String title) {
		paidoutByAddress = new HashMap<String, List<AddressPaidoutDTO>>();
		this.fireHideEvent = true;

		window = new Window();
		window.setHeaderVisible(true);
		window.setHeadingText(title);
		window.setHeight(600);
		window.setWidth(550);
		window.setMaximizable(true);
		window.setCollapsible(true);
		window.setResizable(true);

		window.addMaximizeHandler(new MaximizeHandler() {
			public void onMaximize(MaximizeEvent event) {
				window.expand();
			}
		});

		mainContainer = new BorderLayoutContainer();

		chartsContainer = new VerticalLayoutContainer();

		paidoutChart = new Chart();
		paidoutChart.setChartTitleText("Paidouts");
		paidoutChart.setZoomType(BaseChart.ZoomType.X);

		paidoutChart.setSeriesPlotOptions(new SeriesPlotOptions().setMarker(new Marker().setEnabled(false).setHoverState(
				new Marker().setEnabled(true).setRadius(5))));

		paidoutChart.getXAxis().setType(Axis.Type.DATE_TIME).setAxisTitleText("Date");

		paidoutChart.getYAxis(0).setType(Axis.Type.LINEAR).setAxisTitleText("BTC (Daily)");
		paidoutChart.getYAxis(0).setMinorTickIntervalAuto();
		paidoutChart.getYAxis(0).setGridLineWidth(2);

		paidoutChart.getYAxis(1).setType(Axis.Type.LINEAR).setAxisTitleText("BTC (Total)");
		paidoutChart.getYAxis(1).setOpposite(true);
		paidoutChart.getYAxis(1).setMin(0);
		paidoutChart.getYAxis(1).setMinorTickIntervalAuto();
		paidoutChart.getYAxis(1).setGridLineWidth(2);

		final DateTimeFormat dtf = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
		paidoutChart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			public String format(ToolTipData toolTipData) {
				int nbSeries = toolTipData.getPointsLength();

				String result = "<b>" + dtf.format(new Date(toolTipData.getXAsLong())) + "</b>";

				for (int i = 0; i < nbSeries; i++) {
					result += "<br/><b>" + toolTipData.getSeriesName(i).split(":")[0] + ":</b> " + formatBTCValue(toolTipData.getYAsDouble(i));
				}
				result += "<br/><b>Day total:</b> " + formatBTCValue(toolTipData.getTotal(0));
				result += "<br/><b>Total paidout:</b> " + formatBTCValue(toolTipData.getTotal(1));

				return result;
			}
		}).setShared(true).setFollowPointer(true));

		chartsContainer.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						paidoutChart.setSizeToMatchContainer();
					}
				});
			}
		});

		paidoutStore = new ListStore<AddressPaidoutDTO>(propertyAccess.key());

		ColumnConfig<AddressPaidoutDTO, Date> dateColumn = new ColumnConfig<AddressPaidoutDTO, Date>(propertyAccess.time(), 50, "Date");
		dateColumn.setWidth(125);
		dateColumn.setCell(new DateCell());
		ColumnConfig<AddressPaidoutDTO, String> transactionIdColumn = new ColumnConfig<AddressPaidoutDTO, String>(propertyAccess.transactionId(),
				100, "Transaction Id");
		transactionIdColumn.setWidth(250);
		transactionIdColumn.setCell(new BlockChainCell());
		ColumnConfig<AddressPaidoutDTO, Float> amountColumn = new ColumnConfig<AddressPaidoutDTO, Float>(propertyAccess.amount(), 75, "Amount");
		amountColumn.setWidth(100);

		List<ColumnConfig<AddressPaidoutDTO, ?>> columns = new ArrayList<ColumnConfig<AddressPaidoutDTO, ?>>();
		columns.add(dateColumn);
		columns.add(transactionIdColumn);
		columns.add(amountColumn);

		final ColumnModel<AddressPaidoutDTO> columnModel = new ColumnModel<AddressPaidoutDTO>(columns);

		AggregationRowConfig<AddressPaidoutDTO> total = new AggregationRowConfig<AddressPaidoutDTO>();
		total.setRenderer(transactionIdColumn, new AggregationSafeHtmlRenderer<AddressPaidoutDTO>("Total"));
		total.setRenderer(amountColumn, new AggregationNumberSummaryRenderer<AddressPaidoutDTO, Float>(NumberFormat.getFormat("#.##########"),
				new SumSummaryType<Float>()));
		columnModel.addAggregationRow(total);

		final GridView<AddressPaidoutDTO> gridView = new GridView<AddressPaidoutDTO>();

		paidoutGrid = new Grid<AddressPaidoutDTO>(paidoutStore, columnModel, gridView);
		paidoutGrid.addStyleName("paidoutGrid");
		paidoutGrid.addViewReadyHandler(new ViewReadyHandler() {
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

		loadingImage = new Image();
		loadingImage.setUrl(ClientResources.INSTANCE.loading().getSafeUri());
		loadingImage.setHeight("32px");
		loadingImage.setWidth("32px");
		FlowLayoutContainer simpleContainer = new FlowLayoutContainer();
		simpleContainer.addStyleName("textCenter");
		simpleContainer.add(loadingImage);
		window.setWidget(simpleContainer);

		chartsContainer.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						paidoutChart.setSizeToMatchContainer();
					}
				});
			}
		});

		chartsContainer.add(paidoutChart, new VerticalLayoutData(1, 370));

		BorderLayoutData layoutData = new BorderLayoutData();
		layoutData.setMaxSize(2000);
		layoutData.setSize(370);
		layoutData.setCollapsible(true);
		layoutData.setSplit(true);
		mainContainer.setNorthWidget(chartsContainer, layoutData);
		mainContainer.add(paidoutGrid);

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

	@Override
	public void addAddressPaidout(String address, List<AddressPaidoutDTO> paidoutDTO) {
		if (paidoutStore.size() < 1) {
			window.setWidget(mainContainer);
			window.forceLayout();
		}

		Series seriesDailyPaidout = paidoutChart.createSeries();
		seriesDailyPaidout.setType(Series.Type.COLUMN);
		seriesDailyPaidout.setName(nbSeries++ + ": Day paidout for " + address);
		seriesDailyPaidout.setPlotOptions(new AreaPlotOptions().setZIndex(nbSeries).setStacking(Stacking.NORMAL));
		seriesDailyPaidout.setStack("daily");
		seriesDailyPaidout.setYAxis(0);

		Series seriesTotalPaidout = paidoutChart.createSeries();
		seriesTotalPaidout.setType(Series.Type.AREA);
		seriesTotalPaidout.setName(nbSeries++ + ": Total paidout for " + address);
		seriesTotalPaidout.setStack("total");
		seriesTotalPaidout.setPlotOptions(new AreaPlotOptions().setFillOpacity(0.1).setZIndex(nbSeries * 50).setStacking(Stacking.NORMAL));
		seriesTotalPaidout.setYAxis(1);

		Float totalPaidout = 0F;
		if (paidoutDTO != null) {
			paidoutByAddress.put(address, paidoutDTO);

			// Merge the paidout by date. If several payout at the same day,
			// make one payout for all
			Map<Date, AddressPaidoutDTO> paidoutByDate = new HashMap<Date, AddressPaidoutDTO>();
			for (AddressPaidoutDTO dto : paidoutDTO) {
				Date date = dateNormalizer.parse(dateNormalizer.format(dto.getTime()));
				AddressPaidoutDTO paidout = paidoutByDate.get(date);

				if (paidout == null) {
					paidout = new AddressPaidoutDTO();
					paidout.setAmount(dto.getAmount());
					paidout.setTime(date);
					paidoutByDate.put(date, paidout);
				} else {
					paidout.setAmount(paidout.getAmount() + dto.getAmount());
				}
			}

			// Sort the new paidout by date.
			List<AddressPaidoutDTO> mergedPaidout = new ArrayList<AddressPaidoutDTO>(paidoutByDate.values());
			Collections.sort(mergedPaidout, new Comparator<AddressPaidoutDTO>() {
				public int compare(AddressPaidoutDTO o1, AddressPaidoutDTO o2) {
					return o1.getTime().compareTo(o2.getTime());
				}
			});

			// Then add these daily paidout to the graph
			for (AddressPaidoutDTO dto : mergedPaidout) {
				totalPaidout += dto.getAmount();

				seriesDailyPaidout.addPoint(dto.getTime().getTime(), dto.getAmount());
				seriesTotalPaidout.addPoint(dto.getTime().getTime(), totalPaidout);
			}
			paidoutChart.addSeries(seriesDailyPaidout);
			paidoutChart.addSeries(seriesTotalPaidout);

			// remove the fake dtos (without transaction id). Add only the real
			// dtos to the list
			for (AddressPaidoutDTO dto : paidoutDTO) {
				if (dto.getTransactionId() != null) {
					paidoutStore.add(dto);
				}
			}

			paidoutStore.addSortInfo(new StoreSortInfo<AddressPaidoutDTO>(propertyAccess.time(), SortDir.DESC));
		}
		paidoutChart.redraw();
		paidoutChart.setSizeToMatchContainer();
	}

	private String formatBTCValue(Number value) {
		NumberFormat nf = NumberFormat.getFormat("#.#########");
		return nf.format(value);
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
