package com.newtally.core;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlDataSourceFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;

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