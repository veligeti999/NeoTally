package com.newtally.core.wallet;

import com.blockcypher.exception.BlockCypherException;
import com.blockcypher.model.transaction.Transaction;
import com.blockcypher.model.transaction.summary.TransactionSummary;

import java.util.List;

/**
 * Wallet provides abstraction of crypto wallets
 *
 */
public interface IWallet {

    /**
     * initializes the wallet with seed and private and public keys.
     * This should also include registering the wallets with third party wallet apis like blockcypher.
     *
     */
    void initialize() throws BlockCypherException;

    /**
     * @return the private key/address
     */
    String getPrivateAddress();

    /**
     * TODO: Do i need to change this address everytime i receive some coins on it? Please answer
     *
     * @return the public address
     */
    String getPublicAddress();

    /**
     *
     * @return the balance of the amount on this wallet
     */
    long getBalance() throws BlockCypherException;

    /**
     * @param toAddress receiver address
     * @param amount amount to be with drawn from this wallet
     * @return the transaction state for moving the amount to address
     */
    Transaction sendTo(String toAddress, long amount) throws BlockCypherException;

    /**
     * returns the transactions from the specified time
     *
     * @param fromInSecs
     * @return
     */
    List<TransactionSummary> getTransactions(long fromInSecs) throws BlockCypherException;


}
