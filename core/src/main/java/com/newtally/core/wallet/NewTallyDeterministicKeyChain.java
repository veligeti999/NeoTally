package com.newtally.core.wallet;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;

import com.google.common.collect.ImmutableList;
import com.newtally.core.ServiceFactory;
import com.newtally.core.wallet.WalletManager;

public class NewTallyDeterministicKeyChain extends DeterministicKeyChain{
 
	public NewTallyDeterministicKeyChain(DeterministicSeed seed) {
		super(seed);
	}

  	@Override
	protected ImmutableList<ChildNumber> getAccountPath() {
		WalletManager walletManager = ServiceFactory.getInstance().getWalletManager();
		int accountNum = walletManager.getThreadContext().getCurrentBranchAccountNum();
		return ImmutableList.of(new ChildNumber(44, true), new ChildNumber(1, true), new ChildNumber(accountNum, true));
    }
	
}
