package com.newtally.core.wallet;

import com.blockcypher.model.transaction.Transaction;

import java.util.List;

/**
 * Wallet provides abstraction of crypto wallets
 *
 */
public interface IWallet {

    /**
     * initializes the wallet with seed and private and public keys.
     * This should also include registering the wallets with third party wallet apis.
     */
    void initialize();

    /**
     * @return the private key/address
     */
    String getPrivateAddress();

    /**
     *
     * @return the public address
     */
    String getPublicAddress();

    /**
     *
     * @return the balance of the amount on this wallet
     */
    long getBalance();

    /**
     *
     * @param address receriver address
     * @param amount amount to be with drawn from this wallet
     * @return the transaction state for moving the amount to address
     */
    Transaction sendTo(String address, long amount);

    /**
     * returns the transactions from the specified time
     *
     * @param fromInSecs
     * @return
     */
    List<Transaction> getTransactions(long fromInSecs);

    /**
     * handle the logic when a particular transaction is confirmed
     * and notification should be sent to the consumer or merchant
     */
    void notifyOnSendOrReceiveConfirmation();

}
