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

import strat.mining.multipool.stats.client.resources.ClientResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;

public class TwitterTimelineWidget implements IsWidget {

	private static int timelineCounter = 0;

	private HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);

	private HtmlLayoutContainer container;

	private boolean isLoaded = false;
	private Image loadingImage;

	private int counter;

	public interface HtmlLayoutContainerTemplate extends XTemplates {
		@XTemplate("<img /><a id=\"twitterTimelineContainer{timelineId}\" class=\"twitter-timeline\"  href=\"{href}\"  data-widget-id=\"{dataWidgetId}\"></a>")
		SafeHtml getTemplate(String timelineId, String href, String dataWidgetId);
	}

	public TwitterTimelineWidget(String href, String dataWidgetId) {
		counter = timelineCounter++;
		loadingImage = new Image();
		loadingImage.setUrl(ClientResources.INSTANCE.loading().getSafeUri());
		loadingImage.setHeight("32px");
		loadingImage.setWidth("32px");

		container = new HtmlLayoutContainer(templates.getTemplate(Integer.toString(counter), href, dataWidgetId));
		container.addStyleName("textCenter");
		container.add(loadingImage, new HtmlData(".twitter-timeline"));
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	public void load() {
		jsLoad();
	}

	public native void jsLoad() /*-{
								var counter = this.@strat.mining.multipool.stats.client.component.TwitterTimelineWidget::counter;
								var js, fjs = $doc.getElementById("twitterTimelineContainer" + counter), p = /^http:/
								.test($doc.location) ? 'http' : 'https';
								$doc.getElementById("twitter-wjs")
								js = $doc.createElement("script");
								js.id = "twitter-wjs";
								js.src = p + "://platform.twitter.com/widgets.js";
								fjs.parentNode.insertBefore(js, fjs.nextSibling);
								}-*/;

	private native void setHeight(int height) /*-{
												var counter = this.@strat.mining.multipool.stats.client.component.TwitterTimelineWidget::counter;
												var element = $doc.getElementById("twitterTimelineContainer" + counter);
												element.contentDocument.children[0].children[1].children[0].children[2].style.height = height
												+ "px";
												}-*/;

	public void setSize(int width, int height) {
		NodeList<Element> iframeNodes = container.getElement().getElementsByTagName("iframe");

		if (iframeNodes != null && iframeNodes.getLength() > 0) {
			int iFrameHeight = height - 30;
			iFrameHeight = iFrameHeight > 0 ? iFrameHeight : 0;

			iframeNodes.getItem(0).setAttribute("width", width + "px");
			iframeNodes.getItem(0).setAttribute("height", iFrameHeight + "px");

			int streamHeight = iFrameHeight - 87;
			streamHeight = streamHeight > 0 ? streamHeight : 0;

			setHeight(streamHeight);
		}
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return container.addDomHandler(handler, ClickEvent.getType());
	}
}
