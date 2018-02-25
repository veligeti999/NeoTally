package com.newtally.core.resource;

import com.newtally.core.ServiceFactory;
import com.newtally.core.dto.CoinDto;
import com.newtally.core.dto.DiscountDto;
import com.newtally.core.dto.ResponseDto;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Order;
import com.newtally.core.model.Role;
import com.newtally.core.model.Discount;
import com.newtally.core.model.Merchant;
import com.newtally.core.service.MerchantBranchService;
import com.newtally.core.service.MerchantService;
import com.newtally.core.wallet.WalletManager;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bitcoinj.wallet.Wallet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/merchants")
public class MerchantResource extends BaseResource {

    private final MerchantService mrctServ = ServiceFactory.getInstance().getMerchantService();
    private final ThreadContext context = ServiceFactory.getInstance().getSessionContext();

    @PermitAll
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerMerchant(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        Merchant merchant = gson.fromJson(new InputStreamReader(req.getInputStream()), Merchant.class);

        merchant = mrctServ.registerMerchant(merchant);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Merchant has been registered successfully");
        dto.setResponse_data(merchant);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to registered  the merhant");
            dto.setResponse_data(e.getMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCurrentMerchant(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        Merchant merchant = gson.fromJson(new InputStreamReader(req.getInputStream()), Merchant.class);

        mrctServ.updateCurrentMerchant(merchant);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully updated the current merchant details");
        dto.setResponse_data(merchant);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to update the merhant");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentMerchant(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        Merchant merchant = mrctServ.getCurrentMerchant();
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully get the current merchant details");
        dto.setResponse_data(merchant);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get the merhant");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMerchantById(@PathParam("id") long id){
        
        ResponseDto dto=new ResponseDto();
        try {
        Merchant merchant = mrctServ.getMerchantById(id);
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully getthe merchant");
        dto.setResponse_data(merchant);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get the merhant");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @GET
    @Path("/inactive")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInActiveMerchant(){
        
        ResponseDto dto=new ResponseDto();
        try {
        Merchant merchant = mrctServ.getInActiveMerchant();
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully inactive the merchant");
        dto.setResponse_data(merchant);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to inactive the merhant");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @PUT
    @Path("/{id}/changestatus?active=<active>")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setMerchantState(@PathParam("id") long id, @QueryParam("active") boolean isActive) {
        
        ResponseDto dto=new ResponseDto();
        try {
        mrctServ.changeStatus(id, isActive);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully change the status of merchant");
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to change the status of merhant");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(dto).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @GET
    @Path("/branches")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBranches() throws IOException {
        
        ResponseDto dto=new ResponseDto();
        try {
        List<MerchantBranch> branches = mrctServ.getAllBranches();
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully get the branches");
        dto.setResponse_data(branches);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to register Branch");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson_pretty.toJson(dto)).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @POST
    @Path("/branch")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerBranch(@Context HttpServletRequest req ){

        ResponseDto dto=new ResponseDto();
        try {
        MerchantBranch branch = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), MerchantBranch.class);

        branch = mrctServ.registerBranch(branch);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Branch has registered successfully");
        dto.setResponse_data(branch);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to register Branch");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(dto).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @PUT
    @Path("/branch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBranch(@Context HttpServletRequest req ){

        ResponseDto dto=new ResponseDto();
        try {
        MerchantBranch branch = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), MerchantBranch.class);

        mrctServ.updateBranch(branch);
        dto.setResponse_code(0);
        dto.setResponse_message("Branch has updated successfully");
        dto.setResponse_data(branch);
        }catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Branch has updated Failed");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(dto).build();
    }
    
    @RolesAllowed({Role.MERCHANT})
    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders(@Context HttpServletRequest req ){

        ResponseDto dto=new ResponseDto();
        try {
        List<Order> orders=mrctServ.getAllOrders();
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully get all transactions");
        dto.setResponse_data(orders);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get all transactions");
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(dto).build();
    }
    
    @GET
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest req ) {
        
        ResponseDto dto=new ResponseDto();
        try {
        HttpSession session = req.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully logout");
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to logout");
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(dto).build();
    }
    
    @RolesAllowed({Role.MERCHANT})
    @POST
    @Path("/counter/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCounter(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        MerchantCounter counter = gson.fromJson(new InputStreamReader(req.getInputStream()), MerchantCounter.class);

        counter = mrctServ.registerCounterForBranch(counter);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Counter has been registered successfully");
        dto.setResponse_data(counter);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to register counter");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.MERCHANT})
    @PUT
    @Path("/counter/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCounter(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        MerchantCounter counter = gson.fromJson(new InputStreamReader(req.getInputStream()), MerchantCounter.class);

        counter = mrctServ.updateCounter(counter);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Counter has been registered successfully");
        dto.setResponse_data(counter);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to register counter");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    @RolesAllowed({Role.MERCHANT})
    @GET
    @Path("/currency/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscounts(@Context HttpServletRequest req ){

        ResponseDto dto=new ResponseDto();
        try {
        List<DiscountDto> discounts=mrctServ.getDisounts();
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully get all currency discounts");
        dto.setResponse_data(discounts);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get all currency discounts");
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(gson_pretty.toJson(dto)).build();
    }
    @RolesAllowed({Role.MERCHANT})
    @POST
    @Path("/currency/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveDiscounts(@Context HttpServletRequest req ){

        ResponseDto dto=new ResponseDto();
        try {
        DiscountDto discount = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), DiscountDto.class);
        discount=mrctServ.saveDisounts(discount);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully update currency discounts");
        dto.setResponse_data(discount);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to update currency discounts");
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(gson_pretty.toJson(dto)).build();
    }

	@RolesAllowed({Role.MERCHANT, Role.BRANCH_MANAGER})
	@GET
	@Path("/balance")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWalletBalance() {
		ResponseDto response = new ResponseDto();
		try {
			CoinDto coin = mrctServ.getWalletBalance(context.getCurrentMerchantId(),context.getCurrentBranchId());
			response.setResponse_code(0);
			response.setResponse_message("Successfully Retrieved Wallet Balance");
			response.setResponse_data(coin);
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse_code(1);
			response.setResponse_message("Failed To Retrieve Wallet Balance");
			response.setResponse_data(e.getLocalizedMessage());
		}
		return Response.ok(gson_pretty.toJson(response)).build();
	}
	
	@RolesAllowed({Role.MERCHANT})
	@GET
	@Path("/withdraw")
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdrawBalance() {
		ResponseDto response = new ResponseDto();
		try {
			mrctServ.withdrawCoinsFromMerchantWallet();
			response.setResponse_code(0);
			response.setResponse_message("Successfully Withdrew Wallet Balance");
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse_code(1);
			response.setResponse_message("Failed To Withdraw Wallet Balance");
			response.setResponse_data(e.getLocalizedMessage());
		}
		return Response.ok(gson_pretty.toJson(response)).build();
	}

	@RolesAllowed({Role.MERCHANT})
    @POST
    @Path("/change/password")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePassword(@Context HttpServletRequest req ){

        ResponseDto dto=new ResponseDto();
        try {
        HashMap passwordMap = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), HashMap.class);
        mrctServ.updatePassword(passwordMap);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully update password");
        } catch(RuntimeException e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message(e.getLocalizedMessage());
            dto.setResponse_data(e.getLocalizedMessage());
        }catch (IOException e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to update password");
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(gson_pretty.toJson(dto)).build();
    }

}
