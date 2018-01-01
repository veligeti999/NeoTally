package com.newtally.core.wallet;

public abstract class AbstractCryptoWallet implements IWallet {

    private final String identifier;

    public AbstractCryptoWallet(String identifier) {

        this.identifier = identifier;
    }
}
