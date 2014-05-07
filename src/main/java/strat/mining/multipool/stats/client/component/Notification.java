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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import strat.mining.multipool.stats.client.util.Pair;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class Notification {

	private static List<Pair<String, String>> alreadyClosedNotifications = new ArrayList<Pair<String, String>>();

	private static final int MAX_NOTIFICATION_OPENED = 5;

	private static final int BOTTOM_MARGIN = 170;
	private static final int RIGHT_MARGIN = 25;

	private static final int WIDTH = 250;
	private static final int HEIGHT = 140;

	private static LinkedList<NotificationWindow> openedNotifications = new LinkedList<NotificationWindow>();

	static {
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						updatePositions();
					}
				});
			}
		});
	}

	public static void showNotification(String title, String message) {

		if (!isAlreadyOpened(title, message) && !isAlreadyClosed(title, message)) {

			if (openedNotifications.size() >= MAX_NOTIFICATION_OPENED) {
				// Close the first notification
				openedNotifications.getFirst().hide();
			}

			final NotificationWindow notification = new NotificationWindow(title, message);
			Point position = getNextPosition();
			notification.setPosition(position.getX(), position.getY());

			notification.addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					openedNotifications.remove(notification);
					alreadyClosedNotifications.add(new Pair<String, String>(notification.getTitleText(), notification.getMessage()));
					updatePositions();
				}
			});

			openedNotifications.add(notification);

			notification.show();
		}

	}

	private static boolean isAlreadyClosed(String title, String message) {
		return alreadyClosedNotifications.contains(new Pair<String, String>(title, message));
	}

	private static void updatePositions() {
		int i = 0;
		for (NotificationWindow window : openedNotifications) {
			Point position = getPosition(i);
			window.setPosition(position.getX(), position.getY());
			i++;
		}
	}

	private static boolean isAlreadyOpened(String title, String message) {
		boolean alreadyOpened = false;
		for (NotificationWindow openedWindow : openedNotifications) {
			if (openedWindow.isSame(title, message)) {
				alreadyOpened = true;
				break;
			}
		}

		return alreadyOpened;
	}

	private static Point getNextPosition() {
		return getPosition(openedNotifications.size());
	}

	private static Point getPosition(int index) {
		Size s = XDOM.getViewportSize();
		int left = s.getWidth() - WIDTH - RIGHT_MARGIN;

		int top = s.getHeight() - index * HEIGHT - BOTTOM_MARGIN;

		return new Point(left, top);
	}

	private static class NotificationWindow extends Window {

		private String message;

		public NotificationWindow(String title, String message) {
			setClosable(true);
			setResizable(false);
			setModal(false);
			setHeadingText(title);
			setHeight(HEIGHT);
			setWidth(WIDTH);
			setDraggable(false);

			getHeader().setText(title);
			this.message = message;

			HTML html = new HTML(message);
			html.addStyleName("notification");
			html.addStyleName("whiteBackground");
			ScrollPanel scrollPanel = new ScrollPanel(html);
			scrollPanel.addStyleName("whiteBackground");

			setWidget(scrollPanel);
		}

		public String getMessage() {
			return message;
		}

		public String getTitleText() {
			return getHeader().getText();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NotificationWindow)) {
				return false;
			} else {
				NotificationWindow toCompare = (NotificationWindow) obj;
				if (toCompare.getTitleText() == null && getTitleText() == null || toCompare.getTitleText() != null && getTitleText() != null
						&& toCompare.getTitleText().equals(getTitleText())) {
					if (toCompare.getMessage().equals(message)) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean isSame(String title, String message) {
			if (title == null && getTitleText() == null || title != null && getTitleText() != null && title.equals(getTitleText())) {
				if (message.equals(getMessage())) {
					return true;
				}
			}
			return false;
		}

	}
}
