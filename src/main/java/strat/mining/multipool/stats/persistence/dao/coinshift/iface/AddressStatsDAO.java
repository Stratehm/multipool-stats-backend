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
package strat.mining.multipool.stats.persistence.dao.coinshift.iface;

import java.util.Date;
import java.util.List;

import strat.mining.multipool.stats.persistence.model.coinshift.AddressStats;

public interface AddressStatsDAO {

	public void insertAddressStats(List<AddressStats> addressStats);

	public void insertAddressStats(AddressStats addressStats);

	public void deleteAddressStats(AddressStats addressStats);

	public int deleteAddressStatsBefore(Date time);

	public void deleteAddressStats(Integer idAddress);

	public void deleteAddressStatsBefore(Integer idAddress, Date time);

	public AddressStats getLastAddressStats(Integer idAddress);

	public List<AddressStats> getAddressStats(Integer idAddress);

	public List<AddressStats> getAddressStatsSince(Integer idAddress, Date time);

}
