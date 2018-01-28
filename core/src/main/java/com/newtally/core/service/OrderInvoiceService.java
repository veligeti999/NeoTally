package com.newtally.core.service;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.Wallet;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.Order;
import com.newtally.core.model.OrderStatus;
import com.newtally.core.resource.ThreadContext;
import com.newtally.core.wallet.BitcoinConfiguration;
import com.newtally.core.wallet.WalletManager;

public class OrderInvoiceService extends AbstractService{

	private final BranchCounterService counterService;
	private final WalletManager walletManager;

    public OrderInvoiceService(EntityManager em, ThreadContext sessionContext, BranchCounterService counterService, WalletManager walletManager) {
        super(em, sessionContext);
        this.counterService = counterService;
        this.walletManager = walletManager;
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
            order.setQrCode("http://kkalyan.com:8888/new-tally/images/dummy-QR.jpg");
            return order;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
        
    }
    
	private void setCreateParams(Order order, Query query) {
		String branchId = counterService.getBranchIdByCounterId(order.getCounterId());
		Map<String, Wallet> wallets = walletManager.getBranchWallets();
		Wallet wallet = wallets.get(branchId);
		DeterministicKey counterKey = wallet.freshReceiveKey();
		String address = counterKey.toAddress(walletManager.getBitcoinConfiguration().getParams()).toString();
		System.out.println(address);
		query.setParameter("id", order.getId());
		order.setWalletAddress(address);
		query.setParameter("wallet_address", address);
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
