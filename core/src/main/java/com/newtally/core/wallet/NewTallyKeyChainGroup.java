package com.newtally.core.wallet;
import java.util.Arrays;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import com.newtally.core.ServiceFactory;
import com.newtally.core.service.MerchantService;

public class NewTallyKeyChainGroup extends KeyChainGroup{

	public NewTallyKeyChainGroup(NetworkParameters params) {
		super(params);
	}
	
	public void createAndActivateNewHDChain() {
		//fetch the mnemonic registered for a merchant to create a key chain during wallet creation and 
		//add it to a key chain group
		WalletManager walletManager = ServiceFactory.getInstance().getWalletManager();
		MerchantService merchantService = ServiceFactory.getInstance().getMerchantService();
		String mnemonic = merchantService.getMnenonicForAMerchant(walletManager.getThreadContext().getCurrentMerchantId());
        DeterministicKeyChain chain = new NewTallyDeterministicKeyChain(new DeterministicSeed(Arrays.asList(mnemonic.split(",")), null, "", MnemonicCode.BIP39_STANDARDISATION_TIME_SECS));
        addAndActivateHDChain(chain);
    }
}
