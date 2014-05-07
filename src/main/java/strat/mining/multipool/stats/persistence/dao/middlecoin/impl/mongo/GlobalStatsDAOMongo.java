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
package strat.mining.multipool.stats.persistence.dao.middlecoin.impl.mongo;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.persistence.dao.middlecoin.iface.GlobalStatsDAO;
import strat.mining.multipool.stats.persistence.model.middlecoin.GlobalStats;

@Component("middlecoinGlobalStatsDAOMongo")
public class GlobalStatsDAOMongo implements GlobalStatsDAO {

	@Resource(name = "middlecoinMongoTemplate")
	private MongoOperations mongoOperation;

	@Override
	public void insertGlobalStats(List<GlobalStats> globalStats) {
		for (GlobalStats gs : globalStats) {
			mongoOperation.save(gs);
		}
	}

	@Override
	public void insertGlobalStats(GlobalStats globalStats) {
		mongoOperation.save(globalStats);
	}

	@Override
	public void deleteGlobalStats(GlobalStats globalStats) {
		mongoOperation.remove(globalStats);
	}

	@Override
	public void deleteGlobalStats(Date time) {
		Query query = new Query(Criteria.where("refreshTime").is(time));
		mongoOperation.remove(query, GlobalStats.class);
	}

	@Override
	public int deleteGlobalStatsBefore(Date time) {
		Query query = new Query(Criteria.where("refreshTime").lt(time));
		long result = mongoOperation.count(query, GlobalStats.class);
		mongoOperation.remove(query, GlobalStats.class);
		return (int) result;
	}

	@Override
	public GlobalStats getLastGlobalStats() {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, "refreshTime"));
		return mongoOperation.findOne(query, GlobalStats.class);
	}

	@Override
	public GlobalStats getGlobalStats(Date time) {
		Query query = new Query(Criteria.where("refreshTime").is(time));
		query.with(new Sort(Sort.Direction.ASC, "refreshTime"));
		return mongoOperation.findOne(query, GlobalStats.class);
	}

	@Override
	public List<GlobalStats> getGlobalStatsSince(Date time) {
		Query query = new Query(Criteria.where("refreshTime").gt(time));
		query.with(new Sort(Sort.Direction.ASC, "refreshTime"));
		return mongoOperation.find(query, GlobalStats.class);
	}

	@Override
	public List<GlobalStats> getAllGlobalStats() {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.ASC, "refreshTime"));
		return mongoOperation.find(query, GlobalStats.class);
	}

}
