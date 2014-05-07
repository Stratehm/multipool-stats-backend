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
package strat.mining.multipool.stats.client.mvp.view.impl;

import java.util.List;

import strat.mining.multipool.stats.client.mvp.view.PoolSelectionView;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

public class PoolSelectionViewImpl implements PoolSelectionView {

	private ContentPanel mainContainer;

	private VerticalLayoutContainer verticalContainer;

	private SimpleComboBox<String> poolNameComboBox;

	public PoolSelectionViewImpl(final List<String> poolNames, final List<String> poolDescription) {
		mainContainer = new ContentPanel();
		mainContainer.setHeaderVisible(false);

		verticalContainer = new VerticalLayoutContainer();
		mainContainer.setWidget(verticalContainer);

		poolNameComboBox = new SimpleComboBox<String>(new StringLabelProvider<String>());
		poolNameComboBox.setAllowBlank(false);
		poolNameComboBox.setEditable(false);
		poolNameComboBox.setEmptyText("Select pool...");
		poolNameComboBox.setTriggerAction(TriggerAction.ALL);
		poolNameComboBox.add(poolNames);
		poolNameComboBox.getElement().getElementsByTagName("input").getItem(0).addClassName("poolComboBox");

		verticalContainer.add(poolNameComboBox, new VerticalLayoutData(1, 30, new Margins(5, 50, 5, 50)));

		poolNameComboBox.addSelectionHandler(new SelectionHandler<String>() {
			public void onSelection(SelectionEvent<String> event) {
				int index = poolNames.indexOf(event.getSelectedItem());
				updateDescription(poolDescription.get(index));
			}
		});

		poolNameComboBox.addFocusHandler(new FocusEvent.FocusHandler() {
			public void onFocus(FocusEvent event) {
				poolNameComboBox.expand();
			}
		});

		updateDescription("Please, select a pool.");
	}

	@Override
	public Widget asWidget() {
		return mainContainer;
	}

	@Override
	public String getSelectedPoolName() {
		poolNameComboBox.validate();
		return poolNameComboBox.getSelectedIndex() >= 0 ? poolNameComboBox.getStore().get(poolNameComboBox.getSelectedIndex()) : "";
	}

	private void updateDescription(String description) {
		HtmlLayoutContainer descriptionContainer = new HtmlLayoutContainer(description);

		if (verticalContainer.getWidgetCount() > 1) {
			verticalContainer.remove(1);
		}
		verticalContainer.add(descriptionContainer, new VerticalLayoutData(1, 1, new Margins(15, 10, 10, 10)));
	}
}
