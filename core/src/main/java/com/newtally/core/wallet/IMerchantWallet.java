package com.newtally.core.wallet;

/**
 * TODO: Should we use HD wallet here? I emailed blockcypher
 * and they mentioned to use HD. How is this different from normal wallet?
 */
public interface IMerchantWallet extends IWallet {

    /**
     * This should adhere to BIP70 protocol for the payment processing
     *
     * @param amount amount expecting the payment from the consumer
     * @return
     */
    String generatePaymentAddress(long amount);
}
