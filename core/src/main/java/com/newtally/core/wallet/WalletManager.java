package com.newtally.core.wallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.protocols.channels.ValueOutOfRangeException;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletExtension;
import org.bitcoinj.wallet.WalletProtobufSerializer;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;

import com.google.common.util.concurrent.ListenableFuture;
import com.newtally.core.ServiceFactory;
import com.newtally.core.model.OrderStatus;
import com.newtally.core.resource.ThreadContext;

public class WalletManager {

	private BitcoinConfiguration configuration;
	private ThreadContext context;
	private Wallet wallet;
	private Map<String, Wallet> wallets = new HashMap<String, Wallet>();
	
	public WalletManager(BitcoinConfiguration configuration, ThreadContext context){
		this.configuration = configuration;
		this.context = context;
	}
	
	/**
	 * Creating a key chain from the mnemonic code by setting the seed time to the default seed time.
	 * This can be changed to the current time if required
	 * We are not using this at the moment.Right now the key chain is getting created while initializing the key chain group 
	 * @return
	 */
	private DeterministicKeyChain createDeterministicKeyChain(List<String> mnemonic){
		DeterministicSeed seed = new DeterministicSeed(mnemonic, null, "", MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
		return new NewTallyDeterministicKeyChain(seed);
	}
	
	/**
	 * Load the wallet from the file or create a new one if the file doesn't exist
	 * @return
	 * @throws UnreadableWalletException 
	 * @throws IOException
	 */
	public Wallet createOrLoadWallet(List<String> mnemonic, String branchId) throws UnreadableWalletException, IOException{
		File walletFile = new File(branchId);
		if(walletFile.exists()){
			wallet = loadWallet(walletFile);
		}else{
			wallet = new Wallet(configuration.getParams(), new NewTallyKeyChainGroup(configuration.getParams()));
			wallet.saveToFile(walletFile);
			wallet = loadWallet(walletFile);
		}
		autoSave(walletFile);
		registerWalletToListen();
		configuration.getBlockChain().addWallet(wallet);
		configuration.getPeerGroup().addWallet(wallet);
		wallets.put(branchId, wallet);
		return wallet;
	}
	
	public ThreadContext getThreadContext(){
		return context;
	}
	
	public Map<String, Wallet> getBranchWallets(){
		return wallets;
	}
	
	/**
	 * Loading wallet files by adding our custom key chain factory to the protocol buffer(used for de-serialization that supports BIP44)
	 * instead of relying on Wallet.loadFromFile(walletFile, walletExtension) that uses a default key chain factory
	 * @param walletFile
	 * @return
	 * @throws UnreadableWalletException
	 * @throws FileNotFoundException
	 */
	public Wallet loadWallet(File walletFile) throws UnreadableWalletException, FileNotFoundException{
		FileInputStream stream = new FileInputStream(walletFile);
		WalletProtobufSerializer protoBuf = new WalletProtobufSerializer();
		protoBuf.setKeyChainFactory(new NewTallyKeyChainFactory());
		wallet = protoBuf.readWallet(stream, new WalletExtension[0]);
		return wallet;
	}
	
	public void autoSave(File walletFile){
		wallet.autosaveToFile(walletFile, 5, TimeUnit.SECONDS, null);
	}
	
	public BitcoinConfiguration getBitcoinConfiguration(){
		return configuration;
	}
	
	public long getBalance(Wallet wallet){
		return wallet.getBalance().getValue();
	}

	/**
	 * Enable wallet to listen on to various events
	 */
	public void registerWalletToListen(){
		wallet.addChangeEventListener(new WalletChangeEventListener() {
			
			@Override
			public void onWalletChanged(Wallet wallet) {
				// TODO Auto-generated method stub
				String transactionId;
				Iterator<Transaction> itr = wallet.getTransactions(false).iterator();
				while(itr.hasNext()){
					Transaction transaction = (Transaction)itr.next();
					transactionId = transaction.getHash().toString();
					System.out.println("transaction depth"+transaction.getConfidence().getDepthInBlocks() );
					if(transaction.getConfidence().getDepthInBlocks() == 1){
						if(ServiceFactory.getInstance().getOrderInvoiceService().checkTransaction(transactionId)){
						    ServiceFactory.getInstance().getOrderInvoiceService().updateOrderStatusByTransactionId(transactionId, OrderStatus.Success.toString());
						}
					}
				}
			}
		});
		
		wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
			
			@Override
			public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
				String address;
				String transactionId;
				try {
					List<TransactionOutput> walletTxnOutputs = tx.getWalletOutputs(wallet);
					for (TransactionOutput txnOutput : walletTxnOutputs) {
						address = txnOutput.getAddressFromP2PKHScript(configuration.getParams()).toString();
						transactionId = txnOutput.getParentTransaction().getHash().toString();
						// update the transactionId for the address to which the
						// coins are sent
						if(ServiceFactory.getInstance().getOrderInvoiceService().checkAddress(address)){
						    ServiceFactory.getInstance().getOrderInvoiceService().updateOrderStatus(transactionId, address,
								OrderStatus.Pending.toString());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		wallet.addCoinsSentEventListener(new WalletCoinsSentEventListener() {
			
			@Override
			public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	/**
	 * summation of all the merchant related wallet balances
	 * @param walletIds
	 * @return
	 */
	public Long getBitcoinWalletBalance(List<BigInteger> walletIds){
		Wallet wallet;
		long balance = 0L;
		for(BigInteger walletId : walletIds){
			wallet = wallets.get(walletId.toString());
			balance += getBalance(wallet);
		}
		return balance;
	}

	public void withdrawCoinsFromMerchantWallet(List<BigInteger> walletIds, String merchantWalletAddress, String adminWalletAddress, Integer currencyId) throws InsufficientMoneyException, IOException, InterruptedException, ExecutionException, ValueOutOfRangeException{
		//send 90%(excluding the transaction fee) funds to the merchant personal wallet address and 10% to the new tally admin's wallet address
		for(BigInteger walletId : walletIds){
			wallet = wallets.get(walletId.toString());
			withdrawCoinsFromWallet(wallet, walletId.toString(), merchantWalletAddress, adminWalletAddress, currencyId);
		}
	}

	public void withdrawCoinsFromWallet(Wallet wallet, String walletId, String merchantWalletAddress, String adminWalletAddress, Integer currencyId) throws InsufficientMoneyException, IOException, InterruptedException, ExecutionException, ValueOutOfRangeException{
		//calculate the amount that needs to be sent to the merchant after the commission(hard coding it to 90% at the moment)
		long finalAmount = (long) (wallet.getBalance().value - (0.1 * wallet.getBalance().value));
		double transactionAmount=BigDecimal.valueOf(finalAmount/Coin.COIN.getValue()).setScale(8, RoundingMode.HALF_DOWN).doubleValue();
		SendRequest merchantRequest = SendRequest.to(Address.fromBase58(configuration.getParams(), merchantWalletAddress), Coin.valueOf(finalAmount));
		wallet.completeTx(merchantRequest);
		wallet.commitTx(merchantRequest.tx);
		//wallet.saveToFile(new File(walletId));
		ListenableFuture<Transaction> future = configuration.getPeerGroup().broadcastTransaction(merchantRequest.tx).broadcast();
		future.get();
		//SendRequest adminRequest = SendRequest.to(new Address(configuration.getParams(), adminWalletAddress), Coin.valueOf((long)(wallet.getBalance().value - (0.0005 * wallet.getBalance().value))));
		Coin valueAfterFee = wallet.getBalance().subtract(Transaction.DEFAULT_TX_FEE);
        double commissionAmount=BigDecimal.valueOf(valueAfterFee.value/Coin.COIN.getValue()).setScale(8, RoundingMode.HALF_DOWN).doubleValue();
		if (Transaction.MIN_NONDUST_OUTPUT.compareTo(valueAfterFee) > 0)
            throw new ValueOutOfRangeException("totalValue too small to use");
		SendRequest adminRequest = SendRequest.to(Address.fromBase58(configuration.getParams(), adminWalletAddress), valueAfterFee);
		wallet.completeTx(adminRequest);
		wallet.commitTx(adminRequest.tx);
		//wallet.saveToFile(new File(walletId));
		ListenableFuture<Transaction> newFuture = configuration.getPeerGroup().broadcastTransaction(adminRequest.tx).broadcast();
		newFuture.get();
		ServiceFactory.getInstance().getMerchantService().createWithdrawTransaction(currencyId, merchantWalletAddress, transactionAmount, commissionAmount, adminWalletAddress);
	}
}
