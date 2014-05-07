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
package strat.mining.multipool.stats.jersey;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import strat.mining.multipool.stats.jersey.model.middlecoin.AddressReport;

public class ReportDeserializer extends JsonDeserializer<Map<String, AddressReport>> {

	public ReportDeserializer() {
	}

	@Override
	public Map<String, AddressReport> deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
		HashMap<String, AddressReport> result = new HashMap<>();

		// Skip the first [
		jp.nextToken();

		while (!JsonToken.END_ARRAY.equals(jp.nextToken()) && jp.getCurrentToken() != null) {
			// If the current token is not a VALUE_STRING, advance to it
			if (!JsonToken.VALUE_STRING.equals(jp.getCurrentToken())) {
				while (!JsonToken.VALUE_STRING.equals(jp.nextToken()))
					;
			}
			String key = jp.getText();

			AddressReport report = new AddressReport();

			while (!JsonToken.END_OBJECT.equals(jp.nextToken())) {
				// If the current token is not a field name, advance to it
				if (!JsonToken.FIELD_NAME.equals(jp.getCurrentToken())) {
					while (!JsonToken.FIELD_NAME.equals(jp.nextToken()))
						;
				}

				String fieldname = jp.getCurrentName();

				// Advane to the value of the field
				jp.nextToken();

				if ("lastHourShares".equals(fieldname)) {
					report.setLastHourShares(jp.getIntValue());
				} else if ("immatureBalance".equals(fieldname)) {
					report.setImmatureBalance(StringUtils.isEmpty(jp.getText()) ? null : Float.parseFloat(jp.getText()));
				} else if ("lastHourRejectedShares".equals(fieldname)) {
					report.setLastHourRejectedShares(jp.getIntValue());
				} else if ("paidOut".equals(fieldname)) {
					report.setPaidOut(StringUtils.isEmpty(jp.getText()) ? null : Float.parseFloat(jp.getText()));
				} else if ("unexchangedBalance".equals(fieldname)) {
					report.setUnexchangedBalance(StringUtils.isEmpty(jp.getText()) ? null : Float.parseFloat(jp.getText()));
				} else if ("megahashesPerSecond".equals(fieldname)) {
					report.setMegahashesPerSecond(StringUtils.isEmpty(jp.getText()) ? null : Float.parseFloat(jp.getText()));
				} else if ("bitcoinBalance".equals(fieldname)) {
					report.setBitcoinBalance(StringUtils.isEmpty(jp.getText()) ? null : Float.parseFloat(jp.getText()));
				} else if ("rejectedMegahashesPerSecond".equals(fieldname)) {
					report.setRejectedMegahashesPerSecond(StringUtils.isEmpty(jp.getText()) ? null : Float.parseFloat(jp.getText()));
				}
			}

			// Read the end array of a report entry
			jp.nextToken();

			result.put(key, report);
		}

		return result;
	}

}
