package com.newtally.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Role;
import com.newtally.core.resource.ThreadContext;

public class ApplicationService extends AbstractService{
    
    private final Map<String, IAuthenticator> roleVsAuth = new HashMap<>();
    
    private final String domainURI="http://18.219.39.174:8080/new-tally/";

    public ApplicationService(EntityManager em, ThreadContext sessionContext) {
        super(em, sessionContext);
    }

    public void generateResetPasswordLink(HashMap passwordMap) {
        String email=(String) passwordMap.get("email");
        String userType=(String) passwordMap.get("userType");
        ServiceFactory instance = ServiceFactory.getInstance();
        roleVsAuth.put(Role.USER, instance.getUserService());
        roleVsAuth.put(Role.MERCHANT, instance.getMerchantService());
        roleVsAuth.put(Role.BRANCH_MANAGER, instance.getMerchantBranchService());
        roleVsAuth.put(Role.BRANCH_COUNTER, instance.getMerchantCounterService());
        IAuthenticator iAuthenticator = roleVsAuth.get(userType);
        if(iAuthenticator.checkEmail(email)) {
            EntityTransaction trn = em.getTransaction();
            trn.begin();
            try {
                Query query = em.createNativeQuery("UPDATE password_reset_token " +
                        "SET active = false WHERE email = :email and user_type = :userType");
    
                query.setParameter("email", email);
                query.setParameter("userType", userType);
                query.executeUpdate();
                
                Query queryToCreate = em.createNativeQuery("INSERT INTO password_reset_token ( " +
                        "token, email, user_type, generated_time, active) " +
                        "VALUES( :token, :email, :user_type, :generated_time, true)");
    
                String token=generateToken();
                queryToCreate.setParameter("token", token);
                queryToCreate.setParameter("email", email);
                queryToCreate.setParameter("user_type", userType);
                queryToCreate.setParameter("generated_time", new Date());
                queryToCreate.executeUpdate();
    
                trn.commit();
                sendNotification(token,userType, email);
    
            } catch (Exception e) {
                trn.rollback();
                throw e;
            }
        }
        else {
            throw new RuntimeException("Email is not registered");
        }
    }
    
    protected String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    private void sendNotification(String token, String userType, String email) {
        EmailService.sendEmail(email, "Reset Newtally Password", " Hi \n \n Password reset Link: "+domainURI+"reset_password.html?token="+token+"&userType="+userType+"\n\n From \n Newtally.com");
    }
    public void resetPassword(HashMap passwordMap, String token, String userType) {

        ServiceFactory instance = ServiceFactory.getInstance();
        roleVsAuth.put(Role.USER, instance.getUserService());
        roleVsAuth.put(Role.MERCHANT, instance.getMerchantService());
        roleVsAuth.put(Role.BRANCH_MANAGER, instance.getMerchantBranchService());
        roleVsAuth.put(Role.BRANCH_COUNTER, instance.getMerchantCounterService());
        IAuthenticator iAuthenticator = roleVsAuth.get(userType);
        Query query = em.createNativeQuery("select email from password_reset_token where token = :token and active=true");

        query.setParameter("token", token);
        List rs=query.getResultList();
        if(!rs.isEmpty()) {
            String email=(String) rs.get(0);
            iAuthenticator.resetPassword(email, (String)passwordMap.get("password"));
            EntityTransaction trn = em.getTransaction();
            trn.begin();
            try {
                Query queryToInActive = em.createNativeQuery("UPDATE password_reset_token " +
                        "SET active = false where token = :token and active=true");
    
                queryToInActive.setParameter("token", token);
                queryToInActive.executeUpdate();
    
                trn.commit();
    
            } catch (Exception e) {
                trn.rollback();
                throw e;
            }
            
        }
        else {
            throw new RuntimeException("Invalid token to reset the password");
        }
        
    }

    public void confirmEmail(String token) {
        Query query = em.createNativeQuery("select email from confirm_email_token where token = :token and active=true");

        query.setParameter("token", token);
        List rs=query.getResultList();
        if(!rs.isEmpty()) {
            String email=(String) rs.get(0);
            EntityTransaction trn = em.getTransaction();
            trn.begin();
            try {
                Query queryToInActive = em.createNativeQuery("UPDATE confirm_email_token " +
                        "SET active = false where token = :token and active=true");
    
                queryToInActive.setParameter("token", token);
                queryToInActive.executeUpdate();
    
                trn.commit();
    
            } catch (Exception e) {
                trn.rollback();
                throw e;
            }
            
        }
        else {
            throw new RuntimeException("Invalid token to confirm the email");
        }
        
    }
    public void generateConfirmEmailLink(String email) {
            EntityTransaction trn = em.getTransaction();
            trn.begin();
            try {
                Query queryToCreate = em.createNativeQuery("INSERT INTO confirm_email_token ( " +
                        "token, email, generated_time, active) " +
                        "VALUES( :token, :email, :generated_time, true)");
    
                String token=generateToken();
                queryToCreate.setParameter("token", token);
                queryToCreate.setParameter("email", email);
                queryToCreate.setParameter("generated_time", new Date());
                queryToCreate.executeUpdate();
                ServiceFactory.getInstance().getMerchantService().updateMerchantAfterCofirmEmail(email);
                trn.commit();
                sendNotificationForEmailConfirmation(token, email);
    
            } catch (Exception e) {
                trn.rollback();
                throw e;
            }
    }

    private void sendNotificationForEmailConfirmation(String token, String email) {
        EmailService.sendEmail(email, "Email confirmation for Newtally", " Hi \n \n Confirm your email on click of this link: "+domainURI+"email_confirmation.html?token="+token+"\n\n From \n Newtally.com");
    }
}
