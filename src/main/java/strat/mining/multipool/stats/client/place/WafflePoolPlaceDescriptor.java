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

import java.util.List;

import org.jsonmaker.gwt.client.Jsonizer;

public class WafflePoolPlaceDescriptor {

	public interface WafflePlaceDescriptorJsonizer extends Jsonizer {
	}

	private List<String> addresses;
	private boolean isGlobalCollapsed = false;
	private boolean isDisplaySummary = true;
	private boolean isDisplayBTC = true;
	private boolean isDisplayPower = true;

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

	public boolean isGlobalCollapsed() {
		return isGlobalCollapsed;
	}

	public void setGlobalCollapsed(boolean isGlobalCollapsed) {
		this.isGlobalCollapsed = isGlobalCollapsed;
	}

	public boolean isDisplaySummary() {
		return isDisplaySummary;
	}

	public void setDisplaySummary(boolean isDisplaySummary) {
		this.isDisplaySummary = isDisplaySummary;
	}

	public boolean isDisplayBTC() {
		return isDisplayBTC;
	}

	public void setDisplayBTC(boolean isDisplayBTC) {
		this.isDisplayBTC = isDisplayBTC;
	}

	public boolean isDisplayPower() {
		return isDisplayPower;
	}

	public void setDisplayPower(boolean isDisplayPower) {
		this.isDisplayPower = isDisplayPower;
	}
}
