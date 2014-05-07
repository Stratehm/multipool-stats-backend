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

import org.jsonmaker.gwt.client.JsonizerParser;

import strat.mining.multipool.stats.client.place.DescriptorContainer.DescriptorContainerJsonizer;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class MainPlace extends Place {

	private DescriptorContainer descriptorContainer;

	public MainPlace() {
		descriptorContainer = new DescriptorContainer();
	}

	public MiddlecoinPoolPlaceDescriptor getMiddlecoinDescriptor() {
		return descriptorContainer.getMiddlecoinDescriptor();
	}

	public void setMiddlecoinDescriptor(MiddlecoinPoolPlaceDescriptor middlecoinDescriptor) {
		this.descriptorContainer.setMiddlecoinDescriptor(middlecoinDescriptor);
	}

	public WafflePoolPlaceDescriptor getWaffleDescriptor() {
		return descriptorContainer.getWaffleDescriptor();
	}

	public void setWaffleDescriptor(WafflePoolPlaceDescriptor waffleDescriptor) {
		this.descriptorContainer.setWaffleDescriptor(waffleDescriptor);
	}

	public CoinshiftPoolPlaceDescriptor getCoinshiftDescriptor() {
		return descriptorContainer.getCoinshiftDescriptor();
	}

	public void setCoinshiftDescriptor(CoinshiftPoolPlaceDescriptor coinshiftDescriptor) {
		this.descriptorContainer.setCoinshiftDescriptor(coinshiftDescriptor);
	}

	public CoinsolverPoolPlaceDescriptor getCoinsolverDescriptor() {
		return descriptorContainer.getCoinsolverDescriptor();
	}

	public void setCoinsolverDescriptor(CoinsolverPoolPlaceDescriptor coinsolverDescriptor) {
		descriptorContainer.setCoinsolverDescriptor(coinsolverDescriptor);
	}

	/**
	 * The token is in URL encoded json
	 * 
	 * @author Strat
	 * 
	 */
	public static class Tokenizer implements PlaceTokenizer<MainPlace> {

		private static DescriptorContainerJsonizer descriptorContainerJsonizer = (DescriptorContainerJsonizer) GWT
				.create(DescriptorContainerJsonizer.class);

		@Override
		public MainPlace getPlace(String token) {
			MainPlace result = new MainPlace();
			result.descriptorContainer = (DescriptorContainer) JsonizerParser.parse(descriptorContainerJsonizer, URL.decode(token));
			return result;
		}

		@Override
		public String getToken(MainPlace place) {
			String json = descriptorContainerJsonizer.asString(place.descriptorContainer);
			return URL.encode(json);
		}
	}
}
