package com.newtally.core.service;

public interface IAuthenticator {

    public boolean authenticate(String user, String password);
}
