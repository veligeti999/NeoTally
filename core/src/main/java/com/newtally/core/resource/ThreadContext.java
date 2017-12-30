package com.newtally.core.resource;

import com.newtally.core.model.User;

public class ThreadContext {

    private ThreadLocal<Long> userLocal = new ThreadLocal<>();

    private ThreadLocal<Long> merchantLocal = new ThreadLocal<>();

    private ThreadLocal<Long> mrctBranchId = new ThreadLocal<>();

    public long getCurrentUserId() {
        return userLocal.get();
    }

    void setCurrentUserId(Long userId) {
        userLocal.set(userId);
    }

    public long getCurrentMerchantId() {
        return merchantLocal.get();
    }

    void setCurrentMerchantId(Long merchantId) {
        merchantLocal.set(merchantId);
    }

    public long getCurrentBranchId() {
        return mrctBranchId.get();
    }

    /**
     * should be called after the request is served
     */
    public void clearContext() {
        userLocal.remove();
        merchantLocal.remove();
        mrctBranchId.remove();
    }

}
