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

import java.util.Map;

import strat.mining.multipool.stats.client.mvp.handler.EveryLastAddressStatsLoadHandler;

import com.google.gwt.event.shared.GwtEvent;

public class EveryLastAddressStatsLoadedEvent<T> extends GwtEvent<EveryLastAddressStatsLoadHandler> {

	public static final GwtEvent.Type<EveryLastAddressStatsLoadHandler> TYPE = new GwtEvent.Type<EveryLastAddressStatsLoadHandler>();

	private Map<String, T> stats;

	public EveryLastAddressStatsLoadedEvent(Map<String, T> stats) {
		this.stats = stats;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EveryLastAddressStatsLoadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EveryLastAddressStatsLoadHandler handler) {
		handler.everyLastAddressStatsLoaded(this);
	}

	public Map<String, T> getStats() {
		return stats;
	}

}
