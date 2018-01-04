package com.newtally.core.service;

import com.newtally.core.util.CollectionUtil;
import com.newtally.core.resource.ThreadContext;
import com.newtally.core.model.*;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserService extends  AbstractService implements IAuthenticator {

    public UserService(EntityManager em, ThreadContext ctx) {
        super(em, ctx);
    }

    public User registerUser(User user) {

        // TODO: validate user object
        // after that validate with official adhar api

        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("INSERT INTO user ( id, first_name," +
                    "last_name, middle_name, password, adhar_id, dob, male, phone, " +
                    "email, address, city, state, zip, country, active)" +
                    "values(:id, :first_name, :last_name, :middle_name, :password, :adhar_id," +
                    " :dob, :male, :phone, :email, :address, :city, :state, :zip, :country, false)");

            user.setId(nextId());
            setCreateParams(user, query);

            query.executeUpdate();

            trn.commit();

            user.setPassword(null);
            return user;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    private void setCreateParams(User user, Query query) {
        query.setParameter("first_name", user.getFirstName());
        query.setParameter("last_name", user.getLastName());
        query.setParameter("middle_name", user.getMiddleName());
        query.setParameter("adhar_id", user.getAdharId());
        query.setParameter("password", user.getPassword());
        query.setParameter("dob", Date.valueOf(user.getDob()));
        query.setParameter("male", user.isMale());

        setUpdateParams(user, query);
    }

    private void setUpdateParams(User user, Query query) {
        query.setParameter("id", user.getId());
        query.setParameter("phone", user.getPhone());
        query.setParameter("email", user.getEmail());

        PhysicalAddress address = user.getAddress();
        query.setParameter("address", address.getAddress());
        query.setParameter("city", address.getCity());
        query.setParameter("state", address.getState());
        query.setParameter("zip", address.getZip());
        query.setParameter("country", address.getCity());
    }

    public void changeCurrentUserPassword(String newPwd, String currentPwd) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE  user SET password = :newPwd" +
                    "WHERE id = :id AND password = :currentPwd");

            query.setParameter("id", ctx.getCurrentUserId());
            query.setParameter("newPwd", newPwd);
            query.setParameter("currentPwd", currentPwd);

            if(query.executeUpdate() == 1) {
                throw new IllegalArgumentException("Invalid user or password");
            }

            trn.commit();
        } catch (Exception e){
                trn.rollback();
        }
    }

    public void updateCurrentUser(User user) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE user SET phone = :phone, " +
                    "email = :email, address = :address, city = :city, state = :state," +
                    " zip = :zip, country = :country WHERE id = :id");

            user.setId(ctx.getCurrentUserId());

            setUpdateParams(user, query);
            query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    public User getCurrentUser() {
        return getUserById(ctx.getCurrentUserId());
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    public User getUserById(long id) {
        return getUserOfWhereClause("WHERE id = :id", CollectionUtil.getSingleEntryMap("id", id));
    }

    private User getUserOfWhereClause(String whereClause, Map<String, Object> params) {
        Query query = em.createNativeQuery("SELECT  id, first_name, last_name, " +
                "middle_name, adhar_id, dob, male, phone, email, address, " +
                "city, state, zip, country FROM user " + whereClause);

        setParams(params, query);

        List list =  query.getResultList();

        User user = readUser((Object[]) list.get(0));

        return user;
    }

    private User readUser(Object[] fields){
        User user = new User();
        user.setId( ((BigInteger) fields[0]).longValue());
        user.setLastName((String) fields[1]);
        user.setFirstName((String) fields[2]);
        user.setMiddleName((String) fields[3]);
        user.setAdharId((String) fields[4]);
        user.setDob(((Date) fields[5]).toString());
        user.setMale((Boolean) fields[6]);

        user.setPhone((String) fields[7]);
        user.setEmail((String) fields[8]);
        user.setAddress(readAddress(9, fields));

        return user;
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    public User getInActiveUser() {
        return getUserOfWhereClause("WHERE active = false", Collections.EMPTY_MAP);
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    public void setUserState(long userId, boolean active) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE user " +
                    "SET active = :active WHERE id = :id");

            query.setParameter("id", userId);
            query.setParameter("active", active);

            int count = query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    public CoinBalance[] getCurrentUserBalances() {
        return null;
    }

    public boolean authenticate(String id, String password) {
        Query query = em.createNativeQuery("SELECT  count(*) FROM user " +
                "WHERE id = :id AND password = :password");

        query.setParameter("id", Long.parseLong(id));
        query.setParameter("password", password);

        BigInteger count = (BigInteger) query.getSingleResult();

        return count.intValue() == 1;
    }

}
