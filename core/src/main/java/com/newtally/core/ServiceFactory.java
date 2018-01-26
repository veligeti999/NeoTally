package com.newtally.core;

import com.newtally.core.resource.ThreadContext;
import com.newtally.core.service.MerchantBranchService;
import com.newtally.core.service.BranchCounterService;
import com.newtally.core.service.MerchantService;
import com.newtally.core.service.OrderInvoiceService;
import com.newtally.core.service.UserService;
import com.newtally.core.wallet.BitcoinConfiguration;

import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;

public class ServiceFactory {

    private final EntityManager em;
    private final EntityManagerFactory emf;
    private static  ServiceFactory factory;
    private final UserService userService;
    private final ThreadContext sessionContext = new ThreadContext();
    private final MerchantService merchantService;
    private final MerchantBranchService branchService;
    private final BranchCounterService counterService;
    private final OrderInvoiceService orderInvoiceService;
    //private final NetworkParameters params;
    //private final BlockStore blockStore;
//    private final BitcoinConfiguration bitcoinConfiguration;

    private ServiceFactory(EntityManagerFactory emf) throws BlockStoreException {
        this.emf = emf;
        em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);

        userService = new UserService(em, sessionContext);
        counterService = new BranchCounterService(em, sessionContext);
        branchService = new MerchantBranchService(em, sessionContext);
        merchantService = new MerchantService(em, sessionContext);
        orderInvoiceService= new OrderInvoiceService(em, sessionContext);

        //Setting up bitcoin environment with SPV blockstore and Regression Test environment for the moment.
        //We will change this to a different blockstore based on our requirement
        //The environment is going to become MainNet eventually when moving to production
        //This is a one time operation and is going to take time(not sure how long)
        int i = 0;
        if(i == 1) {
            throw new BlockStoreException("");
        }
        /*params = RegTestParams.get();
        blockStore = new SPVBlockStore(params, new File("block_store"));
        bitcoinConfiguration = new BitcoinConfiguration(params, blockStore);*/
    }

    static synchronized void initializeFactory(EntityManagerFactory emf) throws BlockStoreException {

        if(factory != null)
            throw new RuntimeException("Already initialized the factory");

        factory = new ServiceFactory(emf);
    }

    public static ServiceFactory getInstance() {
        return factory;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public UserService getUserService() {
         return userService;
    }

    public ThreadContext getSessionContext() {
        return sessionContext;
    }

    public MerchantService getMerchantService() {
        return merchantService;
    }

    public MerchantBranchService getMerchantBranchService() {
        return branchService;
    }

    public BranchCounterService getMerchantCounterService() {
        return counterService;
    }
    public OrderInvoiceService getOrderInvoiceService() {
        return orderInvoiceService;
    }

//	public BitcoinConfiguration getBitcoinConfiguration() {
//		return bitcoinConfiguration;
//	}

}
