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

import strat.mining.multipool.stats.client.mvp.handler.AddressPaidoutLoadHandler;

import com.google.gwt.event.shared.GwtEvent;

public class AddressPaidoutLoadedEvent<T> extends GwtEvent<AddressPaidoutLoadHandler> {

	public static final GwtEvent.Type<AddressPaidoutLoadHandler> TYPE = new GwtEvent.Type<AddressPaidoutLoadHandler>();

	private String address;
	private List<T> paidout;

	public AddressPaidoutLoadedEvent(String address, List<T> paidout) {
		this.paidout = paidout;
		this.address = address;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AddressPaidoutLoadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddressPaidoutLoadHandler handler) {
		handler.addressPaidoutLoaded(this);
	}

	public String getAddress() {
		return address;
	}

	public List<T> getPaidout() {
		return paidout;
	}

}
