package com.newtally.core.service;

import com.newtally.core.util.CollectionUtil;
import com.newtally.core.ServiceFactory;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Order;
import com.newtally.core.model.OrderStatus;
import com.newtally.core.resource.ThreadContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
                "branch_id, password, phone, email, active, name) " +
                "VALUES( :branch_id, :password, :phone, :email, true, :name )");

        counter.setPassword(generateNewPassword());

        query.setParameter("branch_id", counter.getBranchId());
        query.setParameter("phone", counter.getPhone());
        query.setParameter("password", counter.getPassword());
        query.setParameter("email", counter.getEmail());
        query.setParameter("name", counter.getName());

        query.executeUpdate();

        return counter;
    }
    
    public MerchantCounter updateCounter(MerchantCounter counter) {

        Query query = em.createNativeQuery("UPDATE branch_counter SET " +
                "branch_id=:branch_id, phone=:phone, email=:email, active=:active, name=:name where id=:id");

        counter.setPassword(generateNewPassword());

        query.setParameter("branch_id", counter.getBranchId());
        query.setParameter("phone", counter.getPhone());
        query.setParameter("email", counter.getEmail());
        query.setParameter("active", counter.getActive());
        query.setParameter("id", counter.getId());
        query.setParameter("name", counter.getName());

        query.executeUpdate();

        return counter;
    }
    
    public MerchantCounter updateCurrentBranchCounter(MerchantCounter counter) {
        EntityTransaction trx = em.getTransaction();

        trx.begin();
        try {
        counter.setBranchId(ctx.getCurrentBranchId());
        updateCounter(counter);
        return counter;
        } catch (Exception e) {
            e.printStackTrace();
            trx.rollback();

            throw e;
        }
    }


    public List<MerchantCounter> getCounters(Long branchId) {
        Query query = em.createNativeQuery("SELECT  branch_id, phone, email, password, id, active, name" +
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
            counter.setActive((Boolean) fields[5]);
            counter.setName((String) fields[6]);
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

	/**
	 * This is for generating the key-chain's for each branch
	 * @param branchId
	 * @return
	 */
	public int getBranchNoByBranchId(long branchId){
		Query query = em.createNativeQuery("select branch_no from merchant_branch where id=:branchId");
		query.setParameter("branchId", branchId);
		return (int) query.getResultList().get(0);
	}
	
    public List<MerchantCounter> getCounters() {
        // TODO Auto-generated method stub
        return getCounters(ctx.getCurrentBranchId());
    }

    public List<Order> getTransactions() {
            List<MerchantCounter> counters= getCounters(ctx.getCurrentBranchId());
            List<Long> counterIds=counters.stream()
                    .map(MerchantCounter::getId).collect(Collectors.toList());
        System.out.println("counterIds:::"+counterIds.size());
        Query queryForOrders = em.createNativeQuery("SELECT  id, currency_amount, discount_amount,currency_id, currency_code, status, created_date, payment_amount FROM order_invoice " +
                "WHERE counter_id IN :counterIds order by created_date desc");
        queryForOrders.setParameter("counterIds", counterIds);
        List rs = queryForOrders.getResultList();
        System.out.println(rs.size());
        List<Order> orders = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            Order order = new Order();
            order.setId(((BigInteger) fields[0]).longValue());
            order.setCurrencyAmount((Double)fields[1]);
            order.setDiscountAmount((Double)fields[2]);
            order.setCurrencyId((Integer)fields[3]);
            order.setCurrencyCode((String)fields[4]);
            order.setStatus(OrderStatus.valueOf((String)fields[5]));
            order.setCreatedDate((Date)fields[6]);
            order.setPaymentAmount((Double)fields[7]);
            orders.add(order);
        }
        return orders;
    }
    
	public List<BigInteger> getBranchIdsByMerchantId(long merchantId) {
		Query query = em.createNativeQuery("select id from merchant_branch where merchant_id=:merchant_id");
		query.setParameter("merchant_id", merchantId);
		return query.getResultList();
	}

    public void updatePassword(HashMap passwordMap) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant_branch SET password = :password " +
                    "WHERE id = :id");

            query.setParameter("password", passwordMap.get("newPassword"));
            query.setParameter("id", ctx.getCurrentBranchId());
            query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
        
    }
}
