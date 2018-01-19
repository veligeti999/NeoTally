package com.newtally.core.service;

import com.newtally.core.util.CollectionUtil;
import com.newtally.core.ServiceFactory;
import com.newtally.core.model.*;
import com.newtally.core.resource.ThreadContext;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MerchantService extends AbstractService implements IAuthenticator {


    public MerchantService(EntityManager em, ThreadContext ctx ) {
        super(em, ctx);
    }

    public Merchant registerMerchant(Merchant merchant) {
        // TODO: validate merchant object
        // after that validate with official merchant license api

        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("INSERT INTO merchant ( " +
                    "id, name, owner_name, password, pan, phone, email, " +
                    "address, city, state, zip, country, active) " +
                    "VALUES( :id, :name, :owner_name, :password, :pan, :phone, " +
                    ":email, :address, :city, :state, :zip, :country, false)");

            merchant.setId(nextId());
            setCreateParams(merchant, query);

            query.executeUpdate();

            MerchantBranch branch = new MerchantBranch();
            branch.setMerchantId(merchant.getId());
            branch.setName(merchant.getName());
            branch.setManagerName(merchant.getOwnerName());
            branch.setPassword(merchant.getPassword());
            branch.setPhone(merchant.getPhone());
            branch.setEmail(merchant.getEmail());
            branch.setAddress(merchant.getAddress());
            branch.setHeadQuarter(true);

            createBranch(branch);

            trn.commit();

            merchant.setPassword(null);
            return merchant;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    private void setCreateParams(Merchant merchant, Query query) {
        query.setParameter("name", merchant.getName());
        query.setParameter("owner_name", merchant.getOwnerName());
        query.setParameter("pan", merchant.getPan());
        query.setParameter("password", merchant.getPassword());

        setUpdateParams(merchant, query);
    }

    private void setUpdateParams(Merchant merchant, Query query) {
        query.setParameter("id", merchant.getId());
        query.setParameter("phone", merchant.getPhone());
        query.setParameter("email", merchant.getEmail());

        PhysicalAddress address = merchant.getAddress();
        setPhysicalAddressParams(query, address);
    }

    @RolesAllowed({Role.MERCHANT})
    public void updateMerchant(Merchant merchant) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant SET phone = :phone, " +
                    "email = :email, address = :address, city = :city, state = :state," +
                    " zip = :zip, country = :country WHERE id = :id");

            merchant.setId(ctx.getCurrentMerchantId());

            setUpdateParams(merchant, query);
            query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    public void updateCurrentMerchant(Merchant merchant) {
        merchant.setId(ctx.getCurrentMerchantId());
    }

    public Merchant getCurrentMerchant() {
        return getMerchantWithWhereClause("WHERE id = :id",
                CollectionUtil.getSingleEntryMap("id", ctx.getCurrentMerchantId()));
    }

    public Merchant getMerchantById(long merchantId) {
        return getMerchantWithWhereClause("WHERE id = :id",
                CollectionUtil.getSingleEntryMap("id", merchantId));
    }

    private Merchant getMerchantWithWhereClause(String whereClause, Map params) {
        Query query = em.createNativeQuery("SELECT  id, name, owner_name, " +
                "pan, phone, email, address, " +
                "city, state, zip, country FROM merchant WHERE id = :id");

        setParams(params, query);

        Object [] fields = (Object[]) query.getResultList().get(0);

        Merchant merchant = new Merchant();
        merchant.setId( ((BigInteger) fields[0]).longValue());
        merchant.setName((String) fields[1]);
        merchant.setOwnerName((String) fields[2]);
        merchant.setPan((String) fields[3]);
        merchant.setPhone((String) fields[4]);
        merchant.setEmail((String) fields[5]);
        merchant.setAddress(readAddress(6, fields));

        return merchant;
    }

    @RolesAllowed({Role.SYSTEM})
    public Merchant getInActiveMerchant() {
        return getMerchantWithWhereClause("WHERE active = false", Collections.EMPTY_MAP);
    }


    @RolesAllowed({Role.SYSTEM})
    public void changeStatus(long merchantId, boolean active) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant " +
                    "SET active = :active WHERE id = :id");

            query.setParameter("id", merchantId);
            query.setParameter("active", active);

            int count = query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }


    private void setBranchParams(MerchantBranch branch, Query query) {
        query.setParameter("id", branch.getId());
        query.setParameter("merchant_id", branch.getMerchantId());
        query.setParameter("head_quarter", branch.isHeadQuarter() ? true : null);

        query.setParameter("name", branch.getName());
        query.setParameter("phone", branch.getPhone());
        query.setParameter("manager_name", branch.getManagerName());
        query.setParameter("email", branch.getEmail());
        setPhysicalAddressParams(query, branch.getAddress());
    }

    private void createBranch(MerchantBranch branch) {
        Query query = em.createNativeQuery("INSERT INTO merchant_branch ( " +
                "id, merchant_id, name, manager_name, password, head_quarter, phone, email, " +
                "address, city, state, zip, country) " +
                "VALUES( :id, :merchant_id, :name, :manager_name, :password, :head_quarter, :phone, " +
                ":email, :address, :city, :state, :zip, :country)");

        branch.setId(nextId());

        query.setParameter("password", branch.getPassword());
        setBranchParams(branch, query);

        query.executeUpdate();

        MerchantCounter counter = new MerchantCounter();
        counter.setPhone(branch.getPhone());
        counter.setBranchId(branch.getId());

        ServiceFactory.getInstance().getMerchantBranchService()._registerCounter(counter);
    }

    @RolesAllowed({Role.MERCHANT})
    public MerchantBranch registerBranch(MerchantBranch branch) {

        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            branch.setMerchantId(ctx.getCurrentMerchantId());

            branch.setHeadQuarter(false);
            createBranch(branch);

            trn.commit();

            branch.setPassword(null);

            return branch;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    public void updateBranch(MerchantBranch branch) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant_branch " +
                    "SET name = :name, manager_name = :manager_name, head_quarter = :head_quarter," +
                    " phone = :phone, email = :email, address = :address, city = :city, " +
                    "state = :state, zip = :zip, country = :country " +
                    "WHERE id = :id and mercant_id = :merchant_id");

            branch.setMerchantId(ctx.getCurrentMerchantId());

            setBranchParams(branch, query);
            query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    public List<MerchantBranch> getAllBranches() {
        return getBranchesOfWhereClause("WHERE merchant_id = :merchant_id",
                CollectionUtil.getSingleEntryMap("merchant_id", ctx.getCurrentMerchantId()));
    }

    List<MerchantBranch> getBranchesOfWhereClause(
            String whereClause, Map<String, Object> params) {
        Query query = em.createNativeQuery("SELECT  id, merchant_id, name, manager_name, " +
                "head_quarter, phone, email, address, city, state, zip, country " +
                "FROM merchant_branch " + whereClause);

        setParams(params, query);

        List rs = query.getResultList();

        List<MerchantBranch> branches = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            MerchantBranch branch = new MerchantBranch();
            branch.setId( ((BigInteger) fields[0]).longValue());
            branch.setMerchantId( ((BigInteger) fields[1]).longValue());
            branch.setName((String) fields[2]);
            branch.setManagerName((String) fields[3]);
            branch.setHeadQuarter((Boolean) fields[4]);
            branch.setPhone((String) fields[5]);
            branch.setEmail((String) fields[6]);
            branch.setAddress(readAddress(7, fields));

            branches.add(branch);
        }

        return branches;
    }

    public boolean authenticate(String merchantId, String password) {
        Query query = em.createNativeQuery("SELECT  count(*) FROM merchant " +
                "WHERE id = :id AND password = :password");

        query.setParameter("id", Long.parseLong(merchantId));
        query.setParameter("password", password);

        BigInteger count = (BigInteger) query.getSingleResult();

        return count.intValue() == 1;
    }
}
