package com.newtally.core.service;

import com.newtally.core.model.Merchant;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Role;

import javax.annotation.security.RolesAllowed;

@RolesAllowed(Role.BRANCH_MANAGER)
public class MerchantBranchService {

    public MerchantBranch getCurrentBranch() {
        return null;
    }

    public MerchantCounter registerCounter(String phone) {
        return null;
    }

    public MerchantCounter [] getCounters() {
        return null;
    }

    public void deactivateCounter(long counterId) {

    }

    public MerchantCounter resetCounter(long counterId) {
        return null;
    }
}
