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
package strat.mining.multipool.stats.client.mvp.presenter.coinshift;

import java.util.ArrayList;
import java.util.List;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.dto.AddressSuggestionDTO;
import strat.mining.multipool.stats.dto.AddressSuggestionRequestDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

public class AddressSuggestOracle extends SuggestOracle {

	private ClientFactory clientFactory;

	public AddressSuggestOracle(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void requestSuggestions(final Request request, final Callback callback) {
		AddressSuggestionRequestDTO requestDTO = new AddressSuggestionRequestDTO();
		requestDTO.setAddressPattern(request.getQuery());
		requestDTO.setLimit(request.getLimit());

		clientFactory.getCoinshiftServices().getSuggestions(requestDTO, new AsyncCallback<AddressSuggestionDTO>() {
			public void onSuccess(AddressSuggestionDTO result) {
				Response response = new Response();
				response.setMoreSuggestions(result.isHasMoreSuggestion());
				response.setMoreSuggestionsCount(result.getMoreSuggestionCount());

				List<Suggestion> suggestions = new ArrayList<Suggestion>();
				if (result.getSuggestions() != null) {
					for (String address : result.getSuggestions()) {
						SuggestionImpl suggestion = new SuggestionImpl();
						suggestion.setValue(address);
						suggestion.setMatchPattern(request.getQuery());
						suggestions.add(suggestion);
					}
				}

				response.setSuggestions(suggestions);
				callback.onSuggestionsReady(request, response);
			}

			public void onFailure(Throwable caught) {
				// Do nothing
			}
		});
	}

	@Override
	public boolean isDisplayStringHTML() {
		return true;
	}

	private class SuggestionImpl implements Suggestion {

		private String value;

		private String matchPattern;

		private String displayString;

		public void setValue(String value) {
			this.value = value;
			updateDisplayString();
		}

		public void setMatchPattern(String pattern) {
			this.matchPattern = pattern;
			updateDisplayString();
		}

		@Override
		public String getDisplayString() {
			return displayString;
		}

		@Override
		public String getReplacementString() {
			return value;
		}

		private void updateDisplayString() {
			if (value != null && matchPattern != null) {
				List<Integer> matchIndexes = new ArrayList<Integer>();
				displayString = value;

				String upperCaseValue = value.toUpperCase();
				String upperCaseMatchPattern = matchPattern.toUpperCase();

				int matchIndex = upperCaseValue.indexOf(upperCaseMatchPattern);
				while (matchIndex > -1) {
					matchIndexes.add(matchIndex);
					matchIndex = upperCaseValue.indexOf(upperCaseMatchPattern, matchIndex + 1);
				}

				for (Integer index : matchIndexes) {
					displayString = displayString.subSequence(0, index + matchPattern.length()) + "</b>"
							+ displayString.subSequence(index + matchPattern.length(), displayString.length());
					displayString = displayString.subSequence(0, index) + "<b>" + displayString.subSequence(index, displayString.length());
				}
			}
		}
	}
}
