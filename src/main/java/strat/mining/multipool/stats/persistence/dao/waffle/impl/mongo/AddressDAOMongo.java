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
package strat.mining.multipool.stats.persistence.dao.waffle.impl.mongo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.persistence.dao.waffle.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.model.waffle.Address;

@Component(value = "waffleAddressDAOMongo")
public class AddressDAOMongo implements AddressDAO {

	private AtomicInteger addressId;

	@Resource(name = "waffleMongoTemplate")
	private MongoOperations mongoOperation;

	@PostConstruct
	private void initialize() {
		List<Address> addresses = mongoOperation.findAll(Address.class);
		int maxId = 0;
		if (CollectionUtils.isNotEmpty(addresses)) {
			for (Address address : addresses) {
				if (address.getId() > maxId) {
					maxId = address.getId();
				}
			}
		}
		addressId = new AtomicInteger(maxId + 1);
	}

	@Override
	public Address insertAddress(Address address) {
		address.setId(addressId.getAndIncrement());
		mongoOperation.save(address);
		return address;
	}

	@Override
	public List<Address> getAllAddresses() {
		return mongoOperation.findAll(Address.class);
	}

	@Override
	public Address getAddress(String address) {
		Query query = new Query(Criteria.where("address").is(address));
		return mongoOperation.findOne(query, Address.class);
	}

	@Override
	public void removeAddress(Address address) {
		mongoOperation.remove(address);
	}

}
