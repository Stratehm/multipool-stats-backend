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
package strat.mining.multipool.stats.client.services.rest;

import java.util.List;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionRequestDTO;
import strat.mining.multipool.stats.dto.middlecoin.AddressStatsDTO;
import strat.mining.multipool.stats.dto.middlecoin.GlobalStatsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Services call implemented with RequestBuilder and JSON gwt-jsonmaker library.
 * 
 * @author Strat
 * 
 */
public class MiddlecoinServicesImpl implements MiddlecoinServices {

	private ClientFactory clientFactory;

	private String serviceBaseUrl = GWT.getHostPageBaseURL() + "services/";

	public MiddlecoinServicesImpl(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void getAllGlobalStats(final AsyncCallback<List<GlobalStatsDTO>> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "middlecoin/stats/global/all");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						List<GlobalStatsDTO> result = clientFactory.getDtoFactory().parseJsonToMiddlecoinGlobalStatsList(response.getText());
						callback.onSuccess(result);
					} else {
						callback.onFailure(new Exception("Response with error code " + response.getStatusCode() + "\n" + response.getText()));
					}
				}

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

	@Override
	public void getLastGlobalStats(final AsyncCallback<GlobalStatsDTO> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "middlecoin/stats/global/last");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						GlobalStatsDTO result = clientFactory.getDtoFactory().parseJsonToMiddlecoinGlobalStats(response.getText());
						callback.onSuccess(result);
					} else {
						callback.onFailure(new Exception("Response with error code " + response.getStatusCode() + "\n" + response.getText()));
					}
				}

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

	@Override
	public void getLastAddressStats(String address, final AsyncCallback<AddressStatsDTO> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "middlecoin/stats/" + address + "/last");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						AddressStatsDTO result = clientFactory.getDtoFactory().parseJsonToMiddlecoinAddressStats(response.getText());
						callback.onSuccess(result);
					} else {
						callback.onFailure(new Exception("Response with error code " + response.getStatusCode() + "\n" + response.getText()));
					}
				}

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

	@Override
	public void getAllAddressStats(String address, final AsyncCallback<List<AddressStatsDTO>> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "middlecoin/stats/" + address + "/all");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						List<AddressStatsDTO> result = clientFactory.getDtoFactory().parseJsonToMiddlecoinAddressStatsList(response.getText());
						callback.onSuccess(result);
					} else {
						callback.onFailure(new Exception("Response with error code " + response.getStatusCode() + "\n" + response.getText()));
					}
				}

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

	@Override
	public void getAllAddressPaidout(String address, final AsyncCallback<List<AddressPaidoutDTO>> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "middlecoin/stats/" + address + "/paidout");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						List<AddressPaidoutDTO> result = clientFactory.getDtoFactory().parseJsonToAddressPaidoutList(response.getText());
						callback.onSuccess(result);
					} else {
						callback.onFailure(new Exception("Response with error code " + response.getStatusCode() + "\n" + response.getText()));
					}
				}

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

	@Override
	public void getSuggestions(final AddressSuggestionRequestDTO request, final AsyncCallback<AddressSuggestionDTO> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, serviceBaseUrl + "middlecoin/suggest/address");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(clientFactory.getDtoFactory().parseAddressSuggestionRequestToJson(request), new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						AddressSuggestionDTO result = clientFactory.getDtoFactory().parseJsonToAddressSuggestion(response.getText());
						callback.onSuccess(result);
					} else {
						callback.onFailure(new Exception("Response with error code " + response.getStatusCode() + "\n" + response.getText()));
					}
				}

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

}
