package com.newtally.core.model;

import java.util.Date;

public class Order {
    private Long id;
    private String walletAddress;
    private Double currencyAmount;
    private Double discountAmount;
    private Double paymentAmount;
    private Integer currencyId;
    private String currencyCode;
    private Integer counterId;
    private OrderStatus status;
    private String qrCode;
    private Date createdDate;
    private Date modifiedDate;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getWalletAddress() {
        return walletAddress;
    }
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    public Double getCurrencyAmount() {
        return currencyAmount;
    }
    public void setCurrencyAmount(Double currencyAmount) {
        this.currencyAmount = currencyAmount;
    }
    public Double getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
    public Integer getCurrencyId() {
        return currencyId;
    }
    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    public Integer getCounterId() {
        return counterId;
    }
    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public Date getModifiedDate() {
        return modifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    public Double getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    @Override
    public String toString() {
        return "Order [id=" + id + ", walletAddress=" + walletAddress + ", currencyAmount=" + currencyAmount
                + ", discountAmount=" + discountAmount + ", currencyId=" + currencyId + ", currencyCode=" + currencyCode
                + ", counterId=" + counterId + ", status=" + status + ", qrCode=" + qrCode + "]";
    }
    
}
