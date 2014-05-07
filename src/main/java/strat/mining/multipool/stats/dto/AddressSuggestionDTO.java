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
package strat.mining.multipool.stats.dto;

import java.util.List;

import org.jsonmaker.gwt.client.Jsonizer;

public class AddressSuggestionDTO {

	public interface AddressSuggestionDTOJsonizer extends Jsonizer {
	}

	private boolean hasMoreSuggestion;
	private int moreSuggestionCount;
	private List<String> suggestions;

	public boolean isHasMoreSuggestion() {
		return hasMoreSuggestion;
	}

	public void setHasMoreSuggestion(boolean hasMoreSuggestion) {
		this.hasMoreSuggestion = hasMoreSuggestion;
	}

	public int getMoreSuggestionCount() {
		return moreSuggestionCount;
	}

	public void setMoreSuggestionCount(int moreSuggestionCount) {
		this.moreSuggestionCount = moreSuggestionCount;
	}

	public List<String> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<String> suggestions) {
		this.suggestions = suggestions;
	}

}
