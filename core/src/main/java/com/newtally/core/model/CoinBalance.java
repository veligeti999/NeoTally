package com.newtally.core.model;

public class CoinBalance {

    private CoinType coinType;

    private double balance;

    public CoinType getCoinType() {
        return coinType;
    }

    public void setCoinType(CoinType coinType) {
        this.coinType = coinType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
