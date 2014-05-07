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
package strat.mining.multipool.stats.client.mvp.model.waffle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.waffle.AddressStatsDTO;
import strat.mining.multipool.stats.dto.waffle.GlobalStatsDTO;

public class WaffleDataContainer {

	private List<String> addresses;

	private Map<String, List<AddressStatsDTO>> addressesStats;

	private List<AddressStatsDTO> totalStats;

	private List<GlobalStatsDTO> globalStats;

	private Map<String, List<AddressPaidoutDTO>> addressesPaidout;

	private Map<String, BlockchainAddressInfoDTO> blockChainInfo;
	private BlockchainAddressInfoDTO totalBlockchainInfo;

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		addressesStats = null;
		totalStats = null;
		blockChainInfo = null;
		addressesPaidout = null;
		this.addresses = addresses;
	}

	public Map<String, List<AddressStatsDTO>> getAddressesStats() {
		if (addressesStats == null) {
			addressesStats = new LinkedHashMap<String, List<AddressStatsDTO>>();
		}
		return addressesStats;
	}

	public List<AddressStatsDTO> getTotalStats() {
		if (totalStats == null) {
			totalStats = new ArrayList<AddressStatsDTO>();
		}
		return totalStats;
	}

	public Map<String, List<AddressPaidoutDTO>> getAddressesPaidout() {
		if (addressesPaidout == null) {
			addressesPaidout = new HashMap<String, List<AddressPaidoutDTO>>();
		}
		return addressesPaidout;
	}

	public Map<String, BlockchainAddressInfoDTO> getBlockChainInfo() {
		if (blockChainInfo == null) {
			blockChainInfo = new HashMap<String, BlockchainAddressInfoDTO>();
		}
		return blockChainInfo;
	}

	public List<GlobalStatsDTO> getGlobalStats() {
		if (globalStats == null) {
			globalStats = new ArrayList<GlobalStatsDTO>();
		}
		return globalStats;
	}

	public BlockchainAddressInfoDTO getTotalBlockchainInfo() {
		return totalBlockchainInfo;
	}

	public void setTotalBlockchainInfo(BlockchainAddressInfoDTO totalBlockchainInfo) {
		this.totalBlockchainInfo = totalBlockchainInfo;
	}

}
