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
package strat.mining.multipool.stats.client.component.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class BlockChainCell extends AbstractCell<String> {

	private static final String blockChainURL = "http://blockchain.info/tx/";

	/**
	 * The HTML templates used to render the cell.
	 */
	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<a href=\"{0}\" target=\"{1}\">{2}</a>")
		SafeHtml cell(String href, String target, String text);
	}

	private static Templates templates = GWT.create(Templates.class);

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		String url = blockChainURL + value;
		sb.append(templates.cell(url, "_blank", value));
	}
}
