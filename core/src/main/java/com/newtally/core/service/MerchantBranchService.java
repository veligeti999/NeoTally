package com.newtally.core.service;

import com.newtally.core.CollectionUtil;
import com.newtally.core.ServiceFactory;
import com.newtally.core.model.Merchant;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.resource.ThreadContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MerchantBranchService extends AbstractService{

    public MerchantBranchService(EntityManager em, ThreadContext ctx) {
        super(em, ctx);
    }

    public MerchantBranch getCurrentBranch() {
        return ServiceFactory.getInstance().getMerchantService()
                .getBranchesOfWhereClause("WHERE branch_id = :branchId",
                CollectionUtil.getSingleEntryMap("branch_id", ctx.getCurrentBranchId())).get(0);
    }

    public MerchantCounter registerCounter(MerchantCounter counter) {
        counter.setBranchId(ctx.getCurrentBranchId());

        return _registerCounter(counter);
    }

    MerchantCounter _registerCounter(MerchantCounter counter) {

        EntityTransaction trn = em.getTransaction();
        trn.begin();

        try {
            Query query = em.createNativeQuery("INSERT INTO merchant_counter ( " +
                    "id, branch_id, password, phone) " +
                    "VALUES( :id, :branch_id, :password, :phone )");

            counter.setPhone(generateNewPassword());

            query.setParameter("id", nextId());
            query.setParameter("branch_id", counter.getBranchId());
            query.setParameter("phone", counter.getPhone());
            query.setParameter("password", counter.getPassword());

            query.executeUpdate();

            trn.commit();

            return counter;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }


    public List<MerchantCounter> getCounters() {
        Query query = em.createNativeQuery("SELECT  id, branch_id, phone, password " +
                " FROM merchant_counter WHERE branch_id = :branch_id");

        List rs = query.getResultList();

        List<MerchantCounter> branches = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            MerchantCounter counter = new MerchantCounter();
            counter.setId( ((BigInteger) fields[0]).longValue());
            counter.setBranchId(((BigInteger) fields[1]).longValue());
            counter.setPhone((String) fields[2]);
        }

        return branches;

    }

    public void deactivateCounter(long counterId) {
    }

    public MerchantCounter resetCounter(String phone) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();

        try {
            Query query = em.createNativeQuery("UPDATE merchant_counter ( " +
                    "SET :password = :password  " +
                    " WHERE branch_id = :branch_id AND phone = :phone");

            MerchantCounter counter = new MerchantCounter();
            counter.setPhone(phone);
            counter.setBranchId(ctx.getCurrentBranchId());
            counter.setPassword(generateNewPassword());

            query.setParameter("branch_id", counter.getBranchId());
            query.setParameter("password", counter);
            query.setParameter("phone", counter.getPhone());

            query.executeUpdate();

            trn.commit();

            return counter;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }
}
