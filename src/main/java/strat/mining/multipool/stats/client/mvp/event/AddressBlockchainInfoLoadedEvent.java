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

import strat.mining.multipool.stats.client.mvp.handler.AddressBlockchainInfoLoadHandler;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;

import com.google.gwt.event.shared.GwtEvent;

public class AddressBlockchainInfoLoadedEvent extends GwtEvent<AddressBlockchainInfoLoadHandler> {

	public static final GwtEvent.Type<AddressBlockchainInfoLoadHandler> TYPE = new GwtEvent.Type<AddressBlockchainInfoLoadHandler>();

	private String address;
	private BlockchainAddressInfoDTO blockchainInfo;

	public AddressBlockchainInfoLoadedEvent(String address, BlockchainAddressInfoDTO blockchainInfo) {
		this.blockchainInfo = blockchainInfo;
		this.address = address;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AddressBlockchainInfoLoadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddressBlockchainInfoLoadHandler handler) {
		handler.addressBlockchainInfoLoaded(this);
	}

	public String getAddress() {
		return address;
	}

	public BlockchainAddressInfoDTO getBlockchainInfo() {
		return blockchainInfo;
	}

}
