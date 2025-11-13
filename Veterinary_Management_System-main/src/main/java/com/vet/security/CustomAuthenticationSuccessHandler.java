package com.vet.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        if (roles.contains("ROLE_ADMIN")) {
            setDefaultTargetUrl("/dashboard/admin");
        } else if (roles.contains("ROLE_VETERINARIAN")) {
            setDefaultTargetUrl("/dashboard/vet");
        } else if (roles.contains("ROLE_OWNER")) {
            setDefaultTargetUrl("/owner/dashboard");
        } else {
            setDefaultTargetUrl("/");
        }
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
} 