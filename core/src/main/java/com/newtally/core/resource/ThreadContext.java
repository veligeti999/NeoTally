package com.newtally.core.resource;

public class ThreadContext {

    private ThreadLocal<Long> userLocal = new ThreadLocal<>();
    private ThreadLocal<Long> merchantLocal = new ThreadLocal<>();
    private ThreadLocal<Long> mrctBranchId = new ThreadLocal<>();
    private ThreadLocal<String> mrctCtrId = new ThreadLocal<>();
    private ThreadLocal<Integer> branchAccountNum = new ThreadLocal<>();

    public long getCurrentUserId() {
        return userLocal.get();
    }

    void setCurrentUserId(Long userId) {
        userLocal.set(userId);
    }

    public Long getCurrentMerchantId() {
        return merchantLocal.get();
    }

    void setCurrentMerchantId(Long merchantId) {
        merchantLocal.set(merchantId);
    }

    public Long getCurrentBranchId() {
        return mrctBranchId.get();
    }
    
    public String getCurrentCounterCode() {
        return mrctCtrId.get();
    }

    public void setCurrentMerchantCounterId(String mrctCtrId) {
        this.mrctCtrId.set(mrctCtrId);
    }
    
    public void setMerchantBranchId(Long mrctBranchId) {
        this.mrctBranchId.set(mrctBranchId);
    }

    public void setCurrentBranchAccNum(Integer branchAccountNum){
        this.branchAccountNum.set(branchAccountNum);
    }

    public int getCurrentBranchAccountNum(){
        return branchAccountNum.get();
    }
    /**
     * should be called after the request is served
     */
    public void clearContext() {
        userLocal.remove();
        merchantLocal.remove();
        mrctBranchId.remove();
        mrctCtrId.remove();
        branchAccountNum.remove();
    }

}
