package com.newtally.core.service;

import com.newtally.core.ServiceFactory;
import com.newtally.core.dto.CounterDto;
import com.newtally.core.dto.CurrencyDiscountDto;
import com.newtally.core.model.CoinType;
import com.newtally.core.model.Currency;
import com.newtally.core.model.Merchant;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.MerchantConfiguration;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Order;
import com.newtally.core.model.OrderStatus;
import com.newtally.core.model.Role;
import com.newtally.core.resource.ThreadContext;
import com.newtally.core.util.CollectionUtil;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchCounterService extends AbstractService implements IAuthenticator {

    public BranchCounterService(EntityManager em, ThreadContext ctx) {
        super(em, ctx);
    }

    @RolesAllowed(Role.BRANCH_COUNTER)
    public String handlePayment(CoinType type, double amount) {
        return null;
    }

    @RolesAllowed({Role.BRANCH_COUNTER})
    public MerchantConfiguration getConfiguration() {
        return null;
    }

    public boolean authenticate(String merchantId, String password) {
        Query query = em.createNativeQuery("SELECT  count(*) FROM branch_counter " +
                "WHERE password = :password AND active = true");

        query.setParameter("password", password);

        BigInteger count = (BigInteger) query.getSingleResult();

        return count.intValue() == 1;
    }
    
    public MerchantCounter getCurrentCounter() {
        return getCountersOfWhereClause("WHERE password = :counterCode",
                CollectionUtil.getSingleEntryMap("counterCode", ctx.getCurrentCounterCode())).get(0);
    }
    
    List<MerchantCounter> getCountersOfWhereClause(
            String whereClause, Map<String, Object> params) {
        Query query = em.createNativeQuery("SELECT  id, branch_id, password, phone, email, active " +
                "FROM branch_counter " + whereClause);

        setParams(params, query);

        List rs = query.getResultList();

        List<MerchantCounter> counters = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            MerchantCounter counter = new MerchantCounter();
            counter.setId(((BigInteger) fields[0]).longValue());
            counter.setBranchId( ((BigInteger) fields[1]).longValue());
            counter.setPassword((String) fields[2]); 
            counter.setPhone((String) fields[3]);
            counter.setEmail((String) fields[4]);
           
            
            counters.add(counter);
        }

        return counters;
    }
    public CounterDto getCounterDetails(){
        CounterDto counterDto=new CounterDto();
        MerchantCounter counter=getCurrentCounter();
        MerchantBranch branch=ServiceFactory.getInstance().getMerchantService()
                .getBranchesOfWhereClause("WHERE id = :branchId",
                CollectionUtil.getSingleEntryMap("branchId", counter.getBranchId())).get(0);
        Merchant merchant=ServiceFactory.getInstance().getMerchantService().getMerchantById(branch.getMerchantId());
        counterDto.setAddress(branch.getAddress());
        counterDto.setBranch_name(branch.getName());
        counterDto.setCounter_id(counter.getId());
        counterDto.setCounter_name("Counter #"+counter.getId());
        counterDto.setMerchant_name(merchant.getName());
        counterDto.setEmail(counter.getEmail());
         return counterDto;       
    }

    public List<CurrencyDiscountDto> getCurrencyDiscounts(Double paymentAmount) {
        Query query = em.createNativeQuery("SELECT  id, code, name FROM currency") ;

        List rs = query.getResultList();
        List<Currency> currencies = new ArrayList<>();
        List<CurrencyDiscountDto> currencyDiscountDtos=new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            Currency currency = new Currency();
            CurrencyDiscountDto currencyDiscountDto=new CurrencyDiscountDto();
            currency.setId(((Integer) fields[0]).longValue());
            currency.setCode(CoinType.valueOf((String) fields[1])); 
            currency.setName((String) fields[2]);
            currencyDiscountDto.setCurrency_id(currency.getId());
            currencyDiscountDto.setCurrency_code(currency.getCode().toString());
            currencyDiscountDto.setCurrency_name(currency.getName());
            currencyDiscountDto.setDiscount(20d);
            currencyDiscountDto.setDiscount_amount(paymentAmount*currencyDiscountDto.getDiscount()/100);
           
            currencyDiscountDtos.add(currencyDiscountDto);
            currencies.add(currency);
        }
        return currencyDiscountDtos;
    }

    public List<Order> getOrders(HashMap<String, Object> input) {
        Query query = em.createNativeQuery("SELECT  id, currency_amount, discount_amount,currency_id, "+
                                            "currency_code, status FROM order_invoice where counter_id=:counter_id");
        query.setParameter("counter_id", input.get("counter_id"));
        List rs = query.getResultList();
        List<Order> orders=new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;
            Order order=new Order();
            order.setId(((BigInteger) fields[0]).longValue());
            order.setCurrencyAmount((Double)fields[1]);
            order.setDiscountAmount((Double)fields[2]);
            order.setCurrencyId((Integer)fields[3]);
            order.setCurrencyCode((String)fields[4]);
            order.setStatus(OrderStatus.valueOf((String)fields[5]));
            orders.add(order);
        }
        return orders;
    }

    public CounterDto getCounterById(long id) {
        CounterDto counterDto=new CounterDto();
        MerchantCounter counter=getCountersOfWhereClause("WHERE id = :id",
                CollectionUtil.getSingleEntryMap("id", id)).get(0);
        MerchantBranch branch=ServiceFactory.getInstance().getMerchantService()
                .getBranchesOfWhereClause("WHERE id = :branchId",
                CollectionUtil.getSingleEntryMap("branchId", counter.getBranchId())).get(0);
        Merchant merchant=ServiceFactory.getInstance().getMerchantService().getMerchantById(branch.getMerchantId());
        counterDto.setAddress(branch.getAddress());
        counterDto.setBranch_name(branch.getName());
        counterDto.setCounter_id(counter.getId());
        counterDto.setCounter_name("Counter #"+counter.getId());
        counterDto.setMerchant_name(merchant.getName());
         return counterDto;  
    }

    public List<Order> getAllOrders() {
        Query query = em.createNativeQuery("SELECT  id, currency_amount, discount_amount,currency_id, "+
                "currency_code, status FROM order_invoice where counter_id=:counter_id");
            query.setParameter("counter_id", ctx.getCurrentCounterCode());
            List rs = query.getResultList();
            List<Order> orders=new ArrayList<>();
            for(Object ele : rs) {
            Object [] fields = (Object[]) ele;
            Order order=new Order();
            order.setId(((BigInteger) fields[0]).longValue());
            order.setCurrencyAmount((Double)fields[1]);
            order.setDiscountAmount((Double)fields[2]);
            order.setCurrencyId((Integer)fields[3]);
            order.setCurrencyCode((String)fields[4]);
            order.setStatus(OrderStatus.valueOf((String)fields[5]));
            orders.add(order);
            }
           return orders;
    }

    public String getBranchIdByCounterPwd(String counterPassword){
        Query query = em.createNativeQuery("select branch_id from branch_counter where password=:counterPassword");
        query.setParameter("counterPassword", counterPassword);
        return query.getResultList().get(0).toString();
    }

    public String getBranchIdByCounterId(int counterId){
        Query query = em.createNativeQuery("select branch_id from branch_counter where id=:id");
        query.setParameter("id", counterId);
        return query.getResultList().get(0).toString();
    }
}
