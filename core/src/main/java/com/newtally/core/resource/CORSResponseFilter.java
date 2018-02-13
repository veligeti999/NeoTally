package com.newtally.core.resource;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

@Provider
public class CORSResponseFilter implements ContainerResponseFilter {

@Override
public void filter(ContainerRequestContext creq, ContainerResponseContext cres) {
    cres.getHeaders().add("Access-Control-Allow-Origin", "*");
    cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
    cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
    cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    cres.getHeaders().add("Access-Control-Max-Age", "");
    
    if ("OPTIONS".equalsIgnoreCase(creq.getMethod())) {
        ResponseBuilder responseBuilder = Response.status(Status.ACCEPTED);
        Response response = responseBuilder.status(Status.ACCEPTED).build();
        creq.abortWith(response);
        return;
    } 
}

}