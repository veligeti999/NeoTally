package com.newtally.core.resource;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.Merchant;
import com.newtally.core.model.Role;
import com.newtally.core.model.User;
import com.newtally.core.service.UserService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Path("/users")
public class UserResource extends BaseResource {

    private UserService usrService = ServiceFactory.getInstance().getUserService();

    @PermitAll
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(@Context HttpServletRequest req) throws IOException {

        User user = gson.fromJson(new InputStreamReader(req.getInputStream()), User.class);
        user = usrService.registerUser(user);

        return Response.ok(user).build();
    }

    @RolesAllowed({Role.USER})
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCurrentUser(@Context HttpServletRequest req) throws IOException {

        User user = gson.fromJson(req.getReader(), User.class);

        usrService.updateCurrentUser(user);

        return Response.ok().build();
    }

    @RolesAllowed({Role.USER})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest req) throws IOException {

        System.out.println("calling getCurrentUser()");
        User user = usrService.getCurrentUser();

        return Response.ok(user).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @GET
    @Path("/<id>")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") long id) throws IOException {

        User user = usrService.getUserById(id);

        return Response.ok(user).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @GET
    @Path("/inactive")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInActiveUser() throws IOException {

        User user = usrService.getInActiveUser();

        return Response.ok(user).build();
    }

    @RolesAllowed( {Role.SYSTEM, Role.USER_ADMIN})
    @PUT
    @Path("/<id>/changestatus?active=<active>")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setUserState(@PathParam("id") long id, @QueryParam("active") boolean isActive) {

        usrService.setUserState(id, isActive);

        return Response.ok().build();
    }
}
