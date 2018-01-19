package com.newtally.core.dto;

import com.newtally.core.model.PhysicalAddress;

public class CounterDto {
    private Long counter_id;
    private String counter_name;
    private String merchant_name;
    private String branch_name;
    private PhysicalAddress address;
    public Long getCounter_id() {
        return counter_id;
    }
    public void setCounter_id(Long counter_id) {
        this.counter_id = counter_id;
    }
    public String getCounter_name() {
        return counter_name;
    }
    public void setCounter_name(String counter_name) {
        this.counter_name = counter_name;
    }
    public String getMerchant_name() {
        return merchant_name;
    }
    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }
    public String getBranch_name() {
        return branch_name;
    }
    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }
    public PhysicalAddress getAddress() {
        return address;
    }
    public void setAddress(PhysicalAddress address) {
        this.address = address;
    }
    
    

}
