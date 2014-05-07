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
package strat.mining.multipool.stats.client;

import org.moxieapps.gwt.highcharts.client.Global;
import org.moxieapps.gwt.highcharts.client.Highcharts;

import strat.mining.multipool.stats.client.component.Notification;
import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.factory.ClientFactoryImpl;
import strat.mining.multipool.stats.client.mvp.MainActivityMapper;
import strat.mining.multipool.stats.client.mvp.MainPlaceHistoryMapper;
import strat.mining.multipool.stats.client.place.MainPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class MultipoolStatsClientEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {

		Viewport mainPanel = new Viewport();

		RootPanel.get().add(mainPanel);

		Highcharts.setOptions(new Highcharts.Options().setGlobal(new Global().setUseUTC(false)));

		ClientFactory clientFactory = new ClientFactoryImpl();

		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();

		// Start ActivityManager
		ActivityMapper activityMapper = new MainActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(mainPanel);

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		MainPlaceHistoryMapper historyMapper = GWT.create(MainPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

		historyHandler.register(placeController, eventBus, new MainPlace());

		// Goes to the default place
		historyHandler.handleCurrentHistory();

		Notification.showNotification("Stratehm", "IMPORTANT: This service will shutdown in few days. See <a href=\"\">here</a> for more details.");

	}

}
