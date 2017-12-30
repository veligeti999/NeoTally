package com.newtally.core.resource;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.Role;
import com.newtally.core.service.UserService;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.AccessDeniedException;
import java.util.*;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class PreAuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    public static final String USER_ID_SESSION_ATTR = "id";
    public static final String ROLE_SESSION_ATTR = "role";
//    private final RuntimeException ACCESS_DENY = new RuntimeException("Access denied");

    private UserService usrService = ServiceFactory.getInstance().getUserService();
    private ThreadContext sessionCtx = ServiceFactory.getInstance().getSessionContext();

    @Context
    private ResourceInfo resInfo;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {

        Method method = resInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }
        if (method.isAnnotationPresent(DenyAll.class)) {
            throw new RuntimeException("Access denied");
        }

        if (!method.isAnnotationPresent(RolesAllowed.class)) {
            return;
        }

//        HttpServletRequest req = (HttpServletRequest) ctx.getRequest();
        RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
        Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

        // TODO: handle session authorization
        
//        if (req.isRequestedSessionIdValid()) {
//            HttpSession session = req.getSession();
//
//            setContextVariables(session);
//
//            validateRoles(session, rolesSet);
//        }

        //Fetch authorization header
        final String authorization = ctx.getHeaderString(AUTHORIZATION_PROPERTY);

        //If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            throw new RuntimeException("Access denied");
        }

        //Get encoded username and password
        final String encodedUserPassword = authorization.replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        //Decode username and password
        String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword));

        //Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String userType = tokenizer.nextToken().toLowerCase();
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        if (userType.equals(Role.USER)) {
            boolean valid = usrService.authenticateUser(username, password);

            if (valid) {
                validateRoles(Role.USER, rolesSet);


//                HttpSession session = req.getSession(true);
//                session.setAttribute(USER_ID_SESSION_ATTR, username);
//                session.setAttribute(ROLE_SESSION_ATTR, Role.USER);
//                session.setMaxInactiveInterval(30 * 60);

                setContextVariables(Role.USER, username);
            } else {
                throw new RuntimeException("Access denied");
            }
        }
    }

    private void validateRoles(String role, Set<String> rolesSet) throws AccessDeniedException {
        if(!rolesSet.contains(role))
            throw new RuntimeException("Access denied");;
    }

    public void setContextVariables(String role, String id) {

        if(role.equals(Role.USER)) {
            sessionCtx.setCurrentUserId(Long.parseLong(id));
        }
    }
}