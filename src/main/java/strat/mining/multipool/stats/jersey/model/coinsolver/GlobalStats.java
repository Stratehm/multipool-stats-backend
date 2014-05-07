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
package strat.mining.multipool.stats.jersey.model.coinsolver;

import java.util.List;

public class GlobalStats{
   	private String balance;
   	private List<Currently_mining> currently_mining;
   	private Number immature;
   	private String pool_hashrate;
   	private String pool_workers;
   	private String shares;
   	private Number unexchanged;

 	public String getBalance(){
		return this.balance;
	}
	public void setBalance(String balance){
		this.balance = balance;
	}
 	public List<Currently_mining> getCurrently_mining(){
		return this.currently_mining;
	}
	public void setCurrently_mining(List<Currently_mining> currently_mining){
		this.currently_mining = currently_mining;
	}
 	public Number getImmature(){
		return this.immature;
	}
	public void setImmature(Number immature){
		this.immature = immature;
	}
 	public String getPool_hashrate(){
		return this.pool_hashrate;
	}
	public void setPool_hashrate(String pool_hashrate){
		this.pool_hashrate = pool_hashrate;
	}
 	public String getPool_workers(){
		return this.pool_workers;
	}
	public void setPool_workers(String pool_workers){
		this.pool_workers = pool_workers;
	}
 	public String getShares(){
		return this.shares;
	}
	public void setShares(String shares){
		this.shares = shares;
	}
 	public Number getUnexchanged(){
		return this.unexchanged;
	}
	public void setUnexchanged(Number unexchanged){
		this.unexchanged = unexchanged;
	}
}
