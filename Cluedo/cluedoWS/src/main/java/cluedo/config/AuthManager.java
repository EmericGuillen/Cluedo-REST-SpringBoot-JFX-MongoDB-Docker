package cluedo.config;
import cluedo.controleur.CluedoControl;
import cluedo.jwtTokens.TokensG;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component

public class AuthManager implements AuthenticationProvider {

    @Autowired
    TokensG tokenG;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String SECRET = "75QsvB925G58sU6X2S3h4en8Up2G4K9x4sfhHKwv9VMRckKpVcqvK7N8gL5H4z8A";

        String authToken = authentication.getCredentials().toString();
        String login;

        try {
            login = tokenG.getClaimFromToken(authToken);

        } catch (Exception e) {
            login = null;
        }
        if (login != null && ! tokenG.isTokenExpired(authToken) && CluedoControl.userConnect.contains(login)) {
            Claims claims = tokenG.getAllClaimsFromToken(authToken);
            List roles = claims.get(SECRET, List.class);
            List authorities = new ArrayList();
            List<GrantedAuthority> grantedAuths =
                    AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN_ROLE");
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(login, login, grantedAuths);

            SecurityContextHolder.getContext().setAuthentication(auth);
            return  auth;
        } else {
           SecurityContextHolder.clearContext();
           return authentication;
        }
    }



    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }




}
