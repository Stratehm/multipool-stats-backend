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
import strat.mining.multipool.stats.dto.AddressDonationDetailsDTO;
import strat.mining.multipool.stats.dto.BlockchainAddressInfoDTO;
import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.ExchangePlaceDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CommonServicesImpl implements CommonServices {

	private ClientFactory clientFactory;

	private String serviceBaseUrl = GWT.getHostPageBaseURL() + "services/";

	public CommonServicesImpl(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void getExchangePlaces(final AsyncCallback<List<ExchangePlaceDTO>> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "currency/places");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						List<ExchangePlaceDTO> result = clientFactory.getDtoFactory().parseJsonToExchangePlaceList(response.getText());
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
	public void getCurrencyTicker(String exchangePlaceName, String currencyCode, final AsyncCallback<CurrencyTickerDTO> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "currency/" + exchangePlaceName + "/" + currencyCode);
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						CurrencyTickerDTO result = clientFactory.getDtoFactory().parseJsonToCurrencyTicker(response.getText());
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
	public void getBlockchainAddressInfo(String bitcoinAddress, final AsyncCallback<BlockchainAddressInfoDTO> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "blockchain/" + bitcoinAddress + "/blockchainInfo");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						BlockchainAddressInfoDTO result = clientFactory.getDtoFactory().parseJsonToBlockchainAddressInfo(response.getText());
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
	public void getDonationDetails(final AsyncCallback<DonationDetailsDTO> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "donation/details");
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						DonationDetailsDTO result = clientFactory.getDtoFactory().parseJsonToDonationDetails(response.getText());
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
	public void getDonationDetailsByAddress(String address, final AsyncCallback<AddressDonationDetailsDTO> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceBaseUrl + "donation/address/" + address);
		builder.setHeader("Accept", "application/json");
		builder.setHeader("Content-Type", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !"".equals(response.getText().trim()) && response.getStatusCode() == 200) {
						AddressDonationDetailsDTO result = clientFactory.getDtoFactory().parseJsonToAddressDonationDetails(response.getText());
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
