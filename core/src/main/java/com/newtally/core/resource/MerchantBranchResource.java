package com.newtally.core.resource;

import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.internal.LinkedTreeMap;
import com.newtally.core.ServiceFactory;
import com.newtally.core.dto.ResponseDto;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Role;
import com.newtally.core.service.MerchantBranchService;

@Path("/branches")
public class MerchantBranchResource extends BaseResource{

    private final MerchantBranchService branchServ = ServiceFactory.getInstance().getMerchantBranchService();

    @RolesAllowed({Role.BRANCH_MANAGER})
    @POST
    @Path("/counter/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCounter(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        MerchantCounter counter = gson.fromJson(new InputStreamReader(req.getInputStream()), MerchantCounter.class);

        counter = branchServ.registerCounter(counter);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Counter has been registered successfully");
        dto.setResponse_data(counter);
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to register counter");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_MANAGER})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentBranch(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        MerchantBranch branch = branchServ.getCurrentBranch();
        
        dto.setResponse_code(0);
        dto.setResponse_message("successfully get the branch details");
        dto.setResponse_data(branch);
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get the branch details");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.MERCHANT})
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBranchCounters(@PathParam("id") long id) {
        
        ResponseDto dto=new ResponseDto();
        try {
        List<MerchantCounter> counters = branchServ.getCounters(id);
        
        dto.setResponse_code(0);
        dto.setResponse_message("successfully get the branch counters");
        dto.setResponse_data(counters);
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get the branch counters");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }

	@PermitAll
	@POST
	@Path("/notification")
	@Produces(MediaType.APPLICATION_JSON)
	public Response receiveNotifications(@Context HttpServletRequest req) {
		try {
			System.out.println("hit");
			LinkedTreeMap event = gson.fromJson(new InputStreamReader(req.getInputStream()), LinkedTreeMap.class);
			System.out.println("event" + event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().build();
	}
}
