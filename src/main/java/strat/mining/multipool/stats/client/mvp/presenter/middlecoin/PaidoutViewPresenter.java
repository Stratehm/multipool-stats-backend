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
package strat.mining.multipool.stats.client.mvp.presenter.middlecoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import strat.mining.multipool.stats.client.factory.ClientFactory;
import strat.mining.multipool.stats.client.mvp.event.EveryAddressPaidoutLoadedEvent;
import strat.mining.multipool.stats.client.mvp.handler.EveryAddressPaidoutLoadHandler;
import strat.mining.multipool.stats.client.mvp.view.middlecoin.PaidoutView;
import strat.mining.multipool.stats.client.mvp.view.middlecoin.impl.PaidoutViewImpl;
import strat.mining.multipool.stats.dto.AddressPaidoutDTO;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class PaidoutViewPresenter implements PaidoutView.PaidoutViewPresenter {

	private ClientFactory clientFactory;

	private PaidoutView view;

	private static final DateTimeFormat dateNormalizer = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

	private List<String> requestedAddresses;

	public PaidoutViewPresenter(ClientFactory clientFactory, List<String> addresses, boolean isAllAddress) {
		this.clientFactory = clientFactory;
		this.requestedAddresses = addresses;

		String title = "Paidout for ";
		if (isAllAddress) {
			title += "all selected addresses";
		} else {
			if (addresses != null) {
				for (String address : addresses) {
					title += address + ", ";
				}
				// remove the two last characters.
				title = title.substring(0, title.length() - 2);
			}
		}

		view = new PaidoutViewImpl(title);

		loadData(isAllAddress);

	}

	private void loadData(boolean isAllAddress) {
		// If all addresses selected, check if all addresses data are
		// available.
		Map<String, List<AddressPaidoutDTO>> addressesPaidout = clientFactory.getMainDataManager().getMiddlecoinDataManager().getDataContainer()
				.getAddressesPaidout();
		if (addressesPaidout.keySet().containsAll(requestedAddresses)) {
			// If data already available, then fill the graph
			fillGraph(addressesPaidout);
		} else {
			// If not already available, add a handler to be notified when
			// data are loaded
			clientFactory.getMainDataManager().getMiddlecoinDataManager()
					.addEveryAddressPaidoutLoadHandler(new EveryAddressPaidoutLoadHandler<AddressPaidoutDTO>() {
						public void everyAddressPaidoutLoaded(EveryAddressPaidoutLoadedEvent<AddressPaidoutDTO> event) {
							fillGraph(event.getPaidout());
						}
					});
			// And request the load.
			clientFactory.getMainDataManager().getMiddlecoinDataManager().loadAllPaidout();
		}
	}

	/**
	 * Fill the graph with the given data.
	 * 
	 * @param addressesPaidout
	 */
	private void fillGraph(Map<String, List<AddressPaidoutDTO>> addressesPaidout) {
		Map<String, List<AddressPaidoutDTO>> clonedData = new HashMap<String, List<AddressPaidoutDTO>>();
		// Keep only requested addresses
		for (String address : requestedAddresses) {
			List<AddressPaidoutDTO> addressPaidout = addressesPaidout.get(address);
			if (addressPaidout != null) {
				clonedData.put(address, new ArrayList<AddressPaidoutDTO>(addressPaidout));
			} else {
				clonedData.put(address, new ArrayList<AddressPaidoutDTO>());
			}
		}

		// If several addresses to display, normalize the data.
		// if (clonedData.size() > 1) {
		normalizeData(clonedData);
		// }

		// Then display all addresses
		for (Entry<String, List<AddressPaidoutDTO>> entry : clonedData.entrySet()) {
			view.addAddressPaidout(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Add missing data in all series. At the end of the method, all series have
	 * the same number of data.
	 * 
	 * @param addressesPaidout
	 */
	private void normalizeData(Map<String, List<AddressPaidoutDTO>> addressesPaidout) {
		if (addressesPaidout != null) {
			Map<String, Map<Date, List<AddressPaidoutDTO>>> addressPaidoutByDate = new HashMap<String, Map<Date, List<AddressPaidoutDTO>>>();
			Set<Date> dateSet = new HashSet<Date>();

			// Build a map from the initial one.
			for (Entry<String, List<AddressPaidoutDTO>> entry : addressesPaidout.entrySet()) {
				Map<Date, List<AddressPaidoutDTO>> paidoutByDate = new HashMap<Date, List<AddressPaidoutDTO>>();
				for (AddressPaidoutDTO paidout : entry.getValue()) {
					Date date = dateNormalizer.parse(dateNormalizer.format(paidout.getTime()));
					dateSet.add(date);

					// If the date is already present, add the transaction to
					// the already present one
					List<AddressPaidoutDTO> alreadyPresentPaidout = paidoutByDate.get(date);
					if (alreadyPresentPaidout == null) {
						alreadyPresentPaidout = new ArrayList<AddressPaidoutDTO>();
						paidoutByDate.put(date, alreadyPresentPaidout);
					}
					alreadyPresentPaidout.add(paidout);
				}
				addressPaidoutByDate.put(entry.getKey(), paidoutByDate);
			}

			// Fill missing date in all series
			for (Map<Date, List<AddressPaidoutDTO>> paidoutByDate : addressPaidoutByDate.values()) {
				for (Date date : dateSet) {
					// If the dto does not exist for the given date, then create
					// a fake dto.
					if (!paidoutByDate.containsKey(date)) {
						List<AddressPaidoutDTO> dtos = new ArrayList<AddressPaidoutDTO>();
						AddressPaidoutDTO dto = new AddressPaidoutDTO();
						dto.setAmount(0F);
						dto.setTime(date);
						dto.setTransactionId(null);
						dtos.add(dto);
						paidoutByDate.put(date, dtos);
					}
				}
			}

			// For all address, sort the new list of DTOs
			for (Entry<String, Map<Date, List<AddressPaidoutDTO>>> entry : addressPaidoutByDate.entrySet()) {
				List<AddressPaidoutDTO> dtos = new ArrayList<AddressPaidoutDTO>();
				for (List<AddressPaidoutDTO> dateDtos : entry.getValue().values()) {
					dtos.addAll(dateDtos);
				}
				Collections.sort(dtos, new Comparator<AddressPaidoutDTO>() {
					public int compare(AddressPaidoutDTO o1, AddressPaidoutDTO o2) {
						return o1.getTime().compareTo(o2.getTime());
					}
				});

				// Replace the list of DTOs with the new one for the given
				// address.
				addressesPaidout.put(entry.getKey(), dtos);
			}
		}
	}

	@Override
	public PaidoutView getView() {
		return view;
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return view.addHideHandler(handler);
	}

	@Override
	public void bringToFront() {
		if (view != null) {
			view.show();
		}
	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public void hide() {
		view.hide();
	}

	@Override
	public void activate() {
		view.activate();
	}
}
