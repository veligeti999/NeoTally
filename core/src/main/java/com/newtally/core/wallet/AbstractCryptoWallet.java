package com.newtally.core.wallet;

import com.blockcypher.context.BlockCypherContext;
import com.blockcypher.service.AddressService;
import com.blockcypher.utils.BlockCypherRestfulConstants;
import com.blockcypher.utils.config.EndpointConfig;

public abstract class AbstractCryptoWallet implements IWallet {

    public final BlockCypherContext cc = new BlockCypherContext(
            BlockCypherRestfulConstants.VERSION_V1,
            BlockCypherRestfulConstants.CURRENCY_BCY,
            BlockCypherRestfulConstants.NETWORK_BCY_TESTNET,
            "99c096c693e94044be06ef00d4001902");
    /**
     * use this identifier to register with third party apis
     */
    protected final String identifier;

    public AbstractCryptoWallet(String identifier) {
        this.identifier = identifier;
    }
}
