package com.newtally.core.resource;

import com.newtally.core.model.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser() {
        User user = new User();
        user.setFirstName("Vinod");
        user.setLastName("Eligeti");

        return Response.ok(user).build();
    }
}
