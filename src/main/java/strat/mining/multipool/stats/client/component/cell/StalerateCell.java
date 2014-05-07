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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class StalerateCell extends AbstractCell<Float> {

	private static final NumberFormat numberFormat = NumberFormat.getFormat("0.00");

	private Float threshold;

	public StalerateCell(Float threshold) {
		this.threshold = threshold;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, Float value, SafeHtmlBuilder sb) {
		if (threshold != null && value > threshold) {
			sb.appendHtmlConstant("<span style=\"color: red;\">" + numberFormat.format(value) + " %</span>");
		} else {
			sb.appendEscaped(numberFormat.format(value == null ? 0 : value) + " %");
		}
	}
}
