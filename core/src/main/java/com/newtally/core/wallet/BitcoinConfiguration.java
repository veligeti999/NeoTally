package com.newtally.core.wallet;

import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener;
import org.bitcoinj.net.discovery.DnsDiscovery;
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
		System.out.println("starting blocks download");
		peerGroup.start();
		peerGroup.addPeerDiscovery(new DnsDiscovery(params));
		peerGroup.addBlocksDownloadedEventListener(new BlocksDownloadedEventListener() {

			@Override
			public void onBlocksDownloaded(Peer peer, Block block, FilteredBlock filteredBlock, int blocksLeft) {
				// TODO Auto-generated method stub
		        System.out.println("blocksLeft"+blocksLeft);
			}
		});

		peerGroup.downloadBlockChain();
		System.out.println("block chain download successful");
	}
	
}
