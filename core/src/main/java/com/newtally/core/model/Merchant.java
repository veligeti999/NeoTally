package com.newtally.core.model;

public class Merchant {
    private long id;
    private String name;
    private String password;
    private String licenseId;
    private String ownerName;
    private String phone;
    private String email;

    private MerchantConfiguration configuration;

    private PhysicalAddress address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MerchantConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MerchantConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PhysicalAddress getAddress() {
        return address;
    }

    public void setAddress(PhysicalAddress address) {
        this.address = address;
    }
}
