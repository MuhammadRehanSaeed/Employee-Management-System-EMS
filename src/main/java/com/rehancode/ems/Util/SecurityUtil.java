package com.rehancode.ems.Util;

import com.rehancode.ems.Config.DetailsService.UserPrinicple;
import com.rehancode.ems.Exception.UserNotAuthenticated;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {

    private SecurityUtil() {
        // static utility – no instantiation
    }

    /**
     * Returns the ID of the currently authenticated user.
     *
     * @throws UserNotAuthenticated if there is no valid-authenticated principal in the context
     */
    public static Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth.getPrincipal() == null ||
                "anonymousUser".equals(auth.getPrincipal())) {

            throw new UserNotAuthenticated("User not authenticated");
        }

        if (!(auth.getPrincipal() instanceof UserPrinicple userPrincipal)) {
            throw new UserNotAuthenticated("Invalid user principal");
        }

        return userPrincipal.getUser().getId();
    }
}
