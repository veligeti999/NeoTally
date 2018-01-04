package com.newtally.core;

import com.newtally.core.resource.ThreadContext;
import com.newtally.core.service.MerchantBranchService;
import com.newtally.core.service.BranchCounterService;
import com.newtally.core.service.MerchantService;
import com.newtally.core.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;

public class ServiceFactory {

    private final EntityManager em;
    private final EntityManagerFactory emf;
    private static  ServiceFactory factory;

    private final UserService userService;
    private final ThreadContext sessionContext = new ThreadContext();
    private final MerchantService merchantService;
    private final MerchantBranchService branchService;
    private final BranchCounterService counterService;

    private ServiceFactory(EntityManagerFactory emf) {
        this.emf = emf;
        em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);

        userService = new UserService(em, sessionContext);
        counterService = new BranchCounterService(em, sessionContext);
        branchService = new MerchantBranchService(em, sessionContext);
        merchantService = new MerchantService(em, sessionContext);
    }

    static synchronized void initializeFactory(EntityManagerFactory emf) {

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
}
