package com.newtally.core.model;

public class Currency {

    private Long id;
    private CoinType code;
    private String name;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public CoinType getCode() {
        return code;
    }
    public void setCode(CoinType code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
