package com.newtally.core.resource;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("rest/")
public class NewTallyApplication extends ResourceConfig {

    public NewTallyApplication() {
        packages(NewTallyApplication.class.getPackage().getName());
        register(PreAuthenticationFilter.class);
    }

    public static void main(String... args) {
        System.out.println(NewTallyApplication.class.getPackage().getName());
    }
}
