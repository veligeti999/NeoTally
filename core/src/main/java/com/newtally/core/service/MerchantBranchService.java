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
                .getBranchesOfWhereClause("WHERE id = :branchId",
                CollectionUtil.getSingleEntryMap("branchId", ctx.getCurrentBranchId())).get(0);
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

        Query query = em.createNativeQuery("INSERT INTO branch_counter ( " +
                "branch_id, password, phone, email, active) " +
                "VALUES( :branch_id, :password, :phone, :email, true )");

        counter.setPassword(generateNewPassword());

        query.setParameter("branch_id", counter.getBranchId());
        query.setParameter("phone", counter.getPhone());
        query.setParameter("password", counter.getPassword());
        query.setParameter("email", counter.getEmail());

        query.executeUpdate();

        return counter;
    }


    public List<MerchantCounter> getCounters(Long branchId) {
        Query query = em.createNativeQuery("SELECT  branch_id, phone, email, password, id" +
                " FROM branch_counter WHERE branch_id = :branch_id");
        
        query.setParameter("branch_id", branchId);
        List rs = query.getResultList();

        List<MerchantCounter> counters = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            MerchantCounter counter = new MerchantCounter();
            counter.setBranchId(((BigInteger) fields[0]).longValue());
            counter.setPhone((String) fields[1]);
            counter.setEmail((String) fields[2]);
            counter.setPassword((String) fields[3]);
            counter.setId(((BigInteger) fields[4]).longValue());
            counters.add(counter);
            
        }

        return counters;

    }

    public void deactivateCounter(long counterId) {
    }

    public MerchantCounter resetCounter(String phone) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();

        try {
            Query query = em.createNativeQuery("UPDATE branch_counter ( " +
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

    public boolean authenticate(String username, String password) {
        Query query = em.createNativeQuery("SELECT  count(*) FROM merchant_branch " +
                "WHERE email = :email AND password = :password");

        query.setParameter("email", username);
        query.setParameter("password", password);

        BigInteger count = (BigInteger) query.getSingleResult();

        return count.intValue() == 1;
    }
    
    public String getUserId(String username, String password) {
        Query query = em.createNativeQuery("SELECT  id FROM merchant_branch " +
                "WHERE email = :email AND password = :password");

        query.setParameter("email", username);
        query.setParameter("password", password);

        BigInteger id = (BigInteger) query.getSingleResult();

        return id.toString();
    }

	public String getMerchantIdByBranchId(long branchId) {
		Query query = em.createNativeQuery("select merchant_id from merchant_branch where id=:id");
		query.setParameter("id", branchId);
		return query.getResultList().get(0).toString();
	}

	/**
	 * The branch number for a merchant starts with zero.This is used to created
	 * the deterministic key-chain for each branch
	 *
	 * @param merchantId
	 * @return
	 */
	public int getMaxBranchNoForAMerchant(long merchantId) {
		Query query = em.createNativeQuery("select max(branch_no) from merchant_branch where merchant_id=:merchantId");
		query.setParameter("merchantId", merchantId);
		return (int) query.getResultList().get(0);
	}

}
