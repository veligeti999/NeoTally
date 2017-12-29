package com.newtally.core;

import com.newtally.core.resource.ThreadContext;
import com.newtally.core.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ServiceFactory {

    private final EntityManager em;
    private final EntityManagerFactory emf;
    private static  ServiceFactory factory;

    private final UserService userService;
    private final ThreadContext sessionContext = new ThreadContext();

    private ServiceFactory(EntityManagerFactory emf) {
        this.emf = emf;
        em = emf.createEntityManager();
        userService = new UserService(em, sessionContext);
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
}
