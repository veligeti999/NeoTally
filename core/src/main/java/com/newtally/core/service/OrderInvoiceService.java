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
            order.setQrCode("https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl="+order.getWalletAddress());
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
		//this piece of code is used for testing hooks
		DeterministicKey watchingKey = wallet.getActiveKeyChain().getWatchingKey();
		System.out.println("watching key path"+watchingKey.getPath());
		System.out.println("key address"+watchingKey.toAddress(walletManager.getBitcoinConfiguration().getParams()));
		DeterministicKey counterKey = wallet.freshReceiveKey();
		System.out.println("counterKey"+counterKey);
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

	public void updateOrderStatus(String transactionId, String address, String status) {
		EntityTransaction txn = em.getTransaction();
		txn.begin();
		try {
			Query query = em.createNativeQuery(
					"update order_invoice set status=:status, transaction_id=:transactionId where wallet_address=:address");
			query.setParameter("status", status);
			query.setParameter("transactionId", transactionId);
			query.setParameter("address", address);
			query.executeUpdate();
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			throw e;
		}
	}

	public void updateOrderStatusByTransactionId(String transactionId, String status) {
		EntityTransaction txn = em.getTransaction();
		txn.begin();
		try {
			Query query = em
					.createNativeQuery("update order_invoice set status=:status where transaction_id=:transactionId");
			query.setParameter("status", status);
			query.setParameter("transactionId", transactionId);
			query.executeUpdate();
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			throw e;
		}
	}
}
