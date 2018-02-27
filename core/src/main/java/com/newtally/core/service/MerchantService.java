package com.newtally.core.service;

import com.newtally.core.util.CollectionUtil;
import com.newtally.core.util.RandomNumberGenerator;
import com.newtally.core.wallet.WalletManager;
import com.newtally.core.ServiceFactory;
import com.newtally.core.dto.CoinDto;
import com.newtally.core.dto.DiscountDto;
import com.newtally.core.dto.ResponseDto;
import com.newtally.core.dto.WalletDto;
import com.newtally.core.model.*;
import com.newtally.core.resource.ThreadContext;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.bitcoinj.core.Coin;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException;
import org.bitcoinj.utils.MonetaryFormat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MerchantService extends AbstractService implements IAuthenticator {

    public MerchantService(EntityManager em, ThreadContext ctx ) {
        super(em, ctx);
    }

    public Merchant registerMerchant(Merchant merchant) throws Exception {
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

            long merchantId = nextId();
            merchant.setId(merchantId);
            setCreateParams(merchant, query);
            query.executeUpdate();
            //generating mnemonic and setting it to the merchant
            //TO-DO -> we need to store the encrypted the mnemonic string in the DB
            List<String> mnemonicWords= this.generateMnemonic();
            this.setMnemonicForMerchant(String.join(",", mnemonicWords), merchantId);
            if(!EmailService.emailValidator(merchant.getEmail())) {
                throw new RuntimeException("Email doesn't existed");
            }
            MerchantBranch branch = new MerchantBranch();
            branch.setMerchantId(merchant.getId());
            branch.setName(merchant.getName());
            branch.setManagerName(merchant.getOwnerName());
            branch.setPassword(merchant.getPassword());
            branch.setPhone(merchant.getPhone());
            branch.setEmail(merchant.getEmail());
            branch.setAddress(merchant.getAddress());
            branch.setHeadQuarter(true);
            branch.setBranchNo(0);

            createBranch(branch);
            
            createDiscounts(merchant.getId());
            trn.commit();
            ServiceFactory.getInstance().getApplicationService().generateConfirmEmailLink(merchant.getEmail());
//            sendNotification(merchant);
            merchant.setPassword(null);
            //
            return merchant;
        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    private void sendNotification(Merchant merchant) {
        EmailService.sendEmail(merchant.getEmail(), "Merchant Registration", " Welcome to NewTally \n \n Merchant has been registered successfully \n\n Password:"+merchant.getPassword()+"\n\n From \n Newtally.com");
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
        
        System.out.println("params;;;;"+params);
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
        System.out.println(branch);
        query.setParameter("id", branch.getId());
        query.setParameter("merchant_id", branch.getMerchantId());
        query.setParameter("head_quarter", branch.isHeadQuarter() ? true : false);

        query.setParameter("name", branch.getName());
        query.setParameter("phone", branch.getPhone());
        query.setParameter("manager_name", branch.getManagerName());
        query.setParameter("email", branch.getEmail());
        query.setParameter("branch_no", branch.getBranchNo());
        setPhysicalAddressParams(query, branch.getAddress());
    }

    private void createBranch(MerchantBranch branch) {
        Query query = em.createNativeQuery("INSERT INTO merchant_branch ( " +
                "id, merchant_id, name, manager_name, password, head_quarter, phone, email, " +
                "address, city, state, zip, country, branch_no) " +
                "VALUES( :id, :merchant_id, :name, :manager_name, :password, :head_quarter, :phone, " +
                ":email, :address, :city, :state, :zip, :country, :branch_no)");

        long branchId = nextId();
        branch.setId(branchId);

        query.setParameter("password", branch.getPassword());
        setBranchParams(branch, query);
        System.out.println("branchId"+branchId);
        query.executeUpdate();
        MerchantCounter counter = new MerchantCounter();
        counter.setName(branch.getName());
        counter.setPhone(branch.getPhone());
        counter.setEmail(branch.getEmail());
        counter.setBranchId(branch.getId());

        ServiceFactory.getInstance().getMerchantBranchService()._registerCounter(counter);
        sendNotificationForCounter(counter);
    }

    private void createDiscounts(Long merchantId) {
        Query query = em.createNativeQuery("select id from currency where active=true");
        List<Integer> currencyIds = query.getResultList();
        for(Integer currencyId:currencyIds) {
            Query queryForCreateDiscout = em.createNativeQuery("INSERT INTO discount (currency_id, merchant_id) values(:currencyId, :merchantId)");
            queryForCreateDiscout.setParameter("merchantId", merchantId);
            queryForCreateDiscout.setParameter("currencyId", currencyId);
            queryForCreateDiscout.executeUpdate();
        }
    }

    @RolesAllowed({Role.MERCHANT})
    public MerchantBranch registerBranch(MerchantBranch branch) {

        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            branch.setMerchantId(ctx.getCurrentMerchantId());
            branch.setHeadQuarter(false);
            //incrementing the branch_no based on the previous branch
            int existingBranchNumber = ServiceFactory.getInstance().getMerchantBranchService().getMaxBranchNoForAMerchant(ctx.getCurrentMerchantId());
            branch.setBranchNo(existingBranchNumber+1);
            createBranch(branch);
            System.out.println("branch"+branch.getEmail());
            trn.commit();
            sendNotificationForBranch(branch);
            branch.setPassword(null);

            return branch;
        } catch (Exception e) {
            e.printStackTrace();
            trn.rollback();
            throw e;
        }
    }
    private void sendNotificationForBranch(MerchantBranch branch) {
        EmailService.sendEmail(branch.getEmail(), "Branch Registration", " Welcome to NewTally \n \n Branch has been registered successfully \n\n Password:"+branch.getPassword()+"\n\n From \n Newtally.com");
    }
    public void updateBranch(MerchantBranch branch) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant_branch " +
                    "SET name = :name, manager_name = :manager_name, head_quarter = :head_quarter," +
                    " phone = :phone, email = :email, address = :address, city = :city, " +
                    "state = :state, zip = :zip, country = :country, branch_no =:branch_no " +
                    "WHERE id = :id and merchant_id = :merchant_id");

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

    public boolean authenticate(String username, String password) {
        Query query = em.createNativeQuery("SELECT  count(*) FROM merchant " +
                "WHERE email = :email AND password = :password AND active=true");

        query.setParameter("email", username);
        query.setParameter("password", password);

        BigInteger count = (BigInteger) query.getSingleResult();

        return count.intValue() == 1;
    }
    
    public String getUserId(String username, String password) {
        Query query = em.createNativeQuery("SELECT  id FROM merchant " +
                "WHERE email = :email AND password = :password");

        query.setParameter("email", username);
        query.setParameter("password", password);

        BigInteger id = (BigInteger) query.getSingleResult();

        return id.toString();
    }

    public List<Order> getAllOrders() {
        List<MerchantBranch> branches=getAllBranches();
        List<Long> counterIds=new ArrayList<>();
        
        for(MerchantBranch branch:branches) {
            List<MerchantCounter> counters=ServiceFactory.getInstance().getMerchantBranchService().getCounters(branch.getId());
            counterIds.addAll(counters.stream()
                    .map(MerchantCounter::getId).collect(Collectors.toList()));
        }
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

    /**
     * Setting mnemonic for a merchant during merchant registration.This is where the entire
     * tree of keys get generated from.
     * @param mnemonic
     * @param merchantId
     */
	public void setMnemonicForMerchant(String mnemonic, long merchantId) {
		Query query = em.createNativeQuery("insert into mnemonic(mnemonic_id, mnemonic_code, created_on, merchant_id)"
				+ "values(:mnemonic_id, :mnemonic_code, :created_on, :merchant_id)");
		query.setParameter("mnemonic_id", nextId());
		query.setParameter("mnemonic_code", mnemonic);
		query.setParameter("created_on", new Date().getTime());
		query.setParameter("merchant_id", merchantId);
		query.executeUpdate();
	}

    /**
     * Generating 24 mnemonic words during merchant registration
     * @return
     * @throws MnemonicLengthException
     */
	private List<String> generateMnemonic() throws MnemonicLengthException {
		return MnemonicCode.INSTANCE.toMnemonic(new RandomNumberGenerator().getRandomNumber());
	}

	public String getMnenonicForAMerchant(long merchantId) {
		Query query = em.createNativeQuery("select mnemonic_code from mnemonic where merchant_id=:merchant_id");
	    query.setParameter("merchant_id", merchantId);
        return query.getResultList().get(0).toString();
	}
	
	 public MerchantCounter registerCounterForBranch(MerchantCounter counter) {

	        EntityTransaction trx = em.getTransaction();

	        trx.begin();
	        try {
	            counter = ServiceFactory.getInstance().getMerchantBranchService()._registerCounter(counter);

	            trx.commit();
	            sendNotificationForCounter(counter);
	            return counter;
	        } catch (Exception e) {
	            trx.rollback();

	            throw e;
	        }
	    }
	 
	 private void sendNotificationForCounter(MerchantCounter counter) {
	        EmailService.sendEmail(counter.getEmail(), "Counter Registration", " Welcome to NewTally \n \n Counter has been registered successfully \n\n Password:"+counter.getPassword()+"\n\n From \n Newtally.com");
	    }
	 public List<DiscountDto> getDisounts(){
	     Query query = em.createNativeQuery("select d.id, c.code, c.name, d.percentage from discount d join currency c on d.currency_id = c.id where merchant_id=:merchant_id");
	        query.setParameter("merchant_id", ctx.getCurrentMerchantId());
	        List rs = query.getResultList();
	        List<DiscountDto> discounts = new ArrayList<>();
	        for(Object ele : rs) {
	            Object [] fields = (Object[]) ele;

	            DiscountDto discount = new DiscountDto();
	            discount.setId(((Integer) fields[0]));
	            discount.setCurrencyCode( fields[1].toString());	          
	            discount.setCurrencyName(fields[2].toString());
	            discount.setPercentage((Double)fields[3]);
	            
	            discounts.add(discount);
	        }
	        return discounts;
	    }

    public DiscountDto saveDisounts(DiscountDto discount) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE discount SET percentage = :percentage " +
                    "WHERE id = :id");

            query.setParameter("percentage", discount.getPercentage());
            query.setParameter("id", discount.getId());
            query.executeUpdate();

            trn.commit();
            return discount;

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
        
    }

	/**
	 * The merchant wallet balance is the summation of all the merchant related
	 * branch wallets Right now it's only for Bitcoin. Litecoin and ethereum are
	 * on the way
	 *
	 * @param merchantId
	 * @return
	 * @throws Exception
	 */
	public CoinDto getWalletBalance(Long merchantId, Long branchId) throws Exception {
		double totalCoins = 0.0;
		double coinINRValue = 0.0;
		List<BigInteger> branchIds = null;;
		if (merchantId != null) {
			branchIds = ServiceFactory.getInstance().getMerchantBranchService().getBranchIdsByMerchantId(merchantId);
		}else{
			branchIds = new ArrayList<>();
			branchIds.add(new BigInteger(String.valueOf(branchId)));
		}
		long totalSatoshis = ServiceFactory.getInstance().getWalletManager().getBitcoinWalletBalance(branchIds);
		if(totalSatoshis != 0){
			totalCoins = (double)totalSatoshis/Coin.COIN.getValue();
		    //get the current INR value of a bitcoin to calculate the total bitcoins value in INR
			coinINRValue = ServiceFactory.getInstance().getMerchantCounterService().getBitCoinCostInINR();
		}
		CoinDto coin = new CoinDto();
		coin.setCoinName("Bitcoin");
		coin.setCoinCode(MonetaryFormat.CODE_BTC);
		coin.setCoinValue(totalCoins);
		coin.setCoinValueInCurrency(Math.round(totalCoins * coinINRValue));
		return coin;
	}

	public void withdrawCoinsFromMerchantWallet(Integer currencyId) throws Exception{
		try{
		Long merchantId = ServiceFactory.getInstance().getSessionContext().getCurrentMerchantId();
		String merchantWalletAddress =  getMerchantPersonalWalletAddress(merchantId);
		List<BigInteger> branchIds = ServiceFactory.getInstance().getMerchantBranchService().getBranchIdsByMerchantId(merchantId);
		ServiceFactory.getInstance().getWalletManager().withdrawCoinsFromMerchantWallet(branchIds, merchantWalletAddress, getAdminWalletAddress(), currencyId);
		}catch(Exception e){
			throw e;
		}
	}

    public void updatePassword(HashMap passwordMap) {
        Query queryForPassword = em.createNativeQuery("SELECT  password FROM merchant WHERE id = :id");
        queryForPassword.setParameter("id", ctx.getCurrentMerchantId());
        String password = (String) queryForPassword.getSingleResult();
        if(password.equals(passwordMap.get("currentPassword"))) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant SET password = :password " +
                    "WHERE id = :id");

            query.setParameter("password", passwordMap.get("newPassword"));
            query.setParameter("id", ctx.getCurrentMerchantId());
            query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
        }
        else {
            throw new RuntimeException("Password doesn't match");
        }
    }

    public MerchantCounter updateCounter(MerchantCounter counter) {
            EntityTransaction trx = em.getTransaction();

            trx.begin();
            try {
                counter = ServiceFactory.getInstance().getMerchantBranchService().updateCounter(counter);

                trx.commit();

                return counter;
            } catch (Exception e) {
                e.printStackTrace();
                trx.rollback();

                throw e;
            }
    }

    @Override
    public boolean checkEmail(String email) {
        Query query = em.createNativeQuery("select email from merchant where email=:email");
        query.setParameter("email", email);
        List rs=query.getResultList();
        if(rs.isEmpty())
            return false;
        else
            return true;
    }

    @Override
    public void resetPassword(String email, String password) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant SET password = :password " +
                    "WHERE email = :email");

            query.setParameter("password", password);
            query.setParameter("email", email);
            query.executeUpdate();

            trn.commit();

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    public void updateMerchantAfterCofirmEmail(String email) {
            Query query = em.createNativeQuery("UPDATE merchant SET active = true " +
                    "WHERE email = :email");

            query.setParameter("email", email);
            query.executeUpdate();
    }

    public List<WalletDto> getWalletAddress() {
        Query query = em.createNativeQuery("select w.id, w.currency_id, c.code, c.name, w.wallet_address from merchant_personal_wallet w join currency c on w.currency_id = c.id where merchant_id=:merchant_id");
        query.setParameter("merchant_id", ctx.getCurrentMerchantId());
        List rs = query.getResultList();
        List<WalletDto> wallets = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            WalletDto wallet = new WalletDto();
            wallet.setId(((Integer) fields[0]));
            wallet.setCurrencyId(((Integer) fields[1]));
            wallet.setCurrencyCode( fields[2].toString());              
            wallet.setCurrencyName(fields[3].toString());
            wallet.setWalletAddress(fields[4].toString());
            
            wallets.add(wallet);
        }
        return wallets;
    }

    public WalletDto saveWalletAddress(WalletDto wallet) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("INSERT INTO merchant_personal_wallet(currency_id, merchant_id, wallet_address)" +
                    " values(:currency_id, :merchant_id, :wallet_address)");

            query.setParameter("currency_id", wallet.getCurrencyId());
            query.setParameter("merchant_id", ctx.getCurrentMerchantId());
            query.setParameter("wallet_address", wallet.getWalletAddress());
            query.executeUpdate();

            trn.commit();
            return wallet;

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

    public WalletDto updateWalletAddress(WalletDto wallet) {
        EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("UPDATE merchant_personal_wallet SET wallet_address=:wallet_address where id=:id");

            query.setParameter("id", wallet.getId());
            query.setParameter("wallet_address", wallet.getWalletAddress());
            query.executeUpdate();

            trn.commit();
            return wallet;

        } catch (Exception e) {
            trn.rollback();
            throw e;
        }
    }

	public String getMerchantPersonalWalletAddress(long merchantId) throws Exception{
		String walletAddress = null;
		Query query = em.createNativeQuery(
				"select  wallet_address from merchant_personal_wallet where merchant_id =:merchantId");
		query.setParameter("merchantId", merchantId);
		if(query.getResultList().isEmpty()){
			throw new Exception("Address Undefined");
		}else{
			walletAddress = query.getResultList().get(0).toString();
		}
		return walletAddress;
	}

	public String getAdminWalletAddress() {
		Query query = em.createNativeQuery("select config_value from admin_configuration where config_key='wallet_address'");
		return query.getResultList().get(0).toString();
	}
	
	public void createWithdrawTransaction(Integer currencyId, String walletAddress, Double transactionAmount, Double commissionAmount, String commissionWalletAddress) {
	    EntityTransaction trn = em.getTransaction();
        trn.begin();
        try {
            Query query = em.createNativeQuery("INSERT INTO withdrawal_transaction_history(id, currency_id, to_wallet_address,"
                    + " merchant_id, transaction_date, transaction_amount, commission_amount, transaction_status) "
                    + "VALUES( :id, :currencyId, :walletAddress, :merchantId, :transactionDate, :transactionAmount, :commissionAmount, :transactionStatus)");
            Long id=nextId();
            query.setParameter("id", id);
            query.setParameter("currencyId", currencyId);
            query.setParameter("walletAddress", walletAddress);
            query.setParameter("merchantId", ctx.getCurrentMerchantId());
            query.setParameter("transactionDate", new Date());
            query.setParameter("transactionAmount", transactionAmount);
            query.setParameter("commissionAmount", commissionAmount);
            query.setParameter("transactionStatus", true);
            System.out.println("transactionAmount"+transactionAmount);
            System.out.println("commissionAmount"+commissionAmount);
            query.executeUpdate();
            //createCommissionTransaction(currencyId, commissionWalletAddress, commissionAmount, id);
            trn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            trn.rollback();
            throw e;
        }
	}
	
	public void createCommissionTransaction(Integer currencyId, String walletAddress, Double commissionAmount, Long withdrawlTransactionHistoryId) {
        try {
            Query query = em.createNativeQuery("INSERT INTO commission_transaction_history(id, currency_id, to_wallet_address,"
                    + " merchant_id, transaction_date, withdrawl_transaction_history_id, commission_amount, transaction_status) "
                    + "VALUES( :id, :currencyId, :walletAddress, :merchantId, :transactionDate, :withdrawlTransactionHistoryId,"
                    + " :commissionAmount, :transactionStatus)");
            Long id=nextId();
            query.setParameter("id", id);
            query.setParameter("currencyId", currencyId);
            query.setParameter("walletAddress", walletAddress);
            query.setParameter("merchantId", ctx.getCurrentMerchantId());
            query.setParameter("transactionDate", new Date());
            query.setParameter("withdrawlTransactionHistoryId", withdrawlTransactionHistoryId);
            query.setParameter("commissionAmount", commissionAmount);
            query.setParameter("transactionStatus", true);
            query.executeUpdate();

        } catch (Exception e) {
            throw e;
        }
	}

    public List<Withdrawal> getWithdrawals() {
        Query query = em.createNativeQuery("select c.code, c.name, h.id, h.transaction_date, "
                + "h.to_wallet_address, h.transaction_amount, h.commission_amount, h.transaction_status"
                + " from withdrawal_transaction_history h join currency c on h.currency_id=c.id "
                + "where h.merchant_id=:merchantId");
        query.setParameter("merchantId", ctx.getCurrentMerchantId());
        List rs=query.getResultList();
        List<Withdrawal> withdrawals = new ArrayList<>();
        for(Object ele : rs) {
            Object [] fields = (Object[]) ele;

            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setCurrencyCode((String)fields[0]);
            withdrawal.setCurrencyName((String)fields[1]);
            withdrawal.setId(((BigInteger) fields[2]).longValue());
            withdrawal.setTransactionDate((Date)fields[3]);
            withdrawal.setWalletAddress((String)fields[4]);
            withdrawal.setTransactionAmount((Double)fields[5]);
            withdrawal.setCommissionAmount((Double)fields[6]);
            withdrawal.setStatus((Boolean)fields[7]);
            withdrawals.add(withdrawal);
        }
        return withdrawals;
    }
}
