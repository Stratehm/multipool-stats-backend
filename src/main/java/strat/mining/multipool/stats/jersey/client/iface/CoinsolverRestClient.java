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
package strat.mining.multipool.stats.jersey.client.iface;

import java.util.List;

import strat.mining.multipool.stats.jersey.model.coinsolver.AddressPaidout;
import strat.mining.multipool.stats.jersey.model.coinsolver.AddressStats;
import strat.mining.multipool.stats.jersey.model.coinsolver.GlobalStats;

public interface CoinsolverRestClient {

	/**
	 * Retrieve the global stats from the coinsolver pool website.
	 * 
	 * @return
	 */
	public GlobalStats getGlobalStats();

	/**
	 * Return the last address stats for the given address
	 * 
	 * @param address
	 * @return
	 */
	public AddressStats getAddressStats(String address);

	/**
	 * Return the address paidout for the given address
	 * 
	 * @param address
	 * @return
	 */
	public List<AddressPaidout> getAddressPaidout(String address);

}
