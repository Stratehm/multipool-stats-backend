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
package strat.mining.multipool.stats.jersey.model.waffle;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Worker_hashrates {
	private Number hashrate;
	private String str;
	private String username;
	private Number last_seen;
	private Number stalerate;

	public Number getHashrate() {
		return this.hashrate;
	}

	public void setHashrate(Number hashrate) {
		this.hashrate = hashrate;
	}

	public String getStr() {
		return this.str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Number getLast_seen() {
		return last_seen;
	}

	public void setLast_seen(Number last_seen) {
		this.last_seen = last_seen;
	}

	public Number getStalerate() {
		return stalerate;
	}

	public void setStalerate(Number stalerate) {
		this.stalerate = stalerate;
	}

}
