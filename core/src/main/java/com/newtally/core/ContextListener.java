package com.newtally.core;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bitcoinj.store.BlockStoreException;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqlpersistence");

        try {
			ServiceFactory.initializeFactory(emf);
		} catch (BlockStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}