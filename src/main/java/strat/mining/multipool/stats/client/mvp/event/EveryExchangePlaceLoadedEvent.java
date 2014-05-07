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
package strat.mining.multipool.stats.client.mvp.event;

import java.util.List;

import strat.mining.multipool.stats.client.mvp.handler.EveryExchangePlaceLoadHandler;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

import com.google.gwt.event.shared.GwtEvent;

public class EveryExchangePlaceLoadedEvent extends GwtEvent<EveryExchangePlaceLoadHandler> {

	public static final GwtEvent.Type<EveryExchangePlaceLoadHandler> TYPE = new GwtEvent.Type<EveryExchangePlaceLoadHandler>();

	private List<ExchangePlaceDTO> exchangePlaces;

	public EveryExchangePlaceLoadedEvent(List<ExchangePlaceDTO> exchangePlaces) {
		this.exchangePlaces = exchangePlaces;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EveryExchangePlaceLoadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EveryExchangePlaceLoadHandler handler) {
		handler.everyExchangePlaceLoaded(this);
	}

	public List<ExchangePlaceDTO> getExchangePlaces() {
		return exchangePlaces;
	}

}
