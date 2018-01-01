package com.newtally.core.resource;

import com.newtally.core.model.Merchant;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.PhysicalAddress;
import com.newtally.core.model.Role;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.List;

public class MerchantResourceTest extends AbstractResourceTest {

    @Test
    public void registerMerchant() {

        Merchant merchant = new Merchant();
        merchant.setName(System.currentTimeMillis() + "");
        merchant.setLicenseId(System.currentTimeMillis() + "");
        merchant.setOwnerName("vinod");
        merchant.setPassword("test123");
        merchant.setEmail("help@gmail.com");
        merchant.setPhone("+91 9849019746");

        PhysicalAddress address = new PhysicalAddress();
        address.setAddress("231 subari");
        address.setCity("warangal");
        address.setState("telangana");
        address.setCountry("india");
        address.setZip("506001");

        merchant.setAddress(address);



        Merchant createdMrct = post("/merchants/register", merchant, null, null);

        Merchant getMrct = get("/merchants", Merchant.class,
                Role.MERCHANT + ":" + createdMrct.getId(), merchant.getPassword());

        merchant.setId(createdMrct.getId());

        assertMerchant(merchant, getMrct);

        List<MerchantBranch> branches = getList("/merchants/branches", MerchantBranch.class,
                Role.MERCHANT + ":" + createdMrct.getId(), merchant.getPassword());

        assertTrue(branches.size() == 1);

        MerchantBranch branch = branches.get(0);

        assertEquals(getMrct.getEmail(), branch.getEmail());
        assertEquals(getMrct.getOwnerName(), branch.getManagerName());
        assertEquals(getMrct.getName(), branch.getName());
        assertEquals(getMrct.getPhone(), branch.getPhone());
        assertAddress(getMrct.getAddress(), branch.getAddress());
    }

    private void assertMerchant(Merchant expected, Merchant actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getLicenseId(), actual.getLicenseId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getOwnerName(), actual.getOwnerName());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertAddress(expected.getAddress(), actual.getAddress());
    }

    private void assertAddress(PhysicalAddress expected, PhysicalAddress actual) {
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getCity(), actual.getCity());
        assertEquals(expected.getState(), actual.getState());
        assertEquals(expected.getZip(), actual.getZip());
        assertEquals(expected.getCountry(), actual.getCountry());
    }

}
