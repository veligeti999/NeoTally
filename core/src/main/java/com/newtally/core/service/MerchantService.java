package com.newtally.core.service;

import com.newtally.core.model.*;

import javax.annotation.security.RolesAllowed;

public class MerchantService {

    public Merchant registerMerchant() {
        return null;
    }

    public Merchant getCurrentMerchant() {
        return null;
    }

    @RolesAllowed({Role.SYSTEM})
    public Merchant getNextUnActiveMerchant() {
        return null;
    }

    @RolesAllowed({Role.SYSTEM})
    public void activateMerchant(String merchantId) {
    }

    @RolesAllowed({Role.MERCHANT})
    public MerchantBranch registerBranch() {
        return null;
    }

    @RolesAllowed(Role.MERCHANT)
    public MerchantBranch [] getAllBranches() {
        return null;
    }

    @RolesAllowed(Role.BRANCH_MANAGER)
    public MerchantBranch getCurrentBranch() {
        return null;
    }

    public MerchantCounter registerMerchantCounter(String phone) {
        return null;
    }


    @RolesAllowed(Role.BRANCH_COUNTER)
    public String handlePayment(CoinType type, double amount) {
        return null;
    }

}
