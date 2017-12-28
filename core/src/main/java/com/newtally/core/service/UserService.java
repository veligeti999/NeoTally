package com.newtally.core.service;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.*;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.Random;

public class UserService {
    private final EntityManager em;
    private final Random idGen = new Random();

    public UserService(EntityManager em) {
        this.em = em;
    }

    public User registerUser(User user) {

        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("INSERT INTO user ( id, first_name," +
                    "last_name, middle_name, password, adhar_id, dob, gender, phone, " +
                    "email, address, city, state, zip, country)" +
                    "values(:id, :first_name, :last_name, :middle_name, :password, :adhar_id," +
                    " :dob, :gender, :phone, :email, :address, :city, :state, :zip, :country)");

            user.setId(idGen.nextLong());
            setCreateParams(user, query);

            query.executeUpdate();

            return user;
        } finally {
            if(!trn.isActive()) {
                trn.rollback();
            }
        }
    }

    private void setCreateParams(User user, Query query) {
        query.setParameter("id", user.getId());
        query.setParameter("first_name", user.getFirstName());
        query.setParameter("last_name", user.getLastName());
        query.setParameter("middle_name", user.getMiddleName());
        query.setParameter("adhar_id", user.getAdharId());
        query.setParameter("password", user.getPassword());
        query.setParameter("dob", user.getDob());
        query.setParameter("gender", user.getGender());

        setUpdateParams(user, query);
    }

    private void setUpdateParams(User user, Query query) {
        query.setParameter("phone", user.getPhone());
        query.setParameter("email", user.getEmail());

        UserAddress address = user.getAddress();
        query.setParameter("address", address.getAddress());
        query.setParameter("city", address.getCity());
        query.setParameter("state", address.getState());
        query.setParameter("zip", address.getZip());
        query.setParameter("country", address.getCity());
    }

    @RolesAllowed({Role.ANY})
    public User getCurrentUser() {
        return null;
    }

    @RolesAllowed({Role.ANY})
    public void changeCurrentUserPassword(String newPwd, String currentPwd) {

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
