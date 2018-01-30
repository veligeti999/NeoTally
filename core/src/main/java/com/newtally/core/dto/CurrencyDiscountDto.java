package com.newtally.core.dto;

public class CurrencyDiscountDto {
    private Long currency_id;
    private String currency_code;
    private String currency_name;
    private Double discount =0d;
    private Double discount_amount =0d;
    private Double currency_amount;
    public Long getCurrency_id() {
        return currency_id;
    }
    public void setCurrency_id(Long currency_id) {
        this.currency_id = currency_id;
    }
    public String getCurrency_code() {
        return currency_code;
    }
    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }
    public String getCurrency_name() {
        return currency_name;
    }
    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }
    public Double getDiscount() {
        return discount;
    }
    public void setDiscount(Double discount) {
        this.discount = discount;
    }
    public Double getDiscount_amount() {
        return discount_amount;
    }
    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }
    public Double getCurrency_amount() {
        return currency_amount;
    }
    public void setCurrency_amount(Double currency_amount) {
        this.currency_amount = currency_amount;
    }

}
