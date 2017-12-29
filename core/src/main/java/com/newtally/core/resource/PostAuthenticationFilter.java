package com.newtally.core.resource;

import com.newtally.core.ServiceFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

//@Provider
public class PostAuthenticationFilter implements ContainerRequestFilter {

    private final ThreadContext threadCtx = ServiceFactory.getInstance().getSessionContext();
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        threadCtx.clearContext();
        System.out.println("POST AUTHENTICATION");
    }
}
