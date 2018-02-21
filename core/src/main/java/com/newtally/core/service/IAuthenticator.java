package com.newtally.core.service;

public interface IAuthenticator {

    public boolean authenticate(String user, String password);
    
    public String getUserId(String user, String password);
    
    public boolean checkEmail(String email);
    
    public void resetPassword(String email, String password);
}
