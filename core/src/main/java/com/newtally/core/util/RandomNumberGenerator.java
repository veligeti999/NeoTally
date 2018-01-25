package com.newtally.core.util;
import org.bitcoinj.crypto.LinuxSecureRandom;

public class RandomNumberGenerator extends LinuxSecureRandom{
	
	private static final long serialVersionUID = 1L;
    private static final int NUM_BYTES = 32;
	
	private byte[] randomNumber; 
	
	/**
	 * Picking up an entropy of 32 bytes to generate 24 mnemonic words
	 * @param numBytes
	 */
	public RandomNumberGenerator(){
		randomNumber =  super.engineGenerateSeed(NUM_BYTES);
	}
	
	public byte[] getRandomNumber(){
		return randomNumber;
	}
}
