package com.newtally.core.service;

import com.newtally.core.model.*;

public class Wallet {
    private CoinType coinType;

    private String [] seed;
    private String pubAddr;

    private User user;

    public CoinBalance getBalance() {
        return null;
    }

    public Transaction[] getTransactions() {
        return null;
    }

    public Transaction send(double amount, String to) {
        return null;
    }

    public String getPublicAddress() {
        return pubAddr;
    }
}
