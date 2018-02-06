package com.newtally.core.service;

public interface IAuthenticator {

    public boolean authenticate(String user, String password);
    
    public String getUserId(String user, String password);
}
