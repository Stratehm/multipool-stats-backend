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
package strat.mining.multipool.stats.client.place;

import org.jsonmaker.gwt.client.Jsonizer;

public class DescriptorContainer {
	public interface DescriptorContainerJsonizer extends Jsonizer {
	}

	private MiddlecoinPoolPlaceDescriptor middlecoinDescriptor;
	private WafflePoolPlaceDescriptor waffleDescriptor;
	private CoinshiftPoolPlaceDescriptor coinshiftDescriptor;
	private CoinsolverPoolPlaceDescriptor coinsolverDescriptor;

	public MiddlecoinPoolPlaceDescriptor getMiddlecoinDescriptor() {
		return middlecoinDescriptor;
	}

	public void setMiddlecoinDescriptor(MiddlecoinPoolPlaceDescriptor middlecoinDescriptor) {
		this.middlecoinDescriptor = middlecoinDescriptor;
	}

	public WafflePoolPlaceDescriptor getWaffleDescriptor() {
		return waffleDescriptor;
	}

	public void setWaffleDescriptor(WafflePoolPlaceDescriptor waffleDescriptor) {
		this.waffleDescriptor = waffleDescriptor;
	}

	public CoinshiftPoolPlaceDescriptor getCoinshiftDescriptor() {
		return coinshiftDescriptor;
	}

	public void setCoinshiftDescriptor(CoinshiftPoolPlaceDescriptor coinshiftDescriptor) {
		this.coinshiftDescriptor = coinshiftDescriptor;
	}

	public CoinsolverPoolPlaceDescriptor getCoinsolverDescriptor() {
		return coinsolverDescriptor;
	}

	public void setCoinsolverDescriptor(CoinsolverPoolPlaceDescriptor coinsolverDescriptor) {
		this.coinsolverDescriptor = coinsolverDescriptor;
	}

}
