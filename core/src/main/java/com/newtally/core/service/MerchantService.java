package com.newtally.core.service;

import com.newtally.core.model.*;

import javax.annotation.security.RolesAllowed;

public class MerchantService {

    public Merchant registerMerchant() {
        return null;
    }

    @RolesAllowed({Role.MERCHANT})
    public Merchant updateMerchant(Merchant merchant) {
        return null;
    }

    @RolesAllowed({Role.MERCHANT})
    public MerchantBranch registerBranch(MerchantBranch branch) {
        return null;
    }

    @RolesAllowed(Role.MERCHANT)
    public MerchantBranch [] getAllBranches() {
        return null;
    }

    @RolesAllowed(Role.MERCHANT)
    public void deleteBranch(long branchId) {
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

}
