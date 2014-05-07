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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.tips.Tip;

public class ImageTip extends Tip {

	public enum Anchor {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

	private ImageResourceRenderer renderer;

	private ImageResource image;

	private int showDelay;

	private int bottomOffset;
	private int topOffset;
	private int rightOffset;
	private int leftOffset;

	private int x, y;

	private Anchor anchor;

	private Timer showTimer;

	public ImageTip(Widget target, ImageResource image) {
		this.image = image;
		this.renderer = new ImageResourceRenderer();
		this.showDelay = 0;
		this.bottomOffset = 0;
		this.anchor = Anchor.BOTTOM_LEFT;

		target.addDomHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				startShowTimer();
			}

		}, MouseOverEvent.getType());

		target.addDomHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				stopShowTimer();
			}

		}, MouseOutEvent.getType());

		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						if (isVisible()) {
							updateXY();
							show();
						}
					}
				});
			}
		});
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
		updateXY();
	}

	private void startShowTimer() {
		if (showDelay > 0) {
			stopShowTimer();

			showTimer = new Timer() {
				public void run() {
					show();
				}
			};
			showTimer.schedule(showDelay);
		}
	}

	private void stopShowTimer() {
		if (showTimer != null) {
			showTimer.cancel();
			showTimer = null;
		}
	}

	@Override
	public void show() {
		updateXY();
		showAt(x, y);
	}

	protected void updateXY() {
		Size s = XDOM.getViewportSize();

		switch (anchor) {
		case BOTTOM_LEFT:
			x = leftOffset;
			y = s.getHeight() - image.getHeight() + bottomOffset;
			break;
		case BOTTOM_RIGHT:
			x = s.getWidth() - image.getWidth() + rightOffset;
			y = s.getHeight() - image.getHeight() + bottomOffset;
			break;
		case TOP_LEFT:
			x = leftOffset;
			y = topOffset;
			break;
		case TOP_RIGHT:
			x = s.getWidth() - image.getWidth() + rightOffset;
			y = topOffset;
			break;

		default:
			break;
		}
	}

	@Override
	protected void updateContent() {
		appearance.updateContent(getElement(), renderer.render(image).asString(), "");
	}

	public int getShowDelay() {
		return showDelay;
	}

	public void setShowDelay(int showDelay) {
		this.showDelay = showDelay;
	}

	public int getBottomOffset() {
		return bottomOffset;
	}

	public void setBottomOffset(int bottomOffset) {
		this.bottomOffset = bottomOffset;
		updateXY();
	}

	public int getTopOffset() {
		return topOffset;
	}

	public void setTopOffset(int topOffset) {
		this.topOffset = topOffset;
		updateXY();
	}

	public int getRightOffset() {
		return rightOffset;
	}

	public void setRightOffset(int rightOffset) {
		this.rightOffset = rightOffset;
		updateXY();
	}

	public int getLeftOffset() {
		return leftOffset;
	}

	public void setLeftOffset(int leftOffset) {
		this.leftOffset = leftOffset;
		updateXY();
	}

}
