package com.newtally.core.resource;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response registerCounter(@Context HttpServletRequest req) throws IOException {

        MerchantCounter counter = gson.fromJson(new InputStreamReader(req.getInputStream()), MerchantCounter.class);

        counter = branchServ.registerCounter(counter);
        
        ResponseDto dto=new ResponseDto();
        dto.setResponse_code(0);
        dto.setResponse_message("Counter has been registered successfully");
        dto.setResponse_data(counter);
        return Response.ok(gson.toJson(counter)).build();
    }
    
    @RolesAllowed({Role.BRANCH_MANAGER})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentBranch(@Context HttpServletRequest req) throws IOException {

        MerchantBranch branch = branchServ.getCurrentBranch();
        
        ResponseDto dto=new ResponseDto();
        dto.setResponse_code(0);
        dto.setResponse_message("successfully get the branch details");
        dto.setResponse_data(branch);

        return Response.ok(gson.toJson(dto)).build();
    }
}
