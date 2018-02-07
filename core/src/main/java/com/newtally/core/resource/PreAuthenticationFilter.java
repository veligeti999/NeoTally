package com.newtally.core.resource;

import com.newtally.core.ServiceFactory;
import com.newtally.core.model.Role;
import com.newtally.core.service.BranchCounterService;
import com.newtally.core.service.IAuthenticator;
import com.newtally.core.service.MerchantBranchService;
import com.newtally.core.service.MerchantService;
import com.newtally.core.service.UserService;
import com.newtally.core.wallet.WalletManager;

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

import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
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

    ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private final ThreadContext threadCtx = serviceFactory.getSessionContext();
    private final WalletManager walletManager = serviceFactory.getWalletManager();
    private final BranchCounterService branchCounterService = serviceFactory.getMerchantCounterService();
    private final MerchantService merchantService = serviceFactory.getMerchantService();
    private final MerchantBranchService branchService = serviceFactory.getMerchantBranchService();

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
        System.out.println(userType);
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();


        IAuthenticator iAuthenticator = roleVsAuth.get(userType);

        if(iAuthenticator != null) {
            boolean valid = iAuthenticator.authenticate(username, password);
            if (valid) {

                String userId=iAuthenticator.getUserId(username, password);
                _authorizeAndSetSession(rolesSet, userId, userType);
                //This is how it needs to be done even in case of merchant
                if(userType.equals(Role.BRANCH_COUNTER) || userType.equals(Role.BRANCH_MANAGER)){
                try {
						initializeWallet(userId, userType);
					} catch (UnreadableWalletException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
				if (userType.equals(Role.MERCHANT)) {
					// fetch all the branches for a merchant and initialize
					// wallets for them.
					// This is for calculating the balances
					List<BigInteger> branchIds = branchService.getBranchIdsByMerchantId(Long.valueOf(userId));
					for (BigInteger branchId : branchIds) {
						try {
							setPrincipalOnThreadContext(Role.BRANCH_MANAGER, branchId.toString());
							initializeWallet(branchId.toString(), "");
						} catch (UnreadableWalletException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
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

	/**
	 * Load the wallet during authentication(branch and counter -> at the
	 * moment) Check whether the wallet already exists in memory or load it from
	 * the wallet file
	 *
	 * @param userId
	 * @param role
	 * @throws IOException
	 * @throws UnreadableWalletException
	 */
	private void initializeWallet(String userId, String role) throws UnreadableWalletException, IOException {
		if (role.equals(Role.BRANCH_COUNTER)) {
			userId = branchCounterService.getBranchIdByCounterPwd(userId);
		}
		Map<String, Wallet> wallets = walletManager.getBranchWallets();
		if (!wallets.containsKey(userId)) {
			String merchantId = branchService.getMerchantIdByBranchId(Long.valueOf(userId));
			// fetch the mnemonic for the merchant and load up the wallet
			String mnemonic = merchantService.getMnenonicForAMerchant(Long.valueOf(merchantId));
			List<String> mnemonicList = Arrays.asList(mnemonic.split(","));
			walletManager.createOrLoadWallet(mnemonicList, userId);
		}
	}

    private void validateRoles(String role, Set<String> rolesSet) throws AccessDeniedException {
        if(!rolesSet.contains(role))
            throw new RuntimeException("Access denied");;
    }

    public void setPrincipalOnThreadContext(String role, String id) {
		String branchId = null;
		int branchNo = 0;
        // clear previous ones
        threadCtx.clearContext();
        if(role.equals(Role.USER)) {
            threadCtx.setCurrentUserId(Long.parseLong(id));
        } else if(role.equals(Role.MERCHANT)) {
            threadCtx.setCurrentMerchantId(Long.parseLong(id));
        } else if(role.equals(Role.BRANCH_COUNTER)) {
			branchId = branchCounterService.getBranchIdByCounterPwd(id);
			branchNo = branchService.getBranchNoByBranchId(Long.valueOf(branchId));
            threadCtx.setCurrentMerchantCounterId(id);
            threadCtx.setCurrentBranchAccNum(branchNo);
        } else if(role.equals(Role.BRANCH_MANAGER)) {
			branchNo = branchService.getBranchNoByBranchId(Long.valueOf(id));
            threadCtx.setMerchantBranchId(Long.parseLong(id));
            threadCtx.setCurrentBranchAccNum(branchNo);
        } 
        else {
            throw new IllegalArgumentException("Unknown role: " + role + " specified");
        }
    }
}