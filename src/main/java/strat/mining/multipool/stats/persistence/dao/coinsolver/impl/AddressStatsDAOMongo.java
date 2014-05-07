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
package strat.mining.multipool.stats.persistence.dao.coinsolver.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.persistence.dao.coinsolver.iface.AddressStatsDAO;
import strat.mining.multipool.stats.persistence.model.coinsolver.AddressStats;

@Component("coinsolverAddressStatsDAOMongo")
public class AddressStatsDAOMongo implements AddressStatsDAO {

	@Resource(name = "coinsolverMongoTemplate")
	private MongoOperations mongoOperation;

	@Override
	public void insertAddressStats(List<AddressStats> addressStats) {
		for (AddressStats as : addressStats) {
			mongoOperation.save(as);
		}
	}

	@Override
	public void insertAddressStats(AddressStats addressStats) {
		mongoOperation.save(addressStats);
	}

	@Override
	public void deleteAddressStats(AddressStats addressStats) {
		mongoOperation.remove(addressStats);

	}

	@Override
	public int deleteAddressStatsBefore(Date time) {
		Query query = new Query(Criteria.where("refreshTime").lt(time));
		long result = mongoOperation.count(query, AddressStats.class);
		mongoOperation.remove(query, AddressStats.class);
		return (int) result;
	}

	@Override
	public void deleteAddressStats(Integer addressId) {
		Query query = new Query(Criteria.where("addressId").is(addressId));
		mongoOperation.remove(query, AddressStats.class);
	}

	@Override
	public void deleteAddressStatsBefore(Integer addressId, Date time) {
		Query query = new Query(Criteria.where("refreshTime").lt(time).and("addressId").is(addressId));
		mongoOperation.remove(query, AddressStats.class);

	}

	@Override
	public AddressStats getLastAddressStats(Integer addressId) {
		Query query = new Query(Criteria.where("addressId").is(addressId));
		query.with(new Sort(Sort.Direction.DESC, "refreshTime"));
		return mongoOperation.findOne(query, AddressStats.class);
	}

	@Override
	public List<AddressStats> getAddressStats(Integer addressId) {
		Query query = new Query(Criteria.where("addressId").is(addressId));
		query.with(new Sort(Sort.Direction.ASC, "refreshTime"));
		return mongoOperation.find(query, AddressStats.class);
	}

	@Override
	public List<AddressStats> getAddressStatsSince(Integer addressId, Date time) {
		Query query = new Query(Criteria.where("addressId").is(addressId).and("refreshTime").gte(time));
		query.with(new Sort(Sort.Direction.ASC, "refreshTime"));
		return mongoOperation.find(query, AddressStats.class);
	}

}
