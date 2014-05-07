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
package strat.mining.multipool.stats.client.component;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class TwitterTimelineWindow implements IsWidget {

	private Window window;

	private TwitterTimelineWidget timelineWidget;

	private boolean fireHideEvent;

	private String href;
	private String dataWidgetId;

	public TwitterTimelineWindow(String title, String href, String dataWidgetId) {
		this.href = href;
		this.dataWidgetId = dataWidgetId;

		fireHideEvent = true;

		window = new Window();
		window.setHeadingText(title);
		window.setWidth(450);
		window.setHeight(490);
		window.setCollapsible(false);
		window.setResizable(true);
		window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				if (timelineWidget != null) {
					timelineWidget.setSize(event.getWidth(), event.getHeight());
				}
			}
		});

		window.show();
		window.center();
		loadTimeline();
	}

	private void loadTimeline() {
		if (timelineWidget == null) {
			timelineWidget = new TwitterTimelineWidget(href, dataWidgetId);
			window.setWidget(timelineWidget);
			timelineWidget.load();
		}
	}

	public void activate() {
		window.show();
		loadTimeline();
	}

	public void hide() {
		fireHideEvent = false;
		window.remove(0);
		timelineWidget = null;
		window.hide();
		fireHideEvent = true;
	}

	@Override
	public Widget asWidget() {
		return window;
	}

	public HandlerRegistration addHideHandler(final HideHandler handler) {
		return window.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if (fireHideEvent) {
					handler.onHide(event);
				}
			}
		});
	}
}
