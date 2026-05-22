package com.rehancode.ems.Config.Jwt;

import com.rehancode.ems.Config.DetailsService.CustomUserDetailsService;
import com.rehancode.ems.Exception.AccountLockedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    public JwtFilter(JwtService jwtService,CustomUserDetailsService customUserDetailsService){
        this.jwtService=jwtService;
        this.customUserDetailsService=customUserDetailsService;
    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader=request.getHeader("Authorization");
//        String token=null;
//        String username=null;
//        if(authHeader!=null && authHeader.startsWith("Bearer ")){
//            token=authHeader.substring(7);
//            if (!token.isBlank()) {
//                try {
//                    username = jwtService.extractUsername(token);
//                } catch (Exception e) {
//                    System.out.println("Invalid JWT Token: " + e.getMessage());
//                }
//            }
//        }
//        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//            UserDetails userDetails=customUserDetailsService.loadUserByUsername(username);
//            if (!userDetails.isAccountNonLocked()) {
//                throw new AccountLockedException("User account is blocked by admin");
//            }
//            if(jwtService.validateToken(token,userDetails)){
//                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//
//            }
//        }
//        filterChain.doFilter(request,response);
//    }
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);

        if (!token.isBlank()) {
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                log.warn("Invalid JWT token – uri='{}' error='{}'",
                        request.getRequestURI(), e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        log.debug("Authenticating JWT for username='{}' uri='{}'", username, request.getRequestURI());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (!userDetails.isAccountNonLocked()) {
            log.warn("Blocked request – account locked username='{}'", username);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User account is locked");
            return;
        }

        if (token != null && jwtService.validateToken(token, userDetails)) {

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("JWT authentication set for username='{}' authorities='{}'",
                    username, userDetails.getAuthorities());
        }
    }

    filterChain.doFilter(request, response);
}
}
