package com.newtally.core.model;

import java.util.Date;

public class Withdrawal {
    
    private Long id;
    private Long merchantId;
    private String currencyCode;
    private String currencyName;
    private Double transactionAmount;
    private Double commissionAmount;
    private String walletAddress;
    private Date transactionDate;
    private Boolean status;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMerchantId() {
        return merchantId;
    }
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    public String getCurrencyName() {
        return currencyName;
    }
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
    public Double getTransactionAmount() {
        return transactionAmount;
    }
    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    public Double getCommissionAmount() {
        return commissionAmount;
    }
    public void setCommissionAmount(Double commissionAmount) {
        this.commissionAmount = commissionAmount;
    }
    public String getWalletAddress() {
        return walletAddress;
    }
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    public Date getTransactionDate() {
        return transactionDate;
    }
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
}
