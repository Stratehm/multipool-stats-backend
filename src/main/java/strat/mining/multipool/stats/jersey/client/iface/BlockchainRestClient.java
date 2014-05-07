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

import strat.mining.multipool.stats.jersey.model.blockchain.BlockChainSingleAddress;
import strat.mining.multipool.stats.jersey.model.blockchain.BlockHeight;

public interface BlockchainRestClient {

	/**
	 * Return a single address data with the last transactionNbLimit
	 * transactions.
	 * 
	 * @param bitcoinAddress
	 * @param transactionNbLimit
	 * @return
	 */
	public BlockChainSingleAddress getBlockChainSingleAddress(String bitcoinAddress, int transactionNbLimit);

	/**
	 * Return a single address data with the given number of transactions from
	 * the given offset.
	 * 
	 * @param bitcoinAddress
	 * @param transactionNbLimit
	 * @return
	 */
	public BlockChainSingleAddress getBlockChainSingleAddress(String bitcoinAddress, int nbTransactions, int offset);

	/**
	 * Return the block info for the block at the given height.
	 * 
	 * @param blockHeight
	 * @return
	 */
	public BlockHeight getBlockHeight(String blockHeight);

}
