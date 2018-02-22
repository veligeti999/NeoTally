package com.newtally.core.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.annotation.security.PermitAll;
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

import com.newtally.core.ServiceFactory;
import com.newtally.core.dto.ResponseDto;
import com.newtally.core.service.ApplicationService;
import com.newtally.core.service.MerchantService;

@Path("/")
public class ApplicationResource extends BaseResource {
    
    private final ApplicationService appService = ServiceFactory.getInstance().getApplicationService();
    
    @PermitAll
    @POST
    @Path("forget/password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateResetPassword(@Context HttpServletRequest req ){

        ResponseDto dto=new ResponseDto();
        try {
        HashMap passwordMap = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), HashMap.class);
        appService.generateResetPasswordLink(passwordMap);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully generate reset password link");
        } catch(RuntimeException e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message(e.getLocalizedMessage());
            dto.setResponse_data(e.getLocalizedMessage());
        }catch (IOException e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to generate reset password link");
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(gson_pretty.toJson(dto)).build();
    }
    
    @PermitAll
    @POST
    @Path("reset/password/{token}/{userType}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(@Context HttpServletRequest req, @PathParam("token") String token, @PathParam("userType") String userType){

        ResponseDto dto=new ResponseDto();
        try {
        HashMap passwordMap = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), HashMap.class);
        appService.resetPassword(passwordMap, token, userType);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully reset password");
        } catch(RuntimeException e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message(e.getLocalizedMessage());
            dto.setResponse_data(e.getLocalizedMessage());
        }catch (IOException e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to reset password");
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(gson_pretty.toJson(dto)).build();
    }
    
    @PermitAll
    @GET
    @Path("confirm/email/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response confirmEmail(@PathParam("token") String token){

        ResponseDto dto=new ResponseDto();
        try {
        appService.confirmEmail(token);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully confirmed the email");
        } catch(RuntimeException e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message(e.getLocalizedMessage());
            dto.setResponse_data(e.getLocalizedMessage());
        }

        return Response.ok(gson_pretty.toJson(dto)).build();
    }

}
