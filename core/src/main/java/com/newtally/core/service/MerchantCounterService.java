package com.newtally.core.service;

import com.newtally.core.model.CoinType;
import com.newtally.core.model.MerchantConfiguration;
import com.newtally.core.model.Role;
import com.newtally.core.resource.ThreadContext;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;

public class MerchantCounterService extends AbstractService implements IAuthenticator {

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

    public boolean authenticate(String merchantId, String password) {
        Query query = em.createNativeQuery("SELECT  count(*) FROM branch_counter " +
                "WHERE password = :password");

        query.setParameter("password", password);

        BigInteger count = (BigInteger) query.getSingleResult();

        return count.intValue() == 1;
    }

}
