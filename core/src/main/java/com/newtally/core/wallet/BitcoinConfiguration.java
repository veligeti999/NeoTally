package com.newtally.core.wallet;

import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;

public class BitcoinConfiguration {

	private AbstractBlockChain blockChain;
	private PeerGroup peerGroup;
	private NetworkParameters params;
	private BlockStore blockStore;
	
	public BitcoinConfiguration(NetworkParameters networkParams, BlockStore blockStore) throws BlockStoreException{
		params = networkParams;
		blockChain = new BlockChain(params, blockStore);
		peerGroup = new PeerGroup(new Context(params), blockChain);
		startAndDownloadBlockChain(peerGroup);
	}
	
	public AbstractBlockChain getBlockChain() {
		return blockChain;
	}
	
	public void setBlockChain(AbstractBlockChain blockChain) {
		this.blockChain = blockChain;
	}
	
	public PeerGroup getPeerGroup() {
		return peerGroup;
	}
	
	public void setPeerGroup(PeerGroup peerGroup) {
		this.peerGroup = peerGroup;
	}
	
	public NetworkParameters getParams() {
		return params;
	}
	
	public void setParams(NetworkParameters params) {
		this.params = params;
	}
	
	public BlockStore getBlockStore() {
		return blockStore;
	}
	
	public void setBlockStore(BlockStore blockStore) {
		this.blockStore = blockStore;
	}
	
	public void startAndDownloadBlockChain(PeerGroup peerGroup){
		peerGroup.start();
		peerGroup.downloadBlockChain();
	}
	
}
