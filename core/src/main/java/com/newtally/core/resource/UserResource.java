package com.newtally.core.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newtally.core.ServiceFactory;
import com.newtally.core.model.User;
import com.newtally.core.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/user")
public class UserResource {

    private UserService usrService = ServiceFactory.getInstance().getUserService();

    private JsonParser parser = new JsonParser();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@Context HttpServletRequest req) throws IOException {

        User user = parser.parseObject(User.class, req.getInputStream());

        user = usrService.registerUser(user);

        return Response.ok(user).build();
    }
}
