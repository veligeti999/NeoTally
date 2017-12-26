package com.newtally.core.service;

import com.newtally.core.model.*;

import javax.annotation.security.RolesAllowed;

public class UserService {

    public User registerUser() {
        return null;
    }

    @RolesAllowed({Role.ANY})
    public User getCurrentUser() {
        return null;
    }

    @RolesAllowed({Role.ANY})
    public void changeCurrentUserPassword(String newPassword) {

    }

    @RolesAllowed({Role.ANY})
    public void updateCurrentUser(User user) {

    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    public User getUserById(String id) {
        return null;
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    public User getUnActiveUser() {
        return null;
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    public User activateUser(String userId) {
        return null;
    }

    @RolesAllowed({Role.ANY})
    public CoinBalance[] getCurrentUserBalances() {
        return null;
    }

    @RolesAllowed({Role.ANY})
    public Wallet getCurrentUserWalletOfCoin(CoinType coinType) {
        return null;
    }

}
