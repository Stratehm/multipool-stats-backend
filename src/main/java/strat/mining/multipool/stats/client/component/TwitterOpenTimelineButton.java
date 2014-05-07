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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;

public class TwitterOpenTimelineButton implements IsWidget, HasClickHandlers {

	private HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);

	private HtmlLayoutContainer container;

	public interface HtmlLayoutContainerTemplate extends XTemplates {
		@XTemplate("<div class=\"twitterOpenTimelineWidget\"><div class=\"btn-o\"><i></i><span>{message}&nbsp;<b>{name}</b></span></div></div>")
		SafeHtml getTemplate(String message, String name);
	}

	public TwitterOpenTimelineButton(String message, String name) {
		container = new HtmlLayoutContainer(templates.getTemplate(message, name));
		container.addStyleName("textCenter");
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return container.addDomHandler(handler, ClickEvent.getType());
	}

}
