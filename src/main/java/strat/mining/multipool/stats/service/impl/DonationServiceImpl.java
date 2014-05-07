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
package strat.mining.multipool.stats.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import strat.mining.multipool.stats.dto.CurrencyTickerDTO;
import strat.mining.multipool.stats.dto.DonationDetailsDTO;
import strat.mining.multipool.stats.dto.DonationTransactionDetailsDTO;
import strat.mining.multipool.stats.jersey.client.iface.BlockchainRestClient;
import strat.mining.multipool.stats.jersey.model.blockchain.BlockChainSingleAddress;
import strat.mining.multipool.stats.jersey.model.blockchain.BlockHeight;
import strat.mining.multipool.stats.jersey.model.blockchain.Inputs;
import strat.mining.multipool.stats.jersey.model.blockchain.Out;
import strat.mining.multipool.stats.jersey.model.blockchain.Txs;
import strat.mining.multipool.stats.persistence.dao.donation.iface.AddressDAO;
import strat.mining.multipool.stats.persistence.dao.donation.iface.TransactionDAO;
import strat.mining.multipool.stats.persistence.model.donation.Address;
import strat.mining.multipool.stats.persistence.model.donation.Transaction;
import strat.mining.multipool.stats.service.iface.CurrencyService;
import strat.mining.multipool.stats.service.iface.DonationService;
import strat.mining.multipool.stats.service.impl.place.BitcoinCentralProvider;

