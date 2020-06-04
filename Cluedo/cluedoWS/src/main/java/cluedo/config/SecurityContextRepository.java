package cluedo.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SecurityContextRepository implements org.springframework.security.web.context.SecurityContextRepository {

    @Autowired
    AuthManager authManager;


    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder HttpRequestResponseHolder) {
        HttpServletRequest request = HttpRequestResponseHolder.getRequest();

        String authHeader;
        String authToken = null;
        if(request.getHeaders(HttpHeaders.AUTHORIZATION).hasMoreElements()){
            authHeader = request.getHeaders(HttpHeaders.AUTHORIZATION).nextElement();
        }
        else {
            authHeader = null;
        }
        if (authHeader != null && authHeader.startsWith("")) {
            authToken = authHeader.replace("", "");
        }else {
            //System.out.println("ERROR");
        }

        if (authToken != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
            return new SecurityContextImpl(this.authManager.authenticate(auth));

        } else {
            return new SecurityContextImpl();
        }
    }
    @Override
    public void saveContext(SecurityContext securityContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public boolean containsContext(HttpServletRequest httpServletRequest) {
        return false;
    }


}
