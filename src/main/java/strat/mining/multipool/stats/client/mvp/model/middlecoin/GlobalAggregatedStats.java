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
package strat.mining.multipool.stats.client.mvp.model.middlecoin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import strat.mining.multipool.stats.client.util.Pair;

/**
 * Contains all global data that are calculated.
 * 
 * @author Strat
 * 
 */
public class GlobalAggregatedStats {

	// Associate a date with the BTC/MH for the given day only for balance.
	private List<Pair<Date, Float>> earningByMHBalance;

	// Associate a date with the BTC/MH for the given day only for the total BTC
	// amount (balance, immature and unexchanged).
	private List<Pair<Date, Float>> earningByMHTotal;

	public List<Pair<Date, Float>> getEarningByMHBalance() {
		if (earningByMHBalance == null) {
			earningByMHBalance = new ArrayList<Pair<Date, Float>>();
		}
		return earningByMHBalance;
	}

	public List<Pair<Date, Float>> getEarningByMHTotal() {
		if (earningByMHTotal == null) {
			earningByMHTotal = new ArrayList<Pair<Date, Float>>();
		}
		return earningByMHTotal;
	}

}
