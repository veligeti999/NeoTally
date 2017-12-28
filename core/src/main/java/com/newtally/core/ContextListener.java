package com.newtally.core;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqlpersistence");
        ServiceFactory.initializeFactory(emf);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}