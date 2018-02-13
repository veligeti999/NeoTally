package com.newtally.core.wallet;

import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.wallet.DefaultKeyChainFactory;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Protos;

public class NewTallyKeyChainFactory extends DefaultKeyChainFactory{

	@Override
    public DeterministicKeyChain makeKeyChain(Protos.Key key, Protos.Key firstSubKey, DeterministicSeed seed, KeyCrypter crypter, boolean isMarried) {
        DeterministicKeyChain chain = new NewTallyDeterministicKeyChain(seed);
        return chain;
    }
}
