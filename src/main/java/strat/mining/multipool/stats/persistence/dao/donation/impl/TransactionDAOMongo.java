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
package strat.mining.multipool.stats.persistence.dao.donation.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.persistence.dao.donation.iface.TransactionDAO;
import strat.mining.multipool.stats.persistence.model.donation.Transaction;

@Component("donationTransactionDAOMongo")
public class TransactionDAOMongo implements TransactionDAO {

	@Resource(name = "donationMongoTemplate")
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
	public void deleteTransaction(String transactionId) {
		Query query = new Query(Criteria.where("transactionId").is(transactionId));
		mongoOperation.remove(query, Transaction.class);
	}

	@Override
	public void deleteTransactionBefore(Date date) {
		Query query = new Query(Criteria.where("date").lt(date));
		mongoOperation.remove(query, Transaction.class);
	}

	@Override
	public Transaction getTransaction(String transactionId) {
		Query query = new Query(Criteria.where("transactionId").is(transactionId));
		return mongoOperation.findOne(query, Transaction.class);
	}

	@Override
	public List<Transaction> getTransactionsBetween(Date startDate, Date endDate) {
		Query query = new Query(Criteria.where("date").lt(endDate).andOperator(Criteria.where("date").gt(startDate)));
		return mongoOperation.find(query, Transaction.class);
	}

	@Override
	public void updateTransaction(Transaction tx) {
		mongoOperation.save(tx);
	}

}
