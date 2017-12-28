package com.newtally.core.service;

import com.newtally.core.model.CoinType;
import com.newtally.core.model.MerchantConfiguration;
import com.newtally.core.model.Role;

import javax.annotation.security.RolesAllowed;

public class MerchantCounterService {
    @RolesAllowed(Role.BRANCH_COUNTER)
    public String handlePayment(CoinType type, double amount) {
        return null;
    }

    @RolesAllowed({Role.BRANCH_COUNTER})
    public MerchantConfiguration getConfiguration() {
        return null;
    }
}
