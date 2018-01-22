package com.newtally.core.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.newtally.core.model.Order;
import com.newtally.core.model.OrderStatus;
import com.newtally.core.resource.ThreadContext;

public class OrderInvoiceService extends AbstractService{

    public OrderInvoiceService(EntityManager em, ThreadContext sessionContext) {
        super(em, sessionContext);
    }
    
    public Order createOrder(Order order) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            String q="INSERT INTO order_invoice ( " +
                    "id, wallet_address, currency_amount, discount_amount, currency_id, currency_code, counter_id, status) " +
                    "VALUES( :id, :wallet_address, :currency_amount, :discount_amount, :currency_id, :currency_code, :counter_id, :status)";
            Query query = em.createNativeQuery(q);

            order.setId(nextId());
            setCreateParams(order, query);
            query.executeUpdate();
            trn.commit();
            order.setQrCode("http://newTally.com/paymentOrder/qr_code/werwqerasd");
            return order;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
        
    }
    
    private void setCreateParams(Order order, Query query) {
        query.setParameter("id", order.getId());
        query.setParameter("wallet_address", "asdfsadf124124124124");
        query.setParameter("currency_amount", order.getCurrencyAmount());
        query.setParameter("discount_amount", order.getDiscountAmount());
        query.setParameter("currency_id", order.getCurrencyId());
        query.setParameter("currency_code", order.getCurrencyCode());
        query.setParameter("status", OrderStatus.Pending.toString());
        query.setParameter("counter_id", order.getCounterId());
    }
    
    public void cancelOrders(long id) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
        Query query = em.createNativeQuery("Update  order_invoice set  status=:status where id=:id");
        query.setParameter("id", id);
        query.setParameter("status", OrderStatus.Cancel.toString());
        query.executeUpdate();
        trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }
}
