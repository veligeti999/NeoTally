package com.newtally.core.model;

public class MerchantBranch {

    public static final String MERCHANT_ID = "MERCHANT_ID";
    public static final String BRANCH_NO = "BRANCH_NO";
    private long id;
    private long merchantId;
    private String name;
    private String managerName;
    private String password;
    private String phone;
    private String email;
    private int branchNo;
    private boolean headQuarter;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PhysicalAddress getAddress() {
        return address;
    }

    public void setAddress(PhysicalAddress address) {
        this.address = address;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public boolean isHeadQuarter() {
        return headQuarter;
    }

    public void setHeadQuarter(boolean headQuarter) {
        this.headQuarter = headQuarter;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

	public int getBranchNo() {
		return branchNo;
	}

	public void setBranchNo(int branchNo) {
		this.branchNo = branchNo;
	}

}
