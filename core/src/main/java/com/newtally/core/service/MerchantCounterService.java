package com.newtally.core.service;

import com.newtally.core.model.CoinType;
import com.newtally.core.model.MerchantConfiguration;
import com.newtally.core.model.Role;
import com.newtally.core.resource.ThreadContext;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;

public class MerchantCounterService extends AbstractService{

    public MerchantCounterService(EntityManager em, ThreadContext ctx) {
        super(em, ctx);
    }

    @RolesAllowed(Role.BRANCH_COUNTER)
    public String handlePayment(CoinType type, double amount) {
        return null;
    }

    @RolesAllowed({Role.BRANCH_COUNTER})
    public MerchantConfiguration getConfiguration() {
        return null;
    }
}
