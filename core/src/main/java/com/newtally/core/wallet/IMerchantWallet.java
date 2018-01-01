package com.newtally.core.wallet;

public interface IMerchantWallet extends IWallet {

    /**
     * This should adhere to BIP70 protocol for the payment processing
     *
     * @param amount amount expecting the payment from the consumer
     * @return
     */
    String generatePaymentAddress(long amount);
}
