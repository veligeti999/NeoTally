package com.newtally.core.wallet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletExtension;
import org.bitcoinj.wallet.WalletTransaction;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;

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
			wallet.addAndActivateHDChain(createDeterministicKeyChain(mnemonic));
		}else{
			wallet = new Wallet(configuration.getParams());
			wallet.addAndActivateHDChain(createDeterministicKeyChain(mnemonic));
			wallet.saveToFile(walletFile);
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
	
	public Wallet loadWallet(File walletFile) throws UnreadableWalletException{
		return Wallet.loadFromFile(walletFile, new WalletExtension[0]);
	}
	
	public void autoSave(File walletFile){
		wallet.autosaveToFile(walletFile, 5, TimeUnit.SECONDS, null);
	}
	
	public BitcoinConfiguration getBitcoinConfiguration(){
		return configuration;
	}
	
	public String getBalance(Wallet wallet){
		return wallet.getBalance().toString();
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
						ServiceFactory.getInstance().getOrderInvoiceService().updateOrderStatusByTransactionId(transactionId, OrderStatus.Success.toString());
					}
				}
			}
		});
		
		wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
			
			@Override
			public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
				String address;
				String transactionId;
				try{
				List<TransactionOutput> walletTxnOutputs = tx.getWalletOutputs(wallet);
				for(TransactionOutput txnOutput : walletTxnOutputs){
					address = txnOutput.getAddressFromP2PKHScript(configuration.getParams()).toString();
					transactionId = txnOutput.getParentTransaction().getHash().toString();
					//update the transactionId for the address to which the coins are sent
					ServiceFactory.getInstance().getOrderInvoiceService().updateOrderStatus(transactionId, address, OrderStatus.Pending.toString());
				}
				}catch(Exception e){
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
	
}
