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
package strat.mining.multipool.stats.service.iface.place;

import java.util.List;

import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

public interface ExchangePlaceProvider {

	/**
	 * Return the info of this exchange place.
	 * 
	 * @return
	 */
	public ExchangePlaceDTO getExchangePlaceInfo();

	/**
	 * Return the ticker of this exchange place for the given currency
	 * 
	 * @return
	 */
	public CurrencyTickerDTO getTicker(String currencyCode);

	/**
	 * Return the ticker of all supported currency of this exchange place
	 * 
	 * @return
	 */
	public List<CurrencyTickerDTO> getTickers();

}
