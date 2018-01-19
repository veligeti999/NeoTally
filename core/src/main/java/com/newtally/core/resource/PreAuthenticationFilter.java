package com.newtally.core.resource;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.Role;
import com.newtally.core.service.IAuthenticator;
import com.newtally.core.service.MerchantBranchService;
import com.newtally.core.service.MerchantService;
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
    private final RuntimeException ACCESS_DENY = new RuntimeException("Access denied");

    private final Map<String, IAuthenticator> roleVsAuth = new HashMap<>();

    private final ThreadContext threadCtx = ServiceFactory.getInstance().getSessionContext();

    @Context
    private ResourceInfo resInfo;

    @Context
    private HttpServletRequest req;

    public PreAuthenticationFilter() {
        ServiceFactory instance = ServiceFactory.getInstance();

        roleVsAuth.put(Role.USER, instance.getUserService());
        roleVsAuth.put(Role.MERCHANT, instance.getMerchantService());
        roleVsAuth.put(Role.BRANCH_MANAGER, instance.getMerchantBranchService());
        roleVsAuth.put(Role.BRANCH_COUNTER, instance.getMerchantCounterService());
    }

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

        RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
        Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

        // TODO: handle session authorization
        HttpSession session = req.getSession(false);
        if (session != null) {

            String role = (String) session.getAttribute(ROLE_SESSION_ATTR);
            String userId = (String) session.getAttribute(USER_ID_SESSION_ATTR);


            validateRoles(role, rolesSet);
            setPrincipalOnThreadContext(role, userId);
            return;
        }

        //Fetch authorization header
        final String authorization = ctx.getHeaderString(AUTHORIZATION_PROPERTY);

        //If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            throw new RuntimeException("Access denied");
        }

        //Get encoded userId and password
        final String encodedUserPassword = authorization.replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        //Decode userId and password
        String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword));

        //Split userId and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String userType = tokenizer.nextToken().toLowerCase();
        final String userId = tokenizer.nextToken();
        final String password = tokenizer.nextToken();


        IAuthenticator iAuthenticator = roleVsAuth.get(userType);

        if(iAuthenticator != null) {
            boolean valid = iAuthenticator.authenticate(userId, password);

            if (valid) {
                _authorizeAndSetSession(rolesSet, userId, userType);
                return;
            }
        }
        throw new RuntimeException("Access denied");
    }

    private void _authorizeAndSetSession(Set<String> rolesSet, String userId, String role) throws AccessDeniedException {
        validateRoles(role, rolesSet);
        setPrincipalOnThreadContext(role, userId);

        HttpSession session = req.getSession(true);
        session.setMaxInactiveInterval(60 * 30 ); // 30 minutes
        session.setAttribute(ROLE_SESSION_ATTR, role);
        session.setAttribute(USER_ID_SESSION_ATTR, userId);
    }

    private void validateRoles(String role, Set<String> rolesSet) throws AccessDeniedException {
        if(!rolesSet.contains(role))
            throw new RuntimeException("Access denied");;
    }

    public void setPrincipalOnThreadContext(String role, String id) {
        // clear previous ones
        threadCtx.clearContext();

        if(role.equals(Role.USER)) {
            threadCtx.setCurrentUserId(Long.parseLong(id));
        } else if(role.equals(Role.MERCHANT)) {
            threadCtx.setCurrentMerchantId(Long.parseLong(id));
        } else if(role.equals(Role.BRANCH_COUNTER)) {
            threadCtx.setCurrentMerchantCounterId(id);
        } else if(role.equals(Role.BRANCH_MANAGER)) {
            threadCtx.setMerchantBranchId(Long.parseLong(id));
        } 
        else {
            throw new IllegalArgumentException("Unknown role: " + role + " specified");
        }
    }
}