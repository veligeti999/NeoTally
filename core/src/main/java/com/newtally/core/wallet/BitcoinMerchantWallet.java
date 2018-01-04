package com.newtally.core.wallet;

import com.blockcypher.model.transaction.Transaction;
import com.blockcypher.model.transaction.summary.TransactionSummary;

import java.util.List;

/**
 * TODO: implement this
 */
public class BitcoinMerchantWallet extends AbstractCryptoWallet implements IMerchantWallet{

    public BitcoinMerchantWallet(String identifier) {
        super(identifier);
    }

    @Override
    public String generatePaymentAddress(long amount) {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public String getPrivateAddress() {
        return null;
    }

    @Override
    public String getPublicAddress() {
        return null;
    }

    @Override
    public long getBalance() {
        return 0;
    }

    @Override
    public Transaction sendTo(String toAddress, long amount) {
        return null;
    }

    @Override
    public List<TransactionSummary> getTransactions(long fromInSecs) {
        return null;
    }
}