@Component("donationService")
public class DonationServiceImpl implements DonationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DonationServiceImpl.class);

	private static final float DEFAULT_BTC_PRICE = 450;

	private static final float SERVER_RENT_PRICE = 30;

	private static final String DONATION_BITCOIN_ADDRESS = "19wv8FQKv3NkwTdzBCQn1AGsb9ghqBPWXi";

	private volatile DonationDetailsDTO donationDetails;

	private volatile Map<String, List<Transaction>> transactionsNotThankedByAddress;

	@Resource
	private BlockchainRestClient blockchainRestClient;

	@Resource
	private CurrencyService currencyService;

	@Resource
	private AddressDAO donationAddressDao;

	@Resource
	private TransactionDAO donationTransactionDao;

	@PostConstruct
	public void init() {
		transactionsNotThankedByAddress = Collections.synchronizedMap(new HashMap<String, List<Transaction>>());
		updateDonations();
	}

	@Override
	public DonationDetailsDTO getDonationDetails() {
		return donationDetails;
	}

	/**
	 * update donations details every 10 minutes
	 */
	@Scheduled(cron = "0 2/10 * * * *")
	private void updateDonations() {
		DonationDetailsDTO newDetails = new DonationDetailsDTO();
		newDetails.setTransactions(new ArrayList<DonationTransactionDetailsDTO>());

		CurrencyTickerDTO currencyTicker = currencyService.getCurrencyTicker(BitcoinCentralProvider.EXCHANGE_NAME, "EUR");

		if (currencyTicker == null) {
			currencyTicker = new CurrencyTickerDTO();
			currencyTicker.setLast(DEFAULT_BTC_PRICE);
		}

		boolean hasErrorOccured = extractTransactions(newDetails);

		newDetails.setLastMonthDonationsValue(getLastMonthDonationsValue());

		if (!hasErrorOccured) {
			float rentValueInBTC = SERVER_RENT_PRICE / currencyTicker.getLast();
			newDetails.setDonationsNeeded(rentValueInBTC);
			newDetails.setBtcPriceInEuro(currencyTicker.getLast());
			newDetails.setDonationBtcAddress(DONATION_BITCOIN_ADDRESS);
			newDetails.setRentPriceInEuro(SERVER_RENT_PRICE);
			donationDetails = newDetails;
		} else {
			donationDetails = null;
		}

		updateDonationsNotThanked();

	}

	/**
	 * Return the value of all transactions of the previous month.
	 * 
	 * @return
	 */
	private Float getLastMonthDonationsValue() {
		Calendar start = new GregorianCalendar();
		start.add(Calendar.MONTH, -1);
		start.set(Calendar.DAY_OF_MONTH, 1);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		List<Transaction> transactions = donationTransactionDao.getTransactionsBetween(start.getTime(), getFirstDayOfMonthDate());

		Float result = 0f;
		if (CollectionUtils.isNotEmpty(transactions)) {
			for (Transaction transaction : transactions) {
				result += transaction.getAmount();
			}
		}

		return result;
	}

	/**
	 * Fill the given details with transactions details
	 * 
	 * @param newDetails
	 * @return
	 */
	private boolean extractTransactions(DonationDetailsDTO newDetails) {
		BlockChainSingleAddress blockChainInfo = null;
		Date month = getFirstDayOfMonthDate();

		int totalDonationsValueInMonth = 0;
		boolean isMonthComplete = false;
		boolean noMoreTxs = false;
		boolean hasErrorOccured = false;

		int currentPage = 0;
		int pageSize = 10;

		while (!isMonthComplete && !noMoreTxs && !hasErrorOccured) {
			blockChainInfo = blockchainRestClient.getBlockChainSingleAddress(DONATION_BITCOIN_ADDRESS, pageSize, currentPage++ * pageSize);

			hasErrorOccured = blockChainInfo == null;

			// Iterate on transactions until a transaction is before the first
			// day of the current month. If this transaction is not found, then
			// retrieve the next transaction page.
			if (blockChainInfo != null && blockChainInfo.getTxs() != null && blockChainInfo.getTxs().size() > 0) {
				for (Txs tx : blockChainInfo.getTxs()) {
					Date txDate = new Date();
					Float txValue = 0f;

					if (tx.getTime() != null) {
						txDate = new Date(tx.getTime().longValue() * 1000);
					} else {
						BlockHeight blockHeight = blockchainRestClient.getBlockHeight(tx.getBlock_height().toString());
						if (CollectionUtils.isNotEmpty(blockHeight.getBlocks()) && blockHeight.getBlocks().get(0).getTime() != null) {
							txDate = new Date(blockHeight.getBlocks().get(0).getTime().longValue() * 1000);
						}
					}

					if (txDate.after(month)) {
						LOGGER.debug("TX kept for date {}", txDate);
						DonationTransactionDetailsDTO txDetails = new DonationTransactionDetailsDTO();

						// Look for the output with my address to get the
						// donation value. The transaction is a donation only if
						// the donation address is in the outputs (else, it is a
						// payement done with this address)
						boolean isDonation = false;
						for (Out out : tx.getOut()) {
							if (DONATION_BITCOIN_ADDRESS.equals(out.getAddr())) {
								txValue = out.getValue().floatValue() / 100000000F;
								LOGGER.debug("Value: {}", txValue);
								txDetails.setValue(txValue);
								totalDonationsValueInMonth += out.getValue().intValue();
								isDonation = true;
								break;
							}
						}

						// Add the transaction only if it is a donation.
						if (isDonation) {
							txDetails.setSrcAddresses(new ArrayList<String>());
							for (Inputs input : tx.getInputs()) {
								txDetails.getSrcAddresses().add(input.getPrev_out().getAddr());
							}

							txDetails.setTime(txDate);

							newDetails.getTransactions().add(txDetails);

							saveTransaction(tx, txDate, txValue);
						}
					} else {
						// Stop to retrieve transactions
						isMonthComplete = true;
						break;
					}
				}
			} else {
				noMoreTxs = true;
			}
		}

		newDetails.setDonationsInBTC((float) totalDonationsValueInMonth / 100000000F);

		return hasErrorOccured;
	}

	@Override
	public List<DonationTransactionDetailsDTO> getAddressDonationsNotThanked(String bitcoinAddress) {
		List<DonationTransactionDetailsDTO> result = null;
		List<Transaction> transactions = transactionsNotThankedByAddress.remove(bitcoinAddress);
		if (CollectionUtils.isNotEmpty(transactions)) {
			result = new ArrayList<>();
			for (Transaction transaction : transactions) {
				DonationTransactionDetailsDTO details = new DonationTransactionDetailsDTO();
				details.setTime(transaction.getDate());
				details.setValue(transaction.getAmount());
				result.add(details);

				transaction.setHasBeenThanked(true);
				donationTransactionDao.updateTransaction(transaction);
			}
		}

		return result;
	}

	/**
	 * Update the map of donation transactions that have not been thanked yet.
	 */
	private void updateDonationsNotThanked() {
		Map<String, List<Transaction>> transactionsByAddress = Collections.synchronizedMap(new HashMap<String, List<Transaction>>());

		List<Address> donatorsAddresses = donationAddressDao.getAllAddressesSince(getFirstDayOfMonthDate());

		if (CollectionUtils.isNotEmpty(donatorsAddresses)) {
			for (Address address : donatorsAddresses) {
				List<Transaction> notThankedDonationTransactions = new ArrayList<>();

				if (address != null && CollectionUtils.isNotEmpty(address.getTransactionIds())) {
					for (String transactionId : address.getTransactionIds()) {
						Transaction transaction = donationTransactionDao.getTransaction(transactionId);
						if (transaction != null && !transaction.getHasBeenThanked()) {
							notThankedDonationTransactions.add(transaction);
						}
					}
				}

				// If the current address has some transactions not thanked. add
				// it.
				if (CollectionUtils.isNotEmpty(notThankedDonationTransactions)) {
					transactionsByAddress.put(address.getAddress(), notThankedDonationTransactions);
				}
			}
		}

		transactionsNotThankedByAddress = transactionsByAddress;
	}

	/**
	 * Return the date object that represent the first day of month
	 * 
	 * @return
	 */
	private Date getFirstDayOfMonthDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * Save the transaction (if not already done).
	 * 
	 * @param tx
	 */
	private void saveTransaction(Txs tx, Date txDate, Float amount) {
		Transaction transaction = donationTransactionDao.getTransaction(tx.getHash());

		// Save the transation if not already present.
		if (transaction == null && CollectionUtils.isNotEmpty(tx.getInputs())) {
			Set<Integer> inputAddressId = new HashSet<>();
			for (Inputs input : tx.getInputs()) {
				String inputAddress = input.getPrev_out().getAddr();
				if (inputAddress != null) {
					Address address = donationAddressDao.getAddress(inputAddress);
					Set<String> transactions = new HashSet<>();

					if (address == null) {
						address = new Address();
						address.setAddress(inputAddress);
						address.setLastUpdated(new Date());
						transactions.add(tx.getHash());
						address.setTransactionIds(transactions);
						donationAddressDao.insertAddress(address);
					} else {
						address.setLastUpdated(new Date());
						transactions.add(tx.getHash());
						if (address.getTransactionIds() != null) {
							transactions.addAll(address.getTransactionIds());
						}
						address.setTransactionIds(transactions);
						donationAddressDao.updateAddress(address);
					}

					inputAddressId.add(address.getId());
				}
			}

			transaction = new Transaction();
			transaction.setTransactionId(tx.getHash());
			transaction.setHasBeenThanked(false);
			transaction.setDate(txDate);
			transaction.setSourceAddressesId(inputAddressId);
			transaction.setAmount(amount);
			donationTransactionDao.insertTransaction(transaction);
		}
	}
}
