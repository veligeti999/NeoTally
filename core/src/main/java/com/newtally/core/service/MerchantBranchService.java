package com.newtally.core.service;

import com.newtally.core.util.CollectionUtil;
import com.newtally.core.ServiceFactory;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.resource.ThreadContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MerchantBranchService extends AbstractService implements IAuthenticator {

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

        EntityTransaction trx = em.getTransaction();

        trx.begin();
        try {
            counter = _registerCounter(counter);

            trx.commit();

            return counter;
        } catch (Exception e) {
            trx.rollback();

            throw e;
        }
    }

    MerchantCounter _registerCounter(MerchantCounter counter) {

        Query query = em.createNativeQuery("INSERT INTO merchant_counter ( " +
                "branch_id, password, phone, active) " +
                "VALUES( :branch_id, :password, :phone, true )");

        counter.setPassword(generateNewPassword());

        query.setParameter("branch_id", counter.getBranchId());
        query.setParameter("phone", counter.getPhone());
        query.setParameter("password", counter.getPassword());

        query.executeUpdate();

        return counter;
    }


    public List<MerchantCounter> getCounters() {
        Query query = em.createNativeQuery("SELECT  branch_id, phone, password " +
                " FROM merchant_counter WHERE branch_id = :branch_id");

        List rs = query.getResultList();

        List<MerchantCounter> branches = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            MerchantCounter counter = new MerchantCounter();
            counter.setBranchId(((BigInteger) fields[0]).longValue());
            counter.setPhone((String) fields[1]);
            counter.setPassword((String) fields[2]);
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
                    "SET :password = :password, active = true  " +
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

    public boolean authenticate(String merchantId, String password) {
        Query query = em.createNativeQuery("SELECT  count(*) FROM merchant_branch " +
                "WHERE id = :id AND password = :password");

        query.setParameter("id", Long.parseLong(merchantId));
        query.setParameter("password", password);

        BigInteger count = (BigInteger) query.getSingleResult();

        return count.intValue() == 1;
    }

}
