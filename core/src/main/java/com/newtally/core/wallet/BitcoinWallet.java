package com.newtally.core.wallet;

import com.blockcypher.exception.BlockCypherException;
import com.blockcypher.model.address.Address;
import com.blockcypher.model.transaction.Transaction;
import com.blockcypher.model.transaction.intermediary.IntermediaryTransaction;
import com.blockcypher.model.transaction.summary.TransactionSummary;
import com.blockcypher.service.TransactionService;
import com.blockcypher.utils.gson.GsonFactory;
import com.blockcypher.utils.sign.SignUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BitcoinWallet extends AbstractCryptoWallet {

    private Address address;

    public BitcoinWallet(String identifier) {
        super(identifier);
    }

    @Override
    public void initialize() throws BlockCypherException {
        address = cc.getAddressService().createAddress();
    }

    void initialize(Address address) throws BlockCypherException {
        this.address = address;
    }

    @Override
    public String getPrivateAddress() {
        return address.getPrivate();
    }

    @Override
    public String getPublicAddress() {
        return address.getPublic();
    }

    private String getAddress() {
        return address.getAddress();
    }

    @Override
    public long getBalance() throws BlockCypherException {
        return cc.getAddressService().getAddress(address.getAddress()).getBalance().longValue();
    }

    @Override
    public Transaction sendTo(String toAddress, long amount) throws BlockCypherException {

        TransactionService txSrv = cc.getTransactionService();

        IntermediaryTransaction inTx = txSrv.newTransaction(
                Arrays.asList(new String[]{address.getAddress()}),
                Arrays.asList(new String[]{toAddress}), amount);

        // TODO: make it two step process so that user is ok with the fee structure.

        SignUtils.signWithHexKeyWithPubKey(inTx, address.getPrivate());

        return txSrv.sendTransaction(inTx);
    }

    @Override
    public String toString() {
        return identifier + ":" + address.toString();
    }

    @Override
    public List<TransactionSummary> getTransactions(long fromInSecs) throws BlockCypherException {
        return  cc.getAddressService().getAddress(address.getAddress()).getTxrefs();
    }

    public static void main(String... args) throws BlockCypherException {
        BitcoinWallet sender = new BitcoinWallet("Vinod");
        sender.initialize(new Address("C2hunMxBk6MgdxSxxDmmCSLyeVch98EEcb",
                "02d2f73c70390032dcf3aa4fc2e2c865ba0037c2f959dbef7dc7218d8e4fb242ab",
                "66aa26cda4b403dbd897cdcb29027cc1c5050a500f145293ace4eca72ff62ca2"));
        BitcoinWallet receiver = new BitcoinWallet("Kitty");
        receiver.initialize(new Address("C3FMPp8RLbppwxsa7mTiEyVE5c1HhfjJwP",
                "0209cd90141b27aac80ac64a8f43140833dc1353c17f2d2e50691764ecc223694d",
                "eac3bdd2c498044b280b0b85c649cbb4942945e51644cd7f7bdacef0e32378a2"));

        System.out.println(sender.getBalance());
        System.out.println(receiver.getBalance());

        System.out.println(sender.sendTo(receiver.getAddress(), 20000));
    }
}
