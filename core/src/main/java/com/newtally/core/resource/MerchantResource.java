package com.newtally.core.resource;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.MerchantBranch;
import com.newtally.core.model.Role;
import com.newtally.core.model.Merchant;
import com.newtally.core.service.MerchantService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/merchants")
public class MerchantResource extends BaseResource {

    private final MerchantService mrctServ = ServiceFactory.getInstance().getMerchantService();

    @PermitAll
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerMerchant(@Context HttpServletRequest req) throws IOException {

        Merchant merchant = parser.parseObject(Merchant.class, req.getInputStream());

        merchant = mrctServ.registerMerchant(merchant);

        return Response.ok(merchant).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCurrentMerchant(@Context HttpServletRequest req) throws IOException {

        Merchant merchant = parser.parseObject(Merchant.class, req.getInputStream());

        mrctServ.updateCurrentMerchant(merchant);

        return Response.ok().build();
    }

    @RolesAllowed({Role.MERCHANT})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentMerchant(@Context HttpServletRequest req) throws IOException {

        Merchant merchant = mrctServ.getCurrentMerchant();

        return Response.ok(merchant).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @GET
    @Path("/<id>")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMerchantById(@PathParam("id") long id) throws IOException {

        Merchant merchant = mrctServ.getMerchantById(id);

        return Response.ok(merchant).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @GET
    @Path("/inactive")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInActiveMerchant() throws IOException {

        Merchant merchant = mrctServ.getInActiveMerchant();

        return Response.ok(merchant).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @PUT
    @Path("/<id>/changestatus?active=<active>")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setMerchantState(@PathParam("id") long id, @QueryParam("active") boolean isActive) {

        mrctServ.changeStatus(id, isActive);

        return Response.ok().build();
    }

    @RolesAllowed({Role.MERCHANT})
    @GET
    @Path("/branches")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBranches() throws IOException {

        List<MerchantBranch> branches = mrctServ.getAllBranches();

        return Response.ok(branches).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @POST
    @Path("/branch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerBranch(@Context HttpServletRequest req ) throws IOException {

        MerchantBranch branch = parser.parseObject(MerchantBranch.class, req.getInputStream());

        branch = mrctServ.registerBranch(branch);

        return Response.ok(branch).build();
    }

    @RolesAllowed({Role.MERCHANT})
    @PUT
    @Path("/branch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBranch(@Context HttpServletRequest req ) throws IOException {

        MerchantBranch branch = parser.parseObject(MerchantBranch.class, req.getInputStream());

        mrctServ.updateBranch(branch);

        return Response.ok().build();
    }

}
