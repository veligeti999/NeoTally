package com.newtally.core.wallet;

import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;

public class BitcoinConfiguration {

	private final AbstractBlockChain blockChain;
	private final PeerGroup peerGroup;
	private final NetworkParameters params;
	
	public BitcoinConfiguration(NetworkParameters networkParams, BlockStore blockStore) throws BlockStoreException{
		params = networkParams;
		blockChain = new BlockChain(params, blockStore);
		peerGroup = new PeerGroup(new Context(params), blockChain);
		startAndDownloadBlockChain(peerGroup);
	}
	
	public AbstractBlockChain getBlockChain() {
		return blockChain;
	}
	
	public PeerGroup getPeerGroup() {
		return peerGroup;
	}
	
	public NetworkParameters getParams() {
		return params;
	}
	
	public void startAndDownloadBlockChain(PeerGroup peerGroup){
		peerGroup.start();
		peerGroup.downloadBlockChain();
	}
	
}
