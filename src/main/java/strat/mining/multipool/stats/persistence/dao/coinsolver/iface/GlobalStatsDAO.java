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
package strat.mining.multipool.stats.persistence.dao.coinsolver.iface;

import java.util.Date;
import java.util.List;

import strat.mining.multipool.stats.persistence.model.coinsolver.GlobalStats;

public interface GlobalStatsDAO {

	public void insertGlobalStats(List<GlobalStats> globalStats);

	public void insertGlobalStats(GlobalStats globalStats);

	public void deleteGlobalStats(GlobalStats globalStats);

	public void deleteGlobalStats(Date time);

	public int deleteGlobalStatsBefore(Date time);

	public GlobalStats getLastGlobalStats();

	public GlobalStats getGlobalStats(Date time);

	public List<GlobalStats> getGlobalStatsSince(Date time);

	public List<GlobalStats> getAllGlobalStats();

}
