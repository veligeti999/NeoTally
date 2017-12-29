package com.newtally.core.service;

import com.newtally.core.resource.ThreadContext;

import javax.persistence.EntityManager;

public abstract class AbstractService {

    protected final EntityManager em;
    protected final ThreadContext ctx;
    protected final IdGenerator idGen = new IdGenerator();

    public AbstractService(EntityManager em, ThreadContext ctx) {
        this.em = em;
        this.ctx = ctx;
    }

}
