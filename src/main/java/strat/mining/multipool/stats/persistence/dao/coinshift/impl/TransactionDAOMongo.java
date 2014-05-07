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
package strat.mining.multipool.stats.persistence.dao.coinshift.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.persistence.dao.coinshift.iface.TransactionDAO;
import strat.mining.multipool.stats.persistence.model.coinshift.Transaction;

@Component("coinshiftTransactionDAOMongo")
public class TransactionDAOMongo implements TransactionDAO {

	@Resource(name = "coinshiftMongoTemplate")
	private MongoOperations mongoOperation;

	@Override
	public void insertTransaction(Transaction tx) {
		mongoOperation.save(tx);
	}

	@Override
	public void deleteTransaction(Transaction tx) {
		mongoOperation.remove(tx);
	}

	@Override
	public void deleteTransaction(Integer addressId) {
		Query query = new Query(Criteria.where("addressId").is(addressId));
		mongoOperation.remove(query, Transaction.class);
	}

	@Override
	public List<Transaction> getAllTransactions(Integer addressId) {
		Query query = new Query(Criteria.where("addressId").is(addressId));
		query.with(new Sort(Sort.Direction.ASC, "date"));
		return mongoOperation.find(query, Transaction.class);
	}

	@Override
	public Transaction getLastTransaction(Integer addressId) {
		Query query = new Query(Criteria.where("addressId").is(addressId));
		query.with(new Sort(Sort.Direction.DESC, "date"));
		return mongoOperation.findOne(query, Transaction.class);
	}

}
