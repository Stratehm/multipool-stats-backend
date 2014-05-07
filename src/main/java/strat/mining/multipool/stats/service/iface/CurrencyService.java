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
package strat.mining.multipool.stats.service.iface;

import java.util.List;

import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

public interface CurrencyService {

	/**
	 * Return the list of all exchange places supported.
	 * 
	 * @return
	 */
	public List<ExchangePlaceDTO> getExchangePlaces();

	/**
	 * Return the currency ticker for the given exchange place and currency.
	 * 
	 * @param currencyCode
	 * @return
	 */
	public CurrencyTickerDTO getCurrencyTicker(String exchangePlaceName, String currencyCode);

}
