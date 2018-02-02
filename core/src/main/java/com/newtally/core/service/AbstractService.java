package com.newtally.core.service;

import com.blockcypher.utils.gson.GsonFactory;
import com.google.gson.Gson;
import com.newtally.core.model.PhysicalAddress;
import com.newtally.core.resource.ThreadContext;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public abstract class AbstractService {

    protected final EntityManager em;
    protected final ThreadContext ctx;
    private final Random random = new Random();
    protected final Gson gson = GsonFactory.getGson();

    public AbstractService(EntityManager em, ThreadContext ctx) {
        this.em = em;
        this.ctx = ctx;
    }

    protected void setPhysicalAddressParams(Query query, PhysicalAddress address) {
        query.setParameter("address", address.getAddress());
        query.setParameter("city", address.getCity());
        query.setParameter("state", address.getState());
        query.setParameter("zip", address.getZip());
        query.setParameter("country", address.getCountry());
    }

    protected PhysicalAddress readAddress(int fromIndex, Object[] fields) {
        PhysicalAddress address = new PhysicalAddress();
        address.setAddress((String) fields[fromIndex + 0]);
        address.setCity((String) fields[fromIndex + 1]);
        address.setState((String) fields[fromIndex + 2]);
        address.setZip((String) fields[fromIndex + 3]);
        address.setCountry((String) fields[fromIndex + 4]);

        return address;
    }

    protected void setParams(Map<String, Object> params, Query query) {
        for(Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    public long nextId() {
        return Math.abs(random.nextInt());
    }

    protected String generateNewPassword() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }


}
