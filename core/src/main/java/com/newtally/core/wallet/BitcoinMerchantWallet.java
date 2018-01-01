package com.newtally.core.wallet;

import com.blockcypher.model.transaction.Transaction;

import java.util.List;

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
    public Transaction sendTo(String address, long amount) {
        return null;
    }

    @Override
    public List<Transaction> getTransactions(long fromInSecs) {
        return null;
    }

    @Override
    public void notifyOnSendOrReceiveConfirmation() {

    }
}
