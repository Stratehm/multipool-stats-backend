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

import strat.mining.multipool.stats.jersey.model.coinshift.AddressStats;
import strat.mining.multipool.stats.jersey.model.coinshift.GlobalStats;

public interface CoinshiftRestClient {

	/**
	 * Retrieve the global stats from the shiftcoin pool website.
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

}
